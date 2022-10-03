package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.Block;

public class RecoveredBlock extends RecoveredStatement implements TerminalTokens
{
    public Block blockDeclaration;
    public RecoveredStatement[] statements;
    public int statementCount;
    public boolean preserveContent;
    public RecoveredLocalVariable pendingArgument;
    int pendingModifiers;
    int pendingModifersSourceStart;
    RecoveredAnnotation[] pendingAnnotations;
    int pendingAnnotationCount;
    
    public RecoveredBlock(final Block block, final RecoveredElement parent, final int bracketBalance) {
        super(block, parent, bracketBalance);
        this.preserveContent = false;
        this.pendingModifersSourceStart = -1;
        this.blockDeclaration = block;
        this.foundOpeningBrace = true;
        this.preserveContent = (this.parser().methodRecoveryActivated || this.parser().statementRecoveryActivated);
    }
    
    @Override
    public RecoveredElement add(final AbstractMethodDeclaration methodDeclaration, final int bracketBalanceValue) {
        if (this.parent != null && this.parent instanceof RecoveredMethod) {
            final RecoveredMethod enclosingRecoveredMethod = (RecoveredMethod)this.parent;
            if (enclosingRecoveredMethod.methodBody == this && enclosingRecoveredMethod.parent == null) {
                this.resetPendingModifiers();
                return this;
            }
        }
        return super.add(methodDeclaration, bracketBalanceValue);
    }
    
    @Override
    public RecoveredElement add(final Block nestedBlockDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.blockDeclaration.sourceEnd != 0 && nestedBlockDeclaration.sourceStart > this.blockDeclaration.sourceEnd) {
            return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
        }
        final RecoveredBlock element = new RecoveredBlock(nestedBlockDeclaration, this, bracketBalanceValue);
        if (this.pendingArgument != null) {
            element.attach(this.pendingArgument);
            this.pendingArgument = null;
        }
        if (this.parser().statementRecoveryActivated) {
            this.addBlockStatement(element);
        }
        this.attach(element);
        if (nestedBlockDeclaration.sourceEnd == 0) {
            return element;
        }
        return this;
    }
    
    @Override
    public RecoveredElement add(final LocalDeclaration localDeclaration, final int bracketBalanceValue) {
        return this.add(localDeclaration, bracketBalanceValue, false);
    }
    
    public RecoveredElement add(final LocalDeclaration localDeclaration, final int bracketBalanceValue, final boolean delegatedByParent) {
        if (localDeclaration.isRecoveredFromLoneIdentifier()) {
            return this;
        }
        if (this.blockDeclaration.sourceEnd != 0 && localDeclaration.declarationSourceStart > this.blockDeclaration.sourceEnd) {
            this.resetPendingModifiers();
            if (delegatedByParent) {
                return this;
            }
            return this.parent.add(localDeclaration, bracketBalanceValue);
        }
        else {
            final RecoveredLocalVariable element = new RecoveredLocalVariable(localDeclaration, this, bracketBalanceValue);
            if (this.pendingAnnotationCount > 0) {
                element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
            }
            this.resetPendingModifiers();
            if (localDeclaration instanceof Argument) {
                this.pendingArgument = element;
                return this;
            }
            this.attach(element);
            if (localDeclaration.declarationSourceEnd == 0) {
                return element;
            }
            return this;
        }
    }
    
    @Override
    public RecoveredElement add(final Statement stmt, final int bracketBalanceValue) {
        return this.add(stmt, bracketBalanceValue, false);
    }
    
    public RecoveredElement add(final Statement stmt, final int bracketBalanceValue, final boolean delegatedByParent) {
        this.resetPendingModifiers();
        if (this.blockDeclaration.sourceEnd != 0 && stmt.sourceStart > this.blockDeclaration.sourceEnd) {
            if (delegatedByParent) {
                return this;
            }
            return this.parent.add(stmt, bracketBalanceValue);
        }
        else {
            final RecoveredStatement element = new RecoveredStatement(stmt, this, bracketBalanceValue);
            this.attach(element);
            if (stmt.sourceEnd == 0) {
                return element;
            }
            return this;
        }
    }
    
    @Override
    public RecoveredElement add(final TypeDeclaration typeDeclaration, final int bracketBalanceValue) {
        return this.add(typeDeclaration, bracketBalanceValue, false);
    }
    
    public RecoveredElement add(final TypeDeclaration typeDeclaration, final int bracketBalanceValue, final boolean delegatedByParent) {
        if (this.blockDeclaration.sourceEnd != 0 && typeDeclaration.declarationSourceStart > this.blockDeclaration.sourceEnd) {
            this.resetPendingModifiers();
            if (delegatedByParent) {
                return this;
            }
            return this.parent.add(typeDeclaration, bracketBalanceValue);
        }
        else {
            final RecoveredType element = new RecoveredType(typeDeclaration, this, bracketBalanceValue);
            if (this.pendingAnnotationCount > 0) {
                element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
            }
            this.resetPendingModifiers();
            this.attach(element);
            if (typeDeclaration.declarationSourceEnd == 0) {
                return element;
            }
            return this;
        }
    }
    
    @Override
    public RecoveredElement addAnnotationName(final int identifierPtr, final int identifierLengthPtr, final int annotationStart, final int bracketBalanceValue) {
        if (this.pendingAnnotations == null) {
            this.pendingAnnotations = new RecoveredAnnotation[5];
            this.pendingAnnotationCount = 0;
        }
        else if (this.pendingAnnotationCount == this.pendingAnnotations.length) {
            System.arraycopy(this.pendingAnnotations, 0, this.pendingAnnotations = new RecoveredAnnotation[2 * this.pendingAnnotationCount], 0, this.pendingAnnotationCount);
        }
        final RecoveredAnnotation element = new RecoveredAnnotation(identifierPtr, identifierLengthPtr, annotationStart, this, bracketBalanceValue);
        return this.pendingAnnotations[this.pendingAnnotationCount++] = element;
    }
    
    @Override
    public void addModifier(final int flag, final int modifiersSourceStart) {
        this.pendingModifiers |= flag;
        if (this.pendingModifersSourceStart < 0) {
            this.pendingModifersSourceStart = modifiersSourceStart;
        }
    }
    
    void attach(final RecoveredStatement recoveredStatement) {
        if (this.statements == null) {
            this.statements = new RecoveredStatement[5];
            this.statementCount = 0;
        }
        else if (this.statementCount == this.statements.length) {
            System.arraycopy(this.statements, 0, this.statements = new RecoveredStatement[2 * this.statementCount], 0, this.statementCount);
        }
        this.statements[this.statementCount++] = recoveredStatement;
    }
    
    void attachPendingModifiers(final RecoveredAnnotation[] pendingAnnots, final int pendingAnnotCount, final int pendingMods, final int pendingModsSourceStart) {
        this.pendingAnnotations = pendingAnnots;
        this.pendingAnnotationCount = pendingAnnotCount;
        this.pendingModifiers = pendingMods;
        this.pendingModifersSourceStart = pendingModsSourceStart;
    }
    
    @Override
    public ASTNode parseTree() {
        return this.blockDeclaration;
    }
    
    @Override
    public void resetPendingModifiers() {
        this.pendingAnnotations = null;
        this.pendingAnnotationCount = 0;
        this.pendingModifiers = 0;
        this.pendingModifersSourceStart = -1;
    }
    
    @Override
    public String toString(final int tab) {
        final StringBuffer result = new StringBuffer(this.tabString(tab));
        result.append("Recovered block:\n");
        this.blockDeclaration.print(tab + 1, result);
        if (this.statements != null) {
            for (int i = 0; i < this.statementCount; ++i) {
                result.append("\n");
                result.append(this.statements[i].toString(tab + 1));
            }
        }
        return result.toString();
    }
    
    public Block updatedBlock(final int depth, final Set<TypeDeclaration> knownTypes) {
        if (!this.preserveContent || this.statementCount == 0) {
            return null;
        }
        final Statement[] updatedStatements = new Statement[this.statementCount];
        int updatedCount = 0;
        final RecoveredStatement lastStatement = this.statements[this.statementCount - 1];
        final RecoveredMethod enclosingMethod = this.enclosingMethod();
        final RecoveredInitializer enclosingIntializer = this.enclosingInitializer();
        int bodyEndValue = 0;
        if (enclosingMethod != null) {
            bodyEndValue = enclosingMethod.methodDeclaration.bodyEnd;
            if (enclosingIntializer != null && enclosingMethod.methodDeclaration.sourceStart < enclosingIntializer.fieldDeclaration.sourceStart) {
                bodyEndValue = enclosingIntializer.fieldDeclaration.declarationSourceEnd;
            }
        }
        else if (enclosingIntializer != null) {
            bodyEndValue = enclosingIntializer.fieldDeclaration.declarationSourceEnd;
        }
        else {
            bodyEndValue = this.blockDeclaration.sourceEnd - 1;
        }
        if (lastStatement instanceof RecoveredLocalVariable) {
            final RecoveredLocalVariable lastLocalVariable = (RecoveredLocalVariable)lastStatement;
            if (lastLocalVariable.localDeclaration.declarationSourceEnd == 0) {
                lastLocalVariable.localDeclaration.declarationSourceEnd = bodyEndValue;
                lastLocalVariable.localDeclaration.declarationEnd = bodyEndValue;
            }
        }
        else if (lastStatement instanceof RecoveredBlock) {
            final RecoveredBlock lastBlock = (RecoveredBlock)lastStatement;
            if (lastBlock.blockDeclaration.sourceEnd == 0) {
                lastBlock.blockDeclaration.sourceEnd = bodyEndValue;
            }
        }
        else if (!(lastStatement instanceof RecoveredType) && lastStatement.statement.sourceEnd == 0) {
            lastStatement.statement.sourceEnd = bodyEndValue;
        }
        int lastEnd = this.blockDeclaration.sourceStart;
    Label_0461:
        for (int i = 0; i < this.statementCount; ++i) {
            final Statement updatedStatement = this.statements[i].updatedStatement(depth, knownTypes);
            if (updatedStatement != null) {
                for (int j = 0; j < i; ++j) {
                    if (updatedStatements[j] instanceof LocalDeclaration) {
                        final LocalDeclaration local = (LocalDeclaration)updatedStatements[j];
                        if (local.initialization != null && updatedStatement.sourceStart >= local.initialization.sourceStart && updatedStatement.sourceEnd <= local.initialization.sourceEnd) {
                            continue Label_0461;
                        }
                    }
                }
                updatedStatements[updatedCount++] = updatedStatement;
                if (updatedStatement instanceof LocalDeclaration) {
                    final LocalDeclaration localDeclaration = (LocalDeclaration)updatedStatement;
                    if (localDeclaration.declarationSourceEnd > lastEnd) {
                        lastEnd = localDeclaration.declarationSourceEnd;
                    }
                }
                else if (updatedStatement instanceof TypeDeclaration) {
                    final TypeDeclaration typeDeclaration = (TypeDeclaration)updatedStatement;
                    if (typeDeclaration.declarationSourceEnd > lastEnd) {
                        lastEnd = typeDeclaration.declarationSourceEnd;
                    }
                }
                else if (updatedStatement.sourceEnd > lastEnd) {
                    lastEnd = updatedStatement.sourceEnd;
                }
            }
        }
        if (updatedCount == 0) {
            return null;
        }
        if (updatedCount != this.statementCount) {
            System.arraycopy(updatedStatements, 0, this.blockDeclaration.statements = new Statement[updatedCount], 0, updatedCount);
        }
        else {
            this.blockDeclaration.statements = updatedStatements;
        }
        if (this.blockDeclaration.sourceEnd == 0) {
            if (lastEnd < bodyEndValue) {
                this.blockDeclaration.sourceEnd = bodyEndValue;
            }
            else {
                this.blockDeclaration.sourceEnd = lastEnd;
            }
        }
        return this.blockDeclaration;
    }
    
    @Override
    public Statement updatedStatement(final int depth, final Set<TypeDeclaration> knownTypes) {
        return this.updatedBlock(depth, knownTypes);
    }
    
    @Override
    public RecoveredElement updateOnClosingBrace(final int braceStart, final int braceEnd) {
        final int bracketBalance = this.bracketBalance - 1;
        this.bracketBalance = bracketBalance;
        if (bracketBalance > 0 || this.parent == null) {
            return this;
        }
        this.updateSourceEndIfNecessary(braceStart, braceEnd);
        final RecoveredMethod method = this.enclosingMethod();
        if (method != null && method.methodBody == this) {
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        final RecoveredInitializer initializer = this.enclosingInitializer();
        if (initializer != null && initializer.initializerBody == this) {
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        return this.parent;
    }
    
    @Override
    public RecoveredElement updateOnOpeningBrace(final int braceStart, final int braceEnd) {
        final Block block = new Block(0);
        block.sourceStart = this.parser().scanner.startPosition;
        return this.add(block, 1);
    }
    
    @Override
    public void updateParseTree() {
        this.updatedBlock(0, new HashSet<TypeDeclaration>());
    }
    
    @Override
    public RecoveredElement add(final FieldDeclaration fieldDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        final char[][] fieldTypeName;
        if ((fieldDeclaration.modifiers & 0xFFFFFFEF) != 0x0 || fieldDeclaration.type == null || ((fieldTypeName = fieldDeclaration.type.getTypeName()).length == 1 && CharOperation.equals(fieldTypeName[0], TypeBinding.VOID.sourceName()))) {
            this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(fieldDeclaration.declarationSourceStart - 1));
            return this.parent.add(fieldDeclaration, bracketBalanceValue);
        }
        if (this.blockDeclaration.sourceEnd != 0 && fieldDeclaration.declarationSourceStart > this.blockDeclaration.sourceEnd) {
            return this.parent.add(fieldDeclaration, bracketBalanceValue);
        }
        return this;
    }
}
