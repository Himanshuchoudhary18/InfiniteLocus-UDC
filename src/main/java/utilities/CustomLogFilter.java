package utilities;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomLogFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CustomLogFilter.class);
    private StringBuilder requestBuilderLogs;
    private StringBuilder responseBuilderLogs;

    public CustomLogFilter() {
    }

    public Response filter(FilterableRequestSpecification filterableRequestSpecification, FilterableResponseSpecification filterableResponseSpecification, FilterContext filterContext) {
        Response response = filterContext.next(filterableRequestSpecification, filterableResponseSpecification);

        try {
            this.requestBuilderLogs = new StringBuilder();
            this.requestBuilderLogs.append("\n");
            this.requestBuilderLogs.append("Request method: " + this.objectValidation(filterableRequestSpecification.getMethod()));
            this.requestBuilderLogs.append("\n");
            this.requestBuilderLogs.append("Request URI: " + this.objectValidation(filterableRequestSpecification.getURI()));
            this.requestBuilderLogs.append("\n");
            this.requestBuilderLogs.append("Form Params: " + this.objectValidation(filterableRequestSpecification.getFormParams()));
            this.requestBuilderLogs.append("\n");
            this.requestBuilderLogs.append("Request Param: " + this.objectValidation(filterableRequestSpecification.getRequestParams()));
            this.requestBuilderLogs.append("\n");
            this.requestBuilderLogs.append("Headers: " + this.objectValidation(filterableRequestSpecification.getHeaders()));
            this.requestBuilderLogs.append("\n");
            this.requestBuilderLogs.append("Cookies: " + this.objectValidation(filterableRequestSpecification.getCookies()));
            this.requestBuilderLogs.append("\n");
            this.requestBuilderLogs.append("Proxy: " + this.objectValidation(filterableRequestSpecification.getProxySpecification()));
            this.requestBuilderLogs.append("\n");
            this.requestBuilderLogs.append("Body: " + this.objectValidation(filterableRequestSpecification.getBody()));
            this.requestBuilderLogs.append("\n");
            this.requestBuilderLogs.append("******************************");
            this.responseBuilderLogs = new StringBuilder();
            this.responseBuilderLogs.append("\n\n\n");
            this.responseBuilderLogs.append("Status Code: " + response.getStatusCode());
            this.responseBuilderLogs.append("\n");
            this.responseBuilderLogs.append("Status Line: " + response.getStatusLine());
            this.responseBuilderLogs.append("\n");
            this.responseBuilderLogs.append("BonusImage_ResponseDto Cookies: " + response.getDetailedCookies());
            this.responseBuilderLogs.append("\n");
            this.responseBuilderLogs.append("BonusImage_ResponseDto Content Type: " + response.getContentType());
            this.responseBuilderLogs.append("\n");
            this.responseBuilderLogs.append("BonusImage_ResponseDto Headers: " + response.getHeaders());
            this.responseBuilderLogs.append("\n");
            this.responseBuilderLogs.append("BonusImage_ResponseDto Body: \n" + response.getBody().prettyPrint());
        } catch (Exception var6) {
            logger.error("Error occurred : " + var6.getLocalizedMessage());
        }

        return response;
    }

    public String getRequestBuilder() {
        return this.requestBuilderLogs.toString();
    }

    public String getResponseBuilder() {
        return this.responseBuilderLogs.toString();
    }

    public String objectValidation(Object o) {
        return o == null ? null : o.toString();
    }
}
