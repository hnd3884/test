package org.apache.poi.ss.formula.eval;

public final class FunctionNameEval implements ValueEval
{
    private final String _functionName;
    
    public FunctionNameEval(final String functionName) {
        this._functionName = functionName;
    }
    
    public String getFunctionName() {
        return this._functionName;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [" + this._functionName + "]";
    }
}
