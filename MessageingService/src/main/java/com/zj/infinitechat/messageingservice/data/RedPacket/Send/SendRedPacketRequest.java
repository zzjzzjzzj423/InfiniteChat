package com.zj.infinitechat.messageingservice.data.RedPacket.Send;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class SendRedPacketRequest {
    @NonNull
    private Long sessionId;
    @NonNull
    private Long receiveUserId;
    @NonNull
    private Long sendUserId;
    @NonNull
    private Integer type;

    private Integer sessionType;

    private Body body;

    @Data
    @Accessors(chain = true)
    public static class Body {

        private Integer redPacketType;

        private BigDecimal totalAmount;

        private Integer totalCount;

        private String redPacketWrapperText;
    }
}