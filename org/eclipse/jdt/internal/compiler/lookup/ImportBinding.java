package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;

public class ImportBinding extends Binding
{
    public char[][] compoundName;
    public boolean onDemand;
    public ImportReference reference;
    public Binding resolvedImport;
    
    public ImportBinding(final char[][] compoundName, final boolean isOnDemand, final Binding binding, final ImportReference reference) {
        this.compoundName = compoundName;
        this.onDemand = isOnDemand;
        this.resolvedImport = binding;
        this.reference = reference;
    }
    
    @Override
    public final int kind() {
        return 32;
    }
    
    public boolean isStatic() {
        return this.reference != null && this.reference.isStatic();
    }
    
    @Override
    public char[] readableName() {
        if (this.onDemand) {
            return CharOperation.concat(CharOperation.concatWith(this.compoundName, '.'), ".*".toCharArray());
        }
        return CharOperation.concatWith(this.compoundName, '.');
    }
    
    @Override
    public String toString() {
        return "import : " + new String(this.readableName());
    }
}
