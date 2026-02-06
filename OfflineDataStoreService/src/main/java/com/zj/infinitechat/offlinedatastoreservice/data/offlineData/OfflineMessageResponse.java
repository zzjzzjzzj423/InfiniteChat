package com.zj.infinitechat.offlinedatastoreservice.data.offlineData;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class OfflineMessageResponse {

    private List<OfflineMessage> offlineMessages;
}