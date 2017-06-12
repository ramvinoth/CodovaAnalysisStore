package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import javax.sound.midi.Patch;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.UnreachableBrowserException;

import AppAnalyzer.Configs;
import javafx.application.Platform;

public class DownloadThread extends Thread {
	static boolean keepGoing;
	FirefoxProfile profile;
	FirefoxDriver driver;

	public DownloadThread() {

		profile = new FirefoxProfile();
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.dir", Configs.tempfilesLocPrimary);
		profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/vnd.android.package-archive, application/octet-stream");
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.download.manager.focusWhenStarting", false);
		profile.setPreference("browser.download.useDownloadDir", true);
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.download.manager.alertOnEXEOpen", false);
		profile.setPreference("browser.download.manager.closeWhenDone", true);
		profile.setPreference("browser.download.manager.showAlertOnComplete", true);
		profile.setPreference("browser.download.manager.useWindow", false);
		profile.setPreference("services.sync.prefs.sync.browser.download.manager.showWhenStarting", false);
		profile.setPreference("pdfjs.disabled", true);

		File file = new File("adblock_plus-2.7.3-sm+tb+fx+an.xpi");

		try {
			profile.addExtension(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		profile.setPreference("extensions.firebug.currentVersion", "1.8.1");

		driver = new FirefoxDriver(profile);

		keepGoing = true;
	}

	public void run() {

		String packagename = getNextApp();

		while (keepGoing && packagename != null) {

			if (driver == null) {
				driver = new FirefoxDriver(profile);
				ErrorLogger.writeError(
						"Nieuwe driver moeten maken - mag normaal niet... timestamp: " + System.currentTimeMillis());
			}

			System.out.println("ingelezen apk: " + packagename);

			try {

				long starttime = System.currentTimeMillis();

				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				// driver.get("http://google.com");
				driver.get("http://downloader-apk.com/?id=" + packagename);

				WebElement buttondownload = driver.findElement(By.id("download_apk_link"));

				buttondownload.click();

				// checken of er nog eens geklikt moet worden:

				String page = driver.getPageSource();
				if (!page.contains("apk is now downloading")) {
					System.out.println("er zal een 2e klik moeten gebeuren");
					if (page.contains("Proceed to")) {
						System.out.println("proceed to gevonden...");
						driver.findElement(By.id("download_apk_link")).click();
						System.out.println("2e klik zou gebeurd moeten zijn.");

					}

				}

				File dir = new File(Configs.tempfilesLocPrimary);

				boolean exists = false;

				while (!exists) {

					if (dir.isDirectory()) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						File[] files = dir.listFiles();

						Arrays.sort(files, new Comparator<File>() {
							public int compare(File f1, File f2) {
								return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
							}
						});

						if (files[0].lastModified() > starttime && files[0].getName().endsWith(".apk")
								&& files[0].length() > 0) {
							exists = true;
							// filename veranderen naar packagename.apk

							File newFile = new File(Configs.tempfilesLocPrimary + "/" + packagename + ".apk");
							files[0].renameTo(newFile);

							FileUtils.moveFile(newFile, new File(Configs.tempfilesLoc + "/" + packagename + ".apk"));

							// zeggen dat gedownload is
							DBUtils.setDownloaded(packagename);
							DBUtils.setDownloadTime(packagename, System.currentTimeMillis());

						} else {
							if (System.currentTimeMillis() > starttime + (1000 * 60 * 5)) {
								throw new DownloadException("DownloadException: timeout (took more than 5 minutes)");

							}

						}

					}

				}

				System.out.println("file: " + packagename + " gedownload.");
				// driver.quit();

			} catch (UnreachableBrowserException ube) {
				try {
					driver.quit();
				} catch (Exception eee) {
				}
				driver = new FirefoxDriver(profile);
				ErrorLogger.writeError(
						"Nieuwe driver moeten maken - UnreachableBrowserException: " + System.currentTimeMillis());
			} catch (Exception e) {
				System.err.println("DOWNLOAD FAILED: " + packagename);
				DBUtils.setDownloadFailed(packagename);
				// tussen try catch zetten want soms sluit browser vanzelf
				// af als er iets mis is.
				// try {
				// driver.quit();
				// } catch (Exception ek) {
				// System.err.println("driver stond al af...");
				// }

			}

			packagename = getNextApp();
			try {
				Thread.sleep(3000);
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

	public String getNextApp() {
		String ret = null;
		ret = DBUtils.getAppFromStage(Configs.stage_new);
		while (keepGoing && ret == null) {

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.err.println("error bij wachten - mag niet gebeuren");
			}
			ret = DBUtils.getAppFromStage(Configs.stage_new);
		}
		return ret;
	}

}
