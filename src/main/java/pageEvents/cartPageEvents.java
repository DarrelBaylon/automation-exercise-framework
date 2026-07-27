package pageEvents;

import org.testng.Assert;

import base.BaseTest;
import pageObjects.cartPageElements;

public class cartPageEvents extends BaseTest{

    public void validateCartPageVisible(){
        logger.info("Validate that cart page is displayed");
        assertElementIsDisplayed(cartPageElements.hdrShoppingCartBreadcrumb);
    }

    public int getCartRowCount(){
        return ele.getXPATHWebElements(cartPageElements.secCartRows).size();
    }

    public void validateCartItemCount(int expectedCount){
        logger.info("Validate cart contains " + expectedCount + " product(s)");
        int actualCount = getCartRowCount();
        Assert.assertEquals(actualCount, expectedCount,
                "Cart item count is incorrect. Expected " + expectedCount + " but found " + actualCount);
    }

    public void validateCartRowDetails(int rowIndex, String expectedPrice, String expectedQuantity, String expectedTotal){
        logger.info("Validate price, quantity and total of cart row " + rowIndex);
        String row = cartPageElements.secCartRowIndexed + "[" + rowIndex + "]";
        String actualPrice = getText(row + cartPageElements.txtCartPriceSuffix);
        String actualQuantity = getText(row + cartPageElements.txtCartQuantitySuffix);
        String actualTotal = getText(row + cartPageElements.txtCartTotalSuffix);
        Assert.assertEquals(actualPrice, expectedPrice, "Cart row " + rowIndex + " price is incorrect.");
        Assert.assertEquals(actualQuantity, expectedQuantity, "Cart row " + rowIndex + " quantity is incorrect.");
        Assert.assertEquals(actualTotal, expectedTotal, "Cart row " + rowIndex + " total price is incorrect.");
    }

    public String getCartRowPrice(int rowIndex){
        String row = cartPageElements.secCartRowIndexed + "[" + rowIndex + "]";
        return getText(row + cartPageElements.txtCartPriceSuffix);
    }

    public String getCartRowQuantity(int rowIndex){
        String row = cartPageElements.secCartRowIndexed + "[" + rowIndex + "]";
        return getText(row + cartPageElements.txtCartQuantitySuffix);
    }

    public void validateCartRowQuantity(int rowIndex, String expectedQuantity){
        logger.info("Validate product in cart row " + rowIndex + " has quantity " + expectedQuantity);
        String actualQuantity = getCartRowQuantity(rowIndex);
        Assert.assertEquals(actualQuantity, expectedQuantity,
                "Cart quantity is incorrect. Expected " + expectedQuantity + " but found " + actualQuantity);
    }

    public void removeFirstProductFromCart(){
        logger.info("Click 'X' button corresponding to the product");
        String deleteButton = cartPageElements.secCartRowIndexed + "[1]" + cartPageElements.btnCartDeleteSuffix;
        click(deleteButton);
    }

    public void validateCartIsEmpty(){
        logger.info("Validate that product is removed from the cart");
        assertElementIsDisplayed(cartPageElements.msgCartEmpty);
    }

    public java.util.List<String> getCartProductNames(){
        java.util.List<org.openqa.selenium.WebElement> nameElements =
                ele.getXPATHWebElements(cartPageElements.secCartDescriptions);
        java.util.List<String> names = new java.util.ArrayList<>();
        for(org.openqa.selenium.WebElement e : nameElements){
            names.add(normalizeText(e.getText()));
        }
        return names;
    }

    public void validateProductInCart(String expectedProductName){
        logger.info("Validate that product '" + expectedProductName + "' was added to the cart page");
        java.util.List<String> names = getCartProductNames();
        String expected = normalizeText(expectedProductName).toLowerCase();
        boolean found = false;
        for(String name : names){
            logger.info("Cart product found: " + name);
            if(name.toLowerCase().contains(expected)){
                found = true;
            }
        }
        Assert.assertTrue(found,
                "Expected product '" + expectedProductName + "' in the cart but it was not found. Cart contained: " + names);
    }

    public void clickProceedToCheckout(){
        logger.info("Click 'Proceed To Checkout' button");
        click(cartPageElements.btnProceedToCheckout);
    }

    public void clickRegisterLoginFromModal(){
        logger.info("Click 'Register / Login' button on checkout modal");
        click(cartPageElements.lnkRegisterLoginModal);
    }

}
