package pageEvents;

import org.testng.Assert;

import base.BaseTest;
import pageObjects.productsPageElements;
import utils.Constants;

public class productsPageEvents extends BaseTest{

    public void validateAllProductsPageVisible(){
        logger.info("Validate user is navigated to ALL PRODUCTS page successfully");
        assertElementIsDisplayed(productsPageElements.hdrAllProducts);
    }

    public void validateProductListVisible(){
        logger.info("Validate the products list is visible");
        int productCount = ele.getXPATHWebElements(productsPageElements.secProductList).size();
        Assert.assertTrue(productCount > 0, "Expected at least one product in the products list but found " + productCount);
    }

    public void clickViewProductOfFirstProduct(){
        logger.info("Click on 'View Product' of first product");
        scrollToElement(productsPageElements.lnkViewProductFirst);
        click(productsPageElements.lnkViewProductFirst);
    }

    public void searchProduct(String productName){
        logger.info("Enter product name '" + productName + "' in search input and click search button");
        clear(productsPageElements.txtSearchProduct);
        sendKeys(productsPageElements.txtSearchProduct, productName);
        click(productsPageElements.btnSubmitSearch);
    }

    public void validateSearchedProductsVisible(){
        logger.info("Validate 'SEARCHED PRODUCTS' is visible");
        assertElementIsDisplayed(productsPageElements.hdrSearchedProducts);
    }

    public java.util.List<String> getVisibleProductNames(){
        java.util.List<org.openqa.selenium.WebElement> nameElements =
                ele.getXPATHWebElements(productsPageElements.secProductNames);
        java.util.List<String> names = new java.util.ArrayList<>();
        for(org.openqa.selenium.WebElement e : nameElements){
            try {
                if(!e.isDisplayed()){
                    continue;
                }
                String text = normalizeText(e.getText());
                if(!text.isEmpty()){
                    names.add(text);
                }
            } catch (Exception ignored) {
                // stale/hidden element; skip
            }
        }
        return names;
    }

    public void validateSearchResultsRelatedTo(String productName){
        logger.info("Validate all the products related to search are visible");
        // Wait for the searched products heading and at least one product wrapper.
        waitForPresentAndDisplayed(productsPageElements.hdrSearchedProducts, 15);
        waitForPresentAndDisplayed(productsPageElements.secProductList, 15);

        // Wait for at least one displayed, non-empty product-name element to render.
        java.util.List<String> names = new java.util.ArrayList<>();
        long deadline = System.currentTimeMillis() + 15000;
        while(System.currentTimeMillis() < deadline){
            names = getVisibleProductNames();
            if(!names.isEmpty()){
                break;
            }
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        int wrappers = ele.getXPATHWebElements(productsPageElements.secProductList).size();
        Assert.assertTrue(wrappers > 0,
                "Expected search results for '" + productName + "' but found no product wrappers. " + buildSearchDiagnostics());
        Assert.assertTrue(!names.isEmpty(),
                "Expected at least one named search result for '" + productName + "'. " + buildSearchDiagnostics());

        // Every displayed searched-product name must relate to the search term.
        String expected = normalizeText(productName).toLowerCase();
        for(String name : names){
            logger.info("Searched product found: " + name);
            Assert.assertTrue(name.toLowerCase().contains(expected),
                    "Searched product '" + name + "' does not relate to search term '" + productName + "'");
        }
    }

    private String buildSearchDiagnostics(){
        String url = "n/a";
        int wrappers = 0;
        int productinfos = 0;
        int nameP = 0;
        int displayedNames = 0;
        try { url = driver.getCurrentUrl(); } catch (Exception ignored) { }
        try { wrappers = ele.getXPATHWebElements(productsPageElements.secProductList).size(); } catch (Exception ignored) { }
        try {
            productinfos = ele.getXPATHWebElements(
                    "//div[contains(concat(' ', normalize-space(@class), ' '), ' productinfo ')]").size();
        } catch (Exception ignored) { }
        try {
            java.util.List<org.openqa.selenium.WebElement> ps =
                    ele.getXPATHWebElements(productsPageElements.secProductNames);
            nameP = ps.size();
            for(org.openqa.selenium.WebElement e : ps){
                try {
                    if(e.isDisplayed() && !e.getText().trim().isEmpty()){
                        displayedNames++;
                    }
                } catch (Exception ignored) { }
            }
        } catch (Exception ignored) { }
        return "url=" + url + "; productWrappers=" + wrappers + "; productinfoElements=" + productinfos
                + "; nameParagraphs=" + nameP + "; displayedNonEmptyNames=" + displayedNames;
    }

    public int getVisibleProductCount(){
        return ele.getXPATHWebElements(productsPageElements.secProductList).size();
    }

    /**
     * Hovers over the product at the given 1-based index and clicks the
     * 'Add to cart' button inside the hover overlay, following the official
     * Test Case 12 steps.
     */
    public void addProductToCartByIndex(int index){
        logger.info("Hover over product " + index + " and click 'Add to cart'");
        String productWrapper = productsPageElements.secProductWrapperIndexed + "[" + index + "]";
        String overlayAddToCart = productWrapper + productsPageElements.secOverlayAddToCartSuffix;
        hoverOverElement(productWrapper);
        click(overlayAddToCart);
    }

    public void clickContinueShopping(){
        logger.info("Click 'Continue Shopping' button");
        click(productsPageElements.btnContinueShopping);
    }

    public void clickViewCartFromModal(){
        logger.info("Click 'View Cart' button");
        click(productsPageElements.lnkViewCartModal);
    }

    //Brands (Test Case 19)
    public void validateBrandsSidebarVisible(){
        logger.info("Validate Brands are visible on left side bar");
        // Recover if a vignette interrupted arrival on the Products page; if the
        // URL still carries #google_vignette or is scroll-locked, re-open /products.
        recoverHomeIfVignetteInterrupted(Constants.url);
        if (driver.getCurrentUrl().contains("#google_vignette") || isDocumentScrollLocked()
                || !driver.getCurrentUrl().contains("/products")) {
            recoverVignetteScrollLock();
            waitForVignetteRecovery(5);
            driver.navigate().to(Constants.url + "products");
            waitForVignetteRecovery(3);
        }
        // The official requirement is that Brands are visible on the left sidebar.
        // The rendered DOM can position the "Brands" heading outside the
        // brands_products container, so proof of visibility is the sidebar
        // container plus at least one displayed brand link (Polo/Madame included).
        boolean sidebarContainerReady = waitForPresentAndDisplayed(productsPageElements.secBrandsSidebar, 20);
        boolean visibleBrandLinkReady = waitUntilAtLeastOneDisplayed(productsPageElements.secBrandLinks, 20);
        Assert.assertTrue(sidebarContainerReady && visibleBrandLinkReady,
                "Brands sidebar is not visible. " + buildBrandsDiagnostics());
    }

    private String buildBrandsDiagnostics(){
        String url = "n/a";
        String title = "n/a";
        String hash = "n/a";
        try { url = driver.getCurrentUrl(); } catch (Exception ignored) { }
        try { title = driver.getTitle(); } catch (Exception ignored) { }
        try { hash = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return location.hash;"); } catch (Exception ignored) { }
        int brandContainers = 0;
        int brandLinks = 0;
        int h2count = 0;
        try { brandContainers = ele.getXPATHWebElements("//div[contains(concat(' ', normalize-space(@class), ' '), ' brands_products ')]").size(); } catch (Exception ignored) { }
        try { brandLinks = ele.getXPATHWebElements(productsPageElements.secBrandLinks).size(); } catch (Exception ignored) { }
        try { h2count = ele.getXPATHWebElements("//h2").size(); } catch (Exception ignored) { }
        long bodyTop = 0;
        boolean locked = false;
        try { bodyTop = getBodyRectTop(); } catch (Exception ignored) { }
        try { locked = isDocumentScrollLocked(); } catch (Exception ignored) { }
        return "url=" + url + "; hash=" + hash + "; title=" + title
                + "; allProductsDisplayed=" + isPresentAndDisplayed(productsPageElements.hdrAllProducts)
                + "; brandsProductsContainers=" + brandContainers
                + "; h2Count=" + h2count
                + "; brandLinks=" + brandLinks
                + "; poloPresent=" + isPresent(productsPageElements.lnkBrandPolo)
                + "; poloDisplayed=" + isPresentAndDisplayed(productsPageElements.lnkBrandPolo)
                + "; madamePresent=" + isPresent(productsPageElements.lnkBrandMadame)
                + "; madameDisplayed=" + isPresentAndDisplayed(productsPageElements.lnkBrandMadame)
                + "; bodyRectTop=" + bodyTop + "; scrollLocked=" + locked;
    }

    public void clickBrandPolo(){
        logger.info("Click on 'Polo' brand name");
        clickAndNavigateWithAdFallback(productsPageElements.lnkBrandPolo, "/brand_products/Polo", Constants.url + "brand_products/Polo", productsPageElements.secProductList);
    }

    public void clickBrandMadame(){
        logger.info("Click on 'Madame' brand link");
        clickAndNavigateWithAdFallback(productsPageElements.lnkBrandMadame, "/brand_products/Madame", Constants.url + "brand_products/Madame", productsPageElements.secProductList);
    }

    public void validateBrandPageTitle(String expectedTitle){
        logger.info("Validate brand page title contains: " + expectedTitle);
        assertTextContains(productsPageElements.hdrTitleCenter, expectedTitle);
    }

}
