package pageObjects;

public interface signupPageElements {

    //Enter Account Information section
    String hdrEnterAccountInfo = "//b[text()='Enter Account Information']";
    String rdnTitleMr = "//input[@id='id_gender1']";
    String txtPassword = "//input[@data-qa='password']";
    String slcDobDay = "//select[@data-qa='days']";
    String slcDobMonth = "//select[@data-qa='months']";
    String slcDobYear = "//select[@data-qa='years']";
    String chkNewsletter = "//input[@id='newsletter']";
    String chkSpecialOffers = "//input[@id='optin']";

    //Address Information section
    String txtFirstName = "//input[@data-qa='first_name']";
    String txtLastName = "//input[@data-qa='last_name']";
    String txtCompany = "//input[@data-qa='company']";
    String txtAddress = "//input[@data-qa='address']";
    String txtAddress2 = "//input[@data-qa='address2']";
    String slcCountry = "//select[@data-qa='country']";
    String txtState = "//input[@data-qa='state']";
    String txtCity = "//input[@data-qa='city']";
    String txtZipcode = "//input[@data-qa='zipcode']";
    String txtMobileNumber = "//input[@data-qa='mobile_number']";
    String btnCreateAccount = "//button[@data-qa='create-account']";

    //Confirmation pages
    String hdrAccountCreated = "//h2[@data-qa='account-created']";
    String hdrAccountDeleted = "//h2[@data-qa='account-deleted']";
    String btnContinue = "//a[@data-qa='continue-button']";

}
