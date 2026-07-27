package pageObjects;

public interface productDetailsPageElements {

    //Product information
    String hdrProductName = "//div[contains(@class,'product-information')]//h2";
    String txtProductCategory = "//div[contains(@class,'product-information')]//p[contains(text(),'Category')]";
    String txtProductPrice = "//div[contains(@class,'product-information')]//span/span";
    String txtProductAvailability = "//div[contains(@class,'product-information')]//b[text()='Availability:']";
    String txtProductCondition = "//div[contains(@class,'product-information')]//b[text()='Condition:']";
    String txtProductBrand = "//div[contains(@class,'product-information')]//b[text()='Brand:']";

    //Quantity and cart
    String txtQuantity = "//input[@id='quantity']";
    String btnAddToCart = "//button[contains(@class,'cart')]";

    //Review section
    String lnkWriteYourReview = "//a[@href='#reviews']";
    String txtReviewName = "//input[@id='name']";
    String txtReviewEmail = "//input[@id='email']";
    String txtReviewText = "//textarea[@id='review']";
    String btnSubmitReview = "//button[@id='button-review']";
    String msgReviewSuccess = "//span[contains(text(),'Thank you for your review.')]";

}
