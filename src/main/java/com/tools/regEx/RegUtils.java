package com.tools.regEx;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//正则表达式工具类
//http://blog.csdn.net/u012230055/article/details/51482424
//部分类由edwin编写

public final class RegUtils {
    /*------------------ 正则表达式 ---------------------*/
    /**
     * 邮箱
     */
    private static final String REGEX_EMAIL = "^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z0-9]+$";
    /**
     * 手机号码
     */
    private static final String REGEX_PHONE = "^13[0-9]{9}|15[012356789][0-9]{8}|18[0-9]{9}|(14[57][0-9]{8})|(17[015678][0-9]{8})$";
    /**
     * 仅中文
     */
    private static final String REGEX_CHINESE = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$";
    /**
     * 整数
     */
    private static final String REGEX_INTEGER = "^-?[1-9]\\d*$";
    /**
     * 数字
     */
    private static final String REGEX_NUMBER = "^([+-]?)\\d*\\.?\\d+$";
    /**
     * 正整数
     */
    private static final String REGEX_INTEGER_POS = "^[1-9]\\d*$";
    /**
     * 浮点数
     */
    private static final String REGEX_FLOAT = "^([+-]?)\\d*\\.\\d+$";
    /**
     * 正浮点数
     */
    private static final String REGEX_FLOAT_POS = "^[1-9]\\d*.\\d*|0.\\d*[1-9]\\d*$";
    /**
     * 字母
     */
    private static final String REGEX_LETTER = "^[A-Za-z]+$";
    /**
     * 大写字母
     */
    private static final String REGEX_LETTER_UPPERCASE = "^[A-Z]+$";
    /**
     * 小写字母
     */
    private static final String REGEX_LETTER_LOWERCASE = "^[a-z]+$";
    /**
     * 邮编
     */
    private static final String REGEX_ZIPCODE = "^\\d{6}$";
    /**
     * ip v4地址
     */
    private static final String REGEX_IP4 = "^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$";
    /**
     * 图片
     */
    private static final String REGEX_PICTURE = "(.*)\\.(jpg|bmp|gif|ico|pcx|jpeg|tif|png|raw|tga)$";/**
     /**
     * 压缩文件
     */
    private static final String REGEX_RAR = "(.*)\\.(rar|zip|7zip|tgz)$";
    /**
     * QQ号码，最短5位，最长15位数字
     */
    private static final String REGEX_QQ = "^[1-9]\\d{4,14}$";
    /**
     * 日期（yyyy-MM-dd）
     */
    private static final String REGEX_DATE = "^\\d{4}\\D+\\d{2}\\D+\\d{2}$";
    /**
     * 日期（yyyy-MM-dd），精确，能检查到2月及31号
     */
    private static final String REGEX_DATE_PRECISE = "^((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
    /**
     * 时间（HH:mm:ss或HH:mm）
     */
    private static final String REGEX_TIME = "^((([0-1][0-9])|2[0-3]):[0-5][0-9])(:[0-5][0-9])?$";

    //替换字符串中的某个字符串为另一个字符串
    public String replaceAll(String strSource, String replacedStr, String asStr){
        Pattern p = Pattern.compile(replacedStr);
        // get a matcher object
        Matcher m = p.matcher(strSource);
        return m.replaceAll(asStr);
    }

    //根据正则表达式的pattern进行匹配并返回结果Matcher
    //************************************************
    //使用实例：
    //RegUtils reg = new RegUtils();
    //Matcher m;
    //String str = "http://hq.sinajs.cn/list=sh122143";
    //int i = 0;
    //m = reg.regexMatches(str,"(.*?),");
    //
    //int maxGroup = 0;
    //while (m.find()) {
    //    //根据惯例，零组表示整个模式。它不包括在此计数中。即group(0)表示的是匹配到的整个文本。
    //    System.out.println(String.format("==打印大组%s：%s", maxGroup + 1, m.group(0)));
    //
    //    for(i=0; i < m.groupCount(); i++){
    //        System.out.println(String.format("--打印小组%s：%s", i + 1, m.group(i + 1)));
    //    }
    //
    //    maxGroup++;
    //}
    //
    //System.out.println("本次结果共" + String.valueOf(maxGroup) + "个大组");
    //System.out.println("每个大组中包含" + String.valueOf(m.groupCount()) + "个小组");
    //************************************************
    public Matcher regexMatches(String strSource, String pattern) {
        try {
            //System.out.println(pattern);
            //System.out.println(strSource);

            // 创建Pattern对象
            Pattern p = Pattern.compile(pattern);

            //返回生成的matcher对象
            return p.matcher(strSource);
        }
        catch (Exception e){
            e.printStackTrace();

            return null;
        }
    }

    /**
     * 校验手机号码
     * @param mobile
     * @return
     * @author lqyao
     */
    public static final boolean isMoblie(String mobile){
        boolean flag = false;
        if (null != mobile && !mobile.trim().equals("") && mobile.trim().length() == 11) {
            Pattern pattern = Pattern.compile(REGEX_PHONE);
            Matcher matcher = pattern.matcher(mobile.trim());
            flag = matcher.matches();
        }
        return flag;
    }
    /**
     * 校验邮箱
     * @param value
     * @return
     * @author lqyao
     */
    public static final boolean isEmail(String value){
        boolean flag = false;
        if (null != value && !value.trim().equals("")) {
            Pattern pattern = Pattern.compile(REGEX_EMAIL);
            Matcher matcher = pattern.matcher(value.trim());
            flag = matcher.matches();
        }
        return flag;
    }
    /**
     * 校验密码
     * @param password
     * @return 长度符合返回true，否则为false
     * @author lqyao
     * @since 2015-09-24
     */
    public static final boolean isPassword(String password){
        boolean flag = false;
        if (null != password && !password.trim().equals("")) {
            password = password.trim();
            if(password.length() >= 6 && password.length() <= 30){
                return true;
            }
        }
        return flag;
    }
    /**
     * 校验手机验证码
     * @param value
     * @return 符合正则表达式返回true，否则返回false
     * @author lqyao
     * @since 2015-09-24
     */
    public static final boolean isPhoneValidateCode(String value){
        boolean flag = false;
        if (null != value && !value.trim().equals("")) {
            Pattern pattern = Pattern.compile("^8\\d{5}$");
            Matcher matcher = pattern.matcher(value.trim());
            flag = matcher.matches();
        }
        return flag;
    }
    /**
     * 正则表达式校验,符合返回True
     * @param regex 正则表达式
     * @param content 校验的内容
     * @return
     * @author lqy
     */
    public static boolean isMatch(String regex, CharSequence content){
        return Pattern.matches(regex, content);
    }

    //public static boolean isUpperCase(String str){
    //    if(StrUtils.isEmpty(str)){
    //        return false;
    //    }
    //    String reg = "^[A-Z]$";
    //    return isMatch(reg,str);
    //}
}