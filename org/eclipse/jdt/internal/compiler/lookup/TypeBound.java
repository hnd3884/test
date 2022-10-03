package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;

public class TypeBound extends ReductionResult
{
    InferenceVariable left;
    boolean isSoft;
    long nullHints;
    
    static TypeBound createBoundOrDependency(final InferenceSubstitution theta, final TypeBinding type, final InferenceVariable variable) {
        return new TypeBound(variable, theta.substitute(theta, type), 2, true);
    }
    
    TypeBound(final InferenceVariable inferenceVariable, final TypeBinding typeBinding, final int relation) {
        this(inferenceVariable, typeBinding, relation, false);
    }
    
    TypeBound(final InferenceVariable inferenceVariable, final TypeBinding typeBinding, final int relation, final boolean isSoft) {
        this.left = inferenceVariable;
        this.right = this.safeType(typeBinding);
        if (((inferenceVariable.tagBits | this.right.tagBits) & 0x180000000000000L) != 0x0L) {
            if ((inferenceVariable.tagBits & 0x180000000000000L) == (this.right.tagBits & 0x180000000000000L)) {
                this.left = (InferenceVariable)inferenceVariable.withoutToplevelNullAnnotation();
                this.right = this.right.withoutToplevelNullAnnotation();
            }
            else {
                long mask = 0L;
                switch (relation) {
                    case 4: {
                        mask = 108086391056891904L;
                        break;
                    }
                    case 2: {
                        mask = 72057594037927936L;
                        break;
                    }
                    case 3: {
                        mask = 36028797018963968L;
                        break;
                    }
                }
                final InferenceVariable prototype = inferenceVariable.prototype();
                prototype.nullHints |= (this.right.tagBits & mask);
            }
        }
        this.relation = relation;
        this.isSoft = isSoft;
    }
    
    private TypeBinding safeType(final TypeBinding type) {
        if (type != null && type.isLocalType()) {
            final MethodBinding enclosingMethod = ((LocalTypeBinding)type.original()).enclosingMethod;
            if (enclosingMethod != null && CharOperation.prefixEquals(TypeConstants.ANONYMOUS_METHOD, enclosingMethod.selector)) {
                return type.superclass();
            }
        }
        return type;
    }
    
    boolean isBound() {
        return this.right.isProperType(true);
    }
    
    @Override
    public int hashCode() {
        return this.left.hashCode() + this.right.hashCode() + this.relation;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TypeBound) {
            final TypeBound other = (TypeBound)obj;
            return this.relation == other.relation && TypeBinding.equalsEquals(this.left, other.left) && TypeBinding.equalsEquals(this.right, other.right);
        }
        return false;
    }
    
    @Override
    public String toString() {
        final boolean isBound = this.right.isProperType(true);
        final StringBuffer buf = new StringBuffer();
        buf.append(isBound ? "TypeBound  " : "Dependency ");
        buf.append(this.left.sourceName);
        buf.append(ReductionResult.relationToString(this.relation));
        buf.append(this.right.readableName());
        return buf.toString();
    }
}
