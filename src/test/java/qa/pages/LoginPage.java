package qa.pages;

import org.openqa.selenium.By;
import utilities.Base;
import utilsDatabase.ConnectionManagerMySQL;
import utilsDatabase.ConnectionManagerPostgreSQL;
import utilsWeb.CommonFunctionsWeb;

public class LoginPage extends CommonFunctionsWeb {
    public static By crossButtonOnSurpriseMePopUp = By.xpath("//*[@id=\"radix-:r0:\"]/div[2]/button");
    public static By profileIconOnLoginPage = By.xpath("//img[@alt='Profile Image']");
    public static By loginButtonOnProfileDropDown = By.xpath("//button[@class='w-full text-left px-4 py-2 hover:bg-gray-50']");
    public static By mobilenumber = By.xpath("//input[@placeholder='Mobile Number*']");
    public static By otp = By.xpath("//button[text()='Get OTP']");
    public static By inputotp = By.xpath("//input[@placeholder='OTP*']");
    public static By submitLoginButton = By.xpath("//button[text()='Submit']");
    public static By surpriseMePopTitle = By.xpath("//img[@alt='surprise-me']");

    public static String getOtpFromPostgreSQL(String query, String columnName){
        return ConnectionManagerPostgreSQL.executeSelectQuery(query).get(0).get(columnName);
    }
}
