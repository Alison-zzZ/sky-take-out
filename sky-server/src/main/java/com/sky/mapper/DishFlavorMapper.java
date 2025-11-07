package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    /**
     * 新增菜品口味
     */
    @AutoFill(OperationType.INSERT)
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除口味数据
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id批量删除关联的口味
     */
    void deleteByDishIds(List<Long> ids);

    /**
     * 根据菜品id查询关联的口味数据
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);
}
