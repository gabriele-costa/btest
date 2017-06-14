package it.unige.cseclab.stim;

public class DragAction extends com.android.monkeyrunner.recorder.actions.DragAction implements SerialAction {

	public final static byte DRAG_ACT  = 0x1;
	// {NORTH, SOUTH, EAST, WEST} + int + int + int + int + int + long
	
	public Direction dir;
	public int startx, starty, endx, endy, numSteps;
	public long millis;
	
	public DragAction(Direction dir, int startx, int starty, int endx, int endy, int numSteps, long millis) {

		super(dir, startx, starty, endx, endy, numSteps, millis);
		this.dir = dir;
		this.startx = startx;
		this.starty = starty;
		this.endx = endx;
		this.endy = endy;
		this.numSteps = numSteps;
		this.millis = millis;
		
	}

	@Override
	public int size() {
		return 1 + 1 + 5 * Integer.BYTES + Long.BYTES;
	}
	
	@Override
	public String toString() {
		return "drag " + startx + " " + starty + " " + endx + " " + endy ;
	}

}
