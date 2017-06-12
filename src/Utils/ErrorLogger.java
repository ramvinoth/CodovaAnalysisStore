package Utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import AppAnalyzer.Configs;

public class ErrorLogger {

	
	public synchronized static void writeError(String error){
		
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(Configs.errorFile, true)));
			out.write(error);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			out.close();
		}
		
	}
	
}
