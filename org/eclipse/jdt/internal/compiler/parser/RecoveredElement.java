package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;

public class RecoveredElement
{
    public RecoveredElement parent;
    public int bracketBalance;
    public boolean foundOpeningBrace;
    protected Parser recoveringParser;
    public int lambdaNestLevel;
    
    public RecoveredElement(final RecoveredElement parent, final int bracketBalance) {
        this(parent, bracketBalance, null);
    }
    
    public RecoveredElement(final RecoveredElement parent, final int bracketBalance, final Parser parser) {
        this.parent = parent;
        this.bracketBalance = bracketBalance;
        this.recoveringParser = parser;
    }
    
    public RecoveredElement addAnnotationName(final int identifierPtr, final int identifierLengthPtr, final int annotationStart, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(annotationStart - 1));
        return this.parent.addAnnotationName(identifierPtr, identifierLengthPtr, annotationStart, bracketBalanceValue);
    }
    
    public RecoveredElement add(final AbstractMethodDeclaration methodDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(methodDeclaration.declarationSourceStart - 1));
        return this.parent.add(methodDeclaration, bracketBalanceValue);
    }
    
    public RecoveredElement add(final Block nestedBlockDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(nestedBlockDeclaration.sourceStart - 1));
        return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
    }
    
    public RecoveredElement add(final FieldDeclaration fieldDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(fieldDeclaration.declarationSourceStart - 1));
        return this.parent.add(fieldDeclaration, bracketBalanceValue);
    }
    
    public RecoveredElement add(final ImportReference importReference, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(importReference.declarationSourceStart - 1));
        return this.parent.add(importReference, bracketBalanceValue);
    }
    
    public RecoveredElement add(final LocalDeclaration localDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(localDeclaration.declarationSourceStart - 1));
        return this.parent.add(localDeclaration, bracketBalanceValue);
    }
    
    public RecoveredElement add(final Statement statement, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        if (this instanceof RecoveredType) {
            final TypeDeclaration typeDeclaration = ((RecoveredType)this).typeDeclaration;
            if (typeDeclaration != null && (typeDeclaration.bits & 0x200) != 0x0 && statement.sourceStart > typeDeclaration.sourceStart && statement.sourceEnd < typeDeclaration.sourceEnd) {
                return this;
            }
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(statement.sourceStart - 1));
        return this.parent.add(statement, bracketBalanceValue);
    }
    
    public RecoveredElement add(final TypeDeclaration typeDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(typeDeclaration.declarationSourceStart - 1));
        return this.parent.add(typeDeclaration, bracketBalanceValue);
    }
    
    protected void addBlockStatement(final RecoveredBlock recoveredBlock) {
        final Block block = recoveredBlock.blockDeclaration;
        if (block.statements != null) {
            final Statement[] statements = block.statements;
            for (int i = 0; i < statements.length; ++i) {
                recoveredBlock.add(statements[i], 0);
            }
        }
    }
    
    public void addModifier(final int flag, final int modifiersSourceStart) {
    }
    
    public int depth() {
        int depth = 0;
        RecoveredElement current = this;
        while ((current = current.parent) != null) {
            ++depth;
        }
        return depth;
    }
    
    public RecoveredInitializer enclosingInitializer() {
        for (RecoveredElement current = this; current != null; current = current.parent) {
            if (current instanceof RecoveredInitializer) {
                return (RecoveredInitializer)current;
            }
        }
        return null;
    }
    
    public RecoveredMethod enclosingMethod() {
        for (RecoveredElement current = this; current != null; current = current.parent) {
            if (current instanceof RecoveredMethod) {
                return (RecoveredMethod)current;
            }
        }
        return null;
    }
    
    public RecoveredType enclosingType() {
        for (RecoveredElement current = this; current != null; current = current.parent) {
            if (current instanceof RecoveredType) {
                return (RecoveredType)current;
            }
        }
        return null;
    }
    
    public Parser parser() {
        for (RecoveredElement current = this; current != null; current = current.parent) {
            if (current.recoveringParser != null) {
                return current.recoveringParser;
            }
        }
        return null;
    }
    
    public ASTNode parseTree() {
        return null;
    }
    
    public void resetPendingModifiers() {
    }
    
    public void preserveEnclosingBlocks() {
        for (RecoveredElement current = this; current != null; current = current.parent) {
            if (current instanceof RecoveredBlock) {
                ((RecoveredBlock)current).preserveContent = true;
            }
            if (current instanceof RecoveredType) {
                ((RecoveredType)current).preserveContent = true;
            }
        }
    }
    
    public int previousAvailableLineEnd(final int position) {
        final Parser parser = this.parser();
        if (parser == null) {
            return position;
        }
        final Scanner scanner = parser.scanner;
        if (scanner.lineEnds == null) {
            return position;
        }
        final int index = Util.getLineNumber(position, scanner.lineEnds, 0, scanner.linePtr);
        if (index < 2) {
            return position;
        }
        final int previousLineEnd = scanner.lineEnds[index - 2];
        final char[] source = scanner.source;
        for (int i = previousLineEnd + 1; i < position; ++i) {
            if (source[i] != ' ' && source[i] != '\t') {
                return position;
            }
        }
        return previousLineEnd;
    }
    
    public int sourceEnd() {
        return 0;
    }
    
    protected String tabString(final int tab) {
        final StringBuffer result = new StringBuffer();
        for (int i = tab; i > 0; --i) {
            result.append("  ");
        }
        return result.toString();
    }
    
    public RecoveredElement topElement() {
        RecoveredElement current;
        for (current = this; current.parent != null; current = current.parent) {}
        return current;
    }
    
    @Override
    public String toString() {
        return this.toString(0);
    }
    
    public String toString(final int tab) {
        return super.toString();
    }
    
    public RecoveredType type() {
        for (RecoveredElement current = this; current != null; current = current.parent) {
            if (current instanceof RecoveredType) {
                return (RecoveredType)current;
            }
        }
        return null;
    }
    
    public void updateBodyStart(final int bodyStart) {
        this.foundOpeningBrace = true;
    }
    
    public void updateFromParserState() {
    }
    
    public RecoveredElement updateOnClosingBrace(final int braceStart, final int braceEnd) {
        final int bracketBalance = this.bracketBalance - 1;
        this.bracketBalance = bracketBalance;
        if (bracketBalance <= 0 && this.parent != null) {
            this.updateSourceEndIfNecessary(braceStart, braceEnd);
            return this.parent;
        }
        return this;
    }
    
    public RecoveredElement updateOnOpeningBrace(final int braceStart, final int braceEnd) {
        if (this.bracketBalance++ == 0) {
            this.updateBodyStart(braceEnd + 1);
            return this;
        }
        return null;
    }
    
    public void updateParseTree() {
    }
    
    public void updateSourceEndIfNecessary(final int braceStart, final int braceEnd) {
    }
    
    public void updateSourceEndIfNecessary(final int sourceEnd) {
        this.updateSourceEndIfNecessary(sourceEnd + 1, sourceEnd);
    }
}
