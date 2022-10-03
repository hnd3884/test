package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;

public class CatchParameterBinding extends LocalVariableBinding
{
    TypeBinding[] preciseTypes;
    
    public CatchParameterBinding(final LocalDeclaration declaration, final TypeBinding type, final int modifiers, final boolean isArgument) {
        super(declaration, type, modifiers, isArgument);
        this.preciseTypes = Binding.NO_EXCEPTIONS;
    }
    
    public TypeBinding[] getPreciseTypes() {
        return this.preciseTypes;
    }
    
    public void setPreciseType(final TypeBinding raisedException) {
        final int length = this.preciseTypes.length;
        for (int i = 0; i < length; ++i) {
            if (TypeBinding.equalsEquals(this.preciseTypes[i], raisedException)) {
                return;
            }
        }
        System.arraycopy(this.preciseTypes, 0, this.preciseTypes = new TypeBinding[length + 1], 0, length);
        this.preciseTypes[length] = raisedException;
    }
    
    @Override
    public boolean isCatchParameter() {
        return true;
    }
}
