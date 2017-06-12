package PhoneGapUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import AppAnalyzer.Configs;
import Utils.DBUtils;

public class PhoneGapAnalyzerUtils {
	
	
	
	public static void populateDedicatedPhoneGapTable(){
		ArrayList<String>list = DBUtils.getUnusedPhoneGapApps();
		for (int i = 0; i < list.size(); i++) {
			DBUtils.addPhoneGapAppToDedicatedTable(list.get(i), "0", "0");
		}
		
	}

	public static boolean getConfigXML(String packageName) {
		boolean ret = true;
		File xmlFile = Paths.get("/Volumes/Seagate/AppAnalysis/apps/decompfiles/" + packageName + "/res/xml/config.xml")
				.toFile();
		System.out.println(xmlFile.getPath());

		if (xmlFile.exists()) {
			try {
				FileUtils.copyFile(xmlFile, new File(Configs.pg_configFiles + "/" + packageName + "_config.xml"));
				DBUtils.setConfigXmlStatus(packageName, "1");
			} catch (IOException e) {
				DBUtils.setConfigXmlStatus(packageName, "3");
				ret = false;
				e.printStackTrace();
			}
		} else {
			// todo
			DBUtils.setConfigXmlStatus(packageName, "2");
			ret = false;
		}

		return ret;
	}

	public static boolean findAllPlugins(String packageName) {
		boolean ret = true;

		Map<String, String> foundPluginList = new HashMap<String, String>();

		try {
			File xmlDocument = Paths.get(Configs.pg_configFiles + "/" + packageName + "_config.xml").toFile();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document document = documentBuilder.parse(xmlDocument);

			NodeList plugins = document.getElementsByTagName("feature");

			for (int i = 0; i < plugins.getLength(); i++) {
				Element plugin = (Element) plugins.item(i);
				foundPluginList.put(plugin.getAttribute("name"),
						((Element) (plugin.getElementsByTagName("param").item(0))).getAttribute("value"));
			}
			System.out.println("Total plugins: " + foundPluginList.size());

			plugins = document.getElementsByTagName("plugin");
			for (int i = 0; i < plugins.getLength(); i++) {
				Element plugin = (Element) plugins.item(i);
				foundPluginList.put(plugin.getAttribute("name"), plugin.getAttribute("value"));

			}

			if (foundPluginList.size() < 1) {
				ret = false;

				System.out.println("no plugins found in config list");
				DBUtils.setPGPluginStatus(packageName, "2");
			} else {

				System.out.println("Total plugins: " + foundPluginList.size());

				for (String key : foundPluginList.keySet()){
					System.out.println(key + " - " + foundPluginList.get(key));
					DBUtils.addPhoneGapPlugin(packageName, key, foundPluginList.get(key), "-1");}
			}

			DBUtils.setPGPluginStatus(packageName, "1");

		} catch (Exception e) {
			ret = false;
			DBUtils.setPGPluginStatus(packageName, "3");
			e.printStackTrace();
		}

		return ret;
	}

}
