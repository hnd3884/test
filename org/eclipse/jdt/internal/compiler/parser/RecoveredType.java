package org.eclipse.jdt.internal.compiler.parser;

import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Initializer;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

public class RecoveredType extends RecoveredStatement implements TerminalTokens
{
    public static final int MAX_TYPE_DEPTH = 256;
    public TypeDeclaration typeDeclaration;
    public RecoveredAnnotation[] annotations;
    public int annotationCount;
    public int modifiers;
    public int modifiersStart;
    public RecoveredType[] memberTypes;
    public int memberTypeCount;
    public RecoveredField[] fields;
    public int fieldCount;
    public RecoveredMethod[] methods;
    public int methodCount;
    public boolean preserveContent;
    public int bodyEnd;
    public boolean insideEnumConstantPart;
    public TypeParameter[] pendingTypeParameters;
    public int pendingTypeParametersStart;
    int pendingModifiers;
    int pendingModifersSourceStart;
    RecoveredAnnotation[] pendingAnnotations;
    int pendingAnnotationCount;
    
    public RecoveredType(final TypeDeclaration typeDeclaration, final RecoveredElement parent, final int bracketBalance) {
        super(typeDeclaration, parent, bracketBalance);
        this.preserveContent = false;
        this.insideEnumConstantPart = false;
        this.pendingModifersSourceStart = -1;
        this.typeDeclaration = typeDeclaration;
        if (typeDeclaration.allocation != null && typeDeclaration.allocation.type == null) {
            this.foundOpeningBrace = true;
        }
        else {
            this.foundOpeningBrace = !this.bodyStartsAtHeaderEnd();
        }
        this.insideEnumConstantPart = (TypeDeclaration.kind(typeDeclaration.modifiers) == 3);
        if (this.foundOpeningBrace) {
            ++this.bracketBalance;
        }
        this.preserveContent = (this.parser().methodRecoveryActivated || this.parser().statementRecoveryActivated);
    }
    
    @Override
    public RecoveredElement add(final AbstractMethodDeclaration methodDeclaration, final int bracketBalanceValue) {
        if (this.typeDeclaration.declarationSourceEnd != 0 && methodDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd) {
            this.pendingTypeParameters = null;
            this.resetPendingModifiers();
            return this.parent.add(methodDeclaration, bracketBalanceValue);
        }
        if (this.methods == null) {
            this.methods = new RecoveredMethod[5];
            this.methodCount = 0;
        }
        else if (this.methodCount == this.methods.length) {
            System.arraycopy(this.methods, 0, this.methods = new RecoveredMethod[2 * this.methodCount], 0, this.methodCount);
        }
        final RecoveredMethod element = new RecoveredMethod(methodDeclaration, this, bracketBalanceValue, this.recoveringParser);
        this.methods[this.methodCount++] = element;
        if (this.pendingTypeParameters != null) {
            element.attach(this.pendingTypeParameters, this.pendingTypeParametersStart);
            this.pendingTypeParameters = null;
        }
        if (this.pendingAnnotationCount > 0 || this.pendingModifiers != 0) {
            element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        this.insideEnumConstantPart = false;
        if (!this.foundOpeningBrace) {
            this.foundOpeningBrace = true;
            ++this.bracketBalance;
        }
        if (methodDeclaration.declarationSourceEnd == 0) {
            return element;
        }
        return this;
    }
    
    @Override
    public RecoveredElement add(final Block nestedBlockDeclaration, final int bracketBalanceValue) {
        this.pendingTypeParameters = null;
        this.resetPendingModifiers();
        int mods = 0;
        if (this.parser().recoveredStaticInitializerStart != 0) {
            mods = 8;
        }
        return this.add(new Initializer(nestedBlockDeclaration, mods), bracketBalanceValue);
    }
    
    @Override
    public RecoveredElement add(final FieldDeclaration fieldDeclaration, final int bracketBalanceValue) {
        this.pendingTypeParameters = null;
        if (this.typeDeclaration.declarationSourceEnd != 0 && fieldDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd) {
            this.resetPendingModifiers();
            return this.parent.add(fieldDeclaration, bracketBalanceValue);
        }
        if (this.fields == null) {
            this.fields = new RecoveredField[5];
            this.fieldCount = 0;
        }
        else if (this.fieldCount == this.fields.length) {
            System.arraycopy(this.fields, 0, this.fields = new RecoveredField[2 * this.fieldCount], 0, this.fieldCount);
        }
        RecoveredField element = null;
        switch (fieldDeclaration.getKind()) {
            case 1:
            case 3: {
                element = new RecoveredField(fieldDeclaration, this, bracketBalanceValue);
                break;
            }
            case 2: {
                element = new RecoveredInitializer(fieldDeclaration, this, bracketBalanceValue);
                break;
            }
            default: {
                return this;
            }
        }
        this.fields[this.fieldCount++] = element;
        if (this.pendingAnnotationCount > 0) {
            element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
        }
        this.resetPendingModifiers();
        if (!this.foundOpeningBrace) {
            this.foundOpeningBrace = true;
            ++this.bracketBalance;
        }
        if (fieldDeclaration.declarationSourceEnd == 0) {
            return element;
        }
        return this;
    }
    
    @Override
    public RecoveredElement add(final TypeDeclaration memberTypeDeclaration, final int bracketBalanceValue) {
        this.pendingTypeParameters = null;
        if (this.typeDeclaration.declarationSourceEnd != 0 && memberTypeDeclaration.declarationSourceStart > this.typeDeclaration.declarationSourceEnd) {
            this.resetPendingModifiers();
            return this.parent.add(memberTypeDeclaration, bracketBalanceValue);
        }
        this.insideEnumConstantPart = false;
        if ((memberTypeDeclaration.bits & 0x200) != 0x0) {
            if (this.methodCount > 0) {
                final RecoveredMethod lastMethod = this.methods[this.methodCount - 1];
                lastMethod.methodDeclaration.bodyEnd = 0;
                lastMethod.methodDeclaration.declarationSourceEnd = 0;
                final RecoveredMethod recoveredMethod = lastMethod;
                ++recoveredMethod.bracketBalance;
                this.resetPendingModifiers();
                return lastMethod.add(memberTypeDeclaration, bracketBalanceValue);
            }
            return this;
        }
        else {
            if (this.memberTypes == null) {
                this.memberTypes = new RecoveredType[5];
                this.memberTypeCount = 0;
            }
            else if (this.memberTypeCount == this.memberTypes.length) {
                System.arraycopy(this.memberTypes, 0, this.memberTypes = new RecoveredType[2 * this.memberTypeCount], 0, this.memberTypeCount);
            }
            final RecoveredType element = new RecoveredType(memberTypeDeclaration, this, bracketBalanceValue);
            this.memberTypes[this.memberTypeCount++] = element;
            if (this.pendingAnnotationCount > 0) {
                element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
            }
            this.resetPendingModifiers();
            if (!this.foundOpeningBrace) {
                this.foundOpeningBrace = true;
                ++this.bracketBalance;
            }
            if (memberTypeDeclaration.declarationSourceEnd == 0) {
                return element;
            }
            return this;
        }
    }
    
    public void add(final TypeParameter[] parameters, final int startPos) {
        this.pendingTypeParameters = parameters;
        this.pendingTypeParametersStart = startPos;
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
    
    public void attach(final RecoveredAnnotation[] annots, final int annotCount, final int mods, final int modsSourceStart) {
        if (annotCount > 0) {
            final Annotation[] existingAnnotations = this.typeDeclaration.annotations;
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
    
    public int bodyEnd() {
        if (this.bodyEnd == 0) {
            return this.typeDeclaration.declarationSourceEnd;
        }
        return this.bodyEnd;
    }
    
    public boolean bodyStartsAtHeaderEnd() {
        if (this.typeDeclaration.superInterfaces != null) {
            return this.typeDeclaration.bodyStart == this.typeDeclaration.superInterfaces[this.typeDeclaration.superInterfaces.length - 1].sourceEnd + 1;
        }
        if (this.typeDeclaration.superclass != null) {
            return this.typeDeclaration.bodyStart == this.typeDeclaration.superclass.sourceEnd + 1;
        }
        if (this.typeDeclaration.typeParameters == null) {
            return this.typeDeclaration.bodyStart == this.typeDeclaration.sourceEnd + 1;
        }
        return this.typeDeclaration.bodyStart == this.typeDeclaration.typeParameters[this.typeDeclaration.typeParameters.length - 1].sourceEnd + 1;
    }
    
    @Override
    public RecoveredType enclosingType() {
        for (RecoveredElement current = this.parent; current != null; current = current.parent) {
            if (current instanceof RecoveredType) {
                return (RecoveredType)current;
            }
        }
        return null;
    }
    
    public int lastMemberEnd() {
        int lastMemberEnd = this.typeDeclaration.bodyStart;
        if (this.fieldCount > 0) {
            final FieldDeclaration lastField = this.fields[this.fieldCount - 1].fieldDeclaration;
            if (lastMemberEnd < lastField.declarationSourceEnd && lastField.declarationSourceEnd != 0) {
                lastMemberEnd = lastField.declarationSourceEnd;
            }
        }
        if (this.methodCount > 0) {
            final AbstractMethodDeclaration lastMethod = this.methods[this.methodCount - 1].methodDeclaration;
            if (lastMemberEnd < lastMethod.declarationSourceEnd && lastMethod.declarationSourceEnd != 0) {
                lastMemberEnd = lastMethod.declarationSourceEnd;
            }
        }
        if (this.memberTypeCount > 0) {
            final TypeDeclaration lastType = this.memberTypes[this.memberTypeCount - 1].typeDeclaration;
            if (lastMemberEnd < lastType.declarationSourceEnd && lastType.declarationSourceEnd != 0) {
                lastMemberEnd = lastType.declarationSourceEnd;
            }
        }
        return lastMemberEnd;
    }
    
    public char[] name() {
        return this.typeDeclaration.name;
    }
    
    @Override
    public ASTNode parseTree() {
        return this.typeDeclaration;
    }
    
    @Override
    public void resetPendingModifiers() {
        this.pendingAnnotations = null;
        this.pendingAnnotationCount = 0;
        this.pendingModifiers = 0;
        this.pendingModifersSourceStart = -1;
    }
    
    @Override
    public int sourceEnd() {
        return this.typeDeclaration.declarationSourceEnd;
    }
    
    @Override
    public String toString(final int tab) {
        final StringBuffer result = new StringBuffer(this.tabString(tab));
        result.append("Recovered type:\n");
        if ((this.typeDeclaration.bits & 0x200) != 0x0) {
            result.append(this.tabString(tab));
            result.append(" ");
        }
        this.typeDeclaration.print(tab + 1, result);
        if (this.annotations != null) {
            for (int i = 0; i < this.annotationCount; ++i) {
                result.append("\n");
                result.append(this.annotations[i].toString(tab + 1));
            }
        }
        if (this.memberTypes != null) {
            for (int i = 0; i < this.memberTypeCount; ++i) {
                result.append("\n");
                result.append(this.memberTypes[i].toString(tab + 1));
            }
        }
        if (this.fields != null) {
            for (int i = 0; i < this.fieldCount; ++i) {
                result.append("\n");
                result.append(this.fields[i].toString(tab + 1));
            }
        }
        if (this.methods != null) {
            for (int i = 0; i < this.methodCount; ++i) {
                result.append("\n");
                result.append(this.methods[i].toString(tab + 1));
            }
        }
        return result.toString();
    }
    
    @Override
    public void updateBodyStart(final int bodyStart) {
        this.foundOpeningBrace = true;
        this.typeDeclaration.bodyStart = bodyStart;
    }
    
    @Override
    public Statement updatedStatement(final int depth, final Set knownTypes) {
        if ((this.typeDeclaration.bits & 0x200) != 0x0 && !this.preserveContent) {
            return null;
        }
        final TypeDeclaration updatedType = this.updatedTypeDeclaration(depth + 1, knownTypes);
        if (updatedType != null && (updatedType.bits & 0x200) != 0x0) {
            final QualifiedAllocationExpression allocation = updatedType.allocation;
            if (allocation.statementEnd == -1) {
                allocation.statementEnd = updatedType.declarationSourceEnd;
            }
            return allocation;
        }
        return updatedType;
    }
    
    public TypeDeclaration updatedTypeDeclaration(final int depth, final Set<TypeDeclaration> knownTypes) {
        if (depth >= 256) {
            return null;
        }
        if (knownTypes.contains(this.typeDeclaration)) {
            return null;
        }
        knownTypes.add(this.typeDeclaration);
        int lastEnd = this.typeDeclaration.bodyStart;
        if (this.modifiers != 0) {
            final TypeDeclaration typeDeclaration = this.typeDeclaration;
            typeDeclaration.modifiers |= this.modifiers;
            if (this.modifiersStart < this.typeDeclaration.declarationSourceStart) {
                this.typeDeclaration.declarationSourceStart = this.modifiersStart;
            }
        }
        if (this.annotationCount > 0) {
            final int existingCount = (this.typeDeclaration.annotations == null) ? 0 : this.typeDeclaration.annotations.length;
            final Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
            if (existingCount > 0) {
                System.arraycopy(this.typeDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
            }
            for (int i = 0; i < this.annotationCount; ++i) {
                annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
            }
            this.typeDeclaration.annotations = annotationReferences;
            final int start = this.annotations[0].annotation.sourceStart;
            if (start < this.typeDeclaration.declarationSourceStart) {
                this.typeDeclaration.declarationSourceStart = start;
            }
        }
        if (this.memberTypeCount > 0) {
            final int existingCount = (this.typeDeclaration.memberTypes == null) ? 0 : this.typeDeclaration.memberTypes.length;
            TypeDeclaration[] memberTypeDeclarations = new TypeDeclaration[existingCount + this.memberTypeCount];
            if (existingCount > 0) {
                System.arraycopy(this.typeDeclaration.memberTypes, 0, memberTypeDeclarations, 0, existingCount);
            }
            if (this.memberTypes[this.memberTypeCount - 1].typeDeclaration.declarationSourceEnd == 0) {
                final int bodyEndValue = this.bodyEnd();
                this.memberTypes[this.memberTypeCount - 1].typeDeclaration.declarationSourceEnd = bodyEndValue;
                this.memberTypes[this.memberTypeCount - 1].typeDeclaration.bodyEnd = bodyEndValue;
            }
            int updatedCount = 0;
            for (int j = 0; j < this.memberTypeCount; ++j) {
                final TypeDeclaration updatedTypeDeclaration = this.memberTypes[j].updatedTypeDeclaration(depth + 1, knownTypes);
                if (updatedTypeDeclaration != null) {
                    memberTypeDeclarations[existingCount + updatedCount++] = updatedTypeDeclaration;
                }
            }
            if (updatedCount < this.memberTypeCount) {
                final int length = existingCount + updatedCount;
                System.arraycopy(memberTypeDeclarations, 0, memberTypeDeclarations = new TypeDeclaration[length], 0, length);
            }
            if (memberTypeDeclarations.length > 0) {
                this.typeDeclaration.memberTypes = memberTypeDeclarations;
                if (memberTypeDeclarations[memberTypeDeclarations.length - 1].declarationSourceEnd > lastEnd) {
                    lastEnd = memberTypeDeclarations[memberTypeDeclarations.length - 1].declarationSourceEnd;
                }
            }
        }
        if (this.fieldCount > 0) {
            final int existingCount = (this.typeDeclaration.fields == null) ? 0 : this.typeDeclaration.fields.length;
            final FieldDeclaration[] fieldDeclarations = new FieldDeclaration[existingCount + this.fieldCount];
            if (existingCount > 0) {
                System.arraycopy(this.typeDeclaration.fields, 0, fieldDeclarations, 0, existingCount);
            }
            if (this.fields[this.fieldCount - 1].fieldDeclaration.declarationSourceEnd == 0) {
                final int temp = this.bodyEnd();
                this.fields[this.fieldCount - 1].fieldDeclaration.declarationSourceEnd = temp;
                this.fields[this.fieldCount - 1].fieldDeclaration.declarationEnd = temp;
            }
            for (int i = 0; i < this.fieldCount; ++i) {
                fieldDeclarations[existingCount + i] = this.fields[i].updatedFieldDeclaration(depth, knownTypes);
            }
            for (int i = this.fieldCount - 1; i > 0; --i) {
                if (fieldDeclarations[existingCount + i - 1].declarationSourceStart == fieldDeclarations[existingCount + i].declarationSourceStart) {
                    fieldDeclarations[existingCount + i - 1].declarationSourceEnd = fieldDeclarations[existingCount + i].declarationSourceEnd;
                    fieldDeclarations[existingCount + i - 1].declarationEnd = fieldDeclarations[existingCount + i].declarationEnd;
                }
            }
            this.typeDeclaration.fields = fieldDeclarations;
            if (fieldDeclarations[fieldDeclarations.length - 1].declarationSourceEnd > lastEnd) {
                lastEnd = fieldDeclarations[fieldDeclarations.length - 1].declarationSourceEnd;
            }
        }
        final int existingCount = (this.typeDeclaration.methods == null) ? 0 : this.typeDeclaration.methods.length;
        boolean hasConstructor = false;
        boolean hasRecoveredConstructor = false;
        boolean hasAbstractMethods = false;
        int defaultConstructorIndex = -1;
        if (this.methodCount > 0) {
            AbstractMethodDeclaration[] methodDeclarations = new AbstractMethodDeclaration[existingCount + this.methodCount];
            for (int k = 0; k < existingCount; ++k) {
                final AbstractMethodDeclaration m = this.typeDeclaration.methods[k];
                if (m.isDefaultConstructor()) {
                    defaultConstructorIndex = k;
                }
                if (m.isAbstract()) {
                    hasAbstractMethods = true;
                }
                methodDeclarations[k] = m;
            }
            if (this.methods[this.methodCount - 1].methodDeclaration.declarationSourceEnd == 0) {
                final int bodyEndValue2 = this.bodyEnd();
                this.methods[this.methodCount - 1].methodDeclaration.declarationSourceEnd = bodyEndValue2;
                this.methods[this.methodCount - 1].methodDeclaration.bodyEnd = bodyEndValue2;
            }
            int totalMethods = existingCount;
            int l = 0;
        Label_1062:
            while (l < this.methodCount) {
                while (true) {
                    for (int j2 = 0; j2 < existingCount; ++j2) {
                        if (methodDeclarations[j2] == this.methods[l].methodDeclaration) {
                            ++l;
                            continue Label_1062;
                        }
                    }
                    final AbstractMethodDeclaration updatedMethod = this.methods[l].updatedMethodDeclaration(depth, knownTypes);
                    if (updatedMethod.isConstructor()) {
                        hasRecoveredConstructor = true;
                    }
                    if (updatedMethod.isAbstract()) {
                        hasAbstractMethods = true;
                    }
                    methodDeclarations[totalMethods++] = updatedMethod;
                    continue;
                }
            }
            if (totalMethods != methodDeclarations.length) {
                System.arraycopy(methodDeclarations, 0, methodDeclarations = new AbstractMethodDeclaration[totalMethods], 0, totalMethods);
            }
            this.typeDeclaration.methods = methodDeclarations;
            if (methodDeclarations[methodDeclarations.length - 1].declarationSourceEnd > lastEnd) {
                lastEnd = methodDeclarations[methodDeclarations.length - 1].declarationSourceEnd;
            }
            if (hasAbstractMethods) {
                final TypeDeclaration typeDeclaration2 = this.typeDeclaration;
                typeDeclaration2.bits |= 0x800;
            }
            hasConstructor = this.typeDeclaration.checkConstructors(this.parser());
        }
        else {
            for (int i2 = 0; i2 < existingCount; ++i2) {
                if (this.typeDeclaration.methods[i2].isConstructor()) {
                    hasConstructor = true;
                }
            }
        }
        if (this.typeDeclaration.needClassInitMethod()) {
            boolean alreadyHasClinit = false;
            for (int k = 0; k < existingCount; ++k) {
                if (this.typeDeclaration.methods[k].isClinit()) {
                    alreadyHasClinit = true;
                    break;
                }
            }
            if (!alreadyHasClinit) {
                this.typeDeclaration.addClinit();
            }
        }
        if (defaultConstructorIndex >= 0 && hasRecoveredConstructor) {
            final AbstractMethodDeclaration[] methodDeclarations = new AbstractMethodDeclaration[this.typeDeclaration.methods.length - 1];
            if (defaultConstructorIndex != 0) {
                System.arraycopy(this.typeDeclaration.methods, 0, methodDeclarations, 0, defaultConstructorIndex);
            }
            if (defaultConstructorIndex != this.typeDeclaration.methods.length - 1) {
                System.arraycopy(this.typeDeclaration.methods, defaultConstructorIndex + 1, methodDeclarations, defaultConstructorIndex, this.typeDeclaration.methods.length - defaultConstructorIndex - 1);
            }
            this.typeDeclaration.methods = methodDeclarations;
        }
        else {
            final int kind = TypeDeclaration.kind(this.typeDeclaration.modifiers);
            if (!hasConstructor && kind != 2 && kind != 4 && this.typeDeclaration.allocation == null) {
                boolean insideFieldInitializer = false;
                for (RecoveredElement parentElement = this.parent; parentElement != null; parentElement = parentElement.parent) {
                    if (parentElement instanceof RecoveredField) {
                        insideFieldInitializer = true;
                        break;
                    }
                }
                this.typeDeclaration.createDefaultConstructor(!this.parser().diet || insideFieldInitializer, true);
            }
        }
        if (this.parent instanceof RecoveredType) {
            final TypeDeclaration typeDeclaration3 = this.typeDeclaration;
            typeDeclaration3.bits |= 0x400;
        }
        else if (this.parent instanceof RecoveredMethod) {
            final TypeDeclaration typeDeclaration4 = this.typeDeclaration;
            typeDeclaration4.bits |= 0x100;
        }
        if (this.typeDeclaration.declarationSourceEnd == 0) {
            this.typeDeclaration.declarationSourceEnd = lastEnd;
            this.typeDeclaration.bodyEnd = lastEnd;
        }
        return this.typeDeclaration;
    }
    
    @Override
    public void updateFromParserState() {
        if (this.bodyStartsAtHeaderEnd() && this.typeDeclaration.allocation == null) {
            final Parser parser = this.parser();
            if (parser.listLength > 0 && parser.astLengthPtr > 0) {
                final int length = parser.astLengthStack[parser.astLengthPtr];
                final int astPtr = parser.astPtr - length;
                boolean canConsume = astPtr >= 0;
                if (canConsume) {
                    if (!(parser.astStack[astPtr] instanceof TypeDeclaration)) {
                        canConsume = false;
                    }
                    for (int i = 1, max = length + 1; i < max; ++i) {
                        if (!(parser.astStack[astPtr + i] instanceof TypeReference)) {
                            canConsume = false;
                        }
                    }
                }
                if (canConsume) {
                    parser.consumeClassHeaderImplements();
                }
            }
            else if (parser.listTypeParameterLength > 0) {
                final int length = parser.listTypeParameterLength;
                int genericsPtr = parser.genericsPtr;
                boolean canConsume = genericsPtr + 1 >= length && parser.astPtr > -1;
                if (canConsume) {
                    if (!(parser.astStack[parser.astPtr] instanceof TypeDeclaration)) {
                        canConsume = false;
                    }
                    while (genericsPtr + 1 > length && !(parser.genericsStack[genericsPtr] instanceof TypeParameter)) {
                        --genericsPtr;
                    }
                    for (int i = 0; i < length; ++i) {
                        if (!(parser.genericsStack[genericsPtr - i] instanceof TypeParameter)) {
                            canConsume = false;
                        }
                    }
                }
                if (canConsume) {
                    final TypeDeclaration typeDecl = (TypeDeclaration)parser.astStack[parser.astPtr];
                    System.arraycopy(parser.genericsStack, genericsPtr - length + 1, typeDecl.typeParameters = new TypeParameter[length], 0, length);
                    typeDecl.bodyStart = typeDecl.typeParameters[length - 1].declarationSourceEnd + 1;
                    parser.listTypeParameterLength = 0;
                    parser.lastCheckPoint = typeDecl.bodyStart;
                }
            }
        }
    }
    
    @Override
    public RecoveredElement updateOnClosingBrace(final int braceStart, final int braceEnd) {
        final int bracketBalance = this.bracketBalance - 1;
        this.bracketBalance = bracketBalance;
        if (bracketBalance <= 0 && this.parent != null) {
            this.updateSourceEndIfNecessary(braceStart, braceEnd);
            this.bodyEnd = braceStart - 1;
            return this.parent;
        }
        return this;
    }
    
    @Override
    public RecoveredElement updateOnOpeningBrace(final int braceStart, final int braceEnd) {
        Label_0096: {
            if (this.bracketBalance == 0) {
                final Parser parser = this.parser();
                switch (parser.lastIgnoredToken) {
                    case -1:
                    case 14:
                    case 15:
                    case 16:
                    case 96:
                    case 114: {
                        if (parser.recoveredStaticInitializerStart == 0) {
                            break Label_0096;
                        }
                        break;
                    }
                }
                this.foundOpeningBrace = true;
                this.bracketBalance = 1;
            }
        }
        if (this.bracketBalance == 1) {
            final Block block = new Block(0);
            final Parser parser2 = this.parser();
            block.sourceStart = parser2.scanner.startPosition;
            Initializer init;
            if (parser2.recoveredStaticInitializerStart == 0) {
                init = new Initializer(block, 0);
            }
            else {
                init = new Initializer(block, 8);
                init.declarationSourceStart = parser2.recoveredStaticInitializerStart;
            }
            init.bodyStart = parser2.scanner.currentPosition;
            return this.add(init, 1);
        }
        return super.updateOnOpeningBrace(braceStart, braceEnd);
    }
    
    @Override
    public void updateParseTree() {
        this.updatedTypeDeclaration(0, new HashSet<TypeDeclaration>());
    }
    
    @Override
    public void updateSourceEndIfNecessary(final int start, final int end) {
        if (this.typeDeclaration.declarationSourceEnd == 0) {
            this.bodyEnd = 0;
            this.typeDeclaration.declarationSourceEnd = end;
            this.typeDeclaration.bodyEnd = end;
        }
    }
    
    public void annotationsConsumed(final Annotation[] consumedAnnotations) {
        final RecoveredAnnotation[] keep = new RecoveredAnnotation[this.pendingAnnotationCount];
        int numKeep = 0;
        final int pendingCount = this.pendingAnnotationCount;
        final int consumedLength = consumedAnnotations.length;
        int i = 0;
    Label_0082:
        while (i < pendingCount) {
            final Annotation pendingAnnotationAST = this.pendingAnnotations[i].annotation;
            while (true) {
                for (int j = 0; j < consumedLength; ++j) {
                    if (consumedAnnotations[j] == pendingAnnotationAST) {
                        ++i;
                        continue Label_0082;
                    }
                }
                keep[numKeep++] = this.pendingAnnotations[i];
                continue;
            }
        }
        if (numKeep != this.pendingAnnotationCount) {
            this.pendingAnnotations = keep;
            this.pendingAnnotationCount = numKeep;
        }
    }
}
