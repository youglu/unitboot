package org.union.sbp.springdemo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.union.sbp.springdemo.service.DemoService;

@Api(tags = "SpringBoot单元Demo1")
@RestController
@RequestMapping(path = "/demo1")
public class HomeController {

    @Autowired
    private DemoService demoService;

    public HomeController() {
        System.out.println("实例化HomeController,demoService");
    }

    @ApiOperation(value = "home方法")
    @ApiImplicitParam(name = "name", value = "姓名", required = true)
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(@RequestParam(value = "name") String name) {
        return "Hello，Spring Boot:" + demoService;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "标识", required = true),
            @ApiImplicitParam(name = "version", value = "版本", required = true)
    })
    @ApiOperation(value = "获得name列表")
    @RequestMapping(value = "/names", method = RequestMethod.GET)
    public String names(@RequestParam(value = "name") String name, @RequestParam(value = "version") int version) {
        return demoService.findNames();
    }

    @ApiImplicitParams({@ApiImplicitParam(name = "name", value = "标识", required = true),
            @ApiImplicitParam(name = "version", value = "版本", required = true)})
    @ApiOperation(value = "服务名")
    @RequestMapping(value = "/serviceName", method = RequestMethod.GET)
    public String serviceName(@RequestParam(value = "name") String name, @RequestParam(value = "version") int version) {
        return demoService.findNames();
    }
}