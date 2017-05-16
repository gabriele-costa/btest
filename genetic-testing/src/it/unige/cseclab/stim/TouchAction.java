package it.unige.cseclab.stim;

public class TouchAction extends com.android.monkeyrunner.recorder.actions.TouchAction implements SerialAction {

	public final static byte TOUCH_ACT = 0x2;
	// int + int + {MonkeyDevice.DOWN_AND_UP, MonkeyDevice.DOWN, MonkeyDevice.UP, "Up"}
	
	public int x, y;
	public String direction;
	
	public TouchAction(int x, int y, String direction) {
		super(x, y, direction);
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

	@Override
	public int size() {
		return 1 + 1 + 2 * Integer.BYTES;
	}

}
