package Utils;

public class DlLimitException extends Exception {

	public DlLimitException(String string) {
		 super(string);
	        ErrorLogger.writeError("DLException: "+ string);
	}

}
