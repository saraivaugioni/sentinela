package br.com.saraivaugioni.sentinela.util.report;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.saraivaugioni.sentinela.util.files.ManipulateFiles;

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
			List<String> infHistorico = ManipulateFiles.lerInformacoesMetaDados(Paths.get(getPathReport() + "\\"
					+ ManipulateFiles.getListString("nameDirReportRecords") + "\\" + recordExecution));

			// String dataHora = infHistorico.get(0);
			// DateFormat formatIN = new
			// SimpleDateFormat(ManipulateFiles.getListString("currentDateTimeFormat"));
			// Date dataHoraHistorico = null;
			// try {
			// dataHoraHistorico = formatIN.parse(dataHora);
			// } catch (ParseException e3) {
			// e3.getMessage();
			// }

			relatorio.startNewTest(ManipulateFiles.getListString("testOverviewName") + " - " + recordExecution,
					ManipulateFiles.getListString("testOverviewDescription"));
			relatorio.addTagTest(ManipulateFiles.getListString("testOverviewName") + " - " + recordExecution,
					recordExecution);
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

			relatorio.addLogTestInfo(ManipulateFiles.getListString("testOverviewName") + " - " + recordExecution, "",
					"<b>"+ManipulateFiles.getListString("testQtyDiffPictCompared")+": </b>" + totalImgComparadas + "<br><b>"+ManipulateFiles.getListString("testQtyDiffPict")+": </b>"
							+ qtdTelasDiferencas + "<br><b>"+ManipulateFiles.getListString("testPercDiffPict")+": </b>" + percentualImgDiferentes
							+ "<br><br><b>"+ManipulateFiles.getListString("testQtyPixelCompared")+": </b>" + totalPixelsComparados
							+ "<br><b>"+ManipulateFiles.getListString("testQtyDiffPixel")+": </b>" + totalPixelsDiferentes
							+ "<br><b>"+ManipulateFiles.getListString("testPercDiffPixel")+": </b>" + percentualDiferencas);
			relatorio.endTest(ManipulateFiles.getListString("testOverviewName") + " - " + recordExecution);
		}
	}

	private void genDiffReport(List<Path> timeLineRecords) {

		// For each timeLineRecord
		for (Path record : timeLineRecords) {

			// Read metadata record
			String recordExecution = record.getFileName().toString();
			List<String> infHistorico = ManipulateFiles
					.lerInformacoesMetaDados(Paths.get(getPathReport() + "\\"+ManipulateFiles.getListString("nameDirReportRecords")+"\\" + recordExecution));

			// For each image test result
			for (int i = 1; i < infHistorico.size(); i++) {
				String[] infImagem = infHistorico.get(i).split(";");
				String nomeValidacao = infImagem[0];
				String arquivoImagem = infImagem[1];
				String percentualDiff = infImagem[2];
				String qtdTotalPixel = infImagem[3];
				String qtdTotalPixelDiff = infImagem[4];
				String srcImgA = ManipulateFiles.getListString("nameDirReportRecords")+"\\" + recordExecution + "\\"+ManipulateFiles.getListString("nameDirBaseline")+"\\" + arquivoImagem;
				String srcImgB = ManipulateFiles.getListString("nameDirReportRecords")+"\\" + recordExecution + "\\"+ManipulateFiles.getListString("nameDirActualTest")+"\\" + arquivoImagem;
				String srcImgC = ManipulateFiles.getListString("nameDirReportRecords")+"\\" + recordExecution + "\\"+ManipulateFiles.getListString("nameDirComparisonResults")+"\\" + arquivoImagem;
				boolean fail = false;

				relatorio.startNewTest(recordExecution + " - " + arquivoImagem, nomeValidacao);
				relatorio.addTagTest(recordExecution + " - " + arquivoImagem, recordExecution);
				if (Integer.valueOf(qtdTotalPixelDiff) == 0) {
					fail = false;
				} else {
					fail = true;
				}

				if (fail) {
					relatorio.addLogTestFail(recordExecution + " - " + arquivoImagem,
							ManipulateFiles.getListString("testImgComparison")+" - " + arquivoImagem,
							"<b>"+ManipulateFiles.getListString("testQtyPixelCompared")+": </b>" + qtdTotalPixel + "<br><b>"+ManipulateFiles.getListString("testQtyDiffPixel")+": </b>"
									+ qtdTotalPixelDiff + "<br><b>"+ManipulateFiles.getListString("testPercDiffPixel")+": </b>" + percentualDiff
									+ "<br>",
							srcImgA + ";"+ManipulateFiles.getListString("descriptionImgA"), srcImgB + ";"+ManipulateFiles.getListString("descriptionImgB"), srcImgC + ";"+ManipulateFiles.getListString("descriptionImgC"));
				} else {
					relatorio.addLogTestPass(recordExecution + " - " + arquivoImagem,
							ManipulateFiles.getListString("testImgComparison")+" - " + arquivoImagem,
							"<b>"+ManipulateFiles.getListString("testQtyPixelCompared")+": </b>" + qtdTotalPixel + "<br><b>"+ManipulateFiles.getListString("testQtyDiffPixel")+": </b>"
									+ qtdTotalPixelDiff + "<br><b>"+ManipulateFiles.getListString("testPercDiffPixel")+": </b>" + percentualDiff
									+ "<br>",
							srcImgA + ";"+ManipulateFiles.getListString("descriptionImgA"), srcImgB + ";"+ManipulateFiles.getListString("descriptionImgB"), srcImgC + ";"+ManipulateFiles.getListString("descriptionImgC"));
				}

				relatorio.endTest(recordExecution + " - " + arquivoImagem);

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
