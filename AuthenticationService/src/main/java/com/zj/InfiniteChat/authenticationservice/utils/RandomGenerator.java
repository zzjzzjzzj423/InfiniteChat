package com.zj.InfiniteChat.authenticationservice.utils;

import java.util.Random;

public class RandomGenerator {
    static String[] prefix={"dog","dog1","dog3","dog2","cat","cat1","cat2","cat3"};
    static String[] aftfix={"12","d23","d2343","122","cat","c31","2","ca3"};


    public static String generatorNickName(){
        Random r=new Random();
        String pre=prefix[r.nextInt(prefix.length)];
        String aft=aftfix[r.nextInt(aftfix.length)];
        return pre+aft;

    }

    public static String generatorRandomCode(){
        Random r=new Random();
        String code= String.valueOf(r.nextInt(90000)+10000);
        return code;
    }


}
