package com.ttms.controller;

import com.ttms.component.OrderComponent;
import com.ttms.component.ResponseEntityComponent;
import com.ttms.component.TicketAndSeatComponent;
import com.ttms.enums.OrderStatus;
import com.ttms.enums.SeatStatus;
import com.ttms.pojo.*;
import com.ttms.tools.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ticket")
public class TicketAndSeatController {

    @Autowired
    private OrderComponent orderComponent;

    @Autowired
    private TicketAndSeatComponent ticketAndSeatComponent;

    @RequestMapping(value = "/getTickerByPlanId",method = RequestMethod.GET)
    public ResponseEntity<Response> getTickerByPlanId(@RequestParam("id") Long planId) {

        return ticketAndSeatComponent.getTicketByPlanId(planId);
    }

    @RequestMapping(value = "/getOrderByUid",method = RequestMethod.GET)
    public ResponseEntity<Response> getOrderByUid(@RequestParam("token") String token, @RequestParam("uid") Long uid) {
        if(TokenUtils.NoLevelVerify(token) == null){
            return ResponseEntityComponent.Token_Err;
        }

        return orderComponent.getOrderByUid(uid);
    }

    @GetMapping("/getOrderByOrderId")
    public ResponseEntity<Response> getOrderByOrderId(@RequestParam("token") String token,@RequestParam("id") Long id){
        if(TokenUtils.NoLevelVerify(token) == null){
            return ResponseEntityComponent.Token_Err;
        }

        return orderComponent.getOrderByOrderId(id);
    }

    @PostMapping("/updateTicket/buy")
    public ResponseEntity<Response> updateTicketBuy(@RequestBody TokenAndT<Manage<Ticket>> tokenAndTickets) throws Exception {
        if(TokenUtils.NoLevelVerify(tokenAndTickets.getToken()) == null){
            return ResponseEntityComponent.Token_Err;
        }
        List<Ticket> values = tokenAndTickets.getValue().getValues();
        if (values == null || values.size() == 0 || values.get(0) == null){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        return ticketAndSeatComponent.updateTicket(tokenAndTickets, OrderStatus.unSole);
    }

    @PostMapping("/updateTicket/refund")
    public ResponseEntity<Response> updateTicketRefund(@RequestBody TokenAndT<Order> orderTokenAndT) throws Exception {
        if(TokenUtils.NoLevelVerify(orderTokenAndT.getToken()) == null){
            return ResponseEntityComponent.Token_Err;
        }

        return ticketAndSeatComponent.updateTicketToPay(orderTokenAndT,OrderStatus.CancelOrder);
    }

    @PostMapping("/updateTicket/pay")
    public ResponseEntity<Response> updateTicketPay(@RequestBody TokenAndT<Order> orderTokenAndT) throws Exception {
        if(TokenUtils.NoLevelVerify(orderTokenAndT.getToken()) == null){
            return ResponseEntityComponent.Token_Err;
        }

        return ticketAndSeatComponent.updateTicketToPay(orderTokenAndT,OrderStatus.Paid);
    }

    /**
     * 校验票是否过期，并修改票状态为使用
     * @param orderTokenAndT
     * @return
     * @throws Exception
     */
    @PostMapping("/updateTicket/use")
    public ResponseEntity<Response> updateTicketUser(@RequestBody TokenAndT<Order> orderTokenAndT) throws Exception {
        if(TokenUtils.verify(orderTokenAndT.getToken(),true) == null){
            return ResponseEntityComponent.Token_Err;
        }

        return ticketAndSeatComponent.updateTicketUser(orderTokenAndT);
    }

    @RequestMapping(value = "/getSeatByStudioId",method = RequestMethod.GET)
    public ResponseEntity<Response> getSeatByStudioId(@RequestParam("token") String token,@RequestParam("id") Long id) {
        if(TokenUtils.verify(token,true) == null){
            return ResponseEntityComponent.Token_Err;
        }

        return ticketAndSeatComponent.getSeatByStudioId(id);
    }

    /**
     * 需要studioId、column、row、status
     * @param tokenAndSeat
     * @return
     * @throws Exception
     */
    @PostMapping("/updateSeatStatus")
    public ResponseEntity<Response> updateSeatStatus(@RequestBody TokenAndT<Manage<Seat>> tokenAndSeat) throws Exception {
        if(TokenUtils.NoLevelVerify(tokenAndSeat.getToken()) == null){
            return ResponseEntityComponent.Token_Err;
        }
        List<Seat> seatManage = tokenAndSeat.getValue().getValues();
        SeatStatus status = tokenAndSeat.getStatus();
        if(seatManage.size() == 0 || seatManage.get(0) == null){
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        return ticketAndSeatComponent.updateSeatStatus(seatManage,status);
    }
}
