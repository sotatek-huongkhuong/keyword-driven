package org.selenium.automationtest.common;

import org.apache.log4j.Level;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestReport extends TestListenerAdapter {
	private void logResult(ITestResult result) {
		Level level = Level.INFO;
		String message = "";
		switch (result.getStatus()) {
			case ITestResult.STARTED:
				message = "---------TEST STARTED-----------";
				break;
			case ITestResult.FAILURE:
				level = Level.ERROR;
				message = "---------TEST ERROR-------------";
				break;
			case ITestResult.SKIP:
				message = "---------TEST SKIPPED-----------";
				break;
			case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
				level = Level.WARN;
				break;
			case ITestResult.SUCCESS:
				message = "---------TEST SUCCESS------------";
				break;
		}
		TestLogger.log(message, level);
	}
	
	@Override
	public void onTestStart(ITestResult result) {
		logResult(result);
	}
		
	@Override
	public void onTestSuccess(ITestResult result) {
		logResult(result);
	}
	
	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		logResult(result);
	}
}
