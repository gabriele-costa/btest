package it.unige.cseclab.cg;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import soot.MethodSource;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;

public class CallGraphBuilder {
	
	public static final double STEP_INCREMENT = 1;
	public static final double TARGET_DISTANCE = 1;

	static String USER = "avalz";
	static String androidPlatformPath = "/home/" + USER + "/Android/Sdk/platforms/";

	public static CallGraph cg(String appPath) {
		
		SetupApplication app = new SetupApplication(androidPlatformPath, appPath);
		
		try {
			app.calculateSourcesSinksEntrypoints("./AndroidCallbacks.txt");
		} catch (IOException | XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        soot.G.reset();
        
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_process_dir(Collections.singletonList(appPath));
        Options.v().set_android_jars(androidPlatformPath);
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().setPhaseOption("cg.spark", "on");

        Scene.v().loadNecessaryClasses();

        SootMethod entryPoint = app.getEntryPointCreator().createDummyMain();
        Options.v().set_main_class(entryPoint.getSignature());
        Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
        
        System.out.println(entryPoint.getActiveBody());

        PackManager.v().runPacks();

        CallGraph appCallGraph = Scene.v().getCallGraph();
		
        return appCallGraph;
	}
	
	public static Map<String,Double> visit(CallGraph cg, Set<String> api) {
		
		Set<String> targets = new HashSet<>();
		Map<String,Double> distance = new HashMap<>();
		
		Iterator<Edge> ei = cg.iterator();
		
		Set<SootMethod> M = new HashSet<>();
		
		while(ei.hasNext()) {
			Edge e = ei.next();
			
			PatchingChain<Unit> units;
			
			if(! M.contains(e.getSrc().method())) {
				M.add(e.getSrc().method());
				if(e.getSrc().method().hasActiveBody()) {
					units = e.getSrc().method().getActiveBody().getUnits();
						
					for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
						final Unit u = iter.next();
						boolean found = containsUnit(api, u.toString());
						if(found) {
							targets.add(e.getSrc().method().getSignature());
							System.out.println("Added: " + e.getSrc().method().getSignature());
						}
						else {
							System.out.println("Not found in: " + e.getSrc().method().getSignature());
						}
					}
				}
				else {
					System.out.println("No active body: " + e.getSrc().method().getSignature());
				}
			}
			
			if(! M.contains(e.getTgt().method())) {
				M.add(e.getTgt().method());
				if(e.getTgt().method().hasActiveBody()) {
					units = e.getTgt().method().getActiveBody().getUnits();
					
					for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
						final Unit u = iter.next();
						boolean found = containsUnit(api, u.toString());
						if(found) {
							targets.add(e.getTgt().method().getSignature());
							System.out.println("Added: " + e.getTgt().method().getSignature());
						}
						else {
							System.out.println("Not found in: " + e.getTgt().method().getSignature());
						}
					}
				}
				else {
					System.out.println("No active body: " + e.getTgt().method().getSignature());
				}
			}
		}
			
		for(String s : targets) {
			distance.put(s, new Double(TARGET_DISTANCE));
		}
		
		computeDistances(cg, distance);
		
		return distance;
	}
	
	private static boolean containsUnit(Set<String> api, String u) {
		for(String s : api) {
			if(u.contains(s))
				return true;
		}
		return false;
	}

	/*
	 * Computes the distances from the target nodes
	 */
	private static Map<String, Double> computeDistances(CallGraph cg, Map<String, Double> distance) {
		
		boolean mod;
		
		do {
			mod = false;
			
			Iterator<Edge> ei = cg.iterator();
			
			while(ei.hasNext()) {
				Edge e = ei.next();
				
				boolean tgtKnown = distance.containsKey(e.getTgt().method().getSignature());
				boolean srcKnown = distance.containsKey(e.getSrc().method().getSignature());
				
				if(! srcKnown && tgtKnown) {
					mod = true;
					
					distance.put(e.getSrc().method().getSignature(), new Double(
									distance.get(e.getTgt().method().getSignature()).doubleValue() + STEP_INCREMENT));
				} else if(distance.containsKey("<dummyMainClass: void dummyMainMethod(java.lang.String[])>")) {
					if(! tgtKnown && e.getTgt().method().hasActiveBody()) {
						PatchingChain<Unit> units = e.getTgt().method().getActiveBody().getUnits();
						for(Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext();) {
							final Unit u = iter.next();
							
							// TODO check this control
							boolean found = isIntentLauncher(u);
							if(found) {
								mod = true;
								distance.put(e.getTgt().method().getSignature(), new Double(
										distance.get("<dummyMainClass: void dummyMainMethod(java.lang.String[])>").doubleValue()));
							}
						}
					}
				}
			}
		} while(mod);
		
		// DummyMain is not good for testing
		distance.put("<dummyMainClass: void dummyMainMethod(java.lang.String[])>", Double.MAX_VALUE);
		
		return distance;
		
	}

	private static boolean isIntentLauncher(Unit u) {
		
		if(u.toString().contains("startActivityForResult"))
			return true;
		if(u.toString().contains("startActivity"))
			return true;
		
		return false;
	}

	private static Set<SootMethod> findTargets(CallGraph cg, Set<String> apisign) {
		
		Set<SootMethod> M = new HashSet<>();
		
		Iterator<SootClass> ci = Scene.v().getClasses().iterator();
		while(ci.hasNext()) {
			SootClass sc = ci.next();
			
			for(SootMethod sm : sc.getMethods()) {
				MethodSource ms = sm.getSource();
			}
		}
		
		return M;
		
	}
	
	static class InvokeStmtVisitor extends AbstractStmtSwitch {
		
		boolean found;
		Set<String> apisign;
		
		InvokeStmtVisitor(Set<String> s) {
			found = false;
			apisign = s;
		}
		
		public void caseInvokeStmt(InvokeStmt stmt) {
			if(!found) {
				InvokeExpr invokeExpr = stmt.getInvokeExpr();
				if(apisign.contains(invokeExpr.getMethod().getSignature())) {
					found = true;
				}
			}
		}
		
		boolean found() {
			return found;
		}
	}
	
}
