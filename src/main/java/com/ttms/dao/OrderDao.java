package com.ttms.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ttms.enums.OrderStatus;
import com.ttms.enums.TicketStatus;
import com.ttms.pojo.Order;
import com.ttms.pojo.Plan;
import com.ttms.pojo.Ticket;
import com.ttms.tools.TimeUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class OrderDao {

    public boolean saveOrder(Order order) {
        return Db.save(order);
    }

    public List<Order> getOrderByUid(Long uid){
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>(Order.class);
        wrapper.eq(Order::getUid,uid);
        return Db.list(wrapper);
    }

    public Order getOrderByOrderId(Long id){
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>(Order.class);
        wrapper.eq(Order::getId,id);
        return Db.getOne(wrapper);
    }

    public void remove(Order order){
        Db.removeById(order);
    }

    @Transactional
    public void selectIsExpired() {
        // 一小时
        // 找到所有过期演出计划，并更新
        Long time = Instant.now().plusSeconds(60*60).toEpochMilli();
        LambdaQueryWrapper<Plan> planLambdaQueryWrapper = new LambdaQueryWrapper<>(Plan.class);
        planLambdaQueryWrapper.select(Plan::getId).le(Plan::getReleaseTime,time);

        List<Plan> list = Db.list(planLambdaQueryWrapper);

        // 根据演出计划设置票过期
        Set<Long> oids = new HashSet<>();
        for (Plan plan : list) {
            LambdaQueryWrapper<Ticket> queryWrapper = new LambdaQueryWrapper<>(Ticket.class);
            queryWrapper.select(Ticket::getOid).eq(Ticket::getPlanId,plan.getId()).notIn(Ticket::getStatus,TicketStatus.Expired,TicketStatus.Used);
            List<Ticket> list1 = Db.list(queryWrapper);
            for (Ticket ticket : list1) {
                oids.add(ticket.getOid());
            }

            LambdaUpdateWrapper<Ticket> wrapper = new LambdaUpdateWrapper<>(Ticket.class);
            wrapper.eq(Ticket::getPlanId,plan.getId()).notIn(Ticket::getStatus,TicketStatus.Expired,TicketStatus.Used);
            wrapper.set(Ticket::getStatus, TicketStatus.Expired);
            Db.update(wrapper);
        }

        // 根据票设置订单过期
        for (Long oid : oids) {
            LambdaUpdateWrapper<Order> wrapper = new LambdaUpdateWrapper<>(Order.class);
            wrapper.eq(Order::getId,oid).in(Order::getOrderStatus,OrderStatus.Paid,OrderStatus.unSole);
            wrapper.set(Order::getOrderStatus, OrderStatus.Overdue);
            Db.update(wrapper);
        }

    }

    public Long getCancelOrder(Long start, Long end) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>(Order.class);
        wrapper.eq(Order::getOrderStatus,OrderStatus.CancelOrder);
        if(start != null && end != null){
            String startStr = TimeUtil.getFormatByDate(start);
            String endStr = TimeUtil.getFormatByDate(end);
            wrapper.ge(Order::getTime,startStr).le(Order::getTime,endStr);
        }

        return Db.count(wrapper);
    }

    public Long getCancelOrderByPlanId(Long id){
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>(Order.class);
        wrapper.eq(Order::getOrderStatus,OrderStatus.CancelOrder).eq(Order::getId,id);

        return Db.count(wrapper);
    }

}
