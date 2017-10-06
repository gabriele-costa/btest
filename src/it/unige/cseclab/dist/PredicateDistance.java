package it.unige.cseclab.dist;

import java.util.Map;
import java.util.Vector;

import com.lagodiuk.ga.GeneticAlgorithm;
import com.lagodiuk.ga.IterartionListener;
import com.lagodiuk.ga.Population;

import it.unige.cseclab.pred.Predicate;

public class PredicateDistance {
	
	Predicate p;
	
	public PredicateDistance(Predicate p) {
		super();
		this.p = p;
	}

	private static final int POP_SIZE = 4;
	private static final int MAX_ITER = 20;

	public double d(Map<String, Object> v) {
		
		Population<EnvChromosome> pop = createInitialPopulation(POP_SIZE);
		
		DistanceFitness fitness = new DistanceFitness(p, v);

		GeneticAlgorithm<EnvChromosome, Double> ga = new GeneticAlgorithm<>(pop, fitness);

		addListener(ga);

		ga.evolve(MAX_ITER);
		
		return fitness.calculate(ga.getBest());
	}

	private Population<EnvChromosome> createInitialPopulation(int s) {
		Population<EnvChromosome> pop = new Population<>();
		
		Predicate pp = new Predicate(p);
		
		for(int i = 0; i < s; i++) {
			Map<String,Object> sol = pp.solve();
			
			if(sol == null)
				break;
			
			pop.addChromosome(new EnvChromosome(sol));
			pp = pp.exclude(sol);
		}
		
		return pop;
	}
	
	private static void addListener(GeneticAlgorithm<EnvChromosome, Double> ga) {
		// just for pretty print
		System.out.println(String.format("%s\t%s\t%s", "iter", "fit", "chromosome"));

		// Lets add listener, which prints best chromosome after each iteration
		ga.addIterationListener(new IterartionListener<EnvChromosome, Double>() {

			private final double threshold = 1e-5;

			@Override
			public void update(GeneticAlgorithm<EnvChromosome, Double> ga) {

				EnvChromosome best = ga.getBest();
				double bestFit = ga.fitness(best);
				int iteration = ga.getIteration();

				// Listener prints best achieved solution
				System.out.println(String.format("%s\t%s\t%s", iteration, bestFit, best));

				// If fitness is satisfying - we can stop Genetic algorithm
				if (bestFit < this.threshold) {
					ga.terminate();
				}
			}
		});
	}

}
