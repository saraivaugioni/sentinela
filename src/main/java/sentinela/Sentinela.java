package sentinela;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
		prepareEnvironment();
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
		prepareEnvironment();
	}

	public Sentinela() {
		setEnabledValidation(false);
	}

	private void savePrint(File tempFilePrintSelenium, String finalPrintName, int width, int height) {
		String fileName = getImgsPath().toString() + finalPrintName + ".png";
		// Cria a imagem no disco.
		try {
			FileUtils.copyFile(tempFilePrintSelenium, new File(fileName), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Alterar resolução da imagem.
		// Change image resolution
		BufferedImage img = new ImgUtils().scaleImage(width, height, fileName);
		File filePrintNewResolution = new File(fileName);
		try {
			ImageIO.write(img, "png", filePrintNewResolution);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Janela completa
	// Full window(browser)
	private File printWindowBrowser() {
		File scrFile = ((TakesScreenshot) getDriverSelenium()).getScreenshotAs(OutputType.FILE);
		return scrFile;
	}

	// Elemento especifico
	// print only a WebElement
	private File printWebElement(WebElement element) throws IOException {
		File scrFile = ((TakesScreenshot) getDriverSelenium()).getScreenshotAs(OutputType.FILE);
		Point p = element.getLocation();
		int width = element.getSize().getWidth();
		int height = element.getSize().getHeight();
		BufferedImage img = null;
		BufferedImage dest = null;
		img = ImageIO.read(scrFile);
		if (element.isDisplayed()) {
			dest = img.getSubimage(p.getX(), p.getY(), width, height);
		} else {
			dest = img.getSubimage(0, 0, 1, 1);
		}
		ImageIO.write(dest, "png", scrFile);
		return scrFile;
	}

	private void saveHeadMetaData(String head) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(
					getImgsPath() + "\\" + getDateTimeExecutionCurrent() + "\\Resultados\\metadados.ini", true);
			out = new BufferedWriter(fstream);
			out.write(head + "\n");
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveInfMetaData(String inf) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(
					getImgsPath() + "\\" + getDateTimeExecutionCurrent() + "\\Resultados\\metadados.ini", true);
			out = new BufferedWriter(fstream);
			out.write(inf + "\n");
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateReport() {
		if (!isValidacaoAtivada()) {
			return;
		}
		if (!isBaseLineCreated) {
			new GeneratorReport(getPathRelatorio().toString(), getImgsPath() + "\\", getPathBaseLine() + "\\");
		}
	}

	private void compare(String imgName, String validationName) {
		BufferedImage img1 = null;
		BufferedImage img2 = null;
		CompareImages comparator = new CompareImages();
		try {
			img1 = ImageIO.read(new File(getPathBaseLine() + "\\" + imgName));
			img2 = ImageIO.read(new File(getImgsPath() + "\\" + getDateTimeExecutionCurrent() + "\\" + imgName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage outImg = comparator.getDifferenceImage(img1, img2);
		File outputfile = new File(getImgsPath() + "\\" + getDateTimeExecutionCurrent() + "\\Resultados\\" + imgName);
		try {
			ImageIO.write(outImg, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		saveInfMetaData(validationName + ";" + imgName + ";" + comparator.getPercentualDiferencaUltimaImagem() + ";"
				+ comparator.getQtdTotalPixelComparadosUltimaImagem() + ";"
				+ comparator.getQtdPixelDiferentesUltimaImagem());
		setDiff(comparator.isDiff());
	}

	public double compare(File img1, File img2) {
		BufferedImage bfImg1 = null;
		BufferedImage bfImg2 = null;
		CompareImages comparator = new CompareImages();
		try {
			bfImg1 = ImageIO.read(img1);
			bfImg2 = ImageIO.read(img2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		comparator.getDifferenceImage(bfImg1, bfImg2);
		return comparator.getPercentualDiferencaUltimaImagem();
	}

	private void removeFile(String fileFullName) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileFullName));
			out.write("aString1\n");
			out.close();
			new File(fileFullName).delete();
		} catch (IOException e) {
			System.out.println("Error remove file: " + fileFullName + ". " + e.getMessage());
		}
	}

	private void prepareEnvironment() {
		if (!isValidacaoAtivada()) {
			return;
		}
		Path dirBaseLine = getPathBaseLine();
		Path dirComparison = Paths.get(getImgsPath() + "\\" + getDateTimeExecutionCurrent());
		Path dirComparisonResults = Paths.get(dirComparison + "\\Resultados");
		// Se o diretório do baseLine não existir, ele é criado.
		if (!Files.exists(dirBaseLine)) {
			File fdiretoriobaseLine = new File(dirBaseLine.toString());
			fdiretoriobaseLine.mkdir();
			isBaseLineCreated = true;
			return;
		}
		// Se o diretório de comparacao não existir, ele é criado.
		if (!Files.exists(dirComparison)) {
			File fdiretoriocomparacao = new File(dirComparison.toString());
			File fdiretoriocomparacaoresultado = new File(dirComparisonResults.toString());
			fdiretoriocomparacao.mkdir();
			fdiretoriocomparacaoresultado.mkdir();
			// Cria o arquivo de metadados
			saveHeadMetaData(dirComparison.getFileName().toString());
		}
	}

	/**
	 * Valida o layout da janela inteira Recebe como parametro o nome da
	 * validação O método cria as imagens baseada no nome da validação
	 * 
	 * @param validateName
	 */
	public void validate(String validateName) {
		if (!isValidacaoAtivada()) {
			return;
		}
		File baseLineFile = new File(getPathBaseLine() + "\\" + validateName + ".png");
		// Verifica se já existe arquivo para essa validação na baseline.
		if (baseLineFile.exists()) {
			// Verifica se o arquivo de comparação já existe. Se exisitr ele é
			// removido.
			File comparisonFile = new File(
					getImgsPath() + "\\" + getDateTimeExecutionCurrent() + "\\" + validateName + ".png");
			if (comparisonFile.exists()) {
				removeFile(comparisonFile.getAbsolutePath());
			}
			// Cria um print novo com prefixo comparar+nomeValidacao.png
			savePrint(printWindowBrowser(), "\\" + getDateTimeExecutionCurrent() + "\\" + validateName, getWidthTela(),
					getHeightTela());
			// Compara print atual com print da base line
			compare(validateName + ".png", validateName);
		}
		// Se não existe, cria um print novo
		else {
			savePrint(printWindowBrowser(), "\\" + getPathBaseLine().getFileName() + "\\" + validateName,
					getWidthTela(), getHeightTela());
		}
	}

	/**
	 * Validar layout de um elemento especifico Recebe como parametro um
	 * elemento do selenium e o nome da validação. O método cria as imagens
	 * baseada no nome da validação. Ex.: nomeValidação: elementoNomeFornecedor
	 * elemento: campoNomeFornecedor Imagem gerada: elementoNomeFornecedor.png
	 * 
	 * @param element
	 * @param validateName
	 */
	public void validate(WebElement element, String validateName) {
		if (!isValidacaoAtivada()) {
			return;
		}
		File baseLineFile = new File(getPathBaseLine() + "\\" + validateName + ".png");
		// Verifica se já existe arquivo para essa validação na baseline.
		if (baseLineFile.exists()) {
			// Verifica se o arquivo de comparação já existe. Se exisitr ele é
			// removido.
			File comparisonFile = new File(
					getImgsPath() + "\\" + getDateTimeExecutionCurrent() + "\\" + validateName + ".png");
			if (comparisonFile.exists()) {
				removeFile(comparisonFile.getAbsolutePath());
			}
			// Cria um print novo com prefixo comparar+nomeValidacao.png
			try {
				savePrint(printWebElement(element), "\\" + getDateTimeExecutionCurrent() + "\\" + validateName,
						getWidthElementos(), getHeightElementos());
			} catch (IOException e) {
				System.out.println("Error to take element image. Element id: " + element.getAttribute("id"));
			}
			// Compara print atual com print da base line
			compare(validateName + ".png", validateName);
		}
		// Se não existe, cria um print novo com prefixo
		else {
			try {
				savePrint(printWebElement(element), "\\" + getPathBaseLine().getFileName() + "\\" + validateName,
						getWidthElementos(), getHeightElementos());
			} catch (IOException e) {
				System.out.println("Error to take element image. Element id: " + element.getAttribute("id"));
			}
		}
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
	 * @param validateName
	 */
	public void validate(List<WebElement> elements, String validateName) {
		if (!isValidacaoAtivada()) {
			return;
		}
		int cx = 0;
		for (WebElement element : elements) {
			File baseLineFile = new File(getPathBaseLine() + "\\" + validateName + cx + ".png");
			// Verifica se já existe arquivo para essa validação na baseline.
			if (baseLineFile.exists()) {
				// Verifica se o arquivo de comparação já existe. Se exisitr ele
				// é removido.
				File comparisonFile = new File(getImgsPath() + "\\" + getDateTimeExecutionCurrent() + "\\comparar_"
						+ validateName + cx + ".png");
				if (comparisonFile.exists()) {
					removeFile(comparisonFile.getAbsolutePath());
				}
				// Cria um print novo com prefixo comparar+nomeValidacao.png
				try {
					savePrint(printWebElement(element), "\\" + getDateTimeExecutionCurrent() + "\\" + validateName + cx,
							getWidthElementos(), getHeightElementos());
				} catch (IOException e) {
					System.out.println("Error to take element image. Element id: " + element.getAttribute("id"));
				}
				// Compara print atual com print da base line
				compare(validateName + cx + ".png", validateName);
			}
			// Se não existe, cria um print novo com prefixo
			// original_+nomeValidacao.png
			else {
				try {
					savePrint(printWebElement(element),
							"\\" + getPathBaseLine().getFileName() + "\\" + validateName + cx, getWidthElementos(),
							getHeightElementos());
				} catch (IOException e) {
					System.out.println("Error to take element image. Element id: " + element.getAttribute("id"));
				}
			}
			cx++;
		}
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

	private void setDiff(boolean isDiff) {
		this.isDiff = isDiff;
	}
}