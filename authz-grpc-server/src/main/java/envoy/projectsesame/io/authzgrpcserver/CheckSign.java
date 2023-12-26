package envoy.projectsesame.io.authzgrpcserver;

import com.google.protobuf.ByteString;
import io.envoyproxy.envoy.service.auth.v3.AttributeContext;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yangyang
 * @date 2023/12/1 15:29
 */
public class CheckSign {
    public static SignInfo checkSignFormat(String sign) throws Exception {
        if ("".equals(sign)) {
            throw new Exception("sign is empty");
        }
        String[] signArr = sign.split(",");

        if (signArr.length != 4) {
            throw new Exception("sign format error");
        }

        // accessKeyId
        String[] idArr = signArr[0].split("=", 2);
        // algorithm
        String[] algorithmArr = signArr[1].split("=", 2);
        // headers
        String[] headersArr = signArr[2].split("=", 2);
        // signature
        String[] signatureArr = signArr[3].split("=", 2);

        if (idArr.length != 2 || algorithmArr.length != 2 || headersArr.length != 2 || signatureArr.length != 2) {
            throw new Exception("sign format error");
        }

        if (!"id".equals(idArr[0]) || !"algorithm".equals(algorithmArr[0]) || !"headers".equals(headersArr[0]) || !"signature".equals(signatureArr[0])) {
            throw new Exception("sign format error");
        }
        return SignInfo.builder().accessKeyId(idArr[1]).algorithm(algorithmArr[1]).signHeaders(headersArr[1].split(";")).signature(signatureArr[1]).build();
    }

    public static String getSignHeader(Map<String, String> headerMaps, String[] signHeaders) throws Exception {
        if (signHeaders.length == 0) {
            return "";
        }
        Map<String, String> treeMap = new TreeMap<>();
        for (String signHeader : signHeaders) {
            if (!headerMaps.containsKey(signHeader.toLowerCase())) {
                throw new Exception("sign header is not exist");
            }
            treeMap.put(signHeader.toLowerCase(), headerMaps.get(signHeader.toLowerCase()));
        }
        StringBuilder sortedHeadersBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            sortedHeadersBuilder.append(entry.getKey());
            sortedHeadersBuilder.append(": ");
            sortedHeadersBuilder.append(entry.getValue().trim());
            sortedHeadersBuilder.append("\n");
        }
        String sortedHeaders = sortedHeadersBuilder.toString();
        sortedHeaders = sortedHeaders.substring(0, sortedHeaders.length() - 1);

        return sortedHeaders;
    }

    public static String getSignQuery(String path) throws Exception {
        String query = URI.create(path).getQuery();
        if (query == null || "".equals(query)) {
            return "";
        }
        String[] queryParams = query.split("&");
        Map<String, String> treeMap = new TreeMap<>();

        for (String q : queryParams) {
            String[] kv = query.split("=");
            treeMap.put(kv[0], kv[1]);
        }

        StringBuilder sortedQueryParamsBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : treeMap.entrySet()) {
            sortedQueryParamsBuilder.append(entry.getKey());
            sortedQueryParamsBuilder.append("=");
            sortedQueryParamsBuilder.append(entry.getValue());
            sortedQueryParamsBuilder.append("&");
        }
        String sortedQueryParams = sortedQueryParamsBuilder.toString();
        sortedQueryParams = sortedQueryParams.substring(0, sortedQueryParams.length() - 1);

        return sortedQueryParams;
    }

    public static String getSignPath(String path) {
        URI uri = URI.create(path);
        return uri.getPath();
    }

    /**
     * 校验请求时间为和现在在15分钟之内
     *
     * @param date 格式为:EEE, dd MMM yyyy HH:mm:ss 'GMT'
     */
    public static boolean checkDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date d = sdf.parse(date);
            Date now = new Date();
            return (now.getTime() - d.getTime()) <= 15 * 60 * 1000;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkDate(Long date) {
        try {
            Date now = new Date();
            return (now.getTime() - date) <= 15 * 60 * 1000;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getSignBody(AttributeContext.HttpRequest http) {
        if (http.getBody().length() > 0){
            return signBody(http.getBody());
        }

        if (!http.getRawBody().isEmpty()){
            return signBody(http.getRawBody().toStringUtf8());
        }

        return "";
    }

    private static String signBody(String body){
        if (body.isEmpty()){
            return "";
        }
        return Tool.base64Encode(Tool.getMD5(body).getBytes());
    }
}


