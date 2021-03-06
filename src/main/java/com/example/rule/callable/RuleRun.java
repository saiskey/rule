package com.example.rule.callable;

import com.alibaba.fastjson.JSON;
import com.example.rule.entity.dto.RuleDto;
import com.example.rule.entity.vo.RuleResultVO;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class RuleRun implements Callable<RuleResultVO> {

    private RuleDto ruleDto;

    public RuleRun(RuleDto ruleDto) {
        this.ruleDto = ruleDto;
    }

    @Override
    public RuleResultVO call() {
        RuleResultVO resultVO = new RuleResultVO();
        resultVO.setRuleName(ruleDto.getRuleName());
        try {
            Class<?> aClass1 = Class.forName(ruleDto.getRuleName());
            Object o = aClass1.newInstance();
            Map<String, String> params = ruleDto.getParams();
            Object rule = null;
            if (CollectionUtils.isEmpty(params)) {
                rule = aClass1.getDeclaredMethod(ruleDto.getTemplateMethodName()).invoke(o);
            } else {
                for (Method ctMethod : aClass1.getMethods()) {
                    if (ctMethod.getName().equals(ruleDto.getTemplateMethodName())) {
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
            if (rule == null) {
                resultVO.setRuleResult(null);
            } else if (rule.getClass().isPrimitive()) {
                resultVO.setRuleResult(rule + "");
            } else {
                resultVO.setRuleResult(JSON.toJSONString(rule));
            }
            return resultVO;
        } catch (Exception e) {
            log.info("规则执行错误！", e);
            resultVO.setRuleResult("规则不通过！");
            return resultVO;
        }
    }
}
