package utilities;

public class Constants {
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String CONFIGURATION_PROPERTIES = USER_DIR + "/src/test/resources/configurations.properties";
    public static final String EXTENTREPORT_PATH = "./testResults/extentReports/";
    public static final String ARCHIVED_EXTENTREPORT_PATH = "./testResults/extentreportsArchived/";
    public static final String ARCHIVED_SCREENSHOT_PATH = "./testResults/screenshotArchived/";
    public static long currentTimestamp = System.currentTimeMillis();
    public static final String SCREENSHOT_PATH = "/testResults/Screenshot/" + currentTimestamp + ".jpg";
}
