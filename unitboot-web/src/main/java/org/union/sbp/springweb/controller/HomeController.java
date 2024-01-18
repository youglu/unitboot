package org.union.sbp.springweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.union.sbp.springweb.service.DemoService;

@RestController
@RequestMapping(path = "/web")
public class HomeController {

    public HomeController(){
        System.out.println("初始化HomeController...");
    }

    @Autowired
    private DemoService demoService;

    @RequestMapping(value = "/home2", method = RequestMethod.GET)
    public String home() {
        return "Hello2，Spring Boot:"+demoService;
    }

}