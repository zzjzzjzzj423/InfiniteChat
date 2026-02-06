package com.zj.infinitechat.offlinedatastoreservice.data.offlineData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class OfflineRedPacketMessageBody extends OfflineMessageBody {
    private String redPacketWrapperText;
}
