package pageEvents;

import java.util.Arrays;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import base.BaseTest;
import pageObjects.homePageElements;
import pageObjects.productsPageElements;
import pageObjects.signupPageElements;
import utils.Constants;

public class homePageEvents extends BaseTest{

    public void validateHomePageVisible(){
        logger.info("Validate home page is visible successfully");
        // Recover if a vignette interrupted the arrival on home (no-op otherwise).
        recoverHomeIfVignetteInterrupted(Constants.url);
        handleOptionalConsent();

        // Primary signal: the Features Items heading.
        if (waitForPresentAndDisplayed(homePageElements.hdrFeaturesItems, 20)) {
            return;
        }
        // Recover once more, then confirm home via corroborating stable signals so
        // an advertisement-only page is never accepted as the home page.
        recoverHomeIfVignetteInterrupted(Constants.url);
        handleOptionalConsent();
        if (waitForPresentAndDisplayed(homePageElements.hdrFeaturesItems, 10)) {
            return;
        }
        boolean onSite = waitForUrlContains("automationexercise.com", 5);
        boolean homeSignal = waitForAnyPresentAndDisplayed(Arrays.asList(
                homePageElements.hdrSliderText,
                homePageElements.lnkSignupLogin,
                homePageElements.lnkLoggedInAs), 10);
        if (onSite && homeSignal) {
            return;
        }
        throw new AssertionError("Home page is not visible. " + buildAccountNavDiagnostics(""));
    }

    public void navigateToSignupLogin(){
        logger.info("Click 'Signup / Login' button");
        clickAndNavigateWithAdFallback(homePageElements.lnkSignupLogin, "/login", Constants.url + "login");
    }

    public void navigateToProducts(){
        logger.info("Click 'Products' button");
        clickAndNavigateWithAdFallback(homePageElements.lnkProducts, "/products", Constants.url + "products");
    }

    public void navigateToCart(){
        logger.info("Click 'Cart' button");
        clickAndNavigateWithAdFallback(homePageElements.lnkCart, "/view_cart", Constants.url + "view_cart");
    }

    public void navigateToContactUs(){
        logger.info("Click 'Contact Us' button");
        clickAndNavigateWithAdFallback(homePageElements.lnkContactUs, "/contact_us", Constants.url + "contact_us");
    }

    public void navigateToTestCases(){
        logger.info("Click 'Test Cases' button");
        clickAndNavigateWithAdFallback(homePageElements.lnkTestCases, "/test_cases", Constants.url + "test_cases");
    }

    public void validateLoggedInAs(String userName){
        logger.info("Validate 'Logged in as " + userName + "' is visible");
        // A vignette can interrupt the return to the home page after Continue;
        // recover and return to home (session preserved) before locating the link.
        recoverHomeIfVignetteInterrupted(Constants.url);
        handleOptionalConsent();
        if (!waitForPresentAndDisplayed(homePageElements.lnkLoggedInAs, 20)) {
            // One more recovery attempt, then fail with full diagnostics.
            recoverHomeIfVignetteInterrupted(Constants.url);
            handleOptionalConsent();
            if (!waitForPresentAndDisplayed(homePageElements.lnkLoggedInAs, 15)) {
                throw new AssertionError("'Logged in as' link not visible after account confirmation. "
                        + buildAccountNavDiagnostics(userName));
            }
        }
        assertElementIsDisplayed(homePageElements.lnkLoggedInAs);
        assertTextContains(homePageElements.lnkLoggedInAs, userName);
    }

    /**
     * Diagnostics for post-account-confirmation navigation failures.
     */
    private String buildAccountNavDiagnostics(String userName){
        String url = "n/a";
        String title = "n/a";
        String hash = "n/a";
        try { url = driver.getCurrentUrl(); } catch (Exception ignored) { }
        try { title = driver.getTitle(); } catch (Exception ignored) { }
        try { hash = (String) ((JavascriptExecutor) driver).executeScript("return location.hash;"); } catch (Exception ignored) { }
        long bodyTop = 0;
        boolean locked = false;
        try { bodyTop = getBodyRectTop(); } catch (Exception ignored) { }
        try { locked = isDocumentScrollLocked(); } catch (Exception ignored) { }
        return "userName=" + userName + "; url=" + url + "; hash=" + hash + "; title=" + title
                + "; accountCreatedVisible=" + isPresent(signupPageElements.hdrAccountCreated)
                + "; continueVisible=" + isPresent(signupPageElements.btnContinue)
                + "; logoutPresent=" + isPresent(homePageElements.lnkLogout)
                + "; deleteAccountPresent=" + isPresent(homePageElements.lnkDeleteAccount)
                + "; loggedInAsPresent=" + isPresent(homePageElements.lnkLoggedInAs)
                + "; bodyRectTop=" + bodyTop + "; scrollLocked=" + locked;
    }

    public void clickLogout(){
        logger.info("Click 'Logout' button");
        click(homePageElements.lnkLogout);
    }

    public void clickDeleteAccount(){
        logger.info("Click 'Delete Account' button");
        click(homePageElements.lnkDeleteAccount);
    }

    public void clickFirstViewProductOnHome(){
        logger.info("Click 'View Product' for the first product on home page");
        click(homePageElements.lnkFirstViewProductHome);
    }

    //Footer subscription (shared by Test Cases 10 and 11 - same footer on every page)
    public void scrollToFooter(){
        logger.info("Scroll down to footer");
        scrollToBottom();
    }

    public void validateSubscriptionHeadingVisible(){
        logger.info("Validate text 'SUBSCRIPTION' is visible");
        assertElementIsDisplayed(homePageElements.hdrSubscription);
    }

    public void subscribeWithEmail(String email){
        logger.info("Enter email address in subscription input and click arrow button");
        scrollToElement(homePageElements.txtSubscribeEmail);
        clear(homePageElements.txtSubscribeEmail);
        sendKeys(homePageElements.txtSubscribeEmail, email);
        click(homePageElements.btnSubscribe);
    }

    public void validateSubscriptionSuccess(){
        logger.info("Validate success message 'You have been successfully subscribed!' is visible");
        assertElementIsDisplayed(homePageElements.msgSubscribeSuccess);
    }

    //Scroll functionality (Test Cases 25 and 26)
    public void clickScrollUpArrow(){
        logger.info("Click on arrow at bottom right side to move upward");
        scrollUpStartPageYOffset = getVerticalScrollPosition();
        scrollUpArrowClickCount = 0;
        scrollUpOverlayFound = false;
        scrollUpOverlayCloseAttempted = false;

        // First genuine arrow click (real Selenium click, then MouseEvent dispatch).
        clickScrollUpArrowWithFallback(homePageElements.btnScrollUpArrow);
        scrollUpArrowClickCount++;

        // The Google vignette applies its scroll lock ASYNCHRONOUSLY after the
        // click, so a single immediate check misses it. Poll for up to 10s for
        // either genuine rendered-top success or a vignette scroll lock. A bare
        // pageYOffset of 0 is never treated as success on its own.
        int status = pollForRenderedTopOrScrollLock(homePageElements.hdrSliderActiveHeading, 10);

        if (status == 2) {
            // Advertisement scroll lock: recover (does not depend on a Close
            // button), restore the bottom start state, and click the REAL arrow
            // again so the final upward movement still comes from #scrollUp.
            // Allow only ONE recovery / a maximum of TWO genuine arrow clicks.
            scrollUpOverlayFound = true;
            logger.info("Advertisement scroll lock detected after arrow click; recovering. " + buildScrollDiagnostics());
            recoverVignetteScrollLock();
            waitForVignetteRecovery(5);
            scrollToBottom();
            validateSubscriptionHeadingVisible();
            clickScrollUpArrowWithFallback(homePageElements.btnScrollUpArrow);
            scrollUpArrowClickCount++;
            pollForRenderedTopOrScrollLock(homePageElements.hdrSliderActiveHeading, 10);
        }
    }

    /**
     * Verifies the page returned near the top and the slider heading is visible
     * in the viewport WITHOUT scrolling the element into view (which would create
     * a false pass). Used by Test Cases 25 and 26.
     */
    public void validateSliderTextVisibleAtTop(){
        logger.info("Wait until the page is scrolled up (pageYOffset near zero)");
        waitUntilPageNearTop(5, 15);
        assertPageIsNearTop(5);
        logger.info("Validate 'Full-Fledged practice website for Automation Engineers' text is visible on screen in the active slide without scrolling");
        assertActiveSliderHeadingVisibleOnScreen(homePageElements.hdrSliderActiveHeading, homePageElements.hdrSliderText);
    }

    //Category side bar (Test Case 18)
    public void validateCategorySidebarVisible(){
        logger.info("Validate categories are visible on left side bar");
        assertElementIsDisplayed(homePageElements.hdrCategory);
    }

    public void clickWomenCategory(){
        logger.info("Click on 'Women' category");
        click(homePageElements.lnkWomenCategory);
    }

    public void clickWomenTopsSubCategory(){
        logger.info("Click on 'Tops' sub-category under 'Women' category");
        clickAndNavigateWithAdFallback(homePageElements.lnkWomenTopsSubCategory, "/category_products/2", Constants.url + "category_products/2");
    }

    public void clickMenCategory(){
        logger.info("Click on 'Men' category");
        click(homePageElements.lnkMenCategory);
    }

    public void clickMenTshirtsSubCategory(){
        logger.info("Click on 'Tshirts' sub-category under 'Men' category");
        clickAndNavigateWithAdFallback(homePageElements.lnkMenTshirtsSubCategory, "/category_products/3", Constants.url + "category_products/3");
    }

    public void validateCategoryPageTitle(String expectedTitle){
        logger.info("Validate category page title contains: " + expectedTitle);
        assertTextContains(productsPageElements.hdrTitleCenter, expectedTitle);
    }

    //Recommended items (Test Case 22)
    public void validateRecommendedItemsVisible(){
        logger.info("Validate 'RECOMMENDED ITEMS' are visible");
        scrollToBottom();
        assertElementIsDisplayed(homePageElements.hdrRecommendedItems);
    }

    /**
     * Adds the active recommended item to the cart and returns its exact product
     * name, so Test Case 22 can confirm the same product appears in the cart.
     */
    /**
     * Adds a recommended item to the cart and returns its exact product name.
     * The name and the Add To Cart button are resolved from the SAME product card
     * inside the active carousel slide, so a rotating carousel can never cause the
     * captured name and the clicked button to come from different products.
     */
    public String addRecommendedItemToCartAndGetName(){
        logger.info("Add a recommended item from a single active-slide card and capture its exact name");
        scrollToElement(homePageElements.hdrRecommendedItems);
        int attempts = 0;
        while (attempts < 3) {
            attempts++;
            try {
                WebElement activeSlide = waitForElementVisible(homePageElements.secRecommendedActiveSlide, 15);
                java.util.List<WebElement> cards = activeSlide.findElements(By.xpath(
                        ".//div[contains(concat(' ', normalize-space(@class), ' '), ' product-image-wrapper ')]"));
                if (cards.isEmpty()) {
                    cards = activeSlide.findElements(By.xpath(
                            ".//div[contains(concat(' ', normalize-space(@class), ' '), ' col-sm-4 ')]"));
                }
                WebElement card = null;
                for (WebElement c : cards) {
                    try {
                        if (c.isDisplayed()) { card = c; break; }
                    } catch (StaleElementReferenceException ignored) {
                        // rotated; will retry outer loop
                    }
                }
                if (card == null) {
                    throw new NoSuchElementException("No visible recommended product card in the active slide");
                }
                String productName = normalizeText(card.findElement(By.xpath(
                        ".//div[contains(concat(' ', normalize-space(@class), ' '), ' productinfo ')]//p")).getText());
                WebElement addButton = card.findElement(By.xpath(
                        ".//a[contains(concat(' ', normalize-space(@class), ' '), ' add-to-cart ')]"));
                logger.info("Recommended product (same card): " + productName);
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", addButton);
                try {
                    addButton.click();
                } catch (Exception clickError) {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].dispatchEvent(new MouseEvent('click', {view: window, bubbles: true, cancelable: true}));",
                            addButton);
                }
                // Wait for the Added! modal so the subsequent View Cart click is stable.
                waitForPresentAndDisplayed(productsPageElements.lnkViewCartModal, 10);
                return productName;
            } catch (StaleElementReferenceException se) {
                // Carousel rotated mid-read; reacquire the active slide and retry.
            }
        }
        throw new AssertionError("Could not add a recommended item from a single card after retries.");
    }

}
