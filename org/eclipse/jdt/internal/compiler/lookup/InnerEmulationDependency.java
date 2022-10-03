package org.eclipse.jdt.internal.compiler.lookup;

public class InnerEmulationDependency
{
    public BlockScope scope;
    public boolean wasEnclosingInstanceSupplied;
    
    public InnerEmulationDependency(final BlockScope scope, final boolean wasEnclosingInstanceSupplied) {
        this.scope = scope;
        this.wasEnclosingInstanceSupplied = wasEnclosingInstanceSupplied;
    }
}
