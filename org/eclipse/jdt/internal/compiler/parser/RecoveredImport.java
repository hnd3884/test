package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;

public class RecoveredImport extends RecoveredElement
{
    public ImportReference importReference;
    
    public RecoveredImport(final ImportReference importReference, final RecoveredElement parent, final int bracketBalance) {
        super(parent, bracketBalance);
        this.importReference = importReference;
    }
    
    @Override
    public ASTNode parseTree() {
        return this.importReference;
    }
    
    @Override
    public int sourceEnd() {
        return this.importReference.declarationSourceEnd;
    }
    
    @Override
    public String toString(final int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered import: " + this.importReference.toString();
    }
    
    public ImportReference updatedImportReference() {
        return this.importReference;
    }
    
    @Override
    public void updateParseTree() {
        this.updatedImportReference();
    }
    
    @Override
    public void updateSourceEndIfNecessary(final int bodyStart, final int bodyEnd) {
        if (this.importReference.declarationSourceEnd == 0) {
            this.importReference.declarationSourceEnd = bodyEnd;
            this.importReference.declarationEnd = bodyEnd;
        }
    }
}
