package Utils;

import AppAnalyzer.Configs;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.ResponseContext;

public class AndroidMarketThread extends Thread {

	String keyword;
	MarketSession session;
	static boolean done;
	static boolean keepGoing;

	public AndroidMarketThread(MarketSession s) {
		session = s;
		done = false;
		keepGoing = true;
	}

	public void run() {
		keyword = getNextKeyword();
		boolean gelukt = true;
		int index = 0;

		while (keepGoing && keyword != null) {
			done = false;
			index=0;
			// alle resultaten voor keyword afgaan en in database steken
			while (!done) {
				gelukt=true;
				try {
					AppsRequest appsRequest = AppsRequest.newBuilder().setQuery(keyword).setStartIndex(index)
							.setEntriesCount(10).setWithExtendedInfo(true).build();

					session.append(appsRequest, new Callback<AppsResponse>() {
						@Override
						public void onResult(ResponseContext context, AppsResponse response) {
							System.out.println("apps found: " + response.getAppCount());
							for (int i = 0; i < response.getAppCount(); i++) {

								String packageName = response.getApp(i).getPackageName();
								String creator = response.getApp(i).getCreator();
								String displayedName = response.getApp(i).getTitle();
								int version = response.getApp(i).getVersionCode();
								String dlCount = response.getApp(i).getExtendedInfo().getDownloadsCountText()
										.toString();
								String type = response.getApp(i).getAppType().toString();
								String category = response.getApp(i).getExtendedInfo().getCategory();
								double rating = Double.parseDouble(response.getApp(i).getRating());
								int ratingCount = response.getApp(i).getRatingsCount();
								int installSize = response.getApp(i).getExtendedInfo().getInstallSize();
								AndroidPlayStoreHelper apsh = new AndroidPlayStoreHelper();
								String[] updateAndOsVersion = apsh.getLastUpdatedAndOsVersion(packageName);
								String lastUpdate = updateAndOsVersion[0];
								String osVersionNeeded = updateAndOsVersion[1];

								System.err.println(displayedName);
								System.out.println(packageName + " " + creator + " " + DBUtils.sanitize(displayedName)
										+ " " + version + " " + dlCount + " " + type + " " + category + " " + rating
										+ " " + ratingCount + " " + installSize + " " + lastUpdate + " "
										+ osVersionNeeded);
								DBUtils.addValue(Configs.table_foundapps,
										"'" + packageName + "','" + DBUtils.sanitize(creator) + "','"
												+ DBUtils.sanitize(displayedName) + "'," + version + ",'" + dlCount
												+ "','" + type + "','" + category + "'," + rating + "," + ratingCount
												+ "," + installSize + ",'" + lastUpdate + "','" + osVersionNeeded
												+ "',0," + 0 + "," + 0 + "," + "0");

								// permissions

								List<String> permissions = response.getApp(i).getExtendedInfo().getPermissionIdList();

								for (int j = 0; j < permissions.size(); j++) {

									DBUtils.addPermission(packageName, permissions.get(j));

								}

							}
							if (response.getAppCount() != 10) {
								DBUtils.setKeywordUsed(keyword);
								done = true;
							} else {
								try {
									Thread.sleep(1500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});

					session.flush();
				} catch (RuntimeException e) {
					e.printStackTrace();
					System.out.println(
							"bovenstaande runtime exception werd opgevangen... 1min slapen en opnieuw proberen");
					try {
						Thread.sleep(1000 * 60);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					gelukt = false;
				}
				if (gelukt) {
					index = index + 10;
				}
			}
			keyword = getNextKeyword();

		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				GUI.Main.getMainController().updateCrawlerStopped();
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
