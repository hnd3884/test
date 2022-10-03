package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Statement;

public class RecoveredStatement extends RecoveredElement
{
    public Statement statement;
    
    public RecoveredStatement(final Statement statement, final RecoveredElement parent, final int bracketBalance) {
        super(parent, bracketBalance);
        this.statement = statement;
    }
    
    @Override
    public ASTNode parseTree() {
        return this.statement;
    }
    
    @Override
    public int sourceEnd() {
        return this.statement.sourceEnd;
    }
    
    @Override
    public String toString(final int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered statement:\n" + (Object)this.statement.print(tab + 1, new StringBuffer(10));
    }
    
    public Statement updatedStatement(final int depth, final Set<TypeDeclaration> knownTypes) {
        return this.statement;
    }
    
    @Override
    public void updateParseTree() {
        this.updatedStatement(0, new HashSet<TypeDeclaration>());
    }
    
    @Override
    public void updateSourceEndIfNecessary(final int bodyStart, final int bodyEnd) {
        if (this.statement.sourceEnd == 0) {
            this.statement.sourceEnd = bodyEnd;
        }
    }
    
    @Override
    public RecoveredElement updateOnClosingBrace(final int braceStart, final int braceEnd) {
        final int bracketBalance = this.bracketBalance - 1;
        this.bracketBalance = bracketBalance;
        if (bracketBalance <= 0 && this.parent != null) {
            this.updateSourceEndIfNecessary(braceStart, braceEnd);
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        return this;
    }
}
