package com.hackathon.stepdefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.WebDriver;
import utilities.DriverManagers;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void setUp() {
        DriverManagers.initDriver();
        driver = DriverManagers.getDriver();
    }

    @After
    public void tearDown() {
        DriverManagers.quitDriver();
        driver = null;
    }
}


