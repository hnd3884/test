package org.eclipse.jdt.internal.compiler.parser;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.compiler.CharOperation;

public class RecoveryScanner extends Scanner
{
    public static final char[] FAKE_IDENTIFIER;
    private RecoveryScannerData data;
    private int[] pendingTokens;
    private int pendingTokensPtr;
    private char[] fakeTokenSource;
    private boolean isInserted;
    private boolean precededByRemoved;
    private int skipNextInsertedTokens;
    public boolean record;
    
    static {
        FAKE_IDENTIFIER = "$missing$".toCharArray();
    }
    
    public RecoveryScanner(final Scanner scanner, final RecoveryScannerData data) {
        super(false, scanner.tokenizeWhiteSpace, scanner.checkNonExternalizedStringLiterals, scanner.sourceLevel, scanner.complianceLevel, scanner.taskTags, scanner.taskPriorities, scanner.isTaskCaseSensitive);
        this.pendingTokensPtr = -1;
        this.fakeTokenSource = null;
        this.isInserted = true;
        this.precededByRemoved = false;
        this.skipNextInsertedTokens = -1;
        this.record = true;
        this.setData(data);
    }
    
    public RecoveryScanner(final boolean tokenizeWhiteSpace, final boolean checkNonExternalizedStringLiterals, final long sourceLevel, final long complianceLevel, final char[][] taskTags, final char[][] taskPriorities, final boolean isTaskCaseSensitive, final RecoveryScannerData data) {
        super(false, tokenizeWhiteSpace, checkNonExternalizedStringLiterals, sourceLevel, complianceLevel, taskTags, taskPriorities, isTaskCaseSensitive);
        this.pendingTokensPtr = -1;
        this.fakeTokenSource = null;
        this.isInserted = true;
        this.precededByRemoved = false;
        this.skipNextInsertedTokens = -1;
        this.record = true;
        this.setData(data);
    }
    
    public void insertToken(final int token, final int completedToken, final int position) {
        this.insertTokens(new int[] { token }, completedToken, position);
    }
    
    private int[] reverse(final int[] tokens) {
        final int length = tokens.length;
        for (int i = 0, max = length / 2; i < max; ++i) {
            final int tmp = tokens[i];
            tokens[i] = tokens[length - i - 1];
            tokens[length - i - 1] = tmp;
        }
        return tokens;
    }
    
    public void insertTokens(final int[] tokens, final int completedToken, final int position) {
        if (!this.record) {
            return;
        }
        if (completedToken > -1 && Parser.statements_recovery_filter[completedToken] != '\0') {
            return;
        }
        final RecoveryScannerData data = this.data;
        ++data.insertedTokensPtr;
        if (this.data.insertedTokens == null) {
            this.data.insertedTokens = new int[10][];
            this.data.insertedTokensPosition = new int[10];
            this.data.insertedTokenUsed = new boolean[10];
        }
        else if (this.data.insertedTokens.length == this.data.insertedTokensPtr) {
            final int length = this.data.insertedTokens.length;
            System.arraycopy(this.data.insertedTokens, 0, this.data.insertedTokens = new int[length * 2][], 0, length);
            System.arraycopy(this.data.insertedTokensPosition, 0, this.data.insertedTokensPosition = new int[length * 2], 0, length);
            System.arraycopy(this.data.insertedTokenUsed, 0, this.data.insertedTokenUsed = new boolean[length * 2], 0, length);
        }
        this.data.insertedTokens[this.data.insertedTokensPtr] = this.reverse(tokens);
        this.data.insertedTokensPosition[this.data.insertedTokensPtr] = position;
        this.data.insertedTokenUsed[this.data.insertedTokensPtr] = false;
    }
    
    public void insertTokenAhead(final int token, final int index) {
        if (!this.record) {
            return;
        }
        final int length = this.data.insertedTokens[index].length;
        final int[] tokens = new int[length + 1];
        System.arraycopy(this.data.insertedTokens[index], 0, tokens, 1, length);
        tokens[0] = token;
        this.data.insertedTokens[index] = tokens;
    }
    
    public void replaceTokens(final int token, final int start, final int end) {
        this.replaceTokens(new int[] { token }, start, end);
    }
    
    public void replaceTokens(final int[] tokens, final int start, final int end) {
        if (!this.record) {
            return;
        }
        final RecoveryScannerData data = this.data;
        ++data.replacedTokensPtr;
        if (this.data.replacedTokensStart == null) {
            this.data.replacedTokens = new int[10][];
            this.data.replacedTokensStart = new int[10];
            this.data.replacedTokensEnd = new int[10];
            this.data.replacedTokenUsed = new boolean[10];
        }
        else if (this.data.replacedTokensStart.length == this.data.replacedTokensPtr) {
            final int length = this.data.replacedTokensStart.length;
            System.arraycopy(this.data.replacedTokens, 0, this.data.replacedTokens = new int[length * 2][], 0, length);
            System.arraycopy(this.data.replacedTokensStart, 0, this.data.replacedTokensStart = new int[length * 2], 0, length);
            System.arraycopy(this.data.replacedTokensEnd, 0, this.data.replacedTokensEnd = new int[length * 2], 0, length);
            System.arraycopy(this.data.replacedTokenUsed, 0, this.data.replacedTokenUsed = new boolean[length * 2], 0, length);
        }
        this.data.replacedTokens[this.data.replacedTokensPtr] = this.reverse(tokens);
        this.data.replacedTokensStart[this.data.replacedTokensPtr] = start;
        this.data.replacedTokensEnd[this.data.replacedTokensPtr] = end;
        this.data.replacedTokenUsed[this.data.replacedTokensPtr] = false;
    }
    
    public void removeTokens(final int start, final int end) {
        if (!this.record) {
            return;
        }
        final RecoveryScannerData data = this.data;
        ++data.removedTokensPtr;
        if (this.data.removedTokensStart == null) {
            this.data.removedTokensStart = new int[10];
            this.data.removedTokensEnd = new int[10];
            this.data.removedTokenUsed = new boolean[10];
        }
        else if (this.data.removedTokensStart.length == this.data.removedTokensPtr) {
            final int length = this.data.removedTokensStart.length;
            System.arraycopy(this.data.removedTokensStart, 0, this.data.removedTokensStart = new int[length * 2], 0, length);
            System.arraycopy(this.data.removedTokensEnd, 0, this.data.removedTokensEnd = new int[length * 2], 0, length);
            System.arraycopy(this.data.removedTokenUsed, 0, this.data.removedTokenUsed = new boolean[length * 2], 0, length);
        }
        this.data.removedTokensStart[this.data.removedTokensPtr] = start;
        this.data.removedTokensEnd[this.data.removedTokensPtr] = end;
        this.data.removedTokenUsed[this.data.removedTokensPtr] = false;
    }
    
    @Override
    protected int getNextToken0() throws InvalidInputException {
        if (this.pendingTokensPtr > -1) {
            final int pendingToken = this.pendingTokens[this.pendingTokensPtr--];
            if (pendingToken == 22) {
                this.fakeTokenSource = RecoveryScanner.FAKE_IDENTIFIER;
            }
            else {
                this.fakeTokenSource = CharOperation.NO_CHAR;
            }
            return pendingToken;
        }
        this.fakeTokenSource = null;
        this.precededByRemoved = false;
        if (this.data.insertedTokens != null) {
            for (int i = 0; i <= this.data.insertedTokensPtr; ++i) {
                if (this.data.insertedTokensPosition[i] == this.currentPosition - 1 && i > this.skipNextInsertedTokens) {
                    this.data.insertedTokenUsed[i] = true;
                    this.pendingTokens = this.data.insertedTokens[i];
                    this.pendingTokensPtr = this.data.insertedTokens[i].length - 1;
                    this.isInserted = true;
                    this.startPosition = this.currentPosition;
                    this.skipNextInsertedTokens = i;
                    final int pendingToken2 = this.pendingTokens[this.pendingTokensPtr--];
                    if (pendingToken2 == 22) {
                        this.fakeTokenSource = RecoveryScanner.FAKE_IDENTIFIER;
                    }
                    else {
                        this.fakeTokenSource = CharOperation.NO_CHAR;
                    }
                    return pendingToken2;
                }
            }
            this.skipNextInsertedTokens = -1;
        }
        final int previousLocation = this.currentPosition;
        final int currentToken = super.getNextToken0();
        if (this.data.replacedTokens != null) {
            for (int j = 0; j <= this.data.replacedTokensPtr; ++j) {
                if (this.data.replacedTokensStart[j] >= previousLocation && this.data.replacedTokensStart[j] <= this.startPosition && this.data.replacedTokensEnd[j] >= this.currentPosition - 1) {
                    this.data.replacedTokenUsed[j] = true;
                    this.pendingTokens = this.data.replacedTokens[j];
                    this.pendingTokensPtr = this.data.replacedTokens[j].length - 1;
                    this.fakeTokenSource = RecoveryScanner.FAKE_IDENTIFIER;
                    this.isInserted = false;
                    this.currentPosition = this.data.replacedTokensEnd[j] + 1;
                    final int pendingToken3 = this.pendingTokens[this.pendingTokensPtr--];
                    if (pendingToken3 == 22) {
                        this.fakeTokenSource = RecoveryScanner.FAKE_IDENTIFIER;
                    }
                    else {
                        this.fakeTokenSource = CharOperation.NO_CHAR;
                    }
                    return pendingToken3;
                }
            }
        }
        if (this.data.removedTokensStart != null) {
            for (int j = 0; j <= this.data.removedTokensPtr; ++j) {
                if (this.data.removedTokensStart[j] >= previousLocation && this.data.removedTokensStart[j] <= this.startPosition && this.data.removedTokensEnd[j] >= this.currentPosition - 1) {
                    this.data.removedTokenUsed[j] = true;
                    this.currentPosition = this.data.removedTokensEnd[j] + 1;
                    this.precededByRemoved = false;
                    return this.getNextToken0();
                }
            }
        }
        return currentToken;
    }
    
    @Override
    public char[] getCurrentIdentifierSource() {
        if (this.fakeTokenSource != null) {
            return this.fakeTokenSource;
        }
        return super.getCurrentIdentifierSource();
    }
    
    @Override
    public char[] getCurrentTokenSourceString() {
        if (this.fakeTokenSource != null) {
            return this.fakeTokenSource;
        }
        return super.getCurrentTokenSourceString();
    }
    
    @Override
    public char[] getCurrentTokenSource() {
        if (this.fakeTokenSource != null) {
            return this.fakeTokenSource;
        }
        return super.getCurrentTokenSource();
    }
    
    public RecoveryScannerData getData() {
        return this.data;
    }
    
    public boolean isFakeToken() {
        return this.fakeTokenSource != null;
    }
    
    public boolean isInsertedToken() {
        return this.fakeTokenSource != null && this.isInserted;
    }
    
    public boolean isReplacedToken() {
        return this.fakeTokenSource != null && !this.isInserted;
    }
    
    public boolean isPrecededByRemovedToken() {
        return this.precededByRemoved;
    }
    
    public void setData(final RecoveryScannerData data) {
        if (data == null) {
            this.data = new RecoveryScannerData();
        }
        else {
            this.data = data;
        }
    }
    
    public void setPendingTokens(final int[] pendingTokens) {
        this.pendingTokens = pendingTokens;
        this.pendingTokensPtr = pendingTokens.length - 1;
    }
}
