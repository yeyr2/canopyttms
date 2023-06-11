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

    public ResponseEntity<Response> getDailyDales() {
        long time = Instant.now().toEpochMilli();
        Long start = TimeUtil.getTodayStart(time);
        Long end = TimeUtil.computeStartOfNextDay(time);

        // 查询所有的演出计划
        List<Plan> plans = planDao.getPlan(start,end);
        System.out.println(plans);

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

    @Transactional
    public ResponseEntity<Response> getVideoSales(String videoName) {
        VideoPlanSales videoPlanSales = new VideoPlanSales();

        // 获取video信息
        List<Video> videos = videoDao.getVideoByName(videoName);
        if(videos == null || videos.size() == 0) {
            return ResponseEntityComponent.Not_Found(videoName+"剧目信息");
        }

        // 获取演出计划信息
        List<Plan> plans = planDao.getPlanByVideoId(videos.get(0).getId());
        if(plans == null || plans.size() == 0) {
            return ResponseEntityComponent.Not_Found(videoName+"相关演出计划");
        }

        // 查询订单的取消数量
        List<Long> planId = new ArrayList<>();
        Long cancelOrder = 0L;
        double sales = 0.0;
        Integer numOfUsed = 0;
        int ticketNumber = 0;
        for(Plan plan : plans){
            planId.add(plan.getId());
            cancelOrder += orderDao.getCancelOrderByPlanId(plan.getId());
            List<Ticket> ticketByPlanId = ts.getSoleTicketByPlanId(plan.getId());
            if(ticketByPlanId == null || ticketByPlanId.size() == 0){
                continue;
            }
            sales += plan.getPrice() * ticketByPlanId.size();
            ticketNumber += ticketByPlanId.size();

            // 查询演出计划对应的售出票数
            for(Ticket ticket : ticketByPlanId){
                if(ticket.getStatus() == TicketStatus.Used){
                    numOfUsed++;
                }
            }
        }

        videoPlanSales.setPlanId(planId);
        videoPlanSales.setRefunds(cancelOrder);
        videoPlanSales.setVideo(videoName);
        videoPlanSales.setTicketNumber(ticketNumber);
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
        if(plan == null) {
            return ResponseEntityComponent.Not_Found(planId+"信息");
        }

        Integer numOfUsed = 0;
        List<Ticket> ticketByPlanId = ts.getSoleTicketByPlanId(plan.getId());
        if(ticketByPlanId == null){
            return ResponseEntityComponent.Not_Found(planId+"信息");
        }

        double sales = plan.getPrice() * ticketByPlanId.size();
        int ticketNumber = ticketByPlanId.size();
        // 查询演出计划对应的售出票数
        for(Ticket ticket : ticketByPlanId){
            if(ticket.getStatus() == TicketStatus.Used){
                numOfUsed++;
            }
        }

        // 查询订单的取消数量
        Long cancelOrder = orderDao.getCancelOrderByPlanId(plan.getId());
        if(cancelOrder == null){
            cancelOrder = 0L;
        }

        // todo:getVideoPlanSales

        videoPlanSales.setPlanId(Collections.singletonList(planId));
        videoPlanSales.setTime(TimeUtil.getFormatByDate(plan.getReleaseTime()));
        videoPlanSales.setNumberViewers(numOfUsed);
        videoPlanSales.setTicketNumber(ticketNumber);
        videoPlanSales.setSales(sales);
        videoPlanSales.setRefunds(cancelOrder);
        videoPlanSales.setVideo(plan.getVideo());

        Response response = new Response();
        response.setValue(videoPlanSales);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
