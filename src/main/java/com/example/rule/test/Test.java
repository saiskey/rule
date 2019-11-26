package com.example.rule.test;

import com.google.common.collect.Lists;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/5 17:48
 **/
public class Test {


    public static void main(String[] args) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass zz = pool.get("com.example.rule.template.RuleTemplate");
        CtClass cc = null;
        cc = pool.makeClass("TestRule2");
        CtMethod declaredMethod = null;
        StringBuilder sb = new StringBuilder();
        declaredMethod = zz.getDeclaredMethod("rule");
        String returnType = "String";
        double i = 0;
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
        }
        if (returnType == null) {
            returnType = declaredMethod.getReturnType().getName();
        }
        sb.append("public ").append(returnType).append(" ").append("rule").append(
                "(){").append(initCode).append("}");
        CtMethod m = CtNewMethod.make(
                sb.toString(), cc);
        cc.addMethod(m);
        CtMethod rule = cc.getDeclaredMethod("rule");
        CtClass xx = pool.get("com.example.rule.entity.dto.RuleTest");
        rule.addParameter(xx);
        rule.insertAfter("{System.out.println(\"aaa\");System.out.println(\"bbb\");int hitCount = 0;if($1.getCount() >" +
                " 50){hitCount++;}if($1.getCount() > 50){hitCount++;}if(hitCount >=1){return \"false\";}}");
        cc.setName("TestRule2");
        cc.writeFile("D:\\testCode\\code\\");
    }


}
