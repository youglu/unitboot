package org.union.sbp.springdemo.service;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class DemoService {
    public DemoService(){
        System.out.println("实始化 DemoService");
    }

    public String findNames() {
        return "a,b,c";
    }
}
