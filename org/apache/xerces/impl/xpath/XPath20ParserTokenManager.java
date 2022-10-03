package org.apache.xerces.impl.xpath;

import java.io.IOException;
import java.io.PrintStream;

public class XPath20ParserTokenManager
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    protected char curChar;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    
    public void setDebugStream(final PrintStream debugStream) {
        this.debugStream = debugStream;
    }
    
    private final int jjStopStringLiteralDfa_0(final int n, final long n2) {
        switch (n) {
            case 0: {
                if ((n2 & 0x800000L) != 0x0L) {
                    return 23;
                }
                if ((n2 & 0x400000L) != 0x0L) {
                    return 24;
                }
                if ((n2 & 0x3CL) != 0x0L) {
                    this.jjmatchedKind = 19;
                    return 22;
                }
                return -1;
            }
            case 1: {
                if ((n2 & 0x28L) != 0x0L) {
                    return 22;
                }
                if ((n2 & 0x400000L) != 0x0L) {
                    return 4;
                }
                if ((n2 & 0x800000L) != 0x0L) {
                    return 9;
                }
                if ((n2 & 0x14L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 1;
                    return 22;
                }
                return -1;
            }
            case 2: {
                if ((n2 & 0x4L) != 0x0L) {
                    return 22;
                }
                if ((n2 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 2;
                    return 22;
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
            case '\n': {
                return this.jjStopAtPos(0, 25);
            }
            case '!': {
                return this.jjMoveStringLiteralDfa1_0(4096L);
            }
            case '\"': {
                return this.jjMoveStringLiteralDfa1_0(4194304L);
            }
            case '\'': {
                return this.jjMoveStringLiteralDfa1_0(8388608L);
            }
            case '(': {
                return this.jjStopAtPos(0, 9);
            }
            case ')': {
                return this.jjStopAtPos(0, 10);
            }
            case ':': {
                return this.jjStopAtPos(0, 6);
            }
            case '<': {
                this.jjmatchedKind = 13;
                return this.jjMoveStringLiteralDfa1_0(32768L);
            }
            case '=': {
                return this.jjStopAtPos(0, 11);
            }
            case '>': {
                this.jjmatchedKind = 14;
                return this.jjMoveStringLiteralDfa1_0(65536L);
            }
            case '?': {
                return this.jjStopAtPos(0, 8);
            }
            case '@': {
                return this.jjStopAtPos(0, 7);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(36L);
            }
            case 'c': {
                return this.jjMoveStringLiteralDfa1_0(16L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_0(8L);
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
            case '\"': {
                if ((n & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 22, 4);
                }
                break;
            }
            case '\'': {
                if ((n & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 23, 9);
                }
                break;
            }
            case '=': {
                if ((n & 0x1000L) != 0x0L) {
                    return this.jjStopAtPos(1, 12);
                }
                if ((n & 0x8000L) != 0x0L) {
                    return this.jjStopAtPos(1, 15);
                }
                if ((n & 0x10000L) != 0x0L) {
                    return this.jjStopAtPos(1, 16);
                }
                break;
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(n, 16L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa2_0(n, 4L);
            }
            case 'r': {
                if ((n & 0x8L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 3, 22);
                }
                break;
            }
            case 's': {
                if ((n & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 5, 22);
                }
                break;
            }
        }
        return this.jjStartNfa_0(0, n);
    }
    
    private int jjMoveStringLiteralDfa2_0(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_0(0, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException ex) {
            this.jjStopStringLiteralDfa_0(1, n2);
            return 2;
        }
        switch (this.curChar) {
            case 'd': {
                if ((n2 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 2, 22);
                }
                break;
            }
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(n2, 16L);
            }
        }
        return this.jjStartNfa_0(1, n2);
    }
    
    private int jjMoveStringLiteralDfa3_0(final long n, long n2) {
        if ((n2 &= n) == 0x0L) {
            return this.jjStartNfa_0(1, n);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException ex) {
            this.jjStopStringLiteralDfa_0(2, n2);
            return 3;
        }
        switch (this.curChar) {
            case 't': {
                if ((n2 & 0x10L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 4, 22);
                }
                break;
            }
        }
        return this.jjStartNfa_0(2, n2);
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
        this.jjnewStateCnt = 23;
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
                        case 23: {
                            if ((0xFFFFFF7FFFFFFFFFL & n3) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                            }
                            else if (this.curChar == '\'' && jjmatchedKind > 24) {
                                jjmatchedKind = 24;
                            }
                            if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if ((0xFFFFFFFBFFFFFFFFL & n3) != 0x0L) {
                                this.jjCheckNAddStates(3, 5);
                            }
                            else if (this.curChar == '\"' && jjmatchedKind > 24) {
                                jjmatchedKind = 24;
                            }
                            if (this.curChar == '\"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x3FF600000000000L & n3) != 0x0L) {
                                if (jjmatchedKind > 21) {
                                    jjmatchedKind = 21;
                                }
                            }
                            else if (this.curChar == '\'') {
                                this.jjCheckNAddStates(0, 2);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(3, 5);
                            }
                            if ((0x3FF000000000000L & n3) != 0x0L) {
                                if (jjmatchedKind > 17) {
                                    jjmatchedKind = 17;
                                }
                                this.jjCheckNAddStates(6, 9);
                                continue;
                            }
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(1);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 17) {
                                jjmatchedKind = 17;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 2: {
                            if ((0x3FF600000000000L & n3) != 0x0L && jjmatchedKind > 21) {
                                jjmatchedKind = 21;
                                continue;
                            }
                            continue;
                        }
                        case 3:
                        case 4: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if (this.curChar == '\"') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if ((0xFFFFFFFBFFFFFFFFL & n3) != 0x0L) {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if (this.curChar == '\"' && jjmatchedKind > 24) {
                                jjmatchedKind = 24;
                                continue;
                            }
                            continue;
                        }
                        case 8:
                        case 9: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if (this.curChar == '\'') {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if ((0xFFFFFF7FFFFFFFFFL & n3) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if (this.curChar == '\'' && jjmatchedKind > 24) {
                                jjmatchedKind = 24;
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if ((0x3FF000000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 17) {
                                jjmatchedKind = 17;
                            }
                            this.jjCheckNAddStates(6, 9);
                            continue;
                        }
                        case 14: {
                            if ((0x3FF000000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 17) {
                                jjmatchedKind = 17;
                            }
                            this.jjCheckNAddStates(10, 12);
                            continue;
                        }
                        case 15: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (jjmatchedKind > 17) {
                                jjmatchedKind = 17;
                            }
                            this.jjCheckNAddTwoStates(16, 17);
                            continue;
                        }
                        case 16: {
                            if ((0x3FF000000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 17) {
                                jjmatchedKind = 17;
                            }
                            this.jjCheckNAddTwoStates(16, 17);
                            continue;
                        }
                        case 18: {
                            if ((0x280000000000L & n3) != 0x0L) {
                                this.jjCheckNAdd(19);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((0x3FF000000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 17) {
                                jjmatchedKind = 17;
                            }
                            this.jjCheckNAdd(19);
                            continue;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 18) {
                                jjmatchedKind = 18;
                            }
                            this.jjCheckNAdd(20);
                            continue;
                        }
                        case 22: {
                            if ((0x3FF600000000000L & n3) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 19) {
                                jjmatchedKind = 19;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 22;
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
                        case 0: {
                            if ((0x7FFFFFE87FFFFFEL & n4) != 0x0L) {
                                if (jjmatchedKind > 19) {
                                    jjmatchedKind = 19;
                                }
                                this.jjCheckNAdd(22);
                            }
                            if ((0x7FFFFFE87FFFFFEL & n4) != 0x0L && jjmatchedKind > 21) {
                                jjmatchedKind = 21;
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & n4) != 0x0L && jjmatchedKind > 21) {
                                jjmatchedKind = 21;
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if ((0x2000000020L & n4) != 0x0L) {
                                this.jjAddStates(13, 14);
                                continue;
                            }
                            continue;
                        }
                        case 21:
                        case 22: {
                            if ((0x7FFFFFE87FFFFFEL & n4) == 0x0L) {
                                continue;
                            }
                            if (jjmatchedKind > 19) {
                                jjmatchedKind = 19;
                            }
                            this.jjCheckNAdd(22);
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 11:
                        case 23: {
                            this.jjCheckNAddStates(0, 2);
                            continue;
                        }
                        case 6:
                        case 24: {
                            this.jjCheckNAddStates(3, 5);
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
                        case 11:
                        case 23: {
                            if ((XPath20ParserTokenManager.jjbitVec0[n5] & n6) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 6:
                        case 24: {
                            if ((XPath20ParserTokenManager.jjbitVec0[n5] & n6) != 0x0L) {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
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
            final int n8 = 23;
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
    
    public XPath20ParserTokenManager(final SimpleCharStream input_stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[23];
        this.jjstateSet = new int[46];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = input_stream;
    }
    
    public XPath20ParserTokenManager(final SimpleCharStream simpleCharStream, final int n) throws XPathException {
        this(simpleCharStream);
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
        int n = 23;
        while (n-- > 0) {
            this.jjrounds[n] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final SimpleCharStream simpleCharStream, final int n) throws XPathException {
        this.ReInit(simpleCharStream);
        this.SwitchTo(n);
    }
    
    public void SwitchTo(final int curLexState) throws XPathException {
        if (curLexState >= 1 || curLexState < 0) {
            throw new XPathException("c-general-xpath");
        }
        this.curLexState = curLexState;
    }
    
    protected Token jjFillToken() {
        final String s = XPath20ParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
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
    
    public Token getNextToken() throws XPathException {
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (final IOException ex) {
                this.jjmatchedKind = 0;
                return this.jjFillToken();
            }
            try {
                this.input_stream.backup(0);
                while (this.curChar <= ' ' && (0x100000000L & 1L << this.curChar) != 0x0L) {
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
                boolean b = false;
                try {
                    this.input_stream.readChar();
                    this.input_stream.backup(1);
                }
                catch (final IOException ex3) {
                    b = true;
                    if (this.curChar == '\n' || this.curChar == '\r') {
                        ++endLine;
                    }
                    else {
                        ++endColumn;
                    }
                }
                if (!b) {
                    this.input_stream.backup(1);
                }
                throw new XPathException("c-general-xpath");
            }
            if (this.jjmatchedPos + 1 < jjMoveStringLiteralDfa0_0) {
                this.input_stream.backup(jjMoveStringLiteralDfa0_0 - this.jjmatchedPos - 1);
            }
            if ((XPath20ParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
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
            this.jjstateSet[this.jjnewStateCnt++] = XPath20ParserTokenManager.jjnextStates[n];
        } while (n++ != n2);
    }
    
    private void jjCheckNAddTwoStates(final int n, final int n2) {
        this.jjCheckNAdd(n);
        this.jjCheckNAdd(n2);
    }
    
    private void jjCheckNAddStates(int n, final int n2) {
        do {
            this.jjCheckNAdd(XPath20ParserTokenManager.jjnextStates[n]);
        } while (n++ != n2);
    }
    
    static {
        jjbitVec0 = new long[] { 0L, 0L, -1L, -1L };
        jjnextStates = new int[] { 10, 11, 12, 5, 6, 7, 14, 15, 17, 20, 14, 15, 17, 18, 19 };
        jjstrLiteralImages = new String[] { "", null, "and", "or", "cast", "as", ":", "@", "?", "(", ")", "=", "!=", "<", ">", "<=", ">=", null, null, null, null, null, "\"\"", "''", null, "\n" };
        lexStateNames = new String[] { "DEFAULT" };
        jjtoToken = new long[] { 67108861L };
        jjtoSkip = new long[] { 2L };
    }
}
