package br.com.saraivaugioni.sentinela.util.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import br.com.saraivaugioni.sentinela.main.Sentinela;
import br.com.saraivaugioni.sentinela.util.language.LanguageCodes;

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
			FileWriter fstream = new FileWriter(pathImg + "\\" + dateTimeExecutionCurrent + "\\"+getListString("nameDirResults")+"\\"+getListString("nameFileMetadata"),
					true);
			out = new BufferedWriter(fstream);
			out.write(inf + "\n");
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveHeadMetaData(Path pathImg, String dateTimeExecutionCurrent, String head) {
		BufferedWriter out = null;
		try {
			FileWriter fstream = new FileWriter(pathImg + "\\" + dateTimeExecutionCurrent + "\\"+getListString("nameDirResults")+"\\"+getListString("nameFileMetadata"),
					true);
			out = new BufferedWriter(fstream);
			out.write(head + "\n");
			out.close();
			fstream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean prepareEnvironment(Path directoryBaseLine, Path directoryImgPath,
			String dateTimeExecutionCurrent) {
		Path dirBaseLine = directoryBaseLine;
		Path dirComparison = Paths.get(directoryImgPath + "\\" + dateTimeExecutionCurrent);
		Path dirComparisonResults = Paths.get(dirComparison + "\\"+ManipulateFiles.getListString("nameDirResults"));
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

	public static List<String> lerInformacoesMetaDados(Path record) {
		File metadados = new File(record + "\\"+getListString("nameFileMetadata"));
		BufferedReader reader = null;
		List<String> informacoesImagens = new ArrayList<String>();
		try {
			reader = new BufferedReader(new FileReader(metadados));
			String text = null;
			while ((text = reader.readLine()) != null) {
				informacoesImagens.add(text);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return informacoesImagens;
	}

	public static void copyFilesBaseLineToRecords(Path directoryBaseLine, File fHistoryLocal) {
		// Lista as imagens dentro da base line
		File fdirectoryBaseLine = new File(directoryBaseLine.toString() + "\\");
		File[] fList = fdirectoryBaseLine.listFiles();
		for (File fileBaseLine : fList) {
			if (fileBaseLine.isFile()) {
				try {
					FileUtils.copyFile(fileBaseLine, new File(
							fHistoryLocal + "\\" + directoryBaseLine.getFileName() + "\\" + fileBaseLine.getName()));
				} catch (IOException e) {
					e.getMessage();
				}
			}
		}
	}

	public static void copyFilesComparedToRecords(File fHistoryLocal, File fHistoryLocalReport) {
		// Lista as imagens dentro do historico da Sentinela
		File fDirHistory = new File(fHistoryLocal.toString() + "\\");
		File[] fList = fDirHistory.listFiles();
		for (File fileHistory : fList) {
			if (fileHistory.isFile()) {
				try {
					FileUtils.copyFile(fileHistory,
							new File(fHistoryLocalReport + "\\"+getListString("nameDirActualTest")+"\\" + fileHistory.getName()));
				} catch (IOException e) {
					e.getMessage();
				}
			}
		}
	}

	public static void copyFilesResultsComparedToRecords(File fHistoryLocal, File fHistoryLocalReport) {
		File dirResultImgs = new File(fHistoryLocal.toString() + "\\"+getListString("nameDirResults")+"\\");
		File[] fList = dirResultImgs.listFiles();
		// Copia todos os arquivos da pasta de Resultados: que foram gerados
		// pela comparação.
		for (File arquivo : fList) {
			if (arquivo.isFile()) {
				try {
					if (arquivo.getName().trim().toUpperCase().equals(getListString("nameFileMetadata").trim().toUpperCase())) {
						FileUtils.copyFile(arquivo, new File(fHistoryLocalReport + "\\" + arquivo.getName()));
					} else {
						FileUtils.copyFile(arquivo,
								new File(fHistoryLocalReport + "\\"+getListString("nameDirComparisonResults")+"\\" + arquivo.getName()));
					}
				} catch (IOException e) {
					e.getMessage();
				}
			}
		}

	}

	public static List<Path> findTimeLineRecords(Path directoryImgPath) {
		List<Path> listFilesRecords = new ArrayList<Path>();
		File diretorioImagens = new File(directoryImgPath.toString());
		File[] fList = diretorioImagens.listFiles();
		for (File arquivo : fList) {
			if (arquivo.isDirectory()) {
				if (isRecord(arquivo.getAbsolutePath())) {
					if (validateTimeLineRecords(arquivo)) {
						listFilesRecords.add(Paths.get(arquivo.getAbsolutePath()));
					}
				}
			}
		}
		return Lists.reverse(listFilesRecords);
	}

	private static boolean isRecord(String dirName) {
		if (Files.exists(Paths.get(dirName + "\\"+getListString("nameDirResults")+"\\"+getListString("nameFileMetadata")))) {
			return true;
		}
		return false;
	}

	private static boolean validateTimeLineRecords(File localRecord) {
		Path metaDadosFilePath = Paths.get(localRecord + "\\"+getListString("nameDirResults")+"\\");
		if (!Files.exists(Paths.get(metaDadosFilePath + "\\"+getListString("nameFileMetadata")))) {
			return false;
		}
		List<String> infMetaDados = ManipulateFiles.lerInformacoesMetaDados(metaDadosFilePath);
		if (infMetaDados.size() <= 1) {
			return false;
		}
		return true;
	}
	
	public static String getListString(String stringName) {
		Properties properties = new Properties();
		String valor = "";
		ClassLoader classLoader = ManipulateFiles.class.getClassLoader();
		String prefixLanguageCode = "";
		
		if(Sentinela.languageCode==LanguageCodes.PT_BR){
			prefixLanguageCode = "_pt_BR";
		}else if(Sentinela.languageCode==LanguageCodes.ZH_TW){
			prefixLanguageCode = "_zh_TW";
		}
		
		String languageFile = "listStrings"+prefixLanguageCode;
		
		try {
			properties.load(new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(languageFile),"UTF-8")));
			//properties.load(new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("listStrings_pt_BR"),"UTF-8")));
			valor = properties.getProperty(stringName);
		}catch(Exception ex){
			valor = "";
		}
		return valor;
	}

}
