package com.example.rule.service.impl;

import com.alibaba.fastjson.JSON;
import com.example.rule.callable.RuleRun;
import com.example.rule.entity.dto.RuleDto;
import com.example.rule.entity.exception.ResponseException;
import com.example.rule.entity.pojo.ConditionInfo;
import com.example.rule.entity.pojo.RuleInfo;
import com.example.rule.mapper.ConditionInfoMapper;
import com.example.rule.mapper.RuleInfoMapper;
import com.example.rule.service.RuleService;
import com.example.rule.utils.LoadJarUtil;
import com.example.rule.utils.SnowFlake;
import com.example.rule.utils.SpringUtil;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.LineNumberAttribute;
import javassist.bytecode.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/22 10:57
 **/
@Service
@Slf4j
public class RuleServiceImpl implements RuleService {

    @Autowired
    private RuleInfoMapper ruleInfoMapper;
    @Autowired
    private ConditionInfoMapper conditionInfoMapper;
    @Value("${rule.class.path}")
    private String classPath;

    @Override
    public void addRule(RuleDto request) {
        int byName = ruleInfoMapper.findCountByName(request.getRuleName());
        if (byName > 0) {
            throw new RuntimeException("规则已存在");
        }
        List<Long> conditionIds = request.getConditionIds();
        if (CollectionUtils.isEmpty(conditionIds)) {
            throw new RuntimeException("条件不能为空！");
        }
        List<ConditionInfo> list = conditionInfoMapper.findByIds(conditionIds);
        Map<String, List<ConditionInfo>> collect =
                list.stream().collect(Collectors.groupingBy(a -> a.getTemplateName() + "." + a.getTemplateMethod()));
        if (collect.size() > 1) {
            throw new RuntimeException("条件模板不一致！");
        }
        packRule(list, request);
    }

    @Override
    public void addCondition(RuleDto info, MultipartFile file) {
        int count = conditionInfoMapper.findByName(info.getConditionName());
        if (count > 0) {
            throw new RuntimeException("条件已存在");
        }
        CtClass zz = null;
        try {
            if (file != null) {
                File outFile = new File("D:\\testCode\\jar\\", file.getOriginalFilename());
                FileOutputStream outputStream = new FileOutputStream(outFile);
                outputStream.write(file.getBytes());
                outputStream.flush();
                outputStream.close();
                LoadJarUtil.loadJarLocal(outFile);
            }
            zz = getCtClass(info.getTemplateName(), info.getTemplateMethodName(), info.getConditionName(), null);
        } catch (Exception e) {
            log.info("解析jar包失败!", e);
            throw new ResponseException("解析jar包失败!");
        }
        CtClass cc = zz;
        List<String> fieldType = info.getFieldType();
        if (StringUtils.isEmpty(info.getPreContent())) {
            info.setRuleContent("if(" + info.getJudgeContent() + ");");
        } else {
            info.setRuleContent(info.getPreContent() + "if(" + info.getJudgeContent() + "){}");
        }
        this.addField(cc, fieldType);
        this.addContent(cc, info);
        cc.setName(info.getConditionName());
        try {
            cc.writeFile(classPath);
        } catch (CannotCompileException e) {
            log.info("规则文件编译失败！");
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.info("规则文件写出失败！");
            throw new RuntimeException(e);
        }
        ConditionInfo conditionInfo = new ConditionInfo();
        conditionInfo.setId(SnowFlake.getSnowFlakeId());
        conditionInfo.setConditionName(info.getConditionName());
        conditionInfo.setTemplateName(info.getTemplateName());
        conditionInfo.setTemplateMethod(info.getTemplateMethodName());
        if (!StringUtils.isEmpty(info.getFieldType())) {
            conditionInfo.setFieldType(JSON.toJSONString(info.getFieldType()));
        }
        if (!StringUtils.isEmpty(info.getParamsType())) {
            conditionInfo.setParamType(JSON.toJSONString(info.getParamsType()));
        }
        conditionInfo.setPreContent(info.getPreContent());
        conditionInfo.setJudgeContent(info.getJudgeContent());
        conditionInfoMapper.insertSelective(conditionInfo);
    }

    @Override
    public Object run(List<RuleDto> list) {
        ExecutorService exec = Executors.newCachedThreadPool();//工头
        ArrayList<Future<Object>> results = new ArrayList<>();
        list.forEach(a -> {
            RuleInfo ruleInfo = ruleInfoMapper.findOneByName(a.getRuleName());
            if (ruleInfo == null) {
                throw new RuntimeException("规则不存在");
            }
            a.setTemplateName(ruleInfo.getTemplateName());
            results.add(exec.submit(new RuleRun(a)));
        });
        log.info("size1: {}", results.size());
        List<Object> result = new ArrayList<>();
        for (Future<Object> future : results) {
            try {
                Object o = future.get();
                result.add(o);
            } catch (Exception e) {
                log.info("规则执行错误！", e);
                throw new ResponseException("规则执行错误!");
            }
        }
        return result;
    }

    private void addField(CtClass cc, List<String> fieldType) {
        if (!CollectionUtils.isEmpty(fieldType)) {
            fieldType.forEach(a -> {
                try {
                    //添加到spring中
                    LoadJarUtil.addToSpring(a);
                    String shortClassName = getShortClassName(a);
                    try {
                        CtField field = cc.getField(shortClassName);
                        if (field == null) {
                            //添加字段并初始化
                            addFieldAndInit(a, cc);
                        }
                    } catch (NotFoundException exception) {
                        addFieldAndInit(a, cc);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private String addContent(CtClass target, RuleDto info) {
        String ruleBody = info.getRuleContent();
        CtMethod[] methods = target.getMethods();
        for (CtMethod ctMethod : methods) {
            try {
                if (ctMethod.getName().equals(info.getTemplateMethodName())) {
                    List<String> paramsType = info.getParamsType();
                    if (CollectionUtils.isEmpty(paramsType)) {
                        ctMethod.insertAfter(ruleBody);
                    } else {
                        for (String a : paramsType) {
                            CtClass[] parameterTypes = ctMethod.getParameterTypes();
                            boolean flag = false;
                            int order = 0;
                            while (order < parameterTypes.length) {
                                if (a.equals(parameterTypes[order].getName())) {
                                    flag = true;
                                    break;
                                }
                                order++;
                            }
                            order++;
                            if (!flag) {
                                CtClass ctClass = ClassPool.getDefault().get(a);
                                ctMethod.addParameter(ctClass);
                            }
                            String paramSort = a.substring(a.lastIndexOf(".") + 1);
                            paramSort = paramSort.substring(0, 1).toLowerCase() + paramSort.substring(1);
                            String content = ruleBody.replace(paramSort, "$" + order);
                            log.info("content:{}", content);
                            ruleBody = content;
                        }
//                        CodeAttribute codeAttribute = ctMethod.getMethodInfo().getCodeAttribute();
//                        LineNumberAttribute lineNumberAttribute =
//                                (LineNumberAttribute) codeAttribute.getAttribute(LineNumberAttribute.tag);
//                        int i = lineNumberAttribute.tableLength();
//                        log.info("tableLength:{}", i);
                        String code = ctMethod.getName()+"{";

//                        int lineNumber = ctMethod.getMethodInfo().getLineNumber(0);
//                        log.info("lineNumber:{}", lineNumber);
//                        ctMethod.insertAt(6, "{" + ruleBody + "}");
                        ctMethod.insertAfter( "{" + ruleBody + "}");
                    }
                }
            } catch (Exception e) {
                log.info("代码编译失败！", e);
                throw new ResponseException("规则编译失败!");
            }
        }
        return ruleBody;
    }

    private void addFieldAndInit(String name, CtClass target) throws Exception {
        String shortClassName = getShortClassName(name);
        CtClass ctClass = ClassPool.getDefault().get(name);
        CtField f = new CtField(ctClass, shortClassName, target);
        f.setModifiers(java.lang.reflect.Modifier.PRIVATE);

        FieldInfo fieldInfo = f.getFieldInfo();
        // 属性附上注解
        ClassFile classFile = target.getClassFile();
        ConstPool constPool = classFile.getConstPool();
        AnnotationsAttribute fieldAttr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation autowired = new Annotation("com.example.rule.annotation.SpringGet", constPool);
        fieldAttr.addAnnotation(autowired);
        fieldInfo.addAttribute(fieldAttr);
        log.info("添加的属性名：{}", shortClassName);
        target.addField(f);
        Object bean = SpringUtil.getBean(name);
        if (bean == null) {
            Class<?> aClass = Class.forName(name);
            SpringUtil.getBeanFactory().registerSingleton(name, aClass);
        }
        CtConstructor[] constructors = target.getConstructors();
        if (constructors.length == 0) {
            CtConstructor ctConstructor = new CtConstructor(null, target);
            ctConstructor.setBody(shortClassName + " = com.example.rule.utils.SpringUtil.getBean(\"" + name +
                    "\");");
            target.addConstructor(ctConstructor);
        } else {
            constructors[0].insertAfter(shortClassName + " = com.example.rule.utils.SpringUtil.getBean(\"" + name +
                    "\");");
        }
    }

    private String getShortClassName(String name) {
        String classNameShort = name.substring(name.lastIndexOf(".") + 1);
        String beanName = StringUtils.uncapitalize(classNameShort);
        return beanName;
    }

    private String getCode(String name) {
        String code = name.substring(1, name.length() - 1);
        return code;
    }

    private void packRule(List<ConditionInfo> list, RuleDto request) {
        CtClass ctClass = getCtClass(request.getTemplateName(), request.getTemplateMethodName(),
                request.getRuleName(), request.getReturnType());
        List<String> fieldList = list.stream().map(ConditionInfo::getFieldType).collect(Collectors.toList());
        fieldList = getList(fieldList);
        this.addField(ctClass, fieldList);
        List<String> paramList = list.stream().map(ConditionInfo::getParamType).collect(Collectors.toList());
        paramList = getList(paramList);
        List<String> preContentList = list.stream().map(ConditionInfo::getPreContent).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        preContentList.forEach(a -> sb.append(StringUtils.isEmpty(a) ? "" : a));
        List<String> judgeContentList = list.stream().map(ConditionInfo::getJudgeContent).collect(Collectors.toList());
        Map<String, String> judgeMap = list.stream().collect(Collectors.toMap(ConditionInfo::getConditionName,
                ConditionInfo::getJudgeContent));
        List<List<String>> hitRule = request.getHitRule();
        if (CollectionUtils.isEmpty(hitRule)) {
            int hitCount = request.getHitCount();
            sb.append("int hitCount = 0;");
            judgeContentList.forEach(a -> sb.append("if(" + a + "){hitCount++;}"));
            sb.append("if(hitCount >=" + hitCount + "){" + request.getHitContent() + "}");
        } else {
            StringBuilder sb2 = new StringBuilder();
            hitRule.forEach(a -> {
                StringBuilder stringBuilder = new StringBuilder();
                a.forEach(b -> {
                    String s = judgeMap.get(b);
                    stringBuilder.append(s).append("&&");
                });
                String s = stringBuilder.toString();
                String substring = s.substring(0, s.length() - 2);
                sb2.append("(" + substring + ") ||");
            });
            String s = sb2.toString();
            String substring = s.substring(0, s.length() - 2);
            sb.append("if(" + substring + "){" + request.getHitContent() + "}");
        }
        RuleDto info = new RuleDto();
        info.setRuleContent(sb.toString());
        info.setTemplateMethodName(request.getTemplateMethodName());
        info.setParamsType(paramList);
        info.setReturnType(request.getReturnType());
        String content = this.addContent(ctClass, info);
        ctClass.setName(request.getRuleName());
        try {
            ctClass.writeFile(classPath);
        } catch (CannotCompileException e) {
            log.info("规则文件编译失败！");
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.info("规则文件写出失败！");
            throw new RuntimeException(e);
        }
//        ctClass.defrost();
        RuleInfo ruleInfo = new RuleInfo();
        ruleInfo.setId(SnowFlake.getSnowFlakeId());
        ruleInfo.setRuleName(request.getRuleName());
        ruleInfo.setTemplateName(request.getTemplateName());
        ruleInfo.setTemplateMethod(request.getTemplateMethodName());
        ruleInfo.setRuleContent(content);
        if (!StringUtils.isEmpty(request.getFieldType())) {
            ruleInfo.setFieldType(JSON.toJSONString(request.getFieldType()));
        }
        if (!StringUtils.isEmpty(request.getParamsType())) {
            ruleInfo.setParamType(JSON.toJSONString(request.getParamsType()));
        }
        if (!StringUtils.isEmpty(request.getConditionIds())) {
            ruleInfo.setConditionIds(JSON.toJSONString(request.getConditionIds()));
        }
        ruleInfo.setReturnType(request.getReturnType());
        ruleInfoMapper.insertSelective(ruleInfo);
    }

    private CtClass getCtClass(String templateName, String method, String className, String returnType) {
        CtClass cc = null;
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass zz = pool.get(templateName);
            if (zz.isInterface()) {
                cc = pool.makeClass(templateName + className);
                CtMethod declaredMethod = null;
                StringBuilder sb = new StringBuilder();
                declaredMethod = zz.getDeclaredMethod(method);
                if (returnType == null) {
                    returnType = declaredMethod.getReturnType().getName();
                }
                String initCode = getInitCode(returnType);
                sb.append("public ").append(returnType).append(" ").append(method).append(
                        "(){").append(initCode).append("}");
                CtMethod m = CtNewMethod.make(
                        sb.toString(), cc);
                cc.addMethod(m);
            } else {
                cc = zz;
            }
        } catch (Exception e) {
            log.info("解析规则错误!", e);
            throw new ResponseException("解析规则错误！");
        }
        return cc;
    }

    private String getInitCode(String returnType) {
        Map<String,String> baseMap = new HashMap<>();
        baseMap.put("int","int i = 0;return i;");
        baseMap.put("byte","byte i = 0;return i;");
        baseMap.put("double","double i = 0;return i;");
        baseMap.put("long","long i = 0;return i;");
        baseMap.put("float","float i = 0;return i;");
        baseMap.put("boolean","boolean i = false;return i;");
        baseMap.put("char","char i = 0;return i;");
        baseMap.put("short","short i = 0;return i;");
        baseMap.put("String","String i = \"\";return i;");
        baseMap.put("string","String i = \"\";return i;");
        String initCode = "";
        if (baseMap.containsKey(returnType)){
            initCode = baseMap.get(returnType);
        }else {
            initCode = returnType+" i = new "+returnType+"();";
        }
        return initCode;
    }

    private List<String> getList(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        List<String> listNew = new ArrayList<>();
        list.forEach(a -> {
            List<String> jsonList = JSON.parseArray(a, String.class);
            if (!CollectionUtils.isEmpty(jsonList)) {
                jsonList.forEach(b -> {
                    if (!listNew.contains(b)) {
                        listNew.add(b);
                    }
                });
            }
        });
        return listNew;
    }
}
