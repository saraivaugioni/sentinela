package br.com.saraivaugioni.sentinelaAPI.util.report;

import java.util.List;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.NetworkMode;

public class GenericReport {

	private ExtentReports extent;
	private List<ExtentTest> myTests;

	public GenericReport() {
		newReport();
	}

	private void newReport() {
		extent = new ExtentReports("target", NetworkMode.OFFLINE);
	}

	public void startNewTest(String testName, String testDescription) {
		ExtentTest test = extent.startTest(testName, testDescription);
		myTests.add(test);
	}

	public void endTest(String testName) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				extent.endTest(myTest);
			}
		}
	}

	public void endAllTest() {
		for (ExtentTest myTest : myTests) {
			extent.endTest(myTest);
		}
	}

	public void addLogTestFail(String testName, String stepName, String details) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				myTest.log(LogStatus.FAIL, stepName, details);
			}
		}
	}

	public void addLogTestError(String testName, String stepName, String details) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				myTest.log(LogStatus.ERROR, stepName, details);
			}
		}
	}
	
	public void addLogTestFatal(String testName, String stepName, String details) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				myTest.log(LogStatus.FATAL, stepName, details);
			}
		}
	}
	
	public void addLogTestInfo(String testName, String stepName, String details) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				myTest.log(LogStatus.INFO, stepName, details);
			}
		}
	}
	
	public void addLogTestPass(String testName, String stepName, String details) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				myTest.log(LogStatus.PASS, stepName, details);
			}
		}
	}
	
	public void addLogTestSkip(String testName, String stepName, String details) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				myTest.log(LogStatus.SKIP, stepName, details);
			}
		}
	}
	
	public void addLogTestUnknown(String testName, String stepName, String details) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				myTest.log(LogStatus.UNKNOWN, stepName, details);
			}
		}
	}
	
	public void addLogTestWarning(String testName, String stepName, String details) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				myTest.log(LogStatus.WARNING, stepName, details);
			}
		}
	}
	
}
