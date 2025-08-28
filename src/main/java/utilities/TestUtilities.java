package utilities;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utilsApi.ApiUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

public class TestUtilities extends Base {
    public static String env;
    public static String elementPath;
    public static HashMap<String, HashMap<String, HashMap<String, String>>> locators = new HashMap<String, HashMap<String, HashMap<String, String>>>();
    public static DesiredCapabilities capabilities = new DesiredCapabilities();
    public static Logger log = LoggerFactory.getLogger(TestUtilities.class);
    static Properties prop = new Properties();
    ApiUtils apiUtils = new ApiUtils();

    public static Properties loadConfigProperties() {
        try {
            FileInputStream fis = new FileInputStream(
                    new File(Constants.CONFIGURATION_PROPERTIES));
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static void archiveExtentReports() {
        Date d = new Date();
        String reportName = "ExtenReport_" + d.toString().replaceAll("[ :]", "_") + ".html";
        String absoluteExtentReportPath = Constants.EXTENTREPORT_PATH + "extentReport.html";

        try {
            File scrFile = new File(absoluteExtentReportPath);
            FileUtils.copyFile(scrFile, new File(Constants.ARCHIVED_EXTENTREPORT_PATH + reportName));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
