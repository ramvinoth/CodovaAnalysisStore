package Utils;

public class DownloadException extends Exception {
	public DownloadException(String message) {
		
        super(message);
        ErrorLogger.writeError("DLException: "+ message);
    }
}
