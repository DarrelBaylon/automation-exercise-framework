package regression;

import java.lang.reflect.Method;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import base.BaseTest;
import pageEvents.cartPageEvents;
import pageEvents.checkoutPageEvents;
import pageEvents.contactUsPageEvents;
import pageEvents.homePageEvents;
import pageEvents.loginPageEvents;
import pageEvents.paymentPageEvents;
import pageEvents.productDetailsPageEvents;
import pageEvents.productsPageEvents;
import pageEvents.signupPageEvents;
import pageEvents.testCasesPageEvents;
import utils.Constants;

public class AutomationExerciseTestCases extends BaseTest{
    String browser;
    homePageEvents homePage = new homePageEvents();
    loginPageEvents loginPage = new loginPageEvents();
    signupPageEvents signupPage = new signupPageEvents();
    contactUsPageEvents contactUsPage = new contactUsPageEvents();
    testCasesPageEvents testCasesPage = new testCasesPageEvents();
    productsPageEvents productsPage = new productsPageEvents();
    productDetailsPageEvents productDetailsPage = new productDetailsPageEvents();
    cartPageEvents cartPage = new cartPageEvents();
    checkoutPageEvents checkoutPage = new checkoutPageEvents();
    paymentPageEvents paymentPage = new paymentPageEvents();

    @BeforeTest(alwaysRun = true)
    @Parameters({"browser"})
    public void prepareReport(@Optional("chrome")String browser){
        this.browser = browser;
        beforeTestMethod(browser);
    }

    @BeforeMethod(alwaysRun = true)
    public void setup(Method testMethod){
        initializeBrowser(browser, testMethod);
    }

    private String createFreshAccountAndStayLoggedIn(String name){
        homePage.navigateToSignupLogin();
        String email = generateUniqueEmail();
        signupPage.createAccount(name, email, Constants.TEST_PASSWORD);
        homePage.validateLoggedInAs(name);
        return email;
    }

    private String createFreshAccountAndLogout(String name){
        String email = createFreshAccountAndStayLoggedIn(name);
        homePage.clickLogout();
        loginPage.validateLoginToAccountVisible();
        return email;
    }

    private void bestEffortLoginAndDeleteAccount(String email){
        try {
            loginPage.login(email, Constants.TEST_PASSWORD);
            signupPage.deleteAccount();
        } catch (Exception e) {
            System.out.println("Cleanup (not part of official steps) did not complete for " + email + ": " + e.getMessage());
        }
    }

    private void bestEffortDeleteAccount(){
        try {
            signupPage.deleteAccountBestEffort();
        } catch (Exception e) {
            System.out.println("Cleanup (not part of official steps) did not complete: " + e.getMessage());
        }
    }

    @Test(priority = 1, description = "Test Case 1: Register User")
    public void tc_01_RegisterUser(){
        homePage.validateHomePageVisible();
        homePage.navigateToSignupLogin();
        String name = "AutoUser" + generate4Digit();
        String email = generateUniqueEmail();
        signupPage.createAccount(name, email, Constants.TEST_PASSWORD);
        homePage.validateLoggedInAs(name);
        signupPage.deleteAccount();
    }

    @Test(priority = 2, description = "Test Case 2: Login User with correct email and password")
    public void tc_02_LoginUserWithCorrectEmailAndPassword(){
        homePage.validateHomePageVisible();
        String name = "AutoUser" + generate4Digit();
        String email = createFreshAccountAndLogout(name);

        loginPage.validateLoginToAccountVisible();
        loginPage.login(email, Constants.TEST_PASSWORD);
        homePage.validateLoggedInAs(name);
        signupPage.deleteAccount();
    }

    @Test(priority = 3, description = "Test Case 3: Login User with incorrect email and password")
    public void tc_03_LoginUserWithIncorrectEmailAndPassword(){
        homePage.validateHomePageVisible();
        homePage.navigateToSignupLogin();
        loginPage.validateLoginToAccountVisible();
        loginPage.login("wronguser" + System.currentTimeMillis() + "@example.com", "WrongPassword123");
        loginPage.validateLoginError();
    }

    @Test(priority = 4, description = "Test Case 4: Logout User")
    public void tc_04_LogoutUser(){
        homePage.validateHomePageVisible();
        String name = "AutoUser" + generate4Digit();
        String email = createFreshAccountAndLogout(name);

        loginPage.validateLoginToAccountVisible();
        loginPage.login(email, Constants.TEST_PASSWORD);
        homePage.validateLoggedInAs(name);
        homePage.clickLogout();
        loginPage.validateLoginToAccountVisible();

        bestEffortLoginAndDeleteAccount(email);
    }

    @Test(priority = 5, description = "Test Case 5: Register User with existing email")
    public void tc_05_RegisterUserWithExistingEmail(){
        homePage.validateHomePageVisible();
        String name = "AutoUser" + generate4Digit();
        String email = createFreshAccountAndLogout(name);

        loginPage.validateNewUserSignupVisible();
        loginPage.enterSignupNameAndEmail(name, email);
        loginPage.clickSignupButton();
        loginPage.validateEmailAlreadyExistsError();

        bestEffortLoginAndDeleteAccount(email);
    }

    @Test(priority = 6, description = "Test Case 6: Contact Us Form")
    public void tc_06_ContactUsForm(){
        homePage.validateHomePageVisible();
        homePage.navigateToContactUs();
        contactUsPage.validateGetInTouchVisible();
        contactUsPage.fillContactForm("AutoUser" + generate4Digit(), generateUniqueEmail());
        contactUsPage.uploadFile();
        contactUsPage.clickSubmitAndAcceptAlert();
        contactUsPage.validateSubmitSuccess();
        contactUsPage.clickHomeButton();
        homePage.validateHomePageVisible();
    }

    @Test(priority = 7, description = "Test Case 7: Verify Test Cases Page")
    public void tc_07_VerifyTestCasesPage(){
        homePage.validateHomePageVisible();
        homePage.navigateToTestCases();
        testCasesPage.validateTestCasesPageVisible();
    }

    @Test(priority = 8, description = "Test Case 8: Verify All Products and product detail page")
    public void tc_08_VerifyAllProductsAndProductDetailPage(){
        homePage.validateHomePageVisible();
        homePage.navigateToProducts();
        productsPage.validateAllProductsPageVisible();
        productsPage.validateProductListVisible();
        productsPage.clickViewProductOfFirstProduct();
        productDetailsPage.validateProductDetailPageVisible();
        productDetailsPage.validateProductDetailsVisible();
    }

    @Test(priority = 9, description = "Test Case 9: Search Product")
    public void tc_09_SearchProduct(){
        homePage.validateHomePageVisible();
        homePage.navigateToProducts();
        productsPage.validateAllProductsPageVisible();
        productsPage.searchProduct(Constants.SEARCH_PRODUCT);
        productsPage.validateSearchedProductsVisible();
        productsPage.validateSearchResultsRelatedTo(Constants.SEARCH_PRODUCT);
    }

    @Test(priority = 10, description = "Test Case 10: Verify Subscription in home page")
    public void tc_10_VerifySubscriptionInHomePage(){
        homePage.validateHomePageVisible();
        homePage.scrollToFooter();
        homePage.validateSubscriptionHeadingVisible();
        homePage.subscribeWithEmail(Constants.SUBSCRIPTION_EMAIL);
        homePage.validateSubscriptionSuccess();
    }

    @Test(priority = 11, description = "Test Case 11: Verify Subscription in Cart page")
    public void tc_11_VerifySubscriptionInCartPage(){
        homePage.validateHomePageVisible();
        homePage.navigateToCart();
        homePage.scrollToFooter();
        homePage.validateSubscriptionHeadingVisible();
        homePage.subscribeWithEmail(Constants.SUBSCRIPTION_EMAIL);
        homePage.validateSubscriptionSuccess();
    }

    @Test(priority = 12, description = "Test Case 12: Add Products in Cart")
    public void tc_12_AddProductsInCart(){
        homePage.validateHomePageVisible();
        homePage.navigateToProducts();
        productsPage.validateAllProductsPageVisible();
        productsPage.addProductToCartByIndex(1);
        productsPage.clickContinueShopping();
        productsPage.addProductToCartByIndex(2);
        productsPage.clickViewCartFromModal();
        cartPage.validateCartPageVisible();
        cartPage.validateCartItemCount(2);
        cartPage.validateCartRowDetails(1, "Rs. 500", "1", "Rs. 500");
        cartPage.validateCartRowDetails(2, "Rs. 400", "1", "Rs. 400");
    }

    @Test(priority = 13, description = "Test Case 13: Verify Product quantity in Cart")
    public void tc_13_VerifyProductQuantityInCart(){
        homePage.validateHomePageVisible();
        homePage.clickFirstViewProductOnHome();
        productDetailsPage.validateProductDetailPageVisible();
        productDetailsPage.setQuantity("4");
        productDetailsPage.clickAddToCart();
        productsPage.clickViewCartFromModal();
        cartPage.validateCartPageVisible();
        cartPage.validateCartItemCount(1);
        cartPage.validateCartRowQuantity(1, "4");
    }

    @Test(priority = 14, description = "Test Case 14: Place Order: Register while Checkout")
    public void tc_14_PlaceOrderRegisterWhileCheckout(){
        homePage.validateHomePageVisible();
        homePage.navigateToProducts();
        productsPage.addProductToCartByIndex(1);
        productsPage.clickContinueShopping();
        homePage.navigateToCart();
        cartPage.validateCartPageVisible();
        cartPage.clickProceedToCheckout();
        cartPage.clickRegisterLoginFromModal();

        String name = "AutoUser" + generate4Digit();
        String email = generateUniqueEmail();
        signupPage.createAccount(name, email, Constants.TEST_PASSWORD);
        homePage.validateLoggedInAs(name);

        homePage.navigateToCart();
        cartPage.clickProceedToCheckout();
        checkoutPage.validateAddressDetailsAndReviewOrderVisible();
        checkoutPage.enterCommentAndPlaceOrder();
        paymentPage.completePayment();
        paymentPage.validateOrderPlaced();
        signupPage.deleteAccount();
    }

    @Test(priority = 15, description = "Test Case 15: Place Order: Register before Checkout")
    public void tc_15_PlaceOrderRegisterBeforeCheckout(){
        homePage.validateHomePageVisible();
        String name = "AutoUser" + generate4Digit();
        createFreshAccountAndStayLoggedIn(name);

        homePage.navigateToProducts();
        productsPage.addProductToCartByIndex(1);
        productsPage.clickContinueShopping();
        homePage.navigateToCart();
        cartPage.validateCartPageVisible();
        cartPage.clickProceedToCheckout();
        checkoutPage.validateAddressDetailsAndReviewOrderVisible();
        checkoutPage.enterCommentAndPlaceOrder();
        paymentPage.completePayment();
        paymentPage.validateOrderPlaced();
        signupPage.deleteAccount();
    }

    @Test(priority = 16, description = "Test Case 16: Place Order: Login before Checkout")
    public void tc_16_PlaceOrderLoginBeforeCheckout(){
        homePage.validateHomePageVisible();
        String name = "AutoUser" + generate4Digit();
        String email = createFreshAccountAndLogout(name);

        loginPage.login(email, Constants.TEST_PASSWORD);
        homePage.validateLoggedInAs(name);
        homePage.navigateToProducts();
        productsPage.addProductToCartByIndex(1);
        productsPage.clickContinueShopping();
        homePage.navigateToCart();
        cartPage.validateCartPageVisible();
        cartPage.clickProceedToCheckout();
        checkoutPage.validateAddressDetailsAndReviewOrderVisible();
        checkoutPage.enterCommentAndPlaceOrder();
        paymentPage.completePayment();
        paymentPage.validateOrderPlaced();
        signupPage.deleteAccount();
    }

    @Test(priority = 17, description = "Test Case 17: Remove Products From Cart")
    public void tc_17_RemoveProductsFromCart(){
        homePage.validateHomePageVisible();
        homePage.navigateToProducts();
        productsPage.addProductToCartByIndex(1);
        productsPage.clickContinueShopping();
        homePage.navigateToCart();
        cartPage.validateCartPageVisible();
        cartPage.removeFirstProductFromCart();
        cartPage.validateCartIsEmpty();
    }

    @Test(priority = 18, description = "Test Case 18: View Category Products")
    public void tc_18_ViewCategoryProducts(){
        homePage.validateCategorySidebarVisible();
        homePage.clickWomenCategory();
        homePage.clickWomenTopsSubCategory();
        homePage.validateCategoryPageTitle("Women - Tops Products");
        homePage.clickMenCategory();
        homePage.clickMenTshirtsSubCategory();
        homePage.validateCategoryPageTitle("Men - Tshirts Products");
    }

    @Test(priority = 19, description = "Test Case 19: View & Cart Brand Products")
    public void tc_19_ViewAndCartBrandProducts(){
        homePage.navigateToProducts();
        productsPage.validateBrandsSidebarVisible();
        productsPage.clickBrandPolo();
        productsPage.validateBrandPageTitle("Brand - Polo Products");
        productsPage.validateProductListVisible();
        productsPage.clickBrandMadame();
        productsPage.validateBrandPageTitle("Brand - Madame Products");
        productsPage.validateProductListVisible();
    }

    @Test(priority = 20, description = "Test Case 20: Search Products and Verify Cart After Login")
    public void tc_20_SearchProductsAndVerifyCartAfterLogin(){
        String name = "AutoUser" + generate4Digit();
        String email = createFreshAccountAndLogout(name);

        homePage.navigateToProducts();
        productsPage.validateAllProductsPageVisible();
        productsPage.searchProduct(Constants.SEARCH_PRODUCT);
        productsPage.validateSearchedProductsVisible();
        productsPage.validateSearchResultsRelatedTo(Constants.SEARCH_PRODUCT);

        int searchedCount = productsPage.getVisibleProductCount();
        for(int i = 1; i <= searchedCount; i++){
            productsPage.addProductToCartByIndex(i);
            productsPage.clickContinueShopping();
        }

        homePage.navigateToCart();
        cartPage.validateCartPageVisible();
        cartPage.validateCartItemCount(searchedCount);

        homePage.navigateToSignupLogin();
        loginPage.login(email, Constants.TEST_PASSWORD);
        homePage.validateLoggedInAs(name);

        homePage.navigateToCart();
        cartPage.validateCartPageVisible();
        cartPage.validateCartItemCount(searchedCount);

        bestEffortDeleteAccount();
    }

    @Test(priority = 21, description = "Test Case 21: Add review on product")
    public void tc_21_AddReviewOnProduct(){
        homePage.navigateToProducts();
        productsPage.validateAllProductsPageVisible();
        productsPage.clickViewProductOfFirstProduct();
        productDetailsPage.validateWriteYourReviewVisible();
        productDetailsPage.submitReview();
        productDetailsPage.validateReviewSuccess();
    }

    @Test(priority = 22, description = "Test Case 22: Add to cart from Recommended items")
    public void tc_22_AddToCartFromRecommendedItems(){
        homePage.validateHomePageVisible();
        homePage.validateRecommendedItemsVisible();
        String recommendedProductName = homePage.addRecommendedItemToCartAndGetName();
        productsPage.clickViewCartFromModal();
        cartPage.validateCartPageVisible();
        cartPage.validateCartItemCount(1);
        cartPage.validateProductInCart(recommendedProductName);
    }

    @Test(priority = 23, description = "Test Case 23: Verify address details in checkout page")
    public void tc_23_VerifyAddressDetailsInCheckoutPage(){
        homePage.validateHomePageVisible();
        String name = "AutoUser" + generate4Digit();
        createFreshAccountAndStayLoggedIn(name);

        homePage.navigateToProducts();
        productsPage.addProductToCartByIndex(1);
        productsPage.clickContinueShopping();
        homePage.navigateToCart();
        cartPage.validateCartPageVisible();
        cartPage.clickProceedToCheckout();
        checkoutPage.validateAddressDetailsAndReviewOrderVisible();
        checkoutPage.validateAddressesMatchRegistration(name);
        signupPage.deleteAccount();
    }

    @Test(priority = 24, description = "Test Case 24: Download Invoice after purchase order")
    public void tc_24_DownloadInvoiceAfterPurchaseOrder(){
        homePage.validateHomePageVisible();
        cleanDownloadsFolder();

        homePage.navigateToProducts();
        productsPage.addProductToCartByIndex(1);
        productsPage.clickContinueShopping();
        homePage.navigateToCart();
        cartPage.validateCartPageVisible();
        cartPage.clickProceedToCheckout();
        cartPage.clickRegisterLoginFromModal();

        String name = "AutoUser" + generate4Digit();
        String email = generateUniqueEmail();
        signupPage.createAccount(name, email, Constants.TEST_PASSWORD);
        homePage.validateLoggedInAs(name);

        homePage.navigateToCart();
        cartPage.clickProceedToCheckout();
        checkoutPage.validateAddressDetailsAndReviewOrderVisible();
        checkoutPage.enterCommentAndPlaceOrder();
        paymentPage.completePayment();
        paymentPage.validateOrderPlaced();
        paymentPage.downloadInvoice();
        paymentPage.validateInvoiceDownloaded();
        paymentPage.clickContinue();
        signupPage.deleteAccount();
    }

    @Test(priority = 25, description = "Test Case 25: Verify Scroll Up using 'Arrow' button and Scroll Down functionality")
    public void tc_25_VerifyScrollUpUsingArrowButton(){
        homePage.validateHomePageVisible();
        scrollToBottom();
        homePage.validateSubscriptionHeadingVisible();
        homePage.clickScrollUpArrow();
        homePage.validateSliderTextVisibleAtTop();
    }

    @Test(priority = 26, description = "Test Case 26: Verify Scroll Up without 'Arrow' button and Scroll Down functionality")
    public void tc_26_VerifyScrollUpWithoutArrowButton(){
        homePage.validateHomePageVisible();
        scrollToBottom();
        homePage.validateSubscriptionHeadingVisible();
        scrollToTop();
        homePage.validateSliderTextVisibleAtTop();
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result){
        afterMethod(result, browser);
    }

}
