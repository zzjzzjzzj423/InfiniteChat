package com.zj.infinitechat.offlinedatastoreservice.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import com.zj.infinitechat.offlinedatastoreservice.model.Message;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【message】的数据库操作Mapper
* @createDate 2026-02-05 19:32:39
* @Entity generator.domain.Message
*/
@Mapper
public interface MessageMapper extends MPJBaseMapper<Message> {

}




