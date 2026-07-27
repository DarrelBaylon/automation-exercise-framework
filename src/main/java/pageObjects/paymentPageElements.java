package pageObjects;

public interface paymentPageElements {

    //Payment form
    String txtNameOnCard = "//input[@data-qa='name-on-card']";
    String txtCardNumber = "//input[@data-qa='card-number']";
    String txtCardCvc = "//input[@data-qa='cvc']";
    String txtCardExpiryMonth = "//input[@data-qa='expiry-month']";
    String txtCardExpiryYear = "//input[@data-qa='expiry-year']";
    String btnPayAndConfirm = "//button[@data-qa='pay-button']";

    //Order confirmation
    String hdrOrderPlaced = "//h2[@data-qa='order-placed']";
    String msgOrderConfirmed = "//p[contains(text(),'Congratulations! Your order has been confirmed!')]";
    String btnDownloadInvoice = "//a[contains(@href,'download_invoice')]";
    String btnContinue = "//a[@data-qa='continue-button']";

}
