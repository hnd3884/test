package org.eclipse.jdt.internal.compiler.lookup;

public interface Substitution
{
    TypeBinding substitute(final TypeVariableBinding p0);
    
    LookupEnvironment environment();
    
    boolean isRawSubstitution();
}
