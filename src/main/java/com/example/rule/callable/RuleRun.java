package com.example.rule.callable;

import com.alibaba.fastjson.JSON;
import com.example.rule.entity.dto.RuleDto;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/22 11:43
 **/
public class RuleRun implements Callable<Boolean> {

    private RuleDto ruleDto;

    public RuleRun(RuleDto ruleDto) {
        this.ruleDto = ruleDto;
    }

    @Override
    public Boolean call() throws Exception {
        try {
            Class<?> aClass1 = Class.forName(ruleDto.getTemplateName() + ruleDto.getRuleName());
            Object o = aClass1.newInstance();
            Map<String, String> params = ruleDto.getParams();
            Object rule = null;
            if (CollectionUtils.isEmpty(params)) {
                rule = aClass1.getDeclaredMethod(ruleDto.getRuleName()).invoke(o);
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
                        rule = ctMethod.invoke(o, paramsList.toArray());
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
