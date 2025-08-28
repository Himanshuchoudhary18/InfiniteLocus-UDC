package udc.pages;

import org.openqa.selenium.By;
import utilsWeb.CommonFunctionsWeb;

public class NewLocation extends CommonFunctionsWeb {
    public static By cityName = By.xpath("//input[@placeholder='Enter city name']");
    public static By SaveButton = By.xpath("//button[normalize-space()='Save']");
    public static By NewLocationButton = By.xpath("//a[.//p[normalize-space()='New Location']]");
    public static By LocationName = By.xpath("//input[@name='name']");
    public static By LocationCode = By.xpath("//input[@name='location_code']");
    public static By AddressColumn = By.xpath("//input[@name='address']");
    public static By PostalCode = By.xpath("//input[@name='postal_code']");
    public static By PhoneNumber = By.xpath("//input[@name='mobile_number']");
    public static By HSTNumber = By.xpath("//input[@name='hst_number']");
    public static By HSTPercentage = By.xpath("//input[@name='hst_percentage']");
    public static By WebsiteName = By.xpath("//input[@name='website_display_name']");
    public static By ExpiryDateColmumn = By.xpath("//button[@aria-haspopup='menu' and @type='button'][1]");
    public static By ExpiryDateNextMonth = By.xpath("//button[@aria-label='Go to the Next Month']");
    public static By ExpiryDateDateSelection = By.xpath("//button[@aria-label='Thursday, September 18th, 2025' and text()='18']");
    public static By GoogleURLColumn = By.xpath("//input[@name='google_review_url']");
    public static By clientIdButton = By.xpath("//input[@name='client_id']");
    public static By PaypalPassword = By.xpath("//input[@name='client_secret']");


    // public static By FranchiseOwner = By.xpath("//button[contains(text(), 'Search for user email')]");
    // public static By NewFeeButton = By.xpath("//button[normalize-space(.)='New Fee']");
    // public static By CalendarFilter = By.xpath("(//button[normalize-space()='2025-08-13'])[2]");
    // public static By CalendarFilterDateSelected = By.xpath("(//button[contains(@aria-label,'August 15th, 2025')])[2]");
    // public static By Royality = By.xpath("(//input[@name='location_fees.royalty'])[2]");
    // public static By AdvertisingFund = By.xpath("(//input[@name='location_fees.advertising_fund'])[2]");
    // public static By PhoneServices = By.xpath("(//input[@name='location_fees.phone_services'])[2]");
    // public static By DeleteButton = By.xpath("//button[@type='button' and text()='Delete']");
    // public static By AddButton = By.xpath("//button[normalize-space()='Add']");

    public static By CreateButton = By.xpath("//button[normalize-space()='Create']");
}
