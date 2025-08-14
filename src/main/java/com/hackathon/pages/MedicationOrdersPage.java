package com.hackathon.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MedicationOrdersPage {

    WebDriver driver;
    static WebDriverWait wait;

    private By prescribedMedicationsLink = By.cssSelector("a[href='/medication-orders']");
    private By searchInputid = By.xpath("//span[@role='combobox' and contains(@aria-label, 'Search by Patient ID')]");
    private static By searchButton = By.xpath("//button[normalize-space()='Search']");
    private static By resultsTable = By.cssSelector("table tbody tr");
    private static By startDateInput = By.xpath("//input[@placeholder='dd/mm/yyyy']");


    public MedicationOrdersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateToMedicationOrders() {
        wait.until(ExpectedConditions.elementToBeClickable(prescribedMedicationsLink)).click();
    }

    public void searchByPatientId(String patientId) {

        wait.until(ExpectedConditions.visibilityOfElementLocated(searchInputid)).sendKeys(patientId);
        By firstResult = By.cssSelector("li.p-dropdown-item");
        wait.until(ExpectedConditions.elementToBeClickable(firstResult)).click();
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(resultsTable));
    }

    public static void searchByMedicineName(String medicineName) {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@role='combobox' and contains(@aria-label, 'Search by name')]")));
        searchBox.click();
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input.p-dropdown-filter")));
        input.sendKeys(medicineName);
        input.sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(resultsTable));
    }

    public boolean isPatientSearchResultDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(resultsTable)).isDisplayed();
    }



    public boolean isResultDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(resultsTable)).isDisplayed();
    }

    public boolean isPatientdatainTable(String patientId) {
        try {
            // Wait for table to be present and visible
            wait.until(ExpectedConditions.presenceOfElementLocated(resultsTable));
            
            // Find all table rows
            java.util.List<WebElement> tableRows = driver.findElements(resultsTable);

            for (WebElement row : tableRows) {
                if (row.getText().contains(patientId)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void setStartDate(String date) {
        try {
            // Use the calendar-aware method for read-only date fields
            setStartDateWithCalendar(date);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set start date: " + date + ". Error: " + e.getMessage(), e);
        }
    }

    public void setStartDateAlternative(String date) {
        try {
            WebElement startDateElement = wait.until(ExpectedConditions.elementToBeClickable(startDateInput));

            startDateElement.click();
            
            // Use JavaScript to set the value directly
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));", 
                startDateElement, date
            );
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to set start date (alternative method): " + date + ". Error: " + e.getMessage(), e);
        }
    }

    public void setStartDateWithCalendar(String date) {
        try {

            WebElement startDateElement = wait.until(ExpectedConditions.elementToBeClickable(startDateInput));

            startDateElement.click();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            String[] calendarSelectors = {
                "//div[contains(@class,'p-calendar')]",
                "//div[contains(@class,'datepicker')]",
                "//div[contains(@class,'calendar')]",
                "//div[contains(@class,'ui-datepicker')]",
                "//div[contains(@class,'mat-calendar')]",
                "//div[contains(@class,'ant-calendar')]",
                "//div[contains(@class,'bootstrap-datepicker')]"
            };
            
            WebElement calendar = null;
            for (String selector : calendarSelectors) {
                try {
                    calendar = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(selector)));
                    System.out.println("Found calendar with selector: " + selector);
                    break;
                } catch (Exception e) {
                    // Continue to next selector
                }
            }
            
            if (calendar != null) {

                String[] dateParts = date.split("/");
                String day = dateParts[0];
                
                // Try different day locators
                String[] daySelectors = {
                    "//td[contains(@class,'day')]//span[text()='" + day + "']",
                    "//td[contains(@class,'date')]//span[text()='" + day + "']",
                    "//td[text()='" + day + "']",
                    "//span[text()='" + day + "']",
                    "//button[text()='" + day + "']"
                };
                
                boolean dateSelected = false;
                for (String daySelector : daySelectors) {
                    try {
                        WebElement dayElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(daySelector)));
                        dayElement.click();
                        System.out.println("Successfully selected date: " + date + " using selector: " + daySelector);
                        dateSelected = true;
                        break;
                    } catch (Exception e) {
                        // Continue to next selector
                    }
                }
                
                if (!dateSelected) {
                    System.out.println("Could not select date from calendar, falling back to JavaScript method");
                    setStartDateAlternative(date);
                }
            } else {
                System.out.println("No calendar found, using JavaScript method");
                setStartDateAlternative(date);
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to set start date with calendar: " + date + ". Error: " + e.getMessage(), e);
        }
    }

    public String getStartDate() {
        try {
            WebElement startDateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
            return startDateElement.getAttribute("value");
        } catch (Exception e) {
            throw new RuntimeException("Failed to get start date value", e);
        }
    }

    public boolean isStartDateFieldEditable() {
        try {
            WebElement startDateElement = wait.until(ExpectedConditions.visibilityOfElementLocated(startDateInput));
            
            // Check if element is enabled and not read-only
            boolean isEnabled = startDateElement.isEnabled();
            boolean isReadOnly = "true".equals(startDateElement.getAttribute("readonly"));
            boolean isDisabled = "true".equals(startDateElement.getAttribute("disabled"));
            
            System.out.println("Start Date Field Status:");
            System.out.println("  - Enabled: " + isEnabled);
            System.out.println("  - Read-only: " + isReadOnly);
            System.out.println("  - Disabled: " + isDisabled);
            System.out.println("  - Current value: " + startDateElement.getAttribute("value"));
            System.out.println("  - Placeholder: " + startDateElement.getAttribute("placeholder"));
            
            return isEnabled && !isReadOnly && !isDisabled;
        } catch (Exception e) {
            System.out.println("Error checking start date field status: " + e.getMessage());
            return false;
        }
    }

}
