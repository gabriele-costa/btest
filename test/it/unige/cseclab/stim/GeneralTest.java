package it.unige.cseclab.stim;

import com.lagodiuk.ga.GeneticAlgorithm;
import com.lagodiuk.ga.IterartionListener;
import com.lagodiuk.ga.Population;
import info.leadinglight.jdot.Graph;
import info.leadinglight.jdot.Node;
import it.unige.cseclab.cg.CallGraphBuilder;
import it.unige.cseclab.instr.ApkSetup;
import it.unige.cseclab.instr.MethodCallInliner;
import it.unige.cseclab.log.Log;
import it.unige.cseclab.pred.Predicate;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class GeneralTest {
	
	final static String APP = "case-study";
	final static String API = "android.database.sqlite.SQLiteDatabase: android.database.Cursor rawQuery(java.lang.String,java.lang.String[])";
	final static String DOT = "graph.dot";
	final static Predicate predicate = TestingConditions.getRawQuery();
	
	final static int POP_SIZE = 2;
	private static final int MAX_ITER = 4;
	
	@Test
	public void general() {
		
		String APK = "/home/avalz/browser/" + APP + ".apk";
		
		// Generate CG
		CallGraph cg = CallGraphBuilder.cg(APK);

		Log.log("Call Graph size: " + cg.size());
		
		Set<String> api = new HashSet<>();
		api.add(API);
		
		makeDot(cg, DOT);

        Map<String,Double> dist = CallGraphBuilder.visit(cg, api);
		
		if(dist.size() == 0) {
			System.out.println("API not found");
			return;
		}

		String table = "\n";
		for(String k : dist.keySet()) {
			table += k + " := " + dist.get(k) + "\n";
		}
		
		Log.log("Distance vector size: " + dist.size() + table);
		
		
		// Instrument
		MethodCallInliner.instrument(APK, api);
		
		ProcessManifest processMan = null;
		try {
			processMan = new ProcessManifest(APK);
		} catch (IOException | XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assert false;
		}
		
		if(processMan == null) { 
			throw new UnsupportedOperationException("Manifest is null");
		}
		
		String pkg = processMan.getPackageName();
		
		// install apk
		
		ApkSetup.signAndInstall(APP);
		
		// repeat GA with test
		
		Population<TestChromosome> population = createInitialPopulation(POP_SIZE);
		
		TestRunner runner = new TestRunner(pkg, dist, predicate);

		GeneticAlgorithm<TestChromosome, Double> ga = new GeneticAlgorithm<TestChromosome, Double>(population, runner);

		addListener(ga);

		ga.evolve(MAX_ITER);
		
		Log.log(String.format("\nWinner: %s", ga.getBest().printActions("\n")));
		
	}

	private void makeDot(CallGraph cg, String filename) {
		Graph g = new Graph("CG");
		
		Iterator<Edge> it = cg.iterator();
		while(it.hasNext()) {
			Edge e = it.next();
			g.addNode(new Node(e.getSrc().method().getSignature()));
			g.addNode(new Node(e.getTgt().method().getSignature()));
			g.addEdge(new info.leadinglight.jdot.Edge().addNode(e.getSrc().method().getSignature()).addNode(e.getTgt().method().getSignature()));
		}
	
		File f = new File(filename);
		try {
			FileOutputStream fos = new FileOutputStream(f);
			
			fos.write(g.toDot().getBytes());
			
			fos.flush();
			
			fos.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static Population<TestChromosome> createInitialPopulation(int size) {
		Population<TestChromosome> population = new Population<>();
		for (int i = 0; i < size; i++) {
			TestChromosome c = TestChromosome.random();
			population.addChromosome(c);
		}
		return population;
	}
	
	/**
	 * After each iteration Genetic algorithm notifies listener
	 */
	private static void addListener(GeneticAlgorithm<TestChromosome, Double> ga) {
		// just for pretty print
		Log.log(String.format("%s\t%s\t%s", "iter", "fit", "chromosome"));

		// Let's add listener, which prints best chromosome after each iteration
		ga.addIterationListener(new IterartionListener<TestChromosome, Double>() {

			private final double threshold = 1;

			@Override
			public void update(GeneticAlgorithm<TestChromosome, Double> ga) {

				TestChromosome best = ga.getBest();
				double bestFit = ga.fitness(best);
				int iteration = ga.getIteration();

				// Listener prints best achieved solution
				Log.log(String.format("\n"
						+ "Population size: %s\n"
						+ "Iteration: %s\n"
						+ "Best Fitness: %s\n"
						+ "Test Actions: %s\n", ga.getPopulation().getSize(), iteration, bestFit, best.printActions(" >> ")));

				// If fitness is satisfying - we can stop Genetic algorithm
				if(bestFit < this.threshold) {
					ga.terminate();
					Log.log("Completed for threshold in " + iteration);
				}
				
				if(iteration >= MAX_ITER) {
					ga.terminate();
					Log.log("Completed for iterations");
				}
			}
		});
	}

}
