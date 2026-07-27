package pageEvents;

import base.BaseTest;
import pageObjects.testCasesPageElements;

public class testCasesPageEvents extends BaseTest{

    public void validateTestCasesPageVisible(){
        logger.info("Validate user is navigated to test cases page successfully");
        org.testng.Assert.assertTrue(waitForUrlContains("/test_cases", 15),
                "Expected the URL to contain '/test_cases' but it was: " + driver.getCurrentUrl());
        assertElementIsDisplayed(testCasesPageElements.hdrTestCases);
        assertElementIsDisplayed(testCasesPageElements.secTestCaseList);
    }

}
