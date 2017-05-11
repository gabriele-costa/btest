package it.unige.cseclab.stim;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.android.monkeyrunner.recorder.actions.Action;
import com.android.monkeyrunner.recorder.actions.DragAction;
import com.android.monkeyrunner.recorder.actions.PressAction;
import com.android.monkeyrunner.recorder.actions.TouchAction;
import com.android.monkeyrunner.recorder.actions.TypeAction;
import com.android.monkeyrunner.recorder.actions.WaitAction;
import com.lagodiuk.ga.Chromosome;

import it.unige.cseclab.test.Demo.MyVector;

public class TestChromosome implements Chromosome<TestChromosome>, Cloneable {
	
	private final static byte WAIT_ACT = 0x0;
	// float
	private final static byte DRAG_ACT  = 0x1;
	// {NORTH, SOUTH, EAST, WEST} + int + int + int + int + int + long
	private final static byte TOUCH_ACT = 0x2;
	// int + int + {MonkeyDevice.DOWN_AND_UP, MonkeyDevice.DOWN, MonkeyDevice.UP, "Up"}
	private final static byte TYPE_ACT  = 0x3;
	// char[K]

	private static final Random random = new Random();
	private static final int K = 8;

	private byte[] vector;
	
	// no more than 256 stimulation
	public int length() {
		return vector[0];
	}
	
	/**
	 * Returns clone of current chromosome, which is mutated a bit
	 */
	@Override
	public TestChromosome mutate() {
		TestChromosome result = this.clone();

		// pick a random element and modify it
		int point = random.nextInt(this.length() + 1);
		
		if(point == 0) {
			result.vector[0] = (byte) (vector[0] ^ (1 << random.nextInt(8)));
		}
		else {
			int index = getIndex(point - 1);
			Action act = getActionAt(index);
			result.setAction(index, mutate(act));
		}

		return result;
	}

	private void setAction(int i, Action a) {
		// TODO Auto-generated method stub
		
	}

	private Action mutate(Action act) {
		
		Action res = null;
		
		if(act instanceof TypeAction) {
			int pos = random.nextInt(K + 1);
			if(pos == 0)
				res = newRandomAction();
		}
		else if(act instanceof DragAction) {
			
		}
		else if(act instanceof TouchAction) {
			
		}
		else if(act instanceof WaitAction) {
			
		}
		
		return res;
	}

	public Action getActionAt(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	private int getIndex(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Returns list of siblings <br/>
	 * Siblings are actually new chromosomes, <br/>
	 * created using any of crossover strategy
	 */
	@Override
	public List<TestChromosome> crossover(TestChromosome other) {
		TestChromosome thisClone = this.clone();
		TestChromosome otherClone = other.clone();

		// one point crossover
		int index = random.nextInt(this.vector.length - 1);
		for (int i = index; i < this.vector.length; i++) {
			int tmp = thisClone.vector[i];
			thisClone.vector[i] = otherClone.vector[i];
			otherClone.vector[i] = tmp;
		}

		return Arrays.asList(thisClone, otherClone);
	}

	@Override
	protected TestChromosome clone() {
		TestChromosome clone = new TestChromosome();
		System.arraycopy(this.vector, 0, clone.vector, 0, this.vector.length);
		return clone;
	}

	public byte[] getVector() {
		return this.vector;
	}

	@Override
	public String toString() {
		return Arrays.toString(this.vector);
	}
}
