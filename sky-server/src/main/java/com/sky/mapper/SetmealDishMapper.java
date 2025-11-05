package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品Id查询套餐id
     * 有可能查到多个套餐，用List来接收
     */
    List<Long> getSetmealDishIdsByDishId(List<Long> dishIds);
}
