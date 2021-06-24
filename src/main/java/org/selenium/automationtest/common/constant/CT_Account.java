package org.selenium.automationtest.common.constant;

import org.openqa.selenium.By;

public class CT_Account {
	public static By ELEMENT_USERNAME_TEXTBOX = By.xpath("//*[@id='username']|//*[@class='user-name']/input");
	public static By ELEMENT_PASSWORD_TEXTBOX = By.xpath("//*[@id='password']|//*[@class='password']/input");
	public static By ELEMENT_LOGIN_BUTTON = By.xpath("//*[@value='ĐĂNG NHẬP']|//*[@value='Log in' or @value='Login']|//button[contains(text(),'Đăng nhập')]");
	
}
