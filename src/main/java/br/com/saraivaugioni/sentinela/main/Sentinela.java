package br.com.saraivaugioni.sentinela.main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import br.com.saraivaugioni.sentinela.util.files.ManipulateFiles;
import br.com.saraivaugioni.sentinela.util.language.LanguageCodes;
import br.com.saraivaugioni.sentinela.util.report.GeneratorExtentReport;
import br.com.saraivaugioni.sentinela.validation.Validation;


//tradução ok

public class Sentinela {

	private Path imgsPath;
	private Path reportPath;
	private Path baseLinePath;
	private WebDriver driverSelenium;
	private String dateTimeExecutionCurrent;
	private int elementsWidth = 80;
	private int elementsHeight = 40;
	private int imgWidth;
	private int imgHeight;
	private boolean isEnabledValidation = true;
	private boolean isBaseLineCreated = false;
	private boolean isDiff = false;
	public static LanguageCodes languageCode = LanguageCodes.EN_US;

	public Sentinela(WebDriver driver, String pathImgs, String pathReport, int imgWidth, int imgHeight,
			String baseLineName) {
		Path localPath = Paths.get(pathImgs);
		Path localhPathReport = Paths.get(pathReport);
		setImgsPath(localPath);
		setReportPath(localhPathReport);
		setDriverSelenium(driver);
		// Set current base line
		setBaseLinePath(baseLineName);
		// Setup resolution
		setWidthTela(imgWidth);
		setImgHeight(imgHeight);
		// Save current date time to be used as the historical test run
		// Salva a data e hora atual para ser usada como histórico da nova
		// bateria de execução;
		SimpleDateFormat currentDateTime = new SimpleDateFormat(ManipulateFiles.getListString("currentDateTimeFormat"));
		setDateTimeExecutionCurrent(currentDateTime.format(new Date()));
		// Prepara o ambiente
		setBaseLineCreated(ManipulateFiles.prepareEnvironment(getBaseLinePath(), getImgsPath(), getDateTimeExecutionCurrent()));
	}

	public Sentinela(WebDriver driver, String pathImgs, String pathReport, int imgWidth, int imgHeight) {
		Path localPath = Paths.get(pathImgs);
		Path localhPathReport = Paths.get(pathReport);
		setImgsPath(localPath);
		setReportPath(localhPathReport);
		setDriverSelenium(driver);
		// Set current base line as default value
		setBaseLinePath(ManipulateFiles.getListString("nameDirBaseline"));
		// Setup resolution
		setWidthTela(imgWidth);
		setImgHeight(imgHeight);
		// Save current date time to be used as the historical test run
		// Salva a data e hora atual para ser usada como histórico da nova
		// bateria de execução
		SimpleDateFormat currentDateTime = new SimpleDateFormat(ManipulateFiles.getListString("currentDateTimeFormat"));
		setDateTimeExecutionCurrent(currentDateTime.format(new Date()));
		// Prepara o ambiente
		setBaseLineCreated(ManipulateFiles.prepareEnvironment(getBaseLinePath(), getImgsPath(), getDateTimeExecutionCurrent()));
	}
	
	public Sentinela(WebDriver driver, String pathImgs, String pathReport, int imgWidth, int imgHeight,
			String baseLineName, LanguageCodes lc) {
		languageCode = lc;
		Path localPath = Paths.get(pathImgs);
		Path localhPathReport = Paths.get(pathReport);
		setImgsPath(localPath);
		setReportPath(localhPathReport);
		setDriverSelenium(driver);
		// Set current base line
		setBaseLinePath(baseLineName);
		// Setup resolution
		setWidthTela(imgWidth);
		setImgHeight(imgHeight);
		// Save current date time to be used as the historical test run
		// Salva a data e hora atual para ser usada como histórico da nova
		// bateria de execução;
		SimpleDateFormat currentDateTime = new SimpleDateFormat(ManipulateFiles.getListString("currentDateTimeFormat"));
		setDateTimeExecutionCurrent(currentDateTime.format(new Date()));
		// Prepara o ambiente
		setBaseLineCreated(ManipulateFiles.prepareEnvironment(getBaseLinePath(), getImgsPath(), getDateTimeExecutionCurrent()));
	}
	
	public Sentinela(WebDriver driver, String pathImgs, String pathReport, int imgWidth, int imgHeight, LanguageCodes lc) {
		languageCode = lc;
		Path localPath = Paths.get(pathImgs);
		Path localhPathReport = Paths.get(pathReport);
		setImgsPath(localPath);
		setReportPath(localhPathReport);
		setDriverSelenium(driver);
		// Set current base line as default value
		setBaseLinePath(ManipulateFiles.getListString("nameDirBaseline"));
		// Setup resolution
		setWidthTela(imgWidth);
		setImgHeight(imgHeight);
		// Save current date time to be used as the historical test run
		// Salva a data e hora atual para ser usada como histórico da nova
		// bateria de execução
		SimpleDateFormat currentDateTime = new SimpleDateFormat(ManipulateFiles.getListString("currentDateTimeFormat"));
		setDateTimeExecutionCurrent(currentDateTime.format(new Date()));
		// Prepara o ambiente
		setBaseLineCreated(ManipulateFiles.prepareEnvironment(getBaseLinePath(), getImgsPath(), getDateTimeExecutionCurrent()));
	}

	public Sentinela() {
		setEnabledValidation(false);
	}


	public void generateReport() {
		GeneratorExtentReport generatorExtentReport;
		if (!isEnabledValidation()) {
			return;
		}
		if (!isBaseLineCreated) {
			generatorExtentReport = new GeneratorExtentReport(getReportPath().toString(), getImgsPath() + "\\", getBaseLinePath() + "\\");
			generatorExtentReport.generateReport();
			//new GeneratorReport(getPathRelatorio().toString(), getImgsPath() + "\\", getPathBaseLine() + "\\");
		}
	}


	/**
	 * Valida o layout da janela inteira Recebe como parametro o nome da
	 * validação O método cria as imagens baseada no nome da validação
	 * 
	 * @param imageName
	 */
	public void validate(String imageName) {
		Validation.validate(this, imageName);
	}

	/**
	 * Valida o layout da janela inteira Recebe como parametro o nome da
	 * validação O método cria as imagens baseada no nome da validação
	 * 
	 * @param imageName
	 * @param testDetails
	 */
	public void validate(String imageName, String testDetails) {
		Validation.validate(this, imageName, testDetails);
	}

	/**
	 * Validar layout de um elemento especifico Recebe como parametro um
	 * elemento do selenium e o nome da validação. O método cria as imagens
	 * baseada no nome da validação. Ex.: nomeValidação: elementoNomeFornecedor
	 * elemento: campoNomeFornecedor Imagem gerada: elementoNomeFornecedor.png
	 * 
	 * @param element
	 * @param imageName
	 */
	public void validate(WebElement element, String imageName) {
		Validation.validate(this, element, imageName);
	}

	/**
	 * Validar layout de um elemento especifico Recebe como parametro um
	 * elemento do selenium e o nome da validação. O método cria as imagens
	 * baseada no nome da validação. Ex.: nomeValidação: elementoNomeFornecedor
	 * elemento: campoNomeFornecedor Imagem gerada: elementoNomeFornecedor.png
	 * 
	 * @param element
	 * @param imageName
	 * @param testDetails
	 */
	public void validate(WebElement element, String imageName, String testDetails) {
		Validation.validate(this, element, imageName, testDetails);
	}

	/**
	 * Validar layout de uma lista de elementos Recebe como parametro uma lista
	 * de elementos do selenium. List<WebElement> e o nome da validação o método
	 * cria as imagens baseada no nome de validação incrementando um contador
	 * para cada imagem. Ex.: nomeValidação: elementosTelaMateriais elementos:
	 * id_nomeMaterial, id_codigoMaterial Imagens que serão geradas:
	 * elementosTelaMateriais1.png, elementosTelaMateriais2.png
	 * 
	 * @param elements
	 * @param imageName
	 */
	public void validate(List<WebElement> elements, String imageName) {
		Validation.validate(this, elements, imageName);
	}

	/**
	 * Validar layout de uma lista de elementos Recebe como parametro uma lista
	 * de elementos do selenium. List<WebElement> e o nome da validação o método
	 * cria as imagens baseada no nome de validação incrementando um contador
	 * para cada imagem. Ex.: nomeValidação: elementosTelaMateriais elementos:
	 * id_nomeMaterial, id_codigoMaterial Imagens que serão geradas:
	 * elementosTelaMateriais1.png, elementosTelaMateriais2.png
	 * 
	 * @param elements
	 * @param imageName
	 * @param testDetails
	 */
	public void validate(List<WebElement> elements, String imageName, String testDetails) {
		Validation.validate(this, elements, imageName, testDetails);
	}

	public Path getImgsPath() {
		return imgsPath;
	}

	public void setImgsPath(Path imgsPath) {
		this.imgsPath = imgsPath;
	}

	public WebDriver getDriverSelenium() {
		return driverSelenium;
	}

	public void setDriverSelenium(WebDriver driverSelenium) {
		this.driverSelenium = driverSelenium;
	}

	public String getDateTimeExecutionCurrent() {
		return dateTimeExecutionCurrent;
	}

	private void setDateTimeExecutionCurrent(String dateTimeExecutionCurrent) {
		this.dateTimeExecutionCurrent = dateTimeExecutionCurrent;
	}

	public int getElementsWidth() {
		return elementsWidth;
	}

	public void setElementsWidth(int elementsWidth) {
		this.elementsWidth = elementsWidth;
	}

	public int getElementsHeight() {
		return elementsHeight;
	}

	public void setElementsHeight(int elementsHeight) {
		this.elementsHeight = elementsHeight;
	}

	public int getImgWidth() {
		return imgWidth;
	}

	public void setWidthTela(int imgWidth) {
		this.imgWidth = imgWidth;
	}

	public int getImgHeight() {
		return imgHeight;
	}

	public void setImgHeight(int imgHeight) {
		this.imgHeight = imgHeight;
	}

	public void setReportPath(Path pathRelatorio) {
		this.reportPath = pathRelatorio;
	}

	public Path getReportPath() {
		return reportPath;
	}

	public Path getBaseLinePath() {
		return baseLinePath;
	}

	private void setBaseLinePath(String nomeBaseLine) {
		this.baseLinePath = Paths.get(getImgsPath() + "\\" + nomeBaseLine);
	}

	public boolean isEnabledValidation() {
		return isEnabledValidation;
	}

	public void setEnabledValidation(boolean isEnabledValidation) {
		this.isEnabledValidation = isEnabledValidation;
	}

	public boolean isDiff() {
		return isDiff;
	}

	public void setDiff(boolean isDiff) {
		this.isDiff = isDiff;
	}

	public boolean isBaseLineCreated() {
		return isBaseLineCreated;
	}

	public void setBaseLineCreated(boolean isBaseLineCreated) {
		this.isBaseLineCreated = isBaseLineCreated;
	}
	
}