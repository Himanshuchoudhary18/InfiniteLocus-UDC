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
        "created_by",
        "created_on",
        "id",
        "image_url",
        "is_active",
        "is_deleted",
        "is_published",
        "last_modified_by",
        "last_modified_on",
        "name",
        "published_by",
        "published_on"
})
@Generated("jsonschema2pojo")
public class Datum {

    @JsonProperty("created_by")
    public String createdBy;
    @JsonProperty("created_on")
    public String createdOn;
    @JsonProperty("id")
    public Integer id;
    @JsonProperty("image_url")
    public String imageUrl;
    @JsonProperty("is_active")
    public Boolean isActive;
    @JsonProperty("is_deleted")
    public Boolean isDeleted;
    @JsonProperty("is_published")
    public Boolean isPublished;
    @JsonProperty("last_modified_by")
    public Object lastModifiedBy;
    @JsonProperty("last_modified_on")
    public Object lastModifiedOn;
    @JsonProperty("name")
    public String name;
    @JsonProperty("published_by")
    public Object publishedBy;
    @JsonProperty("published_on")
    public Object publishedOn;

}