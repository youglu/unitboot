package org.union.sbp.unitmanager.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.union.sbp.unitmanager.service.UnitService;

import java.util.List;

@RestController
@RequestMapping(path = UnitInfoController.HomeController_PATH)
public class UnitInfoController {

    public static final String HomeController_PATH = "/unitManager/unitinfo";//${unitName:}

    @Autowired
    private UnitService unitService;

    @RequestMapping(value = "/unitlist", method = RequestMethod.GET)
    public List<JSONObject> unitlist() {
        List<JSONObject> unitList = unitService.fetchAllUnit();
        return unitList;
    }

}