package pageObjects;

public interface loginPageElements {

    //New User Signup section
    String hdrNewUserSignup = "//h2[text()='New User Signup!']";
    String txtSignupName = "//input[@data-qa='signup-name']";
    String txtSignupEmail = "//input[@data-qa='signup-email']";
    String btnSignup = "//button[@data-qa='signup-button']";
    String msgEmailExists = "//p[text()='Email Address already exist!']";

    //Login section
    String hdrLoginToAccount = "//h2[text()='Login to your account']";
    String txtLoginEmail = "//input[@data-qa='login-email']";
    String txtLoginPassword = "//input[@data-qa='login-password']";
    String btnLogin = "//button[@data-qa='login-button']";
    String msgLoginError = "//p[text()='Your email or password is incorrect!']";

}
