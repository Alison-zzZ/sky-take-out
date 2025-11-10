package com.sky.mapper;

import com.sky.entity.AddressBook;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {


    /**
     * 根据openid查用户
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 插入用户，*需要返回主键值*
     * @param user
     */
    void insert(User user);

    /**
     * 根据id查询
     */
    @Select("select * from user where id = #{userid}")
    User getById(Long userId);
}
