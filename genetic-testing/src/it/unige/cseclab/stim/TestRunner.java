package it.unige.cseclab.stim;

import java.io.IOException;
import java.util.Map;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.monkeyrunner.recorder.actions.Action;
import com.lagodiuk.ga.Fitness;

import it.unige.cseclab.test.Demo.MyVector;

public class TestRunner implements Fitness<TestChromosome, Double> {
	
	static AdbBackend ab = null;
	String pkg;
	Map<String,Double> Env;
	
	public TestRunner(String pkg, Map<String, Double> env) {
		super();
		this.pkg = pkg;
		Env = env;
	}

	public static double run(String appkg, TestChromosome T, Map<String,Double> E) {
		
		if(ab == null)
			 ab = new AdbBackend();
		
		ProcessBuilder pb = new ProcessBuilder("adb", "shell", "monkey", "-p", appkg, "-c", "android.intent.category.LAUNCHER", "1");
		Process P;
		try {
			P = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
			return Double.MAX_VALUE;
		} 
		
		try {
			P.waitFor();
		} catch (InterruptedException e2) {	}
		
	    IChimpDevice device = ab.waitForConnection();
	    // adb", "logcat", "GACALL:V", "*:S
	    pb = new ProcessBuilder("adb","logcat","GACALL:V","*:S");
        // pb.redirectErrorStream(true);
	    
		try {
			P = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
			return Double.MAX_VALUE;
		} 
	    
	    LogReader reader = new LogReader(P, E);
	    reader.start();
	    
	    for(Action a : T.aVector()) {
	    	try {
				a.execute(device);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e);
				System.out.println("Action: " + a.serialize() +"\n");
			}
	    }
	    
	    reader.end();
	    reader.interrupt();
	    
	    device.dispose();
	    
	    // adb shell pm clear com.my.app.package
	    pb = new ProcessBuilder("adb", "shell", "pm", "clear", appkg);
		try {
			P = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
			return Double.MAX_VALUE;
		} 
		
		try {
			P.waitFor();
		} catch (InterruptedException e2) {	}
	    
	    return reader.getScore(T);
	}

	@Override
	public Double calculate(TestChromosome chromosome) {
		return run(pkg, chromosome, Env);
	}

}
