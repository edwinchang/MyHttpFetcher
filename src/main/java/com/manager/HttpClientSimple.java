package com.manager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by edwinchang on 2016-9-15.
 */
public class HttpClientSimple {
    public static void main(String args[]) throws IOException {
        //URI uri = new URIBuilder()
        //        .setScheme("http")
        //        .setHost("www.google.com")
        //        .setPath("/search")
        //        .setParameter("q", "httpclient")
        //        .setParameter("btnG", "Google Search")
        //        .setParameter("aq", "f")
        //        .setParameter("oq", "")
        //        .build();

        String uri = "http://hq.sinajs.cn/list=sh122143";

        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setExpectContinueEnabled(true)
                .build();


        //                .useExpectContinue()
//                .version(HttpVersion.HTTP_1_1)
//                .bodyString("Important stuff", ContentType.DEFAULT_TEXT)
//                .execute().returnContent().asBytes();


        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = new HttpPost(uri);

        //List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        //formparams.add(new BasicNameValuePair("account", ""));
        //formparams.add(new BasicNameValuePair("password", ""));
        //HttpEntity reqEntity = new UrlEncodedFormEntity(formparams, "utf-8");
        //post.setEntity(reqEntity);

        post.setConfig(requestConfig);
        HttpResponse response = httpclient.execute(post);

        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity resEntity = response.getEntity();
            String message = EntityUtils.toString(resEntity, "utf-8");
            System.out.println(message);
        } else {
            System.out.println("请求失败");
        }
    }

// Execute a POST with the 'expect-continue' handshake, using HTTP/1.1,
// containing a request body as String and return response content as byte array.
//        Request.Post("http://somehost/do-stuff")
//                .useExpectContinue()
//                .version(HttpVersion.HTTP_1_1)
//                .bodyString("Important stuff", ContentType.DEFAULT_TEXT)
//                .execute().returnContent().asBytes();



}
