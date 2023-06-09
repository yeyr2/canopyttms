package com.ttms.component;

import com.ttms.dao.OrderDao;
import com.ttms.dao.TicketAndSeatDao;
import com.ttms.pojo.Order;
import com.ttms.pojo.Response;
import com.ttms.pojo.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;

@Component
public class OrderComponent {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private TicketAndSeatDao ts;

    public ResponseEntity<Response> getOrderByUid(Long uid){
        List<Order> orders = orderDao.getOrderByUid(uid);
        //获取ticket
        for (Order order : orders) {
            List<Ticket> ticketByOid = ts.getTicketByOid(order.getId());
            order.setTickets(ticketByOid);
        }

        Response response = new Response();
        response.setValue(orders);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> getOrderByOrderId(Long id){
        Order order = orderDao.getOrderByOrderId(id);

        //获取ticket
        order.setTickets(ts.getTicketByOid(id));

        Response response = new Response();
        response.setValue(order);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
