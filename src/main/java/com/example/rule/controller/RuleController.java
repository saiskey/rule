package com.example.rule.controller;

import com.alibaba.fastjson.JSON;
import com.example.rule.entity.dto.RuleDto;
import com.example.rule.entity.dto.RuleTest;
import com.example.rule.entity.vo.ResponseVo;
import com.example.rule.entity.vo.RuleResultVO;
import com.example.rule.service.RuleService;
import com.example.rule.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/5 10:09
 **/
@RequestMapping("/rule")
@RestController
@Slf4j
public class RuleController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RuleService ruleService;

    @PostMapping(value = "/addRule")
    @Transactional
    public ResponseVo addRule(@RequestBody RuleDto request) {
        ruleService.addRule(request);
        return ResponseVo.success();
    }

    @PostMapping(value = "/addCondition")
    @Transactional
    public ResponseVo addCondition(@RequestPart("json") String request, @RequestPart("file") MultipartFile file) {
        RuleDto info = JSON.parseObject(request, RuleDto.class);
        ruleService.addCondition(info, file);
        return ResponseVo.success();
    }

    @PostMapping("/run")
    public ResponseVo run(@RequestBody List<RuleDto> request) {
        List<RuleResultVO> run = ruleService.run(request);
        return ResponseVo.success(run);
    }

    @PostMapping("/login")
    public void login(@RequestBody RuleTest ruleTest) {
        Object login = redisUtil.get("loginCount");
        if (login == null) {
            redisUtil.set("loginCount", "1");
        } else {
            Integer count = Integer.valueOf(login.toString());
            count++;
            redisUtil.set("loginCount", count.toString());
        }
        redisUtil.set(ruleTest.getKey(), JSON.toJSON(ruleTest));

    }

}
