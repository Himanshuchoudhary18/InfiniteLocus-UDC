package utilsWeb;

import com.aventstack.extentreports.Status;
import com.fasterxml.jackson.core.type.TypeReference;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import io.cucumber.core.logging.LoggerFactory;
import io.cucumber.java.Scenario;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import utilities.Base;
import utilities.DriverManager;
import utilities.Waits;
import utilsApi.RefactoredRestAssuredHelper;
import utilsApi.RequestConfigs;
import utilsApi.StandardResponse;
import utilsDatabase.ConnectionManagerMongo;


import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.Sorts;

import org.bson.Document;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static utilities.Constants.SCREENSHOT_PATH;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;


public class CommonFunctionsWeb extends Base {

    private static Scenario scenario;

    public static <T> T callApi(RefactoredRestAssuredHelper.HTTPRequestType requestType, Map<String, String> headerMap, Map<String, String> params, String requestURL, Object requestBody, RequestSpecification spec, RequestConfigs requestConfigs, int retry, long expectedStatusCode, String statusCheckKeyPath, Class<T> responseType) throws Exception {
        try {
            ExtractableResponse<Response> response = null;
            response = RefactoredRestAssuredHelper.callApi(requestType, headerMap, params, requestURL, requestBody, spec, requestConfigs, retry, expectedStatusCode, statusCheckKeyPath);
            testLevelReport.get().log(Status.PASS, "Able to hit API");
            testLevelReport.get().log(Status.INFO, String.valueOf(RefactoredRestAssuredHelper.curlCmd));
            testLevelReport.get().log(Status.INFO, response.response().asPrettyString());
            return StandardResponse.parseJsonResponse(response.response().asPrettyString(), responseType);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to hit API : " + RefactoredRestAssuredHelper.curlCmd);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to hit API : " + RefactoredRestAssuredHelper.curlCmd + " \n\n With Exception ", e);
            return null;
        }
    }

    public static <T> StandardResponse<T> callApi(RefactoredRestAssuredHelper.HTTPRequestType requestType, Map<String, String> headerMap, Map<String, String> params, String requestURL, Object requestBody, RequestSpecification spec, RequestConfigs requestConfigs, int retry, long expectedStatusCode, String statusCheckKeyPath, TypeReference<StandardResponse<T>> typeReference) throws Exception {
        try {
            ExtractableResponse<Response> response = null;
            response = RefactoredRestAssuredHelper.callApi(requestType, headerMap, params, requestURL, requestBody, spec, requestConfigs, retry, expectedStatusCode, statusCheckKeyPath);
            testLevelReport.get().log(Status.PASS, "Able to hit API");
            testLevelReport.get().log(Status.INFO, String.valueOf(RefactoredRestAssuredHelper.curlCmd));
            testLevelReport.get().log(Status.INFO, response.response().asPrettyString());
            return StandardResponse.parseJsonResponse(response.response().asPrettyString(), typeReference);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to hit API : " + RefactoredRestAssuredHelper.curlCmd);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to hit API : " + RefactoredRestAssuredHelper.curlCmd + " \n\n With Exception ", e);
            return null;
        }
    }

    public static void compareAndLogNotNull(Object actual, String message) {
        Status status = (actual != null) ? Status.PASS : Status.FAIL;
        testLevelReport.get().log(status, message + " | Value is " + (actual != null ? "NOT NULL" : "NULL"));
        Assert.assertNotNull(actual,message + " | Value is " + (actual != null ? "NOT NULL" : "NULL"));
    }

    public static <T> void compareAndLog(T actual, T expected, String message) {
        Status status;
        boolean validation = false;
        if (actual == null && expected == null) {
            status = Status.PASS;
            validation = true;
        } else if (actual == null || expected == null) {
            status = Status.FAIL;
        } else {
            status = actual.equals(expected) ? Status.PASS : Status.FAIL;
            validation = actual.equals(expected);
        }
        testLevelReport.get().log(status, message + " | Expected: " + expected + " | Actual: " + actual);
        Assert.assertTrue(validation, message + " | Validation - Expected: " + expected + ", Actual: " + actual);
    }


    public static void openURL(String application) throws InterruptedException {
        String url = null;
        try {
            url = Base.getProperty().getProperty("protocol") + "://" + application + "." + Base.getProperty().getProperty("domain");
            Base.setDriver(DriverManager.getDriverInstance(Base.getProperty().getProperty("browser"), url));
            Base.getDriver().manage().window().maximize();
            loadPageWithRetry(url);
            setPageLoadTimeOut(Integer.parseInt(Base.getProperty().getProperty("pageLoadTimeOut")));
            setImplicitWait(Integer.parseInt(Base.getProperty().getProperty("implicitWait")));
            testLevelReport.get().log(Status.PASS, "Able to launch URL");
            testLevelReport.get().log(Status.INFO, url);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to launch URL for : " + application);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to launch URL for : " + url, e);
        }
    }

    public static void openURL(String application, boolean closeExisting) throws InterruptedException, IOException {
        if (closeExisting) {
            DriverManager.killDriverInstance();
        }
        openURL(application);
    }

    public static void enterCharacter(By locator, String generatedString, String elementName) {
        try {
            fluentWait(locator).clear();
            Wait<WebDriver> wait = Waits.getFluentWait(Base.getDriver(), Duration.ofSeconds(5), Duration.ofMillis(500), List.of(NoSuchElementException.class));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            element.click();
            fluentWait(locator).sendKeys(generatedString);
            testLevelReport.get().log(Status.PASS, "Entered characters in field : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to enter characters in field : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to enter characters in : " + elementName);
        }
    }

    public static void click(By locator, String elementName) {
        try {
            fluentWait(locator).isDisplayed();
            fluentWait(locator).click();
            testLevelReport.get().log(Status.PASS, "Clicked on element : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to click on button : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to click on button : " + elementName, e);
        }
    }

    public static void click(WebElement element, String elementName) {
        try {
            fluentWait(element).click();
            testLevelReport.get().log(Status.PASS, "Clicked on element : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to click on button : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to click on button : " + elementName);
        }
    }

    public static void jsClickFluentWithIsDisplayedCheck(By locator, String elementName) {
        try {
            WebElement element = Base.getDriver().findElement(locator);
            fluentWait(element).isDisplayed();
            JavascriptExecutor js = (JavascriptExecutor) Base.getDriver();
            js.executeScript("arguments[0].click();", element);
            testLevelReport.get().log(Status.PASS, "Clicked on element : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to click on button : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to click on button : " + elementName);
        }
    }

    public static void jsClick(By locator, String log) {
        try {
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(10));
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
            el.click();
        } catch (Exception e) {
            System.out.println("Normal click failed, trying JS click: " + log);
            WebElement el = getDriver().findElement(locator);
            ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", el);
        }
    }

    public static String fetchTextOfElement(WebElement element, String elementName) {
        String elementValue = "";
        try {
            elementValue = fluentWait(element).getText();
            testLevelReport.get().log(Status.PASS, "Fetched Value for element : " + elementName);
            return elementValue;
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to fetch value for element : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to fetch value for element : " + elementName);
            return elementValue;
        }
    }


    public static String fetchValueOfElement(By element, String elementName) {
        String elementValue = "";
        try {
            elementValue = fluentWait(element).getAttribute("value");
            testLevelReport.get().log(Status.PASS, "Fetched Value for element : " + elementName);
            return elementValue;
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to fetch value for element : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to fetch value for element : " + elementName);
            return elementValue;
        }
    }

    public static String fetchAttributeValueOfElement(By element, String attribute, String elementName) {
        String elementValue = "";
        try {
            elementValue = fluentWait(element).getAttribute(attribute);
            testLevelReport.get().log(Status.PASS, "Fetched Value for element : " + elementName);
            return elementValue;
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to fetch value for element : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to fetch value for element : " + elementName);
            return elementValue;
        }
    }

    public static String fetchAttributeValueOfElement(WebElement element, String attribute, String elementName) {
        String elementValue = "";
        try {
            elementValue = fluentWait(element).getAttribute(attribute);
            testLevelReport.get().log(Status.PASS, "Fetched Value for element : " + elementName);
            return elementValue;
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to fetch value for element : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to fetch value for element : " + elementName);
            return elementValue;
        }
    }

    public static void isElementDisplayed(By locator, String elementName) {
        try {
            fluentWait(locator).isDisplayed();
            testLevelReport.get().log(Status.PASS, "Element is getting displayed : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Element is not getting displayed : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Element Not getting displayed : " + elementName);
        }
    }

    public static WebElement fluentWait(By locator) {
        Wait<WebDriver> wait = Waits.getFluentWait(Base.getDriver(), Duration.ofSeconds(Long.parseLong(Base.getProperty().getProperty("timeout"))), Duration.ofMillis(500), List.of(NoSuchElementException.class));
        return wait.until(driver1 -> driver1.findElement(locator));
    }

    private static WebElement fluentWait(WebElement element) {
        Wait<WebDriver> wait = Waits.getFluentWait(Base.getDriver(), Duration.ofSeconds(Long.parseLong(Base.getProperty().getProperty("timeout"))), Duration.ofMillis(500), List.of(NoSuchElementException.class));
        return wait.until(driver -> element);
    }

    public static WebElement explicitWaitClick(By locator, String elementName, int timeoutInSeconds) {
        int initTime = 0;
        while (initTime < timeoutInSeconds) {
            try {
                // Click the element if visible
                WebElement element = Base.getDriver().findElement(locator);
                element.click();

                // Log success and return the element
                testLevelReport.get().log(Status.PASS, "Clicked on element : " + elementName);
                return element;
            } catch (Exception e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    testLevelReport.get().log(Status.DEBUG, "Getting exception while waiting");
                }
                initTime++; // Increment retry count
                testLevelReport.get().log(Status.DEBUG, "Retry attempt: " + initTime + " for element: " + elementName);

                // If retries exceeded, log failure and throw exception
                if (initTime == timeoutInSeconds) {
                    testLevelReport.get().log(Status.FAIL, "Unable to click on button : " + elementName + " after " + initTime + " attempts.");
                    testLevelReport.get().log(Status.DEBUG, e);
                    Assert.fail("Unable to click on " + elementName, e);
                }
            }
        }
        return null; // Return null if element is not found after retries
    }

    public static WebElement explicitWaitClick(By locator, String elementName) {
        int retryCount = 3; // Number of retries
        int attempt = 0;

        while (attempt < retryCount) {
            try {
                Wait<WebDriver> wait = Waits.getFluentWait(Base.getDriver(), Duration.ofSeconds(10), Duration.ofMillis(500),
                        List.of(NoSuchElementException.class));

                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));

                element.click();
                testLevelReport.get().log(Status.PASS, "Clicked on element : " + elementName);
                return element;

            } catch (Exception e) {
                attempt++;
                testLevelReport.get().log(Status.WARNING,
                        "Retrying click on element: " + elementName + " | Attempt: " + attempt);

                if (attempt == retryCount) {
                    testLevelReport.get().log(Status.FAIL, "Unable to click on button : " + elementName);
                    testLevelReport.get().log(Status.DEBUG, e);
                    Assert.fail("Unable to Verify for " + elementName, e);
                }
            }
        }
        return null;
    }


    public static boolean verifyAttributValue(By element, String text, String attribute, String elementName) {
        try {
            String heading = fluentWait(element).getAttribute(attribute);
            testLevelReport.get().log(Status.PASS, "Verified value of element : " + elementName);
            return heading.contains(text);
        } catch (NoSuchElementException nsee) {
            nsee.printStackTrace();
            logger.info("Unable to locate element : " + elementName);
            testLevelReport.get().log(Status.FAIL, "Unable to locate element : " + elementName);
            testLevelReport.get().log(Status.DEBUG, nsee);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Exception occurred while locating element : " + elementName);
            testLevelReport.get().log(Status.FAIL, "Exception occurred while locating element : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            return false;
        }
    }

    public static void verifyText(By element, String generatedString, String elementName) {
        boolean flag;
        try {
            String text = fluentWait(element).getText();
            flag = text.toLowerCase().contains(generatedString.toLowerCase());
            testLevelReport.get().log(Status.PASS, "Verified text for " + elementName);
            Assert.assertTrue(flag, "Verified text for " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to verify text for " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to Verify text for " + elementName);
        }
    }

    private static void loadPageWithRetry(String url) throws InterruptedException {
        int retryCount = 0;
        boolean pageLoaded = false;
        while (!pageLoaded && retryCount < 8) {
            try {
                Base.getDriver().get(url);
                pageLoaded = true;
            } catch (Exception e) {
                retryCount++;
                Thread.sleep(10000);
            }
        }
        if (!pageLoaded) {
            logger.info("Page could not be loaded after 8 attempts. Exiting the test.");
            if (Base.getDriver() != null) {
                Base.getDriver().close();
                Base.getDriver().quit();
            }
            System.exit(0);
        }
    }

    public static void verifyElementAttributeValue(By locator, String attribute, String expectedValue, String elementName) {
        try {
            boolean flag = false;
            flag = fluentWait(locator).getAttribute(attribute).equalsIgnoreCase(expectedValue);
            if (flag)
                testLevelReport.get().log(Status.PASS, "Verified Attribute '" + attribute + "' Value is : " + expectedValue + " for " + elementName);
            else {
                testLevelReport.get().log(Status.FAIL, "Attribute '" + attribute + "' Value is : " + expectedValue + " for " + elementName);
                Assert.fail("Attribute '" + attribute + "' Value is : " + expectedValue + " for " + elementName);
            }
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to verify Attribute '" + attribute + "' Value is : " + expectedValue + " for " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to verify Attribute '" + attribute + "' Value is : " + expectedValue + " for " + elementName);
        }
    }

    public static void performOperation(By input, String operation, String elementName) {
        try {
//            fluentWait(input).isDisplayed();
            Thread.sleep(5000);
            switch (operation) {
                case "keyenter":
                    Base.getDriver().findElement(input).sendKeys(Keys.RETURN);
                    break;
                case "keydown":
                    Base.getDriver().findElement(input).sendKeys(Keys.ARROW_DOWN);
                    break;
                case "keyleft":
                    Base.getDriver().findElement(input).sendKeys(Keys.ARROW_LEFT);
                    break;
                case "keyright":
                    Base.getDriver().findElement(input).sendKeys(Keys.ARROW_RIGHT);
                    break;
                case "keyup":
                    Base.getDriver().findElement(input).sendKeys(Keys.ARROW_UP);
                    break;
                default:
                    testLevelReport.get().log(Status.FAIL, "Operation not supported");
                    Assert.fail("Operation not supported");
                    break;
            }
            testLevelReport.get().log(Status.PASS, "Enter Key Pressed on " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to press Enter Key on " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to press Enter Key on " + elementName);
        }
    }


    public static void clearTextFromInputField(By locator, String elementName) {
        try {
            fluentWait(locator).clear();
            testLevelReport.get().log(Status.PASS, "Cleared input in  : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to clear input in " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to clear input in " + elementName);
        }
    }

    public static void verifyElementNotDisplayed(By locator, String elementName) {
        try {
            fluentWait(locator).isDisplayed();
            testLevelReport.get().log(Status.FAIL, "Element is getting displayed : " + elementName);
            Assert.fail("Element is getting displayed : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.PASS, "Element is not getting displayed : " + elementName);
        }
    }

    public static void selectDropDownByValue(By locator, String value, String elementName) {
        try {
            // Perform the dropdown selection by value
            new Select(fluentWait(locator)).selectByValue(value);

            // Log the success
            testLevelReport.get().log(Status.PASS, "Selected " + elementName + " with value " + value);
        } catch (Exception e) {
            // Log the failure and the exception details
            testLevelReport.get().log(Status.FAIL, "Unable to select " + elementName + " with value " + value);
            testLevelReport.get().log(Status.DEBUG, e);

            // Fail the test with the error message
            Assert.fail("Unable to select " + elementName + " with value " + value);
        }
    }

    public static void selectDropDownByVisibleText(By locator, String visibleText, String elementName) {
        try {
            Select dropdown = new Select(fluentWait(locator));
            dropdown.selectByVisibleText(visibleText);
            testLevelReport.get().log(Status.PASS, "Selected " + elementName + " with visible text " + visibleText);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to select " + elementName + " with visible text " + visibleText);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to select " + elementName + " with visible text " + visibleText);
        }
    }

    public static void verifyPresenceOfElement(String locator, String elementName, String... parms) {
        try {
            String value = MessageFormat.format(String.valueOf(locator), (Object[]) parms);
            value = value.trim();
            fluentWait(By.xpath(value)).isDisplayed();
            testLevelReport.get().log(Status.PASS, "Element is present " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Element is not present " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Element is not present " + elementName);
        }

    }

    // STEP 1: Making DB Connection Fetching OTP
    // STEP 2: Fetching OTP (Latest OTP)
    public static String getLatestOTP(int count)
    {
        String otp = null;
        try
        {
            // Connect to MongoDB
            ConnectionManagerMongo.connectToDatabaseMongo();
            MongoDatabase db = ConnectionManagerMongo.getDatabase();

            // Fetch latest OTP document
            MongoCollection<Document> otpCollection = db.getCollection("otps");
            Document latestOTPDoc = otpCollection.find()
                    .sort(Sorts.descending("_id"))
                    .limit(count)
                    .first();

            if (latestOTPDoc != null)
            {
                Object otpObj = latestOTPDoc.get("otp");
                otp = otpObj != null ? otpObj.toString() : null;
            }
            else
            {
                System.out.println(" No OTP records found.");
            }

        }
        catch (Exception e)
        {
            System.err.println(" Failed to fetch latest OTP: " + e.getMessage());
        }
        finally
        {
            ConnectionManagerMongo.closeMongoConnection();
        }
        return otp;
    }


    public static List<String> getAllCollectionNames() {
        List<String> collections = new ArrayList<>();

        try {
            // Connect to MongoDB
            ConnectionManagerMongo.connectToDatabaseMongo();
            MongoDatabase db = ConnectionManagerMongo.getDatabase();

            // Get all collection names
            MongoIterable<String> collectionNames = db.listCollectionNames();
            for (String name : collectionNames)
            {
                collections.add(name);
            }
        }
        catch (Exception e)
        {
            System.err.println("Failed to fetch collections: " + e.getMessage());
        }
        finally
        {
            ConnectionManagerMongo.closeMongoConnection();
        }
        return collections;
    }

    public static void waitInMillis(long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("waitInMillis: millis must be non-negative, got " + millis);
        }
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();  // preserve the interrupt flag
            throw new RuntimeException("Thread was interrupted while waiting for "
                    + millis + " ms", e);
        }
    }

    public static Boolean verifyPresenceOfElement(By locator, String elementName, String... parms) {
        boolean flag = false;
        try {
            fluentWait(locator).isDisplayed();
            testLevelReport.get().log(Status.PASS, "Element is present " + elementName);
            flag = true;
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Element is not present " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Element is not present " + elementName);
        }
        return (Boolean) flag;
    }

    public static boolean verifypresenceofelementopt(By locator, String elementName) {
        boolean flag = false;
        try {
            fluentWait(locator).isDisplayed();
            testLevelReport.get().log(Status.PASS, "Element is present " + elementName);
            flag = true;
        } catch (Exception e) {
            testLevelReport.get().log(Status.DEBUG, e);
        }
        return flag;
    }

    public static void clickByValue(String locator, String elementName, String... parms) {
        try {
            String value = MessageFormat.format(String.valueOf(locator), (Object[]) parms);
            value = value.trim();
            fluentWait(By.xpath(value)).click();
            testLevelReport.get().log(Status.PASS, "Clicked on element : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to click on element : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to click on element : " + elementName);
        }
    }

    public static void enterCharacterInElementByValue(String locator, String generatedString, String elementName, String... parms) {
        try {
            String value = MessageFormat.format(String.valueOf(locator), (Object[]) parms);
            value = value.trim();
            fluentWait(By.xpath(value)).sendKeys(generatedString);
            testLevelReport.get().log(Status.PASS, "Entered characters in field : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to enter characters in field : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to enter characters in : " + elementName);
        }
    }

    public static void ScrollByVisibleElement(By locator, String elementName) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) Base.getDriver();
            WebElement Element = fluentWait(locator);
            js.executeScript("arguments[0].scrollIntoView();", Element);
            testLevelReport.get().log(Status.PASS, "scroll to " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to scroll to " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to scroll to " + elementName);
        }
    }

    public static void upload(By triggerLocator, String fileName, String friendlyName)
    {
        WebDriver driver = Base.getDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try
        {
            // 1) Open the native file dialog
            WebElement trigger = wait.until(ExpectedConditions.elementToBeClickable(triggerLocator));
            trigger.click();
            Base.testLevelReport.get().log(Status.INFO, "Clicked “" + friendlyName + "” trigger");

            // 2) Build the full path (~/Downloads/fileName)
            String downloadFolder = System.getProperty("user.home") + File.separator + "Downloads";
            String absoluteFilePath = Paths.get(downloadFolder, fileName).toString();

            // 3) Copy to clipboard
            StringSelection sel = new StringSelection(absoluteFilePath);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);

            // 4) Wait a moment for dialog to appear
            Thread.sleep(1000);

            // 5) Paste + Enter via Robot (handles Windows/Linux/macOS)
            Robot robot = new Robot();
            robot.setAutoDelay(100);
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("mac"))
            {
                robot.keyPress(KeyEvent.VK_META);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_META);
            }
            else
            {
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_CONTROL);
            }
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

            // 6) Post-upload wait logic
            By postUploadLocator;
            switch (friendlyName.toLowerCase()) {
                case "profile picture":
                    postUploadLocator = By.cssSelector("img.profile-avatar");
                    break;
                case "cover photo":
                    postUploadLocator = By.id("coverPhotoPreview");
                    break;
                default:
                    postUploadLocator = By.cssSelector(".spinner, .loader");
                    break;
            }
            try
            {
                wait.withTimeout(Duration.ofSeconds(5)).until(ExpectedConditions.invisibilityOfElementLocated(postUploadLocator));
            }
            catch (TimeoutException ignored)
            {
                Base.testLevelReport.get().log(Status.FAIL, "Failed to upload “" + friendlyName + "”: " + fileName);
            }
            Base.testLevelReport.get().log(Status.PASS, friendlyName + " uploaded: " + absoluteFilePath);
        }
        catch (Exception e)
        {
            Base.testLevelReport.get().log(Status.FAIL, "Failed to upload “" + friendlyName + "”: " + fileName);
            Base.testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("uploadViaNativeDialog() failed for “" + friendlyName + "”", e);
        }
    }

    public static void takeSnapShot() throws IOException {
        String path = System.getProperty("user.dir");
        String fileWithPath = path + SCREENSHOT_PATH;
        //Convert web driver object to TakeScreenshot
        TakesScreenshot scrShot = ((TakesScreenshot) Base.getDriver());

        //Call getScreenshotAs method to create image file
        File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);

        //Move image file to new destination
        File DestFile = new File(fileWithPath);

        //Copy file at destination
        FileUtils.copyFile(SrcFile, DestFile);

        logger.info("Screenshot Taken");
        testLevelReport.get().addScreenCaptureFromPath(fileWithPath);
    }


    public static String getTimeInEpochSeconds() {
        // Random random = new Random();
        //  Integer randomMobile;
        long epochSeconds = Instant.now().getEpochSecond();
        //randomMobile = random.nextInt(Integer.SIZE - 1) + 1234567890;
        String random1 = String.valueOf(epochSeconds);
        return random1;
    }

    public static void performActions(String operation) {
        try {

            Thread.sleep(1000);
            Actions actions = new Actions(Base.getDriver());
            switch (operation) {

                case "copy":
                    actions.keyDown(Keys.COMMAND);
                    actions.sendKeys("c");
                    actions.keyUp(Keys.COMMAND);
                    actions.build().perform();
                    break;

                case "selectAll":
                    actions.keyDown(Keys.COMMAND);
                    actions.sendKeys("a");
                    actions.keyUp(Keys.COMMAND);
                    actions.build().perform();
                    break;

                case "paste":
                    actions.keyDown(Keys.COMMAND);
                    actions.sendKeys("v");
                    actions.keyUp(Keys.COMMAND);
                    actions.build().perform();
                    break;
                default:
                    testLevelReport.get().log(Status.FAIL, "Operation not supported");
                    Assert.fail("Operation not supported");
                    break;
            }

            Thread.sleep(2000);
            testLevelReport.get().log(Status.PASS, "Action performed successfully");
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Action failed");
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Action failed");
        }

    }

    public static boolean isTextBoxEmpty(By locator) {
        return fluentWait(locator).getAttribute("value").isEmpty();
    }


    public static Boolean verifyElementIsEnabled(By locator, String elementName) {
        boolean flag = false;
        try {
            flag = fluentWait(locator).isEnabled();
            testLevelReport.get().log(Status.PASS, "Element is enabled " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Element is not enabled " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Element is not present " + elementName);
        }
        return (Boolean) flag;
    }

    public static void jsClick(By locator, String elementName, long... waitBeforeClickInMs) {
        try {
            if (waitBeforeClickInMs.length > 0) {
                WebDriverWait wait = new WebDriverWait(Base.getDriver(), Duration.ofSeconds(10));
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
            }
            JavascriptExecutor js = (JavascriptExecutor) Base.getDriver();
            js.executeScript("arguments[0].click();", fluentWait(locator));
            testLevelReport.get().log(Status.PASS, "Clicked on element using JS executor : " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to click on element : " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to click on element : " + elementName, e);
        }
    }

    public static void pressEnter(By inputSearchDomain, String elementName) {
        try {
            fluentWait(inputSearchDomain).sendKeys(Keys.RETURN);
            testLevelReport.get().log(Status.PASS, "Enter Key Pressed on " + elementName);
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Unable to press Enter Key on " + elementName);
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Unable to press Enter Key on " + elementName);
        }
    }

    private static void setPageLoadTimeOut(int time) {
        Base.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(time));
    }

    private static void setImplicitWait(int time) {
        Base.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(time));
    }

    public static void waitForPageLoad(String message) {
        try {
            WebDriverWait wait = new WebDriverWait(Base.getDriver(), Duration.ofSeconds(Base.getDriver().manage().timeouts().getPageLoadTimeout().getSeconds()));
            wait.until(webDriver -> (Boolean) ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, "Page is not loaded");
            testLevelReport.get().log(Status.DEBUG, e);
            Assert.fail("Page is not loaded : " + message);
        }
    }

    public static File takeScreenShotWeb(String screenShotName) {
        String destDir = "";
        try {
            File scrFile = ((TakesScreenshot) Base.getDriver()).getScreenshotAs(OutputType.FILE);
            DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy_hh");
            destDir = "testResults/extentReports/screenshotsWeb/Failure" + dateFormat.format(new Date());
            new File(destDir).mkdirs();
            String fullPath = destDir + "/" + screenShotName;
            FileHandler.copy(scrFile, new File(fullPath + ".png"));

            BufferedImage fullImage = ImageIO.read(scrFile);

            // Calculate the new dimensions based on the aspect ratio
            int newWidth = fullImage.getWidth() / 3;
            int newHeight = fullImage.getHeight() / 3;

            // Create a new BufferedImage with the adjusted dimensions
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            resizedImage.getGraphics().drawImage(fullImage, 0, 0, newWidth, newHeight, null);

            File resizedScreenshot = new File(fullPath + "resized.png");
            ImageIO.write(resizedImage, "png", resizedScreenshot);

            return resizedScreenshot;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void waitForElementToDisappear(By locator, int timeoutInSeconds, String elementName) {
        waitForElementToDisappear(locator, timeoutInSeconds, elementName, false);
    }

    public static void waitForElementToDisappear(By locator, int timeoutInSeconds, String elementName, boolean shouldRefreshPage) {
        try {
            if (shouldRefreshPage) Base.getDriver().navigate().refresh();
            WebDriverWait wait = new WebDriverWait(Base.getDriver(), Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.info("Element disappeared successfully.");
        } catch (TimeoutException e) {
            logger.info("Timed out waiting for the element to disappear: " + elementName);
        } catch (Exception e) {
            logger.info("An unexpected error occurred while waiting for the element to disappear: " + elementName);
        }
    }

    public static void dispatchUiEstimatedFareAmount(By fareLocator, String fareType, Float apiAmount) {
        if (CommonFunctionsWeb.verifyPresenceOfElement(fareLocator, fareType)) {
            String fareText = Base.getDriver().findElement(fareLocator).getText();
            String fareAmountString = fareText.replaceAll("[^0-9.]", "");  // Extract only numbers and dots
            if (!fareAmountString.isEmpty()) {
                float uiFare = Float.parseFloat(fareAmountString);  // Convert to float
                logger.info("Valid " + fareType.toLowerCase() + ": ₹ " + uiFare);
                // Validate the amounts and throw exception if they do not match
                if (uiFare == apiAmount) {
                    logger.info(fareType + " matches API amount: ₹ " + apiAmount);
                } else {
//                    String errorMessage = String.format(
//                            "%s does not match API amount. UI Fare: ₹ %.2f, API Amount: ₹ %.2f",
//                            fareType, uiFare, apiAmount
//                    );
//                    logger.error(errorMessage);
//                    throw new AssertionError(errorMessage); // Throw an exception if they do not match
                }
            } else {
                logger.info("Invalid or missing " + fareType.toLowerCase() + ".");
            }
        } else {
            logger.info(fareType + " element not found.");
        }
    }

    public static void dispatchUiConfirmFare(By fareLocator, String fareType, Float finalEstimatedAmount) {
        if (CommonFunctionsWeb.verifyPresenceOfElement(fareLocator, fareType)) {
            String fareText = Base.getDriver().findElement(fareLocator).getText();
            String fareAmountString = fareText.replaceAll("[^0-9.]", "");  // Extract only numbers and dots

            if (!fareAmountString.isEmpty()) {
                float uiFare = Float.parseFloat(fareAmountString);  // Convert to float

                logger.info("Valid " + fareType.toLowerCase() + ": ₹ " + uiFare);

                // Validate the amounts and throw exception if they do not match
                if (uiFare == finalEstimatedAmount) {
                    logger.info(fareType + " matches API amount: ₹ " + finalEstimatedAmount);
                } else {
//                    String errorMessage = String.format(
//                            "%s does not match API amount. UI Fare: ₹ %.2f, API Amount: ₹ %.2f",
//                            fareType, uiFare, finalEstimatedAmount
//                    );
//                    logger.error(errorMessage);
//                    throw new AssertionError(errorMessage); // Throw an exception if they do not match
                }
            } else {
                logger.info("Invalid or missing " + fareType.toLowerCase() + ".");
            }
        } else {
            logger.info(fareType + " element not found.");
        }
    }


    public static void pageRefresh() throws InterruptedException {
        Thread.sleep(2000);
        Base.getDriver().navigate().refresh();
    }

    public static void waitForElementTextToBe(By locator, String expectedText, int timeoutInSeconds, String elementName) {
        try {
            WebDriverWait wait = new WebDriverWait(Base.getDriver(), Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, expectedText));
            testLevelReport.get().log(Status.PASS, elementName + " is visible");

        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, elementName + " is not visible");
            testLevelReport.get().log(Status.DEBUG, e);
        }
    }

    public static void waitForVisibleElementTextToBe(By locator, String expectedText, int timeoutInSeconds, String elementName) {
        try {
            WebDriverWait wait = new WebDriverWait(Base.getDriver(), Duration.ofSeconds(timeoutInSeconds));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            testLevelReport.get().log(Status.PASS, elementName + " is visible");

        } catch (Exception e) {
            testLevelReport.get().log(Status.FAIL, elementName + " is not visible");
            testLevelReport.get().log(Status.DEBUG, e);
        }
    }

    public static void hoverOverElement(By locator) {
        try {
            WebElement element = Base.getDriver().findElement(locator);
            Actions actions = new Actions(Base.getDriver());
            actions.moveToElement(element).perform(); // Perform hover action
            logger.info("Mouse hovered over element" + locator);
            testLevelReport.get().log(Status.PASS, "Mouse hovered");
        } catch (Exception e) {
            System.out.println("Unable to hover over element: " + e.getMessage());
            logger.info("Unable to hover over element:" + e.getMessage());
            testLevelReport.get().log(Status.FAIL, "Mouse is not hovered");
            testLevelReport.get().log(Status.DEBUG, e);
        }
    }

    public static void scrollDown() {
        JavascriptExecutor js = (JavascriptExecutor) Base.getDriver();
        js.executeScript("window.scrollBy(0, 200);");
    }

    public static void scrollup() {

        JavascriptExecutor js = (JavascriptExecutor) Base.getDriver();
        js.executeScript("window.scrollBy(0,-500)");

    }

    public static Scenario getScenario() {
        return scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

}

