package com.steadystate.css.parser;

import java.io.IOException;
import java.io.PrintStream;

public class SACParserCSS3TokenManager implements SACParserCSS3Constants
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
                if ((active0 & 0x800000000000000L) != 0x0L) {
                    return 1361;
                }
                if ((active0 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 0;
                    return 1362;
                }
                if ((active0 & 0x2A0000L) != 0x0L || (active1 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 0;
                    return 1363;
                }
                if ((active0 & 0x2000000000000L) != 0x0L || (active1 & 0x1L) != 0x0L) {
                    return 729;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 1;
                    return 1364;
                }
                if ((active0 & 0x2A0000L) != 0x0L || (active1 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 1;
                    return 1363;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x20000L) != 0x0L) {
                    return 1363;
                }
                if ((active0 & 0x40000L) != 0x0L) {
                    return 1365;
                }
                if ((active0 & 0x280000L) != 0x0L || (active1 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 2;
                    return 1363;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x80000L) != 0x0L) {
                    return 1363;
                }
                if ((active0 & 0x200000L) != 0x0L || (active1 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 3;
                    return 1363;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x200000L) != 0x0L || (active1 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 4;
                    return 1363;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x200000L) != 0x0L || (active1 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 5;
                    return 1363;
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
            case 36: {
                return this.jjMoveStringLiteralDfa1_0(9007199254740992L, 0L);
            }
            case 40: {
                return this.jjStopAtPos(0, 57);
            }
            case 41: {
                return this.jjStopAtPos(0, 58);
            }
            case 42: {
                this.jjmatchedKind = 62;
                this.jjmatchedPos = 0;
                return this.jjMoveStringLiteralDfa1_0(18014398509481984L, 0L);
            }
            case 45: {
                this.jjmatchedKind = 64;
                this.jjmatchedPos = 0;
                return this.jjMoveStringLiteralDfa1_0(562949953421312L, 0L);
            }
            case 46: {
                return this.jjStartNfaWithStates_0(0, 59, 1361);
            }
            case 47: {
                this.jjmatchedKind = 63;
                this.jjmatchedPos = 0;
                return this.jjMoveStringLiteralDfa1_0(8L, 0L);
            }
            case 58: {
                return this.jjStopAtPos(0, 61);
            }
            case 59: {
                return this.jjStopAtPos(0, 60);
            }
            case 60: {
                return this.jjMoveStringLiteralDfa1_0(281474976710656L, 0L);
            }
            case 61: {
                return this.jjStopAtPos(0, 65);
            }
            case 91: {
                return this.jjStopAtPos(0, 66);
            }
            case 93: {
                return this.jjStopAtPos(0, 67);
            }
            case 94: {
                return this.jjMoveStringLiteralDfa1_0(4503599627370496L, 0L);
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa1_0(131072L, 0L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa1_0(2097152L, 0L);
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa1_0(262144L, 0L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa1_0(524288L, 0L);
            }
            case 80:
            case 112: {
                return this.jjMoveStringLiteralDfa1_0(0L, 4398046511104L);
            }
            case 124: {
                return this.jjMoveStringLiteralDfa1_0(2251799813685248L, 0L);
            }
            case 125: {
                return this.jjStopAtPos(0, 56);
            }
            case 126: {
                this.jjmatchedKind = 70;
                this.jjmatchedPos = 0;
                return this.jjMoveStringLiteralDfa1_0(1125899906842624L, 0L);
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
                return this.jjMoveStringLiteralDfa2_0(active0, 281474976710656L, active1, 0L);
            }
            case 42: {
                if ((active0 & 0x8L) != 0x0L) {
                    return this.jjStopAtPos(1, 3);
                }
                break;
            }
            case 45: {
                return this.jjMoveStringLiteralDfa2_0(active0, 562949953421312L, active1, 0L);
            }
            case 61: {
                if ((active0 & 0x4000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 50);
                }
                if ((active0 & 0x8000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 51);
                }
                if ((active0 & 0x10000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 52);
                }
                if ((active0 & 0x20000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 53);
                }
                if ((active0 & 0x40000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 54);
                }
                break;
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa2_0(active0, 2752512L, active1, 0L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa2_0(active0, 262144L, active1, 0L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 4398046511104L);
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
                return this.jjMoveStringLiteralDfa3_0(active0, 281474976710656L, active1, 0L);
            }
            case 62: {
                if ((active0 & 0x2000000000000L) != 0x0L) {
                    return this.jjStopAtPos(2, 49);
                }
                break;
            }
            case 68:
            case 100: {
                if ((active0 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 17, 1363);
                }
                break;
            }
            case 72:
            case 104: {
                return this.jjMoveStringLiteralDfa3_0(active0, 2097152L, active1, 0L);
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa3_0(active0, 524288L, active1, 0L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 4398046511104L);
            }
            case 84:
            case 116: {
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 18, 1365);
                }
                break;
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
                if ((active0 & 0x1000000000000L) != 0x0L) {
                    return this.jjStopAtPos(3, 48);
                }
                break;
            }
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa4_0(active0, 2097152L, active1, 0L);
            }
            case 71:
            case 103: {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 4398046511104L);
            }
            case 89:
            case 121: {
                if ((active0 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 19, 1363);
                }
                break;
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
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 4398046511104L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa5_0(active0, 2097152L, active1, 0L);
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
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 4398046511104L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa6_0(active0, 2097152L, active1, 0L);
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
                if ((active1 & 0x40000000000L) != 0x0L) {
                    return this.jjStopAtPos(6, 106);
                }
                break;
            }
            case 84:
            case 116: {
                if ((active0 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 21, 1363);
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
        this.jjnewStateCnt = 1361;
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
                            case 1364: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                }
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 1362: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                }
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 1363: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                }
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 729: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                }
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 1365: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                }
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 0: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddStates(1680, 1684);
                                    break;
                                }
                                break;
                            }
                            case 1:
                            case 7: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(193, 195);
                                break;
                            }
                            case 23:
                            case 29: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(190, 192);
                                break;
                            }
                            case 49:
                            case 51: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 72) {
                                        kind = 72;
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
                                this.jjAddStates(1722, 1723);
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
                            case 695:
                            case 696:
                            case 698: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 97) {
                                        kind = 97;
                                    }
                                    this.jjCheckNAddTwoStates(696, 697);
                                    break;
                                }
                                break;
                            }
                            case 730:
                            case 732: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                    break;
                                }
                                break;
                            }
                            case 748:
                            case 752: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 749: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 1127:
                            case 1128:
                            case 1130: {
                                if (jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    if (kind > 104) {
                                        kind = 104;
                                    }
                                    this.jjCheckNAddTwoStates(1128, 1129);
                                    break;
                                }
                                break;
                            }
                            case 1210:
                            case 1214: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(1537, 1540);
                                break;
                            }
                            case 1231:
                            case 1237: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(1556, 1558);
                                break;
                            }
                            case 1253:
                            case 1259: {
                                if (!jjCanMove_0(hiByte, i2, i3, l1, l2)) {
                                    break;
                                }
                                this.jjCheckNAddStates(1574, 1576);
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
                            case 1364: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar == 92) {
                                        this.jjCheckNAddTwoStates(732, 733);
                                    }
                                }
                                else {
                                    this.jjCheckNAddStates(0, 2);
                                }
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar == 92) {
                                        this.jjCheckNAddStates(1672, 1675);
                                    }
                                }
                                else {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                }
                                if ((0x10000000100000L & j) != 0x0L) {
                                    this.jjstateSet[this.jjnewStateCnt++] = 188;
                                    break;
                                }
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(752, 753);
                                break;
                            }
                            case 1362: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar == 92) {
                                        this.jjCheckNAddTwoStates(732, 733);
                                    }
                                }
                                else {
                                    this.jjCheckNAddStates(0, 2);
                                }
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar == 92) {
                                        this.jjCheckNAddStates(1676, 1679);
                                    }
                                }
                                else {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                }
                                if ((0x800000008000L & j) != 0x0L) {
                                    this.jjCheckNAddTwoStates(187, 189);
                                    break;
                                }
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(752, 753);
                                break;
                            }
                            case 1363: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar == 92) {
                                        this.jjCheckNAddTwoStates(732, 733);
                                    }
                                }
                                else {
                                    this.jjCheckNAddStates(0, 2);
                                }
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                    break;
                                }
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(752, 753);
                                break;
                            }
                            case 729: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar == 92) {
                                        this.jjCheckNAddTwoStates(732, 783);
                                    }
                                }
                                else {
                                    this.jjCheckNAddStates(0, 2);
                                }
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                    break;
                                }
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(752, 769);
                                break;
                            }
                            case 1365: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar == 92) {
                                        this.jjCheckNAddTwoStates(732, 733);
                                    }
                                }
                                else {
                                    this.jjCheckNAddStates(0, 2);
                                }
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                    break;
                                }
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(752, 753);
                                break;
                            }
                            case 0: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    if (this.curChar != 92) {
                                        if (this.curChar != 64) {
                                            if (this.curChar == 123) {
                                                if (kind > 55) {
                                                    kind = 55;
                                                }
                                            }
                                        }
                                        else {
                                            this.jjCheckNAddStates(1697, 1709);
                                        }
                                    }
                                    else {
                                        this.jjCheckNAddStates(1685, 1696);
                                    }
                                }
                                else {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddStates(1680, 1684);
                                }
                                if ((0x20000000200000L & j) != 0x0L) {
                                    this.jjAddStates(1710, 1711);
                                    break;
                                }
                                if ((0x100000001000L & j) != 0x0L) {
                                    this.jjCheckNAddTwoStates(212, 238);
                                    break;
                                }
                                if ((0x400000004000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(186, 200);
                                break;
                            }
                            case 1: {
                                if ((0xFFFFFFFFEFFFFFFFL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(193, 195);
                                break;
                            }
                            case 3: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1712, 1715);
                                break;
                            }
                            case 7: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(193, 195);
                                break;
                            }
                            case 8: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(196, 205);
                                break;
                            }
                            case 9: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(206, 210);
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
                                this.jjCheckNAddStates(190, 192);
                                break;
                            }
                            case 25: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1716, 1719);
                                break;
                            }
                            case 29: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(190, 192);
                                break;
                            }
                            case 30: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(211, 220);
                                break;
                            }
                            case 31: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(221, 225);
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
                                if (kind <= 55) {
                                    break;
                                }
                                kind = 55;
                                break;
                            }
                            case 49: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 72) {
                                        kind = 72;
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
                                this.jjAddStates(1720, 1721);
                                break;
                            }
                            case 51: {
                                if ((0xFFFFFF81FFFFFF81L & j) != 0x0L) {
                                    if (kind > 72) {
                                        kind = 72;
                                    }
                                    this.jjCheckNAddTwoStates(49, 50);
                                    break;
                                }
                                break;
                            }
                            case 52: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 72) {
                                        kind = 72;
                                    }
                                    this.jjCheckNAddStates(226, 234);
                                    break;
                                }
                                break;
                            }
                            case 53: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 72) {
                                        kind = 72;
                                    }
                                    this.jjCheckNAddStates(235, 238);
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
                                this.jjAddStates(1722, 1723);
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
                                this.jjAddStates(1724, 1725);
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
                                if (kind <= 78) {
                                    break;
                                }
                                kind = 78;
                                break;
                            }
                            case 86: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1726, 1729);
                                break;
                            }
                            case 97: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1730, 1733);
                                break;
                            }
                            case 98: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1734, 1737);
                                break;
                            }
                            case 108: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1738, 1740);
                                break;
                            }
                            case 119: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1741, 1744);
                                break;
                            }
                            case 130: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1745, 1748);
                                break;
                            }
                            case 141: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1749, 1752);
                                break;
                            }
                            case 152: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1753, 1756);
                                break;
                            }
                            case 163: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1757, 1760);
                                break;
                            }
                            case 164: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1761, 1764);
                                break;
                            }
                            case 174: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1765, 1768);
                                break;
                            }
                            case 185: {
                                if ((0x400000004000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(186, 200);
                                break;
                            }
                            case 186: {
                                if ((0x800000008000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(187, 189);
                                break;
                            }
                            case 187: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 188;
                                break;
                            }
                            case 189: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1672, 1675);
                                break;
                            }
                            case 200: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1676, 1679);
                                break;
                            }
                            case 211: {
                                if ((0x100000001000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(212, 238);
                                break;
                            }
                            case 212: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(213, 227);
                                break;
                            }
                            case 213: {
                                if ((0x400000004000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(214, 216);
                                break;
                            }
                            case 214: {
                                if ((0x8000000080L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 215;
                                break;
                            }
                            case 216: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1769, 1772);
                                break;
                            }
                            case 227: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1773, 1776);
                                break;
                            }
                            case 228: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1777, 1780);
                                break;
                            }
                            case 238: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1781, 1783);
                                break;
                            }
                            case 260: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(261, 262);
                                break;
                            }
                            case 261: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 79) {
                                    break;
                                }
                                kind = 79;
                                break;
                            }
                            case 262: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1784, 1787);
                                break;
                            }
                            case 263: {
                                if ((0x1000000010L & j) != 0x0L) {
                                    if (kind > 79) {
                                        kind = 79;
                                    }
                                    this.jjAddStates(1788, 1789);
                                    break;
                                }
                                break;
                            }
                            case 273: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1790, 1792);
                                break;
                            }
                            case 285: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(286, 287);
                                break;
                            }
                            case 286: {
                                if ((0x100000001000000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 80) {
                                    break;
                                }
                                kind = 80;
                                break;
                            }
                            case 287: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1793, 1796);
                                break;
                            }
                            case 298: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1797, 1799);
                                break;
                            }
                            case 310: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(311, 312);
                                break;
                            }
                            case 311: {
                                if ((0x100000001000000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 81) {
                                    break;
                                }
                                kind = 81;
                                break;
                            }
                            case 312: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1800, 1803);
                                break;
                            }
                            case 323: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1804, 1807);
                                break;
                            }
                            case 335: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(336, 337);
                                break;
                            }
                            case 336: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 82) {
                                    break;
                                }
                                kind = 82;
                                break;
                            }
                            case 337: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1808, 1811);
                                break;
                            }
                            case 338: {
                                if ((0x1000000010L & j) != 0x0L) {
                                    if (kind > 82) {
                                        kind = 82;
                                    }
                                    this.jjAddStates(1812, 1813);
                                    break;
                                }
                                break;
                            }
                            case 348: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1814, 1816);
                                break;
                            }
                            case 360: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(361, 362);
                                break;
                            }
                            case 361: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 83) {
                                    break;
                                }
                                kind = 83;
                                break;
                            }
                            case 362: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1817, 1820);
                                break;
                            }
                            case 363: {
                                if ((0x1000000010L & j) != 0x0L) {
                                    if (kind > 83) {
                                        kind = 83;
                                    }
                                    this.jjAddStates(1821, 1822);
                                    break;
                                }
                                break;
                            }
                            case 373: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1823, 1826);
                                break;
                            }
                            case 374: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1827, 1830);
                                break;
                            }
                            case 385: {
                                if ((0x20000000200L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(386, 387);
                                break;
                            }
                            case 386: {
                                if ((0x400000004000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 84) {
                                    break;
                                }
                                kind = 84;
                                break;
                            }
                            case 387: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1831, 1834);
                                break;
                            }
                            case 388: {
                                if ((0x2000000020L & j) != 0x0L) {
                                    if (kind > 84) {
                                        kind = 84;
                                    }
                                    this.jjAddStates(1835, 1836);
                                    break;
                                }
                                break;
                            }
                            case 398: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1837, 1840);
                                break;
                            }
                            case 410: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(411, 412);
                                break;
                            }
                            case 411: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 85) {
                                    break;
                                }
                                kind = 85;
                                break;
                            }
                            case 412: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1841, 1844);
                                break;
                            }
                            case 423: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1845, 1848);
                                break;
                            }
                            case 435: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1849, 1850);
                                break;
                            }
                            case 436: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 86) {
                                    break;
                                }
                                kind = 86;
                                break;
                            }
                            case 437: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1851, 1853);
                                break;
                            }
                            case 448: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1854, 1857);
                                break;
                            }
                            case 460: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1858, 1859);
                                break;
                            }
                            case 461: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(462, 463);
                                break;
                            }
                            case 462: {
                                if ((0x8000000080L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 87) {
                                    break;
                                }
                                kind = 87;
                                break;
                            }
                            case 463: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1860, 1863);
                                break;
                            }
                            case 474: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1864, 1866);
                                break;
                            }
                            case 485: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1867, 1869);
                                break;
                            }
                            case 497: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1870, 1871);
                                break;
                            }
                            case 498: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1872, 1873);
                                break;
                            }
                            case 499: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 88) {
                                    break;
                                }
                                kind = 88;
                                break;
                            }
                            case 500: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1874, 1876);
                                break;
                            }
                            case 511: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1877, 1879);
                                break;
                            }
                            case 522: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1880, 1883);
                                break;
                            }
                            case 534: {
                                if ((0x8000000080L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(535, 560);
                                break;
                            }
                            case 535: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1884, 1885);
                                break;
                            }
                            case 536: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1886, 1887);
                                break;
                            }
                            case 537: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 89) {
                                    break;
                                }
                                kind = 89;
                                break;
                            }
                            case 538: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1888, 1890);
                                break;
                            }
                            case 549: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1891, 1893);
                                break;
                            }
                            case 560: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1894, 1897);
                                break;
                            }
                            case 571: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1898, 1901);
                                break;
                            }
                            case 583: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(584, 585);
                                break;
                            }
                            case 584: {
                                if ((0x8000000080000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 90) {
                                    break;
                                }
                                kind = 90;
                                break;
                            }
                            case 585: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1902, 1905);
                                break;
                            }
                            case 596: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1906, 1909);
                                break;
                            }
                            case 597: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1910, 1913);
                                break;
                            }
                            case 608: {
                                if ((0x8000000080000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 91) {
                                    break;
                                }
                                kind = 91;
                                break;
                            }
                            case 609: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1914, 1917);
                                break;
                            }
                            case 621: {
                                if ((0x10000000100L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(622, 623);
                                break;
                            }
                            case 622: {
                                if ((0x400000004000000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 92) {
                                    break;
                                }
                                kind = 92;
                                break;
                            }
                            case 623: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1918, 1921);
                                break;
                            }
                            case 624: {
                                if ((0x200000002L & j) != 0x0L) {
                                    if (kind > 92) {
                                        kind = 92;
                                    }
                                    this.jjAddStates(1922, 1923);
                                    break;
                                }
                                break;
                            }
                            case 634: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1924, 1927);
                                break;
                            }
                            case 646: {
                                if ((0x80000000800L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(647, 660);
                                break;
                            }
                            case 647: {
                                if ((0x10000000100L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(648, 649);
                                break;
                            }
                            case 648: {
                                if ((0x400000004000000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 93) {
                                    break;
                                }
                                kind = 93;
                                break;
                            }
                            case 649: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1928, 1931);
                                break;
                            }
                            case 650: {
                                if ((0x200000002L & j) != 0x0L) {
                                    if (kind > 93) {
                                        kind = 93;
                                    }
                                    this.jjAddStates(1932, 1933);
                                    break;
                                }
                                break;
                            }
                            case 660: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1934, 1937);
                                break;
                            }
                            case 671: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1938, 1941);
                                break;
                            }
                            case 672: {
                                if ((0x400000004L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1942, 1945);
                                break;
                            }
                            case 683: {
                                if ((0x20000000200L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 94) {
                                    break;
                                }
                                kind = 94;
                                break;
                            }
                            case 684: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 683;
                                break;
                            }
                            case 685: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 684;
                                break;
                            }
                            case 687: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 95) {
                                    break;
                                }
                                kind = 95;
                                break;
                            }
                            case 688: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 687;
                                break;
                            }
                            case 689: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 688;
                                break;
                            }
                            case 690: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 689;
                                break;
                            }
                            case 695:
                            case 696: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 97) {
                                        kind = 97;
                                    }
                                    this.jjCheckNAddTwoStates(696, 697);
                                    break;
                                }
                                break;
                            }
                            case 697: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(698, 699);
                                break;
                            }
                            case 698: {
                                if ((0xFFFFFF81FFFFFF81L & j) != 0x0L) {
                                    if (kind > 97) {
                                        kind = 97;
                                    }
                                    this.jjCheckNAddTwoStates(696, 697);
                                    break;
                                }
                                break;
                            }
                            case 699: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 97) {
                                        kind = 97;
                                    }
                                    this.jjCheckNAddStates(943, 951);
                                    break;
                                }
                                break;
                            }
                            case 700: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 97) {
                                        kind = 97;
                                    }
                                    this.jjCheckNAddStates(952, 955);
                                    break;
                                }
                                break;
                            }
                            case 704:
                            case 706:
                            case 709:
                            case 713: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(700);
                                break;
                            }
                            case 705: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 706;
                                break;
                            }
                            case 707: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 708;
                                break;
                            }
                            case 708: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 709;
                                break;
                            }
                            case 710: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 711;
                                break;
                            }
                            case 711: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 712;
                                break;
                            }
                            case 712: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 713;
                                break;
                            }
                            case 714: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(698, 715);
                                break;
                            }
                            case 715: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 97) {
                                        kind = 97;
                                    }
                                    this.jjCheckNAddStates(956, 964);
                                    break;
                                }
                                break;
                            }
                            case 716: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 97) {
                                        kind = 97;
                                    }
                                    this.jjCheckNAddStates(965, 968);
                                    break;
                                }
                                break;
                            }
                            case 718:
                            case 720:
                            case 723:
                            case 727: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(716);
                                break;
                            }
                            case 719: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 720;
                                break;
                            }
                            case 721: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 722;
                                break;
                            }
                            case 722: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 723;
                                break;
                            }
                            case 724: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 725;
                                break;
                            }
                            case 725: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 726;
                                break;
                            }
                            case 726: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 727;
                                break;
                            }
                            case 730: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 22) {
                                        kind = 22;
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
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddTwoStates(730, 731);
                                    break;
                                }
                                break;
                            }
                            case 733: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddStates(969, 977);
                                    break;
                                }
                                break;
                            }
                            case 734: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddStates(978, 981);
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
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 749: {
                                if ((0x7FFFFFE87FFFFFEL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 751: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(752, 753);
                                break;
                            }
                            case 752: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(0, 2);
                                break;
                            }
                            case 753: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(982, 991);
                                break;
                            }
                            case 754: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(992, 996);
                                break;
                            }
                            case 758:
                            case 760:
                            case 763:
                            case 767: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(754);
                                break;
                            }
                            case 759: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 760;
                                break;
                            }
                            case 761: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 762;
                                break;
                            }
                            case 762: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 763;
                                break;
                            }
                            case 764: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 765;
                                break;
                            }
                            case 765: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 766;
                                break;
                            }
                            case 766: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 767;
                                break;
                            }
                            case 768: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(752, 769);
                                break;
                            }
                            case 769: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(997, 1006);
                                break;
                            }
                            case 770: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1007, 1011);
                                break;
                            }
                            case 772:
                            case 774:
                            case 777:
                            case 781: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(770);
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
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(732, 783);
                                break;
                            }
                            case 783: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddStates(1012, 1020);
                                    break;
                                }
                                break;
                            }
                            case 784: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddStates(1021, 1024);
                                    break;
                                }
                                break;
                            }
                            case 786:
                            case 788:
                            case 791:
                            case 795: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(784);
                                break;
                            }
                            case 787: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 788;
                                break;
                            }
                            case 789: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 790;
                                break;
                            }
                            case 790: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 791;
                                break;
                            }
                            case 792: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 793;
                                break;
                            }
                            case 793: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 794;
                                break;
                            }
                            case 794: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 795;
                                break;
                            }
                            case 796: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 22) {
                                        kind = 22;
                                    }
                                    this.jjCheckNAddStates(1680, 1684);
                                    break;
                                }
                                break;
                            }
                            case 797: {
                                if (this.curChar != 64) {
                                    break;
                                }
                                this.jjCheckNAddStates(1697, 1709);
                                break;
                            }
                            case 798: {
                                if ((0x20000000200L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(799, 848);
                                break;
                            }
                            case 799: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(800, 837);
                                break;
                            }
                            case 800: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(801, 826);
                                break;
                            }
                            case 801: {
                                if ((0x800000008000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(802, 815);
                                break;
                            }
                            case 802: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(803, 804);
                                break;
                            }
                            case 803: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 73) {
                                    break;
                                }
                                kind = 73;
                                break;
                            }
                            case 804: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1946, 1949);
                                break;
                            }
                            case 815: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1950, 1953);
                                break;
                            }
                            case 826: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1954, 1957);
                                break;
                            }
                            case 837: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1958, 1961);
                                break;
                            }
                            case 848: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1962, 1965);
                                break;
                            }
                            case 849: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1966, 1969);
                                break;
                            }
                            case 859: {
                                if ((0x1000000010000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1970, 1971);
                                break;
                            }
                            case 860: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(861, 874);
                                break;
                            }
                            case 861: {
                                if ((0x8000000080L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1972, 1973);
                                break;
                            }
                            case 862: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 74) {
                                    break;
                                }
                                kind = 74;
                                break;
                            }
                            case 863: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1974, 1976);
                                break;
                            }
                            case 874: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1977, 1980);
                                break;
                            }
                            case 885: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1981, 1983);
                                break;
                            }
                            case 896: {
                                if ((0x200000002000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(897, 934);
                                break;
                            }
                            case 897: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1984, 1985);
                                break;
                            }
                            case 898: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(899, 912);
                                break;
                            }
                            case 899: {
                                if ((0x20000000200L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1986, 1987);
                                break;
                            }
                            case 900: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 75) {
                                    break;
                                }
                                kind = 75;
                                break;
                            }
                            case 901: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1988, 1990);
                                break;
                            }
                            case 912: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1991, 1994);
                                break;
                            }
                            case 923: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1995, 1997);
                                break;
                            }
                            case 934: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(1998, 2000);
                                break;
                            }
                            case 945: {
                                if ((0x4000000040L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(946, 1020);
                                break;
                            }
                            case 946: {
                                if ((0x800000008000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(947, 1009);
                                break;
                            }
                            case 947: {
                                if ((0x400000004000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(948, 998);
                                break;
                            }
                            case 948: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 949;
                                break;
                            }
                            case 950: {
                                if ((0x4000000040L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(2001, 2002);
                                break;
                            }
                            case 951: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(2003, 2004);
                                break;
                            }
                            case 952: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(2005, 2006);
                                break;
                            }
                            case 953: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 76) {
                                    break;
                                }
                                kind = 76;
                                break;
                            }
                            case 954: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2007, 2009);
                                break;
                            }
                            case 965: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2010, 2012);
                                break;
                            }
                            case 976: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2013, 2015);
                                break;
                            }
                            case 987: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2016, 2018);
                                break;
                            }
                            case 998: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2019, 2022);
                                break;
                            }
                            case 1009: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2023, 2026);
                                break;
                            }
                            case 1010: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(2027, 2030);
                                break;
                            }
                            case 1020: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2031, 2034);
                                break;
                            }
                            case 1031: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2035, 2037);
                                break;
                            }
                            case 1042: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(1043, 1104);
                                break;
                            }
                            case 1043: {
                                if ((0x10000000100L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(2038, 2039);
                                break;
                            }
                            case 1044: {
                                if ((0x200000002L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(1045, 1082);
                                break;
                            }
                            case 1045: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(1046, 1071);
                                break;
                            }
                            case 1046: {
                                if ((0x8000000080000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(2040, 2041);
                                break;
                            }
                            case 1047: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(1048, 1049);
                                break;
                            }
                            case 1048: {
                                if ((0x10000000100000L & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 77) {
                                    break;
                                }
                                kind = 77;
                                break;
                            }
                            case 1049: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2042, 2045);
                                break;
                            }
                            case 1060: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2046, 2048);
                                break;
                            }
                            case 1071: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2049, 2052);
                                break;
                            }
                            case 1082: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2053, 2056);
                                break;
                            }
                            case 1093: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2057, 2059);
                                break;
                            }
                            case 1104: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2060, 2063);
                                break;
                            }
                            case 1115: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2064, 2066);
                                break;
                            }
                            case 1127:
                            case 1128: {
                                if ((0x7FFFFFE87FFFFFEL & j) != 0x0L) {
                                    if (kind > 104) {
                                        kind = 104;
                                    }
                                    this.jjCheckNAddTwoStates(1128, 1129);
                                    break;
                                }
                                break;
                            }
                            case 1129: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(1130, 1131);
                                break;
                            }
                            case 1130: {
                                if ((0xFFFFFF81FFFFFF81L & j) != 0x0L) {
                                    if (kind > 104) {
                                        kind = 104;
                                    }
                                    this.jjCheckNAddTwoStates(1128, 1129);
                                    break;
                                }
                                break;
                            }
                            case 1131: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 104) {
                                        kind = 104;
                                    }
                                    this.jjCheckNAddStates(1442, 1450);
                                    break;
                                }
                                break;
                            }
                            case 1132: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 104) {
                                        kind = 104;
                                    }
                                    this.jjCheckNAddStates(1451, 1454);
                                    break;
                                }
                                break;
                            }
                            case 1136:
                            case 1138:
                            case 1141:
                            case 1145: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(1132);
                                break;
                            }
                            case 1137: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1138;
                                break;
                            }
                            case 1139: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1140;
                                break;
                            }
                            case 1140: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1141;
                                break;
                            }
                            case 1142: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1143;
                                break;
                            }
                            case 1143: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1144;
                                break;
                            }
                            case 1144: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1145;
                                break;
                            }
                            case 1146: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddTwoStates(1130, 1147);
                                break;
                            }
                            case 1147: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 104) {
                                        kind = 104;
                                    }
                                    this.jjCheckNAddStates(1455, 1463);
                                    break;
                                }
                                break;
                            }
                            case 1148: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 104) {
                                        kind = 104;
                                    }
                                    this.jjCheckNAddStates(1464, 1467);
                                    break;
                                }
                                break;
                            }
                            case 1150:
                            case 1152:
                            case 1155:
                            case 1159: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(1148);
                                break;
                            }
                            case 1151: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1152;
                                break;
                            }
                            case 1153: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1154;
                                break;
                            }
                            case 1154: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1155;
                                break;
                            }
                            case 1156: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1157;
                                break;
                            }
                            case 1157: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1158;
                                break;
                            }
                            case 1158: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1159;
                                break;
                            }
                            case 1160: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2067, 2070);
                                break;
                            }
                            case 1161: {
                                if ((0x1000000010L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(2071, 2074);
                                break;
                            }
                            case 1171: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2075, 2078);
                                break;
                            }
                            case 1182: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(2079, 2082);
                                break;
                            }
                            case 1193: {
                                if ((0x20000000200000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjAddStates(1710, 1711);
                                break;
                            }
                            case 1195: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAddTwoStates(1196, 1203);
                                    break;
                                }
                                break;
                            }
                            case 1197: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjstateSet[this.jjnewStateCnt++] = 1198;
                                    break;
                                }
                                break;
                            }
                            case 1198: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAddStates(1512, 1515);
                                    break;
                                }
                                break;
                            }
                            case 1199: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                if (kind <= 99) {
                                    break;
                                }
                                kind = 99;
                                break;
                            }
                            case 1200: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAdd(1199);
                                    break;
                                }
                                break;
                            }
                            case 1201: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAddTwoStates(1199, 1200);
                                    break;
                                }
                                break;
                            }
                            case 1202: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAddStates(1516, 1518);
                                    break;
                                }
                                break;
                            }
                            case 1203: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAddStates(1519, 1523);
                                    break;
                                }
                                break;
                            }
                            case 1204: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAdd(1196);
                                    break;
                                }
                                break;
                            }
                            case 1205: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAddTwoStates(1204, 1196);
                                    break;
                                }
                                break;
                            }
                            case 1206: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAddStates(1524, 1526);
                                    break;
                                }
                                break;
                            }
                            case 1207: {
                                if ((0x7E0000007EL & j) != 0x0L) {
                                    if (kind > 99) {
                                        kind = 99;
                                    }
                                    this.jjCheckNAddStates(1527, 1530);
                                    break;
                                }
                                break;
                            }
                            case 1208: {
                                if ((0x100000001000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1209;
                                break;
                            }
                            case 1210: {
                                if ((0x7FFFFFFFEFFFFFFFL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1537, 1540);
                                break;
                            }
                            case 1213: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2083, 2084);
                                break;
                            }
                            case 1214: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1537, 1540);
                                break;
                            }
                            case 1215: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1541, 1550);
                                break;
                            }
                            case 1216: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1551, 1555);
                                break;
                            }
                            case 1220:
                            case 1222:
                            case 1225:
                            case 1229: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(1216);
                                break;
                            }
                            case 1221: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1222;
                                break;
                            }
                            case 1223: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1224;
                                break;
                            }
                            case 1224: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1225;
                                break;
                            }
                            case 1226: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1227;
                                break;
                            }
                            case 1227: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1228;
                                break;
                            }
                            case 1228: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1229;
                                break;
                            }
                            case 1231: {
                                if ((0xFFFFFFFFEFFFFFFFL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1556, 1558);
                                break;
                            }
                            case 1233: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2085, 2088);
                                break;
                            }
                            case 1237: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1556, 1558);
                                break;
                            }
                            case 1238: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1559, 1568);
                                break;
                            }
                            case 1239: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1569, 1573);
                                break;
                            }
                            case 1242:
                            case 1244:
                            case 1247:
                            case 1251: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(1239);
                                break;
                            }
                            case 1243: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1244;
                                break;
                            }
                            case 1245: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1246;
                                break;
                            }
                            case 1246: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1247;
                                break;
                            }
                            case 1248: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1249;
                                break;
                            }
                            case 1249: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1250;
                                break;
                            }
                            case 1250: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1251;
                                break;
                            }
                            case 1253: {
                                if ((0xFFFFFFFFEFFFFFFFL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1574, 1576);
                                break;
                            }
                            case 1255: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjAddStates(2089, 2092);
                                break;
                            }
                            case 1259: {
                                if ((0xFFFFFF81FFFFFF81L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1574, 1576);
                                break;
                            }
                            case 1260: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1577, 1586);
                                break;
                            }
                            case 1261: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(1587, 1591);
                                break;
                            }
                            case 1264:
                            case 1266:
                            case 1269:
                            case 1273: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAdd(1261);
                                break;
                            }
                            case 1265: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1266;
                                break;
                            }
                            case 1267: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1268;
                                break;
                            }
                            case 1268: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1269;
                                break;
                            }
                            case 1270: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1271;
                                break;
                            }
                            case 1271: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1272;
                                break;
                            }
                            case 1272: {
                                if ((0x7E0000007EL & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1273;
                                break;
                            }
                            case 1275: {
                                if ((0x4000000040000L & j) == 0x0L) {
                                    break;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1208;
                                break;
                            }
                            case 1276: {
                                if (this.curChar != 92) {
                                    break;
                                }
                                this.jjCheckNAddStates(1685, 1696);
                                break;
                            }
                            case 1277: {
                                if ((0x2000000020L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(2093, 2096);
                                break;
                            }
                            case 1283: {
                                if ((0x800000008L & j) == 0x0L) {
                                    break;
                                }
                                this.jjCheckNAddStates(2097, 2100);
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
                        case 1364: {
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                if (this.curChar == 40) {
                                    if (kind > 103) {
                                        kind = 103;
                                    }
                                }
                            }
                            else {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                break;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAddTwoStates(730, 731);
                            break;
                        }
                        case 1362: {
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                if (this.curChar == 40) {
                                    if (kind > 103) {
                                        kind = 103;
                                    }
                                }
                            }
                            else {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                break;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAddTwoStates(730, 731);
                            break;
                        }
                        case 1363: {
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                if (this.curChar == 40) {
                                    if (kind > 103) {
                                        kind = 103;
                                    }
                                }
                            }
                            else {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                break;
                            }
                            if (kind > 22) {
                                kind = 22;
                            }
                            this.jjCheckNAddTwoStates(730, 731);
                            break;
                        }
                        case 1365: {
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                if (this.curChar == 40) {
                                    if (kind > 103) {
                                        kind = 103;
                                    }
                                }
                            }
                            else {
                                this.jjCheckNAddStates(0, 2);
                            }
                            if ((0x3FF200000000000L & j) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddTwoStates(730, 731);
                                break;
                            }
                            if (this.curChar != 40) {
                                break;
                            }
                            if (kind <= 101) {
                                break;
                            }
                            kind = 101;
                            break;
                        }
                        case 1361: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddStates(3, 6);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddTwoStates(691, 692);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddTwoStates(686, 690);
                            }
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                this.jjCheckNAddTwoStates(682, 685);
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
                                if (kind > 24) {
                                    kind = 24;
                                }
                                this.jjCheckNAdd(258);
                            }
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAdd(257);
                            break;
                        }
                        case 0: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 20) {
                                    kind = 20;
                                }
                                this.jjCheckNAddStates(52, 150);
                                break;
                            }
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAddStates(151, 160);
                                break;
                            }
                            if (this.curChar == 45) {
                                this.jjAddStates(161, 164);
                                break;
                            }
                            if (this.curChar == 46) {
                                this.jjCheckNAddStates(165, 185);
                                break;
                            }
                            if (this.curChar == 33) {
                                this.jjCheckNAddStates(186, 189);
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
                                            this.jjCheckNAddStates(190, 192);
                                            break;
                                        }
                                        if (this.curChar != 34) {
                                            break;
                                        }
                                        this.jjCheckNAddStates(193, 195);
                                        break;
                                    }
                                    else {
                                        if (kind <= 68) {
                                            break;
                                        }
                                        kind = 68;
                                        break;
                                    }
                                }
                                else {
                                    if (kind <= 69) {
                                        break;
                                    }
                                    kind = 69;
                                    break;
                                }
                            }
                            else {
                                if (kind <= 71) {
                                    break;
                                }
                                kind = 71;
                                break;
                            }
                            break;
                        }
                        case 1: {
                            if ((0xFFFFFFFBFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(193, 195);
                            break;
                        }
                        case 2: {
                            if (this.curChar != 34) {
                                break;
                            }
                            if (kind <= 25) {
                                break;
                            }
                            kind = 25;
                            break;
                        }
                        case 4: {
                            if ((0x3400L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(193, 195);
                            break;
                        }
                        case 5: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(193, 195);
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
                            this.jjCheckNAddStates(193, 195);
                            break;
                        }
                        case 8: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(196, 205);
                            break;
                        }
                        case 9: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(206, 210);
                            break;
                        }
                        case 11: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(193, 195);
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
                            this.jjCheckNAddStates(190, 192);
                            break;
                        }
                        case 23: {
                            if ((0xFFFFFF7FFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(190, 192);
                            break;
                        }
                        case 24: {
                            if (this.curChar != 39) {
                                break;
                            }
                            if (kind <= 25) {
                                break;
                            }
                            kind = 25;
                            break;
                        }
                        case 26: {
                            if ((0x3400L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(190, 192);
                            break;
                        }
                        case 27: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(190, 192);
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
                            this.jjCheckNAddStates(190, 192);
                            break;
                        }
                        case 30: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(211, 220);
                            break;
                        }
                        case 31: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(221, 225);
                            break;
                        }
                        case 33: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(190, 192);
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
                            if (kind <= 68) {
                                break;
                            }
                            kind = 68;
                            break;
                        }
                        case 46: {
                            if (this.curChar != 62) {
                                break;
                            }
                            if (kind <= 69) {
                                break;
                            }
                            kind = 69;
                            break;
                        }
                        case 47: {
                            if (this.curChar != 44) {
                                break;
                            }
                            if (kind <= 71) {
                                break;
                            }
                            kind = 71;
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
                                if (kind > 72) {
                                    kind = 72;
                                }
                                this.jjCheckNAddTwoStates(49, 50);
                                break;
                            }
                            break;
                        }
                        case 51: {
                            if ((0xFC00FFFFFFFFCBFFL & j) != 0x0L) {
                                if (kind > 72) {
                                    kind = 72;
                                }
                                this.jjCheckNAddTwoStates(49, 50);
                                break;
                            }
                            break;
                        }
                        case 52: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 72) {
                                    kind = 72;
                                }
                                this.jjCheckNAddStates(226, 234);
                                break;
                            }
                            break;
                        }
                        case 53: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 72) {
                                    kind = 72;
                                }
                                this.jjCheckNAddStates(235, 238);
                                break;
                            }
                            break;
                        }
                        case 54: {
                            if (this.curChar == 10) {
                                if (kind > 72) {
                                    kind = 72;
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
                                if (kind > 72) {
                                    kind = 72;
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
                            this.jjCheckNAddStates(186, 189);
                            break;
                        }
                        case 68: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(186, 189);
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
                            this.jjCheckNAddStates(239, 241);
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
                            this.jjCheckNAddStates(242, 244);
                            break;
                        }
                        case 76: {
                            if (this.curChar != 47) {
                                break;
                            }
                            this.jjCheckNAddStates(186, 189);
                            break;
                        }
                        case 87: {
                            if (this.curChar == 52) {
                                if (kind > 78) {
                                    kind = 78;
                                }
                                this.jjAddStates(245, 246);
                                break;
                            }
                            break;
                        }
                        case 88: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 78) {
                                break;
                            }
                            kind = 78;
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
                            if (kind <= 78) {
                                break;
                            }
                            kind = 78;
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
                            this.jjCheckNAddStates(247, 251);
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
                            this.jjCheckNAddStates(252, 254);
                            break;
                        }
                        case 96: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(255, 258);
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
                            this.jjCheckNAddStates(259, 263);
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
                            this.jjCheckNAddStates(264, 266);
                            break;
                        }
                        case 107: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(267, 270);
                            break;
                        }
                        case 109: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(271, 274);
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
                            this.jjCheckNAddStates(275, 279);
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
                            this.jjCheckNAddStates(280, 282);
                            break;
                        }
                        case 118: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(283, 286);
                            break;
                        }
                        case 120: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAddStates(287, 290);
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
                            this.jjCheckNAddStates(291, 295);
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
                            this.jjCheckNAddStates(296, 298);
                            break;
                        }
                        case 129: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(299, 302);
                            break;
                        }
                        case 131: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(303, 306);
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
                            this.jjCheckNAddStates(307, 311);
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
                            this.jjCheckNAddStates(312, 314);
                            break;
                        }
                        case 140: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(315, 318);
                            break;
                        }
                        case 142: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(319, 322);
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
                            this.jjCheckNAddStates(323, 327);
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
                            this.jjCheckNAddStates(328, 330);
                            break;
                        }
                        case 151: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(331, 334);
                            break;
                        }
                        case 153: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(335, 338);
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
                            this.jjCheckNAddStates(339, 343);
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
                            this.jjCheckNAddStates(344, 346);
                            break;
                        }
                        case 162: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(347, 350);
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
                            this.jjCheckNAddStates(351, 355);
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
                            this.jjCheckNAddStates(356, 358);
                            break;
                        }
                        case 173: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(359, 362);
                            break;
                        }
                        case 175: {
                            if (this.curChar != 57) {
                                break;
                            }
                            this.jjCheckNAddStates(363, 366);
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
                            this.jjCheckNAddStates(367, 371);
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
                            this.jjCheckNAddStates(372, 374);
                            break;
                        }
                        case 184: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(375, 378);
                            break;
                        }
                        case 188: {
                            if (this.curChar != 40) {
                                break;
                            }
                            if (kind <= 101) {
                                break;
                            }
                            kind = 101;
                            break;
                        }
                        case 190: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAddStates(379, 381);
                            break;
                        }
                        case 191: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAdd(188);
                            break;
                        }
                        case 192: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 191;
                            break;
                        }
                        case 193: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(188);
                            break;
                        }
                        case 194: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(190);
                            break;
                        }
                        case 195: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(190);
                            break;
                        }
                        case 196: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(382, 386);
                            break;
                        }
                        case 197: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(194, 195);
                            break;
                        }
                        case 198: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(387, 389);
                            break;
                        }
                        case 199: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(390, 393);
                            break;
                        }
                        case 201: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(394, 397);
                            break;
                        }
                        case 202: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(187, 189);
                            break;
                        }
                        case 203: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 202;
                            break;
                        }
                        case 204: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(187, 189);
                            break;
                        }
                        case 205: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 206: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(201);
                            break;
                        }
                        case 207: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(398, 402);
                            break;
                        }
                        case 208: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(205, 206);
                            break;
                        }
                        case 209: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(403, 405);
                            break;
                        }
                        case 210: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(406, 409);
                            break;
                        }
                        case 215: {
                            if (this.curChar != 40) {
                                break;
                            }
                            if (kind <= 102) {
                                break;
                            }
                            kind = 102;
                            break;
                        }
                        case 217: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAddStates(410, 412);
                            break;
                        }
                        case 218: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAdd(215);
                            break;
                        }
                        case 219: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 218;
                            break;
                        }
                        case 220: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(215);
                            break;
                        }
                        case 221: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(217);
                            break;
                        }
                        case 222: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(217);
                            break;
                        }
                        case 223: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(413, 417);
                            break;
                        }
                        case 224: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(221, 222);
                            break;
                        }
                        case 225: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(418, 420);
                            break;
                        }
                        case 226: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(421, 424);
                            break;
                        }
                        case 229: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(214, 216);
                            break;
                        }
                        case 230: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 229;
                            break;
                        }
                        case 231: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(214, 216);
                            break;
                        }
                        case 232: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(228);
                            break;
                        }
                        case 233: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(228);
                            break;
                        }
                        case 234: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(425, 429);
                            break;
                        }
                        case 235: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(232, 233);
                            break;
                        }
                        case 236: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(430, 432);
                            break;
                        }
                        case 237: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(433, 436);
                            break;
                        }
                        case 239: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(437, 440);
                            break;
                        }
                        case 240: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(213, 227);
                            break;
                        }
                        case 241: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 240;
                            break;
                        }
                        case 242: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(213, 227);
                            break;
                        }
                        case 243: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(239);
                            break;
                        }
                        case 244: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(239);
                            break;
                        }
                        case 245: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(441, 445);
                            break;
                        }
                        case 246: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(243, 244);
                            break;
                        }
                        case 247: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(446, 448);
                            break;
                        }
                        case 248: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(449, 452);
                            break;
                        }
                        case 249: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAddStates(151, 160);
                                break;
                            }
                            break;
                        }
                        case 250: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAdd(250);
                                break;
                            }
                            break;
                        }
                        case 251: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 2) {
                                    kind = 2;
                                }
                                this.jjCheckNAdd(251);
                                break;
                            }
                            break;
                        }
                        case 252: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(252, 44);
                            break;
                        }
                        case 253: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(253, 45);
                            break;
                        }
                        case 254: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(254, 46);
                            break;
                        }
                        case 255: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(255, 47);
                            break;
                        }
                        case 256: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAddStates(165, 185);
                            break;
                        }
                        case 257: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 20) {
                                    kind = 20;
                                }
                                this.jjCheckNAdd(257);
                                break;
                            }
                            break;
                        }
                        case 258: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 24) {
                                    kind = 24;
                                }
                                this.jjCheckNAdd(258);
                                break;
                            }
                            break;
                        }
                        case 259: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(49, 51);
                            break;
                        }
                        case 264: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 79) {
                                break;
                            }
                            kind = 79;
                            break;
                        }
                        case 265: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 264;
                            break;
                        }
                        case 266: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 79) {
                                break;
                            }
                            kind = 79;
                            break;
                        }
                        case 267: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(263);
                            break;
                        }
                        case 268: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(263);
                            break;
                        }
                        case 269: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(453, 457);
                            break;
                        }
                        case 270: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(267, 268);
                            break;
                        }
                        case 271: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(458, 460);
                            break;
                        }
                        case 272: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(461, 464);
                            break;
                        }
                        case 274: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(465, 468);
                            break;
                        }
                        case 275: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(261, 262);
                            break;
                        }
                        case 276: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 275;
                            break;
                        }
                        case 277: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(261, 262);
                            break;
                        }
                        case 278: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(274);
                            break;
                        }
                        case 279: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(274);
                            break;
                        }
                        case 280: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(469, 473);
                            break;
                        }
                        case 281: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(278, 279);
                            break;
                        }
                        case 282: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(474, 476);
                            break;
                        }
                        case 283: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(477, 480);
                            break;
                        }
                        case 284: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(46, 48);
                            break;
                        }
                        case 288: {
                            if (this.curChar == 56) {
                                if (kind > 80) {
                                    kind = 80;
                                }
                                this.jjAddStates(481, 482);
                                break;
                            }
                            break;
                        }
                        case 289: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 80) {
                                break;
                            }
                            kind = 80;
                            break;
                        }
                        case 290: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 289;
                            break;
                        }
                        case 291: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 80) {
                                break;
                            }
                            kind = 80;
                            break;
                        }
                        case 292: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(288);
                            break;
                        }
                        case 293: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(288);
                            break;
                        }
                        case 294: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(483, 487);
                            break;
                        }
                        case 295: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(292, 293);
                            break;
                        }
                        case 296: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(488, 490);
                            break;
                        }
                        case 297: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(491, 494);
                            break;
                        }
                        case 299: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(495, 498);
                            break;
                        }
                        case 300: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(286, 287);
                            break;
                        }
                        case 301: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 300;
                            break;
                        }
                        case 302: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(286, 287);
                            break;
                        }
                        case 303: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(299);
                            break;
                        }
                        case 304: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(299);
                            break;
                        }
                        case 305: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(499, 503);
                            break;
                        }
                        case 306: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(303, 304);
                            break;
                        }
                        case 307: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(504, 506);
                            break;
                        }
                        case 308: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(507, 510);
                            break;
                        }
                        case 309: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(43, 45);
                            break;
                        }
                        case 313: {
                            if (this.curChar == 56) {
                                if (kind > 81) {
                                    kind = 81;
                                }
                                this.jjAddStates(511, 512);
                                break;
                            }
                            break;
                        }
                        case 314: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 81) {
                                break;
                            }
                            kind = 81;
                            break;
                        }
                        case 315: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 314;
                            break;
                        }
                        case 316: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 81) {
                                break;
                            }
                            kind = 81;
                            break;
                        }
                        case 317: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(313);
                            break;
                        }
                        case 318: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(313);
                            break;
                        }
                        case 319: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(513, 517);
                            break;
                        }
                        case 320: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(317, 318);
                            break;
                        }
                        case 321: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(518, 520);
                            break;
                        }
                        case 322: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(521, 524);
                            break;
                        }
                        case 324: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(525, 528);
                            break;
                        }
                        case 325: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(311, 312);
                            break;
                        }
                        case 326: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 325;
                            break;
                        }
                        case 327: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(311, 312);
                            break;
                        }
                        case 328: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(324);
                            break;
                        }
                        case 329: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(324);
                            break;
                        }
                        case 330: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(529, 533);
                            break;
                        }
                        case 331: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(328, 329);
                            break;
                        }
                        case 332: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(534, 536);
                            break;
                        }
                        case 333: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(537, 540);
                            break;
                        }
                        case 334: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(40, 42);
                            break;
                        }
                        case 339: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 82) {
                                break;
                            }
                            kind = 82;
                            break;
                        }
                        case 340: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 339;
                            break;
                        }
                        case 341: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 82) {
                                break;
                            }
                            kind = 82;
                            break;
                        }
                        case 342: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(338);
                            break;
                        }
                        case 343: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(338);
                            break;
                        }
                        case 344: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(541, 545);
                            break;
                        }
                        case 345: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(342, 343);
                            break;
                        }
                        case 346: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(546, 548);
                            break;
                        }
                        case 347: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(549, 552);
                            break;
                        }
                        case 349: {
                            if (this.curChar != 51) {
                                break;
                            }
                            this.jjCheckNAddStates(553, 556);
                            break;
                        }
                        case 350: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(336, 337);
                            break;
                        }
                        case 351: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 350;
                            break;
                        }
                        case 352: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(336, 337);
                            break;
                        }
                        case 353: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(349);
                            break;
                        }
                        case 354: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(349);
                            break;
                        }
                        case 355: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(557, 561);
                            break;
                        }
                        case 356: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(353, 354);
                            break;
                        }
                        case 357: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(562, 564);
                            break;
                        }
                        case 358: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(565, 568);
                            break;
                        }
                        case 359: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(37, 39);
                            break;
                        }
                        case 364: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 83) {
                                break;
                            }
                            kind = 83;
                            break;
                        }
                        case 365: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 364;
                            break;
                        }
                        case 366: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 83) {
                                break;
                            }
                            kind = 83;
                            break;
                        }
                        case 367: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(363);
                            break;
                        }
                        case 368: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(363);
                            break;
                        }
                        case 369: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(569, 573);
                            break;
                        }
                        case 370: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(367, 368);
                            break;
                        }
                        case 371: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(574, 576);
                            break;
                        }
                        case 372: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(577, 580);
                            break;
                        }
                        case 375: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(361, 362);
                            break;
                        }
                        case 376: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 375;
                            break;
                        }
                        case 377: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(361, 362);
                            break;
                        }
                        case 378: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(374);
                            break;
                        }
                        case 379: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(374);
                            break;
                        }
                        case 380: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(581, 585);
                            break;
                        }
                        case 381: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(378, 379);
                            break;
                        }
                        case 382: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(586, 588);
                            break;
                        }
                        case 383: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(589, 592);
                            break;
                        }
                        case 384: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(34, 36);
                            break;
                        }
                        case 389: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 84) {
                                break;
                            }
                            kind = 84;
                            break;
                        }
                        case 390: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 389;
                            break;
                        }
                        case 391: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 84) {
                                break;
                            }
                            kind = 84;
                            break;
                        }
                        case 392: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(388);
                            break;
                        }
                        case 393: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(388);
                            break;
                        }
                        case 394: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(593, 597);
                            break;
                        }
                        case 395: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(392, 393);
                            break;
                        }
                        case 396: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(598, 600);
                            break;
                        }
                        case 397: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(601, 604);
                            break;
                        }
                        case 399: {
                            if (this.curChar != 57) {
                                break;
                            }
                            this.jjCheckNAddStates(605, 608);
                            break;
                        }
                        case 400: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(386, 387);
                            break;
                        }
                        case 401: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 400;
                            break;
                        }
                        case 402: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(386, 387);
                            break;
                        }
                        case 403: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(399);
                            break;
                        }
                        case 404: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(399);
                            break;
                        }
                        case 405: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(609, 613);
                            break;
                        }
                        case 406: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(403, 404);
                            break;
                        }
                        case 407: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(614, 616);
                            break;
                        }
                        case 408: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(617, 620);
                            break;
                        }
                        case 409: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(31, 33);
                            break;
                        }
                        case 413: {
                            if (this.curChar == 52) {
                                if (kind > 85) {
                                    kind = 85;
                                }
                                this.jjAddStates(621, 622);
                                break;
                            }
                            break;
                        }
                        case 414: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 85) {
                                break;
                            }
                            kind = 85;
                            break;
                        }
                        case 415: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 414;
                            break;
                        }
                        case 416: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 85) {
                                break;
                            }
                            kind = 85;
                            break;
                        }
                        case 417: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(413);
                            break;
                        }
                        case 418: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(413);
                            break;
                        }
                        case 419: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(623, 627);
                            break;
                        }
                        case 420: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(417, 418);
                            break;
                        }
                        case 421: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(628, 630);
                            break;
                        }
                        case 422: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(631, 634);
                            break;
                        }
                        case 424: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(635, 638);
                            break;
                        }
                        case 425: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(411, 412);
                            break;
                        }
                        case 426: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 425;
                            break;
                        }
                        case 427: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(411, 412);
                            break;
                        }
                        case 428: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(424);
                            break;
                        }
                        case 429: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(424);
                            break;
                        }
                        case 430: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(639, 643);
                            break;
                        }
                        case 431: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(428, 429);
                            break;
                        }
                        case 432: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(644, 646);
                            break;
                        }
                        case 433: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(647, 650);
                            break;
                        }
                        case 434: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(28, 30);
                            break;
                        }
                        case 438: {
                            if (this.curChar == 51) {
                                if (kind > 86) {
                                    kind = 86;
                                }
                                this.jjAddStates(651, 652);
                                break;
                            }
                            break;
                        }
                        case 439: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 86) {
                                break;
                            }
                            kind = 86;
                            break;
                        }
                        case 440: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 439;
                            break;
                        }
                        case 441: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 86) {
                                break;
                            }
                            kind = 86;
                            break;
                        }
                        case 442: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(438);
                            break;
                        }
                        case 443: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(438);
                            break;
                        }
                        case 444: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(653, 657);
                            break;
                        }
                        case 445: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(442, 443);
                            break;
                        }
                        case 446: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(658, 660);
                            break;
                        }
                        case 447: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(661, 664);
                            break;
                        }
                        case 449: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(665, 668);
                            break;
                        }
                        case 450: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(436, 437);
                            break;
                        }
                        case 451: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 450;
                            break;
                        }
                        case 452: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(436, 437);
                            break;
                        }
                        case 453: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(449);
                            break;
                        }
                        case 454: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(449);
                            break;
                        }
                        case 455: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(669, 673);
                            break;
                        }
                        case 456: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(453, 454);
                            break;
                        }
                        case 457: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(674, 676);
                            break;
                        }
                        case 458: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(677, 680);
                            break;
                        }
                        case 459: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(25, 27);
                            break;
                        }
                        case 464: {
                            if (this.curChar == 55) {
                                if (kind > 87) {
                                    kind = 87;
                                }
                                this.jjAddStates(681, 682);
                                break;
                            }
                            break;
                        }
                        case 465: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 87) {
                                break;
                            }
                            kind = 87;
                            break;
                        }
                        case 466: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 465;
                            break;
                        }
                        case 467: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 87) {
                                break;
                            }
                            kind = 87;
                            break;
                        }
                        case 468: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(464);
                            break;
                        }
                        case 469: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(464);
                            break;
                        }
                        case 470: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(683, 687);
                            break;
                        }
                        case 471: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(468, 469);
                            break;
                        }
                        case 472: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(688, 690);
                            break;
                        }
                        case 473: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(691, 694);
                            break;
                        }
                        case 475: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(695, 698);
                            break;
                        }
                        case 476: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(462, 463);
                            break;
                        }
                        case 477: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 476;
                            break;
                        }
                        case 478: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(462, 463);
                            break;
                        }
                        case 479: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(475);
                            break;
                        }
                        case 480: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(475);
                            break;
                        }
                        case 481: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(699, 703);
                            break;
                        }
                        case 482: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(479, 480);
                            break;
                        }
                        case 483: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(704, 706);
                            break;
                        }
                        case 484: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(707, 710);
                            break;
                        }
                        case 486: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAddStates(711, 714);
                            break;
                        }
                        case 487: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(461, 474);
                            break;
                        }
                        case 488: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 487;
                            break;
                        }
                        case 489: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(461, 474);
                            break;
                        }
                        case 490: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(486);
                            break;
                        }
                        case 491: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(486);
                            break;
                        }
                        case 492: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(715, 719);
                            break;
                        }
                        case 493: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(490, 491);
                            break;
                        }
                        case 494: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(720, 722);
                            break;
                        }
                        case 495: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(723, 726);
                            break;
                        }
                        case 496: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(22, 24);
                            break;
                        }
                        case 501: {
                            if (this.curChar == 52) {
                                if (kind > 88) {
                                    kind = 88;
                                }
                                this.jjAddStates(727, 728);
                                break;
                            }
                            break;
                        }
                        case 502: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 88) {
                                break;
                            }
                            kind = 88;
                            break;
                        }
                        case 503: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 502;
                            break;
                        }
                        case 504: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 88) {
                                break;
                            }
                            kind = 88;
                            break;
                        }
                        case 505: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(501);
                            break;
                        }
                        case 506: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(501);
                            break;
                        }
                        case 507: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(729, 733);
                            break;
                        }
                        case 508: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(505, 506);
                            break;
                        }
                        case 509: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(734, 736);
                            break;
                        }
                        case 510: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(737, 740);
                            break;
                        }
                        case 512: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(741, 744);
                            break;
                        }
                        case 513: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(499, 500);
                            break;
                        }
                        case 514: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 513;
                            break;
                        }
                        case 515: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(499, 500);
                            break;
                        }
                        case 516: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(512);
                            break;
                        }
                        case 517: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(512);
                            break;
                        }
                        case 518: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(745, 749);
                            break;
                        }
                        case 519: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(516, 517);
                            break;
                        }
                        case 520: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(750, 752);
                            break;
                        }
                        case 521: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(753, 756);
                            break;
                        }
                        case 523: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(757, 760);
                            break;
                        }
                        case 524: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(498, 511);
                            break;
                        }
                        case 525: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 524;
                            break;
                        }
                        case 526: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(498, 511);
                            break;
                        }
                        case 527: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(523);
                            break;
                        }
                        case 528: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(523);
                            break;
                        }
                        case 529: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(761, 765);
                            break;
                        }
                        case 530: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(527, 528);
                            break;
                        }
                        case 531: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(766, 768);
                            break;
                        }
                        case 532: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(769, 772);
                            break;
                        }
                        case 533: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(19, 21);
                            break;
                        }
                        case 539: {
                            if (this.curChar == 52) {
                                if (kind > 89) {
                                    kind = 89;
                                }
                                this.jjAddStates(773, 774);
                                break;
                            }
                            break;
                        }
                        case 540: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 89) {
                                break;
                            }
                            kind = 89;
                            break;
                        }
                        case 541: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 540;
                            break;
                        }
                        case 542: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 89) {
                                break;
                            }
                            kind = 89;
                            break;
                        }
                        case 543: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(539);
                            break;
                        }
                        case 544: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(539);
                            break;
                        }
                        case 545: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(775, 779);
                            break;
                        }
                        case 546: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(543, 544);
                            break;
                        }
                        case 547: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(780, 782);
                            break;
                        }
                        case 548: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(783, 786);
                            break;
                        }
                        case 550: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(787, 790);
                            break;
                        }
                        case 551: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(537, 538);
                            break;
                        }
                        case 552: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 551;
                            break;
                        }
                        case 553: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(537, 538);
                            break;
                        }
                        case 554: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(550);
                            break;
                        }
                        case 555: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(550);
                            break;
                        }
                        case 556: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(791, 795);
                            break;
                        }
                        case 557: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(554, 555);
                            break;
                        }
                        case 558: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(796, 798);
                            break;
                        }
                        case 559: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(799, 802);
                            break;
                        }
                        case 561: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(803, 806);
                            break;
                        }
                        case 562: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(536, 549);
                            break;
                        }
                        case 563: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 562;
                            break;
                        }
                        case 564: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(536, 549);
                            break;
                        }
                        case 565: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(561);
                            break;
                        }
                        case 566: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(561);
                            break;
                        }
                        case 567: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(807, 811);
                            break;
                        }
                        case 568: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(565, 566);
                            break;
                        }
                        case 569: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(812, 814);
                            break;
                        }
                        case 570: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(815, 818);
                            break;
                        }
                        case 572: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAddStates(819, 822);
                            break;
                        }
                        case 573: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(535, 560);
                            break;
                        }
                        case 574: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 573;
                            break;
                        }
                        case 575: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(535, 560);
                            break;
                        }
                        case 576: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(572);
                            break;
                        }
                        case 577: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(572);
                            break;
                        }
                        case 578: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(823, 827);
                            break;
                        }
                        case 579: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(576, 577);
                            break;
                        }
                        case 580: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(828, 830);
                            break;
                        }
                        case 581: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(831, 834);
                            break;
                        }
                        case 582: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(16, 18);
                            break;
                        }
                        case 586: {
                            if (this.curChar == 51) {
                                if (kind > 90) {
                                    kind = 90;
                                }
                                this.jjAddStates(835, 836);
                                break;
                            }
                            break;
                        }
                        case 587: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 90) {
                                break;
                            }
                            kind = 90;
                            break;
                        }
                        case 588: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 587;
                            break;
                        }
                        case 589: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 90) {
                                break;
                            }
                            kind = 90;
                            break;
                        }
                        case 590: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(586);
                            break;
                        }
                        case 591: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(586);
                            break;
                        }
                        case 592: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(837, 841);
                            break;
                        }
                        case 593: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(590, 591);
                            break;
                        }
                        case 594: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(842, 844);
                            break;
                        }
                        case 595: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(845, 848);
                            break;
                        }
                        case 598: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(584, 585);
                            break;
                        }
                        case 599: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 598;
                            break;
                        }
                        case 600: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(584, 585);
                            break;
                        }
                        case 601: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(597);
                            break;
                        }
                        case 602: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(597);
                            break;
                        }
                        case 603: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(849, 853);
                            break;
                        }
                        case 604: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(601, 602);
                            break;
                        }
                        case 605: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(854, 856);
                            break;
                        }
                        case 606: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(857, 860);
                            break;
                        }
                        case 607: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(13, 15);
                            break;
                        }
                        case 610: {
                            if (this.curChar == 51) {
                                if (kind > 91) {
                                    kind = 91;
                                }
                                this.jjAddStates(861, 862);
                                break;
                            }
                            break;
                        }
                        case 611: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 91) {
                                break;
                            }
                            kind = 91;
                            break;
                        }
                        case 612: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 611;
                            break;
                        }
                        case 613: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 91) {
                                break;
                            }
                            kind = 91;
                            break;
                        }
                        case 614: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(610);
                            break;
                        }
                        case 615: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(610);
                            break;
                        }
                        case 616: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(863, 867);
                            break;
                        }
                        case 617: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(614, 615);
                            break;
                        }
                        case 618: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(868, 870);
                            break;
                        }
                        case 619: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(871, 874);
                            break;
                        }
                        case 620: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(10, 12);
                            break;
                        }
                        case 625: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 92) {
                                break;
                            }
                            kind = 92;
                            break;
                        }
                        case 626: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 625;
                            break;
                        }
                        case 627: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 92) {
                                break;
                            }
                            kind = 92;
                            break;
                        }
                        case 628: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(624);
                            break;
                        }
                        case 629: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(624);
                            break;
                        }
                        case 630: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(875, 879);
                            break;
                        }
                        case 631: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(628, 629);
                            break;
                        }
                        case 632: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(880, 882);
                            break;
                        }
                        case 633: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(883, 886);
                            break;
                        }
                        case 635: {
                            if (this.curChar != 56) {
                                break;
                            }
                            this.jjCheckNAddStates(887, 890);
                            break;
                        }
                        case 636: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(622, 623);
                            break;
                        }
                        case 637: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 636;
                            break;
                        }
                        case 638: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(622, 623);
                            break;
                        }
                        case 639: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(635);
                            break;
                        }
                        case 640: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(635);
                            break;
                        }
                        case 641: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(891, 895);
                            break;
                        }
                        case 642: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(639, 640);
                            break;
                        }
                        case 643: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(896, 898);
                            break;
                        }
                        case 644: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(899, 902);
                            break;
                        }
                        case 645: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(7, 9);
                            break;
                        }
                        case 651: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 93) {
                                break;
                            }
                            kind = 93;
                            break;
                        }
                        case 652: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 651;
                            break;
                        }
                        case 653: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 93) {
                                break;
                            }
                            kind = 93;
                            break;
                        }
                        case 654: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(650);
                            break;
                        }
                        case 655: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(650);
                            break;
                        }
                        case 656: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(903, 907);
                            break;
                        }
                        case 657: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(654, 655);
                            break;
                        }
                        case 658: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(908, 910);
                            break;
                        }
                        case 659: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(911, 914);
                            break;
                        }
                        case 661: {
                            if (this.curChar != 56) {
                                break;
                            }
                            this.jjCheckNAddStates(915, 918);
                            break;
                        }
                        case 662: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(648, 649);
                            break;
                        }
                        case 663: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 662;
                            break;
                        }
                        case 664: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(648, 649);
                            break;
                        }
                        case 665: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(661);
                            break;
                        }
                        case 666: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(661);
                            break;
                        }
                        case 667: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(919, 923);
                            break;
                        }
                        case 668: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(665, 666);
                            break;
                        }
                        case 669: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(924, 926);
                            break;
                        }
                        case 670: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(927, 930);
                            break;
                        }
                        case 673: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(647, 660);
                            break;
                        }
                        case 674: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 673;
                            break;
                        }
                        case 675: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(647, 660);
                            break;
                        }
                        case 676: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(672);
                            break;
                        }
                        case 677: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(672);
                            break;
                        }
                        case 678: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(931, 935);
                            break;
                        }
                        case 679: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(676, 677);
                            break;
                        }
                        case 680: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(936, 938);
                            break;
                        }
                        case 681: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(939, 942);
                            break;
                        }
                        case 682: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(682, 685);
                            break;
                        }
                        case 686: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(686, 690);
                            break;
                        }
                        case 691: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(691, 692);
                            break;
                        }
                        case 692: {
                            if (this.curChar != 37) {
                                break;
                            }
                            if (kind <= 96) {
                                break;
                            }
                            kind = 96;
                            break;
                        }
                        case 693: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(3, 6);
                            break;
                        }
                        case 694: {
                            if (this.curChar != 45) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(695, 714);
                            break;
                        }
                        case 696: {
                            if ((0x3FF200000000000L & j) != 0x0L) {
                                if (kind > 97) {
                                    kind = 97;
                                }
                                this.jjCheckNAddTwoStates(696, 697);
                                break;
                            }
                            break;
                        }
                        case 698: {
                            if ((0xFC00FFFFFFFFCBFFL & j) != 0x0L) {
                                if (kind > 97) {
                                    kind = 97;
                                }
                                this.jjCheckNAddTwoStates(696, 697);
                                break;
                            }
                            break;
                        }
                        case 699: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 97) {
                                    kind = 97;
                                }
                                this.jjCheckNAddStates(943, 951);
                                break;
                            }
                            break;
                        }
                        case 700: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 97) {
                                    kind = 97;
                                }
                                this.jjCheckNAddStates(952, 955);
                                break;
                            }
                            break;
                        }
                        case 701: {
                            if (this.curChar == 10) {
                                if (kind > 97) {
                                    kind = 97;
                                }
                                this.jjCheckNAddTwoStates(696, 697);
                                break;
                            }
                            break;
                        }
                        case 702:
                        case 717: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(701);
                            break;
                        }
                        case 703: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 97) {
                                    kind = 97;
                                }
                                this.jjCheckNAddTwoStates(696, 697);
                                break;
                            }
                            break;
                        }
                        case 704:
                        case 706:
                        case 709:
                        case 713: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(700);
                            break;
                        }
                        case 705: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 706;
                            break;
                        }
                        case 707: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 708;
                            break;
                        }
                        case 708: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 709;
                            break;
                        }
                        case 710: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 711;
                            break;
                        }
                        case 711: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 712;
                            break;
                        }
                        case 712: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 713;
                            break;
                        }
                        case 715: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 97) {
                                    kind = 97;
                                }
                                this.jjCheckNAddStates(956, 964);
                                break;
                            }
                            break;
                        }
                        case 716: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 97) {
                                    kind = 97;
                                }
                                this.jjCheckNAddStates(965, 968);
                                break;
                            }
                            break;
                        }
                        case 718:
                        case 720:
                        case 723:
                        case 727: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(716);
                            break;
                        }
                        case 719: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 720;
                            break;
                        }
                        case 721: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 722;
                            break;
                        }
                        case 722: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 723;
                            break;
                        }
                        case 724: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 725;
                            break;
                        }
                        case 725: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 726;
                            break;
                        }
                        case 726: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 727;
                            break;
                        }
                        case 728: {
                            if (this.curChar != 45) {
                                break;
                            }
                            this.jjAddStates(161, 164);
                            break;
                        }
                        case 730: {
                            if ((0x3FF200000000000L & j) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddTwoStates(730, 731);
                                break;
                            }
                            break;
                        }
                        case 732: {
                            if ((0xFC00FFFFFFFFCBFFL & j) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddTwoStates(730, 731);
                                break;
                            }
                            break;
                        }
                        case 733: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddStates(969, 977);
                                break;
                            }
                            break;
                        }
                        case 734: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddStates(978, 981);
                                break;
                            }
                            break;
                        }
                        case 735: {
                            if (this.curChar == 10) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddTwoStates(730, 731);
                                break;
                            }
                            break;
                        }
                        case 736:
                        case 785: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(735);
                            break;
                        }
                        case 737: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
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
                            if ((0x3FF200000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 750: {
                            if (this.curChar != 40) {
                                break;
                            }
                            if (kind <= 103) {
                                break;
                            }
                            kind = 103;
                            break;
                        }
                        case 752: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 753: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(982, 991);
                            break;
                        }
                        case 754: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(992, 996);
                            break;
                        }
                        case 755: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 756:
                        case 771: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(755);
                            break;
                        }
                        case 757: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(0, 2);
                            break;
                        }
                        case 758:
                        case 760:
                        case 763:
                        case 767: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(754);
                            break;
                        }
                        case 759: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 760;
                            break;
                        }
                        case 761: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 762;
                            break;
                        }
                        case 762: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 763;
                            break;
                        }
                        case 764: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 765;
                            break;
                        }
                        case 765: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 766;
                            break;
                        }
                        case 766: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 767;
                            break;
                        }
                        case 769: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(997, 1006);
                            break;
                        }
                        case 770: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1007, 1011);
                            break;
                        }
                        case 772:
                        case 774:
                        case 777:
                        case 781: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(770);
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
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddStates(1012, 1020);
                                break;
                            }
                            break;
                        }
                        case 784: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 22) {
                                    kind = 22;
                                }
                                this.jjCheckNAddStates(1021, 1024);
                                break;
                            }
                            break;
                        }
                        case 786:
                        case 788:
                        case 791:
                        case 795: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(784);
                            break;
                        }
                        case 787: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 788;
                            break;
                        }
                        case 789: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 790;
                            break;
                        }
                        case 790: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 791;
                            break;
                        }
                        case 792: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 793;
                            break;
                        }
                        case 793: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 794;
                            break;
                        }
                        case 794: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 795;
                            break;
                        }
                        case 805: {
                            if (this.curChar == 52) {
                                if (kind > 73) {
                                    kind = 73;
                                }
                                this.jjAddStates(1025, 1026);
                                break;
                            }
                            break;
                        }
                        case 806: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 73) {
                                break;
                            }
                            kind = 73;
                            break;
                        }
                        case 807: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 806;
                            break;
                        }
                        case 808: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 73) {
                                break;
                            }
                            kind = 73;
                            break;
                        }
                        case 809: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(805);
                            break;
                        }
                        case 810: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(805);
                            break;
                        }
                        case 811: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1027, 1031);
                            break;
                        }
                        case 812: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(809, 810);
                            break;
                        }
                        case 813: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1032, 1034);
                            break;
                        }
                        case 814: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1035, 1038);
                            break;
                        }
                        case 816: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(1039, 1042);
                            break;
                        }
                        case 817: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(803, 804);
                            break;
                        }
                        case 818: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 817;
                            break;
                        }
                        case 819: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(803, 804);
                            break;
                        }
                        case 820: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(816);
                            break;
                        }
                        case 821: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(816);
                            break;
                        }
                        case 822: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1043, 1047);
                            break;
                        }
                        case 823: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(820, 821);
                            break;
                        }
                        case 824: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1048, 1050);
                            break;
                        }
                        case 825: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1051, 1054);
                            break;
                        }
                        case 827: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(1055, 1058);
                            break;
                        }
                        case 828: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(802, 815);
                            break;
                        }
                        case 829: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 828;
                            break;
                        }
                        case 830: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(802, 815);
                            break;
                        }
                        case 831: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(827);
                            break;
                        }
                        case 832: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(827);
                            break;
                        }
                        case 833: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1059, 1063);
                            break;
                        }
                        case 834: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(831, 832);
                            break;
                        }
                        case 835: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1064, 1066);
                            break;
                        }
                        case 836: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1067, 1070);
                            break;
                        }
                        case 838: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1071, 1074);
                            break;
                        }
                        case 839: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(801, 826);
                            break;
                        }
                        case 840: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 839;
                            break;
                        }
                        case 841: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(801, 826);
                            break;
                        }
                        case 842: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(838);
                            break;
                        }
                        case 843: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(838);
                            break;
                        }
                        case 844: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1075, 1079);
                            break;
                        }
                        case 845: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(842, 843);
                            break;
                        }
                        case 846: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1080, 1082);
                            break;
                        }
                        case 847: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1083, 1086);
                            break;
                        }
                        case 850: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(800, 837);
                            break;
                        }
                        case 851: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 850;
                            break;
                        }
                        case 852: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(800, 837);
                            break;
                        }
                        case 853: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(849);
                            break;
                        }
                        case 854: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(849);
                            break;
                        }
                        case 855: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1087, 1091);
                            break;
                        }
                        case 856: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(853, 854);
                            break;
                        }
                        case 857: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1092, 1094);
                            break;
                        }
                        case 858: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1095, 1098);
                            break;
                        }
                        case 864: {
                            if (this.curChar == 53) {
                                if (kind > 74) {
                                    kind = 74;
                                }
                                this.jjAddStates(1099, 1100);
                                break;
                            }
                            break;
                        }
                        case 865: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 74) {
                                break;
                            }
                            kind = 74;
                            break;
                        }
                        case 866: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 865;
                            break;
                        }
                        case 867: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 74) {
                                break;
                            }
                            kind = 74;
                            break;
                        }
                        case 868: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(864);
                            break;
                        }
                        case 869: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(864);
                            break;
                        }
                        case 870: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1101, 1105);
                            break;
                        }
                        case 871: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(868, 869);
                            break;
                        }
                        case 872: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1106, 1108);
                            break;
                        }
                        case 873: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1109, 1112);
                            break;
                        }
                        case 875: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAddStates(1113, 1116);
                            break;
                        }
                        case 876: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(862, 863);
                            break;
                        }
                        case 877: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 876;
                            break;
                        }
                        case 878: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(862, 863);
                            break;
                        }
                        case 879: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(875);
                            break;
                        }
                        case 880: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(875);
                            break;
                        }
                        case 881: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1117, 1121);
                            break;
                        }
                        case 882: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(879, 880);
                            break;
                        }
                        case 883: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1122, 1124);
                            break;
                        }
                        case 884: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1125, 1128);
                            break;
                        }
                        case 886: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(1129, 1132);
                            break;
                        }
                        case 887: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(861, 874);
                            break;
                        }
                        case 888: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 887;
                            break;
                        }
                        case 889: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(861, 874);
                            break;
                        }
                        case 890: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(886);
                            break;
                        }
                        case 891: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(886);
                            break;
                        }
                        case 892: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1133, 1137);
                            break;
                        }
                        case 893: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(890, 891);
                            break;
                        }
                        case 894: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1138, 1140);
                            break;
                        }
                        case 895: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1141, 1144);
                            break;
                        }
                        case 902: {
                            if (this.curChar == 49) {
                                if (kind > 75) {
                                    kind = 75;
                                }
                                this.jjAddStates(1145, 1146);
                                break;
                            }
                            break;
                        }
                        case 903: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 75) {
                                break;
                            }
                            kind = 75;
                            break;
                        }
                        case 904: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 903;
                            break;
                        }
                        case 905: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 75) {
                                break;
                            }
                            kind = 75;
                            break;
                        }
                        case 906: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(902);
                            break;
                        }
                        case 907: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(902);
                            break;
                        }
                        case 908: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1147, 1151);
                            break;
                        }
                        case 909: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(906, 907);
                            break;
                        }
                        case 910: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1152, 1154);
                            break;
                        }
                        case 911: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1155, 1158);
                            break;
                        }
                        case 913: {
                            if (this.curChar != 57) {
                                break;
                            }
                            this.jjCheckNAddStates(1159, 1162);
                            break;
                        }
                        case 914: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(900, 901);
                            break;
                        }
                        case 915: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 914;
                            break;
                        }
                        case 916: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(900, 901);
                            break;
                        }
                        case 917: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(913);
                            break;
                        }
                        case 918: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(913);
                            break;
                        }
                        case 919: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1163, 1167);
                            break;
                        }
                        case 920: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(917, 918);
                            break;
                        }
                        case 921: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1168, 1170);
                            break;
                        }
                        case 922: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1171, 1174);
                            break;
                        }
                        case 924: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAddStates(1175, 1178);
                            break;
                        }
                        case 925: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(899, 912);
                            break;
                        }
                        case 926: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 925;
                            break;
                        }
                        case 927: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(899, 912);
                            break;
                        }
                        case 928: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(924);
                            break;
                        }
                        case 929: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(924);
                            break;
                        }
                        case 930: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1179, 1183);
                            break;
                        }
                        case 931: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(928, 929);
                            break;
                        }
                        case 932: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1184, 1186);
                            break;
                        }
                        case 933: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1187, 1190);
                            break;
                        }
                        case 935: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(1191, 1194);
                            break;
                        }
                        case 936: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(898, 923);
                            break;
                        }
                        case 937: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 936;
                            break;
                        }
                        case 938: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(898, 923);
                            break;
                        }
                        case 939: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(935);
                            break;
                        }
                        case 940: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(935);
                            break;
                        }
                        case 941: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1195, 1199);
                            break;
                        }
                        case 942: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(939, 940);
                            break;
                        }
                        case 943: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1200, 1202);
                            break;
                        }
                        case 944: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1203, 1206);
                            break;
                        }
                        case 949: {
                            if (this.curChar != 45) {
                                break;
                            }
                            this.jjAddStates(1207, 1208);
                            break;
                        }
                        case 955: {
                            if (this.curChar == 53) {
                                if (kind > 76) {
                                    kind = 76;
                                }
                                this.jjAddStates(1209, 1210);
                                break;
                            }
                            break;
                        }
                        case 956: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 76) {
                                break;
                            }
                            kind = 76;
                            break;
                        }
                        case 957: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 956;
                            break;
                        }
                        case 958: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 76) {
                                break;
                            }
                            kind = 76;
                            break;
                        }
                        case 959: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(955);
                            break;
                        }
                        case 960: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(955);
                            break;
                        }
                        case 961: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1211, 1215);
                            break;
                        }
                        case 962: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(959, 960);
                            break;
                        }
                        case 963: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1216, 1218);
                            break;
                        }
                        case 964: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1219, 1222);
                            break;
                        }
                        case 966: {
                            if (this.curChar != 51) {
                                break;
                            }
                            this.jjCheckNAddStates(1223, 1226);
                            break;
                        }
                        case 967: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(953, 954);
                            break;
                        }
                        case 968: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 967;
                            break;
                        }
                        case 969: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(953, 954);
                            break;
                        }
                        case 970: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(966);
                            break;
                        }
                        case 971: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(966);
                            break;
                        }
                        case 972: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1227, 1231);
                            break;
                        }
                        case 973: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(970, 971);
                            break;
                        }
                        case 974: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1232, 1234);
                            break;
                        }
                        case 975: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1235, 1238);
                            break;
                        }
                        case 977: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(1239, 1242);
                            break;
                        }
                        case 978: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(952, 965);
                            break;
                        }
                        case 979: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 978;
                            break;
                        }
                        case 980: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(952, 965);
                            break;
                        }
                        case 981: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(977);
                            break;
                        }
                        case 982: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(977);
                            break;
                        }
                        case 983: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1243, 1247);
                            break;
                        }
                        case 984: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(981, 982);
                            break;
                        }
                        case 985: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1248, 1250);
                            break;
                        }
                        case 986: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1251, 1254);
                            break;
                        }
                        case 988: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAddStates(1255, 1258);
                            break;
                        }
                        case 989: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(951, 976);
                            break;
                        }
                        case 990: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 989;
                            break;
                        }
                        case 991: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(951, 976);
                            break;
                        }
                        case 992: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(988);
                            break;
                        }
                        case 993: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(988);
                            break;
                        }
                        case 994: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1259, 1263);
                            break;
                        }
                        case 995: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(992, 993);
                            break;
                        }
                        case 996: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1264, 1266);
                            break;
                        }
                        case 997: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1267, 1270);
                            break;
                        }
                        case 999: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAddStates(1271, 1273);
                            break;
                        }
                        case 1000: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAdd(949);
                            break;
                        }
                        case 1001: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1000;
                            break;
                        }
                        case 1002: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(949);
                            break;
                        }
                        case 1003: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(999);
                            break;
                        }
                        case 1004: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(999);
                            break;
                        }
                        case 1005: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1274, 1278);
                            break;
                        }
                        case 1006: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1003, 1004);
                            break;
                        }
                        case 1007: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1279, 1281);
                            break;
                        }
                        case 1008: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1282, 1285);
                            break;
                        }
                        case 1011: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(948, 998);
                            break;
                        }
                        case 1012: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1011;
                            break;
                        }
                        case 1013: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(948, 998);
                            break;
                        }
                        case 1014: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1010);
                            break;
                        }
                        case 1015: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1010);
                            break;
                        }
                        case 1016: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1286, 1290);
                            break;
                        }
                        case 1017: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1014, 1015);
                            break;
                        }
                        case 1018: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1291, 1293);
                            break;
                        }
                        case 1019: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1294, 1297);
                            break;
                        }
                        case 1021: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(1298, 1301);
                            break;
                        }
                        case 1022: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(947, 1009);
                            break;
                        }
                        case 1023: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1022;
                            break;
                        }
                        case 1024: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(947, 1009);
                            break;
                        }
                        case 1025: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(1021);
                            break;
                        }
                        case 1026: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(1021);
                            break;
                        }
                        case 1027: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1302, 1306);
                            break;
                        }
                        case 1028: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1025, 1026);
                            break;
                        }
                        case 1029: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1307, 1309);
                            break;
                        }
                        case 1030: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1310, 1313);
                            break;
                        }
                        case 1032: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAddStates(1314, 1317);
                            break;
                        }
                        case 1033: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(946, 1020);
                            break;
                        }
                        case 1034: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1033;
                            break;
                        }
                        case 1035: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(946, 1020);
                            break;
                        }
                        case 1036: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1032);
                            break;
                        }
                        case 1037: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1032);
                            break;
                        }
                        case 1038: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1318, 1322);
                            break;
                        }
                        case 1039: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1036, 1037);
                            break;
                        }
                        case 1040: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1323, 1325);
                            break;
                        }
                        case 1041: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1326, 1329);
                            break;
                        }
                        case 1050: {
                            if (this.curChar == 52) {
                                if (kind > 77) {
                                    kind = 77;
                                }
                                this.jjAddStates(1330, 1331);
                                break;
                            }
                            break;
                        }
                        case 1051: {
                            if (this.curChar != 10) {
                                break;
                            }
                            if (kind <= 77) {
                                break;
                            }
                            kind = 77;
                            break;
                        }
                        case 1052: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1051;
                            break;
                        }
                        case 1053: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 77) {
                                break;
                            }
                            kind = 77;
                            break;
                        }
                        case 1054: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(1050);
                            break;
                        }
                        case 1055: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(1050);
                            break;
                        }
                        case 1056: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1332, 1336);
                            break;
                        }
                        case 1057: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1054, 1055);
                            break;
                        }
                        case 1058: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1337, 1339);
                            break;
                        }
                        case 1059: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1340, 1343);
                            break;
                        }
                        case 1061: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAddStates(1344, 1347);
                            break;
                        }
                        case 1062: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1048, 1049);
                            break;
                        }
                        case 1063: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1062;
                            break;
                        }
                        case 1064: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1048, 1049);
                            break;
                        }
                        case 1065: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1061);
                            break;
                        }
                        case 1066: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1061);
                            break;
                        }
                        case 1067: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1348, 1352);
                            break;
                        }
                        case 1068: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1065, 1066);
                            break;
                        }
                        case 1069: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1353, 1355);
                            break;
                        }
                        case 1070: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1356, 1359);
                            break;
                        }
                        case 1072: {
                            if (this.curChar != 51) {
                                break;
                            }
                            this.jjCheckNAddStates(1360, 1363);
                            break;
                        }
                        case 1073: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1047, 1060);
                            break;
                        }
                        case 1074: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1073;
                            break;
                        }
                        case 1075: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1047, 1060);
                            break;
                        }
                        case 1076: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(1072);
                            break;
                        }
                        case 1077: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(1072);
                            break;
                        }
                        case 1078: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1364, 1368);
                            break;
                        }
                        case 1079: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1076, 1077);
                            break;
                        }
                        case 1080: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1369, 1371);
                            break;
                        }
                        case 1081: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1372, 1375);
                            break;
                        }
                        case 1083: {
                            if (this.curChar != 50) {
                                break;
                            }
                            this.jjCheckNAddStates(1376, 1379);
                            break;
                        }
                        case 1084: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1046, 1071);
                            break;
                        }
                        case 1085: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1084;
                            break;
                        }
                        case 1086: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1046, 1071);
                            break;
                        }
                        case 1087: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(1083);
                            break;
                        }
                        case 1088: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(1083);
                            break;
                        }
                        case 1089: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1380, 1384);
                            break;
                        }
                        case 1090: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1087, 1088);
                            break;
                        }
                        case 1091: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1385, 1387);
                            break;
                        }
                        case 1092: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1388, 1391);
                            break;
                        }
                        case 1094: {
                            if (this.curChar != 49) {
                                break;
                            }
                            this.jjCheckNAddStates(1392, 1395);
                            break;
                        }
                        case 1095: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1045, 1082);
                            break;
                        }
                        case 1096: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1095;
                            break;
                        }
                        case 1097: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1045, 1082);
                            break;
                        }
                        case 1098: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1094);
                            break;
                        }
                        case 1099: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1094);
                            break;
                        }
                        case 1100: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1396, 1400);
                            break;
                        }
                        case 1101: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1098, 1099);
                            break;
                        }
                        case 1102: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1401, 1403);
                            break;
                        }
                        case 1103: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1404, 1407);
                            break;
                        }
                        case 1105: {
                            if (this.curChar != 56) {
                                break;
                            }
                            this.jjCheckNAddStates(1408, 1411);
                            break;
                        }
                        case 1106: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1044, 1093);
                            break;
                        }
                        case 1107: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1106;
                            break;
                        }
                        case 1108: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1044, 1093);
                            break;
                        }
                        case 1109: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1105);
                            break;
                        }
                        case 1110: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1105);
                            break;
                        }
                        case 1111: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1412, 1416);
                            break;
                        }
                        case 1112: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1109, 1110);
                            break;
                        }
                        case 1113: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1417, 1419);
                            break;
                        }
                        case 1114: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1420, 1423);
                            break;
                        }
                        case 1116: {
                            if (this.curChar != 51) {
                                break;
                            }
                            this.jjCheckNAddStates(1424, 1427);
                            break;
                        }
                        case 1117: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1043, 1104);
                            break;
                        }
                        case 1118: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1117;
                            break;
                        }
                        case 1119: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1043, 1104);
                            break;
                        }
                        case 1120: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1116);
                            break;
                        }
                        case 1121: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1116);
                            break;
                        }
                        case 1122: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1428, 1432);
                            break;
                        }
                        case 1123: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1120, 1121);
                            break;
                        }
                        case 1124: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1433, 1435);
                            break;
                        }
                        case 1125: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1436, 1439);
                            break;
                        }
                        case 1126: {
                            if (this.curChar != 45) {
                                break;
                            }
                            this.jjAddStates(1440, 1441);
                            break;
                        }
                        case 1128: {
                            if ((0x3FF200000000000L & j) != 0x0L) {
                                if (kind > 104) {
                                    kind = 104;
                                }
                                this.jjCheckNAddTwoStates(1128, 1129);
                                break;
                            }
                            break;
                        }
                        case 1130: {
                            if ((0xFC00FFFFFFFFCBFFL & j) != 0x0L) {
                                if (kind > 104) {
                                    kind = 104;
                                }
                                this.jjCheckNAddTwoStates(1128, 1129);
                                break;
                            }
                            break;
                        }
                        case 1131: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 104) {
                                    kind = 104;
                                }
                                this.jjCheckNAddStates(1442, 1450);
                                break;
                            }
                            break;
                        }
                        case 1132: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 104) {
                                    kind = 104;
                                }
                                this.jjCheckNAddStates(1451, 1454);
                                break;
                            }
                            break;
                        }
                        case 1133: {
                            if (this.curChar == 10) {
                                if (kind > 104) {
                                    kind = 104;
                                }
                                this.jjCheckNAddTwoStates(1128, 1129);
                                break;
                            }
                            break;
                        }
                        case 1134:
                        case 1149: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(1133);
                            break;
                        }
                        case 1135: {
                            if ((0x100003600L & j) != 0x0L) {
                                if (kind > 104) {
                                    kind = 104;
                                }
                                this.jjCheckNAddTwoStates(1128, 1129);
                                break;
                            }
                            break;
                        }
                        case 1136:
                        case 1138:
                        case 1141:
                        case 1145: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(1132);
                            break;
                        }
                        case 1137: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1138;
                            break;
                        }
                        case 1139: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1140;
                            break;
                        }
                        case 1140: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1141;
                            break;
                        }
                        case 1142: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1143;
                            break;
                        }
                        case 1143: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1144;
                            break;
                        }
                        case 1144: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1145;
                            break;
                        }
                        case 1147: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 104) {
                                    kind = 104;
                                }
                                this.jjCheckNAddStates(1455, 1463);
                                break;
                            }
                            break;
                        }
                        case 1148: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 104) {
                                    kind = 104;
                                }
                                this.jjCheckNAddStates(1464, 1467);
                                break;
                            }
                            break;
                        }
                        case 1150:
                        case 1152:
                        case 1155:
                        case 1159: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(1148);
                            break;
                        }
                        case 1151: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1152;
                            break;
                        }
                        case 1153: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1154;
                            break;
                        }
                        case 1154: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1155;
                            break;
                        }
                        case 1156: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1157;
                            break;
                        }
                        case 1157: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1158;
                            break;
                        }
                        case 1158: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1159;
                            break;
                        }
                        case 1162: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(897, 934);
                            break;
                        }
                        case 1163: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1162;
                            break;
                        }
                        case 1164: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(897, 934);
                            break;
                        }
                        case 1165: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1161);
                            break;
                        }
                        case 1166: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1161);
                            break;
                        }
                        case 1167: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1468, 1472);
                            break;
                        }
                        case 1168: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1165, 1166);
                            break;
                        }
                        case 1169: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1473, 1475);
                            break;
                        }
                        case 1170: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1476, 1479);
                            break;
                        }
                        case 1172: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1480, 1483);
                            break;
                        }
                        case 1173: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(860, 885);
                            break;
                        }
                        case 1174: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1173;
                            break;
                        }
                        case 1175: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(860, 885);
                            break;
                        }
                        case 1176: {
                            if (this.curChar != 53) {
                                break;
                            }
                            this.jjCheckNAdd(1172);
                            break;
                        }
                        case 1177: {
                            if (this.curChar != 55) {
                                break;
                            }
                            this.jjCheckNAdd(1172);
                            break;
                        }
                        case 1178: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1484, 1488);
                            break;
                        }
                        case 1179: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1176, 1177);
                            break;
                        }
                        case 1180: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1489, 1491);
                            break;
                        }
                        case 1181: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1492, 1495);
                            break;
                        }
                        case 1183: {
                            if (this.curChar != 57) {
                                break;
                            }
                            this.jjCheckNAddStates(1496, 1499);
                            break;
                        }
                        case 1184: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(799, 848);
                            break;
                        }
                        case 1185: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1184;
                            break;
                        }
                        case 1186: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(799, 848);
                            break;
                        }
                        case 1187: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1183);
                            break;
                        }
                        case 1188: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1183);
                            break;
                        }
                        case 1189: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1500, 1504);
                            break;
                        }
                        case 1190: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1187, 1188);
                            break;
                        }
                        case 1191: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1505, 1507);
                            break;
                        }
                        case 1192: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1508, 1511);
                            break;
                        }
                        case 1194: {
                            if (this.curChar != 43) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1195;
                            break;
                        }
                        case 1195: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAddTwoStates(1196, 1203);
                                break;
                            }
                            break;
                        }
                        case 1196: {
                            if (this.curChar != 45) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1197;
                            break;
                        }
                        case 1197: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjstateSet[this.jjnewStateCnt++] = 1198;
                                break;
                            }
                            break;
                        }
                        case 1198: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAddStates(1512, 1515);
                                break;
                            }
                            break;
                        }
                        case 1199: {
                            if ((0x83FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            if (kind <= 99) {
                                break;
                            }
                            kind = 99;
                            break;
                        }
                        case 1200: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAdd(1199);
                                break;
                            }
                            break;
                        }
                        case 1201: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAddTwoStates(1199, 1200);
                                break;
                            }
                            break;
                        }
                        case 1202: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAddStates(1516, 1518);
                                break;
                            }
                            break;
                        }
                        case 1203: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAddStates(1519, 1523);
                                break;
                            }
                            break;
                        }
                        case 1204: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAdd(1196);
                                break;
                            }
                            break;
                        }
                        case 1205: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAddTwoStates(1204, 1196);
                                break;
                            }
                            break;
                        }
                        case 1206: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAddStates(1524, 1526);
                                break;
                            }
                            break;
                        }
                        case 1207: {
                            if ((0x83FF000000000000L & j) != 0x0L) {
                                if (kind > 99) {
                                    kind = 99;
                                }
                                this.jjCheckNAddStates(1527, 1530);
                                break;
                            }
                            break;
                        }
                        case 1209: {
                            if (this.curChar != 40) {
                                break;
                            }
                            this.jjCheckNAddStates(1531, 1536);
                            break;
                        }
                        case 1210: {
                            if ((0xFFFFFC7A00000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1537, 1540);
                            break;
                        }
                        case 1211: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1211, 1212);
                            break;
                        }
                        case 1212: {
                            if (this.curChar != 41) {
                                break;
                            }
                            if (kind <= 100) {
                                break;
                            }
                            kind = 100;
                            break;
                        }
                        case 1214: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1537, 1540);
                            break;
                        }
                        case 1215: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1541, 1550);
                            break;
                        }
                        case 1216: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1551, 1555);
                            break;
                        }
                        case 1217: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(1537, 1540);
                            break;
                        }
                        case 1218: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1217;
                            break;
                        }
                        case 1219: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1537, 1540);
                            break;
                        }
                        case 1220:
                        case 1222:
                        case 1225:
                        case 1229: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(1216);
                            break;
                        }
                        case 1221: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1222;
                            break;
                        }
                        case 1223: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1224;
                            break;
                        }
                        case 1224: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1225;
                            break;
                        }
                        case 1226: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1227;
                            break;
                        }
                        case 1227: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1228;
                            break;
                        }
                        case 1228: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1229;
                            break;
                        }
                        case 1230: {
                            if (this.curChar != 39) {
                                break;
                            }
                            this.jjCheckNAddStates(1556, 1558);
                            break;
                        }
                        case 1231: {
                            if ((0xFFFFFF7FFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1556, 1558);
                            break;
                        }
                        case 1232: {
                            if (this.curChar != 39) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1211, 1212);
                            break;
                        }
                        case 1234: {
                            if ((0x3400L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1556, 1558);
                            break;
                        }
                        case 1235: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(1556, 1558);
                            break;
                        }
                        case 1236:
                        case 1240: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(1235);
                            break;
                        }
                        case 1237: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1556, 1558);
                            break;
                        }
                        case 1238: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1559, 1568);
                            break;
                        }
                        case 1239: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1569, 1573);
                            break;
                        }
                        case 1241: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1556, 1558);
                            break;
                        }
                        case 1242:
                        case 1244:
                        case 1247:
                        case 1251: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(1239);
                            break;
                        }
                        case 1243: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1244;
                            break;
                        }
                        case 1245: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1246;
                            break;
                        }
                        case 1246: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1247;
                            break;
                        }
                        case 1248: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1249;
                            break;
                        }
                        case 1249: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1250;
                            break;
                        }
                        case 1250: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1251;
                            break;
                        }
                        case 1252: {
                            if (this.curChar != 34) {
                                break;
                            }
                            this.jjCheckNAddStates(1574, 1576);
                            break;
                        }
                        case 1253: {
                            if ((0xFFFFFFFBFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1574, 1576);
                            break;
                        }
                        case 1254: {
                            if (this.curChar != 34) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1211, 1212);
                            break;
                        }
                        case 1256: {
                            if ((0x3400L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1574, 1576);
                            break;
                        }
                        case 1257: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddStates(1574, 1576);
                            break;
                        }
                        case 1258:
                        case 1262: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjCheckNAdd(1257);
                            break;
                        }
                        case 1259: {
                            if ((0xFC00FFFFFFFFCBFFL & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1574, 1576);
                            break;
                        }
                        case 1260: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1577, 1586);
                            break;
                        }
                        case 1261: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1587, 1591);
                            break;
                        }
                        case 1263: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1574, 1576);
                            break;
                        }
                        case 1264:
                        case 1266:
                        case 1269:
                        case 1273: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAdd(1261);
                            break;
                        }
                        case 1265: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1266;
                            break;
                        }
                        case 1267: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1268;
                            break;
                        }
                        case 1268: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1269;
                            break;
                        }
                        case 1270: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1271;
                            break;
                        }
                        case 1271: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1272;
                            break;
                        }
                        case 1272: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1273;
                            break;
                        }
                        case 1274: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1592, 1598);
                            break;
                        }
                        case 1278: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(186, 200);
                            break;
                        }
                        case 1279: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1278;
                            break;
                        }
                        case 1280: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(186, 200);
                            break;
                        }
                        case 1281: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1277);
                            break;
                        }
                        case 1282: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1277);
                            break;
                        }
                        case 1284: {
                            if (this.curChar != 10) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(212, 238);
                            break;
                        }
                        case 1285: {
                            if (this.curChar != 13) {
                                break;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1284;
                            break;
                        }
                        case 1286: {
                            if ((0x100003600L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(212, 238);
                            break;
                        }
                        case 1287: {
                            if (this.curChar != 52) {
                                break;
                            }
                            this.jjCheckNAdd(1283);
                            break;
                        }
                        case 1288: {
                            if (this.curChar != 54) {
                                break;
                            }
                            this.jjCheckNAdd(1283);
                            break;
                        }
                        case 1289: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1599, 1603);
                            break;
                        }
                        case 1290: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1287, 1288);
                            break;
                        }
                        case 1291: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1604, 1606);
                            break;
                        }
                        case 1292: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1607, 1610);
                            break;
                        }
                        case 1293: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1611, 1615);
                            break;
                        }
                        case 1294: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1281, 1282);
                            break;
                        }
                        case 1295: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1616, 1618);
                            break;
                        }
                        case 1296: {
                            if (this.curChar != 48) {
                                break;
                            }
                            this.jjCheckNAddStates(1619, 1622);
                            break;
                        }
                        case 1297: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 20) {
                                    kind = 20;
                                }
                                this.jjCheckNAddStates(52, 150);
                                break;
                            }
                            break;
                        }
                        case 1298: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 20) {
                                    kind = 20;
                                }
                                this.jjCheckNAdd(1298);
                                break;
                            }
                            break;
                        }
                        case 1299: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1299, 1300);
                            break;
                        }
                        case 1300: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(257);
                            break;
                        }
                        case 1301: {
                            if ((0x3FF000000000000L & j) != 0x0L) {
                                if (kind > 24) {
                                    kind = 24;
                                }
                                this.jjCheckNAdd(1301);
                                break;
                            }
                            break;
                        }
                        case 1302: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1302, 1303);
                            break;
                        }
                        case 1303: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(258);
                            break;
                        }
                        case 1304: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1623, 1625);
                            break;
                        }
                        case 1305: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1305, 1306);
                            break;
                        }
                        case 1306: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(259);
                            break;
                        }
                        case 1307: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1626, 1628);
                            break;
                        }
                        case 1308: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1308, 1309);
                            break;
                        }
                        case 1309: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(284);
                            break;
                        }
                        case 1310: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1629, 1631);
                            break;
                        }
                        case 1311: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1311, 1312);
                            break;
                        }
                        case 1312: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(309);
                            break;
                        }
                        case 1313: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1632, 1634);
                            break;
                        }
                        case 1314: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1314, 1315);
                            break;
                        }
                        case 1315: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(334);
                            break;
                        }
                        case 1316: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1635, 1637);
                            break;
                        }
                        case 1317: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1317, 1318);
                            break;
                        }
                        case 1318: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(359);
                            break;
                        }
                        case 1319: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1638, 1640);
                            break;
                        }
                        case 1320: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1320, 1321);
                            break;
                        }
                        case 1321: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(384);
                            break;
                        }
                        case 1322: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1641, 1643);
                            break;
                        }
                        case 1323: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1323, 1324);
                            break;
                        }
                        case 1324: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(409);
                            break;
                        }
                        case 1325: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1644, 1646);
                            break;
                        }
                        case 1326: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1326, 1327);
                            break;
                        }
                        case 1327: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(434);
                            break;
                        }
                        case 1328: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1647, 1649);
                            break;
                        }
                        case 1329: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1329, 1330);
                            break;
                        }
                        case 1330: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(459);
                            break;
                        }
                        case 1331: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1650, 1652);
                            break;
                        }
                        case 1332: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1332, 1333);
                            break;
                        }
                        case 1333: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(496);
                            break;
                        }
                        case 1334: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1653, 1655);
                            break;
                        }
                        case 1335: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1335, 1336);
                            break;
                        }
                        case 1336: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(533);
                            break;
                        }
                        case 1337: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1656, 1658);
                            break;
                        }
                        case 1338: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1338, 1339);
                            break;
                        }
                        case 1339: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(582);
                            break;
                        }
                        case 1340: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1659, 1661);
                            break;
                        }
                        case 1341: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1341, 1342);
                            break;
                        }
                        case 1342: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(607);
                            break;
                        }
                        case 1343: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1662, 1664);
                            break;
                        }
                        case 1344: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1344, 1345);
                            break;
                        }
                        case 1345: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(620);
                            break;
                        }
                        case 1346: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1665, 1667);
                            break;
                        }
                        case 1347: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1347, 1348);
                            break;
                        }
                        case 1348: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(645);
                            break;
                        }
                        case 1349: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1349, 685);
                            break;
                        }
                        case 1350: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1350, 1351);
                            break;
                        }
                        case 1351: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(682);
                            break;
                        }
                        case 1352: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1352, 690);
                            break;
                        }
                        case 1353: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1353, 1354);
                            break;
                        }
                        case 1354: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(686);
                            break;
                        }
                        case 1355: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1355, 692);
                            break;
                        }
                        case 1356: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1356, 1357);
                            break;
                        }
                        case 1357: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(691);
                            break;
                        }
                        case 1358: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddStates(1668, 1671);
                            break;
                        }
                        case 1359: {
                            if ((0x3FF000000000000L & j) == 0x0L) {
                                break;
                            }
                            this.jjCheckNAddTwoStates(1359, 1360);
                            break;
                        }
                        case 1360: {
                            if (this.curChar != 46) {
                                break;
                            }
                            this.jjCheckNAdd(693);
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
            final int n2 = 1361;
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
            final String im = SACParserCSS3TokenManager.jjstrLiteralImages[this.jjmatchedKind];
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
                return (SACParserCSS3TokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (SACParserCSS3TokenManager.jjbitVec0[i1] & l1) != 0x0L;
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
                        if (this.jjmatchedPos < 0 || (this.jjmatchedPos == 0 && this.jjmatchedKind > 105)) {
                            this.jjmatchedKind = 105;
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
                if ((SACParserCSS3TokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                    final Token matchedToken = this.jjFillToken();
                    this.TokenLexicalActions(matchedToken);
                    if (SACParserCSS3TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSS3TokenManager.jjnewLexState[this.jjmatchedKind];
                    }
                    return matchedToken;
                }
                if ((SACParserCSS3TokenManager.jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                    this.jjimageLen += this.jjmatchedPos + 1;
                    if (SACParserCSS3TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSS3TokenManager.jjnewLexState[this.jjmatchedKind];
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
                if (SACParserCSS3TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = SACParserCSS3TokenManager.jjnewLexState[this.jjmatchedKind];
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
            case 25: {
                final StringBuilder image = this.image;
                final CharStream input_stream = this.input_stream;
                final int jjimageLen = this.jjimageLen;
                final int lengthOfMatch = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch;
                image.append(input_stream.GetSuffix(jjimageLen + lengthOfMatch));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 1);
                break;
            }
            case 79: {
                final StringBuilder image2 = this.image;
                final CharStream input_stream2 = this.input_stream;
                final int jjimageLen2 = this.jjimageLen;
                final int lengthOfMatch2 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch2;
                image2.append(input_stream2.GetSuffix(jjimageLen2 + lengthOfMatch2));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 80: {
                final StringBuilder image3 = this.image;
                final CharStream input_stream3 = this.input_stream;
                final int jjimageLen3 = this.jjimageLen;
                final int lengthOfMatch3 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch3;
                image3.append(input_stream3.GetSuffix(jjimageLen3 + lengthOfMatch3));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 81: {
                final StringBuilder image4 = this.image;
                final CharStream input_stream4 = this.input_stream;
                final int jjimageLen4 = this.jjimageLen;
                final int lengthOfMatch4 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch4;
                image4.append(input_stream4.GetSuffix(jjimageLen4 + lengthOfMatch4));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 82: {
                final StringBuilder image5 = this.image;
                final CharStream input_stream5 = this.input_stream;
                final int jjimageLen5 = this.jjimageLen;
                final int lengthOfMatch5 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch5;
                image5.append(input_stream5.GetSuffix(jjimageLen5 + lengthOfMatch5));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 83: {
                final StringBuilder image6 = this.image;
                final CharStream input_stream6 = this.input_stream;
                final int jjimageLen6 = this.jjimageLen;
                final int lengthOfMatch6 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch6;
                image6.append(input_stream6.GetSuffix(jjimageLen6 + lengthOfMatch6));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 84: {
                final StringBuilder image7 = this.image;
                final CharStream input_stream7 = this.input_stream;
                final int jjimageLen7 = this.jjimageLen;
                final int lengthOfMatch7 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch7;
                image7.append(input_stream7.GetSuffix(jjimageLen7 + lengthOfMatch7));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 85: {
                final StringBuilder image8 = this.image;
                final CharStream input_stream8 = this.input_stream;
                final int jjimageLen8 = this.jjimageLen;
                final int lengthOfMatch8 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch8;
                image8.append(input_stream8.GetSuffix(jjimageLen8 + lengthOfMatch8));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 86: {
                final StringBuilder image9 = this.image;
                final CharStream input_stream9 = this.input_stream;
                final int jjimageLen9 = this.jjimageLen;
                final int lengthOfMatch9 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch9;
                image9.append(input_stream9.GetSuffix(jjimageLen9 + lengthOfMatch9));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 87: {
                final StringBuilder image10 = this.image;
                final CharStream input_stream10 = this.input_stream;
                final int jjimageLen10 = this.jjimageLen;
                final int lengthOfMatch10 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch10;
                image10.append(input_stream10.GetSuffix(jjimageLen10 + lengthOfMatch10));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 88: {
                final StringBuilder image11 = this.image;
                final CharStream input_stream11 = this.input_stream;
                final int jjimageLen11 = this.jjimageLen;
                final int lengthOfMatch11 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch11;
                image11.append(input_stream11.GetSuffix(jjimageLen11 + lengthOfMatch11));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 89: {
                final StringBuilder image12 = this.image;
                final CharStream input_stream12 = this.input_stream;
                final int jjimageLen12 = this.jjimageLen;
                final int lengthOfMatch12 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch12;
                image12.append(input_stream12.GetSuffix(jjimageLen12 + lengthOfMatch12));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 4);
                break;
            }
            case 90: {
                final StringBuilder image13 = this.image;
                final CharStream input_stream13 = this.input_stream;
                final int jjimageLen13 = this.jjimageLen;
                final int lengthOfMatch13 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch13;
                image13.append(input_stream13.GetSuffix(jjimageLen13 + lengthOfMatch13));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 91: {
                final StringBuilder image14 = this.image;
                final CharStream input_stream14 = this.input_stream;
                final int jjimageLen14 = this.jjimageLen;
                final int lengthOfMatch14 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch14;
                image14.append(input_stream14.GetSuffix(jjimageLen14 + lengthOfMatch14));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 1);
                break;
            }
            case 92: {
                final StringBuilder image15 = this.image;
                final CharStream input_stream15 = this.input_stream;
                final int jjimageLen15 = this.jjimageLen;
                final int lengthOfMatch15 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch15;
                image15.append(input_stream15.GetSuffix(jjimageLen15 + lengthOfMatch15));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 93: {
                final StringBuilder image16 = this.image;
                final CharStream input_stream16 = this.input_stream;
                final int jjimageLen16 = this.jjimageLen;
                final int lengthOfMatch16 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch16;
                image16.append(input_stream16.GetSuffix(jjimageLen16 + lengthOfMatch16));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 94: {
                final StringBuilder image17 = this.image;
                final CharStream input_stream17 = this.input_stream;
                final int jjimageLen17 = this.jjimageLen;
                final int lengthOfMatch17 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch17;
                image17.append(input_stream17.GetSuffix(jjimageLen17 + lengthOfMatch17));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 95: {
                final StringBuilder image18 = this.image;
                final CharStream input_stream18 = this.input_stream;
                final int jjimageLen18 = this.jjimageLen;
                final int lengthOfMatch18 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch18;
                image18.append(input_stream18.GetSuffix(jjimageLen18 + lengthOfMatch18));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 4);
                break;
            }
            case 96: {
                final StringBuilder image19 = this.image;
                final CharStream input_stream19 = this.input_stream;
                final int jjimageLen19 = this.jjimageLen;
                final int lengthOfMatch19 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch19;
                image19.append(input_stream19.GetSuffix(jjimageLen19 + lengthOfMatch19));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 1);
                break;
            }
            case 100: {
                final StringBuilder image20 = this.image;
                final CharStream input_stream20 = this.input_stream;
                final int jjimageLen20 = this.jjimageLen;
                final int lengthOfMatch20 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch20;
                image20.append(input_stream20.GetSuffix(jjimageLen20 + lengthOfMatch20));
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
            this.jjstateSet[this.jjnewStateCnt++] = SACParserCSS3TokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(SACParserCSS3TokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    public SACParserCSS3TokenManager(final CharStream stream) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[1361];
        this.jjstateSet = new int[2722];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.input_stream = stream;
    }
    
    public SACParserCSS3TokenManager(final CharStream stream, final int lexState) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[1361];
        this.jjstateSet = new int[2722];
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
        int i = 1361;
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
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "<!--", "-->", "~=", "|=", "^=", "$=", "*=", null, "}", "(", ")", ".", ";", ":", "*", "/", "-", "=", "[", "]", null, null, "~", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        jjnextStates = new int[] { 749, 750, 751, 693, 694, 695, 714, 645, 646, 671, 620, 621, 634, 607, 608, 609, 582, 583, 596, 533, 534, 571, 496, 497, 522, 459, 460, 485, 434, 435, 448, 409, 410, 423, 384, 385, 398, 359, 360, 373, 334, 335, 348, 309, 310, 323, 284, 285, 298, 259, 260, 273, 1298, 1299, 1300, 1301, 1302, 1303, 1304, 1305, 1306, 260, 273, 1307, 1308, 1309, 285, 298, 1310, 1311, 1312, 310, 1313, 1314, 1315, 335, 348, 1316, 1317, 1318, 360, 1319, 1320, 1321, 385, 1322, 1323, 1324, 410, 1325, 1326, 1327, 435, 1328, 1329, 1330, 460, 485, 1331, 1332, 1333, 497, 1334, 1335, 1336, 534, 1337, 1338, 1339, 583, 1340, 1341, 1342, 608, 1343, 1344, 1345, 621, 1346, 1347, 1348, 646, 1349, 1350, 1351, 685, 1352, 1353, 1354, 690, 1355, 1356, 1357, 692, 1358, 1359, 1360, 694, 695, 714, 671, 634, 609, 596, 571, 522, 448, 423, 398, 373, 323, 250, 251, 252, 44, 253, 45, 254, 46, 255, 47, 729, 748, 768, 782, 257, 258, 259, 284, 309, 334, 359, 384, 409, 434, 459, 496, 533, 582, 607, 620, 645, 682, 686, 691, 693, 68, 69, 77, 174, 23, 24, 25, 1, 2, 3, 1, 9, 12, 13, 15, 18, 10, 11, 2, 3, 1, 10, 11, 2, 3, 23, 31, 34, 35, 37, 40, 32, 33, 24, 25, 23, 32, 33, 24, 25, 49, 53, 57, 58, 60, 63, 55, 56, 50, 49, 55, 56, 50, 72, 73, 76, 73, 75, 76, 89, 90, 94, 91, 92, 95, 96, 94, 91, 92, 94, 91, 92, 95, 105, 102, 103, 106, 107, 105, 102, 103, 105, 102, 103, 106, 111, 112, 84, 97, 116, 113, 114, 117, 118, 116, 113, 114, 116, 113, 114, 117, 122, 123, 83, 108, 127, 124, 125, 128, 129, 127, 124, 125, 127, 124, 125, 128, 133, 134, 82, 119, 138, 135, 136, 139, 140, 138, 135, 136, 138, 135, 136, 139, 144, 145, 81, 130, 149, 146, 147, 150, 151, 149, 146, 147, 149, 146, 147, 150, 155, 156, 80, 141, 160, 157, 158, 161, 162, 160, 157, 158, 160, 157, 158, 161, 171, 168, 169, 172, 173, 171, 168, 169, 171, 168, 169, 172, 177, 178, 78, 163, 182, 179, 180, 183, 184, 182, 179, 180, 182, 179, 180, 183, 192, 193, 188, 197, 194, 195, 198, 199, 197, 194, 195, 197, 194, 195, 198, 203, 204, 187, 189, 208, 205, 206, 209, 210, 208, 205, 206, 208, 205, 206, 209, 219, 220, 215, 224, 221, 222, 225, 226, 224, 221, 222, 224, 221, 222, 225, 235, 232, 233, 236, 237, 235, 232, 233, 235, 232, 233, 236, 241, 242, 213, 227, 246, 243, 244, 247, 248, 246, 243, 244, 246, 243, 244, 247, 270, 267, 268, 271, 272, 270, 267, 268, 270, 267, 268, 271, 276, 277, 261, 262, 281, 278, 279, 282, 283, 281, 278, 279, 281, 278, 279, 282, 290, 291, 295, 292, 293, 296, 297, 295, 292, 293, 295, 292, 293, 296, 301, 302, 286, 287, 306, 303, 304, 307, 308, 306, 303, 304, 306, 303, 304, 307, 315, 316, 320, 317, 318, 321, 322, 320, 317, 318, 320, 317, 318, 321, 326, 327, 311, 312, 331, 328, 329, 332, 333, 331, 328, 329, 331, 328, 329, 332, 345, 342, 343, 346, 347, 345, 342, 343, 345, 342, 343, 346, 351, 352, 336, 337, 356, 353, 354, 357, 358, 356, 353, 354, 356, 353, 354, 357, 370, 367, 368, 371, 372, 370, 367, 368, 370, 367, 368, 371, 381, 378, 379, 382, 383, 381, 378, 379, 381, 378, 379, 382, 395, 392, 393, 396, 397, 395, 392, 393, 395, 392, 393, 396, 401, 402, 386, 387, 406, 403, 404, 407, 408, 406, 403, 404, 406, 403, 404, 407, 415, 416, 420, 417, 418, 421, 422, 420, 417, 418, 420, 417, 418, 421, 426, 427, 411, 412, 431, 428, 429, 432, 433, 431, 428, 429, 431, 428, 429, 432, 440, 441, 445, 442, 443, 446, 447, 445, 442, 443, 445, 442, 443, 446, 451, 452, 436, 437, 456, 453, 454, 457, 458, 456, 453, 454, 456, 453, 454, 457, 466, 467, 471, 468, 469, 472, 473, 471, 468, 469, 471, 468, 469, 472, 477, 478, 462, 463, 482, 479, 480, 483, 484, 482, 479, 480, 482, 479, 480, 483, 488, 489, 461, 474, 493, 490, 491, 494, 495, 493, 490, 491, 493, 490, 491, 494, 503, 504, 508, 505, 506, 509, 510, 508, 505, 506, 508, 505, 506, 509, 514, 515, 499, 500, 519, 516, 517, 520, 521, 519, 516, 517, 519, 516, 517, 520, 525, 526, 498, 511, 530, 527, 528, 531, 532, 530, 527, 528, 530, 527, 528, 531, 541, 542, 546, 543, 544, 547, 548, 546, 543, 544, 546, 543, 544, 547, 552, 553, 537, 538, 557, 554, 555, 558, 559, 557, 554, 555, 557, 554, 555, 558, 563, 564, 536, 549, 568, 565, 566, 569, 570, 568, 565, 566, 568, 565, 566, 569, 574, 575, 535, 560, 579, 576, 577, 580, 581, 579, 576, 577, 579, 576, 577, 580, 588, 589, 593, 590, 591, 594, 595, 593, 590, 591, 593, 590, 591, 594, 604, 601, 602, 605, 606, 604, 601, 602, 604, 601, 602, 605, 612, 613, 617, 614, 615, 618, 619, 617, 614, 615, 617, 614, 615, 618, 631, 628, 629, 632, 633, 631, 628, 629, 631, 628, 629, 632, 637, 638, 622, 623, 642, 639, 640, 643, 644, 642, 639, 640, 642, 639, 640, 643, 657, 654, 655, 658, 659, 657, 654, 655, 657, 654, 655, 658, 663, 664, 648, 649, 668, 665, 666, 669, 670, 668, 665, 666, 668, 665, 666, 669, 679, 676, 677, 680, 681, 679, 676, 677, 679, 676, 677, 680, 696, 700, 704, 705, 707, 710, 702, 703, 697, 696, 702, 703, 697, 716, 718, 719, 721, 724, 717, 703, 696, 697, 717, 703, 696, 697, 730, 734, 738, 739, 741, 744, 736, 737, 731, 730, 736, 737, 731, 749, 754, 758, 759, 761, 764, 756, 757, 750, 751, 749, 756, 757, 750, 751, 770, 772, 773, 775, 778, 771, 757, 749, 750, 751, 771, 757, 749, 750, 751, 784, 786, 787, 789, 792, 785, 737, 730, 731, 785, 737, 730, 731, 807, 808, 812, 809, 810, 813, 814, 812, 809, 810, 812, 809, 810, 813, 818, 819, 803, 804, 823, 820, 821, 824, 825, 823, 820, 821, 823, 820, 821, 824, 829, 830, 802, 815, 834, 831, 832, 835, 836, 834, 831, 832, 834, 831, 832, 835, 840, 841, 801, 826, 845, 842, 843, 846, 847, 845, 842, 843, 845, 842, 843, 846, 856, 853, 854, 857, 858, 856, 853, 854, 856, 853, 854, 857, 866, 867, 871, 868, 869, 872, 873, 871, 868, 869, 871, 868, 869, 872, 877, 878, 862, 863, 882, 879, 880, 883, 884, 882, 879, 880, 882, 879, 880, 883, 888, 889, 861, 874, 893, 890, 891, 894, 895, 893, 890, 891, 893, 890, 891, 894, 904, 905, 909, 906, 907, 910, 911, 909, 906, 907, 909, 906, 907, 910, 915, 916, 900, 901, 920, 917, 918, 921, 922, 920, 917, 918, 920, 917, 918, 921, 926, 927, 899, 912, 931, 928, 929, 932, 933, 931, 928, 929, 931, 928, 929, 932, 937, 938, 898, 923, 942, 939, 940, 943, 944, 942, 939, 940, 942, 939, 940, 943, 950, 987, 957, 958, 962, 959, 960, 963, 964, 962, 959, 960, 962, 959, 960, 963, 968, 969, 953, 954, 973, 970, 971, 974, 975, 973, 970, 971, 973, 970, 971, 974, 979, 980, 952, 965, 984, 981, 982, 985, 986, 984, 981, 982, 984, 981, 982, 985, 990, 991, 951, 976, 995, 992, 993, 996, 997, 995, 992, 993, 995, 992, 993, 996, 1001, 1002, 949, 1006, 1003, 1004, 1007, 1008, 1006, 1003, 1004, 1006, 1003, 1004, 1007, 1017, 1014, 1015, 1018, 1019, 1017, 1014, 1015, 1017, 1014, 1015, 1018, 1023, 1024, 947, 1009, 1028, 1025, 1026, 1029, 1030, 1028, 1025, 1026, 1028, 1025, 1026, 1029, 1034, 1035, 946, 1020, 1039, 1036, 1037, 1040, 1041, 1039, 1036, 1037, 1039, 1036, 1037, 1040, 1052, 1053, 1057, 1054, 1055, 1058, 1059, 1057, 1054, 1055, 1057, 1054, 1055, 1058, 1063, 1064, 1048, 1049, 1068, 1065, 1066, 1069, 1070, 1068, 1065, 1066, 1068, 1065, 1066, 1069, 1074, 1075, 1047, 1060, 1079, 1076, 1077, 1080, 1081, 1079, 1076, 1077, 1079, 1076, 1077, 1080, 1085, 1086, 1046, 1071, 1090, 1087, 1088, 1091, 1092, 1090, 1087, 1088, 1090, 1087, 1088, 1091, 1096, 1097, 1045, 1082, 1101, 1098, 1099, 1102, 1103, 1101, 1098, 1099, 1101, 1098, 1099, 1102, 1107, 1108, 1044, 1093, 1112, 1109, 1110, 1113, 1114, 1112, 1109, 1110, 1112, 1109, 1110, 1113, 1118, 1119, 1043, 1104, 1123, 1120, 1121, 1124, 1125, 1123, 1120, 1121, 1123, 1120, 1121, 1124, 1127, 1146, 1128, 1132, 1136, 1137, 1139, 1142, 1134, 1135, 1129, 1128, 1134, 1135, 1129, 1148, 1150, 1151, 1153, 1156, 1149, 1135, 1128, 1129, 1149, 1135, 1128, 1129, 1168, 1165, 1166, 1169, 1170, 1168, 1165, 1166, 1168, 1165, 1166, 1169, 1174, 1175, 860, 885, 1179, 1176, 1177, 1180, 1181, 1179, 1176, 1177, 1179, 1176, 1177, 1180, 1185, 1186, 799, 848, 1190, 1187, 1188, 1191, 1192, 1190, 1187, 1188, 1190, 1187, 1188, 1191, 1199, 1200, 1201, 1202, 1199, 1200, 1201, 1204, 1196, 1205, 1206, 1207, 1204, 1196, 1205, 1204, 1196, 1205, 1206, 1210, 1230, 1252, 1212, 1213, 1274, 1210, 1211, 1212, 1213, 1210, 1216, 1220, 1221, 1223, 1226, 1218, 1212, 1213, 1219, 1210, 1218, 1212, 1213, 1219, 1231, 1232, 1233, 1231, 1239, 1242, 1243, 1245, 1248, 1240, 1241, 1232, 1233, 1231, 1240, 1241, 1232, 1233, 1253, 1254, 1255, 1253, 1261, 1264, 1265, 1267, 1270, 1262, 1263, 1254, 1255, 1253, 1262, 1263, 1254, 1255, 1210, 1230, 1252, 1211, 1212, 1213, 1274, 1290, 1287, 1288, 1291, 1292, 1290, 1287, 1288, 1290, 1287, 1288, 1291, 1294, 1281, 1282, 1295, 1296, 1294, 1281, 1282, 1294, 1281, 1282, 1295, 1304, 260, 273, 1307, 285, 298, 1310, 310, 323, 1313, 335, 348, 1316, 360, 373, 1319, 385, 398, 1322, 410, 423, 1325, 435, 448, 1328, 460, 485, 1331, 497, 522, 1334, 534, 571, 1337, 583, 596, 1340, 608, 609, 1343, 621, 634, 1346, 646, 671, 1358, 694, 695, 714, 194, 195, 187, 196, 205, 206, 186, 207, 730, 749, 750, 751, 731, 732, 1281, 1282, 185, 1287, 1288, 211, 752, 769, 1289, 1293, 783, 798, 859, 896, 945, 1031, 1042, 1115, 1126, 1127, 1146, 1160, 1171, 1182, 1194, 1275, 4, 6, 7, 8, 26, 28, 29, 30, 51, 52, 71, 72, 83, 108, 91, 92, 85, 93, 102, 103, 84, 104, 100, 101, 85, 86, 113, 114, 115, 124, 125, 82, 126, 135, 136, 81, 137, 146, 147, 80, 148, 157, 158, 79, 159, 168, 169, 78, 170, 166, 167, 79, 152, 179, 180, 77, 181, 221, 222, 214, 223, 232, 233, 213, 234, 230, 231, 214, 216, 243, 244, 245, 267, 268, 261, 269, 265, 266, 278, 279, 280, 292, 293, 286, 294, 303, 304, 305, 317, 318, 311, 319, 328, 329, 310, 330, 342, 343, 336, 344, 340, 341, 353, 354, 355, 367, 368, 361, 369, 365, 366, 378, 379, 360, 380, 376, 377, 361, 362, 392, 393, 386, 394, 390, 391, 403, 404, 385, 405, 417, 418, 411, 419, 428, 429, 410, 430, 436, 437, 442, 443, 444, 453, 454, 435, 455, 461, 474, 468, 469, 462, 470, 479, 480, 481, 490, 491, 492, 498, 511, 499, 500, 505, 506, 507, 516, 517, 518, 527, 528, 497, 529, 536, 549, 537, 538, 543, 544, 545, 554, 555, 556, 565, 566, 535, 567, 576, 577, 534, 578, 590, 591, 584, 592, 601, 602, 583, 603, 599, 600, 584, 585, 614, 615, 608, 616, 628, 629, 622, 630, 626, 627, 639, 640, 621, 641, 654, 655, 648, 656, 652, 653, 665, 666, 647, 667, 676, 677, 646, 678, 674, 675, 647, 660, 809, 810, 803, 811, 820, 821, 802, 822, 831, 832, 801, 833, 842, 843, 800, 844, 853, 854, 799, 855, 851, 852, 800, 837, 860, 885, 862, 863, 868, 869, 870, 879, 880, 861, 881, 890, 891, 892, 898, 923, 900, 901, 906, 907, 908, 917, 918, 899, 919, 928, 929, 930, 939, 940, 941, 951, 976, 952, 965, 953, 954, 959, 960, 961, 970, 971, 972, 981, 982, 983, 992, 993, 994, 1003, 1004, 948, 1005, 1014, 1015, 947, 1016, 1012, 1013, 948, 998, 1025, 1026, 946, 1027, 1036, 1037, 1038, 1044, 1093, 1047, 1060, 1054, 1055, 1048, 1056, 1065, 1066, 1067, 1076, 1077, 1046, 1078, 1087, 1088, 1045, 1089, 1098, 1099, 1100, 1109, 1110, 1043, 1111, 1120, 1121, 1122, 1165, 1166, 896, 1167, 1163, 1164, 897, 934, 1176, 1177, 859, 1178, 1187, 1188, 798, 1189, 1214, 1215, 1234, 1236, 1237, 1238, 1256, 1258, 1259, 1260, 1279, 1280, 186, 200, 1285, 1286, 212, 238 };
        lexStateNames = new String[] { "DEFAULT", "COMMENT" };
        jjnewLexState = new int[] { -1, -1, -1, 1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { -281474918121465L, 8778913153023L };
        jjtoSkip = new long[] { 16L, 0L };
        jjtoSpecial = new long[] { 0L, 0L };
        jjtoMore = new long[] { 40L, 0L };
    }
}
