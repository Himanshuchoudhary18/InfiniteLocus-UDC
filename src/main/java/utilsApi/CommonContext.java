package utilsApi;

import io.cucumber.java.Scenario;
import lombok.Getter;
import lombok.Setter;
import utilities.CustomLogFilter;

@Setter
@Getter
public class CommonContext {
    private static final CommonContext testContext = new CommonContext();
    private Scenario scenario;
    private CustomLogFilter filter;

    public CommonContext() {
    }

    public static CommonContext getInstance() {
        return testContext;
    }

}
