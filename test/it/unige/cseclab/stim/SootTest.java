package it.unige.cseclab.stim;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import it.unige.cseclab.cg.CallGraphBuilder;
import it.unige.cseclab.instr.MethodCallInliner;
import soot.jimple.toolkits.callgraph.CallGraph;

public class SootTest {

	//@Test
	public void generateCallGraph() {
		CallGraph cg = CallGraphBuilder.cg("./microsoft.apk");
		
		Set<String> api = new HashSet<>();
		api.add("java.lang.String: byte[] getBytes()");
		
		Map<String,Double> dist = CallGraphBuilder.visit(cg, api);
		
		for(String s : dist.keySet()) {
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
