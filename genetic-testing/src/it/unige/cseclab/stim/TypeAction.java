package it.unige.cseclab.stim;

public class TypeAction extends com.android.monkeyrunner.recorder.actions.TypeAction implements SerialAction {

	public final static byte TYPE_ACT  = 0x3;
	// char[K]
	
	String whatToType;
	
	public TypeAction(String whatToType) {
		super((whatToType.length() > TestChromosome.N_CHARS) ? whatToType.substring(0, TestChromosome.N_CHARS) : whatToType);
		
		this.whatToType = (whatToType.length() > TestChromosome.N_CHARS) ? whatToType.substring(0, TestChromosome.N_CHARS) : whatToType;
	}

	@Override
	public int size() {
		return 1 + TestChromosome.N_CHARS * Character.BYTES;
	}

}
