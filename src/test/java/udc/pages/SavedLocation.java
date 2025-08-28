package udc.pages;

import org.openqa.selenium.By;
import utilsWeb.CommonFunctionsWeb;

public class SavedLocation extends CommonFunctionsWeb {
    public static By checkboxactive = By.xpath("//div[contains(@class,'w-11') and contains(@class,'bg-udc_blue_100')]");
    public static By checkboxinactive = By.xpath("//div[contains(@class,'w-11') and contains(@class,'bg-udc_grey_100')]");
    public static By checkbox = By.xpath("//input[@type='checkbox' and contains(@class,'checked:bg-udc_blue_800')]");
    public static By showButton = By.xpath("//button[normalize-space()='Show']");
    public static By hideButton = By.xpath("//button[normalize-space()='Hide']");
    public static By Deactivate = By.xpath("//button[normalize-space()='De-activate']");
    public static By Activate = By.xpath("//button[normalize-space()='Activate']");
}
