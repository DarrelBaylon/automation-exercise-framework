package pageEvents;

import base.BaseTest;
import pageObjects.productDetailsPageElements;
import utils.Constants;

public class productDetailsPageEvents extends BaseTest{

    public void validateProductDetailPageVisible(){
        logger.info("Validate user is landed to product detail page");
        // Confirm navigation actually reached the product detail page and did not
        // remain on the products page due to an ad interception or redirect.
        org.testng.Assert.assertTrue(waitForUrlContains("/product_details/", 15),
                "Expected the URL to contain '/product_details/' but it was: " + driver.getCurrentUrl());
        handleOptionalConsent();
        waitForElementVisible(productDetailsPageElements.hdrProductName, 20);
        assertElementIsDisplayed(productDetailsPageElements.hdrProductName);
    }

    public void validateProductDetailsVisible(){
        logger.info("Validate product name, category, price, availability, condition and brand are visible");
        assertElementIsDisplayed(productDetailsPageElements.hdrProductName);
        assertElementIsDisplayed(productDetailsPageElements.txtProductCategory);
        assertElementIsDisplayed(productDetailsPageElements.txtProductPrice);
        assertElementIsDisplayed(productDetailsPageElements.txtProductAvailability);
        assertElementIsDisplayed(productDetailsPageElements.txtProductCondition);
        assertElementIsDisplayed(productDetailsPageElements.txtProductBrand);
    }

    public void setQuantity(String quantity){
        logger.info("Increase quantity to " + quantity);
        clear(productDetailsPageElements.txtQuantity);
        sendKeys(productDetailsPageElements.txtQuantity, quantity);
    }

    public void clickAddToCart(){
        logger.info("Click 'Add to cart' button");
        click(productDetailsPageElements.btnAddToCart);
    }

    //Review (Test Case 21)
    public void validateWriteYourReviewVisible(){
        logger.info("Validate 'Write Your Review' is visible");
        assertElementIsDisplayed(productDetailsPageElements.lnkWriteYourReview);
    }

    public void submitReview(){
        logger.info("Enter name, email and review then click 'Submit' button");
        scrollToElement(productDetailsPageElements.txtReviewName);
        sendKeys(productDetailsPageElements.txtReviewName, Constants.REVIEW_NAME);
        sendKeys(productDetailsPageElements.txtReviewEmail, Constants.REVIEW_EMAIL);
        sendKeys(productDetailsPageElements.txtReviewText, Constants.REVIEW_TEXT);
        click(productDetailsPageElements.btnSubmitReview);
    }

    public void validateReviewSuccess(){
        logger.info("Validate success message 'Thank you for your review.' is visible");
        assertElementIsDisplayed(productDetailsPageElements.msgReviewSuccess);
    }

}
