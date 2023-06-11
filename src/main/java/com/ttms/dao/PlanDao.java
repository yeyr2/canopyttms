package com.ttms.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ttms.enums.SeatStatus;
import com.ttms.enums.TicketStatus;
import com.ttms.pojo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PlanDao {

    private final TicketAndSeatDao td = new TicketAndSeatDao();

    /**
     * 过期不读
     * @return
     */
    public List<Plan> getPlan(Long start, Long end) {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>(Plan.class);
        wrapper.orderByDesc(Plan::getReleaseTime);
//        wrapper.last("limit "+20);
        if(start != null && end != null){
            wrapper.ge(Plan::getReleaseTime,start).le(Plan::getReleaseTime,end);
        }

        return Db.list(wrapper);
    }


    public List<Plan> getPlanByName(String name) {
        LambdaQueryWrapper<Plan> planWrapper = new LambdaQueryWrapper<>(Plan.class);
        planWrapper.like(Plan::getVideo,name);

        return Db.list(planWrapper);
    }

    public List<Plan> getPlanByVideoId(Long videoId,long start,long end,Boolean isAdmin) {
        Long time = Instant.now().plusSeconds(60*60).toEpochMilli();
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>(Plan.class);
        wrapper.eq(Plan::getVideoId,videoId);
        wrapper.lt(Plan::getReleaseTime,end);
        if (!isAdmin){
            wrapper.ge(Plan::getReleaseTime,time);
        }else{
            wrapper.ge(Plan::getReleaseTime,start);
        }

        return Db.list(wrapper);
    }

    public List<Plan> getPlanByVideoId(Long videoId) {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>(Plan.class);
        wrapper.eq(Plan::getVideoId,videoId);

        return Db.list(wrapper);
    }

    public Plan getPlanById(Long id) {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>(Plan.class);
        wrapper.eq(Plan::getId,id);

        return Db.getOne(wrapper);
    }

    public Plan getPlanByTicketId(Long id) {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>(Plan.class);
        wrapper.eq(Plan::getId,id);

        return Db.getOne(wrapper);
    }

    public String verify(Plan plan) {
        // 判断演出剧目的存在
        // 获取videoId
        if(plan.getVideo() != null) {
            LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>(Video.class);
            wrapper.select(Video::getId).eq(Video::getName,plan.getVideo());
            Video one = Db.getOne(wrapper);
            if(one != null) {
                plan.setVideoId(one.getId());
            }
        }

        // 判断演出厅中的信息存在
        if(plan.getStudio() != null) {
            LambdaQueryWrapper<Studio> studioWrapper = new LambdaQueryWrapper<>(Studio.class);
            studioWrapper.select(Studio::getId).eq(Studio::getName,plan.getStudio());
            Studio studio = Db.getOne(studioWrapper);
            if(studio != null) {
                plan.setStudioId(studio.getId());
            }
        }

        return "";
    }

    @Transactional
    public boolean insert(Plan plan) {
        //todo : 获取座位相关信息
        List<Seat> seats = td.getSeatBy(plan);

        if(!Db.save(plan)){
            return false;
        }

        List<Ticket> tickets = new ArrayList<>();
        for (Seat s : seats) {
            if(s.getStatus() == SeatStatus.Normal){
                Ticket ticket = new Ticket(plan.getId(),s.getSeatRow(),s.getSeatColumn(), TicketStatus.Unsold);
                tickets.add(ticket);
            }
        }

        return Db.saveBatch(tickets);
    }

    public boolean update(Plan plan) {
        // 检查是否和原本一致
        return Db.updateById(plan);
    }

    @Transactional
    public boolean delete(Long id) {
        Plan plan = getPlanById(id);

        try{
            Db.removeById(id,Plan.class);
        }catch (Exception e){
            return false;
        }

        // 删除票
        return td.deleteTicketByPlanId(plan.getId());
    }

    public Plan isExpired(Long planId) {
        Long time = Instant.now().plusSeconds(60*60).toEpochMilli();
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>(Plan.class);
        wrapper.eq(Plan::getId,planId).ge(Plan::getReleaseTime,time);

        return Db.getOne(wrapper);
    }

    public List<Plan> getPlanByStudioId(long studioId) {
        LambdaQueryWrapper<Plan> wrapper = new LambdaQueryWrapper<>(Plan.class);
        wrapper.select(Plan::getId).eq(Plan::getStudioId,studioId);

        return Db.list(wrapper);
    }

    @Transactional
    public boolean updateTicketByPlans(List<Seat> seats, List<Plan> plans, TicketStatus status) {
        List<Long> list = new ArrayList<>();
        for(Plan plan : plans){
            list.add(plan.getId());
        }
        for(Seat seat : seats){
            LambdaUpdateWrapper<Ticket> wrapper = new LambdaUpdateWrapper<>(Ticket.class);
            wrapper.in(Ticket::getPlanId,list).eq(Ticket::getTicketRows,seat.getSeatRow()).eq(Ticket::getTicketColumns,seat.getSeatColumn()).in(Ticket::getStatus,TicketStatus.Unsold,TicketStatus.None);
            wrapper.set(Ticket::getStatus,status);
            List<Ticket> tickets = Db.list(wrapper);
            if(tickets == null || tickets.size() == 0){
                return false;
            }
        }

        return true;
    }
}
