package it.unige.cseclab;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.monkeyrunner.MonkeyDevice;
import com.android.monkeyrunner.recorder.actions.Action;

public class TestRunner {
	
	public static double run(AndroidTest T) {
		AdbBackend ab = new AdbBackend();
	    IChimpDevice device = ab.waitForConnection();
	    
	    double fitness = 0;
	    
	    for(Action a : T.getSteps()) {
	    	try {
				a.execute(device);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    device.dispose();
	    
	    return fitness;
	}

}
