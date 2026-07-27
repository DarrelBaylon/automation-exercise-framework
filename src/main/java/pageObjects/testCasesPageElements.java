package pageObjects;

public interface testCasesPageElements {

    String hdrTestCases = "//h2[contains(@class,'title') and contains(translate(normalize-space(.),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'TEST CASES')]";
    String secTestCaseList = "//div[@class='panel-group']";

}
