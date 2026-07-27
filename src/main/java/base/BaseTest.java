package base;

import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.StaleElementReferenceException;
import org.testng.ITestResult;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.Constants;
import utils.ElementFetch;

public class BaseTest {
	public static WebDriver driver;
	public static ExtentSparkReporter sparkReporter;
	public static ExtentReports extent;
	public static ExtentTest logger;
	public ElementFetch ele = new ElementFetch();

	public static final String DOWNLOAD_DIR = System.getProperty("user.dir") + File.separator + "downloads";

	// Scroll-up (Test Case 25) orchestration state, surfaced in failure diagnostics.
	public long scrollUpStartPageYOffset = -1;
	public int scrollUpArrowClickCount = 0;
	public boolean scrollUpOverlayFound = false;
	public boolean scrollUpOverlayCloseAttempted = false;

	public void beforeTestMethod(String browser) {
		String reportname = "REGRESSION_" + browser.toUpperCase();
		reportname = reportname.replace("-HEADLESS", "");
		String reportPath = System.getProperty("user.dir") + File.separator + "Reports" + File.separator + reportname + File.separator + reportname + "_TESTING.html";
		sparkReporter = new ExtentSparkReporter(reportPath);
		extent = new ExtentReports();
		extent.attachReporter(sparkReporter);
		sparkReporter.config().setTheme(Theme.DARK);
		extent.setSystemInfo("Browser", browser);
		extent.setSystemInfo("Execution Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		sparkReporter.config().setDocumentTitle("Automation Report");
		sparkReporter.config().setReportName(reportname);
	}

	public void afterMethod(ITestResult result, String browser) {
		if(result.getStatus() == ITestResult.FAILURE) {
			logger.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " - Test Case Failed", ExtentColor.RED));
			logger.log(Status.FAIL, MarkupHelper.createLabel(result.getThrowable() + " - Test Case Failed", ExtentColor.RED));
		}else if(result.getStatus() == ITestResult.SKIP) {
			logger.log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " - Test Case Skip", ExtentColor.ORANGE));
		}else if(result.getStatus() == ITestResult.SUCCESS) {
			logger.log(Status.PASS, MarkupHelper.createLabel(result.getName() + " - Test Case Passed", ExtentColor.GREEN));
		}

		try {
			if (driver != null) {
				String testName = result.getName();
				if (testName == null || testName.isEmpty()) {
					testName = result.getMethod().getMethodName();
				}
				System.out.println("Capturing screenshot for test: " + testName);
				String reportname = "REGRESSION_" + browser.toUpperCase();
				reportname = reportname.replace("-HEADLESS", "");
				captureScreenshot(testName, reportname);
			}
		} catch (NoSuchSessionException e) {
			System.err.println("No active session to capture screenshot for test: " + result.getName() + ". Error: " + e.getMessage());
		} finally {
			if (driver != null) {
				driver.quit();
			}
			extent.flush();
		}
	}

	@AfterTest
	public void afterTest() {
		extent.flush();
	}

	public void setupDriver(String browser) {
		switch(browser) {
		case "chrome":
			ChromeOptions options = new ChromeOptions();
			options.addArguments("window-size=1920,1080");
			options.addArguments("--disable-gpu");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--disable-site-isolation-trials");
			options.addArguments("--lang=en");
			options.addArguments("--disable-notifications");
			options.addArguments("disable-infobars");
			options.addArguments("--disable-extensions");
			options.setCapability("acceptInsecureCerts", true);
			options.setExperimentalOption("prefs", downloadPreferences());
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(options);
			break;

		case "chrome-headless":
			options = new ChromeOptions();
			options.addArguments("--headless=new");
			options.addArguments("window-size=1920,1080");
			options.addArguments("--disable-gpu");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");
			options.addArguments("--disable-site-isolation-trials");
			options.addArguments("--lang=en");
			options.addArguments("--disable-notifications");
			options.addArguments("disable-infobars");
			options.addArguments("--disable-extensions");
			options.setCapability("acceptInsecureCerts", true);
			options.setExperimentalOption("prefs", downloadPreferences());
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(options);
			break;

		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			break;

		case "edge":
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			break;

		default:
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			break;
		}

	}

	/**
	 * Chrome preferences so that Test Case 24 downloads the invoice into a
	 * predictable, project-local folder without a save dialog.
	 */
	private Map<String, Object> downloadPreferences() {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", DOWNLOAD_DIR);
		prefs.put("download.prompt_for_download", false);
		prefs.put("download.directory_upgrade", true);
		prefs.put("safebrowsing.enabled", true);
		return prefs;
	}

	public void captureScreenshot(String screenshotName, String reportname) {
		String timestamp = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss_SSS").format(new Date());
		// Make the file name safe for every operating system
		String safeName = screenshotName.replaceAll("[^a-zA-Z0-9_-]", "_");
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

		try {
			String baseDir = System.getProperty("user.dir") + File.separator + "Reports" + File.separator + reportname + File.separator + "img-src";

			File screenshotDir = new File(baseDir);
			if (!screenshotDir.exists()) {
				screenshotDir.mkdirs();
			}

			File destFile = new File(screenshotDir, safeName + "_" + timestamp + ".png");
			FileUtils.copyFile(srcFile, destFile);
			System.out.println("Screenshot saved to: " + destFile.getAbsolutePath());

			String relativeImagePath = "." + File.separator + "img-src" + File.separator + safeName + "_" + timestamp + ".png";
			logger.info("Screenshot: " + safeName, MediaEntityBuilder.createScreenCaptureFromPath(relativeImagePath).build());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Parameters({"browser"})
	public void initializeBrowser(String browser, Method testMethod) {
		logger = extent.createTest(testMethod.getName());
		setupDriver(browser);
		driver.manage().window().maximize();
		driver.get(Constants.url);
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		logger.info("URL: " + Constants.url);
		handleOptionalConsent();
	}

	// ---------------------------------------------------------------
	// Shared element actions (reference framework style)
	// ---------------------------------------------------------------

	public void click(String webElement) {
		try {
			waitForElementClickable(webElement, 10);
			ele.getXPATHWebElement(webElement).click();
		} catch (Exception e) {
			// JavaScript fallback for clicks intercepted by ads or overlays.
			// findElement below still fails loudly if the element truly does not exist,
			// so genuine application errors are not hidden.
			WebElement element = driver.findElement(By.xpath(webElement));
			((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
		}
	}

	public void sendKeys(String webElement, String keysToSend) {
		waitForElementVisible(webElement, 10);
		ele.getXPATHWebElement(webElement).sendKeys(keysToSend);
	}

	public void clear(String webElement) {
		ele.getXPATHWebElement(webElement).sendKeys(Keys.CONTROL, "a");
		ele.getXPATHWebElement(webElement).sendKeys(Keys.chord(Keys.DELETE));
	}

	public String getText(String webElement) {
		waitForElementVisible(webElement, 10);
		return ele.getXPATHWebElement(webElement).getText();
	}

	public void selectElementByVisibleText(String webElement, String visibleText) {
		WebElement element = ele.getXPATHWebElement(webElement);
		Select select = new Select(element);
		select.selectByVisibleText(visibleText);
	}

	public void hoverOverElement(String webElement) {
		WebElement element = ele.getXPATHWebElement(webElement);
		scrollToElement(webElement);
		new Actions(driver).moveToElement(element).perform();
	}

	public int generate4Digit() {
		Random rand = new Random();
		int intRandom = rand.nextInt(9000) + 1000;
		return intRandom;
	}

	public String generateUniqueEmail() {
		return "automationuser" + System.currentTimeMillis() + "@example.com";
	}

	// ---------------------------------------------------------------
	// Explicit waits
	// ---------------------------------------------------------------

	public WebElement waitForElementVisible(String webElement, int timeoutSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
		return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(webElement)));
	}

	public WebElement waitForElementClickable(String webElement, int timeoutSeconds) {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
		return wait.until(ExpectedConditions.elementToBeClickable(By.xpath(webElement)));
	}

	// ---------------------------------------------------------------
	// Assertions
	// ---------------------------------------------------------------

	public void assertElementIsDisplayed(String webElement) {
		try {
			WebElement element = driver.findElement(By.xpath(webElement));

			JavascriptExecutor js = (JavascriptExecutor) driver;
			js.executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			wait.until(ExpectedConditions.visibilityOf(element));

			assertTrue(element.isDisplayed(), "The element is not displayed: " + webElement);
		} catch (NoSuchElementException e) {
			throw new AssertionError("Element not found: " + webElement, e);
		} catch (TimeoutException e) {
			throw new AssertionError("Element was not visible within the timeout: " + webElement, e);
		}
	}

	/**
	 * Collapses repeated whitespace to a single space and trims, so comparisons
	 * are not broken by the site rendering headings with irregular spacing
	 * (for example "WOMEN -  Dress PRODUCTS").
	 */
	public String normalizeText(String text) {
		if (text == null) {
			return "";
		}
		return text.replaceAll("\\s+", " ").trim();
	}

	public void assertTextContains(String webElement, String expectedText) {
		String actualText = getText(webElement);
		String normalizedActual = normalizeText(actualText).toLowerCase();
		String normalizedExpected = normalizeText(expectedText).toLowerCase();
		assertTrue(normalizedActual.contains(normalizedExpected),
				"Expected text '" + expectedText + "' but actual text was '" + actualText + "' for element: " + webElement);
	}

	// ---------------------------------------------------------------
	// Scrolling helpers (Test Cases 10, 11, 22, 25, 26)
	// ---------------------------------------------------------------

	public void scrollToElement(String webElement) {
		WebElement element = ele.getXPATHWebElement(webElement);
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'nearest'});", element);
	}

	public void scrollToBottom() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
	}

	public void scrollToTop() {
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
	}

	public long getVerticalScrollPosition() {
		return (long) ((JavascriptExecutor) driver).executeScript("return Math.round(window.pageYOffset);");
	}

	public long getBodyRectTop() {
		Object v = ((JavascriptExecutor) driver).executeScript(
				"return Math.round(document.body.getBoundingClientRect().top);");
		return (v instanceof Number) ? ((Number) v).longValue() : 0L;
	}

	/**
	 * Detects a stale advertisement scroll lock. A Google vignette pins the
	 * document with body/html "position: fixed; top: -NNNNpx", so pageYOffset can
	 * read 0 while the real content is displaced far above the viewport. Returns
	 * true when pageYOffset is near zero but the body/html rectangle is pushed far
	 * up, or when body/html is fixed with a large negative top.
	 */
	public boolean isDocumentScrollLocked() {
		Object v = ((JavascriptExecutor) driver).executeScript(
				"var b=document.body, h=document.documentElement;"
				+ "function pf(x){var n=parseFloat(x);return isNaN(n)?0:n;}"
				+ "var y=window.pageYOffset||0;"
				+ "var bt=Math.round(b.getBoundingClientRect().top);"
				+ "var ht=Math.round(h.getBoundingClientRect().top);"
				+ "var bcs=window.getComputedStyle(b), hcs=window.getComputedStyle(h);"
				+ "var bComputedTop=pf(bcs.top);"
				+ "var bInlineTop=pf(b.style.top);"
				+ "var bpos=bcs.position;"
				+ "var hash=(location.hash||'');"
				+ "var displaced=(bt<-100 || bComputedTop<-100 || bInlineTop<-100);"
				+ "var lockedByOffset=(y<=5 && (bt<-100 || ht<-100 || bComputedTop<-100 || bInlineTop<-100));"
				+ "var vignetteLocked=(hash==='#google_vignette' && displaced);"
				+ "var posLocked=((bpos==='fixed'||bpos==='relative') && (bComputedTop<-100 || bInlineTop<-100));"
				+ "return lockedByOffset || vignetteLocked || posLocked;");
		return Boolean.TRUE.equals(v);
	}

	/**
	 * Instantaneous (non-waiting) check that the rendered document is genuinely at
	 * the top: pageYOffset near zero AND the body not displaced upward. Unlike a
	 * bare pageYOffset check, this is not fooled by a vignette scroll lock.
	 */
	public boolean isRenderedPageAtTop() {
		Object v = ((JavascriptExecutor) driver).executeScript(
				"var b=document.body;"
				+ "var y=window.pageYOffset||0;"
				+ "var bt=Math.round(b.getBoundingClientRect().top);"
				+ "return y<=5 && Math.abs(bt)<100;");
		return Boolean.TRUE.equals(v);
	}

	/**
	 * Instantaneous (non-waiting) check that at least one displayed element
	 * matching the active-slide heading xpath intersects the viewport.
	 */
	public boolean isActiveSliderHeadingInViewport(String activeHeadingXpath) {
		java.util.List<WebElement> elements = driver.findElements(By.xpath(activeHeadingXpath));
		for (WebElement element : elements) {
			try {
				if (!element.isDisplayed()) {
					continue;
				}
				Boolean v = (Boolean) ((JavascriptExecutor) driver).executeScript(VIEWPORT_INTERSECTION_JS, element);
				if (Boolean.TRUE.equals(v)) {
					return true;
				}
			} catch (StaleElementReferenceException ignored) {
				// carousel changed slides; ignore this candidate
			}
		}
		return false;
	}

	/**
	 * After clicking the scroll-up arrow, the vignette lock is applied
	 * asynchronously, so a single immediate check misses it. This polls for up to
	 * the given timeout and returns:
	 *   1 = rendered genuinely at top AND active heading visible on screen,
	 *   2 = advertisement scroll lock detected,
	 *   0 = neither within the timeout.
	 * A bare pageYOffset of 0 is never treated as success on its own.
	 */
	public int pollForRenderedTopOrScrollLock(String activeHeadingXpath, int timeoutSeconds) {
		long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L);
		while (System.currentTimeMillis() < deadline) {
			if (isDocumentScrollLocked()) {
				return 2;
			}
			if (isRenderedPageAtTop() && isActiveSliderHeadingInViewport(activeHeadingXpath)) {
				return 1;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return 0;
			}
		}
		return 0;
	}

	/**
	 * Recovers from a confirmed stale Google vignette scroll lock without depending
	 * on finding a Close button. Removes the inline/positioning properties the
	 * vignette uses to displace the document (top / position / transform /
	 * overflow on both body and html), hides known vignette containers, and clears
	 * the "#google_vignette" hash via history.replaceState (no page reload). All
	 * work is guarded so it can never fail the test.
	 */
	public void recoverVignetteScrollLock() {
		scrollUpOverlayCloseAttempted = true;
		try {
			((JavascriptExecutor) driver).executeScript(
					"try {"
					+ "  const body=document.body, html=document.documentElement;"
					+ "  body.style.removeProperty('top');"
					+ "  body.style.removeProperty('position');"
					+ "  body.style.removeProperty('transform');"
					+ "  body.style.removeProperty('overflow');"
					+ "  html.style.removeProperty('top');"
					+ "  html.style.removeProperty('position');"
					+ "  html.style.removeProperty('transform');"
					+ "  html.style.removeProperty('overflow');"
					+ "  var v=document.querySelectorAll('ins.adsbygoogle, iframe[src*=\"google_vignette\"], [id*=\"google_vignette\"], .google-vignette');"
					+ "  for (var k=0;k<v.length;k++){ try{ v[k].style.display='none'; }catch(e){} }"
					+ "  if (location.hash==='#google_vignette') {"
					+ "    history.replaceState(null, document.title, location.pathname + location.search);"
					+ "  }"
					+ "} catch (e) {}");
		} catch (Exception e) {
			// Never fail the test because of recovery.
		}
	}

	/**
	 * Waits until the document is confirmed restored after a vignette lock: the
	 * body rectangle is near the top, computed body top is auto or near zero, and
	 * the URL no longer contains "#google_vignette".
	 */
	public boolean waitForVignetteRecovery(int timeoutSeconds) {
		long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L);
		while (System.currentTimeMillis() < deadline) {
			try {
				Boolean ok = (Boolean) ((JavascriptExecutor) driver).executeScript(
						"var b=document.body;var bcs=window.getComputedStyle(b);"
						+ "function pf(x){var n=parseFloat(x);return isNaN(n)?0:n;}"
						+ "var bt=Math.round(b.getBoundingClientRect().top);"
						+ "var ct=bcs.top;"
						+ "var topOk=Math.abs(bt)<100;"
						+ "var computedOk=(ct==='auto' || Math.abs(pf(ct))<100);"
						+ "var hashOk=(location.hash!=='#google_vignette');"
						+ "return topOk && computedOk && hashOk;");
				if (Boolean.TRUE.equals(ok)) {
					return true;
				}
			} catch (Exception ignored) {
				// keep polling
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}

	/** True if at least one element matching the xpath is present in the DOM. */
	public boolean isPresent(String xpath) {
		try {
			return !driver.findElements(By.xpath(xpath)).isEmpty();
		} catch (Exception e) {
			return false;
		}
	}

	/** True if the element matching the xpath is present AND displayed. */
	public boolean isPresentAndDisplayed(String xpath) {
		try {
			java.util.List<WebElement> els = driver.findElements(By.xpath(xpath));
			for (WebElement e : els) {
				try {
					if (e.isDisplayed()) {
						return true;
					}
				} catch (Exception ignored) {
					// stale/hidden; keep checking
				}
			}
		} catch (Exception ignored) {
			// none
		}
		return false;
	}

	/** Waits up to the timeout for the element to be present and displayed. */
	public boolean waitForPresentAndDisplayed(String xpath, int timeoutSeconds) {
		long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L);
		while (System.currentTimeMillis() < deadline) {
			if (isPresentAndDisplayed(xpath)) {
				return true;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}

	/**
	 * Polls up to the timeout and returns true when AT LEAST ONE element matching
	 * the xpath is displayed. Unlike a single-match check, this inspects every
	 * match each poll, so a locator that returns many elements (for example the
	 * brand links in the sidebar) passes as soon as any one of them is visible.
	 */
	public boolean waitUntilAtLeastOneDisplayed(String xpath, int timeoutSeconds) {
		long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L);
		while (System.currentTimeMillis() < deadline) {
			try {
				for (WebElement e : driver.findElements(By.xpath(xpath))) {
					try {
						if (e.isDisplayed()) {
							return true;
						}
					} catch (StaleElementReferenceException ignored) {
						// element went stale; keep checking others
					}
				}
			} catch (Exception ignored) {
				// transient; retry
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}

	/** Waits until at least one of the given xpaths is present and displayed. */
	public boolean waitForAnyPresentAndDisplayed(java.util.List<String> xpaths, int timeoutSeconds) {
		long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L);
		while (System.currentTimeMillis() < deadline) {
			for (String xp : xpaths) {
				if (isPresentAndDisplayed(xp)) {
					return true;
				}
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}

	/**
	 * If a Google vignette interrupted a navigation (the URL carries
	 * "#google_vignette" or the document is scroll-locked), recover the lock,
	 * clear the hash, and navigate directly to the given home URL. The session is
	 * preserved (no new browser, no cookie clearing), so an authenticated session
	 * survives. No-op when there is no vignette, so unaffected flows are untouched.
	 */
	public void recoverHomeIfVignetteInterrupted(String homeUrl) {
		try {
			String url = driver.getCurrentUrl();
			boolean vignette = (url != null && url.contains("#google_vignette")) || isDocumentScrollLocked();
			if (vignette) {
				logger.info("Vignette interrupted navigation; recovering and returning to home (session preserved).");
				recoverVignetteScrollLock();
				waitForVignetteRecovery(5);
				driver.navigate().to(homeUrl);
				waitForVignetteRecovery(3);
			}
		} catch (Exception ignored) {
			// Never fail because of recovery; the caller's assertion will report.
		}
	}

	/**
	 * Best-effort dismissal of advertisement overlays and recovery from a stale
	 * scroll lock. Clicks common visible close/dismiss controls in the main
	 * document, hides obvious full-screen vignette overlays, and removes body/html
	 * "position: fixed" locks so the document can scroll again. Cross-origin iframe
	 * contents are never accessed, and all work is guarded so overlay handling can
	 * never fail the test. Returns true if anything was handled.
	 */
	public boolean dismissOverlaysAndRestoreScrollLock() {
		scrollUpOverlayCloseAttempted = true;
		handleOptionalConsent();
		try {
			Object handled = ((JavascriptExecutor) driver).executeScript(
					"var handled=false;"
					+ "try{"
					+ "  var selectors=['#dismiss-button','.close-button','[aria-label=\"Close ad\"]','[aria-label=\"Close\"]','[aria-label=\"Dismiss\"]','[title=\"Close\"]'];"
					+ "  for(var i=0;i<selectors.length;i++){"
					+ "    var els=document.querySelectorAll(selectors[i]);"
					+ "    for(var j=0;j<els.length;j++){var el=els[j];var r=el.getBoundingClientRect();"
					+ "      if(r.width>0&&r.height>0){try{el.click();handled=true;}catch(e){}}}"
					+ "  }"
					+ "  var vignettes=document.querySelectorAll('ins.adsbygoogle, iframe[src*=\"google_vignette\"], [id*=\"google_vignette\"], .google-vignette');"
					+ "  for(var k=0;k<vignettes.length;k++){try{vignettes[k].style.display='none';handled=true;}catch(e){}}"
					+ "  var b=document.body, h=document.documentElement;"
					+ "  var bcs=window.getComputedStyle(b);"
					+ "  if(bcs.position==='fixed'||b.style.position==='fixed'){b.style.position='';b.style.top='';b.style.left='';b.style.width='';b.style.overflow='';handled=true;}"
					+ "  h.style.position='';h.style.top='';h.style.overflow='';"
					+ "}catch(e){}"
					+ "return handled;");
			return Boolean.TRUE.equals(handled);
		} catch (Exception e) {
			// Never fail the test because of overlay handling.
			return false;
		}
	}

	/**
	 * Waits until the page has scrolled back near the top (pageYOffset within the
	 * given pixel threshold). Used by Test Cases 25 and 26 to confirm the scroll-up
	 * action actually happened before verifying the slider heading.
	 */
	public void waitUntilPageNearTop(int thresholdPixels, int timeoutSeconds) {
		long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
		while (System.currentTimeMillis() < endTime) {
			if (getVerticalScrollPosition() <= thresholdPixels) {
				return;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
	}

	/**
	 * Near-top must reflect the actual rendered document, not just pageYOffset.
	 * Requires pageYOffset within the threshold AND the body not displaced far
	 * above the viewport (which would indicate a stale advertisement scroll lock
	 * where pageYOffset=0 is misleading).
	 */
	public void assertPageIsNearTop(int thresholdPixels) {
		long position = getVerticalScrollPosition();
		long bodyTop = getBodyRectTop();
		boolean nearTop = position <= thresholdPixels && bodyTop > -100;
		assertTrue(nearTop,
				"Expected the page to be near the top (pageYOffset <= " + thresholdPixels + ", got " + position
				+ "; and the body not displaced upward, bodyRectTop=" + bodyTop + "). " + buildScrollDiagnostics());
	}

	/**
	 * Comprehensive scroll / overlay diagnostics for failure messages: document
	 * scroll-lock state, body/html rectangles and styles, URL and hash, plus the
	 * scroll-up orchestration state (arrow click count, overlay found / close
	 * attempted, start pageYOffset).
	 */
	public String buildScrollDiagnostics() {
		String jsState = "js=err";
		try {
			jsState = (String) ((JavascriptExecutor) driver).executeScript(
					"function px(v){return Math.round(v);}"
					+ "var b=document.body,h=document.documentElement;"
					+ "var bcs=window.getComputedStyle(b),hcs=window.getComputedStyle(h);"
					+ "var br=b.getBoundingClientRect(),hr=h.getBoundingClientRect();"
					+ "var out='finalPageYOffset='+(window.pageYOffset||0);"
					+ "out+='; bodyRectTop='+px(br.top);"
					+ "out+='; htmlRectTop='+px(hr.top);"
					+ "out+='; bodyInlinePosition=['+(b.style.position||'')+']; bodyComputedPosition='+bcs.position;"
					+ "out+='; bodyInlineTop=['+(b.style.top||'')+']; bodyComputedTop='+bcs.top;"
					+ "out+='; bodyTransform='+bcs.transform;"
					+ "out+='; htmlComputedPosition='+hcs.position+'; htmlComputedTop='+hcs.top+'; htmlTransform='+hcs.transform;"
					+ "out+='; url='+location.href+'; hash='+location.hash;"
					+ "return out;");
		} catch (Exception e) {
			jsState = "js=err(" + e.getClass().getSimpleName() + ")";
		}
		String viewport = "n/a";
		try {
			viewport = (String) ((JavascriptExecutor) driver).executeScript(
					"return (window.innerWidth || document.documentElement.clientWidth) + 'x' + "
					+ "(window.innerHeight || document.documentElement.clientHeight);");
		} catch (Exception ignored) {
			// leave viewport n/a
		}
		return "startPageYOffset=" + scrollUpStartPageYOffset
				+ "; arrowClickCount=" + scrollUpArrowClickCount
				+ "; overlayFound=" + scrollUpOverlayFound
				+ "; overlayCloseAttempted=" + scrollUpOverlayCloseAttempted
				+ "; " + jsState
				+ "; viewport=" + viewport;
	}

	/**
	 * Waits until AT LEAST ONE element matching the xpath is both displayed and
	 * has its bounding rectangle fully inside the current viewport, WITHOUT
	 * scrolling the page. This is needed for the home slider, whose heading text
	 * is duplicated across several carousel slides: most matches are hidden
	 * (inactive) slides, so validating an arbitrary first match would fail even
	 * when the active slide's heading is correctly at the top of the viewport.
	 * Hidden matches are ignored, and because the carousel rotates automatically,
	 * the wait keeps retrying until the matching heading in the active slide is in
	 * view. It never scrolls the element into view, so it cannot create a false
	 * pass.
	 */
	public boolean waitForAnyMatchingElementInViewportWithoutScrolling(String xpath, int timeoutSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
			return wait.until(currentDriver -> {
				java.util.List<WebElement> elements = currentDriver.findElements(By.xpath(xpath));
				for (WebElement element : elements) {
					try {
						if (!element.isDisplayed()) {
							continue;
						}
						Boolean inViewport = (Boolean) ((JavascriptExecutor) currentDriver).executeScript(
								"const r = arguments[0].getBoundingClientRect();"
								+ "return r.top >= 0 && r.left >= 0 && "
								+ "r.bottom <= (window.innerHeight || document.documentElement.clientHeight) && "
								+ "r.right <= (window.innerWidth || document.documentElement.clientWidth);",
								element);
						if (Boolean.TRUE.equals(inViewport)) {
							return true;
						}
					} catch (StaleElementReferenceException ignored) {
						// Carousel changed slides mid-check; retry through the wait.
					}
				}
				return false;
			});
		} catch (org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}

	public void assertElementInViewportWithoutScrolling(String webElement) {
		assertTrue(waitForAnyMatchingElementInViewportWithoutScrolling(webElement, 20),
				"Expected element to be visible in the viewport without scrolling: " + webElement);
	}

	// JavaScript that reports whether an element intersects the viewport and is
	// not visually hidden. Uses intersection (not full containment) so a heading
	// that sits partially under a fixed navbar still counts as visible on screen.
	private static final String VIEWPORT_INTERSECTION_JS =
			"const e = arguments[0];"
			+ "const r = e.getBoundingClientRect();"
			+ "const s = window.getComputedStyle(e);"
			+ "const vh = window.innerHeight || document.documentElement.clientHeight;"
			+ "const vw = window.innerWidth || document.documentElement.clientWidth;"
			+ "return r.width > 0 && r.height > 0 && "
			+ "r.bottom > 0 && r.right > 0 && "
			+ "r.top < vh && r.left < vw && "
			+ "s.visibility !== 'hidden' && "
			+ "parseFloat(s.opacity || '1') > 0;";

	/**
	 * Waits until at least one displayed element matching the active-slide xpath
	 * intersects the viewport (is visible on screen) WITHOUT scrolling. The
	 * carousel rotates automatically, so this retries through the wait until the
	 * active slide presents its heading on screen.
	 */
	public boolean waitForActiveSliderHeadingVisibleOnScreen(String activeHeadingXpath, int timeoutSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
			return wait.until(currentDriver -> {
				java.util.List<WebElement> elements = currentDriver.findElements(By.xpath(activeHeadingXpath));
				for (WebElement element : elements) {
					try {
						if (!element.isDisplayed()) {
							continue;
						}
						Boolean visibleOnScreen = (Boolean) ((JavascriptExecutor) currentDriver)
								.executeScript(VIEWPORT_INTERSECTION_JS, element);
						if (Boolean.TRUE.equals(visibleOnScreen)) {
							return true;
						}
					} catch (StaleElementReferenceException ignored) {
						// Carousel changed slides mid-check; retry through the wait.
					}
				}
				return false;
			});
		} catch (org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}

	/**
	 * Asserts that the active carousel slide's heading is visible on screen
	 * (intersects the viewport) without scrolling. On failure it fails with rich
	 * diagnostics so the exact reason is clear.
	 */
	public void assertActiveSliderHeadingVisibleOnScreen(String activeHeadingXpath, String allHeadingsXpath) {
		if (waitForActiveSliderHeadingVisibleOnScreen(activeHeadingXpath, 20)) {
			return;
		}
		assertTrue(false,
				"Expected the active slider heading to be visible in the viewport without scrolling: "
				+ activeHeadingXpath + " | " + buildSliderDiagnostics(activeHeadingXpath, allHeadingsXpath));
	}

	private String buildSliderDiagnostics(String activeHeadingXpath, String allHeadingsXpath) {
		StringBuilder diag = new StringBuilder();
		diag.append(buildScrollDiagnostics());

		int matching = 0;
		int displayed = 0;
		try {
			java.util.List<WebElement> all = driver.findElements(By.xpath(allHeadingsXpath));
			matching = all.size();
			for (WebElement e : all) {
				try {
					if (e.isDisplayed()) {
						displayed++;
					}
				} catch (Exception ignored) {
					// ignore individual stale/hidden elements while counting
				}
			}
		} catch (Exception ignored) {
			// leave counts at zero
		}
		diag.append("; matchingHeadings=").append(matching);
		diag.append("; displayedHeadings=").append(displayed);

		String activeClass = "n/a";
		try {
			WebElement activeItem = driver.findElement(By.xpath(
					"//*[@id='slider']//div[contains(concat(' ', normalize-space(@class), ' '), ' item ') "
					+ "and contains(concat(' ', normalize-space(@class), ' '), ' active ')]"));
			activeClass = activeItem.getAttribute("class");
		} catch (Exception ignored) {
			// no active slide found
		}
		diag.append("; activeSlideClass=[").append(activeClass).append("]");

		String rectInfo = "n/a";
		try {
			java.util.List<WebElement> active = driver.findElements(By.xpath(activeHeadingXpath));
			if (!active.isEmpty()) {
				rectInfo = (String) ((JavascriptExecutor) driver).executeScript(
						"const r = arguments[0].getBoundingClientRect();"
						+ "return 'top=' + Math.round(r.top) + ',left=' + Math.round(r.left) + "
						+ "',bottom=' + Math.round(r.bottom) + ',right=' + Math.round(r.right) + "
						+ "',w=' + Math.round(r.width) + ',h=' + Math.round(r.height);",
						active.get(0));
			}
		} catch (Exception ignored) {
			// leave rect as n/a
		}
		diag.append("; activeHeadingRect=[").append(rectInfo).append("]");

		String viewport = "n/a";
		try {
			viewport = (String) ((JavascriptExecutor) driver).executeScript(
					"return (window.innerWidth || document.documentElement.clientWidth) + 'x' + "
					+ "(window.innerHeight || document.documentElement.clientHeight);");
		} catch (Exception ignored) {
			// leave viewport as n/a
		}
		diag.append("; viewport=").append(viewport);

		return diag.toString();
	}

	/**
	 * Waits until the current URL contains the given fragment. Returns true on
	 * success, false if the timeout is reached. Used to confirm navigation
	 * completed (for example landing on /product_details/ or /test_cases).
	 */
	public boolean waitForUrlContains(String fragment, int timeoutSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
			return wait.until(ExpectedConditions.urlContains(fragment));
		} catch (TimeoutException e) {
			return false;
		}
	}

	/**
	 * Clicks the requested navigation element and confirms the expected page
	 * opened. If a Google vignette advertisement interrupts the navigation (the
	 * URL becomes ".../#google_vignette" instead of the intended path), it
	 * recovers by navigating directly to the intended Automation Exercise URL,
	 * then waits again. It fails normally if the intended page still does not
	 * load, so a genuine navigation problem is never hidden.
	 */
	/**
	 * Clicks an element and reliably lands on a destination page, verified by a
	 * destination-ready element (not by URL path alone). Polls for either genuine
	 * arrival (path present, ready element displayed, no vignette lock) or a
	 * vignette interruption (#google_vignette or a document scroll lock); on
	 * interruption it recovers the lock, clears the hash, and navigates directly
	 * to the destination URL, preserving cookies / authentication / cart / session.
	 * Fails with diagnostics if the destination-ready element never renders.
	 */
	public void clickAndNavigateWithAdFallback(String elementXpath, String expectedPath, String destinationUrl,
			String destinationReadyXpath) {
		click(elementXpath);
		long deadline = System.currentTimeMillis() + 10000;
		boolean recovered = false;
		while (System.currentTimeMillis() < deadline) {
			String currentUrl = driver.getCurrentUrl();
			boolean vignette = (currentUrl != null && currentUrl.contains("#google_vignette")) || isDocumentScrollLocked();
			if (vignette) {
				logger.info("Vignette interrupted navigation to " + expectedPath + "; recovering and opening target directly.");
				recoverVignetteScrollLock();
				waitForVignetteRecovery(5);
				driver.navigate().to(destinationUrl);
				waitForVignetteRecovery(3);
				recovered = true;
				break;
			}
			// Genuine arrival: path present AND ready element displayed.
			if (currentUrl != null && currentUrl.contains(expectedPath) && isPresentAndDisplayed(destinationReadyXpath)) {
				break;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
		// If we recovered by direct navigation, or arrival is still pending, wait
		// for the destination-ready element to render before returning.
		if (recovered || !isPresentAndDisplayed(destinationReadyXpath)) {
			waitForPresentAndDisplayed(destinationReadyXpath, 15);
		}
		org.testng.Assert.assertTrue(
				driver.getCurrentUrl().contains(expectedPath)
						&& !driver.getCurrentUrl().contains("#google_vignette")
						&& !isDocumentScrollLocked()
						&& isPresentAndDisplayed(destinationReadyXpath),
				"Expected to reach '" + expectedPath + "' with a rendered page. " + buildNavDiagnostics(destinationReadyXpath));
	}

	/** Generic navigation diagnostics used by the ad-aware navigation helper. */
	public String buildNavDiagnostics(String readyXpath) {
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
		return "url=" + url + "; hash=" + hash + "; title=" + title
				+ "; readyPresent=" + isPresent(readyXpath)
				+ "; readyDisplayed=" + isPresentAndDisplayed(readyXpath)
				+ "; bodyRectTop=" + bodyTop + "; scrollLocked=" + locked;
	}

	public void clickAndNavigateWithAdFallback(String elementXpath, String expectedPath, String destinationUrl) {
		click(elementXpath);
		if (!waitForUrlContains(expectedPath, 5)) {
			String currentUrl = driver.getCurrentUrl();
			if (currentUrl.contains("#google_vignette")) {
				logger.info("Google vignette interrupted navigation. Opening target URL directly: " + destinationUrl);
				driver.navigate().to(destinationUrl);
			}
		}
		org.testng.Assert.assertTrue(waitForUrlContains(expectedPath, 15),
				"Expected URL to contain '" + expectedPath + "' but it was: " + driver.getCurrentUrl());
	}

	/**
	 * Clicks the fixed-position scroll-up arrow (for example #scrollUp) and
	 * confirms the page actually scrolled upward, so Test Case 25 genuinely
	 * exercises the arrow rather than scrolling programmatically.
	 *
	 * The arrow is a fixed-position button, so it is NEVER scrolled into view
	 * first. A real Selenium click is attempted on the actual element. If that
	 * click is intercepted (for example by an advertisement overlay) or if the
	 * page does not move, optional overlays are dismissed and a bubbling
	 * MouseEvent 'click' is dispatched on the same element - dispatching the
	 * event directly reaches the arrow's handler even when an overlay is on top.
	 * This never falls back to window.scrollTo, so the arrow interaction is the
	 * thing under test. If the arrow still does not scroll, the near-top
	 * assertion in the calling test fails clearly.
	 */
	public void clickScrollUpArrowWithFallback(String arrowXpath) {
		long startY = getVerticalScrollPosition();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
		WebElement arrow = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(arrowXpath)));

		// First attempt: real Selenium click on the actual arrow element.
		try {
			arrow.click();
		} catch (Exception e) {
			// Click intercepted by an overlay: dismiss overlays and dispatch a
			// bubbling click event on the same element.
			handleOptionalConsent();
			dispatchClickEvent(arrow);
		}

		// Confirm the arrow actually moved the page up. If it did not, reacquire
		// the element and dispatch a JavaScript click event once more.
		if (!waitForScrollDecrease(startY, 100, 5)) {
			logger.info("Scroll-up arrow click did not move the page; dispatching a JavaScript click event on the arrow");
			handleOptionalConsent();
			try {
				WebElement arrowAgain = driver.findElement(By.xpath(arrowXpath));
				dispatchClickEvent(arrowAgain);
			} catch (Exception ignored) {
				// If the arrow can no longer be found, the near-top assertion in
				// the test will fail clearly.
			}
		}
	}

	/**
	 * Dispatches a bubbling, cancelable MouseEvent 'click' straight to the
	 * element. Unlike a coordinate-based Selenium click, this reaches the
	 * element's own click handler even if an advertisement overlay is visually
	 * on top of it.
	 */
	private void dispatchClickEvent(WebElement element) {
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].dispatchEvent(new MouseEvent('click', {view: window, bubbles: true, cancelable: true}));",
				element);
	}

	/**
	 * Polls until pageYOffset has dropped by at least minDecrease pixels from
	 * startY (evidence the scroll actually happened), or the timeout expires.
	 */
	public boolean waitForScrollDecrease(long startY, long minDecrease, int timeoutSeconds) {
		long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
		while (System.currentTimeMillis() < endTime) {
			if (startY - getVerticalScrollPosition() >= minDecrease) {
				return true;
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}

	// ---------------------------------------------------------------
	// Optional pop-up handling
	// ---------------------------------------------------------------

	/**
	 * Accepts the Google consent dialog when it appears. The dialog is optional,
	 * so its absence must never fail a test. Only the consent lookup itself is
	 * guarded; real test failures are unaffected.
	 */
	public void handleOptionalConsent() {
		try {
			WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
			WebElement consentButton = shortWait.until(ExpectedConditions.elementToBeClickable(
					By.xpath("//button[@aria-label='Consent'] | //button[.//p[contains(text(),'Consent')]] | //button[contains(@class,'fc-cta-consent')]")));
			consentButton.click();
			System.out.println("Optional consent dialog accepted.");
		} catch (TimeoutException e) {
			// No consent dialog present - continue normally.
		}
	}

	/**
	 * Accepts a JavaScript alert when present (Test Case 6 shows a confirm dialog
	 * after submitting the Contact Us form). Absence is not a failure.
	 */
	public void acceptAlertIfPresent(int timeoutSeconds) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
			wait.until(ExpectedConditions.alertIsPresent());
			driver.switchTo().alert().accept();
			System.out.println("Alert accepted.");
		} catch (TimeoutException | NoAlertPresentException e) {
			// No alert present - continue normally.
		}
	}

	// ---------------------------------------------------------------
	// File download helpers (Test Case 24)
	// ---------------------------------------------------------------

	public void cleanDownloadsFolder() {
		File downloadFolder = new File(DOWNLOAD_DIR);
		if (downloadFolder.exists()) {
			File[] files = downloadFolder.listFiles();
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
		} else {
			downloadFolder.mkdirs();
		}
	}

	/**
	 * Polls the project-local downloads folder until a file whose name contains
	 * the given text exists and is not empty, or the timeout expires.
	 */
	public boolean isFileDownloaded(String fileNameContains, int timeoutSeconds) {
		File downloadFolder = new File(DOWNLOAD_DIR);
		long endTime = System.currentTimeMillis() + (timeoutSeconds * 1000L);
		while (System.currentTimeMillis() < endTime) {
			File[] files = downloadFolder.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.getName().contains(fileNameContains)
							&& !file.getName().endsWith(".crdownload")
							&& file.length() > 0) {
						return true;
					}
				}
			}
			try {
				Thread.sleep(500); // short poll interval while waiting for the OS file system
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}
		return false;
	}

}
