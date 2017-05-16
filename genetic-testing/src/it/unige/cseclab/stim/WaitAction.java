package it.unige.cseclab.stim;

public class WaitAction extends com.android.monkeyrunner.recorder.actions.WaitAction implements SerialAction {

	public final static byte WAIT_ACT = 0x0;
	// float
	
	public float seconds; 
	
	public WaitAction(float howLongSeconds) {
		super(howLongSeconds);
		seconds = howLongSeconds;
	}

	@Override
	public int size() {
		return 1 + Float.BYTES;
	}

}
