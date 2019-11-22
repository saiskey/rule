package com.example.rule.template;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/7 17:40
 **/
public class TestRule {
    private int a;

    public boolean rule() {
        boolean flag = true;

        return flag;
    }

    public int test(int b) {
        int i = 5;
        return i + b;
    }
}
