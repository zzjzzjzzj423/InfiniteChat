package com.zj.InfiniteChat.authenticationservice.controller;


import com.zj.InfiniteChat.authenticationservice.Data.Common.CommonRequest;
import com.zj.InfiniteChat.authenticationservice.Data.Common.CommonResponse;
import com.zj.InfiniteChat.authenticationservice.Data.Common.Upload.UploadResponse;
import com.zj.InfiniteChat.authenticationservice.commom.Result;
import com.zj.InfiniteChat.authenticationservice.service.CommonService;
import jodd.net.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/Common")
public class CommonController {

    private final CommonService commonService;

    @Autowired
    public CommonController(CommonService commonService){
        this.commonService=commonService;
    }


    @GetMapping("SendEmail")
    public Result<CommonResponse> sendEmail(@RequestBody CommonRequest commonRequest){
        CommonResponse commonResponse=commonService.sendEmail(commonRequest.getEmail());

        return new Result<CommonResponse>().setData(commonResponse).setCode(HttpStatus.ok().status());



    }

    @GetMapping("SendEmailLogin")
    public Result<CommonResponse> sendEmailLogin(@RequestBody CommonRequest commonRequest){
        CommonResponse commonResponse=commonService.sendEmailLogin(commonRequest.getEmail());
        return new Result<CommonResponse>().setData(commonResponse).setCode(HttpStatus.ok().status());

    }





}