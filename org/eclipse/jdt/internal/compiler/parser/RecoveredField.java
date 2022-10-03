package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayInitializer;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;

public class RecoveredField extends RecoveredElement
{
    public FieldDeclaration fieldDeclaration;
    boolean alreadyCompletedFieldInitialization;
    public RecoveredAnnotation[] annotations;
    public int annotationCount;
    public int modifiers;
    public int modifiersStart;
    public RecoveredType[] anonymousTypes;
    public int anonymousTypeCount;
    
    public RecoveredField(final FieldDeclaration fieldDeclaration, final RecoveredElement parent, final int bracketBalance) {
        this(fieldDeclaration, parent, bracketBalance, null);
    }
    
    public RecoveredField(final FieldDeclaration fieldDeclaration, final RecoveredElement parent, final int bracketBalance, final Parser parser) {
        super(parent, bracketBalance, parser);
        this.fieldDeclaration = fieldDeclaration;
        this.alreadyCompletedFieldInitialization = (fieldDeclaration.initialization != null);
    }
    
    @Override
    public RecoveredElement add(final LocalDeclaration localDeclaration, final int bracketBalanceValue) {
        if (this.lambdaNestLevel > 0) {
            return this;
        }
        return super.add(localDeclaration, bracketBalanceValue);
    }
    
    @Override
    public RecoveredElement add(final FieldDeclaration addedfieldDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.parent == null) {
            return this;
        }
        if (this.fieldDeclaration.declarationSourceStart == addedfieldDeclaration.declarationSourceStart) {
            if (this.fieldDeclaration.initialization != null) {
                this.updateSourceEndIfNecessary(this.fieldDeclaration.initialization.sourceEnd);
            }
            else {
                this.updateSourceEndIfNecessary(this.fieldDeclaration.sourceEnd);
            }
        }
        else {
            this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(addedfieldDeclaration.declarationSourceStart - 1));
        }
        return this.parent.add(addedfieldDeclaration, bracketBalanceValue);
    }
    
    @Override
    public RecoveredElement add(final Statement statement, final int bracketBalanceValue) {
        if (this.alreadyCompletedFieldInitialization || !(statement instanceof Expression)) {
            return super.add(statement, bracketBalanceValue);
        }
        if (statement.sourceEnd > 0) {
            this.alreadyCompletedFieldInitialization = true;
        }
        this.fieldDeclaration.initialization = (Expression)statement;
        this.fieldDeclaration.declarationSourceEnd = statement.sourceEnd;
        this.fieldDeclaration.declarationEnd = statement.sourceEnd;
        return this;
    }
    
    @Override
    public RecoveredElement add(final TypeDeclaration typeDeclaration, final int bracketBalanceValue) {
        if (this.alreadyCompletedFieldInitialization || (typeDeclaration.bits & 0x200) == 0x0 || (this.fieldDeclaration.declarationSourceEnd != 0 && typeDeclaration.sourceStart > this.fieldDeclaration.declarationSourceEnd)) {
            return super.add(typeDeclaration, bracketBalanceValue);
        }
        if (this.anonymousTypes == null) {
            this.anonymousTypes = new RecoveredType[5];
            this.anonymousTypeCount = 0;
        }
        else if (this.anonymousTypeCount == this.anonymousTypes.length) {
            System.arraycopy(this.anonymousTypes, 0, this.anonymousTypes = new RecoveredType[2 * this.anonymousTypeCount], 0, this.anonymousTypeCount);
        }
        final RecoveredType element = new RecoveredType(typeDeclaration, this, bracketBalanceValue);
        return this.anonymousTypes[this.anonymousTypeCount++] = element;
    }
    
    public void attach(final RecoveredAnnotation[] annots, final int annotCount, final int mods, final int modsSourceStart) {
        if (annotCount > 0) {
            final Annotation[] existingAnnotations = this.fieldDeclaration.annotations;
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
        return this.fieldDeclaration;
    }
    
    @Override
    public int sourceEnd() {
        return this.fieldDeclaration.declarationSourceEnd;
    }
    
    @Override
    public String toString(final int tab) {
        final StringBuffer buffer = new StringBuffer(this.tabString(tab));
        buffer.append("Recovered field:\n");
        this.fieldDeclaration.print(tab + 1, buffer);
        if (this.annotations != null) {
            for (int i = 0; i < this.annotationCount; ++i) {
                buffer.append("\n");
                buffer.append(this.annotations[i].toString(tab + 1));
            }
        }
        if (this.anonymousTypes != null) {
            for (int i = 0; i < this.anonymousTypeCount; ++i) {
                buffer.append("\n");
                buffer.append(this.anonymousTypes[i].toString(tab + 1));
            }
        }
        return buffer.toString();
    }
    
    public FieldDeclaration updatedFieldDeclaration(final int depth, final Set<TypeDeclaration> knownTypes) {
        if (this.modifiers != 0) {
            final FieldDeclaration fieldDeclaration = this.fieldDeclaration;
            fieldDeclaration.modifiers |= this.modifiers;
            if (this.modifiersStart < this.fieldDeclaration.declarationSourceStart) {
                this.fieldDeclaration.declarationSourceStart = this.modifiersStart;
            }
        }
        if (this.annotationCount > 0) {
            final int existingCount = (this.fieldDeclaration.annotations == null) ? 0 : this.fieldDeclaration.annotations.length;
            final Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
            if (existingCount > 0) {
                System.arraycopy(this.fieldDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
            }
            for (int i = 0; i < this.annotationCount; ++i) {
                annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
            }
            this.fieldDeclaration.annotations = annotationReferences;
            final int start = this.annotations[0].annotation.sourceStart;
            if (start < this.fieldDeclaration.declarationSourceStart) {
                this.fieldDeclaration.declarationSourceStart = start;
            }
        }
        if (this.anonymousTypes != null) {
            if (this.fieldDeclaration.initialization == null) {
                ArrayInitializer recoveredInitializers = null;
                int recoveredInitializersCount = 0;
                if (this.anonymousTypeCount > 1) {
                    recoveredInitializers = new ArrayInitializer();
                    recoveredInitializers.expressions = new Expression[this.anonymousTypeCount];
                }
                for (int i = 0; i < this.anonymousTypeCount; ++i) {
                    final RecoveredType recoveredType = this.anonymousTypes[i];
                    final TypeDeclaration typeDeclaration = recoveredType.typeDeclaration;
                    if (typeDeclaration.declarationSourceEnd == 0) {
                        typeDeclaration.declarationSourceEnd = this.fieldDeclaration.declarationSourceEnd;
                        typeDeclaration.bodyEnd = this.fieldDeclaration.declarationSourceEnd;
                    }
                    if (recoveredType.preserveContent) {
                        final TypeDeclaration anonymousType = recoveredType.updatedTypeDeclaration(depth + 1, knownTypes);
                        if (anonymousType != null) {
                            if (this.anonymousTypeCount > 1) {
                                if (recoveredInitializersCount == 0) {
                                    this.fieldDeclaration.initialization = recoveredInitializers;
                                }
                                recoveredInitializers.expressions[recoveredInitializersCount++] = anonymousType.allocation;
                            }
                            else {
                                this.fieldDeclaration.initialization = anonymousType.allocation;
                            }
                            final int end = anonymousType.declarationSourceEnd;
                            if (end > this.fieldDeclaration.declarationSourceEnd) {
                                this.fieldDeclaration.declarationSourceEnd = end;
                                this.fieldDeclaration.declarationEnd = end;
                            }
                        }
                    }
                }
                if (this.anonymousTypeCount > 0) {
                    final FieldDeclaration fieldDeclaration2 = this.fieldDeclaration;
                    fieldDeclaration2.bits |= 0x2;
                }
            }
            else if (this.fieldDeclaration.getKind() == 3) {
                for (int j = 0; j < this.anonymousTypeCount; ++j) {
                    final RecoveredType recoveredType2 = this.anonymousTypes[j];
                    final TypeDeclaration typeDeclaration2 = recoveredType2.typeDeclaration;
                    if (typeDeclaration2.declarationSourceEnd == 0) {
                        typeDeclaration2.declarationSourceEnd = this.fieldDeclaration.declarationSourceEnd;
                        typeDeclaration2.bodyEnd = this.fieldDeclaration.declarationSourceEnd;
                    }
                    recoveredType2.updatedTypeDeclaration(depth, knownTypes);
                }
            }
        }
        return this.fieldDeclaration;
    }
    
    @Override
    public RecoveredElement updateOnClosingBrace(final int braceStart, final int braceEnd) {
        if (this.bracketBalance > 0) {
            --this.bracketBalance;
            if (this.bracketBalance == 0) {
                if (this.fieldDeclaration.getKind() == 3) {
                    this.updateSourceEndIfNecessary(braceEnd);
                    return this.parent;
                }
                if (this.fieldDeclaration.declarationSourceEnd > 0) {
                    this.alreadyCompletedFieldInitialization = true;
                }
            }
            return this;
        }
        if (this.bracketBalance == 0) {
            this.alreadyCompletedFieldInitialization = true;
            this.updateSourceEndIfNecessary(braceEnd - 1);
        }
        if (this.parent != null) {
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        return this;
    }
    
    @Override
    public RecoveredElement updateOnOpeningBrace(final int braceStart, final int braceEnd) {
        if (this.fieldDeclaration.declarationSourceEnd == 0) {
            if (!(this.fieldDeclaration.type instanceof ArrayTypeReference) && !(this.fieldDeclaration.type instanceof ArrayQualifiedTypeReference)) {
                ++this.bracketBalance;
                return null;
            }
            if (!this.alreadyCompletedFieldInitialization) {
                ++this.bracketBalance;
                return null;
            }
        }
        if (this.fieldDeclaration.declarationSourceEnd == 0 && this.fieldDeclaration.getKind() == 3) {
            ++this.bracketBalance;
            return null;
        }
        this.updateSourceEndIfNecessary(braceStart - 1, braceEnd - 1);
        return this.parent.updateOnOpeningBrace(braceStart, braceEnd);
    }
    
    @Override
    public void updateParseTree() {
        this.updatedFieldDeclaration(0, new HashSet<TypeDeclaration>());
    }
    
    @Override
    public void updateSourceEndIfNecessary(final int bodyStart, final int bodyEnd) {
        if (this.fieldDeclaration.declarationSourceEnd == 0) {
            this.fieldDeclaration.declarationSourceEnd = bodyEnd;
            this.fieldDeclaration.declarationEnd = bodyEnd;
        }
    }
}
