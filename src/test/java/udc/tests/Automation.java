package udc.tests;

import com.aventstack.extentreports.Status;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import qa.Retry;
import udc.pages.*;
import utilities.Base;
import static utilsWeb.CommonFunctionsWeb.*;
import static utilsWeb.CommonFunctionsWeb.enterCharacter;

@Test(description = "Sign Up and Login Flow for UDC Website")
public class Automation extends Base {
    @DataProvider(name = "LoginCredentials")
    public Object[][] LoginDetails() {
        return new Object[][]{
                {"brampton@ultimatedrivers.ca", "Mehta@12345"}
        };
    }
    @DataProvider(name = "LocationData")
    public Object[][] getLocationData() {
        return new Object[][]{
                {"BRAMPTON", "Shelburne"}
        };
    }
    @DataProvider(name = "NewLocation")
    public Object[][] validateNewLocation ()
    {
        return new Object[][]{
                {"Himanshu Choudhary",
                        "120016",
                        "107, Sector-23, Gurugram",
                        "M1K 2K3",
                        "(766) 996-6400",
                        "1002", "10",
                        "Sample Display Name 1",
                        "https://www.google.com",
                        "sample-client-id-1",
                        "himanshu@1234",
                        "sachinsingh3232",
                        "10",
                        "10",
                        "100",
                        "10",
                        "10"}
        };
    }
    // TC_001: Verify existing franchise/admin sees their data, new franchise/admin sees blank (Loging-in as an admin/franchise, will be able to see all the data)
    @Test(priority = 1, description = "UDC Website OpenURL | Visit Website | TC_001 : Visiting Website URL", retryAnalyzer = Retry.class, alwaysRun = true, groups = "smoke")
    public void openUDCWebsite() {
        try {
              openURL(Base.getProperty().getProperty("application"), true);
              jsClick(LoginPage.visitWebsiteButton, "Clicked On Visit Website Button");
        }
        catch (Exception e)
        {
            testLevelReport.get().log(Status.FAIL, "Test Execution Failed for : " + getClass().getAnnotation(Test.class).description());
        }
    }
    // TC_002: Validating the Login flow completely using correct email and address
    @Test(priority = 2, description = "Validating Login Flow | Login & Sign Up Flow | TC_002 : Validating Email and Password", dataProvider = "LoginCredentials", alwaysRun = true, enabled = true, groups = "smoke")
    public void validateLoginPage(String email, String password) {
        try {
            enterCharacter(LoginPage.emailButton, email, "Email Submitted");
            enterCharacter(LoginPage.passwordButton, password, "Password Submitted");
            jsClick(LoginPage.LoginButton, "Login Button Clicked");
        }
        catch (Exception e)
        {
            testLevelReport.get().log(Status.FAIL, "Test Execution Failed for : " + getClass().getAnnotation(Test.class).description() + e.getMessage());
        }
    }
    // TC_003 : Validating Dashboard Photo Upload
    @Test(priority = 3, description = "Validating My Profile Photo and Signature | Profile flow | TC_003 : Validating My Profile and Signature", alwaysRun = true, enabled = true, groups = "smoke")
    public void validateProfile()
    {
        try
        {
            jsClick(Profile.ClickRemoveProfilePhoto, "Removed Profile Photo");
            upload(Profile.ClickEditProfilePhoto,"FAQs","Profile Picture Uploaded");
            jsClick(Profile.SubmitButton, "Submit clicked with Safe Button");
        }
        catch (Exception e)
        {
            System.out.println("Error : " + e.getMessage());
            testLevelReport.get().log(Status.FAIL, "Test Execution Failed for : " + getClass().getAnnotation(Test.class).description() + e.getMessage());
        }
    }
    // TC_004 : Verify KPIs are displayed correctly for registered user (Login as an admin/franchise, will be able to see all the data) || TC_005 : Verify "View Tasks" link in Pending Tasks || TC_006 : Verify location-wise data is displayed correctly || TC_007 : Validating the data on the basis of Calendar Date || TC_008 : Validating View Tasks button and data displayed in it is according to the calendar filter || TC_009 : Verify on refresh all filters must get removed
    @Test(priority = 4, description = "Verifying Dashboard Functionality | Dashboard flow | TC_004 : Verifying Location Filter on Dashboard || TC_005 : Verifying Calendar Filter on Dashboard | TC_006 : Verifying View Tasks link in Pending Tasks | TC_007 : Verify View All Locations Checkbox Button functionality | TC_008 : Validating Side Bar Options | TC_0010 : Verifying the month in calendar filter", alwaysRun = true, enabled = true, groups = "smoke")
    public void validateDashboard()
    {
        try
        {
            jsClick(Dashboard.AllLocationFilter, "Clicked to change the location");
            waitInMillis(1000);
            enterCharacter(Dashboard.SearchLocation, "BRAMPTON", "Search results in Location Search Column");
            jsClick(Dashboard.checkboxSelection, "Location checkbox selected");
            waitInMillis(1000);
            jsClick(Dashboard.selectLocation, "Select Locations Button Clicked");
            jsClick(Dashboard.calendarFilterButton, "Calendar filter clicked");
            waitInMillis(1000);
            jsClick(Dashboard.CalendarYearButton, "Calendar Year Button clicked");
            jsClick(Dashboard.YearAppliedButton, "Year Button Clicked");

            pageRefresh();

            waitInMillis(2000);
            jsClick(Dashboard.calendarFilterButton, "Calendar filter clicked");
            waitInMillis(1000);
            jsClick(Dashboard.CalendarYearButton, "Calendar Year Button clicked");
            jsClick(Dashboard.YearAppliedButton, "Year Button clicked");
            jsClick(Dashboard.ViewTasksButton, "ViewTaskButton clicked");

            ScrollByVisibleElement(Dashboard.AllLocationFilter, "Scrolled Up to All locations filter option");
            jsClick(Dashboard.ViewLocation, "View Location Button clicked");
            waitInMillis(2000);
            jsClick(Dashboard.ViewLocationDateFilter, "View Location Date Filter Button clicked");
            waitInMillis(1000);
            jsClick(Dashboard.ViewLocationYearButton, "View Location Year Button clicked");
            waitInMillis(1000);
            jsClick(Dashboard.ViewLocationSelectYearButton, "View Location Year Selected Button clicked");
            waitInMillis(2000);
            jsClick(Dashboard.SideBarOpenButton, "SideBar open option is clicked");
            waitInMillis(1000);
            jsClick(Dashboard.ManageLocationButton, "Manage Location Button clicked");
            waitInMillis(1000);
            jsClick(Dashboard.SideBarCloseButton, "SideBar close option is clicked");
        }
        catch (Exception e)
        {
            testLevelReport.get().log(Status.FAIL, "Test Execution Failed for : " + getClass().getAnnotation(Test.class).description() + e.getMessage());
        }
    }
    // TC_010 : Verify course enrollment numbers (on static data) || TC_011 : Managing filters of Manage Cities & Locations || TC_014 : Verify that clicking New City opens the New City || TC_015 : Verifying the search button functionality
    @Test(priority = 5, dataProvider = "LocationData", description = "Verifying functionality of Master Location | Manage Cities & Locations | TC_008 : Managing filters of Manage Cities & Locations | TC_009 : Verify that clicking New City opens the New City | TC_0010 : Verifying the search button functionality | TC_0011 : Validating the +New Location Button", alwaysRun = true, enabled = true, groups = "smoke")
    public static void masterLocation (String getLocation, String getLocation2)
    {
        try
        {
            waitInMillis(2000);
            if(verifyElementIsEnabled(Location.statusActivebutton, "Status Button is activated"))
            {
                jsClick(Location.statusActivebutton, "Status Button is clicked");
            }
            else
            {
                jsClick(Location.statusInactivebutton, "Status inactive button is clicked");
            }
            waitInMillis(1000);
            jsClick(Location.checkbox, "Checkbox Button is clicked");
            waitInMillis(1000);
            jsClick(Location.showButton, "Show Button is clicked");
            waitInMillis(1000);
            jsClick(Location.searchButton, "Search Button is clicked");
            waitInMillis(1000);
            enterCharacter(Location.seachColumn, getLocation, "Entered character in Search Column");
            waitInMillis(1000);
            jsClick(Location.crossButton, "Cross Button is clicked");
            waitInMillis(1000);
            jsClick(Location.searchButton, "Search Button is clicked Again");
            enterCharacter(Location.seachColumn, getLocation2, "Entered character in Search Column");
            waitInMillis(1000);
            jsClick(Location.citycheckbox, "Checkbox button clicked");
            waitInMillis(1000);
            if(verifyElementIsEnabled(Location.activatebutton, "activate button is present and enabled already"))
            {
                  jsClick(Location.activatebutton, "activate button clicked");
            }
            else
            {
                jsClick(Location.deactivatebutton, "deactivate button clicked");
            }
            jsClick(Location.NewCityButton, "+ New City button clicked");
        }
        catch (Exception e)
        {
            testLevelReport.get().log(Status.FAIL, "Test Execution Failed for : " + Automation.class.getAnnotation(Test.class).description() + e.getMessage());
        }
    }
    // TC_016 : Validating +NewLocation Button || TC_017 : Validating entire New Location Form without saving the Location (must be giving error, Negative scenario) || TC_018 : Validating that on clicking on "Create Button" it should not save the New Location
    @Test(priority = 6, dataProvider = "NewLocation", description = "HLS_TC_0012 : Validating the +New Location Form without saving the Location | HLS_TC_0013 : Validating Location Name, Location Code, Address Column, Postal Code, Phone Number, HST Number, HST Percentage, Website Name, Expiry Date Column, ExpiryDateNextMonth, clientIdButton, client Password, Client URL, Royality, AdvertisingFund, PhoneServices, PhoneServices, RoyalityInFilter, AdvertisingFundInFilter without saving the Location", alwaysRun = true, groups = "smoke")
    public static void newLocationTestNegativeScenario (String LocationName, String LocationCode, String AddressColumn, String PostalCode, String PhoneNumber, String HSTNumber, String HSTPercentage, String WebsiteName, String GoogleURLColumn, String clientID, String PaypalPassword, String FranchiseOwnerUsername, String Royality, String AdvertisingFund, String PhoneServices, String RoyalityInFilter, String AdvertisingFundInFilter)
    {
        try
        {
            jsClick(NewLocation.NewLocationButton, "New Location button clicked");
            enterCharacter(NewLocation.LocationName, LocationName, "Location Name is added");
            enterCharacter(NewLocation.LocationCode, LocationCode , "Location Code Filled");
            enterCharacter(NewLocation.AddressColumn, AddressColumn, "Adress Filled");
            enterCharacter(NewLocation.PostalCode, PostalCode, "Postal Code added");
            enterCharacter(NewLocation.PhoneNumber, PhoneNumber, "Phone Number added");
            enterCharacter(NewLocation.HSTNumber, HSTNumber, "HST Number added");
            enterCharacter(NewLocation.HSTPercentage, HSTPercentage, "HST Percentage added");
            enterCharacter(NewLocation.WebsiteName, WebsiteName, "Website name is displayed");
            jsClick(NewLocation.ExpiryDateColmumn,"Date Display Name shown");
            waitInMillis(1000);
            jsClick(NewLocation.ExpiryDateNextMonth, "Expiry Date Next Month Clicked");
            waitInMillis(1000);
            jsClick(NewLocation.ExpiryDateDateSelection, "Expiry Date Selection");
            enterCharacter(NewLocation.GoogleURLColumn, GoogleURLColumn,"Google URL provided");
            enterCharacter(NewLocation.clientIdButton, clientID, "Client ID is added");
            enterCharacter(NewLocation.PaypalPassword, PaypalPassword, "Password is added");
            jsClick(NewLocation.CreateButton, "Create Button is clicked");
            waitInMillis(2000);
        }
        catch (Exception e)
        {
            testLevelReport.get().log(Status.FAIL, "Test Execution Failed for : " + Automation.class.getAnnotation(Test.class).description() + e.getMessage());
        }
    }
    // TC_021 : Validate that "New Location" should get created as the new location is already saved.
    @Test(priority = 6, dataProvider = "NewLocation", description = "HLS_TC_0014 : Validating the +New Location Form with Location Saved || HLS_TC_0013 : Validating Location Name, Location Code, Address Column, Postal Code, Phone Number, HST Number, HST Percentage, Website Name, Expiry Date Column, ExpiryDateNextMonth, clientIdButton, client Password, Client URL, Royality, AdvertisingFund, PhoneServices, PhoneServices, RoyalityInFilter, AdvertisingFundInFilter with Location getting saved", alwaysRun = true, groups = "smoke")
    public static void newLocationTestPositiveScenario (String LocationName, String LocationCode, String AddressColumn, String PostalCode, String PhoneNumber, String HSTNumber, String HSTPercentage, String WebsiteName, String GoogleURLColumn, String clientID, String PaypalPassword, String FranchiseOwnerUsername, String Royality, String AdvertisingFund, String PhoneServices, String RoyalityInFilter, String AdvertisingFundInFilter)
    {
        try
        {
            enterCharacter(NewLocation.cityName, "BRAMPTON","City Name is added");
            jsClick(NewLocation.SaveButton, "Save button is clicked");
            jsClick(NewLocation.NewLocationButton, "New Location button clicked");
            enterCharacter(NewLocation.LocationName, LocationName, "Location Name is added");
            enterCharacter(NewLocation.LocationCode, LocationCode , "Location Code Filled");
            enterCharacter(NewLocation.AddressColumn, AddressColumn, "Adress Filled");
            enterCharacter(NewLocation.PostalCode, PostalCode, "Postal Code added");
            enterCharacter(NewLocation.PhoneNumber, PhoneNumber, "Phone Number added");
            enterCharacter(NewLocation.HSTNumber, HSTNumber, "HST Number added");
            enterCharacter(NewLocation.HSTPercentage, HSTPercentage, "HST Percentage added");
            enterCharacter(NewLocation.WebsiteName, WebsiteName, "Website name is displayed");
            jsClick(NewLocation.ExpiryDateColmumn,"Date Display Name shown");
            waitInMillis(1000);
            jsClick(NewLocation.ExpiryDateNextMonth, "Expiry Date Next Month Clicked");
            waitInMillis(1000);
            jsClick(NewLocation.ExpiryDateDateSelection, "Expiry Date Selection");
            enterCharacter(NewLocation.GoogleURLColumn, GoogleURLColumn,"Google URL provided");
            enterCharacter(NewLocation.clientIdButton, clientID, "Client ID is added");
            enterCharacter(NewLocation.PaypalPassword, PaypalPassword, "Password is added");
            jsClick(NewLocation.CreateButton, "Create Button is clicked");
            waitInMillis(2000);

            // ScrollByVisibleElement(NewLocation.FranchiseOwner, "Scrolled to Franchise Owner Email address");
            // enterCharacter(NewLocation.FranchiseOwner, "Sachin", "Franchise Owner is added");
            // jsClick(NewLocation.NewFeeButton, "New Fee Button is clicked");
            // waitInMillis(1000);
            // jsClick(NewLocation.CalendarFilter, "Calendar Filter clicked");
            // jsClick(NewLocation.CalendarFilterDateSelected, "Calendar Date from filter is selected");
            // enterCharacter(NewLocation.Royality, "10", "Royality Percentage is added");
            // enterCharacter(NewLocation.AdvertisingFund, "10", "Advertising Fund is added");
            // enterCharacter(NewLocation.PhoneServices, "10", "Phone Services is added");
            // waitInMillis(1000);
            // if (verifyElementIsEnabled(NewLocation.DeleteButton, "Delete Button is present"))
            // {
            //     jsClick(NewLocation.DeleteButton, "Delete Button clicked");
            // }
            // else
            // {
            //     System.out.println("Delete Button not present");
            // }
            // waitInMillis(1000);
            // jsClick(NewLocation.AddButton, "Add Button is clicked");
            // waitInMillis(1000);
            // ScrollByVisibleElement(NewLocation.CreateButton, "Scrolled up to Create Button");
            // waitInMillis(1000);
            // jsClick(NewLocation.CreateButton, "Submit Button is clicked");
        }
        catch (Exception e)
        {
            testLevelReport.get().log(Status.FAIL, "Test Execution Failed for : " + Automation.class.getAnnotation(Test.class).description() + e.getMessage());
        }
    }

    // TC_022 : Validating the Created New Location Functionality || TC_023 : Validating all scenarios related to the New Location created (Hide, Show, Activate and De-activate) || TC_024 : Validating the checkbox on the basis of checkbox visibility (1) If New Location is being shown then Hide button should be visible (2) If Location is Hidden then Show Button must be visible (3) If Location Status is Active then "De-activate" button should be visible and (4) If Location status is De-activated then "Active" button must be visible [All scenarios have been handled]
    @Test(priority = 7, description = "HLS_TC_0014 : Validating the Created New Location Functionality | HLS_TC_0015 : Validating all scenarios related to the New Location created (Hide, Show, Activate and De-activate)", alwaysRun = true, groups = "smoke")
    public static void savedLocation ()
    {
        try
        {
            // First Scenario
            if(verifyElementIsEnabled(SavedLocation.checkboxactive, "Checkbox is active"))
            {
                jsClick(SavedLocation.checkboxactive, "Checkbox is activated");
            }
            else
            {
                jsClick(SavedLocation.checkboxinactive, "Checkbox is inactive");
            }
            waitInMillis(1000);
            // Second Scenario
            jsClick(SavedLocation.checkbox, "Checkbox is marked");
            waitInMillis(1000);
            if (verifyElementIsEnabled(SavedLocation.showButton, "Show Button is visible"))
            {
                jsClick(SavedLocation.showButton, "Show Button is clicked");
            }
            else
            {
                jsClick(SavedLocation.hideButton, "Hide Button is clicked");
            }
            waitInMillis(1000);
            // Third Scenario
            jsClick(SavedLocation.checkbox, "Checkbox is marked");
            waitInMillis(1000);
            if (verifyElementIsEnabled(SavedLocation.Activate, "Activate button is visible"))
            {
                jsClick(SavedLocation.Activate, "Activate Button is clicked");
            }
            else
            {
                jsClick(SavedLocation.Deactivate, "Deactivate Button is clicked");
            }
        }
        catch (Exception e)
        {
            testLevelReport.get().log(Status.FAIL, "Test Execution Failed for : " + Automation.class.getAnnotation(Test.class).description() + e.getMessage());
        }
    }
}
