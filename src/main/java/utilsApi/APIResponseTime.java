package utilsApi;

public class APIResponseTime {
    private long id;
    private String apiName;
    private StringBuilder curlRequest;
    private long responseTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public StringBuilder getCurlRequest() {
        return curlRequest;
    }

    public void setCurlRequest(StringBuilder curlRequest) {
        this.curlRequest = curlRequest;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }
}
