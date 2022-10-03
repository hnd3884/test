package org.apache.lucene.queryparser.flexible.standard.parser;

import java.io.IOException;

public class StandardSyntaxParserTokenManager implements StandardSyntaxParserConstants
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
                return this.jjStopAtPos(0, 13);
            }
            case ')': {
                return this.jjStopAtPos(0, 14);
            }
            case '+': {
                return this.jjStopAtPos(0, 11);
            }
            case '-': {
                return this.jjStopAtPos(0, 12);
            }
            case ':': {
                return this.jjStopAtPos(0, 15);
            }
            case '<': {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_2(262144L);
            }
            case '=': {
                return this.jjStopAtPos(0, 16);
            }
            case '>': {
                this.jjmatchedKind = 19;
                return this.jjMoveStringLiteralDfa1_2(1048576L);
            }
            case '[': {
                return this.jjStopAtPos(0, 26);
            }
            case '^': {
                return this.jjStopAtPos(0, 21);
            }
            case '{': {
                return this.jjStopAtPos(0, 27);
            }
            default: {
                return this.jjMoveNfa_2(0, 0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_2(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_2(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case '=': {
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStopAtPos(1, 18);
                }
                if ((active0 & 0x100000L) != 0x0L) {
                    return this.jjStopAtPos(1, 20);
                }
                break;
            }
        }
        return this.jjStartNfa_2(0, active0);
    }
    
    private int jjMoveNfa_2(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 33;
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
                            if ((0x8BFF54F8FFFFD9FFL & l) != 0x0L) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(20, 21);
                            }
                            else if ((0x100002600L & l) != 0x0L) {
                                if (kind > 7) {
                                    kind = 7;
                                }
                            }
                            else if (this.curChar == '/') {
                                this.jjCheckNAddStates(0, 2);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddStates(3, 5);
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
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if (this.curChar == '\"' && kind > 22) {
                                kind = 22;
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((0x8BFF54F8FFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(20, 21);
                            continue;
                        }
                        case 20: {
                            if ((0x8BFF7CF8FFFFD9FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(20, 21);
                            continue;
                        }
                        case 25: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjAddStates(6, 7);
                            continue;
                        }
                        case 26: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(27);
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 28:
                        case 30: {
                            if (this.curChar == '/') {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if ((0xFFFF7FFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 2);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if (this.curChar == '/' && kind > 25) {
                                kind = 25;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 17: {
                            this.jjCheckNAddStates(3, 5);
                            continue;
                        }
                        case 22: {
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(20, 21);
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
                            if ((0x97FFFFFF87FFFFFFL & l) != 0x0L) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(20, 21);
                            }
                            else if (this.curChar == '~') {
                                if (kind > 24) {
                                    kind = 24;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 25;
                            }
                            else if (this.curChar == '\\') {
                                this.jjCheckNAdd(22);
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
                        case 15: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 17;
                                continue;
                            }
                            continue;
                        }
                        case 19:
                        case 20: {
                            if ((0x97FFFFFF87FFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(20, 21);
                            continue;
                        }
                        case 21: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAddTwoStates(22, 22);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if (this.curChar == '\\') {
                                this.jjCheckNAdd(22);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if (this.curChar != '~') {
                                continue;
                            }
                            if (kind > 24) {
                                kind = 24;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 25;
                            continue;
                        }
                        case 31: {
                            if (this.curChar == '\\') {
                                this.jjstateSet[this.jjnewStateCnt++] = 30;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 17: {
                            this.jjCheckNAddStates(3, 5);
                            continue;
                        }
                        case 22: {
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(20, 21);
                            continue;
                        }
                        case 29: {
                            this.jjAddStates(0, 2);
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
                            if (jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                if (kind > 23) {
                                    kind = 23;
                                }
                                this.jjCheckNAddTwoStates(20, 21);
                                continue;
                            }
                            continue;
                        }
                        case 15:
                        case 17: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(3, 5);
                                continue;
                            }
                            continue;
                        }
                        case 19:
                        case 20: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(20, 21);
                            continue;
                        }
                        case 22: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 23) {
                                kind = 23;
                            }
                            this.jjCheckNAddTwoStates(20, 21);
                            continue;
                        }
                        case 29: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(0, 2);
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
            final int n2 = 33;
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
                            if (kind > 28) {
                                kind = 28;
                            }
                            this.jjAddStates(8, 9);
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
                            if (kind > 28) {
                                kind = 28;
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
                if ((active0 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 33;
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
                return this.jjMoveStringLiteralDfa1_1(536870912L);
            }
            case ']': {
                return this.jjStopAtPos(0, 30);
            }
            case '}': {
                return this.jjStopAtPos(0, 31);
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
                if ((active0 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_1(1, 29, 6);
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
                                if (kind > 33) {
                                    kind = 33;
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
                                this.jjCheckNAddStates(10, 12);
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddStates(10, 12);
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if (this.curChar == '\"' && kind > 32) {
                                kind = 32;
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 33) {
                                kind = 33;
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
                            if (kind > 33) {
                                kind = 33;
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
                            this.jjAddStates(10, 12);
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
                                if (kind > 33) {
                                    kind = 33;
                                }
                                this.jjCheckNAdd(6);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(10, 12);
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 33) {
                                kind = 33;
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
                return (StandardSyntaxParserTokenManager.jjbitVec0[i2] & l2) != 0x0L;
            }
            default: {
                return false;
            }
        }
    }
    
    private static final boolean jjCanMove_1(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (StandardSyntaxParserTokenManager.jjbitVec3[i2] & l2) != 0x0L;
            }
            default: {
                return (StandardSyntaxParserTokenManager.jjbitVec1[i1] & l1) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_2(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (StandardSyntaxParserTokenManager.jjbitVec3[i2] & l2) != 0x0L;
            }
            case 48: {
                return (StandardSyntaxParserTokenManager.jjbitVec1[i2] & l2) != 0x0L;
            }
            default: {
                return (StandardSyntaxParserTokenManager.jjbitVec4[i1] & l1) != 0x0L;
            }
        }
    }
    
    public StandardSyntaxParserTokenManager(final CharStream stream) {
        this.jjrounds = new int[33];
        this.jjstateSet = new int[66];
        this.curLexState = 2;
        this.defaultLexState = 2;
        this.input_stream = stream;
    }
    
    public StandardSyntaxParserTokenManager(final CharStream stream, final int lexState) {
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
        int i = 33;
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
        final String im = StandardSyntaxParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
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
            if ((StandardSyntaxParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                if (StandardSyntaxParserTokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = StandardSyntaxParserTokenManager.jjnewLexState[this.jjmatchedKind];
                }
                return matchedToken;
            }
            if (StandardSyntaxParserTokenManager.jjnewLexState[this.jjmatchedKind] == -1) {
                continue;
            }
            this.curLexState = StandardSyntaxParserTokenManager.jjnewLexState[this.jjmatchedKind];
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
            this.jjstateSet[this.jjnewStateCnt++] = StandardSyntaxParserTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(StandardSyntaxParserTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    static {
        jjbitVec0 = new long[] { 1L, 0L, 0L, 0L };
        jjbitVec1 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec3 = new long[] { 0L, 0L, -1L, -1L };
        jjbitVec4 = new long[] { -281474976710658L, -1L, -1L, -1L };
        jjnextStates = new int[] { 29, 31, 32, 15, 16, 18, 25, 26, 0, 1, 2, 4, 5 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, "+", "-", "(", ")", ":", "=", "<", "<=", ">", ">=", "^", null, null, null, null, "[", "{", null, "TO", "]", "}", null, null };
        lexStateNames = new String[] { "Boost", "Range", "DEFAULT" };
        jjnewLexState = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, 1, 1, 2, -1, 2, 2, -1, -1 };
        jjtoToken = new long[] { 17179868929L };
        jjtoSkip = new long[] { 128L };
    }
}
