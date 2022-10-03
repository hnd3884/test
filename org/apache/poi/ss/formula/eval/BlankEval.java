package org.apache.poi.ss.formula.eval;

public final class BlankEval implements ValueEval
{
    public static final BlankEval instance;
    
    private BlankEval() {
    }
    
    static {
        instance = new BlankEval();
    }
}
