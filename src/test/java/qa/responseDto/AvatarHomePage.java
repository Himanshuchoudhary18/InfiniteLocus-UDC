package qa.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Generated;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "message",
        "status",
        "success"
})
@Generated("jsonschema2pojo")
public class AvatarHomePage {

    @JsonProperty("data")
    public Data data;
    @JsonProperty("message")
    public String message;
    @JsonProperty("status")
    public Integer status;
    @JsonProperty("success")
    public Boolean success;
}