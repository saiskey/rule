package com.example.rule.service;

import com.example.rule.entity.dto.RuleDto;
import com.example.rule.entity.vo.RuleResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/22 10:57
 **/
public interface RuleService {

    void addRule(RuleDto request);

    void addCondition(RuleDto info, MultipartFile file);

    List<RuleResultVO> run(List<RuleDto> request);
}
