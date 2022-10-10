package org.union.sbp.springdemo2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.union.sbp.springdemo2.service.DemoService;

@RestController
@RequestMapping(path = "/demo2")
public class HomeController {

    @Autowired
    private DemoService demoService;

    public HomeController(){
        System.out.println("实例化HomeController,demoService");
    }
    @RequestMapping(value = "/home2", method = RequestMethod.GET)
    public String home() {
        return "Hello2，Spring Boot:"+demoService;
    }

}