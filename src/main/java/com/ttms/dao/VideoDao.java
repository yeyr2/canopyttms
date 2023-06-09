package com.ttms.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ttms.enums.VideoEnum;
import com.ttms.enums.VideoType;
import com.ttms.pojo.Page;
import com.ttms.pojo.Plan;
import com.ttms.pojo.Video;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VideoDao {

    private static final Integer Page_MAX = 8;
    private static final Integer NavigatePages = 5;

    private static final TicketAndSeatDao ts = new TicketAndSeatDao();

    /**
     *
     * @param start
     * @param end
     * @param pageByTotalNum
     * @param page
     * @param order 正序或倒序
     * @param type  需要排序的种类
     * @param object
     * @return
     */
    @Transactional
    public List<Video> getVideo(long start,long end,Integer pageByTotalNum,Integer page,Integer order,VideoEnum type,Object object) {
//        PageHelper.startPage(page,pageByTotalNum);
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>(Video.class);
        switch (type) {
            case Score:
                wrapper = order == 1 ? wrapper.orderByAsc(Video::getScore) : wrapper.orderByDesc(Video::getScore);
                break;
            case Time:
                if (order == 1) {
                    wrapper.orderByAsc(Video::getReleaseTime);
                }else{
                    wrapper.orderByDesc(Video::getReleaseTime);
                }
                break;
            case Type:
                wrapper.like(Video::getType,(VideoType)object);
                break;
            case Hot:
                wrapper.eq(Video::getHot,(boolean)object);
                break;
            case ComingSoon:
                wrapper.eq(Video::getComingSoon,object);
                break;
            case Name:
                wrapper.like(Video::getName,object);
                break;
            default:
                break;
        }
        wrapper.le(Video::getReleaseTime,start).ge(Video::getReleaseTime,end);
        wrapper.last(Page.limit(page,pageByTotalNum));

        return Db.list(wrapper);
    }

    public String getVideoNameByVideoId(Long id) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>(Video.class);
        wrapper.eq(Video::getId,id);
        Video one = Db.getOne(wrapper);

        return one.getName();
    }

    public boolean verify(Video video) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>(Video.class);
        wrapper.eq(Video::getName,video.getName());
        if (video.getId() != null) {
            wrapper.notIn(Video::getId,video.getId());
        }

        return Db.count(wrapper) == 0;
    }

    public boolean updateVideo(Video video) {
        try {
            Db.updateById(video);
            return true;
        }catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean insertVideo(Video video) {
        // 查看演出计划是否有未定义的video
        LambdaUpdateWrapper<Plan> wrapper = new LambdaUpdateWrapper<>(Plan.class);
        wrapper.eq(Plan::getVideo,video.getName()).and(update -> update.isNull(Plan::getVideoId).or().le(Plan::getVideoId,0));
        wrapper.set(Plan::getVideoId,video.getId());
        try{
            Db.update(null,wrapper);
        }catch (Exception e){
            return false;
        }


        return Db.save(video);
    }

    @Transactional
    public boolean deleteVideo(Long id) {
        // 删除对应的演出计划
        LambdaQueryWrapper<Plan> planWrapper = new LambdaQueryWrapper<>(Plan.class);
        planWrapper.eq(Plan::getVideoId,id);

        //删除演出计划对应的票
        try{
            Db.removeById(id,Video.class);
        }catch (Exception e){
            return false;
        }

        try{
            Db.remove(planWrapper);
        }catch (Exception e){
            return false;
        }

        //videoId
        return ts.deleteTicketByStudioIdOrByVideoId(id);
    }

    public List<Video> getVideoByName(String videoName) {
        LambdaQueryWrapper<Video> wrapper = new LambdaQueryWrapper<>(Video.class);
        wrapper.eq(Video::getName,videoName);

        return Db.list(wrapper);
    }
}
