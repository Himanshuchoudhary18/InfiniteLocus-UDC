package utilsApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RequestConfigs {
    private static final Logger logger = LoggerFactory.getLogger(RequestConfigs.class);
    private static boolean logsNeeded = true;
    private String userName;
    private String passWord;
    private Integer timeout;
    private Boolean autoRedirection;
    private Boolean urlEncoding;
    private Map<String, Object> formParams = new HashMap();
    private Boolean specificReqLogNeeded;
    private boolean isCsvResponse = false;

    public RequestConfigs() {
    }

    public boolean isLogsNeeded() {
        if (specificReqLogNeeded == null)
            specificReqLogNeeded = RequestConfigs.logsNeeded;
        return specificReqLogNeeded;
    }

    public static void setLogsNeeded(boolean logsNeeded) {
        RequestConfigs.logsNeeded = logsNeeded;
    }

    public void setSpecificReqLogNeeded(Boolean specificReqLogNeeded) {
        logger.warn("Log printing status for particular object changed to {}", specificReqLogNeeded);
        this.specificReqLogNeeded = specificReqLogNeeded;
    }

    public Map<String, Object> getFormParams() {
        return this.formParams;
    }

    public void setFormParams(Map<String, Object> formParams) {
        this.formParams = formParams;
    }

    public Boolean getAutoRedirection() {
        return this.autoRedirection;
    }

    public void setAutoRedirection(Boolean autoRedirection) {
        this.autoRedirection = autoRedirection;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return this.passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Boolean getUrlEncoding() {
        return this.urlEncoding;
    }

    public void setUrlEncoding(Boolean urlEncoding) {
        this.urlEncoding = urlEncoding;
    }

    public boolean getIsCsvResponse() {
        return this.isCsvResponse;
    }

    public void setIsCsvResponse(boolean isCsvResponse) {
        this.isCsvResponse = isCsvResponse;
    }
}
