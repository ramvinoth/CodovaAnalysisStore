package AppAnalyzer;

import PhoneGapUtils.PhoneGapAnalyzerUtils;
import Utils.DBUtils;

public class Test {

	public static void main(String[] args) {

//		DBUtils.dropTable(Configs.table_phonegap_analysis);
//		DBUtils.dropTable(Configs.table_plugins_phonegap);
//		DBUtils.createTablesIfNeeded();
//		PhoneGapAnalyzerUtils.populateDedicatedPhoneGapTable();
		
	//System.out.println(DBUtils.getAllPlugins());
	//System.out.println(DBUtils.getAllPlugins().size());
	System.out.println(DBUtils.getAmount(Configs.table_plugins_phonegap, ""));

	}

}
