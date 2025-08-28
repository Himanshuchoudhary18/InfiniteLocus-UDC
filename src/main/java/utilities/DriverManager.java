package utilities;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DriverManager extends Base {
    static Logger logger = LoggerFactory.getLogger(DriverManager.class);
    private static Map<String, Method> methods = new HashMap<String, Method>();

    public static WebDriver getDriver() {
        return Base.getDriver();
    }

    public static void setDriver(WebDriver dvr) {
        Base.setDriver(dvr);
    }

    public static WebDriver getDriverInstance(String browser, String url) {
        if (getDriver() == null) {
            if (browser.equalsIgnoreCase("CHROME")) {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");

                // By-pass CORS
                if (Base.getProperty().getProperty("bypassCORS").equalsIgnoreCase("true")) {
                    options.addArguments("--disable-web-security");
                    options.addArguments("--allow-running-insecure-content");
                }
                if (Base.getProperty().getProperty("headless").equalsIgnoreCase("true")) {
                    options.addArguments("--headless");
                    options.addArguments("--disable-gpu");
                    options.addArguments("--window-size=1920,1080");
                    options.addArguments("--ignore-certificate-errors");
                    options.addArguments("--disable-extensions");
                    options.addArguments("--no-sandbox");
                    options.addArguments("--disable-dev-shm-usage");
                }
                options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
                options.setCapability("browserVersion", "stable");
                options.setCapability("browserName", "chrome");
                Map<String, Object> prefs = new HashMap<>();
                prefs.put("profile.default_content_setting_values.notifications", Optional.of(1)); // 1 = Allow, 2 = Block
                options.setExperimentalOption("prefs", prefs);
                setDriver(new ChromeDriver(options));
            } else {
                logger.info("Please Select a valid browser");
                Assert.fail("Unable to launch browser : " + browser);
            }
        }
        setDriver(getDriver());
        return getDriver();
    }

    public static void killDriverInstance() {
        if (getDriver() != null) {
            // getDriver().quit();
            setDriver(null);
        }
    }

    public static void setImplicitWait(int time) {
        getDriver().manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(time));
    }

    public static void setPageLoadTimeOut(int time) {
        getDriver().manage().timeouts().pageLoadTimeout(java.time.Duration.ofSeconds(time));
    }
}