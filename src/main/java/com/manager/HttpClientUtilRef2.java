package com.manager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class HttpClientUtilRef2

{
    private PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
    private CloseableHttpClient httpClient;
    private HttpHost httpHost;
    private String path;
    private int timeout;
    private RequestConfig requestConfig;

    public HttpClientUtilRef2(String ip, int port, int timeout)
    {
        //超时时长设置
        this.timeout = timeout * 1000;
        //设置连接池最大并发连接
        connManager.setMaxTotal(1000);
        //设置单个路由最大连接，覆盖默认值2
        connManager.setDefaultMaxPerRoute(10);
        //初始化httpClient
        httpClient = HttpClients.custom().setConnectionManager(connManager).build();
        //请求目标服务器设置
        if ( port == 0 ) {
            httpHost = new HttpHost(ip);
        }
        else{
            httpHost = new HttpHost(ip, port);
        }
        //设置全局请求参数
        RequestConfig defaultRequestConfig =
                RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.BEST_MATCH)
                        .setExpectContinueEnabled(true)
                        .setStaleConnectionCheckEnabled(true)
                        .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                        .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                        .build();

        requestConfig = RequestConfig.copy(defaultRequestConfig).setConnectTimeout(this.timeout).build();
    }

    /**
     * @Title: getResponse
     * @Description: 请求HTTP服务器上的内容并解析返回结果
     * @param @return
     * @param @throws Exception
     * @return String
     * @throws
     */
    public String getResponse()
            throws Exception
    {
        try
        {
            //设置协议头相关信息
            HttpGet httpGet = new HttpGet(path);
            httpGet.setHeader("User-Agent", "mdn");
            httpGet.setHeader("Connection", "Keep-Alive");
            httpGet.setConfig(requestConfig);

            //请求返回值句柄
            CloseableHttpResponse resp = httpClient.execute(httpHost, httpGet);
            //创建响应处理器处理服务器响应内容
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            //执行请求并获取结果
            String responseBody = httpClient.execute(httpGet, responseHandler);
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
                if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode())
                {
                    // 获取返回对象
                    HttpEntity entity = resp.getEntity();
                    if (entity != null)
                    {
                        //获取返回对象的内容流
                        InputStream instream = entity.getContent();
                        try
                        {
                            //读取内容流进行处理
                            int l;
                            byte[] tmp = new byte[2048];
                            StringBuffer bufferResp = new StringBuffer();
                            while ((l = instream.read(tmp)) != -1)
                            {
                                bufferResp.append(new String(tmp, 0, l, "UTF-8"));
                            }
                            EntityUtils.consume(resp.getEntity());
                            return bufferResp.toString();
                        }
                        catch (IOException ex)
                        {
                            throw ex;
                        }
                        finally
                        {
                            //关闭流
                            if(null != instream)
                            {
                                instream.close();
                            }
                        }
                    }
                    else
                    {
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
            finally
            {
                if (null != resp)
                {
                    resp.close();
                }
            }
        }
        finally
        {
            if (null != httpClient)
            {
                httpClient.close();
            }
        }
    }

    /**
     * @Title: setPath
     * @Description: 设置请求路径
     * @param @param path
     * @return void
     * @throws
     */
    public void setPath(String path)
    {
        if (path == null || path.equals(""))
        {
            this.path = "";
        }
        else if (path.charAt(0) == '/')
        {
            this.path = path;
        }
        else
        {
            this.path = "/" + path;
        }
    }


    public static void main(String[] args)
    {
        HttpClientUtilRef2 httpClient = new HttpClientUtilRef2("hq.sinajs.cn", 8080, 10);
        httpClient.setPath("/httpserver?filename=CMS.mpd");
        String response = "";
        try
        {
            response = httpClient.getResponse();
            System.out.println(response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        System.out.println(response);
    }
}