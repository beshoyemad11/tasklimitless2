package com.hackathon.stepdefinitions;

import com.hackathon.pages.LoginPage;
import com.hackathon.pages.MedicationOrdersPage;
import com.hackathon.pages.PatientListPage;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class SearchPatientSteps {

    WebDriver driver = Hooks.driver;
    LoginPage loginPage;
    PatientListPage patientListPage;
    MedicationOrdersPage medicationOrdersPage;

    @Given("I am logged in as {string} with password {string}")
    public void iAmLoggedIn(String email, String password) {
        driver = Hooks.driver;
        loginPage = new LoginPage(driver);
        loginPage.login(email, password);
        patientListPage = new PatientListPage(driver);
        patientListPage.goToMedicationOrders();
        medicationOrdersPage = new MedicationOrdersPage(driver);
    }

    @When("I search for patient ID {string}")
    public void iSearchForPatientId(String id) {
        medicationOrdersPage.searchByPatientId(id);
    }

    @When("I search for medicine name {string}")
    public void iSearchForMedicineName(String medicineName) {
        MedicationOrdersPage.searchByMedicineName(medicineName);
    }

    @When("I search by start date {string}")
    public void iSearchByStartDate(String date) {
        medicationOrdersPage.searchByStartDateOnly(date);
    }

    @When("I set the end date to {string}")
    public void iSetTheEndDateTo(String date) {
        medicationOrdersPage.setEndDate(date);
    }

    @When("I search by end date {string}")
    public void iSearchByEndDate(String date) {
        medicationOrdersPage.searchByEndDateOnly(date);
    }

    @When("I search by date range from {string} to {string}")
    public void iSearchByDateRange(String startDate, String endDate) {
        medicationOrdersPage.searchByDateRange(startDate, endDate);
    }

    @When("I set the start date to {string}")
    public void iSetTheStartDateTo(String date) {
        // Check the field status for debugging
        medicationOrdersPage.isStartDateFieldEditable();
        
        // Try to set the date (our method now handles read-only fields)
        try {
            medicationOrdersPage.setStartDate(date);
        } catch (Exception e) {
            System.out.println("First method failed, trying alternative method: " + e.getMessage());
            medicationOrdersPage.setStartDateAlternative(date);
        }
    }

    @Then("I should see the start date is set to {string}")
    public void iShouldSeeTheStartDateIsSetTo(String expectedDate) {
        String actualDate = medicationOrdersPage.getStartDate();
        Assert.assertEquals(actualDate, expectedDate, "Start date was not set correctly. Expected: " + expectedDate + ", Actual: " + actualDate);
    }

//   medicineName @When("I enter the Start Date {string}")
//    public void iEnterTheStartDate(String date) {
//        MedicationOrdersPage.selectStartDate(date);
//    }

    @Then("I should see the patient medication orders")
    public void iShouldSeeThePatientMedicationOrders() {
        Assert.assertTrue(medicationOrdersPage.isPatientSearchResultDisplayed(), "No patient medication orders were displayed in the table.");
        Assert.assertTrue(medicationOrdersPage.isPatientdatainTable("01021901196"), "Patient phone  01284322659 was not found in the table results.");
    }
}
