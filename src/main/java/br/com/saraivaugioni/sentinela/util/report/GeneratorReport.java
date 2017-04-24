package br.com.saraivaugioni.sentinela.util.report;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

public class GeneratorReport {

	private Path pathRelatorio;
	private Path pathImagens;
	private Path pathBaseLine;
	private List<Path> historicoExecucoes = new ArrayList<Path>();
	private Path diretorioRelatorio;
	private Path diretorioRelatorioCSS;
	private Path diretorioRelatorioJS;
	private Path diretorioRelatorioHistorico;
	private Path diretorioRelatorioHistoricoTemplate;

	public GeneratorReport(String pathRelatorio, String pathImagens, String pathBaseLne) {
		setPathRelatorio(pathRelatorio);
		setPathImagens(pathImagens);
		setPathBaseLine(pathBaseLne);
		Path diretorioImagens = getPathImagens();
		// Se o diretório de imagens não existe o relatório não é gerado.
		if (Files.exists(diretorioImagens)) {
			prepararAmbiente();
			// lerInformacoesMetaDados();
			// gerarhistorico();;
			// for (String inf : informacoesImagens) {
			// System.out.println(inf);
			// }
		}
	}

	private List<String> lerInformacoesMetaDados(Path historico) {
		File metadados = new File(historico + "\\metadados.ini");
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

	private void prepararAmbiente() {
		diretorioRelatorio = getPathRelatorio();
		diretorioRelatorioCSS = Paths.get(diretorioRelatorio + "\\css\\");
		diretorioRelatorioJS = Paths.get(diretorioRelatorio + "\\js\\");
		diretorioRelatorioHistorico = Paths.get(diretorioRelatorio + "\\historico\\");
		diretorioRelatorioHistoricoTemplate = Paths.get(diretorioRelatorioHistorico + "\\template\\");
		// Se o diretório para geração do relatório não existe. Ele será criado.
		if (!Files.exists(diretorioRelatorio)) {
			File fdiretorioRelatorio = new File(diretorioRelatorio.toString());
			fdiretorioRelatorio.mkdir();
		}
		if (!Files.exists(diretorioRelatorioCSS)) {
			File fdiretorioRelatorioCSS = new File(diretorioRelatorioCSS.toString());
			fdiretorioRelatorioCSS.mkdir();
		}
		if (!Files.exists(diretorioRelatorioJS)) {
			File fdiretorioRelatorioJS = new File(diretorioRelatorioJS.toString());
			fdiretorioRelatorioJS.mkdir();
		}
		if (!Files.exists(diretorioRelatorioHistorico)) {
			File fdiretorioRelatorioHistorico = new File(diretorioRelatorioHistorico.toString());
			fdiretorioRelatorioHistorico.mkdir();
		}
		if (!Files.exists(diretorioRelatorioHistoricoTemplate)) {
			File fdiretorioRelatorioHistoricoTemplate = new File(diretorioRelatorioHistoricoTemplate.toString());
			fdiretorioRelatorioHistoricoTemplate.mkdir();
		}
		// Copia os arquivos de template, extraindo de dentro do .jar para o
		// sistema de arquivos.
		try {
			// Index
			ExportResource("/index.html", diretorioRelatorioHistoricoTemplate.toString() + "\\");
			// Histórico
			ExportResource("/diferencas.html", diretorioRelatorioHistoricoTemplate.toString() + "\\");
			ExportResource("/execucao.html", diretorioRelatorioHistoricoTemplate.toString() + "\\");
			// CSS
			ExportResource("/3-col-portfolio.css", diretorioRelatorioCSS.toString() + "\\");
			ExportResource("/bootstrap.min.css", diretorioRelatorioCSS.toString() + "\\");
			// JS
			ExportResource("/bootstrap.min.js", diretorioRelatorioJS.toString() + "\\");
			ExportResource("/jquery.js", diretorioRelatorioJS.toString() + "\\");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Faz uma busca de historico
		buscarHistoricos();
		// Cria os diretorios de historico na pasta do relatorio e copia as
		// imagens de resultados e o metadados.
		for (Path historico : historicoExecucoes) {
			File fdirHistorico = new File(diretorioRelatorioHistorico + "\\" + historico.getFileName().toString());
			fdirHistorico.mkdir();
			// Copia os arquivo da baseLine e os arquivos do teste realizado
			// para o pasta do relatório.
			copiaArquivosBaseLineParaHistorico(fdirHistorico);
			copiaArquivosComparadosParaHistorico(new File(historico.toString()), fdirHistorico);
			// Lista os arquivos dentro do diretorio de resultados das imagens
			// comparadas
			File diretorioResultadoImagens = new File(historico.toString() + "\\Resultados\\");
			File[] fList = diretorioResultadoImagens.listFiles();
			// Copia todos os arquivos da pasta de Resultados: que foram gerados
			// pela comparação.
			for (File arquivo : fList) {
				if (arquivo.isFile()) {
					try {
						if (arquivo.getName().trim().toUpperCase().equals("metadados.ini".trim().toUpperCase())) {
							FileUtils.copyFile(arquivo, new File(fdirHistorico + "\\" + arquivo.getName()));
						} else {
							FileUtils.copyFile(arquivo,
									new File(fdirHistorico + "\\ResultadoComparacao\\" + arquivo.getName()));
						}
					} catch (IOException e) {
						e.getMessage();
					}
				}
			}
			// Edita o HTML de execução
			editarRelatorioExecucao(Paths.get(fdirHistorico.getAbsolutePath() + "\\"));
		}
		// Edita o HTML de index
		editarRelatorioIndex();
	}

	private void copiaArquivosBaseLineParaHistorico(File localHistorico) {
		// Lista as imagens dentro da base line
		File diretorioBaseLine = new File(getPathBaseLine().toString() + "\\");
		File[] fList = diretorioBaseLine.listFiles();
		for (File arquivoBaseLine : fList) {
			if (arquivoBaseLine.isFile()) {
				try {
					FileUtils.copyFile(arquivoBaseLine, new File(localHistorico + "\\" + getPathBaseLine().getFileName()
							+ "\\" + arquivoBaseLine.getName()));
				} catch (IOException e) {
					e.getMessage();
				}
			}
		}
	}

	private void copiaArquivosComparadosParaHistorico(File localHistoricoSentinela, File localHistoricoRelatorio) {
		// Lista as imagens dentro do historico da Sentinela
		File diretorioHistoricoSentinela = new File(localHistoricoSentinela.toString() + "\\");
		File[] fList = diretorioHistoricoSentinela.listFiles();
		for (File arquivoHistoricoSentinela : fList) {
			if (arquivoHistoricoSentinela.isFile()) {
				try {
					FileUtils.copyFile(arquivoHistoricoSentinela, new File(
							localHistoricoRelatorio + "\\ImagensTesteAtual\\" + arquivoHistoricoSentinela.getName()));
				} catch (IOException e) {
					e.getMessage();
				}
			}
		}
	}

	private void editarRelatorioIndex() {
		String htmlIndex = "";
		BufferedReader br = null;
		BufferedWriter bw = null;
		OutputStreamWriter osw = null;
		File arquivoIndexLeituraTemplate = new File(diretorioRelatorioHistoricoTemplate + "\\index.html");
		File arquivoIndexEscrita = new File(diretorioRelatorio + "\\index.html");
		String divHistorico = "<div class='panel panel-default'>" + System.getProperty("line.separator");
		divHistorico += "<div class='panel-heading' role='tab' id='headingOne'>" + System.getProperty("line.separator");
		divHistorico += "<h4 class='panel-title'>" + System.getProperty("line.separator");
		divHistorico += "<a role='button' data-toggle='collapse' data-parent='#accordion' href='#collapse<!--IDCOLLAPSE-->' aria-expanded='true' aria-controls='collapse<!--IDCOLLAPSE-->'>"
				+ System.getProperty("line.separator");
		divHistorico += "<!--BUTTONSTATUSTEST-->";
		divHistorico += "<b> - Execução em <!--DATA--> as <!--HORA--></b> - <!--ERROS-->"
				+ System.getProperty("line.separator");
		divHistorico += "</a>" + System.getProperty("line.separator");
		divHistorico += "</h4>" + System.getProperty("line.separator");
		divHistorico += "</div>" + System.getProperty("line.separator");
		divHistorico += "<div id='collapse<!--IDCOLLAPSE-->' class='panel-collapse collapse' role='tabpanel' aria-labelledby='headingOne'>"
				+ System.getProperty("line.separator");
		divHistorico += "<div class='panel-body'>" + System.getProperty("line.separator");
		divHistorico += "<!--INFTESTES-->" + System.getProperty("line.separator");
		divHistorico += "</br>" + System.getProperty("line.separator");
		divHistorico += "<b><a href='historico/<!--HISTORICONOME-->/execucao.html'>Acessar relatório detalhado</a></b>"
				+ System.getProperty("line.separator");
		divHistorico += "</div>" + System.getProperty("line.separator");
		divHistorico += "</div>" + System.getProperty("line.separator");
		divHistorico += "</div>" + System.getProperty("line.separator");
		divHistorico += "<!--HISTORICO-->" + System.getProperty("line.separator");
		// Leitura do HTML.
		try {
			osw = new OutputStreamWriter(new FileOutputStream(arquivoIndexEscrita), "UTF-8");
		} catch (IOException e2) {
			e2.getMessage();
		}
		String linhaAtualHTML;
		try {
			br = new BufferedReader(new FileReader(arquivoIndexLeituraTemplate));
			bw = new BufferedWriter(osw);
		} catch (FileNotFoundException e1) {
			e1.getMessage();
		}
		try {
			while ((linhaAtualHTML = br.readLine()) != null) {
				htmlIndex += linhaAtualHTML + System.getProperty("line.separator");
			}
		} catch (IOException e) {
			e.getMessage();
		}
		int contadorCollapse = 1;
		// Para cada historico de execução
		for (Path hisExecucao : historicoExecucoes) {
			// Ler o metadados do historico
			String historicoExecucao = hisExecucao.getFileName().toString();
			List<String> infHistorico = lerInformacoesMetaDados(
					Paths.get(diretorioRelatorioHistorico + "\\" + historicoExecucao));
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
			// Consolidado dos dados sobre a execução
			int totalImgComparadas = 0;
			int totalPixelsComparados = 0;
			int totalPixelsDiferentes = 0;
			int qtdTelasDiferencas = 0;
			double percentualDiferencas = 0;
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
			String errosEncontrados = "";
			String buttonStatusTest = "";
			if (qtdTelasDiferencas == 0) {
				errosEncontrados += "Não foi encontrada nenhuma tela com diferenças.";
				buttonStatusTest = "<button type='button' class='btn btn-success' ></button>";
			} else if (qtdTelasDiferencas == 1) {
				errosEncontrados += "1 tela apresentou diferenças.";
				buttonStatusTest = "<button type='button' class='btn btn-danger' ></button>";
			} else if (qtdTelasDiferencas > 1) {
				errosEncontrados += qtdTelasDiferencas + " telas apresentaram diferenças.";
				buttonStatusTest = "<button type='button' class='btn btn-danger' ></button>";
			}
			String informacoesTesteRealizado = "";
			if (totalImgComparadas == 1) {
				informacoesTesteRealizado += "Foi testado uma imagem. ";
			} else if (totalImgComparadas > 1) {
				informacoesTesteRealizado += "Foram testadas " + totalImgComparadas + " imagens. ";
			}
			if (qtdTelasDiferencas == 0) {
				informacoesTesteRealizado += "Nenhuma imagem apresentou diferenças.";
			} else if (qtdTelasDiferencas == 1) {
				informacoesTesteRealizado += "Dessas, uma apresentou diferenças.";
			} else if (qtdTelasDiferencas > 1) {
				informacoesTesteRealizado += "Dessas, " + qtdTelasDiferencas + " apresentaram diferenças.";
			}
			if (totalPixelsDiferentes == 0) {
				informacoesTesteRealizado += "No total foram " + totalPixelsComparados
						+ " pixels comparados dos quais nenhum apresentou diferenças.";
			} else if (totalPixelsDiferentes > 1) {
				informacoesTesteRealizado += "No total foram " + totalPixelsComparados + " pixels comparados sendo "
						+ totalPixelsDiferentes + " pixels diferentes, correspondendo a " + percentualDiferencas
						+ "% dos comparados.";
			}
			// Montagem e escrita do HTML
			htmlIndex = htmlIndex.replace("<!--HISTORICO-->", divHistorico);
			htmlIndex = htmlIndex.replace("<!--DATA-->", formatoutDATA.format(dataHoraHistorico));
			htmlIndex = htmlIndex.replace("<!--HORA-->", formatoutHORA.format(dataHoraHistorico));
			htmlIndex = htmlIndex.replace("<!--ERROS-->", errosEncontrados);
			htmlIndex = htmlIndex.replace("<!--BUTTONSTATUSTEST-->", buttonStatusTest);
			htmlIndex = htmlIndex.replace("<!--INFTESTES-->", informacoesTesteRealizado);
			htmlIndex = htmlIndex.replace("<!--HISTORICONOME-->", historicoExecucao);
			htmlIndex = htmlIndex.replace("<!--IDCOLLAPSE-->", String.valueOf(contadorCollapse));
			contadorCollapse++;
		}
		try {
			br.close();
			bw.write(htmlIndex);
			bw.close();
		} catch (IOException e) {
			e.getMessage();
		}
	}

	private void editarRelatorioDiferencas(Path historico, String arquivoImagem, String infTestes,
			String percentualDiff) {
		String htmlExecucao = "";
		BufferedReader br = null;
		BufferedWriter bw = null;
		OutputStreamWriter osw = null;
		File arquivoDiferencasLeituraTemplate = new File(diretorioRelatorioHistoricoTemplate + "\\diferencas.html");
		File arquivoDiferencasEscritaHistorico = new File(
				historico + "\\" + arquivoImagem.replace(".png", "") + ".html");
		try {
			osw = new OutputStreamWriter(new FileOutputStream(arquivoDiferencasEscritaHistorico), "UTF-8");
		} catch (IOException e2) {
			e2.getMessage();
		}
		String linhaAtualHTML;
		try {
			br = new BufferedReader(new FileReader(arquivoDiferencasLeituraTemplate));
			bw = new BufferedWriter(osw);
		} catch (FileNotFoundException e1) {
			e1.getMessage();
		}
		try {
			while ((linhaAtualHTML = br.readLine()) != null) {
				htmlExecucao += linhaAtualHTML + System.getProperty("line.separator");
			}
		} catch (IOException e) {
			e.getMessage();
		}
		try {
			// Edi��o do HTML
			htmlExecucao = htmlExecucao.replace("<!--NOMEBASELINE-->", getPathBaseLine().getFileName().toString());
			htmlExecucao = htmlExecucao.replace("<!--ARQUIVOIMAGEM-->", arquivoImagem);
			htmlExecucao = htmlExecucao.replace("<!--INFTESTE-->", infTestes);
			htmlExecucao = htmlExecucao.replace("<!--PERCENTODIFF-->", percentualDiff + "%");
			br.close();
			bw.write(htmlExecucao);
			bw.close();
		} catch (IOException e) {
			e.getMessage();
		}
	}

	private void editarRelatorioExecucao(Path historico) {
		String htmlExecucao = "";
		BufferedReader br = null;
		BufferedWriter bw = null;
		OutputStreamWriter osw = null;
		File arquivoExecucaoLeituraTemplate = new File(diretorioRelatorioHistoricoTemplate + "\\execucao.html");
		File arquivoExecucaoEscritaHistorico = new File(historico + "\\execucao.html");
		List<String> infHistorico = lerInformacoesMetaDados(historico);
		String dataHora = infHistorico.get(0);
		DateFormat formatIN = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		DateFormat formatoutDATA = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formatoutHORA = new SimpleDateFormat("HH:mm:ss");
		Date dataHoraExecucao = null;
		try {
			dataHoraExecucao = formatIN.parse(dataHora);
		} catch (ParseException e3) {
			e3.getMessage();
		}
		// DIV ROW
		String divRow = "<div class='row'>" + System.getProperty("line.separator");
		divRow += "<!--DIVIMAGEM-->" + System.getProperty("line.separator");
		divRow += "</div><hr><br><br><br>" + System.getProperty("line.separator");
		divRow += "<!--DIVROW-->" + System.getProperty("line.separator");
		// DIV IMAGEM
		String divImagem = "<div class='col-md-4 portfolio-item'>" + System.getProperty("line.separator");
		divImagem += "<h3>" + System.getProperty("line.separator");
		divImagem += "<a href='<!--HTMLIMAGEM-->'><!--NOMEIMAGEM--></a>" + System.getProperty("line.separator");
		divImagem += "</h3>" + System.getProperty("line.separator");
		divImagem += "<a href='<!--HTMLIMAGEM-->'>" + System.getProperty("line.separator");
		divImagem += "<img class='img-responsive' src='ImagensTesteAtual/<!--ARQUIVOIMAGEM-->' alt=''>"
				+ System.getProperty("line.separator");
		divImagem += "</a>" + System.getProperty("line.separator");
		divImagem += "<p><!--INFIMAGENS--></p>" + System.getProperty("line.separator");
		divImagem += "</div>" + System.getProperty("line.separator");
		divImagem += "<!--DIVIMAGEM-->" + System.getProperty("line.separator");
		try {
			osw = new OutputStreamWriter(new FileOutputStream(arquivoExecucaoEscritaHistorico), "UTF-8");
		} catch (IOException e2) {
			e2.getMessage();
		}
		String linhaAtualHTML;
		try {
			br = new BufferedReader(new FileReader(arquivoExecucaoLeituraTemplate));
			bw = new BufferedWriter(osw);
		} catch (FileNotFoundException e1) {
			e1.getMessage();
		}
		try {
			while ((linhaAtualHTML = br.readLine()) != null) {
				htmlExecucao += linhaAtualHTML + System.getProperty("line.separator");
			}
		} catch (IOException e) {
			e.getMessage();
		}
		try {
			htmlExecucao = htmlExecucao.replace("<!--DATAEXECUCAO-->", formatoutDATA.format(dataHoraExecucao));
			htmlExecucao = htmlExecucao.replace("<!--HORAEXECUCAO-->", formatoutHORA.format(dataHoraExecucao));
			int coluna = 3;
			for (int i = 1; i < infHistorico.size(); i++) {
				String[] infImagem = infHistorico.get(i).split(";");
				String nomeValidacao = infImagem[0];
				String arquivoImagem = infImagem[1];
				String percentualDiff = infImagem[2];
				String qtdTotalPixel = infImagem[3];
				String qtdTotalPixelDiff = infImagem[4];
				String HTMLimagem = arquivoImagem.replace(".png", "") + ".html";
				String textoDinamicoPixel = "";
				if (Integer.valueOf(qtdTotalPixelDiff) == 0) {
					textoDinamicoPixel = "Nenhum pixel apresentou diferenças.";
				} else {
					textoDinamicoPixel = "Desses, " + qtdTotalPixelDiff
							+ " pixels apresentaram diferenças. Corresponde um percentual de: " + percentualDiff + "%.";
				}
				String dadosImagem = "A imagem possui: " + qtdTotalPixel + " pixels. " + textoDinamicoPixel;
				// Adiciona div de nova linha no HTML
				if (coluna == 3) {
					htmlExecucao = htmlExecucao.replace("<!--DIVIMAGEM-->", "");
					htmlExecucao = htmlExecucao.replace("<!--DIVROW-->", divRow);
					coluna = 0;
				}
				// Adiciona nova imagem no HTML
				htmlExecucao = htmlExecucao.replace("<!--DIVIMAGEM-->", divImagem);
				htmlExecucao = htmlExecucao.replace("<!--HTMLIMAGEM-->", HTMLimagem);
				htmlExecucao = htmlExecucao.replace("<!--ARQUIVOIMAGEM-->", arquivoImagem);
				htmlExecucao = htmlExecucao.replace("<!--NOMEIMAGEM-->", nomeValidacao);
				htmlExecucao = htmlExecucao.replace("<!--INFIMAGENS-->", dadosImagem);
				editarRelatorioDiferencas(historico, arquivoImagem, dadosImagem, percentualDiff);
				coluna++;
			}
			br.close();
			bw.write(htmlExecucao);
			bw.close();
		} catch (IOException e) {
			e.getMessage();
		}
	}

	private boolean isHistorico(String dirName) {
		if (Files.exists(Paths.get(dirName + "\\Resultados\\metadados.ini"))) {
			return true;
		}
		return false;
	}

	private boolean validarHistorico(File localHistorico) {
		Path arquivoMetaDadosPath = Paths.get(localHistorico + "\\Resultados\\");
		if (!Files.exists(Paths.get(arquivoMetaDadosPath + "\\metadados.ini"))) {
			return false;
		}
		List<String> infMetaDados = lerInformacoesMetaDados(arquivoMetaDadosPath);
		if (infMetaDados.size() <= 1) {
			return false;
		}
		return true;
	}

	private void buscarHistoricos() {
		List<Path> listaArquivosHistorico = new ArrayList<Path>();
		File diretorioImagens = new File(getPathImagens().toString());
		File[] fList = diretorioImagens.listFiles();
		for (File arquivo : fList) {
			if (arquivo.isDirectory()) {
				if (isHistorico(arquivo.getAbsolutePath())) {
					if (validarHistorico(arquivo)) {
						listaArquivosHistorico.add(Paths.get(arquivo.getAbsolutePath()));
					}
				}
				;
			}
		}
		historicoExecucoes = Lists.reverse(listaArquivosHistorico);
	}

	private String ExportResource(String resourceName, String resourceNameDest) throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;
		try {
			stream = GeneratorReport.class.getResourceAsStream(resourceName);
			if (stream == null) {
				throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
			}
			int readBytes;
			byte[] buffer = new byte[4096];
			// jarFolder = new
			// File(GeradorRelatorio.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile().getPath().replace('\\',
			// '/');
			resStreamOut = new FileOutputStream(resourceNameDest + resourceName);
			while ((readBytes = stream.read(buffer)) > 0) {
				resStreamOut.write(buffer, 0, readBytes);
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			stream.close();
			resStreamOut.close();
		}
		return resourceName;
	}

	public Path getPathRelatorio() {
		return pathRelatorio;
	}

	public void setPathRelatorio(String pathRelatorio) {
		this.pathRelatorio = Paths.get(pathRelatorio);
	}

	public Path getPathImagens() {
		return pathImagens;
	}

	public void setPathImagens(String pathImagens) {
		this.pathImagens = Paths.get(pathImagens);
	}

	public Path getPathBaseLine() {
		return pathBaseLine;
	}

	public void setPathBaseLine(String pathBaseLine) {
		this.pathBaseLine = Paths.get(pathBaseLine);
	}
}