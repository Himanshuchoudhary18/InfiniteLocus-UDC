package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static utilsWeb.CommonFunctionsWeb.takeScreenShotWeb;

/**
 * Base class for all test classes. Handles reporting, driver management, and test lifecycle hooks.
 * All test classes should extend this class.
 */
public class Base {
    // ======================
    // Thread-Local Fields (Thread-Safe)
    // ======================
    /**
     * Thread-local WebDriver instance for thread safety.
     */
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    /**
     * Thread-local Properties instance for thread safety.
     */
    private static final ThreadLocal<Properties> property = new ThreadLocal<>();
    /**
     * Thread-local ExtentTest for class-level reporting.
     */
    public static final ThreadLocal<ExtentTest> classLevelReport = new ThreadLocal<>();
    /**
     * Thread-local ExtentTest for test-level reporting.
     */
    public static final ThreadLocal<ExtentTest> testLevelReport = new ThreadLocal<>();
    // ======================
    // Static Fields (Global)
    // ======================
    /**
     * ExtentReports instance for reporting.
     */
    public static ExtentReports extent;
    /**
     * Name of the current test class.
     */
    public static String className;
    /**
     * Scenario status map for reporting.
     */
    public static final Map<String, Map<String, Object>> SCENARIO_STATUS_MAP = new ConcurrentHashMap<>();
    /**
     * Lambda execution flag.
     */
    public static String isRunningOnLambda;
    /**
     * Logger for Base class.
     */
    public static final Logger logger = LoggerFactory.getLogger(Base.class);
    /**
     * Platform name (web, mobile, etc.).
     */
    public static String platformName;
    /**
     * Name of the current test method.
     */
    private static String testName;
    /**
     * Unique test run ID.
     */
    private static String testRunId;

    // ======================
    // Instance Fields
    // ======================
    /**
     * OS name for the current environment.
     */
    protected final String os = System.getProperty("os.name").toLowerCase();
    /**
     * If true, driver will be killed after each test method.
     */
    private boolean shouldKillDriverAfterTest = false;

    // ======================
    // Thread-Local Accessors
    // ======================
    public static WebDriver getDriver() { return driver.get(); }
    public static void setDriver(WebDriver drv) { driver.set(drv); }
    public static Properties getProperty() { return property.get(); }
    public static void setProperty(Properties prop) { property.set(prop); }

    /**
     * Default constructor. Sets OS-specific properties.
     */
    public Base() {
        OSValidator.setPropValues(os);
    }

    /**
     * Serializes the scenario status map to a JSON file.
     * @param filePath Path to output JSON file.
     */
    public static void setScenarioStatusMapToJSON(final String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(filePath), Base.SCENARIO_STATUS_MAP);
        } catch (Exception e) {
            logger.error("Failed to write scenario status map to JSON", e);
        }
    }

    /**
     * Loads configuration and initializes reporting before the suite starts.
     */
    @BeforeSuite(alwaysRun = true)
    @Parameters({"env", "type", "runningOnLambda"})
    public void setUpResources(@Optional String env, @Optional String type, @Optional("false") String runningOnLambda) {
        setProperty(TestUtilities.loadConfigProperties());
        if (getProperty().getProperty("runningOnJenkins").equalsIgnoreCase("true")) {
            getProperty().setProperty("environment", env);
            getProperty().setProperty("type", type);
            getProperty().setProperty("runningOnLambda", runningOnLambda);
            isRunningOnLambda = runningOnLambda;
            logger.info("Running on Jenkins with parameters type:- {} env:- {} countryCode:- {} runningOnLambda:- {}", type, env, runningOnLambda);
        } else {
            type = getProperty().getProperty("type");
            env = getProperty().getProperty("environment");
            isRunningOnLambda = getProperty().getProperty("runningOnLambda");
            logger.info("Running on local machine with parameters type:- {} env:- {} runningOnLambda:- {}", type, env, isRunningOnLambda);
        }
        extent = ExtentManager.getExtent();
    }

    /**
     * Initializes class-level reporting before each test class.
     */
    @BeforeClass(alwaysRun = true)
    @Parameters({"env", "type", "runningOnLambda"})
    public void startClass(@Optional String env, @Optional String type, @Optional("false") String runningOnLambda) {
        ExtentTest parent = extent.createTest(getTestDescription(getClass()));
        parent.assignCategory("Epic_Level_Report");
        classLevelReport.set(parent);
        classLevelReport.get().log(Status.INFO, "Execution Started for : " + getTestDescription(getClass()));
        className = this.getClass().getSimpleName();
    }

    /**
     * Initializes test-level reporting before each test method.
     */
    @Parameters({"platformName"})
    @BeforeMethod(alwaysRun = true)
    public void startMethod(@Optional("platformName") String platformName, final Method m, final ITestResult result) {
        String description = getTestDescription(m);
        logger.info("Started Execution of Test Case : {}", description);
        testName = m.getName();
        testRunId = m.getName();
        ExtentTest test = classLevelReport.get().createNode(description);
        test.assignCategory("Test_Level_Report");
        testLevelReport.set(test);
        testLevelReport.get().log(Status.INFO, "Execution Started for : " + description);
    }

    /**
     * Handles reporting and driver cleanup after each test method.
     */
    @SneakyThrows
    @Parameters({"platformName"})
    @AfterMethod(alwaysRun = true)
    public void killMethod(@Optional("platformName") String platformName, final Method m, final ITestResult result) {
        String description = getTestDescription(m);
        logger.info("Ended Execution of Test Case : {}", description);
        testLevelReport.get().log(Status.INFO, "Execution Ended for : " + description);
        if (!result.isSuccess()) {
            try {
                if (getDriver() != null)
                    testLevelReport.get().addScreenCaptureFromPath(takeScreenShotWeb(result.getMethod().getMethodName()).getPath().substring(26));
            } catch (Exception e) {
                logger.error("Error in taking screenshot", e);
            }
        }
        extent.flush();
        if (shouldKillDriverAfterTest) {
            logger.warn("Killing Driver Instance");
            DriverManager.killDriverInstance();
            logger.info("Driver Instance Killed resetting value to false to kill after test");
        }
    }

    /**
     * Handles reporting and driver cleanup after each test class.
     */
    @SneakyThrows
    @AfterClass(alwaysRun = true)
    public void killClass() {
        String description = getTestDescription(getClass());
        classLevelReport.get().log(Status.INFO, "Execution Started for : " + description);
        logger.info("Ended Execution of Test Class : {}", description);
        DriverManager.killDriverInstance();
    }

    /**
     * Archives extent reports after the suite ends.
     */
    @AfterSuite(alwaysRun = true)
    public void killResources() {
        TestUtilities.archiveExtentReports();
    }

    // ======================
    // Helper Methods
    // ======================

    /**
     * Gets the TestNG @Test description for a class, or the class name if not present.
     */
    private static String getTestDescription(final Class<?> clazz) {
        Test annotation = clazz.getAnnotation(Test.class);
        return annotation != null ? annotation.description() : clazz.getSimpleName();
    }

    /**
     * Gets the TestNG @Test description for a method, or the method name if not present.
     */
    private static String getTestDescription(final Method m) {
        Test annotation = m.getAnnotation(Test.class);
        return annotation != null ? annotation.description() : m.getName();
    }

    /**
     * Gets the current test run ID.
     */
    public static String getTestRunId() {
        return testRunId;
    }

    /**
     * Sets whether the driver should be killed after each test method.
     */
    public void setShouldKillDriverAfterTest(boolean shouldKill) {
        this.shouldKillDriverAfterTest = shouldKill;
    }
}