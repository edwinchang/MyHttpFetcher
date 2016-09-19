package com.caller;

import com.manager.HttpClientUtil;
import com.tools.file.FileUtil;
import com.tools.music.MusicUtil;
import com.tools.regEx.RegUtils;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

/**
 * Created by edwinchang on 2016-9-18.
 */
public class CallProductInstantAlert {
    private static ScheduledExecutorService scheduExec;

    //文件获取后存放的String数组、url数组、买卖1的量价数据数组、临时个债个股前2位代码变量
    private String fileStrSetting[],fileStr[],urlArr[],dataArr[][],tmpFst2Code;
    //个债个股前两位对应上海市场数组
    private static String[] marketShAbbrArr = {"01","12","13"};
    //所在个债个股代码文件所在路径，后续从配置文件中获取
    private static String productFilePath = "d:\\Temp\\productlist.txt";
    //播放音乐文件所在路径，后续从配置文件中获取
    private static String musicFilePath = "e:\\Temp\\通达信预警音乐\\645.wav";
    //用于显示日期信息
    private static Date now = new Date();
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
    //获取的到的response、临时response
    private String resp,tmpResp;
    //正则表达式中使用
    private Matcher m;
    //是否第一次获取远端数据
    private boolean firstGet = true;
    //产品数量
    private int productCount = 0;

    //运行时间周期
    //private long initialDelay = 24 * 60 * 60 * 1000; //1天
    private static long initialDelay = 0; //0表示立即执行
    //private long period = getTimeMillis("20:00:00") - System.currentTimeMillis();//表示晚上8点
    private static long period = 10; //表示n秒，后续从配置文件中获取

    //工具类初始化
    //HTTP工具类
    private static HttpClientUtil httpClientUtil = new HttpClientUtil();
    //文件工具类
    private static FileUtil fileUtil = new FileUtil();
    //正则表达式工具类
    private static RegUtils reg = new RegUtils();
    //音乐工具类
    private static MusicUtil musicUtil = new MusicUtil();

    CallProductInstantAlert(){
        this.scheduExec = Executors.newSingleThreadScheduledExecutor();
        //this.scheduExec =  Executors.newScheduledThreadPool(2);
    }

    public void runIt(){
        scheduExec.scheduleAtFixedRate(new Runnable() {
            public void run() {
                //跳出这次所有的校验循环（暂不使用）
                boolean isContinue = false;
                //本次校验是否播放音乐
                boolean isPlayMusic = false;
                //遍历获取文件后的数组并拼接出url数组
                for (int i = 0; i < productCount; i++) {
                    //将string末尾回车删除
                    fileStr[i] = StringUtils.trim(fileStr[i]);

                    //获取个债个股代码的前两位
                    tmpFst2Code = fileStr[i].substring(0,2);

                    //个债个股代码的前两位在数组中则为深圳个债个股"sz"，否则为上海"sh"
                    //完整url如"http://hq.sinajs.cn/list=sh122143";
                    if (ArrayUtils.contains(marketShAbbrArr, tmpFst2Code) == true){
                        urlArr[i] = "http://hq.sinajs.cn/list=" + "sh" + fileStr[i];
                    }
                    else{
                        urlArr[i] = "http://hq.sinajs.cn/list=" + "sz" + fileStr[i];
                    }

                    //获取URL中的信息
                    resp = httpClientUtil.httpGetRequest(urlArr[i]);
                    if(resp != ""){//获取成功
//                        System.out.println(resp);
                        //对获取的response的值进行处理
                        tmpResp=resp;

                        tmpResp=reg.replaceAll(tmpResp,"var hq_str_s[h|z]","");
                        //System.out.println(tmpResp);

                        tmpResp=reg.replaceAll(tmpResp,"=\"",",");
                        //System.out.println(tmpResp);

                        m = reg.regexMatches(tmpResp,"(.*?),");
                        //tmpResp="dada ada adad adsda ad asdda adr3 fas daf fas fdsf 234 adda";
                        //m = reg.regexMatches(tmpResp,"\\b\\w{3} *\\w{4}\\b");
                        //m = reg.regexMatches(tmpResp,"\\b(\\w{3}) *(\\w{4})\\b");

                        //查找每个个债个股信息
                        int maxGroup = 0;
                        int groupID =0;
                        while (m.find()) {
                            //根据惯例，零组表示整个模式。它不包括在此计数中。即group(0)表示的是匹配到的整个文本。
                            //System.out.println(String.format("==打印大组%s：%s", groupID + 1, m.group(0)));

                            int j;
                            for(j=0; j < m.groupCount(); j++){
//                                System.out.println(String.format("--打印小组%s：%s", j + 1, m.group(j + 1)));

                                //保存获取到的数据12（买1量）、数据13（买1价格）、数据22（卖1量）、数据23（卖1价格）
                                //如果为第一次获取远端数据，则不需要进行对比，只需要进行赋值
                                //否则进行买1量、卖1量、买1价格、卖1价格对比，之后赋值
                                //如果发生变化，则进行报警
                                //对比（不是第一次获取远端数据）
                                if(!firstGet){
                                    //买1量
                                    if(groupID == 11 && !m.group(j + 1).equals(dataArr[i][0])){
                                        System.out.println(String.format("======%s======",dateFormat.format(now)));
                                        System.out.println(String.format("产品：%s，买1量发生变化：%s | %s",fileStr[i],m.group(j+1),dataArr[i][0]));
                                        isPlayMusic=true;
                                    }
                                    //买1价格
                                    else if(groupID == 12 && !m.group(j + 1).equals(dataArr[i][1])){
                                        System.out.println(String.format("======%s======",dateFormat.format(now)));
                                        System.out.println(String.format("产品：%s，买1价格发生变化：%s | %s",fileStr[i],m.group(j+1),dataArr[i][1]));
                                        isPlayMusic=true;
                                    }
                                    //卖1量
                                    else if(groupID == 21 && !m.group(j + 1).equals(dataArr[i][2])){
                                        System.out.println(String.format("======%s======",dateFormat.format(now)));
                                        System.out.println(String.format("产品：%s，卖1量发生变化：%s | %s",fileStr[i],m.group(j+1),dataArr[i][2]));
                                        isPlayMusic=true;
                                    }
                                    //卖1价格
                                    else if(groupID == 22 && !m.group(j + 1).equals(dataArr[i][3])){
                                        System.out.println(String.format("======%s======",dateFormat.format(now)));
                                        System.out.println(String.format("产品：%s，卖1价格发生变化：%s | %s",fileStr[i],m.group(j+1),dataArr[i][3]));
                                        isPlayMusic=true;
                                    }
                                }
                                //赋值
                                //System.out.println("开始赋值");
                                //买1量
                                if(groupID == 11){
                                    dataArr[i][0] = m.group(j + 1);
                                }
                                //买1价格
                                else if(groupID == 12){
                                    dataArr[i][1] = m.group(j + 1);
                                }
                                //卖1量
                                else if(groupID == 21){
                                    dataArr[i][2] = m.group(j + 1);
                                }
                                //卖1价格
                                else if(groupID == 22){
                                    dataArr[i][3] = m.group(j + 1);
                                }

                                //跳出此次循环
                                //if(isContinue){break;}
                            }

                            groupID++;

                            //跳出此次循环
                            //if(isContinue){break;}
                        }

                        //获取每组数据总数
                        //maxGroup = groupID;
                        //System.out.println("本次结果共" + String.valueOf(maxGroup) + "个大组");
                        //System.out.println("每个大组中包含" + String.valueOf(m.groupCount()) + "个小组");

                        //跳出此次循环
                        //if(isContinue){break;}
                    }
                    else{//获取失败
                        System.out.println(String.format("产品：%s获取失败!",fileStr[i]));
                    }

                    //测试时使用
                    //dataArr[0][0]= String.valueOf(Math.random());
                    //dataArr[0][1]= String.valueOf(Math.random());
                    //dataArr[0][2]= String.valueOf(Math.random());
                    //dataArr[0][3]= String.valueOf(Math.random());
                    //dataArr[1][0]= String.valueOf(Math.random());
                    //dataArr[1][1]= String.valueOf(Math.random());
                    //dataArr[1][2]= String.valueOf(Math.random());
                    //dataArr[1][3]= String.valueOf(Math.random());
                    //
                    //System.out.println("-------------");
                    //System.out.println(dataArr[0][0]);
                    //System.out.println(dataArr[0][1]);
                    //System.out.println(dataArr[0][2]);
                    //System.out.println(dataArr[0][3]);
                    //System.out.println("-");
                    //System.out.println(dataArr[1][0]);
                    //System.out.println(dataArr[1][1]);
                    //System.out.println(dataArr[1][2]);
                    //System.out.println(dataArr[1][3]);

                    //跳出此次循环
                    //if(isContinue){break;}
                }//遍历个债个股结束

                //非第一次获取远端数据
                firstGet = false;

                //如必要，播放音乐
                if (isPlayMusic) {
                    musicUtil.playit(musicFilePath);
                }
            }
        },initialDelay,period,TimeUnit.SECONDS);
    }

    private void getFileToArr() throws IOException {
        //查找默认java目录路径
        //System.out.println(System.getProperty("user.dir"));

        //读取配置文件
        fileStrSetting = fileUtil.readFileByLinesUTF("setting.txt");
        //获取每次刷新间隔时间
        period = Long.parseLong(fileStrSetting[0].substring(1,fileStrSetting[0].lastIndexOf("#")));
        System.out.println(period);
        //获取所在个债个股代码文件所在路径
        productFilePath = fileStrSetting[1].substring(1,fileStrSetting[1].lastIndexOf("#"));
        System.out.println(productFilePath);
        //获取播放音乐文件所在路径
        musicFilePath = fileStrSetting[2].substring(1,fileStrSetting[2].lastIndexOf("#"));
        System.out.println(musicFilePath);

        //启动时进行初始化，即读取配置文件中所有个债个股信息到当前数组中
        fileStr = fileUtil.readFileByLinesUTF(productFilePath);
        //获取个债个股数量
        productCount = fileStr.length;
        //url数组初始化
        urlArr = new String[productCount];
        //二维数组：买1量、卖1量、买1价格、卖1价格，初始化
        dataArr = new String[productCount][4];
    }

    private void closeHttpClient(){
        //关闭HttpClient
        try {
            httpClientUtil.closeHttpClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        CallProductInstantAlert caller = new CallProductInstantAlert();
        caller.getFileToArr();
        caller.runIt();
        //caller.closeHttpClient();
    }
}
