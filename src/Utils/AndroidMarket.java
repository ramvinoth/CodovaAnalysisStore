package Utils;

import AppAnalyzer.Configs;

import com.gc.android.market.api.MarketSession;
import com.gc.android.market.api.MarketSession.Callback;
import com.gc.android.market.api.model.Market.AppsRequest;
import com.gc.android.market.api.model.Market.AppsResponse;
import com.gc.android.market.api.model.Market.ResponseContext;

public class AndroidMarket {

	MarketSession session;
	AndroidMarketThread amt;
	
	public AndroidMarket() {

		session = new MarketSession(false);
		session.setAuthSubToken(Configs.ma_authToken);
		session.setAndroidId(Configs.ma_androidID);
		session.setIsSecure(false);
		

	}

	public void startGetAppNames() {
		amt = new AndroidMarketThread(session);
		amt.start();
	}
	
	public void stopGetAppNames() {
		amt.keepGoing=false;		
	}
	
	
	
}
