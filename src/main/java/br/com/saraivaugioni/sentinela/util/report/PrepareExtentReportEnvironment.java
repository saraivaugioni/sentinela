package br.com.saraivaugioni.sentinela.util.report;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import br.com.saraivaugioni.sentinela.util.files.ManipulateFiles;

public class PrepareExtentReportEnvironment {
	private Path pathReport;
	private Path pathImgs;
	private Path pathBaseLine;
	private List<Path> recordsExecutions = new ArrayList<Path>();
	private Path reportDir;
	private Path reportDirRecords;
	
	public PrepareExtentReportEnvironment(String pathReport, String pathImgs, String pathBaseLne) {
		setPathReport(pathReport);
		setPathImgs(pathImgs);
		setPathBaseLine(pathBaseLne);
	}
	
	public List<Path> prepareEnvironment(){
		
		List<Path> timeLineRecords = new ArrayList<Path>();
		
		reportDir = getPathReport();
		reportDirRecords = Paths.get(reportDir + "\\"+ManipulateFiles.getListString("nameDirReportRecords")+"\\");
		
		if(!Files.exists(reportDir)){
			File fReportDir = new File(reportDir.toString());
			fReportDir.mkdir();
		}
		
		if(!Files.exists(reportDirRecords)){
			File fReportDirRecords = new File(reportDirRecords.toString());
			fReportDirRecords.mkdir();
		}
		
		
		timeLineRecords = ManipulateFiles.findTimeLineRecords(getPathImgs());
		
		// Cria os diretorios de historico na pasta do relatorio e copia as
		// imagens de resultados e o metadados.
		for (Path pathRecords : timeLineRecords) {
			File fdirRecord = new File(reportDirRecords + "\\" + pathRecords.getFileName().toString());
			fdirRecord.mkdir();
			// Copia os arquivo da baseLine e os arquivos do teste realizado
			// para o pasta do relatório.
			ManipulateFiles.copyFilesBaseLineToRecords(getPathBaseLine(), fdirRecord);
			ManipulateFiles.copyFilesComparedToRecords(new File(pathRecords.toString()), fdirRecord);
			// Lista os arquivos dentro do diretorio de resultados das imagens
			// comparadas
			ManipulateFiles.copyFilesResultsComparedToRecords(new File(pathRecords.toString()), fdirRecord);
		}
		
		return timeLineRecords;
		
	}
	
	
	public Path getPathReport() {
		return pathReport;
	}

	public void setPathReport(String pathReport) {
		this.pathReport = Paths.get(pathReport);
	}

	public Path getPathImgs() {
		return pathImgs;
	}

	public void setPathImgs(String pathImgs) {
		this.pathImgs = Paths.get(pathImgs);
	}

	public Path getPathBaseLine() {
		return pathBaseLine;
	}

	public void setPathBaseLine(String pathBaseLine) {
		this.pathBaseLine = Paths.get(pathBaseLine);
	}

	public List<Path> getRecordsExecutions() {
		return recordsExecutions;
	}

	public void setRecordsExecutions(List<Path> recordsExecutions) {
		this.recordsExecutions = recordsExecutions;
	}

	public Path getReportDir() {
		return reportDir;
	}

	public void setReportDir(Path reportDir) {
		this.reportDir = reportDir;
	}

	public Path getReportDirRecords() {
		return reportDirRecords;
	}

	public void setReportDirRecords(Path reportDirRecords) {
		this.reportDirRecords = reportDirRecords;
	}
	
	

}
