package com.ttms.component;

import com.ttms.dao.PlanDao;
import com.ttms.pojo.Plan;
import com.ttms.pojo.Response;
import com.ttms.tools.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;

@Component
public class PlanComponent {

    @Autowired
    private PlanDao planDao;

    public ResponseEntity<Response> getPlan() {
        List<Plan> plans = planDao.getPlan(null,null);

        Response response = new Response();
        response.setValue(plans);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> getPlanByVideoName(String name) {
        List<Plan> plans = planDao.getPlanByName(name);

        Response response = new Response();
        response.setValue(plans);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> getPlan(Long cur,Long videoId,Boolean isAdmin) throws ParseException {

        long start = TimeUtil.getTodayStart(cur);
//        long end = TimeUtil.computeStartOfNextDay(cur);
        long end = Instant.now().plusSeconds(60 * 60 * 24 * 5).toEpochMilli();

        List<Plan> plans = planDao.getPlanByVideoId(videoId, start,end,isAdmin);
        Response response = new Response();
        response.setValue(plans);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> insert(Plan plan) {
        if (plan.getId() != null) {
            return ResponseEntityComponent.ID_ERR;
        }

        if(!plan.verify("insert")) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        // 需要判断演出剧目的存在
        String verify = planDao.verify(plan);
        if(!"".equals(verify)) {
            return ResponseEntityComponent.Conflict(verify);
        }

        if(plan.getStudioId() == null) {
            return ResponseEntityComponent.Studio_Err(plan.getStudio());
        }

        //todo:完成ticket关联和video关联
        if (!planDao.insert(plan)) {
            return ResponseEntityComponent.Create_Failed("plan");
        }

        Response response = new Response();
        response.setValue(plan.getId());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> update(Plan plan) {
        if(plan.getId() == null || !plan.verify("update")) {
            return ResponseEntityComponent.PROPERTIES_ERR;
        }

        // 不允许修改演出厅
        if (plan.getStudio() != null) {
            return new ResponseEntity<>(new Response().setMsg("不允许修改演出厅"), HttpStatus.OK);
        }

        String verify = planDao.verify(plan);
        if(!"".equals(verify)) {
            return ResponseEntityComponent.Conflict(verify);
        }

        if(!planDao.update(plan)){
            return ResponseEntityComponent.Update_Failed("plan");
        }

        Response response = new Response();
        response.setValue(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<Response> delete(Long id){
        if (!planDao.delete(id)) {
            return ResponseEntityComponent.Delete_Failed("plan");
        }

        Response response = new Response();
        response.setValue(true);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
