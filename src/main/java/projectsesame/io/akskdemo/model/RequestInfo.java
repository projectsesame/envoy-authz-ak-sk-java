package projectsesame.io.akskdemo.model;

import lombok.Data;
import lombok.Getter;
import projectsesame.io.akskdemo.util.tool;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static projectsesame.io.akskdemo.util.tool.base64Encode;

/**
 * @author yangyang
 * @date 2023/11/30 16:43
 */
@Data

/**
 * 请求信息，如果有其他需要签名的可以继续增加
 */
public class RequestInfo {
    public String url;
    public String host;
    public String apiKey;
    /**
     * 签名secret，这里demo固定为secret，实际应用中需要根据accessKeyId获取secret
     */
    public String secret = "secret";
    public String method;
    public Map<String,String> headers;
    public String requestBody;
    public String signature;
    public List<String> signHeaders;

    /**
     *  根据请求信息进行请求签名
     *  demo应用签名字符串生成方式
     *  x-data: 请求方法\n请求路径\n请求参数\n请求时间\n请求头\n请求体
     *  其中请求头按照字典序排序，多个请求头用逗号分隔，需要签名的请求头可以通过signHeaders字段指定，不指定则只对x-data请求头进行签名
     *  x-data请求头为自定义请求头，为请求时间
     *  签名算法为根据secret对x-data进行hmac-sha1加密，然后base64编码
     *  返回的签名字符串为id=apiKey,algorithm=签名算法,headers=签名请求头,signature=签名字符串
     *
     *  该签名方式未包含防重放逻辑，实际应用中需要根据业务场景增加防重放逻辑
     *
     *  @param date 请求时间
     *
     * @return 签名字符串
     */
    public String sign(String date) throws Exception {
        URL parseUrl = URI.create(this.url).toURL();
        String strSign = String.format("x-data: %s\n%s\n%s\n%s\n%s\n%s", signMethod(), signPath(parseUrl), signQueryParams(parseUrl),date,signHeaders(date), signBody());
        System.out.println(strSign);
        byte[] hmacStr = tool.HmacSHA1Encrypt(strSign, this.secret);
        String signature = base64Encode(hmacStr);
        System.out.println(signature);
        return String.format("id=%s,algorithm=hmac-sha1,headers=%s,signature=%s", this.apiKey,signHeadersString(),signature);
    }

    public String sign(Long date) throws Exception {
        URL parseUrl = URI.create(this.url).toURL();
        String strSign = String.format("x-data: %s\n%s\n%s\n%s\n%s\n%s", signMethod(), signPath(parseUrl), signQueryParams(parseUrl),date,signHeaders(String.valueOf(date)), signBody());
        System.out.println(strSign);
        byte[] hmacStr = tool.HmacSHA1Encrypt(strSign, this.secret);
        String signature = base64Encode(hmacStr);
        System.out.println(signature);
        return String.format("id=%s,algorithm=hmac-sha1,headers=%s,signature=%s", this.apiKey,signHeadersString(),signature);
    }

    /**
     * 签名请求方法
     */
    private String signMethod(){
        return !"".equals(this.method) ? this.method : "";
    }

    /**
     * 签名请求路径
     */
    private String signPath(URL url) {
        return !"".equals(url.getPath()) ? url.getPath() : "";
    }

    /**
     * 签名请求参数
     */
    private String signQueryParams(URL url) {
        if (!"".equals(url.getQuery())) {
            return tool.sortQueryParams(url.getQuery());
        }
        return "";
    }

    /**
     * 签名请求头
     */
    private String signHeaders(String date) {
        if (this.headers != null) {
            Map<String,String> map = new HashMap<>();
            map.put("x-date",date);
            this.headers.forEach((k,v) -> {
                if (this.signHeaders.contains(k)) {
                    map.put(k.toLowerCase(),v);
                }
            });
            return tool.sortHeaders(map);
        }
        return "";
    }

    private String signHeadersString() {
       return String.join(";", this.signHeaders);
    }

    private String signBody() {
        if (this.requestBody != null) {
            return base64Encode(tool.getMD5(this.requestBody).getBytes());
        }
        return "";
    }

    private void setSignHeaders(List<String> headers) {
        if (!headers.contains("x-date")){
            headers.add("x-date");
        }
        this.signHeaders = headers;
    }
}

