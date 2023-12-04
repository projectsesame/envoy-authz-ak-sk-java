package projectsesame.io.akskdemo.util;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yangyang
 * @date 2023/11/30 17:08
 */
public class tool {
    private static final String ENCODING = "UTF-8";
    private static final String MAC_NAME = "HmacSHA1";

    public static String getGMTTime(){
        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String gmtTime = sdf.format(cd.getTime());
        return gmtTime;
    }

    public static String sortQueryParams(String queryParam){
        // parameters should be in alphabetical order
        if (queryParam == null || "".equals(queryParam)){
            return "";
        }

        String[] queryParams = queryParam.split("&");
        Map<String, String> queryPairs = new TreeMap<>();
        for(String query: queryParams){
            String[] kv = query.split("=");
            queryPairs.put(kv[0], kv[1]);
        }

        StringBuilder sortedParamsBuilder = new StringBuilder();
        for (Map.Entry<String,String> entry : queryPairs.entrySet()) {
            sortedParamsBuilder.append(entry.getKey());
            sortedParamsBuilder.append("=");
            sortedParamsBuilder.append(entry.getValue());
            sortedParamsBuilder.append("&");
        }
        String sortedParams = sortedParamsBuilder.toString();
        sortedParams = sortedParams.substring(0, sortedParams.length() - 1);

        return sortedParams;
    }

    public static String sortHeaders(Map<String, String> headers){
        if (headers == null || headers.isEmpty()){
            return "";
        }

        Map<String, String> queryPairs = new TreeMap<>(headers);
        StringBuilder sortedHeadersBuilder = new StringBuilder();
        for (Map.Entry<String,String> entry : queryPairs.entrySet()) {
            sortedHeadersBuilder.append(entry.getKey());
            sortedHeadersBuilder.append(": ");
            sortedHeadersBuilder.append(entry.getValue().trim());
            sortedHeadersBuilder.append("\n");
        }
        String sortedHeaders = sortedHeadersBuilder.toString();
        sortedHeaders = sortedHeaders.substring(0, sortedHeaders.length() - 1);

        return sortedHeaders;
    }

    public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        Mac mac = Mac.getInstance(MAC_NAME);
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(ENCODING);
        return mac.doFinal(text);
    }

    public static String base64Encode(byte[] key) {
        final Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(key);
    }

    public static String getMD5(String str) {
        String md5Hex = DigestUtils.md5Hex(str);
        return md5Hex;
    }


}
