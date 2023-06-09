package com.ttms.controller;

import com.ttms.component.FinancialStatisticsComponent;
import com.ttms.component.ResponseEntityComponent;
import com.ttms.pojo.Response;
import com.ttms.tools.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/FinancialStatistics")
public class FinancialStatisticsController {

    @Autowired
    private FinancialStatisticsComponent fs;

    /**
     * 总销售量：查询以前的+今天的
     * @return
     */
    @GetMapping("/getTotalSales")
    public ResponseEntity<Response> getTotalSales(@RequestParam("token") String token ){
        if(TokenUtils.verify(token,true) == null){
            return ResponseEntityComponent.Token_Err;
        }
        return fs.getTotalSales();
    }

    /**
     * 每日销售量：计算今天的
     * @return
     */
    @GetMapping("/getDailyDales")
    public ResponseEntity<Response> getDailyDales(@RequestParam("token") String token){
        if(TokenUtils.verify(token,true) == null){
            return ResponseEntityComponent.Token_Err;
        }

        return fs.getDailyDales();
    }

    /**
     * 根据剧目获取销售量,根据剧目id
     * @return 每个演出计划的销售量
     */
    @GetMapping("/getVideoSales")
    public ResponseEntity<Response> getVideoSales(@RequestParam("token") String token,@RequestParam("VideoName") String VideoName){
        if(TokenUtils.verify(token,true) == null){
            return ResponseEntityComponent.Token_Err;
        }

        return fs.getVideoSales(VideoName);
    }

    /**
     * 获取对应演出计划的销售量，根据演出计划id
     * @return
     */
    @GetMapping("/getVideoPlanSales")
    public ResponseEntity<Response> getVideoPlanSales(@RequestParam("token") String token,@RequestParam("planId") Long planId){
        if(TokenUtils.verify(token,true) == null){
            return ResponseEntityComponent.Token_Err;
        }

        return fs.getVideoPlanSales(planId);
    }
}
