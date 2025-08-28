package qa.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Generated;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "data",
        "status"
})
@Generated("jsonschema2pojo")
public class Data {

    @JsonProperty("data")
    public List<Datum> data;
    @JsonProperty("status")
    public String status;

}