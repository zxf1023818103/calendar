package chaoxinghelper.apiclient.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostAccountInfoResult {
    public String mes;
    public String url;
    public Boolean status;
    public String errorMsg;
    public Integer type;
}
