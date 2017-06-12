package Utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import AppAnalyzer.Configs;

public class DBUtils {

	public synchronized static ArrayList<String> getAllPhoneGapApps() {

		ArrayList<String> list = new ArrayList<String>();
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT NAME FROM " + Configs.table_foundapps + " WHERE CPT IS 'PhoneGap';");

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				list.add(rs.getString(1));
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return list;

	}

	public synchronized static ArrayList<Map<String, ArrayList<String>>> getPluginMaps() {
		Map<String, ArrayList<String>> pluginAppMap = new HashMap<String, ArrayList<String>>();
		Map<String, ArrayList<String>> appPluginMap = new HashMap<String, ArrayList<String>>();

		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + Configs.table_plugins_phonegap + ";");

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				String app = rs.getString(1);
				String plugin = rs.getString(3);

				// plugin-app map

				if (pluginAppMap.containsKey(plugin)) {// plugin bestaat al
					pluginAppMap.get(plugin).add(app);
				} else {// plugin bestaat ng niet
					pluginAppMap.put(plugin, new ArrayList<String>());
					pluginAppMap.get(plugin).add(app);
				}

				// ---------------------------------------------
				// app-permission map
				if (appPluginMap.containsKey(app)) {// app bestaat al
					appPluginMap.get(app).add(plugin);
				} else {// app bestaat ng niet
					appPluginMap.put(app, new ArrayList<String>());
					appPluginMap.get(app).add(plugin);
				}

			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ArrayList<Map<String, ArrayList<String>>> ret = new ArrayList<Map<String, ArrayList<String>>>();
		ret.add(appPluginMap);
		ret.add(pluginAppMap);
		return ret;
	}

	public synchronized static String getNextNonXMLPGApp() {
		String ret = null;
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM " + Configs.table_phonegap_analysis + " WHERE CONFIG IS 0 LIMIT 1;");

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				ret = rs.getString(1);
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;

	}

	public synchronized static ArrayList<String> getUnusedPhoneGapApps() {

		ArrayList<String> list = new ArrayList<String>();
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT NAME FROM " + Configs.table_foundapps
					+ " WHERE CPT IS 'PhoneGap' AND NAME NOT IN (SELECT NAME FROM " + Configs.table_phonegap_analysis
					+ ");");

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				list.add(rs.getString(1));
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return list;
	}

	public synchronized static void addPhoneGapPlugin(String app, String pluginName, String pluginPackageName,
			String version) {
		addValue(Configs.table_plugins_phonegap,
				"'" + app + "','" + pluginName + "','" + pluginPackageName + "','" + version + "'");
	}

	public synchronized static void addPhoneGapAppToDedicatedTable(String app, String config, String plugins) {
		addValue(Configs.table_phonegap_analysis, "'" + app + "','" + config + "','" + plugins + "'");
	}

	public synchronized static void addPermission(String app, String Permission) {
		addValue(Configs.table_permissions, "'" + app + "','" + Permission + "'");
	}

	public synchronized static void setConfigXmlStatus(String app, String state) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_phonegap_analysis + " SET CONFIG = '" + state + "' WHERE name = '"
					+ app + "'";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized static void setPGPluginStatus(String app, String state) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_phonegap_analysis + " SET PLUGINS = '" + state + "' WHERE name = '"
					+ app + "'";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized static int getAmount(String tableName, String where) {
		int ret = 0;

		Connection c = null;
		Statement stmt = null;

		ArrayList<String> al = new ArrayList<String>();

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String statement = "";
			if (where.equals("")) {
				statement = "SELECT COUNT(*) FROM " + tableName + ";";
			} else {
				statement = "SELECT COUNT(*) FROM " + tableName + " WHERE " + where + ";";
			}
			ResultSet rs = stmt.executeQuery(statement);

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				ret = rs.getInt(1);
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

	public synchronized static String getAppFromStage(int stage) {
		String ret = null;
		Connection c = null;
		Statement stmt = null;

		ArrayList<String> al = new ArrayList<String>();

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + Configs.table_foundapps + " WHERE STAGE IS " + stage
					+ " AND FAILED IS 0 LIMIT 1;");

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				ret = rs.getString(1);
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

	public synchronized static void setCPT(String appName, String cpt) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);

			// tabellen maken als ze nog niet bestaan

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_foundapps + " SET CPT = '" + cpt + "' WHERE name = '" + appName
					+ "'";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public synchronized static void setStage(String appName, int stage) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);

			// tabellen maken als ze nog niet bestaan

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_foundapps + " SET STAGE = " + stage + " WHERE name = '" + appName
					+ "'";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized static void setFailedState(String appName, int failedStage) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);

			// tabellen maken als ze nog niet bestaan

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_foundapps + " SET FAILED = " + failedStage + " WHERE name = '"
					+ appName + "'";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// UNTESTED function
	public synchronized static void setDownloaded(String keyword) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);

			// tabellen maken als ze nog niet bestaan

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_foundapps + " SET STAGE = " + Configs.stage_downloaded
					+ " WHERE name = '" + keyword + "'";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// UNTESTED function
	public synchronized static void setNotDownloaded(String keyword) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_foundapps + " SET STAGE = " + Configs.stage_new + " WHERE name = '"
					+ keyword + "'";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public synchronized static void fixFoundApps() {
		Connection c = null;
		Statement stmt = null;
		ResultSet rs;
		ArrayList<String> to1 = new ArrayList<String>();
		ArrayList<String> to0 = new ArrayList<String>();
		// ArrayList<String> al = new ArrayList<String>();

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			rs = stmt.executeQuery("SELECT * FROM " + Configs.table_foundapps);

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			boolean stage1 = false;
			while (rs.next()) {
				int failedstate = rs.getInt(15);
				if (rs.getString(1).equals("com.WomenPajamas.troxoapps")) {
					stage1 = true;
				}
				if (failedstate == 11 && !stage1) {
					to1.add(rs.getString(1));

				}
				// if(failedstate==11&&stage1){
				// to0.add(rs.getString(1));

				// }

			}

			rs.close();
			/*
			
			*/
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
				for (int i = 0; i < to1.size(); i++) {
					DBUtils.setFailedState(to1.get(i), 1);
				}
				for (int i = 0; i < to0.size(); i++) {
					DBUtils.setFailedState(to0.get(i), 0);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public synchronized static ArrayList<String> getDownloadedNotAnalyzedApps() {
		Connection c = null;
		Statement stmt = null;

		ArrayList<String> al = new ArrayList<String>();

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + Configs.table_foundapps + " WHERE STAGE IS "
					+ Configs.stage_downloaded + " AND FAILED IS 0;");

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				al.add(rs.getString(1));
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return al;

	}

	public synchronized static ArrayList<String> getNonDownloadedApps() {
		Connection c = null;
		Statement stmt = null;

		ArrayList<String> al = new ArrayList<String>();

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM " + Configs.table_foundapps + " WHERE STAGE IS 0 AND FAILED IS 0;");

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				al.add(rs.getString(1));
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return al;

	}

	public synchronized static String getUnusedKeyword() {
		String ret = null;
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM " + Configs.table_keywords + " WHERE CHECKED IS 0 LIMIT 1;");

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				ret = rs.getString(1);
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;

	}

	// UNTESTED CODE. TODO
	public synchronized static void setDownloadFailed(String keyword) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + AppAnalyzer.Configs.dbName);

			// tabellen maken als ze nog niet bestaan

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_foundapps + " SET FAILED = 1 WHERE name = '" + keyword
					+ "' AND FAILED = 0";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public synchronized static void setKeywordUsed(String keyword) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + AppAnalyzer.Configs.dbName);

			// tabellen maken als ze nog niet bestaan

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_keywords + " SET CHECKED = 1 WHERE name = '" + keyword
					+ "' AND CHECKED = 0";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// //deze functie later uitcommenten!!!!!!
	public synchronized static void dropTable(String tableName) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + AppAnalyzer.Configs.dbName);

			// tabellen maken als ze nog niet bestaan

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "DROP TABLE IF EXISTS " + tableName;
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Table deleted successfully");

	}

	public synchronized static void createTablesIfNeeded() {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + AppAnalyzer.Configs.dbName);

			// tabellen maken als ze nog niet bestaan

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS KEYWORDS " + "(NAME           TEXT  UNIQUE   NOT NULL, "
					+ " CHECKED        INT     NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();

			// table voor apps
			stmt = c.createStatement();
			sql = "CREATE TABLE IF NOT EXISTS FOUNDAPPS " + "(NAME           TEXT  UNIQUE   NOT NULL, "
					+ " CREATOR        TEXT			  NOT NULL, " + " DISPLAYEDNAME  TEXT			  NOT NULL, "
					+ " VERSION        INT			  NOT NULL, " + " DOWNLOADCOUNT  TEXT			  NOT NULL, "
					+ " TYPE	       TEXT 		  NOT NULL, " + " CATEGORY       TEXT 		  NOT NULL, "
					+ " RATING	       DOUBLE 		  NOT NULL, " + " RATINGCOUNT    INT	 		  NOT NULL, "
					+ " INSTALLSIZE    INT	 		  NOT NULL, " + " LASTUPDATE	   TEXT	 		  NOT NULL, "
					+ " OSVERSIONREQ   TEXT 		  NOT NULL, " + " TIMEOFDOWNLOAD LONG 		  NOT NULL, "
					+ " STAGE 			INT			  NOT NULL, " + " FAILED     		INT     	  NOT NULL, "
					+ " CPT				TEXT		  NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();

			// table voor PhoneGapAnalyse
			stmt = c.createStatement();
			sql = "CREATE TABLE IF NOT EXISTS " + Configs.table_phonegap_analysis + " "
					+ "(NAME        TEXT UNIQUE 		  NOT NULL, " + " CONFIG      TEXT		  NOT NULL, "
					+ " PLUGINS		TEXT		  NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();

			// table voor PhoneGap Plugins
			stmt = c.createStatement();
			sql = "CREATE TABLE IF NOT EXISTS " + Configs.table_plugins_phonegap + " "
					+ "(NAME        		TEXT   	NOT NULL, " + " PLUGINNAME      	TEXT		  	NOT NULL, "
					+ " PLUGINPACKAGENAME 	TEXT			NOT NULL, "
					+ " VERSION				TEXT			NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();

			// table permissions
			stmt = c.createStatement();
			sql = "CREATE TABLE IF NOT EXISTS PERMISSIONTABLE " + "(NAME           TEXT			  NOT NULL, "
					+ " PERMISSION				TEXT		  NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Table created successfully");

	}

	public synchronized static void addValue(String tableName, String inputString) {

		Connection c = null;
		Statement stmt = null;
		String overhead = null;

		switch (tableName) {
		case "KEYWORDS": {
			overhead = AppAnalyzer.Configs.db_KeywordsOverhead;
			break;
		}
		case "FOUNDAPPS": {
			overhead = AppAnalyzer.Configs.db_FoundAppsOverhead;
			break;
		}
		case "PERMISSIONTABLE": {
			overhead = AppAnalyzer.Configs.db_PermissionsOverhead;
			break;
		}
		case Configs.table_phonegap_analysis: {
			overhead = AppAnalyzer.Configs.db_PgAnalysisOverhead;
			break;
		}
		case Configs.table_plugins_phonegap: {
			overhead = AppAnalyzer.Configs.db_PgPluginOverhead;
			break;
		}

		default:
			break;
		}

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + AppAnalyzer.Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String sql = "INSERT OR IGNORE INTO " + tableName + " " + overhead + " " + "VALUES (" + inputString + " );";
			System.out.println(sql);
			stmt.executeUpdate(sql);

			c.commit();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Value added successfully");

	}

	// untested, but should work
	public synchronized static void fillKeywordsFromFile(String path) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));

			String inp;
			while ((inp = br.readLine()) != null) {
				DBUtils.addValue(Configs.table_keywords, "'" + inp + "',0");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}

	public synchronized static void printTable(String tableName) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + AppAnalyzer.Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + ";");

			ResultSetMetaData rsmd = rs.getMetaData();

			int numberOfColumns = rsmd.getColumnCount();

			for (int i = 1; i <= numberOfColumns; i++) {
				if (i > 1)
					System.out.print(",  ");
				String columnName = rsmd.getColumnName(i);
				System.out.print(columnName);
			}
			System.out.println("");

			while (rs.next()) {
				for (int i = 1; i <= numberOfColumns; i++) {
					if (i > 1)
						System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(columnValue);
				}
				System.out.println("");
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Table printed successfully");

	}

	public synchronized static void writeQueryResultToCsv(String query, String out) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(out));

			Connection c = null;
			Statement stmt = null;

			try {
				// starten
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:" + AppAnalyzer.Configs.dbName);
				c.setAutoCommit(false);

				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery(query);

				ResultSetMetaData rsmd = rs.getMetaData();

				int numberOfColumns = rsmd.getColumnCount();

				for (int i = 1; i <= numberOfColumns; i++) {
					if (i > 1) {
						System.out.print(",  ");
						writer.write(";");
					}
					String columnName = rsmd.getColumnName(i);
					writer.write(columnName);
					System.out.print(columnName);
				}
				System.out.println("");
				writer.write("\n");

				while (rs.next()) {
					for (int i = 1; i <= numberOfColumns; i++) {
						if (i > 1) {
							System.out.print(",  ");
							writer.write(";");
						}

						String columnValue = rs.getString(i);
						System.out.print(columnValue);
						writer.write(columnValue);
					}
					System.out.println("");
					writer.write("\n");
				}

				rs.close();

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			} finally {
				try {
					stmt.close();
					c.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Table written to CSV file.");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	////////////////////////////////////
	public synchronized static void writeTableToCsv(String tableName, String out) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(out));

			Connection c = null;
			Statement stmt = null;

			try {
				// starten
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection("jdbc:sqlite:" + AppAnalyzer.Configs.dbName);
				c.setAutoCommit(false);

				stmt = c.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + ";");

				ResultSetMetaData rsmd = rs.getMetaData();

				int numberOfColumns = rsmd.getColumnCount();

				for (int i = 1; i <= numberOfColumns; i++) {
					if (i > 1) {
						System.out.print(",  ");
						writer.write(";");
					}
					String columnName = rsmd.getColumnName(i);
					writer.write(columnName);
					System.out.print(columnName);
				}
				System.out.println("");
				writer.write("\n");

				while (rs.next()) {
					for (int i = 1; i <= numberOfColumns; i++) {
						if (i > 1) {
							System.out.print(",  ");
							writer.write(";");
						}

						String columnValue = rs.getString(i);
						System.out.print(columnValue);
						writer.write(columnValue);
					}
					System.out.println("");
					writer.write("\n");
				}

				rs.close();

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.exit(0);
			} finally {
				try {
					stmt.close();
					c.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Table written to CSV file.");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public synchronized static String sanitize(String inp) {
		inp = inp.replace("'", "");
		inp = inp.replace("\\", "");
		inp = inp.replace(",", ".");

		return inp;
	}

	public synchronized static void setDownloadTime(String appName, long currentTimeMillis) {
		Connection c = null;
		Statement stmt = null;

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);

			// tabellen maken als ze nog niet bestaan

			// table voor zoekwoorden
			stmt = c.createStatement();
			String sql = "UPDATE " + Configs.table_foundapps + " SET TIMEOFDOWNLOAD = " + currentTimeMillis
					+ " WHERE name = '" + appName + "'";
			System.out.println(sql);
			stmt.executeUpdate(sql);
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public synchronized static String getAppFromFailedState(int failedState) {
		String ret = null;
		Connection c = null;
		Statement stmt = null;

		ArrayList<String> al = new ArrayList<String>();

		try {
			// starten
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + Configs.dbName);
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(
					"SELECT * FROM " + Configs.table_foundapps + " WHERE FAILED IS " + failedState + " LIMIT 1;");

			ResultSetMetaData rsmd = rs.getMetaData();

			// int numberOfColumns = rsmd.getColumnCount();

			while (rs.next()) {
				ret = rs.getString(1);
			}

			rs.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				stmt.close();
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

}
