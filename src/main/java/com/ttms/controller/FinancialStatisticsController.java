package com.ttms.controller;

import com.ttms.pojo.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/FinancialStatistics")
public class FinancialStatisticsController {

    /**
     * 总销售量：查询以前的+今天的
     * @return
     */
    @GetMapping("/getTotalSales")
    public ResponseEntity<Response> getTotalSales(){


        return null;
    }

    /**
     * 每日销售量：计算今天的
     * @return
     */
    @GetMapping("/getDailyDales")
    public ResponseEntity<Response> getDailyDales(){


        return null;
    }

    /**
     * 根据剧目获取销售量,根据剧目id
     * @return 每个演出计划的销售量
     */
    @GetMapping("/getVideoSales")
    public ResponseEntity<Response> getVideoSales(){



        return null;
    }

    /**
     * 获取对应演出计划的销售量，根据演出计划id
     * @return
     */
    @GetMapping("/getVideoPlanSales")
    public ResponseEntity<Response> getVideoPlanSales(){



        return null;
    }
}
