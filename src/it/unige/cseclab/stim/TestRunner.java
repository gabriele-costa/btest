package it.unige.cseclab.stim;

import java.io.IOException;
import java.util.Map;

import com.android.chimpchat.adb.AdbBackend;
import com.android.chimpchat.core.IChimpDevice;
import com.android.monkeyrunner.recorder.actions.Action;
import com.lagodiuk.ga.Fitness;
import it.unige.cseclab.log.Log;
import it.unige.cseclab.pred.Predicate;

public class TestRunner implements Fitness<TestChromosome, Double> {
	
	static AdbBackend ab = null;
	static int counter = 0;
	String pkg;
	Map<String,Double> Env;
	Predicate p;
	
	public TestRunner(String pkg, Map<String, Double> env, Predicate predicate) {
		super();
		this.pkg = pkg;
		Env = env;
		p = predicate;

	}

	public static double run(String appkg, TestChromosome T, Map<String,Double> E, Predicate predicate) {

		Log.log("Running Test " + T.toString());
		if(ab == null)
			 ab = new AdbBackend();

		Process P = null;
        try {
            P = Runtime.getRuntime().exec("adb shell monkey -p " + appkg + " -c android.intent.category.LAUNCHER 1");
            P.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

	    IChimpDevice device = ab.waitForConnection();
	    // adb", "logcat", "GACALL:V", "*:S
        try {
            P = Runtime.getRuntime().exec("adb logcat GACALL:V \"*\":S");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // pb.redirectErrorStream(true);
	    
	    LogReader reader = new LogReader(P, E, predicate);
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
        try {
            P = Runtime.getRuntime().exec("adb shell pm clear " + appkg);
            P.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double score = reader.getScore(T);
        Log.log("Score: " + score);

	    return score;
	}

	@Override
	public Double calculate(TestChromosome chromosome) {
		System.out.println("Test number: " + counter++);
		System.out.println(chromosome);

		return run(pkg, chromosome, Env, p);
	}

}
