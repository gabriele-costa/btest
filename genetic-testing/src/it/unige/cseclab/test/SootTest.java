package it.unige.cseclab.test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import it.unige.cseclab.cg.CallGraphBuilder;
import soot.jimple.toolkits.callgraph.CallGraph;

public class SootTest {

	@Test
	public void generateCallGraph() {
		CallGraph cg = CallGraphBuilder.cg("./app.apk");
		
		Set<String> api = new HashSet<>();
		api.add("java.lang.String: byte[] getBytes()");
		
		Map<String,Double> dist = CallGraphBuilder.visit(cg, api);
		
		for(String s : dist.keySet()) {
			System.out.println(s + " := " + dist.get(s));
		}
		
		
	}

}
