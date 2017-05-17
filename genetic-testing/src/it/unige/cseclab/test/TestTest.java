package it.unige.cseclab.test;

import java.util.Vector;

import org.junit.Test;

import it.unige.cseclab.stim.SerialAction;
import it.unige.cseclab.stim.TestChromosome;
import it.unige.cseclab.stim.TestRunner;

public class TestTest {
	
	//@Test
	public void randomWait() {
		
		System.out.println("\n** RANDOM WAIT **\n");
		
		SerialAction wait = TestChromosome.randomWait();
		
		Vector<SerialAction> v = new Vector<>();
		
		v.add(wait);
		
		TestChromosome t = new TestChromosome(v);
		
		TestRunner.run(t);

	}
	
	//@Test
	public void randomDrag() {
		
		System.out.println("\n** RANDOM DRAG **\n");
		
		SerialAction drag = TestChromosome.randomDrag();
		
		Vector<SerialAction> v = new Vector<>();
		
		v.add(drag);
		
		TestChromosome t = new TestChromosome(v);
		
		TestRunner.run(t);


	}
	
	@Test
	public void randomTouch() {
		
		System.out.println("\n** RANDOM TOUCH **\n");
		
		SerialAction touch = TestChromosome.randomTouch();
		
		Vector<SerialAction> v = new Vector<>();
		
		v.add(touch);
		
		TestChromosome t = new TestChromosome(v);
		
		TestRunner.run(t);

	}
	
	//@Test
	public void randomType() {
		
		System.out.println("\n** RANDOM TYPE **\n");
		
		SerialAction type = TestChromosome.randomType();
		
		Vector<SerialAction> v = new Vector<>();
		
		v.add(type);
		
		TestChromosome t = new TestChromosome(v);
		
		TestRunner.run(t);

	}

}
