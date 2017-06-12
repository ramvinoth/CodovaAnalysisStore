package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.io.monitor.FileEntry;

import AppAnalyzer.Configs;
import javafx.application.Platform;

public class DecompilerThread extends Thread {
	static boolean keepGoing;
	


	public DecompilerThread() {
		keepGoing=true;
	}

	public void run() {
		
		//get all files

		
		String appName= getNextApp();
		while(keepGoing&&appName!=null){

			File f = new File(Configs.tempfilesLoc + "/" + appName + ".apk");
			if (f.exists()) {
				FileEntry fileEntry = new FileEntry(f);

				if (!alreadyDecompiled(fileEntry.getName())) {
					decompileApp(fileEntry.getName());
				}else{
					System.out.println("was al gedecompiled (look into this)");
				}
			}
			else {
				System.out.println("was niet gedownload, wordt terug op niet gedownload gezet");
				DBUtils.setNotDownloaded(appName);
				ErrorLogger.writeError(System.currentTimeMillis()+": app: " + appName + " was not downloaded yet");
			}

			appName=getNextApp();
		}
		
		Platform.runLater(new Runnable() {
			   @Override
			   public void run() {GUI.Main.getMainController().updateDecompilerStopped();
			      // Update/Query the FX classes here
			   }
			});
		
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	
	public String getNextApp(){
		System.out.println("in get next app");
		String ret = null;
		ret=DBUtils.getAppFromStage(Configs.stage_downloaded);
		while(keepGoing&&ret==null){
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			System.err.println("error bij wachten - mag niet gebeuren");
		}
		ret=DBUtils.getAppFromStage(Configs.stage_downloaded);
		}
		return ret;
	}

	private static boolean alreadyDecompiled(String name) {
		String folderName = name.substring(0, name.length() - 4);
		System.out.println(folderName);
		// hier heb ik iets raar gedaan, werkt mogelijk nie
		for (final File fileEntry : new File(Configs.decompiledTempFilesLoc)
				.listFiles()) {
			// System.out.println(fileEntry.getName());
			if (fileEntry.getName().equals(folderName)) {
				System.out.println("app: " + folderName + " was already decompiled");
				DBUtils.setStage(folderName, Configs.stage_decompiled);
				ErrorLogger.writeError(System.currentTimeMillis()+": app: " + name + " was already decompiled");
				return true;
			}

		}
		System.out.println("app: " + name + " was not decompiled already");
		return false;
	}

	private static void decompileApp(String name) {
		System.out.println("decompiling " + name);
		try {
			ProcessBuilder pb = new ProcessBuilder(
					"java",
					"-jar",
					Configs.locationAPKtool,
					"d",
					"--frame-path",
					"/Users/michielwillocx/Downloads/apkstudio-2/binaries/apktool/",
					Configs.tempfilesLoc + "/" + name, "-o",
					Configs.decompiledTempFilesLoc + "/"
							+ name.substring(0, name.length() - 4));
			Process p;

			p = pb.start();

			// uitprinten command line output

			String output = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(
					p.getErrorStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				output = output + line + "\n";
			}
			while ((line = reader2.readLine()) != null) {
				output = output + line + "\n";
			}
			//System.out.println(output);

			int tttt = p.waitFor();
			
			
			
			//STAGE AANPASSEN NAAR GEDEDOMPILEERD
			DBUtils.setStage(name.substring(0,name.length()-4), Configs.stage_decompiled);
			//ALS HET GEDECOMPILEERD IS: originele APK file verwijderen.
			FileHelper.removeFile(Configs.tempfilesLoc + "/" + name);
			
		} catch (IOException | InterruptedException e) {
			//Decomile failed
			DBUtils.setFailedState(name, Configs.failed_decomp);
			
			
			
			
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	
	

}
