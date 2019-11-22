package com.example.rule.service;

import com.example.rule.entity.dto.ConditionDto;
import com.example.rule.entity.dto.RuleDto;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/22 10:57
 **/
public interface RuleService {

    void addRule(RuleDto request);

    void addCondition(ConditionDto info, MultipartFile file);

    boolean run(RuleDto request);
}
