package GUI;

import java.util.ArrayList;

import AppAnalyzer.Configs;
import Utils.DBUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ListView;

public class GeneralOverviewThread extends Thread {

	static boolean keepUpdating;
	
	ListView<String> cptListView;
	ListView<String> overallListView;
	PieChart pcCPT;
	PieChart pcOverview;

	public GeneralOverviewThread(ListView<String> lv1, ListView<String> lv2, PieChart pc1, PieChart pc2) {
		overallListView = lv1;
		cptListView = lv2;
		pcCPT = pc1;
		pcOverview = pc2;
		keepUpdating=false;
	}
	
	public void stopUpdating(){
		keepUpdating=false;
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void generateOverallListView() {
		ArrayList<String> al = new ArrayList<String>();
		al.add("KEYWORDS: ");
		al.add("\t Total: " + DBUtils.getAmount("KEYWORDS", "") + ", Checked: "
				+ DBUtils.getAmount("KEYWORDS", "CHECKED IS 1"));
		al.add("\t Total permissions found: " + DBUtils.getAmount(Configs.table_permissions, ""));

		al.add("FOUND APPS: ");
		al.add("\t Total apps found: " + DBUtils.getAmount(Configs.table_foundapps, ""));
		al.add("\t Apps downloaded: " + DBUtils.getAmount(Configs.table_foundapps, "STAGE > 0"));
		al.add("\t Apps decompiled: " + DBUtils.getAmount(Configs.table_foundapps, "STAGE > 1"));
		al.add("\t Manifests found: " + DBUtils.getAmount(Configs.table_foundapps, "STAGE > 2"));
		al.add("\t Manifests analyzed for cpt usage: " + DBUtils.getAmount(Configs.table_foundapps, "STAGE > 3"));

		al.add("ERRORS: ");
		al.add("\t Download failed (DLer1): " + DBUtils.getAmount(Configs.table_foundapps, "FAILED = 1"));
		al.add("\t Download failed (DLer2): " + DBUtils.getAmount(Configs.table_foundapps, "FAILED = 11"));
		al.add("\t Decompilation failed: " + DBUtils.getAmount(Configs.table_foundapps, "FAILED = 2"));
		al.add("\t Finding manifest failed: " + DBUtils.getAmount(Configs.table_foundapps, "FAILED = 3"));
		al.add("\t Manifest useless: " + DBUtils.getAmount(Configs.table_foundapps, "FAILED = 4"));
		al.add("");
		al.add("SOM AANGERAAKTE APPS: " + DBUtils.getAmount(Configs.table_foundapps, "STAGE <> 0 or FAILED <> 0"));
		ObservableList<String> observableList = FXCollections.observableList(al);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				overallListView.setItems(observableList);
				// if you change the UI, do it here !
			}
		});

	}

	public void generateCPTListView() {
		ArrayList<String> al = new ArrayList<String>();
		al.add("CROSS PLATFORM TOOLS FOUND: ");
		al.add("\t No CPT: " + DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.nativeorunknown + "'"));
		al.add("\t PhoneGap: " + DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_phonegap + "'"));
		al.add("\t Adobe Air: " + DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_adobeair + "'"));
		al.add("\t NativeScript: "
				+ DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_nativescript + "'"));
		al.add("\t React Native: " + DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_react + "'"));
		al.add("\t Titanium: " + DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_titanium + "'"));
		al.add("\t Unity: " + DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_unity + "'"));
		al.add("\t Xamarin: " + DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_xamarin + "'"));
		ObservableList<String> observableList = FXCollections.observableList(al);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				cptListView.setItems(observableList);
				// if you change the UI, do it here !
			}
		});

	}

	public void generateCPTPieChart() {
		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("No CPT",
						DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.nativeorunknown + "'")),
				new PieChart.Data("PhoneGap",
						DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_phonegap + "'")),
				new PieChart.Data("Adobe Air",
						DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_adobeair + "'")),
				new PieChart.Data("NativeScript",
						DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_nativescript + "'")),
				new PieChart.Data("React Native",
						DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_react + "'")),
				new PieChart.Data("Titanium",
						DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_titanium + "'")),
				new PieChart.Data("Unity",
						DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_unity + "'")),
				new PieChart.Data("Xamarin",
						DBUtils.getAmount(Configs.table_foundapps, "CPT IS '" + Configs.cpt_xamarin + "'")));

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				pcCPT.setTitle("FOUND CPTs");
				pcCPT.setData(pieChartData);
			}
		});

	}

	public void generateOverviewPieChart() {

		int downloadedapps = DBUtils.getAmount(Configs.table_foundapps, "STAGE IS 1");
		int decompiledapps = DBUtils.getAmount(Configs.table_foundapps, "STAGE IS 2");
		int analyzedapps = DBUtils.getAmount(Configs.table_foundapps, "STAGE > 3");

		ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
				new PieChart.Data("\t Failed Apps", DBUtils.getAmount(Configs.table_foundapps, "FAILED > 0")),
				new PieChart.Data("\t Downloaded Stage", downloadedapps),
				new PieChart.Data("\t Decompiled Stage", decompiledapps),
				new PieChart.Data("\t Analyzed", analyzedapps));
		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				pcOverview.setTitle("PROCESSED APPS");
				pcOverview.setData(pieChartData);
			}
		});

	}

	public void run() {
		keepUpdating=true;
		while (keepUpdating) {
			generateOverallListView();
			generateCPTListView();
			generateCPTPieChart();
			generateOverviewPieChart();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
