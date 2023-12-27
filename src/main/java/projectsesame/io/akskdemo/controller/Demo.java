package projectsesame.io.akskdemo.controller;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import projectsesame.io.akskdemo.model.RequestInfo;
import projectsesame.io.akskdemo.model.SignInfo;
import projectsesame.io.akskdemo.util.Constants;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

import projectsesame.io.akskdemo.util.tool;

/**
 * @author yangyang
 * @date 2023/11/30 16:41
 */
@RestController
public class Demo {
    @PostMapping("/mock")
    public Mono<ResponseEntity<String>> json(@RequestBody(required = false) RequestInfo requestInfo) throws Exception {
        Long date = System.currentTimeMillis();
        String sign = requestInfo.sign(date);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        // Send request
        if (Objects.equals(requestInfo.getMethod(), Constants.HTTP_METHOD_GET)){
            HttpGet httpGet = new HttpGet(requestInfo.getUrl());
            httpGet.setHeader("Host", requestInfo.getHost());
            httpGet.setHeader("x-date", String.valueOf(date));
            httpGet.setHeader("Authorization", sign);
            requestInfo.getHeaders().forEach(httpGet::setHeader);
            response = httpClient.execute(httpGet);
        }

        if (Objects.equals(requestInfo.getMethod(), Constants.HTTP_METHOD_POST)) {
            HttpPost httpPost = new HttpPost(requestInfo.getUrl());
            httpPost.setHeader("Host", requestInfo.getHost());
            httpPost.setHeader("x-date", String.valueOf(date));
            httpPost.setHeader("Content-MD5", tool.base64Encode(tool.getMD5(requestInfo.getRequestBody()).getBytes()));
            httpPost.setHeader("Authorization", sign);
            requestInfo.getHeaders().forEach(httpPost::setHeader);
            StringEntity stringEntity = new StringEntity(requestInfo.getRequestBody(), "UTF-8");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
        }
        // Receive response
        assert response != null;
        HttpEntity responseEntity = response.getEntity();
        if (responseEntity != null) {
            return Mono.just(ResponseEntity.status(response.getStatusLine().getStatusCode()).body(EntityUtils.toString(responseEntity)));
        }
        return Mono.just(ResponseEntity.status(response.getStatusLine().getStatusCode()).build());
    }

    @PostMapping("/signstr")
    public Mono<ResponseEntity<SignInfo>> signstr(@RequestBody(required = false) RequestInfo requestInfo) throws Exception {
        Long date = System.currentTimeMillis();
        String sign = requestInfo.sign(date);
        SignInfo signInfo = new SignInfo();
        signInfo.setSignStr(sign);
        signInfo.setDate(date);
        return Mono.just(ResponseEntity.status(200).body(signInfo));
    }

    @PostMapping("/signstrbytime")
    public Mono<ResponseEntity<SignInfo>> signstrbytime(@RequestBody(required = false) RequestInfo requestInfo,@RequestParam long date) throws Exception {
        String sign = requestInfo.sign(date);
        SignInfo signInfo = new SignInfo();
        signInfo.setSignStr(sign);
        signInfo.setDate(date);
        return Mono.just(ResponseEntity.status(200).body(signInfo));
    }

}
