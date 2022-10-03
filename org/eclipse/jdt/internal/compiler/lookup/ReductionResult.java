package org.eclipse.jdt.internal.compiler.lookup;

public abstract class ReductionResult
{
    protected static final ConstraintTypeFormula TRUE;
    protected static final ConstraintTypeFormula FALSE;
    protected static final int COMPATIBLE = 1;
    protected static final int SUBTYPE = 2;
    protected static final int SUPERTYPE = 3;
    protected static final int SAME = 4;
    protected static final int TYPE_ARGUMENT_CONTAINED = 5;
    protected static final int CAPTURE = 6;
    static final int EXCEPTIONS_CONTAINED = 7;
    protected static final int POTENTIALLY_COMPATIBLE = 8;
    protected TypeBinding right;
    protected int relation;
    
    static {
        TRUE = new ConstraintTypeFormula() {
            @Override
            public Object reduce(final InferenceContext18 context) {
                return this;
            }
            
            @Override
            public String toString() {
                return "TRUE";
            }
        };
        FALSE = new ConstraintTypeFormula() {
            @Override
            public Object reduce(final InferenceContext18 context) {
                return this;
            }
            
            @Override
            public String toString() {
                return "FALSE";
            }
        };
    }
    
    protected static String relationToString(final int relation) {
        switch (relation) {
            case 4: {
                return " = ";
            }
            case 1: {
                return " \u2192 ";
            }
            case 8: {
                return " \u2192? ";
            }
            case 2: {
                return " <: ";
            }
            case 3: {
                return " :> ";
            }
            case 5: {
                return " <= ";
            }
            case 6: {
                return " captureOf ";
            }
            default: {
                throw new IllegalArgumentException("Unknown type relation " + relation);
            }
        }
    }
}
