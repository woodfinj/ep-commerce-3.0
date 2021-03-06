package com.elasticpath.cucumber.definitions;

import java.util.Date;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

import com.elasticpath.selenium.dialogs.ProblemOccurredDialog;
import com.elasticpath.selenium.setup.SetUp;
import com.elasticpath.selenium.util.Constants;

/**
 * Test Base class contains some methods for Before and After tests to be run for each test.
 */
public class TestBase {
	private static boolean screenShotTaken;
	private static final Logger LOGGER = Logger.getLogger(TestBase.class);
	private ProblemOccurredDialog problemOccurredDialog;

	/**
	 * For After hooks, higher order number run first.
	 * Default order value is 10000
	 */

	/**
	 * Takes screenshot on scenario failure, it will run after scenario's steps and its @After hook.
	 *
	 * @param scenario Scenario
	 */
	@After(order = 1)
	public void tearDown(final Scenario scenario) {
		try {
			if (scenario.isFailed() && !screenShotTaken && SetUp.getDriverInstance() != null) {
				final byte[] screenshot = ((TakesScreenshot) SetUp.getDriver()).getScreenshotAs(OutputType.BYTES);
				scenario.embed(screenshot, "image/png");
			}
		} catch (UnhandledAlertException uae) {
			SetUp.getDriver().switchTo().alert().accept();
		} finally {
			SetUp.quitDriver();
		}
	}

	/**
	 * Captures the logs from the chromeDriver.
	 */
	private void analyzeLog() {
		LogEntries logEntries = SetUp.getDriver().manage().logs().get(LogType.DRIVER);
		for (LogEntry entry : logEntries) {
			LOGGER.debug(new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
		}
	}

	/**
	 * Takes screenshot on scenario step failure, it will run after scenario's steps but before its @After hook.
	 *
	 * @param scenario Scenario
	 */
	@After(order = Constants.SCREENSHOT_ORDER_NUMBER)
	public void tearDown1(final Scenario scenario) {

		if (scenario.isFailed() && SetUp.getDriverInstance() != null) {
			handleProblemOccuredDialogPresent();

			LOGGER.debug("START of failure logs for scenario " + scenario.getName());
			analyzeLog();
			LOGGER.debug("END of failure logs for scenario " + scenario.getName());
			try {
				final byte[] screenshot = ((TakesScreenshot) SetUp.getDriver()).getScreenshotAs(OutputType.BYTES);
				scenario.embed(screenshot, "image/png");

				String html = "<b>Page Source</b><br>"
						+ "<textarea rows='50' cols='150'>" + SetUp.getDriver().getPageSource() + "</textarea> ";
				scenario.embed(html.getBytes(), "text/html");

				screenShotTaken = true;
			} catch (UnhandledAlertException uae) {
				SetUp.getDriver().switchTo().alert().accept();
			}
		}
	}

	/**
	 * Checks if `Problem Occured` Dialog is present, and if present, clicks the Details
	 * button on the dialog.
	 */
	private void handleProblemOccuredDialogPresent() {
		problemOccurredDialog = new ProblemOccurredDialog(SetUp.getDriver());
		if (problemOccurredDialog.isDialogPresent()) {
			problemOccurredDialog.clickDetailsButton();
		}
	}
}
