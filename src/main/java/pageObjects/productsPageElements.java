package pageObjects;

public interface productsPageElements {

    //All Products / Searched Products headings
    String hdrAllProducts = "//h2[contains(text(),'All Products')]";
    String hdrSearchedProducts = "//h2[contains(text(),'Searched Products')]";
    String hdrTitleCenter = "//h2[@class='title text-center']";

    //Search
    String txtSearchProduct = "//input[@id='search_product']";
    String btnSubmitSearch = "//button[@id='submit_search']";

    //Product grid
    String secProductList = "//div[@class='features_items']//div[@class='product-image-wrapper']";
    String secProductNames = "//div[contains(concat(' ', normalize-space(@class), ' '), ' features_items ')]//div[contains(concat(' ', normalize-space(@class), ' '), ' productinfo ')]//p[normalize-space()]";
    String lnkViewProductFirst = "//div[@class='features_items']//a[@href='/product_details/1']";
    //Indexed pieces used to hover a product and click its overlay Add to cart.
    //Usage: (prefix)[index] and (prefix)[index] + overlay suffix
    String secProductWrapperIndexed = "(//div[@class='features_items']//div[@class='product-image-wrapper'])";
    String secOverlayAddToCartSuffix = "//div[@class='overlay-content']//a[contains(@class,'add-to-cart')]";

    //Add-to-cart modal
    String hdrModalAdded = "//h4[contains(text(),'Added!')]";
    String btnContinueShopping = "//button[contains(text(),'Continue Shopping')]";
    String lnkViewCartModal = "//u[text()='View Cart']";

    //Brands side bar
    String hdrBrands = "//div[contains(concat(' ', normalize-space(@class), ' '), ' brands_products ')]//h2[translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')='BRANDS']";
    String lnkBrandPolo = "//div[contains(concat(' ', normalize-space(@class), ' '), ' brands_products ')]//a[contains(@href,'/brand_products/Polo')]";
    String lnkBrandMadame = "//div[contains(concat(' ', normalize-space(@class), ' '), ' brands_products ')]//a[contains(@href,'/brand_products/Madame')]";
    String secBrandLinks = "//div[contains(concat(' ', normalize-space(@class), ' '), ' brands_products ')]//a[contains(@href,'/brand_products/')]";
    String secBrandsSidebar = "//div[contains(concat(' ', normalize-space(@class), ' '), ' brands_products ')]";

}
