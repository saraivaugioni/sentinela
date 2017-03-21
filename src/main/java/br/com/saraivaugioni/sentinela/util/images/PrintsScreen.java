package br.com.saraivaugioni.sentinela.util.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import br.com.saraivaugioni.sentinela.util.files.ManipulateFiles;

public class PrintsScreen {
	
	// Janela completa
	// Full window(browser)
	public static File printWindowBrowser(WebDriver driver) {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		return scrFile;
	}
	
	// Elemento especifico
	// print only a WebElement
	public static File printWebElement(WebElement element, WebDriver driver) throws IOException {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
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
		ImageIO.write(dest, ManipulateFiles.getListString("imgExtension"), scrFile);
		return scrFile;
	}
	
	public static void savePrint(Path imgPath, File tempFilePrintSelenium, String finalPrintName, int width, int height) {
		String fileName = imgPath.toString() + finalPrintName + "."+ManipulateFiles.getListString("imgExtension");
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
			ImageIO.write(img, ManipulateFiles.getListString("imgExtension"), filePrintNewResolution);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
