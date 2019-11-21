package com.example.rule.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext appCtx;

    /**
     * 获取ApplicationContext
     *
     * @return
     */
    public static ApplicationContext getAppCtx() {
        return appCtx;
    }


    /**
     * 根据beanName获取bean
     *
     * @param name beanName
     * @return bean
     */
    public static Object getBean(String name) {
        Object bean = null;
        try {
            bean = appCtx.getBean(name);
        } catch (Exception exception) {
            log.info("spring 容器获取{}失败！", name);
        }
        return bean;
    }

    public static boolean exist(String name) {
        Object bean = SpringUtil.getBean(name);
        return bean != null;
    }


    /**
     * 根据class获取bean
     *
     * @param clazz class对象
     * @return bean
     */
    public static <T> T getBean(Class<T> clazz) {
        return getAppCtx().getBean(clazz);
    }

    public static DefaultListableBeanFactory getBeanFactory() {
        //将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext =
                (ConfigurableApplicationContext) appCtx;

        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

//        // 通过BeanDefinitionBuilder创建bean定义
//        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(UserController
//        .class);
        return defaultListableBeanFactory;
    }

    /**
     * 通过name,以及class返回指定的Bean
     *
     * @param name  beanName
     * @param clazz class对象
     * @param <T>   泛型，返回的bean类型
     * @return bean
     */

    public static <T> T getBean(String name, Class<T> clazz) {
        return getAppCtx().getBean(name, clazz);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appCtx = applicationContext;
    }
}