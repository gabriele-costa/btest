package it.unige.cseclab.dist;

public class DefaultDistance {
	
	public static Distance<String> s() {
		return new LevenshteinDistance();
	}
	
	public static Distance<Boolean> b() {
		return new Distance<Boolean> () {

			@Override
			public double d(Boolean a, Boolean b) {
				return (a.booleanValue() == b.booleanValue()) ? 0 : 1;
			}
			
		};
	}
	
	public static Distance<Integer> i() {
		return new Distance<Integer>() {

			@Override
			public double d(Integer a, Integer b) {
				return Math.abs(a - b);
			}};
	}
	
	public static Distance<Double> d() {
		return new Distance<Double>() {

			@Override
			public double d(Double a, Double b) {
				return Math.abs(a - b);
			}};
	}
	
	public static Distance<Object> o() {
		return new Distance<Object>() {

			@Override
			public double d(Object a, Object b) {
				if(a == null)
					return (b == null) ? 0 : 1;
				else
					return (a.equals(b)) ? 0 : 1;
			}};
	}

}
