package br.com.saraivaugioni.sentinelaAPI.util.report;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.saraivaugioni.sentinelaAPI.util.files.ManipulateFiles;

public class GeneratorExtentReport {

	private Path pathReport;
	private Path pathImgs;
	private Path pathBaseLine;
	private GenericReport relatorio;

	public GeneratorExtentReport(String pathReport, String pathImgs, String pathBaseLne) {
		setPathReport(pathReport);
		setPathImgs(pathImgs);
		setPathBaseLine(pathBaseLne);
	}

	public void generateReport() {
		Path dirPathImgs = getPathImgs();
		if (!Files.exists(dirPathImgs)) {
			return;
		}

		relatorio = new GenericReport(getPathReport().toString());

		PrepareExtentReportEnvironment reportEnvironment = new PrepareExtentReportEnvironment(
				getPathReport().toString(), getPathImgs().toString(), getPathBaseLine().toString());
		List<Path> timeLineRecords = reportEnvironment.prepareEnvironment();
		genConsolidatedReport(timeLineRecords);
		genDiffReport(timeLineRecords);
		relatorio.endReport();
	}

	private void genConsolidatedReport(List<Path> timeLineRecords) {

		// For each timeLineRecord
		for (Path record : timeLineRecords) {

			// Read metadata record
			String recordExecution = record.getFileName().toString();
			List<String> infHistorico = ManipulateFiles
					.lerInformacoesMetaDados(Paths.get(getPathReport() + "\\records\\" + recordExecution));
			String dataHora = infHistorico.get(0);
			DateFormat formatIN = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
			DateFormat formatoutDATA = new SimpleDateFormat("dd/MM/yyyy");
			DateFormat formatoutHORA = new SimpleDateFormat("HH:mm:ss");
			Date dataHoraHistorico = null;
			try {
				dataHoraHistorico = formatIN.parse(dataHora);
			} catch (ParseException e3) {
				e3.getMessage();
			}

			relatorio.startNewTest("Overview - " + recordExecution, "Consolidated test information");

			// Consolidado dos dados sobre a execução
			int totalImgComparadas = 0;
			int totalPixelsComparados = 0;
			int totalPixelsDiferentes = 0;
			int qtdTelasDiferencas = 0;
			double percentualDiferencas = 0;
			double percentualImgDiferentes = 0;
			for (int i = 1; i < infHistorico.size(); i++) {
				String[] infs = infHistorico.get(i).split(";");
				totalImgComparadas++;
				totalPixelsComparados += Integer.valueOf(infs[3]);
				totalPixelsDiferentes += Integer.valueOf(infs[4]);
				if (Integer.valueOf(infs[4]) > 0) {
					qtdTelasDiferencas++;
				}
			}

			if (totalPixelsDiferentes > 0) {
				percentualDiferencas = (100 * totalPixelsDiferentes) / totalPixelsComparados;
			}

			if (qtdTelasDiferencas > 0) {
				percentualImgDiferentes = (100 * qtdTelasDiferencas) / totalImgComparadas;
			}

			relatorio.addLogTestPass("Overview - " + recordExecution, "",
					"<b>Quantity pictures compared: </b>" + totalImgComparadas + "<br><b>Quantity diff pictures: </b>"
							+ qtdTelasDiferencas + "<br><b>Percentage diff pictures: </b>" + percentualImgDiferentes
							+ "<br><br><b>Quantity pixels compared: </b>" + totalPixelsComparados
							+ "<br><b>Quantity diff pixels: </b>" + totalPixelsDiferentes
							+ "<br><b>Percentage diff pixels: </b>" + percentualDiferencas);
			relatorio.endTest("Overview - " + recordExecution);
		}
	}

	private void genDiffReport(List<Path> timeLineRecords) {

		// For each timeLineRecord
		for (Path record : timeLineRecords) {

			// Read metadata record
			String recordExecution = record.getFileName().toString();
			List<String> infHistorico = ManipulateFiles
					.lerInformacoesMetaDados(Paths.get(getPathReport() + "\\records\\" + recordExecution));

			// For each image test result
			for (int i = 1; i < infHistorico.size(); i++) {
				String[] infImagem = infHistorico.get(i).split(";");
				String nomeValidacao = infImagem[0];
				String arquivoImagem = infImagem[1];
				String percentualDiff = infImagem[2];
				String qtdTotalPixel = infImagem[3];
				String qtdTotalPixelDiff = infImagem[4];
				String srcImgA = "records\\" + recordExecution+"\\baseLine\\"+arquivoImagem;
				String srcImgB = "records\\" + recordExecution+"\\imgsActualTest\\"+arquivoImagem;
				String srcImgC = "records\\" + recordExecution+"\\ComparisonResults\\"+arquivoImagem;
				boolean fail = false;

				relatorio.startNewTest("Overview - " + recordExecution + " - " + arquivoImagem, nomeValidacao);
				relatorio.addTagTest("Overview - " + recordExecution + " - " + arquivoImagem, recordExecution);
				if (Integer.valueOf(qtdTotalPixelDiff) == 0) {
					fail = false;
				} else {
					fail = true;
				}

				if (fail) {
					relatorio.addLogTestFail("Overview - " + recordExecution + " - " + arquivoImagem,
							"Image comparison - " + arquivoImagem,
							"<b>Quantity pixels compared: </b>" + qtdTotalPixel + "<br><b>Quantity diff pixels: </b>"
									+ qtdTotalPixelDiff + "<br><b>Percentage diff pixels: </b>" + percentualDiff, srcImgA, srcImgB, srcImgC);
				} else {
					relatorio.addLogTestPass("Overview - " + recordExecution + " - " + arquivoImagem,
							"Image comparison - " + arquivoImagem,
							"<b>Quantity pixels compared: </b>" + qtdTotalPixel + "<br><b>Quantity diff pixels: </b>"
									+ qtdTotalPixelDiff + "<br><b>Percentage diff pixels: </b>" + percentualDiff, srcImgA, srcImgB, srcImgC);

				}

				relatorio.endTest("Overview - " + recordExecution + " - " + arquivoImagem);

			}

		}

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
