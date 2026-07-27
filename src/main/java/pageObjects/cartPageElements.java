package pageObjects;

public interface cartPageElements {

    String hdrShoppingCartBreadcrumb = "//ol[@class='breadcrumb']//li[text()='Shopping Cart']";
    String tblCartInfo = "//table[@id='cart_info_table']";
    String secCartRows = "//table[@id='cart_info_table']//tbody//tr[contains(@id,'product')]";

    //Indexed row pieces. Usage: (secCartRowIndexed)[index] + suffix
    String secCartRowIndexed = "(//table[@id='cart_info_table']//tbody//tr[contains(@id,'product')])";
    String txtCartDescriptionSuffix = "//td[@class='cart_description']//a";
    String txtCartPriceSuffix = "//td[@class='cart_price']//p";
    String txtCartQuantitySuffix = "//td[@class='cart_quantity']//button";
    String txtCartTotalSuffix = "//td[@class='cart_total']//p";
    String btnCartDeleteSuffix = "//a[@class='cart_quantity_delete']";
    String secCartDescriptions = "//table[@id='cart_info_table']//td[@class='cart_description']//a";

    String msgCartEmpty = "//span[@id='empty_cart']";
    String btnProceedToCheckout = "//a[contains(@class,'check_out')]";
    String lnkRegisterLoginModal = "//u[text()='Register / Login']";

}
