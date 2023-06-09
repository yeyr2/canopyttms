package com.ttms.dao;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.ttms.pojo.Video;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class Dao<T> {

    /**
     * 判断页数是否超出范围
     * @return
     */
    public static <T> Long getNoMaxByPagesNum(Integer pageByTotalNum,Integer page,Class<T> t) {
        if (page <= 0 ) {
            return -1L;
        }
        if (pageByTotalNum <= 0) {
            return -1L;
        }
        long count = Db.count(t);

        // 保证所有页数
//        return page > (count + pageByTotalNum - 1) / pageByTotalNum ;
        return (count + pageByTotalNum - 1) / (pageByTotalNum);
    }
}
