package org.apache.taglibs.standard.lang.jstl.parser;

import java.io.IOException;
import java.io.PrintStream;

public class ELParserTokenManager implements ELParserConstants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final long[] jjbitVec2;
    static final long[] jjbitVec3;
    static final long[] jjbitVec4;
    static final long[] jjbitVec5;
    static final long[] jjbitVec6;
    static final long[] jjbitVec7;
    static final long[] jjbitVec8;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    public static final int[] jjnewLexState;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    private SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    protected char curChar;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    
    public void setDebugStream(final PrintStream ds) {
        this.debugStream = ds;
    }
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 1;
                    return 2;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_0(final int pos, final long active0) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }
    
    private final int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private final int jjStartNfaWithStates_0(final int pos, final int kind, final int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            return pos + 1;
        }
        return this.jjMoveNfa_0(state, pos + 1);
    }
    
    private final int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '$': {
                return this.jjMoveStringLiteralDfa1_0(4L);
            }
            default: {
                return this.jjMoveNfa_0(1, 0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_0(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '{': {
                if ((active0 & 0x4L) != 0x0L) {
                    return this.jjStopAtPos(1, 2);
                }
                break;
            }
        }
        return this.jjStartNfa_0(0, active0);
    }
    
    private final void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private final void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = ELParserTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private final void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private final void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(ELParserTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    private final void jjCheckNAddStates(final int start) {
        this.jjCheckNAdd(ELParserTokenManager.jjnextStates[start]);
        this.jjCheckNAdd(ELParserTokenManager.jjnextStates[start + 1]);
    }
    
    private final int jjMoveNfa_0(final int startState, int curPos) {
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
                        case 1: {
                            if ((0xFFFFFFEFFFFFFFFFL & l) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAdd(0);
                                continue;
                            }
                            if (this.curChar == '$') {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAdd(2);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0xFFFFFFEFFFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(0);
                            continue;
                        }
                        case 2: {
                            if ((0xFFFFFFEFFFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 1) {
                                kind = 1;
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
                    switch (this.jjstateSet[--i]) {
                        case 2: {
                            if ((0xF7FFFFFFFFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 0:
                        case 1: {
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(0);
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
                        case 0:
                        case 1: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(0);
                            continue;
                        }
                        case 2: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 2;
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
            final int n2 = 3;
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
    
    private final int jjStopStringLiteralDfa_1(final int pos, final long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x1568015547000L) != 0x0L) {
                    this.jjmatchedKind = 49;
                    return 6;
                }
                if ((active0 & 0x10000L) != 0x0L) {
                    return 1;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x400015540000L) != 0x0L) {
                    return 6;
                }
                if ((active0 & 0x1168000007000L) != 0x0L) {
                    this.jjmatchedKind = 49;
                    this.jjmatchedPos = 1;
                    return 6;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x168000000000L) != 0x0L) {
                    return 6;
                }
                if ((active0 & 0x1000000007000L) != 0x0L) {
                    this.jjmatchedKind = 49;
                    this.jjmatchedPos = 2;
                    return 6;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x5000L) != 0x0L) {
                    return 6;
                }
                if ((active0 & 0x1000000002000L) != 0x0L) {
                    this.jjmatchedKind = 49;
                    this.jjmatchedPos = 3;
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
    
    private final int jjStartNfaWithStates_1(final int pos, final int kind, final int state) {
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
    
    private final int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case '!': {
                this.jjmatchedKind = 43;
                return this.jjMoveStringLiteralDfa1_1(134217728L);
            }
            case '%': {
                return this.jjStopAtPos(0, 40);
            }
            case '&': {
                return this.jjMoveStringLiteralDfa1_1(35184372088832L);
            }
            case '(': {
                return this.jjStopAtPos(0, 29);
            }
            case ')': {
                return this.jjStopAtPos(0, 30);
            }
            case '*': {
                return this.jjStopAtPos(0, 37);
            }
            case '+': {
                return this.jjStopAtPos(0, 35);
            }
            case ',': {
                return this.jjStopAtPos(0, 31);
            }
            case '-': {
                return this.jjStopAtPos(0, 36);
            }
            case '.': {
                return this.jjStartNfaWithStates_1(0, 16, 1);
            }
            case '/': {
                return this.jjStopAtPos(0, 38);
            }
            case ':': {
                return this.jjStopAtPos(0, 32);
            }
            case '<': {
                this.jjmatchedKind = 19;
                return this.jjMoveStringLiteralDfa1_1(8388608L);
            }
            case '=': {
                return this.jjMoveStringLiteralDfa1_1(2097152L);
            }
            case '>': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_1(33554432L);
            }
            case '[': {
                return this.jjStopAtPos(0, 33);
            }
            case ']': {
                return this.jjStopAtPos(0, 34);
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa1_1(17592186044416L);
            }
            case 'd': {
                return this.jjMoveStringLiteralDfa1_1(549755813888L);
            }
            case 'e': {
                return this.jjMoveStringLiteralDfa1_1(281474980904960L);
            }
            case 'f': {
                return this.jjMoveStringLiteralDfa1_1(8192L);
            }
            case 'g': {
                return this.jjMoveStringLiteralDfa1_1(67371008L);
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa1_1(17825792L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa1_1(2199023255552L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa1_1(4398314962944L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa1_1(70368744177664L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa1_1(4096L);
            }
            case '|': {
                return this.jjMoveStringLiteralDfa1_1(140737488355328L);
            }
            case '}': {
                return this.jjStopAtPos(0, 15);
            }
            default: {
                return this.jjMoveNfa_1(0, 0);
            }
        }
    }
    
    private final int jjMoveStringLiteralDfa1_1(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_1(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '&': {
                if ((active0 & 0x200000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 45);
                }
                break;
            }
            case '=': {
                if ((active0 & 0x200000L) != 0x0L) {
                    return this.jjStopAtPos(1, 21);
                }
                if ((active0 & 0x800000L) != 0x0L) {
                    return this.jjStopAtPos(1, 23);
                }
                if ((active0 & 0x2000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 25);
                }
                if ((active0 & 0x8000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 27);
                }
                break;
            }
            case 'a': {
                return this.jjMoveStringLiteralDfa2_1(active0, 8192L);
            }
            case 'e': {
                if ((active0 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(1, 24, 6);
                }
                if ((active0 & 0x4000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(1, 26, 6);
                }
                if ((active0 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(1, 28, 6);
                }
                break;
            }
            case 'i': {
                return this.jjMoveStringLiteralDfa2_1(active0, 549755813888L);
            }
            case 'm': {
                return this.jjMoveStringLiteralDfa2_1(active0, 281474976710656L);
            }
            case 'n': {
                return this.jjMoveStringLiteralDfa2_1(active0, 17592186044416L);
            }
            case 'o': {
                return this.jjMoveStringLiteralDfa2_1(active0, 6597069766656L);
            }
            case 'q': {
                if ((active0 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(1, 22, 6);
                }
                break;
            }
            case 'r': {
                if ((active0 & 0x400000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(1, 46, 6);
                }
                return this.jjMoveStringLiteralDfa2_1(active0, 4096L);
            }
            case 't': {
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(1, 18, 6);
                }
                if ((active0 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(1, 20, 6);
                }
                break;
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa2_1(active0, 16384L);
            }
            case '|': {
                if ((active0 & 0x800000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 47);
                }
                break;
            }
        }
        return this.jjStartNfa_1(0, active0);
    }
    
    private final int jjMoveStringLiteralDfa2_1(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_1(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_1(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 'd': {
                if ((active0 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(2, 41, 6);
                }
                if ((active0 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(2, 44, 6);
                }
                break;
            }
            case 'l': {
                return this.jjMoveStringLiteralDfa3_1(active0, 24576L);
            }
            case 'p': {
                return this.jjMoveStringLiteralDfa3_1(active0, 281474976710656L);
            }
            case 't': {
                if ((active0 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(2, 42, 6);
                }
                break;
            }
            case 'u': {
                return this.jjMoveStringLiteralDfa3_1(active0, 4096L);
            }
            case 'v': {
                if ((active0 & 0x8000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(2, 39, 6);
                }
                break;
            }
        }
        return this.jjStartNfa_1(1, active0);
    }
    
    private final int jjMoveStringLiteralDfa3_1(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_1(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_1(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(3, 12, 6);
                }
                break;
            }
            case 'l': {
                if ((active0 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(3, 14, 6);
                }
                break;
            }
            case 's': {
                return this.jjMoveStringLiteralDfa4_1(active0, 8192L);
            }
            case 't': {
                return this.jjMoveStringLiteralDfa4_1(active0, 281474976710656L);
            }
        }
        return this.jjStartNfa_1(2, active0);
    }
    
    private final int jjMoveStringLiteralDfa4_1(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_1(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_1(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 'e': {
                if ((active0 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(4, 13, 6);
                }
                break;
            }
            case 'y': {
                if ((active0 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(4, 48, 6);
                }
                break;
            }
        }
        return this.jjStartNfa_1(3, active0);
    }
    
    private final int jjMoveNfa_1(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 35;
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
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 7) {
                                    kind = 7;
                                }
                                this.jjCheckNAddStates(0, 4);
                                continue;
                            }
                            if ((0x1800000000L & l) != 0x0L) {
                                if (kind > 49) {
                                    kind = 49;
                                }
                                this.jjCheckNAdd(6);
                                continue;
                            }
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(5, 9);
                                continue;
                            }
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(10, 14);
                                continue;
                            }
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(1);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 8) {
                                kind = 8;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 3: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(4);
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 8) {
                                kind = 8;
                            }
                            this.jjCheckNAdd(4);
                            continue;
                        }
                        case 5: {
                            if ((0x1800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 49) {
                                kind = 49;
                            }
                            this.jjCheckNAdd(6);
                            continue;
                        }
                        case 6: {
                            if ((0x3FF001000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 49) {
                                kind = 49;
                            }
                            this.jjCheckNAdd(6);
                            continue;
                        }
                        case 7: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 7) {
                                kind = 7;
                            }
                            this.jjCheckNAddStates(0, 4);
                            continue;
                        }
                        case 8: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 7) {
                                kind = 7;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 9: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(9, 10);
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 8) {
                                kind = 8;
                            }
                            this.jjCheckNAddTwoStates(11, 12);
                            continue;
                        }
                        case 11: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 8) {
                                kind = 8;
                            }
                            this.jjCheckNAddTwoStates(11, 12);
                            continue;
                        }
                        case 13: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(14);
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 8) {
                                kind = 8;
                            }
                            this.jjCheckNAdd(14);
                            continue;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(15, 16);
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(18);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 8) {
                                kind = 8;
                            }
                            this.jjCheckNAdd(18);
                            continue;
                        }
                        case 19: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(10, 14);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(15, 17);
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(15, 17);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if (this.curChar == '\"' && kind > 10) {
                                kind = 10;
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
                        case 26: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L && kind > 11) {
                                kind = 11;
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(5, 9);
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(18, 20);
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddStates(18, 20);
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if (this.curChar == '\'' && kind > 10) {
                                kind = 10;
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(32, 33);
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L && kind > 11) {
                                kind = 11;
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
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0:
                        case 6: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 49) {
                                kind = 49;
                            }
                            this.jjCheckNAdd(6);
                            continue;
                        }
                        case 2: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(21, 22);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(23, 24);
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(25, 26);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(15, 17);
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 22;
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddStates(15, 17);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjAddStates(27, 28);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 26;
                                continue;
                            }
                            continue;
                        }
                        case 26:
                        case 34: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L && kind > 11) {
                                kind = 11;
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(18, 20);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 30;
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddStates(18, 20);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjAddStates(29, 30);
                                continue;
                            }
                            continue;
                        }
                        case 33: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 34;
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
            else {
                final int hiByte = this.curChar >> 8;
                final int i2 = hiByte >> 6;
                final long l2 = 1L << (hiByte & 0x3F);
                final int i3 = (this.curChar & '\u00ff') >> 6;
                final long l3 = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 0:
                        case 6: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 49) {
                                kind = 49;
                            }
                            this.jjCheckNAdd(6);
                            continue;
                        }
                        case 20: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(15, 17);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(27, 28);
                                continue;
                            }
                            continue;
                        }
                        case 26:
                        case 34: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 11) {
                                kind = 11;
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(18, 20);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(29, 30);
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
            final int n2 = 35;
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
            case 0: {
                return (ELParserTokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (ELParserTokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_1(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (ELParserTokenManager.jjbitVec4[i2] & l2) != 0x0L;
            }
            case 48: {
                return (ELParserTokenManager.jjbitVec5[i2] & l2) != 0x0L;
            }
            case 49: {
                return (ELParserTokenManager.jjbitVec6[i2] & l2) != 0x0L;
            }
            case 51: {
                return (ELParserTokenManager.jjbitVec7[i2] & l2) != 0x0L;
            }
            case 61: {
                return (ELParserTokenManager.jjbitVec8[i2] & l2) != 0x0L;
            }
            default: {
                return (ELParserTokenManager.jjbitVec3[i1] & l1) != 0x0L;
            }
        }
    }
    
    public ELParserTokenManager(final SimpleCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[35];
        this.jjstateSet = new int[70];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }
    
    public ELParserTokenManager(final SimpleCharStream stream, final int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }
    
    public void ReInit(final SimpleCharStream stream) {
        final int n = 0;
        this.jjnewStateCnt = n;
        this.jjmatchedPos = n;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }
    
    private final void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 35;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final SimpleCharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 2 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    private final Token jjFillToken() {
        final Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        final String im = ELParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
        t.image = ((im == null) ? this.input_stream.GetImage() : im);
        t.beginLine = this.input_stream.getBeginLine();
        t.beginColumn = this.input_stream.getBeginColumn();
        t.endLine = this.input_stream.getEndLine();
        t.endColumn = this.input_stream.getEndColumn();
        return t;
    }
    
    public final Token getNextToken() {
        final Token specialToken = null;
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
                    try {
                        this.input_stream.backup(0);
                        while (this.curChar <= ' ' && (0x100002600L & 1L << this.curChar) != 0x0L) {
                            this.curChar = this.input_stream.BeginToken();
                        }
                    }
                    catch (final IOException e2) {
                        continue;
                    }
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    this.jjmatchedPos = 0;
                    curPos = this.jjMoveStringLiteralDfa0_1();
                    if (this.jjmatchedPos == 0 && this.jjmatchedKind > 53) {
                        this.jjmatchedKind = 53;
                        break;
                    }
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
                catch (final IOException e3) {
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
            if ((ELParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                if (ELParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = ELParserTokenManager.jjnewLexState[this.jjmatchedKind];
                }
                return matchedToken;
            }
            if (ELParserTokenManager.jjnewLexState[this.jjmatchedKind] == -1) {
                continue;
            }
            this.curLexState = ELParserTokenManager.jjnewLexState[this.jjmatchedKind];
        }
    }
    
    static {
        jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
        jjbitVec3 = new long[] { 2301339413881290750L, -16384L, 4294967295L, 432345564227567616L };
        jjbitVec4 = new long[] { 0L, 0L, 0L, -36028797027352577L };
        jjbitVec5 = new long[] { 0L, -1L, -1L, -1L };
        jjbitVec6 = new long[] { -1L, -1L, 65535L, 0L };
        jjbitVec7 = new long[] { -1L, -1L, 0L, 0L };
        jjbitVec8 = new long[] { 70368744177663L, 0L, 0L, 0L };
        jjnextStates = new int[] { 8, 9, 10, 15, 16, 28, 29, 31, 32, 33, 20, 21, 23, 24, 25, 20, 21, 23, 28, 29, 31, 3, 4, 13, 14, 17, 18, 24, 25, 32, 33 };
        jjstrLiteralImages = new String[] { "", null, "${", null, null, null, null, null, null, null, null, null, "true", "false", "null", "}", ".", ">", "gt", "<", "lt", "==", "eq", "<=", "le", ">=", "ge", "!=", "ne", "(", ")", ",", ":", "[", "]", "+", "-", "*", "/", "div", "%", "mod", "not", "!", "and", "&&", "or", "||", "empty", null, null, null, null, null };
        lexStateNames = new String[] { "DEFAULT", "IN_EXPRESSION" };
        jjnewLexState = new int[] { -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { 10133099161582983L };
        jjtoSkip = new long[] { 120L };
    }
}
