package com.github.odiszapc.nginxparser.javacc;

import java.io.IOException;
import java.io.PrintStream;

public class NginxConfigParserTokenManager implements NginxConfigParserConstants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    public static final String[] lexStateNames;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    protected char curChar;
    
    public void setDebugStream(final PrintStream debugStream) {
        this.debugStream = debugStream;
    }
    
    private final int jjStopStringLiteralDfa_0(final int n, final long n2) {
        switch (n) {
            case 0: {
                if ((n2 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 13;
                    return 5;
                }
                if ((n2 & 0x20L) != 0x0L) {
                    return 20;
                }
                if ((n2 & 0x40L) != 0x0L) {
                    return 5;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_0(final int n, final long n2) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(n, n2), n + 1);
    }
    
    private int jjStopAtPos(final int jjmatchedPos, final int jjmatchedKind) {
        this.jjmatchedKind = jjmatchedKind;
        return (this.jjmatchedPos = jjmatchedPos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '(': {
                return this.jjStartNfaWithStates_0(0, 5, 20);
            }
            case ')': {
                return this.jjStartNfaWithStates_0(0, 6, 5);
            }
            case ';': {
                return this.jjStopAtPos(0, 9);
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(1024L);
            }
            case '{': {
                return this.jjStopAtPos(0, 7);
            }
            case '}': {
                return this.jjStopAtPos(0, 8);
            }
            default: {
                return this.jjMoveNfa_0(0, 0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_0(final long n) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException ex) {
            this.jjStopStringLiteralDfa_0(0, n);
            return 1;
        }
        switch (this.curChar) {
            case 'f': {
                if ((n & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 10, 5);
                }
                break;
            }
        }
        return this.jjStartNfa_0(0, n);
    }
    
    private int jjStartNfaWithStates_0(final int jjmatchedPos, final int jjmatchedKind, final int n) {
        this.jjmatchedKind = jjmatchedKind;
        this.jjmatchedPos = jjmatchedPos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException ex) {
            return jjmatchedPos + 1;
        }
        return this.jjMoveNfa_0(n, jjmatchedPos + 1);
    }
    
    private int jjMoveNfa_0(final int n, int jjmatchedPos) {
        int n2 = 0;
        this.jjnewStateCnt = 20;
        int i = 1;
        this.jjstateSet[0] = n;
        int jjmatchedKind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < '@') {
                final long n3 = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 20: {
                            if ((0xFFFFFDFFFFFFFFFFL & n3) != 0x0L) {
                                this.jjCheckNAddTwoStates(1, 2);
                            }
                            if ((0xA7FFEF5000000000L & n3) != 0x0L) {
                                if (jjmatchedKind > 13) {
                                    jjmatchedKind = 13;
                                }
                                this.jjCheckNAdd(5);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0xA7FFEF5000000000L & n3) != 0x0L) {
                                if (jjmatchedKind > 13) {
                                    jjmatchedKind = 13;
                                }
                                this.jjCheckNAdd(5);
                            }
                            else if (this.curChar == '#') {
                                if (jjmatchedKind > 16) {
                                    jjmatchedKind = 16;
                                }
                                this.jjCheckNAdd(19);
                            }
                            else if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(16, 17);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if ((0x3FE000000000000L & n3) != 0x0L) {
                                if (jjmatchedKind > 12) {
                                    jjmatchedKind = 12;
                                }
                                this.jjCheckNAdd(4);
                                continue;
                            }
                            if (this.curChar == '(') {
                                this.jjCheckNAdd(1);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0xFFFFFDFFFFFFFFFFL & n3) != 0x0L) {
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (this.curChar == ')' && jjmatchedKind > 11) {
                                jjmatchedKind = 11;
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if ((0x3FE000000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 12) {
                                jjmatchedKind = 12;
                            }
                            this.jjCheckNAdd(4);
                            continue;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 12) {
                                jjmatchedKind = 12;
                            }
                            this.jjCheckNAdd(4);
                            continue;
                        }
                        case 5: {
                            if ((0xA7FFEF5000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 13) {
                                jjmatchedKind = 13;
                            }
                            this.jjCheckNAdd(5);
                            continue;
                        }
                        case 6: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if ((0xFFFFFFFBFFFFFFFFL & n3) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if ((0x8400000000L & n3) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if (this.curChar == '\"' && jjmatchedKind > 14) {
                                jjmatchedKind = 14;
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if ((0xFF000000000000L & n3) != 0x0L) {
                                this.jjCheckNAddStates(3, 6);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if ((0xFF000000000000L & n3) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if ((0xF000000000000L & n3) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0xFF000000000000L & n3) != 0x0L) {
                                this.jjCheckNAdd(12);
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(16, 17);
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if ((0xFFFFFF7FFFFFFFFFL & n3) != 0x0L) {
                                this.jjCheckNAddTwoStates(16, 17);
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if (this.curChar == '\'' && jjmatchedKind > 15) {
                                jjmatchedKind = 15;
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if (this.curChar != '#') {
                                continue;
                            }
                            if (jjmatchedKind > 16) {
                                jjmatchedKind = 16;
                            }
                            this.jjCheckNAdd(19);
                            continue;
                        }
                        case 19: {
                            if ((0xFFFFFFFFFFFFDBFFL & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 16) {
                                jjmatchedKind = 16;
                            }
                            this.jjCheckNAdd(19);
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else if (this.curChar < '\u0080') {
                final long n4 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 20: {
                            this.jjCheckNAddTwoStates(1, 2);
                            if ((0x57FFFFFED7FFFFFFL & n4) != 0x0L) {
                                if (jjmatchedKind > 13) {
                                    jjmatchedKind = 13;
                                }
                                this.jjCheckNAdd(5);
                                continue;
                            }
                            continue;
                        }
                        case 0:
                        case 5: {
                            if ((0x57FFFFFED7FFFFFFL & n4) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 13) {
                                jjmatchedKind = 13;
                            }
                            this.jjCheckNAdd(5);
                            continue;
                        }
                        case 7: {
                            if ((0xFFFFFFFFEFFFFFFFL & n4) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if (this.curChar == '\\') {
                                this.jjAddStates(7, 9);
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if ((0x14404410000000L & n4) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 1: {
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 16: {
                            this.jjAddStates(10, 11);
                            continue;
                        }
                        case 19: {
                            if (jjmatchedKind > 16) {
                                jjmatchedKind = 16;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            continue;
                        }
                    }
                } while (i != n2);
            }
            else {
                final int n5 = (this.curChar & '\u00ff') >> 6;
                final long n6 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1:
                        case 20: {
                            if ((NginxConfigParserTokenManager.jjbitVec0[n5] & n6) != 0x0L) {
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if ((NginxConfigParserTokenManager.jjbitVec0[n5] & n6) != 0x0L) {
                                this.jjAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if ((NginxConfigParserTokenManager.jjbitVec0[n5] & n6) != 0x0L) {
                                this.jjAddStates(10, 11);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((NginxConfigParserTokenManager.jjbitVec0[n5] & n6) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 16) {
                                jjmatchedKind = 16;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            continue;
                        }
                        default: {
                            continue;
                        }
                    }
                } while (i != n2);
            }
            if (jjmatchedKind != Integer.MAX_VALUE) {
                this.jjmatchedKind = jjmatchedKind;
                this.jjmatchedPos = jjmatchedPos;
                jjmatchedKind = Integer.MAX_VALUE;
            }
            ++jjmatchedPos;
            final int n7 = i = this.jjnewStateCnt;
            final int n8 = 20;
            final int jjnewStateCnt = n2;
            this.jjnewStateCnt = jjnewStateCnt;
            if (n7 == (n2 = n8 - jjnewStateCnt)) {
                break;
            }
            try {
                this.curChar = this.input_stream.readChar();
            }
            catch (final IOException ex) {
                return jjmatchedPos;
            }
        }
        return jjmatchedPos;
    }
    
    protected Token jjFillToken() {
        final String s = NginxConfigParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        final String s2 = (s == null) ? this.input_stream.GetImage() : s;
        final int beginLine = this.input_stream.getBeginLine();
        final int beginColumn = this.input_stream.getBeginColumn();
        final int endLine = this.input_stream.getEndLine();
        final int endColumn = this.input_stream.getEndColumn();
        final Token token = Token.newToken(this.jjmatchedKind, s2);
        token.beginLine = beginLine;
        token.endLine = endLine;
        token.beginColumn = beginColumn;
        token.endColumn = endColumn;
        return token;
    }
    
    public Token getNextToken() {
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (final IOException ex) {
                this.jjmatchedKind = 0;
                this.jjmatchedPos = -1;
                return this.jjFillToken();
            }
            try {
                this.input_stream.backup(0);
                while (this.curChar <= ' ' && (0x100002600L & 1L << this.curChar) != 0x0L) {
                    this.curChar = this.input_stream.BeginToken();
                }
            }
            catch (final IOException ex2) {
                continue;
            }
            this.jjmatchedKind = Integer.MAX_VALUE;
            this.jjmatchedPos = 0;
            final int jjMoveStringLiteralDfa0_0 = this.jjMoveStringLiteralDfa0_0();
            if (this.jjmatchedKind == Integer.MAX_VALUE) {
                int endLine = this.input_stream.getEndLine();
                int endColumn = this.input_stream.getEndColumn();
                String s = null;
                boolean b = false;
                try {
                    this.input_stream.readChar();
                    this.input_stream.backup(1);
                }
                catch (final IOException ex3) {
                    b = true;
                    s = ((jjMoveStringLiteralDfa0_0 <= 1) ? "" : this.input_stream.GetImage());
                    if (this.curChar == '\n' || this.curChar == '\r') {
                        ++endLine;
                        endColumn = 0;
                    }
                    else {
                        ++endColumn;
                    }
                }
                if (!b) {
                    this.input_stream.backup(1);
                    s = ((jjMoveStringLiteralDfa0_0 <= 1) ? "" : this.input_stream.GetImage());
                }
                throw new TokenMgrError(b, this.curLexState, endLine, endColumn, s, this.curChar, 0);
            }
            if (this.jjmatchedPos + 1 < jjMoveStringLiteralDfa0_0) {
                this.input_stream.backup(jjMoveStringLiteralDfa0_0 - this.jjmatchedPos - 1);
            }
            if ((NginxConfigParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                return this.jjFillToken();
            }
        }
    }
    
    private void jjCheckNAdd(final int n) {
        if (this.jjrounds[n] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = n;
            this.jjrounds[n] = this.jjround;
        }
    }
    
    private void jjAddStates(int n, final int n2) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = NginxConfigParserTokenManager.jjnextStates[n];
        } while (n++ != n2);
    }
    
    private void jjCheckNAddTwoStates(final int n, final int n2) {
        this.jjCheckNAdd(n);
        this.jjCheckNAdd(n2);
    }
    
    private void jjCheckNAddStates(int n, final int n2) {
        do {
            this.jjCheckNAdd(NginxConfigParserTokenManager.jjnextStates[n]);
        } while (n++ != n2);
    }
    
    public NginxConfigParserTokenManager(final SimpleCharStream input_stream) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[20];
        this.jjstateSet = new int[40];
        this.input_stream = input_stream;
    }
    
    public NginxConfigParserTokenManager(final SimpleCharStream simpleCharStream, final int n) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[20];
        this.jjstateSet = new int[40];
        this.ReInit(simpleCharStream);
        this.SwitchTo(n);
    }
    
    public void ReInit(final SimpleCharStream input_stream) {
        final int n = 0;
        this.jjnewStateCnt = n;
        this.jjmatchedPos = n;
        this.curLexState = this.defaultLexState;
        this.input_stream = input_stream;
        this.ReInitRounds();
    }
    
    private void ReInitRounds() {
        this.jjround = -2147483647;
        int n = 20;
        while (n-- > 0) {
            this.jjrounds[n] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final SimpleCharStream simpleCharStream, final int n) {
        this.ReInit(simpleCharStream);
        this.SwitchTo(n);
    }
    
    public void SwitchTo(final int curLexState) {
        if (curLexState >= 1 || curLexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + curLexState + ". State unchanged.", 2);
        }
        this.curLexState = curLexState;
    }
    
    static {
        jjbitVec0 = new long[] { 0L, 0L, -1L, -1L };
        jjnextStates = new int[] { 7, 8, 10, 7, 8, 12, 10, 9, 11, 13, 16, 17 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, "(", ")", "{", "}", ";", "if", null, null, null, null, null, null, null, null };
        lexStateNames = new String[] { "DEFAULT" };
        jjtoToken = new long[] { 131041L };
        jjtoSkip = new long[] { 30L };
    }
}
