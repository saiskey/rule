package com.example.rule.config;

import com.example.rule.utils.LoadJarUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class ContextFinishListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${rule.class.path}")
    private String classPath;
    @Value("${rule.jar.path}")
    private String jarPath;


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("容器完成监听启动！");
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {//保证只执行一次
            //加载jar
            File file = new File(jarPath);
            if (file.exists()) {
                File[] files = file.listFiles();
                for (File jar : files) {
                    if (jar.getName().endsWith(".jar")) {
                        LoadJarUtil.loadJarLocal(jar);
                    }
                }
            }
            //加载类
            file = new File(classPath);
            if (file.exists()) {
                LoadJarUtil.loadJarLocal(file);
            }
            file = new File(classPath);
            loadToSpring(file);
        }
    }

    private void loadToSpring(File file) {
        File[] classList = file.listFiles();
        for (File cc : classList) {
            if (cc.isFile() && cc.getName().endsWith(".class")) {
                String replace = cc.getAbsolutePath().replace(classPath, "").replace("\\", ".").replace(".class", "");
                log.info(replace);
                LoadJarUtil.addToSpring(replace);
            } else {
                loadToSpring(cc);
            }
        }
    }
}
