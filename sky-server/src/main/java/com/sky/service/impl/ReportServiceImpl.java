package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    /**
     * 统计指定时间段的营业额
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 1. 根据返回数据TurnoverReportVO的要求，计算出dateList
        // 格式： 2022-10-01,2022-10-02,2022-10-03
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while(!begin.equals(end)){
            // 日期计算，计算指定日期的后一天对应的日期
            begin =  begin.plusDays(1);
            dateList.add(begin);
        }
        String dateString = StringUtils.join(dateList, ",");
        
        // 2. 查询出turnoverList
        // 存放每天的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 查询date日期对应的营业额数据，营业额是指：状态为“已完成”的订单金额总和
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);    // 00:00
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);      // 23:59

            // select sum(amount) from orders where order_time > beginTime and order_time < endTime and status = 5
            // 需要传入三个参数，用Map封装
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            // 假如营业额为0（这样查询结果为null，不能就这样放到list中，要设置为0）
            turnover = turnover == null ? 0 : turnover;
            turnoverList.add(turnover);
        }
        String turnoverString = StringUtils.join(turnoverList, ",");
        
        return TurnoverReportVO
                .builder()
                .dateList(dateString)
                .turnoverList(turnoverString)
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 1. 根据返回数据UserReportVO的要求，计算出dateList
        // 格式： 2022-10-01,2022-10-02,2022-10-03
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while(!begin.equals(end)){
            // 日期计算，计算指定日期的后一天对应的日期
            begin =  begin.plusDays(1);
            dateList.add(begin);
        }
        String dateString = StringUtils.join(dateList, ",");

        // 2. 查询出totalUserList
        // 存放每天总用户量
        List<Integer> totalUserList = new ArrayList<>();
        // 存放新增用户量
        List<Integer> newUserList = new ArrayList<>();

        // 根据User属性的注册时间createTime来判断
        for (LocalDate date : dateList) {
            // 查询date日期对应的总用户数据
            // 营业额是指：状态为“已完成”的订单金额总和
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);    // 00:00
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);      // 23:59

            // 新增用户 select count(id) from user where create_time > beginTime and create_time < endTime
            // 总用户 select count(id) from user where create_time < endTime
            // 需要传入三个参数，用Map封装
            Map map = new HashMap();
            map.put("endTime", endTime);

            Integer totalUser = userMapper.countByMap(map); // 当天新增用户
            // 假如营业额为0（这样查询结果为null，不能就这样放到list中，要设置为0）
            totalUser = totalUser == null ? 0 : totalUser;


            map.put("beginTime", beginTime);
            Integer newUser = userMapper.countByMap(map); // 总用户
            newUser = newUser == null ? 0 : newUser;

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        String totalUserString = StringUtils.join(totalUserList, ",");
        String newUserString = StringUtils.join(newUserList, ",");

        return UserReportVO
                .builder()
                .dateList(dateString)
                .totalUserList(totalUserString)
                .newUserList(newUserString)
                .build();
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 1. 封装dateList
        List<LocalDate> dateList = new ArrayList();
        dateList.add(begin);
        while(!begin.equals(end)){
            // 日期计算，计算指定日期的后一天对应的日期
            begin =  begin.plusDays(1);
            dateList.add(begin);
        }
        String dateString = StringUtils.join(dateList, ",");

        // 2. 封装订单数列表(每天订单数和有效订单数）
        // 存放
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("beginTime", beginTime);
            map.put("endTime", endTime);

            // 查询每天订单总数：下单时间为当天
            // select count(id) from orders where order_time < endTime and order_time > beginTime
            Integer orderNumber = orderMapper.countByMap(map);
            orderNumber = orderNumber == null ? 0 : orderNumber;

            // 查询每天有效订单数
            // select count(id) from orders where order_time < endTime and order_time > beginTime and status = COMPLETED
            map.put("status", Orders.COMPLETED);
            Integer validOrderNumber = orderMapper.countByMap(map);
            validOrderNumber = validOrderNumber == null ? 0 : validOrderNumber;

            orderCountList.add(orderNumber);
            validOrderCountList.add(validOrderNumber);
        }
        // 计算这段时间内的订单总数和有效订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            // 订单完成率
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();
        }

        String orderCountString = StringUtils.join(orderCountList, ",");
        String validOrderCountString = StringUtils.join(validOrderCountList, ",");

        return OrderReportVO
                .builder()
                .dateList(dateString)
                .orderCountList(orderCountString)
                .validOrderCountList(validOrderCountString)
                .validOrderCount(validOrderCount)
                .totalOrderCount(totalOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {

        // 要查两张表：从order_detail表中查菜品份数，从orders表中保证订单已完成、未被取消
        // select od.name, sum(od.number) from order_detail od, orders o
        // where od.order_id = o.id
        //      and o.status = 5
        //      and o.order_time between (beginTime, endTime)
        // group by od.name
        // order by sum(number) desc
        // limit 0,10

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10List = orderMapper.getSalesTop(beginTime, endTime);
        List<String> nameList = salesTop10List.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = salesTop10List.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        String nameString = StringUtils.join(nameList, ",");
        String numberString = StringUtils.join(numberList, ",");

        return SalesTop10ReportVO
                .builder()
                .nameList(nameString)
                .numberList(numberString)
                .build();

    }
}
