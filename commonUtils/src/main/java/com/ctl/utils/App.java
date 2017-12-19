package com.ctl.utils;

import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;

import com.common.util.MD5Util;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws NoSuchAlgorithmException{
        System.out.println( "Hello World!" +StringUtils.leftPad("1",4, "0"));
        System.out.println(MD5Util.md5_32("admin").toUpperCase());
    }
}
