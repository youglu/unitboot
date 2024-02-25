/*
 * Copyright (C) 2015 - 2030 union.Inc All Rights Reserved.
 * union.Inc PROPRIETARY/CONFIDENTIAL.Use is subject to license terms.
 */
package org.union.sbp.unitmanager.service.impl;

import com.alibaba.fastjson2.JSONObject;
import org.eclipse.osgi.framework.internal.core.AbstractBundle;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.springframework.stereotype.Component;
import org.union.sbp.unitmanager.service.UnitService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 单元服务实现类 <br/>
 * @Author: youg <br/>
 * @SINCE: JDK1.8 <br/>
 * @Date: 2024-02-22 18:07 <br/>
 **/
@Component
public class UnitServiceImpl implements UnitService {
    /**
     * @description:
     * @author: youg
     * @date: 2024-02-24 10:22
     * @param: []
     * @return: java.util.List<com.alibaba.fastjson2.JSONObject>
     **/ 
    public List<JSONObject> fetchAllUnit() {
        List<JSONObject> bundleInfoList = new ArrayList<>();
        BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
        Bundle[] bundles = context.getBundles();
        for(Bundle bundle:bundles){
            //if(bundle instanceof  BundleHost) {
                JSONObject jsonUnit = new JSONObject();
                jsonUnit.put("unitName", bundle.getSymbolicName());
                jsonUnit.put("startLevel", ((AbstractBundle) bundle).getStartLevel());
                bundleInfoList.add(jsonUnit);
            //}
        }
        return bundleInfoList;
    }
}
