package udc.pages;

import org.openqa.selenium.By;
import utilsWeb.CommonFunctionsWeb;

public class LoginPage extends CommonFunctionsWeb {
    public static By visitWebsiteButton = By.xpath("//button[contains(@class, 'bg-blue-600') and contains(., 'Visit Site')]");
    public static By emailButton = By.xpath("//input[@name='email']");
    public static By passwordButton = By.name("password");
    public static By LoginButton = By.xpath("//button[@type='submit' and text()='Login']");
}
