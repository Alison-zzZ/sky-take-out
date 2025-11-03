package com.sky.annotation;

import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识需要进行公共字段自动填充的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 枚举类指定需要被拦截的操作
    // 数据库操作类型：update insert
    OperationType value();
}
