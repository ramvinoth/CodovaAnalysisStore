import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.exec.util.StringUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import Utils.DBUtils;

public class CordovaAnalyser {
	static String endResultString;

	public static void main(String args[]) {
		// doAllVersions();
		// doAllCSPStuff();
		// doAllJSFrameWorkChecks();
		// checkPluginOrigin();
		// countAllEvals();
		 doDeepCSPAnalysis();
		//checkFileTransderPlugin();

	}

	static void checkFileTransderPlugin() {

		try {
			ArrayList<String> results = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader("filetransferapps.txt"));
			String appname;
			appname = br.readLine();

			while (appname != null) {
				String pathToAppSource = "/Volumes/Seagate/AppAnalysis/apps/decompfiles/" + appname;
				ArrayList<File> listOfFilesAl = new ArrayList<File>();
				listf(pathToAppSource, listOfFilesAl);
				
	
				for(int i = 0; i < listOfFilesAl.size(); i++){
				if(listOfFilesAl.get(i).getName().endsWith(".js")||listOfFilesAl.get(i).getName().endsWith(".html")){
				
				BufferedReader brfile = new BufferedReader(new FileReader(listOfFilesAl.get(i)));

				String line;
				while ((line = brfile.readLine()) != null) {
					
					if(line.contains(".download(")){
						results.add("DOWNLOAD;"+line.trim()+";"+listOfFilesAl.get(i));
						System.out.println("DOWNLOAD:\t"+line.trim());
					}
					if(line.contains(".upload(")){
						results.add("UPLOAD;"+line.trim()+";"+listOfFilesAl.get(i));
						System.out.println("UPLOAD:\t"+line.trim());
					}

				}
				
				
				}
				
				}

				appname = br.readLine();
			}
			
			
			System.out.println(results);
			Path file = Paths.get("resultsfiletransfer.txt");
			Files.write(file, results, Charset.forName("UTF-8"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	////////////////////////////////

	static int countMatches(String input, String word) {

		int index = input.indexOf(word);
		int count = 0;
		while (index != -1) {
			count++;
			input = input.substring(index + 1);
			index = input.indexOf(word);
		}
		return count;
	}

	private static void doDeepCSPAnalysis() {
		try (BufferedReader br = new BufferedReader(new FileReader("cspsdef.txt"))) {
			int totalProcessedLines = 0;
			int amountofunsafeEvals = 0;
			int amountofunsafeInlines = 0;
			int amountofhttp = 0;
			ArrayList<String> lijstje = new ArrayList<String>();
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					totalProcessedLines = totalProcessedLines + 1;
					if (line.toLowerCase().contains("unsafe-eval")) {
						amountofunsafeEvals = amountofunsafeEvals + 1;
					}
					if (line.toLowerCase().contains("unsafe-inline")) {
						amountofunsafeInlines = amountofunsafeInlines + 1;
					}
					int tempamountofhttp = countMatches(line.toLowerCase(), "http")
							- countMatches(line.toLowerCase(), "https")
							- countMatches(line.toLowerCase(), "http-equiv");
					if (tempamountofhttp > 0) {
						amountofhttp = amountofhttp + 1;
						lijstje.add(line+ "\n");
					} else {
						System.err.println("da kan ier gwnweg nie");
					}
				}
			}

			System.out.println("totallll: " + totalProcessedLines);
			System.out.println("unsafeEvals: " + amountofunsafeEvals);
			System.out.println("unsafeInline: " + amountofunsafeInlines);
			System.out.println("http: " + amountofhttp);
			System.out.println(lijstje);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static int checkforEval(String pathToAppSource) {
		int teller = 0;

		// alle files binnen die map ophalen
		String wwwDirPath = pathToAppSource + "/assets/www/";
		ArrayList<File> listOfFilesAl = new ArrayList<File>();
		listf(wwwDirPath, listOfFilesAl);

		// allemaal doorzoekenenenene
		for (int i = 0; i < listOfFilesAl.size(); i++) {

			BufferedReader br = null;

			try {
				br = new BufferedReader(new FileReader(listOfFilesAl.get(i)));
				String currentLine = br.readLine();

				while (currentLine != null) {

					if (currentLine.toLowerCase().contains("crosswalk")) {
						teller++;
					}

					currentLine = br.readLine();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return teller;
	}

	static void countAllEvals() {
		int totalevals = 0;
		ArrayList<String> al = DBUtils.getAllPhoneGapApps();
		Map<String, Integer> resultmap = new HashMap<String, Integer>();
		// System.out.println(al);
		for (int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i));

			String pathToAppSource = "/Volumes/Seagate/AppAnalysis/apps/decompfiles/" + al.get(i);
			int amountOfEvals = checkforEval(pathToAppSource);

			System.out.println("evals found for " + al.get(i) + ": " + amountOfEvals);
			totalevals = totalevals + amountOfEvals;
			resultmap.put(al.get(i), amountOfEvals);

		}

		System.out.println(resultmap);
		System.out.println(totalevals);

		try {
			PrintWriter pw = new PrintWriter(new FileWriter("crosswalks.csv"));
			for (Map.Entry<String, Integer> entry : resultmap.entrySet()) {

				System.out.println(entry.getKey() + "/" + entry.getValue());
				pw.println(entry.getKey() + ";" + entry.getValue());
			}
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static void checkPluginOrigin() {

		// alle plugins per app (average plugins per app)
		Map<String, ArrayList<String>> m1 = DBUtils.getPluginMaps().get(0);

		// alle apps per plugin (gebruiken voor tellerke, en om na te kijken
		// welke plugin van store komt)
		Map<String, ArrayList<String>> m2 = DBUtils.getPluginMaps().get(1);

		// resultmap aanmaken
		Map<String, Boolean> mres = new HashMap<String, Boolean>();

		// arrayList van alle plugins in de store
		ArrayList<String> al = new ArrayList<String>();
		// vullen
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("PluginMapping.csv"));
			String inp = br.readLine();
			while (inp != null) {
				al.add(inp.split(";")[1]);
				inp = br.readLine();
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}

		// System.out.println(al);
		int total = 0;
		int juist = 0;
		int fout = 0;

		int totalgewogen = 0;
		int juistgewogen = 0;
		int foutgewogen = 0;

		for (Map.Entry<String, ArrayList<String>> entry : m2.entrySet()) {
			String plugin = entry.getKey();

			if (al.contains(plugin)) {
				mres.put(plugin, true);
				juist++;
				juistgewogen = juistgewogen + entry.getValue().size();
			} else {
				mres.put(plugin, false);
				fout++;
				foutgewogen = foutgewogen + entry.getValue().size();
			}
			total++;
			totalgewogen = totalgewogen + entry.getValue().size();
		}

		System.out.println(mres);
		System.out.println(total + " " + juist + " " + fout);
		System.out.println(totalgewogen + " " + juistgewogen + " " + foutgewogen);

		// inlezen alle plugins.

	}

	static void doAllJSFrameWorkChecks() {
		ArrayList<String> al = DBUtils.getAllPhoneGapApps();
		Map<String, ArrayList<String>> resultmap = new HashMap<String, ArrayList<String>>();
		int teller = 0;
		for (int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i));

			String pathToAppSource = "/Volumes/Seagate/AppAnalysis/apps/decompfiles/" + al.get(i);
			ArrayList<String> foundjsFrameworks = checkForJSFrameworks(pathToAppSource);
			if (foundjsFrameworks.size() > 0) {
				teller++;
			}
			for (int j = 0; j < foundjsFrameworks.size(); j++) {
				String foundjsfw = foundjsFrameworks.get(j);
				System.out.println(foundjsfw);

				if (resultmap.containsKey(foundjsfw)) {
					resultmap.get(foundjsfw).add(al.get(i));
				} else {
					resultmap.put(foundjsfw, new ArrayList<String>());
					resultmap.get(foundjsfw).add(al.get(i));
				}
			}

		}

		for (String name : resultmap.keySet()) {

			String key = name.toString();
			String value = resultmap.get(name).toString();
			System.out.println(key + ";" + value);

		}

		for (String name : resultmap.keySet()) {

			String key = name.toString();
			String value = resultmap.get(name).size() + "";
			System.out.println(key + ";" + value);

		}

		System.out.println("Totaal aantal onderzochte pg apps: " + teller);
	}

	static void doAllCSPStuff() {

		endResultString = "";
		ArrayList<String> al = DBUtils.getAllPhoneGapApps();
		Map<String, ArrayList<String>> resultmap = new HashMap<String, ArrayList<String>>();

		for (int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i));

			String pathToAppSource = "/Volumes/Seagate/AppAnalysis/apps/decompfiles/" + al.get(i);
			String foundVersion = checkForCSP(pathToAppSource);

			System.out.println(foundVersion);

			if (resultmap.containsKey(foundVersion)) {
				resultmap.get(foundVersion).add(al.get(i));
			} else {
				resultmap.put(foundVersion, new ArrayList<String>());
				resultmap.get(foundVersion).add(al.get(i));
			}

		}

		for (String name : resultmap.keySet()) {

			String key = name.toString();
			String value = resultmap.get(name).toString();
			System.out.println(key + ";" + value);

		}

		for (String name : resultmap.keySet()) {

			String key = name.toString();
			String value = resultmap.get(name).size() + "";
			System.out.println(key + ";" + value);

		}

		try {
			PrintWriter out = new PrintWriter("cspsdef.txt");
			out.println(endResultString);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static void doAllVersions() {
		ArrayList<String> al = DBUtils.getAllPhoneGapApps();
		Map<String, ArrayList<String>> resultmap = new HashMap<String, ArrayList<String>>();

		for (int i = 0; i < al.size(); i++) {
			System.out.println(al.get(i));

			String pathToAppSource = "/Volumes/Seagate/AppAnalysis/apps/decompfiles/" + al.get(i);
			String foundVersion = getCordovaVersion(pathToAppSource);

			System.out.println(foundVersion);

			if (resultmap.containsKey(foundVersion)) {
				resultmap.get(foundVersion).add(al.get(i));
			} else {
				resultmap.put(foundVersion, new ArrayList<String>());
				resultmap.get(foundVersion).add(al.get(i));
			}

		}

		for (String name : resultmap.keySet()) {

			String key = name.toString();
			String value = resultmap.get(name).toString();
			System.out.println(key + ";" + value);

		}

		for (String name : resultmap.keySet()) {

			String key = name.toString();
			String value = resultmap.get(name).size() + "";
			System.out.println(key + ";" + value);

		}

	}

	public static void listf(String directoryName, ArrayList<File> files) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		if (fList != null) {
			for (File file : fList) {
				if (file.isFile()) {
					files.add(file);
				} else if (file.isDirectory()) {
					listf(file.getAbsolutePath(), files);
				}
			}
		}
	}

	public static ArrayList<String> checkForJSFrameworks(String pathToAppSource) {

		ArrayList<String> al = new ArrayList<>();
		String wwwDirPath = pathToAppSource + "/assets/www/";

		// get all items from the WWWdirectory.
		File wwwFolder = new File(wwwDirPath);
		ArrayList<File> listOfFilesAl = new ArrayList<File>();

		listf(wwwDirPath, listOfFilesAl);

		// File[] listOfFiles = wwwFolder.listFiles();

		if (listOfFilesAl != null) {
			for (int i = 0; i < listOfFilesAl.size(); i++) {
				System.out.println(listOfFilesAl.get(i).toString());

				if (listOfFilesAl.get(i).toString().toLowerCase().contains("sencha")) {
					al.add("sencha");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("jquery")) {
					al.add("jquery");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("ionic")) {
					al.add("ionic");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("famous")) {
					al.add("famous");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("famo.us")) {
					al.add("famo.us");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("ratchet")) {
					al.add("ratchet");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("lungo")) {
					al.add("lungo");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("junior")) {
					al.add("junior");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("onsen")) {
					al.add("onsen");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("intel")) {
					al.add("intel");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("kendo")) {
					al.add("kendo");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("angular")) {
					al.add("angular");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("zepto")) {
					al.add("zepto");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("limejs")) {
					al.add("limejs");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("jqtouch")) {
					al.add("jqtouch");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("mobilize")) {
					al.add("mobilize");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("dhtmlx")) {
					al.add("dhtmlx");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("enyo")) {
					al.add("enyo");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("tabris")) {
					al.add("tabris");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("xdk")) {
					al.add("xdk");
				}
				if (listOfFilesAl.get(i).toString().toLowerCase().contains("bootstrap")) {
					al.add("bootstrap");
				}

			}

			if (al.size() == 0) {
				al.add("No Known Js FrameWork Used");
			}

			// dupes verwijderen....
			al = new ArrayList<String>(new LinkedHashSet<String>(al));

		}

		return al;

	}

	static String checkForCSP(String pathToAppSource) {
		String hasCSPInHTML = "false";
		String checkstring = "http-equiv=\"Content-Security-Policy\"";
		String filePath = pathToAppSource + "/assets/www/index.html";
		File file = new File(filePath);
		if (file.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(file));
				String currentLine = br.readLine();
				while (currentLine != null && hasCSPInHTML.equals("false")) {
					if (currentLine.toLowerCase().contains(checkstring.toLowerCase())) {
						hasCSPInHTML = "true";
						System.out.println(currentLine);
						endResultString = endResultString + currentLine.trim() + "\n";
					} else {
						currentLine = br.readLine();
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			hasCSPInHTML = "index not found";
		}
		return hasCSPInHTML;
	}

	static String getCordovaVersion(String pathToAppSource) {
		// remake of this method
		String version = "Not Found";
		String tool = "Tool Unknown";
		String filePath = pathToAppSource + "/assets/www/";
		// kijken of een een phonegap.js of cordova.js file is
		File dir = new File(filePath + "/cordova.js");
		File dir2 = new File(filePath + "/phonegap.js");
		if (dir.exists() || dir2.exists()) {
			File cordovaFile = null;
			if (dir.exists()) {
				tool = "cordova";
				cordovaFile = dir;
			} else {
				cordovaFile = dir2;
				tool = "phonegap";
			}
			boolean versionFound = false;
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(cordovaFile));
				String currentLine = br.readLine();
				while (currentLine != null && !versionFound) {
					if (currentLine.contains("var PLATFORM_VERSION_BUILD_LABEL")
							|| currentLine.contains("var CORDOVA_JS_BUILD_LABEL")) {
						version = currentLine.split("'")[1];
						versionFound = true;
					} else {
						currentLine = br.readLine();
					}
				}
			} catch (IOException e) {
				version = "Version Not Found error1";
			}
		} else {
			// versienummer staat in de naam normaal gezien
			File fileDir = new File(filePath);
			FileFilter fileFilter = new WildcardFileFilter("cordova*.js");
			File[] files = fileDir.listFiles(fileFilter);
			if (files != null && files.length != 0) {
				tool = "cordova";
				version = files[0].toString().split("www/cordova")[1];
			} else {
				fileFilter = new WildcardFileFilter("phonegap*.js");
				files = fileDir.listFiles(fileFilter);
				if (files != null && files.length != 0) {
					tool = "phonegap";
					version = files[0].toString().split("www/phonegap")[1];
				} else {
					version = "Version Not Found 2";
				}
			}
		}
		return tool + " " + version;
	}

}
