package envoy.projectsesame.io.authzgrpcserver;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author yangyang
 * @date 2023/12/1 16:12
 */
public class Tool {
    private static final String ENCODING = "UTF-8";
    private static final String MAC_NAME = "HmacSHA1";

    private static final String MAC_SHA_256 = "HmacSHA256";
    public static byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        return getBytes(encryptText, encryptKey, MAC_NAME);
    }

    private static byte[] getBytes(String encryptText, String encryptKey, String macName) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = encryptKey.getBytes(ENCODING);
        SecretKey secretKey = new SecretKeySpec(data, macName);
        Mac mac = Mac.getInstance(macName);
        mac.init(secretKey);

        byte[] text = encryptText.getBytes(ENCODING);
        return mac.doFinal(text);
    }

    public static byte[] HmacSHA256Encrypt(String encryptText, String encryptKey) throws Exception {
        return getBytes(encryptText, encryptKey, MAC_SHA_256);
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
