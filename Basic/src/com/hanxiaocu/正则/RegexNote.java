package com.hanxiaocu.正则;

/**
 * Description:
 * User: hanchenghai
 * Date: 2018/09/14 上午9:34
 */
public class RegexNote {


    public static void main(String[] args) {
         String regex = "^1\\d{10}$";
        boolean b = "123456787919".matches(regex);
    }
}
