package com.platform.test;

import org.apache.commons.codec.digest.DigestUtils;

public class Demo1 {
    public static void main(String[] args) {
        System.out.println(DigestUtils.sha256Hex("123456"));
    }
}
