package envoy.projectsesame.io.authzgrpcserver;

import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

/**
 * @author yangyang
 * @date 2023/11/30 16:43
 */
@Data
@Builder
/**
 * 请求信息，如果有其他需要签名的可以继续增加
 */
public class RequestInfo {
    public String method;
    public String path;
    public String params;
    public String headers;
    public String body;
    public String date;

    public boolean checkSign(SignInfo signInfo) throws Exception {
        String strSign = String.format("x-data: %s\n%s\n%s\n%s\n%s\n%s", this.method,this.path,this.params,this.date,this.headers,this.body);

        switch (signInfo.getAlgorithm()) {
            case "hmac-sha1":
                return checkHmacSHA1Sign(strSign, signInfo);
            case "hmac-sha256":
                return checkHmacSHA256Sign(strSign, signInfo);
            default:
                return false;
        }
    }

    private boolean checkHmacSHA1Sign(String strSign, SignInfo signInfo) throws Exception {
        byte[] hmacStr = Tool.HmacSHA1Encrypt(strSign, signInfo.getSecret());
        String signature = Tool.base64Encode(hmacStr);
        return signature.equals(signInfo.getSignature());
    }

    private boolean checkHmacSHA256Sign(String strSign, SignInfo signInfo) throws Exception {
        byte[] hmacStr = Tool.HmacSHA256Encrypt(strSign, signInfo.getSecret());
        String signature = Tool.base64Encode(hmacStr);
        return signature.equals(signInfo.getSignature());
    }
    // todo 更多签名算法
}

