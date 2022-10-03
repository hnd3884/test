package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.core.compiler.CharOperation;
import java.util.ArrayList;
import org.eclipse.jdt.internal.compiler.util.Util;
import java.util.List;
import org.eclipse.jdt.core.compiler.InvalidInputException;

public abstract class AbstractCommentParser implements JavadocTagConstants
{
    public static final int COMPIL_PARSER = 1;
    public static final int DOM_PARSER = 2;
    public static final int SELECTION_PARSER = 4;
    public static final int COMPLETION_PARSER = 8;
    public static final int SOURCE_PARSER = 16;
    public static final int FORMATTER_COMMENT_PARSER = 32;
    protected static final int PARSER_KIND = 255;
    protected static final int TEXT_PARSE = 256;
    protected static final int TEXT_VERIF = 512;
    protected static final int QUALIFIED_NAME_RECOVERY = 1;
    protected static final int ARGUMENT_RECOVERY = 2;
    protected static final int ARGUMENT_TYPE_RECOVERY = 3;
    protected static final int EMPTY_ARGUMENT_RECOVERY = 4;
    public Scanner scanner;
    public char[] source;
    protected Parser sourceParser;
    private int currentTokenType;
    public boolean checkDocComment;
    public boolean setJavadocPositions;
    public boolean reportProblems;
    protected long complianceLevel;
    protected long sourceLevel;
    protected long[] inheritedPositions;
    protected int inheritedPositionsPtr;
    private static final int INHERITED_POSITIONS_ARRAY_INCREMENT = 4;
    protected boolean deprecated;
    protected Object returnStatement;
    protected int javadocStart;
    protected int javadocEnd;
    protected int javadocTextStart;
    protected int javadocTextEnd;
    protected int firstTagPosition;
    protected int index;
    protected int lineEnd;
    protected int tokenPreviousPosition;
    protected int lastIdentifierEndPosition;
    protected int starPosition;
    protected int textStart;
    protected int memberStart;
    protected int tagSourceStart;
    protected int tagSourceEnd;
    protected int inlineTagStart;
    protected int[] lineEnds;
    protected boolean lineStarted;
    protected boolean inlineTagStarted;
    protected boolean abort;
    protected int kind;
    protected int tagValue;
    protected int lastBlockTagValue;
    private int linePtr;
    private int lastLinePtr;
    protected int identifierPtr;
    protected char[][] identifierStack;
    protected int identifierLengthPtr;
    protected int[] identifierLengthStack;
    protected long[] identifierPositionStack;
    protected static final int AST_STACK_INCREMENT = 10;
    protected int astPtr;
    protected Object[] astStack;
    protected int astLengthPtr;
    protected int[] astLengthStack;
    
    protected AbstractCommentParser(final Parser sourceParser) {
        this.currentTokenType = -1;
        this.checkDocComment = false;
        this.setJavadocPositions = false;
        this.javadocTextEnd = -1;
        this.lineStarted = false;
        this.inlineTagStarted = false;
        this.abort = false;
        this.tagValue = 0;
        this.lastBlockTagValue = 0;
        this.sourceParser = sourceParser;
        this.scanner = new Scanner(false, false, false, 3080192L, null, null, true);
        this.identifierStack = new char[20][];
        this.identifierPositionStack = new long[20];
        this.identifierLengthStack = new int[10];
        this.astStack = new Object[30];
        this.astLengthStack = new int[20];
        this.reportProblems = (sourceParser != null);
        if (sourceParser != null) {
            this.checkDocComment = this.sourceParser.options.docCommentSupport;
            this.sourceLevel = this.sourceParser.options.sourceLevel;
            this.scanner.sourceLevel = this.sourceLevel;
            this.complianceLevel = this.sourceParser.options.complianceLevel;
        }
    }
    
    protected boolean commentParse() {
        boolean validComment = true;
        try {
            this.astLengthPtr = -1;
            this.astPtr = -1;
            this.identifierPtr = -1;
            this.currentTokenType = -1;
            this.setInlineTagStarted(false);
            this.inlineTagStart = -1;
            this.lineStarted = false;
            this.returnStatement = null;
            this.inheritedPositions = null;
            this.lastBlockTagValue = 0;
            this.deprecated = false;
            this.lastLinePtr = this.getLineNumber(this.javadocEnd);
            this.textStart = -1;
            this.abort = false;
            char previousChar = '\0';
            int invalidTagLineEnd = -1;
            int invalidInlineTagLineEnd = -1;
            boolean lineHasStar = true;
            final boolean verifText = (this.kind & 0x200) != 0x0;
            final boolean isDomParser = (this.kind & 0x2) != 0x0;
            final boolean isFormatterParser = (this.kind & 0x20) != 0x0;
            int lastStarPosition = -1;
            this.linePtr = this.getLineNumber(this.firstTagPosition);
            int realStart = (this.linePtr == 1) ? this.javadocStart : (this.scanner.getLineEnd(this.linePtr - 1) + 1);
            if (realStart < this.javadocStart) {
                realStart = this.javadocStart;
            }
            this.scanner.resetTo(realStart, this.javadocEnd);
            if ((this.index = realStart) == this.javadocStart) {
                this.readChar();
                this.readChar();
            }
            int previousPosition = this.index;
            char nextCharacter = '\0';
            if (realStart == this.javadocStart) {
                nextCharacter = this.readChar();
                while (this.peekChar() == '*') {
                    nextCharacter = this.readChar();
                }
                this.javadocTextStart = this.index;
            }
            this.lineEnd = ((this.linePtr == this.lastLinePtr) ? this.javadocEnd : (this.scanner.getLineEnd(this.linePtr) - 1));
            this.javadocTextEnd = this.javadocEnd - 2;
            int textEndPosition = -1;
            while (!this.abort && this.index < this.javadocEnd) {
                previousPosition = this.index;
                previousChar = nextCharacter;
                if (this.index > this.lineEnd + 1) {
                    this.updateLineEnd();
                }
                if (this.currentTokenType < 0) {
                    nextCharacter = this.readChar();
                }
                else {
                    previousPosition = this.scanner.getCurrentTokenStartPosition();
                    switch (this.currentTokenType) {
                        case 32: {
                            nextCharacter = '}';
                            break;
                        }
                        case 6: {
                            nextCharacter = '*';
                            break;
                        }
                        default: {
                            nextCharacter = this.scanner.currentCharacter;
                            break;
                        }
                    }
                    this.consumeToken();
                }
                switch (nextCharacter) {
                    case '@': {
                        if (!this.lineStarted || previousChar == '{') {
                            if (this.inlineTagStarted) {
                                this.setInlineTagStarted(false);
                                if (this.reportProblems) {
                                    final int end = (previousPosition < invalidInlineTagLineEnd) ? previousPosition : invalidInlineTagLineEnd;
                                    this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, end);
                                }
                                validComment = false;
                                if (this.textStart != -1 && this.textStart < textEndPosition) {
                                    this.pushText(this.textStart, textEndPosition);
                                }
                                if (isDomParser || isFormatterParser) {
                                    this.refreshInlineTagPosition(textEndPosition);
                                }
                            }
                            if (previousChar == '{') {
                                if (this.textStart != -1 && this.textStart < textEndPosition) {
                                    this.pushText(this.textStart, textEndPosition);
                                }
                                this.setInlineTagStarted(true);
                                invalidInlineTagLineEnd = this.lineEnd;
                            }
                            else if (this.textStart != -1 && this.textStart < invalidTagLineEnd) {
                                this.pushText(this.textStart, invalidTagLineEnd);
                            }
                            this.scanner.resetTo(this.index, this.javadocEnd);
                            this.currentTokenType = -1;
                            try {
                                if (!this.parseTag(previousPosition)) {
                                    validComment = false;
                                    if (isDomParser) {
                                        this.createTag();
                                    }
                                    this.textStart = this.tagSourceEnd + 1;
                                    invalidTagLineEnd = this.lineEnd;
                                    textEndPosition = this.index;
                                }
                            }
                            catch (final InvalidInputException ex) {
                                this.consumeToken();
                            }
                        }
                        else {
                            textEndPosition = this.index;
                            if (verifText && this.tagValue == 3 && this.returnStatement != null) {
                                this.refreshReturnStatement();
                            }
                            else if (isFormatterParser && this.textStart == -1) {
                                this.textStart = previousPosition;
                            }
                        }
                        this.lineStarted = true;
                        continue;
                    }
                    case '\n':
                    case '\r': {
                        if (this.lineStarted) {
                            if (isFormatterParser && !ScannerHelper.isWhitespace(previousChar)) {
                                textEndPosition = previousPosition;
                            }
                            if (this.textStart != -1 && this.textStart < textEndPosition) {
                                this.pushText(this.textStart, textEndPosition);
                            }
                        }
                        this.lineStarted = false;
                        lineHasStar = false;
                        this.textStart = -1;
                        continue;
                    }
                    case '}': {
                        if (verifText && this.tagValue == 3 && this.returnStatement != null) {
                            this.refreshReturnStatement();
                        }
                        if (this.inlineTagStarted) {
                            textEndPosition = this.index - 1;
                            if (this.lineStarted && this.textStart != -1 && this.textStart < textEndPosition) {
                                this.pushText(this.textStart, textEndPosition);
                            }
                            this.refreshInlineTagPosition(previousPosition);
                            if (!isFormatterParser) {
                                this.textStart = this.index;
                            }
                            this.setInlineTagStarted(false);
                        }
                        else if (!this.lineStarted) {
                            this.textStart = previousPosition;
                        }
                        this.lineStarted = true;
                        textEndPosition = this.index;
                        continue;
                    }
                    case '{': {
                        if (verifText && this.tagValue == 3 && this.returnStatement != null) {
                            this.refreshReturnStatement();
                        }
                        if (this.inlineTagStarted) {
                            this.setInlineTagStarted(false);
                            if (this.reportProblems) {
                                final int end = (previousPosition < invalidInlineTagLineEnd) ? previousPosition : invalidInlineTagLineEnd;
                                this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, end);
                            }
                            if (this.lineStarted && this.textStart != -1 && this.textStart < textEndPosition) {
                                this.pushText(this.textStart, textEndPosition);
                            }
                            this.refreshInlineTagPosition(textEndPosition);
                            textEndPosition = this.index;
                        }
                        else if (this.peekChar() != '@') {
                            if (this.textStart == -1) {
                                this.textStart = previousPosition;
                            }
                            textEndPosition = this.index;
                        }
                        if (!this.lineStarted) {
                            this.textStart = previousPosition;
                        }
                        this.lineStarted = true;
                        this.inlineTagStart = previousPosition;
                        continue;
                    }
                    case '*': {
                        lastStarPosition = previousPosition;
                        if (previousChar == '*') {
                            continue;
                        }
                        this.starPosition = previousPosition;
                        if (!isDomParser && !isFormatterParser) {
                            continue;
                        }
                        if (lineHasStar) {
                            this.lineStarted = true;
                            if (this.textStart == -1) {
                                this.textStart = previousPosition;
                                if (this.index <= this.javadocTextEnd) {
                                    textEndPosition = this.index;
                                }
                            }
                        }
                        if (!this.lineStarted) {
                            lineHasStar = true;
                            continue;
                        }
                        continue;
                    }
                    case '\t':
                    case '\f':
                    case ' ': {
                        if (isFormatterParser) {
                            if (!ScannerHelper.isWhitespace(previousChar)) {
                                textEndPosition = previousPosition;
                                continue;
                            }
                            continue;
                        }
                        else {
                            if (this.lineStarted && isDomParser) {
                                textEndPosition = this.index;
                                continue;
                            }
                            continue;
                        }
                        break;
                    }
                    case '/': {
                        if (previousChar == '*') {
                            continue;
                        }
                        break;
                    }
                }
                if (isFormatterParser && nextCharacter == '<') {
                    final int initialIndex = this.index;
                    this.scanner.resetTo(this.index, this.javadocEnd);
                    if (!ScannerHelper.isWhitespace(previousChar)) {
                        textEndPosition = previousPosition;
                    }
                    if (this.parseHtmlTag(previousPosition, textEndPosition)) {
                        continue;
                    }
                    if (this.abort) {
                        return false;
                    }
                    this.scanner.currentPosition = initialIndex;
                    this.index = initialIndex;
                }
                if (verifText && this.tagValue == 3 && this.returnStatement != null) {
                    this.refreshReturnStatement();
                }
                if (!this.lineStarted || this.textStart == -1) {
                    this.textStart = previousPosition;
                }
                this.lineStarted = true;
                textEndPosition = this.index;
            }
            this.javadocTextEnd = this.starPosition - 1;
            if (this.inlineTagStarted) {
                if (this.reportProblems) {
                    int end = (this.javadocTextEnd < invalidInlineTagLineEnd) ? this.javadocTextEnd : invalidInlineTagLineEnd;
                    if (this.index >= this.javadocEnd) {
                        end = invalidInlineTagLineEnd;
                    }
                    this.sourceParser.problemReporter().javadocUnterminatedInlineTag(this.inlineTagStart, end);
                }
                if (this.lineStarted && this.textStart != -1 && this.textStart < textEndPosition) {
                    this.pushText(this.textStart, textEndPosition);
                }
                this.refreshInlineTagPosition(textEndPosition);
                this.setInlineTagStarted(false);
            }
            else if (this.lineStarted && this.textStart != -1 && this.textStart <= textEndPosition && (this.textStart < this.starPosition || this.starPosition == lastStarPosition)) {
                this.pushText(this.textStart, textEndPosition);
            }
            this.updateDocComment();
        }
        catch (final Exception ex2) {
            validComment = false;
        }
        return validComment;
    }
    
    protected void consumeToken() {
        this.currentTokenType = -1;
        this.updateLineEnd();
    }
    
    protected abstract Object createArgumentReference(final char[] p0, final int p1, final boolean p2, final Object p3, final long[] p4, final long p5) throws InvalidInputException;
    
    protected boolean createFakeReference(final int start) {
        return true;
    }
    
    protected abstract Object createFieldReference(final Object p0) throws InvalidInputException;
    
    protected abstract Object createMethodReference(final Object p0, final List p1) throws InvalidInputException;
    
    protected Object createReturnStatement() {
        return null;
    }
    
    protected abstract void createTag();
    
    protected abstract Object createTypeReference(final int p0);
    
    private int getIndexPosition() {
        if (this.index > this.lineEnd) {
            return this.lineEnd;
        }
        return this.index - 1;
    }
    
    private int getLineNumber(final int position) {
        if (this.scanner.linePtr != -1) {
            return Util.getLineNumber(position, this.scanner.lineEnds, 0, this.scanner.linePtr);
        }
        if (this.lineEnds == null) {
            return 1;
        }
        return Util.getLineNumber(position, this.lineEnds, 0, this.lineEnds.length - 1);
    }
    
    private int getTokenEndPosition() {
        if (this.scanner.getCurrentTokenEndPosition() > this.lineEnd) {
            return this.lineEnd;
        }
        return this.scanner.getCurrentTokenEndPosition();
    }
    
    protected int getCurrentTokenType() {
        return this.currentTokenType;
    }
    
    protected Object parseArguments(final Object receiver) throws InvalidInputException {
        int modulo = 0;
        int iToken = 0;
        char[] argName = null;
        final List arguments = new ArrayList(10);
        final int start = this.scanner.getCurrentTokenStartPosition();
        Object typeRef = null;
        int dim = 0;
        boolean isVarargs = false;
        final long[] dimPositions = new long[20];
        char[] name = null;
        long argNamePos = -1L;
    Label_0669:
        while (this.index < this.scanner.eofPosition) {
            try {
                typeRef = this.parseQualifiedName(false);
                if (this.abort) {
                    return null;
                }
            }
            catch (final InvalidInputException ex) {
                break;
            }
            final boolean firstArg = modulo == 0;
            if (firstArg) {
                if (iToken != 0) {
                    break;
                }
            }
            else if (iToken % modulo != 0) {
                break;
            }
            if (typeRef == null) {
                if (!firstArg || this.currentTokenType != 25) {
                    break;
                }
                if (!this.verifySpaceOrEndComment()) {
                    int end = (this.starPosition == -1) ? this.lineEnd : this.starPosition;
                    if (this.source[end] == '\n') {
                        --end;
                    }
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocMalformedSeeReference(start, end);
                    }
                    return null;
                }
                this.lineStarted = true;
                return this.createMethodReference(receiver, null);
            }
            else {
                ++iToken;
                dim = 0;
                isVarargs = false;
                if (this.readToken() == 10) {
                    while (this.readToken() == 10) {
                        final int dimStart = this.scanner.getCurrentTokenStartPosition();
                        this.consumeToken();
                        if (this.readToken() != 64) {
                            break Label_0669;
                        }
                        this.consumeToken();
                        dimPositions[dim++] = ((long)dimStart << 32) + this.scanner.getCurrentTokenEndPosition();
                    }
                }
                else if (this.readToken() == 113) {
                    final int dimStart = this.scanner.getCurrentTokenStartPosition();
                    dimPositions[dim++] = ((long)dimStart << 32) + this.scanner.getCurrentTokenEndPosition();
                    this.consumeToken();
                    isVarargs = true;
                }
                argNamePos = -1L;
                if (this.readToken() == 22) {
                    this.consumeToken();
                    if (firstArg) {
                        if (iToken != 1) {
                            break;
                        }
                    }
                    else if (iToken % modulo != 1) {
                        break;
                    }
                    if (argName == null && !firstArg) {
                        break;
                    }
                    argName = this.scanner.getCurrentIdentifierSource();
                    argNamePos = ((long)this.scanner.getCurrentTokenStartPosition() << 32) + this.scanner.getCurrentTokenEndPosition();
                    ++iToken;
                }
                else if (argName != null) {
                    break;
                }
                if (firstArg) {
                    modulo = iToken + 1;
                }
                else if (iToken % modulo != modulo - 1) {
                    break;
                }
                final int token = this.readToken();
                name = ((argName == null) ? CharOperation.NO_CHAR : argName);
                if (token == 33) {
                    final Object argument = this.createArgumentReference(name, dim, isVarargs, typeRef, dimPositions, argNamePos);
                    if (this.abort) {
                        return null;
                    }
                    arguments.add(argument);
                    this.consumeToken();
                    ++iToken;
                }
                else {
                    if (token != 25) {
                        break;
                    }
                    if (!this.verifySpaceOrEndComment()) {
                        int end2 = (this.starPosition == -1) ? this.lineEnd : this.starPosition;
                        if (this.source[end2] == '\n') {
                            --end2;
                        }
                        if (this.reportProblems) {
                            this.sourceParser.problemReporter().javadocMalformedSeeReference(start, end2);
                        }
                        return null;
                    }
                    final Object argument = this.createArgumentReference(name, dim, isVarargs, typeRef, dimPositions, argNamePos);
                    if (this.abort) {
                        return null;
                    }
                    arguments.add(argument);
                    this.consumeToken();
                    return this.createMethodReference(receiver, arguments);
                }
            }
        }
        throw new InvalidInputException();
    }
    
    protected boolean parseHtmlTag(final int previousPosition, final int endTextPosition) throws InvalidInputException {
        return false;
    }
    
    protected boolean parseHref() throws InvalidInputException {
        final boolean skipComments = this.scanner.skipComments;
        this.scanner.skipComments = true;
        try {
            int start = this.scanner.getCurrentTokenStartPosition();
            char currentChar = this.readChar();
            if (currentChar == 'a' || currentChar == 'A') {
                this.scanner.currentPosition = this.index;
                if (this.readToken() == 22) {
                    this.consumeToken();
                    try {
                        if (CharOperation.equals(this.scanner.getCurrentIdentifierSource(), AbstractCommentParser.HREF_TAG, false) && this.readToken() == 70) {
                            this.consumeToken();
                            if (this.readToken() == 48) {
                                this.consumeToken();
                                while (this.index < this.javadocEnd) {
                                    while (this.readToken() != 15) {
                                        if (this.scanner.currentPosition >= this.scanner.eofPosition || this.scanner.currentCharacter == '@' || (this.inlineTagStarted && this.scanner.currentCharacter == '}')) {
                                            this.index = this.tokenPreviousPosition;
                                            this.scanner.currentPosition = this.tokenPreviousPosition;
                                            this.currentTokenType = -1;
                                            if (this.tagValue != 10 && this.reportProblems) {
                                                this.sourceParser.problemReporter().javadocInvalidSeeHref(start, this.lineEnd);
                                            }
                                            return false;
                                        }
                                        this.currentTokenType = -1;
                                    }
                                    this.consumeToken();
                                    while (this.readToken() != 11) {
                                        if (this.scanner.currentPosition >= this.scanner.eofPosition || this.scanner.currentCharacter == '@' || (this.inlineTagStarted && this.scanner.currentCharacter == '}')) {
                                            this.index = this.tokenPreviousPosition;
                                            this.scanner.currentPosition = this.tokenPreviousPosition;
                                            this.currentTokenType = -1;
                                            if (this.tagValue != 10 && this.reportProblems) {
                                                this.sourceParser.problemReporter().javadocInvalidSeeHref(start, this.lineEnd);
                                            }
                                            return false;
                                        }
                                        this.consumeToken();
                                    }
                                    this.consumeToken();
                                    start = this.scanner.getCurrentTokenStartPosition();
                                    currentChar = this.readChar();
                                    if (currentChar == '/') {
                                        currentChar = this.readChar();
                                        if (currentChar == 'a' || currentChar == 'A') {
                                            currentChar = this.readChar();
                                            if (currentChar == '>') {
                                                return true;
                                            }
                                        }
                                    }
                                    if (currentChar == '\r' || currentChar == '\n' || currentChar == '\t') {
                                        break;
                                    }
                                    if (currentChar == ' ') {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    catch (final InvalidInputException ex) {}
                }
            }
            this.index = this.tokenPreviousPosition;
            this.scanner.currentPosition = this.tokenPreviousPosition;
            this.currentTokenType = -1;
            if (this.tagValue != 10 && this.reportProblems) {
                this.sourceParser.problemReporter().javadocInvalidSeeHref(start, this.lineEnd);
            }
        }
        finally {
            this.scanner.skipComments = skipComments;
        }
        this.scanner.skipComments = skipComments;
        return false;
    }
    
    protected boolean parseIdentifierTag(final boolean report) {
        final int token = this.readTokenSafely();
        switch (token) {
            case 22: {
                this.pushIdentifier(true, false);
                return true;
            }
            default: {
                if (report) {
                    this.sourceParser.problemReporter().javadocMissingIdentifier(this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
                }
                return false;
            }
        }
    }
    
    protected Object parseMember(final Object receiver) throws InvalidInputException {
        this.identifierPtr = -1;
        this.identifierLengthPtr = -1;
        int start = this.scanner.getCurrentTokenStartPosition();
        this.memberStart = start;
        if (this.readToken() != 22) {
            int end = this.getTokenEndPosition() - 1;
            end = ((start > end) ? start : end);
            if (this.reportProblems) {
                this.sourceParser.problemReporter().javadocInvalidReference(start, end);
            }
            this.index = this.tokenPreviousPosition;
            this.scanner.currentPosition = this.tokenPreviousPosition;
            this.currentTokenType = -1;
            return null;
        }
        if (this.scanner.currentCharacter == '.') {
            this.parseQualifiedName(true);
        }
        else {
            this.consumeToken();
            this.pushIdentifier(true, false);
        }
        final int previousPosition = this.index;
        if (this.readToken() == 24) {
            this.consumeToken();
            start = this.scanner.getCurrentTokenStartPosition();
            try {
                return this.parseArguments(receiver);
            }
            catch (final InvalidInputException ex) {
                int end2 = (this.scanner.getCurrentTokenEndPosition() < this.lineEnd) ? this.scanner.getCurrentTokenEndPosition() : this.scanner.getCurrentTokenStartPosition();
                end2 = ((end2 < this.lineEnd) ? end2 : this.lineEnd);
                if (this.reportProblems) {
                    this.sourceParser.problemReporter().javadocInvalidSeeReferenceArgs(start, end2);
                }
                return null;
            }
        }
        this.index = previousPosition;
        this.scanner.currentPosition = previousPosition;
        this.currentTokenType = -1;
        if (!this.verifySpaceOrEndComment()) {
            int end2 = (this.starPosition == -1) ? this.lineEnd : this.starPosition;
            if (this.source[end2] == '\n') {
                --end2;
            }
            if (this.reportProblems) {
                this.sourceParser.problemReporter().javadocMalformedSeeReference(start, end2);
            }
            return null;
        }
        return this.createFieldReference(receiver);
    }
    
    protected boolean parseParam() throws InvalidInputException {
        int start = this.tagSourceStart;
        int end = this.tagSourceEnd;
        final boolean tokenWhiteSpace = this.scanner.tokenizeWhiteSpace;
        this.scanner.tokenizeWhiteSpace = true;
        try {
            final boolean isCompletionParser = (this.kind & 0x8) != 0x0;
            if (this.scanner.currentCharacter != ' ' && !ScannerHelper.isWhitespace(this.scanner.currentCharacter)) {
                if (this.reportProblems) {
                    this.sourceParser.problemReporter().javadocInvalidTag(start, this.scanner.getCurrentTokenEndPosition());
                }
                if (!isCompletionParser) {
                    this.scanner.currentPosition = start;
                    this.index = start;
                }
                this.currentTokenType = -1;
                return false;
            }
            this.identifierPtr = -1;
            this.identifierLengthPtr = -1;
            boolean hasMultiLines = this.scanner.currentPosition > this.lineEnd + 1;
            boolean isTypeParam = false;
            boolean valid = true;
            boolean empty = true;
            final boolean mayBeGeneric = this.sourceLevel >= 3211264L;
            int token = -1;
            Label_0542: {
            Label_0411_Outer:
                while (true) {
                    this.currentTokenType = -1;
                    try {
                        token = this.readToken();
                    }
                    catch (final InvalidInputException ex) {
                        valid = false;
                    }
                    while (true) {
                        switch (token) {
                            case 22: {
                                if (valid) {
                                    this.pushIdentifier(true, false);
                                    start = this.scanner.getCurrentTokenStartPosition();
                                    end = (hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition());
                                    break Label_0542;
                                }
                            }
                            case 11: {
                                if (valid && mayBeGeneric) {
                                    this.pushIdentifier(true, true);
                                    start = this.scanner.getCurrentTokenStartPosition();
                                    end = (hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition());
                                    isTypeParam = true;
                                    break Label_0542;
                                }
                                break;
                            }
                            case 1000: {
                                if (this.scanner.currentPosition > this.lineEnd + 1) {
                                    hasMultiLines = true;
                                }
                                if (valid) {
                                    continue Label_0411_Outer;
                                }
                                break Label_0411_Outer;
                            }
                            case 60: {
                                break Label_0411_Outer;
                            }
                        }
                        if (token == 18) {
                            isTypeParam = true;
                        }
                        if (valid && !hasMultiLines) {
                            start = this.scanner.getCurrentTokenStartPosition();
                        }
                        valid = false;
                        if (hasMultiLines) {
                            end = this.lineEnd;
                            continue;
                        }
                        break;
                    }
                    empty = false;
                    end = (hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition());
                }
                if (this.reportProblems) {
                    if (empty) {
                        this.sourceParser.problemReporter().javadocMissingParamName(start, end, this.sourceParser.modifiers);
                    }
                    else if (mayBeGeneric && isTypeParam) {
                        this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
                    }
                    else {
                        this.sourceParser.problemReporter().javadocInvalidParamTagName(start, end);
                    }
                }
                if (!isCompletionParser) {
                    this.scanner.currentPosition = start;
                    this.index = start;
                }
                this.currentTokenType = -1;
                return false;
            }
            if (isTypeParam && mayBeGeneric) {
                Block_35: {
                Label_0632:
                    while (true) {
                        this.currentTokenType = -1;
                        try {
                            token = this.readToken();
                        }
                        catch (final InvalidInputException ex2) {
                            valid = false;
                        }
                        switch (token) {
                            case 1000: {
                                if (valid && this.scanner.currentPosition <= this.lineEnd + 1) {
                                    continue;
                                }
                                break Label_0632;
                            }
                            case 60: {
                                break Label_0632;
                            }
                            case 22: {
                                end = (hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition());
                                if (valid) {
                                    break Block_35;
                                }
                                continue;
                            }
                            default: {
                                end = (hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition());
                                valid = false;
                                continue;
                            }
                        }
                    }
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
                    }
                    if (!isCompletionParser) {
                        this.scanner.currentPosition = start;
                        this.index = start;
                    }
                    this.currentTokenType = -1;
                    return false;
                }
                this.pushIdentifier(false, false);
                boolean spaces = false;
                Block_43: {
                Label_0833:
                    while (true) {
                        this.currentTokenType = -1;
                        try {
                            token = this.readToken();
                        }
                        catch (final InvalidInputException ex3) {
                            valid = false;
                        }
                        switch (token) {
                            case 1000: {
                                if (this.scanner.currentPosition > this.lineEnd + 1) {
                                    hasMultiLines = true;
                                    valid = false;
                                }
                                spaces = true;
                                if (valid) {
                                    continue;
                                }
                                break Label_0833;
                            }
                            case 60: {
                                break Label_0833;
                            }
                            case 15: {
                                end = (hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition());
                                if (valid) {
                                    break Block_43;
                                }
                                continue;
                            }
                            default: {
                                if (!spaces) {
                                    end = (hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition());
                                }
                                valid = false;
                                continue;
                            }
                        }
                    }
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
                    }
                    if (!isCompletionParser) {
                        this.scanner.currentPosition = start;
                        this.index = start;
                    }
                    this.currentTokenType = -1;
                    return false;
                }
                this.pushIdentifier(false, true);
            }
            if (valid) {
                this.currentTokenType = -1;
                final int restart = this.scanner.currentPosition;
                try {
                    token = this.readTokenAndConsume();
                }
                catch (final InvalidInputException ex4) {
                    valid = false;
                }
                if (token == 1000) {
                    this.scanner.resetTo(restart, this.javadocEnd);
                    this.index = restart;
                    return this.pushParamName(isTypeParam);
                }
            }
            this.currentTokenType = -1;
            if (isCompletionParser) {
                return false;
            }
            if (this.reportProblems) {
                end = (hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition());
                try {
                    while ((token = this.readToken()) != 1000) {
                        if (token == 60) {
                            break;
                        }
                        this.currentTokenType = -1;
                        end = (hasMultiLines ? this.lineEnd : this.scanner.getCurrentTokenEndPosition());
                    }
                }
                catch (final InvalidInputException ex5) {
                    end = this.lineEnd;
                }
                if (mayBeGeneric && isTypeParam) {
                    this.sourceParser.problemReporter().javadocInvalidParamTypeParameter(start, end);
                }
                else {
                    this.sourceParser.problemReporter().javadocInvalidParamTagName(start, end);
                }
            }
            this.scanner.currentPosition = start;
            this.index = start;
            this.currentTokenType = -1;
            return false;
        }
        finally {
            this.scanner.tokenizeWhiteSpace = tokenWhiteSpace;
        }
    }
    
    protected Object parseQualifiedName(final boolean reset) throws InvalidInputException {
        if (reset) {
            this.identifierPtr = -1;
            this.identifierLengthPtr = -1;
        }
        int primitiveToken = -1;
        final int parserKind = this.kind & 0xFF;
        int iToken = 0;
        Label_0709: {
        Label_0575:
            while (true) {
                final int token = this.readTokenSafely();
                switch (token) {
                    case 22: {
                        if ((iToken & 0x1) != 0x0) {
                            break Label_0709;
                        }
                        this.pushIdentifier(iToken == 0, false);
                        this.consumeToken();
                        break;
                    }
                    case 3: {
                        if ((iToken & 0x1) == 0x0) {
                            throw new InvalidInputException();
                        }
                        this.consumeToken();
                        break;
                    }
                    case 17:
                    case 34:
                    case 35:
                    case 36:
                    case 38:
                    case 39:
                    case 40:
                    case 41:
                    case 42:
                    case 51:
                    case 52:
                    case 53:
                    case 54:
                    case 55:
                    case 56:
                    case 57:
                    case 58:
                    case 59:
                    case 67:
                    case 68:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 76:
                    case 77:
                    case 78:
                    case 80:
                    case 81:
                    case 82:
                    case 95:
                    case 96:
                    case 97:
                    case 98:
                    case 99:
                    case 100:
                    case 101:
                    case 102:
                    case 103:
                    case 104:
                    case 105:
                    case 106:
                    case 107:
                    case 108:
                    case 109:
                    case 111:
                    case 114: {
                        if (iToken == 0) {
                            this.pushIdentifier(true, true);
                            primitiveToken = token;
                            this.consumeToken();
                            break Label_0709;
                        }
                        break Label_0575;
                    }
                    default: {
                        break Label_0575;
                    }
                }
                ++iToken;
            }
            if (iToken == 0) {
                if (this.identifierPtr >= 0) {
                    this.lastIdentifierEndPosition = (int)this.identifierPositionStack[this.identifierPtr];
                }
                return null;
            }
            if ((iToken & 0x1) == 0x0) {
                switch (parserKind) {
                    case 8: {
                        if (this.identifierPtr >= 0) {
                            this.lastIdentifierEndPosition = (int)this.identifierPositionStack[this.identifierPtr];
                        }
                        return this.syntaxRecoverQualifiedName(primitiveToken);
                    }
                    case 2: {
                        if (this.currentTokenType != -1) {
                            this.index = this.tokenPreviousPosition;
                            this.scanner.currentPosition = this.tokenPreviousPosition;
                            this.currentTokenType = -1;
                            break;
                        }
                        break;
                    }
                }
                throw new InvalidInputException();
            }
        }
        if (parserKind != 8 && this.currentTokenType != -1) {
            this.index = this.tokenPreviousPosition;
            this.scanner.currentPosition = this.tokenPreviousPosition;
            this.currentTokenType = -1;
        }
        if (this.identifierPtr >= 0) {
            this.lastIdentifierEndPosition = (int)this.identifierPositionStack[this.identifierPtr];
        }
        return this.createTypeReference(primitiveToken);
    }
    
    protected boolean parseReference() throws InvalidInputException {
        final int currentPosition = this.scanner.currentPosition;
        try {
            Object typeRef = null;
            Object reference = null;
            int previousPosition = -1;
            int typeRefStartPosition = -1;
        Label_0595:
            while (this.index < this.scanner.eofPosition) {
                previousPosition = this.index;
                final int token = this.readTokenSafely();
                switch (token) {
                    case 48: {
                        if (typeRef != null) {
                            break Label_0595;
                        }
                        this.consumeToken();
                        final int start = this.scanner.getCurrentTokenStartPosition();
                        if (this.tagValue == 10) {
                            if (this.reportProblems) {
                                this.sourceParser.problemReporter().javadocInvalidValueReference(start, this.getTokenEndPosition(), this.sourceParser.modifiers);
                            }
                            return false;
                        }
                        if (this.verifyEndLine(previousPosition)) {
                            return this.createFakeReference(start);
                        }
                        if (this.reportProblems) {
                            this.sourceParser.problemReporter().javadocUnexpectedText(this.scanner.currentPosition, this.lineEnd);
                        }
                        return false;
                    }
                    case 11: {
                        if (typeRef != null) {
                            break Label_0595;
                        }
                        this.consumeToken();
                        final int start = this.scanner.getCurrentTokenStartPosition();
                        if (this.parseHref()) {
                            this.consumeToken();
                            if (this.tagValue == 10) {
                                if (this.reportProblems) {
                                    this.sourceParser.problemReporter().javadocInvalidValueReference(start, this.getIndexPosition(), this.sourceParser.modifiers);
                                }
                                return false;
                            }
                            if (this.verifyEndLine(previousPosition)) {
                                return this.createFakeReference(start);
                            }
                            if (this.reportProblems) {
                                this.sourceParser.problemReporter().javadocUnexpectedText(this.scanner.currentPosition, this.lineEnd);
                            }
                        }
                        else if (this.tagValue == 10 && this.reportProblems) {
                            this.sourceParser.problemReporter().javadocInvalidValueReference(start, this.getIndexPosition(), this.sourceParser.modifiers);
                        }
                        return false;
                    }
                    case 118: {
                        this.consumeToken();
                        if (this.scanner.currentCharacter == '#') {
                            reference = this.parseMember(typeRef);
                            return reference != null && this.pushSeeRef(reference);
                        }
                        final char[] currentError = this.scanner.getCurrentIdentifierSource();
                        if (currentError.length > 0 && currentError[0] == '\"') {
                            if (this.reportProblems) {
                                boolean isUrlRef = false;
                                if (this.tagValue == 6) {
                                    int length;
                                    int i;
                                    for (length = currentError.length, i = 1; i < length && ScannerHelper.isLetter(currentError[i]); ++i) {}
                                    if (i < length - 2 && currentError[i] == ':' && currentError[i + 1] == '/' && currentError[i + 2] == '/') {
                                        isUrlRef = true;
                                    }
                                }
                                if (isUrlRef) {
                                    this.sourceParser.problemReporter().javadocInvalidSeeUrlReference(this.scanner.getCurrentTokenStartPosition(), this.getTokenEndPosition());
                                }
                                else {
                                    this.sourceParser.problemReporter().javadocInvalidReference(this.scanner.getCurrentTokenStartPosition(), this.getTokenEndPosition());
                                }
                            }
                            return false;
                        }
                        break Label_0595;
                    }
                    case 22: {
                        if (typeRef != null) {
                            break Label_0595;
                        }
                        typeRefStartPosition = this.scanner.getCurrentTokenStartPosition();
                        typeRef = this.parseQualifiedName(true);
                        if (this.abort) {
                            return false;
                        }
                        continue;
                    }
                    default: {
                        break Label_0595;
                    }
                }
            }
            if (reference == null) {
                reference = typeRef;
            }
            if (reference == null) {
                this.index = this.tokenPreviousPosition;
                this.scanner.currentPosition = this.tokenPreviousPosition;
                this.currentTokenType = -1;
                if (this.tagValue == 10) {
                    if ((this.kind & 0x2) != 0x0) {
                        this.createTag();
                    }
                    return true;
                }
                if (this.reportProblems) {
                    this.sourceParser.problemReporter().javadocMissingReference(this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
                }
                return false;
            }
            else {
                if (this.lastIdentifierEndPosition > this.javadocStart) {
                    this.index = this.lastIdentifierEndPosition + 1;
                    this.scanner.currentPosition = this.index;
                }
                this.currentTokenType = -1;
                if (this.tagValue == 10) {
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocInvalidReference(typeRefStartPosition, this.lineEnd);
                    }
                    return false;
                }
                final int currentIndex = this.index;
                char ch = this.readChar();
                switch (ch) {
                    case '(': {
                        if (this.reportProblems) {
                            this.sourceParser.problemReporter().javadocMissingHashCharacter(typeRefStartPosition, this.lineEnd, String.valueOf(this.source, typeRefStartPosition, this.lineEnd - typeRefStartPosition + 1));
                        }
                        return false;
                    }
                    case ':': {
                        ch = this.readChar();
                        if (ch == '/' && ch == this.readChar() && this.reportProblems) {
                            this.sourceParser.problemReporter().javadocInvalidSeeUrlReference(typeRefStartPosition, this.lineEnd);
                            return false;
                        }
                        break;
                    }
                }
                this.index = currentIndex;
                if (!this.verifySpaceOrEndComment()) {
                    this.index = this.tokenPreviousPosition;
                    this.scanner.currentPosition = this.tokenPreviousPosition;
                    this.currentTokenType = -1;
                    int end = (this.starPosition == -1) ? this.lineEnd : this.starPosition;
                    if (this.source[end] == '\n') {
                        --end;
                    }
                    if (this.reportProblems) {
                        this.sourceParser.problemReporter().javadocMalformedSeeReference(typeRefStartPosition, end);
                    }
                    return false;
                }
                return this.pushSeeRef(reference);
            }
        }
        catch (final InvalidInputException ex) {
            if (this.reportProblems) {
                this.sourceParser.problemReporter().javadocInvalidReference(currentPosition, this.getTokenEndPosition());
            }
            this.index = this.tokenPreviousPosition;
            this.scanner.currentPosition = this.tokenPreviousPosition;
            this.currentTokenType = -1;
            return false;
        }
    }
    
    protected abstract boolean parseTag(final int p0) throws InvalidInputException;
    
    protected boolean parseThrows() {
        final int start = this.scanner.currentPosition;
        try {
            final Object typeRef = this.parseQualifiedName(true);
            if (this.abort) {
                return false;
            }
            if (typeRef != null) {
                return this.pushThrowName(typeRef);
            }
            if (this.reportProblems) {
                this.sourceParser.problemReporter().javadocMissingThrowsClassName(this.tagSourceStart, this.tagSourceEnd, this.sourceParser.modifiers);
            }
        }
        catch (final InvalidInputException ex) {
            if (this.reportProblems) {
                this.sourceParser.problemReporter().javadocInvalidThrowsClass(start, this.getTokenEndPosition());
            }
        }
        return false;
    }
    
    protected char peekChar() {
        int idx = this.index;
        char c = this.source[idx++];
        if (c == '\\' && this.source[idx] == 'u') {
            ++idx;
            while (this.source[idx] == 'u') {
                ++idx;
            }
            final int c2;
            final int c3;
            final int c4;
            final int c5;
            if ((c2 = ScannerHelper.getHexadecimalValue(this.source[idx++])) <= 15 && c2 >= 0 && (c3 = ScannerHelper.getHexadecimalValue(this.source[idx++])) <= 15 && c3 >= 0 && (c4 = ScannerHelper.getHexadecimalValue(this.source[idx++])) <= 15 && c4 >= 0 && (c5 = ScannerHelper.getHexadecimalValue(this.source[idx++])) <= 15 && c5 >= 0) {
                c = (char)(((c2 * 16 + c3) * 16 + c4) * 16 + c5);
            }
        }
        return c;
    }
    
    protected void pushIdentifier(final boolean newLength, final boolean isToken) {
        int stackLength = this.identifierStack.length;
        if (++this.identifierPtr >= stackLength) {
            System.arraycopy(this.identifierStack, 0, this.identifierStack = new char[stackLength + 10][], 0, stackLength);
            System.arraycopy(this.identifierPositionStack, 0, this.identifierPositionStack = new long[stackLength + 10], 0, stackLength);
        }
        this.identifierStack[this.identifierPtr] = (isToken ? this.scanner.getCurrentTokenSource() : this.scanner.getCurrentIdentifierSource());
        this.identifierPositionStack[this.identifierPtr] = ((long)this.scanner.startPosition << 32) + (this.scanner.currentPosition - 1);
        if (newLength) {
            stackLength = this.identifierLengthStack.length;
            if (++this.identifierLengthPtr >= stackLength) {
                System.arraycopy(this.identifierLengthStack, 0, this.identifierLengthStack = new int[stackLength + 10], 0, stackLength);
            }
            this.identifierLengthStack[this.identifierLengthPtr] = 1;
        }
        else {
            final int[] identifierLengthStack = this.identifierLengthStack;
            final int identifierLengthPtr = this.identifierLengthPtr;
            ++identifierLengthStack[identifierLengthPtr];
        }
    }
    
    protected void pushOnAstStack(final Object node, final boolean newLength) {
        if (node == null) {
            final int stackLength = this.astLengthStack.length;
            if (++this.astLengthPtr >= stackLength) {
                System.arraycopy(this.astLengthStack, 0, this.astLengthStack = new int[stackLength + 10], 0, stackLength);
            }
            this.astLengthStack[this.astLengthPtr] = 0;
            return;
        }
        int stackLength = this.astStack.length;
        if (++this.astPtr >= stackLength) {
            System.arraycopy(this.astStack, 0, this.astStack = new Object[stackLength + 10], 0, stackLength);
            this.astPtr = stackLength;
        }
        this.astStack[this.astPtr] = node;
        if (newLength) {
            stackLength = this.astLengthStack.length;
            if (++this.astLengthPtr >= stackLength) {
                System.arraycopy(this.astLengthStack, 0, this.astLengthStack = new int[stackLength + 10], 0, stackLength);
            }
            this.astLengthStack[this.astLengthPtr] = 1;
        }
        else {
            final int[] astLengthStack = this.astLengthStack;
            final int astLengthPtr = this.astLengthPtr;
            ++astLengthStack[astLengthPtr];
        }
    }
    
    protected abstract boolean pushParamName(final boolean p0);
    
    protected abstract boolean pushSeeRef(final Object p0);
    
    protected void pushText(final int start, final int end) {
    }
    
    protected abstract boolean pushThrowName(final Object p0);
    
    protected char readChar() {
        char c = this.source[this.index++];
        if (c == '\\' && this.source[this.index] == 'u') {
            final int pos = this.index;
            ++this.index;
            while (this.source[this.index] == 'u') {
                ++this.index;
            }
            final int c2;
            final int c3;
            final int c4;
            final int c5;
            if ((c2 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c2 >= 0 && (c3 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c3 >= 0 && (c4 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c4 >= 0 && (c5 = ScannerHelper.getHexadecimalValue(this.source[this.index++])) <= 15 && c5 >= 0) {
                c = (char)(((c2 * 16 + c3) * 16 + c4) * 16 + c5);
            }
            else {
                this.index = pos;
            }
        }
        return c;
    }
    
    protected int readToken() throws InvalidInputException {
        if (this.currentTokenType < 0) {
            this.tokenPreviousPosition = this.scanner.currentPosition;
            this.currentTokenType = this.scanner.getNextToken();
            if (this.scanner.currentPosition > this.lineEnd + 1) {
                this.lineStarted = false;
                while (this.currentTokenType == 6) {
                    this.currentTokenType = this.scanner.getNextToken();
                }
            }
            this.index = this.scanner.currentPosition;
            this.lineStarted = true;
        }
        return this.currentTokenType;
    }
    
    protected int readTokenAndConsume() throws InvalidInputException {
        final int token = this.readToken();
        this.consumeToken();
        return token;
    }
    
    protected int readTokenSafely() {
        int token = 118;
        try {
            token = this.readToken();
        }
        catch (final InvalidInputException ex) {}
        return token;
    }
    
    protected void recordInheritedPosition(final long position) {
        if (this.inheritedPositions == null) {
            this.inheritedPositions = new long[4];
            this.inheritedPositionsPtr = 0;
        }
        else if (this.inheritedPositionsPtr == this.inheritedPositions.length) {
            System.arraycopy(this.inheritedPositions, 0, this.inheritedPositions = new long[this.inheritedPositionsPtr + 4], 0, this.inheritedPositionsPtr);
        }
        this.inheritedPositions[this.inheritedPositionsPtr++] = position;
    }
    
    protected void refreshInlineTagPosition(final int previousPosition) {
    }
    
    protected void refreshReturnStatement() {
    }
    
    protected void setInlineTagStarted(final boolean started) {
        this.inlineTagStarted = started;
    }
    
    protected Object syntaxRecoverQualifiedName(final int primitiveToken) throws InvalidInputException {
        return null;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        final int startPos = (this.scanner.currentPosition < this.index) ? this.scanner.currentPosition : this.index;
        final int endPos = (this.scanner.currentPosition < this.index) ? this.index : this.scanner.currentPosition;
        if (startPos == this.source.length) {
            return "EOF\n\n" + new String(this.source);
        }
        if (endPos > this.source.length) {
            return "behind the EOF\n\n" + new String(this.source);
        }
        final char[] front = new char[startPos];
        System.arraycopy(this.source, 0, front, 0, startPos);
        final int middleLength = endPos - 1 - startPos + 1;
        char[] middle;
        if (middleLength > -1) {
            middle = new char[middleLength];
            System.arraycopy(this.source, startPos, middle, 0, middleLength);
        }
        else {
            middle = CharOperation.NO_CHAR;
        }
        final char[] end = new char[this.source.length - (endPos - 1)];
        System.arraycopy(this.source, endPos - 1 + 1, end, 0, this.source.length - (endPos - 1) - 1);
        buffer.append(front);
        if (this.scanner.currentPosition < this.index) {
            buffer.append("\n===============================\nScanner current position here -->");
        }
        else {
            buffer.append("\n===============================\nParser index here -->");
        }
        buffer.append(middle);
        if (this.scanner.currentPosition < this.index) {
            buffer.append("<-- Parser index here\n===============================\n");
        }
        else {
            buffer.append("<-- Scanner current position here\n===============================\n");
        }
        buffer.append(end);
        return buffer.toString();
    }
    
    protected abstract void updateDocComment();
    
    protected void updateLineEnd() {
        while (this.index > this.lineEnd + 1) {
            if (this.linePtr >= this.lastLinePtr) {
                this.lineEnd = this.javadocEnd;
                return;
            }
            this.lineEnd = this.scanner.getLineEnd(++this.linePtr) - 1;
        }
    }
    
    protected boolean verifyEndLine(final int textPosition) {
        final boolean domParser = (this.kind & 0x2) != 0x0;
        if (!this.inlineTagStarted) {
            final int startPosition = this.index;
            int previousPosition = this.index;
            this.starPosition = -1;
            char ch = this.readChar();
        Label_0231:
            while (true) {
                switch (ch) {
                    case '\n':
                    case '\r': {
                        if (domParser) {
                            this.createTag();
                            this.pushText(textPosition, previousPosition);
                        }
                        this.index = previousPosition;
                        return true;
                    }
                    case '\t':
                    case '\f':
                    case ' ': {
                        if (this.starPosition >= 0) {
                            break Label_0231;
                        }
                        break;
                    }
                    case '*': {
                        this.starPosition = previousPosition;
                        break;
                    }
                    case '/': {
                        if (this.starPosition >= textPosition) {
                            if (domParser) {
                                this.createTag();
                                this.pushText(textPosition, this.starPosition);
                            }
                            return true;
                        }
                        break Label_0231;
                    }
                    default: {
                        break Label_0231;
                    }
                }
                previousPosition = this.index;
                ch = this.readChar();
            }
            this.index = startPosition;
            return false;
        }
        if (this.peekChar() == '}') {
            if (domParser) {
                this.createTag();
                this.pushText(textPosition, this.index);
            }
            return true;
        }
        return false;
    }
    
    protected boolean verifySpaceOrEndComment() {
        this.starPosition = -1;
        final int startPosition = this.index;
        char ch = this.peekChar();
        switch (ch) {
            case '}': {
                return this.inlineTagStarted;
            }
            default: {
                if (ScannerHelper.isWhitespace(ch)) {
                    return true;
                }
                int previousPosition = this.index;
                ch = this.readChar();
                while (this.index < this.source.length) {
                    switch (ch) {
                        case '*': {
                            this.starPosition = previousPosition;
                            previousPosition = this.index;
                            ch = this.readChar();
                            continue;
                        }
                        case '/': {
                            if (this.starPosition >= startPosition) {
                                return true;
                            }
                            break;
                        }
                    }
                    this.index = startPosition;
                    return false;
                }
                this.index = startPosition;
                return false;
            }
        }
    }
}
