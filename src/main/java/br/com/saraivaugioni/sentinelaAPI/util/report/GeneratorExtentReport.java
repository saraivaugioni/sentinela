package br.com.saraivaugioni.sentinelaAPI.util.report;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GeneratorExtentReport {
	
	private Path pathReport;
	private Path pathImgs;
	private Path pathBaseLine;
	
	public GeneratorExtentReport(String pathReport, String pathImgs, String pathBaseLne) {
		setPathReport(pathReport);
		setPathImgs(pathImgs);
		setPathBaseLine(pathBaseLne);
		
	}
	
	public void generateReport(){
		
		Path dirPathImgs = getPathImgs();		
		if (Files.exists(dirPathImgs)) {
			return;
		}

		GenericReport relatorio = new GenericReport(getPathReport().toString());
		
		PrepareExtentReportEnvironment reportEnvironment = new PrepareExtentReportEnvironment(getPathReport().toString(), getPathImgs().toString(), getPathBaseLine().toString());
		reportEnvironment.prepareEnvironment();

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


}
