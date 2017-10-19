package it.unige.cseclab.stim;

import java.util.Vector;

import it.unige.cseclab.pred.Predicate;
import org.junit.Test;

public class TestTest {

	@Test
	public void randomWait() {
		
		System.out.println("\n** RANDOM WAIT **\n");
		
		SerialAction wait = TestChromosome.randomWait();
		
		Vector<SerialAction> v = new Vector<>();
		
		v.add(wait);
		
		TestChromosome t = new TestChromosome(v);
		
		TestRunner.run("", t, null, null);

	}
	
	@Test
	public void randomDrag() {
		
		System.out.println("\n** RANDOM DRAG **\n");
		
		SerialAction drag = TestChromosome.randomDrag();
		
		Vector<SerialAction> v = new Vector<>();
		
		v.add(drag);
		
		TestChromosome t = new TestChromosome(v);
		
		TestRunner.run("", t, null, null);


	}
	
	@Test
	public void randomTouch() {
		
		System.out.println("\n** RANDOM TOUCH **\n");
		
		SerialAction touch = TestChromosome.randomTouch();
		
		Vector<SerialAction> v = new Vector<>();
		
		v.add(touch);
		
		TestChromosome t = new TestChromosome(v);
		
		TestRunner.run("", t, null, null);

	}

	@Test
	public void randomType() {
		
		System.out.println("\n** RANDOM TYPE **\n");
		
		SerialAction type = TestChromosome.randomType();
		
		Vector<SerialAction> v = new Vector<>();
		
		v.add(type);
		
		TestChromosome t = new TestChromosome(v);
		
		TestRunner.run("", t, null, null);

	}
	
	@Test
	public void randomChromosome() {
		
		
		TestChromosome t = TestChromosome.random();
				
		System.out.println("\n** RANDOM CHROMOSOME (SIZE "+t.length()+") **\n");
		try {	
			TestRunner.run("", t, null, new Predicate(true));
		}
		catch(Exception e) {
			System.out.println(e);
			System.out.println(t.toString());
			throw e;
		}

	}

}
