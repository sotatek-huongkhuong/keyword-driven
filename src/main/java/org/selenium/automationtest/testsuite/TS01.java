package org.selenium.automationtest.testsuite;
import org.openqa.selenium.By;
import org.selenium.automationtest.common.CommonBase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.annotations.Test;
public final class TS01 extends CommonBase {
	  WebDriver driver;
  @BeforeMethod
  public void init() {}
  @AfterMethod
  public void after() {}
  @Test
  public void TC_01() {
    type(By.id("Email"),"Phuongnam.utehy@gmail.com",true);
    waitForPageLoaded(driver);
    type(By.id("Password"),"phuongnam19991",true);
    waitForPageLoaded(driver);
    click(By.xpath("//button[@type='submit' and text()='Log in']"));
    waitForPageLoaded(driver);
    pause(5000);}
}