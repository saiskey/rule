package com.example.rule.utils;

import com.example.rule.annotation.SpringGet;
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

    public static void initToSpring(String name) {
        if (SpringUtil.exist(name)) {
            return;
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
            if (field.isAnnotationPresent(SpringGet.class)) {
                try {
                    Class<?> type = field.getType();
                    Field[] declaredFields = type.getDeclaredFields();
                    o = type.newInstance();
                    field.setAccessible(true);
                    for (Field f : declaredFields) {
                        if (f.isAnnotationPresent(Autowired.class)) {
                            Object fieldBean = SpringUtil.getBean(f.getName());
                            f.setAccessible(true);
                            f.set(o, fieldBean);
                            f.setAccessible(false);
                        }
                    }
                    log.info("手动添加spring bean:{}", type.getName());
                    SpringUtil.getBeanFactory().registerSingleton(type.getName(), o);
                } catch (Exception e) {
                    log.info("手动添加spring bean失败！", e);
                }
                field.setAccessible(false);
            }
        }
    }

    public static void addToSpring(String name) {
        if (SpringUtil.exist(name)) {
            return;
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
                    Object fileBean = SpringUtil.getBean(field.getName());
                    field.setAccessible(true);
                    field.set(o, fileBean);
                    log.info("手动添加spring bean:{}", name);
                    SpringUtil.getBeanFactory().registerSingleton(name, o);
                } catch (Exception e) {
                    log.info("手动添加spring bean失败！");
                    throw new RuntimeException(e);
                }
                field.setAccessible(false);
            }
        }
    }
}
