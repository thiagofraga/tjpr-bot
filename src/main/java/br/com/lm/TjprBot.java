package br.com.lm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SpringBootApplication
public class TjprBot extends Application{

	private ConfigurableApplicationContext springContext;
	
	private Parent rootNode;
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
	@Override
	public void init() throws Exception {
		springContext = SpringApplication.run(TjprBot.class);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/pesquisa.fxml"));
		fxmlLoader.setControllerFactory(springContext::getBean);
		rootNode = fxmlLoader.load();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setScene(new Scene(rootNode));
		primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		springContext.stop();
	}

}