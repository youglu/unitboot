package org.union.sbp.springdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.union.sbp.springdemo.dao.Demo1Dao;
import org.union.sbp.springdemo.model.po.Demo1;

import java.util.List;
import java.util.Map;

@Component
public class DemoService {
    @Autowired
    private Demo1Dao demo1Dao;

    public DemoService(){
        System.out.println("实始化 DemoService");
    }

    public String findNames(Map params) {
        List<Demo1> demo1List = demo1Dao.list(params);
        return "1,a,b,c"+demo1List;
    }
}
