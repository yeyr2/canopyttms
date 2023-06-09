package com.ttms.component;

import com.ttms.dao.OrderDao;
import com.ttms.dao.PlanDao;
import com.ttms.dao.TicketAndSeatDao;
import com.ttms.dao.VideoDao;
import com.ttms.enums.TicketStatus;
import com.ttms.pojo.*;
import com.ttms.tools.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class FinancialStatisticsComponent {

    @Autowired
    private PlanDao planDao;
    @Autowired
    private TicketAndSeatDao ts;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private VideoDao videoDao;

    public ResponseEntity<Response> getTotalSales() {

        // 查询所有的演出计划
        List<Plan> plans = planDao.getPlan(null,null);

        // 查询演出计划对应的售出票数
        FinancialStatistics sales = ts.getTicketByPlanIds(plans);
        if(sales == null){
            return ResponseEntityComponent.Expired("sales");
        }

        // 查询订单的取消数量
        Long cancelOrder = orderDao.getCancelOrder(null,null);

        // 计算总销售量
        sales.setNumberOfScreenings(plans.size());
        sales.setRefunds(cancelOrder);

        Response response = new Response();
        response.setValue(sales);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Response> getVideoSales(String videoName) {
        VideoPlanSales videoPlanSales = new VideoPlanSales();

        // 获取video信息
        List<Video> videos = videoDao.getVideoByName(videoName);

        // 获取演出计划信息
        List<Plan> plans = new ArrayList<>();
        for(Video video : videos){
            Plan plan = planDao.getPlanByVideoId(video.getId());
            plans.add(plan);
        }

        // 查询订单的取消数量
        Long cancelOrder = 0L;
        double sales = 0.0;
        Integer numOfUsed = 0;
        for(Plan plan : plans){
            cancelOrder += orderDao.getCancelOrderByPlanId(plan.getId());
            List<Ticket> ticketByPlanId = ts.getTicketByPlanId(plan.getId());
            sales = plan.getPrice() * ticketByPlanId.size();

            // 查询演出计划对应的售出票数
            for(Ticket ticket : ticketByPlanId){
                if(ticket.getStatus() == TicketStatus.Used){
                    numOfUsed++;
                }
            }
        }

//        videoPlanSales.setPlanId();
        videoPlanSales.setRefunds(cancelOrder);
        videoPlanSales.setVideo(videoName);
        videoPlanSales.setNumberViewers(numOfUsed);
        videoPlanSales.setSales(sales);

        Response response = new Response();
        response.setValue(videoPlanSales);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> getVideoPlanSales(Long planId) {
        VideoPlanSales videoPlanSales = new VideoPlanSales();
        // 获取演出计划
        Plan plan = planDao.getPlanById(planId);

        Integer numOfUsed = 0;
        List<Ticket> ticketByPlanId = ts.getTicketByPlanId(plan.getId());
        double sales = plan.getPrice() * ticketByPlanId.size();
        // 查询演出计划对应的售出票数
        for(Ticket ticket : ticketByPlanId){
            if(ticket.getStatus() == TicketStatus.Used){
                numOfUsed++;
            }
        }

        // 查询订单的取消数量
        Long cancelOrder = orderDao.getCancelOrderByPlanId(plan.getId());

        // todo:getVideoPlanSales

        videoPlanSales.setPlanId(Collections.singletonList(planId));
        videoPlanSales.setTime(TimeUtil.getFormatByDate(plan.getReleaseTime()));
        videoPlanSales.setNumberViewers(numOfUsed);
        videoPlanSales.setSales(sales);
        videoPlanSales.setRefunds(cancelOrder);
        videoPlanSales.setVideo(plan.getVideo());

        Response response = new Response();
        response.setValue(videoPlanSales);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> getDailyDales() {
        long time = Instant.now().toEpochMilli();
        Long start = TimeUtil.getTodayStart(time);
        Long end = TimeUtil.computeStartOfNextDay(time);

        // 查询所有的演出计划
        List<Plan> plans = planDao.getPlan(start,end);

        // 查询演出计划对应的售出票数
        FinancialStatistics sales = ts.getTicketByPlanIds(plans);
        if(sales == null){
            return ResponseEntityComponent.Expired("sales");
        }

        // 查询订单的取消数量
        Long cancelOrder = orderDao.getCancelOrder(start,end);

        // 计算总销售量
        sales.setNumberOfScreenings(plans.size());
        sales.setRefunds(cancelOrder);

        Response response = new Response();
        response.setValue(sales);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
