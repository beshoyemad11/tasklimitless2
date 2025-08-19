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
    private static By startDateInput = By.cssSelector("input[role='combobox'][aria-haspopup='dialog'][placeholder='dd/mm/yyyy']");
    private static By endDateInput = By.cssSelector("input[role='combobox'][aria-haspopup='dialog'][placeholder='dd/mm/yyyy'][aria-controls*='pn_id_']");


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
        WebElement dropdownTrigger = wait.until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[@role='combobox' and contains(@aria-label, 'Search by name')]")
                )
        );
        dropdownTrigger.click();

        // Filter input inside the dropdown panel
        WebElement input = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input.p-dropdown-filter")
                )
        );
        input.clear();
        input.sendKeys(medicineName);

        // Prefer clicking the exact matching option; fall back to first option via keyboard
        By optionLocator = By.xpath(
                "//li[@role='option' and not(@aria-disabled='true')][contains(., '" + medicineName + "')]"
        );
        try {
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionLocator));
            option.click();
        } catch (Exception e) {
            // Fallback: select first visible option using keyboard
            try {
                input.sendKeys(Keys.ARROW_DOWN);
                input.sendKeys(Keys.ENTER);
            } catch (Exception ignored) {
                throw e;
            }
        }

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
            // Ensure results table is present first
            wait.until(ExpectedConditions.presenceOfElementLocated(resultsTable));

            // Wait up to 5 seconds for any row containing the phone to appear
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            By rowWithPhone = By.xpath("//table//tbody//tr[contains(normalize-space(.), '" + patientId + "')]");
            shortWait.until(ExpectedConditions.presenceOfElementLocated(rowWithPhone));

            // Verify presence
            return !driver.findElements(rowWithPhone).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void setStartDate(String date) {
        try {
            // Prefer PrimeNG-specific picker handling
            setStartDateWithPrimeNg(date);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set start date: " + date + ". Error: " + e.getMessage(), e);
        }
    }

    public void setStartDateAlternative(String date) {
        try {
            WebElement startDateElement = wait.until(ExpectedConditions.elementToBeClickable(startDateInput));
            
            // Check if datepicker is already open and close it first
            try {
                WebElement existingPanel = driver.findElement(By.cssSelector("div.p-datepicker[role='dialog']"));
                if (existingPanel.isDisplayed()) {
                    // Close the panel by pressing Escape
                    startDateElement.sendKeys(Keys.ESCAPE);
                    Thread.sleep(500);
                }
            } catch (Exception ignored) {}

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

    private void setStartDateWithPrimeNg(String date) {
        String[] parts = date.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Date must be in dd/MM/yyyy format: " + date);
        }
        String day = parts[0];
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        // Check if datepicker is already open
        boolean isAlreadyOpen = false;
        try {
            WebElement existingPanel = driver.findElement(By.cssSelector("div.p-datepicker[role='dialog']"));
            isAlreadyOpen = existingPanel.isDisplayed();
        } catch (Exception ignored) {}

        // Open the datepicker panel if not already open
        WebElement startDateElement = wait.until(ExpectedConditions.elementToBeClickable(startDateInput));
        if (!isAlreadyOpen) {
            startDateElement.click();
        }

        // Wait for the datepicker panel to be visible
        WebElement panel = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.p-datepicker[role='dialog']")
                )
        );

        try {
            // Click on the year button to open year picker
            WebElement yearButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.p-datepicker-year.p-link")
            ));
            yearButton.click();

            // Select the specific year
            WebElement yearEl = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(@class,'p-yearpicker-year') and normalize-space()='" + year + "']")
            ));
            yearEl.click();

            // Select month (PrimeNG shows month short names)
            String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            String monthName = monthNames[month - 1];
            WebElement monthEl = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(@class,'p-monthpicker-month') and normalize-space()='" + monthName + "']")
            ));
            monthEl.click();

            // Select day - use the specific day number with exact classes
            WebElement dayEl = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(@class,'p-ripple') and contains(@class,'p-element') and normalize-space()='" + day + "']")
            ));
            dayEl.click();

            // Wait for the input value to be set
            wait.until(drv -> {
                String value = startDateElement.getAttribute("value");
                return value != null && !value.isEmpty();
            });
            
        } catch (Exception e) {
            // If PrimeNG method fails, close the panel and use alternative method
            try {
                // Try to close the panel by clicking outside or pressing Escape
                startDateElement.sendKeys(Keys.ESCAPE);
                Thread.sleep(500);
            } catch (Exception ignored) {}
            
            // Now use the alternative method
            setStartDateAlternative(date);
        }
    }

    private void clickPrev(WebElement panel) {
        // Prefer aria-label when available
        java.util.List<WebElement> candidates = new java.util.ArrayList<>();
        candidates.addAll(panel.findElements(By.xpath(".//button[contains(@aria-label,'Previous')]")));
        candidates.addAll(panel.findElements(By.cssSelector(".p-datepicker-prev")));
        for (WebElement el : candidates) {
            if (el.isDisplayed() && el.isEnabled()) {
                el.click();
                return;
            }
        }
        // As a last resort, click title to change view then try again
        try { panel.findElement(By.cssSelector(".p-datepicker-title")).click(); } catch (Exception ignored) {}
        if (!candidates.isEmpty()) { candidates.get(0).click(); }
    }

    private void clickNext(WebElement panel) {
        java.util.List<WebElement> candidates = new java.util.ArrayList<>();
        candidates.addAll(panel.findElements(By.xpath(".//button[contains(@aria-label,'Next')]")));
        candidates.addAll(panel.findElements(By.cssSelector(".p-datepicker-next")));
        for (WebElement el : candidates) {
            if (el.isDisplayed() && el.isEnabled()) {
                el.click();
                return;
            }
        }
        try { panel.findElement(By.cssSelector(".p-datepicker-title")).click(); } catch (Exception ignored) {}
        if (!candidates.isEmpty()) { candidates.get(0).click(); }
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

    public void searchByStartDateOnly(String date) {
        try {
            // Set the start date first
            setStartDate(date);
            
            // Wait a moment for the date to be set
            Thread.sleep(500);
            
            // Click the search button to search by start date only
            wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
            
            // Wait for results to appear
            wait.until(ExpectedConditions.presenceOfElementLocated(resultsTable));
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to search by start date: " + date + ". Error: " + e.getMessage(), e);
        }
    }

    public void setEndDate(String date) {
        try {
            // Prefer PrimeNG-specific picker handling
            setEndDateWithPrimeNg(date);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set end date: " + date + ". Error: " + e.getMessage(), e);
        }
    }

    public void searchByEndDateOnly(String date) {
        try {
            // Set the end date first
            setEndDate(date);
            
            // Wait a moment for the date to be set
            Thread.sleep(500);
            
            // Click the search button to search by end date only
            wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
            
            // Wait for results to appear
            wait.until(ExpectedConditions.presenceOfElementLocated(resultsTable));
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to search by end date: " + date + ". Error: " + e.getMessage(), e);
        }
    }

    public void searchByDateRange(String startDate, String endDate) {
        try {
            // Set both start and end dates
            setStartDate(startDate);
            setEndDate(endDate);
            
            // Wait a moment for both dates to be set
            Thread.sleep(500);
            
            // Click the search button to search by date range
            wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
            
            // Wait for results to appear
            wait.until(ExpectedConditions.presenceOfElementLocated(resultsTable));
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to search by date range: " + startDate + " to " + endDate + ". Error: " + e.getMessage(), e);
        }
    }

    private void setEndDateWithPrimeNg(String date) {
        String[] parts = date.split("/");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Date must be in dd/MM/yyyy format: " + date);
        }
        String day = parts[0];
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        // Check if datepicker is already open
        boolean isAlreadyOpen = false;
        try {
            WebElement existingPanel = driver.findElement(By.cssSelector("div.p-datepicker[role='dialog']"));
            isAlreadyOpen = existingPanel.isDisplayed();
        } catch (Exception ignored) {}

        // Open the datepicker panel if not already open
        WebElement endDateElement = wait.until(ExpectedConditions.elementToBeClickable(endDateInput));
        if (!isAlreadyOpen) {
            endDateElement.click();
        }

        // Wait for the datepicker panel to be visible
        WebElement panel = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("div.p-datepicker[role='dialog']")
                )
        );

        try {
            // Click on the year button to open year picker
            WebElement yearButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.p-datepicker-year.p-link")
            ));
            yearButton.click();

            // Select the specific year
            WebElement yearEl = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(@class,'p-yearpicker-year') and normalize-space()='" + year + "']")
            ));
            yearEl.click();

            // Select month (PrimeNG shows month short names)
            String[] monthNames = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
            String monthName = monthNames[month - 1];
            WebElement monthEl = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(@class,'p-monthpicker-month') and normalize-space()='" + monthName + "']")
            ));
            monthEl.click();

            // Select day - use the specific day number with exact classes
            WebElement dayEl = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[contains(@class,'p-ripple') and contains(@class,'p-element') and normalize-space()='" + day + "']")
            ));
            dayEl.click();

            // Wait for the input value to be set
            wait.until(drv -> {
                String value = endDateElement.getAttribute("value");
                return value != null && !value.isEmpty();
            });
            
        } catch (Exception e) {
            // If PrimeNG method fails, close the panel and use alternative method
            try {
                // Try to close the panel by clicking outside or pressing Escape
                endDateElement.sendKeys(Keys.ESCAPE);
                Thread.sleep(500);
            } catch (Exception ignored) {}
            
            // Now use the alternative method
            setStartDateAlternative(date);
        }
    }

}
