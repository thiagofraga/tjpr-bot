package br.com.lm.view;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import br.com.lm.model.Processo;
import br.com.lm.service.PesquisaService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

@Component
public class PesquisaView {

	@FXML
    private TextField criterioPesquisa;

    @FXML
    private TextField classeProcessual;

    @FXML
    private TextField assunto;

    @FXML
    private TextField orgaoJulgador;

    @FXML
    private TextField relator;

    @FXML
    private TextField comarca;

    @FXML
    private DatePicker inicioJulgamento;

    @FXML
    private DatePicker fimJulgamento;

    @FXML
    private DatePicker inicioPublicacao;

    @FXML
    private DatePicker fimPublicacao;

    @FXML
    private Button btnPesquisar;
	
	@Autowired
	private PesquisaService pesquisaService;
	
	@FXML 
	protected void pesquisar(ActionEvent event) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		List<Processo> processos = pesquisaService.pesquisa(
				criterioPesquisa.getText(),
				classeProcessual.getText(),
				assunto.getText(),
				orgaoJulgador.getText(),
				relator.getText(),
				comarca.getText(),
				inicioJulgamento.getValue(), 
				fimJulgamento.getValue(),
				inicioPublicacao.getValue(),
				fimPublicacao.getValue());
        byte[] bytes = pesquisaService.toXlsx(processos);
        
        FileOutputStream fos = new FileOutputStream("C://resultado.xlsx");
        fos.write(bytes);
        fos.close();
    }
	
}
