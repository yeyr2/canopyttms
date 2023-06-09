package com.ttms.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Page<T> {

    private List<T> object;
    private Long pageNum;
    private Integer page;
    private Integer pageSize;
    private boolean hasNextPage;
    private boolean hasLastPage;

    public Page(List<T> object,Long pageNum,Integer page,Integer pageSize) throws Exception {
        if (page <= 0 || pageNum < 0 || pageSize <= 0) {
            throw new Exception("page or pageNum ge 0");
        }
        this.object = object;
        this.pageNum = pageNum;
        this.page = page;
        this.pageSize = object.size();

        hasNextPage = page != 1;
        hasLastPage = page < pageNum;
    }

    public static String limit(Integer page,Integer pageSize) {
        return "limit "+( page - 1 ) * pageSize+","+pageSize;
    }

}
