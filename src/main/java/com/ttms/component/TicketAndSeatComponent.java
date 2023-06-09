package com.ttms.component;

import com.ttms.dao.*;
import com.ttms.enums.OrderStatus;
import com.ttms.enums.SeatStatus;
import com.ttms.enums.TicketStatus;
import com.ttms.pojo.*;
import com.ttms.tools.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class TicketAndSeatComponent {
    @Autowired
    private TicketAndSeatDao ts;
    @Autowired
    private StudioDao sd;
    @Autowired
    private UserDao userDao;

    @Autowired
    private PlanDao planDao;

    public ResponseEntity<Response> getTicketByPlanId(Long id) {
        List<Ticket> tickets = ts.getTicketByPlanId(id);

        Manage<Ticket> manage = new Manage<>();
        manage.setValues(tickets);

        Studio studio = sd.getOneStudioByPlanId(id);
        manage.setRows(studio.getStudioRows());
        manage.setColumns(studio.getStudioColumns());

        Response response = new Response();
        response.setValue(manage);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> updateTicket(TokenAndT<Manage<Ticket>> tokenAndTickets, OrderStatus type) throws Exception {
        User user = userDao.getUser(tokenAndTickets.getUid());
        if(user == null){
            return ResponseEntityComponent.User_Not_Exist;
        }
        Manage<Ticket> manage = tokenAndTickets.getValue();

        List<Ticket> tickets = manage.getValues();
        Order order = new Order();

        if (type == OrderStatus.unSole) {// 售票
            // 检测演出计划是否过期
            //  获取订单的信息
            Long planId = tickets.get(0).getPlanId();
            Plan plan = planDao.isExpired(planId);
            System.out.println(plan);
            if ( plan == null){
                return ResponseEntityComponent.Expired("plan");
            }

            // 生成订单。
            //根据演出计划和行列值获取id
            List<Long> list = new ArrayList<>();
            for (Ticket t : tickets) {
                list.add(t.getId());
            }
            order.setUid(user.getId());
            order.setTime(TimeUtil.getFormatByDate(plan.getReleaseTime()));
            order.setVideoId(plan.getVideoId());
            order.setVideo(new VideoDao().getVideoNameByVideoId(order.getVideoId()));
            order.setStudioId(plan.getStudioId());
            order.setStudio(new StudioDao().getStudioNameByStudioId(order.getStudioId()));
            order.setPrice(plan.getPrice() * tickets.size());
            order.setOrderStatus(OrderStatus.unSole);
            order.setPlanId(planId);
            // 保存订单
            if (!new OrderDao().saveOrder(order)) {
                throw new Exception("save order failed");
            }
            // 获取票的状态,如果状态为可售出就锁定票，30min
            if (tickets.size() == 0) {
                return ResponseEntityComponent.Wrong_Format("tickets");
            }
            Ticket ticket = ts.getAndUpdateTickets(tickets, order.getId());
            if (ticket != null) {
                new OrderDao().remove(order);
                if (ticket.getStatus() != TicketStatus.Unsold) {
                    return ResponseEntityComponent.Sole_Err(ticket.getTicketRows(), ticket.getTicketColumns());
                }
                return ResponseEntityComponent.Create_Failed("ticket");
            }
            List<Ticket> ticketByIds = ts.getTicketByIds(list);
            order.setTickets(ticketByIds);
        } else {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        Response response = new Response();
        response.setValue(order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> updateTicketToPay(TokenAndT<Order> tokenAndOrder,OrderStatus type) throws Exception {
        User user = userDao.getUser(tokenAndOrder.getUid());
        if(user == null){
            return ResponseEntityComponent.User_Not_Exist;
        }
        Order order = tokenAndOrder.getValue();

        switch (type) {
            case Paid:// 支付
            case CancelOrder:// 退票
                // 等待支付,30min后解除锁定
                try{
                    order = ts.FinishByPay(order.getId(), user.getId(), type);
                    if(order == null){
                        //只传入订单id
                        return ResponseEntityComponent.Wrong_Format("Order");
                    }
                }catch (Exception e){
                    return ResponseEntityComponent.Expired("plan or order");
                }
                break;
            default:
                return ResponseEntityComponent.PROPERTIES_ERR;
        }

        Response response = new Response();
        response.setValue(order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> getSeatByStudioId(Long id) {
        if(id == null || id <= 0){
            return ResponseEntityComponent.ID_ERR;
        }

        Manage<Seat> seatManage = ts.getSeats(id);

        Response response = new Response();
        response.setValue(seatManage);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> updateSeatStatus(List<Seat> seats, SeatStatus status) {
        // todo:修改seat

//        去除对应位置的ticket，已售出的无法更改
        if(ts.updateTicketByFixedSeat(seats,status)){
            return ResponseEntityComponent.Seat_Fixed;
        }
//         修改seat位置
        if(ts.updateSeatStatus(seats,status)){
            return ResponseEntityComponent.Seat_Fixed;
        }

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> updateTicketUser(TokenAndT<Order> orderTokenAndT) {
        User user = userDao.getUser(orderTokenAndT.getUid());
        if(user == null || user.getId() == null){
            return ResponseEntityComponent.User_Not_Exist;
        }

        // 根据票号码查询票
        if(!ts.getSoleTicketAndSetUserByOrderId(orderTokenAndT.getValue().getId())){
            return ResponseEntityComponent.Update_Failed("soleTicket");
        }

        Response response = new Response();
        response.setValue(true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
