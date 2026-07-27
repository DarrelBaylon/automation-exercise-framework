package utils;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import base.BaseTest;

public class ElementFetch {

	public WebElement getXPATHWebElement(String identifierValue) {

		return BaseTest.driver.findElement(By.xpath(identifierValue));
	}

	public List<WebElement> getXPATHWebElements(String identifierValue) {

		return BaseTest.driver.findElements(By.xpath(identifierValue));
	}

}
