package pageEvents;

import base.BaseTest;
import pageObjects.signupPageElements;
import utils.Constants;

public class signupPageEvents extends BaseTest{

    homePageEvents homePage = new homePageEvents();
    loginPageEvents loginPage = new loginPageEvents();

    public void validateEnterAccountInfoVisible(){
        logger.info("Validate 'ENTER ACCOUNT INFORMATION' is visible");
        assertElementIsDisplayed(signupPageElements.hdrEnterAccountInfo);
    }

    public void completeAccountInformation(String password){
        //Account Information
        logger.info("Fill details: Title, Password, Date of birth");
        click(signupPageElements.rdnTitleMr);
        sendKeys(signupPageElements.txtPassword, password);
        selectElementByVisibleText(signupPageElements.slcDobDay, Constants.TEST_DOB_DAY);
        selectElementByVisibleText(signupPageElements.slcDobMonth, Constants.TEST_DOB_MONTH);
        selectElementByVisibleText(signupPageElements.slcDobYear, Constants.TEST_DOB_YEAR);

        logger.info("Select checkboxes 'Sign up for our newsletter!' and 'Receive special offers from our partners!'");
        click(signupPageElements.chkNewsletter);
        click(signupPageElements.chkSpecialOffers);

        //Address Information
        logger.info("Fill details: First name, Last name, Company, Address, Address2, Country, State, City, Zipcode, Mobile Number");
        sendKeys(signupPageElements.txtFirstName, Constants.TEST_FIRST_NAME);
        sendKeys(signupPageElements.txtLastName, Constants.TEST_LAST_NAME);
        sendKeys(signupPageElements.txtCompany, Constants.TEST_COMPANY);
        sendKeys(signupPageElements.txtAddress, Constants.TEST_ADDRESS);
        sendKeys(signupPageElements.txtAddress2, Constants.TEST_ADDRESS2);
        selectElementByVisibleText(signupPageElements.slcCountry, Constants.TEST_COUNTRY);
        sendKeys(signupPageElements.txtState, Constants.TEST_STATE);
        sendKeys(signupPageElements.txtCity, Constants.TEST_CITY);
        sendKeys(signupPageElements.txtZipcode, Constants.TEST_ZIPCODE);
        sendKeys(signupPageElements.txtMobileNumber, Constants.TEST_MOBILE_NUMBER);

        logger.info("Click 'Create Account' button");
        click(signupPageElements.btnCreateAccount);
    }

    public void validateAccountCreated(){
        logger.info("Validate 'ACCOUNT CREATED!' is visible");
        assertElementIsDisplayed(signupPageElements.hdrAccountCreated);
    }

    public void validateAccountDeleted(){
        logger.info("Validate 'ACCOUNT DELETED!' is visible");
        assertElementIsDisplayed(signupPageElements.hdrAccountDeleted);
    }

    public void clickContinue(){
        logger.info("Click 'Continue' button");
        click(signupPageElements.btnContinue);
    }

    /**
     * Clicks the Continue button on the ACCOUNT CREATED / ACCOUNT DELETED
     * confirmation page and reliably lands on the home page, recovering from a
     * Google vignette that can interrupt the transition. Session is preserved.
     */
    public void clickContinueRobustly(){
        logger.info("Click 'Continue' button (ad-aware)");
        click(signupPageElements.btnContinue);
        long deadline = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < deadline) {
            String url = driver.getCurrentUrl();
            boolean vignette = (url != null && url.contains("#google_vignette")) || isDocumentScrollLocked();
            if (vignette) {
                recoverHomeIfVignetteInterrupted(Constants.url);
                break;
            }
            // Navigation succeeded once the confirmation page's Continue button is gone.
            if (!isPresent(signupPageElements.btnContinue)) {
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        handleOptionalConsent();
    }

    /**
     * Full registration workflow starting from the Signup / Login page:
     * signup name and email, account information form, account created
     * confirmation, then continue. Reused by Test Cases 1, 14, 15, 23, 24 and
     * as an account precondition for Test Cases 2, 4, 16 and 20.
     */
    public void createAccount(String name, String email, String password){
        loginPage.validateNewUserSignupVisible();
        loginPage.enterSignupNameAndEmail(name, email);
        loginPage.clickSignupButton();
        validateEnterAccountInfoVisible();
        completeAccountInformation(password);
        validateAccountCreated();
        clickContinueRobustly();
    }

    /**
     * Deletes the logged-in account and confirms 'ACCOUNT DELETED!'. The delete
     * click is ad-aware: if a vignette interrupts it, the account is deleted by
     * navigating directly to /delete_account (the same GET the link performs)
     * while preserving the authenticated session.
     */
    public void deleteAccount(){
        homePage.clickDeleteAccount();
        long deadline = System.currentTimeMillis() + 8000;
        while (System.currentTimeMillis() < deadline) {
            if (isPresent(signupPageElements.hdrAccountDeleted)) {
                break;
            }
            String url = driver.getCurrentUrl();
            boolean vignette = (url != null && url.contains("#google_vignette")) || isDocumentScrollLocked();
            if (vignette) {
                logger.info("Vignette interrupted Delete Account; deleting via direct navigation (session preserved).");
                recoverVignetteScrollLock();
                waitForVignetteRecovery(5);
                driver.navigate().to(Constants.url + "delete_account");
                waitForVignetteRecovery(3);
                break;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        validateAccountDeleted();
        clickContinueRobustly();
    }

    /**
     * Best-effort account deletion for tests where deletion is a non-official
     * cleanup step. Logs a warning with diagnostics if it cannot complete and
     * never rethrows, so an optional cleanup failure can never replace a
     * successful official assertion. NOT for tests where deletion is an official
     * step (those keep the strict deleteAccount()).
     */
    public void deleteAccountBestEffort(){
        try {
            deleteAccount();
        } catch (Exception e) {
            logger.info("CLEANUP WARNING (non-official step): account deletion did not complete. "
                    + "url=" + safeUrl() + "; accountDeletedVisible=" + isPresent(signupPageElements.hdrAccountDeleted)
                    + "; error=" + e.getMessage());
        }
    }

    private String safeUrl(){
        try {
            return driver.getCurrentUrl();
        } catch (Exception e) {
            return "n/a";
        }
    }

}
