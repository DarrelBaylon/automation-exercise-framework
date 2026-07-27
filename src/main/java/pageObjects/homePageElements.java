package pageObjects;

public interface homePageElements {

    //Navigation bar
    String lnkSignupLogin = "//ul[@class='nav navbar-nav']//a[@href='/login']";
    String lnkProducts = "//ul[@class='nav navbar-nav']//a[@href='/products']";
    String lnkCart = "//ul[@class='nav navbar-nav']//a[@href='/view_cart']";
    String lnkContactUs = "//ul[@class='nav navbar-nav']//a[@href='/contact_us']";
    String lnkTestCases = "//ul[@class='nav navbar-nav']//a[@href='/test_cases']";
    String lnkLogout = "//ul[@class='nav navbar-nav']//a[@href='/logout']";
    String lnkDeleteAccount = "//ul[@class='nav navbar-nav']//a[@href='/delete_account']";
    String lnkLoggedInAs = "//a[contains(normalize-space(.),'Logged in as')]";

    //Home page content
    String hdrSliderText = "//*[@id='slider']//h2[contains(normalize-space(.),'Full-Fledged practice website')]";
    String hdrSliderActiveHeading = "//*[@id='slider']//div[contains(concat(' ', normalize-space(@class), ' '), ' item ') and contains(concat(' ', normalize-space(@class), ' '), ' active ')]//h2[contains(normalize-space(.),'Full-Fledged practice website')]";
    String hdrFeaturesItems = "//h2[@class='title text-center' and contains(text(),'Features Items')]";
    String lnkFirstViewProductHome = "//div[@class='features_items']//a[@href='/product_details/1']";

    //Category side bar
    String hdrCategory = "//div[@class='left-sidebar']//h2[text()='Category']";
    String lnkWomenCategory = "//a[@href='#Women']";
    String lnkMenCategory = "//a[@href='#Men']";
    String lnkWomenDressSubCategory = "//div[@id='Women']//a[@href='/category_products/1']";
    String lnkWomenTopsSubCategory = "//div[@id='Women']//a[@href='/category_products/2']";
    String lnkMenTshirtsSubCategory = "//div[@id='Men']//a[@href='/category_products/3']";

    //Recommended items
    String hdrRecommendedItems = "//h2[contains(text(),'recommended items')]";
    String btnAddToCartRecommended = "//div[@id='recommended-item-carousel']//div[contains(@class,'item') and contains(@class,'active')]//a[contains(@class,'add-to-cart')]";
    String secRecommendedActiveSlide = "//div[@id='recommended-item-carousel']//div[contains(concat(' ', normalize-space(@class), ' '), ' item ') and contains(concat(' ', normalize-space(@class), ' '), ' active ')]";
    String txtRecommendedItemName = "//div[@id='recommended-item-carousel']//div[contains(@class,'item') and contains(@class,'active')]//div[@class='productinfo text-center']//p";

    //Footer subscription
    String hdrSubscription = "//div[@class='single-widget']//h2[text()='Subscription']";
    String txtSubscribeEmail = "//input[@id='susbscribe_email']";
    String btnSubscribe = "//button[@id='subscribe']";
    String msgSubscribeSuccess = "//div[contains(@class,'alert-success') and contains(text(),'You have been successfully subscribed!')]";

    //Scroll up arrow
    String btnScrollUpArrow = "//a[@id='scrollUp']";

}
