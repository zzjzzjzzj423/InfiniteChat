package com.zj.InfiniteChat.authenticationservice.service;

import com.zj.InfiniteChat.authenticationservice.Data.Common.CommonResponse;
import org.springframework.stereotype.Service;


public interface CommonService {

    CommonResponse sendEmail(String email);

    CommonResponse sendEmailLogin(String email);
}
