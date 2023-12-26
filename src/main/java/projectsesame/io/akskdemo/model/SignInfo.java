package projectsesame.io.akskdemo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author yangyang
 * @date 2023/12/26 14:32
 */
@Data
public class SignInfo {
    @JsonProperty("x-data")
    Long date;
    @JsonProperty("authorization")
    String signStr;
}
