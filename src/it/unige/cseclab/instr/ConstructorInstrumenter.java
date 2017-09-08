package it.unige.cseclab.instr;

import soot.Body;

import java.util.Map;

/**
 * Instruments Constructors
 *
 * TODO
 * Should not touch @this
 * Should not touch parameters
 * Should come after super()
 */
public class ConstructorInstrumenter extends Instrumenter {

    @Override
    protected void internalTransform(Body body, String s, Map<String, String> map) {
    }
}
