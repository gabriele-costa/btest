package it.unige.cseclab.test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import it.unige.cseclab.cg.CallGraphBuilder;
import it.unige.cseclab.instr.MethodCallInliner;
import soot.jimple.toolkits.callgraph.CallGraph;

public class GeneralTest {
	
	final static String APP = "./microsoft.apk";
	final static String API = "java.lang.String: byte[] getBytes()";
	
	@Test
	public static void general() {
		// Generate CG
		CallGraph cg = CallGraphBuilder.cg(APP);
		Set<String> api = new HashSet<>();
		api.add(API);
		
		Map<String,Double> dist = CallGraphBuilder.visit(cg, api);
		
		// Instrument
		MethodCallInliner.instrument(APP, api);
		
		// repeat GA with test
	}

}
