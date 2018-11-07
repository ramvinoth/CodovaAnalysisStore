package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.UnreachableBrowserException;

import AppAnalyzer.Configs;
import javafx.application.Platform;
import net.anthavio.phanbedder.Phanbedder;

public class AndroidAppPushDownloaderThread extends Thread {

	static final String username = "<fill_your_email_id>";
	static final String password = "<fill_your_password>";
	boolean keepGoing;
	FirefoxDriver driver;
	DesiredCapabilities dcaps;
	FirefoxProfile profile;
	FirefoxOptions option;

	public AndroidAppPushDownloaderThread() {
		
		

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

		profile.addExtension(file);
		profile.setPreference("extensions.firebug.currentVersion", "1.8.1");
		
		System.setProperty("webdriver.gecko.driver", "D:\\Softwares\\geckodriver.exe");
		FirefoxProfile profile =new FirefoxProfile(new File("C:\\Users\\ramvi\\AppData\\Roaming\\Mozilla\\Firefox\\Profiles\\6p4jat9o.default"));
		option=new FirefoxOptions();
		option.setProfile(profile);
		
		keepGoing = true;
		driver = new FirefoxDriver(option);
	}

	@Override
	public void run() {

		loginToPlayStore();

		String packageName = getNextApp();
		while (keepGoing && packageName != null) {

			downloadAppViaPush(packageName);

			packageName = getNextApp();

		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				
				if (DownloadThread.keepGoing == false) {
					GUI.Main.getMainController().updateDownloaderStopped();
				}
				
			}
		});

		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getNextApp() {
		String ret = null;

		ret = DBUtils.getAppFromFailedState(Configs.failed_dl);
		while (keepGoing && ret == null) {

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.err.println("error bij wachten - mag niet gebeuren");
			}
			ret = DBUtils.getAppFromFailedState(Configs.failed_dl);
		}
		return ret;

	}

	public void screenshotDriver() {

		File srcFile = driver.getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(srcFile, new File("test.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void loginToPlayStore() {
		// TODO
		driver.get("https://play.google.com/store/apps/details?id=com.viber.voip");
	
		WebElement loginKnop = driver.findElement(By.cssSelector(".gb_Ae.gb_Aa.gb_Fb"));
		
		System.out.println(loginKnop.getAttribute("innerHTML"));
		loginKnop.click();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
		// fill in username
		WebElement usernameField = driver.findElement(By.cssSelector(".aXBtI.Wic03c"))
				.findElement(By.id("identifierId"));
		usernameField.sendKeys(username);

		// press next
		WebElement nextButton = driver.findElement(By.cssSelector("identifierNext"));
		nextButton.click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}catch(Exception e) {
			
		}
		// fill in password
		WebElement passwordField = driver.findElement(By.cssSelector(".aXBtI.I0VJ4d.Wic03c")).findElement(By.cssSelector(".whsOnd.zHQkBf"));
		passwordField.sendKeys(password);

		WebElement loginButton = driver.findElement(By.id("passwordNext"));
		loginButton.click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// screenshotDriver();
	}

	public void downloadAppViaPush(String packageName) {

		if (driver == null) {
			driver = new FirefoxDriver(option);
			loginToPlayStore();
		}
		System.out.println("Package : "+packageName);
		try {
			driver.get("https://play.google.com/store/apps/details?id=" + packageName);

			WebElement installButton = driver.findElement(
					By.cssSelector(".LkLjZd.ScJHi.HPiPcc.IfEcue"));
			
			
			installButton.click();

			Thread.sleep(2000);

			WebElement installButtonConfirm = driver.findElement(By.id("purchase-ok-button"));
			installButtonConfirm.click();

			Thread.sleep(1000);

			// kijken of het gedownload is (pas als het gedlt en geinstalleerd
			// is komt het in de adb shell pm list packages lijst terecht.

			boolean downloaded = false;
			long startTime = System.currentTimeMillis();

			while (!downloaded) {
				// 15 sec slapen voordat we opnieuw gaan testen
				Thread.sleep(15000);
				Process p = new ProcessBuilder("adb", "shell", "pm", "list", "packages").start();

				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String line = "";
				String out = "";
				while ((line = input.readLine()) != null) {
					out = out + line + "\n";
				}
				// System.out.println(out);
				if (out.contains(packageName)) {
					downloaded = true;
				}

				if (startTime < System.currentTimeMillis() - (60 * 1000 * 10)) {
					throw new DownloadException("timeout bij downloaden door pushen naar phone");
				}

			}

			// pad naar packagename lezen
			Process p = new ProcessBuilder("adb", "shell", "pm", "path", packageName).start();

			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String line = "";
			String out = "";
			while ((line = input.readLine()) != null) {
				out = out + line;
			}

			System.out.println("pad naar package: " + out);

			// app naar de harddisk weg schrijven
			p = new ProcessBuilder("adb", "pull", out.split("package:")[1],
					Configs.tempfilesLoc + "/" + packageName + ".apk").start();

			input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader err = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			line = "";
			out = "";
			while ((line = input.readLine()) != null) {
				out = out + line + "\n";
			}
			while ((line = err.readLine()) != null) {
				out = out + line + "\n";
			}

			System.out.println(out);

			// app terug verwijderen van den telefoon
			p = new ProcessBuilder("adb", "uninstall", packageName).start();

			while ((line = input.readLine()) != null) {
				out = out + line + "\n";
			}
			while ((line = err.readLine()) != null) {
				out = out + line + "\n";
			}

			System.out.println(out);

			DBUtils.setDownloaded(packageName);
			DBUtils.setFailedState(packageName, Configs.failed_no);
			DBUtils.setDownloadTime(packageName, System.currentTimeMillis());

		} catch (UnreachableBrowserException ube) {
			try {
				driver.quit();
			} catch (Exception eee) {
			}
			driver = new FirefoxDriver(option);
			ErrorLogger.writeError(
					"(ALTDL)Nieuwe driver moeten maken - UnreachableBrowserException: " + System.currentTimeMillis());
			loginToPlayStore();
		} catch (Exception e) {
			DBUtils.setFailedState(packageName, 11);
			e.printStackTrace();
		}

	}

}
