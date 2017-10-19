package it.unige.cseclab.stim;

import it.unige.cseclab.dist.PredicateDistance;
import it.unige.cseclab.log.Log;
import it.unige.cseclab.pred.Predicate;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogReader extends Thread {

    Map<String, Double> Env;
    Process P;
    StringBuilder logger;
    PredicateDistance distance;

    public LogReader(Process process, Map<String, Double> environment, Predicate predicate) {
        // TODO: Add weakest-precondition formula (to be computed during Call Graph)
        Env = environment;
        P = process;
        logger = new StringBuilder();
        this.distance = new PredicateDistance(predicate);
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

        String bestApi = null;
        List<String> bestParams = null;



        for (String api : Env.keySet()) {
            if (Env.get(api).doubleValue() < best)
                // If sotto quasi sicuramente sbagliato
                // TODO forse no
                if (log.contains(api)) {
                    String regexApi = api
                            .replaceAll("\\)", "\\\\)")
                            .replaceAll("\\(", "\\\\(")
                            .replaceAll("\\[", "\\\\[")
                            ;
                    Pattern p = Pattern.compile(regexApi + "(((?!GACALL).)*)<>");
                    Matcher m = p.matcher(log);
                    m.find();

                    List<String> params = new ArrayList<>();
                    long toSkip = 1;
                    for (String s : m.group(1).split(":")) {
                        if (toSkip > 0) {
                            toSkip--;
                            continue;
                        }
                        if (s == null || s.isEmpty()) {
                            s = "";
                        }
                        params.add(s);
                    }

                    best = Env.get(api).doubleValue();

                    bestApi = api;
                    bestParams = params;


                    try {
                        Log.log("Found API: " + api + ", " + params + ", distance: " + best);
                    } catch (Exception e) {
                        System.out.println("Can't match " + regexApi);
                        System.out.println(log);
                    }
                }
        }


        // Long tests are worse than short tests (magic max length: 256 - unsigned byte)
        // DONE adjust for [0,1] values
        // best += ((double) T.length()) / 256;

        // TODO: add parameter distance instead of penality
        if (best == 1) {
            Pattern p = Pattern.compile("\\((.*)\\)");
            Matcher m = p.matcher(bestApi);
            m.find();
            List<String> paramList = Arrays.stream(m.group(1).split(",")).collect(Collectors.toList());
            paramList.add(0, "java.lang.Object");

            Map<String, Object> params = new HashMap<>();
            for (int i = 0; i < bestParams.size(); i++) {
                // TODO: Add param type
                String type = paramList.get(i);
                String value = bestParams.get(i);
                if (type.equals("java.lang.String")) {
                    params.put("p"+i, value);
                } else {
                    if (value == null || value.isEmpty()) value = "0";
                    value = value.replaceAll("'", "");
                    params.put("p"+i, Integer.valueOf(value));
                }
            }
            // TODO: leggere i parametri della chiamata distanza 1
            // Usarli per fare la distance in predicate-distance
            best = 1 / (Double.MAX_VALUE - distance.d(params));
        }

        // Check if the test calls the vulnerable API
        //      Check if it's called with the right parameters
        //      Normalize on [0,1]

        return best;
    }

    public void end() {
        P.destroy();
    }
}
