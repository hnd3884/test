package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;

public class RecoveredLocalVariable extends RecoveredStatement
{
    public RecoveredAnnotation[] annotations;
    public int annotationCount;
    public int modifiers;
    public int modifiersStart;
    public LocalDeclaration localDeclaration;
    public boolean alreadyCompletedLocalInitialization;
    
    public RecoveredLocalVariable(final LocalDeclaration localDeclaration, final RecoveredElement parent, final int bracketBalance) {
        super(localDeclaration, parent, bracketBalance);
        this.localDeclaration = localDeclaration;
        this.alreadyCompletedLocalInitialization = (localDeclaration.initialization != null);
    }
    
    @Override
    public RecoveredElement add(final Statement stmt, final int bracketBalanceValue) {
        if (this.alreadyCompletedLocalInitialization || !(stmt instanceof Expression)) {
            return super.add(stmt, bracketBalanceValue);
        }
        this.alreadyCompletedLocalInitialization = true;
        this.localDeclaration.initialization = (Expression)stmt;
        this.localDeclaration.declarationSourceEnd = stmt.sourceEnd;
        this.localDeclaration.declarationEnd = stmt.sourceEnd;
        return this;
    }
    
    public void attach(final RecoveredAnnotation[] annots, final int annotCount, final int mods, final int modsSourceStart) {
        if (annotCount > 0) {
            final Annotation[] existingAnnotations = this.localDeclaration.annotations;
            if (existingAnnotations != null) {
                this.annotations = new RecoveredAnnotation[annotCount];
                this.annotationCount = 0;
                int i = 0;
            Label_0095:
                while (i < annotCount) {
                    while (true) {
                        for (int j = 0; j < existingAnnotations.length; ++j) {
                            if (annots[i].annotation == existingAnnotations[j]) {
                                ++i;
                                continue Label_0095;
                            }
                        }
                        this.annotations[this.annotationCount++] = annots[i];
                        continue;
                    }
                }
            }
            else {
                this.annotations = annots;
                this.annotationCount = annotCount;
            }
        }
        if (mods != 0) {
            this.modifiers = mods;
            this.modifiersStart = modsSourceStart;
        }
    }
    
    @Override
    public ASTNode parseTree() {
        return this.localDeclaration;
    }
    
    @Override
    public int sourceEnd() {
        return this.localDeclaration.declarationSourceEnd;
    }
    
    @Override
    public String toString(final int tab) {
        return String.valueOf(this.tabString(tab)) + "Recovered local variable:\n" + (Object)this.localDeclaration.print(tab + 1, new StringBuffer(10));
    }
    
    @Override
    public Statement updatedStatement(final int depth, final Set knownTypes) {
        if (this.modifiers != 0) {
            final LocalDeclaration localDeclaration = this.localDeclaration;
            localDeclaration.modifiers |= this.modifiers;
            if (this.modifiersStart < this.localDeclaration.declarationSourceStart) {
                this.localDeclaration.declarationSourceStart = this.modifiersStart;
            }
        }
        if (this.annotationCount > 0) {
            final int existingCount = (this.localDeclaration.annotations == null) ? 0 : this.localDeclaration.annotations.length;
            final Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
            if (existingCount > 0) {
                System.arraycopy(this.localDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
            }
            for (int i = 0; i < this.annotationCount; ++i) {
                annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
            }
            this.localDeclaration.annotations = annotationReferences;
            final int start = this.annotations[0].annotation.sourceStart;
            if (start < this.localDeclaration.declarationSourceStart) {
                this.localDeclaration.declarationSourceStart = start;
            }
        }
        return this.localDeclaration;
    }
    
    @Override
    public RecoveredElement updateOnClosingBrace(final int braceStart, final int braceEnd) {
        if (this.bracketBalance > 0) {
            --this.bracketBalance;
            if (this.bracketBalance == 0) {
                this.alreadyCompletedLocalInitialization = true;
            }
            return this;
        }
        if (this.parent != null) {
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        return this;
    }
    
    @Override
    public RecoveredElement updateOnOpeningBrace(final int braceStart, final int braceEnd) {
        if (this.localDeclaration.declarationSourceEnd == 0 && (this.localDeclaration.type instanceof ArrayTypeReference || this.localDeclaration.type instanceof ArrayQualifiedTypeReference) && !this.alreadyCompletedLocalInitialization) {
            ++this.bracketBalance;
            return null;
        }
        this.updateSourceEndIfNecessary(braceStart - 1, braceEnd - 1);
        return this.parent.updateOnOpeningBrace(braceStart, braceEnd);
    }
    
    @Override
    public void updateParseTree() {
        this.updatedStatement(0, new HashSet());
    }
    
    @Override
    public void updateSourceEndIfNecessary(final int bodyStart, final int bodyEnd) {
        if (this.localDeclaration.declarationSourceEnd == 0) {
            this.localDeclaration.declarationSourceEnd = bodyEnd;
            this.localDeclaration.declarationEnd = bodyEnd;
        }
    }
}
