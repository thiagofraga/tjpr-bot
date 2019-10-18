package br.com.lm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TjprBotApplicationTests {

	@Test
	public void contextLoads() throws Exception {
		try (final WebClient webClient = new WebClient()) {
			HtmlPage page = webClient.getPage("http://portal.tjpr.jus.br/jurisprudencia/");
	        
	        DomElement tabPesquisaDetalhada = page.getElementById("tabItem1");
	        tabPesquisaDetalhada.click();
	        
	        HtmlForm form = page.getFormByName("pesquisaForm");
	        
	        HtmlTextInput inputOrgaoJulgador = form.getInputByName("nomeOrgaoJulgador");
	        inputOrgaoJulgador.setText("8ª Câmara Cível");
	        
	        HtmlTextInput inputDataInicio = form.getInputByName("dataJulgamentoInicio");
	        inputDataInicio.setText("01/01/2017");
	        
	        HtmlTextInput inputDataFim = form.getInputByName("dataJulgamentoFim");
	        inputDataFim.setText("31/12/2017");
	        
	        HtmlButton buttonPesquisar = form.getButtonByName("iniciar");
	        buttonPesquisar.click();
		}
	}

}
