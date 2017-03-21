package br.com.saraivaugioni.sentinela.util.report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.relevantcodes.extentreports.NetworkMode;

import br.com.saraivaugioni.sentinela.util.files.ManipulateFiles;

public class GenericReport {

	private ExtentReports extent;
	private List<ExtentTest> myTests = new ArrayList<ExtentTest>();

	public GenericReport() {
		newReport();
	}

	public GenericReport(String dirReport) {
		newReport(dirReport);
	}

	private void newReport() {
		extent = new ExtentReports(ManipulateFiles.getListString("dirTarget"), NetworkMode.OFFLINE);
		configReport();
	}

	private void newReport(String dirReport) {
		extent = new ExtentReports(dirReport + "\\"+ManipulateFiles.getListString("reportIndex"), NetworkMode.OFFLINE);
		configReport();
	}
	
	private void configReport(){
		ClassLoader classLoader = getClass().getClassLoader();
		//Configuração usada pelo Maven, abre o arquivo no resource.
		extent.loadConfig(new File(classLoader.getResource(ManipulateFiles.getListString("extentConfigFile")).getFile()));
		//Configuração usada pelo .jar, abre o arquivo no disco(mesma pasta do .jar)
		extent.loadConfig(new File(ManipulateFiles.getListString("extentConfigFile")));
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

	public void addTagTest(String testName, String... tags) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				myTest.assignCategory(tags);
			}
		}
	}

	public void addLogTestFail(String testName, String stepName, String details, String... imageFile) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				String listImgs = "";
				for (String img : imageFile) {
					String[] imgInfo = img.split(";");
					try{
						listImgs += "<br><b>"+imgInfo[1]+"</b>";
					}catch(IndexOutOfBoundsException ex){
						
					}
					listImgs += myTest.addScreenCapture(imgInfo[0]);
				}
				myTest.log(LogStatus.FAIL, stepName, details + listImgs);
			}
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

	public void addLogTestPass(String testName, String stepName, String details, String... imageFile) {
		for (ExtentTest myTest : myTests) {
			if (myTest.getTest().getName().trim().equals(testName)) {
				String listImgs = "";
				for (String img : imageFile) {
					String[] imgInfo = img.split(";");
					try{
						listImgs += "<br><b>"+imgInfo[1]+"</b>";
					}catch(IndexOutOfBoundsException ex){
						
					}
					listImgs += myTest.addScreenCapture(imgInfo[0]);
				}
				myTest.log(LogStatus.PASS, stepName, details + listImgs);
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

	public void endReport() {
		if (extent != null) {
			extent.flush();
		}
	}

}
