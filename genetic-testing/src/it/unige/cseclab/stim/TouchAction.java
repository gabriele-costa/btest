package it.unige.cseclab.stim;

public class TouchAction extends com.android.monkeyrunner.recorder.actions.TouchAction {

	public int x, y;
	public String direction;
	
	public TouchAction(int x, int y, String direction) {
		super(x, y, direction);
		this.x = x;
		this.y = y;
		this.direction = direction;
	}

}
