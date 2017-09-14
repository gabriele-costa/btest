package it.unige.cseclab.dist;

import java.util.Map;

import com.lagodiuk.ga.GeneticAlgorithm;
import com.lagodiuk.ga.Population;

import it.unige.cseclab.pred.Predicate;

public class PredicateDistance {
	
	Predicate p;
	
	public PredicateDistance(Predicate p) {
		super();
		this.p = p;
	}

	private static final int POP_SIZE = 8;

	public double d(Map<String, Object> env) {
		
		Population<EnvChromosome> pop = createInitialPopulation(env, POP_SIZE);
		
		// TestRunner runner = new TestRunner(pkg, dist);

		// GeneticAlgorithm<TestChromosome, Double> ga = new GeneticAlgorithm<TestChromosome, Double>(population, runner);

		// addListener(ga);

		// ga.evolve(MAX_ITER);
		
		return 0;
	}

	private Population<EnvChromosome> createInitialPopulation(Map<String, Object> env, int s) {
		Population<EnvChromosome> pop = new Population<>();
		
		
		return null;
		// for(int i = 0; i < s; i++)
		//	pop.addChromosome(EnvChromosome.randomChromosome(env));
	}

}
