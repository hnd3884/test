package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.JavadocMessageSend;
import org.eclipse.jdt.internal.compiler.ast.JavadocAllocationExpression;
import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.List;
import org.eclipse.jdt.internal.compiler.ast.JavadocFieldReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocImplicitTypeReference;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ast.JavadocArgumentExpression;
import org.eclipse.jdt.internal.compiler.ast.JavadocArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocArraySingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.JavadocSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;

public class JavadocParser extends AbstractCommentParser
{
    public Javadoc docComment;
    private int invalidParamReferencesPtr;
    private ASTNode[] invalidParamReferencesStack;
    private long validValuePositions;
    private long invalidValuePositions;
    public boolean shouldReportProblems;
    private int tagWaitingForDescription;
    
    public JavadocParser(final Parser sourceParser) {
        super(sourceParser);
        this.invalidParamReferencesPtr = -1;
        this.shouldReportProblems = true;
        this.kind = 513;
        if (sourceParser != null && sourceParser.options != null) {
            this.setJavadocPositions = sourceParser.options.processAnnotations;
        }
    }
    
    public boolean checkDeprecation(final int commentPtr) {
        this.javadocStart = this.sourceParser.scanner.commentStarts[commentPtr];
        this.javadocEnd = this.sourceParser.scanner.commentStops[commentPtr] - 1;
        this.firstTagPosition = this.sourceParser.scanner.commentTagStarts[commentPtr];
        this.validValuePositions = -1L;
        this.invalidValuePositions = -1L;
        this.tagWaitingForDescription = 0;
        if (this.checkDocComment) {
            this.docComment = new Javadoc(this.javadocStart, this.javadocEnd);
        }
        else if (this.setJavadocPositions) {
            this.docComment = new Javadoc(this.javadocStart, this.javadocEnd);
            final Javadoc docComment = this.docComment;
            docComment.bits &= 0xFFFEFFFF;
        }
        else {
            this.docComment = null;
        }
        if (this.firstTagPosition == 0) {
            switch (this.kind & 0xFF) {
                case 1:
                case 16: {
                    return false;
                }
            }
        }
        try {
            this.source = this.sourceParser.scanner.source;
            this.scanner.setSource(this.source);
            if (!this.checkDocComment) {
                final Scanner sourceScanner = this.sourceParser.scanner;
                final int firstLineNumber = Util.getLineNumber(this.javadocStart, sourceScanner.lineEnds, 0, sourceScanner.linePtr);
                final int lastLineNumber = Util.getLineNumber(this.javadocEnd, sourceScanner.lineEnds, 0, sourceScanner.linePtr);
                this.index = this.javadocStart + 3;
                this.deprecated = false;
            Label_0523:
                for (int line = firstLineNumber; line <= lastLineNumber; ++line) {
                    final int lineStart = (line == firstLineNumber) ? (this.javadocStart + 3) : this.sourceParser.scanner.getLineStart(line);
                    this.index = lineStart;
                    this.lineEnd = ((line == lastLineNumber) ? (this.javadocEnd - 2) : this.sourceParser.scanner.getLineEnd(line));
                    while (this.index < this.lineEnd) {
                        final char c = this.readChar();
                        switch (c) {
                            case '\t':
                            case '\n':
                            case '\f':
                            case '\r':
                            case ' ':
                            case '*': {
                                continue;
                            }
                            case '@': {
                                this.parseSimpleTag();
                                if (this.tagValue == 1 && this.abort) {
                                    continue Label_0523;
                                }
                                continue Label_0523;
                            }
                            default: {
                                continue Label_0523;
                            }
                        }
                    }
                }
                return this.deprecated;
            }
            this.scanner.lineEnds = this.sourceParser.scanner.lineEnds;
            this.scanner.linePtr = this.sourceParser.scanner.linePtr;
            this.lineEnds = this.scanner.lineEnds;
            this.commentParse();
        }
        finally {
            this.source = null;
            this.scanner.setSource((char[])null);
        }
        this.source = null;
        this.scanner.setSource((char[])null);
        return this.deprecated;
    }
    
    @Override
    protected Object createArgumentReference(final char[] name, final int dim, final boolean isVarargs, final Object typeRef, final long[] dimPositions, final long argNamePos) throws InvalidInputException {
        try {
            TypeReference argTypeRef = (TypeReference)typeRef;
            if (dim > 0) {
                final long pos = ((long)argTypeRef.sourceStart << 32) + argTypeRef.sourceEnd;
                if (typeRef instanceof JavadocSingleTypeReference) {
                    final JavadocSingleTypeReference singleRef = (JavadocSingleTypeReference)typeRef;
                    argTypeRef = new JavadocArraySingleTypeReference(singleRef.token, dim, pos);
                }
                else {
                    final JavadocQualifiedTypeReference qualifRef = (JavadocQualifiedTypeReference)typeRef;
                    argTypeRef = new JavadocArrayQualifiedTypeReference(qualifRef, dim);
                }
            }
            int argEnd = argTypeRef.sourceEnd;
            if (dim > 0) {
                argEnd = (int)dimPositions[dim - 1];
                if (isVarargs) {
                    final TypeReference typeReference = argTypeRef;
                    typeReference.bits |= 0x4000;
                }
            }
            if (argNamePos >= 0L) {
                argEnd = (int)argNamePos;
            }
            return new JavadocArgumentExpression(name, argTypeRef.sourceStart, argEnd, argTypeRef);
        }
        catch (final ClassCastException ex) {
            throw new InvalidInputException();
        }
    }
    
    @Override
    protected Object createFieldReference(final Object receiver) throws InvalidInputException {
        try {
            TypeReference typeRef = (TypeReference)receiver;
            if (typeRef == null) {
                final char[] name = this.sourceParser.compilationUnit.getMainTypeName();
                typeRef = new JavadocImplicitTypeReference(name, this.memberStart);
            }
            final JavadocFieldReference field = new JavadocFieldReference(this.identifierStack[0], this.identifierPositionStack[0]);
            field.receiver = typeRef;
            field.tagSourceStart = this.tagSourceStart;
            field.tagSourceEnd = this.tagSourceEnd;
            field.tagValue = this.tagValue;
            return field;
        }
        catch (final ClassCastException ex) {
            throw new InvalidInputException();
        }
    }
    
    @Override
    protected Object createMethodReference(final Object receiver, final List arguments) throws InvalidInputException {
        try {
            TypeReference typeRef = (TypeReference)receiver;
            boolean isConstructor = false;
            final int length = this.identifierLengthStack[0];
            if (typeRef == null) {
                char[] name = this.sourceParser.compilationUnit.getMainTypeName();
                final TypeDeclaration typeDecl = this.getParsedTypeDeclaration();
                if (typeDecl != null) {
                    name = typeDecl.name;
                }
                isConstructor = CharOperation.equals(this.identifierStack[length - 1], name);
                typeRef = new JavadocImplicitTypeReference(name, this.memberStart);
            }
            else if (typeRef instanceof JavadocSingleTypeReference) {
                final char[] name = ((JavadocSingleTypeReference)typeRef).token;
                isConstructor = CharOperation.equals(this.identifierStack[length - 1], name);
            }
            else {
                if (!(typeRef instanceof JavadocQualifiedTypeReference)) {
                    throw new InvalidInputException();
                }
                final char[][] tokens = ((JavadocQualifiedTypeReference)typeRef).tokens;
                final int last = tokens.length - 1;
                isConstructor = CharOperation.equals(this.identifierStack[length - 1], tokens[last]);
                if (isConstructor) {
                    boolean valid = true;
                    if (valid) {
                        for (int i = 0; i < length - 1 && valid; valid = CharOperation.equals(this.identifierStack[i], tokens[i]), ++i) {}
                    }
                    if (!valid) {
                        if (this.reportProblems) {
                            this.sourceParser.problemReporter().javadocInvalidMemberTypeQualification((int)(this.identifierPositionStack[0] >>> 32), (int)this.identifierPositionStack[length - 1], -1);
                        }
                        return null;
                    }
                }
            }
            if (arguments == null) {
                if (isConstructor) {
                    final JavadocAllocationExpression allocation = new JavadocAllocationExpression(this.identifierPositionStack[length - 1]);
                    allocation.type = typeRef;
                    allocation.tagValue = this.tagValue;
                    allocation.sourceEnd = this.scanner.getCurrentTokenEndPosition();
                    if (length == 1) {
                        allocation.qualification = new char[][] { this.identifierStack[0] };
                    }
                    else {
                        System.arraycopy(this.identifierStack, 0, allocation.qualification = new char[length][], 0, length);
                        allocation.sourceStart = (int)(this.identifierPositionStack[0] >>> 32);
                    }
                    allocation.memberStart = this.memberStart;
                    return allocation;
                }
                final JavadocMessageSend msg = new JavadocMessageSend(this.identifierStack[length - 1], this.identifierPositionStack[length - 1]);
                msg.receiver = typeRef;
                msg.tagValue = this.tagValue;
                msg.sourceEnd = this.scanner.getCurrentTokenEndPosition();
                return msg;
            }
            else {
                final JavadocArgumentExpression[] expressions = new JavadocArgumentExpression[arguments.size()];
                arguments.toArray(expressions);
                if (isConstructor) {
                    final JavadocAllocationExpression allocation2 = new JavadocAllocationExpression(this.identifierPositionStack[length - 1]);
                    allocation2.arguments = expressions;
                    allocation2.type = typeRef;
                    allocation2.tagValue = this.tagValue;
                    allocation2.sourceEnd = this.scanner.getCurrentTokenEndPosition();
                    if (length == 1) {
                        allocation2.qualification = new char[][] { this.identifierStack[0] };
                    }
                    else {
                        System.arraycopy(this.identifierStack, 0, allocation2.qualification = new char[length][], 0, length);
                        allocation2.sourceStart = (int)(this.identifierPositionStack[0] >>> 32);
                    }
                    allocation2.memberStart = this.memberStart;
                    return allocation2;
                }
                final JavadocMessageSend msg2 = new JavadocMessageSend(this.identifierStack[length - 1], this.identifierPositionStack[length - 1], expressions);
                msg2.receiver = typeRef;
                msg2.tagValue = this.tagValue;
                msg2.sourceEnd = this.scanner.getCurrentTokenEndPosition();
                return msg2;
            }
        }
        catch (final ClassCastException ex) {
            throw new InvalidInputException();
        }
    }
    
    @Override
    protected Object createReturnStatement() {
        return new JavadocReturnStatement(this.scanner.getCurrentTokenStartPosition(), this.scanner.getCurrentTokenEndPosition());
    }
    
    @Override
    protected void createTag() {
        this.tagValue = 100;
    }
    
    @Override
    protected Object createTypeReference(final int primitiveToken) {
        TypeReference typeRef = null;
        final int size = this.identifierLengthStack[this.identifierLengthPtr];
        if (size == 1) {
            typeRef = new JavadocSingleTypeReference(this.identifierStack[this.identifierPtr], this.identifierPositionStack[this.identifierPtr], this.tagSourceStart, this.tagSourceEnd);
        }
        else if (size > 1) {
            final char[][] tokens = new char[size][];
            System.arraycopy(this.identifierStack, this.identifierPtr - size + 1, tokens, 0, size);
            final long[] positions = new long[size];
            System.arraycopy(this.identifierPositionStack, this.identifierPtr - size + 1, positions, 0, size);
            typeRef = new JavadocQualifiedTypeReference(tokens, positions, this.tagSourceStart, this.tagSourceEnd);
        }
        return typeRef;
    }
    
    protected TypeDeclaration getParsedTypeDeclaration() {
        for (int ptr = this.sourceParser.astPtr; ptr >= 0; --ptr) {
            final Object node = this.sourceParser.astStack[ptr];
            if (node instanceof TypeDeclaration) {
                final TypeDeclaration typeDecl = (TypeDeclaration)node;
                if (typeDecl.bodyEnd == 0) {
                    return typeDecl;
                }
            }
        }
        return null;
    }
    
    @Override
    protected boolean parseThrows() {
        final boolean valid = super.parseThrows();
        this.tagWaitingForDescription = ((valid && this.reportProblems) ? 4 : 0);
        return valid;
    }
    
    protected boolean parseReturn() {
        if (this.returnStatement == null) {
            this.returnStatement = this.createReturnStatement();
            return true;
        }
        if (this.reportProblems) {
            this.sourceParser.problemReporter().javadocDuplicatedReturnTag(this.scanner.getCurrentTokenStartPosition(), this.scanner.getCurrentTokenEndPosition());
        }
        return false;
    }
    
    protected void parseSimpleTag() {
        char first = this.source[this.index++];
        if (first == '\\' && this.source[this.index] == 'u') {
            final int pos = this.index;
            ++this.index;
            while (this.source[this.index] == 'u') {
                ++this.index;
            }
            final int c1;
            final int c2;
            final int c3;
            final int c4;
            if ((c1 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c1 >= 0 && (c2 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c2 >= 0 && (c3 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c3 >= 0 && (c4 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c4 >= 0) {
                first = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
            }
            else {
                this.index = pos;
            }
        }
        switch (first) {
            case 'd': {
                if (this.readChar() != 'e' || this.readChar() != 'p' || this.readChar() != 'r' || this.readChar() != 'e' || this.readChar() != 'c' || this.readChar() != 'a' || this.readChar() != 't' || this.readChar() != 'e' || this.readChar() != 'd') {
                    break;
                }
                final char c5 = this.readChar();
                if (ScannerHelper.isWhitespace(c5) || c5 == '*') {
                    this.abort = true;
                    this.deprecated = true;
                    this.tagValue = 1;
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    protected boolean parseTag(final int previousPosition) throws InvalidInputException {
        switch (this.tagWaitingForDescription) {
            case 2:
            case 4: {
                if (!this.inlineTagStarted) {
                    final int start = (int)(this.identifierPositionStack[0] >>> 32);
                    final int end = (int)this.identifierPositionStack[this.identifierPtr];
                    this.sourceParser.problemReporter().javadocMissingTagDescriptionAfterReference(start, end, this.sourceParser.modifiers);
                    break;
                }
                break;
            }
            case 0: {
                break;
            }
            default: {
                if (!this.inlineTagStarted) {
                    this.sourceParser.problemReporter().javadocMissingTagDescription(JavadocParser.TAG_NAMES[this.tagWaitingForDescription], this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
                    break;
                }
                break;
            }
        }
        this.tagWaitingForDescription = 0;
        this.tagSourceStart = this.index;
        this.tagSourceEnd = previousPosition;
        this.scanner.startPosition = this.index;
        int currentPosition = this.index;
        final char firstChar = this.readChar();
        switch (firstChar) {
            case ' ':
            case '#':
            case '*':
            case '}': {
                if (this.reportProblems) {
                    this.sourceParser.problemReporter().javadocInvalidTag(previousPosition, currentPosition);
                }
                if (this.textStart == -1) {
                    this.textStart = currentPosition;
                }
                this.scanner.currentCharacter = firstChar;
                return false;
            }
            default: {
                if (ScannerHelper.isWhitespace(firstChar)) {
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocInvalidTag(previousPosition, currentPosition);
                    }
                    if (this.textStart == -1) {
                        this.textStart = currentPosition;
                    }
                    this.scanner.currentCharacter = firstChar;
                    return false;
                }
                char[] tagName = new char[32];
                int length = 0;
                char currentChar = firstChar;
                int tagNameLength = tagName.length;
                boolean validTag = true;
            Label_0441:
                while (true) {
                    if (length == tagNameLength) {
                        System.arraycopy(tagName, 0, tagName = new char[tagNameLength + 32], 0, tagNameLength);
                        tagNameLength = tagName.length;
                    }
                    tagName[length++] = currentChar;
                    currentPosition = this.index;
                    currentChar = this.readChar();
                    switch (currentChar) {
                        case ' ':
                        case '*':
                        case '}': {
                            break Label_0441;
                        }
                        case '#': {
                            validTag = false;
                            continue;
                        }
                        default: {
                            if (ScannerHelper.isWhitespace(currentChar)) {
                                break Label_0441;
                            }
                            continue;
                        }
                    }
                }
                this.tagSourceEnd = currentPosition - 1;
                this.scanner.currentCharacter = currentChar;
                this.scanner.currentPosition = currentPosition;
                this.index = this.tagSourceEnd + 1;
                if (!validTag) {
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocInvalidTag(this.tagSourceStart, this.tagSourceEnd);
                    }
                    if (this.textStart == -1) {
                        this.textStart = this.index;
                    }
                    this.scanner.currentCharacter = currentChar;
                    return false;
                }
                this.tagValue = 100;
                boolean valid = false;
                switch (firstChar) {
                    case 'a': {
                        if (length == JavadocParser.TAG_AUTHOR_LENGTH && CharOperation.equals(JavadocParser.TAG_AUTHOR, tagName, 0, length)) {
                            this.tagValue = 12;
                            this.tagWaitingForDescription = this.tagValue;
                            break;
                        }
                        break;
                    }
                    case 'c': {
                        if (length == JavadocParser.TAG_CATEGORY_LENGTH && CharOperation.equals(JavadocParser.TAG_CATEGORY, tagName, 0, length)) {
                            this.tagValue = 11;
                            if (!this.inlineTagStarted) {
                                valid = this.parseIdentifierTag(false);
                                break;
                            }
                            break;
                        }
                        else {
                            if (length == JavadocParser.TAG_CODE_LENGTH && this.inlineTagStarted && CharOperation.equals(JavadocParser.TAG_CODE, tagName, 0, length)) {
                                this.tagValue = 18;
                                this.tagWaitingForDescription = this.tagValue;
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case 'd': {
                        if (length == JavadocParser.TAG_DEPRECATED_LENGTH && CharOperation.equals(JavadocParser.TAG_DEPRECATED, tagName, 0, length)) {
                            this.deprecated = true;
                            valid = true;
                            this.tagValue = 1;
                            this.tagWaitingForDescription = this.tagValue;
                            break;
                        }
                        if (length == JavadocParser.TAG_DOC_ROOT_LENGTH && CharOperation.equals(JavadocParser.TAG_DOC_ROOT, tagName, 0, length)) {
                            valid = true;
                            this.tagValue = 20;
                            break;
                        }
                        break;
                    }
                    case 'e': {
                        if (length != JavadocParser.TAG_EXCEPTION_LENGTH || !CharOperation.equals(JavadocParser.TAG_EXCEPTION, tagName, 0, length)) {
                            break;
                        }
                        this.tagValue = 5;
                        if (!this.inlineTagStarted) {
                            valid = this.parseThrows();
                            break;
                        }
                        break;
                    }
                    case 'i': {
                        if (length == JavadocParser.TAG_INHERITDOC_LENGTH && CharOperation.equals(JavadocParser.TAG_INHERITDOC, tagName, 0, length)) {
                            switch (this.lastBlockTagValue) {
                                case 0:
                                case 2:
                                case 3:
                                case 4:
                                case 5: {
                                    valid = true;
                                    if (this.reportProblems) {
                                        this.recordInheritedPosition(((long)this.tagSourceStart << 32) + this.tagSourceEnd);
                                    }
                                    if (this.inlineTagStarted) {
                                        this.parseInheritDocTag();
                                        break;
                                    }
                                    break;
                                }
                                default: {
                                    valid = false;
                                    if (this.reportProblems) {
                                        this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
                                        break;
                                    }
                                    break;
                                }
                            }
                            this.tagValue = 9;
                            break;
                        }
                        break;
                    }
                    case 'l': {
                        if (length == JavadocParser.TAG_LINK_LENGTH && CharOperation.equals(JavadocParser.TAG_LINK, tagName, 0, length)) {
                            this.tagValue = 7;
                            if (this.inlineTagStarted || (this.kind & 0x8) != 0x0) {
                                valid = this.parseReference();
                                break;
                            }
                            break;
                        }
                        else if (length == JavadocParser.TAG_LINKPLAIN_LENGTH && CharOperation.equals(JavadocParser.TAG_LINKPLAIN, tagName, 0, length)) {
                            this.tagValue = 8;
                            if (this.inlineTagStarted) {
                                valid = this.parseReference();
                                break;
                            }
                            break;
                        }
                        else {
                            if (length == JavadocParser.TAG_LITERAL_LENGTH && this.inlineTagStarted && CharOperation.equals(JavadocParser.TAG_LITERAL, tagName, 0, length)) {
                                this.tagValue = 19;
                                this.tagWaitingForDescription = this.tagValue;
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case 'p': {
                        if (length != JavadocParser.TAG_PARAM_LENGTH || !CharOperation.equals(JavadocParser.TAG_PARAM, tagName, 0, length)) {
                            break;
                        }
                        this.tagValue = 2;
                        if (!this.inlineTagStarted) {
                            valid = this.parseParam();
                            break;
                        }
                        break;
                    }
                    case 'r': {
                        if (length != JavadocParser.TAG_RETURN_LENGTH || !CharOperation.equals(JavadocParser.TAG_RETURN, tagName, 0, length)) {
                            break;
                        }
                        this.tagValue = 3;
                        if (!this.inlineTagStarted) {
                            valid = this.parseReturn();
                            break;
                        }
                        break;
                    }
                    case 's': {
                        if (length == JavadocParser.TAG_SEE_LENGTH && CharOperation.equals(JavadocParser.TAG_SEE, tagName, 0, length)) {
                            this.tagValue = 6;
                            if (!this.inlineTagStarted) {
                                valid = this.parseReference();
                                break;
                            }
                            break;
                        }
                        else {
                            if (length == JavadocParser.TAG_SERIAL_LENGTH && CharOperation.equals(JavadocParser.TAG_SERIAL, tagName, 0, length)) {
                                this.tagValue = 13;
                                this.tagWaitingForDescription = this.tagValue;
                                break;
                            }
                            if (length == JavadocParser.TAG_SERIAL_DATA_LENGTH && CharOperation.equals(JavadocParser.TAG_SERIAL_DATA, tagName, 0, length)) {
                                this.tagValue = 14;
                                this.tagWaitingForDescription = this.tagValue;
                                break;
                            }
                            if (length == JavadocParser.TAG_SERIAL_FIELD_LENGTH && CharOperation.equals(JavadocParser.TAG_SERIAL_FIELD, tagName, 0, length)) {
                                this.tagValue = 15;
                                this.tagWaitingForDescription = this.tagValue;
                                break;
                            }
                            if (length == JavadocParser.TAG_SINCE_LENGTH && CharOperation.equals(JavadocParser.TAG_SINCE, tagName, 0, length)) {
                                this.tagValue = 16;
                                this.tagWaitingForDescription = this.tagValue;
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case 't': {
                        if (length != JavadocParser.TAG_THROWS_LENGTH || !CharOperation.equals(JavadocParser.TAG_THROWS, tagName, 0, length)) {
                            break;
                        }
                        this.tagValue = 4;
                        if (!this.inlineTagStarted) {
                            valid = this.parseThrows();
                            break;
                        }
                        break;
                    }
                    case 'v': {
                        if (length == JavadocParser.TAG_VALUE_LENGTH && CharOperation.equals(JavadocParser.TAG_VALUE, tagName, 0, length)) {
                            this.tagValue = 10;
                            if (this.sourceLevel >= 3211264L) {
                                if (this.inlineTagStarted) {
                                    valid = this.parseReference();
                                    break;
                                }
                                break;
                            }
                            else if (this.validValuePositions == -1L) {
                                if (this.invalidValuePositions != -1L && this.reportProblems) {
                                    this.sourceParser.problemReporter().javadocUnexpectedTag((int)(this.invalidValuePositions >>> 32), (int)this.invalidValuePositions);
                                }
                                if (valid) {
                                    this.validValuePositions = ((long)this.tagSourceStart << 32) + this.tagSourceEnd;
                                    this.invalidValuePositions = -1L;
                                    break;
                                }
                                this.invalidValuePositions = ((long)this.tagSourceStart << 32) + this.tagSourceEnd;
                                break;
                            }
                            else {
                                if (this.reportProblems) {
                                    this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
                                    break;
                                }
                                break;
                            }
                        }
                        else {
                            if (length == JavadocParser.TAG_VERSION_LENGTH && CharOperation.equals(JavadocParser.TAG_VERSION, tagName, 0, length)) {
                                this.tagValue = 17;
                                this.tagWaitingForDescription = this.tagValue;
                                break;
                            }
                            this.createTag();
                            break;
                        }
                        break;
                    }
                    default: {
                        this.createTag();
                        break;
                    }
                }
                this.textStart = this.index;
                if (this.tagValue != 100) {
                    if (!this.inlineTagStarted) {
                        this.lastBlockTagValue = this.tagValue;
                    }
                    if ((this.inlineTagStarted && JavadocParser.JAVADOC_TAG_TYPE[this.tagValue] == 2) || (!this.inlineTagStarted && JavadocParser.JAVADOC_TAG_TYPE[this.tagValue] == 1)) {
                        valid = false;
                        this.tagValue = 100;
                        this.tagWaitingForDescription = 0;
                        if (this.reportProblems) {
                            this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
                        }
                    }
                }
                return valid;
            }
        }
    }
    
    protected void parseInheritDocTag() {
    }
    
    @Override
    protected boolean parseParam() throws InvalidInputException {
        final boolean valid = super.parseParam();
        this.tagWaitingForDescription = ((valid && this.reportProblems) ? 2 : 0);
        return valid;
    }
    
    @Override
    protected boolean pushParamName(final boolean isTypeParam) {
        ASTNode nameRef = null;
        if (isTypeParam) {
            final JavadocSingleTypeReference ref = (JavadocSingleTypeReference)(nameRef = new JavadocSingleTypeReference(this.identifierStack[1], this.identifierPositionStack[1], this.tagSourceStart, this.tagSourceEnd));
        }
        else {
            final JavadocSingleNameReference ref2 = (JavadocSingleNameReference)(nameRef = new JavadocSingleNameReference(this.identifierStack[0], this.identifierPositionStack[0], this.tagSourceStart, this.tagSourceEnd));
        }
        if (this.astLengthPtr == -1) {
            this.pushOnAstStack(nameRef, true);
        }
        else {
            if (!isTypeParam) {
                for (int i = 1; i <= this.astLengthPtr; i += 3) {
                    if (this.astLengthStack[i] != 0) {
                        if (this.reportProblems) {
                            this.sourceParser.problemReporter().javadocUnexpectedTag(this.tagSourceStart, this.tagSourceEnd);
                        }
                        if (this.invalidParamReferencesPtr == -1L) {
                            this.invalidParamReferencesStack = new JavadocSingleNameReference[10];
                        }
                        final int stackLength = this.invalidParamReferencesStack.length;
                        if (++this.invalidParamReferencesPtr >= stackLength) {
                            System.arraycopy(this.invalidParamReferencesStack, 0, this.invalidParamReferencesStack = new JavadocSingleNameReference[stackLength + 10], 0, stackLength);
                        }
                        this.invalidParamReferencesStack[this.invalidParamReferencesPtr] = nameRef;
                        return false;
                    }
                }
            }
            switch (this.astLengthPtr % 3) {
                case 0: {
                    this.pushOnAstStack(nameRef, false);
                    break;
                }
                case 2: {
                    this.pushOnAstStack(nameRef, true);
                    break;
                }
                default: {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    protected boolean pushSeeRef(final Object statement) {
        if (this.astLengthPtr == -1) {
            this.pushOnAstStack(null, true);
            this.pushOnAstStack(null, true);
            this.pushOnAstStack(statement, true);
        }
        else {
            switch (this.astLengthPtr % 3) {
                case 0: {
                    this.pushOnAstStack(null, true);
                    this.pushOnAstStack(statement, true);
                    break;
                }
                case 1: {
                    this.pushOnAstStack(statement, true);
                    break;
                }
                case 2: {
                    this.pushOnAstStack(statement, false);
                    break;
                }
                default: {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    protected void pushText(final int start, final int end) {
        this.tagWaitingForDescription = 0;
    }
    
    @Override
    protected boolean pushThrowName(final Object typeRef) {
        if (this.astLengthPtr == -1) {
            this.pushOnAstStack(null, true);
            this.pushOnAstStack(typeRef, true);
        }
        else {
            switch (this.astLengthPtr % 3) {
                case 0: {
                    this.pushOnAstStack(typeRef, true);
                    break;
                }
                case 1: {
                    this.pushOnAstStack(typeRef, false);
                    break;
                }
                case 2: {
                    this.pushOnAstStack(null, true);
                    this.pushOnAstStack(typeRef, true);
                    break;
                }
                default: {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    protected void refreshInlineTagPosition(final int previousPosition) {
        if (this.tagWaitingForDescription != 0) {
            this.sourceParser.problemReporter().javadocMissingTagDescription(JavadocParser.TAG_NAMES[this.tagWaitingForDescription], this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
            this.tagWaitingForDescription = 0;
        }
    }
    
    @Override
    protected void refreshReturnStatement() {
        final JavadocReturnStatement javadocReturnStatement = (JavadocReturnStatement)this.returnStatement;
        javadocReturnStatement.bits &= 0xFFFBFFFF;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("check javadoc: ").append(this.checkDocComment).append("\n");
        buffer.append("javadoc: ").append(this.docComment).append("\n");
        buffer.append(super.toString());
        return buffer.toString();
    }
    
    @Override
    protected void updateDocComment() {
        switch (this.tagWaitingForDescription) {
            case 2:
            case 4: {
                if (!this.inlineTagStarted) {
                    final int start = (int)(this.identifierPositionStack[0] >>> 32);
                    final int end = (int)this.identifierPositionStack[this.identifierPtr];
                    this.sourceParser.problemReporter().javadocMissingTagDescriptionAfterReference(start, end, this.sourceParser.modifiers);
                    break;
                }
                break;
            }
            case 0: {
                break;
            }
            default: {
                if (!this.inlineTagStarted) {
                    this.sourceParser.problemReporter().javadocMissingTagDescription(JavadocParser.TAG_NAMES[this.tagWaitingForDescription], this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
                    break;
                }
                break;
            }
        }
        this.tagWaitingForDescription = 0;
        if (this.inheritedPositions != null && this.inheritedPositionsPtr != this.inheritedPositions.length) {
            System.arraycopy(this.inheritedPositions, 0, this.inheritedPositions = new long[this.inheritedPositionsPtr], 0, this.inheritedPositionsPtr);
        }
        this.docComment.inheritedPositions = this.inheritedPositions;
        this.docComment.valuePositions = ((this.validValuePositions != -1L) ? this.validValuePositions : this.invalidValuePositions);
        if (this.returnStatement != null) {
            this.docComment.returnStatement = (JavadocReturnStatement)this.returnStatement;
        }
        if (this.invalidParamReferencesPtr >= 0) {
            this.docComment.invalidParameters = new JavadocSingleNameReference[this.invalidParamReferencesPtr + 1];
            System.arraycopy(this.invalidParamReferencesStack, 0, this.docComment.invalidParameters, 0, this.invalidParamReferencesPtr + 1);
        }
        if (this.astLengthPtr == -1) {
            return;
        }
        final int[] sizes = new int[3];
        for (int i = 0; i <= this.astLengthPtr; ++i) {
            final int[] array = sizes;
            final int n = i % 3;
            array[n] += this.astLengthStack[i];
        }
        this.docComment.seeReferences = new Expression[sizes[2]];
        this.docComment.exceptionReferences = new TypeReference[sizes[1]];
        int paramRefPtr = sizes[0];
        this.docComment.paramReferences = new JavadocSingleNameReference[paramRefPtr];
        int paramTypeParamPtr = sizes[0];
        this.docComment.paramTypeParameters = new JavadocSingleTypeReference[paramTypeParamPtr];
        while (this.astLengthPtr >= 0) {
            final int ptr = this.astLengthPtr % 3;
            switch (ptr) {
                default: {
                    continue;
                }
                case 2: {
                    for (int size = this.astLengthStack[this.astLengthPtr--], j = 0; j < size; ++j) {
                        final Expression[] seeReferences = this.docComment.seeReferences;
                        final int[] array2 = sizes;
                        final int n2 = ptr;
                        seeReferences[--array2[n2]] = (Expression)this.astStack[this.astPtr--];
                    }
                    continue;
                }
                case 1: {
                    for (int size = this.astLengthStack[this.astLengthPtr--], j = 0; j < size; ++j) {
                        final TypeReference[] exceptionReferences = this.docComment.exceptionReferences;
                        final int[] array3 = sizes;
                        final int n3 = ptr;
                        exceptionReferences[--array3[n3]] = (TypeReference)this.astStack[this.astPtr--];
                    }
                    continue;
                }
                case 0: {
                    for (int size = this.astLengthStack[this.astLengthPtr--], j = 0; j < size; ++j) {
                        final Expression reference = (Expression)this.astStack[this.astPtr--];
                        if (reference instanceof JavadocSingleNameReference) {
                            this.docComment.paramReferences[--paramRefPtr] = (JavadocSingleNameReference)reference;
                        }
                        else if (reference instanceof JavadocSingleTypeReference) {
                            this.docComment.paramTypeParameters[--paramTypeParamPtr] = (JavadocSingleTypeReference)reference;
                        }
                    }
                    continue;
                }
            }
        }
        if (paramRefPtr == 0) {
            this.docComment.paramTypeParameters = null;
        }
        else if (paramTypeParamPtr == 0) {
            this.docComment.paramReferences = null;
        }
        else {
            final int size2 = sizes[0];
            System.arraycopy(this.docComment.paramReferences, paramRefPtr, this.docComment.paramReferences = new JavadocSingleNameReference[size2 - paramRefPtr], 0, size2 - paramRefPtr);
            System.arraycopy(this.docComment.paramTypeParameters, paramTypeParamPtr, this.docComment.paramTypeParameters = new JavadocSingleTypeReference[size2 - paramTypeParamPtr], 0, size2 - paramTypeParamPtr);
        }
    }
}
