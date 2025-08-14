Feature: Search Patient Medication Orders

  Scenario: Valid search for patient ID
    Given I am logged in as "hiv.assistant@doctor1.com" with password "strinG@123"
    When I search for patient ID "30208250100666"
    Then I should see the patient medication orders

  Scenario: Valid search for medicine name
    Given I am logged in as "hiv.assistant@doctor1.com" with password "strinG@123"
    When I search for medicine name "Abacavir"
    Then I should see the patient medication orders


#  Scenario: Search patient by Start Date
#    Given  I am logged in as "hiv.assistant@doctor1.com" with password "strinG@123"
#    When I enter the Start Date "04/08/2025"
#    And I click the Search button
#    Then I should see the patient medication orders

#orders  Scenario: Set and verify start date
#    Given I am logged in as "hiv.assistant@doctor1.com" with password "strinG@123"
#    When I set the start date to "03/08/2025"
#    Then I should see the start date is set to "03/08/2025"

  Scenario: Search patient by start date filter
    Given I am logged in as "hiv.assistant@doctor1.com" with password "strinG@123"
    When I set the start date to "03/08/2025"
    And I search for patient ID "30208250100666"
    Then I should see the patient medication orders