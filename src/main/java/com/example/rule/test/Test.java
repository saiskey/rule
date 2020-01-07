package com.example.rule.test;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/5 17:48
 **/
public class Test {


    public static void main(String[] args) throws Exception {

        //获取容器，返回的默认ClassPool搜索与底层JVM（Java虚拟机）具有相同的路径。
        ClassPool pool = ClassPool.getDefault();
        //指定加载路径
        pool.insertClassPath("");
        //从容器中获取指定类
        CtClass zz = pool.get("com.example.rule.template.RuleTemplate");
        CtClass cc = null;
        //创建类
        cc = pool.makeClass("TestRule2");
        //获取一个类的指定方法无参（有参需要传参数类型 CtClass[]）
        CtMethod declaredMethod  = zz.getDeclaredMethod("rule");
        String returnType = "String";
        String initCode = getInitCode(returnType);
        if (returnType == null) {
            returnType = declaredMethod.getReturnType().getName();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("public ").append(returnType).append(" ").append("rule").append(
                "(){").append(initCode).append("}");
        //创建方法
        CtMethod m = CtNewMethod.make(sb.toString(), cc);
        //为这个类添加方法
        cc.addMethod(m);
        CtMethod rule = cc.getDeclaredMethod("rule");
        CtClass xx = pool.get("com.example.rule.entity.dto.RuleTest");
        //为一个方法添加参数
        rule.addParameter(xx);
        //为一个方法添加代码
        rule.insertAfter("{System.out.println(\"aaa\");System.out.println(\"bbb\");int hitCount = 0;if($1.getCount() " +
                ">" +
                " 50){hitCount++;}if($1.getCount() > 50){hitCount++;}if(hitCount >=1){return \"false\";}}");
        //设置类名
        cc.setName("TestRule2");
        //输出class文件
        cc.writeFile("D:\\testCode\\code\\");
    }


    private static String getInitCode(String returnType) {
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("int", "int i = 0;return i;");
        baseMap.put("byte", "byte i = 0;return i;");
        baseMap.put("double", "double i = 0;return i;");
        baseMap.put("long", "long i = 0;return i;");
        baseMap.put("float", "float i = 0;return i;");
        baseMap.put("boolean", "boolean i = false;return i;");
        baseMap.put("char", "char i = 0;return i;");
        baseMap.put("short", "short i = 0;return i;");
        baseMap.put("String", "String i = \"\";return i;");
        baseMap.put("string", "String i = \"\";return i;");
        baseMap.put("void", "");
        String initCode = "";
        if (baseMap.containsKey(returnType)) {
            initCode = baseMap.get(returnType);
        } else {
            initCode = returnType + " i = new " + returnType + "();";
        }
        return initCode;
    }
}
