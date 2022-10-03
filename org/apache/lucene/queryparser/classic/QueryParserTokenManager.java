package org.apache.lucene.queryparser.classic;

import java.io.IOException;

public class QueryParserTokenManager implements QueryParserConstants
{
    static final long[] jjbitVec0;
    static final long[] jjbitVec1;
    static final long[] jjbitVec3;
    static final long[] jjbitVec4;
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
    
    private final int jjStopStringLiteralDfa_2(final int pos, final long active0) {
        return -1;
    }
    
    private final int jjStartNfa_2(final int pos, final long active0) {
        return this.jjMoveNfa_2(this.jjStopStringLiteralDfa_2(pos, active0), pos + 1);
    }
    
    private int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_2() {
        switch (this.curChar) {
            case '(': {
                return this.jjStopAtPos(0, 14);
            }
            case ')': {
                return this.jjStopAtPos(0, 15);
            }
            case '*': {
                return this.jjStartNfaWithStates_2(0, 17, 49);
            }
            case '+': {
                return this.jjStartNfaWithStates_2(0, 11, 15);
            }
            case '-': {
                return this.jjStartNfaWithStates_2(0, 12, 15);
            }
            case ':': {
                return this.jjStopAtPos(0, 16);
            }
            case '[': {
                return this.jjStopAtPos(0, 25);
            }
            case '^': {
                return this.jjStopAtPos(0, 18);
            }
            case '{': {
                return this.jjStopAtPos(0, 26);
            }
            default: {
                return this.jjMoveNfa_2(0, 0);
            }
        }
    }
    
    private int jjStartNfaWithStates_2(final int pos, final int kind, final int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_2(state, pos + 1);
    }
    
    private int jjMoveNfa_2(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 49;
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
                        case 33:
                        case 49: {
                            if ((0xFBFF7CF8FFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            continue;
                        }
                        case 0: {
                            if ((0xFBFF54F8FFFFD9FFL & l) != 0x0L) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(33, 34);
                            }
                            else if ((0x100002600L & l) != 0x0L) {
                                if (kind > 7) {
                                    kind = 7;
                                }
                            }
                            else if ((0x280200000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                            }
                            else if (this.curChar == '/') {
                                this.jjCheckNAddStates(0, 2);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(3, 5);
                            }
                            if ((0x7BFF50F8FFFFD9FFL & l) != 0x0L) {
                                if (kind > 20) {
                                    kind = 20;
                                }
                                this.jjCheckNAddStates(6, 10);
                            }
                            else if (this.curChar == '*') {
                                if (kind > 22) {
                                    kind = 22;
                                }
                            }
                            else if (this.curChar == '!' && kind > 10) {
                                kind = 10;
                            }
                            if (this.curChar == '&') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar == '&' && kind > 8) {
                                kind = 8;
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if (this.curChar == '&') {
                                this.jjstateSet[this.jjnewStateCnt++] = 4;
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if (this.curChar == '!' && kind > 10) {
                                kind = 10;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0x280200000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if ((0x100002600L & l) != 0x0L && kind > 13) {
                                kind = 13;
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if (this.curChar == '\"' && kind > 19) {
                                kind = 19;
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddStates(11, 14);
                            continue;
                        }
                        case 23: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(24);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddStates(15, 17);
                            continue;
                        }
                        case 25: {
                            if ((0x7BFF78F8FFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            continue;
                        }
                        case 28: {
                            if ((0x7BFF78F8FFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            continue;
                        }
                        case 31: {
                            if (this.curChar == '*' && kind > 22) {
                                kind = 22;
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0xFBFF54F8FFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            continue;
                        }
                        case 36:
                        case 38: {
                            if (this.curChar == '/') {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 37: {
                            if ((0xFFFF7FFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if (this.curChar == '/' && kind > 24) {
                                kind = 24;
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if ((0x7BFF50F8FFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(6, 10);
                            continue;
                        }
                        case 42: {
                            if ((0x7BFF78F8FFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            continue;
                        }
                        case 45: {
                            if ((0x7BFF78F8FFFFD9FFL & l) != 0x0L) {
                                this.jjCheckNAddStates(18, 20);
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 19: {
                            this.jjCheckNAddStates(3, 5);
                            continue;
                        }
                        case 27: {
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            continue;
                        }
                        case 30: {
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            continue;
                        }
                        case 35: {
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            continue;
                        }
                        case 44: {
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            continue;
                        }
                        case 47: {
                            this.jjCheckNAddStates(18, 20);
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 49: {
                            if ((0x97FFFFFF87FFFFFFL & l) != 0x0L) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(33, 34);
                                continue;
                            }
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(35, 35);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x97FFFFFF87FFFFFFL & l) != 0x0L) {
                                if (kind > 20) {
                                    kind = 20;
                                }
                                this.jjCheckNAddStates(6, 10);
                            }
                            else if (this.curChar == '\\') {
                                this.jjCheckNAddStates(21, 23);
                            }
                            else if (this.curChar == '~') {
                                if (kind > 21) {
                                    kind = 21;
                                }
                                this.jjCheckNAddStates(24, 26);
                            }
                            if ((0x97FFFFFF87FFFFFFL & l) != 0x0L) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(33, 34);
                            }
                            if (this.curChar == 'N') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                continue;
                            }
                            if (this.curChar == '|') {
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                                continue;
                            }
                            if (this.curChar == 'O') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                                continue;
                            }
                            if (this.curChar == 'A') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (this.curChar == 'D' && kind > 8) {
                                kind = 8;
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (this.curChar == 'N') {
                                this.jjstateSet[this.jjnewStateCnt++] = 1;
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == 'A') {
                                this.jjstateSet[this.jjnewStateCnt++] = 2;
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if (this.curChar == 'R' && kind > 9) {
                                kind = 9;
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if (this.curChar == 'O') {
                                this.jjstateSet[this.jjnewStateCnt++] = 6;
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if (this.curChar == '|' && kind > 9) {
                                kind = 9;
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if (this.curChar == '|') {
                                this.jjstateSet[this.jjnewStateCnt++] = 8;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if (this.curChar == 'T' && kind > 10) {
                                kind = 10;
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if (this.curChar == 'O') {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if (this.curChar == 'N') {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 19;
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if (this.curChar != '~') {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddStates(24, 26);
                            continue;
                        }
                        case 25: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            continue;
                        }
                        case 26: {
                            if (this.curChar == '\\') {
                                this.jjAddStates(27, 28);
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            continue;
                        }
                        case 29: {
                            if (this.curChar == '\\') {
                                this.jjAddStates(29, 30);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            continue;
                        }
                        case 33: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            continue;
                        }
                        case 34: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(35, 35);
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 38;
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(6, 10);
                            continue;
                        }
                        case 42: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            continue;
                        }
                        case 43: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(44, 44);
                                continue;
                            }
                            continue;
                        }
                        case 45: {
                            if ((0x97FFFFFF87FFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(18, 20);
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(47, 47);
                                continue;
                            }
                            continue;
                        }
                        case 48: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddStates(21, 23);
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 19: {
                            this.jjCheckNAddStates(3, 5);
                            continue;
                        }
                        case 27: {
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            continue;
                        }
                        case 30: {
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            continue;
                        }
                        case 35: {
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            continue;
                        }
                        case 37: {
                            this.jjAddStates(0, 2);
                            continue;
                        }
                        case 44: {
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            continue;
                        }
                        case 47: {
                            this.jjCheckNAddStates(18, 20);
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
                        case 33:
                        case 49: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            continue;
                        }
                        case 0: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 7) {
                                kind = 7;
                            }
                            if (jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(33, 34);
                            }
                            if (jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                if (kind > 20) {
                                    kind = 20;
                                }
                                this.jjCheckNAddStates(6, 10);
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 13) {
                                kind = 13;
                                continue;
                            }
                            continue;
                        }
                        case 17:
                        case 19: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            continue;
                        }
                        case 27: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(25, 26);
                            continue;
                        }
                        case 28: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            continue;
                        }
                        case 30: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 21) {
                                kind = 21;
                            }
                            this.jjCheckNAddTwoStates(28, 29);
                            continue;
                        }
                        case 32: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            continue;
                        }
                        case 35: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(33, 34);
                            continue;
                        }
                        case 37: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(6, 10);
                            continue;
                        }
                        case 42: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            continue;
                        }
                        case 44: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(42, 43);
                            continue;
                        }
                        case 45: {
                            if (jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(18, 20);
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(18, 20);
                                continue;
                            }
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
            final int n2 = 49;
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
                            if (kind > 27) {
                                kind = 27;
                            }
                            this.jjAddStates(31, 32);
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
                            if (kind > 27) {
                                kind = 27;
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
    
    private final int jjStopStringLiteralDfa_1(final int pos, final long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    return 6;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_1(final int pos, final long active0) {
        return this.jjMoveNfa_1(this.jjStopStringLiteralDfa_1(pos, active0), pos + 1);
    }
    
    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case 'T': {
                return this.jjMoveStringLiteralDfa1_1(268435456L);
            }
            case ']': {
                return this.jjStopAtPos(0, 29);
            }
            case '}': {
                return this.jjStopAtPos(0, 30);
            }
            default: {
                return this.jjMoveNfa_1(0, 0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_1(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_1(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case 'O': {
                if ((active0 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(1, 28, 6);
                }
                break;
            }
        }
        return this.jjStartNfa_1(0, active0);
    }
    
    private int jjStartNfaWithStates_1(final int pos, final int kind, final int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_1(state, pos + 1);
    }
    
    private int jjMoveNfa_1(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 7;
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
                            if ((0xFFFFFFFEFFFFFFFFL & l) != 0x0L) {
                                if (kind > 32) {
                                    kind = 32;
                                }
                                this.jjCheckNAdd(6);
                            }
                            if ((0x100002600L & l) != 0x0L) {
                                if (kind > 7) {
                                    kind = 7;
                                    continue;
                                }
                                continue;
                            }
                            else {
                                if (this.curChar == '\"') {
                                    this.jjCheckNAddTwoStates(2, 4);
                                    continue;
                                }
                                continue;
                            }
                            break;
                        }
                        case 1: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(2, 4);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(33, 35);
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(33, 35);
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if (this.curChar == '\"' && kind > 31) {
                                kind = 31;
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAdd(6);
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
                        case 0:
                        case 6: {
                            if ((0xDFFFFFFFDFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAdd(6);
                            continue;
                        }
                        case 4: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 3;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 2: {
                            this.jjAddStates(33, 35);
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
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 7) {
                                kind = 7;
                            }
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                if (kind > 32) {
                                    kind = 32;
                                }
                                this.jjCheckNAdd(6);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(33, 35);
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAdd(6);
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
            final int n2 = 7;
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
    
    private static final boolean jjCanMove_0(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 48: {
                return (QueryParserTokenManager.jjbitVec0[i2] & l2) != 0x0L;
            }
            default: {
                return false;
            }
        }
    }
    
    private static final boolean jjCanMove_1(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (QueryParserTokenManager.jjbitVec3[i2] & l2) != 0x0L;
            }
            default: {
                return (QueryParserTokenManager.jjbitVec1[i1] & l1) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_2(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (QueryParserTokenManager.jjbitVec3[i2] & l2) != 0x0L;
            }
            case 48: {
                return (QueryParserTokenManager.jjbitVec1[i2] & l2) != 0x0L;
            }
            default: {
                return (QueryParserTokenManager.jjbitVec4[i1] & l1) != 0x0L;
            }
        }
    }
    
    public QueryParserTokenManager(final CharStream stream) {
        this.jjrounds = new int[49];
        this.jjstateSet = new int[98];
        this.curLexState = 2;
        this.defaultLexState = 2;
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
        int i = 49;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final CharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 3 || lexState < 0) {
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
                case 2: {
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_2();
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
        jjbitVec0 = new long[] { 1L, 0L, 0L, 0L };
        jjbitVec1 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec3 = new long[] { 0L, 0L, -1L, -1L };
        jjbitVec4 = new long[] { -281474976710658L, -1L, -1L, -1L };
        jjnextStates = new int[] { 37, 39, 40, 17, 18, 20, 42, 45, 31, 46, 43, 22, 23, 25, 26, 24, 25, 26, 45, 31, 46, 44, 47, 35, 22, 28, 29, 27, 27, 30, 30, 0, 1, 2, 4, 5 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, "+", "-", null, "(", ")", ":", "*", "^", null, null, null, null, null, null, "[", "{", null, "TO", "]", "}", null, null };
        lexStateNames = new String[] { "Boost", "Range", "DEFAULT" };
        jjnewLexState = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, 1, 1, 2, -1, 2, 2, -1, -1 };
        jjtoToken = new long[] { 8589934337L };
        jjtoSkip = new long[] { 128L };
    }
}
