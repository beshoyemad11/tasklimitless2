package base;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import utilities.DriverManagers;

import java.time.Duration;
import java.util.List;


public class PageBase {
    protected static WebDriver driver;

    public PageBase() {
        this.driver = DriverManagers.getDriver();
    }

    private static final Duration timeOut = Duration.ofSeconds(60);

    public static void waitUntilWebElementIsPresent(By element) {
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(element));
            wait.until((ExpectedConditions.presenceOfElementLocated(element)));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        }
    }

    public static void waitUntilWebElementIsNotPresent(By element) {
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(element));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void waitUntilWebElementBrClickable(By element) {
        WebDriverWait wait = new WebDriverWait(driver, timeOut);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static String waitThenGetWebElementAttribute(By element, String Attribute) {
        PageBase.waitUntilWebElementIsPresent(element);
        String AttributeOfElement = "";
        try {
            AttributeOfElement = driver.findElement(element).getAttribute(Attribute);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return AttributeOfElement;
    }

    public static void waitUntilWebElementBeClickableThenClick(By element) {
        PageBase.waitUntilWebElementBrClickable(element);
        try {
            PageBase.waitAndClickOnWebElement(element);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void waitAndClickOnWebElementUsingJavaScript(By element) throws Exception {
        PageBase.waitUntilWebElementIsPresent(element);
        try {
            WebElement ele = driver.findElement(element);
            JavascriptExecutor executor = (JavascriptExecutor) driver;
            executor.executeScript("arguments[0].click();", ele);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not click on webElement: " + element.toString(), e);
        }

    }

    public static void waitAndClickOnWebElement(By element) throws Exception {
        PageBase.waitUntilWebElementBrClickable(element);
        try {
            driver.findElement(element).click();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not click on webElement: " + element.toString(), e);
        }
    }

    public static void ClickOnWebElement(By element) throws Exception {
        try {
            driver.findElement(element).click();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not click on webElement: " + element.toString(), e);
        }
    }

    public static void waitAndEnterTextInWebElement(String text, By element) throws Exception {
        waitUntilWebElementIsPresent(element);
        try {
           // driver.findElement(element).clear();
            driver.findElement(element).sendKeys(text);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not enter text in webElement: " + element.toString(), e);
        }
    }

    public static void waitAndEnterTextInWebElementThenClickEnter(String text, By element) throws Exception {
        PageBase.waitUntilWebElementIsPresent(element);
        try {
            driver.findElement(element).clear();
            driver.findElement(element).sendKeys(text, Keys.ENTER);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not enter text in webElement: " + element.toString(), e);
        }
    }

    public static void waitAndClearTextIbWebElement(By element) throws Exception {
        PageBase.waitUntilWebElementIsPresent(element);
        try {
            driver.findElement(element).clear();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not clear text in webElement: " + element.toString(), e);
        }
    }

    public static String waitAndGetWebElementText(By element) throws Exception {
        PageBase.waitUntilWebElementIsPresent(element);
        String webElementText;
        try {
            webElementText = driver.findElement(element).getText();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not get text from webElement: " + element.toString(), e);
        }
        return webElementText;
    }

    public static boolean isWebElementPresent(By element) {
        PageBase.waitUntilWebElementIsPresent(element);
        boolean found;
        try {
            driver.findElement(element);
            found = true;
        } catch (Exception ex) {
            found = false;
        }
        return found;
    }

    public static void switchToFrameById(String ID) throws Exception {
        try {
            driver.switchTo().frame(ID);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Frame is not found by id: " + ID, e);
        }
    }

    public static void switchToMainWindow() throws Exception {
        try {
            driver.switchTo().defaultContent();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Default Frame is not Found", e);
        }

    }

    public static void waitAndSelectFromDropdown(String value, By element) {
        PageBase.waitUntilWebElementIsPresent(element);
        try {
            Select dropdownElement = new Select(driver.findElement(element));
            dropdownElement.selectByVisibleText(value);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public static void waitAndSelectOptionFromDropdown(String optionText, By element) {
        PageBase.waitUntilWebElementIsPresent(element);
        driver.findElement(element).click();
        By option = By.xpath("//*[contains(text(),'" + optionText + "')]");
        driver.findElement(option).click();
    }

    public static void waitAndHoverOverElement(By element) throws Exception {
        PageBase.waitUntilWebElementIsPresent(element);
        try {
            WebElement webElement = driver.findElement(element);
            Actions actions = new Actions(driver);
            actions.moveToElement(webElement).perform();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not hover on webElement: " + element.toString(), e);
        }
    }

    public static void scrollIntoView(By element) throws Exception {
        PageBase.waitUntilWebElementIsPresent(element);
        try {
            WebElement webElement = driver.findElement(element);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", webElement);

        } catch (Exception e) {
            System.out.println(element);
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not scroll to webElement: " + element.toString(), e);
        }
    }

    public static WebElement retryingFindElement(By by) {
        WebElement element = null;
        int attempts = 0;
        while (attempts < 10) {
            try {
                element = driver.findElement(by);
                break;
            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element encountered, retrying...");
            }
            attempts++;
        }
        return element;
    }

    public static void scrollIntoNavBar(By element, By navbar) throws Exception {
        PageBase.waitUntilWebElementIsPresent(navbar);
        try {
            WebElement webElement = driver.findElement(navbar).findElement(element);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", webElement);

        } catch (Exception e) {
            System.out.println(element);
            e.printStackTrace();
            System.out.println(e.getMessage());
            throw new Exception("Can not scroll to webElement: " + element.toString(), e);
        }
    }

    public static void removeSurveyPopup() {
        By surveyElement = By.id("kampyleInviteContainer");
        List<WebElement> surveyPopup = driver.findElements(surveyElement);
        if (surveyPopup.size() > 0) {
            String popupInjectionScript = "document.getElementById('kampyleInviteContainer').remove()";
            ((JavascriptExecutor) driver).executeScript(popupInjectionScript);
        }
    }

    /// /////////////////////////////////////////////////////////////////////////////
    // click method
//    protected static void ClickButton (WebElement element)
//    {
//        element.click();
//    }
//    // sendkey method
//    protected static void SetElementText (WebElement TextElement , String Value)
//    {
//        TextElement.sendKeys(Value);
//    }
    public static void dragAndDropFunction(By sources, By targets) {

        // Locate the source element to drag
        waitUntilWebElementIsPresent(sources);
        WebElement source = driver.findElement(sources);

        // Locate the target element to drop onto

        WebElement target = driver.findElement(targets);

        // Create Actions instance

        Actions actions = new Actions(driver);

        // Perform drag and drop

        actions.dragAndDrop(source, target).perform();

    }

    // sleep method
    public void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}