package udc.pages;

import utilsWeb.CommonFunctionsWeb;
import org.openqa.selenium.By;

public class Dashboard extends CommonFunctionsWeb {
    public static By AllLocationFilter = By.xpath("//button[normalize-space()='All Locations']");
    public static By SearchLocation = By.xpath("//input[@placeholder='Search for a location']");
    public static By checkboxSelection = By.xpath("//input[@type='checkbox' and contains(@class, 'cursor-pointer')]");
    public static By selectLocation = By.xpath("//button[text()='Select 64 Locations' and contains(@class, 'bg-udc_blue_800')]");
    public static By calendarFilterButton = By.xpath("//button[starts-with(normalize-space(text()), 'Aug') or contains(text(), ', 2025')]");
    public static By CalendarYearButton = By.xpath("//button[contains(@class, 'rounded-lg') and text()='Year']");
    public static By YearAppliedButton = By.xpath("//button[normalize-space(text())='2025']");
    public static By ViewTasksButton = By.xpath("//button[normalize-space(text())='View Tasks']");
    public static By ViewLocation = By.xpath("//button[.//h1[normalize-space(text())='View Location']]");
    public static By ViewLocationDateFilter = By.xpath("//button[@type='button' and contains(@class, 'rounded-full') and contains(., 'Today')]");
    public static By ViewLocationYearButton = By.xpath("//button[normalize-space(text())='Year']");
    public static By ViewLocationSelectYearButton = By.xpath("//button[normalize-space(text())='2025']");
    public static By SideBarOpenButton = By.xpath("//button[.//img[@alt='Open']]");
    public static By ManageLocationButton = By.xpath("//a[contains(@href, 'masterAdmin') and .//span[contains(text(), 'Manage')]]");
    public static By SideBarCloseButton = By.xpath("//button[img[@alt='Close']]");
}
