package com.zj.infinitechat.offlinedatastoreservice.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.zj.infinitechat.offlinedatastoreservice.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2026-02-05 19:33:20
* @Entity generator.domain.User
*/
@Mapper
public interface UserMapper extends MPJBaseMapper<User> {

}




