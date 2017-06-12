package Utils;

import java.io.File;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import net.anthavio.phanbedder.Phanbedder;

public class AndroidPlayStoreHelper {
PhantomJSDriver driver;


public AndroidPlayStoreHelper() {
	 //Phanbedder to the rescue!
    File phantomjs = Phanbedder.unpack();
    DesiredCapabilities dcaps = new DesiredCapabilities();
    dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjs.getAbsolutePath());
    driver = new PhantomJSDriver(dcaps);

}
	
	
	
	public String[] getLastUpdatedAndOsVersion(String packageName){
		String date="";
		String osVersion="";
    //Usual Selenium stuff...
        driver.get("https://play.google.com/store/apps/details?id="+packageName);
       // System.out.println(driver.getPageSource().contains("ijgewerkt"));
        List<WebElement> we= driver.findElements(By.className(("details-section-contents")));
		//System.out.println(we.size());
		for (int i = 0; i < we.size(); i++) {
			if(we.get(i).getAttribute("innerHTML").contains("ijgewerkt")){
				//System.out.println("YEPPAAAA");
				 List<WebElement> we2=we.get(i).findElements(By.className("meta-info"));
				 for (int j = 0; j < we2.size(); j++) {
					 if(we2.get(j).getAttribute("innerHTML").contains("itemprop=\"operating")){
						 WebElement we3=we2.get(j);
						 osVersion=we3.getText().split("\n")[1];
						 //System.out.println(osVersion);
					 }
					 if(we2.get(j).getAttribute("innerHTML").contains("itemprop=\"date")){
						 WebElement we3=we2.get(j);
						
						 date=we3.getText().split("\n")[1];
						// System.out.println(date);
					 }
				}
			}
		}
		driver.quit();
        return new String[]{date,osVersion};
}
}