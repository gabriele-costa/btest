package it.unige.cseclab.stim;

public class TypeAction extends com.android.monkeyrunner.recorder.actions.TypeAction {

	String whatToType;
	
	public TypeAction(String whatToType) {
		super((whatToType.length() > 8) ? whatToType.substring(0, TestChromosome.N_CHARS) : whatToType);
		
		this.whatToType = (whatToType.length() > 8) ? whatToType.substring(0, TestChromosome.N_CHARS) : whatToType;
	}

}
