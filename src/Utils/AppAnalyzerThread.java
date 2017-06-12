package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import AppAnalyzer.Configs;
import javafx.application.Platform;

public class AppAnalyzerThread extends Thread {

	static boolean keepGoing;

	public AppAnalyzerThread() {
		keepGoing = true;
	}

	public void run() {

		String appName = getNextApp();
		while (keepGoing && appName != null) {
			// getmanifest
			String fileLoc = Configs.decompiledTempFilesLoc + "/" + appName;
			File manifest = getAndroidManifest(fileLoc);

			// check cpu usage
			if (manifest != null) {
				checkCPTUsage(manifest, appName);
			}
			appName = getNextApp();
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				GUI.Main.getMainController().updateAnalyzerStopped();
			}
		});

		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getNextApp() {
		System.out.println("in getNextApp");
		String ret = null;
		ret = DBUtils.getAppFromStage(Configs.stage_decompiled);
		while (keepGoing && ret == null) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.err.println("error bij wachten - mag niet gebeuren");
			}
			ret = DBUtils.getAppFromStage(Configs.stage_decompiled);
		}
		return ret;
	}

	public static File getAndroidManifest(String fileLoc) {
		File f = new File(fileLoc);
		File[] matchingFiles = f.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.startsWith("AndroidManifest") && name.endsWith("xml");
			}
		});
		String appName = fileLoc.split("/")[fileLoc.split("/").length - 1];
		if (matchingFiles.length == 0) {

			// GEEN MANIFEST GEVONDEN => Failed state aanpassen

			DBUtils.setFailedState(appName, Configs.failed_manifestNotFound);

			return null;

		} else {
			// manifest wegschrijven naar map waar alle manifests terecht moeten
			// komen

			File manifestFile = matchingFiles[0];
			try {
				Files.copy(manifestFile.toPath(),
						new File(Configs.allManifests + "/" + appName + "_AndroidManifest.xml").toPath());
				DBUtils.setStage(appName, Configs.stage_manifest);
			} catch (IOException e) {
				System.out.println(
						"probleemke bij wegkopieren van de manifestfile, kan normaal gezien niet mis gaan....");

				// TODO Auto-generated catch block
				e.printStackTrace();

			}
			return manifestFile;

		}

	}

	public static String checkCPTUsage(File man, String appName) {
		String manif = "";
		Scanner sc = null;
		try {
			sc = new Scanner(man);

			while (sc.hasNextLine()) {
				manif = manif + sc.nextLine();
			}
		} catch (FileNotFoundException e) {
			// DIT OPVANGEN?
			// Kan nrml niet...

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String CPT = Configs.nativeorunknown;
		if (manif.length() < 10) {

			// MANIFEST IS USELESS
			DBUtils.setFailedState(appName, Configs.failed_manifestUseless);

		} else {

			String manifLC = manif.toLowerCase();

			// PHONEGAP
			if (manifLC.contains("cordova") || manifLC.contains("phonegap")) {
				CPT = Configs.cpt_phonegap;
			}

			// XAMARIN
			if (manifLC.contains("monoruntime") || manifLC.contains("xamarin")) {
				CPT = Configs.cpt_xamarin;
			}

			// ADOBE AIR
			if (manifLC.contains("air.")) {
				CPT = Configs.cpt_adobeair;
			}

			// TITANIUM
			if (manifLC.contains("titanium.")) {
				CPT = Configs.cpt_titanium;
			}

			// React
			if (manifLC.contains(".react.")) {
				CPT = Configs.cpt_react;
			}

			// Nativescript
			if (manifLC.contains(".tns.") || manifLC.contains("nativescript")) {
				CPT = Configs.cpt_nativescript;
			}

			// Unity
			if (manifLC.contains("unityplayer") || manifLC.contains("unity3d") || manifLC.contains(".unityactivity")) {
				CPT = Configs.cpt_unity;
			}

		}
		DBUtils.setCPT(appName, CPT);
		DBUtils.setStage(appName, Configs.stage_analysedForCpt);
		return CPT;
	}

}
