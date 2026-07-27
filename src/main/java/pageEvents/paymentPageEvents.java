package pageEvents;

import org.testng.Assert;

import base.BaseTest;
import pageObjects.paymentPageElements;
import utils.Constants;

public class paymentPageEvents extends BaseTest{

    public void completePayment(){
        // Confirm the payment page is genuinely rendered before entering card
        // details; a vignette can leave us on /payment#google_vignette with the
        // form unrendered. Recover and re-open /payment directly if needed.
        recoverHomeIfVignetteInterrupted(Constants.url);
        if (driver.getCurrentUrl().contains("#google_vignette") || isDocumentScrollLocked()
                || !isPresentAndDisplayed(paymentPageElements.txtNameOnCard)) {
            recoverVignetteScrollLock();
            waitForVignetteRecovery(5);
            driver.navigate().to(Constants.url + "payment");
            waitForVignetteRecovery(3);
        }
        boolean ready = driver.getCurrentUrl().contains("/payment")
                && !driver.getCurrentUrl().contains("#google_vignette")
                && !isDocumentScrollLocked()
                && waitForPresentAndDisplayed(paymentPageElements.txtNameOnCard, 15)
                && isPresentAndDisplayed(paymentPageElements.txtCardNumber)
                && isPresentAndDisplayed(paymentPageElements.btnPayAndConfirm);
        Assert.assertTrue(ready, "Payment page is not ready to accept card details. " + buildPaymentDiagnostics());

        logger.info("Enter payment details: Name on Card, Card Number, CVC, Expiration date");
        sendKeys(paymentPageElements.txtNameOnCard, Constants.CARD_NAME);
        sendKeys(paymentPageElements.txtCardNumber, Constants.CARD_NUMBER);
        sendKeys(paymentPageElements.txtCardCvc, Constants.CARD_CVC);
        sendKeys(paymentPageElements.txtCardExpiryMonth, Constants.CARD_EXPIRY_MONTH);
        sendKeys(paymentPageElements.txtCardExpiryYear, Constants.CARD_EXPIRY_YEAR);
        logger.info("Click 'Pay and Confirm Order' button");
        click(paymentPageElements.btnPayAndConfirm);
    }

    private String buildPaymentDiagnostics(){
        String url = "n/a";
        String title = "n/a";
        String hash = "n/a";
        try { url = driver.getCurrentUrl(); } catch (Exception ignored) { }
        try { title = driver.getTitle(); } catch (Exception ignored) { }
        try { hash = (String) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return location.hash;"); } catch (Exception ignored) { }
        long bodyTop = 0;
        boolean locked = false;
        try { bodyTop = getBodyRectTop(); } catch (Exception ignored) { }
        try { locked = isDocumentScrollLocked(); } catch (Exception ignored) { }
        return "url=" + url + "; hash=" + hash + "; title=" + title
                + "; nameOnCardPresent=" + isPresent(paymentPageElements.txtNameOnCard)
                + "; nameOnCardDisplayed=" + isPresentAndDisplayed(paymentPageElements.txtNameOnCard)
                + "; payButtonPresent=" + isPresent(paymentPageElements.btnPayAndConfirm)
                + "; payButtonDisplayed=" + isPresentAndDisplayed(paymentPageElements.btnPayAndConfirm)
                + "; bodyRectTop=" + bodyTop + "; scrollLocked=" + locked;
    }

    /**
     * The site briefly flashes 'Your order has been placed successfully!' and
     * immediately redirects to the order confirmation page. The stable expected
     * result is the 'Order Placed!' heading with its confirmation text, so that
     * is what is asserted here.
     */
    public void validateOrderPlaced(){
        logger.info("Validate order success: 'Order Placed!' confirmation is visible");
        assertElementIsDisplayed(paymentPageElements.hdrOrderPlaced);
        assertElementIsDisplayed(paymentPageElements.msgOrderConfirmed);
    }

    public void downloadInvoice(){
        logger.info("Click 'Download Invoice' button");
        click(paymentPageElements.btnDownloadInvoice);
    }

    public void validateInvoiceDownloaded(){
        logger.info("Validate invoice is downloaded successfully");
        boolean downloaded = isFileDownloaded(Constants.INVOICE_FILE_NAME, 30);
        Assert.assertTrue(downloaded, "Invoice file was not downloaded to " + DOWNLOAD_DIR + " within the timeout.");
    }

    public void clickContinue(){
        logger.info("Click 'Continue' button");
        click(paymentPageElements.btnContinue);
    }

}
