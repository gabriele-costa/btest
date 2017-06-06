package it.unige.cseclab.stim;

import com.android.monkeyrunner.MonkeyDevice;

public class TouchAction extends com.android.monkeyrunner.recorder.actions.TouchAction implements SerialAction {

	public final static byte TOUCH_ACT = 0x2;
	// int + int + {MonkeyDevice.DOWN_AND_UP, MonkeyDevice.DOWN, MonkeyDevice.UP, "Up"}
	
	public int x, y;
	
	public TouchAction(int x, int y) {
		super(x, y, "downAndUp");
		this.x = x;
		this.y = y;
	}

	@Override
	public int size() {
		return 1 + 2 * Integer.BYTES;
	}

	@Override
	public String toString() {
		return "touch " + x + " " + y;
	}
}
