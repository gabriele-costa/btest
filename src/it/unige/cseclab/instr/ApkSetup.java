package it.unige.cseclab.instr;

import java.io.File;
import java.io.IOException;

public class ApkSetup {
	
	static final String WORKDIR = "out";
	static final String SIGNSCRIPT = "./AutoSignApk.sh";
	static final String SEP = "/"; 		

	public static void signAndInstall(String app) {

		ProcessBuilder pb = new ProcessBuilder(SIGNSCRIPT, SEP, app);
		pb.directory(new File(WORKDIR));
		pb.redirectErrorStream(true);
		Process P = null;
		try {
			P = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		
		try {
			P.waitFor();
		} catch (InterruptedException e2) {	
			System.out.println(e2);
		}
		
		pb = new ProcessBuilder("adb", "install", app + "-signed.apk");
		pb.directory(new File(WORKDIR));
		pb.redirectErrorStream(true);
		try {
			P = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
		} 
		
		try {
			P.waitFor();
		} catch (InterruptedException e2) {	}

		/*
		// Lists all files in folder
		File folder = new File(WORKDIR);
		File[] fList = folder.listFiles();
		// Searchs .lck
		for (File aFList : fList) {
			String pes = aFList.getName();
			if (pes.endsWith(".apk")) {
				// and deletes
				aFList.delete();
			}
		}
		*/
	
	}

}
