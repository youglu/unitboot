package org.union.sbp.springbase.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/basetest")
public class BaseTestController {
    public Class subClass;
    public Object subObj;

    public void setSubClass(final Class subClass){
        this.subClass = subClass;
    }

    public void setSubObj(Object subObj) {
        this.subObj = subObj;
    }
    @GetMapping("/classinfo")
    public String info(){
        StringBuilder info = new StringBuilder();
        if(null != subClass){
            info.append(subClass.toString()+":"+subClass.getClassLoader()).append("<br/>");
        }
        if(null != subObj){
            info.append(subObj.toString()+":"+subObj.getClass().getClassLoader()).append("<br/>");
        }
        return info.toString();
    }
}
