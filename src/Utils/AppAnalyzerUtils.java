package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

import AppAnalyzer.Configs;

public class AppAnalyzerUtils {
	DecompilerThread dt;
	AppAnalyzerThread at;
	
	public void startDecompileApps(){
		dt = new DecompilerThread();
		dt.start(); 
		
	}

	public void stopDecompileApps(){
		dt.keepGoing=false;
		
	}

	public void startAnalyzeApps() {
		at = new AppAnalyzerThread();
		at.start();
	}

	public void stopAnalyzeApps() {
		at.keepGoing=false;
	}
	
	
	
	//TODO: functies toevoegen voor general analyser.
	
	
}
