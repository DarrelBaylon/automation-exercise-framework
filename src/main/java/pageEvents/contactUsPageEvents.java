package pageEvents;

import java.io.File;

import base.BaseTest;
import pageObjects.contactUsPageElements;
import utils.Constants;

public class contactUsPageEvents extends BaseTest{

    public void validateGetInTouchVisible(){
        logger.info("Validate 'GET IN TOUCH' is visible");
        assertElementIsDisplayed(contactUsPageElements.hdrGetInTouch);
    }

    public void fillContactForm(String name, String email){
        logger.info("Enter name, email, subject and message");
        sendKeys(contactUsPageElements.txtName, name);
        sendKeys(contactUsPageElements.txtEmail, email);
        sendKeys(contactUsPageElements.txtSubject, Constants.CONTACT_SUBJECT);
        sendKeys(contactUsPageElements.txtMessage, Constants.CONTACT_MESSAGE);
    }

    public void uploadFile(){
        logger.info("Upload file");
        String filePath = new File(System.getProperty("user.dir")
                + File.separator + "src" + File.separator + "test" + File.separator + "resources"
                + File.separator + "test-data" + File.separator + "contact-upload.txt").getAbsolutePath();
        //File inputs receive the path directly through sendKeys, so the field
        //must not be cleared first and no wait-for-visibility is needed.
        ele.getXPATHWebElement(contactUsPageElements.txtUploadFile).sendKeys(filePath);
    }

    public void clickSubmitAndAcceptAlert(){
        logger.info("Click 'Submit' button and accept the confirmation alert");
        click(contactUsPageElements.btnSubmit);
        acceptAlertIfPresent(5);
    }

    public void validateSubmitSuccess(){
        logger.info("Validate success message 'Success! Your details have been submitted successfully.' is visible");
        assertElementIsDisplayed(contactUsPageElements.msgSubmitSuccess);
        assertTextContains(contactUsPageElements.msgSubmitSuccess, "Success! Your details have been submitted successfully.");
    }

    public void clickHomeButton(){
        logger.info("Click 'Home' button");
        click(contactUsPageElements.btnHome);
        // A vignette can interrupt the return to home; recover and land on home.
        recoverHomeIfVignetteInterrupted(Constants.url);
    }

}
