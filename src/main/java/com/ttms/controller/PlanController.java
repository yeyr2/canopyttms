package com.ttms.controller;

import com.ttms.component.PlanComponent;
import com.ttms.component.ResponseEntityComponent;
import com.ttms.pojo.Plan;
import com.ttms.pojo.Response;
import com.ttms.pojo.TokenAndT;
import com.ttms.tools.TimeUtil;
import com.ttms.tools.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.text.ParseException;

@RestController
@RequestMapping("/plan")
public class PlanController {

    @Autowired
    private PlanComponent planComponent;

    @RequestMapping(value = "/getPlan",method = RequestMethod.GET)
    public ResponseEntity<Response> getPlan() {
        return planComponent.getPlan();
    }

    @RequestMapping(value = "/getPlanByVideoName",method = RequestMethod.GET)
    public ResponseEntity<Response> getPlanByVideoName(@RequestParam("name") String name) {
        return planComponent.getPlanByVideoName(name);
    }

    @RequestMapping(value = "/getPlanByVideoId",method = RequestMethod.GET)
    public ResponseEntity<Response> getPlan(@RequestParam(value = "token",required = false) String token,@RequestParam("videoId") Long videoId,@RequestParam("time") String time) throws ParseException {
        if (!TimeUtil.validationFormat(time)){
            return ResponseEntityComponent.Wrong_Format("time");
        }

        long cur = TimeUtil.getDateByFormat(time);
        Boolean isAdmin = TokenUtils.IsAdmin(token) == 0;

        return planComponent.getPlan(cur,videoId,isAdmin);
    }

    @PostMapping("/insertPlan")
    public ResponseEntity<Response> insertPlan(@RequestBody TokenAndT<Plan> tokenAndPlan) throws ParseException {
        if (TokenUtils.verify(tokenAndPlan.getToken(),true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        Plan plan = tokenAndPlan.getValue();
        String time = tokenAndPlan.getTime();
        if(time == null || !TimeUtil.validationFormat(time)) {
            return ResponseEntityComponent.Wrong_Format("time");
        }
        plan.setReleaseTime(TimeUtil.getDateByFormat(time));

        return planComponent.insert(plan);
    }

    @PostMapping("/updatePlan")
    public ResponseEntity<Response> updatePlan(@RequestBody TokenAndT<Plan> tokenAndPlan) throws ParseException {
        if (TokenUtils.verify(tokenAndPlan.getToken(),true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        Plan plan = tokenAndPlan.getValue();
        String time = tokenAndPlan.getTime();
        if(time != null) {
            if(!TimeUtil.validationFormat(time)){
                return ResponseEntityComponent.Wrong_Format("time");
            }
            plan.setReleaseTime(TimeUtil.getDateByFormat(time));
        }

        return planComponent.update(plan);
    }

    @GetMapping("/deletePlan")
    public ResponseEntity<Response> deletePlan(@RequestParam("token") String token,@RequestParam("id") Long id){
        if (TokenUtils.verify(token,true) == null) {
            return ResponseEntityComponent.Token_Err;
        }

        return planComponent.delete(id);
    }
}
