package com.ttms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ttms.pojo.User;
import org.springframework.stereotype.Repository;

@Repository // 标注为持久层软件
public interface UserMapper extends BaseMapper<User> {

}

