package envoy.projectsesame.io.authzgrpcserver;

import lombok.Builder;
import lombok.Data;

/**
 * @author yangyang
 * @date 2023/12/1 16:07
 */
@Data
@Builder
public class SignInfo {
    public String method;
    public String path;
    public String params;
    public String headers;
    public String body;
    public String date;

    private String accessKeyId;
    private String algorithm;
    private String secret;
    private String[] signHeaders;
    private String signature;

    /**
     *  根据key获取secret
     * @return secret
     */
    public String getSecret() {
        if ("key".equals(this.accessKeyId)){
            this.secret = "secret";
            return this.secret;
        }
        return "";
    }
}
