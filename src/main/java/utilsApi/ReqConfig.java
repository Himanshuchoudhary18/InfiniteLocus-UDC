package utilsApi;

public class ReqConfig {
    public static RequestConfigs getDefaultRequest() {
        RequestConfigs requestConfigs = new RequestConfigs();
        requestConfigs.setTimeout(60000);
        return requestConfigs;
    }

    public static RequestConfigs getRequestConfig(Boolean isLogNeeded) {
        RequestConfigs requestConfigs = getDefaultRequest();
        requestConfigs.setSpecificReqLogNeeded(isLogNeeded);
        return requestConfigs;
    }

}
