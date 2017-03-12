package br.com.saraivaugioni.sentinelaAPI.util.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ManipulateFiles {

	public static void removeFile(String fileFullName) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileFullName));
			out.write("aString1\n");
			out.close();
			new File(fileFullName).delete();
		} catch (IOException e) {
			System.out.println("Error remove file: " + fileFullName + ". " + e.getMessage());
		}
	}

	public static void saveInfMetaData(Path pathImg, String dateTimeExecutionCurrent, String inf) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(pathImg + "\\" + dateTimeExecutionCurrent + "\\Resultados\\metadados.ini", true);
			out = new BufferedWriter(fstream);
			out.write(inf + "\n");
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveHeadMetaData(Path pathImg, String dateTimeExecutionCurrent,String head) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(
					pathImg + "\\" + dateTimeExecutionCurrent + "\\Resultados\\metadados.ini", true);
			out = new BufferedWriter(fstream);
			out.write(head + "\n");
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean prepareEnvironment(Path directoryBaseLine, Path directoryImgPath, String dateTimeExecutionCurrent) {
		Path dirBaseLine = directoryBaseLine;
		Path dirComparison = Paths.get(directoryImgPath + "\\" + dateTimeExecutionCurrent);
		Path dirComparisonResults = Paths.get(dirComparison + "\\Resultados");
		// Se o diretório do baseLine não existir, ele é criado.
		if (!Files.exists(dirBaseLine)) {
			File fdiretoriobaseLine = new File(dirBaseLine.toString());
			fdiretoriobaseLine.mkdir();
			return true;
		}
		// Se o diretório de comparacao não existir, ele é criado.
		if (!Files.exists(dirComparison)) {
			File fdiretoriocomparacao = new File(dirComparison.toString());
			File fdiretoriocomparacaoresultado = new File(dirComparisonResults.toString());
			fdiretoriocomparacao.mkdir();
			fdiretoriocomparacaoresultado.mkdir();
			// Cria o arquivo de metadados
			saveHeadMetaData(directoryImgPath, dateTimeExecutionCurrent, dirComparison.getFileName().toString());
		}
		return false;
	}

}
