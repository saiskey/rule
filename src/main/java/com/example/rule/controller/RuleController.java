package com.example.rule.controller;

import com.alibaba.fastjson.JSON;
import com.example.rule.entity.dto.ConditionDto;
import com.example.rule.entity.dto.RuleDto;
import com.example.rule.entity.pojo.ConditionInfo;
import com.example.rule.entity.pojo.RuleClassInfo;
import com.example.rule.entity.pojo.RuleInfo;
import com.example.rule.mapper.ConditionInfoMapper;
import com.example.rule.mapper.RuleClassInfoMapper;
import com.example.rule.mapper.RuleInfoMapper;
import com.example.rule.utils.LoadJarUtil;
import com.example.rule.utils.RedisUtil;
import com.example.rule.utils.SnowFlake;
import com.example.rule.utils.SpringUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/5 10:09
 **/
@RequestMapping("/rule")
@RestController
@Slf4j
public class RuleController {

    @Autowired
    private RuleInfoMapper ruleInfoMapper;
    @Autowired
    private RuleClassInfoMapper ruleClassInfoMapper;
    @Autowired
    private ConditionInfoMapper conditionInfoMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${rule.class.path}")
    private String classPath;

    @PostMapping(value = "/addRule")
    @Transactional
    public String addRule(@RequestBody RuleDto request) {
        int byName = ruleInfoMapper.findCountByName(request.getRuleName());
        if (byName > 0) {
            return "规则名已存在";
        }
        RuleInfo ruleInfo = new RuleInfo();
        ruleInfo.setId(SnowFlake.getSnowFlakeId());
        ruleInfo.setRuleName(request.getRuleName());
        ruleInfo.setTemplateName(request.getTemplateName());
        ruleInfo.setTemplateMethod(request.getTemplateMethodName());
        ruleInfoMapper.insertSelective(ruleInfo);
        return "200";
    }

    @PostMapping(value = "/addCondition")
    @Transactional
    public String addCondition(@RequestPart("json") String request, @RequestPart("file") MultipartFile file) throws Exception {
        ConditionDto info = JSON.parseObject(request, ConditionDto.class);
        int count = conditionInfoMapper.findByName(info.getConditionName());
        if (count > 0) {
            return "条件名已存在";
        }
        RuleInfo ruleInfo = ruleInfoMapper.selectByPrimaryKey(info.getRuleId());
        if (ruleInfo == null) {
            return "规则不存在";
        }
        info.setTemplateName(ruleInfo.getTemplateName());
        info.setTemplateMethodName(ruleInfo.getTemplateMethod());
        info.setRuleName(ruleInfo.getRuleName());
        if (file != null) {
            File outFile = new File("D:\\testCode\\jar\\", file.getOriginalFilename());
            FileOutputStream outputStream = new FileOutputStream(outFile);
            outputStream.write(file.getBytes());
            outputStream.flush();
            outputStream.close();
            LoadJarUtil.loadJarLocal(outFile);
        }
        ClassPool pool = ClassPool.getDefault();
        CtClass zz = null;
        try {
            zz = pool.get(ruleInfo.getTemplateName() + info.getRuleName());
        } catch (NotFoundException e) {
            zz = pool.get(ruleInfo.getTemplateName());
        }
        CtClass cc = zz;
        List<String> fieldType = info.getFieldType();
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
        addCondition(cc, info);
        cc.setName(info.getTemplateName() + info.getRuleName());
        cc.writeFile(classPath);
        cc.defrost();
        ConditionInfo conditionInfo = new ConditionInfo();
        conditionInfo.setId(SnowFlake.getSnowFlakeId());
        conditionInfo.setConditionName(info.getConditionName());
        conditionInfo.setRuleId(info.getRuleId());
        conditionInfo.setFieldType(JSON.toJSONString(info.getFieldType()));
        conditionInfo.setParamType(JSON.toJSONString(info.getParamsType()));
        conditionInfo.setConditionContent(info.getConditionContent());
        conditionInfoMapper.insertSelective(conditionInfo);
        updateRule(ruleInfo, info);
        return "200";
    }

    @PostMapping("/run")
    public String run(@RequestBody RuleDto ruleDto) throws Exception {
        try {
            RuleInfo ruleInfo = ruleInfoMapper.findOneByName(ruleDto.getRuleName());
            if (ruleInfo == null) {
                return "规则不存在";
            }
            Class<?> aClass1 = Class.forName(ruleInfo.getTemplateName() + ruleInfo.getRuleName());
            Object o = aClass1.newInstance();
            Map<String, String> params = ruleDto.getParams();
            Object rule = null;
            if (CollectionUtils.isEmpty(params)) {
                aClass1.getDeclaredMethod(ruleDto.getRuleName()).invoke(o);
            } else {
                for (Method ctMethod : aClass1.getMethods()) {
                    if (ctMethod.getName().equals(ruleDto.getRuleName())) {
                        List<Object> paramsList = new ArrayList<>();
                        params.forEach((a, b) -> {
                            try {
                                Class<?> aClass3 = Class.forName(a);
                                Object object = JSON.parseObject(b, aClass3);
                                paramsList.add(object);
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        });
                        ctMethod.invoke(o, paramsList.toArray());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "200";
    }

    @PostMapping("/login")
    public void login(@RequestBody RuleDto ruleDto) throws Exception {
        Object login = redisUtil.get("loginCount");
        if (login == null) {
            redisUtil.set("loginCount", "1");
        } else {
            Integer count = Integer.valueOf(login.toString());
            count++;
            redisUtil.set("loginCount", count.toString());
        }
        redisUtil.set("loginName", ruleDto.getRuleName());
    }

    private String getImplName(String name) {
        int lastIdx = name.lastIndexOf(".");
        String implName = name.substring(0, lastIdx);
        implName = implName + ".impl" + name.substring(lastIdx) + "Impl";
        return implName;
    }

    private void addCondition(CtClass target, ConditionDto info) throws Exception {
        CtMethod[] methods = target.getMethods();
        for (CtMethod ctMethod : methods) {
            if (ctMethod.getName().equals(info.getTemplateMethodName())) {
                List<String> paramsType = info.getParamsType();
                if (CollectionUtils.isEmpty(paramsType)) {
                    ctMethod.insertAfter(info.getConditionContent());
                } else {
                    paramsType.forEach(a -> {
                        try {
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
                            info.setConditionContent(info.getConditionContent().replace(paramSort, "$" + order));
                            log.info("ruleBody:{}", info.getConditionContent());
                            ctMethod.insertAfter(info.getConditionContent());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                }
                ctMethod.setName(info.getRuleName());
            }
        }
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
        Annotation autowired = new Annotation("com.example.rule.annotation.SpringGet",constPool);
        fieldAttr.addAnnotation(autowired);
        fieldInfo.addAttribute(fieldAttr);
        log.info("添加的属性名：{}", shortClassName);
        target.addField(f);
        Object bean = SpringUtil.getBean(name);
        if (bean == null) {
            Class<?> aClass = Class.forName(name);
            SpringUtil.getBeanFactory().registerSingleton(name,aClass);
        }
        CtConstructor[] constructors = target.getConstructors();
        constructors[0].insertAfter(shortClassName + " = com.example.rule.utils.SpringUtil.getBean(\"" + name +
                "\");");
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

    private void updateRule(RuleInfo ruleInfo, ConditionDto info) {
        updateFieldType(ruleInfo, info);
        updateParamType(ruleInfo, info);
        String ruleContent = ruleInfo.getRuleContent();
        if (StringUtils.isEmpty(ruleContent)) {
            ruleInfo.setRuleContent(info.getConditionContent());
        } else {
            ruleContent = "{" + getCode(ruleContent) + getCode(info.getConditionContent()) + "}";
            ruleInfo.setRuleContent(ruleContent);
        }
        ruleInfoMapper.updateByPrimaryKeySelective(ruleInfo);
    }

    private void updateParamType(RuleInfo ruleInfo, ConditionDto info) {
        if (CollectionUtils.isEmpty(info.getFieldType())) {
            return;
        }
        String fieldType = ruleInfo.getFieldType();
        if (StringUtils.isEmpty(fieldType)) {
            ruleInfo.setFieldType(JSON.toJSONString(info.getFieldType()));
        } else {
            List<String> fieldTypeList = JSON.parseArray(fieldType, String.class);
            info.getFieldType().forEach(a -> {
                if (!fieldTypeList.contains(a)) {
                    fieldTypeList.add(a);
                }
            });
            ruleInfo.setFieldType(JSON.toJSONString(info.getFieldType()));
        }
    }

    private void updateFieldType(RuleInfo ruleInfo, ConditionDto info) {
        if (CollectionUtils.isEmpty(info.getParamsType())) {
            return;
        }
        String paramType = ruleInfo.getParamType();
        if (StringUtils.isEmpty(paramType)) {
            ruleInfo.setParamType(JSON.toJSONString(info.getParamsType()));
        } else {
            List<String> list = JSON.parseArray(paramType, String.class);
            info.getParamsType().forEach(a -> {
                if (!list.contains(a)) {
                    list.add(a);
                }
            });
            ruleInfo.setFieldType(JSON.toJSONString(info.getParamsType()));
        }
    }

    private void updateClassImpl(RuleInfo ruleInfo, ConditionDto info) {
        RuleClassInfo byTemplateMethod = null;
        if (byTemplateMethod == null) {
            RuleClassInfo ruleClassInfo = new RuleClassInfo();
            ruleClassInfo.setId(SnowFlake.getSnowFlakeId());
            ruleClassInfo.setClassName(ruleInfo.getTemplateName());
            ruleClassInfo.setMethodName(ruleInfo.getTemplateMethod());
            ruleClassInfo.setCodeBody(info.getConditionContent());
            ruleClassInfo.setMethodParamType(JSON.toJSONString(info.getParamsType()));
            ruleClassInfo.setFieldType(JSON.toJSONString(info.getFieldType()));
            ruleClassInfoMapper.insertSelective(ruleClassInfo);
        } else {
            String codeBody = byTemplateMethod.getCodeBody();
            codeBody = codeBody.substring(1, codeBody.length() - 1);
            String ruleContent = info.getConditionContent();
            ruleContent = ruleContent.substring(1, ruleContent.length() - 1);
            codeBody = codeBody + ruleContent;
            RuleClassInfo ruleClassInfo = new RuleClassInfo();
            ruleClassInfo.setId(byTemplateMethod.getId());
            ruleClassInfo.setCodeBody("{" + codeBody + "}");
            if (!CollectionUtils.isEmpty(info.getFieldType())) {
                String fieldTypes = byTemplateMethod.getFieldType();
                if (StringUtils.isEmpty(fieldTypes)) {
                    ruleClassInfo.setFieldType(JSON.toJSONString(info.getFieldType()));
                } else {
                    List<String> strings = JSON.parseArray(fieldTypes, String.class);
                    strings.addAll(info.getFieldType());
                    ruleClassInfo.setFieldType(JSON.toJSONString(strings));
                }

                String paramType = byTemplateMethod.getMethodParamType();
                if (StringUtils.isEmpty(paramType)) {
                    ruleClassInfo.setFieldType(JSON.toJSONString(info.getFieldType()));
                } else {
                    List<String> strings = JSON.parseArray(paramType, String.class);
                    strings.addAll(info.getParamsType());
                    ruleClassInfo.setFieldType(JSON.toJSONString(strings));
                }
                ruleClassInfoMapper.updateByPrimaryKeySelective(ruleClassInfo);
            }
        }
    }
}
