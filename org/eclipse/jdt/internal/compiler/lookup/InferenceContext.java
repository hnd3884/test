package org.eclipse.jdt.internal.compiler.lookup;

public class InferenceContext
{
    private TypeBinding[][][] collectedSubstitutes;
    MethodBinding genericMethod;
    int depth;
    int status;
    TypeBinding expectedType;
    boolean hasExplicitExpectedType;
    public boolean isUnchecked;
    TypeBinding[] substitutes;
    static final int FAILED = 1;
    
    public InferenceContext(final MethodBinding genericMethod) {
        this.genericMethod = genericMethod;
        final TypeVariableBinding[] typeVariables = genericMethod.typeVariables;
        final int varLength = typeVariables.length;
        this.collectedSubstitutes = new TypeBinding[varLength][3][];
        this.substitutes = new TypeBinding[varLength];
    }
    
    public TypeBinding[] getSubstitutes(final TypeVariableBinding typeVariable, final int constraint) {
        return this.collectedSubstitutes[typeVariable.rank][constraint];
    }
    
    public boolean hasUnresolvedTypeArgument() {
        for (int i = 0, varLength = this.substitutes.length; i < varLength; ++i) {
            if (this.substitutes[i] == null) {
                return true;
            }
        }
        return false;
    }
    
    public void recordSubstitute(final TypeVariableBinding typeVariable, final TypeBinding actualType, final int constraint) {
        final TypeBinding[][] variableSubstitutes = this.collectedSubstitutes[typeVariable.rank];
        TypeBinding[] constraintSubstitutes = variableSubstitutes[constraint];
        int length;
        if (constraintSubstitutes == null) {
            length = 0;
            constraintSubstitutes = new TypeBinding[] { null };
        }
        else {
            length = constraintSubstitutes.length;
            for (int i = 0; i < length; ++i) {
                final TypeBinding substitute = constraintSubstitutes[i];
                if (substitute == actualType) {
                    return;
                }
                if (substitute == null) {
                    constraintSubstitutes[i] = actualType;
                    return;
                }
            }
            System.arraycopy(constraintSubstitutes, 0, constraintSubstitutes = new TypeBinding[length + 1], 0, length);
        }
        constraintSubstitutes[length] = actualType;
        variableSubstitutes[constraint] = constraintSubstitutes;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(20);
        buffer.append("InferenceContex for ");
        for (int i = 0, length = this.genericMethod.typeVariables.length; i < length; ++i) {
            buffer.append(this.genericMethod.typeVariables[i]);
        }
        buffer.append(this.genericMethod);
        buffer.append("\n\t[status=");
        switch (this.status) {
            case 0: {
                buffer.append("ok]");
                break;
            }
            case 1: {
                buffer.append("failed]");
                break;
            }
        }
        if (this.expectedType == null) {
            buffer.append(" [expectedType=null]");
        }
        else {
            buffer.append(" [expectedType=").append(this.expectedType.shortReadableName()).append(']');
        }
        buffer.append(" [depth=").append(this.depth).append(']');
        buffer.append("\n\t[collected={");
        for (int i = 0, length = (this.collectedSubstitutes == null) ? 0 : this.collectedSubstitutes.length; i < length; ++i) {
            final TypeBinding[][] collected = this.collectedSubstitutes[i];
            for (int j = 0; j <= 2; ++j) {
                final TypeBinding[] constraintCollected = collected[j];
                if (constraintCollected != null) {
                    for (int k = 0, clength = constraintCollected.length; k < clength; ++k) {
                        buffer.append("\n\t\t").append(this.genericMethod.typeVariables[i].sourceName);
                        switch (j) {
                            case 0: {
                                buffer.append("=");
                                break;
                            }
                            case 1: {
                                buffer.append("<:");
                                break;
                            }
                            case 2: {
                                buffer.append(">:");
                                break;
                            }
                        }
                        if (constraintCollected[k] != null) {
                            buffer.append(constraintCollected[k].shortReadableName());
                        }
                    }
                }
            }
        }
        buffer.append("}]");
        buffer.append("\n\t[inferred=");
        int count = 0;
        for (int l = 0, length2 = (this.substitutes == null) ? 0 : this.substitutes.length; l < length2; ++l) {
            if (this.substitutes[l] != null) {
                ++count;
                buffer.append('{').append(this.genericMethod.typeVariables[l].sourceName);
                buffer.append("=").append(this.substitutes[l].shortReadableName()).append('}');
            }
        }
        if (count == 0) {
            buffer.append("{}");
        }
        buffer.append(']');
        return buffer.toString();
    }
}
