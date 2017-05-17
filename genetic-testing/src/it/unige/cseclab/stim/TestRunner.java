package it.unige.cseclab.stim;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.monkeyrunner.recorder.actions.Action;

public class TestRunner {
	
	static AdbBackend ab = null;
	
	public static double run(TestChromosome T) {
		
		if(ab == null)
			 ab = new AdbBackend();
		
			
	    IChimpDevice device = ab.waitForConnection();
	    
	    double fitness = 0;
	    
	    for(Action a : T.aVector()) {
	    	try {
				a.execute(device);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Action: " + a.serialize() +"\n");
			}
	    }
	    
	    device.dispose();
	    
	    return fitness;
	}

}
