package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.SuperReference;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;

public class RecoveredMethod extends RecoveredElement implements TerminalTokens
{
    public AbstractMethodDeclaration methodDeclaration;
    public RecoveredAnnotation[] annotations;
    public int annotationCount;
    public int modifiers;
    public int modifiersStart;
    public RecoveredType[] localTypes;
    public int localTypeCount;
    public RecoveredBlock methodBody;
    public boolean discardBody;
    int pendingModifiers;
    int pendingModifersSourceStart;
    RecoveredAnnotation[] pendingAnnotations;
    int pendingAnnotationCount;
    
    public RecoveredMethod(final AbstractMethodDeclaration methodDeclaration, final RecoveredElement parent, final int bracketBalance, final Parser parser) {
        super(parent, bracketBalance, parser);
        this.discardBody = true;
        this.pendingModifersSourceStart = -1;
        this.methodDeclaration = methodDeclaration;
        this.foundOpeningBrace = !this.bodyStartsAtHeaderEnd();
        if (this.foundOpeningBrace) {
            ++this.bracketBalance;
        }
    }
    
    @Override
    public RecoveredElement add(final Block nestedBlockDeclaration, final int bracketBalanceValue) {
        return this.add(nestedBlockDeclaration, bracketBalanceValue, false);
    }
    
    public RecoveredElement add(final Block nestedBlockDeclaration, final int bracketBalanceValue, final boolean isArgument) {
        if (this.methodDeclaration.declarationSourceEnd > 0 && nestedBlockDeclaration.sourceStart > this.methodDeclaration.declarationSourceEnd) {
            this.resetPendingModifiers();
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(nestedBlockDeclaration, bracketBalanceValue);
        }
        else {
            if (!this.foundOpeningBrace && !isArgument) {
                this.foundOpeningBrace = true;
                ++this.bracketBalance;
            }
            this.methodBody = new RecoveredBlock(nestedBlockDeclaration, this, bracketBalanceValue);
            if (nestedBlockDeclaration.sourceEnd == 0) {
                return this.methodBody;
            }
            return this;
        }
    }
    
    @Override
    public RecoveredElement add(final FieldDeclaration fieldDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        final char[][] fieldTypeName;
        if ((fieldDeclaration.modifiers & 0xFFFFFFEF) != 0x0 || fieldDeclaration.type == null || ((fieldTypeName = fieldDeclaration.type.getTypeName()).length == 1 && CharOperation.equals(fieldTypeName[0], TypeBinding.VOID.sourceName()))) {
            if (this.parent == null) {
                return this;
            }
            this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(fieldDeclaration.declarationSourceStart - 1));
            return this.parent.add(fieldDeclaration, bracketBalanceValue);
        }
        else {
            if (this.methodDeclaration.declarationSourceEnd <= 0 || fieldDeclaration.declarationSourceStart <= this.methodDeclaration.declarationSourceEnd) {
                if (!this.foundOpeningBrace) {
                    this.foundOpeningBrace = true;
                    ++this.bracketBalance;
                }
                return this;
            }
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(fieldDeclaration, bracketBalanceValue);
        }
    }
    
    @Override
    public RecoveredElement add(final LocalDeclaration localDeclaration, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.methodDeclaration.declarationSourceEnd != 0 && localDeclaration.declarationSourceStart > this.methodDeclaration.declarationSourceEnd) {
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(localDeclaration, bracketBalanceValue);
        }
        else {
            if (this.methodBody == null) {
                final Block block = new Block(0);
                block.sourceStart = this.methodDeclaration.bodyStart;
                RecoveredElement currentBlock = this.add(block, 1, localDeclaration.isArgument());
                if (this.bracketBalance > 0) {
                    for (int i = 0; i < this.bracketBalance - 1; ++i) {
                        currentBlock = currentBlock.add(new Block(0), 1);
                    }
                    this.bracketBalance = 1;
                }
                return currentBlock.add(localDeclaration, bracketBalanceValue);
            }
            return this.methodBody.add(localDeclaration, bracketBalanceValue, true);
        }
    }
    
    @Override
    public RecoveredElement add(final Statement statement, final int bracketBalanceValue) {
        this.resetPendingModifiers();
        if (this.methodDeclaration.declarationSourceEnd != 0 && statement.sourceStart > this.methodDeclaration.declarationSourceEnd) {
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(statement, bracketBalanceValue);
        }
        else {
            if (this.methodBody == null) {
                final Block block = new Block(0);
                block.sourceStart = this.methodDeclaration.bodyStart;
                RecoveredElement currentBlock = this.add(block, 1);
                if (this.bracketBalance > 0) {
                    for (int i = 0; i < this.bracketBalance - 1; ++i) {
                        currentBlock = currentBlock.add(new Block(0), 1);
                    }
                    this.bracketBalance = 1;
                }
                return currentBlock.add(statement, bracketBalanceValue);
            }
            return this.methodBody.add(statement, bracketBalanceValue, true);
        }
    }
    
    @Override
    public RecoveredElement add(final TypeDeclaration typeDeclaration, final int bracketBalanceValue) {
        if (this.methodDeclaration.declarationSourceEnd != 0 && typeDeclaration.declarationSourceStart > this.methodDeclaration.declarationSourceEnd) {
            if (this.parent == null) {
                return this;
            }
            return this.parent.add(typeDeclaration, bracketBalanceValue);
        }
        else {
            if ((typeDeclaration.bits & 0x100) != 0x0 || this.parser().methodRecoveryActivated || this.parser().statementRecoveryActivated) {
                if (this.methodBody == null) {
                    final Block block = new Block(0);
                    block.sourceStart = this.methodDeclaration.bodyStart;
                    this.add(block, 1);
                }
                this.methodBody.attachPendingModifiers(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
                this.resetPendingModifiers();
                return this.methodBody.add(typeDeclaration, bracketBalanceValue, true);
            }
            switch (TypeDeclaration.kind(typeDeclaration.modifiers)) {
                case 2:
                case 4: {
                    this.resetPendingModifiers();
                    this.updateSourceEndIfNecessary(this.previousAvailableLineEnd(typeDeclaration.declarationSourceStart - 1));
                    if (this.parent == null) {
                        return this;
                    }
                    return this.parent.add(typeDeclaration, bracketBalanceValue);
                }
                default: {
                    if (this.localTypes == null) {
                        this.localTypes = new RecoveredType[5];
                        this.localTypeCount = 0;
                    }
                    else if (this.localTypeCount == this.localTypes.length) {
                        System.arraycopy(this.localTypes, 0, this.localTypes = new RecoveredType[2 * this.localTypeCount], 0, this.localTypeCount);
                    }
                    final RecoveredType element = new RecoveredType(typeDeclaration, this, bracketBalanceValue);
                    this.localTypes[this.localTypeCount++] = element;
                    if (this.pendingAnnotationCount > 0) {
                        element.attach(this.pendingAnnotations, this.pendingAnnotationCount, this.pendingModifiers, this.pendingModifersSourceStart);
                    }
                    this.resetPendingModifiers();
                    if (!this.foundOpeningBrace) {
                        this.foundOpeningBrace = true;
                        ++this.bracketBalance;
                    }
                    return element;
                }
            }
        }
    }
    
    public boolean bodyStartsAtHeaderEnd() {
        return this.methodDeclaration.bodyStart == this.methodDeclaration.sourceEnd + 1;
    }
    
    @Override
    public ASTNode parseTree() {
        return this.methodDeclaration;
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
        return this.methodDeclaration.declarationSourceEnd;
    }
    
    @Override
    public String toString(final int tab) {
        final StringBuffer result = new StringBuffer(this.tabString(tab));
        result.append("Recovered method:\n");
        this.methodDeclaration.print(tab + 1, result);
        if (this.annotations != null) {
            for (int i = 0; i < this.annotationCount; ++i) {
                result.append("\n");
                result.append(this.annotations[i].toString(tab + 1));
            }
        }
        if (this.localTypes != null) {
            for (int i = 0; i < this.localTypeCount; ++i) {
                result.append("\n");
                result.append(this.localTypes[i].toString(tab + 1));
            }
        }
        if (this.methodBody != null) {
            result.append("\n");
            result.append(this.methodBody.toString(tab + 1));
        }
        return result.toString();
    }
    
    @Override
    public void updateBodyStart(final int bodyStart) {
        this.foundOpeningBrace = true;
        this.methodDeclaration.bodyStart = bodyStart;
    }
    
    public AbstractMethodDeclaration updatedMethodDeclaration(final int depth, final Set<TypeDeclaration> knownTypes) {
        if (this.modifiers != 0) {
            final AbstractMethodDeclaration methodDeclaration = this.methodDeclaration;
            methodDeclaration.modifiers |= this.modifiers;
            if (this.modifiersStart < this.methodDeclaration.declarationSourceStart) {
                this.methodDeclaration.declarationSourceStart = this.modifiersStart;
            }
        }
        if (this.annotationCount > 0) {
            final int existingCount = (this.methodDeclaration.annotations == null) ? 0 : this.methodDeclaration.annotations.length;
            final Annotation[] annotationReferences = new Annotation[existingCount + this.annotationCount];
            if (existingCount > 0) {
                System.arraycopy(this.methodDeclaration.annotations, 0, annotationReferences, this.annotationCount, existingCount);
            }
            for (int i = 0; i < this.annotationCount; ++i) {
                annotationReferences[i] = this.annotations[i].updatedAnnotationReference();
            }
            this.methodDeclaration.annotations = annotationReferences;
            final int start = this.annotations[0].annotation.sourceStart;
            if (start < this.methodDeclaration.declarationSourceStart) {
                this.methodDeclaration.declarationSourceStart = start;
            }
        }
        if (this.methodBody != null) {
            final Block block = this.methodBody.updatedBlock(depth, knownTypes);
            if (block != null) {
                this.methodDeclaration.statements = block.statements;
                if (this.methodDeclaration.declarationSourceEnd == 0) {
                    this.methodDeclaration.declarationSourceEnd = block.sourceEnd;
                    this.methodDeclaration.bodyEnd = block.sourceEnd;
                }
                if (this.methodDeclaration.isConstructor()) {
                    final ConstructorDeclaration constructor = (ConstructorDeclaration)this.methodDeclaration;
                    if (this.methodDeclaration.statements != null && this.methodDeclaration.statements[0] instanceof ExplicitConstructorCall) {
                        constructor.constructorCall = (ExplicitConstructorCall)this.methodDeclaration.statements[0];
                        final int length = this.methodDeclaration.statements.length;
                        System.arraycopy(this.methodDeclaration.statements, 1, this.methodDeclaration.statements = new Statement[length - 1], 0, length - 1);
                    }
                    if (constructor.constructorCall == null) {
                        constructor.constructorCall = SuperReference.implicitSuperConstructorCall();
                    }
                }
            }
        }
        else if (this.methodDeclaration.declarationSourceEnd == 0) {
            if (this.methodDeclaration.sourceEnd + 1 == this.methodDeclaration.bodyStart) {
                this.methodDeclaration.declarationSourceEnd = this.methodDeclaration.sourceEnd;
                this.methodDeclaration.bodyStart = this.methodDeclaration.sourceEnd;
                this.methodDeclaration.bodyEnd = this.methodDeclaration.sourceEnd;
            }
            else {
                this.methodDeclaration.declarationSourceEnd = this.methodDeclaration.bodyStart;
                this.methodDeclaration.bodyEnd = this.methodDeclaration.bodyStart;
            }
        }
        if (this.localTypeCount > 0) {
            final AbstractMethodDeclaration methodDeclaration2 = this.methodDeclaration;
            methodDeclaration2.bits |= 0x2;
        }
        return this.methodDeclaration;
    }
    
    @Override
    public void updateFromParserState() {
        if (this.bodyStartsAtHeaderEnd() && this.parent != null) {
            final Parser parser = this.parser();
            if (parser.listLength > 0 && parser.astLengthPtr > 0) {
                if (this.methodDeclaration.sourceEnd == parser.rParenPos) {
                    final int length = parser.astLengthStack[parser.astLengthPtr];
                    final int astPtr = parser.astPtr - length;
                    boolean canConsume = astPtr >= 0;
                    if (canConsume) {
                        if (!(parser.astStack[astPtr] instanceof AbstractMethodDeclaration)) {
                            canConsume = false;
                        }
                        for (int i = 1, max = length + 1; i < max; ++i) {
                            if (!(parser.astStack[astPtr + i] instanceof TypeReference)) {
                                canConsume = false;
                            }
                        }
                    }
                    if (canConsume) {
                        parser.consumeMethodHeaderThrowsClause();
                    }
                    else {
                        parser.listLength = 0;
                    }
                }
                else {
                    if (parser.currentToken == 24 || parser.currentToken == 28) {
                        final int[] astLengthStack = parser.astLengthStack;
                        final int astLengthPtr = parser.astLengthPtr;
                        --astLengthStack[astLengthPtr];
                        final Parser parser2 = parser;
                        --parser2.astPtr;
                        final Parser parser3 = parser;
                        --parser3.listLength;
                        parser.currentToken = 0;
                    }
                    int argLength = parser.astLengthStack[parser.astLengthPtr];
                    int argStart = parser.astPtr - argLength + 1;
                    boolean needUpdateRParenPos = parser.rParenPos < parser.lParenPos;
                    MemberValuePair[] memberValuePairs = null;
                    while (argLength > 0 && parser.astStack[parser.astPtr] instanceof MemberValuePair) {
                        System.arraycopy(parser.astStack, argStart, memberValuePairs = new MemberValuePair[argLength], 0, argLength);
                        final Parser parser4 = parser;
                        --parser4.astLengthPtr;
                        final Parser parser5 = parser;
                        parser5.astPtr -= argLength;
                        argLength = parser.astLengthStack[parser.astLengthPtr];
                        argStart = parser.astPtr - argLength + 1;
                        needUpdateRParenPos = true;
                    }
                    for (int count = 0; count < argLength; ++count) {
                        final ASTNode aNode = parser.astStack[argStart + count];
                        if (!(aNode instanceof Argument)) {
                            parser.astLengthStack[parser.astLengthPtr] = count;
                            parser.astPtr = argStart + count - 1;
                            parser.listLength = count;
                            parser.currentToken = 0;
                            break;
                        }
                        final Argument argument = (Argument)aNode;
                        final char[][] argTypeName = argument.type.getTypeName();
                        if ((argument.modifiers & 0xFFFFFFEF) != 0x0 || (argTypeName.length == 1 && CharOperation.equals(argTypeName[0], TypeBinding.VOID.sourceName()))) {
                            parser.astLengthStack[parser.astLengthPtr] = count;
                            parser.astPtr = argStart + count - 1;
                            parser.listLength = count;
                            parser.currentToken = 0;
                            break;
                        }
                        if (needUpdateRParenPos) {
                            parser.rParenPos = argument.sourceEnd + 1;
                        }
                    }
                    if (parser.listLength > 0 && parser.astLengthPtr > 0) {
                        final int length2 = parser.astLengthStack[parser.astLengthPtr];
                        final int astPtr2 = parser.astPtr - length2;
                        boolean canConsume2 = astPtr2 >= 0;
                        if (canConsume2) {
                            if (!(parser.astStack[astPtr2] instanceof AbstractMethodDeclaration)) {
                                canConsume2 = false;
                            }
                            for (int j = 1, max2 = length2 + 1; j < max2; ++j) {
                                if (!(parser.astStack[astPtr2 + j] instanceof Argument)) {
                                    canConsume2 = false;
                                }
                            }
                        }
                        if (canConsume2) {
                            parser.consumeMethodHeaderRightParen();
                            if (parser.currentElement == this) {
                                if (this.methodDeclaration.arguments != null) {
                                    this.methodDeclaration.sourceEnd = this.methodDeclaration.arguments[this.methodDeclaration.arguments.length - 1].sourceEnd;
                                }
                                else {
                                    this.methodDeclaration.sourceEnd = this.methodDeclaration.receiver.sourceEnd;
                                }
                                this.methodDeclaration.bodyStart = this.methodDeclaration.sourceEnd + 1;
                                parser.lastCheckPoint = this.methodDeclaration.bodyStart;
                            }
                        }
                    }
                    if (memberValuePairs != null) {
                        System.arraycopy(memberValuePairs, 0, parser.astStack, parser.astPtr + 1, memberValuePairs.length);
                        final Parser parser6 = parser;
                        parser6.astPtr += memberValuePairs.length;
                        parser.astLengthStack[++parser.astLengthPtr] = memberValuePairs.length;
                    }
                }
            }
        }
    }
    
    @Override
    public RecoveredElement updateOnClosingBrace(final int braceStart, final int braceEnd) {
        if (!this.methodDeclaration.isAnnotationMethod()) {
            if (this.parent != null && this.parent instanceof RecoveredType) {
                final int mods = ((RecoveredType)this.parent).typeDeclaration.modifiers;
                if (TypeDeclaration.kind(mods) == 2 && !this.foundOpeningBrace) {
                    this.updateSourceEndIfNecessary(braceStart - 1, braceStart - 1);
                    return this.parent.updateOnClosingBrace(braceStart, braceEnd);
                }
            }
            return super.updateOnClosingBrace(braceStart, braceEnd);
        }
        this.updateSourceEndIfNecessary(braceStart, braceEnd);
        if (!this.foundOpeningBrace && this.parent != null) {
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        return this;
    }
    
    @Override
    public RecoveredElement updateOnOpeningBrace(final int braceStart, final int braceEnd) {
        if (this.bracketBalance == 0) {
            switch (this.parser().lastIgnoredToken) {
                case -1:
                case 112: {
                    break;
                }
                default: {
                    this.foundOpeningBrace = true;
                    this.bracketBalance = 1;
                    break;
                }
            }
        }
        return super.updateOnOpeningBrace(braceStart, braceEnd);
    }
    
    @Override
    public void updateParseTree() {
        this.updatedMethodDeclaration(0, new HashSet<TypeDeclaration>());
    }
    
    @Override
    public void updateSourceEndIfNecessary(final int braceStart, final int braceEnd) {
        if (this.methodDeclaration.declarationSourceEnd == 0) {
            if (this.parser().rBraceSuccessorStart >= braceEnd) {
                this.methodDeclaration.declarationSourceEnd = this.parser().rBraceEnd;
                this.methodDeclaration.bodyEnd = this.parser().rBraceStart;
            }
            else {
                this.methodDeclaration.declarationSourceEnd = braceEnd;
                this.methodDeclaration.bodyEnd = braceStart - 1;
            }
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
    
    void attach(final TypeParameter[] parameters, final int startPos) {
        if (this.methodDeclaration.modifiers != 0) {
            return;
        }
        final int lastParameterEnd = parameters[parameters.length - 1].sourceEnd;
        final Parser parser = this.parser();
        final Scanner scanner = parser.scanner;
        if (Util.getLineNumber(this.methodDeclaration.declarationSourceStart, scanner.lineEnds, 0, scanner.linePtr) != Util.getLineNumber(lastParameterEnd, scanner.lineEnds, 0, scanner.linePtr)) {
            return;
        }
        if (parser.modifiersSourceStart > lastParameterEnd && parser.modifiersSourceStart < this.methodDeclaration.declarationSourceStart) {
            return;
        }
        if (this.methodDeclaration instanceof MethodDeclaration) {
            ((MethodDeclaration)this.methodDeclaration).typeParameters = parameters;
            this.methodDeclaration.declarationSourceStart = startPos;
        }
        else if (this.methodDeclaration instanceof ConstructorDeclaration) {
            ((ConstructorDeclaration)this.methodDeclaration).typeParameters = parameters;
            this.methodDeclaration.declarationSourceStart = startPos;
        }
    }
    
    public void attach(final RecoveredAnnotation[] annots, final int annotCount, final int mods, final int modsSourceStart) {
        if (annotCount > 0) {
            final Annotation[] existingAnnotations = this.methodDeclaration.annotations;
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
}
