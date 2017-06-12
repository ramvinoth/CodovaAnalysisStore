package Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.w3c.dom.Document;

import com.google.common.base.CaseFormat;

import javafx.application.Platform;
import net.anthavio.phanbedder.Phanbedder;

public class AndroidMarketThreadNoMarketAPI extends Thread {

	PhantomJSDriver driver;
	// FirefoxDriver driver;
	boolean done;
	boolean keepGoing;
	String keyword;

	public AndroidMarketThreadNoMarketAPI() {
		File phantomjs = Phanbedder.unpack();
		DesiredCapabilities dcaps = new DesiredCapabilities();
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());
		driver = new PhantomJSDriver(dcaps);
		// driver = new FirefoxDriver();
		done = false;
		keepGoing = true;

	}

	public void run() {
		// keyword = getNextKeyword();
		keyword = "pis";
		// while (keepGoing && keyword != null) {
		ArrayList<String> packageNames = new ArrayList<String>();
		done = false;

		// pagina openen en naar beneden scrollen
		driver.get("https://play.google.com/store/search?q=" + keyword + "&c=apps");

		boolean scrolledtothemax = scrollAsDownAsPossible();
		System.out.println(scrolledtothemax);

		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(srcFile, new File("/Users/michielwillocx/Desktop/screenshotteke.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<WebElement> apps = driver.findElements(By.cssSelector(".preview-overlay-container"));
		System.out.println(apps.size());
		for (int i = 0; i < apps.size(); i++) {
			String pname = apps.get(i).getAttribute("data-docid");
			System.out.println(pname);
			packageNames.add(pname);
		}

		for (int i = 0; i < packageNames.size(); i++) {
			String pname = packageNames.get(i);
			driver.get("https://play.google.com/store/apps/details?id=" + pname);

	

			String dlbuttintext = driver.findElement(By.cssSelector(".price.buy.id-track-click.id-track-impression"))
					.getAttribute("innerHTML");

			//eerst checken of het ne gratis app is.
			if (!dlbuttintext.toLowerCase().contains("<span>kopen")) {

				String packageName = pname;
				String creator = "";// OK
				String displayedName = "";// OK
				int version = 0;// NOTOK NOTOK --> manifest
				String dlCount = "";// OK
				String type = "";// ?
				String category = "";// ?
				double rating = 0;// OK
				int ratingCount = 0;// OK
				int installSize = 0;// OK
				String lastUpdate = "";// OK
				String osVersionNeeded = "";// OK

				// displayedName
				WebElement dispnameElem = driver.findElement(By.className("id-app-title"));
				displayedName = dispnameElem.getAttribute("innerHTML");

				WebElement creatorElem = driver.findElement(By.cssSelector(".document-subtitle.primary"));
				creator = creatorElem.findElement(By.tagName("span")).getAttribute("innerHTML");

				List<WebElement> scoreElem = driver.findElements(By.className("score-container"));
				try {
					rating = Double.parseDouble(scoreElem.get(0)
							.findElement(By.xpath("//meta[@itemprop='ratingValue']")).getAttribute("content"));
				} catch (Exception e) {
					rating = -1.0;
				}

				try {
					ratingCount = Integer.parseInt(scoreElem.get(0)
							.findElement(By.xpath("//meta[@itemprop='ratingCount']")).getAttribute("content"));
				} catch (Exception e) {
					rating = -1;
				}

				List<WebElement> wes = driver.findElement(By.cssSelector(".details-section.metadata"))
						.findElement(By.className("details-section-contents"))
						.findElements(By.cssSelector(".meta-info"));

				for (int j = 0; j < wes.size(); j++) {
					// System.out.println(wes.get(j).getAttribute("innerHTML"));

					// last update:
					if (wes.get(j).findElement(By.className("title")).getAttribute("innerHTML").contains("ijgewerkt")) {
						lastUpdate = wes.get(j).findElement(By.className("content")).getAttribute("innerHTML");
					}

					// dlcount:
					if (wes.get(j).findElement(By.className("title")).getAttribute("innerHTML")
							.contains("nstallaties")) {
						dlCount = wes.get(j).findElement(By.className("content")).getAttribute("innerHTML").trim();
					}

					// installsize: size omzetten naar een int
					if (wes.get(j).findElement(By.className("title")).getAttribute("innerHTML").contains("rootte")) {
						String temp = wes.get(j).findElement(By.className("content")).getAttribute("innerHTML");
						if (temp.contains("M")) {
							try {
								installSize = (int) Math.round(Double.parseDouble(temp.split("M")[0].trim().replace(",", "."))*(1024*1024));
								
							} catch (NumberFormatException nfe) {
							nfe.printStackTrace();
								installSize = -1;
							}
						} else {
							installSize = -1;
						}

					}

					// Osversionneeded:
					if (wes.get(j).findElement(By.className("title")).getAttribute("innerHTML")
							.contains("ndroid vereist")) {
						osVersionNeeded = wes.get(j).findElement(By.className("content")).getAttribute("innerHTML")
								.trim();
					}

				}
				System.out.println("\nlastUpdate: " + lastUpdate + "\npackageName: " + packageName + "\ncreator: "
						+ creator + "\ndisplayedName: " + displayedName + "\nversion: " + version + "\ndlCount: "
						+ dlCount + "\ntype: " + type + "\ncategory: " + category + "\nrating: " + rating
						+ "\nratingCount: " + ratingCount + "\ninstallSize: " + installSize + "\nosVersionNeeded: "
						+ osVersionNeeded);
			} else {
				System.err.println("Dit was een betalende app (testing purposes)");
			}
		}
		// nieuw keyword opvragen
		// }
		/*
		 * Platform.runLater(new Runnable() {
		 * 
		 * @Override public void run() {
		 * GUI.Main.getMainController().updateCrawlerStopped(); // Update/Query
		 * the FX classes here } });
		 * 
		 * try { this.finalize(); } catch (Throwable e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */
	}

	private boolean scrollAsDownAsPossible() {
		int teller = 0;
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		WebElement temp;
		boolean scrollFinishedSuccess = true;
		By selBy = By.tagName("body");
		int currentScrollHeight = 0;
		String previousPage = "";
		String currentPage = "";
		boolean done = false;
		while (!done) {
			temp = driver.findElement(By.id("show-more-button"));
			if (temp.isDisplayed()) {
				temp.click();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return false;
				}
			}
			currentScrollHeight = currentScrollHeight + 200;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			jse.executeScript("window.scrollTo(0," + currentScrollHeight + ");", "");
			currentPage = driver.getPageSource();
			if (currentPage.equals(previousPage)) {
				teller++;
				if (teller > 30) {
					done = true;
				}

			} else {
				teller = 0;
				previousPage = currentPage;
			}
		}

		return scrollFinishedSuccess;
	}

	public String getNextKeyword() {
		System.out.println("in get next keyword");
		String ret = null;
		ret = DBUtils.getUnusedKeyword();
		while (keepGoing && ret == null) {

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				System.err.println("error bij wachten - mag niet gebeuren");
			}
			ret = DBUtils.getUnusedKeyword();
		}
		return ret;
	}

}
