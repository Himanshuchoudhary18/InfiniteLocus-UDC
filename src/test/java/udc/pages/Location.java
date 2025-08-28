package udc.pages;

import org.openqa.selenium.By;
import utilsWeb.CommonFunctionsWeb;

public class Location extends CommonFunctionsWeb {
     public static By statusActivebutton = By.xpath("//div[contains(@class, 'cursor-pointer') and contains(@class, 'bg-udc_blue_100')]");
     public static By statusInactivebutton = By.xpath("//div[contains(@class,'bg-udc_grey_100') and contains(@class,'cursor-pointer')]");
     public static By checkbox = By.xpath("//input[@type='checkbox' and contains(@class, 'accent-udc_blue_800')]");
     public static By showButton = By.xpath("//button[normalize-space(text())='Show' and contains(@class,'text-udc_blue_800')]");
     public static By searchButton = By.xpath("//img[@alt='search' and contains(@src, 'adminSearchIcon.png')]");
     public static By seachColumn = By.xpath("//input[@placeholder='Type to search..' and @type='text']");
     public static By crossButton = By.xpath("//img[@alt='cross' and contains(@src, 'crossIcon.png')]");
     public static By citycheckbox = By.xpath("//input[@type='checkbox' and contains(@class, 'checked:bg-udc_blue_800')]");
     public static By activatebutton = By.xpath("//button[contains(@class, 'text-udc_blue_800') and normalize-space(text())='Activate']");
     public static By deactivatebutton = By.xpath("//button[contains(@class, 'text-udc_blue_800') and normalize-space(text())='De-activate']");
     public static By NewCityButton = By.xpath("//a[@href='/en/masterAdmin/city' and contains(., 'New City')]");
}
