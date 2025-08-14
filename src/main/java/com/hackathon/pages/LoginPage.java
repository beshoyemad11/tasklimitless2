package com.hackathon.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    WebDriver driver;
    WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    private By emailInput = By.id("email");
    private By passwordInput = By.id("password");
    private By loginButton = By.xpath("//button[contains(text(),'Sign in')]");

    // عنصر نتأكد من ظهوره بعد تسجيل الدخول، مثلاً Navbar أو عنصر واضح في الصفحة الجديدة
    private By dashboardHeader = By.xpath("//h1[contains(text(),'Medication Orders')]");

    public void login(String email, String password) {
        driver.get("https://limitlesscaredoctorportal2-staging.azurewebsites.net/auth/login");

        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(loginButton).click();

        // ننتظر لحد ما يتم التحويل لصفحة /patientList
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("/patientList"));
    }
}
