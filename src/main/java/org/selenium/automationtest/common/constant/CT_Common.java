
package org.selenium.automationtest.common.constant;

import org.openqa.selenium.By;

public class CT_Common {
	 
	public static final String DB = "10.58.71.214";
	public static final String SID = "dbora";
	public static final String PORT = "1521";
	public static final String USERNAME = "BCCS_SALE";	
	public static final String PASSWORD = "sale";
	public static By ELEMENT_SOLR_USERNAME = By.id("username");
	public static By ELEMENT_SOLR_PASS = By.id("password");
	public static By ELEMENT_SOLR_LOGIN_BUTTON = By.xpath("//button[@type='submit']");
	public static String userSolr = "bccs2_sale";
	public static String passSolr = "Bccs@cbs#0320";
}
