package AppAnalyzer;
////BELANGIJK BIJ OPZETTEN OP NIEUW SYSTEEM=> DIRECTORIES AANMAKEN
public class Configs {

	//Database
	
	

	public static String dbName="testDB2.db";
	
	public static final String table_keywords="KEYWORDS";
	public static final String table_foundapps="FOUNDAPPS";
	public static final String table_permissions="PERMISSIONTABLE";
	public static final String table_phonegap_analysis = "PGANALYSISTABLE"; //tabel die bijhoudt welke taken al voor welke PhoneGap applicatie gedaan zijn.
	public static final String table_plugins_phonegap = "PGPLUGINTABLE";
	
	public static final String db_KeywordsOverhead="(NAME,CHECKED)";
	public static final String db_FoundAppsOverhead="(NAME,CREATOR,DISPLAYEDNAME,VERSION,DOWNLOADCOUNT,TYPE,CATEGORY,RATING,RATINGCOUNT,INSTALLSIZE,LASTUPDATE,OSVERSIONREQ,TIMEOFDOWNLOAD,STAGE,FAILED,CPT)";
	public static final String db_PermissionsOverhead="(NAME,PERMISSION)";
	public static final String db_PgPluginOverhead="(NAME,PLUGINNAME,PLUGINPACKAGENAME,VERSION)";
	public static final String db_PgAnalysisOverhead="(NAME,CONFIG,PLUGINS)";
	
	public static int stage_new=0;
	public static int stage_downloaded=1;
	public static int stage_decompiled=2;
	public static int stage_manifest=3;
	public static int stage_analysedForCpt=4;
	public static int stage_done=5;
	
	
	public static int failed_no=0;
	public static int failed_dl=1;
	public static int failed_decomp=2;
	public static int failed_manifestNotFound=3;
	public static int failed_manifestUseless=4;
	
	
	public static final String cpt_phonegap="PhoneGap";
	public static final String cpt_xamarin="Xamarin";
	public static final String cpt_adobeair="AdobeAir";
	public static final String cpt_titanium="Titanium";
	public static final String cpt_react="ReactNative";
	public static final String cpt_nativescript="NativeScript";
	public static final String cpt_unity="Unity";
	public static final String nativeorunknown = "NativeOrUnknown";

	
	
	
	
	//Android market api (Doesn't work anymore, Android-Market-API no longer supported)

	
	
	
	public static String ma_authToken = "<fill this>";
	public static String ma_androidID = "<fill this>";


	 
	
	
	//Saved Files 
	//PhoneGap

	public static String keyword_path="D:\\RA\\AppAnalysis\\AnalyseFiles\\keyword.txt";
	public static String pg_wwwwFiles="D:\\RA\\AppAnalysis\\AnalyseFiles\\PhoneGap\\wwwFiles";
	public static String pg_configFiles="D:\\RA\\AppAnalysis\\AnalyseFiles\\PhoneGap\\configFiles";
	public static String pg_AndroidManifestFiles="D:\\RA\\AppAnalysis\\AnalyseFiles\\PhoneGap\\manifestFiles";
	//All
	public static String allManifests="D:\\RA\\AppAnalysis\\AnalyseFiles\\AllManifests";
	
	
	//ApkTool
	public static String locationAPKtool = "D:\\RA\\APKtool_setup\\apktool.jar";
	
	
	
	//Temp Files
	
	//MAC
	public static String tempfilesLoc= "D:\\RA\\AppAnalysis\\apps\\apks";
	public static String tempfilesLocPrimary="D:\\RA\\AppAnalysis\\primarydownloadertemp";
	public static String tempfilesLocAlternate="D:\\RA\\AppAnalysis\\apps\\alternatedownloadertemp";
	public static String decompiledTempFilesLoc="D:\\RA\\AppAnalysis\\AppAnalysis\\apps\\decompfiles";


	
	
	//errorfile
	public static String errorFile="D:\\RA\\AppAnalysis\\errorfile.txt";
	
}
