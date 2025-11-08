package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    // 微信服务接口地址
    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 微信登录
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 1. 调用微信服务器接口，获得微信用户的openid
        // 1.1 用map封装请求参数
//        Map<String,String> map = new HashMap<>();
//        map.put("appid",weChatProperties.getAppid());
//        map.put("secret",weChatProperties.getSecret());
//        map.put("js_code",userLoginDTO.getCode());
//        map.put("grant_type","authorization_code");
//        String json = HttpClientUtil.doGet(WX_LOGIN, map);
//
//        // 1.2 解析微信服务器的返回数据
//        JSONObject jsonObject = JSON.parseObject(json);
//        String openid = jsonObject.getString("openid");

        // 1. 获取openid，调用下面写的函数
        String openid = getOpenid(userLoginDTO.getCode());

        // 2. 判断openid是否为空，如果为空表示登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 3. 判断当前用户是否为外卖系统新用户（openid是否在用户表中）
        // 如果是新用户，自动完成注册（封装成User对象，保存到用户表中）
        User user = new User();
        if(userMapper.getByOpenid(openid) == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 4. 返回用户对象
        return user;
    }

    private String getOpenid(String code){
        Map<String,String> map = new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);

        // 1.2 解析微信服务器的返回数据
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }

}
