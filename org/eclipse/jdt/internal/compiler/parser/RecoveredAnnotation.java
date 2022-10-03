package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.MarkerAnnotation;
import org.eclipse.jdt.internal.compiler.ast.SingleMemberAnnotation;
import org.eclipse.jdt.internal.compiler.ast.NormalAnnotation;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.MemberValuePair;
import org.eclipse.jdt.internal.compiler.ast.SingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Annotation;

public class RecoveredAnnotation extends RecoveredElement
{
    public static final int MARKER = 0;
    public static final int NORMAL = 1;
    public static final int SINGLE_MEMBER = 2;
    private int kind;
    private int identifierPtr;
    private int identifierLengthPtr;
    private int sourceStart;
    public boolean hasPendingMemberValueName;
    public int memberValuPairEqualEnd;
    public Annotation annotation;
    
    public RecoveredAnnotation(final int identifierPtr, final int identifierLengthPtr, final int sourceStart, final RecoveredElement parent, final int bracketBalance) {
        super(parent, bracketBalance);
        this.memberValuPairEqualEnd = -1;
        this.kind = 0;
        this.identifierPtr = identifierPtr;
        this.identifierLengthPtr = identifierLengthPtr;
        this.sourceStart = sourceStart;
    }
    
    @Override
    public RecoveredElement add(final TypeDeclaration typeDeclaration, final int bracketBalanceValue) {
        if (this.annotation == null && (typeDeclaration.bits & 0x200) != 0x0) {
            return this;
        }
        return super.add(typeDeclaration, bracketBalanceValue);
    }
    
    @Override
    public RecoveredElement addAnnotationName(final int identPtr, final int identLengthPtr, final int annotationStart, final int bracketBalanceValue) {
        final RecoveredAnnotation element = new RecoveredAnnotation(identPtr, identLengthPtr, annotationStart, this, bracketBalanceValue);
        return element;
    }
    
    public RecoveredElement addAnnotation(final Annotation annot, final int index) {
        this.annotation = annot;
        if (this.parent != null) {
            return this.parent;
        }
        return this;
    }
    
    @Override
    public void updateFromParserState() {
        final Parser parser = this.parser();
        if (this.annotation == null && this.identifierPtr <= parser.identifierPtr) {
            Annotation annot = null;
            boolean needUpdateRParenPos = false;
            MemberValuePair pendingMemberValueName = null;
            if (this.hasPendingMemberValueName && this.identifierPtr < parser.identifierPtr) {
                final char[] memberValueName = parser.identifierStack[this.identifierPtr + 1];
                final long pos = parser.identifierPositionStack[this.identifierPtr + 1];
                final int start = (int)(pos >>> 32);
                final int end = (int)pos;
                final int valueEnd = (this.memberValuPairEqualEnd > -1) ? this.memberValuPairEqualEnd : end;
                final SingleNameReference fakeExpression = new SingleNameReference(RecoveryScanner.FAKE_IDENTIFIER, (valueEnd + 1L << 32) + valueEnd);
                pendingMemberValueName = new MemberValuePair(memberValueName, start, end, fakeExpression);
            }
            parser.identifierPtr = this.identifierPtr;
            parser.identifierLengthPtr = this.identifierLengthPtr;
            final TypeReference typeReference = parser.getAnnotationType();
            switch (this.kind) {
                case 1: {
                    if (parser.astPtr <= -1 || !(parser.astStack[parser.astPtr] instanceof MemberValuePair)) {
                        break;
                    }
                    MemberValuePair[] memberValuePairs = null;
                    final int argLength = parser.astLengthStack[parser.astLengthPtr];
                    final int argStart = parser.astPtr - argLength + 1;
                    if (argLength > 0) {
                        int annotationEnd;
                        if (pendingMemberValueName != null) {
                            memberValuePairs = new MemberValuePair[argLength + 1];
                            System.arraycopy(parser.astStack, argStart, memberValuePairs, 0, argLength);
                            final Parser parser2 = parser;
                            --parser2.astLengthPtr;
                            final Parser parser3 = parser;
                            parser3.astPtr -= argLength;
                            memberValuePairs[argLength] = pendingMemberValueName;
                            annotationEnd = pendingMemberValueName.sourceEnd;
                        }
                        else {
                            memberValuePairs = new MemberValuePair[argLength];
                            System.arraycopy(parser.astStack, argStart, memberValuePairs, 0, argLength);
                            final Parser parser4 = parser;
                            --parser4.astLengthPtr;
                            final Parser parser5 = parser;
                            parser5.astPtr -= argLength;
                            final MemberValuePair lastMemberValuePair = memberValuePairs[memberValuePairs.length - 1];
                            annotationEnd = ((lastMemberValuePair.value != null) ? ((lastMemberValuePair.value instanceof Annotation) ? ((Annotation)lastMemberValuePair.value).declarationSourceEnd : lastMemberValuePair.value.sourceEnd) : lastMemberValuePair.sourceEnd);
                        }
                        final NormalAnnotation normalAnnotation = new NormalAnnotation(typeReference, this.sourceStart);
                        normalAnnotation.memberValuePairs = memberValuePairs;
                        normalAnnotation.declarationSourceEnd = annotationEnd;
                        final NormalAnnotation normalAnnotation3 = normalAnnotation;
                        normalAnnotation3.bits |= 0x20;
                        annot = normalAnnotation;
                        needUpdateRParenPos = true;
                        break;
                    }
                    break;
                }
                case 2: {
                    if (parser.expressionPtr > -1) {
                        final Expression memberValue = parser.expressionStack[parser.expressionPtr--];
                        final SingleMemberAnnotation singleMemberAnnotation = new SingleMemberAnnotation(typeReference, this.sourceStart);
                        singleMemberAnnotation.memberValue = memberValue;
                        singleMemberAnnotation.declarationSourceEnd = memberValue.sourceEnd;
                        final SingleMemberAnnotation singleMemberAnnotation2 = singleMemberAnnotation;
                        singleMemberAnnotation2.bits |= 0x20;
                        annot = singleMemberAnnotation;
                        needUpdateRParenPos = true;
                        break;
                    }
                    break;
                }
            }
            if (!needUpdateRParenPos) {
                if (pendingMemberValueName != null) {
                    final NormalAnnotation normalAnnotation2 = new NormalAnnotation(typeReference, this.sourceStart);
                    normalAnnotation2.memberValuePairs = new MemberValuePair[] { pendingMemberValueName };
                    normalAnnotation2.declarationSourceEnd = pendingMemberValueName.value.sourceEnd;
                    final NormalAnnotation normalAnnotation4 = normalAnnotation2;
                    normalAnnotation4.bits |= 0x20;
                    annot = normalAnnotation2;
                }
                else {
                    final MarkerAnnotation markerAnnotation = new MarkerAnnotation(typeReference, this.sourceStart);
                    markerAnnotation.declarationSourceEnd = markerAnnotation.sourceEnd;
                    final MarkerAnnotation markerAnnotation2 = markerAnnotation;
                    markerAnnotation2.bits |= 0x20;
                    annot = markerAnnotation;
                }
            }
            parser.currentElement = this.addAnnotation(annot, this.identifierPtr);
            parser.annotationRecoveryCheckPoint(annot.sourceStart, annot.declarationSourceEnd);
            if (this.parent != null) {
                this.parent.updateFromParserState();
            }
        }
    }
    
    @Override
    public ASTNode parseTree() {
        return this.annotation;
    }
    
    @Override
    public void resetPendingModifiers() {
        if (this.parent != null) {
            this.parent.resetPendingModifiers();
        }
    }
    
    public void setKind(final int kind) {
        this.kind = kind;
    }
    
    @Override
    public int sourceEnd() {
        if (this.annotation != null) {
            return this.annotation.declarationSourceEnd;
        }
        final Parser parser = this.parser();
        if (this.identifierPtr < parser.identifierPositionStack.length) {
            return (int)parser.identifierPositionStack[this.identifierPtr];
        }
        return this.sourceStart;
    }
    
    @Override
    public String toString(final int tab) {
        if (this.annotation != null) {
            return String.valueOf(this.tabString(tab)) + "Recovered annotation:\n" + (Object)this.annotation.print(tab + 1, new StringBuffer(10));
        }
        return String.valueOf(this.tabString(tab)) + "Recovered annotation: identiferPtr=" + this.identifierPtr + " identiferlengthPtr=" + this.identifierLengthPtr + "\n";
    }
    
    public Annotation updatedAnnotationReference() {
        return this.annotation;
    }
    
    @Override
    public RecoveredElement updateOnClosingBrace(final int braceStart, final int braceEnd) {
        if (this.bracketBalance > 0) {
            --this.bracketBalance;
            return this;
        }
        if (this.parent != null) {
            return this.parent.updateOnClosingBrace(braceStart, braceEnd);
        }
        return this;
    }
    
    @Override
    public void updateParseTree() {
        this.updatedAnnotationReference();
    }
}
