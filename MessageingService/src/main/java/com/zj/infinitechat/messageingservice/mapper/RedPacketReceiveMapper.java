package com.zj.infinitechat.messageingservice.mapper;

import com.zj.infinitechat.messageingservice.model.RedPacketReceive;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author Administrator
* @description 针对表【red_packet_receive(红包领取记录表)】的数据库操作Mapper
* @createDate 2026-02-02 13:59:04
* @Entity generator.domain.RedPacketReceive
*/
public interface RedPacketReceiveMapper extends BaseMapper<RedPacketReceive> {
    /**
     * 根据红包ID查询领取记录
     *
     * @param redPacketId 红包ID
     * @param pageNum     页码
     * @param pageSize    每页大小
     * @return 红包领取记录列表
     */
    @Select("SELECT * FROM red_packet_receive WHERE red_packet_id = #{redPacketId} " +
            "LIMIT #{pageNum}, #{pageSize}")
    List<RedPacketReceive> selectByRedPacketId(@Param("redPacketId") Long redPacketId,
                                               @Param("pageNum") Integer pageNum,
                                               @Param("pageSize") Integer pageSize);
}




