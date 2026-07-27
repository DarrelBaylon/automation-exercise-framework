package pageObjects;

public interface contactUsPageElements {

    String hdrGetInTouch = "//div[@class='contact-form']//h2[contains(text(),'Get In Touch')]";
    String txtName = "//input[@data-qa='name']";
    String txtEmail = "//input[@data-qa='email']";
    String txtSubject = "//input[@data-qa='subject']";
    String txtMessage = "//textarea[@data-qa='message']";
    String txtUploadFile = "//input[@name='upload_file']";
    String btnSubmit = "//input[@data-qa='submit-button']";
    String msgSubmitSuccess = "//div[contains(@class,'status') and contains(@class,'alert-success')]";
    String btnHome = "//a[@class='btn btn-success']";

}
