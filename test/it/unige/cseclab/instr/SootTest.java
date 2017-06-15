package it.unige.cseclab.instr;

import java.util.HashSet; 
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import it.unige.cseclab.cg.CallGraphBuilder;
import soot.jimple.toolkits.callgraph.CallGraph;

public class SootTest {

    public static final String TESTED_APK = "./apks/microsoft.apk";

    @Test
    public void generateCallGraph() {
        CallGraph cg = CallGraphBuilder.cg(TESTED_APK);

        Set<String> api = new HashSet<>();
        api.add("java.lang.String: byte[] getBytes()");

        Map<String, Double> dist = CallGraphBuilder.visit(cg, api);

        for (String s : dist.keySet()) {
            System.out.println(s + " := " + dist.get(s));
        }
    }

    @Test
    public void instrumentApp() {

        Set<String> api = new HashSet<>();
        api.add("java.lang.String: byte[] getBytes()");
        MethodCallInliner.instrument("./microsoft.apk", api);
    }

}
