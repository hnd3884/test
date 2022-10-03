package com.steadystate.css.parser;

import java.io.IOException;
import java.io.PrintStream;

public class SACParserCSS21TokenManager implements SACParserCSS21Constants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final long[] jjbitVec2;
    public static final String[] jjstrLiteralImages;
    static final int[] jjnextStates;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    public static final String[] lexStateNames;
    public static final int[] jjnewLexState;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    static final long[] jjtoSpecial;
    static final long[] jjtoMore;
    protected CharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    private final StringBuilder jjimage;
    private StringBuilder image;
    private int jjimageLen;
    private int lengthOfMatch;
    protected int curChar;
    
    public void setDebugStream(final PrintStream ds) {
        this.debugStream = ds;
    }
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0, final long active1) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x4000000000000L) != 0x0L) {
                    return 1199;
                }
                if ((active0 & 0x80080000000000L) != 0x0L) {
                    return 763;
                }
                if ((active0 & 0x40000L) != 0x0L || (active1 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 0;
                    return 1200;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x40000L) != 0x0L || (active1 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 1;
                    return 1200;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x40000L) != 0x0L || (active1 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 2;
                    return 1200;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x40000L) != 0x0L || (active1 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 3;
                    return 1200;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x40000L) != 0x0L || (active1 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 4;
                    return 1200;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x40000L) != 0x0L || (active1 & 0x4000000L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 5;
                    return 1200;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_0(final int pos, final long active0, final long active1) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1), pos + 1);
    }
    
    private int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case 40: {
                return this.jjStopAtPos(0, 48);
            }
            case 41: {
                return this.jjStopAtPos(0, 49);
            }
            case 42: {
                return this.jjStopAtPos(0, 53);
            }
            case 45: {
                this.jjmatchedKind = 55;
                this.jjmatchedPos = 0;
                return this.jjMoveStringLiteralDfa1_0(8796093022208L, 0L);
            }
            case 46: {
                return this.jjStartNfaWithStates_0(0, 50, 1199);
            }
            case 47: {
                this.jjmatchedKind = 54;
                this.jjmatchedPos = 0;
                return this.jjMoveStringLiteralDfa1_0(8L, 0L);
            }
            case 58: {
                return this.jjStopAtPos(0, 52);
            }
            case 59: {
                return this.jjStopAtPos(0, 51);
            }
            case 60: {
                return this.jjMoveStringLiteralDfa1_0(4398046511104L, 0L);
            }
            case 61: {
                return this.jjStopAtPos(0, 56);
            }
            case 91: {
                return this.jjStopAtPos(0, 57);
            }
            case 93: {
                return this.jjStopAtPos(0, 58);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa1_0(262144L, 0L);
            }
            case 80:
            case 112: {
                return this.jjMoveStringLiteralDfa1_0(0L, 67108864L);
            }
            case 124: {
                return this.jjMoveStringLiteralDfa1_0(35184372088832L, 0L);
            }
            case 125: {
                return this.jjStopAtPos(0, 47);
            }
            case 126: {
                return this.jjMoveStringLiteralDfa1_0(17592186044416L, 0L);
            }
            default: {
                return this.jjMoveNfa_0(0, 0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_0(final long active0, final long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0, active1);
            return 1;
        }
        switch (this.curChar) {
            case 33: {
                return this.jjMoveStringLiteralDfa2_0(active0, 4398046511104L, active1, 0L);
            }
            case 42: {
                if ((active0 & 0x8L) != 0x0L) {
                    return this.jjStopAtPos(1, 3);
                }
                break;
            }
            case 45: {
                return this.jjMoveStringLiteralDfa2_0(active0, 8796093022208L, active1, 0L);
            }
            case 61: {
                if ((active0 & 0x100000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 44);
                }
                if ((active0 & 0x200000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 45);
                }
                break;
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa2_0(active0, 262144L, active1, 0L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 67108864L);
            }
        }
        return this.jjStartNfa_0(0, active0, active1);
    }
    
    private int jjMoveStringLiteralDfa2_0(final long old0, long active0, final long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0x0L) {
            return this.jjStartNfa_0(0, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0, active1);
            return 2;
        }
        switch (this.curChar) {
            case 45: {
                return this.jjMoveStringLiteralDfa3_0(active0, 4398046511104L, active1, 0L);
            }
            case 62: {
                if ((active0 & 0x80000000000L) != 0x0L) {
                    return this.jjStopAtPos(2, 43);
                }
                break;
            }
            case 72:
            case 104: {
                return this.jjMoveStringLiteralDfa3_0(active0, 262144L, active1, 0L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 67108864L);
            }
        }
        return this.jjStartNfa_0(1, active0, active1);
    }
    
    private int jjMoveStringLiteralDfa3_0(final long old0, long active0, final long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0x0L) {
            return this.jjStartNfa_0(1, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0, active1);
            return 3;
        }
        switch (this.curChar) {
            case 45: {
                if ((active0 & 0x40000000000L) != 0x0L) {
                    return this.jjStopAtPos(3, 42);
                }
                break;
            }
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa4_0(active0, 262144L, active1, 0L);
            }
            case 71:
            case 103: {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 67108864L);
            }
        }
        return this.jjStartNfa_0(2, active0, active1);
    }
    
    private int jjMoveStringLiteralDfa4_0(final long old0, long active0, final long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0x0L) {
            return this.jjStartNfa_0(2, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0, active1);
            return 4;
        }
        switch (this.curChar) {
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 67108864L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa5_0(active0, 262144L, active1, 0L);
            }
            default: {
                return this.jjStartNfa_0(3, active0, active1);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa5_0(final long old0, long active0, final long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0x0L) {
            return this.jjStartNfa_0(3, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0, active1);
            return 5;
        }
        switch (this.curChar) {
            case 68:
            case 100: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 67108864L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa6_0(active0, 262144L, active1, 0L);
            }
            default: {
                return this.jjStartNfa_0(4, active0, active1);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa6_0(final long old0, long active0, final long old1, long active1) {
        if (((active0 &= old0) | (active1 &= old1)) == 0x0L) {
            return this.jjStartNfa_0(4, old0, old1);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0, active1);
            return 6;
        }
        switch (this.curChar) {
            case 58: {
                if ((active1 & 0x4000000L) != 0x0L) {
                    return this.jjStopAtPos(6, 90);
                }
                break;
            }
            case 84:
            case 116: {
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 18, 1200);
                }
                break;
            }
        }
        return this.jjStartNfa_0(5, active0, active1);
    }
    
    private int jjStartNfaWithStates_0(final int pos, final int kind, final int state) {
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
    
    private int jjMoveNfa_0(final int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 1199;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar >= 64) {
                if (this.curChar >= 128) {
                    final int hiByte = this.curChar >> 8;
                    final int i2 = hiByte >> 6;
                    final long l1 = 1L << (hiByte & 0x3F);
                    final int i3 = (this.curChar & 0xFF) >> 6;
                    final long l2 = 1L << (this.curChar & 0x3F);
                    do {
                        switch (this.jjstateSet[--i]) {
                            case 763: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddTwoStates(764, 765);
                                }
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 1200: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddTwoStates(764, 765);
                                }
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 0: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddStates(1477, 1481);
                                    break;
                                }
                                break;
                            }
                            case 1:
                            case 7: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(183, 185);
                                break;
                            }
                            case 23:
                            case 29: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(180, 182);
                                break;
                            }
                            case 49:
                            case 51: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 62) {
                                        kind = 62;
                                    }
                                    this.jjCheckNAddTwoStates(49, 50);
                                    break;
                                }
                                break;
                            }
                            case 71: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjAddStates(1511, 1512);
                                break;
                            }
                            case 73:
                            case 74: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(74, 75);
                                break;
                            }
                            case 187:
                            case 191: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(375, 378);
                                break;
                            }
                            case 208:
                            case 214: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(394, 396);
                                break;
                            }
                            case 230:
                            case 236: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(412, 414);
                                break;
                            }
                            case 729:
                            case 730:
                            case 732: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 84) {
                                        kind = 84;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                    break;
                                }
                                break;
                            }
                            case 764:
                            case 766: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddTwoStates(764, 765);
                                    break;
                                }
                                break;
                            }
                            case 782:
                            case 786: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 783: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 1064:
                            case 1065:
                            case 1067: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 88) {
                                        kind = 88;
                                    }
                                    this.jjCheckNAddTwoStates(1065, 1066);
                                    break;
                                }
                                break;
                            }
                            default: {
                                if (i2 == 0) {
                                    break;
                                }
                                if (l1 == 0L) {
                                    break;
                                }
                                if (i3 == 0) {
                                    break;
                                }
                                if (l2 != 0L) {
                                    break;
                                }
                                break;
                            }
                        }
                    } while (i != startsAt);
                }
                else {
                    final long j = 1L << (this.curChar & 0x3F);
                    do {
                        switch (this.jjstateSet[--i]) {
                            case 763: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar == 92) {
                                        this.jjCheckNAddTwoStates(766, 817);
                                    }
                                }
                                else {
                                    this.jjCheckNAddStates(0, 2);
                                }
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddTwoStates(764, 765);
                                    break;
                                }
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(786, 803);
                                break;
                            }
                            case 1200: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar == 92) {
                                        this.jjCheckNAddTwoStates(766, 767);
                                    }
                                }
                                else {
                                    this.jjCheckNAddStates(0, 2);
                                }
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddTwoStates(764, 765);
                                    break;
                                }
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(786, 787);
                                break;
                            }
                            case 0: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar != 92) {
                                        if (this.curChar != 64) {
                                            if (this.curChar == 123) {
                                                if (kind > 46) {
                                                    kind = 46;
                                                }
                                            }
                                        }
                                        else {
                                            this.jjCheckNAddStates(1490, 1500);
                                        }
                                    }
                                    else {
                                        this.jjCheckNAddStates(1482, 1489);
                                    }
                                }
                                else {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddStates(1477, 1481);
                                }
                                if ((0x100000001000L & j) != 0x0L) {
                                    this.jjCheckNAddTwoStates(255, 281);
                                    break;
                                }
                                if ((0x20000000200000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 252;
                                break;
                            }
                            case 1: {
                                if ((0xFFFFFFFFEFFFFFFFL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(183, 185);
                                break;
                            }
                            case 3: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1501, 1504);
                                break;
                            }
                            case 7: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(183, 185);
                                break;
                            }
                            case 8: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(186, 195);
                                break;
                            }
                            case 9: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(196, 200);
                                break;
                            }
                            case 12:
                            case 14:
                            case 17:
                            case 21: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(9);
                                break;
                            }
                            case 13: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                                break;
                            }
                            case 15: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                                break;
                            }
                            case 16: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 17;
                                break;
                            }
                            case 18: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 19;
                                break;
                            }
                            case 19: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 20;
                                break;
                            }
                            case 20: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 21;
                                break;
                            }
                            case 23: {
                                if ((0xFFFFFFFFEFFFFFFFL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(180, 182);
                                break;
                            }
                            case 25: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1505, 1508);
                                break;
                            }
                            case 29: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(180, 182);
                                break;
                            }
                            case 30: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(201, 210);
                                break;
                            }
                            case 31: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(211, 215);
                                break;
                            }
                            case 34:
                            case 36:
                            case 39:
                            case 43: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(31);
                                break;
                            }
                            case 35: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 36;
                                break;
                            }
                            case 37: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 38;
                                break;
                            }
                            case 38: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 39;
                                break;
                            }
                            case 40: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 41;
                                break;
                            }
                            case 41: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 42;
                                break;
                            }
                            case 42: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 43;
                                break;
                            }
                            case 44: {
                                if (this.curChar != 123) {
                                    break;
                                }
                                if (kind <= 46) {
                                    break;
                                }
                                kind = 46;
                                break;
                            }
                            case 49: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 62) {
                                        kind = 62;
                                    }
                                    this.jjCheckNAddTwoStates(49, 50);
                                    break;
                                }
                                break;
                            }
                            case 50: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1509, 1510);
                                break;
                            }
                            case 51: {
                                if ((0xFFFFFF81FFFFFF81L & j) != 0x0L) {
                                    if (kind > 62) {
                                        kind = 62;
                                    }
                                    this.jjCheckNAddTwoStates(49, 50);
                                    break;
                                }
                                break;
                            }
                            case 52: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 62) {
                                        kind = 62;
                                    }
                                    this.jjCheckNAddStates(216, 224);
                                    break;
                                }
                                break;
                            }
                            case 53: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 62) {
                                        kind = 62;
                                    }
                                    this.jjCheckNAddStates(225, 228);
                                    break;
                                }
                                break;
                            }
                            case 57:
                            case 59:
                            case 62:
                            case 66: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(53);
                                break;
                            }
                            case 58: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 59;
                                break;
                            }
                            case 60: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 61;
                                break;
                            }
                            case 61: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 62;
                                break;
                            }
                            case 63: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 64;
                                break;
                            }
                            case 64: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 65;
                                break;
                            }
                            case 65: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 66;
                                break;
                            }
                            case 71: {
                                this.jjAddStates(1511, 1512);
                                break;
                            }
                            case 73:
                            case 74: {
                                this.jjCheckNAddTwoStates(74, 75);
                                break;
                            }
                            case 77: {
                                if ((0x20000000200L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(78, 163);
                                break;
                            }
                            case 78: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(79, 152);
                                break;
                            }
                            case 79: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(80, 141);
                                break;
                            }
                            case 80: {
                                if ((0x800000008000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(81, 130);
                                break;
                            }
                            case 81: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(82, 119);
                                break;
                            }
                            case 82: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1513, 1514);
                                break;
                            }
                            case 83: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(84, 97);
                                break;
                            }
                            case 84: {
                                if ((0x400000004000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(85, 86);
                                break;
                            }
                            case 85: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 67) {
                                    break;
                                }
                                kind = 67;
                                break;
                            }
                            case 86: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1515, 1518);
                                break;
                            }
                            case 97: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1519, 1522);
                                break;
                            }
                            case 98: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1523, 1526);
                                break;
                            }
                            case 108: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1527, 1529);
                                break;
                            }
                            case 119: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1530, 1533);
                                break;
                            }
                            case 130: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1534, 1537);
                                break;
                            }
                            case 141: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1538, 1541);
                                break;
                            }
                            case 152: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1542, 1545);
                                break;
                            }
                            case 163: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1546, 1549);
                                break;
                            }
                            case 164: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1550, 1553);
                                break;
                            }
                            case 174: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1554, 1557);
                                break;
                            }
                            case 185: {
                                if ((0x100000001000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 186;
                                break;
                            }
                            case 187: {
                                if ((0x7FFFFFFFEFFFFFFFL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(375, 378);
                                break;
                            }
                            case 190: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1558, 1559);
                                break;
                            }
                            case 191: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(375, 378);
                                break;
                            }
                            case 192: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(379, 388);
                                break;
                            }
                            case 193: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(389, 393);
                                break;
                            }
                            case 197:
                            case 199:
                            case 202:
                            case 206: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(193);
                                break;
                            }
                            case 198: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 199;
                                break;
                            }
                            case 200: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 201;
                                break;
                            }
                            case 201: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 202;
                                break;
                            }
                            case 203: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 204;
                                break;
                            }
                            case 204: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 205;
                                break;
                            }
                            case 205: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 206;
                                break;
                            }
                            case 208: {
                                if ((0xFFFFFFFFEFFFFFFFL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(394, 396);
                                break;
                            }
                            case 210: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1560, 1563);
                                break;
                            }
                            case 214: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(394, 396);
                                break;
                            }
                            case 215: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(397, 406);
                                break;
                            }
                            case 216: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(407, 411);
                                break;
                            }
                            case 219:
                            case 221:
                            case 224:
                            case 228: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(216);
                                break;
                            }
                            case 220: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 221;
                                break;
                            }
                            case 222: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 223;
                                break;
                            }
                            case 223: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 224;
                                break;
                            }
                            case 225: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 226;
                                break;
                            }
                            case 226: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 227;
                                break;
                            }
                            case 227: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 228;
                                break;
                            }
                            case 230: {
                                if ((0xFFFFFFFFEFFFFFFFL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(412, 414);
                                break;
                            }
                            case 232: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1564, 1567);
                                break;
                            }
                            case 236: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(412, 414);
                                break;
                            }
                            case 237: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(415, 424);
                                break;
                            }
                            case 238: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(425, 429);
                                break;
                            }
                            case 241:
                            case 243:
                            case 246:
                            case 250: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(238);
                                break;
                            }
                            case 242: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 243;
                                break;
                            }
                            case 244: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 245;
                                break;
                            }
                            case 245: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 246;
                                break;
                            }
                            case 247: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 248;
                                break;
                            }
                            case 248: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 249;
                                break;
                            }
                            case 249: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 250;
                                break;
                            }
                            case 252: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 185;
                                break;
                            }
                            case 253: {
                                if ((0x20000000200000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 252;
                                break;
                            }
                            case 254: {
                                if ((0x100000001000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(255, 281);
                                break;
                            }
                            case 255: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(256, 270);
                                break;
                            }
                            case 256: {
                                if ((0x400000004000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(257, 259);
                                break;
                            }
                            case 257: {
                                if ((0x8000000080L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 258;
                                break;
                            }
                            case 259: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1568, 1571);
                                break;
                            }
                            case 270: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1572, 1575);
                                break;
                            }
                            case 271: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1576, 1579);
                                break;
                            }
                            case 281: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1580, 1582);
                                break;
                            }
                            case 303: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(304, 305);
                                break;
                            }
                            case 304: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 68) {
                                    break;
                                }
                                kind = 68;
                                break;
                            }
                            case 305: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1583, 1586);
                                break;
                            }
                            case 306: {
                                if ((0x1000000010L & j) != 0x0L) {
                                    if (kind > 68) {
                                        kind = 68;
                                    }
                                    this.jjAddStates(1587, 1588);
                                    break;
                                }
                                break;
                            }
                            case 316: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1589, 1591);
                                break;
                            }
                            case 328: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(329, 330);
                                break;
                            }
                            case 329: {
                                if ((0x100000001000000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 69) {
                                    break;
                                }
                                kind = 69;
                                break;
                            }
                            case 330: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1592, 1595);
                                break;
                            }
                            case 341: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1596, 1598);
                                break;
                            }
                            case 353: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(354, 355);
                                break;
                            }
                            case 354: {
                                if ((0x100000001000000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 70) {
                                    break;
                                }
                                kind = 70;
                                break;
                            }
                            case 355: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1599, 1602);
                                break;
                            }
                            case 366: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1603, 1606);
                                break;
                            }
                            case 378: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(379, 380);
                                break;
                            }
                            case 379: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 71) {
                                    break;
                                }
                                kind = 71;
                                break;
                            }
                            case 380: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1607, 1610);
                                break;
                            }
                            case 381: {
                                if ((0x1000000010L & j) != 0x0L) {
                                    if (kind > 71) {
                                        kind = 71;
                                    }
                                    this.jjAddStates(1611, 1612);
                                    break;
                                }
                                break;
                            }
                            case 391: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1613, 1615);
                                break;
                            }
                            case 403: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(404, 405);
                                break;
                            }
                            case 404: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 72) {
                                    break;
                                }
                                kind = 72;
                                break;
                            }
                            case 405: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1616, 1619);
                                break;
                            }
                            case 406: {
                                if ((0x1000000010L & j) != 0x0L) {
                                    if (kind > 72) {
                                        kind = 72;
                                    }
                                    this.jjAddStates(1620, 1621);
                                    break;
                                }
                                break;
                            }
                            case 416: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1622, 1625);
                                break;
                            }
                            case 417: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1626, 1629);
                                break;
                            }
                            case 428: {
                                if ((0x20000000200L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(429, 430);
                                break;
                            }
                            case 429: {
                                if ((0x400000004000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 73) {
                                    break;
                                }
                                kind = 73;
                                break;
                            }
                            case 430: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1630, 1633);
                                break;
                            }
                            case 431: {
                                if ((0x2000000020L & j) != 0x0L) {
                                    if (kind > 73) {
                                        kind = 73;
                                    }
                                    this.jjAddStates(1634, 1635);
                                    break;
                                }
                                break;
                            }
                            case 441: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1636, 1639);
                                break;
                            }
                            case 453: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(454, 455);
                                break;
                            }
                            case 454: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 74) {
                                    break;
                                }
                                kind = 74;
                                break;
                            }
                            case 455: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1640, 1643);
                                break;
                            }
                            case 466: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1644, 1647);
                                break;
                            }
                            case 478: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1648, 1649);
                                break;
                            }
                            case 479: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 75) {
                                    break;
                                }
                                kind = 75;
                                break;
                            }
                            case 480: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1650, 1652);
                                break;
                            }
                            case 491: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1653, 1656);
                                break;
                            }
                            case 503: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1657, 1658);
                                break;
                            }
                            case 504: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(505, 506);
                                break;
                            }
                            case 505: {
                                if ((0x8000000080L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 76) {
                                    break;
                                }
                                kind = 76;
                                break;
                            }
                            case 506: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1659, 1662);
                                break;
                            }
                            case 517: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1663, 1665);
                                break;
                            }
                            case 528: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1666, 1668);
                                break;
                            }
                            case 540: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1669, 1670);
                                break;
                            }
                            case 541: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1671, 1672);
                                break;
                            }
                            case 542: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 77) {
                                    break;
                                }
                                kind = 77;
                                break;
                            }
                            case 543: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1673, 1675);
                                break;
                            }
                            case 554: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1676, 1678);
                                break;
                            }
                            case 565: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1679, 1682);
                                break;
                            }
                            case 577: {
                                if ((0x8000000080L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(578, 603);
                                break;
                            }
                            case 578: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1683, 1684);
                                break;
                            }
                            case 579: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1685, 1686);
                                break;
                            }
                            case 580: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 78) {
                                    break;
                                }
                                kind = 78;
                                break;
                            }
                            case 581: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1687, 1689);
                                break;
                            }
                            case 592: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1690, 1692);
                                break;
                            }
                            case 603: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1693, 1696);
                                break;
                            }
                            case 614: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1697, 1700);
                                break;
                            }
                            case 626: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(627, 628);
                                break;
                            }
                            case 627: {
                                if ((0x8000000080000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 79) {
                                    break;
                                }
                                kind = 79;
                                break;
                            }
                            case 628: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1701, 1704);
                                break;
                            }
                            case 639: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1705, 1708);
                                break;
                            }
                            case 640: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1709, 1712);
                                break;
                            }
                            case 651: {
                                if ((0x8000000080000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 80) {
                                    break;
                                }
                                kind = 80;
                                break;
                            }
                            case 652: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1713, 1716);
                                break;
                            }
                            case 664: {
                                if ((0x10000000100L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(665, 666);
                                break;
                            }
                            case 665: {
                                if ((0x400000004000000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 81) {
                                    break;
                                }
                                kind = 81;
                                break;
                            }
                            case 666: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1717, 1720);
                                break;
                            }
                            case 667: {
                                if ((0x200000002L & j) != 0x0L) {
                                    if (kind > 81) {
                                        kind = 81;
                                    }
                                    this.jjAddStates(1721, 1722);
                                    break;
                                }
                                break;
                            }
                            case 677: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1723, 1726);
                                break;
                            }
                            case 689: {
                                if ((0x80000000800L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(690, 703);
                                break;
                            }
                            case 690: {
                                if ((0x10000000100L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(691, 692);
                                break;
                            }
                            case 691: {
                                if ((0x400000004000000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 82) {
                                    break;
                                }
                                kind = 82;
                                break;
                            }
                            case 692: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1727, 1730);
                                break;
                            }
                            case 693: {
                                if ((0x200000002L & j) != 0x0L) {
                                    if (kind > 82) {
                                        kind = 82;
                                    }
                                    this.jjAddStates(1731, 1732);
                                    break;
                                }
                                break;
                            }
                            case 703: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1733, 1736);
                                break;
                            }
                            case 714: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1737, 1740);
                                break;
                            }
                            case 715: {
                                if ((0x400000004L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1741, 1744);
                                break;
                            }
                            case 729:
                            case 730: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 84) {
                                        kind = 84;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                    break;
                                }
                                break;
                            }
                            case 731: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(732, 733);
                                break;
                            }
                            case 732: {
                                if ((0xFFFFFF81FFFFFF81L & j) != 0x0L) {
                                    if (kind > 84) {
                                        kind = 84;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                    break;
                                }
                                break;
                            }
                            case 733: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 84) {
                                        kind = 84;
                                    }
                                    this.jjCheckNAddStates(970, 978);
                                    break;
                                }
                                break;
                            }
                            case 734: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 84) {
                                        kind = 84;
                                    }
                                    this.jjCheckNAddStates(979, 982);
                                    break;
                                }
                                break;
                            }
                            case 738:
                            case 740:
                            case 743:
                            case 747: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(734);
                                break;
                            }
                            case 739: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 740;
                                break;
                            }
                            case 741: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 742;
                                break;
                            }
                            case 742: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 743;
                                break;
                            }
                            case 744: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 745;
                                break;
                            }
                            case 745: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 746;
                                break;
                            }
                            case 746: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 747;
                                break;
                            }
                            case 748: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(732, 749);
                                break;
                            }
                            case 749: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 84) {
                                        kind = 84;
                                    }
                                    this.jjCheckNAddStates(983, 991);
                                    break;
                                }
                                break;
                            }
                            case 750: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 84) {
                                        kind = 84;
                                    }
                                    this.jjCheckNAddStates(992, 995);
                                    break;
                                }
                                break;
                            }
                            case 752:
                            case 754:
                            case 757:
                            case 761: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(750);
                                break;
                            }
                            case 753: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 754;
                                break;
                            }
                            case 755: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 756;
                                break;
                            }
                            case 756: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 757;
                                break;
                            }
                            case 758: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 759;
                                break;
                            }
                            case 759: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 760;
                                break;
                            }
                            case 760: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 761;
                                break;
                            }
                            case 764: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddTwoStates(764, 765);
                                    break;
                                }
                                break;
                            }
                            case 765: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(766, 767);
                                break;
                            }
                            case 766: {
                                if ((0xFFFFFF81FFFFFF81L & j) != 0x0L) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddTwoStates(764, 765);
                                    break;
                                }
                                break;
                            }
                            case 767: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddStates(996, 1004);
                                    break;
                                }
                                break;
                            }
                            case 768: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddStates(1005, 1008);
                                    break;
                                }
                                break;
                            }
                            case 772:
                            case 774:
                            case 777:
                            case 781: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(768);
                                break;
                            }
                            case 773: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 774;
                                break;
                            }
                            case 775: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 776;
                                break;
                            }
                            case 776: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 777;
                                break;
                            }
                            case 778: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 779;
                                break;
                            }
                            case 779: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 780;
                                break;
                            }
                            case 780: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 781;
                                break;
                            }
                            case 782: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 783: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 785: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(786, 787);
                                break;
                            }
                            case 786: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 787: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1009, 1018);
                                break;
                            }
                            case 788: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1019, 1023);
                                break;
                            }
                            case 792:
                            case 794:
                            case 797:
                            case 801: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(788);
                                break;
                            }
                            case 793: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 794;
                                break;
                            }
                            case 795: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 796;
                                break;
                            }
                            case 796: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 797;
                                break;
                            }
                            case 798: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 799;
                                break;
                            }
                            case 799: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 800;
                                break;
                            }
                            case 800: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 801;
                                break;
                            }
                            case 802: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(786, 803);
                                break;
                            }
                            case 803: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1024, 1033);
                                break;
                            }
                            case 804: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1034, 1038);
                                break;
                            }
                            case 806:
                            case 808:
                            case 811:
                            case 815: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(804);
                                break;
                            }
                            case 807: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 808;
                                break;
                            }
                            case 809: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 810;
                                break;
                            }
                            case 810: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 811;
                                break;
                            }
                            case 812: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 813;
                                break;
                            }
                            case 813: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 814;
                                break;
                            }
                            case 814: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 815;
                                break;
                            }
                            case 816: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(766, 817);
                                break;
                            }
                            case 817: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddStates(1039, 1047);
                                    break;
                                }
                                break;
                            }
                            case 818: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddStates(1048, 1051);
                                    break;
                                }
                                break;
                            }
                            case 820:
                            case 822:
                            case 825:
                            case 829: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(818);
                                break;
                            }
                            case 821: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 822;
                                break;
                            }
                            case 823: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 824;
                                break;
                            }
                            case 824: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 825;
                                break;
                            }
                            case 826: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 827;
                                break;
                            }
                            case 827: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 828;
                                break;
                            }
                            case 828: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 829;
                                break;
                            }
                            case 830: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 19) {
                                        kind = 19;
                                    }
                                    this.jjCheckNAddStates(1477, 1481);
                                    break;
                                }
                                break;
                            }
                            case 831: {
                                if (this.curChar != 64) {
                                    break;
                                }
                                this.jjCheckNAddStates(1490, 1500);
                                break;
                            }
                            case 832: {
                                if ((0x20000000200L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(833, 882);
                                break;
                            }
                            case 833: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(834, 871);
                                break;
                            }
                            case 834: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(835, 860);
                                break;
                            }
                            case 835: {
                                if ((0x800000008000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(836, 849);
                                break;
                            }
                            case 836: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(837, 838);
                                break;
                            }
                            case 837: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 63) {
                                    break;
                                }
                                kind = 63;
                                break;
                            }
                            case 838: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1745, 1748);
                                break;
                            }
                            case 849: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1749, 1752);
                                break;
                            }
                            case 860: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1753, 1756);
                                break;
                            }
                            case 871: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1757, 1760);
                                break;
                            }
                            case 882: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1761, 1764);
                                break;
                            }
                            case 883: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1765, 1768);
                                break;
                            }
                            case 893: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1769, 1770);
                                break;
                            }
                            case 894: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(895, 908);
                                break;
                            }
                            case 895: {
                                if ((0x8000000080L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1771, 1772);
                                break;
                            }
                            case 896: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 64) {
                                    break;
                                }
                                kind = 64;
                                break;
                            }
                            case 897: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1773, 1775);
                                break;
                            }
                            case 908: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1776, 1779);
                                break;
                            }
                            case 919: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1780, 1782);
                                break;
                            }
                            case 930: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(931, 968);
                                break;
                            }
                            case 931: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1783, 1784);
                                break;
                            }
                            case 932: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(933, 946);
                                break;
                            }
                            case 933: {
                                if ((0x20000000200L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1785, 1786);
                                break;
                            }
                            case 934: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 65) {
                                    break;
                                }
                                kind = 65;
                                break;
                            }
                            case 935: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1787, 1789);
                                break;
                            }
                            case 946: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1790, 1793);
                                break;
                            }
                            case 957: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1794, 1796);
                                break;
                            }
                            case 968: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1797, 1799);
                                break;
                            }
                            case 979: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(980, 1041);
                                break;
                            }
                            case 980: {
                                if ((0x10000000100L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1800, 1801);
                                break;
                            }
                            case 981: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(982, 1019);
                                break;
                            }
                            case 982: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(983, 1008);
                                break;
                            }
                            case 983: {
                                if ((0x8000000080000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1802, 1803);
                                break;
                            }
                            case 984: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(985, 986);
                                break;
                            }
                            case 985: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 66) {
                                    break;
                                }
                                kind = 66;
                                break;
                            }
                            case 986: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1804, 1807);
                                break;
                            }
                            case 997: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1808, 1810);
                                break;
                            }
                            case 1008: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1811, 1814);
                                break;
                            }
                            case 1019: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1815, 1818);
                                break;
                            }
                            case 1030: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1819, 1821);
                                break;
                            }
                            case 1041: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1822, 1825);
                                break;
                            }
                            case 1052: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1826, 1828);
                                break;
                            }
                            case 1064:
                            case 1065: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 88) {
                                        kind = 88;
                                    }
                                    this.jjCheckNAddTwoStates(1065, 1066);
                                    break;
                                }
                                break;
                            }
                            case 1066: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(1067, 1068);
                                break;
                            }
                            case 1067: {
                                if ((0xFFFFFF81FFFFFF81L & j) != 0x0L) {
                                    if (kind > 88) {
                                        kind = 88;
                                    }
                                    this.jjCheckNAddTwoStates(1065, 1066);
                                    break;
                                }
                                break;
                            }
                            case 1068: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 88) {
                                        kind = 88;
                                    }
                                    this.jjCheckNAddStates(1346, 1354);
                                    break;
                                }
                                break;
                            }
                            case 1069: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 88) {
                                        kind = 88;
                                    }
                                    this.jjCheckNAddStates(1355, 1358);
                                    break;
                                }
                                break;
                            }
                            case 1073:
                            case 1075:
                            case 1078:
                            case 1082: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(1069);
                                break;
                            }
                            case 1074: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1075;
                                break;
                            }
                            case 1076: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1077;
                                break;
                            }
                            case 1077: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1078;
                                break;
                            }
                            case 1079: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1080;
                                break;
                            }
                            case 1080: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1081;
                                break;
                            }
                            case 1081: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1082;
                                break;
                            }
                            case 1083: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(1067, 1084);
                                break;
                            }
                            case 1084: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 88) {
                                        kind = 88;
                                    }
                                    this.jjCheckNAddStates(1359, 1367);
                                    break;
                                }
                                break;
                            }
                            case 1085: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 88) {
                                        kind = 88;
                                    }
                                    this.jjCheckNAddStates(1368, 1371);
                                    break;
                                }
                                break;
                            }
                            case 1087:
                            case 1089:
                            case 1092:
                            case 1096: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(1085);
                                break;
                            }
                            case 1088: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1089;
                                break;
                            }
                            case 1090: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1091;
                                break;
                            }
                            case 1091: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1092;
                                break;
                            }
                            case 1093: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1094;
                                break;
                            }
                            case 1094: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1095;
                                break;
                            }
                            case 1095: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1096;
                                break;
                            }
                            case 1097: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1829, 1832);
                                break;
                            }
                            case 1098: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1833, 1836);
                                break;
                            }
                            case 1108: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1837, 1840);
                                break;
                            }
                            case 1119: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1841, 1844);
                                break;
                            }
                            case 1130: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1482, 1489);
                                break;
                            }
                            case 1131: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1845, 1848);
                                break;
                            }
                        }
                    } while (i != startsAt);
                }
            }
            else {
                final long j = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 1200: {
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                if (this.curChar == 40) {
                                    if (kind > 87) {
                                        kind = 87;
                                    }
                                }
                            }
                            else {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                break;
                            }
                            if (kind > 19) {
                                kind = 19;
                            }
                            this.jjCheckNAddTwoStates(764, 765);
                            break;
                        }
                        case 1199: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(3, 6);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddTwoStates(725, 726);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(7, 9);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(10, 12);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(13, 15);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(16, 18);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(19, 21);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(22, 24);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(25, 27);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(28, 30);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(31, 33);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(34, 36);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(37, 39);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(40, 42);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(43, 45);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(46, 48);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(49, 51);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 21) {
                                    kind = 21;
                                }
                                this.jjCheckNAdd(301);
                            }
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            if (kind > 17) {
                                kind = 17;
                            }
                            this.jjCheckNAdd(300);
                            break;
                        }
                        case 0: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 17) {
                                    kind = 17;
                                }
                                this.jjCheckNAddStates(52, 142);
                                break;
                            }
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAddStates(143, 152);
                                break;
                            }
                            if (this.curChar == 45) {
                                this.jjAddStates(153, 156);
                                break;
                            }
                            if (this.curChar == 46) {
                                this.jjCheckNAddStates(157, 175);
                                break;
                            }
                            if (this.curChar == 33) {
                                this.jjCheckNAddStates(176, 179);
                                break;
                            }
                            if (this.curChar == 35) {
                                this.jjCheckNAddTwoStates(49, 50);
                                break;
                            }
                            if (this.curChar != 44) {
                                if (this.curChar != 62) {
                                    if (this.curChar != 43) {
                                        if (this.curChar == 39) {
                                            this.jjCheckNAddStates(180, 182);
                                            break;
                                        }
                                        if (this.curChar != 34) {
                                            break;
                                        }
                                        this.jjCheckNAddStates(183, 185);
                                        break;
                                    }
                                    else {
                                        if (kind <= 59) {
                                            break;
                                        }
                                        kind = 59;
                                        break;
                                    }
                                }
                                else {
                                    if (kind <= 60) {
                                        break;
                                    }
                                    kind = 60;
                                    break;
                                }
                            }
                            else {
                                if (kind <= 61) {
                                    break;
                                }
                                kind = 61;
                                break;
                            }
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFBFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(183, 185);
                            break;
                        }
                        case 2: {
                            if (this.curChar != 34) {
                                break;
                            }
                            if (kind <= 22) {
                                break;
                            }
                            kind = 22;
                            break;
                        }
                        case 4: {
                            if ((0x3400L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(183, 185);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(183, 185);
                            break;
                        }
                        case 6:
                        case 10: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(5);
                            break;
                        }
                        case 7: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(183, 185);
                            break;
                        }
                        case 8: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(186, 195);
                            break;
                        }
                        case 9: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(196, 200);
                            break;
                        }
                        case 11: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(183, 185);
                            break;
                        }
                        case 12:
                        case 14:
                        case 17:
                        case 21: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(9);
                            break;
                        }
                        case 13: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            break;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 16;
                            break;
                        }
                        case 16: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            break;
                        }
                        case 18: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 19;
                            break;
                        }
                        case 19: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 20;
                            break;
                        }
                        case 20: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 21;
                            break;
                        }
                        case 22: {
                            if (this.curChar != 39) {
                                break;
                            }
                            this.jjCheckNAddStates(180, 182);
                            break;
                        }
                        case 23: {
                            if ((0xFFFFFF7FFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(180, 182);
                            break;
                        }
                        case 24: {
                            if (this.curChar != 39) {
                                break;
                            }
                            if (kind <= 22) {
                                break;
                            }
                            kind = 22;
                            break;
                        }
                        case 26: {
                            if ((0x3400L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(180, 182);
                            break;
                        }
                        case 27: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(180, 182);
                            break;
                        }
                        case 28:
                        case 32: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(27);
                            break;
                        }
                        case 29: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(180, 182);
                            break;
                        }
                        case 30: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(201, 210);
                            break;
                        }
                        case 31: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(211, 215);
                            break;
                        }
                        case 33: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(180, 182);
                            break;
                        }
                        case 34:
                        case 36:
                        case 39:
                        case 43: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(31);
                            break;
                        }
                        case 35: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 36;
                            break;
                        }
                        case 37: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 38;
                            break;
                        }
                        case 38: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 39;
                            break;
                        }
                        case 40: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 41;
                            break;
                        }
                        case 41: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 42;
                            break;
                        }
                        case 42: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 43;
                            break;
                        }
                        case 45: {
                            if (this.curChar != 43) {
                                break;
                            }
                            if (kind <= 59) {
                                break;
                            }
                            kind = 59;
                            break;
                        }
                        case 46: {
                            if (this.curChar != 62) {
                                break;
                            }
                            if (kind <= 60) {
                                break;
                            }
                            kind = 60;
                            break;
                        }
                        case 47: {
                            if (this.curChar != 44) {
                                break;
                            }
                            if (kind <= 61) {
                                break;
                            }
                            kind = 61;
                            break;
                        }
                        case 48: {
                            if (this.curChar != 35) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(49, 50);
                            break;
                        }
                        case 49: {
                            if ((0x3FF200000000000L & j) != 0x0L) {
                                if (kind > 62) {
                                    kind = 62;
                                }
                                this.jjCheckNAddTwoStates(49, 50);
                                break;
                            }
                            break;
                        }
                        case 51: {
                            if ((0xFC00FFFFFFFFCBFFL & j) != 0x0L) {
                                if (kind > 62) {
                                    kind = 62;
                                }
                                this.jjCheckNAddTwoStates(49, 50);
                                break;
                            }
                            break;
                        }
                        case 52: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 62) {
                                    kind = 62;
                                }
                                this.jjCheckNAddStates(216, 224);
                                break;
                            }
                            break;
                        }
                        case 53: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 62) {
                                    kind = 62;
                                }
                                this.jjCheckNAddStates(225, 228);
                                break;
                            }
                            break;
                        }
                        case 54: {
                            if (this.curChar == 10) {
                                if (kind > 62) {
                                    kind = 62;
                                }
                                this.jjCheckNAddTwoStates(49, 50);
                                break;
                            }
                            break;
                        }
                        case 55: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 54;
                            break;
                        }
                        case 56: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 62) {
                                    kind = 62;
                                }
                                this.jjCheckNAddTwoStates(49, 50);
                                break;
                            }
                            break;
                        }
                        case 57:
                        case 59:
                        case 62:
                        case 66: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(53);
                            break;
                        }
                        case 58: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 59;
                            break;
                        }
                        case 60: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 61;
                            break;
                        }
                        case 61: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 62;
                            break;
                        }
                        case 63: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 64;
                            break;
                        }
                        case 64: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 65;
                            break;
                        }
                        case 65: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 66;
                            break;
                        }
                        case 67: {
                            if (this.curChar != 33) {
                                break;
                            }
                            this.jjCheckNAddStates(176, 179);
                            break;
                        }
                        case 68: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(176, 179);
                            break;
                        }
                        case 69: {
                            if (this.curChar != 47) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 70;
                            break;
                        }
                        case 70: {
                            if (this.curChar != 42) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(71, 72);
                            break;
                        }
                        case 71: {
                            if ((0xFFFFFBFFFFFFFFFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(71, 72);
                            break;
                        }
                        case 72: {
                            if (this.curChar != 42) {
                                break;
                            }
                            this.jjCheckNAddStates(229, 231);
                            break;
                        }
                        case 73: {
                            if ((0xFFFF7BFFFFFFFFFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(74, 75);
                            break;
                        }
                        case 74: {
                            if ((0xFFFFFBFFFFFFFFFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(74, 75);
                            break;
                        }
                        case 75: {
                            if (this.curChar != 42) {
                                break;
                            }
                            this.jjCheckNAddStates(232, 234);
                            break;
                        }
                        case 76: {
                            if (this.curChar != 47) {
                                break;
                            }
                            this.jjCheckNAddStates(176, 179);
                            break;
                        }
                        case 87: {
                            if (this.curChar == 52) {
                                if (kind > 67) {
                                    kind = 67;
                                }
                                this.jjAddStates(235, 236);
                                break;
                            }
                            break;
                        }
                        case 88: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 67) {
                                break;
                            }
                            kind = 67;
                            break;
                        }
                        case 89: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 88;
                            break;
                        }
                        case 90: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 67) {
                                break;
                            }
                            kind = 67;
                            break;
                        }
                        case 91: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(87);
                            break;
                        }
                        case 92: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(87);
                            break;
                        }
                        case 93: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(237, 241);
                            break;
                        }
                        case 94: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(91, 92);
                            break;
                        }
                        case 95: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(242, 244);
                            break;
                        }
                        case 96: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(245, 248);
                            break;
                        }
                        case 99: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(85, 86);
                            break;
                        }
                        case 100: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 99;
                            break;
                        }
                        case 101: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(85, 86);
                            break;
                        }
                        case 102: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(98);
                            break;
                        }
                        case 103: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(98);
                            break;
                        }
                        case 104: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(249, 253);
                            break;
                        }
                        case 105: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(102, 103);
                            break;
                        }
                        case 106: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(254, 256);
                            break;
                        }
                        case 107: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(257, 260);
                            break;
                        }
                        case 109: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(261, 264);
                            break;
                        }
                        case 110: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(84, 97);
                            break;
                        }
                        case 111: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 110;
                            break;
                        }
                        case 112: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(84, 97);
                            break;
                        }
                        case 113: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(109);
                            break;
                        }
                        case 114: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(109);
                            break;
                        }
                        case 115: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(265, 269);
                            break;
                        }
                        case 116: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(113, 114);
                            break;
                        }
                        case 117: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(270, 272);
                            break;
                        }
                        case 118: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(273, 276);
                            break;
                        }
                        case 120: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAddStates(277, 280);
                            break;
                        }
                        case 121: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(83, 108);
                            break;
                        }
                        case 122: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 121;
                            break;
                        }
                        case 123: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(83, 108);
                            break;
                        }
                        case 124: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(120);
                            break;
                        }
                        case 125: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(120);
                            break;
                        }
                        case 126: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(281, 285);
                            break;
                        }
                        case 127: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(124, 125);
                            break;
                        }
                        case 128: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(286, 288);
                            break;
                        }
                        case 129: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(289, 292);
                            break;
                        }
                        case 131: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(293, 296);
                            break;
                        }
                        case 132: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(82, 119);
                            break;
                        }
                        case 133: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 132;
                            break;
                        }
                        case 134: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(82, 119);
                            break;
                        }
                        case 135: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(131);
                            break;
                        }
                        case 136: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(131);
                            break;
                        }
                        case 137: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(297, 301);
                            break;
                        }
                        case 138: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(135, 136);
                            break;
                        }
                        case 139: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(302, 304);
                            break;
                        }
                        case 140: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(305, 308);
                            break;
                        }
                        case 142: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(309, 312);
                            break;
                        }
                        case 143: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(81, 130);
                            break;
                        }
                        case 144: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 143;
                            break;
                        }
                        case 145: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(81, 130);
                            break;
                        }
                        case 146: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(142);
                            break;
                        }
                        case 147: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(142);
                            break;
                        }
                        case 148: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(313, 317);
                            break;
                        }
                        case 149: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(146, 147);
                            break;
                        }
                        case 150: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(318, 320);
                            break;
                        }
                        case 151: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(321, 324);
                            break;
                        }
                        case 153: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(325, 328);
                            break;
                        }
                        case 154: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(80, 141);
                            break;
                        }
                        case 155: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 154;
                            break;
                        }
                        case 156: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(80, 141);
                            break;
                        }
                        case 157: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(153);
                            break;
                        }
                        case 158: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(153);
                            break;
                        }
                        case 159: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(329, 333);
                            break;
                        }
                        case 160: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(157, 158);
                            break;
                        }
                        case 161: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(334, 336);
                            break;
                        }
                        case 162: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(337, 340);
                            break;
                        }
                        case 165: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(79, 152);
                            break;
                        }
                        case 166: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 165;
                            break;
                        }
                        case 167: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(79, 152);
                            break;
                        }
                        case 168: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(164);
                            break;
                        }
                        case 169: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(164);
                            break;
                        }
                        case 170: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(341, 345);
                            break;
                        }
                        case 171: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(168, 169);
                            break;
                        }
                        case 172: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(346, 348);
                            break;
                        }
                        case 173: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(349, 352);
                            break;
                        }
                        case 175: {
                            if (this.curChar != 57) {
                                break;
                            }
                            this.jjCheckNAddStates(353, 356);
                            break;
                        }
                        case 176: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(78, 163);
                            break;
                        }
                        case 177: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 176;
                            break;
                        }
                        case 178: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(78, 163);
                            break;
                        }
                        case 179: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(175);
                            break;
                        }
                        case 180: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(175);
                            break;
                        }
                        case 181: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(357, 361);
                            break;
                        }
                        case 182: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(179, 180);
                            break;
                        }
                        case 183: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(362, 364);
                            break;
                        }
                        case 184: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(365, 368);
                            break;
                        }
                        case 186: {
                            if (this.curChar != 40) {
                                break;
                            }
                            this.jjCheckNAddStates(369, 374);
                            break;
                        }
                        case 187: {
                            if ((0xFFFFFC7A00000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(375, 378);
                            break;
                        }
                        case 188: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(188, 189);
                            break;
                        }
                        case 189: {
                            if (this.curChar != 41) {
                                break;
                            }
                            if (kind <= 85) {
                                break;
                            }
                            kind = 85;
                            break;
                        }
                        case 191: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(375, 378);
                            break;
                        }
                        case 192: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(379, 388);
                            break;
                        }
                        case 193: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(389, 393);
                            break;
                        }
                        case 194: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(375, 378);
                            break;
                        }
                        case 195: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 194;
                            break;
                        }
                        case 196: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(375, 378);
                            break;
                        }
                        case 197:
                        case 199:
                        case 202:
                        case 206: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(193);
                            break;
                        }
                        case 198: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 199;
                            break;
                        }
                        case 200: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 201;
                            break;
                        }
                        case 201: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 202;
                            break;
                        }
                        case 203: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 204;
                            break;
                        }
                        case 204: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 205;
                            break;
                        }
                        case 205: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 206;
                            break;
                        }
                        case 207: {
                            if (this.curChar != 39) {
                                break;
                            }
                            this.jjCheckNAddStates(394, 396);
                            break;
                        }
                        case 208: {
                            if ((0xFFFFFF7FFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(394, 396);
                            break;
                        }
                        case 209: {
                            if (this.curChar != 39) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(188, 189);
                            break;
                        }
                        case 211: {
                            if ((0x3400L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(394, 396);
                            break;
                        }
                        case 212: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(394, 396);
                            break;
                        }
                        case 213:
                        case 217: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(212);
                            break;
                        }
                        case 214: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(394, 396);
                            break;
                        }
                        case 215: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(397, 406);
                            break;
                        }
                        case 216: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(407, 411);
                            break;
                        }
                        case 218: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(394, 396);
                            break;
                        }
                        case 219:
                        case 221:
                        case 224:
                        case 228: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(216);
                            break;
                        }
                        case 220: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 221;
                            break;
                        }
                        case 222: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 223;
                            break;
                        }
                        case 223: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 224;
                            break;
                        }
                        case 225: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 226;
                            break;
                        }
                        case 226: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 227;
                            break;
                        }
                        case 227: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 228;
                            break;
                        }
                        case 229: {
                            if (this.curChar != 34) {
                                break;
                            }
                            this.jjCheckNAddStates(412, 414);
                            break;
                        }
                        case 230: {
                            if ((0xFFFFFFFBFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(412, 414);
                            break;
                        }
                        case 231: {
                            if (this.curChar != 34) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(188, 189);
                            break;
                        }
                        case 233: {
                            if ((0x3400L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(412, 414);
                            break;
                        }
                        case 234: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(412, 414);
                            break;
                        }
                        case 235:
                        case 239: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(234);
                            break;
                        }
                        case 236: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(412, 414);
                            break;
                        }
                        case 237: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(415, 424);
                            break;
                        }
                        case 238: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(425, 429);
                            break;
                        }
                        case 240: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(412, 414);
                            break;
                        }
                        case 241:
                        case 243:
                        case 246:
                        case 250: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(238);
                            break;
                        }
                        case 242: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 243;
                            break;
                        }
                        case 244: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 245;
                            break;
                        }
                        case 245: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 246;
                            break;
                        }
                        case 247: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 248;
                            break;
                        }
                        case 248: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 249;
                            break;
                        }
                        case 249: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 250;
                            break;
                        }
                        case 251: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(430, 436);
                            break;
                        }
                        case 258: {
                            if (this.curChar != 40) {
                                break;
                            }
                            if (kind <= 86) {
                                break;
                            }
                            kind = 86;
                            break;
                        }
                        case 260: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAddStates(437, 439);
                            break;
                        }
                        case 261: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAdd(258);
                            break;
                        }
                        case 262: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 261;
                            break;
                        }
                        case 263: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(258);
                            break;
                        }
                        case 264: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(260);
                            break;
                        }
                        case 265: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(260);
                            break;
                        }
                        case 266: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(440, 444);
                            break;
                        }
                        case 267: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(264, 265);
                            break;
                        }
                        case 268: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(445, 447);
                            break;
                        }
                        case 269: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(448, 451);
                            break;
                        }
                        case 272: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(257, 259);
                            break;
                        }
                        case 273: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 272;
                            break;
                        }
                        case 274: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(257, 259);
                            break;
                        }
                        case 275: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(271);
                            break;
                        }
                        case 276: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(271);
                            break;
                        }
                        case 277: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(452, 456);
                            break;
                        }
                        case 278: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(275, 276);
                            break;
                        }
                        case 279: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(457, 459);
                            break;
                        }
                        case 280: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(460, 463);
                            break;
                        }
                        case 282: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(464, 467);
                            break;
                        }
                        case 283: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(256, 270);
                            break;
                        }
                        case 284: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 283;
                            break;
                        }
                        case 285: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(256, 270);
                            break;
                        }
                        case 286: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(282);
                            break;
                        }
                        case 287: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(282);
                            break;
                        }
                        case 288: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(468, 472);
                            break;
                        }
                        case 289: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(286, 287);
                            break;
                        }
                        case 290: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(473, 475);
                            break;
                        }
                        case 291: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(476, 479);
                            break;
                        }
                        case 292: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAddStates(143, 152);
                                break;
                            }
                            break;
                        }
                        case 293: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAdd(293);
                                break;
                            }
                            break;
                        }
                        case 294: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 2) {
                                    kind = 2;
                                }
                                this.jjCheckNAdd(294);
                                break;
                            }
                            break;
                        }
                        case 295: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(295, 44);
                            break;
                        }
                        case 296: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(296, 45);
                            break;
                        }
                        case 297: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(297, 46);
                            break;
                        }
                        case 298: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(298, 47);
                            break;
                        }
                        case 299: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAddStates(157, 175);
                            break;
                        }
                        case 300: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 17) {
                                    kind = 17;
                                }
                                this.jjCheckNAdd(300);
                                break;
                            }
                            break;
                        }
                        case 301: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 21) {
                                    kind = 21;
                                }
                                this.jjCheckNAdd(301);
                                break;
                            }
                            break;
                        }
                        case 302: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(49, 51);
                            break;
                        }
                        case 307: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 68) {
                                break;
                            }
                            kind = 68;
                            break;
                        }
                        case 308: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 307;
                            break;
                        }
                        case 309: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 68) {
                                break;
                            }
                            kind = 68;
                            break;
                        }
                        case 310: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(306);
                            break;
                        }
                        case 311: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(306);
                            break;
                        }
                        case 312: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(480, 484);
                            break;
                        }
                        case 313: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(310, 311);
                            break;
                        }
                        case 314: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(485, 487);
                            break;
                        }
                        case 315: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(488, 491);
                            break;
                        }
                        case 317: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(492, 495);
                            break;
                        }
                        case 318: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(304, 305);
                            break;
                        }
                        case 319: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 318;
                            break;
                        }
                        case 320: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(304, 305);
                            break;
                        }
                        case 321: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(317);
                            break;
                        }
                        case 322: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(317);
                            break;
                        }
                        case 323: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(496, 500);
                            break;
                        }
                        case 324: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(321, 322);
                            break;
                        }
                        case 325: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(501, 503);
                            break;
                        }
                        case 326: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(504, 507);
                            break;
                        }
                        case 327: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(46, 48);
                            break;
                        }
                        case 331: {
                            if (this.curChar == 56) {
                                if (kind > 69) {
                                    kind = 69;
                                }
                                this.jjAddStates(508, 509);
                                break;
                            }
                            break;
                        }
                        case 332: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 69) {
                                break;
                            }
                            kind = 69;
                            break;
                        }
                        case 333: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 332;
                            break;
                        }
                        case 334: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 69) {
                                break;
                            }
                            kind = 69;
                            break;
                        }
                        case 335: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(331);
                            break;
                        }
                        case 336: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(331);
                            break;
                        }
                        case 337: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(510, 514);
                            break;
                        }
                        case 338: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(335, 336);
                            break;
                        }
                        case 339: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(515, 517);
                            break;
                        }
                        case 340: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(518, 521);
                            break;
                        }
                        case 342: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(522, 525);
                            break;
                        }
                        case 343: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(329, 330);
                            break;
                        }
                        case 344: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 343;
                            break;
                        }
                        case 345: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(329, 330);
                            break;
                        }
                        case 346: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(342);
                            break;
                        }
                        case 347: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(342);
                            break;
                        }
                        case 348: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(526, 530);
                            break;
                        }
                        case 349: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(346, 347);
                            break;
                        }
                        case 350: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(531, 533);
                            break;
                        }
                        case 351: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(534, 537);
                            break;
                        }
                        case 352: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(43, 45);
                            break;
                        }
                        case 356: {
                            if (this.curChar == 56) {
                                if (kind > 70) {
                                    kind = 70;
                                }
                                this.jjAddStates(538, 539);
                                break;
                            }
                            break;
                        }
                        case 357: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 70) {
                                break;
                            }
                            kind = 70;
                            break;
                        }
                        case 358: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 357;
                            break;
                        }
                        case 359: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 70) {
                                break;
                            }
                            kind = 70;
                            break;
                        }
                        case 360: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(356);
                            break;
                        }
                        case 361: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(356);
                            break;
                        }
                        case 362: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(540, 544);
                            break;
                        }
                        case 363: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(360, 361);
                            break;
                        }
                        case 364: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(545, 547);
                            break;
                        }
                        case 365: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(548, 551);
                            break;
                        }
                        case 367: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(552, 555);
                            break;
                        }
                        case 368: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(354, 355);
                            break;
                        }
                        case 369: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 368;
                            break;
                        }
                        case 370: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(354, 355);
                            break;
                        }
                        case 371: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(367);
                            break;
                        }
                        case 372: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(367);
                            break;
                        }
                        case 373: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(556, 560);
                            break;
                        }
                        case 374: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(371, 372);
                            break;
                        }
                        case 375: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(561, 563);
                            break;
                        }
                        case 376: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(564, 567);
                            break;
                        }
                        case 377: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(40, 42);
                            break;
                        }
                        case 382: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 71) {
                                break;
                            }
                            kind = 71;
                            break;
                        }
                        case 383: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 382;
                            break;
                        }
                        case 384: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 71) {
                                break;
                            }
                            kind = 71;
                            break;
                        }
                        case 385: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(381);
                            break;
                        }
                        case 386: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(381);
                            break;
                        }
                        case 387: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(568, 572);
                            break;
                        }
                        case 388: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(385, 386);
                            break;
                        }
                        case 389: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(573, 575);
                            break;
                        }
                        case 390: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(576, 579);
                            break;
                        }
                        case 392: {
                            if (this.curChar != 51) {
                                break;
                            }
                            this.jjCheckNAddStates(580, 583);
                            break;
                        }
                        case 393: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(379, 380);
                            break;
                        }
                        case 394: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 393;
                            break;
                        }
                        case 395: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(379, 380);
                            break;
                        }
                        case 396: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(392);
                            break;
                        }
                        case 397: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(392);
                            break;
                        }
                        case 398: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(584, 588);
                            break;
                        }
                        case 399: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(396, 397);
                            break;
                        }
                        case 400: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(589, 591);
                            break;
                        }
                        case 401: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(592, 595);
                            break;
                        }
                        case 402: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(37, 39);
                            break;
                        }
                        case 407: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 72) {
                                break;
                            }
                            kind = 72;
                            break;
                        }
                        case 408: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 407;
                            break;
                        }
                        case 409: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 72) {
                                break;
                            }
                            kind = 72;
                            break;
                        }
                        case 410: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(406);
                            break;
                        }
                        case 411: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(406);
                            break;
                        }
                        case 412: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(596, 600);
                            break;
                        }
                        case 413: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(410, 411);
                            break;
                        }
                        case 414: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(601, 603);
                            break;
                        }
                        case 415: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(604, 607);
                            break;
                        }
                        case 418: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(404, 405);
                            break;
                        }
                        case 419: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 418;
                            break;
                        }
                        case 420: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(404, 405);
                            break;
                        }
                        case 421: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(417);
                            break;
                        }
                        case 422: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(417);
                            break;
                        }
                        case 423: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(608, 612);
                            break;
                        }
                        case 424: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(421, 422);
                            break;
                        }
                        case 425: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(613, 615);
                            break;
                        }
                        case 426: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(616, 619);
                            break;
                        }
                        case 427: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(34, 36);
                            break;
                        }
                        case 432: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 73) {
                                break;
                            }
                            kind = 73;
                            break;
                        }
                        case 433: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 432;
                            break;
                        }
                        case 434: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 73) {
                                break;
                            }
                            kind = 73;
                            break;
                        }
                        case 435: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(431);
                            break;
                        }
                        case 436: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(431);
                            break;
                        }
                        case 437: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(620, 624);
                            break;
                        }
                        case 438: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(435, 436);
                            break;
                        }
                        case 439: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(625, 627);
                            break;
                        }
                        case 440: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(628, 631);
                            break;
                        }
                        case 442: {
                            if (this.curChar != 57) {
                                break;
                            }
                            this.jjCheckNAddStates(632, 635);
                            break;
                        }
                        case 443: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(429, 430);
                            break;
                        }
                        case 444: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 443;
                            break;
                        }
                        case 445: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(429, 430);
                            break;
                        }
                        case 446: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(442);
                            break;
                        }
                        case 447: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(442);
                            break;
                        }
                        case 448: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(636, 640);
                            break;
                        }
                        case 449: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(446, 447);
                            break;
                        }
                        case 450: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(641, 643);
                            break;
                        }
                        case 451: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(644, 647);
                            break;
                        }
                        case 452: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(31, 33);
                            break;
                        }
                        case 456: {
                            if (this.curChar == 52) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                this.jjAddStates(648, 649);
                                break;
                            }
                            break;
                        }
                        case 457: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 74) {
                                break;
                            }
                            kind = 74;
                            break;
                        }
                        case 458: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 457;
                            break;
                        }
                        case 459: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 74) {
                                break;
                            }
                            kind = 74;
                            break;
                        }
                        case 460: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(456);
                            break;
                        }
                        case 461: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(456);
                            break;
                        }
                        case 462: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(650, 654);
                            break;
                        }
                        case 463: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(460, 461);
                            break;
                        }
                        case 464: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(655, 657);
                            break;
                        }
                        case 465: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(658, 661);
                            break;
                        }
                        case 467: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(662, 665);
                            break;
                        }
                        case 468: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(454, 455);
                            break;
                        }
                        case 469: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 468;
                            break;
                        }
                        case 470: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(454, 455);
                            break;
                        }
                        case 471: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(467);
                            break;
                        }
                        case 472: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(467);
                            break;
                        }
                        case 473: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(666, 670);
                            break;
                        }
                        case 474: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(471, 472);
                            break;
                        }
                        case 475: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(671, 673);
                            break;
                        }
                        case 476: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(674, 677);
                            break;
                        }
                        case 477: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(28, 30);
                            break;
                        }
                        case 481: {
                            if (this.curChar == 51) {
                                if (kind > 75) {
                                    kind = 75;
                                }
                                this.jjAddStates(678, 679);
                                break;
                            }
                            break;
                        }
                        case 482: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 75) {
                                break;
                            }
                            kind = 75;
                            break;
                        }
                        case 483: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 482;
                            break;
                        }
                        case 484: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 75) {
                                break;
                            }
                            kind = 75;
                            break;
                        }
                        case 485: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(481);
                            break;
                        }
                        case 486: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(481);
                            break;
                        }
                        case 487: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(680, 684);
                            break;
                        }
                        case 488: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(485, 486);
                            break;
                        }
                        case 489: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(685, 687);
                            break;
                        }
                        case 490: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(688, 691);
                            break;
                        }
                        case 492: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(692, 695);
                            break;
                        }
                        case 493: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(479, 480);
                            break;
                        }
                        case 494: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 493;
                            break;
                        }
                        case 495: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(479, 480);
                            break;
                        }
                        case 496: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(492);
                            break;
                        }
                        case 497: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(492);
                            break;
                        }
                        case 498: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(696, 700);
                            break;
                        }
                        case 499: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(496, 497);
                            break;
                        }
                        case 500: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(701, 703);
                            break;
                        }
                        case 501: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(704, 707);
                            break;
                        }
                        case 502: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(25, 27);
                            break;
                        }
                        case 507: {
                            if (this.curChar == 55) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                this.jjAddStates(708, 709);
                                break;
                            }
                            break;
                        }
                        case 508: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 76) {
                                break;
                            }
                            kind = 76;
                            break;
                        }
                        case 509: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 508;
                            break;
                        }
                        case 510: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 76) {
                                break;
                            }
                            kind = 76;
                            break;
                        }
                        case 511: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(507);
                            break;
                        }
                        case 512: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(507);
                            break;
                        }
                        case 513: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(710, 714);
                            break;
                        }
                        case 514: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(511, 512);
                            break;
                        }
                        case 515: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(715, 717);
                            break;
                        }
                        case 516: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(718, 721);
                            break;
                        }
                        case 518: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(722, 725);
                            break;
                        }
                        case 519: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(505, 506);
                            break;
                        }
                        case 520: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 519;
                            break;
                        }
                        case 521: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(505, 506);
                            break;
                        }
                        case 522: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(518);
                            break;
                        }
                        case 523: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(518);
                            break;
                        }
                        case 524: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(726, 730);
                            break;
                        }
                        case 525: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(522, 523);
                            break;
                        }
                        case 526: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(731, 733);
                            break;
                        }
                        case 527: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(734, 737);
                            break;
                        }
                        case 529: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAddStates(738, 741);
                            break;
                        }
                        case 530: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(504, 517);
                            break;
                        }
                        case 531: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 530;
                            break;
                        }
                        case 532: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(504, 517);
                            break;
                        }
                        case 533: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(529);
                            break;
                        }
                        case 534: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(529);
                            break;
                        }
                        case 535: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(742, 746);
                            break;
                        }
                        case 536: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(533, 534);
                            break;
                        }
                        case 537: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(747, 749);
                            break;
                        }
                        case 538: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(750, 753);
                            break;
                        }
                        case 539: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(22, 24);
                            break;
                        }
                        case 544: {
                            if (this.curChar == 52) {
                                if (kind > 77) {
                                    kind = 77;
                                }
                                this.jjAddStates(754, 755);
                                break;
                            }
                            break;
                        }
                        case 545: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 77) {
                                break;
                            }
                            kind = 77;
                            break;
                        }
                        case 546: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 545;
                            break;
                        }
                        case 547: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 77) {
                                break;
                            }
                            kind = 77;
                            break;
                        }
                        case 548: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(544);
                            break;
                        }
                        case 549: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(544);
                            break;
                        }
                        case 550: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(756, 760);
                            break;
                        }
                        case 551: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(548, 549);
                            break;
                        }
                        case 552: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(761, 763);
                            break;
                        }
                        case 553: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(764, 767);
                            break;
                        }
                        case 555: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(768, 771);
                            break;
                        }
                        case 556: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(542, 543);
                            break;
                        }
                        case 557: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 556;
                            break;
                        }
                        case 558: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(542, 543);
                            break;
                        }
                        case 559: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(555);
                            break;
                        }
                        case 560: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(555);
                            break;
                        }
                        case 561: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(772, 776);
                            break;
                        }
                        case 562: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(559, 560);
                            break;
                        }
                        case 563: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(777, 779);
                            break;
                        }
                        case 564: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(780, 783);
                            break;
                        }
                        case 566: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(784, 787);
                            break;
                        }
                        case 567: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(541, 554);
                            break;
                        }
                        case 568: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 567;
                            break;
                        }
                        case 569: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(541, 554);
                            break;
                        }
                        case 570: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(566);
                            break;
                        }
                        case 571: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(566);
                            break;
                        }
                        case 572: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(788, 792);
                            break;
                        }
                        case 573: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(570, 571);
                            break;
                        }
                        case 574: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(793, 795);
                            break;
                        }
                        case 575: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(796, 799);
                            break;
                        }
                        case 576: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(19, 21);
                            break;
                        }
                        case 582: {
                            if (this.curChar == 52) {
                                if (kind > 78) {
                                    kind = 78;
                                }
                                this.jjAddStates(800, 801);
                                break;
                            }
                            break;
                        }
                        case 583: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 78) {
                                break;
                            }
                            kind = 78;
                            break;
                        }
                        case 584: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 583;
                            break;
                        }
                        case 585: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 78) {
                                break;
                            }
                            kind = 78;
                            break;
                        }
                        case 586: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(582);
                            break;
                        }
                        case 587: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(582);
                            break;
                        }
                        case 588: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(802, 806);
                            break;
                        }
                        case 589: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(586, 587);
                            break;
                        }
                        case 590: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(807, 809);
                            break;
                        }
                        case 591: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(810, 813);
                            break;
                        }
                        case 593: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(814, 817);
                            break;
                        }
                        case 594: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(580, 581);
                            break;
                        }
                        case 595: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 594;
                            break;
                        }
                        case 596: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(580, 581);
                            break;
                        }
                        case 597: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(593);
                            break;
                        }
                        case 598: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(593);
                            break;
                        }
                        case 599: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(818, 822);
                            break;
                        }
                        case 600: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(597, 598);
                            break;
                        }
                        case 601: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(823, 825);
                            break;
                        }
                        case 602: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(826, 829);
                            break;
                        }
                        case 604: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(830, 833);
                            break;
                        }
                        case 605: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(579, 592);
                            break;
                        }
                        case 606: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 605;
                            break;
                        }
                        case 607: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(579, 592);
                            break;
                        }
                        case 608: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(604);
                            break;
                        }
                        case 609: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(604);
                            break;
                        }
                        case 610: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(834, 838);
                            break;
                        }
                        case 611: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(608, 609);
                            break;
                        }
                        case 612: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(839, 841);
                            break;
                        }
                        case 613: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(842, 845);
                            break;
                        }
                        case 615: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAddStates(846, 849);
                            break;
                        }
                        case 616: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(578, 603);
                            break;
                        }
                        case 617: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 616;
                            break;
                        }
                        case 618: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(578, 603);
                            break;
                        }
                        case 619: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(615);
                            break;
                        }
                        case 620: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(615);
                            break;
                        }
                        case 621: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(850, 854);
                            break;
                        }
                        case 622: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(619, 620);
                            break;
                        }
                        case 623: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(855, 857);
                            break;
                        }
                        case 624: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(858, 861);
                            break;
                        }
                        case 625: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(16, 18);
                            break;
                        }
                        case 629: {
                            if (this.curChar == 51) {
                                if (kind > 79) {
                                    kind = 79;
                                }
                                this.jjAddStates(862, 863);
                                break;
                            }
                            break;
                        }
                        case 630: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 79) {
                                break;
                            }
                            kind = 79;
                            break;
                        }
                        case 631: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 630;
                            break;
                        }
                        case 632: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 79) {
                                break;
                            }
                            kind = 79;
                            break;
                        }
                        case 633: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(629);
                            break;
                        }
                        case 634: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(629);
                            break;
                        }
                        case 635: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(864, 868);
                            break;
                        }
                        case 636: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(633, 634);
                            break;
                        }
                        case 637: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(869, 871);
                            break;
                        }
                        case 638: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(872, 875);
                            break;
                        }
                        case 641: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(627, 628);
                            break;
                        }
                        case 642: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 641;
                            break;
                        }
                        case 643: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(627, 628);
                            break;
                        }
                        case 644: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(640);
                            break;
                        }
                        case 645: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(640);
                            break;
                        }
                        case 646: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(876, 880);
                            break;
                        }
                        case 647: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(644, 645);
                            break;
                        }
                        case 648: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(881, 883);
                            break;
                        }
                        case 649: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(884, 887);
                            break;
                        }
                        case 650: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 653: {
                            if (this.curChar == 51) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                this.jjAddStates(888, 889);
                                break;
                            }
                            break;
                        }
                        case 654: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 80) {
                                break;
                            }
                            kind = 80;
                            break;
                        }
                        case 655: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 654;
                            break;
                        }
                        case 656: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 80) {
                                break;
                            }
                            kind = 80;
                            break;
                        }
                        case 657: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(653);
                            break;
                        }
                        case 658: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(653);
                            break;
                        }
                        case 659: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(890, 894);
                            break;
                        }
                        case 660: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(657, 658);
                            break;
                        }
                        case 661: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(895, 897);
                            break;
                        }
                        case 662: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(898, 901);
                            break;
                        }
                        case 663: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(10, 12);
                            break;
                        }
                        case 668: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 81) {
                                break;
                            }
                            kind = 81;
                            break;
                        }
                        case 669: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 668;
                            break;
                        }
                        case 670: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 81) {
                                break;
                            }
                            kind = 81;
                            break;
                        }
                        case 671: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(667);
                            break;
                        }
                        case 672: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(667);
                            break;
                        }
                        case 673: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(902, 906);
                            break;
                        }
                        case 674: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(671, 672);
                            break;
                        }
                        case 675: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(907, 909);
                            break;
                        }
                        case 676: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(910, 913);
                            break;
                        }
                        case 678: {
                            if (this.curChar != 56) {
                                break;
                            }
                            this.jjCheckNAddStates(914, 917);
                            break;
                        }
                        case 679: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(665, 666);
                            break;
                        }
                        case 680: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 679;
                            break;
                        }
                        case 681: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(665, 666);
                            break;
                        }
                        case 682: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(678);
                            break;
                        }
                        case 683: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(678);
                            break;
                        }
                        case 684: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(918, 922);
                            break;
                        }
                        case 685: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(682, 683);
                            break;
                        }
                        case 686: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(923, 925);
                            break;
                        }
                        case 687: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(926, 929);
                            break;
                        }
                        case 688: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(7, 9);
                            break;
                        }
                        case 694: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 82) {
                                break;
                            }
                            kind = 82;
                            break;
                        }
                        case 695: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 694;
                            break;
                        }
                        case 696: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 82) {
                                break;
                            }
                            kind = 82;
                            break;
                        }
                        case 697: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(693);
                            break;
                        }
                        case 698: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(693);
                            break;
                        }
                        case 699: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(930, 934);
                            break;
                        }
                        case 700: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(697, 698);
                            break;
                        }
                        case 701: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(935, 937);
                            break;
                        }
                        case 702: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(938, 941);
                            break;
                        }
                        case 704: {
                            if (this.curChar != 56) {
                                break;
                            }
                            this.jjCheckNAddStates(942, 945);
                            break;
                        }
                        case 705: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(691, 692);
                            break;
                        }
                        case 706: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 705;
                            break;
                        }
                        case 707: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(691, 692);
                            break;
                        }
                        case 708: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(704);
                            break;
                        }
                        case 709: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(704);
                            break;
                        }
                        case 710: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(946, 950);
                            break;
                        }
                        case 711: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(708, 709);
                            break;
                        }
                        case 712: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(951, 953);
                            break;
                        }
                        case 713: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(954, 957);
                            break;
                        }
                        case 716: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(690, 703);
                            break;
                        }
                        case 717: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 716;
                            break;
                        }
                        case 718: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(690, 703);
                            break;
                        }
                        case 719: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(715);
                            break;
                        }
                        case 720: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(715);
                            break;
                        }
                        case 721: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(958, 962);
                            break;
                        }
                        case 722: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(719, 720);
                            break;
                        }
                        case 723: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(963, 965);
                            break;
                        }
                        case 724: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(966, 969);
                            break;
                        }
                        case 725: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(725, 726);
                            break;
                        }
                        case 726: {
                            if (this.curChar != 37) {
                                break;
                            }
                            if (kind <= 83) {
                                break;
                            }
                            kind = 83;
                            break;
                        }
                        case 727: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(3, 6);
                            break;
                        }
                        case 728: {
                            if (this.curChar != 45) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(729, 748);
                            break;
                        }
                        case 730: {
                            if ((0x3FF200000000000L & j) != 0x0L) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                this.jjCheckNAddTwoStates(730, 731);
                                break;
                            }
                            break;
                        }
                        case 732: {
                            if ((0xFC00FFFFFFFFCBFFL & j) != 0x0L) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                this.jjCheckNAddTwoStates(730, 731);
                                break;
                            }
                            break;
                        }
                        case 733: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                this.jjCheckNAddStates(970, 978);
                                break;
                            }
                            break;
                        }
                        case 734: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                this.jjCheckNAddStates(979, 982);
                                break;
                            }
                            break;
                        }
                        case 735: {
                            if (this.curChar == 10) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                this.jjCheckNAddTwoStates(730, 731);
                                break;
                            }
                            break;
                        }
                        case 736:
                        case 751: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(735);
                            break;
                        }
                        case 737: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                this.jjCheckNAddTwoStates(730, 731);
                                break;
                            }
                            break;
                        }
                        case 738:
                        case 740:
                        case 743:
                        case 747: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(734);
                            break;
                        }
                        case 739: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 740;
                            break;
                        }
                        case 741: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 742;
                            break;
                        }
                        case 742: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 743;
                            break;
                        }
                        case 744: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 745;
                            break;
                        }
                        case 745: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 746;
                            break;
                        }
                        case 746: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 747;
                            break;
                        }
                        case 749: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                this.jjCheckNAddStates(983, 991);
                                break;
                            }
                            break;
                        }
                        case 750: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 84) {
                                    kind = 84;
                                }
                                this.jjCheckNAddStates(992, 995);
                                break;
                            }
                            break;
                        }
                        case 752:
                        case 754:
                        case 757:
                        case 761: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(750);
                            break;
                        }
                        case 753: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 754;
                            break;
                        }
                        case 755: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 756;
                            break;
                        }
                        case 756: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 757;
                            break;
                        }
                        case 758: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 759;
                            break;
                        }
                        case 759: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 760;
                            break;
                        }
                        case 760: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 761;
                            break;
                        }
                        case 762: {
                            if (this.curChar != 45) {
                                break;
                            }
                            this.jjAddStates(153, 156);
                            break;
                        }
                        case 764: {
                            if ((0x3FF200000000000L & j) != 0x0L) {
                                if (kind > 19) {
                                    kind = 19;
                                }
                                this.jjCheckNAddTwoStates(764, 765);
                                break;
                            }
                            break;
                        }
                        case 766: {
                            if ((0xFC00FFFFFFFFCBFFL & j) != 0x0L) {
                                if (kind > 19) {
                                    kind = 19;
                                }
                                this.jjCheckNAddTwoStates(764, 765);
                                break;
                            }
                            break;
                        }
                        case 767: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 19) {
                                    kind = 19;
                                }
                                this.jjCheckNAddStates(996, 1004);
                                break;
                            }
                            break;
                        }
                        case 768: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 19) {
                                    kind = 19;
                                }
                                this.jjCheckNAddStates(1005, 1008);
                                break;
                            }
                            break;
                        }
                        case 769: {
                            if (this.curChar == 10) {
                                if (kind > 19) {
                                    kind = 19;
                                }
                                this.jjCheckNAddTwoStates(764, 765);
                                break;
                            }
                            break;
                        }
                        case 770:
                        case 819: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(769);
                            break;
                        }
                        case 771: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 19) {
                                    kind = 19;
                                }
                                this.jjCheckNAddTwoStates(764, 765);
                                break;
                            }
                            break;
                        }
                        case 772:
                        case 774:
                        case 777:
                        case 781: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(768);
                            break;
                        }
                        case 773: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 774;
                            break;
                        }
                        case 775: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 776;
                            break;
                        }
                        case 776: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 777;
                            break;
                        }
                        case 778: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 779;
                            break;
                        }
                        case 779: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 780;
                            break;
                        }
                        case 780: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 781;
                            break;
                        }
                        case 783: {
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 784: {
                            if (this.curChar != 40) {
                                break;
                            }
                            if (kind <= 87) {
                                break;
                            }
                            kind = 87;
                            break;
                        }
                        case 786: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 787: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1009, 1018);
                            break;
                        }
                        case 788: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1019, 1023);
                            break;
                        }
                        case 789: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 790:
                        case 805: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(789);
                            break;
                        }
                        case 791: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 792:
                        case 794:
                        case 797:
                        case 801: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(788);
                            break;
                        }
                        case 793: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 794;
                            break;
                        }
                        case 795: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 796;
                            break;
                        }
                        case 796: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 797;
                            break;
                        }
                        case 798: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 799;
                            break;
                        }
                        case 799: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 800;
                            break;
                        }
                        case 800: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 801;
                            break;
                        }
                        case 803: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1024, 1033);
                            break;
                        }
                        case 804: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1034, 1038);
                            break;
                        }
                        case 806:
                        case 808:
                        case 811:
                        case 815: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(804);
                            break;
                        }
                        case 807: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 808;
                            break;
                        }
                        case 809: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 810;
                            break;
                        }
                        case 810: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 811;
                            break;
                        }
                        case 812: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 813;
                            break;
                        }
                        case 813: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 814;
                            break;
                        }
                        case 814: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 815;
                            break;
                        }
                        case 817: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 19) {
                                    kind = 19;
                                }
                                this.jjCheckNAddStates(1039, 1047);
                                break;
                            }
                            break;
                        }
                        case 818: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 19) {
                                    kind = 19;
                                }
                                this.jjCheckNAddStates(1048, 1051);
                                break;
                            }
                            break;
                        }
                        case 820:
                        case 822:
                        case 825:
                        case 829: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(818);
                            break;
                        }
                        case 821: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 822;
                            break;
                        }
                        case 823: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 824;
                            break;
                        }
                        case 824: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 825;
                            break;
                        }
                        case 826: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 827;
                            break;
                        }
                        case 827: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 828;
                            break;
                        }
                        case 828: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 829;
                            break;
                        }
                        case 839: {
                            if (this.curChar == 52) {
                                if (kind > 63) {
                                    kind = 63;
                                }
                                this.jjAddStates(1052, 1053);
                                break;
                            }
                            break;
                        }
                        case 840: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 63) {
                                break;
                            }
                            kind = 63;
                            break;
                        }
                        case 841: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 840;
                            break;
                        }
                        case 842: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 63) {
                                break;
                            }
                            kind = 63;
                            break;
                        }
                        case 843: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(839);
                            break;
                        }
                        case 844: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(839);
                            break;
                        }
                        case 845: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1054, 1058);
                            break;
                        }
                        case 846: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(843, 844);
                            break;
                        }
                        case 847: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1059, 1061);
                            break;
                        }
                        case 848: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1062, 1065);
                            break;
                        }
                        case 850: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(1066, 1069);
                            break;
                        }
                        case 851: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(837, 838);
                            break;
                        }
                        case 852: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 851;
                            break;
                        }
                        case 853: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(837, 838);
                            break;
                        }
                        case 854: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(850);
                            break;
                        }
                        case 855: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(850);
                            break;
                        }
                        case 856: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1070, 1074);
                            break;
                        }
                        case 857: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(854, 855);
                            break;
                        }
                        case 858: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1075, 1077);
                            break;
                        }
                        case 859: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1078, 1081);
                            break;
                        }
                        case 861: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(1082, 1085);
                            break;
                        }
                        case 862: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(836, 849);
                            break;
                        }
                        case 863: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 862;
                            break;
                        }
                        case 864: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(836, 849);
                            break;
                        }
                        case 865: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(861);
                            break;
                        }
                        case 866: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(861);
                            break;
                        }
                        case 867: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1086, 1090);
                            break;
                        }
                        case 868: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(865, 866);
                            break;
                        }
                        case 869: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1091, 1093);
                            break;
                        }
                        case 870: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1094, 1097);
                            break;
                        }
                        case 872: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1098, 1101);
                            break;
                        }
                        case 873: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(835, 860);
                            break;
                        }
                        case 874: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 873;
                            break;
                        }
                        case 875: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(835, 860);
                            break;
                        }
                        case 876: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(872);
                            break;
                        }
                        case 877: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(872);
                            break;
                        }
                        case 878: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1102, 1106);
                            break;
                        }
                        case 879: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(876, 877);
                            break;
                        }
                        case 880: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1107, 1109);
                            break;
                        }
                        case 881: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1110, 1113);
                            break;
                        }
                        case 884: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(834, 871);
                            break;
                        }
                        case 885: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 884;
                            break;
                        }
                        case 886: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(834, 871);
                            break;
                        }
                        case 887: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(883);
                            break;
                        }
                        case 888: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(883);
                            break;
                        }
                        case 889: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1114, 1118);
                            break;
                        }
                        case 890: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(887, 888);
                            break;
                        }
                        case 891: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1119, 1121);
                            break;
                        }
                        case 892: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1122, 1125);
                            break;
                        }
                        case 898: {
                            if (this.curChar == 53) {
                                if (kind > 64) {
                                    kind = 64;
                                }
                                this.jjAddStates(1126, 1127);
                                break;
                            }
                            break;
                        }
                        case 899: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 64) {
                                break;
                            }
                            kind = 64;
                            break;
                        }
                        case 900: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 899;
                            break;
                        }
                        case 901: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 64) {
                                break;
                            }
                            kind = 64;
                            break;
                        }
                        case 902: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(898);
                            break;
                        }
                        case 903: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(898);
                            break;
                        }
                        case 904: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1128, 1132);
                            break;
                        }
                        case 905: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(902, 903);
                            break;
                        }
                        case 906: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1133, 1135);
                            break;
                        }
                        case 907: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1136, 1139);
                            break;
                        }
                        case 909: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAddStates(1140, 1143);
                            break;
                        }
                        case 910: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(896, 897);
                            break;
                        }
                        case 911: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 910;
                            break;
                        }
                        case 912: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(896, 897);
                            break;
                        }
                        case 913: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(909);
                            break;
                        }
                        case 914: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(909);
                            break;
                        }
                        case 915: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1144, 1148);
                            break;
                        }
                        case 916: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(913, 914);
                            break;
                        }
                        case 917: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1149, 1151);
                            break;
                        }
                        case 918: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1152, 1155);
                            break;
                        }
                        case 920: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(1156, 1159);
                            break;
                        }
                        case 921: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(895, 908);
                            break;
                        }
                        case 922: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 921;
                            break;
                        }
                        case 923: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(895, 908);
                            break;
                        }
                        case 924: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(920);
                            break;
                        }
                        case 925: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(920);
                            break;
                        }
                        case 926: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1160, 1164);
                            break;
                        }
                        case 927: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(924, 925);
                            break;
                        }
                        case 928: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1165, 1167);
                            break;
                        }
                        case 929: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1168, 1171);
                            break;
                        }
                        case 936: {
                            if (this.curChar == 49) {
                                if (kind > 65) {
                                    kind = 65;
                                }
                                this.jjAddStates(1172, 1173);
                                break;
                            }
                            break;
                        }
                        case 937: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 65) {
                                break;
                            }
                            kind = 65;
                            break;
                        }
                        case 938: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 937;
                            break;
                        }
                        case 939: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 65) {
                                break;
                            }
                            kind = 65;
                            break;
                        }
                        case 940: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(936);
                            break;
                        }
                        case 941: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(936);
                            break;
                        }
                        case 942: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1174, 1178);
                            break;
                        }
                        case 943: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(940, 941);
                            break;
                        }
                        case 944: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1179, 1181);
                            break;
                        }
                        case 945: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1182, 1185);
                            break;
                        }
                        case 947: {
                            if (this.curChar != 57) {
                                break;
                            }
                            this.jjCheckNAddStates(1186, 1189);
                            break;
                        }
                        case 948: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(934, 935);
                            break;
                        }
                        case 949: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 948;
                            break;
                        }
                        case 950: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(934, 935);
                            break;
                        }
                        case 951: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(947);
                            break;
                        }
                        case 952: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(947);
                            break;
                        }
                        case 953: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1190, 1194);
                            break;
                        }
                        case 954: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(951, 952);
                            break;
                        }
                        case 955: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1195, 1197);
                            break;
                        }
                        case 956: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1198, 1201);
                            break;
                        }
                        case 958: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAddStates(1202, 1205);
                            break;
                        }
                        case 959: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(933, 946);
                            break;
                        }
                        case 960: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 959;
                            break;
                        }
                        case 961: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(933, 946);
                            break;
                        }
                        case 962: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(958);
                            break;
                        }
                        case 963: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(958);
                            break;
                        }
                        case 964: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1206, 1210);
                            break;
                        }
                        case 965: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(962, 963);
                            break;
                        }
                        case 966: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1211, 1213);
                            break;
                        }
                        case 967: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1214, 1217);
                            break;
                        }
                        case 969: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(1218, 1221);
                            break;
                        }
                        case 970: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(932, 957);
                            break;
                        }
                        case 971: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 970;
                            break;
                        }
                        case 972: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(932, 957);
                            break;
                        }
                        case 973: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(969);
                            break;
                        }
                        case 974: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(969);
                            break;
                        }
                        case 975: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1222, 1226);
                            break;
                        }
                        case 976: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(973, 974);
                            break;
                        }
                        case 977: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1227, 1229);
                            break;
                        }
                        case 978: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1230, 1233);
                            break;
                        }
                        case 987: {
                            if (this.curChar == 52) {
                                if (kind > 66) {
                                    kind = 66;
                                }
                                this.jjAddStates(1234, 1235);
                                break;
                            }
                            break;
                        }
                        case 988: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 66) {
                                break;
                            }
                            kind = 66;
                            break;
                        }
                        case 989: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 988;
                            break;
                        }
                        case 990: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 66) {
                                break;
                            }
                            kind = 66;
                            break;
                        }
                        case 991: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(987);
                            break;
                        }
                        case 992: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(987);
                            break;
                        }
                        case 993: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1236, 1240);
                            break;
                        }
                        case 994: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(991, 992);
                            break;
                        }
                        case 995: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1241, 1243);
                            break;
                        }
                        case 996: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1244, 1247);
                            break;
                        }
                        case 998: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(1248, 1251);
                            break;
                        }
                        case 999: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(985, 986);
                            break;
                        }
                        case 1000: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 999;
                            break;
                        }
                        case 1001: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(985, 986);
                            break;
                        }
                        case 1002: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(998);
                            break;
                        }
                        case 1003: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(998);
                            break;
                        }
                        case 1004: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1252, 1256);
                            break;
                        }
                        case 1005: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1002, 1003);
                            break;
                        }
                        case 1006: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1257, 1259);
                            break;
                        }
                        case 1007: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1260, 1263);
                            break;
                        }
                        case 1009: {
                            if (this.curChar != 51) {
                                break;
                            }
                            this.jjCheckNAddStates(1264, 1267);
                            break;
                        }
                        case 1010: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(984, 997);
                            break;
                        }
                        case 1011: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1010;
                            break;
                        }
                        case 1012: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(984, 997);
                            break;
                        }
                        case 1013: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(1009);
                            break;
                        }
                        case 1014: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(1009);
                            break;
                        }
                        case 1015: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1268, 1272);
                            break;
                        }
                        case 1016: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1013, 1014);
                            break;
                        }
                        case 1017: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1273, 1275);
                            break;
                        }
                        case 1018: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1276, 1279);
                            break;
                        }
                        case 1020: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(1280, 1283);
                            break;
                        }
                        case 1021: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(983, 1008);
                            break;
                        }
                        case 1022: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1021;
                            break;
                        }
                        case 1023: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(983, 1008);
                            break;
                        }
                        case 1024: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(1020);
                            break;
                        }
                        case 1025: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(1020);
                            break;
                        }
                        case 1026: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1284, 1288);
                            break;
                        }
                        case 1027: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1024, 1025);
                            break;
                        }
                        case 1028: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1289, 1291);
                            break;
                        }
                        case 1029: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1292, 1295);
                            break;
                        }
                        case 1031: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(1296, 1299);
                            break;
                        }
                        case 1032: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(982, 1019);
                            break;
                        }
                        case 1033: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1032;
                            break;
                        }
                        case 1034: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(982, 1019);
                            break;
                        }
                        case 1035: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1031);
                            break;
                        }
                        case 1036: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1031);
                            break;
                        }
                        case 1037: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1300, 1304);
                            break;
                        }
                        case 1038: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1035, 1036);
                            break;
                        }
                        case 1039: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1305, 1307);
                            break;
                        }
                        case 1040: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1308, 1311);
                            break;
                        }
                        case 1042: {
                            if (this.curChar != 56) {
                                break;
                            }
                            this.jjCheckNAddStates(1312, 1315);
                            break;
                        }
                        case 1043: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(981, 1030);
                            break;
                        }
                        case 1044: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1043;
                            break;
                        }
                        case 1045: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(981, 1030);
                            break;
                        }
                        case 1046: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1042);
                            break;
                        }
                        case 1047: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1042);
                            break;
                        }
                        case 1048: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1316, 1320);
                            break;
                        }
                        case 1049: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1046, 1047);
                            break;
                        }
                        case 1050: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1321, 1323);
                            break;
                        }
                        case 1051: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1324, 1327);
                            break;
                        }
                        case 1053: {
                            if (this.curChar != 51) {
                                break;
                            }
                            this.jjCheckNAddStates(1328, 1331);
                            break;
                        }
                        case 1054: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(980, 1041);
                            break;
                        }
                        case 1055: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1054;
                            break;
                        }
                        case 1056: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(980, 1041);
                            break;
                        }
                        case 1057: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1053);
                            break;
                        }
                        case 1058: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1053);
                            break;
                        }
                        case 1059: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1332, 1336);
                            break;
                        }
                        case 1060: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1057, 1058);
                            break;
                        }
                        case 1061: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1337, 1339);
                            break;
                        }
                        case 1062: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1340, 1343);
                            break;
                        }
                        case 1063: {
                            if (this.curChar != 45) {
                                break;
                            }
                            this.jjAddStates(1344, 1345);
                            break;
                        }
                        case 1065: {
                            if ((0x3FF200000000000L & j) != 0x0L) {
                                if (kind > 88) {
                                    kind = 88;
                                }
                                this.jjCheckNAddTwoStates(1065, 1066);
                                break;
                            }
                            break;
                        }
                        case 1067: {
                            if ((0xFC00FFFFFFFFCBFFL & j) != 0x0L) {
                                if (kind > 88) {
                                    kind = 88;
                                }
                                this.jjCheckNAddTwoStates(1065, 1066);
                                break;
                            }
                            break;
                        }
                        case 1068: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 88) {
                                    kind = 88;
                                }
                                this.jjCheckNAddStates(1346, 1354);
                                break;
                            }
                            break;
                        }
                        case 1069: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 88) {
                                    kind = 88;
                                }
                                this.jjCheckNAddStates(1355, 1358);
                                break;
                            }
                            break;
                        }
                        case 1070: {
                            if (this.curChar == 10) {
                                if (kind > 88) {
                                    kind = 88;
                                }
                                this.jjCheckNAddTwoStates(1065, 1066);
                                break;
                            }
                            break;
                        }
                        case 1071:
                        case 1086: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(1070);
                            break;
                        }
                        case 1072: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 88) {
                                    kind = 88;
                                }
                                this.jjCheckNAddTwoStates(1065, 1066);
                                break;
                            }
                            break;
                        }
                        case 1073:
                        case 1075:
                        case 1078:
                        case 1082: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(1069);
                            break;
                        }
                        case 1074: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1075;
                            break;
                        }
                        case 1076: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1077;
                            break;
                        }
                        case 1077: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1078;
                            break;
                        }
                        case 1079: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1080;
                            break;
                        }
                        case 1080: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1081;
                            break;
                        }
                        case 1081: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1082;
                            break;
                        }
                        case 1084: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 88) {
                                    kind = 88;
                                }
                                this.jjCheckNAddStates(1359, 1367);
                                break;
                            }
                            break;
                        }
                        case 1085: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 88) {
                                    kind = 88;
                                }
                                this.jjCheckNAddStates(1368, 1371);
                                break;
                            }
                            break;
                        }
                        case 1087:
                        case 1089:
                        case 1092:
                        case 1096: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(1085);
                            break;
                        }
                        case 1088: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1089;
                            break;
                        }
                        case 1090: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1091;
                            break;
                        }
                        case 1091: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1092;
                            break;
                        }
                        case 1093: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1094;
                            break;
                        }
                        case 1094: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1095;
                            break;
                        }
                        case 1095: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1096;
                            break;
                        }
                        case 1099: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(931, 968);
                            break;
                        }
                        case 1100: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1099;
                            break;
                        }
                        case 1101: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(931, 968);
                            break;
                        }
                        case 1102: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1098);
                            break;
                        }
                        case 1103: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1098);
                            break;
                        }
                        case 1104: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1372, 1376);
                            break;
                        }
                        case 1105: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1102, 1103);
                            break;
                        }
                        case 1106: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1377, 1379);
                            break;
                        }
                        case 1107: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1380, 1383);
                            break;
                        }
                        case 1109: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1384, 1387);
                            break;
                        }
                        case 1110: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(894, 919);
                            break;
                        }
                        case 1111: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1110;
                            break;
                        }
                        case 1112: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(894, 919);
                            break;
                        }
                        case 1113: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(1109);
                            break;
                        }
                        case 1114: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(1109);
                            break;
                        }
                        case 1115: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1388, 1392);
                            break;
                        }
                        case 1116: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1113, 1114);
                            break;
                        }
                        case 1117: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1393, 1395);
                            break;
                        }
                        case 1118: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1396, 1399);
                            break;
                        }
                        case 1120: {
                            if (this.curChar != 57) {
                                break;
                            }
                            this.jjCheckNAddStates(1400, 1403);
                            break;
                        }
                        case 1121: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(833, 882);
                            break;
                        }
                        case 1122: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1121;
                            break;
                        }
                        case 1123: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(833, 882);
                            break;
                        }
                        case 1124: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1120);
                            break;
                        }
                        case 1125: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1120);
                            break;
                        }
                        case 1126: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1404, 1408);
                            break;
                        }
                        case 1127: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1124, 1125);
                            break;
                        }
                        case 1128: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1409, 1411);
                            break;
                        }
                        case 1129: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1412, 1415);
                            break;
                        }
                        case 1132: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(255, 281);
                            break;
                        }
                        case 1133: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1132;
                            break;
                        }
                        case 1134: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(255, 281);
                            break;
                        }
                        case 1135: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1131);
                            break;
                        }
                        case 1136: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1131);
                            break;
                        }
                        case 1137: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1416, 1420);
                            break;
                        }
                        case 1138: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1135, 1136);
                            break;
                        }
                        case 1139: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1421, 1423);
                            break;
                        }
                        case 1140: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1424, 1427);
                            break;
                        }
                        case 1141: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 17) {
                                    kind = 17;
                                }
                                this.jjCheckNAddStates(52, 142);
                                break;
                            }
                            break;
                        }
                        case 1142: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 17) {
                                    kind = 17;
                                }
                                this.jjCheckNAdd(1142);
                                break;
                            }
                            break;
                        }
                        case 1143: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1143, 1144);
                            break;
                        }
                        case 1144: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(300);
                            break;
                        }
                        case 1145: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 21) {
                                    kind = 21;
                                }
                                this.jjCheckNAdd(1145);
                                break;
                            }
                            break;
                        }
                        case 1146: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1146, 1147);
                            break;
                        }
                        case 1147: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(301);
                            break;
                        }
                        case 1148: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1428, 1430);
                            break;
                        }
                        case 1149: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1149, 1150);
                            break;
                        }
                        case 1150: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(302);
                            break;
                        }
                        case 1151: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1431, 1433);
                            break;
                        }
                        case 1152: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1152, 1153);
                            break;
                        }
                        case 1153: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(327);
                            break;
                        }
                        case 1154: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1434, 1436);
                            break;
                        }
                        case 1155: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1155, 1156);
                            break;
                        }
                        case 1156: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(352);
                            break;
                        }
                        case 1157: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1437, 1439);
                            break;
                        }
                        case 1158: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1158, 1159);
                            break;
                        }
                        case 1159: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(377);
                            break;
                        }
                        case 1160: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1440, 1442);
                            break;
                        }
                        case 1161: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1161, 1162);
                            break;
                        }
                        case 1162: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(402);
                            break;
                        }
                        case 1163: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1443, 1445);
                            break;
                        }
                        case 1164: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1164, 1165);
                            break;
                        }
                        case 1165: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(427);
                            break;
                        }
                        case 1166: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1446, 1448);
                            break;
                        }
                        case 1167: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1167, 1168);
                            break;
                        }
                        case 1168: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(452);
                            break;
                        }
                        case 1169: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1449, 1451);
                            break;
                        }
                        case 1170: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1170, 1171);
                            break;
                        }
                        case 1171: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(477);
                            break;
                        }
                        case 1172: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1452, 1454);
                            break;
                        }
                        case 1173: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1173, 1174);
                            break;
                        }
                        case 1174: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(502);
                            break;
                        }
                        case 1175: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1455, 1457);
                            break;
                        }
                        case 1176: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1176, 1177);
                            break;
                        }
                        case 1177: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(539);
                            break;
                        }
                        case 1178: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1458, 1460);
                            break;
                        }
                        case 1179: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1179, 1180);
                            break;
                        }
                        case 1180: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(576);
                            break;
                        }
                        case 1181: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1461, 1463);
                            break;
                        }
                        case 1182: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1182, 1183);
                            break;
                        }
                        case 1183: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(625);
                            break;
                        }
                        case 1184: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1464, 1466);
                            break;
                        }
                        case 1185: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1185, 1186);
                            break;
                        }
                        case 1186: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(650);
                            break;
                        }
                        case 1187: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1467, 1469);
                            break;
                        }
                        case 1188: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1188, 1189);
                            break;
                        }
                        case 1189: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(663);
                            break;
                        }
                        case 1190: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1470, 1472);
                            break;
                        }
                        case 1191: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1191, 1192);
                            break;
                        }
                        case 1192: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(688);
                            break;
                        }
                        case 1193: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1193, 726);
                            break;
                        }
                        case 1194: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1194, 1195);
                            break;
                        }
                        case 1195: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(725);
                            break;
                        }
                        case 1196: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1473, 1476);
                            break;
                        }
                        case 1197: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1197, 1198);
                            break;
                        }
                        case 1198: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(727);
                            break;
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
            final int n2 = 1199;
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
    
    private int jjMoveStringLiteralDfa0_1() {
        switch (this.curChar) {
            case 42: {
                return this.jjMoveStringLiteralDfa1_1(16L);
            }
            default: {
                return 1;
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_1(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            return 1;
        }
        switch (this.curChar) {
            case 47: {
                if ((active0 & 0x10L) != 0x0L) {
                    return this.jjStopAtPos(1, 4);
                }
                return 2;
            }
            default: {
                return 2;
            }
        }
    }
    
    protected Token jjFillToken() {
        String curTokenImage;
        int beginLine;
        int endLine;
        int beginColumn;
        int endColumn;
        if (this.jjmatchedPos < 0) {
            if (this.image == null) {
                curTokenImage = "";
            }
            else {
                curTokenImage = this.image.toString();
            }
            endLine = (beginLine = this.input_stream.getEndLine());
            endColumn = (beginColumn = this.input_stream.getEndColumn());
        }
        else {
            final String im = SACParserCSS21TokenManager.jjstrLiteralImages[this.jjmatchedKind];
            curTokenImage = ((im == null) ? this.input_stream.GetImage() : im);
            beginLine = this.input_stream.getBeginLine();
            beginColumn = this.input_stream.getBeginColumn();
            endLine = this.input_stream.getEndLine();
            endColumn = this.input_stream.getEndColumn();
        }
        final Token t = Token.newToken(this.jjmatchedKind);
        t.kind = this.jjmatchedKind;
        t.image = curTokenImage;
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }
    
    private static final boolean jjCanMove_0(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (SACParserCSS21TokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (SACParserCSS21TokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    public Token getNextToken() {
        int curPos = 0;
    Label_0393:
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (final Exception e) {
                this.jjmatchedKind = 0;
                this.jjmatchedPos = -1;
                final Token matchedToken = this.jjFillToken();
                return matchedToken;
            }
            (this.image = this.jjimage).setLength(0);
            this.jjimageLen = 0;
            while (true) {
                switch (this.curLexState) {
                    case 0: {
                        this.jjmatchedKind = 2;
                        this.jjmatchedPos = -1;
                        curPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_0();
                        if (this.jjmatchedPos < 0 || (this.jjmatchedPos == 0 && this.jjmatchedKind > 89)) {
                            this.jjmatchedKind = 89;
                            this.jjmatchedPos = 0;
                            break;
                        }
                        break;
                    }
                    case 1: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 5) {
                            this.jjmatchedKind = 5;
                            break;
                        }
                        break;
                    }
                }
                if (this.jjmatchedKind == Integer.MAX_VALUE) {
                    break Label_0393;
                }
                if (this.jjmatchedPos + 1 < curPos) {
                    this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                }
                if ((SACParserCSS21TokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                    final Token matchedToken = this.jjFillToken();
                    this.TokenLexicalActions(matchedToken);
                    if (SACParserCSS21TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSS21TokenManager.jjnewLexState[this.jjmatchedKind];
                    }
                    return matchedToken;
                }
                if ((SACParserCSS21TokenManager.jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                    this.jjimageLen += this.jjmatchedPos + 1;
                    if (SACParserCSS21TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSS21TokenManager.jjnewLexState[this.jjmatchedKind];
                    }
                    curPos = 0;
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    try {
                        this.curChar = this.input_stream.readChar();
                        continue;
                    }
                    catch (final IOException ex) {}
                    break Label_0393;
                }
                if (SACParserCSS21TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = SACParserCSS21TokenManager.jjnewLexState[this.jjmatchedKind];
                    break;
                }
                break;
            }
        }
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
            if (this.curChar == 10 || this.curChar == 13) {
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
    
    void SkipLexicalActions(final Token matchedToken) {
        final int jjmatchedKind = this.jjmatchedKind;
    }
    
    void MoreLexicalActions() {
        final int jjimageLen = this.jjimageLen;
        final int lengthOfMatch = this.jjmatchedPos + 1;
        this.lengthOfMatch = lengthOfMatch;
        this.jjimageLen = jjimageLen + lengthOfMatch;
        final int jjmatchedKind = this.jjmatchedKind;
    }
    
    void TokenLexicalActions(final Token matchedToken) {
        switch (this.jjmatchedKind) {
            case 22: {
                final StringBuilder image = this.image;
                final CharStream input_stream = this.input_stream;
                final int jjimageLen = this.jjimageLen;
                final int lengthOfMatch = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch;
                image.append(input_stream.GetSuffix(jjimageLen + lengthOfMatch));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 1);
                break;
            }
            case 68: {
                final StringBuilder image2 = this.image;
                final CharStream input_stream2 = this.input_stream;
                final int jjimageLen2 = this.jjimageLen;
                final int lengthOfMatch2 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch2;
                image2.append(input_stream2.GetSuffix(jjimageLen2 + lengthOfMatch2));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 69: {
                final StringBuilder image3 = this.image;
                final CharStream input_stream3 = this.input_stream;
                final int jjimageLen3 = this.jjimageLen;
                final int lengthOfMatch3 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch3;
                image3.append(input_stream3.GetSuffix(jjimageLen3 + lengthOfMatch3));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 70: {
                final StringBuilder image4 = this.image;
                final CharStream input_stream4 = this.input_stream;
                final int jjimageLen4 = this.jjimageLen;
                final int lengthOfMatch4 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch4;
                image4.append(input_stream4.GetSuffix(jjimageLen4 + lengthOfMatch4));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 71: {
                final StringBuilder image5 = this.image;
                final CharStream input_stream5 = this.input_stream;
                final int jjimageLen5 = this.jjimageLen;
                final int lengthOfMatch5 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch5;
                image5.append(input_stream5.GetSuffix(jjimageLen5 + lengthOfMatch5));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 72: {
                final StringBuilder image6 = this.image;
                final CharStream input_stream6 = this.input_stream;
                final int jjimageLen6 = this.jjimageLen;
                final int lengthOfMatch6 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch6;
                image6.append(input_stream6.GetSuffix(jjimageLen6 + lengthOfMatch6));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 73: {
                final StringBuilder image7 = this.image;
                final CharStream input_stream7 = this.input_stream;
                final int jjimageLen7 = this.jjimageLen;
                final int lengthOfMatch7 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch7;
                image7.append(input_stream7.GetSuffix(jjimageLen7 + lengthOfMatch7));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 74: {
                final StringBuilder image8 = this.image;
                final CharStream input_stream8 = this.input_stream;
                final int jjimageLen8 = this.jjimageLen;
                final int lengthOfMatch8 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch8;
                image8.append(input_stream8.GetSuffix(jjimageLen8 + lengthOfMatch8));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 75: {
                final StringBuilder image9 = this.image;
                final CharStream input_stream9 = this.input_stream;
                final int jjimageLen9 = this.jjimageLen;
                final int lengthOfMatch9 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch9;
                image9.append(input_stream9.GetSuffix(jjimageLen9 + lengthOfMatch9));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 76: {
                final StringBuilder image10 = this.image;
                final CharStream input_stream10 = this.input_stream;
                final int jjimageLen10 = this.jjimageLen;
                final int lengthOfMatch10 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch10;
                image10.append(input_stream10.GetSuffix(jjimageLen10 + lengthOfMatch10));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 77: {
                final StringBuilder image11 = this.image;
                final CharStream input_stream11 = this.input_stream;
                final int jjimageLen11 = this.jjimageLen;
                final int lengthOfMatch11 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch11;
                image11.append(input_stream11.GetSuffix(jjimageLen11 + lengthOfMatch11));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 78: {
                final StringBuilder image12 = this.image;
                final CharStream input_stream12 = this.input_stream;
                final int jjimageLen12 = this.jjimageLen;
                final int lengthOfMatch12 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch12;
                image12.append(input_stream12.GetSuffix(jjimageLen12 + lengthOfMatch12));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 4);
                break;
            }
            case 79: {
                final StringBuilder image13 = this.image;
                final CharStream input_stream13 = this.input_stream;
                final int jjimageLen13 = this.jjimageLen;
                final int lengthOfMatch13 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch13;
                image13.append(input_stream13.GetSuffix(jjimageLen13 + lengthOfMatch13));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 80: {
                final StringBuilder image14 = this.image;
                final CharStream input_stream14 = this.input_stream;
                final int jjimageLen14 = this.jjimageLen;
                final int lengthOfMatch14 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch14;
                image14.append(input_stream14.GetSuffix(jjimageLen14 + lengthOfMatch14));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 1);
                break;
            }
            case 81: {
                final StringBuilder image15 = this.image;
                final CharStream input_stream15 = this.input_stream;
                final int jjimageLen15 = this.jjimageLen;
                final int lengthOfMatch15 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch15;
                image15.append(input_stream15.GetSuffix(jjimageLen15 + lengthOfMatch15));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 82: {
                final StringBuilder image16 = this.image;
                final CharStream input_stream16 = this.input_stream;
                final int jjimageLen16 = this.jjimageLen;
                final int lengthOfMatch16 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch16;
                image16.append(input_stream16.GetSuffix(jjimageLen16 + lengthOfMatch16));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 83: {
                final StringBuilder image17 = this.image;
                final CharStream input_stream17 = this.input_stream;
                final int jjimageLen17 = this.jjimageLen;
                final int lengthOfMatch17 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch17;
                image17.append(input_stream17.GetSuffix(jjimageLen17 + lengthOfMatch17));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 1);
                break;
            }
            case 85: {
                final StringBuilder image18 = this.image;
                final CharStream input_stream18 = this.input_stream;
                final int jjimageLen18 = this.jjimageLen;
                final int lengthOfMatch18 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch18;
                image18.append(input_stream18.GetSuffix(jjimageLen18 + lengthOfMatch18));
                matchedToken.image = ParserUtils.trimUrl(this.image);
                break;
            }
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
            this.jjstateSet[this.jjnewStateCnt++] = SACParserCSS21TokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(SACParserCSS21TokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    public SACParserCSS21TokenManager(final CharStream stream) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[1199];
        this.jjstateSet = new int[2398];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.input_stream = stream;
    }
    
    public SACParserCSS21TokenManager(final CharStream stream, final int lexState) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[1199];
        this.jjstateSet = new int[2398];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.ReInit(stream);
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
        int i = 1199;
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
    
    static {
        jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "<!--", "-->", "~=", "|=", null, "}", "(", ")", ".", ";", ":", "*", "/", "-", "=", "[", "]", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        jjnextStates = new int[] { 783, 784, 785, 727, 728, 729, 748, 688, 689, 714, 663, 664, 677, 650, 651, 652, 625, 626, 639, 576, 577, 614, 539, 540, 565, 502, 503, 528, 477, 478, 491, 452, 453, 466, 427, 428, 441, 402, 403, 416, 377, 378, 391, 352, 353, 366, 327, 328, 341, 302, 303, 316, 1142, 1143, 1144, 1145, 1146, 1147, 1148, 1149, 1150, 303, 316, 1151, 1152, 1153, 328, 341, 1154, 1155, 1156, 353, 1157, 1158, 1159, 378, 391, 1160, 1161, 1162, 403, 1163, 1164, 1165, 428, 1166, 1167, 1168, 453, 1169, 1170, 1171, 478, 1172, 1173, 1174, 503, 528, 1175, 1176, 1177, 540, 1178, 1179, 1180, 577, 1181, 1182, 1183, 626, 1184, 1185, 1186, 651, 1187, 1188, 1189, 664, 1190, 1191, 1192, 689, 1193, 1194, 1195, 726, 1196, 1197, 1198, 728, 729, 748, 714, 677, 652, 639, 614, 565, 491, 466, 441, 416, 366, 293, 294, 295, 44, 296, 45, 297, 46, 298, 47, 763, 782, 802, 816, 300, 301, 302, 327, 352, 377, 402, 427, 452, 477, 502, 539, 576, 625, 650, 663, 688, 725, 727, 68, 69, 77, 174, 23, 24, 25, 1, 2, 3, 1, 9, 12, 13, 15, 18, 10, 11, 2, 3, 1, 10, 11, 2, 3, 23, 31, 34, 35, 37, 40, 32, 33, 24, 25, 23, 32, 33, 24, 25, 49, 53, 57, 58, 60, 63, 55, 56, 50, 49, 55, 56, 50, 72, 73, 76, 73, 75, 76, 89, 90, 94, 91, 92, 95, 96, 94, 91, 92, 94, 91, 92, 95, 105, 102, 103, 106, 107, 105, 102, 103, 105, 102, 103, 106, 111, 112, 84, 97, 116, 113, 114, 117, 118, 116, 113, 114, 116, 113, 114, 117, 122, 123, 83, 108, 127, 124, 125, 128, 129, 127, 124, 125, 127, 124, 125, 128, 133, 134, 82, 119, 138, 135, 136, 139, 140, 138, 135, 136, 138, 135, 136, 139, 144, 145, 81, 130, 149, 146, 147, 150, 151, 149, 146, 147, 149, 146, 147, 150, 155, 156, 80, 141, 160, 157, 158, 161, 162, 160, 157, 158, 160, 157, 158, 161, 171, 168, 169, 172, 173, 171, 168, 169, 171, 168, 169, 172, 177, 178, 78, 163, 182, 179, 180, 183, 184, 182, 179, 180, 182, 179, 180, 183, 187, 207, 229, 189, 190, 251, 187, 188, 189, 190, 187, 193, 197, 198, 200, 203, 195, 189, 190, 196, 187, 195, 189, 190, 196, 208, 209, 210, 208, 216, 219, 220, 222, 225, 217, 218, 209, 210, 208, 217, 218, 209, 210, 230, 231, 232, 230, 238, 241, 242, 244, 247, 239, 240, 231, 232, 230, 239, 240, 231, 232, 187, 207, 229, 188, 189, 190, 251, 262, 263, 258, 267, 264, 265, 268, 269, 267, 264, 265, 267, 264, 265, 268, 278, 275, 276, 279, 280, 278, 275, 276, 278, 275, 276, 279, 284, 285, 256, 270, 289, 286, 287, 290, 291, 289, 286, 287, 289, 286, 287, 290, 313, 310, 311, 314, 315, 313, 310, 311, 313, 310, 311, 314, 319, 320, 304, 305, 324, 321, 322, 325, 326, 324, 321, 322, 324, 321, 322, 325, 333, 334, 338, 335, 336, 339, 340, 338, 335, 336, 338, 335, 336, 339, 344, 345, 329, 330, 349, 346, 347, 350, 351, 349, 346, 347, 349, 346, 347, 350, 358, 359, 363, 360, 361, 364, 365, 363, 360, 361, 363, 360, 361, 364, 369, 370, 354, 355, 374, 371, 372, 375, 376, 374, 371, 372, 374, 371, 372, 375, 388, 385, 386, 389, 390, 388, 385, 386, 388, 385, 386, 389, 394, 395, 379, 380, 399, 396, 397, 400, 401, 399, 396, 397, 399, 396, 397, 400, 413, 410, 411, 414, 415, 413, 410, 411, 413, 410, 411, 414, 424, 421, 422, 425, 426, 424, 421, 422, 424, 421, 422, 425, 438, 435, 436, 439, 440, 438, 435, 436, 438, 435, 436, 439, 444, 445, 429, 430, 449, 446, 447, 450, 451, 449, 446, 447, 449, 446, 447, 450, 458, 459, 463, 460, 461, 464, 465, 463, 460, 461, 463, 460, 461, 464, 469, 470, 454, 455, 474, 471, 472, 475, 476, 474, 471, 472, 474, 471, 472, 475, 483, 484, 488, 485, 486, 489, 490, 488, 485, 486, 488, 485, 486, 489, 494, 495, 479, 480, 499, 496, 497, 500, 501, 499, 496, 497, 499, 496, 497, 500, 509, 510, 514, 511, 512, 515, 516, 514, 511, 512, 514, 511, 512, 515, 520, 521, 505, 506, 525, 522, 523, 526, 527, 525, 522, 523, 525, 522, 523, 526, 531, 532, 504, 517, 536, 533, 534, 537, 538, 536, 533, 534, 536, 533, 534, 537, 546, 547, 551, 548, 549, 552, 553, 551, 548, 549, 551, 548, 549, 552, 557, 558, 542, 543, 562, 559, 560, 563, 564, 562, 559, 560, 562, 559, 560, 563, 568, 569, 541, 554, 573, 570, 571, 574, 575, 573, 570, 571, 573, 570, 571, 574, 584, 585, 589, 586, 587, 590, 591, 589, 586, 587, 589, 586, 587, 590, 595, 596, 580, 581, 600, 597, 598, 601, 602, 600, 597, 598, 600, 597, 598, 601, 606, 607, 579, 592, 611, 608, 609, 612, 613, 611, 608, 609, 611, 608, 609, 612, 617, 618, 578, 603, 622, 619, 620, 623, 624, 622, 619, 620, 622, 619, 620, 623, 631, 632, 636, 633, 634, 637, 638, 636, 633, 634, 636, 633, 634, 637, 647, 644, 645, 648, 649, 647, 644, 645, 647, 644, 645, 648, 655, 656, 660, 657, 658, 661, 662, 660, 657, 658, 660, 657, 658, 661, 674, 671, 672, 675, 676, 674, 671, 672, 674, 671, 672, 675, 680, 681, 665, 666, 685, 682, 683, 686, 687, 685, 682, 683, 685, 682, 683, 686, 700, 697, 698, 701, 702, 700, 697, 698, 700, 697, 698, 701, 706, 707, 691, 692, 711, 708, 709, 712, 713, 711, 708, 709, 711, 708, 709, 712, 722, 719, 720, 723, 724, 722, 719, 720, 722, 719, 720, 723, 730, 734, 738, 739, 741, 744, 736, 737, 731, 730, 736, 737, 731, 750, 752, 753, 755, 758, 751, 737, 730, 731, 751, 737, 730, 731, 764, 768, 772, 773, 775, 778, 770, 771, 765, 764, 770, 771, 765, 783, 788, 792, 793, 795, 798, 790, 791, 784, 785, 783, 790, 791, 784, 785, 804, 806, 807, 809, 812, 805, 791, 783, 784, 785, 805, 791, 783, 784, 785, 818, 820, 821, 823, 826, 819, 771, 764, 765, 819, 771, 764, 765, 841, 842, 846, 843, 844, 847, 848, 846, 843, 844, 846, 843, 844, 847, 852, 853, 837, 838, 857, 854, 855, 858, 859, 857, 854, 855, 857, 854, 855, 858, 863, 864, 836, 849, 868, 865, 866, 869, 870, 868, 865, 866, 868, 865, 866, 869, 874, 875, 835, 860, 879, 876, 877, 880, 881, 879, 876, 877, 879, 876, 877, 880, 890, 887, 888, 891, 892, 890, 887, 888, 890, 887, 888, 891, 900, 901, 905, 902, 903, 906, 907, 905, 902, 903, 905, 902, 903, 906, 911, 912, 896, 897, 916, 913, 914, 917, 918, 916, 913, 914, 916, 913, 914, 917, 922, 923, 895, 908, 927, 924, 925, 928, 929, 927, 924, 925, 927, 924, 925, 928, 938, 939, 943, 940, 941, 944, 945, 943, 940, 941, 943, 940, 941, 944, 949, 950, 934, 935, 954, 951, 952, 955, 956, 954, 951, 952, 954, 951, 952, 955, 960, 961, 933, 946, 965, 962, 963, 966, 967, 965, 962, 963, 965, 962, 963, 966, 971, 972, 932, 957, 976, 973, 974, 977, 978, 976, 973, 974, 976, 973, 974, 977, 989, 990, 994, 991, 992, 995, 996, 994, 991, 992, 994, 991, 992, 995, 1000, 1001, 985, 986, 1005, 1002, 1003, 1006, 1007, 1005, 1002, 1003, 1005, 1002, 1003, 1006, 1011, 1012, 984, 997, 1016, 1013, 1014, 1017, 1018, 1016, 1013, 1014, 1016, 1013, 1014, 1017, 1022, 1023, 983, 1008, 1027, 1024, 1025, 1028, 1029, 1027, 1024, 1025, 1027, 1024, 1025, 1028, 1033, 1034, 982, 1019, 1038, 1035, 1036, 1039, 1040, 1038, 1035, 1036, 1038, 1035, 1036, 1039, 1044, 1045, 981, 1030, 1049, 1046, 1047, 1050, 1051, 1049, 1046, 1047, 1049, 1046, 1047, 1050, 1055, 1056, 980, 1041, 1060, 1057, 1058, 1061, 1062, 1060, 1057, 1058, 1060, 1057, 1058, 1061, 1064, 1083, 1065, 1069, 1073, 1074, 1076, 1079, 1071, 1072, 1066, 1065, 1071, 1072, 1066, 1085, 1087, 1088, 1090, 1093, 1086, 1072, 1065, 1066, 1086, 1072, 1065, 1066, 1105, 1102, 1103, 1106, 1107, 1105, 1102, 1103, 1105, 1102, 1103, 1106, 1111, 1112, 894, 919, 1116, 1113, 1114, 1117, 1118, 1116, 1113, 1114, 1116, 1113, 1114, 1117, 1122, 1123, 833, 882, 1127, 1124, 1125, 1128, 1129, 1127, 1124, 1125, 1127, 1124, 1125, 1128, 1138, 1135, 1136, 1139, 1140, 1138, 1135, 1136, 1138, 1135, 1136, 1139, 1148, 303, 316, 1151, 328, 341, 1154, 353, 366, 1157, 378, 391, 1160, 403, 416, 1163, 428, 441, 1166, 453, 466, 1169, 478, 491, 1172, 503, 528, 1175, 540, 565, 1178, 577, 614, 1181, 626, 639, 1184, 651, 652, 1187, 664, 677, 1190, 689, 714, 1196, 728, 729, 748, 764, 783, 784, 785, 765, 766, 1135, 1136, 254, 786, 803, 1137, 817, 832, 893, 930, 979, 1052, 1063, 1064, 1083, 1097, 1108, 1119, 4, 6, 7, 8, 26, 28, 29, 30, 51, 52, 71, 72, 83, 108, 91, 92, 85, 93, 102, 103, 84, 104, 100, 101, 85, 86, 113, 114, 115, 124, 125, 82, 126, 135, 136, 81, 137, 146, 147, 80, 148, 157, 158, 79, 159, 168, 169, 78, 170, 166, 167, 79, 152, 179, 180, 77, 181, 191, 192, 211, 213, 214, 215, 233, 235, 236, 237, 264, 265, 257, 266, 275, 276, 256, 277, 273, 274, 257, 259, 286, 287, 288, 310, 311, 304, 312, 308, 309, 321, 322, 323, 335, 336, 329, 337, 346, 347, 348, 360, 361, 354, 362, 371, 372, 353, 373, 385, 386, 379, 387, 383, 384, 396, 397, 398, 410, 411, 404, 412, 408, 409, 421, 422, 403, 423, 419, 420, 404, 405, 435, 436, 429, 437, 433, 434, 446, 447, 428, 448, 460, 461, 454, 462, 471, 472, 453, 473, 479, 480, 485, 486, 487, 496, 497, 478, 498, 504, 517, 511, 512, 505, 513, 522, 523, 524, 533, 534, 535, 541, 554, 542, 543, 548, 549, 550, 559, 560, 561, 570, 571, 540, 572, 579, 592, 580, 581, 586, 587, 588, 597, 598, 599, 608, 609, 578, 610, 619, 620, 577, 621, 633, 634, 627, 635, 644, 645, 626, 646, 642, 643, 627, 628, 657, 658, 651, 659, 671, 672, 665, 673, 669, 670, 682, 683, 664, 684, 697, 698, 691, 699, 695, 696, 708, 709, 690, 710, 719, 720, 689, 721, 717, 718, 690, 703, 843, 844, 837, 845, 854, 855, 836, 856, 865, 866, 835, 867, 876, 877, 834, 878, 887, 888, 833, 889, 885, 886, 834, 871, 894, 919, 896, 897, 902, 903, 904, 913, 914, 895, 915, 924, 925, 926, 932, 957, 934, 935, 940, 941, 942, 951, 952, 933, 953, 962, 963, 964, 973, 974, 975, 981, 1030, 984, 997, 991, 992, 985, 993, 1002, 1003, 1004, 1013, 1014, 983, 1015, 1024, 1025, 982, 1026, 1035, 1036, 1037, 1046, 1047, 980, 1048, 1057, 1058, 1059, 1102, 1103, 930, 1104, 1100, 1101, 931, 968, 1113, 1114, 893, 1115, 1124, 1125, 832, 1126, 1133, 1134, 255, 281 };
        lexStateNames = new String[] { "DEFAULT", "COMMENT" };
        jjnewLexState = new int[] { -1, -1, -1, 1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { -4398039302137L, 134217727L };
        jjtoSkip = new long[] { 16L, 0L };
        jjtoSpecial = new long[] { 0L, 0L };
        jjtoMore = new long[] { 40L, 0L };
    }
}
