package it.unige.cseclab.stim;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.android.monkeyrunner.recorder.actions.Action;
import com.android.monkeyrunner.recorder.actions.DragAction.Direction;
import com.lagodiuk.ga.Chromosome;

import it.unige.cseclab.test.Demo.MyVector;

public class TestChromosome implements Chromosome<TestChromosome>, Cloneable {
	
	final static double PnewAct = 0.01;
	
	private final static byte WAIT_ACT = 0x0;
	// float
	private final static byte DRAG_ACT  = 0x1;
	// {NORTH, SOUTH, EAST, WEST} + int + int + int + int + int + long
	private final static byte TOUCH_ACT = 0x2;
	// int + int + {MonkeyDevice.DOWN_AND_UP, MonkeyDevice.DOWN, MonkeyDevice.UP, "Up"}
	private final static byte TYPE_ACT  = 0x3;
	// char[K]

	private static final Random random = new Random();

	public static final float MAX_WAIT = 10;
	public static final int MAX_MILLIS = 2000;
	public static final int MAX_STEPS = 5;
	public static final int HEIGHT = 2000;
	public static final int WIDTH = 2000;
	public static final int N_CHARS = 8;
	private static final String[] DIRECTIONS = {
			Direction.EAST.toString(),
			Direction.NORTH.toString(),
			Direction.SOUTH.toString(),
			Direction.WEST.toString()
			};

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
			result.vector[0] = mutateByte(vector[0]);
		}
		else {
			int index = getIndex(point - 1);
			Action act = getActionAt(index);
			result.setAction(index, mutate(act));
		}

		return result;
	}

	private byte mutateByte(byte b) {
		return (byte) (b ^ (1 << random.nextInt(8)));
	}
	
	private int mutateInt(int b) {
		return (b ^ (1 << random.nextInt(32)));
	}
	
	private long mutateLong(long b) {
		return (b ^ (1 << random.nextInt(32)));
	}

	private void setAction(int i, Action a) {
		// TODO Auto-generated method stub
		
	}

	private Action mutate(Action act) {
		
		Action res = null;
		
		double d = random.nextDouble();
		if(d <= PnewAct) {
			res = newRandomAction();
		}
		else if(act instanceof TypeAction) {
			res = mutateType((TypeAction) act);
		}
		else if(act instanceof DragAction) {
			res = mutateDrag((DragAction) act);
		}
		else if(act instanceof TouchAction) {
			res = mutateTouch((TouchAction) act);
		}
		else if(act instanceof WaitAction) {
			res = mutateWait((WaitAction) act);
		}
		
		return res;
	}

	private WaitAction mutateWait(WaitAction act) {
		return new WaitAction(mutateFloat(act.seconds, MAX_WAIT));
	}

	private float mutateFloat(float seconds, float scale) {
		float delta = random.nextFloat() * scale;
		return (random.nextFloat() > 0.5) ? seconds + delta : seconds - delta;
	}

	private TouchAction mutateTouch(TouchAction act) {
		
		int what = random.nextInt(3);
		
		return new TouchAction(
				(what == 0) ? mutateInt(act.x) : act.x, 
				(what == 1) ? mutateInt(act.y) : act.y, 
				(what == 2) ? mutateStringArray(DIRECTIONS) : act.direction);
	}

	private String mutateStringArray(String[] a) {
		return a[random.nextInt(a.length)];
	}

	private DragAction mutateDrag(DragAction act) {
		int what = random.nextInt(7);
		
		return new DragAction(
				(what == 0) ? mutateDirection(act.dir) : act.dir,
				(what == 1) ? mutateInt(act.startx) : act.startx,
				(what == 2) ? mutateInt(act.starty) : act.starty,
				(what == 3) ? mutateInt(act.endx) : act.endx,
				(what == 4) ? mutateInt(act.endy) : act.endy,
				(what == 5) ? mutateInt(act.numSteps) : act.numSteps,
				(what == 6) ? mutateLong(act.millis) : act.millis
				);
	}

	private Direction mutateDirection(Direction dir) {
		int what = random.nextInt(4);
		
		switch (what) {
		case 0: return Direction.EAST;
		case 1: return Direction.WEST;
		case 2: return Direction.NORTH;
		case 3: return Direction.SOUTH;
		}
		
		return null;
	}

	private TypeAction mutateType(TypeAction act) {
		return new TypeAction(mutateString(act.whatToType));
	}

	private String mutateString(String whatToType) {
		String alphabet = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ@.";
		
		int ins = random.nextInt(whatToType.length() + 1);
		
		char what = alphabet.charAt(random.nextInt(alphabet.length()));
		
		if(ins == whatToType.length()) {
			whatToType += what;
		}
		else {
			char[] chars = whatToType.toCharArray();
			chars[ins] = what;
			whatToType = new String(chars);
		}
		
		return whatToType;
	}

	private Action newRandomAction() {
		byte type = (byte)random.nextInt(4);
		
		switch(type) {
		case WAIT_ACT: {return randomWait();} // float
		case DRAG_ACT: {return randomDrag();} // {NORTH, SOUTH, EAST, WEST} + int + int + int + int + int + long
		case TOUCH_ACT: {return randomTouch();} // int + int + {MonkeyDevice.DOWN_AND_UP, MonkeyDevice.DOWN, MonkeyDevice.UP, "Up"}
		case TYPE_ACT: {return randomType();} // char[K];
		}
		return null;
	}

	private TypeAction randomType() {
		byte[] raw = new byte[N_CHARS];
		random.nextBytes(raw);
		TypeAction act = new TypeAction(new String(raw));
		return act;
	}

	private TouchAction randomTouch() {
		int x = random.nextInt(WIDTH);
		int y = random.nextInt(HEIGHT);
		Direction dir = Direction.values()[random.nextInt(4)];
		TouchAction act = new TouchAction(x, y, dir.toString());
		return act;
	}

	private DragAction randomDrag() {
		Direction dir = Direction.values()[random.nextInt(4)];
		int startx = random.nextInt(WIDTH);
		int starty = random.nextInt(HEIGHT);
		int endx = random.nextInt(WIDTH);
		int endy = random.nextInt(HEIGHT);
		int steps = random.nextInt(MAX_STEPS);
		long millis = random.nextInt(MAX_MILLIS);
		DragAction act = new DragAction(dir, startx, starty, endx, endy, steps, millis);
		
		return act;
	}

	private WaitAction randomWait() {
		float time = random.nextFloat() * MAX_WAIT;
		WaitAction act = new WaitAction(time);
		return act;
	}

	public Action getActionAt(int i) {
		switch (vector[i]) {
		case WAIT_ACT: return makeWaitAction(i + 1);
		case TOUCH_ACT: return makeTouchAction(i + 1);
		case TYPE_ACT: return makeTypeAction(i + 1);
		case DRAG_ACT: return makeDragAction(i + 1);
		}
		
		throw new IllegalArgumentException("Unknown action code " + vector[i]);
	}
	
	private Action makeDragAction(int i) {
		
		byte[] sx = new byte[Integer.BYTES];
		byte[] sy = new byte[Integer.BYTES];
		byte[] ex = new byte[Integer.BYTES];
		byte[] ey = new byte[Integer.BYTES];
		byte[] ns = new byte[Integer.BYTES];
		byte[] ml = new byte[Long.BYTES];
		
		Direction dir = getDirection(vector[i]);
		
		System.arraycopy(vector, i + 1, sx, 0, Integer.BYTES);
		System.arraycopy(vector, i + 1 + Integer.BYTES, sy, 0, Integer.BYTES);
		System.arraycopy(vector, i + 1 + 2*Integer.BYTES, ex, 0, Integer.BYTES);
		System.arraycopy(vector, i + 1 + 3*Integer.BYTES, ey, 0, Integer.BYTES);
		System.arraycopy(vector, i + 1 + 4*Integer.BYTES, ns, 0, Integer.BYTES);
		System.arraycopy(vector, i + 1 + 5*Integer.BYTES, ml, 0, Long.BYTES);
		
		return new DragAction(dir, sx, sy, ex, ey, ns, ml);
		
	}

	private Action makeTypeAction(int i) {
		byte[] c = new byte[8 * Character.BYTES];
		System.arraycopy(vector, i, c, 0, c.length);
		return new TypeAction(ByteUtils.bytesToString(c));
	}

	private Action makeTouchAction(int i) {
		byte[] x = new byte[Integer.BYTES];
		byte[] y = new byte[Integer.BYTES];
		String dir = null;
		System.arraycopy(vector, i, x, 0, Integer.BYTES);
		System.arraycopy(vector, i + Integer.BYTES, y, 0, Integer.BYTES);
		switch (vector[i + 2 * Integer.BYTES]) {
		case 0: dir = DIRECTIONS[0];
		case 1: dir = DIRECTIONS[1];
		case 2: dir = DIRECTIONS[2];
		case 3: dir = DIRECTIONS[3];
		}
		return new TouchAction(ByteUtils.bytesToInt(x), ByteUtils.bytesToInt(y), dir);
	}

	private WaitAction makeWaitAction(int i) {
		byte[] b = new byte[Float.BYTES];
		System.arraycopy(vector, i, b, 0, Float.BYTES);
		return new WaitAction(ByteUtils.bytesToFloat(b));
	}

	public Vector<Action> aVector() {
		Vector<Action> v = new Vector<>();
		
		int pos = 1;
		
		for(int i = 0; i < vector[0] && pos < vector.length; i++) {
			Action a = getActionAt(pos);
			v.add(getActionAt(pos));
			pos += size(a);
		}
		
		return v;
	}

	private int size(Action a) throws IllegalArgumentException {
		if(a instanceof DragAction) {
			return 1 + 5 * Long.BYTES + Long.BYTES;
		} else if(a instanceof TouchAction) {
			return 2 * Integer.BYTES + 1;
		} else if(a instanceof TypeAction) {
			return 8 * Character.BYTES;
		} else if(a instanceof WaitAction) {
			return Float.BYTES;
		} else
			throw new IllegalArgumentException("Unknown action " + a.getClass());
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
