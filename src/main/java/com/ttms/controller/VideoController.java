package com.ttms.controller;

import com.ttms.component.ResponseEntityComponent;
import com.ttms.dao.Dao;
import com.ttms.dao.VideoDao;
import com.ttms.enums.VideoEnum;
import com.ttms.pojo.*;
import com.ttms.tools.TimeUtil;
import com.ttms.tools.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoDao videoDAO;

    /**
     *
     * @param pageByTotalNum  一页的电影数量
     * @param page       第几页
     * @return video
     */

    @GetMapping("/getVideo")
    public ResponseEntity<Response> getVideo(@RequestParam("pageByTotalNum") Integer pageByTotalNum,@RequestParam("page") Integer page) throws Exception {
        Long totalPage = Dao.getNoMaxByPagesNum(pageByTotalNum, page,Video.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }

        List<Video> videos = videoDAO.getVideo(System.currentTimeMillis(),0L,pageByTotalNum,page,1, VideoEnum.Default,null);

        Response request = new Response();
        request.setValue(new Page<>(videos, totalPage, page, pageByTotalNum));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @GetMapping("/getVideoByTime")
    public ResponseEntity<Response> getVideoByTime(@RequestParam("pageByTotalNum") Integer pageByTotalNum,@RequestParam("page") Integer page,@RequestParam("Order") Integer order) throws Exception {
        Long totalPage = Dao.getNoMaxByPagesNum(pageByTotalNum, page,Video.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }
        List<Video> videos = videoDAO.getVideo(System.currentTimeMillis(),0L,pageByTotalNum,page,order,VideoEnum.Time,null);

        Response request = new Response();
        request.setValue(new Page<>(videos, totalPage, page, pageByTotalNum));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @GetMapping("/getVideoByScore")
    public ResponseEntity<Response> getVideoByScore(@RequestParam("pageByTotalNum") Integer pageByTotalNum,@RequestParam("page") Integer page,@RequestParam("order") Integer order) throws Exception {
        Long totalPage = Dao.getNoMaxByPagesNum(pageByTotalNum, page,Video.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }
        List<Video> videos = videoDAO.getVideo(System.currentTimeMillis(),0L,pageByTotalNum,page,order,VideoEnum.Score,null);

        Response request = new Response();
        request.setValue(new Page<>(videos, totalPage, page, pageByTotalNum));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @GetMapping("/getVideoByType")
    public ResponseEntity<Response> getVideoByType(@RequestParam("pageByTotalNum") Integer pageByTotalNum,@RequestParam("page") Integer page,@RequestParam("order") Integer order,@RequestParam("type") String type) throws Exception {
        Long totalPage = Dao.getNoMaxByPagesNum(pageByTotalNum, page,Video.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }
        List<Video> videos = videoDAO.getVideo(System.currentTimeMillis(),0L,pageByTotalNum,page,order,VideoEnum.Type,type);

        Response request = new Response();
        request.setValue(new Page<>(videos, totalPage, page, pageByTotalNum));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @GetMapping("/getVideoByHot")
    public ResponseEntity<Response> getVideoByHot(@RequestParam("pageByTotalNum") Integer pageByTotalNum,@RequestParam("page") Integer page) throws Exception {
        Long totalPage = Dao.getNoMaxByPagesNum(pageByTotalNum, page,Video.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }
        List<Video> videos = videoDAO.getVideo(System.currentTimeMillis(),0L,pageByTotalNum,page,1,VideoEnum.Hot,true);

        Response request = new Response();
        request.setValue(new Page<>(videos, totalPage, page, pageByTotalNum));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }


    @GetMapping("/getVideoByComingSoon")
    public ResponseEntity<Response> getVideoByComingSoon(@RequestParam("pageByTotalNum") Integer pageByTotalNum,@RequestParam("page") Integer page) throws Exception {
        Long totalPage = Dao.getNoMaxByPagesNum(pageByTotalNum, page,Video.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }
        List<Video> videos = videoDAO.getVideo(System.currentTimeMillis(),0L,pageByTotalNum,page,1,VideoEnum.ComingSoon,true);

        Response request = new Response();
        request.setValue(new Page<>(videos, totalPage, page, pageByTotalNum));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @GetMapping("/getVideoByName")
    public ResponseEntity<Response> getVideoByName(@RequestParam("pageByTotalNum") Integer pageByTotalNum,@RequestParam("page") Integer page,@RequestParam("name") String name) throws Exception {
        Long totalPage = Dao.getNoMaxByPagesNum(pageByTotalNum, page,Video.class);
        if (totalPage < 0) {
            return ResponseEntityComponent.Exceeds_Maximum;
        }
        List<Video> videos = videoDAO.getVideo(System.currentTimeMillis(),0L,pageByTotalNum,page,1,VideoEnum.Name,name);

        Response request = new Response();
        request.setValue(new Page<>(videos, totalPage, page, pageByTotalNum));

        return new ResponseEntity<>(request, HttpStatus.OK);
    }

    @PostMapping("/insertVideo")
    public ResponseEntity<Response> insertVideo(@RequestBody TokenAndT<Video> tokenAndVideo) throws ParseException {
        if (TokenUtils.verify(tokenAndVideo.getToken(),true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        Video video = tokenAndVideo.getValue();

        // 获取时间并转化为时间戳
        if(!TimeUtil.validationFormat(tokenAndVideo.getTime())) {
            return ResponseEntityComponent.Wrong_Format("time");
        }
        if(tokenAndVideo.getTime() != null ){
            video.setReleaseTime(TimeUtil.getDateByFormat(tokenAndVideo.getTime()));
        }

        if(video.getId() != null || !video.verify("insert",tokenAndVideo.getTime())) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        // 验证video重名情况
        if (video.getName() != null && !videoDAO.verify(video)) {
            return ResponseEntityComponent.Conflict("name");
        }

        boolean status = videoDAO.insertVideo(video);
        if (!status) {
            return ResponseEntityComponent.Create_Failed("video");
        }

        Response response = new Response();
        response.setValue(video.getId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateVideo",method = RequestMethod.POST)
    public ResponseEntity<Response> updateVideo(@RequestBody TokenAndT<Video> tokenAndVideo) throws ParseException {
        if (TokenUtils.verify(tokenAndVideo.getToken(),true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        Video video = tokenAndVideo.getValue();
        if (video.getId() == null) {
            return ResponseEntityComponent.ID_ERR;
        }

        String time = tokenAndVideo.getTime();
        if(time != null){
            // 获取时间并转化为时间戳
            if(!TimeUtil.validationFormat(time)) {
                return ResponseEntityComponent.Wrong_Format("time");
            }
            video.setReleaseTime(TimeUtil.getDateByFormat(tokenAndVideo.getTime()));
        }else{
            video.setReleaseTime(null);
        }

        if (!video.verify("update",time)) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        if (!videoDAO.verify(video)) {
            return ResponseEntityComponent.Conflict("name");
        }

        boolean status = videoDAO.updateVideo(video);
        if (!status) {
            return ResponseEntityComponent.Update_Failed("video");
        }

        Response response = new Response();
        response.setValue(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/deleteVideo",method = RequestMethod.GET)
    public ResponseEntity<Response> deleteVideo(@RequestParam("token") String token,@RequestParam("id") Long id) {
        if (TokenUtils.verify(token,true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        boolean status = videoDAO.deleteVideo(id);
        if (!status) {
            return ResponseEntityComponent.Delete_Failed("video");
        }

        Response response = new Response();
        response.setValue(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}