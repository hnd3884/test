package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.impl.Constant;

public class ParameterizedFieldBinding extends FieldBinding
{
    public FieldBinding originalField;
    
    public ParameterizedFieldBinding(final ParameterizedTypeBinding parameterizedDeclaringClass, final FieldBinding originalField) {
        super(originalField.name, ((originalField.modifiers & 0x4000) != 0x0) ? parameterizedDeclaringClass : (((originalField.modifiers & 0x8) != 0x0) ? originalField.type : Scope.substitute(parameterizedDeclaringClass, originalField.type)), originalField.modifiers, parameterizedDeclaringClass, null);
        this.originalField = originalField;
        this.tagBits = originalField.tagBits;
        this.id = originalField.id;
    }
    
    @Override
    public Constant constant() {
        return this.originalField.constant();
    }
    
    @Override
    public FieldBinding original() {
        return this.originalField.original();
    }
    
    @Override
    public void setConstant(final Constant constant) {
        this.originalField.setConstant(constant);
    }
}
