package com.mrnew.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mrnew on 2017/1/5.
 * email:474923660@qq.com
 * 正则校验工具相关类
 */
public class CheckUtil {

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 判断是否是合法的身份证号
     *
     * @param string
     * @return
     */
    public static boolean checkICCard(String string) {
        // 身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符X
        Pattern pattern = Pattern.compile("(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)*");
        return !(string == null || string.trim().length() == 0) && pattern.matcher(string).matches();
    }

    /**
     * 检查是否包含特殊字符
     *
     * @param string
     * @return
     */
    public static boolean checkSpecialCharacter(String string) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(string);
        return m.find();
    }

    /**
     * 判断手机号是否有效
     *
     * @param string
     * @return
     */
    public static boolean checkPhone(String string) {
        String regEx = "^1\\d{10}$";
        Pattern p = Pattern.compile(regEx);
        return p.matcher(string).matches();
    }

}
