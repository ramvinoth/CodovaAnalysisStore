package Utils;

import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;

import AppAnalyzer.Configs;

public class DownloadUtils {
	
	String downloadLoc;
	DownloadThread dt;
	AndroidAppPushDownloaderThread adt;
	public DownloadUtils(){
	
	}
	
	
	public void startDownloadApps(){
		
		dt = new DownloadThread();
		dt.start(); 
		adt= new AndroidAppPushDownloaderThread();
		
		
		adt.start();
		
	}
	
	
	public void stopDownloadApps(){
		dt.keepGoing=false;
		adt.keepGoing=false;
		
	}
	
	

	
	
	
	
}
