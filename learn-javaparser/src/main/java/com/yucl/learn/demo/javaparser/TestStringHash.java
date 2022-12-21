package com.yucl.learn.demo.javaparser;

import org.apache.commons.codec.digest.DigestUtils;

public class TestStringHash {
    public static void main(String[] args) {
        String rst = "";

        rst = DigestUtils.sha1Hex("asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasfdasfasdfasfdasdfafdasfdasfdasfdasdfasdf");
        System.out.println(rst.length());
        rst =  DigestUtils.md5Hex("asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasfdasfasdfasfdasdfafdasfdasfdasfdasdfasdf");
        System.out.println(rst.length());


        long s = System.currentTimeMillis();
        for(int i=0;i<10000000;i++){
            DigestUtils.md5Hex("asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasXXXXXXXXXXXXXXXXXXXSDFASDFASDFASDFdfasdfasdfasfdasfasdfasfdasdfafdasfdasfdasfdasdfasdf");
        }
        System.out.println((System.currentTimeMillis() - s));

        s = System.currentTimeMillis();
        for(int i=0;i<10000000;i++){
            DigestUtils.sha1Hex("asdfasdfasdfasdfasdfasdfasdfasdfasdfasdfasXXXXXXXXXXXXXXXXXXXSDFASDFASDFASDFdfasdfasdfasfdasfasdfasfdasdfafdasfdasfdasfdasdfasdf");
        }
        System.out.println((System.currentTimeMillis() - s));
    }
}
