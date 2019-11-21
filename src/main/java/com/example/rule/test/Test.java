package com.example.rule.test;

import com.example.rule.entity.dto.RuleDto;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.MethodInfo;
import org.springframework.util.StringUtils;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/5 17:48
 **/
public class Test {


    public static void main(String[] args) throws Exception {
        String templateName = "com.example.rule.template.TestRule";
        int lastIdx = templateName.lastIndexOf(".");
        String substring = templateName.substring(0, lastIdx);
        substring = substring + ".impl" + templateName.substring(lastIdx);
        System.out.println(substring);
        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath("D:\\\\testCode\\\\jar\\\\hckj-user.jar");
        CtClass cc = pool.get("com.example.rule.template.TestRule");
        CtClass ctClass = null;
        CtMethod[] methods = cc.getMethods();
        for (CtMethod ctMethod : methods) {
            try {
                if (ctMethod.getName().equals("rule")) {
                    ctMethod.insertAfter("{ Long a = com.hckj.saas.user.config.AuthConst.ROLE_PERM_TIMEOUT;" +
                            "System.out.println(a);}");
                }
            } catch (CannotCompileException e) {
                e.printStackTrace();
//                for(int j = 0;j<2;j++) {
//                    int i = ctMethod.getParameterTypes().length;
//                    ctClass = pool.get("com.example.rule.entity.dto.RuleDto");
//                    ctMethod.addParameter(ctClass);
//                    final int sort = i + 1;
//                    System.out.println(sort);
//                }
//                ctMethod.insertAfter("{ String a = $1.getName();System.out.println(a);}");
//                String string = ctMethod.getReturnType().getName();
//                CtMethod make = CtNewMethod.make("public " + string + " rule(" + "com.example.rule.entity.dto" +
//                                ".RuleDto" + " param1){ " +
//                                "return rule();}",
//                        cc);
//                cc.addMethod(make);
//                CtMethod[] newMs = cc.getMethods();
//                for (CtMethod method:newMs) {
//                    try {
//                        if (ctMethod.getName().equals("rule") && ctMethod.get)
//                            ctMethod.insertAfter("{ String a = ruleDto.getName();System.out.println(a);}");
//                    }catch (CannotCompileException ex){
//                        ex.printStackTrace();
//                        continue;
//                    }
//                }
//                System.out.println(string);
            }
        }
        cc.writeFile();
//        cc = pool.get("com.example.rule.template.TestRule");
        Class<?> aClass = cc.toClass();
        Object o = aClass.newInstance();
//        Object rule = o.getClass().getMethod("rule", RuleDto.class).invoke(o,new RuleDto());
//        System.out.println(rule);
    }
}
