package it.unige.cseclab.stim;

import java.nio.ByteBuffer;

import com.android.monkeyrunner.recorder.actions.Action;
import com.android.monkeyrunner.recorder.actions.DragAction.Direction;

public class ByteUtils {
    
	private static ByteBuffer buffer;    

    public static byte[] longToBytes(long x) {
    	buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
    	buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }
    
    public static byte[] intToBytes(int x) {
    	buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.putInt(0, x);
        return buffer.array();
    }

    public static int bytesToInt(byte[] bytes) {
    	buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getInt();
    }
    
    public static byte[] floatToBytes(float x) {
    	buffer = ByteBuffer.allocate(Float.BYTES);
        buffer.putFloat(0, x);
        return buffer.array();
    }

    public static float bytesToFloat(byte[] bytes) {
    	buffer = ByteBuffer.allocate(Float.BYTES);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getFloat();
    }
    
    public static byte[] stringToBytes(String x) {
        return x.getBytes();
    }

    public static String bytesToString(byte[] bytes) {
        return new String(bytes);
    }
    
    public static byte[] actionToBytes(Action x) {
    	if(x instanceof WaitAction) {
    		return waitToBytes((WaitAction) x);
    	}else if(x instanceof DragAction) {
    		return dragToBytes((DragAction) x);
    	}else if(x instanceof TouchAction) {
    		return touchToBytes((TouchAction) x);
    	}else if(x instanceof TypeAction) {
    		return typeToBytes((TypeAction) x);
    	}
    	
    	throw new UnsupportedOperationException("Unknown action " + x);
    }
    
    private static byte[] typeToBytes(TypeAction x) {
    	byte[] b = new byte[x.size()];
		b[0] = x.TYPE_ACT;
		byte[] s = ByteUtils.stringToBytes(x.whatToType);
		System.arraycopy(s, 0, b, 1, s.length);
		return b;
	}

	private static byte[] touchToBytes(TouchAction x) {
		byte[] b = new byte[x.size()];
		b[0] = x.TOUCH_ACT;
		System.arraycopy(ByteUtils.intToBytes(x.x), 0, b, 1, Integer.BYTES);
		System.arraycopy(ByteUtils.intToBytes(x.y), 0, b, 1 + Integer.BYTES, Integer.BYTES);
		// always TAP
		return b;
	}

	private static byte[] directionToBytes(String direction) {
		byte[] b = new byte[1];
		Direction dir = Direction.valueOf(direction);
		if(dir.equals(Direction.values()[0])) {
			b[0] = 0;
		} else if(dir.equals(Direction.values()[1])) {
			b[0] = 1;
		} else if(dir.equals(Direction.values()[2])) {
			b[0] = 2;
		} else if(dir.equals(Direction.values()[3])) {
			b[0] = 3;
		} else {
			throw new UnsupportedOperationException("Unknown direction "+direction);
		}
		return b;
	}

	private static byte[] dragToBytes(DragAction x) {
		byte[] b = new byte[x.size()];
		b[0] = x.DRAG_ACT;
		System.arraycopy(ByteUtils.directionToBytes(x.dir.toString()), 0, b, 1, 1);
		System.arraycopy(ByteUtils.intToBytes(x.startx), 0, b, 2, Integer.BYTES);
		System.arraycopy(ByteUtils.intToBytes(x.starty), 0, b, 2 + Integer.BYTES, Integer.BYTES);
		System.arraycopy(ByteUtils.intToBytes(x.endx), 0, b, 2 + 2 * Integer.BYTES, Integer.BYTES);
		System.arraycopy(ByteUtils.intToBytes(x.endy), 0, b, 2 + 3 * Integer.BYTES, Integer.BYTES);
		System.arraycopy(ByteUtils.intToBytes(x.numSteps), 0, b, 2 + 4 * Integer.BYTES, Integer.BYTES);
		System.arraycopy(ByteUtils.longToBytes(x.millis), 0, b, 2 + 5 * Integer.BYTES, Long.BYTES);
		
		return b;
	}

	private static byte[] waitToBytes(WaitAction x) {
		byte[] b = new byte[x.size()];
		b[0] = x.WAIT_ACT;
		System.arraycopy(ByteUtils.floatToBytes(x.seconds), 0, b, 1, Float.BYTES);
		return b;
	}

	public static Action bytesToAction(byte[] bytes) {
    	switch (bytes[0]) {
    	case WaitAction.WAIT_ACT: return bytesToWait(bytes);
    	case TouchAction.TOUCH_ACT: return bytesToTouch(bytes);
    	case TypeAction.TYPE_ACT: return bytesToType(bytes);
    	case DragAction.DRAG_ACT: return bytesToDrag(bytes);
    	}
    	
    	throw new UnsupportedOperationException("Unknown action code " + bytes[0]);
    }

	private static Action bytesToDrag(byte[] bytes) {
		
		byte[] d = new byte[1];
		byte[] startx = new byte[Integer.BYTES];
		byte[] starty = new byte[Integer.BYTES];
		byte[] endx = new byte[Integer.BYTES];
		byte[] endy = new byte[Integer.BYTES];
		byte[] numSteps = new byte[Integer.BYTES];
		byte[] millis = new byte[Long.BYTES];
		System.arraycopy(bytes, 1, d, 0, 1);
		System.arraycopy(bytes, 2, startx, 0, Integer.BYTES);
		System.arraycopy(bytes, 2 + Integer.BYTES, starty, 0, Integer.BYTES);
		System.arraycopy(bytes, 2 + 2 * Integer.BYTES, endx, 0, Integer.BYTES);
		System.arraycopy(bytes, 2 + 3 * Integer.BYTES, endy, 0, Integer.BYTES);
		System.arraycopy(bytes, 2 + 4 * Integer.BYTES, numSteps, 0, Integer.BYTES);
		System.arraycopy(bytes, 2 + 5 * Integer.BYTES, millis, 0, Long.BYTES);
		
		return new DragAction(
				Direction.valueOf(ByteUtils.bytesToDirection(d)), 
				ByteUtils.bytesToInt(startx),
				ByteUtils.bytesToInt(starty),
				ByteUtils.bytesToInt(endx),
				ByteUtils.bytesToInt(endy),
				ByteUtils.bytesToInt(numSteps),
				ByteUtils.bytesToLong(millis));
	}

	private static Action bytesToType(byte[] bytes) {
		byte[] b = new byte[Character.BYTES * TestChromosome.N_CHARS];
		System.arraycopy(bytes, 1, b, 0, Character.BYTES * TestChromosome.N_CHARS);
		return new TypeAction(new String(bytes));
	}

	private static Action bytesToTouch(byte[] bytes) {
		
		byte[] x = new byte[Integer.BYTES];
		byte[] y = new byte[Integer.BYTES];
		System.arraycopy(bytes, 1, x, 0, Integer.BYTES);
		System.arraycopy(bytes, 1 + Integer.BYTES, y, 0, Integer.BYTES);
		return new TouchAction(ByteUtils.bytesToInt(x), ByteUtils.bytesToInt(y));
	}

	private static String bytesToDirection(byte[] d) {
		
		return Direction.values()[d[0]].toString();
	}

	private static Action bytesToWait(byte[] bytes) {
		byte[] b = new byte[Float.BYTES];
		System.arraycopy(bytes, 1, b, 0, Float.BYTES);
		return new WaitAction(ByteUtils.bytesToFloat(b));
	}
}
