package GUI;
	
import controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;



public class Main extends Application {
	
	static MainController controller;

	@Override
	public void start(Stage stage) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("MainPage.fxml"));
			Parent root=loader.load();
			Scene scene = new Scene(root,1600,800);
			
			controller = loader.<MainController>getController();
			
			controller.initializeAnalyzerScreen();
			controller.initializeDecompilerScreen();
			controller.initializeCrawlerScreen();
			controller.initializeDownloaderScreen();
			controller.initializeDBTab();
			controller.initializeOverviewTab();
			
			stage.setTitle("App Analyzer");
			
			
			
			
			
			stage.setScene(scene);
			stage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static MainController getMainController(){
		return controller;
	}
	
	
	
}
