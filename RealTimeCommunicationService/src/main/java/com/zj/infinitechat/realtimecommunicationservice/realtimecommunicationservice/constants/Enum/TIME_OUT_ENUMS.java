package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.constants.Enum;

import lombok.Getter;

@Getter
public enum TIME_OUT_ENUMS {

    JWT_TIMEOUT("token out day","jwt", 24);



    private final String name;

    private final String prefix;

    private final int timeOut;

    TIME_OUT_ENUMS(String name, String prefix, int timeOut){
        this.name=name;
        this.prefix=prefix;
        this.timeOut=timeOut;
    }

}
