package com.ttms.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ttms.pojo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class StudioDao {

    private final TicketAndSeatDao ts = new TicketAndSeatDao();
    public List<Studio> getStudio( Integer pageSize,Integer page,Object object,String type) {
        LambdaQueryWrapper<Studio> wrapper = new LambdaQueryWrapper<>(Studio.class);
        switch (type) {
            case "id" :
                wrapper.eq(Studio::getId,object);
                break;
            case "name":
                wrapper.like(Studio::getName,object);
                break;
            default:
                break;
        }
        wrapper.last(Page.limit(page,pageSize));

        return Db.list(wrapper);
    }

    public String getStudioNameByStudioId(Long id){
        LambdaQueryWrapper<Studio> wrapper = new LambdaQueryWrapper<>(Studio.class);
        wrapper.eq(Studio::getId,id);
        Studio one = Db.getOne(wrapper);

        return one.getName();
    }

    @Transactional
    public boolean insertStudio(Studio studio) throws Exception {
        // 寻找是否有对应演出计划
        LambdaUpdateWrapper<Plan> wrapper = new LambdaUpdateWrapper<>(Plan.class);
        wrapper.eq(Plan::getStudio, studio.getName()).isNull(Plan::getStudioId);
        wrapper.set(Plan::getStudioId, studio.getId());

        Db.update(null, wrapper);

        Db.save(studio);

        // 创建对应座位
        boolean b;
        try {
            b = ts.createSeat(studio.getId(), studio.getStudioRows(), studio.getStudioColumns());
        } catch (Exception e) {
            throw new Exception(e);
        }

        return b;
    }


    public boolean updateStudio(Studio studio) {
        try {
            Db.updateById(studio);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Transactional
    public boolean deleteStudio(long id) {
        // 删除座位
        if(!ts.deleteSeatByStudioId(id)) {
            return false;
        }
        // 删除演出计划
        LambdaQueryWrapper<Plan> planWrapper = new LambdaQueryWrapper<>(Plan.class);
        planWrapper.eq(Plan::getStudioId,id);
        try{
            Db.remove(planWrapper);
        }catch (Exception e){
            return false;
        }

        //删除演出剧目对应的票
        //studioId
        if(!ts.deleteTicketByStudioIdOrByVideoId(id)){
            return false;
        }

        try {
            Db.removeById(id,Studio.class);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean verify(Studio studio) {
        LambdaQueryWrapper<Studio> wrapper = new LambdaQueryWrapper<>(Studio.class);
        wrapper.eq(Studio::getName,studio.getName());
        if (studio.getId() != null) {
            wrapper.notIn(Studio::getId,studio.getId());
        }
        return Db.count(wrapper) != 0;
    }


    public Studio getOneStudioByPlanId(Long planId) {
        LambdaQueryWrapper<Plan> planLambdaQueryWrapper = new LambdaQueryWrapper<>(Plan.class);
        planLambdaQueryWrapper.select(Plan::getStudioId).eq(Plan::getId,planId);
        Plan one = Db.getOne(planLambdaQueryWrapper);

        LambdaQueryWrapper<Studio> wrapper = new LambdaQueryWrapper<>(Studio.class);
        wrapper.eq(Studio::getId,one.getStudioId());
        return Db.getOne(wrapper);
    }

}
