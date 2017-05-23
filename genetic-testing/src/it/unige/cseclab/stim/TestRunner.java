package it.unige.cseclab.stim;

import java.io.IOException;
import java.util.Map;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.monkeyrunner.recorder.actions.Action;

public class TestRunner {
	
	static AdbBackend ab = null;
	
	public static double run(TestChromosome T, Map<String,Double> E) {
		
		if(ab == null)
			 ab = new AdbBackend();
		
	    IChimpDevice device = ab.waitForConnection();
	    // adb", "logcat", "GACALL:V", "*:S
	    ProcessBuilder pb = new ProcessBuilder("adb","logcat","GACALL:V","*:S");
        pb.redirectErrorStream(true);
        Process P;
		try {
			P = pb.start();
		} catch (IOException e1) {
			e1.printStackTrace();
			return 0;
		} 
	    
	    LogReader reader = new LogReader(P, E);
	    reader.start();
	    
	    for(Action a : T.aVector()) {
	    	try {
				a.execute(device);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Action: " + a.serialize() +"\n");
			}
	    }
	    
	    reader.end();
	    reader.interrupt();
	    
	    device.dispose();
	    
	    return reader.getScore();
	}

}
