package pageEvents;

import base.BaseTest;
import pageObjects.checkoutPageElements;
import utils.Constants;

public class checkoutPageEvents extends BaseTest{

    public void validateAddressDetailsAndReviewOrderVisible(){
        logger.info("Validate Address Details and Review Your Order are visible");
        assertElementIsDisplayed(checkoutPageElements.hdrAddressDetails);
        assertElementIsDisplayed(checkoutPageElements.hdrReviewYourOrder);
    }

    /**
     * Validates that the delivery and billing addresses show the same details
     * that were entered during account registration (Test Case 23).
     */
    public void validateAddressesMatchRegistration(String accountName){
        logger.info("Validate delivery address matches the registration details");
        assertElementIsDisplayed(checkoutPageElements.secDeliveryAddress);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_FIRST_NAME);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_LAST_NAME);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_COMPANY);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_ADDRESS);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_ADDRESS2);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_CITY);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_STATE);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_ZIPCODE);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_COUNTRY);
        assertTextContains(checkoutPageElements.secDeliveryAddress, Constants.TEST_MOBILE_NUMBER);

        logger.info("Validate billing address matches the registration details");
        assertElementIsDisplayed(checkoutPageElements.secBillingAddress);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_FIRST_NAME);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_LAST_NAME);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_COMPANY);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_ADDRESS);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_ADDRESS2);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_CITY);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_STATE);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_ZIPCODE);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_COUNTRY);
        assertTextContains(checkoutPageElements.secBillingAddress, Constants.TEST_MOBILE_NUMBER);
    }

    public void enterCommentAndPlaceOrder(){
        logger.info("Enter description in comment text area and click 'Place Order'");
        scrollToElement(checkoutPageElements.txtOrderComment);
        sendKeys(checkoutPageElements.txtOrderComment, Constants.ORDER_COMMENT);
        // Place Order navigates to /payment; a vignette can interrupt this, so use
        // ad-aware navigation verified by the name-on-card field rendering.
        clickAndNavigateWithAdFallback(
                checkoutPageElements.btnPlaceOrder,
                "/payment",
                Constants.url + "payment",
                pageObjects.paymentPageElements.txtNameOnCard);
    }

}
