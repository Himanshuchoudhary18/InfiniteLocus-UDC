package utilities;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;

/**
 * Need to improve the way waits used in the framework
 * i.e created this class to have a common place for waits
 */
public class Waits {

    static Logger log = LoggerFactory.getLogger(Waits.class);
    private static long DEFAULT_FIND_ELEMENT_TIMEOUT;

    public static long getDefaultFindElementTimeout() {
        if (DEFAULT_FIND_ELEMENT_TIMEOUT != 0) {
            return DEFAULT_FIND_ELEMENT_TIMEOUT;
        }
        DEFAULT_FIND_ELEMENT_TIMEOUT = Long.parseLong(Base.getProperty().getProperty("timeout"));
        return DEFAULT_FIND_ELEMENT_TIMEOUT;
    }


    public static <K extends Throwable> FluentWait<WebDriver> getFluentWait(WebDriver driver, Duration timeoutDuration, Duration pollingDuration, Collection<Class<? extends K>> types) {
        if (types == null || types.isEmpty()) {
            return new FluentWait<>(driver)
                    .withTimeout(timeoutDuration)
                    .pollingEvery(pollingDuration);
        }
        return new FluentWait<>(driver)
                .withTimeout(timeoutDuration)
                .pollingEvery(pollingDuration)
                .ignoreAll(types);
    }

    public static void setImplicitWait(WebDriver driver, Duration duration) {
        log.info("Setting implicit wait to {} milliseconds", duration.toMillis());
        driver.manage().timeouts().implicitlyWait(duration);
    }

}
