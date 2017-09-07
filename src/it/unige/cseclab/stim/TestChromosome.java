package it.unige.cseclab.stim;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.android.monkeyrunner.MonkeyDevice;
import com.android.monkeyrunner.recorder.actions.Action;
import com.android.monkeyrunner.recorder.actions.DragAction.Direction;
import com.lagodiuk.ga.Chromosome;


public class TestChromosome implements Chromosome<TestChromosome>, Cloneable {
	
	final static double PnewAct = 0.01;

	private static final Random random = new Random();

	public static final float MAX_WAIT = 1;
	public static final int MAX_MILLIS = 200;
	public static final int MAX_STEPS = 1;
	public static final int HEIGHT = 1920;
	public static final int WIDTH = 1080;
	public static final int N_CHARS = 16;
	private static final String[] DIRECTIONS = {
			Direction.EAST.toString(),
			Direction.NORTH.toString(),
			Direction.SOUTH.toString(),
			Direction.WEST.toString()
			};

	private static final int AVG_RANDOM_LEN = 20;

	
	private byte[] vector = null;
	
	public TestChromosome(Vector<SerialAction> v) {
		
		vector = serialize(v);
		
	}
	
	private static double IH(int n) {
		double sum = 0;
		for(int i = 0; i < n; i++) {
			sum += random.nextDouble();
		}
		return sum;
	}
	
	public static TestChromosome random() {
		int size = (int)Math.ceil(IH(2 * AVG_RANDOM_LEN));
		Vector<SerialAction> v = new Vector<>();
		for(int i = 0; i < size; i++) {
			v.add(newRandomAction());
		}
		
		return new TestChromosome(v);
	}

	// no more than 256 stimulation
	public int length() {
		return vector[0] & 0xFF;
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
			result.resize();
		}
		else {
			Vector<SerialAction> v = this.aVector();
			v.set(point-1, mutate(v.get(point-1)));
			result = new TestChromosome(v);
		}

		return result;
	}

	/**
	 * Resizes a chromosome based its dimension (first byte)
	 */
	private void resize() {
		Vector<SerialAction> v = aVector();
		for(int i = v.size(); i < length(); i++) {
			v.add(newRandomAction());
		}
		for(int i = length(); i < v.size();) {
			v.removeElementAt(v.size() - 1);
		}
		
		vector = serialize(v);
	}

	private byte[] serialize(Vector<SerialAction> v) {
		byte[] b = new byte[dimension(v)];
		
		b[0] = (byte)v.size();
		
		int pos = 1;
		
		for(SerialAction a : v) {
			if(a != null) {
				System.arraycopy(ByteUtils.actionToBytes(a), 0, b, pos, a.size());
				pos += a.size();
			}
		}
		
		return b;
	}

	private int dimension(Vector<SerialAction> v) {
		int sum = 1; // counter
		
		for(SerialAction a : v) {
			if(v != null) {
				sum += a.size();
			}
		}
		
		return sum;
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

	private SerialAction mutate(SerialAction act) {
		
		SerialAction res = null;
		
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
		
		int what = random.nextInt(2);
		
		return new TouchAction(
				(what == 0) ? mutateInt(act.x) : act.x, 
				(what == 1) ? mutateInt(act.y) : act.y);
	}

	private String mutateStringArray(String[] a) {
		return a[random.nextInt(a.length)];
	}

	private DragAction mutateDrag(DragAction act) {
		int what = random.nextInt(5);
		
		return new DragAction(
				(what == 0) ? mutateDirection(act.dir) : act.dir,
				(what == 1) ? mutateInt(act.startx) : act.startx,
				(what == 2) ? mutateInt(act.starty) : act.starty,
				(what == 3) ? mutateInt(act.endx) : act.endx,
				(what == 4) ? mutateInt(act.endy) : act.endy,
				MAX_STEPS,
				MAX_MILLIS
				);
	}

	private Direction mutateDirection(Direction dir) {
		int what = random.nextInt(4);
		
		return getDirection((byte)what);
	}

	private TypeAction mutateType(TypeAction act) {
		return new TypeAction(mutateString(act.whatToType));
	}

	private String mutateString(String whatToType) {
		
		int ins = random.nextInt(whatToType.length() + 1);
		
		char what = StringRandomizer.ALPHABET.charAt(random.nextInt(StringRandomizer.ALPHABET.length()));
		
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

	public static SerialAction newRandomAction() {
		byte type = (byte)random.nextInt(4);
		
		switch(type) {
		case WaitAction.WAIT_ACT: {return randomWait();} // float
		case DragAction.DRAG_ACT: {return randomDrag();} // {NORTH, SOUTH, EAST, WEST} + int + int + int + int + int + long
		case TouchAction.TOUCH_ACT: {return randomTouch();} // int + int + {MonkeyDevice.DOWN_AND_UP, MonkeyDevice.DOWN, MonkeyDevice.UP, "Up"}
		case TypeAction.TYPE_ACT: {return randomType();} // char[K];
		}
		return null;
	}

	public static TypeAction randomType() {
		
		String w = StringRandomizer.next(N_CHARS);
		
		TypeAction act = new TypeAction(w);
		return act;
	}

	public static TouchAction randomTouch() {
		int x = random.nextInt(WIDTH);
		int y = random.nextInt(HEIGHT);
		TouchAction act = new TouchAction(x, y);
		return act;
	}

	public static  DragAction randomDrag() {
		Direction dir = Direction.values()[random.nextInt(4)];
		int startx = random.nextInt(WIDTH);
		int starty = random.nextInt(HEIGHT);
		int endx = random.nextInt(WIDTH);
		int endy = random.nextInt(HEIGHT);
		//int steps = 1+random.nextInt(MAX_STEPS);
		int steps = MAX_STEPS;
		//long millis = 1+random.nextInt(MAX_MILLIS);
		long millis = MAX_MILLIS;
		
		DragAction act = new DragAction(dir, startx, starty, endx, endy, steps, millis);
		
		return act;
	}

	public static WaitAction randomWait() {
		float time = random.nextFloat() * MAX_WAIT;
		WaitAction act = new WaitAction(time);
		return act;
	}

	public SerialAction getActionAt(int i) {
		
		if(i > vector.length) {
			return null;
		}
		
		switch (vector[i]) {
		case WaitAction.WAIT_ACT: return makeWaitAction(i + 1);
		case TouchAction.TOUCH_ACT: return makeTouchAction(i + 1);
		case TypeAction.TYPE_ACT: return makeTypeAction(i + 1);
		case DragAction.DRAG_ACT: return makeDragAction(i + 1);
		}
		
		throw new IllegalArgumentException("Unknown action code " + vector[i]);
	}
	
	private DragAction makeDragAction(int i) {
		
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
		
		return new DragAction(dir, 
				ByteUtils.bytesToInt(sx), 
				ByteUtils.bytesToInt(sy), 
				ByteUtils.bytesToInt(ex), 
				ByteUtils.bytesToInt(ey), 
				ByteUtils.bytesToInt(ns), 
				ByteUtils.bytesToLong(ml));
		
	}

	private Direction getDirection(byte b) {
		switch (b) {
		case 0: return Direction.EAST;
		case 1: return Direction.WEST;
		case 2: return Direction.NORTH;
		case 3: return Direction.SOUTH;
		}
		
		throw new UnsupportedOperationException("Unknown direction " + b);
	}

	private TypeAction makeTypeAction(int i) {
		byte[] c = new byte[8 * Character.BYTES];
		System.arraycopy(vector, i, c, 0, c.length);
		return new TypeAction(ByteUtils.bytesToString(c));
	}

	private TouchAction makeTouchAction(int i) {
		byte[] x = new byte[Integer.BYTES];
		byte[] y = new byte[Integer.BYTES];
		System.arraycopy(vector, i, x, 0, Integer.BYTES);
		System.arraycopy(vector, i + Integer.BYTES, y, 0, Integer.BYTES);
		
		return new TouchAction(ByteUtils.bytesToInt(x), ByteUtils.bytesToInt(y));
	}

	private WaitAction makeWaitAction(int i) {
		byte[] b = new byte[Float.BYTES];
		System.arraycopy(vector, i, b, 0, Float.BYTES);
		return new WaitAction(ByteUtils.bytesToFloat(b));
	}

	public Vector<SerialAction> aVector() {
		Vector<SerialAction> v = new Vector<>();
		
		int pos = 1;
		
		for(int i = 0; i < length() && pos < vector.length; i++) {
			SerialAction a = getActionAt(pos);
			v.add(a);
			pos += a.size();
		}
		
		return v;
	}

	/**
	 * Returns list of siblings <br/>
	 * Siblings are actually new chromosomes, <br/>
	 * created using any of crossover strategy
	 */
	@Override
	public List<TestChromosome> crossover(TestChromosome other) {
		Vector<SerialAction> thisV = this.aVector();
		Vector<SerialAction> otherV = other.aVector();

		// single point crossover
		int index = random.nextInt(Math.min(this.length(), other.length()));
		
		for(int i = index; i < Math.max(this.length(), other.length()); i++) {
			if(i < thisV.size() && i < otherV.size()) {
				SerialAction tmp = otherV.get(i);
				otherV.set(i, thisV.get(i));
				thisV.set(i, tmp);
			} else if(i < this.length()) {
				otherV.add(thisV.get(i));
				thisV.set(i, null);
			} else if(i < other.length()) {
				thisV.add(otherV.get(i));
				otherV.set(i, null);
			}
		}
		
		compact(thisV);
		compact(otherV);
		
		TestChromosome boy = new TestChromosome(thisV);
		TestChromosome girl = new TestChromosome(otherV);
		
		return Arrays.asList(boy, girl);
	}

	private void compact(Vector<SerialAction> v) {
		v.removeAll(Collections.singleton(null));
	}

	@Override
	protected TestChromosome clone() {
		TestChromosome clone = new TestChromosome(this.aVector());
		return clone;
	}

	public byte[] getVector() {
		return this.vector;
	}

	@Override
	public String toString() {
		return Arrays.toString(this.vector);
	}
	
	public String printActions(String sep) {
		Vector<SerialAction> v = aVector();
		
		StringBuilder sb = new StringBuilder();
		
		for(SerialAction a : v)
			sb.append(a.toString() + sep);
		
		return sb.toString();
	}
}
