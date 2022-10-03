package org.apache.lucene.queryparser.surround.parser;

import java.io.IOException;

public class QueryParserTokenManager implements QueryParserConstants
{
    static final long[] jjbitVec0;
    static final long[] jjbitVec2;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    public static final int[] jjnewLexState;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    protected CharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    protected char curChar;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    
    private final int jjStopStringLiteralDfa_1(final int pos, final long active0) {
        return -1;
    }
    
    private final int jjStartNfa_1(final int pos, final long active0) {
        return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }
    
    private int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '(': {
                return this.jjStopAtPos(0, 13);
            }
            case ')': {
                return this.jjStopAtPos(0, 14);
            }
            case ',': {
                return this.jjStopAtPos(0, 15);
            }
            case ':': {
                return this.jjStopAtPos(0, 16);
            }
            case '^': {
                return this.jjStopAtPos(0, 17);
            }
            default: {
                return this.jjMoveNfa_1(0, 0);
            }
        }
    }
    
    private int jjMoveNfa_1(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 38;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddStates(0, 4);
                            }
                            else if ((0x100002600L & l) != 0x0L) {
                                if (kind > 7) {
                                    kind = 7;
                                }
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(5, 7);
                            }
                            if ((0x3FC000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(8, 11);
                                continue;
                            }
                            if (this.curChar == '1') {
                                this.jjCheckNAddTwoStates(20, 21);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((0x3FC000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(8, 11);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(17);
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(18);
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if (this.curChar == '1') {
                                this.jjCheckNAddTwoStates(20, 21);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(5, 7);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(24, 25);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (this.curChar == '\"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 26;
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if (this.curChar == '*' && kind > 18) {
                                kind = 18;
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(12, 14);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(12, 14);
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if (this.curChar == '\"' && kind > 19) {
                                kind = 19;
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAddStates(0, 4);
                            continue;
                        }
                        case 32: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(32, 33);
                                continue;
                            }
                            continue;
                        }
                        case 33: {
                            if (this.curChar == '*' && kind > 20) {
                                kind = 20;
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(34, 35);
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if ((0x8000040000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(35, 36);
                            continue;
                        }
                        case 36: {
                            if ((0xFBFFECFAFFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAdd(36);
                            continue;
                        }
                        case 37: {
                            if ((0x7BFFE8FAFFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAdd(37);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddStates(0, 4);
                            }
                            if ((0x400000004000L & l) != 0x0L) {
                                if (kind > 12) {
                                    kind = 12;
                                }
                            }
                            else if ((0x80000000800000L & l) != 0x0L) {
                                if (kind > 11) {
                                    kind = 11;
                                }
                            }
                            else if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                            }
                            else if (this.curChar == 'A') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                            }
                            else if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                            }
                            else if (this.curChar == 'O') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                            }
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                                continue;
                            }
                            if (this.curChar == 'N') {
                                this.jjstateSet[this.jjnewStateCnt++] = 12;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == 'R' && kind > 8) {
                                kind = 8;
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (this.curChar == 'O') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == 'r' && kind > 8) {
                                kind = 8;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if (this.curChar == 'D' && kind > 9) {
                                kind = 9;
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if (this.curChar == 'N') {
                                this.jjstateSet[this.jjnewStateCnt++] = 5;
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if (this.curChar == 'A') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if (this.curChar == 'd' && kind > 9) {
                                kind = 9;
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if (this.curChar == 'a') {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if (this.curChar == 'T' && kind > 10) {
                                kind = 10;
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if (this.curChar == 'O') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if (this.curChar == 'N') {
                                this.jjstateSet[this.jjnewStateCnt++] = 12;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if (this.curChar == 't' && kind > 10) {
                                kind = 10;
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if (this.curChar == 'o') {
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if (this.curChar == 'n') {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if ((0x80000000800000L & l) != 0x0L && kind > 11) {
                                kind = 11;
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((0x400000004000L & l) != 0x0L && kind > 12) {
                                kind = 12;
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(12, 14);
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 29;
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddStates(12, 14);
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAddStates(0, 4);
                            continue;
                        }
                        case 32: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(32, 33);
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(34, 35);
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            continue;
                        }
                        case 37: {
                            if ((0xFFFFFFFFBFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAdd(37);
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 24: {
                            this.jjAddStates(15, 16);
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else {
                final int hiByte = this.curChar >> 8;
                final int i2 = hiByte >> 6;
                final long l2 = 1L << (hiByte & 0x3F);
                final int i3 = (this.curChar & '\u00ff') >> 6;
                final long l3 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAddStates(0, 4);
                            continue;
                        }
                        case 24: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(15, 16);
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(12, 14);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(32, 33);
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(34, 35);
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            continue;
                        }
                        case 37: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAdd(37);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n = i = this.jjnewStateCnt;
            final int n2 = 38;
            final int jjnewStateCnt = startsAt;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n == (startsAt = n2 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (final IOException e) {
                return curPos;
            }
        }
        return curPos;
    }
    
    private int jjMoveStringLiteralDfa0_0() {
        return this.jjMoveNfa_0(0, 0);
    }
    
    private int jjMoveNfa_0(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 3;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjAddStates(17, 18);
                            continue;
                        }
                        case 1: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(2);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAdd(2);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    final int n = this.jjstateSet[--i];
                } while (i != startsAt);
            }
            else {
                final int hiByte = this.curChar >> 8;
                final int i2 = hiByte >> 6;
                final long l2 = 1L << (hiByte & 0x3F);
                final int i3 = (this.curChar & '\u00ff') >> 6;
                final long l3 = 1L << (this.curChar & '?');
                do {
                    final int n2 = this.jjstateSet[--i];
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            ++curPos;
            final int n3 = i = this.jjnewStateCnt;
            final int n4 = 3;
            final int jjnewStateCnt = startsAt;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n3 == (startsAt = n4 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (final IOException e) {
                return curPos;
            }
        }
        return curPos;
    }
    
    private static final boolean jjCanMove_0(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (QueryParserTokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (QueryParserTokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    public QueryParserTokenManager(final CharStream stream) {
        this.jjrounds = new int[38];
        this.jjstateSet = new int[76];
        this.curLexState = 1;
        this.defaultLexState = 1;
        this.input_stream = stream;
    }
    
    public QueryParserTokenManager(final CharStream stream, final int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }
    
    public void ReInit(final CharStream stream) {
        final int n = 0;
        this.jjnewStateCnt = n;
        this.jjmatchedPos = n;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }
    
    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 38;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final CharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 2 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    protected Token jjFillToken() {
        final String im = QueryParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        final String curTokenImage = (im == null) ? this.input_stream.GetImage() : im;
        final int beginLine = this.input_stream.getBeginLine();
        final int beginColumn = this.input_stream.getBeginColumn();
        final int endLine = this.input_stream.getEndLine();
        final int endColumn = this.input_stream.getEndColumn();
        final Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }
    
    public Token getNextToken() {
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (final IOException e) {
                this.jjmatchedKind = 0;
                final Token matchedToken = this.jjFillToken();
                return matchedToken;
            }
            switch (this.curLexState) {
                case 0: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_0();
                    break;
                }
                case 1: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_1();
                    break;
                }
            }
            if (this.jjmatchedKind == Integer.MAX_VALUE) {
                int error_line = this.input_stream.getEndLine();
                int error_column = this.input_stream.getEndColumn();
                String error_after = null;
                boolean EOFSeen = false;
                try {
                    this.input_stream.readChar();
                    this.input_stream.backup(1);
                }
                catch (final IOException e2) {
                    EOFSeen = true;
                    error_after = ((curPos <= 1) ? "" : this.input_stream.GetImage());
                    if (this.curChar == '\n' || this.curChar == '\r') {
                        ++error_line;
                        error_column = 0;
                    }
                    else {
                        ++error_column;
                    }
                }
                if (!EOFSeen) {
                    this.input_stream.backup(1);
                    error_after = ((curPos <= 1) ? "" : this.input_stream.GetImage());
                }
                throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
            }
            if (this.jjmatchedPos + 1 < curPos) {
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }
            if ((QueryParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                if (QueryParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = QueryParserTokenManager.jjnewLexState[this.jjmatchedKind];
                }
                return matchedToken;
            }
            if (QueryParserTokenManager.jjnewLexState[this.jjmatchedKind] == -1) {
                continue;
            }
            this.curLexState = QueryParserTokenManager.jjnewLexState[this.jjmatchedKind];
        }
    }
    
    private void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = QueryParserTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(QueryParserTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    static {
        jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
        jjnextStates = new int[] { 32, 33, 34, 35, 37, 24, 27, 28, 20, 17, 21, 18, 27, 28, 30, 24, 25, 0, 1 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, null, null, "(", ")", ",", ":", "^", null, null, null, null, null, null };
        lexStateNames = new String[] { "Boost", "DEFAULT" };
        jjnewLexState = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, 1 };
        jjtoToken = new long[] { 16776961L };
        jjtoSkip = new long[] { 128L };
    }
}
