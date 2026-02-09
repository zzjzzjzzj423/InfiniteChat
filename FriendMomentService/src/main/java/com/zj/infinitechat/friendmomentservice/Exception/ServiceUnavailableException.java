package com.zj.infinitechat.friendmomentservice.Exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
    public ServiceUnavailableException() {
        super("Netty服务不可用");
    }
}
