package org.union.sbp.springdemo2.service;

import org.springframework.stereotype.Component;

@Component
public class DemoService {
    public DemoService(){
        System.out.println("实始化 DemoService");
    }

    public String findNames() {
        return "a,b,c，D";
    }
}
