package com.manager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by edwinchang on 2016-9-15.
 */
public class HttpClientUtilRef1 {
    private static PoolingHttpClientConnectionManager connectionManager = null;
    private static HttpClientBuilder httpBuilder = null;
    private static RequestConfig requestConfig = null;
    private static HttpHost httpHost = null;
    
    //设置连接池最大并发连接
    private final static int MAX_TOTAL_CONNECTIONS = 100;
    //设置单个路由最大连接，默认值2。
    //这里要特别提到route最大连接数这个参数呢，因为这个参数的默认值为2，如果不设置这个参数值，
    //默认情况下对于同一个目标机器的最大并发连接只有2个，这意味着如果你正在执行一个针对某一台目标机器的抓取任务的时候，
    //哪怕你设置连接池的最大连接数为300，但是实际上还是只有2个连接在工作，其他剩余的298个连接都在等待，都是为别的目标机器服务的。
    private final static int MAX_PER_ROUTE_CONNECTIONS = 50;
    //连接超时时间
    public final static int CONNECT_TIMEOUT = 10000;// 10s

    public HttpClientUtilRef1(String ip, int port)
    {
        //请求目标服务器设置
        if ( port == 0 ) {
            httpHost = new HttpHost(ip);
        }
        else{
            httpHost = new HttpHost(ip, port);
        }
        
        //设置http的状态参数
        requestConfig = RequestConfig.custom()
                .setSocketTimeout(CONNECT_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .build();

        //池化连接管理器，即连接池
        connectionManager = new PoolingHttpClientConnectionManager();
        //设置最大连接数
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        //设置每个路由上的默认连接个数
        //DefaultMaxPerRoute是根据连接到的主机对MaxTotal的一个细分；比如：
        //MaxtTotal=400 DefaultMaxPerRoute=200
        //而我只连接到http://sishuok.com时，到这个主机的并发最多只有200；而不是400；
        //而我连接到http://sishuok.com 和 http://qq.com时，到每个主机的并发最多只有200；即加起来是400（但不能超过400）；所以起作用的设置是DefaultMaxPerRoute。
        connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE_CONNECTIONS); //设置每个路由最大连接数
        //单独为某个站点设置最大连接个数
        //connectionManager.setMaxPerRoute(new HttpRoute(httpHost), 20);

        //初始化httpBuilder
        httpBuilder = HttpClients.custom();
        httpBuilder.setConnectionManager(connectionManager);
    }

    public static CloseableHttpClient getConnection() {
        CloseableHttpClient httpClient = httpBuilder.build();
        httpClient = httpBuilder.build();
        return httpClient;
    }


    public static HttpUriRequest getRequestMethod(Map<String, String> map, String url, String method) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        for (Map.Entry<String, String> e : entrySet) {
            String name = e.getKey();
            String value = e.getValue();
            NameValuePair pair = new BasicNameValuePair(name, value);
            params.add(pair);
        }
        HttpUriRequest reqMethod = null;
        if ("post".equals(method)) {
            reqMethod = RequestBuilder.post().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                    .setConfig(requestConfig).build();
        } else if ("get".equals(method)) {
            reqMethod = RequestBuilder.get().setUri(url)
                    .addParameters(params.toArray(new BasicNameValuePair[params.size()]))
                    .setConfig(requestConfig).build();
        }
        return reqMethod;
    }

    public static void main(String args[]) throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("account", "");
        map.put("password", "");

        HttpClient client = getConnection();
        HttpUriRequest post = getRequestMethod(map, "http://cnivi.com.cn/login", "post");
        HttpResponse response = client.execute(post);

        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            String message = EntityUtils.toString(entity, "utf-8");
            System.out.println(message);
        } else {
            System.out.println("请求失败");
        }
    }
}
