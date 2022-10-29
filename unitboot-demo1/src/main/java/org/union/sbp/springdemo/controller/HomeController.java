package org.union.sbp.springdemo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.union.sbp.springdemo.service.DemoService;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "SpringBoot单元Demo1")
@RestController
@RequestMapping(path = HomeController.HomeController_PATH)
public class HomeController {
    public static final String HomeController_PATH = "/demo1";//${unitName:}
    @Autowired
    private Environment environment;

    @Autowired
    private DemoService demoService;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    public HomeController() {
        System.out.println("实例化HomeController,demoService");
    }

    @ApiOperation(value = "home方法")
    @ApiImplicitParam(name = "name", value = "姓名", required = true)
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(@RequestParam(value = "name") String name) {
        return "Hello，Spring Boot:" + demoService
                +",uname="+environment.getProperty("uname")
                +",unitboot:"+environment.getProperty("unitboot")
                +",unitName:"+environment.getProperty("unitName")
                +",env:"+environment
                +",dataSourceProperties:"+dataSourceProperties.getUrl();
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "标识", required = true),
            @ApiImplicitParam(name = "version", value = "版本", required = true)
    })
    @ApiOperation(value = "获得name列表")
    @RequestMapping(value = "/names", method = RequestMethod.GET)
    public String names(@RequestParam(value = "name") String name, @RequestParam(value = "version") int version) {
        Map params = new HashMap();
        return demoService.findNames(params);
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "标识", required = true),
            @ApiImplicitParam(name = "version", value = "版本", required = true)})
    @ApiOperation(value = "服务名")
    @RequestMapping(value = "/serviceName", method = RequestMethod.GET)
    public String serviceName(@RequestParam(value = "name") String name, @RequestParam(value = "version") int version) {
        Map params = new HashMap();
        return demoService.findNames(params);
    }
}