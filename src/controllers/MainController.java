package controllers;

import GUI.GeneralOverviewThread;
import GUI.TableHelper;
import Utils.AndroidMarket;
import Utils.AppAnalyzerUtils;
import Utils.DownloadUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class MainController {

	AndroidMarket androidMarket;
	DownloadUtils downloadUtils;
	AppAnalyzerUtils appAnalyzerUtils;
	
	GeneralOverviewThread got;
	

	// crawler
	@FXML
	private Button crawlerStart;
	@FXML
	private Button crawlerStop;
	@FXML
	private Label crawlerCurrentActionLabel;
	@FXML
	private Label crawlerProgressLabel;

	// downloader
	@FXML
	private Button downloaderStart;
	@FXML
	private Button downloaderStop;
	@FXML
	private Label downloaderCurrentActionLabel;
	@FXML
	private Label downloaderProgressLabel;

	// analyzer
	// decompiler
	@FXML
	private Button decompilerStart;
	@FXML
	private Button decompilerStop;
	@FXML
	private Label decompilerCurrentActionLabel;
	@FXML
	private Label decompilerProgressLabel;
	// analyzer
	@FXML
	private Button analyzerStart;
	@FXML
	private Button analyzerStop;
	@FXML
	private Label analyzerCurrentActionLabel;
	@FXML
	private Label analyzerProgressLabel;

	// DB SECTION
	// General DB
	@FXML
	private Button GeneralDBLookup;
	@FXML
	private TextField generalDBInput;
	@FXML
	private TableView<ObservableList<String>> GeneralDBTable;
	// CRAWLER
	@FXML
	private Button DBCrawlerRefreshButton;
	@FXML
	private TableView<ObservableList<String>> DBCrawlerTableView;
	// APP OVERVIEW
	@FXML
	private Button DBAppOverViewRefreshButton;
	@FXML
	private TableView<ObservableList<String>> DBAppOverviewTableView;
	// PhoneGap
	@FXML
	private Button DBPhonegapRefreshButton;
	@FXML
	private TableView<ObservableList<String>> DBPhonegapTableView;
	
	//OVERVIEWTAB
	@FXML
	private ListView informationOverviewGeneralInfoListViewCPT;
	@FXML
	private ListView informationOverviewGeneralInfoListView;
	@FXML
	private PieChart informationOverviewPieChartCPT;
	@FXML
	private PieChart informationOverviewPieChart;
	@FXML
	private Tab informationOverviewTab;
	
	
	
	
	// crawler code
	public void initializeCrawlerScreen() {

		crawlerStop.setDisable(true);
		crawlerCurrentActionLabel.setText("crawler currently idle");
		crawlerProgressLabel.setText("todo");

		crawlerStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				crawlerStartAction();
			}
		});

		crawlerStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				crawlerStopAction();
			}
		});

	}

	public void crawlerStartAction() {
		System.out.println("crawler started");
		crawlerCurrentActionLabel.setText("starting crawler...");
		if (androidMarket == null) {
			androidMarket = new AndroidMarket();
		}
		androidMarket.startGetAppNames();
		crawlerCurrentActionLabel.setText("crawler running");
		crawlerStart.setDisable(true);
		crawlerStop.setDisable(false);
	}

	public void crawlerStopAction() {
		System.out.println("stopping crawler...");
		crawlerCurrentActionLabel.setText("stopping crawler when possible...");
		androidMarket.stopGetAppNames();
	}

	public void updateCrawlerStopped() {
		crawlerStart.setDisable(false);
		crawlerStop.setDisable(true);
		crawlerCurrentActionLabel.setText("crawler currently idle.");
	}

	// downloader code
	public void initializeDownloaderScreen() {

		downloaderStop.setDisable(true);
		downloaderCurrentActionLabel.setText("downloader currently idle");
		downloaderProgressLabel.setText("todo");

		downloaderStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				downloaderStartAction();
			}
		});

		downloaderStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				downloaderStopAction();
			}
		});

	}

	public void downloaderStartAction() {
		System.out.println("downloader started");
		downloaderCurrentActionLabel.setText("starting downloader...");
		if (downloadUtils == null) {
			downloadUtils = new DownloadUtils();
		}
		downloadUtils.startDownloadApps();
		downloaderCurrentActionLabel.setText("downloader running");
		downloaderStart.setDisable(true);
		downloaderStop.setDisable(false);
	}

	public void downloaderStopAction() {
		System.out.println("stopping downloader...");
		downloaderCurrentActionLabel.setText("stopping downloader when possible...");
		downloadUtils.stopDownloadApps();
	}

	public void updateDownloaderStopped() {
		downloaderStart.setDisable(false);
		downloaderStop.setDisable(true);
		downloaderCurrentActionLabel.setText("downloader currently idle.");
	}

	// analyser code
	// decompiler
	public void initializeDecompilerScreen() {

		decompilerStop.setDisable(true);
		decompilerCurrentActionLabel.setText("decompiler currently idle");
		decompilerProgressLabel.setText("todo");

		decompilerStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				decompilerStartAction();
			}
		});

		decompilerStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				decompilerStopAction();
			}
		});

	}

	public void decompilerStartAction() {
		System.out.println("decompiler started");
		decompilerCurrentActionLabel.setText("starting decompiler...");
		if (appAnalyzerUtils == null) {
			appAnalyzerUtils = new AppAnalyzerUtils();
		}
		appAnalyzerUtils.startDecompileApps();
		decompilerCurrentActionLabel.setText("decompiler running");
		decompilerStart.setDisable(true);
		decompilerStop.setDisable(false);
	}

	public void decompilerStopAction() {
		System.out.println("stopping decompiler...");
		decompilerCurrentActionLabel.setText("stopping decompiler when possible...");
		appAnalyzerUtils.stopDecompileApps();
	}

	public void updateDecompilerStopped() {
		decompilerStart.setDisable(false);
		decompilerStop.setDisable(true);
		decompilerCurrentActionLabel.setText("decompiler currently idle.");
	}

	// analyzer
	public void initializeAnalyzerScreen() {

		analyzerStop.setDisable(true);
		analyzerCurrentActionLabel.setText("analyzer currently idle");
		analyzerProgressLabel.setText("todo");

		analyzerStart.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				analyzerStartAction();
			}
		});

		analyzerStop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				analyzerStopAction();
			}
		});

	}

	public void analyzerStartAction() {
		System.out.println("analyzer started");
		analyzerCurrentActionLabel.setText("starting analyzer...");
		if (appAnalyzerUtils == null) {
			appAnalyzerUtils = new AppAnalyzerUtils();
		}
		appAnalyzerUtils.startAnalyzeApps();
		analyzerCurrentActionLabel.setText("analyzer running");
		analyzerStart.setDisable(true);
		analyzerStop.setDisable(false);
	}

	public void analyzerStopAction() {
		System.out.println("stopping analyzer...");
		analyzerCurrentActionLabel.setText("stopping analyzer when possible...");
		appAnalyzerUtils.stopAnalyzeApps();
	}

	public void updateAnalyzerStopped() {
		analyzerStart.setDisable(false);
		analyzerStop.setDisable(true);
		analyzerCurrentActionLabel.setText("analyzer currently idle.");
	}

	// DB code
	public void initializeDBTab() {
		GeneralDBLookup.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				GeneralDBTable.getItems().clear();
				GeneralDBTable.getColumns().clear();
				String input = generalDBInput.getText();

				TableHelper.fillTable(input, GeneralDBTable);

			}
		});

		DBCrawlerRefreshButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				DBCrawlerTableView.getItems().clear();
				DBCrawlerTableView.getColumns().clear();
				TableHelper.fillTable("SELECT * FROM KEYWORDS;", DBCrawlerTableView);

			}
		});

		DBAppOverViewRefreshButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				DBAppOverviewTableView.getItems().clear();
				DBAppOverviewTableView.getColumns().clear();
				TableHelper.fillTable("SELECT * FROM FOUNDAPPS;", DBAppOverviewTableView);

			}
		});

		DBPhonegapRefreshButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				DBPhonegapTableView.getItems().clear();
				DBPhonegapTableView.getColumns().clear();
				TableHelper.fillTable("SELECT * FROM FOUNDAPPS WHERE CPT IS 'PhoneGap';", DBPhonegapTableView);

			}
		});

	}
	
	
	
	
	//overviewTab starter
	public void initializeOverviewTab(){
	
	informationOverviewTab.setOnSelectionChanged(new EventHandler<Event>() {
		@Override
		public void handle(Event e){
			
			if(informationOverviewTab.isSelected()){
				got= new GeneralOverviewThread(informationOverviewGeneralInfoListView,informationOverviewGeneralInfoListViewCPT,informationOverviewPieChartCPT,informationOverviewPieChart);
				got.start();
			}else{
				got.stopUpdating();
			}
		}
	});
	}
	
	

	// Alertdialog
	public static void ShowAlert(String title, String contentText) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(contentText);

		alert.showAndWait();
	}

}
