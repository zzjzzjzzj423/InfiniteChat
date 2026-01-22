package com.zj.infinitechat.realtimecommunicationservice.realtimecommunicationservice.constants.Enum;

import lombok.Getter;

@Getter
public enum KEY_ENUMS {

    JWT_KEY_PAIRS("JWTKEY","ZZJ_STUDY_KEY");

    private String name;
    private String value;


    KEY_ENUMS(String name, String value){
        this.name=name;
        this.value=value;

    }
}
