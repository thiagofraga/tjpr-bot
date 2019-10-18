package br.com.lm.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import br.com.lm.model.Processo;

@Service
public class PesquisaService {

	public List<Processo> pesquisa(String criterio, String classeProcessual, String assunto, String orgaoJulgador, String relator, String comarca, LocalDate inicioJulgamento, LocalDate fimJulgamento, LocalDate inicioPublicacao, LocalDate fimPublicacao) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		List<Processo> processos = new ArrayList<>();
		final String host = "http://portal.tjpr.jus.br/";
		final String path = "jurisprudencia";
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			webClient.getOptions().setTimeout(12000);
			webClient.waitForBackgroundJavaScript(60000);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getCookieManager().setCookiesEnabled(true);
			
			webClient.getOptions().setUseInsecureSSL(true);
			HtmlPage page = webClient.getPage(host + path);

			//Seleciona pesquisa detalhada
			page.getElementById("tabItem1").click();
			
			//Preenche critério de pesquisa
			if(StringUtils.isNotBlank(criterio)) {
				HtmlTextInput inputCriterioPesquisa = page.getElementByName("criterioPesquisa");
				inputCriterioPesquisa.setText(criterio.trim());
			}
			
			//Preenche classe processual
			if(StringUtils.isNotBlank(classeProcessual)) {
				HtmlTextInput inputClasseProcessual = page.getElementByName("descricaoClasseProcessual");
				inputClasseProcessual.setText(classeProcessual.trim());
			}
			
			//Preenche assunto
			if(StringUtils.isNotBlank(assunto)) {
				HtmlTextArea inputAssunto = page.getElementByName("descricaoAssunto");
				inputAssunto.setText(assunto.trim());
			}
			
			//Preenche orgçao julgador
			if(StringUtils.isNotBlank(orgaoJulgador)) {
				HtmlTextInput inputOrgaoJulgador = page.getElementByName("nomeOrgaoJulgador");
				inputOrgaoJulgador.setText(orgaoJulgador.trim());
			}
			
			//Preenche relator
			if(StringUtils.isNotBlank(relator)) {
				HtmlTextInput inputRelator= page.getElementByName("nomeRelator");
				inputRelator.setText(relator.trim());
			}
			
			//Preenche comarca
			if(StringUtils.isNotBlank(comarca)) {
				HtmlTextInput inputComarca= page.getElementByName("nomeComarca");
				inputComarca.setText(comarca.trim());
			}
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			
	        //Preenche data julgamento inicial
			if(inicioJulgamento != null) {
		        HtmlTextInput inputDataJulgamentoInicio = page.getElementByName("dataJulgamentoInicio");
		        inputDataJulgamentoInicio.setText(inicioJulgamento.format(dtf));
			}
	        
	        //Preenche data julgamento final
			if(fimJulgamento != null) {
		        HtmlTextInput inputDataJulgamentoFim = page.getElementByName("dataJulgamentoFim");
		        inputDataJulgamentoFim.setText(fimJulgamento.format(dtf));
			}
			
			 //Preenche data publicacao inicial
			if(inicioPublicacao != null) {
		        HtmlTextInput inputDataPublicacaoInicio = page.getElementByName("dataPublicacaoInicio");
		        inputDataPublicacaoInicio.setText(inicioPublicacao.format(dtf));
			}
	        
	        //Preenche data publicacao final
			if(fimPublicacao != null) {
		        HtmlTextInput inputDataFim = page.getElementByName("dataPublicacaoFim");
		        inputDataFim.setText(fimPublicacao.format(dtf));
			}
	        
	        //Realiza pesquisa
	        page = page.getElementByName("iniciar").click();
	        
	        //Enquanto houver resultados, parse tabela de resultados e carrega próxima página
	        while(!((HtmlTable) page.getFirstByXPath("//table[contains(@class, 'resultTable')]")).getTextContent().contains("Nenhum registro encontrado!")) {
	        	
	        	HtmlTable table = page.getFirstByXPath("//table[contains(@class, 'resultTable jurisprudencia')]");
	        	List<HtmlTableRow> rows = table.getRows();
	        	
	        	Processo processo = null;
	        	String auxRelator = null;
	        	String auxProcesso = null;
	        	String auxDataPublicacao = null;
	        	String auxOrgaoJulgador = null;
	        	String auxDataJulgamento = null;
	        	String auxFonte = null;
	        	String auxResumo = null;
	        	String auxLink = null;
	        	
	        	for(int i = 1; i < rows.size(); i++) {
	        		HtmlTableRow row = rows.get(i);
	        		processo = new Processo();
	        		for(int j = 0; j < row.getCells().size(); j++) {
	        			HtmlTableCell cell = row.getCell(j);
	        			if(j == 0) {
	        				String linha = null;
							try(BufferedReader reader = new BufferedReader(new StringReader(cell.getLastElementChild().asText()))) {
								while((linha = reader.readLine()) != null) {
									if(linha.contains("Relator:"))
										auxRelator = linha.replace("Relator:", "").trim();
									else if(linha.contains("Processo:")) {
										if(linha.contains("Fonte:")) {
											auxProcesso = linha.substring(linha.lastIndexOf("Processo:"), linha.indexOf("Fonte:")).replace("Processo:", "").trim();
											auxFonte = linha.substring(linha.lastIndexOf("Fonte:"), linha.length()).replace("Fonte:", "").trim();
										} else {
											auxProcesso = linha.replace("Processo:", "").trim();
										}
									}
									else if(linha.contains("Data Publicação:"))
										auxDataPublicacao = linha.replace("Data Publicação:", "").trim();
									else if(linha.contains("Órgão Julgador:"))
										auxOrgaoJulgador = linha.replace("Órgão Julgador:", "").trim();
									else if(linha.contains("Data Julgamento:"))
										auxDataJulgamento = linha.replace("Data Julgamento:", "").trim();
								}
								processo.setProcesso(auxProcesso);
								processo.setRelator(auxRelator);
								processo.setDataPublicacao(auxDataPublicacao);
								processo.setOrgaoJulgador(auxOrgaoJulgador);
								processo.setDataJulgamento(auxDataJulgamento);
								processo.setFonte(auxFonte);
							}
							HtmlAnchor anchor = cell.getFirstByXPath("div[contains(@class, 'juris-tabela-propriedades')]/a");
							auxLink = host + anchor.getHrefAttribute();
							processo.setLink(auxLink);
	        			} else {
	        				auxResumo = cell.asText();
	        				processo.setResumo(auxResumo);
	        			}
	        		}
	        		processos.add(processo);
	        	}
		        
		        //Carrega próxima página
		        page = ((HtmlAnchor) page.getFirstByXPath("//a[contains(@class, 'arrowNextOn')]")).click();
	        }
		}
		
		return processos;
	}
	
	public byte[] toXlsx(List<Processo> processos) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Pesquisa");
			 
			Row header = sheet.createRow(0);
			 
			CellStyle headerStyle = workbook.createCellStyle();
			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			
			XSSFFont font = ((XSSFWorkbook) workbook).createFont();
			font.setFontName("Arial");
			font.setFontHeightInPoints((short) 16);
			font.setBold(true);
			headerStyle.setFont(font);
			 
			Cell headerCell = header.createCell(0);
			headerCell.setCellValue("Processo");
			headerCell.setCellStyle(headerStyle);
			 
			headerCell = header.createCell(1);
			headerCell.setCellValue("Orgão julgador");
			headerCell.setCellStyle(headerStyle);
			
			headerCell = header.createCell(2);
			headerCell.setCellValue("Relator");
			headerCell.setCellStyle(headerStyle);
			
			headerCell = header.createCell(3);
			headerCell.setCellValue("Fonte");
			headerCell.setCellStyle(headerStyle);
			
			headerCell = header.createCell(4);
			headerCell.setCellValue("Julgamento");
			headerCell.setCellStyle(headerStyle);
			
			headerCell = header.createCell(5);
			headerCell.setCellValue("Publicação");
			headerCell.setCellStyle(headerStyle);
			
			headerCell = header.createCell(6);
			headerCell.setCellValue("Link");
			headerCell.setCellStyle(headerStyle);
			
			headerCell = header.createCell(7);
			headerCell.setCellValue("Resumo");
			headerCell.setCellStyle(headerStyle);
			
			CellStyle style = workbook.createCellStyle();
			style.setWrapText(true);
			style.setVerticalAlignment(VerticalAlignment.TOP);
			
			for(int i = 0; i < processos.size(); i++) {
				Row row = sheet.createRow(i + 1);
				Cell cell = row.createCell(0);
				cell.setCellValue(processos.get(i).getProcesso());
				cell.setCellStyle(style);
				 
				cell = row.createCell(1);
				cell.setCellValue(processos.get(i).getOrgaoJulgador());
				cell.setCellStyle(style);
				
				cell = row.createCell(2);
				cell.setCellValue(processos.get(i).getRelator());
				cell.setCellStyle(style);
				
				cell = row.createCell(3);
				cell.setCellValue(processos.get(i).getFonte());
				cell.setCellStyle(style);
				
				cell = row.createCell(4);
				cell.setCellValue(processos.get(i).getDataJulgamento());
				cell.setCellStyle(style);
				
				cell = row.createCell(5);
				cell.setCellValue(processos.get(i).getDataPublicacao());
				cell.setCellStyle(style);
				
				cell = row.createCell(6);
				cell.setCellValue(processos.get(i).getLink());
				cell.setCellStyle(style);
				
				cell = row.createCell(7);
				cell.setCellValue(processos.get(i).getResumo());
				cell.setCellStyle(style);
			}
			
			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);
			sheet.autoSizeColumn(5);
			sheet.setColumnWidth(6, 7500);
			sheet.setColumnWidth(7, 15000);

			for(short i = sheet.getRow(1).getFirstCellNum(), end = sheet.getRow(0).getLastCellNum(); i < end; i++) {
				CellRangeAddress ca = new CellRangeAddress(
						0, 
						processos.size(), 
						sheet.getRow(0).getFirstCellNum(),
						sheet.getRow(0).getLastCellNum() - 1);
				sheet.setAutoFilter(ca);
			}
			
			workbook.write(baos);
		} finally {
			baos.close();
		}
		return baos.toByteArray();
	}
	
}
