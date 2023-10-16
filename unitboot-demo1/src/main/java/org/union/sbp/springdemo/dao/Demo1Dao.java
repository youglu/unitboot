package org.union.sbp.springdemo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.union.sbp.springdemo.model.po.Demo1;

import java.util.List;
import java.util.Map;

@Mapper
public interface Demo1Dao {
    List<Demo1> list(@Param(value = "params")Map params);
}
