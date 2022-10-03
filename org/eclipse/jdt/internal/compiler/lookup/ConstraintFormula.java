package org.eclipse.jdt.internal.compiler.lookup;

import java.util.Set;
import java.util.HashSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class ConstraintFormula extends ReductionResult
{
    static final List<InferenceVariable> EMPTY_VARIABLE_LIST;
    static final ConstraintFormula[] NO_CONSTRAINTS;
    static final char LEFT_ANGLE_BRACKET = '\u27e8';
    static final char RIGHT_ANGLE_BRACKET = '\u27e9';
    
    static {
        EMPTY_VARIABLE_LIST = Collections.emptyList();
        NO_CONSTRAINTS = new ConstraintTypeFormula[0];
    }
    
    public abstract Object reduce(final InferenceContext18 p0) throws InferenceFailureException;
    
    Collection<InferenceVariable> inputVariables(final InferenceContext18 context) {
        return ConstraintFormula.EMPTY_VARIABLE_LIST;
    }
    
    Collection<InferenceVariable> outputVariables(final InferenceContext18 context) {
        final Set<InferenceVariable> variables = new HashSet<InferenceVariable>();
        this.right.collectInferenceVariables(variables);
        if (!variables.isEmpty()) {
            variables.removeAll(this.inputVariables(context));
        }
        return variables;
    }
    
    public boolean applySubstitution(final BoundSet solutionSet, final InferenceVariable[] variables) {
        for (int i = 0; i < variables.length; ++i) {
            final InferenceVariable variable = variables[i];
            final TypeBinding instantiation = solutionSet.getInstantiation(variables[i], null);
            if (instantiation == null) {
                return false;
            }
            this.right = this.right.substituteInferenceVariable(variable, instantiation);
        }
        return true;
    }
    
    protected void appendTypeName(final StringBuffer buf, final TypeBinding type) {
        if (type instanceof CaptureBinding18) {
            buf.append(type.toString());
        }
        else {
            buf.append(type.readableName());
        }
    }
}
