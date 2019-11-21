package com.example.rule.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/12 18:53
 **/
@Slf4j
public class LoadJarUtil {


    public static void loadJarLocal(File file) {
        Method method = null;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        } catch (NoSuchMethodException | SecurityException e1) {
            e1.printStackTrace();
        }
        // 获取方法的访问权限以便写回
        boolean accessible = method.isAccessible();
        try {
            method.setAccessible(true);
            // 获取系统类加载器
            URLClassLoader classLoader = (URLClassLoader) URLClassLoader.getSystemClassLoader();
            URL url = file.toURI().toURL();
            method.invoke(classLoader, url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            method.setAccessible(accessible);
        }
    }

    public static void addToSpring(String name) {
        log.info("开始添加到spring");
        Object bean = null;
        try {
            Class<?> springClass = Class.forName("com.example.rule.utils.SpringUtil");
            ClassLoader classLoaderSpring = springClass.getClassLoader();
            log.info("classLoaderSpring:{}",classLoaderSpring);
            bean = SpringUtil.getBean(name);
            if (bean != null) {
                log.info("{}在spring中已经存在！", name);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("获取bean失败！{}", name);
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        Field[] fields = clazz.getDeclaredFields();
        Object o = null;
        for (Field field : fields) {
            boolean primitive = field.getType().isPrimitive();
            if (primitive) {
                continue;
            }
            if (field.isAnnotationPresent(Autowired.class)) {
                try {
                    o = clazz.newInstance();
                    log.info(field.getName());
                    bean = SpringUtil.getBean(field.getName());
                    field.setAccessible(true);
                    field.set(o, bean);
                    SpringUtil.getBeanFactory().registerSingleton(name, o);
                } catch (Exception e) {
                    log.info("手动添加Autowired失败！", e);
                }
                field.setAccessible(false);
            }
        }
    }
}
