package pageEvents;

import base.BaseTest;
import pageObjects.loginPageElements;

public class loginPageEvents extends BaseTest{

    public void validateNewUserSignupVisible(){
        logger.info("Validate 'New User Signup!' is visible");
        assertElementIsDisplayed(loginPageElements.hdrNewUserSignup);
    }

    public void enterSignupNameAndEmail(String name, String email){
        logger.info("Enter name and email address for signup");
        clear(loginPageElements.txtSignupName);
        sendKeys(loginPageElements.txtSignupName, name);
        clear(loginPageElements.txtSignupEmail);
        sendKeys(loginPageElements.txtSignupEmail, email);
    }

    public void clickSignupButton(){
        logger.info("Click 'Signup' button");
        click(loginPageElements.btnSignup);
    }

    public void validateLoginToAccountVisible(){
        logger.info("Validate 'Login to your account' is visible");
        assertElementIsDisplayed(loginPageElements.hdrLoginToAccount);
    }

    public void login(String email, String password){
        logger.info("Enter email address and password then click 'login' button");
        clear(loginPageElements.txtLoginEmail);
        sendKeys(loginPageElements.txtLoginEmail, email);
        clear(loginPageElements.txtLoginPassword);
        sendKeys(loginPageElements.txtLoginPassword, password);
        click(loginPageElements.btnLogin);
    }

    public void validateLoginError(){
        logger.info("Validate error 'Your email or password is incorrect!' is visible");
        assertElementIsDisplayed(loginPageElements.msgLoginError);
    }

    public void validateEmailAlreadyExistsError(){
        logger.info("Validate error 'Email Address already exist!' is visible");
        assertElementIsDisplayed(loginPageElements.msgEmailExists);
    }

}
