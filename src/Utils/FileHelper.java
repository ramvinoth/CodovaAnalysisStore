package Utils;

import java.io.File;


public class FileHelper {

	public static void removeFile(String path){
		File file= new File(path);
		deleteRecursive(file);
	}
	
	public static void deleteRecursive(File fileOrDirectory) {

		if (fileOrDirectory.isDirectory())
		for (File child : fileOrDirectory.listFiles())
		    deleteRecursive(child);

		fileOrDirectory.delete();

		}
	
	
}
