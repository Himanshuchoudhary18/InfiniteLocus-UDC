package utilities;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ExtentManager {
    private static final String filePath = Constants.EXTENTREPORT_PATH + "extentReport.html";
    static Logger logger = LoggerFactory.getLogger(ExtentManager.class);
    private static ExtentReports extent;
    private static ExtentHtmlReporter htmlReporter;

    public static ExtentReports getInstance() {
        if (extent == null)
            getExtent();
        return extent;
    }

    public static ExtentReports getExtent() {
        Date d = new Date();
        if (extent != null) {
            return extent;
        } else {
            extent = new ExtentReports();
            extent.attachReporter(getHtmlReporter());
            extent.setSystemInfo("Application", Base.getProperty().getProperty("type").toUpperCase());
            extent.setSystemInfo("Environment", Base.getProperty().getProperty("environment").toUpperCase());
            extent.setSystemInfo("Platform", Base.getProperty().getProperty("platform").toUpperCase());
            extent.setSystemInfo("OS", System.getProperty("os.name").toUpperCase());
            extent.setSystemInfo("Run_Date", d.toString());

            extent.setAnalysisStrategy(AnalysisStrategy.TEST);
            return extent;
        }
    }


    public static ExtentHtmlReporter getHtmlReporter() {
        logger.info(filePath);
        htmlReporter = new ExtentHtmlReporter(filePath);
        htmlReporter.config().setChartVisibilityOnOpen(true);
        htmlReporter.config().setDocumentTitle("Infinite Locus Automation Report");
        htmlReporter.config().setReportName(Base.getProperty().getProperty("type").toUpperCase() + " : Infinite Locus Automation Test Case Execution Report");
        htmlReporter.config().setTestViewChartLocation(ChartLocation.TOP);
        htmlReporter.config().setChartVisibilityOnOpen(true);
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.setAppendExisting(false);
        return htmlReporter;
    }
}
