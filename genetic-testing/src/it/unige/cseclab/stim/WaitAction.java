package it.unige.cseclab.stim;

public class WaitAction extends com.android.monkeyrunner.recorder.actions.WaitAction {

	public float seconds; 
	
	public WaitAction(float howLongSeconds) {
		super(howLongSeconds);
		seconds = howLongSeconds;
	}

}
