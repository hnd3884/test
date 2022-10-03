package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.EvaluationName;

public final class ExternalNameEval implements ValueEval
{
    private final EvaluationName _name;
    
    public ExternalNameEval(final EvaluationName name) {
        this._name = name;
    }
    
    public EvaluationName getName() {
        return this._name;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [" + this._name.getNameText() + "]";
    }
}
