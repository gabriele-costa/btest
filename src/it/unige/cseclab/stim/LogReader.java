package it.unige.cseclab.stim;

import it.unige.cseclab.log.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class LogReader extends Thread {

    Map<String, Double> Env;
    Process P;
    StringBuilder logger;

    public LogReader(Process process, Map<String, Double> environment) {
        // TODO: Add weakest-precondition formula (to be computed during Call Graph)
        Env = environment;
        P = process;
        logger = new StringBuilder();
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(P.getInputStream());

            BufferedReader br = new BufferedReader(isr);

            String line = null;
            while ((line = br.readLine()) != null) {
                logger.append(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public double getScore(TestChromosome T) {

        // TODO Test me!

        double best = Double.MAX_VALUE;

        String log = logger.toString();

        for (String api : Env.keySet()) {
            if (Env.get(api).doubleValue() < best)
                // If sotto quasi sicuramente sbagliato
                // TODO forse no
                if (log.contains(api)) {
                    best = Env.get(api).doubleValue();
                    Log.log("Found API: " + api + ", distance: " + best);
                }
        }

        // Long tests are worse than short tests (magic max length: 256 - unsigned byte)
        // TODO adjust for [0,1] values
        best += ((double) T.length()) / 256;

        // TODO: add parameter distance instead of penality

        // Check if the test calls the vulnerable API
        //      Check if it's called with the right parameters
        //      Normalize on [0,1]

        return best;
    }

    public void end() {
        P.destroy();
    }
}
