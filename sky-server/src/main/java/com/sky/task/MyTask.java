package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自定义定时任务类
 */
@Component
@Slf4j
public class MyTask {

    /**
     * 定时任务，定义一个方法：没有返回值，方法名无要求，内容中写具体的逻辑
     */
//    @Scheduled( cron = "0/5 * * * * *")
//    public void task(){
//        log.info("定时任务开始执行:{}", new Date());
//    }
}
