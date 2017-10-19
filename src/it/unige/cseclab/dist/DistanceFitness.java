package it.unige.cseclab.dist;

import java.util.Map;

import com.lagodiuk.ga.Fitness;

import it.unige.cseclab.pred.Predicate;

public class DistanceFitness implements Fitness<EnvChromosome, Double> {
	
	Map<String,Object> ref;
	Predicate p;

	public DistanceFitness(Predicate p, Map<String, Object> ref) {
		super();
		this.ref = ref;
		this.p = p;
	}

	@Override
	public Double calculate(EnvChromosome c) {
		
		if(!p.isSolution(c.env)) {
			return Double.MAX_VALUE;
		}
		
		double sum = 0;
		
		for(String k : c.env.keySet()) {
			sum += Math.pow(distance(ref.get(k), c.env.get(k)), 2);
		}
		
		return Math.sqrt(sum);
	}

	private double distance(Object a, Object b) {
		if(a instanceof Boolean) {
			return DefaultDistance.b().d((Boolean)a, (Boolean)b);
		} else if(a instanceof Integer) {
			return DefaultDistance.i().d((Integer)a, (Integer)b);
		} else if(a instanceof Double) {
			return DefaultDistance.d().d((Double)a, (Double)b);
		} else if(a instanceof String) {
			return DefaultDistance.s().d((String)a, (String)b);
		} else {
			return DefaultDistance.o().d(a, b);
		}
	}
	

}
