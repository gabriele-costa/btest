package it.unige.cseclab.stim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class LogReader extends Thread {
	
	Map<String,Double> Env;
	Process P;
	StringBuilder logger;
	
	public LogReader(Process p, Map<String, Double> e) {
		Env = e;
		P = p;
		logger = new StringBuilder();
	}

	public void run() {
		try {
            InputStreamReader isr = new InputStreamReader(P.getInputStream());

            BufferedReader br = new BufferedReader(isr);

            String line = null;
            while ((line = br.readLine()) != null)
            {
                logger.append(line); 
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	public double getScore(TestChromosome T) {
		
		// TODO Test me!
		
		double best = Double.MAX_VALUE;
		
		String log = logger.toString();
		
		for(String api : Env.keySet()) {
			if(Env.get(api).doubleValue() < best)
				// If sotto quasi sicuramente sbagliato
				if(log.contains(api))
					best = Env.get(api).doubleValue();
		}
		
		best += ((double)T.length()) / 256;
		
		return best;
	}

	public void end() {
		P.destroy();
	}
}
