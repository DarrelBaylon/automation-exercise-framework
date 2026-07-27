package pageObjects;

public interface checkoutPageElements {

    String hdrAddressDetails = "//h2[text()='Address Details']";
    String hdrReviewYourOrder = "//h2[text()='Review Your Order']";
    String secDeliveryAddress = "//ul[@id='address_delivery']";
    String secBillingAddress = "//ul[@id='address_invoice']";
    String txtOrderComment = "//textarea[@name='message']";
    String btnPlaceOrder = "//a[text()='Place Order']";

}
