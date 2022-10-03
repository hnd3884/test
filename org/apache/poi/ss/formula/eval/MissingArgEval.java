package org.apache.poi.ss.formula.eval;

public final class MissingArgEval implements ValueEval
{
    public static final MissingArgEval instance;
    
    private MissingArgEval() {
    }
    
    static {
        instance = new MissingArgEval();
    }
}
