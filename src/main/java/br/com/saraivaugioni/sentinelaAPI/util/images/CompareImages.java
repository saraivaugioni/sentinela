package br.com.saraivaugioni.sentinelaAPI.util.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import br.com.saraivaugioni.sentinelaAPI.main.Sentinela;
import br.com.saraivaugioni.sentinelaAPI.util.files.ManipulateFiles;

public class CompareImages {

	private List<String> pixelsDiferencas = new ArrayList<String>();
	private double percentualDiferencaUltimaImagem;
	private int qtdPixelDiferentesUltimaImagem;
	private int qtdTotalPixelComparadosUltimaImagem;
	private boolean isDiff = false;

	// private int filterRGB(Color c1, Color c2, int rgb) {
	// final int r1 = c1.getRed();
	// final int g1 = c1.getGreen();
	// final int b1 = c1.getBlue();
	// final int r2 = c2.getRed();
	// final int g2 = c2.getGreen();
	// final int b2 = c2.getBlue();
	// int r = (rgb & 0xFF0000) >> 16;
	// int g = (rgb & 0xFF00) >> 8;
	// int b = rgb & 0xFF;
	// if (r >= r1 && r <= r2 && g >= g1 && g <= g2 && b >= b1 && b <= b2) {
	// // Set fully transparent but keep color
	// return rgb & 0xFFFFFF;
	// };
	// return rgb;
	// }

	private boolean verificarMarcacaoJaExiste(int j, int i) {
		if (pixelsDiferencas.indexOf(String.valueOf(j) + ";" + String.valueOf(i)) != -1) {
			return true;
		}
		return false;
	}

	private void aplicarContornoPixel(BufferedImage img1, int j, int i, int cor) {
		try {
			if (!verificarMarcacaoJaExiste(j, i)) {
				img1.setRGB(j, i, cor);
			}
		} catch (Exception ex) {
			// System.out.println(ex.getMessage());
		}
	}

	private void destacarDiff(BufferedImage img1, int j, int i) {
		int jTemp;
		int iTemp;
		int cor = 12341680;
		// Primeira camada da borda
		jTemp = j - 1;
		iTemp = i - 1;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j - 1;
		iTemp = i;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j - 1;
		iTemp = i + 1;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j;
		iTemp = i - 1;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j;
		iTemp = i + 1;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j + 1;
		iTemp = i - 1;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j + 1;
		iTemp = i;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j + 1;
		iTemp = i + 1;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		// Segunda camada da borda
		jTemp = j - 2;
		iTemp = i - 2;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j - 2;
		iTemp = i;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j - 2;
		iTemp = i + 2;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j;
		iTemp = i - 2;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j;
		iTemp = i + 2;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j + 2;
		iTemp = i - 2;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j + 2;
		iTemp = i;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
		jTemp = j + 2;
		iTemp = i + 2;
		aplicarContornoPixel(img1, jTemp, iTemp, cor);
	}

	private double calcularPercentualDiff(int tamanhoImg, int qtdPixelDiferentes) {
		double percentual = (100 * qtdPixelDiferentes) / tamanhoImg;
		return percentual;
	}

	public BufferedImage getDifferenceImage(BufferedImage img1, BufferedImage img2) {
		// http://stackoverflow.com/questions/25022578/highlight-differences-between-images
		int width1 = img1.getWidth(); // Change - getWidth() and getHeight() for
										// BufferedImage
		int width2 = img2.getWidth(); // take no arguments
		int height1 = img1.getHeight();
		int height2 = img2.getHeight();
		if ((width1 != width2) || (height1 != height2)) {
			System.err.println(ManipulateFiles.getListString("erroImgDiffSize"));
			System.exit(1);
		}
		// NEW - Create output Buffered image of type RGB
		BufferedImage outImg = new BufferedImage(width1, height1, BufferedImage.TYPE_INT_RGB);
		// Modified - Changed to int as pixels are ints
		int diff;
		int result; // Stores output pixel
		int cx = 0;
		for (int i = 0; i < height1; i++) {
			for (int j = 0; j < width1; j++) {
				int rgb1 = img1.getRGB(j, i);
				int rgb2 = img2.getRGB(j, i);
				int r1 = (rgb1 >> 16) & 0xff;
				int g1 = (rgb1 >> 8) & 0xff;
				int b1 = (rgb1) & 0xff;
				int r2 = (rgb2 >> 16) & 0xff;
				int g2 = (rgb2 >> 8) & 0xff;
				int b2 = (rgb2) & 0xff;
				diff = Math.abs(r1 - r2); // Change
				diff += Math.abs(g1 - g2);
				diff += Math.abs(b1 - b2);
				diff /= 3; // Change - Ensure result is between 0 - 255
				// Make the difference image gray scale
				// The RGB components are all the same
				// Escala cinza
				// result_cinza = (diff << 16) | (diff << 8) | diff;
				// result_cinza = (diff << 16) | (diff << 8) | diff;
				// Escala azul
				result = (diff << 24) | (diff << 8) | diff;
				if (result == 0) {
					result = rgb2;
				} else {
					pixelsDiferencas.add(String.valueOf(j) + ";" + String.valueOf(i));
				}
				outImg.setRGB(j, i, result); // Set result
				cx++;
			}
		}
		double percentualDiferenca = calcularPercentualDiff(cx, pixelsDiferencas.size());
		setPercentualDiferencaUltimaImagem(percentualDiferenca);
		setQtdPixelDiferentesUltimaImagem(pixelsDiferencas.size());
		setQtdTotalPixelComparadosUltimaImagem(cx);
		if (getQtdPixelDiferentesUltimaImagem() <= 20000) {
			// Preenchimento em volta do pixel
			for (String coordenadasPixel : pixelsDiferencas) {
				String[] ji = coordenadasPixel.split(";");
				String j = ji[0];
				String i = ji[1];
				destacarDiff(outImg, Integer.valueOf(j), Integer.valueOf(i));
			}
		}
		// If there are diff in the images
		if (getQtdPixelDiferentesUltimaImagem() > 0) {
			setDiff(true);
		}
		// Now return
		return outImg;
	}
	
	
	public static void compare(Sentinela sentinela, String imgName, String validationName) {
		BufferedImage img1 = null;
		BufferedImage img2 = null;
		CompareImages comparator = new CompareImages();
		try {
			img1 = ImageIO.read(new File(sentinela.getBaseLinePath() + "\\" + imgName));
			img2 = ImageIO.read(new File(sentinela.getImgsPath() + "\\" + sentinela.getDateTimeExecutionCurrent() + "\\" + imgName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage outImg = comparator.getDifferenceImage(img1, img2);
		File outputfile = new File(sentinela.getImgsPath() + "\\" + sentinela.getDateTimeExecutionCurrent() + "\\"+ManipulateFiles.getListString("nameDirResults")+"\\" + imgName);
		try {
			ImageIO.write(outImg, ManipulateFiles.getListString("imgExtension"), outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ManipulateFiles.saveInfMetaData(sentinela.getImgsPath(),sentinela.getDateTimeExecutionCurrent(),validationName + ";" + imgName + ";" + comparator.getPercentualDiferencaUltimaImagem() + ";"
				+ comparator.getQtdTotalPixelComparadosUltimaImagem() + ";"
				+ comparator.getQtdPixelDiferentesUltimaImagem());
		sentinela.setDiff(comparator.isDiff());
	}
	
	public static double compare(File img1, File img2) {
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



	public List<String> getPixelsDiferencas() {
		return pixelsDiferencas;
	}

	// private void setPixelsDiferencas(List<String> pixelsDiferencas) {
	// this.pixelsDiferencas = pixelsDiferencas;
	// }

	public double getPercentualDiferencaUltimaImagem() {
		return percentualDiferencaUltimaImagem;
	}

	private void setPercentualDiferencaUltimaImagem(double percentualDiferencaUltimaImagem) {
		this.percentualDiferencaUltimaImagem = percentualDiferencaUltimaImagem;
	}

	public int getQtdPixelDiferentesUltimaImagem() {
		return qtdPixelDiferentesUltimaImagem;
	}

	private void setQtdPixelDiferentesUltimaImagem(int qtdPixelDiferentesUltimaImagem) {
		this.qtdPixelDiferentesUltimaImagem = qtdPixelDiferentesUltimaImagem;
	}

	public int getQtdTotalPixelComparadosUltimaImagem() {
		return qtdTotalPixelComparadosUltimaImagem;
	}

	private void setQtdTotalPixelComparadosUltimaImagem(int qtdTotalPixelComparadosUltimaImagem) {
		this.qtdTotalPixelComparadosUltimaImagem = qtdTotalPixelComparadosUltimaImagem;
	}

	public boolean isDiff() {
		return isDiff;
	}

	private void setDiff(boolean isDiff) {
		this.isDiff = isDiff;
	}
}