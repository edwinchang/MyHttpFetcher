package com.manager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author Nan
 * 2015-11
 * http://blog.csdn.net/wangnan537/article/details/50374061
 * 基于HttpClient4.5.1实现Http访问工具类
 */
//**********************************************************
//pom.xml中依赖包设置
//        <dependency>
//        <groupId>org.apache.httpcomponents</groupId>
//        <artifactId>httpclient</artifactId>
//        <version>4.5.1</version>
//        </dependency>
//**********************************************************
public class HttpClientUtil {
    private static CloseableHttpClient httpClient = null;
    private static PoolingHttpClientConnectionManager cm = null;
    private static RequestConfig requestConfig = null;
    private static String EMPTY_STR = "";
    private static String UTF_8 = "UTF-8";

    //设置连接池最大并发连接
    private final static int MAX_TOTAL_CONNECTIONS = 50;
    //设置单个路由最大连接，默认值2。
    //这里要特别提到route最大连接数这个参数呢，因为这个参数的默认值为2，如果不设置这个参数值，
    //默认情况下对于同一个目标机器的最大并发连接只有2个，这意味着如果你正在执行一个针对某一台目标机器的抓取任务的时候，
    //哪怕你设置连接池的最大连接数为300，但是实际上还是只有2个连接在工作，其他剩余的298个连接都在等待，都是为别的目标机器服务的。
    private final static int MAX_PER_ROUTE_CONNECTIONS = 25;
    //连接超时时间
    private final static int CONNECT_TIMEOUT = 10000;// 10s

    private static void init(){
        if(cm == null){
            //池化连接管理器，即连接池
            cm = new PoolingHttpClientConnectionManager();
            //设置最大连接数
            cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);
            //设置每个路由上的默认连接个数
            //DefaultMaxPerRoute是根据连接到的主机对MaxTotal的一个细分；比如：
            //MaxtTotal=400 DefaultMaxPerRoute=200
            //而我只连接到http://sishuok.com时，到这个主机的并发最多只有200；而不是400；
            //而我连接到http://sishuok.com 和 http://qq.com时，到每个主机的并发最多只有200；即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute。
            cm.setDefaultMaxPerRoute(MAX_PER_ROUTE_CONNECTIONS);
            //单独为某个站点设置最大连接个数
            //cm.setMaxPerRoute(new HttpRoute(httpHost), 20);

            //设置http的状态参数
            requestConfig = RequestConfig.custom()
                    .setSocketTimeout(CONNECT_TIMEOUT)
                    .setConnectTimeout(CONNECT_TIMEOUT)
                    .setConnectionRequestTimeout(CONNECT_TIMEOUT)
                    .build();
        }
    }

    /**
     * 通过连接池获取HttpClient
     * @return
     */
    private static void getHttpClient(){
        init();
        //return HttpClients.custom().setConnectionManager(cm).build();
        if(httpClient == null) {
            httpClient = HttpClients.custom().setConnectionManager(cm).build();
        }
    }

    /**
     *
     * @param url
     * @return
     */
    public static String httpGetRequest(String url){
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);

        return getResult(httpGet);
    }

    public static String httpGetRequest(String url, Map<String, Object> params) throws URISyntaxException{
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
        httpGet.setConfig(requestConfig);

        return getResult(httpGet);
    }

    public static String httpGetRequest(String url, Map<String, Object> headers,
                                        Map<String, Object> params) throws URISyntaxException{
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        ub.setParameters(pairs);

        HttpGet httpGet = new HttpGet(ub.build());
        httpGet.setConfig(requestConfig);

        for (Map.Entry<String, Object> param: headers.entrySet()) {
            httpGet.addHeader(param.getKey(), (String) param.getValue());
        }
        return getResult(httpGet);
    }

    public static String httpPostRequest(String url){
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> params) throws UnsupportedEncodingException{
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        return getResult(httpPost);
    }

    public static String httpPostRequest(String url, Map<String, Object> headers,
                                         Map<String, Object> params) throws UnsupportedEncodingException{
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);

        for (Map.Entry<String, Object> param: headers.entrySet()) {
            httpPost.addHeader(param.getKey(), (String) param.getValue());
        }

        ArrayList<NameValuePair> pairs = covertParams2NVPS(params);
        httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));

        return getResult(httpPost);
    }

    private static ArrayList<NameValuePair> covertParams2NVPS(Map<String, Object> params){
        ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, Object> param: params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), (String) param.getValue()));
        }

        return pairs;
    }

    /**
     * 处理Http请求
     * @param request
     * @return
     */
    private static String getResult(HttpRequestBase request){
    //private static String getResult(HttpRequestBase request) throws Exception{
        //CloseableHttpClient httpClient = HttpClients.createDefault();
        //CloseableHttpClient httpClient = getHttpClient();
        getHttpClient();
        try{
            CloseableHttpResponse response = httpClient.execute(request);
            //response.getStatusLine().getStatusCode();
            try
            {
                //请求返回结果为成功
                //200　        请求成功
                //201     请求完成，结果是创建了新资源
                //202　　    请求被接受，但处理还没完成
                //204     服务器已经完成了请求，但是没有返回新的信息
                //300     存在多个可用的被请求资源
                //301     请求道的资源都会分配一个永久的url
                //302     请求道的资源放在一个不同的url中临时保存
                //304     请求的资源未更新
                //400　　    非法请求
                //401     未授权
                //403　　    禁止
                //404　　    找不到页面
                //注：以下的SC_OK表示：请求成功，即200
                if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
                    // 获取返回对象
                    HttpEntity entity = response.getEntity();
                    // 数据获取成功
                    if(entity!=null){
                        //long len = entity.getContentLength();// -1 表示长度未知
                        String result = EntityUtils.toString(entity);
                        //response.close();
                        //httpClient.close();
                        return result;
                    }
                    else{
                        return EMPTY_STR;
                    }
                }
                else{
                    return EMPTY_STR;
                }
            }
            finally
            {
                if (null != response)
                {
                    response.close();
                }
            }
        }catch(ClientProtocolException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            //if (null != httpClient)
            //{
            //    httpClient.close();
            //}
        }

        return EMPTY_STR;
    }

    public static void closeHttpClient() throws IOException {
        if (null != httpClient)
        {
            httpClient.close();
        }
    }
}