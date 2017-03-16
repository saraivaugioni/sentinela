package br.com.saraivaugioni.sentinelaAPI.main;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import br.com.saraivaugioni.sentinelaAPI.util.files.ManipulateFiles;
import br.com.saraivaugioni.sentinelaAPI.util.report.GeneratorExtentReport;
import br.com.saraivaugioni.sentinelaAPI.util.report.GeneratorReport;
import br.com.saraivaugioni.sentinelaAPI.validation.Validation;

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

	public Sentinela(WebDriver driver, String pathImgs, String pathReport, int imgWidth, int imgHeight,
			String baseLineName) {
		Path localPath = Paths.get(pathImgs);
		Path localhPathReport = Paths.get(pathReport);
		setImgsPath(localPath);
		setPathRelatorio(localhPathReport);
		setDriverSelenium(driver);
		// Set current base line
		setBaseLine(baseLineName);
		// Setup resolution
		setWidthTela(imgWidth);
		setHeightTela(imgHeight);
		// Save current date time to be used as the historical test run
		// Salva a data e hora atual para ser usada como histórico da nova
		// bateria de execução;
		SimpleDateFormat currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		setDateTimeExecutionCurrent(currentDateTime.format(new Date()));
		// Prepara o ambiente
		setBaseLineCreated(ManipulateFiles.prepareEnvironment(getPathBaseLine(), getImgsPath(), getDateTimeExecutionCurrent()));
	}

	public Sentinela(WebDriver driver, String pathImgs, String pathReport, int imgWidth, int imgHeight) {
		Path localPath = Paths.get(pathImgs);
		Path localhPathReport = Paths.get(pathReport);
		setImgsPath(localPath);
		setPathRelatorio(localhPathReport);
		setDriverSelenium(driver);
		// Set current base line as default value
		setBaseLine("baseLine");
		// Setup resolution
		setWidthTela(imgWidth);
		setHeightTela(imgHeight);
		// Save current date time to be used as the historical test run
		// Salva a data e hora atual para ser usada como histórico da nova
		// bateria de execução
		SimpleDateFormat currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		setDateTimeExecutionCurrent(currentDateTime.format(new Date()));
		// Prepara o ambiente
		setBaseLineCreated(ManipulateFiles.prepareEnvironment(getPathBaseLine(), getImgsPath(), getDateTimeExecutionCurrent()));
	}

	public Sentinela() {
		setEnabledValidation(false);
	}


	public void generateReport() {
		GeneratorExtentReport generatorExtentReport;
		if (!isValidacaoAtivada()) {
			return;
		}
		if (!isBaseLineCreated) {
			generatorExtentReport = new GeneratorExtentReport(getPathRelatorio().toString(), getImgsPath() + "\\", getPathBaseLine() + "\\");
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

	public int getWidthElementos() {
		return elementsWidth;
	}

	public void setWidthElementos(int widthElementos) {
		this.elementsWidth = widthElementos;
	}

	public int getHeightElementos() {
		return elementsHeight;
	}

	public void setHeightElementos(int heightElementos) {
		this.elementsHeight = heightElementos;
	}

	public int getWidthTela() {
		return imgWidth;
	}

	public void setWidthTela(int widthTela) {
		this.imgWidth = widthTela;
	}

	public int getHeightTela() {
		return imgHeight;
	}

	public void setHeightTela(int heightTela) {
		this.imgHeight = heightTela;
	}

	public void setPathRelatorio(Path pathRelatorio) {
		this.reportPath = pathRelatorio;
	}

	public Path getPathRelatorio() {
		return reportPath;
	}

	public Path getPathBaseLine() {
		return baseLinePath;
	}

	private void setBaseLine(String nomeBaseLine) {
		this.baseLinePath = Paths.get(getImgsPath() + "\\" + nomeBaseLine);
	}

	public boolean isValidacaoAtivada() {
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