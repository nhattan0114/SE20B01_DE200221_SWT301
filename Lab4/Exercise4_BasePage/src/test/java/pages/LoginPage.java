package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage{


    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ===== Locators (private – test không được biết) =====
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton   = By.cssSelector("button[type='submit']");
    private final By successMsg    = By.cssSelector(".flash.success");
    private final By errorMsg      = By.cssSelector(".flash.error");

    // ===== Actions =====
    public void navigate() {
        driver.get("https://the-internet.herokuapp.com/login");
    }

    public void login(String username, String password) {
        type(usernameField,username);
        type(passwordField,password);
        click(loginButton);
    }

    public By getSuccessLocator() { return successMsg; }
    public By getErrorLocator()   { return errorMsg; }

    public String getMessageText(By locator){
        return getText(locator);
    }
}
