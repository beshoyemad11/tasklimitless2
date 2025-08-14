package com.hackathon.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PatientListPage {
    WebDriver driver;
    WebDriverWait wait;

    public PatientListPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // عنصر الذهاب إلى صفحة Medication Orders
    private By medicationOrdersLink = By.xpath("//a[contains(@href, '/medication-orders')]");

    public void goToMedicationOrders() {
        wait.until(ExpectedConditions.elementToBeClickable(medicationOrdersLink)).click();
    }
}
