package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品和对应口味
     * @param dishDTO
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        // 涉及菜品表和口味表的操作，需要开启事务

        // 1. 向菜品表插入一条数据
        // （不传入DTO，因为菜品表中字段还有updateTime等，DTO没有，而且菜品表不需要flavors）
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        // 2. 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();

        // 获取insert语句生成的主键值
        Long dishId = dish.getId();

        // 口味表是非必填数据，先判断有无数据
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(flavor -> {flavor.setDishId(dishId);});
            dishFlavorMapper.insertBatch(flavors);
        }

    }
}
