package org.selenium.automationtest.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

import org.json.*;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.net.HttpURLConnection;

import static org.selenium.automationtest.common.TestLogger.*;
import static org.selenium.automationtest.common.constant.CT_Common.*;
import static org.testng.Assert.assertFalse;

/**
 * @author lientm
 * @date: 18-Nov-2014
 */
public class CommonBase {
	public WebDriver driver;
	protected String baseUrl = "http://10.121.43.33:8200";
	protected String baseProduct = "http://10.58.71.181:8888/PRODUCT_SERVICE/monitoring?action=clear_caches";
	protected String baseSale = "http://10.60.108.62:9751/SALE_SERVICE/monitoring?action=clear_caches";
	protected String baseSaleWeb = "http://10.60.108.62:9750/SALE_WEB/monitoring?action=clear_caches";
	protected String baseSorl = "http://10.60.108.62:8101/solr/#/dbmapactiveinfo/dataimport//dataimport";
	protected String baseCoreSolr = "dbmapactiveinfo";
	protected int DEFAULT_TIMEOUT = 180000;
	protected int WAIT_INTERVAL = 1000;
	public int loopCount = 0;
	public final int ACTION_REPEAT = 5;
	public Actions action;

	/**
	 * init Driver
	 * 
	 * @param URL
	 */
	public WebDriver initDriverTest(String... URL) {
		String url = System.getProperty("Url") != null ? System.getProperty("Url"): URL[0];
		String browser = System.getProperty("browser") != null ? System.getProperty("browser"): URL[1];
		WebDriver dr = null;
		
		if (browser.equalsIgnoreCase("chrome")){
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "driver" + File.separator + "chromedriver.exe");
			dr = new ChromeDriver();
		}else if (browser.equalsIgnoreCase("sfive")){
			ChromeOptions options = new ChromeOptions();
			System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "driver" + File.separator + "chromedriver.exe");
			options.setBinary(System.getProperty("user.dir") + File.separator + "driver" + File.separator + "sfive.exe");
			options.addArguments("enable-vsa-modify-header");
			options.addArguments("user-data-dir=" + System.getProperty("user.dir") + File.separator + "sfive_profile");
			dr = new ChromeDriver(options);
		}else {			
			FirefoxProfile fxProfile = new FirefoxProfile();
			fxProfile.setPreference("browser.download.folderList", 2);
			fxProfile.setPreference(
					"browser.download.manager.showWhenStarting", false);
			fxProfile.setPreference("browser.download.dir", System.getProperty("user.dir") + File.separator + "downloads");
			fxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk",
							"text/csv,application/vnd.android.package-archive,application/apk,application/java-archive,application/apk,application/java-archive,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,image/png,application/pdf,image/jpeg,application/excel");
			dr = new FirefoxDriver(fxProfile);
		}
		dr.get(url);
		dr.manage().window().maximize();
		return dr;
	}

	/**
	 * 
	 * @param driver
	 */
	public void waitForPageLoaded(WebDriver driver) {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver)
						.executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		try {
			Thread.sleep(1000);
			WebDriverWait wait = new WebDriverWait(driver, (DEFAULT_TIMEOUT/1000));
			wait.until(expectation);
		} catch (Throwable error) {
			Assert.fail("Timeout khi cho trang web hoan thanh load");
		}
	}

	/**
	 * Open page
	 * 
	 * @param pageUrl
	 * @param driver
	 */
	public void openPage(String pageUrl, WebDriver driver) {
		String urlSys = System.getProperty("Url");
		info("Url sys" + urlSys);
		if (pageUrl != null && !pageUrl.contains("http://")) {
			if (urlSys != null && urlSys != "") {
				driver.get(urlSys + "/" + pageUrl);
				info("vao nhanh sys " + urlSys + "/" + pageUrl);
			} else {
				driver.get(baseUrl + "/" + pageUrl);
				info("vao nhanh base " + baseUrl + "/" + pageUrl);
			}
		} else {
			driver.get(pageUrl);
		}
//		waitForPageLoaded(driver);
		pause(1000);
	}

	/**
	 * Open page at not loaded status, as clear cache
	 * 
	 * @param pageUrl
	 * @param driver
	 */
	public void openPageNotLoad(String pageUrl, WebDriver driver) {
		if (pageUrl != null) {
			driver.get(pageUrl);
			pause(2000);
		}
	}

	/**
	 * pause driver in timeInMillis
	 * 
	 * @param timeInMillis
	 */
	public void pause(long timeInMillis) {
		try {
			Thread.sleep(timeInMillis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param locator
	 * @return
	 */
	public WebElement getElement(Object locator) {
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		WebElement elem = null;
		try {
			elem = driver.findElement(by);
		} catch (NoSuchElementException e) {
			checkCycling(e, 10);
			pause(WAIT_INTERVAL);
			getElement(locator);
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 10);
			pause(WAIT_INTERVAL);
			getElement(locator);
		}
		return elem;
	}

	/**
	 * get a display element in web page
	 * 
	 * @param locator
	 * @return
	 */
	public WebElement getDisplayedElement(Object locator) {
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		WebElement e = null;
		try {
			if (by != null)
				e = driver.findElement(by);
			if (e != null) {
				if (isDisplay(by))
					return e;
			}
		} catch (NoSuchElementException ex) {
			checkCycling(ex, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			getDisplayedElement(locator);
		} catch (StaleElementReferenceException ex) {
			checkCycling(ex, 10);
			pause(WAIT_INTERVAL);
			getDisplayedElement(locator);
		} finally {
			loopCount = 0;
		}
		return null;
	}

	/**
	 * 
	 * @param locator
	 * @return
	 */
	public List<WebElement> getListElement(Object locator) {
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		List<WebElement> elementOptions;
		try {
			elementOptions = driver.findElements(by);
			return elementOptions;
		} catch (NoSuchElementException ex) {
			checkCycling(ex, 10);
			pause(WAIT_INTERVAL);
			getListElement(locator);
		} catch (StaleElementReferenceException ex) {
			checkCycling(ex, 10);
			pause(WAIT_INTERVAL);
			getListElement(locator);
		} finally {
			loopCount = 0;
		}
		return null;
	}

	/**
	 * lay cac gia tri thuoc tinh cua 1 mang doi tuong element
	 * 
	 * @param locator
	 * @param attribute
	 * @return
	 */
	public String[] getAttOfListElement(Object locator, String attribute) {
		String[] att = new String[20];
		List<WebElement> list;
		list = getListElement(locator);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				att[i] = list.get(i).getAttribute(attribute);
			}
		}
		return att;
	}

	public String[] getTextOfListElement(Object locator) {
		String[] att = new String[20];
		List<WebElement> list;
		list = getListElement(locator);
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				att[i] = list.get(i).getText();
			}
		}
		return att;
	}

	public String getSizeOfListElement(Object locator) {
		return String.valueOf(getListElement(locator).size());
	}

	/**
	 * checking an element is displayed in web page
	 * 
	 * @param locator
	 * @return
	 */
	public boolean isDisplay(Object locator) {
		boolean bool = false;
		WebElement e = getElement(locator);
		try {
			if (e != null)
				bool = e.isDisplayed();
		} catch (StaleElementReferenceException ex) {
			checkCycling(ex, 10);
			pause(WAIT_INTERVAL);
			isDisplay(locator);
		} finally {
			loopCount = 0;
		}
		return bool;
	}

	/**
	 * check repeat times
	 * 
	 * @param e
	 * @param loopCountAllowed
	 */
	public void checkCycling(Exception e, int loopCountAllowed) {
		info("Co exception xay ra: " + e.getClass().getName());
		if (loopCount > loopCountAllowed) {
			driver.manage().deleteAllCookies();
			Assert.fail("Qua thoi gian nhung khong thay hoac thay doi tuong "
					+ e.getMessage());
		}
		info("Lap lai lan thu " + loopCount);
		loopCount++;
	}

	/**
	 * get an element that present in Web Page
	 * 
	 * @param locator
	 * @param opParams
	 * @return
	 */
	public WebElement getElementPresent(Object locator, int... opParams) {
		WebElement elem = null;
		int timeout = opParams.length > 0 ? opParams[0] : DEFAULT_TIMEOUT;
		int isAssert = opParams.length > 1 ? opParams[1] : 1;
		int notDisplayE = opParams.length > 2 ? opParams[2] : 0;
		for (int tick = 0; tick < timeout / WAIT_INTERVAL; tick++) {
			if (notDisplayE == 2) {
				elem = getElement(locator);
			} else {
				elem = getDisplayedElement(locator);
			}
			if (null != elem)
				return elem;
			pause(WAIT_INTERVAL);
		}
		if (isAssert == 1) {
			String date = getDateTime("yyyyMMddHHmmss");
			info("date");
			captureScreen(driver, "Loi_" + date + ".jpg");
			assert false : ("Qua thoi gian " + timeout
					+ "ma khong tim thay doi tuong " + locator);
			quitDriver(driver);
		}
		return null;
	}

	/**
	 * input data to element
	 * 
	 * @param locator
	 * @param value
	 * @param validate
	 */
	public void type(Object locator, String value, boolean validate,
			boolean... clear) {
		boolean clean = clear.length > 0 ? clear[0] : true;
		WebDriverWait wait = new WebDriverWait(driver, 10);
		try {
			for (int loop = 1;; loop++) {
				if (loop >= ACTION_REPEAT) {
					Assert.fail("Qua thoi gian khi input du lieu: " + value
							+ " vao doi tuong " + locator);
				}
				WebElement element = getElementPresent(locator, 10000, 0);
				if (element != null) {
					wait.until(ExpectedConditions.visibilityOf(element));
					if (clean)
						element.clear();
					// element.click();
					element.sendKeys(value);
					if (!validate || value.equals(getValue(locator))) {
						break;
					}
				}
				info("Lap lai tac dong input text lan thu " + loop);
				pause(WAIT_INTERVAL);
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			type(locator, value, validate);
		} catch (NoSuchElementException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			type(locator, value, validate);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			type(locator, value, validate);
		} finally {
			loopCount = 0;
		}
	}

	public void typeElementIfDisplay(Object obj, String value, int... opParams) {
		int timeout = opParams.length > 0 ? opParams[0] : DEFAULT_TIMEOUT;
		if (getElementPresentNoAssert(obj, timeout) != null) {
			type(obj, value, true, true);
		}
	}

	/**
	 * 
	 * @param locator
	 * @param value
	 */
	public void inputTextJavaScript(Object locator, String value) {
		WebElement e = getElementPresent(locator, DEFAULT_TIMEOUT, 1, 2);
		try {
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].innerHTML = '" + value + "'", e);
		} catch (StaleElementReferenceException ex) {
			pause(1000);
			inputTextJavaScript(locator, value);
		}
	}

	/**
	 * get value of element in web page
	 * 
	 * @param locator
	 * @return
	 */
	public String getValue(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		try {
			return getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay)
					.getAttribute("value");
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			return getValue(locator);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * click on an element
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void click(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1,
					notDisplay);
			if (element.isEnabled()) {
				actions.click(element).perform();
			} else {
				info("Element is not enabled");
				// click(locator, notDisplay);
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} catch (NoSuchElementException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} finally {
			loopCount = 0;
		}
	}

	public void clickNotPassAction(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1,
					notDisplay);
			if (element.isEnabled()) {
				element.click();
			} else {
				info("Element is not enabled");
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} catch (ElementNotVisibleException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} catch (NoSuchElementException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			click(locator, notDisplay);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * click on an element using javascript
	 * 
	 * @param obj
	 */
	public void clickJavascript(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1,
					notDisplay);
			((JavascriptExecutor) driver).executeScript(
					"arguments[0].scrollIntoView(true);arguments[0].click();",
					element);
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			clickJavascript(locator, opParams);
		}
	}

	/**
	 * 
	 * @param locator
	 * @param opParams
	 * @return
	 */
	public WebElement waitForElementNotPresent(Object locator, int... opParams) {
		WebElement elem = null;
		int timeout = opParams.length > 0 ? opParams[0] : DEFAULT_TIMEOUT;
		int isAssert = opParams.length > 1 ? opParams[1] : 1;
		int notDisplayE = opParams.length > 2 ? opParams[2] : 0;

		for (int tick = 0; tick < timeout / WAIT_INTERVAL; tick++) {
			if (notDisplayE == 2) {
				elem = getElement(locator);
			} else {
				elem = getDisplayedElement(locator);
			}
			if (elem == null) {
				return null;
			}
			pause(WAIT_INTERVAL);
		}
		if (isAssert == 1) {
			assert false : ("Timeout after " + timeout
					+ "ms waiting for element not present: " + locator);
		}
		return elem;
	}

	public void checkElementNotPresent(Object locator, int... opParams) {
		WebElement ele;
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		int timeout = opParams.length > 0 ? opParams[0] : DEFAULT_TIMEOUT;
		for (int tick = 0; tick < timeout / WAIT_INTERVAL; tick++) {
			try {
				ele = driver.findElement(by);
				if (tick == timeout / WAIT_INTERVAL && ele != null) {
					Assert.fail("Qua thoi gian " + timeout
							+ " nhung doi tuong " + locator
							+ " van dang xuat hien");
				}
			} catch (NoSuchElementException e) {
				pause(WAIT_INTERVAL);
			} catch (StaleElementReferenceException e) {
				pause(WAIT_INTERVAL);
			}
		}
	}

	/**
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void check(Object locator, int... opParams) {
		int notDisplayE = opParams.length > 0 ? opParams[0] : 0;
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1,
					notDisplayE);
			boolean a = element.getAttribute("class").contains(
					"ui-state-active");
			if (!element.isSelected() && !a) {
				actions.click(element).perform();
			} else {
				info("Element " + locator + " is already checked.");
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			check(locator);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void uncheck(Object locator, int... opParams) {
		int notDisplayE = opParams.length > 0 ? opParams[0] : 0;
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1,
					notDisplayE);

			if (element.isSelected()) {
				actions.click(element).perform();
			} else {
				info("Element " + locator + " is already unchecked.");
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 5);
			pause(1000);
			uncheck(locator);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * get absolute path of file
	 * 
	 * @param relativeFilePath
	 * @return
	 */
	public String getAbsoluteFilePath(String relativeFilePath) {
		String curDir = System.getProperty("user.dir");
		String absolutePath = curDir + relativeFilePath;
		return absolutePath;
	}

	/**
	 * @param locator
	 */
	public void doubleClickOnElement(Object locator) {
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator);
			actions.doubleClick(element).perform();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 5);
			pause(1000);
			doubleClickOnElement(locator);
		} finally {
			loopCount = 0;
		}
	}

	public void contextClick(Object locator) {
		Actions actions = new Actions(driver);
		try {
			WebElement element = getElementPresent(locator);
			actions.contextClick(element).perform();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, 5);
			pause(1000);
			contextClick(locator);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * get text of element
	 * 
	 * @param locator
	 * @return
	 */
	public String getText(Object locator) {
		WebElement element = null;
		try {
			element = getElementPresent(locator);
			return element.getText();
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			return getText(locator);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * 
	 * @param locator
	 * @param safeToSERE
	 * @param opParams
	 */
	public void mouseOver(Object locator, boolean safeToSERE,
			Object... opParams) {
		WebElement element;
		Actions actions = new Actions(driver);
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		try {
			if (safeToSERE) {
				for (int i = 1; i < ACTION_REPEAT; i++) {
					info("Thuc hien mouserover repeat lan thu " + i);
					element = getElementPresent(locator, 2000, 0, notDisplay);
					info("Doi tuong " + element);
					if (element == null) {
						pause(WAIT_INTERVAL);
					} else {
						info("Thuc hien action");
						actions.moveToElement(element).build().perform();
						break;
					}
				}
			} else {
				element = getElementPresent(locator);
				actions.moveToElement(element).build().perform();
			}
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			mouseOver(locator, safeToSERE);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void mouseOverAndClick(Object locator, Object... opParams) {
		WebElement element;
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		Actions actions = new Actions(driver);
		try {
			element = getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay);
			actions.moveToElement(element).click(element).build().perform();
		} catch (StaleElementReferenceException e) {
			mouseOverAndClick(locator, opParams);
		}
	}

	/**
	 * quit driver if driver existed
	 * 
	 * @param dr
	 */
	public void quitDriver(WebDriver dr) {
		if (dr.toString().contains("null")) {
			System.out.print("All Browser windows are closed ");
		} else {
			driver.manage().deleteAllCookies();
			dr.quit();
		}
	}

	/**
	 * switch to a frame
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void switchToFrame(Object locator, Object... opParams) {
		info("Switch to frame " + locator);
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		try {
			driver.switchTo().frame(
					getElementPresent(locator, DEFAULT_TIMEOUT, 1, notDisplay));
		} catch (Exception e) {
			switchToFrame(locator, notDisplay);
		}
	}

	/**
	 * back to main frame
	 */
	public void switchToParentFrame() {
		try {
			driver.switchTo().defaultContent();
		} catch (Exception e) {
			switchToParentFrame();
		}
	}

	/**
	 * accept unexpected alert
	 */
	public void acceptAlert() {
		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();

		} catch (NoAlertPresentException ex) {
			info("No Alert present");
			;
		}
	}

	/**
	 * get datetime
	 * 
	 * @param format
	 */
	public String getDateTime(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		String dateTime = dateFormat.format(cal.getTime());
		info("time at moment is " + dateTime);
		return dateTime;
	}

	public String subtractDate(String format, String delta) {
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		int del = Integer.parseInt(delta);
		c.add(Calendar.DATE, -del);
		return formatDateToString(c.getTime(), format);
	}

	public String addDate(String format, String delta) {
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		int del = Integer.parseInt(delta);
		c.add(Calendar.DATE, del);
		return formatDateToString(c.getTime(), format);
	}

	public String addDate(String date, String format, String delta) {
		Date currentDate = formatDate(format, date);
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		int del = Integer.parseInt(delta);
		c.add(Calendar.DATE, del);
		return formatDateToString(c.getTime(), format);
	}

	public String formatDateToString(Date date, String format) {
		DateFormat f = new SimpleDateFormat(format);
		String dat = "";
		try {
			dat = f.format(date);
		} catch (Exception e) {
			info("Exception: " + e);
			info("Can not convert date");
		}
		info("dat " + dat);
		return dat;
	}

	/**
	 * @param format
	 * @param date
	 * @return
	 */
	public Date formatDate(String format, String date) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		Date dat = null;
		try {
			dat = f.parse(date);
		} catch (Exception e) {
			info("Exception: " + e);
			info("Can not convert date");
		}
		return dat;
	}

	/**
	 * 
	 * @param date
	 * @param format
	 * @return
	 */
	public String formatDateToString(String date, String format) {
		DateFormat f = new SimpleDateFormat(format);
		String dat = "";
		try {
			dat = f.format(date);
		} catch (Exception e) {
			info("Exception: " + e);
			info("Can not convert date");
		}
		info("dat" + dat);
		return dat;
	}

	/**
	 * @param date
	 * @param format
	 * @return
	 */
	public String formatDateToString(String date, String originalFormat,
			String intendedFormat) {
		DateFormat oriFormat = new SimpleDateFormat(originalFormat);
		DateFormat intendFormat = new SimpleDateFormat(intendedFormat);
		String dat = "";
		try {
			dat = intendFormat.format(oriFormat.parse(date));
		} catch (Exception e) {
			info("Exception: " + e);
			info("Can not convert date");
		}
		info("dat" + dat);
		return dat;
	}

	/**
	 * @param locator
	 * @param att
	 * @return
	 */
	public String getAttribute(Object locator, String att, int... opParams) {
		try {
			return getElementPresent(locator, opParams).getAttribute(att);
		} catch (StaleElementReferenceException e) {
			checkCycling(e, DEFAULT_TIMEOUT / WAIT_INTERVAL);
			pause(WAIT_INTERVAL);
			return getValue(locator);
		} finally {
			loopCount = 0;
		}
	}

	/**
	 * 
	 * @param object
	 */
	public void clickTab(By object) {
		if (object != null) {
			WebElement e = getElementPresent(object);
			e.sendKeys(Keys.TAB);
		}
	}

	/**
	 * 
	 * @param xpath
	 * @param att
	 * @return
	 */
	public String getAttributeFromJavaScript(String xpath, String att) {
		WebElement e = getElementPresent(xpath);
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		String value = (String) executor.executeScript(
				" return arguments[0].getAttribute('" + att + "')", e);
		info("value" + value);
		return value;
	}

	/**
	 * 
	 * @param format
	 * @param number
	 * @return
	 */
	public String formatCurrency(String number) {
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		info(formatter.format(Integer.parseInt(number)));
		return formatter.format(Integer.parseInt(number)).replace(" đ", "");
	}

	/**
	 * 
	 * @return
	 */
	public String getIpOfMachinhe() {
		String ip = "";
		try {
			ip = Inet4Address.getLocalHost().getHostAddress();
			info("IP of local machine: " + ip);
		} catch (Exception e) {
			info("Exeption: " + e);
		}
		return ip;
	}

	/**
	 * 
	 * @param locator
	 * @param opParams
	 */
	public void scrollToElement(Object locator, Object... opParams) {
		int notDisplay = (Integer) (opParams.length > 0 ? opParams[0] : 0);
		WebElement element = getElementPresent(locator, DEFAULT_TIMEOUT, 1,
				notDisplay);
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].scrollIntoView(true);", element);
	}

	/**
	 * type cho cac input dang so
	 * 
	 * @param locator
	 * @param text
	 * @param validate
	 */
	public void typeHinput(Object locator, String text, boolean validate) {
		if (text != null) {
			if (getElementPresent(locator) != null) {
				try {
					WebElement e = getElementPresent(locator);
					e.click();
					e.sendKeys(Keys.CONTROL + "a");
					e.sendKeys(text);
					for (int i = 0; i < 5; i++) {
						String am = getValue(locator, 2);
						if (am != null) {
							if (am.equalsIgnoreCase(text)) {
								break;
							} else {
								e.sendKeys(Keys.CONTROL + "a");
								e.sendKeys(text);
							}
						}
					}
				} catch (StaleElementReferenceException ex) {
					typeHinput(locator, text, validate);
				}
			}
		}
	}

	/**
	 * compare 2 string
	 * 
	 * @param s1
	 * @param s2
	 */
	public void verifyCompare(String s1, String s2) {
		if (s1 != "" && s1 != null && s2 != null && s2 != "") {
			Assert.assertFalse(!s1.equalsIgnoreCase(s2),
					"So sanh khong bang nhau: " + s1 + " va " + s2);
		} else if ((s1 == "" || s1 == null) && (s2 == "" || s2 == null)) {
			info("2 truong du lieu can so sanh deu null");
		} else {
			Assert.fail("Du lieu so sanh co 1 truong bi null");
		}
	}

	/**
	 * check field is null = ""
	 * 
	 * @param s
	 */
	public void verifyNull(String s) {
		if (!s.equalsIgnoreCase("")) {
			Assert.fail("Du lieu khong null");
		}
	}

	/**
	 * 
	 * @param dateBefore
	 * @param dateAfter
	 */
	public void compareDateBeforeDate(String dateBefore, String dateAfter) {
		Boolean compare = false;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			compare = sdf.parse(dateBefore).before(sdf.parse(dateAfter));
			info("Ket qua so sanh thoi gian: " + compare);
		} catch (Throwable e) {
			info("Loi:" + e);
		}
		Assert.assertFalse(!compare, "Moc thoi gian " + dateAfter
				+ " khong lon hơn " + dateBefore);
	}

	public void verifyContains(String s1, String s2) {
		if (s1 != null && s2 != null) {
			Assert.assertFalse(!s2.contains(s1), "Chuỗi " + s1
					+ " không nằm trong chuỗi " + s2);
		}
	}

	/**
	 * 
	 * @param xpath
	 * @param option
	 */
	public void selectOptionFromCombobox(String xpath, String option) {
		if (option != null) {
			String locator = xpath.replaceAll("&option", option);
			click(locator);
			waitForElementNotPresent(locator, 10000, 0);
		}
	}

	public void waitOptionAfterSelectFromCombox(String xpath, String data) {
		getElementPresent(By.xpath(xpath.replaceAll("&option", data)),
				DEFAULT_TIMEOUT, 2);
	}

	/**
	 * 
	 * @param tbSearch
	 * @param textSearch
	 * @param xpath
	 */
	public void selectOptionFromComBoxSearch(Object tbSearch,
			String textSearch, String xpath) {
		type(tbSearch, textSearch, true, false);
		pause(500);
		click(By.xpath(xpath.replaceAll("&option", textSearch)));
		pause(1000);
	}

	/**
	 * 
	 * @param object
	 * @param autocompletename
	 * @param option
	 */
	public void selectOptionFromAutocomplete(Object object,
			String autocompletename, String option, String... pannelID) {
		String autocomplete = "//*[contains(@class,'ui-autocomplete-panel')]//th[text()='"
				+ autocompletename + "']";
		String opt = "//*[contains(@class,'ui-autocomplete-panel')]//th[text()='"
				+ autocompletename
				+ "']/../../..//*[contains(text(),'"
				+ option + "')]";
		if (pannelID.length > 0) {
			autocomplete = "//*[contains(@class,'ui-autocomplete-panel') and contains(@id,'"
					+ pannelID[0]
					+ "')]//th[text()='"
					+ autocompletename
					+ "']";
			opt = "//*[contains(@class,'ui-autocomplete-panel') and contains(@id,'"
					+ pannelID[0]
					+ "')]//th[text()='"
					+ autocompletename
					+ "']/../../..//*[contains(text(),'" + option + "')]";
		}
		type(object, option, true, true);
		getElementPresent(autocomplete);
		clickJavascript(opt);
		waitForElementNotPresent(autocomplete);
	}

	/**
	 * @param driver
	 */
	public void captureScreen(WebDriver driver, String fileName) {
		try {
			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			String dir = System.getProperty("user.dir");
			FileUtils.copyFile(scrFile, new File(dir + "\\capture_screen\\"
					+ fileName));
		} catch (Exception e) {
			info("Khong capture duoc man hinh");
		}
	}

	/**
	 * parse to object from a xpath contains option
	 * 
	 * @param xpathOption
	 * @param option
	 * @return
	 */
	public String parseStringToObject(String xpathOption, String option) {
		String xpath = xpathOption.replaceAll("&option", option);
		return xpath;
	}

	/**
	 * select option from combobox with FW 1.0
	 * 
	 * @param selectObject
	 * @param index
	 */
	public void selectOptionFW1(Object selectObject, int index) {
		WebElement ele = getElementPresent(selectObject);
		Select select = new Select(ele);
		select.selectByIndex(index);
		pause(1000);
	}

	public String trimCharactor(String input, String trim) {
		info("Xau can xu ly trim: " + input);
		if (input != "" && input != null && trim != "") {
			if (trim == ".") {
				return input.replaceAll("\\.", "");
			} else {
				return input.replaceAll(trim, "");
			}
		} else
			return "";
	}

	/**
	 * get first day of month of next month
	 * 
	 * @param addMonth
	 * @return
	 */
	public String getFirstDayOfMonth(int addMonth) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Calendar cal = Calendar.getInstance();
		info("Current date: " + df.format(cal.getTime()));
		cal.add(Calendar.MONTH, addMonth);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return df.format(cal.getTime());
	}

	/**
	 * 
	 * @param file
	 * @param filePath
	 */
	public void uploadFile(Object file, String filePath) {
		WebElement e = getElement(file);
		info("Upload file "
				+ getAbsoluteFilePath("\\file_to_upload\\" + filePath));
		e.sendKeys(getAbsoluteFilePath("\\file_to_upload\\" + filePath));
		// e.sendKeys("/Users/lientm/Downloads/Kiemthu_Danhgia_T05_PMVT_Tieu_chi_danh_gia_nhan_su_hang_thang.xlsx");
	}

	/**
	 * 
	 * @param profileTypeCode
	 * @param fileNameUpload
	 */
	public void uploadProfileCM(String profileTypeCode, String fileNameUpload) {
		WebElement e = getElement("//label[contains(text(),'["
				+ profileTypeCode + "]')]/../../../..//input[@type='file']");
		e.sendKeys(getAbsoluteFilePath("\\file_to_upload\\" + fileNameUpload));
		getElement(By
				.xpath("//a[contains(@id,'btnOverPreviewFile') and text()='"
						+ fileNameUpload + "']"));
	}

	/**
	 * 
	 * @param urlText
	 * @param data
	 * @param column
	 * @return
	 */
	public static String[] callWS(String urlText, String data, String... column) {
		String output = "";
		String[] da = new String[100];
		String urlT = "";
		if (urlText != "" && !urlText.contains("http")) {
			urlT = System.getProperty("serviceLink") + urlText;
		} else {
			urlT = urlText;
		}
		try {
			URL url = new URL(urlT);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setRequestProperty("Accept", "text/xml");
			info("connection: " + connection);
			OutputStreamWriter osw = new OutputStreamWriter(
					connection.getOutputStream());
			osw.write(data);
			osw.flush();
			osw.close();
			InputStream in = connection.getInputStream();
			output = read(in);
			info("output cua WS: " + output);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource src = new InputSource();
			src.setCharacterStream(new StringReader(output));
			Document doc = builder.parse(src);
			if (column.length > 0) {
				for (int i = 0; i < column.length; i++) {
					if (doc.getElementsByTagName(column[i]).item(0)
							.getTextContent() != null) {
						da[i] = doc.getElementsByTagName(column[i]).item(0)
								.getTextContent();
						info("Gia tri trong tag " + column[i] + "la: " + da[i]);
					}
				}
			}
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			assertFalse(true, "Co loi khi thuc hien WS");

		}
		return da;
	}

	/**
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String read(InputStream input) throws IOException {
		try (BufferedReader buffer = new BufferedReader(new InputStreamReader(
				input))) {
			return buffer.lines().collect(Collectors.joining("\n"));
		}
	}

	public String getValueOfSystemVariable(String var) {
		return System.getProperty(var);
	}

	public void clearCacheSale() {
		String cache = System.getProperty("SaleCache");
		if (cache != null && cache != "") {
			driver.get(cache);
		} else {
			driver.get(baseSale);
		}
		acceptAlert();
		pause(2000);
	}

	public void clearCacheSaleWeb() {
		String cache = System.getProperty("SaleCacheWeb");
		if (cache != null && cache != "") {
			driver.get(cache);
		} else {
			driver.get(baseSaleWeb);
		}
		acceptAlert();
		pause(2000);
	}

	public void clearCacheProduct() {
		String cache = System.getProperty("Product");
		if (cache != null && cache != "") {
			driver.get(cache);
		} else {
			driver.get(baseProduct);
		}
		acceptAlert();
		pause(2000);
	}

	public void importSorl(String... coreSorl) {
		String solr = System.getProperty("Solr");
		String s = coreSorl.length > 0 ? coreSorl[0] : solr;
		String clean = coreSorl.length > 1 ? coreSorl[1] : "clean";

		if (s != null && s != "") {
			openPageNotLoad(s, driver);
		} else {
			openPageNotLoad(baseSorl, driver);
		}
		// login neu co
		typeElementIfDisplay(ELEMENT_SOLR_USERNAME, userSolr, 5000);
		typeElementIfDisplay(ELEMENT_SOLR_PASS, passSolr, 5000);
		clickElementIfDisplay(ELEMENT_SOLR_LOGIN_BUTTON, 5000);

		click(By.xpath("//*[contains(@id,'has-collections')]/div"));
		if (!clean.equalsIgnoreCase("clean")) {
			click(By.id("clean"));
		}
		click(By.xpath("//*[contains(text(),'Execute')]/.."));
		pause(5000);
		click(By.xpath("//button/*[text()='Refresh Status']"));
	}

	public WebElement getElementPresentNoAssert(Object locator, int... opParams) {
		WebElement elem = null;
		int timeout = opParams.length > 0 ? opParams[0] : DEFAULT_TIMEOUT;
		By by = locator instanceof By ? (By) locator : By.xpath(locator
				.toString());
		for (int tick = 0; tick < timeout / WAIT_INTERVAL; tick++) {
			try {
				elem = driver.findElement(by);
				if (null != elem)
					return elem;
				pause(WAIT_INTERVAL);
				info("Lap lai lan thu " + tick + 1);
			} catch (NoSuchElementException ex) {
				if (tick == timeout / WAIT_INTERVAL) {
					return null;
				}
			} catch (WebDriverException e) {
				if (tick == timeout / WAIT_INTERVAL) {
					return null;
				}
			} catch (IllegalStateException e) {
				if (tick == timeout / WAIT_INTERVAL) {
					return null;
				}
			}
		}
		return elem;
	}

	/**
	 * tra ve so lan xuat hien cua 1 xau trong chuoi
	 * 
	 * @param value
	 * @param array
	 * @return
	 */
	public String checkValueInArray(String value, String[] array) {
		int soLan = 0;
		if (value != null && value != "" && array.length > 0) {
			for (int i = 0; i < array.length; i++) {
				if (value.equalsIgnoreCase(array[i])) {
					soLan++;
					info("Chuoi \"" + value
							+ "\" xuat hien trong mang lan thu: " + soLan);
				}
			}
			return String.valueOf(soLan);
		} else {
			info("Chuoi can kiem tra dang null hoac mang khong co phan tu");
			return "";
		}
	}

	/**
	 * 
	 * @param string
	 * @param split
	 * @return
	 */
	public String[] convertStringToArray(String string, String split) {
		String[] a = new String[100];
		if (string != null && string != "") {
			a = string.split(split);
		} else {
			info("Xau ky tu can chuyen sang mang ");
		}
		return a;
	}

	public String subString(String s, int start, int end) {
		return s.substring(start, end);
	}

	public void verifyNotNull(String s) {
		if (s == null || s == "") {
			Assert.fail("Du lieu  null");
		}
	}

	public void clickElementIfDisplay(Object obj, int... opParams) {
		int timeout = opParams.length > 0 ? opParams[0] : DEFAULT_TIMEOUT;
		if (getElementPresentNoAssert(obj, timeout) != null) {
			click(obj);
		}
	}

	public void switchNewTab(int... index) {
		int tab = index.length > 0 ? index[0] : 1;
		ArrayList<String> tabs2 = new ArrayList<String>(
				driver.getWindowHandles());
		driver.switchTo().window(tabs2.get(tab));
	}

	public void verifyCompareNotEqual(String s1, String s2) {
		if (s1 != "" && s1 != null && s2 != null && s2 != "") {
			Assert.assertTrue(!s1.equalsIgnoreCase(s2),
					"Pass neu 2 truong so sanh khong bang nhau: " + s1 + " va "
							+ s2);
		} else if ((s1 == "" || s1 == null) && (s2 == "" || s2 == null)) {
			info("2 truong du lieu can so sanh deu null");
		} else {
			Assert.fail("Du lieu so sanh co 1 truong bi null");
		}
	}

	/**
	 * 
	 * @return dinh dang Tháng 1
	 */
	public String getAndFormatMonth(String... delta) {
		String del = delta.length > 0 ? delta[0] : "0";
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -Integer.parseInt(del));
		int month = cal.get(Calendar.MONTH) + 1;
		return "Tháng " + month;
	}

	/**
	 * 
	 * @param urlText
	 * @param data
	 * @param addParam
	 * @param column
	 * @return
	 */
	public static String postRestService(String urlText, String data,
			String addParam, String... method) {
		String med = method.length > 0 ? method[0] : "POST";
		String output = "";
		String urlT = "";
		if (urlText != "" && !urlText.contains("http")) {
			urlT = System.getProperty("serviceLink") + urlText;
		} else {
			urlT = urlText;
		}
		try {
			URL url = new URL(urlT);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod(med);
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-Length",
					Integer.toString(data.getBytes().length));

			if (addParam != "") {
				String[] param = addParam.split(",");
				if (param.length > 0) {
					for (int i = 0; i < param.length; i++) {
						if (param[i] != "") {
							String[] a = param[i].split(":");
							if (a.length > 0) {
								con.setRequestProperty(a[0], a[1]);
							}
						}
					}
				}
			}
			if (data != "") {
				OutputStream osw = con.getOutputStream();
				osw.write(data.getBytes());
				osw.flush();
				osw.close();
			}
			int code = con.getResponseCode();
			info("Result Execute Services is: " + code + " "
					+ con.getResponseMessage());
			if (code == 200) {
				InputStream in = con.getInputStream();
				output = read(in);
			} else {
				InputStream in = con.getErrorStream();
				output = read(in);
			}
			info("Output of service is: " + output);
		} catch (IOException | RuntimeException e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * 
	 * @param response
	 * @param key
	 * @return
	 */
	public String[] getValueFromRestService(String response, String key) {
		String[] result = new String[10];
		String[] path = null;
		if (response != null && response != "") {
			JSONObject json = new JSONObject(response);
			if (key != "") {
				path = key.split("/");
				if (path.length == 1) {
					Object val = json.get(path[0]);
					result[0] = val != null ? String.valueOf(val) : null;
				} else {
					JSONArray array1 = json.getJSONArray(path[0]);
					if (path.length == 2) {
						int size = array1.length() < 10 ? array1.length() : 10;
						for (int i = 0; i < size; i++) {
							Object val = array1.getJSONObject(i).get(path[1]);
							result[i] = val != null ? val.toString() : null;
						}
					} else if (path.length == 3) {
						JSONArray array2 = array1.getJSONObject(0)
								.getJSONArray(path[1]);
						int size = array2.length() < 10 ? array2.length() : 10;
						for (int i = 0; i < size; i++) {
							Object val = array2.getJSONObject(i).get(path[2]);
							result[i] = val != null ? val.toString() : null;
						}
					}

				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param urlText
	 * @param addParam
	 * @param column
	 * @return
	 */
	public static String getRestService(String urlText, String addParam) {
		String output = "";
		String urlT = "";
		if (urlText != "" && !urlText.contains("http")) {
			urlT = System.getProperty("serviceLink") + urlText;
		} else {
			urlT = urlText;
		}
		try {
			URL url = new URL(urlT);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			if (addParam != "") {
				String[] param = addParam.split(",");
				if (param.length > 0) {
					for (int i = 0; i < param.length; i++) {
						if (param[i] != "") {
							String[] a = param[i].split(":");
							if (a.length > 0) {
								con.setRequestProperty(a[0], a[1]);
							}
						}
					}
				}
			}
			int code = con.getResponseCode();
			info("Result Execute Services is: " + code + " "
					+ con.getResponseMessage());
			if (code == 200) {
				InputStream in = con.getInputStream();
				output = read(in);
			} else {
				InputStream in = con.getErrorStream();
				output = read(in);
			}
			info("Output of service is: " + output);
		} catch (IOException | RuntimeException e) {
			e.printStackTrace();
		}
		return output;
	}

	public String[] exeServiceManyTimes(String dataInput, String regex,
			String urlText, String data, String addParam, String... method) {
		String med = method.length > 0 ? method[0] : "SOAP";
		String[] output = new String[10];
		if (dataInput != null && dataInput != "" && regex != null
				&& regex != "") {
			String[] input = dataInput.split(regex);
			if (input.length > 0) {
				for (int i = 0; i < input.length; i++) {
					if (!med.equalsIgnoreCase("SOAP")) {
						output[i] = postRestService(urlText,
								data.replaceAll("&{param}", input[i]),
								addParam, med);
						pause(2000);
						info("Ket qua thuc thi ws " + urlText + " cua tham so "
								+ input[i] + " is " + output[i]);
					} else {
						output[i] = callWS(urlText,
								data.replaceAll("&param", input[i]), addParam)[0];
						pause(2000);
						info("Ket qua thuc thi ws " + urlText + " cua tham so "
								+ input[i] + " is " + output[i]);
					}
				}
			}
		}
		return output;
	}

	public String postRestServiceHttps(String urlText, String data,
			String addParam, String... method) {
		String med = method.length > 0 ? method[0] : "POST";
		String output = "";
		String urlT = "";
		if (urlText != "" && !urlText.contains("http")) {
			urlT = System.getProperty("serviceLink") + urlText;
		} else {
			urlT = urlText;
		}
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] certs,
						String authType) {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] certs,
						String authType) {
				}
			} };

			// Install the all-trusting trust manager
			try {
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc
						.getSocketFactory());
			} catch (GeneralSecurityException e) {
			}
			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = (hostname, session) -> true;
			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

			URL url = new URL(urlT);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setDoOutput(true);
			con.setRequestMethod(med);
			con.setRequestProperty("Accept", "application/json");

			if (addParam != "") {
				String[] param = addParam.split(",");
				if (param.length > 0) {
					for (int i = 0; i < param.length; i++) {
						if (param[i] != "") {
							String[] a = param[i].split(":");
							if (a.length > 0) {
								con.setRequestProperty(a[0], a[1]);
							}
						}
					}
				}
			}
			if (data != "") {
				OutputStream osw = con.getOutputStream();
				osw.write(data.getBytes());
				osw.flush();
				osw.close();
			}
			info("Result Execute Services is: " + con.getResponseCode() + " "
					+ con.getResponseMessage());
			InputStream in = con.getInputStream();
			output = read(in);
			info("Output of service is: " + output);
		} catch (IOException | RuntimeException e) {
			e.printStackTrace();
		}
		return output;
	}

	public void setProxy(String ip, String port) {
		System.setProperty("https.proxyHost", ip);
		System.setProperty("https.proxyPort", port);
	}

	public String checkCondition(String conditionType, String param1,
			String param2) {
		Boolean check = true;
		if (conditionType.equalsIgnoreCase("contains")) {
			check = param1.contains(param2) ? true : false;
		} else if (conditionType.equalsIgnoreCase("notcontains")) {
			check = !param1.contains(param2) ? true : false;
		} else if (conditionType.equalsIgnoreCase("<")) {
			check = Integer.parseInt(param1) < Integer.parseInt(param2) ? true
					: false;
		} else if (conditionType.equalsIgnoreCase(">")) {
			check = Integer.parseInt(param1) > Integer.parseInt(param2) ? true
					: false;
		} else if (conditionType.equalsIgnoreCase("=")) {
			check = Integer.parseInt(param1) == Integer.parseInt(param2) ? true
					: false;
		}
		info("Ket qua kiem tra dieu kien " + conditionType + " la: " + check);
		return Boolean.toString(check);
	}

	public void exeFuncWithCondition(String condition, String methodName,
			String... parameters) {
		int size = parameters.length;
		CommonBase commonBase = new CommonBase();
		if (condition.equalsIgnoreCase("true")) {
			try {
				if (parameters.length == 0) {
					Method method = CommonBase.class
							.getDeclaredMethod(methodName);
					method.setAccessible(true);
					method.invoke(commonBase);
				} else {
					String[] value = new String[size];
					Class[] classArr = new Class[size];
					for (int i = 0; i < size; i++) {
						String[] p = parameters[i].split("/");
						if (p.length > 0) {
							value[i] = p[1];
							classArr[i] = Class.forName(p[0]);
						}
					}
					Method method = CommonBase.class.getDeclaredMethod(
							methodName, classArr);
					method.setAccessible(true);
					method.invoke(commonBase, value);
				}
			} catch (Exception e) {
				e.printStackTrace();
				info("Loi khi thuc hien goi ham " + methodName);
			}
		}
	}

	public String exeSetValueWithCondition(String condition, String value) {
		String val = "";
		if (condition.equalsIgnoreCase("true")) {
			val = value;
		}
		return val;
	}

	public String[] shouldMatchRegex(String pattern, String message) {
		String[] result = new String[10];
		if (message == "" || message == null) {
			Assert.fail("message bi null");
		}
		Pattern p = Pattern.compile(pattern);
		Matcher matcher = p.matcher(message);
		if (matcher.find()) {
			/*
			 * Tra ve chuoi match patten result[0] => toan bo chuoi match patten
			 * Cac phan tu tiep theo la cac gia tri muon lay ra trong patten
			 */
			info("Patten match String");
			for (int i = 0; i < matcher.groupCount() + 1; i++) {
				result[i] = matcher.group(i);
			}
		} else {
			Assert.fail("String, patten does not match");
		}

		return result;
	}

	/* Evaluates the given expression and returns the result in String format. */
	public String evaluate(String expression) {
		ScriptEngineManager mgr = new ScriptEngineManager();
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		String result = null;
		try {
			result = engine.eval(expression).toString();
			if (result.endsWith(".0")) {
				System.out.println("Ket qua cua bieu thuc " + expression
						+ " la: " + result + " Return phan nguyen");
				for (int i = 0; i < result.length(); i++) {
					if (result.charAt(i) == '.') {
						return result.substring(0, i);
					}
				}
			}
			System.out.println("Ket qua cua bieu thuc " + expression + " la: "
					+ result);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String[] callWSNoAssert(String urlText, String data,
			String... column) {
		String output = "";
		String[] da = new String[100];
		try {
			URL url = new URL(urlText);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setRequestProperty("Accept", "text/xml");
			info("connection: " + connection);
			OutputStreamWriter osw = new OutputStreamWriter(
					connection.getOutputStream());
			osw.write(data);
			osw.flush();
			osw.close();
			InputStream in = connection.getInputStream();
			output = read(in);
			info("output cua WS: " + output);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			InputSource src = new InputSource();
			src.setCharacterStream(new StringReader(output));
			Document doc = builder.parse(src);
			if (column.length > 0) {
				for (int i = 0; i < column.length; i++) {
					if (doc.getElementsByTagName(column[i]).item(0)
							.getTextContent() != null) {
						da[i] = doc.getElementsByTagName(column[i]).item(0)
								.getTextContent();
						info("Gia tri trong tag " + column[i] + "la: " + da[i]);
					}
				}
			}
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			info("Co loi khi thuc hien WS");
		}
		return da;
	}

	public String callWSFullOutPut(String urlText, String data) {
		String output = "";
		String urlT = "";
		if (urlText != "" && !urlText.contains("http")) {
			urlT = System.getProperty("serviceLink") + urlText;
		} else {
			urlT = urlText;
		}
		try {
			URL url = new URL(urlT);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setRequestProperty("Accept", "text/xml");
			OutputStreamWriter osw = new OutputStreamWriter(
					connection.getOutputStream());
			osw.write(data);
			osw.flush();
			osw.close();
			InputStream in = connection.getInputStream();
			output = read(in);
			info("output cua WS: " + output);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return output;
	}

	public void verifyNotContains(String s1, String s2) {
		if (s1 != null && s2 != null && s2.contains(s1)) {
			info("Fail do chuoi " + s2 + "  van chua chuoi " + s1);
			Assert.assertFalse(s2.contains(s1));
		}
	}

	/**
	 * 
	 * @param object
	 */
	public void enter(Object locator) {
		if (locator != null) {
			WebElement e = getElementPresent(locator);
			e.sendKeys(Keys.ENTER);
		}
	}
	
	 public void waitForElementDisappear(Object locator){
		 By by = locator instanceof By ? (By)locator : By.xpath(locator.toString());
		 int i = 0;
		 while(i < 60){
			 try{					 
				 if(driver.findElement(by).isDisplayed()){
					 pause(1000);
					 i ++;
				 }					 
			 } catch(NoSuchElementException ex){
				 break;
			 } catch(StaleElementReferenceException ex){
				 break;
			 }
		 }
		 if (i == 60) {
			 Assert.fail("Qua thoi gian doi tuong van display");
		 }
	 }
	 
	    public void checkFileExist(String fileName) {
	    	String file = "";
	    	if (!fileName.contains("/") || !fileName.contains("\\")){
	    		file = System.getProperty("user.dir") + File.separator + "downloads" + File.separator + fileName;
	    	} else {
	    		file = fileName;
	    	}
	    	File f = new File(file);
	    	if(f.exists() && !f.isDirectory()) { 
	    	    info(fileName + " có tồn tại");
	    	    f.delete();
	    	}else {
	    		Assert.fail(fileName + " không tồn tại");
	    	}
		}
}
