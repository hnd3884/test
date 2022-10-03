package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.internal.compiler.ast.Statement;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.util.Util;

public class Scanner implements TerminalTokens
{
    public long sourceLevel;
    public long complianceLevel;
    public boolean useAssertAsAnIndentifier;
    public boolean containsAssertKeyword;
    public boolean useEnumAsAnIndentifier;
    public boolean recordLineSeparator;
    public char currentCharacter;
    public int startPosition;
    public int currentPosition;
    public int initialPosition;
    public int eofPosition;
    public boolean skipComments;
    public boolean tokenizeComments;
    public boolean tokenizeWhiteSpace;
    public char[] source;
    public char[] withoutUnicodeBuffer;
    public int withoutUnicodePtr;
    public boolean unicodeAsBackSlash;
    public boolean scanningFloatLiteral;
    public static final int COMMENT_ARRAYS_SIZE = 30;
    public int[] commentStops;
    public int[] commentStarts;
    public int[] commentTagStarts;
    public int commentPtr;
    protected int lastCommentLinePosition;
    public char[][] foundTaskTags;
    public char[][] foundTaskMessages;
    public char[][] foundTaskPriorities;
    public int[][] foundTaskPositions;
    public int foundTaskCount;
    public char[][] taskTags;
    public char[][] taskPriorities;
    public boolean isTaskCaseSensitive;
    public boolean diet;
    public int[] lineEnds;
    public int linePtr;
    public boolean wasAcr;
    public static final String END_OF_SOURCE = "End_Of_Source";
    public static final String INVALID_HEXA = "Invalid_Hexa_Literal";
    public static final String INVALID_OCTAL = "Invalid_Octal_Literal";
    public static final String INVALID_CHARACTER_CONSTANT = "Invalid_Character_Constant";
    public static final String INVALID_ESCAPE = "Invalid_Escape";
    public static final String INVALID_INPUT = "Invalid_Input";
    public static final String INVALID_UNICODE_ESCAPE = "Invalid_Unicode_Escape";
    public static final String INVALID_FLOAT = "Invalid_Float_Literal";
    public static final String INVALID_LOW_SURROGATE = "Invalid_Low_Surrogate";
    public static final String INVALID_HIGH_SURROGATE = "Invalid_High_Surrogate";
    public static final String NULL_SOURCE_STRING = "Null_Source_String";
    public static final String UNTERMINATED_STRING = "Unterminated_String";
    public static final String UNTERMINATED_COMMENT = "Unterminated_Comment";
    public static final String INVALID_CHAR_IN_STRING = "Invalid_Char_In_String";
    public static final String INVALID_DIGIT = "Invalid_Digit";
    private static final int[] EMPTY_LINE_ENDS;
    public static final String INVALID_BINARY = "Invalid_Binary_Literal";
    public static final String BINARY_LITERAL_NOT_BELOW_17 = "Binary_Literal_Not_Below_17";
    public static final String ILLEGAL_HEXA_LITERAL = "Illegal_Hexa_Literal";
    public static final String INVALID_UNDERSCORE = "Invalid_Underscore";
    public static final String UNDERSCORES_IN_LITERALS_NOT_BELOW_17 = "Underscores_In_Literals_Not_Below_17";
    static final char[] charArray_a;
    static final char[] charArray_b;
    static final char[] charArray_c;
    static final char[] charArray_d;
    static final char[] charArray_e;
    static final char[] charArray_f;
    static final char[] charArray_g;
    static final char[] charArray_h;
    static final char[] charArray_i;
    static final char[] charArray_j;
    static final char[] charArray_k;
    static final char[] charArray_l;
    static final char[] charArray_m;
    static final char[] charArray_n;
    static final char[] charArray_o;
    static final char[] charArray_p;
    static final char[] charArray_q;
    static final char[] charArray_r;
    static final char[] charArray_s;
    static final char[] charArray_t;
    static final char[] charArray_u;
    static final char[] charArray_v;
    static final char[] charArray_w;
    static final char[] charArray_x;
    static final char[] charArray_y;
    static final char[] charArray_z;
    static final char[] initCharArray;
    static final int TableSize = 30;
    static final int InternalTableSize = 6;
    public static final int OptimizedLength = 7;
    public final char[][][][] charArray_length;
    public static final char[] TAG_PREFIX;
    public static final int TAG_PREFIX_LENGTH;
    public static final char TAG_POSTFIX = '$';
    public static final int TAG_POSTFIX_LENGTH = 1;
    public static final char[] IDENTITY_COMPARISON_TAG;
    public boolean[] validIdentityComparisonLines;
    public boolean checkUninternedIdentityComparison;
    private NLSTag[] nlsTags;
    protected int nlsTagsPtr;
    public boolean checkNonExternalizedStringLiterals;
    protected int lastPosition;
    public boolean returnOnlyGreater;
    int newEntry2;
    int newEntry3;
    int newEntry4;
    int newEntry5;
    int newEntry6;
    public boolean insideRecovery;
    int[] lookBack;
    int nextToken;
    private VanguardScanner vanguardScanner;
    private VanguardParser vanguardParser;
    ConflictedParser activeParser;
    private boolean consumingEllipsisAnnotations;
    public static final int RoundBracket = 0;
    public static final int SquareBracket = 1;
    public static final int CurlyBracket = 2;
    public static final int BracketKinds = 3;
    public static final int LOW_SURROGATE_MIN_VALUE = 56320;
    public static final int HIGH_SURROGATE_MIN_VALUE = 55296;
    public static final int HIGH_SURROGATE_MAX_VALUE = 56319;
    public static final int LOW_SURROGATE_MAX_VALUE = 57343;
    
    static {
        EMPTY_LINE_ENDS = Util.EMPTY_INT_ARRAY;
        charArray_a = new char[] { 'a' };
        charArray_b = new char[] { 'b' };
        charArray_c = new char[] { 'c' };
        charArray_d = new char[] { 'd' };
        charArray_e = new char[] { 'e' };
        charArray_f = new char[] { 'f' };
        charArray_g = new char[] { 'g' };
        charArray_h = new char[] { 'h' };
        charArray_i = new char[] { 'i' };
        charArray_j = new char[] { 'j' };
        charArray_k = new char[] { 'k' };
        charArray_l = new char[] { 'l' };
        charArray_m = new char[] { 'm' };
        charArray_n = new char[] { 'n' };
        charArray_o = new char[] { 'o' };
        charArray_p = new char[] { 'p' };
        charArray_q = new char[] { 'q' };
        charArray_r = new char[] { 'r' };
        charArray_s = new char[] { 's' };
        charArray_t = new char[] { 't' };
        charArray_u = new char[] { 'u' };
        charArray_v = new char[] { 'v' };
        charArray_w = new char[] { 'w' };
        charArray_x = new char[] { 'x' };
        charArray_y = new char[] { 'y' };
        charArray_z = new char[] { 'z' };
        initCharArray = new char[6];
        TAG_PREFIX = "//$NON-NLS-".toCharArray();
        TAG_PREFIX_LENGTH = Scanner.TAG_PREFIX.length;
        IDENTITY_COMPARISON_TAG = "//$IDENTITY-COMPARISON$".toCharArray();
    }
    
    public Scanner() {
        this(false, false, false, 3080192L, null, null, true);
    }
    
    public Scanner(final boolean tokenizeComments, final boolean tokenizeWhiteSpace, final boolean checkNonExternalizedStringLiterals, final long sourceLevel, final long complianceLevel, char[][] taskTags, char[][] taskPriorities, final boolean isTaskCaseSensitive) {
        this.useAssertAsAnIndentifier = false;
        this.containsAssertKeyword = false;
        this.useEnumAsAnIndentifier = false;
        this.recordLineSeparator = false;
        this.skipComments = false;
        this.tokenizeComments = false;
        this.tokenizeWhiteSpace = false;
        this.unicodeAsBackSlash = false;
        this.scanningFloatLiteral = false;
        this.commentStops = new int[30];
        this.commentStarts = new int[30];
        this.commentTagStarts = new int[30];
        this.commentPtr = -1;
        this.lastCommentLinePosition = -1;
        this.foundTaskTags = null;
        this.foundTaskPriorities = null;
        this.foundTaskCount = 0;
        this.taskTags = null;
        this.taskPriorities = null;
        this.isTaskCaseSensitive = true;
        this.diet = false;
        this.lineEnds = new int[250];
        this.linePtr = -1;
        this.wasAcr = false;
        this.charArray_length = new char[7][30][6][];
        this.nlsTags = null;
        this.returnOnlyGreater = false;
        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 30; ++j) {
                for (int k = 0; k < 6; ++k) {
                    this.charArray_length[i][j][k] = Scanner.initCharArray;
                }
            }
        }
        this.newEntry2 = 0;
        this.newEntry3 = 0;
        this.newEntry4 = 0;
        this.newEntry5 = 0;
        this.newEntry6 = 0;
        this.insideRecovery = false;
        this.lookBack = new int[2];
        this.nextToken = 0;
        this.activeParser = null;
        this.consumingEllipsisAnnotations = false;
        this.eofPosition = Integer.MAX_VALUE;
        this.tokenizeComments = tokenizeComments;
        this.tokenizeWhiteSpace = tokenizeWhiteSpace;
        this.sourceLevel = sourceLevel;
        final int[] lookBack = this.lookBack;
        final int n = 0;
        final int[] lookBack2 = this.lookBack;
        final int n2 = 1;
        final int nextToken = 0;
        this.nextToken = nextToken;
        lookBack[n] = (lookBack2[n2] = nextToken);
        this.consumingEllipsisAnnotations = false;
        this.complianceLevel = complianceLevel;
        this.checkNonExternalizedStringLiterals = checkNonExternalizedStringLiterals;
        if (taskTags != null) {
            int length;
            final int taskTagsLength = length = taskTags.length;
            if (taskPriorities != null) {
                final int taskPrioritiesLength = taskPriorities.length;
                if (taskPrioritiesLength != taskTagsLength) {
                    if (taskPrioritiesLength > taskTagsLength) {
                        System.arraycopy(taskPriorities, 0, taskPriorities = new char[taskTagsLength][], 0, taskTagsLength);
                    }
                    else {
                        System.arraycopy(taskTags, 0, taskTags = new char[taskPrioritiesLength][], 0, taskPrioritiesLength);
                        length = taskPrioritiesLength;
                    }
                }
                final int[] initialIndexes = new int[length];
                for (int l = 0; l < length; ++l) {
                    initialIndexes[l] = l;
                }
                Util.reverseQuickSort(taskTags, 0, length - 1, initialIndexes);
                final char[][] temp = new char[length][];
                for (int m = 0; m < length; ++m) {
                    temp[m] = taskPriorities[initialIndexes[m]];
                }
                this.taskPriorities = temp;
            }
            else {
                Util.reverseQuickSort(taskTags, 0, length - 1);
            }
            this.taskTags = taskTags;
            this.isTaskCaseSensitive = isTaskCaseSensitive;
        }
    }
    
    public Scanner(final boolean tokenizeComments, final boolean tokenizeWhiteSpace, final boolean checkNonExternalizedStringLiterals, final long sourceLevel, final char[][] taskTags, final char[][] taskPriorities, final boolean isTaskCaseSensitive) {
        this(tokenizeComments, tokenizeWhiteSpace, checkNonExternalizedStringLiterals, sourceLevel, sourceLevel, taskTags, taskPriorities, isTaskCaseSensitive);
    }
    
    public final boolean atEnd() {
        return this.eofPosition <= this.currentPosition;
    }
    
    public void checkTaskTag(final int commentStart, final int commentEnd) throws InvalidInputException {
        final char[] src = this.source;
        if (this.foundTaskCount > 0 && this.foundTaskPositions[this.foundTaskCount - 1][0] >= commentStart) {
            return;
        }
        final int foundTaskIndex = this.foundTaskCount;
        char previous = src[commentStart + 1];
        for (int i = commentStart + 2; i < commentEnd && i < this.eofPosition; ++i) {
            char[] tag = null;
            char[] priority = null;
            if (previous != '@') {
            Label_0529:
                for (int itag = 0; itag < this.taskTags.length; ++itag) {
                    tag = this.taskTags[itag];
                    final int tagLength = tag.length;
                    if (tagLength != 0) {
                        if (!ScannerHelper.isJavaIdentifierStart(this.complianceLevel, tag[0]) || !ScannerHelper.isJavaIdentifierPart(this.complianceLevel, previous)) {
                            for (int t = 0; t < tagLength; ++t) {
                                final int x = i + t;
                                if (x >= this.eofPosition) {
                                    continue Label_0529;
                                }
                                if (x >= commentEnd) {
                                    continue Label_0529;
                                }
                                final char sc = src[i + t];
                                final char tc;
                                if (sc != (tc = tag[t])) {
                                    if (this.isTaskCaseSensitive) {
                                        continue Label_0529;
                                    }
                                    if (ScannerHelper.toLowerCase(sc) != ScannerHelper.toLowerCase(tc)) {
                                        continue Label_0529;
                                    }
                                }
                            }
                            if (i + tagLength >= commentEnd || !ScannerHelper.isJavaIdentifierPart(this.complianceLevel, src[i + tagLength - 1]) || !ScannerHelper.isJavaIdentifierPart(this.complianceLevel, src[i + tagLength])) {
                                if (this.foundTaskTags == null) {
                                    this.foundTaskTags = new char[5][];
                                    this.foundTaskMessages = new char[5][];
                                    this.foundTaskPriorities = new char[5][];
                                    this.foundTaskPositions = new int[5][];
                                }
                                else if (this.foundTaskCount == this.foundTaskTags.length) {
                                    System.arraycopy(this.foundTaskTags, 0, this.foundTaskTags = new char[this.foundTaskCount * 2][], 0, this.foundTaskCount);
                                    System.arraycopy(this.foundTaskMessages, 0, this.foundTaskMessages = new char[this.foundTaskCount * 2][], 0, this.foundTaskCount);
                                    System.arraycopy(this.foundTaskPriorities, 0, this.foundTaskPriorities = new char[this.foundTaskCount * 2][], 0, this.foundTaskCount);
                                    System.arraycopy(this.foundTaskPositions, 0, this.foundTaskPositions = new int[this.foundTaskCount * 2][], 0, this.foundTaskCount);
                                }
                                priority = (char[])((this.taskPriorities != null && itag < this.taskPriorities.length) ? this.taskPriorities[itag] : null);
                                this.foundTaskTags[this.foundTaskCount] = tag;
                                this.foundTaskPriorities[this.foundTaskCount] = priority;
                                this.foundTaskPositions[this.foundTaskCount] = new int[] { i, i + tagLength - 1 };
                                this.foundTaskMessages[this.foundTaskCount] = CharOperation.NO_CHAR;
                                ++this.foundTaskCount;
                                i += tagLength - 1;
                                break;
                            }
                        }
                    }
                }
            }
            previous = src[i];
        }
        boolean containsEmptyTask = false;
        for (int j = foundTaskIndex; j < this.foundTaskCount; ++j) {
            final int msgStart = this.foundTaskPositions[j][0] + this.foundTaskTags[j].length;
            int max_value = (j + 1 < this.foundTaskCount) ? (this.foundTaskPositions[j + 1][0] - 1) : (commentEnd - 1);
            if (max_value < msgStart) {
                max_value = msgStart;
            }
            int end = -1;
            for (int k = msgStart; k < max_value; ++k) {
                final char c;
                if ((c = src[k]) == '\n' || c == '\r') {
                    end = k - 1;
                    break;
                }
            }
            if (end == -1) {
                for (int k = max_value; k > msgStart; --k) {
                    final char c;
                    if ((c = src[k]) == '*') {
                        end = k - 1;
                        break;
                    }
                }
                if (end == -1) {
                    end = max_value;
                }
            }
            if (msgStart == end) {
                containsEmptyTask = true;
            }
            else {
                while (CharOperation.isWhitespace(src[end]) && msgStart <= end) {
                    --end;
                }
                this.foundTaskPositions[j][1] = end;
                final int messageLength = end - msgStart + 1;
                final char[] message = new char[messageLength];
                System.arraycopy(src, msgStart, message, 0, messageLength);
                this.foundTaskMessages[j] = message;
            }
        }
        if (containsEmptyTask) {
            for (int j = foundTaskIndex, max = this.foundTaskCount; j < max; ++j) {
                if (this.foundTaskMessages[j].length == 0) {
                    for (int l = j + 1; l < max; ++l) {
                        if (this.foundTaskMessages[l].length != 0) {
                            this.foundTaskMessages[j] = this.foundTaskMessages[l];
                            this.foundTaskPositions[j][1] = this.foundTaskPositions[l][1];
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public char[] getCurrentIdentifierSource() {
        if (this.withoutUnicodePtr != 0) {
            final char[] result = new char[this.withoutUnicodePtr];
            System.arraycopy(this.withoutUnicodeBuffer, 1, result, 0, this.withoutUnicodePtr);
            return result;
        }
        final int length = this.currentPosition - this.startPosition;
        if (length == this.eofPosition) {
            return this.source;
        }
        switch (length) {
            case 1: {
                return this.optimizedCurrentTokenSource1();
            }
            case 2: {
                return this.optimizedCurrentTokenSource2();
            }
            case 3: {
                return this.optimizedCurrentTokenSource3();
            }
            case 4: {
                return this.optimizedCurrentTokenSource4();
            }
            case 5: {
                return this.optimizedCurrentTokenSource5();
            }
            case 6: {
                return this.optimizedCurrentTokenSource6();
            }
            default: {
                final char[] result2 = new char[length];
                System.arraycopy(this.source, this.startPosition, result2, 0, length);
                return result2;
            }
        }
    }
    
    public int getCurrentTokenEndPosition() {
        return this.currentPosition - 1;
    }
    
    public char[] getCurrentTokenSource() {
        char[] result;
        if (this.withoutUnicodePtr != 0) {
            System.arraycopy(this.withoutUnicodeBuffer, 1, result = new char[this.withoutUnicodePtr], 0, this.withoutUnicodePtr);
        }
        else {
            final int length;
            System.arraycopy(this.source, this.startPosition, result = new char[length = this.currentPosition - this.startPosition], 0, length);
        }
        return result;
    }
    
    public final String getCurrentTokenString() {
        if (this.withoutUnicodePtr != 0) {
            return new String(this.withoutUnicodeBuffer, 1, this.withoutUnicodePtr);
        }
        return new String(this.source, this.startPosition, this.currentPosition - this.startPosition);
    }
    
    public char[] getCurrentTokenSourceString() {
        char[] result;
        if (this.withoutUnicodePtr != 0) {
            System.arraycopy(this.withoutUnicodeBuffer, 2, result = new char[this.withoutUnicodePtr - 2], 0, this.withoutUnicodePtr - 2);
        }
        else {
            final int length;
            System.arraycopy(this.source, this.startPosition + 1, result = new char[length = this.currentPosition - this.startPosition - 2], 0, length);
        }
        return result;
    }
    
    public final String getCurrentStringLiteral() {
        if (this.withoutUnicodePtr != 0) {
            return new String(this.withoutUnicodeBuffer, 2, this.withoutUnicodePtr - 2);
        }
        return new String(this.source, this.startPosition + 1, this.currentPosition - this.startPosition - 2);
    }
    
    public final char[] getRawTokenSource() {
        final int length = this.currentPosition - this.startPosition;
        final char[] tokenSource = new char[length];
        System.arraycopy(this.source, this.startPosition, tokenSource, 0, length);
        return tokenSource;
    }
    
    public final char[] getRawTokenSourceEnd() {
        final int length = this.eofPosition - this.currentPosition - 1;
        final char[] sourceEnd = new char[length];
        System.arraycopy(this.source, this.currentPosition, sourceEnd, 0, length);
        return sourceEnd;
    }
    
    public int getCurrentTokenStartPosition() {
        return this.startPosition;
    }
    
    public final int getLineEnd(final int lineNumber) {
        if (this.lineEnds == null || this.linePtr == -1) {
            return -1;
        }
        if (lineNumber > this.lineEnds.length + 1) {
            return -1;
        }
        if (lineNumber <= 0) {
            return -1;
        }
        if (lineNumber == this.lineEnds.length + 1) {
            return this.eofPosition;
        }
        return this.lineEnds[lineNumber - 1];
    }
    
    public final int[] getLineEnds() {
        if (this.linePtr == -1) {
            return Scanner.EMPTY_LINE_ENDS;
        }
        final int[] copy;
        System.arraycopy(this.lineEnds, 0, copy = new int[this.linePtr + 1], 0, this.linePtr + 1);
        return copy;
    }
    
    public final int getLineStart(final int lineNumber) {
        if (this.lineEnds == null || this.linePtr == -1) {
            return -1;
        }
        if (lineNumber > this.lineEnds.length + 1) {
            return -1;
        }
        if (lineNumber <= 0) {
            return -1;
        }
        if (lineNumber == 1) {
            return this.initialPosition;
        }
        return this.lineEnds[lineNumber - 2] + 1;
    }
    
    public final int getNextChar() {
        try {
            final char currentCharacter = this.source[this.currentPosition++];
            this.currentCharacter = currentCharacter;
            if (currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
            }
            else {
                this.unicodeAsBackSlash = false;
                if (this.withoutUnicodePtr != 0) {
                    this.unicodeStore();
                }
            }
            return this.currentCharacter;
        }
        catch (final IndexOutOfBoundsException ex) {
            return -1;
        }
        catch (final InvalidInputException ex2) {
            return -1;
        }
    }
    
    public final int getNextCharWithBoundChecks() {
        if (this.currentPosition >= this.eofPosition) {
            return -1;
        }
        this.currentCharacter = this.source[this.currentPosition++];
        if (this.currentPosition >= this.eofPosition) {
            this.unicodeAsBackSlash = false;
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            return this.currentCharacter;
        }
        if (this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
            try {
                this.getNextUnicodeChar();
                return this.currentCharacter;
            }
            catch (final InvalidInputException ex) {
                return -1;
            }
        }
        this.unicodeAsBackSlash = false;
        if (this.withoutUnicodePtr != 0) {
            this.unicodeStore();
        }
        return this.currentCharacter;
    }
    
    public final boolean getNextChar(final char testedChar) {
        if (this.currentPosition >= this.eofPosition) {
            return this.unicodeAsBackSlash = false;
        }
        final int temp = this.currentPosition;
        try {
            final char currentCharacter = this.source[this.currentPosition++];
            this.currentCharacter = currentCharacter;
            if (currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
                if (this.currentCharacter != testedChar) {
                    this.currentPosition = temp;
                    --this.withoutUnicodePtr;
                    return false;
                }
                return true;
            }
            else {
                if (this.currentCharacter != testedChar) {
                    this.currentPosition = temp;
                    return false;
                }
                this.unicodeAsBackSlash = false;
                if (this.withoutUnicodePtr != 0) {
                    this.unicodeStore();
                }
                return true;
            }
        }
        catch (final IndexOutOfBoundsException ex) {
            this.unicodeAsBackSlash = false;
            this.currentPosition = temp;
            return false;
        }
        catch (final InvalidInputException ex2) {
            this.unicodeAsBackSlash = false;
            this.currentPosition = temp;
            return false;
        }
    }
    
    public final int getNextChar(final char testedChar1, final char testedChar2) {
        if (this.currentPosition >= this.eofPosition) {
            return -1;
        }
        final int temp = this.currentPosition;
        try {
            final char currentCharacter = this.source[this.currentPosition++];
            this.currentCharacter = currentCharacter;
            if (currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
                int result;
                if (this.currentCharacter == testedChar1) {
                    result = 0;
                }
                else if (this.currentCharacter == testedChar2) {
                    result = 1;
                }
                else {
                    this.currentPosition = temp;
                    --this.withoutUnicodePtr;
                    result = -1;
                }
                return result;
            }
            int result;
            if (this.currentCharacter == testedChar1) {
                result = 0;
            }
            else {
                if (this.currentCharacter != testedChar2) {
                    this.currentPosition = temp;
                    return -1;
                }
                result = 1;
            }
            if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            return result;
        }
        catch (final IndexOutOfBoundsException ex) {
            this.currentPosition = temp;
            return -1;
        }
        catch (final InvalidInputException ex2) {
            this.currentPosition = temp;
            return -1;
        }
    }
    
    private final void consumeDigits(final int radix) throws InvalidInputException {
        this.consumeDigits(radix, false);
    }
    
    private final void consumeDigits(final int radix, final boolean expectingDigitFirst) throws InvalidInputException {
        switch (this.consumeDigits0(radix, 1, 2, expectingDigitFirst)) {
            case 1: {
                if (this.sourceLevel < 3342336L) {
                    throw new InvalidInputException("Underscores_In_Literals_Not_Below_17");
                }
                break;
            }
            case 2: {
                if (this.sourceLevel < 3342336L) {
                    throw new InvalidInputException("Underscores_In_Literals_Not_Below_17");
                }
                throw new InvalidInputException("Invalid_Underscore");
            }
        }
    }
    
    private final int consumeDigits0(final int radix, final int usingUnderscore, final int invalidPosition, final boolean expectingDigitFirst) throws InvalidInputException {
        int kind = 0;
        if (this.getNextChar('_')) {
            if (expectingDigitFirst) {
                return invalidPosition;
            }
            kind = usingUnderscore;
            while (this.getNextChar('_')) {}
        }
        if (this.getNextCharAsDigit(radix)) {
            while (this.getNextCharAsDigit(radix)) {}
            final int kind2 = this.consumeDigits0(radix, usingUnderscore, invalidPosition, false);
            if (kind2 == 0) {
                return kind;
            }
            return kind2;
        }
        else {
            if (kind == usingUnderscore) {
                return invalidPosition;
            }
            return kind;
        }
    }
    
    public final boolean getNextCharAsDigit() throws InvalidInputException {
        if (this.currentPosition >= this.eofPosition) {
            return false;
        }
        final int temp = this.currentPosition;
        try {
            final char currentCharacter = this.source[this.currentPosition++];
            this.currentCharacter = currentCharacter;
            if (currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
                if (!ScannerHelper.isDigit(this.currentCharacter)) {
                    this.currentPosition = temp;
                    --this.withoutUnicodePtr;
                    return false;
                }
                return true;
            }
            else {
                if (!ScannerHelper.isDigit(this.currentCharacter)) {
                    this.currentPosition = temp;
                    return false;
                }
                if (this.withoutUnicodePtr != 0) {
                    this.unicodeStore();
                }
                return true;
            }
        }
        catch (final IndexOutOfBoundsException ex) {
            this.currentPosition = temp;
            return false;
        }
        catch (final InvalidInputException ex2) {
            this.currentPosition = temp;
            return false;
        }
    }
    
    public final boolean getNextCharAsDigit(final int radix) {
        if (this.currentPosition >= this.eofPosition) {
            return false;
        }
        final int temp = this.currentPosition;
        try {
            final char currentCharacter = this.source[this.currentPosition++];
            this.currentCharacter = currentCharacter;
            if (currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
                if (ScannerHelper.digit(this.currentCharacter, radix) == -1) {
                    this.currentPosition = temp;
                    --this.withoutUnicodePtr;
                    return false;
                }
                return true;
            }
            else {
                if (ScannerHelper.digit(this.currentCharacter, radix) == -1) {
                    this.currentPosition = temp;
                    return false;
                }
                if (this.withoutUnicodePtr != 0) {
                    this.unicodeStore();
                }
                return true;
            }
        }
        catch (final IndexOutOfBoundsException ex) {
            this.currentPosition = temp;
            return false;
        }
        catch (final InvalidInputException ex2) {
            this.currentPosition = temp;
            return false;
        }
    }
    
    public boolean getNextCharAsJavaIdentifierPartWithBoundCheck() {
        final int pos = this.currentPosition;
        if (pos >= this.eofPosition) {
            return false;
        }
        final int temp2 = this.withoutUnicodePtr;
        try {
            boolean unicode = false;
            this.currentCharacter = this.source[this.currentPosition++];
            if (this.currentPosition < this.eofPosition && this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
                unicode = true;
            }
            final char c = this.currentCharacter;
            boolean isJavaIdentifierPart = false;
            if (c >= '\ud800' && c <= '\udbff') {
                if (this.complianceLevel < 3211264L) {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                final char low = (char)this.getNextCharWithBoundChecks();
                if (low < '\udc00' || low > '\udfff') {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(this.complianceLevel, c, low);
            }
            else {
                if (c >= '\udc00' && c <= '\udfff') {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(this.complianceLevel, c);
            }
            if (unicode) {
                if (!isJavaIdentifierPart) {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                return true;
            }
            else {
                if (!isJavaIdentifierPart) {
                    this.currentPosition = pos;
                    return false;
                }
                if (this.withoutUnicodePtr != 0) {
                    this.unicodeStore();
                }
                return true;
            }
        }
        catch (final InvalidInputException ex) {
            this.currentPosition = pos;
            this.withoutUnicodePtr = temp2;
            return false;
        }
    }
    
    public boolean getNextCharAsJavaIdentifierPart() {
        final int pos;
        if ((pos = this.currentPosition) >= this.eofPosition) {
            return false;
        }
        final int temp2 = this.withoutUnicodePtr;
        try {
            boolean unicode = false;
            final char currentCharacter = this.source[this.currentPosition++];
            this.currentCharacter = currentCharacter;
            if (currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
                unicode = true;
            }
            final char c = this.currentCharacter;
            boolean isJavaIdentifierPart = false;
            if (c >= '\ud800' && c <= '\udbff') {
                if (this.complianceLevel < 3211264L) {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                final char low = (char)this.getNextChar();
                if (low < '\udc00' || low > '\udfff') {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(this.complianceLevel, c, low);
            }
            else {
                if (c >= '\udc00' && c <= '\udfff') {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                isJavaIdentifierPart = ScannerHelper.isJavaIdentifierPart(this.complianceLevel, c);
            }
            if (unicode) {
                if (!isJavaIdentifierPart) {
                    this.currentPosition = pos;
                    this.withoutUnicodePtr = temp2;
                    return false;
                }
                return true;
            }
            else {
                if (!isJavaIdentifierPart) {
                    this.currentPosition = pos;
                    return false;
                }
                if (this.withoutUnicodePtr != 0) {
                    this.unicodeStore();
                }
                return true;
            }
        }
        catch (final IndexOutOfBoundsException ex) {
            this.currentPosition = pos;
            this.withoutUnicodePtr = temp2;
            return false;
        }
        catch (final InvalidInputException ex2) {
            this.currentPosition = pos;
            this.withoutUnicodePtr = temp2;
            return false;
        }
    }
    
    public int scanIdentifier() throws InvalidInputException {
        int whiteStart = 0;
        this.withoutUnicodePtr = 0;
        whiteStart = this.currentPosition;
        boolean hasWhiteSpaces = false;
        boolean checkIfUnicode = false;
        boolean isWhiteSpace = false;
        int unicodePtr;
        int offset;
        do {
            unicodePtr = this.withoutUnicodePtr;
            offset = this.currentPosition;
            this.startPosition = this.currentPosition;
            if (this.currentPosition < this.eofPosition) {
                this.currentCharacter = this.source[this.currentPosition++];
                checkIfUnicode = (this.currentPosition < this.eofPosition && this.currentCharacter == '\\' && this.source[this.currentPosition] == 'u');
                if (checkIfUnicode) {
                    isWhiteSpace = this.jumpOverUnicodeWhiteSpace();
                    offset = this.currentPosition - offset;
                }
                else {
                    offset = this.currentPosition - offset;
                    switch (this.currentCharacter) {
                        case '\t':
                        case '\n':
                        case '\f':
                        case '\r':
                        case ' ': {
                            isWhiteSpace = true;
                            break;
                        }
                        default: {
                            isWhiteSpace = false;
                            break;
                        }
                    }
                }
                if (!isWhiteSpace) {
                    continue;
                }
                hasWhiteSpaces = true;
            }
            else {
                if (this.tokenizeWhiteSpace && whiteStart != this.currentPosition - 1) {
                    --this.currentPosition;
                    this.startPosition = whiteStart;
                    return 1000;
                }
                return 60;
            }
        } while (isWhiteSpace);
        if (hasWhiteSpaces) {
            if (this.tokenizeWhiteSpace) {
                this.currentPosition -= offset;
                this.startPosition = whiteStart;
                if (checkIfUnicode) {
                    this.withoutUnicodePtr = unicodePtr;
                }
                return 1000;
            }
            if (checkIfUnicode) {
                this.withoutUnicodePtr = 0;
                this.unicodeStore();
            }
            else {
                this.withoutUnicodePtr = 0;
            }
        }
        final char c = this.currentCharacter;
        if (c < '\u0080') {
            if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0x0) {
                return this.scanIdentifierOrKeywordWithBoundCheck();
            }
            return 118;
        }
        else {
            boolean isJavaIdStart;
            if (c >= '\ud800' && c <= '\udbff') {
                if (this.complianceLevel < 3211264L) {
                    throw new InvalidInputException("Invalid_Unicode_Escape");
                }
                final char low = (char)this.getNextCharWithBoundChecks();
                if (low < '\udc00' || low > '\udfff') {
                    throw new InvalidInputException("Invalid_Low_Surrogate");
                }
                isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c, low);
            }
            else if (c >= '\udc00' && c <= '\udfff') {
                if (this.complianceLevel < 3211264L) {
                    throw new InvalidInputException("Invalid_Unicode_Escape");
                }
                throw new InvalidInputException("Invalid_High_Surrogate");
            }
            else {
                isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c);
            }
            if (isJavaIdStart) {
                return this.scanIdentifierOrKeywordWithBoundCheck();
            }
            return 118;
        }
    }
    
    public void ungetToken(final int unambiguousToken) {
        if (this.nextToken != 0) {
            throw new ArrayIndexOutOfBoundsException("Single cell array overflow");
        }
        this.nextToken = unambiguousToken;
    }
    
    public int getNextToken() throws InvalidInputException {
        if (this.nextToken != 0) {
            final int token = this.nextToken;
            this.nextToken = 0;
            return token;
        }
        int token = this.getNextToken0();
        if (this.activeParser == null) {
            return token;
        }
        if (token == 24 || token == 11 || token == 37) {
            token = this.disambiguatedToken(token);
        }
        else if (token == 113) {
            this.consumingEllipsisAnnotations = false;
        }
        this.lookBack[0] = this.lookBack[1];
        return this.lookBack[1] = token;
    }
    
    protected int getNextToken0() throws InvalidInputException {
        this.wasAcr = false;
        if (this.diet) {
            this.jumpOverMethodBody();
            this.diet = false;
            return (this.currentPosition > this.eofPosition) ? 60 : 32;
        }
        int whiteStart = 0;
        try {
            Label_3418: {
                Label_3397: {
                Label_3382:
                    while (true) {
                        this.withoutUnicodePtr = 0;
                        whiteStart = this.currentPosition;
                        boolean hasWhiteSpaces = false;
                        boolean checkIfUnicode = false;
                        boolean isWhiteSpace = false;
                        int unicodePtr;
                        int offset;
                        do {
                            unicodePtr = this.withoutUnicodePtr;
                            offset = this.currentPosition;
                            this.startPosition = this.currentPosition;
                            try {
                                final char currentCharacter = this.source[this.currentPosition++];
                                this.currentCharacter = currentCharacter;
                                checkIfUnicode = (currentCharacter == '\\' && this.source[this.currentPosition] == 'u');
                            }
                            catch (final IndexOutOfBoundsException ex) {
                                if (this.tokenizeWhiteSpace && whiteStart != this.currentPosition - 1) {
                                    --this.currentPosition;
                                    this.startPosition = whiteStart;
                                    return 1000;
                                }
                                if (this.currentPosition > this.eofPosition) {
                                    return 60;
                                }
                            }
                            if (this.currentPosition > this.eofPosition) {
                                if (this.tokenizeWhiteSpace && whiteStart != this.currentPosition - 1) {
                                    --this.currentPosition;
                                    this.startPosition = whiteStart;
                                    return 1000;
                                }
                                return 60;
                            }
                            else {
                                if (checkIfUnicode) {
                                    isWhiteSpace = this.jumpOverUnicodeWhiteSpace();
                                    offset = this.currentPosition - offset;
                                }
                                else {
                                    offset = this.currentPosition - offset;
                                    if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                                        this.pushLineSeparator();
                                    }
                                    switch (this.currentCharacter) {
                                        case '\t':
                                        case '\n':
                                        case '\f':
                                        case '\r':
                                        case ' ': {
                                            isWhiteSpace = true;
                                            break;
                                        }
                                        default: {
                                            isWhiteSpace = false;
                                            break;
                                        }
                                    }
                                }
                                if (!isWhiteSpace) {
                                    continue;
                                }
                                hasWhiteSpaces = true;
                            }
                        } while (isWhiteSpace);
                        if (hasWhiteSpaces) {
                            if (this.tokenizeWhiteSpace) {
                                this.currentPosition -= offset;
                                this.startPosition = whiteStart;
                                if (checkIfUnicode) {
                                    this.withoutUnicodePtr = unicodePtr;
                                }
                                return 1000;
                            }
                            if (checkIfUnicode) {
                                this.withoutUnicodePtr = 0;
                                this.unicodeStore();
                            }
                            else {
                                this.withoutUnicodePtr = 0;
                            }
                        }
                        switch (this.currentCharacter) {
                            case '@': {
                                return 37;
                            }
                            case '(': {
                                return 24;
                            }
                            case ')': {
                                return 25;
                            }
                            case '{': {
                                return 49;
                            }
                            case '}': {
                                return 32;
                            }
                            case '[': {
                                return 10;
                            }
                            case ']': {
                                return 64;
                            }
                            case ';': {
                                return 28;
                            }
                            case ',': {
                                return 33;
                            }
                            case '.': {
                                if (this.getNextCharAsDigit()) {
                                    return this.scanNumber(true);
                                }
                                final int temp = this.currentPosition;
                                if (!this.getNextChar('.')) {
                                    this.currentPosition = temp;
                                    return 3;
                                }
                                if (this.getNextChar('.')) {
                                    return 113;
                                }
                                this.currentPosition = temp;
                                return 3;
                            }
                            case '+': {
                                final int test;
                                if ((test = this.getNextChar('+', '=')) == 0) {
                                    return 1;
                                }
                                if (test > 0) {
                                    return 84;
                                }
                                return 4;
                            }
                            case '-': {
                                final int test;
                                if ((test = this.getNextChar('-', '=')) == 0) {
                                    return 2;
                                }
                                if (test > 0) {
                                    return 85;
                                }
                                if (this.getNextChar('>')) {
                                    return 110;
                                }
                                return 5;
                            }
                            case '~': {
                                return 63;
                            }
                            case '!': {
                                if (this.getNextChar('=')) {
                                    return 20;
                                }
                                return 62;
                            }
                            case '*': {
                                if (this.getNextChar('=')) {
                                    return 86;
                                }
                                return 6;
                            }
                            case '%': {
                                if (this.getNextChar('=')) {
                                    return 91;
                                }
                                return 8;
                            }
                            case '<': {
                                final int test;
                                if ((test = this.getNextChar('=', '<')) == 0) {
                                    return 12;
                                }
                                if (test <= 0) {
                                    return 11;
                                }
                                if (this.getNextChar('=')) {
                                    return 92;
                                }
                                return 18;
                            }
                            case '>': {
                                if (this.returnOnlyGreater) {
                                    return 15;
                                }
                                int test;
                                if ((test = this.getNextChar('=', '>')) == 0) {
                                    return 13;
                                }
                                if (test <= 0) {
                                    return 15;
                                }
                                if ((test = this.getNextChar('=', '>')) == 0) {
                                    return 93;
                                }
                                if (test <= 0) {
                                    return 14;
                                }
                                if (this.getNextChar('=')) {
                                    return 94;
                                }
                                return 16;
                            }
                            case '=': {
                                if (this.getNextChar('=')) {
                                    return 19;
                                }
                                return 70;
                            }
                            case '&': {
                                final int test;
                                if ((test = this.getNextChar('&', '=')) == 0) {
                                    return 30;
                                }
                                if (test > 0) {
                                    return 88;
                                }
                                return 21;
                            }
                            case '|': {
                                final int test;
                                if ((test = this.getNextChar('|', '=')) == 0) {
                                    return 31;
                                }
                                if (test > 0) {
                                    return 89;
                                }
                                return 26;
                            }
                            case '^': {
                                if (this.getNextChar('=')) {
                                    return 90;
                                }
                                return 23;
                            }
                            case '?': {
                                return 29;
                            }
                            case ':': {
                                if (this.getNextChar(':')) {
                                    return 7;
                                }
                                return 61;
                            }
                            case '\'': {
                                final int test;
                                if ((test = this.getNextChar('\n', '\r')) == 0) {
                                    throw new InvalidInputException("Invalid_Character_Constant");
                                }
                                if (test > 0) {
                                    for (int lookAhead = 0; lookAhead < 3; ++lookAhead) {
                                        if (this.currentPosition + lookAhead == this.eofPosition) {
                                            break;
                                        }
                                        if (this.source[this.currentPosition + lookAhead] == '\n') {
                                            break;
                                        }
                                        if (this.source[this.currentPosition + lookAhead] == '\'') {
                                            this.currentPosition += lookAhead + 1;
                                            break;
                                        }
                                    }
                                    throw new InvalidInputException("Invalid_Character_Constant");
                                }
                                if (this.getNextChar('\'')) {
                                    for (int lookAhead2 = 0; lookAhead2 < 3; ++lookAhead2) {
                                        if (this.currentPosition + lookAhead2 == this.eofPosition) {
                                            break;
                                        }
                                        if (this.source[this.currentPosition + lookAhead2] == '\n') {
                                            break;
                                        }
                                        if (this.source[this.currentPosition + lookAhead2] == '\'') {
                                            this.currentPosition += lookAhead2 + 1;
                                            break;
                                        }
                                    }
                                    throw new InvalidInputException("Invalid_Character_Constant");
                                }
                                if (this.getNextChar('\\')) {
                                    if (this.unicodeAsBackSlash) {
                                        this.unicodeAsBackSlash = false;
                                        final char currentCharacter2 = this.source[this.currentPosition++];
                                        this.currentCharacter = currentCharacter2;
                                        if (currentCharacter2 == '\\' && this.source[this.currentPosition] == 'u') {
                                            this.getNextUnicodeChar();
                                        }
                                        else if (this.withoutUnicodePtr != 0) {
                                            this.unicodeStore();
                                        }
                                    }
                                    else {
                                        this.currentCharacter = this.source[this.currentPosition++];
                                    }
                                    this.scanEscapeCharacter();
                                }
                                else {
                                    this.unicodeAsBackSlash = false;
                                    checkIfUnicode = false;
                                    try {
                                        final char currentCharacter3 = this.source[this.currentPosition++];
                                        this.currentCharacter = currentCharacter3;
                                        checkIfUnicode = (currentCharacter3 == '\\' && this.source[this.currentPosition] == 'u');
                                    }
                                    catch (final IndexOutOfBoundsException ex2) {
                                        --this.currentPosition;
                                        throw new InvalidInputException("Invalid_Character_Constant");
                                    }
                                    if (checkIfUnicode) {
                                        this.getNextUnicodeChar();
                                    }
                                    else if (this.withoutUnicodePtr != 0) {
                                        this.unicodeStore();
                                    }
                                }
                                if (this.getNextChar('\'')) {
                                    return 47;
                                }
                                for (int lookAhead2 = 0; lookAhead2 < 20; ++lookAhead2) {
                                    if (this.currentPosition + lookAhead2 == this.eofPosition) {
                                        break;
                                    }
                                    if (this.source[this.currentPosition + lookAhead2] == '\n') {
                                        break;
                                    }
                                    if (this.source[this.currentPosition + lookAhead2] == '\'') {
                                        this.currentPosition += lookAhead2 + 1;
                                        break;
                                    }
                                }
                                throw new InvalidInputException("Invalid_Character_Constant");
                            }
                            case '\"': {
                                try {
                                    this.unicodeAsBackSlash = false;
                                    boolean isUnicode = false;
                                    final char currentCharacter4 = this.source[this.currentPosition++];
                                    this.currentCharacter = currentCharacter4;
                                    if (currentCharacter4 == '\\' && this.source[this.currentPosition] == 'u') {
                                        this.getNextUnicodeChar();
                                        isUnicode = true;
                                    }
                                    else if (this.withoutUnicodePtr != 0) {
                                        this.unicodeStore();
                                    }
                                    while (this.currentCharacter != '\"') {
                                        if (this.currentPosition >= this.eofPosition) {
                                            throw new InvalidInputException("Unterminated_String");
                                        }
                                        if (this.currentCharacter == '\n' || this.currentCharacter == '\r') {
                                            if (isUnicode) {
                                                final int start = this.currentPosition;
                                                for (int lookAhead3 = 0; lookAhead3 < 50; ++lookAhead3) {
                                                    if (this.currentPosition >= this.eofPosition) {
                                                        this.currentPosition = start;
                                                        break;
                                                    }
                                                    final char currentCharacter5 = this.source[this.currentPosition++];
                                                    this.currentCharacter = currentCharacter5;
                                                    if (currentCharacter5 == '\\' && this.source[this.currentPosition] == 'u') {
                                                        isUnicode = true;
                                                        this.getNextUnicodeChar();
                                                    }
                                                    else {
                                                        isUnicode = false;
                                                    }
                                                    if (!isUnicode && this.currentCharacter == '\n') {
                                                        --this.currentPosition;
                                                        break;
                                                    }
                                                    if (this.currentCharacter == '\"') {
                                                        throw new InvalidInputException("Invalid_Char_In_String");
                                                    }
                                                }
                                            }
                                            else {
                                                --this.currentPosition;
                                            }
                                            throw new InvalidInputException("Invalid_Char_In_String");
                                        }
                                        if (this.currentCharacter == '\\') {
                                            if (this.unicodeAsBackSlash) {
                                                --this.withoutUnicodePtr;
                                                this.unicodeAsBackSlash = false;
                                                final char currentCharacter6 = this.source[this.currentPosition++];
                                                this.currentCharacter = currentCharacter6;
                                                if (currentCharacter6 == '\\' && this.source[this.currentPosition] == 'u') {
                                                    this.getNextUnicodeChar();
                                                    isUnicode = true;
                                                    --this.withoutUnicodePtr;
                                                }
                                                else {
                                                    isUnicode = false;
                                                }
                                            }
                                            else {
                                                if (this.withoutUnicodePtr == 0) {
                                                    this.unicodeInitializeBuffer(this.currentPosition - this.startPosition);
                                                }
                                                --this.withoutUnicodePtr;
                                                this.currentCharacter = this.source[this.currentPosition++];
                                            }
                                            this.scanEscapeCharacter();
                                            if (this.withoutUnicodePtr != 0) {
                                                this.unicodeStore();
                                            }
                                        }
                                        this.unicodeAsBackSlash = false;
                                        final char currentCharacter7 = this.source[this.currentPosition++];
                                        this.currentCharacter = currentCharacter7;
                                        if (currentCharacter7 == '\\' && this.source[this.currentPosition] == 'u') {
                                            this.getNextUnicodeChar();
                                            isUnicode = true;
                                        }
                                        else {
                                            isUnicode = false;
                                            if (this.withoutUnicodePtr == 0) {
                                                continue;
                                            }
                                            this.unicodeStore();
                                        }
                                    }
                                }
                                catch (final IndexOutOfBoundsException ex3) {
                                    --this.currentPosition;
                                    throw new InvalidInputException("Unterminated_String");
                                }
                                catch (final InvalidInputException e) {
                                    if (e.getMessage().equals("Invalid_Escape")) {
                                        for (int lookAhead = 0; lookAhead < 50; ++lookAhead) {
                                            if (this.currentPosition + lookAhead == this.eofPosition) {
                                                break;
                                            }
                                            if (this.source[this.currentPosition + lookAhead] == '\n') {
                                                break;
                                            }
                                            if (this.source[this.currentPosition + lookAhead] == '\"') {
                                                this.currentPosition += lookAhead + 1;
                                                break;
                                            }
                                        }
                                    }
                                    throw e;
                                }
                                return 48;
                            }
                            case '/': {
                                if (this.skipComments) {
                                    break Label_3382;
                                }
                                final int test = this.getNextChar('/', '*');
                                if (test == 0) {
                                    this.lastCommentLinePosition = this.currentPosition;
                                    try {
                                        final char currentCharacter8 = this.source[this.currentPosition++];
                                        this.currentCharacter = currentCharacter8;
                                        if (currentCharacter8 == '\\' && this.source[this.currentPosition] == 'u') {
                                            this.getNextUnicodeChar();
                                        }
                                        if (this.currentCharacter == '\\' && this.source[this.currentPosition] == '\\') {
                                            ++this.currentPosition;
                                        }
                                        boolean isUnicode2 = false;
                                        while (this.currentCharacter != '\r' && this.currentCharacter != '\n') {
                                            if (this.currentPosition >= this.eofPosition) {
                                                this.lastCommentLinePosition = this.currentPosition;
                                                ++this.currentPosition;
                                                throw new IndexOutOfBoundsException();
                                            }
                                            this.lastCommentLinePosition = this.currentPosition;
                                            isUnicode2 = false;
                                            final char currentCharacter9 = this.source[this.currentPosition++];
                                            this.currentCharacter = currentCharacter9;
                                            if (currentCharacter9 == '\\' && this.source[this.currentPosition] == 'u') {
                                                this.getNextUnicodeChar();
                                                isUnicode2 = true;
                                            }
                                            if (this.currentCharacter != '\\' || this.source[this.currentPosition] != '\\') {
                                                continue;
                                            }
                                            ++this.currentPosition;
                                        }
                                        if (this.currentCharacter == '\r' && this.eofPosition > this.currentPosition) {
                                            if (this.source[this.currentPosition] == '\n') {
                                                ++this.currentPosition;
                                                this.currentCharacter = '\n';
                                            }
                                            else if (this.source[this.currentPosition] == '\\' && this.source[this.currentPosition + 1] == 'u') {
                                                this.getNextUnicodeChar();
                                                isUnicode2 = true;
                                            }
                                        }
                                        this.recordComment(1001);
                                        if (this.taskTags != null) {
                                            this.checkTaskTag(this.startPosition, this.currentPosition);
                                        }
                                        if (this.currentCharacter == '\r' || this.currentCharacter == '\n') {
                                            if ((this.checkNonExternalizedStringLiterals || this.checkUninternedIdentityComparison) && this.lastPosition < this.currentPosition) {
                                                this.parseTags();
                                            }
                                            if (this.recordLineSeparator) {
                                                if (isUnicode2) {
                                                    this.pushUnicodeLineSeparator();
                                                }
                                                else {
                                                    this.pushLineSeparator();
                                                }
                                            }
                                        }
                                        if (this.tokenizeComments) {
                                            return 1001;
                                        }
                                        continue;
                                    }
                                    catch (final IndexOutOfBoundsException ex4) {
                                        --this.currentPosition;
                                        this.recordComment(1001);
                                        if (this.taskTags != null) {
                                            this.checkTaskTag(this.startPosition, this.currentPosition);
                                        }
                                        if ((this.checkNonExternalizedStringLiterals || this.checkUninternedIdentityComparison) && this.lastPosition < this.currentPosition) {
                                            this.parseTags();
                                        }
                                        if (this.tokenizeComments) {
                                            return 1001;
                                        }
                                        ++this.currentPosition;
                                        continue;
                                    }
                                }
                                if (test > 0) {
                                    try {
                                        boolean isJavadoc = false;
                                        boolean star = false;
                                        boolean isUnicode3 = false;
                                        this.unicodeAsBackSlash = false;
                                        final char currentCharacter10 = this.source[this.currentPosition++];
                                        this.currentCharacter = currentCharacter10;
                                        if (currentCharacter10 == '\\' && this.source[this.currentPosition] == 'u') {
                                            this.getNextUnicodeChar();
                                            isUnicode3 = true;
                                        }
                                        else {
                                            isUnicode3 = false;
                                            if (this.withoutUnicodePtr != 0) {
                                                this.unicodeStore();
                                            }
                                        }
                                        if (this.currentCharacter == '*') {
                                            isJavadoc = true;
                                            star = true;
                                        }
                                        if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                                            if (isUnicode3) {
                                                this.pushUnicodeLineSeparator();
                                            }
                                            else {
                                                this.pushLineSeparator();
                                            }
                                        }
                                        isUnicode3 = false;
                                        int previous = this.currentPosition;
                                        final char currentCharacter11 = this.source[this.currentPosition++];
                                        this.currentCharacter = currentCharacter11;
                                        if (currentCharacter11 == '\\' && this.source[this.currentPosition] == 'u') {
                                            this.getNextUnicodeChar();
                                            isUnicode3 = true;
                                        }
                                        else {
                                            isUnicode3 = false;
                                        }
                                        if (this.currentCharacter == '\\' && this.source[this.currentPosition] == '\\') {
                                            ++this.currentPosition;
                                        }
                                        if (this.currentCharacter == '/') {
                                            isJavadoc = false;
                                        }
                                        int firstTag = 0;
                                        while (this.currentCharacter != '/' || !star) {
                                            if (this.currentPosition >= this.eofPosition) {
                                                throw new InvalidInputException("Unterminated_Comment");
                                            }
                                            if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                                                if (isUnicode3) {
                                                    this.pushUnicodeLineSeparator();
                                                }
                                                else {
                                                    this.pushLineSeparator();
                                                }
                                            }
                                            Label_3193: {
                                                switch (this.currentCharacter) {
                                                    case '*': {
                                                        star = true;
                                                        break Label_3193;
                                                    }
                                                    case '@': {
                                                        if (firstTag == 0 && this.isFirstTag()) {
                                                            firstTag = previous;
                                                            break;
                                                        }
                                                        break;
                                                    }
                                                }
                                                star = false;
                                            }
                                            previous = this.currentPosition;
                                            final char currentCharacter12 = this.source[this.currentPosition++];
                                            this.currentCharacter = currentCharacter12;
                                            if (currentCharacter12 == '\\' && this.source[this.currentPosition] == 'u') {
                                                this.getNextUnicodeChar();
                                                isUnicode3 = true;
                                            }
                                            else {
                                                isUnicode3 = false;
                                            }
                                            if (this.currentCharacter != '\\' || this.source[this.currentPosition] != '\\') {
                                                continue;
                                            }
                                            ++this.currentPosition;
                                        }
                                        final int token = isJavadoc ? 1003 : 1002;
                                        this.recordComment(token);
                                        this.commentTagStarts[this.commentPtr] = firstTag;
                                        if (this.taskTags != null) {
                                            this.checkTaskTag(this.startPosition, this.currentPosition);
                                        }
                                        if (this.tokenizeComments) {
                                            return token;
                                        }
                                        continue;
                                    }
                                    catch (final IndexOutOfBoundsException ex5) {
                                        --this.currentPosition;
                                        throw new InvalidInputException("Unterminated_Comment");
                                    }
                                    break Label_3382;
                                }
                                break Label_3382;
                            }
                            case '\u001a': {
                                break Label_3397;
                            }
                            default: {
                                break Label_3418;
                            }
                        }
                    }
                    if (this.getNextChar('=')) {
                        return 87;
                    }
                    return 9;
                }
                if (this.atEnd()) {
                    return 60;
                }
                throw new InvalidInputException("Ctrl-Z");
            }
            final char c = this.currentCharacter;
            if (c < '\u0080') {
                if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0x0) {
                    return this.scanIdentifierOrKeyword();
                }
                if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x4) != 0x0) {
                    return this.scanNumber(false);
                }
                return 118;
            }
            else {
                boolean isJavaIdStart;
                if (c >= '\ud800' && c <= '\udbff') {
                    if (this.complianceLevel < 3211264L) {
                        throw new InvalidInputException("Invalid_Unicode_Escape");
                    }
                    final char low = (char)this.getNextChar();
                    if (low < '\udc00' || low > '\udfff') {
                        throw new InvalidInputException("Invalid_Low_Surrogate");
                    }
                    isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c, low);
                }
                else if (c >= '\udc00' && c <= '\udfff') {
                    if (this.complianceLevel < 3211264L) {
                        throw new InvalidInputException("Invalid_Unicode_Escape");
                    }
                    throw new InvalidInputException("Invalid_High_Surrogate");
                }
                else {
                    isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c);
                }
                if (isJavaIdStart) {
                    return this.scanIdentifierOrKeyword();
                }
                if (ScannerHelper.isDigit(this.currentCharacter)) {
                    return this.scanNumber(false);
                }
                return 118;
            }
        }
        catch (final IndexOutOfBoundsException ex6) {
            if (this.tokenizeWhiteSpace && whiteStart != this.currentPosition - 1) {
                --this.currentPosition;
                this.startPosition = whiteStart;
                return 1000;
            }
            return 60;
        }
    }
    
    public void getNextUnicodeChar() throws InvalidInputException {
        int c1 = 0;
        int c2 = 0;
        int c3 = 0;
        int c4 = 0;
        int unicodeSize = 6;
        ++this.currentPosition;
        if (this.currentPosition >= this.eofPosition) {
            --this.currentPosition;
            throw new InvalidInputException("Invalid_Unicode_Escape");
        }
        while (this.source[this.currentPosition] == 'u') {
            ++this.currentPosition;
            if (this.currentPosition >= this.eofPosition) {
                --this.currentPosition;
                throw new InvalidInputException("Invalid_Unicode_Escape");
            }
            ++unicodeSize;
        }
        if (this.currentPosition + 4 > this.eofPosition) {
            this.currentPosition += this.eofPosition - this.currentPosition;
            throw new InvalidInputException("Invalid_Unicode_Escape");
        }
        if ((c1 = ScannerHelper.getHexadecimalValue(this.source[this.currentPosition++])) > 15 || c1 < 0 || ((c2 = ScannerHelper.getHexadecimalValue(this.source[this.currentPosition++])) > 15 || c2 < 0) || ((c3 = ScannerHelper.getHexadecimalValue(this.source[this.currentPosition++])) > 15 || c3 < 0) || (c4 = ScannerHelper.getHexadecimalValue(this.source[this.currentPosition++])) > 15 || c4 < 0) {
            throw new InvalidInputException("Invalid_Unicode_Escape");
        }
        this.currentCharacter = (char)(((c1 * 16 + c2) * 16 + c3) * 16 + c4);
        if (this.withoutUnicodePtr == 0) {
            this.unicodeInitializeBuffer(this.currentPosition - unicodeSize - this.startPosition);
        }
        this.unicodeStore();
        this.unicodeAsBackSlash = (this.currentCharacter == '\\');
    }
    
    public NLSTag[] getNLSTags() {
        final int length = this.nlsTagsPtr;
        if (length != 0) {
            final NLSTag[] result = new NLSTag[length];
            System.arraycopy(this.nlsTags, 0, result, 0, length);
            this.nlsTagsPtr = 0;
            return result;
        }
        return null;
    }
    
    public boolean[] getIdentityComparisonLines() {
        final boolean[] retVal = this.validIdentityComparisonLines;
        this.validIdentityComparisonLines = null;
        return retVal;
    }
    
    public char[] getSource() {
        return this.source;
    }
    
    protected boolean isFirstTag() {
        return true;
    }
    
    public final void jumpOverMethodBody() {
        this.wasAcr = false;
        int found = 1;
        try {
            boolean isWhiteSpace;
            char currentCharacter;
            boolean test;
            char currentCharacter2;
            char currentCharacter3;
            char currentCharacter4;
            int test2;
            char currentCharacter5;
            boolean isUnicode;
            char currentCharacter6;
            boolean isJavadoc;
            boolean star;
            boolean isUnicode2;
            char currentCharacter7;
            int previous;
            char currentCharacter8;
            int firstTag;
            char currentCharacter9;
            char c;
            char low;
            boolean isJavaIdStart;
            Block_7:Label_0440_Outer:
            while (true) {
                this.withoutUnicodePtr = 0;
                do {
                    this.startPosition = this.currentPosition;
                    currentCharacter = this.source[this.currentPosition++];
                    this.currentCharacter = currentCharacter;
                    if (currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                        isWhiteSpace = this.jumpOverUnicodeWhiteSpace();
                    }
                    else {
                        if (this.recordLineSeparator && (this.currentCharacter == '\r' || this.currentCharacter == '\n')) {
                            this.pushLineSeparator();
                        }
                        isWhiteSpace = CharOperation.isWhitespace(this.currentCharacter);
                    }
                } while (isWhiteSpace);
                switch (this.currentCharacter) {
                    case '{': {
                        ++found;
                        continue;
                    }
                    case '}': {
                        if (--found == 0) {
                            break Block_7;
                        }
                        continue;
                    }
                    case '\'': {
                        test = this.getNextChar('\\');
                        if (test) {
                            try {
                                if (this.unicodeAsBackSlash) {
                                    this.unicodeAsBackSlash = false;
                                    currentCharacter2 = this.source[this.currentPosition++];
                                    this.currentCharacter = currentCharacter2;
                                    if (currentCharacter2 == '\\' && this.source[this.currentPosition] == 'u') {
                                        this.getNextUnicodeChar();
                                    }
                                    else if (this.withoutUnicodePtr != 0) {
                                        this.unicodeStore();
                                    }
                                }
                                else {
                                    this.currentCharacter = this.source[this.currentPosition++];
                                }
                                this.scanEscapeCharacter();
                            }
                            catch (final InvalidInputException ex) {}
                        }
                        else {
                            try {
                                this.unicodeAsBackSlash = false;
                                if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                }
                                else if (this.withoutUnicodePtr != 0) {
                                    this.unicodeStore();
                                }
                            }
                            catch (final InvalidInputException ex2) {}
                        }
                        this.getNextChar('\'');
                        continue;
                    }
                    case '\"': {
                        try {
                            while (true) {
                                Label_0677: {
                                    try {
                                        this.unicodeAsBackSlash = false;
                                        currentCharacter3 = this.source[this.currentPosition++];
                                        this.currentCharacter = currentCharacter3;
                                        if (currentCharacter3 == '\\' && this.source[this.currentPosition] == 'u') {
                                            this.getNextUnicodeChar();
                                            break Label_0677;
                                        }
                                        if (this.withoutUnicodePtr != 0) {
                                            this.unicodeStore();
                                        }
                                        break Label_0677;
                                    }
                                    catch (final InvalidInputException ex3) {
                                        break Label_0677;
                                    }
                                    if (this.currentPosition >= this.eofPosition) {
                                        return;
                                    }
                                    if (this.currentCharacter == '\r') {
                                        if (this.source[this.currentPosition] == '\n') {
                                            ++this.currentPosition;
                                            continue Label_0440_Outer;
                                        }
                                        continue Label_0440_Outer;
                                    }
                                    else {
                                        if (this.currentCharacter == '\n') {
                                            continue Label_0440_Outer;
                                        }
                                        if (this.currentCharacter == '\\') {
                                            try {
                                                if (this.unicodeAsBackSlash) {
                                                    this.unicodeAsBackSlash = false;
                                                    currentCharacter4 = this.source[this.currentPosition++];
                                                    this.currentCharacter = currentCharacter4;
                                                    if (currentCharacter4 == '\\' && this.source[this.currentPosition] == 'u') {
                                                        this.getNextUnicodeChar();
                                                    }
                                                    else if (this.withoutUnicodePtr != 0) {
                                                        this.unicodeStore();
                                                    }
                                                }
                                                else {
                                                    this.currentCharacter = this.source[this.currentPosition++];
                                                }
                                                this.scanEscapeCharacter();
                                            }
                                            catch (final InvalidInputException ex4) {}
                                        }
                                        try {
                                            this.unicodeAsBackSlash = false;
                                            if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                                this.getNextUnicodeChar();
                                            }
                                            else if (this.withoutUnicodePtr != 0) {
                                                this.unicodeStore();
                                            }
                                        }
                                        catch (final InvalidInputException ex5) {}
                                    }
                                }
                                if (this.currentCharacter == '\"') {
                                    continue Label_0440_Outer;
                                }
                                continue;
                            }
                        }
                        catch (final IndexOutOfBoundsException ex6) {
                            return;
                        }
                    }
                    case '/': {
                        if ((test2 = this.getNextChar('/', '*')) == 0) {
                            try {
                                this.lastCommentLinePosition = this.currentPosition;
                                currentCharacter5 = this.source[this.currentPosition++];
                                this.currentCharacter = currentCharacter5;
                                if (currentCharacter5 == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                }
                                if (this.currentCharacter == '\\' && this.source[this.currentPosition] == '\\') {
                                    ++this.currentPosition;
                                }
                                isUnicode = false;
                                while (this.currentCharacter != '\r' && this.currentCharacter != '\n') {
                                    if (this.currentPosition >= this.eofPosition) {
                                        this.lastCommentLinePosition = this.currentPosition;
                                        ++this.currentPosition;
                                        throw new IndexOutOfBoundsException();
                                    }
                                    this.lastCommentLinePosition = this.currentPosition;
                                    isUnicode = false;
                                    currentCharacter6 = this.source[this.currentPosition++];
                                    this.currentCharacter = currentCharacter6;
                                    if (currentCharacter6 == '\\' && this.source[this.currentPosition] == 'u') {
                                        isUnicode = true;
                                        this.getNextUnicodeChar();
                                    }
                                    if (this.currentCharacter != '\\' || this.source[this.currentPosition] != '\\') {
                                        continue;
                                    }
                                    ++this.currentPosition;
                                }
                                if (this.currentCharacter == '\r' && this.eofPosition > this.currentPosition) {
                                    if (this.source[this.currentPosition] == '\n') {
                                        ++this.currentPosition;
                                        this.currentCharacter = '\n';
                                    }
                                    else if (this.source[this.currentPosition] == '\\' && this.source[this.currentPosition + 1] == 'u') {
                                        isUnicode = true;
                                        this.getNextUnicodeChar();
                                    }
                                }
                                this.recordComment(1001);
                                if (!this.recordLineSeparator || (this.currentCharacter != '\r' && this.currentCharacter != '\n')) {
                                    continue;
                                }
                                if ((this.checkNonExternalizedStringLiterals || this.checkUninternedIdentityComparison) && this.lastPosition < this.currentPosition) {
                                    this.parseTags();
                                }
                                if (!this.recordLineSeparator) {
                                    continue;
                                }
                                if (isUnicode) {
                                    this.pushUnicodeLineSeparator();
                                }
                                else {
                                    this.pushLineSeparator();
                                }
                            }
                            catch (final IndexOutOfBoundsException ex7) {
                                --this.currentPosition;
                                this.recordComment(1001);
                                if ((this.checkNonExternalizedStringLiterals || this.checkUninternedIdentityComparison) && this.lastPosition < this.currentPosition) {
                                    this.parseTags();
                                }
                                if (this.tokenizeComments) {
                                    continue;
                                }
                                ++this.currentPosition;
                            }
                            continue;
                        }
                        if (test2 > 0) {
                            isJavadoc = false;
                            try {
                                star = false;
                                isUnicode2 = false;
                                this.unicodeAsBackSlash = false;
                                currentCharacter7 = this.source[this.currentPosition++];
                                this.currentCharacter = currentCharacter7;
                                if (currentCharacter7 == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                    isUnicode2 = true;
                                }
                                else {
                                    isUnicode2 = false;
                                    if (this.withoutUnicodePtr != 0) {
                                        this.unicodeStore();
                                    }
                                }
                                if (this.currentCharacter == '*') {
                                    isJavadoc = true;
                                    star = true;
                                }
                                if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                                    if (isUnicode2) {
                                        this.pushUnicodeLineSeparator();
                                    }
                                    else {
                                        this.pushLineSeparator();
                                    }
                                }
                                isUnicode2 = false;
                                previous = this.currentPosition;
                                currentCharacter8 = this.source[this.currentPosition++];
                                this.currentCharacter = currentCharacter8;
                                if (currentCharacter8 == '\\' && this.source[this.currentPosition] == 'u') {
                                    this.getNextUnicodeChar();
                                    isUnicode2 = true;
                                }
                                else {
                                    isUnicode2 = false;
                                }
                                if (this.currentCharacter == '\\' && this.source[this.currentPosition] == '\\') {
                                    ++this.currentPosition;
                                }
                                if (this.currentCharacter == '/') {
                                    isJavadoc = false;
                                }
                                firstTag = 0;
                                while (this.currentCharacter != '/' || !star) {
                                    if (this.currentPosition >= this.eofPosition) {
                                        return;
                                    }
                                    if ((this.currentCharacter == '\r' || this.currentCharacter == '\n') && this.recordLineSeparator) {
                                        if (isUnicode2) {
                                            this.pushUnicodeLineSeparator();
                                        }
                                        else {
                                            this.pushLineSeparator();
                                        }
                                    }
                                    Label_1545: {
                                        switch (this.currentCharacter) {
                                            case '*': {
                                                star = true;
                                                break Label_1545;
                                            }
                                            case '@': {
                                                if (firstTag == 0 && this.isFirstTag()) {
                                                    firstTag = previous;
                                                    break;
                                                }
                                                break;
                                            }
                                        }
                                        star = false;
                                    }
                                    previous = this.currentPosition;
                                    currentCharacter9 = this.source[this.currentPosition++];
                                    this.currentCharacter = currentCharacter9;
                                    if (currentCharacter9 == '\\' && this.source[this.currentPosition] == 'u') {
                                        this.getNextUnicodeChar();
                                        isUnicode2 = true;
                                    }
                                    else {
                                        isUnicode2 = false;
                                    }
                                    if (this.currentCharacter != '\\' || this.source[this.currentPosition] != '\\') {
                                        continue;
                                    }
                                    ++this.currentPosition;
                                }
                                this.recordComment(isJavadoc ? 1003 : 1002);
                                this.commentTagStarts[this.commentPtr] = firstTag;
                                continue;
                            }
                            catch (final IndexOutOfBoundsException ex8) {
                                return;
                            }
                            break;
                        }
                        continue;
                    }
                }
                try {
                    c = this.currentCharacter;
                    if (c < '\u0080') {
                        if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x40) != 0x0) {
                            this.scanIdentifierOrKeyword();
                        }
                        else {
                            if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x4) == 0x0) {
                                continue;
                            }
                            this.scanNumber(false);
                        }
                    }
                    else {
                        if (c >= '\ud800' && c <= '\udbff') {
                            if (this.complianceLevel < 3211264L) {
                                throw new InvalidInputException("Invalid_Unicode_Escape");
                            }
                            low = (char)this.getNextChar();
                            if (low < '\udc00') {
                                continue;
                            }
                            if (low > '\udfff') {
                                continue;
                            }
                            isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c, low);
                        }
                        else {
                            if (c >= '\udc00' && c <= '\udfff') {
                                continue;
                            }
                            isJavaIdStart = ScannerHelper.isJavaIdentifierStart(this.complianceLevel, c);
                        }
                        if (!isJavaIdStart) {
                            continue;
                        }
                        this.scanIdentifierOrKeyword();
                    }
                }
                catch (final InvalidInputException ex9) {}
            }
        }
        catch (final IndexOutOfBoundsException ex10) {}
        catch (final InvalidInputException ex11) {}
    }
    
    public final boolean jumpOverUnicodeWhiteSpace() throws InvalidInputException {
        this.wasAcr = false;
        this.getNextUnicodeChar();
        return CharOperation.isWhitespace(this.currentCharacter);
    }
    
    final char[] optimizedCurrentTokenSource1() {
        final char charOne = this.source[this.startPosition];
        switch (charOne) {
            case 'a': {
                return Scanner.charArray_a;
            }
            case 'b': {
                return Scanner.charArray_b;
            }
            case 'c': {
                return Scanner.charArray_c;
            }
            case 'd': {
                return Scanner.charArray_d;
            }
            case 'e': {
                return Scanner.charArray_e;
            }
            case 'f': {
                return Scanner.charArray_f;
            }
            case 'g': {
                return Scanner.charArray_g;
            }
            case 'h': {
                return Scanner.charArray_h;
            }
            case 'i': {
                return Scanner.charArray_i;
            }
            case 'j': {
                return Scanner.charArray_j;
            }
            case 'k': {
                return Scanner.charArray_k;
            }
            case 'l': {
                return Scanner.charArray_l;
            }
            case 'm': {
                return Scanner.charArray_m;
            }
            case 'n': {
                return Scanner.charArray_n;
            }
            case 'o': {
                return Scanner.charArray_o;
            }
            case 'p': {
                return Scanner.charArray_p;
            }
            case 'q': {
                return Scanner.charArray_q;
            }
            case 'r': {
                return Scanner.charArray_r;
            }
            case 's': {
                return Scanner.charArray_s;
            }
            case 't': {
                return Scanner.charArray_t;
            }
            case 'u': {
                return Scanner.charArray_u;
            }
            case 'v': {
                return Scanner.charArray_v;
            }
            case 'w': {
                return Scanner.charArray_w;
            }
            case 'x': {
                return Scanner.charArray_x;
            }
            case 'y': {
                return Scanner.charArray_y;
            }
            case 'z': {
                return Scanner.charArray_z;
            }
            default: {
                return new char[] { charOne };
            }
        }
    }
    
    final char[] optimizedCurrentTokenSource2() {
        final char[] src = this.source;
        final int start = this.startPosition;
        final char c0;
        final char c2;
        final int hash = (((c0 = src[start]) << 6) + (c2 = src[start + 1])) % 30;
        final char[][] table = this.charArray_length[0][hash];
        int i = this.newEntry2;
        while (++i < 6) {
            final char[] charArray = table[i];
            if (c0 == charArray[0] && c2 == charArray[1]) {
                return charArray;
            }
        }
        i = -1;
        int max = this.newEntry2;
        while (++i <= max) {
            final char[] charArray2 = table[i];
            if (c0 == charArray2[0] && c2 == charArray2[1]) {
                return charArray2;
            }
        }
        if (++max >= 6) {
            max = 0;
        }
        final char[] r;
        System.arraycopy(src, start, r = new char[2], 0, 2);
        final char[][] array = table;
        final int newEntry2 = max;
        this.newEntry2 = newEntry2;
        return array[newEntry2] = r;
    }
    
    final char[] optimizedCurrentTokenSource3() {
        final char[] src = this.source;
        final int start = this.startPosition;
        final char c1 = src[start + 1];
        final char c2;
        final char c3;
        final int hash = (((c2 = src[start]) << 6) + (c3 = src[start + 2])) % 30;
        final char[][] table = this.charArray_length[1][hash];
        int i = this.newEntry3;
        while (++i < 6) {
            final char[] charArray = table[i];
            if (c2 == charArray[0] && c1 == charArray[1] && c3 == charArray[2]) {
                return charArray;
            }
        }
        i = -1;
        int max = this.newEntry3;
        while (++i <= max) {
            final char[] charArray2 = table[i];
            if (c2 == charArray2[0] && c1 == charArray2[1] && c3 == charArray2[2]) {
                return charArray2;
            }
        }
        if (++max >= 6) {
            max = 0;
        }
        final char[] r;
        System.arraycopy(src, start, r = new char[3], 0, 3);
        final char[][] array = table;
        final int newEntry3 = max;
        this.newEntry3 = newEntry3;
        return array[newEntry3] = r;
    }
    
    final char[] optimizedCurrentTokenSource4() {
        final char[] src = this.source;
        final int start = this.startPosition;
        final char c1 = src[start + 1];
        final char c2 = src[start + 3];
        final char c3;
        final char c4;
        final int hash = (((c3 = src[start]) << 6) + (c4 = src[start + 2])) % 30;
        final char[][] table = this.charArray_length[2][hash];
        int i = this.newEntry4;
        while (++i < 6) {
            final char[] charArray = table[i];
            if (c3 == charArray[0] && c1 == charArray[1] && c4 == charArray[2] && c2 == charArray[3]) {
                return charArray;
            }
        }
        i = -1;
        int max = this.newEntry4;
        while (++i <= max) {
            final char[] charArray2 = table[i];
            if (c3 == charArray2[0] && c1 == charArray2[1] && c4 == charArray2[2] && c2 == charArray2[3]) {
                return charArray2;
            }
        }
        if (++max >= 6) {
            max = 0;
        }
        final char[] r;
        System.arraycopy(src, start, r = new char[4], 0, 4);
        final char[][] array = table;
        final int newEntry4 = max;
        this.newEntry4 = newEntry4;
        return array[newEntry4] = r;
    }
    
    final char[] optimizedCurrentTokenSource5() {
        final char[] src = this.source;
        final int start = this.startPosition;
        final char c1 = src[start + 1];
        final char c2 = src[start + 3];
        final char c3;
        final char c4;
        final char c5;
        final int hash = (((c3 = src[start]) << 12) + ((c4 = src[start + 2]) << 6) + (c5 = src[start + 4])) % 30;
        final char[][] table = this.charArray_length[3][hash];
        int i = this.newEntry5;
        while (++i < 6) {
            final char[] charArray = table[i];
            if (c3 == charArray[0] && c1 == charArray[1] && c4 == charArray[2] && c2 == charArray[3] && c5 == charArray[4]) {
                return charArray;
            }
        }
        i = -1;
        int max = this.newEntry5;
        while (++i <= max) {
            final char[] charArray2 = table[i];
            if (c3 == charArray2[0] && c1 == charArray2[1] && c4 == charArray2[2] && c2 == charArray2[3] && c5 == charArray2[4]) {
                return charArray2;
            }
        }
        if (++max >= 6) {
            max = 0;
        }
        final char[] r;
        System.arraycopy(src, start, r = new char[5], 0, 5);
        final char[][] array = table;
        final int newEntry5 = max;
        this.newEntry5 = newEntry5;
        return array[newEntry5] = r;
    }
    
    final char[] optimizedCurrentTokenSource6() {
        final char[] src = this.source;
        final int start = this.startPosition;
        final char c1 = src[start + 1];
        final char c2 = src[start + 3];
        final char c3 = src[start + 5];
        final char c4;
        final char c5;
        final char c6;
        final int hash = (((c4 = src[start]) << 12) + ((c5 = src[start + 2]) << 6) + (c6 = src[start + 4])) % 30;
        final char[][] table = this.charArray_length[4][hash];
        int i = this.newEntry6;
        while (++i < 6) {
            final char[] charArray = table[i];
            if (c4 == charArray[0] && c1 == charArray[1] && c5 == charArray[2] && c2 == charArray[3] && c6 == charArray[4] && c3 == charArray[5]) {
                return charArray;
            }
        }
        i = -1;
        int max = this.newEntry6;
        while (++i <= max) {
            final char[] charArray2 = table[i];
            if (c4 == charArray2[0] && c1 == charArray2[1] && c5 == charArray2[2] && c2 == charArray2[3] && c6 == charArray2[4] && c3 == charArray2[5]) {
                return charArray2;
            }
        }
        if (++max >= 6) {
            max = 0;
        }
        final char[] r;
        System.arraycopy(src, start, r = new char[6], 0, 6);
        final char[][] array = table;
        final int newEntry6 = max;
        this.newEntry6 = newEntry6;
        return array[newEntry6] = r;
    }
    
    private void parseTags() {
        int position = 0;
        final int currentStartPosition = this.startPosition;
        final int currentLinePtr = this.linePtr;
        if (currentLinePtr >= 0) {
            position = this.lineEnds[currentLinePtr] + 1;
        }
        while (ScannerHelper.isWhitespace(this.source[position])) {
            ++position;
        }
        if (currentStartPosition == position) {
            return;
        }
        char[] s = null;
        int sourceEnd = this.currentPosition;
        int sourceStart = currentStartPosition;
        int sourceDelta = 0;
        if (this.withoutUnicodePtr != 0) {
            System.arraycopy(this.withoutUnicodeBuffer, 1, s = new char[this.withoutUnicodePtr], 0, this.withoutUnicodePtr);
            sourceEnd = this.withoutUnicodePtr;
            sourceStart = 1;
            sourceDelta = currentStartPosition;
        }
        else {
            s = this.source;
        }
        int pos;
        if (this.checkNonExternalizedStringLiterals && (pos = CharOperation.indexOf(Scanner.TAG_PREFIX, s, true, sourceStart, sourceEnd)) != -1) {
            if (this.nlsTags == null) {
                this.nlsTags = new NLSTag[10];
                this.nlsTagsPtr = 0;
            }
            while (pos != -1) {
                final int start = pos + Scanner.TAG_PREFIX_LENGTH;
                int end = CharOperation.indexOf('$', s, start, sourceEnd);
                if (end != -1) {
                    NLSTag currentTag = null;
                    final int currentLine = currentLinePtr + 1;
                    try {
                        currentTag = new NLSTag(pos + sourceDelta, end + sourceDelta, currentLine, this.extractInt(s, start, end));
                    }
                    catch (final NumberFormatException ex) {
                        currentTag = new NLSTag(pos + sourceDelta, end + sourceDelta, currentLine, -1);
                    }
                    if (this.nlsTagsPtr == this.nlsTags.length) {
                        System.arraycopy(this.nlsTags, 0, this.nlsTags = new NLSTag[this.nlsTagsPtr + 10], 0, this.nlsTagsPtr);
                    }
                    this.nlsTags[this.nlsTagsPtr++] = currentTag;
                }
                else {
                    end = start;
                }
                pos = CharOperation.indexOf(Scanner.TAG_PREFIX, s, true, end, sourceEnd);
            }
        }
        if (this.checkUninternedIdentityComparison && (pos = CharOperation.indexOf(Scanner.IDENTITY_COMPARISON_TAG, s, true, sourceStart, sourceEnd)) != -1) {
            if (this.validIdentityComparisonLines == null) {
                this.validIdentityComparisonLines = new boolean[0];
            }
            final int currentLine2 = currentLinePtr + 1;
            final int length = this.validIdentityComparisonLines.length;
            System.arraycopy(this.validIdentityComparisonLines, 0, this.validIdentityComparisonLines = new boolean[currentLine2 + 1], 0, length);
            this.validIdentityComparisonLines[currentLine2] = true;
        }
    }
    
    private int extractInt(final char[] array, final int start, final int end) {
        int value = 0;
        for (int i = start; i < end; ++i) {
            final char currentChar = array[i];
            int digit = 0;
            switch (currentChar) {
                case '0': {
                    digit = 0;
                    break;
                }
                case '1': {
                    digit = 1;
                    break;
                }
                case '2': {
                    digit = 2;
                    break;
                }
                case '3': {
                    digit = 3;
                    break;
                }
                case '4': {
                    digit = 4;
                    break;
                }
                case '5': {
                    digit = 5;
                    break;
                }
                case '6': {
                    digit = 6;
                    break;
                }
                case '7': {
                    digit = 7;
                    break;
                }
                case '8': {
                    digit = 8;
                    break;
                }
                case '9': {
                    digit = 9;
                    break;
                }
                default: {
                    throw new NumberFormatException();
                }
            }
            value *= 10;
            if (digit < 0) {
                throw new NumberFormatException();
            }
            value += digit;
        }
        return value;
    }
    
    public final void pushLineSeparator() {
        if (this.currentCharacter == '\r') {
            final int separatorPos = this.currentPosition - 1;
            if (this.linePtr >= 0 && this.lineEnds[this.linePtr] >= separatorPos) {
                return;
            }
            final int length = this.lineEnds.length;
            if (++this.linePtr >= length) {
                System.arraycopy(this.lineEnds, 0, this.lineEnds = new int[length + 250], 0, length);
            }
            this.lineEnds[this.linePtr] = separatorPos;
            try {
                if (this.source[this.currentPosition] == '\n') {
                    this.lineEnds[this.linePtr] = this.currentPosition;
                    ++this.currentPosition;
                    this.wasAcr = false;
                }
                else {
                    this.wasAcr = true;
                }
            }
            catch (final IndexOutOfBoundsException ex) {
                this.wasAcr = true;
            }
        }
        else if (this.currentCharacter == '\n') {
            if (this.wasAcr && this.lineEnds[this.linePtr] == this.currentPosition - 2) {
                this.lineEnds[this.linePtr] = this.currentPosition - 1;
            }
            else {
                final int separatorPos = this.currentPosition - 1;
                if (this.linePtr >= 0 && this.lineEnds[this.linePtr] >= separatorPos) {
                    return;
                }
                final int length = this.lineEnds.length;
                if (++this.linePtr >= length) {
                    System.arraycopy(this.lineEnds, 0, this.lineEnds = new int[length + 250], 0, length);
                }
                this.lineEnds[this.linePtr] = separatorPos;
            }
            this.wasAcr = false;
        }
    }
    
    public final void pushUnicodeLineSeparator() {
        if (this.currentCharacter == '\r') {
            if (this.source[this.currentPosition] == '\n') {
                this.wasAcr = false;
            }
            else {
                this.wasAcr = true;
            }
        }
        else if (this.currentCharacter == '\n') {
            this.wasAcr = false;
        }
    }
    
    public void recordComment(final int token) {
        int commentStart = this.startPosition;
        int stopPosition = this.currentPosition;
        switch (token) {
            case 1001: {
                commentStart = -this.startPosition;
                stopPosition = -this.lastCommentLinePosition;
                break;
            }
            case 1002: {
                stopPosition = -this.currentPosition;
                break;
            }
        }
        final int length = this.commentStops.length;
        if (++this.commentPtr >= length) {
            final int newLength = length + 300;
            System.arraycopy(this.commentStops, 0, this.commentStops = new int[newLength], 0, length);
            System.arraycopy(this.commentStarts, 0, this.commentStarts = new int[newLength], 0, length);
            System.arraycopy(this.commentTagStarts, 0, this.commentTagStarts = new int[newLength], 0, length);
        }
        this.commentStops[this.commentPtr] = stopPosition;
        this.commentStarts[this.commentPtr] = commentStart;
    }
    
    public void resetTo(final int begin, final int end) {
        this.diet = false;
        this.currentPosition = begin;
        this.startPosition = begin;
        this.initialPosition = begin;
        if (this.source != null && this.source.length < end) {
            this.eofPosition = this.source.length;
        }
        else {
            this.eofPosition = ((end < Integer.MAX_VALUE) ? (end + 1) : end);
        }
        this.commentPtr = -1;
        this.foundTaskCount = 0;
        final int[] lookBack = this.lookBack;
        final int n = 0;
        final int[] lookBack2 = this.lookBack;
        final int n2 = 1;
        final int nextToken = 0;
        this.nextToken = nextToken;
        lookBack[n] = (lookBack2[n2] = nextToken);
        this.consumingEllipsisAnnotations = false;
    }
    
    protected final void scanEscapeCharacter() throws InvalidInputException {
        switch (this.currentCharacter) {
            case 'b': {
                this.currentCharacter = '\b';
                break;
            }
            case 't': {
                this.currentCharacter = '\t';
                break;
            }
            case 'n': {
                this.currentCharacter = '\n';
                break;
            }
            case 'f': {
                this.currentCharacter = '\f';
                break;
            }
            case 'r': {
                this.currentCharacter = '\r';
                break;
            }
            case '\"': {
                this.currentCharacter = '\"';
                break;
            }
            case '\'': {
                this.currentCharacter = '\'';
                break;
            }
            case '\\': {
                this.currentCharacter = '\\';
                break;
            }
            default: {
                int number = ScannerHelper.getHexadecimalValue(this.currentCharacter);
                if (number < 0 || number > 7) {
                    throw new InvalidInputException("Invalid_Escape");
                }
                final boolean zeroToThreeNot = number > 3;
                final char c = this.source[this.currentPosition++];
                this.currentCharacter = c;
                if (ScannerHelper.isDigit(c)) {
                    int digit = ScannerHelper.getHexadecimalValue(this.currentCharacter);
                    if (digit >= 0 && digit <= 7) {
                        number = number * 8 + digit;
                        final char c2 = this.source[this.currentPosition++];
                        this.currentCharacter = c2;
                        if (ScannerHelper.isDigit(c2)) {
                            if (zeroToThreeNot) {
                                --this.currentPosition;
                            }
                            else {
                                digit = ScannerHelper.getHexadecimalValue(this.currentCharacter);
                                if (digit >= 0 && digit <= 7) {
                                    number = number * 8 + digit;
                                }
                                else {
                                    --this.currentPosition;
                                }
                            }
                        }
                        else {
                            --this.currentPosition;
                        }
                    }
                    else {
                        --this.currentPosition;
                    }
                }
                else {
                    --this.currentPosition;
                }
                if (number > 255) {
                    throw new InvalidInputException("Invalid_Escape");
                }
                this.currentCharacter = (char)number;
                break;
            }
        }
    }
    
    public int scanIdentifierOrKeywordWithBoundCheck() {
        this.useAssertAsAnIndentifier = false;
        this.useEnumAsAnIndentifier = false;
        final char[] src = this.source;
        final int srcLength = this.eofPosition;
        while (true) {
            int pos;
            while ((pos = this.currentPosition) < srcLength) {
                final char c = src[pos];
                if (c < '\u0080') {
                    if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x3C) != 0x0) {
                        if (this.withoutUnicodePtr != 0) {
                            this.currentCharacter = c;
                            this.unicodeStore();
                        }
                        ++this.currentPosition;
                        continue;
                    }
                    if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x102) != 0x0) {
                        this.currentCharacter = c;
                    }
                    else {
                        while (this.getNextCharAsJavaIdentifierPartWithBoundCheck()) {}
                    }
                }
                else {
                    while (this.getNextCharAsJavaIdentifierPartWithBoundCheck()) {}
                }
                int length;
                char[] data;
                int index;
                if (this.withoutUnicodePtr == 0) {
                    if ((length = this.currentPosition - this.startPosition) == 1) {
                        return 22;
                    }
                    data = this.source;
                    index = this.startPosition;
                }
                else {
                    if ((length = this.withoutUnicodePtr) == 1) {
                        return 22;
                    }
                    data = this.withoutUnicodeBuffer;
                    index = 1;
                }
                return this.internalScanIdentifierOrKeyword(index, length, data);
            }
            continue;
        }
    }
    
    public int scanIdentifierOrKeyword() {
        this.useAssertAsAnIndentifier = false;
        this.useEnumAsAnIndentifier = false;
        final char[] src = this.source;
        final int srcLength = this.eofPosition;
        while (true) {
            int pos;
            while ((pos = this.currentPosition) < srcLength) {
                final char c = src[pos];
                if (c < '\u0080') {
                    if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x3C) != 0x0) {
                        if (this.withoutUnicodePtr != 0) {
                            this.currentCharacter = c;
                            this.unicodeStore();
                        }
                        ++this.currentPosition;
                        continue;
                    }
                    if ((ScannerHelper.OBVIOUS_IDENT_CHAR_NATURES[c] & 0x102) != 0x0) {
                        this.currentCharacter = c;
                    }
                    else {
                        while (this.getNextCharAsJavaIdentifierPart()) {}
                    }
                }
                else {
                    while (this.getNextCharAsJavaIdentifierPart()) {}
                }
                int length;
                char[] data;
                int index;
                if (this.withoutUnicodePtr == 0) {
                    if ((length = this.currentPosition - this.startPosition) == 1) {
                        return 22;
                    }
                    data = this.source;
                    index = this.startPosition;
                }
                else {
                    if ((length = this.withoutUnicodePtr) == 1) {
                        return 22;
                    }
                    data = this.withoutUnicodeBuffer;
                    index = 1;
                }
                return this.internalScanIdentifierOrKeyword(index, length, data);
            }
            continue;
        }
    }
    
    private int internalScanIdentifierOrKeyword(int index, final int length, final char[] data) {
        switch (data[index]) {
            case 'a': {
                switch (length) {
                    case 8: {
                        if (data[++index] == 'b' && data[++index] == 's' && data[++index] == 't' && data[++index] == 'r' && data[++index] == 'a' && data[++index] == 'c' && data[++index] == 't') {
                            return 51;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] != 's' || data[++index] != 's' || data[++index] != 'e' || data[++index] != 'r' || data[++index] != 't') {
                            return 22;
                        }
                        if (this.sourceLevel >= 3145728L) {
                            this.containsAssertKeyword = true;
                            return 72;
                        }
                        this.useAssertAsAnIndentifier = true;
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'b': {
                switch (length) {
                    case 4: {
                        if (data[++index] == 'y' && data[++index] == 't' && data[++index] == 'e') {
                            return 98;
                        }
                        return 22;
                    }
                    case 5: {
                        if (data[++index] == 'r' && data[++index] == 'e' && data[++index] == 'a' && data[++index] == 'k') {
                            return 73;
                        }
                        return 22;
                    }
                    case 7: {
                        if (data[++index] == 'o' && data[++index] == 'o' && data[++index] == 'l' && data[++index] == 'e' && data[++index] == 'a' && data[++index] == 'n') {
                            return 97;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'c': {
                switch (length) {
                    case 4: {
                        if (data[++index] == 'a') {
                            if (data[++index] == 's' && data[++index] == 'e') {
                                return 99;
                            }
                            return 22;
                        }
                        else {
                            if (data[index] == 'h' && data[++index] == 'a' && data[++index] == 'r') {
                                return 101;
                            }
                            return 22;
                        }
                        break;
                    }
                    case 5: {
                        if (data[++index] == 'a') {
                            if (data[++index] == 't' && data[++index] == 'c' && data[++index] == 'h') {
                                return 100;
                            }
                            return 22;
                        }
                        else if (data[index] == 'l') {
                            if (data[++index] == 'a' && data[++index] == 's' && data[++index] == 's') {
                                return 67;
                            }
                            return 22;
                        }
                        else {
                            if (data[index] == 'o' && data[++index] == 'n' && data[++index] == 's' && data[++index] == 't') {
                                return 116;
                            }
                            return 22;
                        }
                        break;
                    }
                    case 8: {
                        if (data[++index] == 'o' && data[++index] == 'n' && data[++index] == 't' && data[++index] == 'i' && data[++index] == 'n' && data[++index] == 'u' && data[++index] == 'e') {
                            return 74;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'd': {
                switch (length) {
                    case 2: {
                        if (data[++index] == 'o') {
                            return 76;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 'o' && data[++index] == 'u' && data[++index] == 'b' && data[++index] == 'l' && data[++index] == 'e') {
                            return 102;
                        }
                        return 22;
                    }
                    case 7: {
                        if (data[++index] == 'e' && data[++index] == 'f' && data[++index] == 'a' && data[++index] == 'u' && data[++index] == 'l' && data[++index] == 't') {
                            return 75;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'e': {
                switch (length) {
                    case 4: {
                        if (data[++index] == 'l') {
                            if (data[++index] == 's' && data[++index] == 'e') {
                                return 111;
                            }
                            return 22;
                        }
                        else {
                            if (data[index] != 'n' || data[++index] != 'u' || data[++index] != 'm') {
                                return 22;
                            }
                            if (this.sourceLevel >= 3211264L) {
                                return 69;
                            }
                            this.useEnumAsAnIndentifier = true;
                            return 22;
                        }
                        break;
                    }
                    case 7: {
                        if (data[++index] == 'x' && data[++index] == 't' && data[++index] == 'e' && data[++index] == 'n' && data[++index] == 'd' && data[++index] == 's') {
                            return 96;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'f': {
                switch (length) {
                    case 3: {
                        if (data[++index] == 'o' && data[++index] == 'r') {
                            return 77;
                        }
                        return 22;
                    }
                    case 5: {
                        if (data[++index] == 'i') {
                            if (data[++index] == 'n' && data[++index] == 'a' && data[++index] == 'l') {
                                return 52;
                            }
                            return 22;
                        }
                        else if (data[index] == 'l') {
                            if (data[++index] == 'o' && data[++index] == 'a' && data[++index] == 't') {
                                return 103;
                            }
                            return 22;
                        }
                        else {
                            if (data[index] == 'a' && data[++index] == 'l' && data[++index] == 's' && data[++index] == 'e') {
                                return 38;
                            }
                            return 22;
                        }
                        break;
                    }
                    case 7: {
                        if (data[++index] == 'i' && data[++index] == 'n' && data[++index] == 'a' && data[++index] == 'l' && data[++index] == 'l' && data[++index] == 'y') {
                            return 109;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'g': {
                if (length == 4 && data[++index] == 'o' && data[++index] == 't' && data[++index] == 'o') {
                    return 117;
                }
                return 22;
            }
            case 'i': {
                switch (length) {
                    case 2: {
                        if (data[++index] == 'f') {
                            return 78;
                        }
                        return 22;
                    }
                    case 3: {
                        if (data[++index] == 'n' && data[++index] == 't') {
                            return 105;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 'm' && data[++index] == 'p' && data[++index] == 'o' && data[++index] == 'r' && data[++index] == 't') {
                            return 104;
                        }
                        return 22;
                    }
                    case 9: {
                        if (data[++index] == 'n' && data[++index] == 't' && data[++index] == 'e' && data[++index] == 'r' && data[++index] == 'f' && data[++index] == 'a' && data[++index] == 'c' && data[++index] == 'e') {
                            return 68;
                        }
                        return 22;
                    }
                    case 10: {
                        if (data[++index] == 'm') {
                            if (data[++index] == 'p' && data[++index] == 'l' && data[++index] == 'e' && data[++index] == 'm' && data[++index] == 'e' && data[++index] == 'n' && data[++index] == 't' && data[++index] == 's') {
                                return 114;
                            }
                            return 22;
                        }
                        else {
                            if (data[index] == 'n' && data[++index] == 's' && data[++index] == 't' && data[++index] == 'a' && data[++index] == 'n' && data[++index] == 'c' && data[++index] == 'e' && data[++index] == 'o' && data[++index] == 'f') {
                                return 17;
                            }
                            return 22;
                        }
                        break;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'l': {
                if (length == 4 && data[++index] == 'o' && data[++index] == 'n' && data[++index] == 'g') {
                    return 106;
                }
                return 22;
            }
            case 'n': {
                switch (length) {
                    case 3: {
                        if (data[++index] == 'e' && data[++index] == 'w') {
                            return 36;
                        }
                        return 22;
                    }
                    case 4: {
                        if (data[++index] == 'u' && data[++index] == 'l' && data[++index] == 'l') {
                            return 39;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 'a' && data[++index] == 't' && data[++index] == 'i' && data[++index] == 'v' && data[++index] == 'e') {
                            return 53;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'p': {
                switch (length) {
                    case 6: {
                        if (data[++index] == 'u' && data[++index] == 'b' && data[++index] == 'l' && data[++index] == 'i' && data[++index] == 'c') {
                            return 56;
                        }
                        return 22;
                    }
                    case 7: {
                        if (data[++index] == 'a') {
                            if (data[++index] == 'c' && data[++index] == 'k' && data[++index] == 'a' && data[++index] == 'g' && data[++index] == 'e') {
                                return 95;
                            }
                            return 22;
                        }
                        else {
                            if (data[index] == 'r' && data[++index] == 'i' && data[++index] == 'v' && data[++index] == 'a' && data[++index] == 't' && data[++index] == 'e') {
                                return 54;
                            }
                            return 22;
                        }
                        break;
                    }
                    case 9: {
                        if (data[++index] == 'r' && data[++index] == 'o' && data[++index] == 't' && data[++index] == 'e' && data[++index] == 'c' && data[++index] == 't' && data[++index] == 'e' && data[++index] == 'd') {
                            return 55;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'r': {
                if (length == 6 && data[++index] == 'e' && data[++index] == 't' && data[++index] == 'u' && data[++index] == 'r' && data[++index] == 'n') {
                    return 79;
                }
                return 22;
            }
            case 's': {
                switch (length) {
                    case 5: {
                        if (data[++index] == 'h') {
                            if (data[++index] == 'o' && data[++index] == 'r' && data[++index] == 't') {
                                return 107;
                            }
                            return 22;
                        }
                        else {
                            if (data[index] == 'u' && data[++index] == 'p' && data[++index] == 'e' && data[++index] == 'r') {
                                return 34;
                            }
                            return 22;
                        }
                        break;
                    }
                    case 6: {
                        if (data[++index] == 't') {
                            if (data[++index] == 'a' && data[++index] == 't' && data[++index] == 'i' && data[++index] == 'c') {
                                return 40;
                            }
                            return 22;
                        }
                        else {
                            if (data[index] == 'w' && data[++index] == 'i' && data[++index] == 't' && data[++index] == 'c' && data[++index] == 'h') {
                                return 80;
                            }
                            return 22;
                        }
                        break;
                    }
                    case 8: {
                        if (data[++index] == 't' && data[++index] == 'r' && data[++index] == 'i' && data[++index] == 'c' && data[++index] == 't' && data[++index] == 'f' && data[++index] == 'p') {
                            return 57;
                        }
                        return 22;
                    }
                    case 12: {
                        if (data[++index] == 'y' && data[++index] == 'n' && data[++index] == 'c' && data[++index] == 'h' && data[++index] == 'r' && data[++index] == 'o' && data[++index] == 'n' && data[++index] == 'i' && data[++index] == 'z' && data[++index] == 'e' && data[++index] == 'd') {
                            return 41;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 't': {
                switch (length) {
                    case 3: {
                        if (data[++index] == 'r' && data[++index] == 'y') {
                            return 82;
                        }
                        return 22;
                    }
                    case 4: {
                        if (data[++index] == 'h') {
                            if (data[++index] == 'i' && data[++index] == 's') {
                                return 35;
                            }
                            return 22;
                        }
                        else {
                            if (data[index] == 'r' && data[++index] == 'u' && data[++index] == 'e') {
                                return 42;
                            }
                            return 22;
                        }
                        break;
                    }
                    case 5: {
                        if (data[++index] == 'h' && data[++index] == 'r' && data[++index] == 'o' && data[++index] == 'w') {
                            return 81;
                        }
                        return 22;
                    }
                    case 6: {
                        if (data[++index] == 'h' && data[++index] == 'r' && data[++index] == 'o' && data[++index] == 'w' && data[++index] == 's') {
                            return 112;
                        }
                        return 22;
                    }
                    case 9: {
                        if (data[++index] == 'r' && data[++index] == 'a' && data[++index] == 'n' && data[++index] == 's' && data[++index] == 'i' && data[++index] == 'e' && data[++index] == 'n' && data[++index] == 't') {
                            return 58;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'v': {
                switch (length) {
                    case 4: {
                        if (data[++index] == 'o' && data[++index] == 'i' && data[++index] == 'd') {
                            return 108;
                        }
                        return 22;
                    }
                    case 8: {
                        if (data[++index] == 'o' && data[++index] == 'l' && data[++index] == 'a' && data[++index] == 't' && data[++index] == 'i' && data[++index] == 'l' && data[++index] == 'e') {
                            return 59;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            case 'w': {
                switch (length) {
                    case 5: {
                        if (data[++index] == 'h' && data[++index] == 'i' && data[++index] == 'l' && data[++index] == 'e') {
                            return 71;
                        }
                        return 22;
                    }
                    default: {
                        return 22;
                    }
                }
                break;
            }
            default: {
                return 22;
            }
        }
    }
    
    public int scanNumber(final boolean dotPrefix) throws InvalidInputException {
        boolean floating = dotPrefix;
        if (!dotPrefix && this.currentCharacter == '0') {
            if (this.getNextChar('x', 'X') >= 0) {
                int start = this.currentPosition;
                this.consumeDigits(16, true);
                int end = this.currentPosition;
                if (this.getNextChar('l', 'L') >= 0) {
                    if (end == start) {
                        throw new InvalidInputException("Invalid_Hexa_Literal");
                    }
                    return 44;
                }
                else if (this.getNextChar('.')) {
                    final boolean hasNoDigitsBeforeDot = end == start;
                    start = this.currentPosition;
                    this.consumeDigits(16, true);
                    end = this.currentPosition;
                    if (hasNoDigitsBeforeDot && end == start) {
                        if (this.sourceLevel < 3211264L) {
                            throw new InvalidInputException("Illegal_Hexa_Literal");
                        }
                        throw new InvalidInputException("Invalid_Hexa_Literal");
                    }
                    else if (this.getNextChar('p', 'P') >= 0) {
                        this.unicodeAsBackSlash = false;
                        final char currentCharacter = this.source[this.currentPosition++];
                        this.currentCharacter = currentCharacter;
                        if (currentCharacter == '\\' && this.source[this.currentPosition] == 'u') {
                            this.getNextUnicodeChar();
                        }
                        else if (this.withoutUnicodePtr != 0) {
                            this.unicodeStore();
                        }
                        if (this.currentCharacter == '-' || this.currentCharacter == '+') {
                            this.unicodeAsBackSlash = false;
                            if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                this.getNextUnicodeChar();
                            }
                            else if (this.withoutUnicodePtr != 0) {
                                this.unicodeStore();
                            }
                        }
                        if (!ScannerHelper.isDigit(this.currentCharacter)) {
                            if (this.sourceLevel < 3211264L) {
                                throw new InvalidInputException("Illegal_Hexa_Literal");
                            }
                            if (this.currentCharacter == '_') {
                                this.consumeDigits(10);
                                throw new InvalidInputException("Invalid_Underscore");
                            }
                            throw new InvalidInputException("Invalid_Hexa_Literal");
                        }
                        else {
                            this.consumeDigits(10);
                            if (this.getNextChar('f', 'F') >= 0) {
                                if (this.sourceLevel < 3211264L) {
                                    throw new InvalidInputException("Illegal_Hexa_Literal");
                                }
                                return 45;
                            }
                            else if (this.getNextChar('d', 'D') >= 0) {
                                if (this.sourceLevel < 3211264L) {
                                    throw new InvalidInputException("Illegal_Hexa_Literal");
                                }
                                return 46;
                            }
                            else if (this.getNextChar('l', 'L') >= 0) {
                                if (this.sourceLevel < 3211264L) {
                                    throw new InvalidInputException("Illegal_Hexa_Literal");
                                }
                                throw new InvalidInputException("Invalid_Hexa_Literal");
                            }
                            else {
                                if (this.sourceLevel < 3211264L) {
                                    throw new InvalidInputException("Illegal_Hexa_Literal");
                                }
                                return 46;
                            }
                        }
                    }
                    else {
                        if (this.sourceLevel < 3211264L) {
                            throw new InvalidInputException("Illegal_Hexa_Literal");
                        }
                        throw new InvalidInputException("Invalid_Hexa_Literal");
                    }
                }
                else if (this.getNextChar('p', 'P') >= 0) {
                    if (end == start) {
                        if (this.sourceLevel < 3211264L) {
                            throw new InvalidInputException("Illegal_Hexa_Literal");
                        }
                        throw new InvalidInputException("Invalid_Hexa_Literal");
                    }
                    else {
                        this.unicodeAsBackSlash = false;
                        final char currentCharacter2 = this.source[this.currentPosition++];
                        this.currentCharacter = currentCharacter2;
                        if (currentCharacter2 == '\\' && this.source[this.currentPosition] == 'u') {
                            this.getNextUnicodeChar();
                        }
                        else if (this.withoutUnicodePtr != 0) {
                            this.unicodeStore();
                        }
                        if (this.currentCharacter == '-' || this.currentCharacter == '+') {
                            this.unicodeAsBackSlash = false;
                            if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                                this.getNextUnicodeChar();
                            }
                            else if (this.withoutUnicodePtr != 0) {
                                this.unicodeStore();
                            }
                        }
                        if (!ScannerHelper.isDigit(this.currentCharacter)) {
                            if (this.sourceLevel < 3211264L) {
                                throw new InvalidInputException("Illegal_Hexa_Literal");
                            }
                            if (this.currentCharacter == '_') {
                                this.consumeDigits(10);
                                throw new InvalidInputException("Invalid_Underscore");
                            }
                            throw new InvalidInputException("Invalid_Float_Literal");
                        }
                        else {
                            this.consumeDigits(10);
                            if (this.getNextChar('f', 'F') >= 0) {
                                if (this.sourceLevel < 3211264L) {
                                    throw new InvalidInputException("Illegal_Hexa_Literal");
                                }
                                return 45;
                            }
                            else if (this.getNextChar('d', 'D') >= 0) {
                                if (this.sourceLevel < 3211264L) {
                                    throw new InvalidInputException("Illegal_Hexa_Literal");
                                }
                                return 46;
                            }
                            else if (this.getNextChar('l', 'L') >= 0) {
                                if (this.sourceLevel < 3211264L) {
                                    throw new InvalidInputException("Illegal_Hexa_Literal");
                                }
                                throw new InvalidInputException("Invalid_Hexa_Literal");
                            }
                            else {
                                if (this.sourceLevel < 3211264L) {
                                    throw new InvalidInputException("Illegal_Hexa_Literal");
                                }
                                return 46;
                            }
                        }
                    }
                }
                else {
                    if (end == start) {
                        throw new InvalidInputException("Invalid_Hexa_Literal");
                    }
                    return 43;
                }
            }
            else if (this.getNextChar('b', 'B') >= 0) {
                final int start = this.currentPosition;
                this.consumeDigits(2, true);
                final int end = this.currentPosition;
                if (end == start) {
                    if (this.sourceLevel < 3342336L) {
                        throw new InvalidInputException("Binary_Literal_Not_Below_17");
                    }
                    throw new InvalidInputException("Invalid_Binary_Literal");
                }
                else if (this.getNextChar('l', 'L') >= 0) {
                    if (this.sourceLevel < 3342336L) {
                        throw new InvalidInputException("Binary_Literal_Not_Below_17");
                    }
                    return 44;
                }
                else {
                    if (this.sourceLevel < 3342336L) {
                        throw new InvalidInputException("Binary_Literal_Not_Below_17");
                    }
                    return 43;
                }
            }
            else if (this.getNextCharAsDigit()) {
                this.consumeDigits(10);
                if (this.getNextChar('l', 'L') >= 0) {
                    return 44;
                }
                if (this.getNextChar('f', 'F') >= 0) {
                    return 45;
                }
                if (this.getNextChar('d', 'D') >= 0) {
                    return 46;
                }
                boolean isInteger = true;
                if (this.getNextChar('.')) {
                    isInteger = false;
                    this.consumeDigits(10);
                }
                if (this.getNextChar('e', 'E') >= 0) {
                    isInteger = false;
                    this.unicodeAsBackSlash = false;
                    final char currentCharacter3 = this.source[this.currentPosition++];
                    this.currentCharacter = currentCharacter3;
                    if (currentCharacter3 == '\\' && this.source[this.currentPosition] == 'u') {
                        this.getNextUnicodeChar();
                    }
                    else if (this.withoutUnicodePtr != 0) {
                        this.unicodeStore();
                    }
                    if (this.currentCharacter == '-' || this.currentCharacter == '+') {
                        this.unicodeAsBackSlash = false;
                        if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                            this.getNextUnicodeChar();
                        }
                        else if (this.withoutUnicodePtr != 0) {
                            this.unicodeStore();
                        }
                    }
                    if (!ScannerHelper.isDigit(this.currentCharacter)) {
                        if (this.currentCharacter == '_') {
                            this.consumeDigits(10);
                            throw new InvalidInputException("Invalid_Underscore");
                        }
                        throw new InvalidInputException("Invalid_Float_Literal");
                    }
                    else {
                        this.consumeDigits(10);
                    }
                }
                if (this.getNextChar('f', 'F') >= 0) {
                    return 45;
                }
                if (this.getNextChar('d', 'D') >= 0 || !isInteger) {
                    return 46;
                }
                return 43;
            }
        }
        this.consumeDigits(10);
        if (!dotPrefix && this.getNextChar('l', 'L') >= 0) {
            return 44;
        }
        if (!dotPrefix && this.getNextChar('.')) {
            this.consumeDigits(10, true);
            floating = true;
        }
        if (this.getNextChar('e', 'E') >= 0) {
            floating = true;
            this.unicodeAsBackSlash = false;
            final char currentCharacter4 = this.source[this.currentPosition++];
            this.currentCharacter = currentCharacter4;
            if (currentCharacter4 == '\\' && this.source[this.currentPosition] == 'u') {
                this.getNextUnicodeChar();
            }
            else if (this.withoutUnicodePtr != 0) {
                this.unicodeStore();
            }
            if (this.currentCharacter == '-' || this.currentCharacter == '+') {
                this.unicodeAsBackSlash = false;
                if ((this.currentCharacter = this.source[this.currentPosition++]) == '\\' && this.source[this.currentPosition] == 'u') {
                    this.getNextUnicodeChar();
                }
                else if (this.withoutUnicodePtr != 0) {
                    this.unicodeStore();
                }
            }
            if (!ScannerHelper.isDigit(this.currentCharacter)) {
                if (this.currentCharacter == '_') {
                    this.consumeDigits(10);
                    throw new InvalidInputException("Invalid_Underscore");
                }
                throw new InvalidInputException("Invalid_Float_Literal");
            }
            else {
                this.consumeDigits(10);
            }
        }
        if (this.getNextChar('d', 'D') >= 0) {
            return 46;
        }
        if (this.getNextChar('f', 'F') >= 0) {
            return 45;
        }
        return floating ? 46 : 43;
    }
    
    public final int getLineNumber(final int position) {
        return Util.getLineNumber(position, this.lineEnds, 0, this.linePtr);
    }
    
    public final void setSource(final char[] sourceString) {
        int sourceLength;
        if (sourceString == null) {
            this.source = CharOperation.NO_CHAR;
            sourceLength = 0;
        }
        else {
            this.source = sourceString;
            sourceLength = sourceString.length;
        }
        this.startPosition = -1;
        this.eofPosition = sourceLength;
        final int n = 0;
        this.currentPosition = n;
        this.initialPosition = n;
        this.containsAssertKeyword = false;
        this.linePtr = -1;
    }
    
    public final void setSource(final char[] contents, final CompilationResult compilationResult) {
        if (contents == null) {
            final char[] cuContents = compilationResult.compilationUnit.getContents();
            this.setSource(cuContents);
        }
        else {
            this.setSource(contents);
        }
        final int[] lineSeparatorPositions = compilationResult.lineSeparatorPositions;
        if (lineSeparatorPositions != null) {
            this.lineEnds = lineSeparatorPositions;
            this.linePtr = lineSeparatorPositions.length - 1;
        }
    }
    
    public final void setSource(final CompilationResult compilationResult) {
        this.setSource(null, compilationResult);
    }
    
    @Override
    public String toString() {
        if (this.startPosition == this.eofPosition) {
            return "EOF\n\n" + new String(this.source);
        }
        if (this.currentPosition > this.eofPosition) {
            return "behind the EOF\n\n" + new String(this.source);
        }
        if (this.currentPosition <= 0) {
            return "NOT started!\n\n" + ((this.source != null) ? new String(this.source) : "");
        }
        final StringBuffer buffer = new StringBuffer();
        if (this.startPosition < 1000) {
            buffer.append(this.source, 0, this.startPosition);
        }
        else {
            buffer.append("<source beginning>\n...\n");
            final int line = Util.getLineNumber(this.startPosition - 1000, this.lineEnds, 0, this.linePtr);
            final int lineStart = this.getLineStart(line);
            buffer.append(this.source, lineStart, this.startPosition - lineStart);
        }
        buffer.append("\n===============================\nStarts here -->");
        final int middleLength = this.currentPosition - 1 - this.startPosition + 1;
        if (middleLength > -1) {
            buffer.append(this.source, this.startPosition, middleLength);
        }
        if (this.nextToken != 0) {
            buffer.append("<-- Ends here [in pipeline " + this.toStringAction(this.nextToken) + "]\n===============================\n");
        }
        else {
            buffer.append("<-- Ends here\n===============================\n");
        }
        buffer.append(this.source, this.currentPosition - 1 + 1, this.eofPosition - (this.currentPosition - 1) - 1);
        return buffer.toString();
    }
    
    public String toStringAction(final int act) {
        switch (act) {
            case 22: {
                return "Identifier(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 51: {
                return "abstract";
            }
            case 97: {
                return "boolean";
            }
            case 73: {
                return "break";
            }
            case 98: {
                return "byte";
            }
            case 99: {
                return "case";
            }
            case 100: {
                return "catch";
            }
            case 101: {
                return "char";
            }
            case 67: {
                return "class";
            }
            case 74: {
                return "continue";
            }
            case 75: {
                return "default";
            }
            case 76: {
                return "do";
            }
            case 102: {
                return "double";
            }
            case 111: {
                return "else";
            }
            case 96: {
                return "extends";
            }
            case 38: {
                return "false";
            }
            case 52: {
                return "final";
            }
            case 109: {
                return "finally";
            }
            case 103: {
                return "float";
            }
            case 77: {
                return "for";
            }
            case 78: {
                return "if";
            }
            case 114: {
                return "implements";
            }
            case 104: {
                return "import";
            }
            case 17: {
                return "instanceof";
            }
            case 105: {
                return "int";
            }
            case 68: {
                return "interface";
            }
            case 106: {
                return "long";
            }
            case 53: {
                return "native";
            }
            case 36: {
                return "new";
            }
            case 39: {
                return "null";
            }
            case 95: {
                return "package";
            }
            case 54: {
                return "private";
            }
            case 55: {
                return "protected";
            }
            case 56: {
                return "public";
            }
            case 79: {
                return "return";
            }
            case 107: {
                return "short";
            }
            case 40: {
                return "static";
            }
            case 34: {
                return "super";
            }
            case 80: {
                return "switch";
            }
            case 41: {
                return "synchronized";
            }
            case 35: {
                return "this";
            }
            case 81: {
                return "throw";
            }
            case 112: {
                return "throws";
            }
            case 58: {
                return "transient";
            }
            case 42: {
                return "true";
            }
            case 82: {
                return "try";
            }
            case 108: {
                return "void";
            }
            case 59: {
                return "volatile";
            }
            case 71: {
                return "while";
            }
            case 43: {
                return "Integer(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 44: {
                return "Long(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 45: {
                return "Float(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 46: {
                return "Double(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 47: {
                return "Char(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 48: {
                return "String(" + new String(this.getCurrentTokenSource()) + ")";
            }
            case 1: {
                return "++";
            }
            case 2: {
                return "--";
            }
            case 19: {
                return "==";
            }
            case 12: {
                return "<=";
            }
            case 13: {
                return ">=";
            }
            case 20: {
                return "!=";
            }
            case 18: {
                return "<<";
            }
            case 14: {
                return ">>";
            }
            case 16: {
                return ">>>";
            }
            case 84: {
                return "+=";
            }
            case 85: {
                return "-=";
            }
            case 110: {
                return "->";
            }
            case 86: {
                return "*=";
            }
            case 87: {
                return "/=";
            }
            case 88: {
                return "&=";
            }
            case 89: {
                return "|=";
            }
            case 90: {
                return "^=";
            }
            case 91: {
                return "%=";
            }
            case 92: {
                return "<<=";
            }
            case 93: {
                return ">>=";
            }
            case 94: {
                return ">>>=";
            }
            case 31: {
                return "||";
            }
            case 30: {
                return "&&";
            }
            case 4: {
                return "+";
            }
            case 5: {
                return "-";
            }
            case 62: {
                return "!";
            }
            case 8: {
                return "%";
            }
            case 23: {
                return "^";
            }
            case 21: {
                return "&";
            }
            case 6: {
                return "*";
            }
            case 26: {
                return "|";
            }
            case 63: {
                return "~";
            }
            case 9: {
                return "/";
            }
            case 15: {
                return ">";
            }
            case 11: {
                return "<";
            }
            case 24: {
                return "(";
            }
            case 25: {
                return ")";
            }
            case 49: {
                return "{";
            }
            case 32: {
                return "}";
            }
            case 10: {
                return "[";
            }
            case 64: {
                return "]";
            }
            case 28: {
                return ";";
            }
            case 29: {
                return "?";
            }
            case 61: {
                return ":";
            }
            case 7: {
                return "::";
            }
            case 33: {
                return ",";
            }
            case 3: {
                return ".";
            }
            case 70: {
                return "=";
            }
            case 60: {
                return "EOF";
            }
            case 1000: {
                return "white_space(" + new String(this.getCurrentTokenSource()) + ")";
            }
            default: {
                return "not-a-token";
            }
        }
    }
    
    public void unicodeInitializeBuffer(final int length) {
        this.withoutUnicodePtr = length;
        if (this.withoutUnicodeBuffer == null) {
            this.withoutUnicodeBuffer = new char[length + 11];
        }
        final int bLength = this.withoutUnicodeBuffer.length;
        if (1 + length >= bLength) {
            System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer = new char[length + 11], 0, bLength);
        }
        System.arraycopy(this.source, this.startPosition, this.withoutUnicodeBuffer, 1, length);
    }
    
    public void unicodeStore() {
        final int pos = ++this.withoutUnicodePtr;
        if (this.withoutUnicodeBuffer == null) {
            this.withoutUnicodeBuffer = new char[10];
        }
        final int length = this.withoutUnicodeBuffer.length;
        if (pos == length) {
            System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer = new char[length * 2], 0, length);
        }
        this.withoutUnicodeBuffer[pos] = this.currentCharacter;
    }
    
    public void unicodeStore(final char character) {
        final int pos = ++this.withoutUnicodePtr;
        if (this.withoutUnicodeBuffer == null) {
            this.withoutUnicodeBuffer = new char[10];
        }
        final int length = this.withoutUnicodeBuffer.length;
        if (pos == length) {
            System.arraycopy(this.withoutUnicodeBuffer, 0, this.withoutUnicodeBuffer = new char[length * 2], 0, length);
        }
        this.withoutUnicodeBuffer[pos] = character;
    }
    
    public static boolean isIdentifier(final int token) {
        return token == 22;
    }
    
    public static boolean isLiteral(final int token) {
        switch (token) {
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isKeyword(final int token) {
        switch (token) {
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
            case 79:
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
            case 112:
            case 114: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private VanguardParser getVanguardParser() {
        if (this.vanguardParser == null) {
            this.vanguardScanner = new VanguardScanner(this.sourceLevel, this.complianceLevel);
            this.vanguardParser = new VanguardParser(this.vanguardScanner);
            this.vanguardScanner.setActiveParser(this.vanguardParser);
        }
        this.vanguardScanner.setSource(this.source);
        this.vanguardScanner.resetTo(this.startPosition, this.eofPosition - 1);
        return this.vanguardParser;
    }
    
    protected final boolean maybeAtLambdaOrCast() {
        switch (this.lookBack[1]) {
            case 22:
            case 34:
            case 35:
            case 41:
            case 71:
            case 77:
            case 78:
            case 80:
            case 82:
            case 100: {
                return false;
            }
            default: {
                return this.activeParser.atConflictScenario(24);
            }
        }
    }
    
    protected final boolean maybeAtReferenceExpression() {
        Label_0234: {
            switch (this.lookBack[1]) {
                case 22: {
                    switch (this.lookBack[0]) {
                        case 11:
                        case 14:
                        case 15:
                        case 17:
                        case 21:
                        case 28:
                        case 32:
                        case 34:
                        case 36:
                        case 37:
                        case 40:
                        case 51:
                        case 52:
                        case 54:
                        case 55:
                        case 56:
                        case 67:
                        case 68:
                        case 69:
                        case 96:
                        case 112:
                        case 114: {
                            return false;
                        }
                        default: {
                            break Label_0234;
                        }
                    }
                    break;
                }
                case 0: {
                    break;
                }
                default: {
                    return false;
                }
            }
        }
        return this.activeParser.atConflictScenario(11);
    }
    
    private final boolean maybeAtEllipsisAnnotationsStart() {
        if (this.consumingEllipsisAnnotations) {
            return false;
        }
        switch (this.lookBack[1]) {
            case 3:
            case 11:
            case 17:
            case 21:
            case 33:
            case 34:
            case 36:
            case 49:
            case 96:
            case 112:
            case 114: {
                return false;
            }
            default: {
                return true;
            }
        }
    }
    
    protected final boolean atTypeAnnotation() {
        return !this.activeParser.atConflictScenario(37);
    }
    
    public void setActiveParser(final ConflictedParser parser) {
        this.activeParser = parser;
        this.lookBack[0] = (this.lookBack[1] = 0);
    }
    
    int disambiguatedToken(int token) {
        final VanguardParser parser = this.getVanguardParser();
        if (token == 24 && this.maybeAtLambdaOrCast()) {
            if (parser.parse(Goal.LambdaParameterListGoal)) {
                this.nextToken = 24;
                return 50;
            }
            this.vanguardScanner.resetTo(this.startPosition, this.eofPosition - 1);
            if (parser.parse(Goal.IntersectionCastGoal)) {
                this.nextToken = 24;
                return 65;
            }
        }
        else if (token == 11 && this.maybeAtReferenceExpression()) {
            if (parser.parse(Goal.ReferenceExpressionGoal)) {
                this.nextToken = 11;
                return 83;
            }
        }
        else if (token == 37 && this.atTypeAnnotation()) {
            token = 27;
            if (this.maybeAtEllipsisAnnotationsStart() && parser.parse(Goal.VarargTypeAnnotationGoal)) {
                this.consumingEllipsisAnnotations = true;
                this.nextToken = 27;
                return 115;
            }
        }
        return token;
    }
    
    protected boolean isAtAssistIdentifier() {
        return false;
    }
    
    public int fastForward(final Statement unused) {
        while (true) {
            int token;
            try {
                token = this.getNextToken();
            }
            catch (final InvalidInputException ex) {
                return 60;
            }
            switch (token) {
                case 22: {
                    if (this.isAtAssistIdentifier()) {
                        return token;
                    }
                }
                case 1:
                case 2:
                case 11:
                case 24:
                case 27:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                case 50:
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
                case 69:
                case 71:
                case 72:
                case 73:
                case 74:
                case 75:
                case 76:
                case 77:
                case 78:
                case 79:
                case 80:
                case 81:
                case 82:
                case 97:
                case 98:
                case 99:
                case 101:
                case 102:
                case 103:
                case 105:
                case 106:
                case 107:
                case 108: {
                    if (this.getVanguardParser().parse(Goal.BlockStatementoptGoal)) {
                        return token;
                    }
                    continue;
                }
                case 28:
                case 60: {
                    return token;
                }
                case 32: {
                    this.ungetToken(token);
                    return 28;
                }
                default: {
                    continue;
                }
            }
        }
    }
    
    private static final class VanguardScanner extends Scanner
    {
        public VanguardScanner(final long sourceLevel, final long complianceLevel) {
            super(false, false, false, sourceLevel, complianceLevel, null, null, false);
        }
        
        @Override
        public int getNextToken() throws InvalidInputException {
            if (this.nextToken != 0) {
                final int token = this.nextToken;
                this.nextToken = 0;
                return token;
            }
            int token = this.getNextToken0();
            if (token == 37 && this.atTypeAnnotation()) {
                if (((VanguardParser)this.activeParser).currentGoal == Goal.LambdaParameterListGoal) {
                    token = this.disambiguatedToken(token);
                }
                else {
                    token = 27;
                }
            }
            return (token == 60) ? 0 : token;
        }
    }
    
    private static final class Goal
    {
        int first;
        int[] follow;
        int rule;
        static int LambdaParameterListRule;
        static int IntersectionCastRule;
        static int ReferenceExpressionRule;
        static int VarargTypeAnnotationsRule;
        static int BlockStatementoptRule;
        static Goal LambdaParameterListGoal;
        static Goal IntersectionCastGoal;
        static Goal VarargTypeAnnotationGoal;
        static Goal ReferenceExpressionGoal;
        static Goal BlockStatementoptGoal;
        
        static {
            Goal.LambdaParameterListRule = 0;
            Goal.IntersectionCastRule = 0;
            Goal.ReferenceExpressionRule = 0;
            Goal.VarargTypeAnnotationsRule = 0;
            Goal.BlockStatementoptRule = 0;
            for (int i = 1; i <= 800; ++i) {
                if ("ParenthesizedLambdaParameterList".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    Goal.LambdaParameterListRule = i;
                }
                else if ("ParenthesizedCastNameAndBounds".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    Goal.IntersectionCastRule = i;
                }
                else if ("ReferenceExpressionTypeArgumentsAndTrunk".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    Goal.ReferenceExpressionRule = i;
                }
                else if ("TypeAnnotations".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    Goal.VarargTypeAnnotationsRule = i;
                }
                else if ("BlockStatementopt".equals(Parser.name[Parser.non_terminal_index[Parser.lhs[i]]])) {
                    Goal.BlockStatementoptRule = i;
                }
            }
            Goal.LambdaParameterListGoal = new Goal(110, new int[] { 110 }, Goal.LambdaParameterListRule);
            Goal.IntersectionCastGoal = new Goal(24, followSetOfCast(), Goal.IntersectionCastRule);
            Goal.VarargTypeAnnotationGoal = new Goal(37, new int[] { 113 }, Goal.VarargTypeAnnotationsRule);
            Goal.ReferenceExpressionGoal = new Goal(11, new int[] { 7 }, Goal.ReferenceExpressionRule);
            Goal.BlockStatementoptGoal = new Goal(49, new int[0], Goal.BlockStatementoptRule);
        }
        
        Goal(final int first, final int[] follow, final int rule) {
            this.first = first;
            this.follow = follow;
            this.rule = rule;
        }
        
        boolean hasBeenReached(final int act, final int token) {
            if (act == this.rule) {
                final int length = this.follow.length;
                if (length == 0) {
                    return true;
                }
                for (int i = 0; i < length; ++i) {
                    if (this.follow[i] == token) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        private static int[] followSetOfCast() {
            return new int[] { 22, 36, 34, 35, 38, 42, 39, 43, 44, 45, 46, 47, 48, 62, 63, 24 };
        }
    }
    
    private static final class VanguardParser extends Parser
    {
        public static final boolean SUCCESS = true;
        public static final boolean FAILURE = false;
        Goal currentGoal;
        
        public VanguardParser(final VanguardScanner scanner) {
            this.scanner = scanner;
        }
        
        protected boolean parse(final Goal goal) {
            this.currentGoal = goal;
            try {
                int act = 1580;
                this.stateStackTop = -1;
                this.currentToken = goal.first;
            Label_0022:
                while (true) {
                    final int stackLength = this.stack.length;
                    if (++this.stateStackTop >= stackLength) {
                        System.arraycopy(this.stack, 0, this.stack = new int[stackLength + 255], 0, stackLength);
                    }
                    this.stack[this.stateStackTop] = act;
                    act = Parser.tAction(act, this.currentToken);
                    if (act == 16382) {
                        return false;
                    }
                    if (act <= 800) {
                        --this.stateStackTop;
                    }
                    else if (act > 16382) {
                        this.unstackedAct = act;
                        try {
                            this.currentToken = this.scanner.getNextToken();
                        }
                        finally {
                            this.unstackedAct = 16382;
                        }
                        this.unstackedAct = 16382;
                        act -= 16382;
                    }
                    else {
                        if (act < 16381) {
                            this.unstackedAct = act;
                            try {
                                this.currentToken = this.scanner.getNextToken();
                            }
                            finally {
                                this.unstackedAct = 16382;
                            }
                            this.unstackedAct = 16382;
                            continue;
                        }
                        return false;
                    }
                    while (!goal.hasBeenReached(act, this.currentToken)) {
                        this.stateStackTop -= Parser.rhs[act] - 1;
                        act = Parser.ntAction(this.stack[this.stateStackTop], Parser.lhs[act]);
                        if (act > 800) {
                            continue Label_0022;
                        }
                    }
                    return true;
                }
            }
            catch (final Exception ex) {
                return false;
            }
        }
        
        @Override
        public String toString() {
            return "\n\n\n----------------Scanner--------------\n" + this.scanner.toString();
        }
    }
}
