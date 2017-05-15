package it.unige.cseclab.stim;

import java.nio.ByteBuffer;

public class ByteUtils {
    
	private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);    

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getLong();
    }
    
    public static byte[] intToBytes(int x) {
        buffer.putInt(0, x);
        return buffer.array();
    }

    public static int bytesToInt(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();
        return buffer.getInt();
    }
    
    public static byte[] floatToBytes(float x) {
        buffer.putFloat(0, x);
        return buffer.array();
    }

    public static float bytesToFloat(byte[] bytes) {
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
    
    
    
}
