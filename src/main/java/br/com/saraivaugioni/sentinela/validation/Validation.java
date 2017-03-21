package br.com.saraivaugioni.sentinela.validation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.openqa.selenium.WebElement;

import br.com.saraivaugioni.sentinela.main.Sentinela;
import br.com.saraivaugioni.sentinela.util.files.ManipulateFiles;
import br.com.saraivaugioni.sentinela.util.images.CompareImages;
import br.com.saraivaugioni.sentinela.util.images.PrintsScreen;

public class Validation {

	/**
	 * Valida o layout da janela inteira Recebe como parametro o nome da
	 * validação O método cria as imagens baseada no nome da validação
	 * 
	 * @param imageName
	 */
	public static void validate(Sentinela sentinela, String imageName) {
		if (!sentinela.isEnabledValidation()) {
			return;
		}

		File baseLineFile = new File(sentinela.getBaseLinePath() + "\\" + imageName + "."+ManipulateFiles.getListString("imgExtension"));
		File printFile = PrintsScreen.printWindowBrowser(sentinela.getDriverSelenium());

		// Verifica se já existe arquivo para essa validação na baseline.
		// Check if file exits for this validation in baseline.
		if (baseLineFile.exists()) {
			// Verifica se o arquivo de comparação já existe. Se exisitr ele é
			// removido.
			// Check if comparation file exists. If true it is removed.
			File comparisonFile = new File(sentinela.getImgsPath() + "\\" + sentinela.getDateTimeExecutionCurrent()
					+ "\\" + imageName + "."+ManipulateFiles.getListString("imgExtension"));
			if (comparisonFile.exists()) {
				ManipulateFiles.removeFile(comparisonFile.getAbsolutePath());
			}
			// Cria um print novo com prefixo comparar+nomeValidacao.png
			// Make new print with prefix comparar+validationName.png
			PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
					"\\" + sentinela.getDateTimeExecutionCurrent() + "\\" + imageName, sentinela.getImgWidth(),
					sentinela.getImgHeight());
			// Compara print atual com print da base line
			// Compare actual print with baseline print.
			CompareImages.compare(sentinela, imageName + "."+ManipulateFiles.getListString("imgExtension"), imageName);
		}
		// Se não existe, cria um print novo na base line
		// If dont exists, make new print in baseline.
		else {
			PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
					"\\" + sentinela.getBaseLinePath().getFileName() + "\\" + imageName, sentinela.getImgWidth(),
					sentinela.getImgHeight());
		}
	}

	/**
	 * Valida o layout da janela inteira Recebe como parametro o nome da
	 * validação O método cria as imagens baseada no nome da validação
	 * 
	 * @param imageName
	 * @param testDetails
	 */
	public static void validate(Sentinela sentinela, String imageName, String testDetails) {
		if (!sentinela.isEnabledValidation()) {
			return;
		}
		File baseLineFile = new File(sentinela.getBaseLinePath() + "\\" + imageName + "."+ManipulateFiles.getListString("imgExtension"));
		File printFile = PrintsScreen.printWindowBrowser(sentinela.getDriverSelenium());
		// Verifica se já existe arquivo para essa validação na baseline.
		if (baseLineFile.exists()) {
			// Verifica se o arquivo de comparação já existe. Se exisitr ele é
			// removido.
			File comparisonFile = new File(sentinela.getImgsPath() + "\\" + sentinela.getDateTimeExecutionCurrent()
					+ "\\" + imageName + "."+ManipulateFiles.getListString("imgExtension"));
			if (comparisonFile.exists()) {
				ManipulateFiles.removeFile(comparisonFile.getAbsolutePath());
			}
			// Cria um print novo com prefixo comparar+nomeValidacao.png
			PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
					"\\" + sentinela.getDateTimeExecutionCurrent() + "\\" + imageName, sentinela.getImgWidth(),
					sentinela.getImgHeight());
			// Compara print atual com print da base line
			CompareImages.compare(sentinela,imageName + "."+ManipulateFiles.getListString("imgExtension"), testDetails);
		}
		// Se não existe, cria um print novo
		else {
			PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
					"\\" + sentinela.getBaseLinePath().getFileName() + "\\" + imageName, sentinela.getImgWidth(),
					sentinela.getImgHeight());
		}
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
	public static void validate(Sentinela sentinela, WebElement element, String imageName) {
		if (!sentinela.isEnabledValidation()) {
			return;
		}
		File baseLineFile = new File(sentinela.getBaseLinePath() + "\\" + imageName + "."+ManipulateFiles.getListString("imgExtension"));
		File printFile = null;

		try {
			printFile = PrintsScreen.printWebElement(element, sentinela.getDriverSelenium());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Verifica se já existe arquivo para essa validação na baseline.
		if (baseLineFile.exists()) {
			// Verifica se o arquivo de comparação já existe. Se exisitr ele é
			// removido.
			File comparisonFile = new File(sentinela.getImgsPath() + "\\" + sentinela.getDateTimeExecutionCurrent()
					+ "\\" + imageName + "."+ManipulateFiles.getListString("imgExtension"));
			if (comparisonFile.exists()) {
				ManipulateFiles.removeFile(comparisonFile.getAbsolutePath());
			}
			PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
					"\\" + sentinela.getDateTimeExecutionCurrent() + "\\" + imageName, sentinela.getElementsWidth(),
					sentinela.getElementsHeight());
			// Compara print atual com print da base line
			CompareImages.compare(sentinela,imageName + "."+ManipulateFiles.getListString("imgExtension"), imageName);
		}
		// Se não existe, cria um print novo com prefixo
		else {
			PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
					"\\" + sentinela.getBaseLinePath().getFileName() + "\\" + imageName, sentinela.getElementsWidth(),
					sentinela.getElementsHeight());
		}
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
	public static void validate(Sentinela sentinela, WebElement element, String imageName, String testDetails) {
		if (!sentinela.isEnabledValidation()) {
			return;
		}
		File baseLineFile = new File(sentinela.getBaseLinePath() + "\\" + imageName + "."+ManipulateFiles.getListString("imgExtension"));
		File printFile = null;

		try {
			printFile = PrintsScreen.printWebElement(element, sentinela.getDriverSelenium());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Verifica se já existe arquivo para essa validação na baseline.
		if (baseLineFile.exists()) {
			// Verifica se o arquivo de comparação já existe. Se exisitr ele é
			// removido.
			File comparisonFile = new File(sentinela.getImgsPath() + "\\" + sentinela.getDateTimeExecutionCurrent()
					+ "\\" + imageName + "."+ManipulateFiles.getListString("imgExtension"));
			if (comparisonFile.exists()) {
				ManipulateFiles.removeFile(comparisonFile.getAbsolutePath());
			}
			PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
					"\\" + sentinela.getDateTimeExecutionCurrent() + "\\" + imageName,
					sentinela.getElementsWidth(), sentinela.getElementsHeight());
			// Compara print atual com print da base line
			CompareImages.compare(sentinela,imageName + "."+ManipulateFiles.getListString("imgExtension"), testDetails);
		}
		// Se não existe, cria um print novo com prefixo
		else {
			PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
					"\\" + sentinela.getBaseLinePath().getFileName() + "\\" + imageName,
					sentinela.getElementsWidth(), sentinela.getElementsHeight());
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
	 * @param imageName
	 */
	public static void validate(Sentinela sentinela, List<WebElement> elements, String imageName) {
		if (!sentinela.isEnabledValidation()) {
			return;
		}
		int cx = 0;
		for (WebElement element : elements) {
			File baseLineFile = new File(sentinela.getBaseLinePath() + "\\" + imageName + cx + "."+ManipulateFiles.getListString("imgExtension"));
			File printFile = null;

			try {
				printFile = PrintsScreen.printWebElement(element, sentinela.getDriverSelenium());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Verifica se já existe arquivo para essa validação na baseline.
			if (baseLineFile.exists()) {
				// Verifica se o arquivo de comparação já existe. Se exisitr ele
				// é removido.
				File comparisonFile = new File(sentinela.getImgsPath() + "\\" + sentinela.getDateTimeExecutionCurrent()
						+ "\\"+ManipulateFiles.getListString("imgComparePrefix")+"_" + imageName + cx + "."+ManipulateFiles.getListString("imgExtension"));
				if (comparisonFile.exists()) {
					ManipulateFiles.removeFile(comparisonFile.getAbsolutePath());
				}
				PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
						"\\" + sentinela.getDateTimeExecutionCurrent() + "\\" + imageName + cx,
						sentinela.getElementsWidth(), sentinela.getElementsHeight());
				// Compara print atual com print da base line
				CompareImages.compare(sentinela,imageName + cx + "."+ManipulateFiles.getListString("imgExtension"), imageName);
			}
			// Se não existe, cria um print novo com prefixo
			// original_+nomeValidacao.png
			else {
				PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
						"\\" + sentinela.getBaseLinePath().getFileName() + "\\" + imageName + cx,
						sentinela.getElementsWidth(), sentinela.getElementsHeight());
			}
			cx++;
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
	 * @param imageName
	 * @param testDetails
	 */
	public static void validate(Sentinela sentinela, List<WebElement> elements, String imageName, String testDetails) {
		if (!sentinela.isEnabledValidation()) {
			return;
		}
		int cx = 0;
		for (WebElement element : elements) {
			File baseLineFile = new File(sentinela.getBaseLinePath() + "\\" + imageName + cx + "."+ManipulateFiles.getListString("imgExtension"));
			File printFile = null;

			try {
				printFile = PrintsScreen.printWebElement(element, sentinela.getDriverSelenium());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Verifica se já existe arquivo para essa validação na baseline.
			if (baseLineFile.exists()) {
				// Verifica se o arquivo de comparação já existe. Se exisitr ele
				// é removido.
				File comparisonFile = new File(sentinela.getImgsPath() + "\\" + sentinela.getDateTimeExecutionCurrent()
						+ "\\"+ManipulateFiles.getListString("imgComparePrefix")+"_" + imageName + cx + "."+ManipulateFiles.getListString("imgExtension"));
				if (comparisonFile.exists()) {
					ManipulateFiles.removeFile(comparisonFile.getAbsolutePath());
				}
				PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
						"\\" + sentinela.getDateTimeExecutionCurrent() + "\\" + imageName + cx,
						sentinela.getElementsWidth(), sentinela.getElementsHeight());
				// Compara print atual com print da base line
				CompareImages.compare(sentinela,imageName + cx + "."+ManipulateFiles.getListString("imgExtension"), testDetails);
			}
			// Se não existe, cria um print novo com prefixo
			// original_+nomeValidacao.png
			else {
				PrintsScreen.savePrint(sentinela.getImgsPath(), printFile,
						"\\" + sentinela.getBaseLinePath().getFileName() + "\\" + imageName + cx,
						sentinela.getElementsWidth(), sentinela.getElementsHeight());
			}
			cx++;
		}
	}

}
