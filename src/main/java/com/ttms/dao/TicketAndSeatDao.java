package com.ttms.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ttms.enums.OrderStatus;
import com.ttms.enums.SeatStatus;
import com.ttms.enums.TicketStatus;
import com.ttms.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class TicketAndSeatDao {

    @Autowired
    private PlanDao planDao;

    public List<Ticket> getTicketByPlanId(Long planId) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>(Ticket.class);
        wrapper.eq(Ticket::getPlanId,planId);

        return Db.list(wrapper);
    }

    public List<Ticket> getTicketByIds(List<Long> ids) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>(Ticket.class);
        wrapper.select(Ticket::getId,Ticket::getTicketRows,Ticket::getTicketColumns).in(Ticket::getId,ids);
        List<Ticket> list = Db.list(wrapper);
        return list;
    }

    @Transactional
    public Ticket getAndUpdateTickets(List<Ticket> tickets,Long orderId) {
        for(Ticket ticket : tickets){
            Long time = Instant.now().plusSeconds(30*60).toEpochMilli();
            //查询票状态
            LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>(Ticket.class);
            wrapper.select(Ticket::getId,Ticket::getStatus).eq(Ticket::getTicketColumns,ticket.getTicketColumns()).
                    eq(Ticket::getTicketRows,ticket.getTicketRows()).eq(Ticket::getPlanId,ticket.getPlanId());
            Ticket one = Db.getOne(wrapper);
            if(one == null){//票被删除
                return ticket;
            }
            if(one.getStatus() != TicketStatus.Unsold){//票被售出
                return ticket;
            }
            //修改票状态
            LambdaUpdateWrapper<Ticket> updateWrapper = new LambdaUpdateWrapper<>(Ticket.class);
            updateWrapper.eq(Ticket::getId,one.getId()).eq(Ticket::getStatus,TicketStatus.Unsold);
            updateWrapper.set(Ticket::getStatus,TicketStatus.Locking).set(Ticket::getLastTime,time).set(Ticket::getOid,orderId);
            boolean update = Db.update(updateWrapper);
            if(!update){//票被售出或删除
                return ticket;
            }
        }
        return null;
    }

    @Transactional
    public boolean selectAndUpdateTicketByLock(Long time) throws Exception {
        // 查询所有未支付票对应的订单id
        LambdaQueryWrapper<Ticket> queryWrapper = new LambdaQueryWrapper<>(Ticket.class);
        queryWrapper.select(Ticket::getOid).eq(Ticket::getStatus, TicketStatus.Locking).le(Ticket::getLastTime,time);
        List<Ticket> list = Db.list(queryWrapper);
        Set<Long> longList = new HashSet<>(list.size());
        for (Ticket ticket : list) {
            longList.add(ticket.getOid());
        }

        // 重置ticket
        LambdaUpdateWrapper<Ticket> wrapper = new LambdaUpdateWrapper<>(Ticket.class);
        wrapper.eq(Ticket::getStatus, TicketStatus.Locking).le(Ticket::getLastTime,time);
        wrapper.set(Ticket::getStatus,TicketStatus.Unsold).set(Ticket::getLastTime,0).set(Ticket::getOid,0);

        //过期order
        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>(Order.class);
        updateWrapper.eq(Order::getOrderStatus,OrderStatus.unSole).in(Order::getId,longList);
        updateWrapper.set(Order::getOrderStatus,OrderStatus.Overdue);

        return Db.update(updateWrapper) && Db.update(wrapper);
    }

    @Transactional
    public Order FinishByPay(Long orderId, Long uid,OrderStatus type) throws Exception {
        LambdaQueryWrapper<Order> orderLambdaQueryWrapper = new LambdaQueryWrapper<>(Order.class);
        orderLambdaQueryWrapper.eq(Order::getId,orderId);
        Order one = Db.getOne(orderLambdaQueryWrapper);

        // 检测对应演出计划是否过期
        if(!selectAndSetExpired(one,orderId)){
            throw new Exception("plan过期");
        }

        LambdaUpdateWrapper<User> wrapper1;
        if(type == OrderStatus.Paid){ // 支付
            // 设置订单状态为支付
            // 检测订单是否未支付状态
            if(one.getOrderStatus() != OrderStatus.unSole){
                throw new Exception("order失去有效性");
            }

            LambdaUpdateWrapper<Ticket> wrapper = new LambdaUpdateWrapper<>(Ticket.class);
            wrapper.eq(Ticket::getOid,orderId);
            wrapper.set(Ticket::getStatus,TicketStatus.Sold).set(Ticket::getLastTime,0);
            if(!Db.update(wrapper)){
                return null;
            }

            wrapper1 = new LambdaUpdateWrapper<>(User.class);
            wrapper1.eq(User::getId,uid);
            wrapper1.setSql("balance = balance - "+one.getPrice());
            if(!Db.update(wrapper1)){
                return null;
            }
        }else if(type == OrderStatus.CancelOrder){ // 退款
            // 设置订单状态为退款

            // 查看订单是否已经使用或过期
            if(one.getOrderStatus() == OrderStatus.Overdue || one.getOrderStatus() == OrderStatus.Used || one.getOrderStatus() == OrderStatus.CancelOrder){
                throw new Exception("order失去有效性");
            }

            // 重置票
            LambdaUpdateWrapper<Ticket> wrapper = new LambdaUpdateWrapper<>(Ticket.class);
            wrapper.eq(Ticket::getOid,orderId);
            wrapper.set(Ticket::getOid,0).set(Ticket::getStatus,TicketStatus.Unsold).set(Ticket::getLastTime,0);
            if(!Db.update(wrapper)){
                return null;
            }

            if(one.getOrderStatus() == OrderStatus.Paid){//已经支付就退款
                wrapper1 = new LambdaUpdateWrapper<>(User.class);
                wrapper1.eq(User::getId,uid);
                wrapper1.setSql("balance = balance + "+one.getPrice());
                if(!Db.update(wrapper1)){
                    return null;
                }
            }
        }else{
            return null;
        }
        //设置订单状态
        LambdaUpdateWrapper<Order> orderLambdaUpdateWrapper = new LambdaUpdateWrapper<>(Order.class);
        orderLambdaUpdateWrapper.eq(Order::getId,orderId);
        orderLambdaUpdateWrapper.set(Order::getOrderStatus,type);

        if(!Db.update(orderLambdaUpdateWrapper)){
            return null;
        }

        return Db.getOne(orderLambdaQueryWrapper);
    }

    public boolean deleteTicketByPlanId(Long planId) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>(Ticket.class);
        wrapper.eq(Ticket::getPlanId,planId);

        try {
            Db.remove(wrapper);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean deleteTicketByStudioIdOrByVideoId(Long id) {
        LambdaQueryWrapper<Plan> planLambdaQueryWrapper = new LambdaQueryWrapper<>(Plan.class);
        planLambdaQueryWrapper.select(Plan::getId).eq(Plan::getStudioId,id);
        List<Plan> list = Db.list(planLambdaQueryWrapper);

        for (Plan p : list){
            LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>(Ticket.class);
            wrapper.eq(Ticket::getPlanId,p.getId());

            Db.remove(wrapper);
        }

        return true;
    }

    public List<Seat> getSeatBy(Plan plan){
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>(Seat.class);
        wrapper.eq(Seat::getStudioId,plan.getStudioId());

        return Db.list(wrapper);
    }

    @Transactional
    public boolean createSeat(Long studioId,int row,int column) throws Exception {
        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= row; i++) {
            for(int j = 1 ; j <= column;j++) {
                Seat seat = new Seat();
                seat.setSeatColumn(j);
                seat.setSeatRow(i);
                seat.setStatus(SeatStatus.Normal);
                seat.setStudioId(studioId);
                seats.add(seat);
            }
        }
        try {
            return Db.saveBatch(seats,seats.size());
        }catch (Exception e){
            throw new Exception(e);
        }

    }

    public boolean deleteSeatByStudioId(Long id) {
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>(Seat.class);
        wrapper.eq(Seat::getStudioId,id);

        try {
            Db.remove(wrapper);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Manage<Seat> getSeats(Long id) {
        Manage<Seat> seatManage = new Manage<>();
        LambdaQueryWrapper<Seat> wrapper = new LambdaQueryWrapper<>(Seat.class);
        wrapper.eq(Seat::getStudioId,id);
        seatManage.setValues(Db.list(wrapper));

        LambdaQueryWrapper<Studio> queryWrapper = new LambdaQueryWrapper<>(Studio.class);
        queryWrapper.select(Studio::getStudioColumns,Studio::getStudioRows).eq(Studio::getId,id);
        Studio one = Db.getOne(queryWrapper);
        seatManage.setRows(one.getStudioRows());
        seatManage.setColumns(one.getStudioColumns());

        return seatManage;
    }

    public List<Ticket> getTicketByOid(Long id) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>(Ticket.class);
        wrapper.eq(Ticket::getOid,id);

        return Db.list(wrapper);
    }

    public boolean selectAndSetExpired(Order one,Long orderId) {
        if(new PlanDao().isExpired(one.getPlanId()) == null){
            //设置票过期
            LambdaUpdateWrapper<Ticket> wrapper = new LambdaUpdateWrapper<>(Ticket.class);
            wrapper.eq(Ticket::getOid,orderId);
            wrapper.set(Ticket::getStatus,TicketStatus.Expired).set(Ticket::getLastTime,0);
            Db.update(wrapper);

            LambdaUpdateWrapper<Order> orderLambdaUpdateWrapper = new LambdaUpdateWrapper<>(Order.class);
            orderLambdaUpdateWrapper.eq(Order::getId,orderId);
            orderLambdaUpdateWrapper.set(Order::getOrderStatus,OrderStatus.Overdue);
            Db.update(orderLambdaUpdateWrapper);
            return false;
        }
        return true;
    }

    public boolean getSoleTicketAndSetUserByOrderId(Long id) {
        LambdaUpdateWrapper<Ticket> wrapper = new LambdaUpdateWrapper<>(Ticket.class);
        wrapper.eq(Ticket::getOid,id).eq(Ticket::getStatus,TicketStatus.Sold);
        wrapper.set(Ticket::getStatus,TicketStatus.Used);
        if(!Db.update(wrapper)){
            return false;
        }

        LambdaUpdateWrapper<Order> updateWrapper = new LambdaUpdateWrapper<>(Order.class);
        updateWrapper.eq(Order::getId,id).eq(Order::getOrderStatus,OrderStatus.Paid);
        updateWrapper.set(Order::getOrderStatus,OrderStatus.Used);

        return Db.update(updateWrapper);
    }

    public boolean updateTicketByFixedSeat(List<Seat> seats, SeatStatus status) {
        long studioId = seats.get(0).getStudioId();
         // 找到对应的演出计划
        List<Plan> plans = planDao.getPlanByStudioId(studioId);
        // 修改对应演出计划的票
        TicketStatus ticketStatus;
        if(status == SeatStatus.Normal){
            ticketStatus = TicketStatus.Unsold;
        }else{
            ticketStatus = TicketStatus.None;
        }
        return planDao.updateTicketByPlans(seats, plans, ticketStatus);
    }

    public boolean updateSeatStatus(List<Seat> seats, SeatStatus status) {
        for(Seat seat : seats){
            LambdaUpdateWrapper<Seat> wrapper = new LambdaUpdateWrapper<>(Seat.class);
            wrapper.in(Seat::getStudioId,seats.get(0).getStudioId()).eq(Seat::getSeatRow,seat.getSeatRow()).eq(Seat::getSeatColumn,seat.getSeatColumn());
            wrapper.set(Seat::getStatus,status);
            Seat one = Db.getOne(wrapper);
            if(one == null){
                return false;
            }
        }
        return true;
    }
}
