package udc.pages;

import utilsWeb.CommonFunctionsWeb;
import org.openqa.selenium.By;

public class Profile extends CommonFunctionsWeb {
    public static By ClickRemoveProfilePhoto = By.xpath("//button[normalize-space()='Remove']");
    public static By ClickEditProfilePhoto = By.xpath("//label[normalize-space(text())='Edit']");
    public static By SubmitButton = By.xpath("//button[@class='font-medium py-2 px-4 rounded-lg focus:outline-none focus:shadow-outline text-center flex justify-center items-center w-40 h-12 bg-udc_blue_800 text-white']");
}