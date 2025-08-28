package utilsApi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.Filter;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import utilities.Base;
import utilities.CustomLogFilter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static utilsWeb.CommonFunctionsWeb.getScenario;

public class RefactoredRestAssuredHelper {
    private static final Logger logger = LoggerFactory.getLogger(RefactoredRestAssuredHelper.class);
    public static StringBuilder curlCmd;
    public static boolean enableAdditionalFieldsCheckInResponse = Base.getProperty().getProperty("enableAdditionalFieldsCheckInResponse").equalsIgnoreCase("true");
    private static long latency;
    private static String responseToLog;
    private static ValidatableResponse validatableResponse = null;
    private static long currentId = 0;

    private static ExtractableResponse<Response> executeRequest(HTTPRequestType requestType, Map<String, String> headerMap, Map<String, String> params, String requestURL, Object requestBody, RequestSpecification spec, RequestConfigs requestConfigs, long expectedStatusCode, String statusCheckKeyPath) {
        RefactoredRestAssuredHelper.setCurlCmd(generateCurlCommand(requestType, headerMap, params, requestURL, requestBody, spec));

        CommonContext commonContext = CommonContextFactory.getCommonContext();
        CustomLogFilter filter = null;//To be handled later, not needed now

        try {
            RequestSpecification requestSpecification = getRequestSpecification(filter, requestConfigs);
            RequestSpecification initialRequest = createInitialRequest(requestSpecification, headerMap, params, spec, requestBody);

            validatableResponse = executeRequestMethod(requestType, initialRequest, requestURL);
            ExtractableResponse<Response> response = extractResponse(validatableResponse, requestConfigs, requestURL);
            validateStatusCode(response, expectedStatusCode, statusCheckKeyPath);

            logLatency(response, requestURL);
            return response;
        } catch (Exception ex) {
            handleException(ex, filter, commonContext, requestURL);
            throw new AssertionError(ex.getLocalizedMessage() + " URL: " + requestURL);
        }
    }

    private static StringBuilder generateCurlCommand(HTTPRequestType requestType, Map<String, String> headerMap, Map<String, String> params, String requestURL, Object requestBody, RequestSpecification spec) {
        StringBuilder curlCmd = new StringBuilder("curl -X " + requestType.name());

        if (headerMap != null) {
            headerMap.forEach((key, value) -> curlCmd.append(" -H '").append(key).append(": ").append(value).append("'"));
        }

        if (params != null && (requestType == HTTPRequestType.GET || requestType == HTTPRequestType.POST || requestType == HTTPRequestType.PUT)) {
            curlCmd.append(" '").append(requestURL).append("?");
            params.forEach((key, value) -> curlCmd.append(key).append("=").append(value).append("&"));
            curlCmd.setLength(curlCmd.length() - 1);
            curlCmd.append("'");
        } else {
            curlCmd.append(" '").append(requestURL).append("'");
        }

        if (requestBody != null) {
            curlCmd.append(" -d '").append(CommonFunctionsAPI.convertDtoToJson(requestBody)).append("'");
        }

        if (spec != null) {
            curlCmd.append(" --config '").append(spec.toString()).append("'");
        }

        return curlCmd;
    }

    private static RequestSpecification createInitialRequest(RequestSpecification requestSpecification, Map<String, String> headerMap, Map<String, String> params, RequestSpecification spec, Object requestBody) throws JsonProcessingException {
        if (headerMap != null && !headerMap.isEmpty()) {
            requestSpecification.headers(headerMap);
        }

        if (params != null && !params.isEmpty()) {
            requestSpecification.queryParams(params);
        }

        if (requestBody != null) {
            requestSpecification.body(getRequestBody(requestBody));
        }

        if (spec != null) {
            requestSpecification.spec(spec);
        }
        return requestSpecification.when();
    }

    private static ValidatableResponse executeRequestMethod(HTTPRequestType requestType, RequestSpecification initialRequest, String requestURL) {
        switch (requestType) {
            case GET:
                return initialRequest.get(requestURL).then();
            case PUT:
                return initialRequest.put(requestURL).then();
            case PATCH:
                return initialRequest.patch(requestURL).then();
            case POST:
                return initialRequest.post(requestURL).then();
            case DELETE:
                return initialRequest.delete(requestURL).then();
            default:
                throw new IllegalArgumentException("Unsupported request type: " + requestType);
        }
    }

    private static ExtractableResponse<Response> extractResponse(ValidatableResponse validatableResponse, RequestConfigs requestConfigs, String requestURL) {
        ExtractableResponse<Response> response = requestConfigs.isLogsNeeded() ? validatableResponse.log().all().extract() : validatableResponse.extract();
        responseToLog = response.asPrettyString();
        logResponse(responseToLog);
        logLatency(response, requestURL);
        return response;
    }

    private static void logResponse(String responseToLog) {
        if (Base.getProperty().getProperty("logResponse").equalsIgnoreCase("true")) {
            logger.info(responseToLog);
        }
    }

    protected static void logLatency(ExtractableResponse<Response> response, String requestURL) {
        long currentLatency = response.response().timeIn(TimeUnit.MILLISECONDS);
        latency = currentLatency;
//        Collecting API data for latency
        APIResponseTime apiResponseTime = new APIResponseTime();
        apiResponseTime.setApiName(requestURL);
        apiResponseTime.setCurlRequest(curlCmd);
        apiResponseTime.setResponseTime(currentLatency);
        apiResponseTime.setId(currentId++);
        ApiUtils.apiList.add(apiResponseTime);

        long minAcceptableLatency = Long.parseLong(Base.getProperty().getProperty("minAcceptableLatency"));

        if (currentLatency < minAcceptableLatency) {
            return;
        }

        if (ApiUtils.latencyMap.getOrDefault(requestURL, 0L) > currentLatency)
            currentLatency = ApiUtils.latencyMap.get(requestURL);
        ApiUtils.latencyMap.put(requestURL, currentLatency);
    }

    private static void handleException(Exception exception, CustomLogFilter filter, CommonContext commonContext, String requestURL) {
        fillLogs(filter, commonContext);
        logger.info(exception.getLocalizedMessage() + " URL: " + requestURL);
    }

    private static void fillLogs(CustomLogFilter filter, CommonContext commonContext) {
        if (filter != null) {
            getScenario().log("\nAPI Request:\n\n" + filter.getRequestBuilder() + "\nAPI Response:" + filter.getResponseBuilder());
        }
    }

    private static void validateStatusCode(ExtractableResponse<Response> response, long expectedStatusCode, String statusCheckKeyPath) {
        if (Base.getProperty().getProperty("statusCheck200").equalsIgnoreCase("true") && expectedStatusCode > 0) {
            if (statusCheckKeyPath == null) {
                logger.warn("Requested key path is null skipping status code check in response data");
                return;
            }
            String statusCode = CommonFunctionsAPI.getKeyFromResponseJson(response.response(), statusCheckKeyPath);
            Assert.assertEquals(statusCode, String.valueOf(expectedStatusCode), "Global Check Failed: Actual vs Expected Status Code - Expected: " + expectedStatusCode + " Actual: " + statusCode);
        }
    }

    protected static RestAssuredConfig getDefaultConfig() {
        return RestAssured.config().httpClient(HttpClientConfig.httpClientConfig().setParam("http.connection.timeout", 60000).setParam("http.socket.timeout", 60000));
    }

    protected static RequestSpecification getRequestSpecification(Filter filter, RequestConfigs requestConfigs) {
        RestAssuredConfig config = getDefaultConfig();
        RequestSpecification requestSpec = (filter != null) ? RestAssured.given().filter(filter).config(config) : RestAssured.given().config(config);

        if (requestConfigs.isLogsNeeded()) {
            requestSpec.log().all();
        }

        return requestSpec;
    }

    protected static String getRequestBody(Object requestBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.convertValue(requestBody, JsonNode.class);
        return objectMapper.writeValueAsString(jsonNode);
    }

    public static ExtractableResponse<Response> callApi(HTTPRequestType requestType, Map<String, String> headerMap, Map<String, String> params, String requestURL, Object requestBody, RequestSpecification spec, RequestConfigs requestConfigs, int retry, long expectedStatusCode, String statusCheckKeyPath) {
        ExtractableResponse<Response> response = null;
        if (requestConfigs == null)
            requestConfigs = new RequestConfigs();
        while (retry >= 0) {
            try {
                response = executeRequest(requestType, headerMap, params, requestURL, requestBody, spec, requestConfigs, expectedStatusCode, statusCheckKeyPath);
                break;
            } catch (AssertionError assertionError) {
                logger.error(assertionError.getMessage());
                if (retry == 0) {
                    throw assertionError;
                }
                retry--;
            }
        }

        return response;
    }

    private static ExtractableResponse<Response> callApi(HTTPRequestType requestType, Map<String, String> headerMap, Map<String, String> params, String requestURL, Object requestBody, RequestSpecification spec, RequestConfigs requestConfigs, int retry, long expectedStatusCode) {
        return callApi(requestType, headerMap, params, requestURL, requestBody, spec, requestConfigs, retry, expectedStatusCode, "statusCode");
    }

    protected static void setLatencyToFile(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
//        Old way of writing latency data to file
        try {
            String directoryPath = "testResults/LatencyData/";
            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directoryPath + "LatencyData_" + fileName + ".json");
            objectMapper.writeValue(file, ApiUtils.latencyMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        New way of writing latency data to json file to show in HTML
        try {
            String directoryPath = "testResults/LatencyData/";
            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directoryPath + "LatencyDataTest_" + fileName + ".json");
            objectMapper.writeValue(file, ApiUtils.apiList);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        New way of writing latency data to csv file to analyse in excel with separator |
        try {
            String directoryPath = "testResults/LatencyData/";
            File directory = new File(directoryPath);

            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directoryPath + "LatencyDataTest_" + fileName + ".csv");

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write CSV header
                writer.write("apiName|curlRequest|responseTime|id");
                writer.newLine();

                // Write data
                for (APIResponseTime apiResponseTime : ApiUtils.apiList) {
                    writer.write(apiResponseTime.getApiName() + "|" +
                            apiResponseTime.getCurlRequest() + "|" +
                            apiResponseTime.getResponseTime() + "|" +
                            apiResponseTime.getId());
                    writer.newLine();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void setCurlCmd(StringBuilder curlCmd) {
        RefactoredRestAssuredHelper.curlCmd = curlCmd;
        logger.info("Curl Command: {}", RefactoredRestAssuredHelper.curlCmd.toString());
    }

    public enum HTTPRequestType {
        GET, POST, DELETE, PUT, PATCH;
    }
}
