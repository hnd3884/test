package com.steadystate.css.parser;

import java.io.IOException;
import java.io.PrintStream;

public class SACParserCSS2TokenManager implements SACParserCSS2Constants
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
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0) {
        switch (pos) {
            case 0: {
                if ((active0 & 0x180001000000000L) != 0x0L) {
                    this.jjmatchedKind = 58;
                    this.jjmatchedPos = 0;
                    return 428;
                }
                if ((active0 & 0x3E0000000L) != 0x0L) {
                    return 60;
                }
                if ((active0 & 0x200L) != 0x0L) {
                    return 429;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x180001000000000L) != 0x0L) {
                    this.jjmatchedKind = 58;
                    this.jjmatchedPos = 1;
                    return 428;
                }
                if ((active0 & 0x3E0000000L) != 0x0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 1;
                    return 430;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x180001000000000L) != 0x0L) {
                    this.jjmatchedKind = 58;
                    this.jjmatchedPos = 2;
                    return 428;
                }
                if ((active0 & 0x3E0000000L) != 0x0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 2;
                    return 430;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x100001000000000L) != 0x0L) {
                    this.jjmatchedKind = 58;
                    this.jjmatchedPos = 3;
                    return 428;
                }
                if ((active0 & 0x3E0000000L) != 0x0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 3;
                    return 430;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x40000000L) != 0x0L) {
                    return 430;
                }
                if ((active0 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 58;
                    this.jjmatchedPos = 4;
                    return 428;
                }
                if ((active0 & 0x3A0000000L) != 0x0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 4;
                    return 430;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 58;
                    this.jjmatchedPos = 5;
                    return 428;
                }
                if ((active0 & 0x320000000L) != 0x0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 5;
                    return 430;
                }
                if ((active0 & 0x80000000L) != 0x0L) {
                    return 430;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x300000000L) != 0x0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 6;
                    return 430;
                }
                if ((active0 & 0x1000000000L) != 0x0L) {
                    return 428;
                }
                if ((active0 & 0x20000000L) != 0x0L) {
                    return 430;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x200000000L) != 0x0L) {
                    return 430;
                }
                if ((active0 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 7;
                    return 430;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 8;
                    return 430;
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
    
    private int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case 41: {
                return this.jjStopAtPos(0, 22);
            }
            case 42: {
                return this.jjStopAtPos(0, 12);
            }
            case 43: {
                return this.jjStopAtPos(0, 14);
            }
            case 44: {
                return this.jjStopAtPos(0, 8);
            }
            case 45: {
                this.jjmatchedKind = 15;
                this.jjmatchedPos = 0;
                return this.jjMoveStringLiteralDfa1_0(67108864L);
            }
            case 46: {
                return this.jjStartNfaWithStates_0(0, 9, 429);
            }
            case 47: {
                this.jjmatchedKind = 13;
                this.jjmatchedPos = 0;
                return this.jjMoveStringLiteralDfa1_0(8L);
            }
            case 58: {
                return this.jjStopAtPos(0, 11);
            }
            case 59: {
                return this.jjStopAtPos(0, 10);
            }
            case 60: {
                return this.jjMoveStringLiteralDfa1_0(33554432L);
            }
            case 61: {
                return this.jjStopAtPos(0, 16);
            }
            case 62: {
                return this.jjStopAtPos(0, 17);
            }
            case 64: {
                return this.jjMoveStringLiteralDfa1_0(16642998272L);
            }
            case 91: {
                return this.jjStopAtPos(0, 18);
            }
            case 93: {
                return this.jjStopAtPos(0, 19);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa1_0(68719476736L);
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa1_0(72057594037927936L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa1_0(36028797018963968L);
            }
            case 123: {
                return this.jjStopAtPos(0, 6);
            }
            case 124: {
                return this.jjMoveStringLiteralDfa1_0(268435456L);
            }
            case 125: {
                return this.jjStopAtPos(0, 7);
            }
            case 126: {
                return this.jjMoveStringLiteralDfa1_0(134217728L);
            }
            default: {
                return this.jjMoveNfa_0(0, 0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_0(final long active0) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
        switch (this.curChar) {
            case 33: {
                return this.jjMoveStringLiteralDfa2_0(active0, 33554432L);
            }
            case 42: {
                if ((active0 & 0x8L) != 0x0L) {
                    return this.jjStopAtPos(1, 3);
                }
                break;
            }
            case 45: {
                return this.jjMoveStringLiteralDfa2_0(active0, 67108864L);
            }
            case 61: {
                if ((active0 & 0x8000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 27);
                }
                if ((active0 & 0x10000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 28);
                }
                break;
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa2_0(active0, 72057594037927936L);
            }
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa2_0(active0, 8589934592L);
            }
            case 70:
            case 102: {
                return this.jjMoveStringLiteralDfa2_0(active0, 4294967296L);
            }
            case 71:
            case 103: {
                return this.jjMoveStringLiteralDfa2_0(active0, 36028797018963968L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa2_0(active0, 536870912L);
            }
            case 77:
            case 109: {
                return this.jjMoveStringLiteralDfa2_0(active0, 2147483648L);
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa2_0(active0, 68719476736L);
            }
            case 80:
            case 112: {
                return this.jjMoveStringLiteralDfa2_0(active0, 1073741824L);
            }
        }
        return this.jjStartNfa_0(0, active0);
    }
    
    private int jjMoveStringLiteralDfa2_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0);
            return 2;
        }
        switch (this.curChar) {
            case 45: {
                return this.jjMoveStringLiteralDfa3_0(active0, 33554432L);
            }
            case 62: {
                if ((active0 & 0x4000000L) != 0x0L) {
                    return this.jjStopAtPos(2, 26);
                }
                break;
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa3_0(active0, 1073741824L);
            }
            case 66:
            case 98: {
                return this.jjMoveStringLiteralDfa3_0(active0, 36028797018963968L);
            }
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa3_0(active0, 2147483648L);
            }
            case 72:
            case 104: {
                return this.jjMoveStringLiteralDfa3_0(active0, 77309411328L);
            }
            case 77:
            case 109: {
                return this.jjMoveStringLiteralDfa3_0(active0, 536870912L);
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa3_0(active0, 72057594037927936L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa3_0(active0, 4294967296L);
            }
        }
        return this.jjStartNfa_0(1, active0);
    }
    
    private int jjMoveStringLiteralDfa3_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0);
            return 3;
        }
        switch (this.curChar) {
            case 40: {
                if ((active0 & 0x80000000000000L) != 0x0L) {
                    return this.jjStopAtPos(3, 55);
                }
                break;
            }
            case 45: {
                if ((active0 & 0x2000000L) != 0x0L) {
                    return this.jjStopAtPos(3, 25);
                }
                break;
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa4_0(active0, 8589934592L);
            }
            case 68:
            case 100: {
                return this.jjMoveStringLiteralDfa4_0(active0, 2147483648L);
            }
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa4_0(active0, 68719476736L);
            }
            case 71:
            case 103: {
                return this.jjMoveStringLiteralDfa4_0(active0, 72057595111669760L);
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa4_0(active0, 4294967296L);
            }
            case 80:
            case 112: {
                return this.jjMoveStringLiteralDfa4_0(active0, 536870912L);
            }
        }
        return this.jjStartNfa_0(2, active0);
    }
    
    private int jjMoveStringLiteralDfa4_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0);
            return 4;
        }
        switch (this.curChar) {
            case 40: {
                if ((active0 & 0x100000000000000L) != 0x0L) {
                    return this.jjStopAtPos(4, 56);
                }
                break;
            }
            case 69:
            case 101: {
                if ((active0 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 30, 430);
                }
                break;
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa5_0(active0, 2147483648L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa5_0(active0, 536870912L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa5_0(active0, 77309411328L);
            }
            case 84:
            case 116: {
                return this.jjMoveStringLiteralDfa5_0(active0, 4294967296L);
            }
        }
        return this.jjStartNfa_0(3, active0);
    }
    
    private int jjMoveStringLiteralDfa5_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(3, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0);
            return 5;
        }
        switch (this.curChar) {
            case 45: {
                return this.jjMoveStringLiteralDfa6_0(active0, 4294967296L);
            }
            case 65:
            case 97: {
                if ((active0 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 31, 430);
                }
                break;
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa6_0(active0, 68719476736L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa6_0(active0, 536870912L);
            }
            case 83:
            case 115: {
                return this.jjMoveStringLiteralDfa6_0(active0, 8589934592L);
            }
        }
        return this.jjStartNfa_0(4, active0);
    }
    
    private int jjMoveStringLiteralDfa6_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(4, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0);
            return 6;
        }
        switch (this.curChar) {
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa7_0(active0, 8589934592L);
            }
            case 70:
            case 102: {
                return this.jjMoveStringLiteralDfa7_0(active0, 4294967296L);
            }
            case 84:
            case 116: {
                if ((active0 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 29, 430);
                }
                if ((active0 & 0x1000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 36, 428);
                }
                break;
            }
        }
        return this.jjStartNfa_0(5, active0);
    }
    
    private int jjMoveStringLiteralDfa7_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(5, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(6, active0);
            return 7;
        }
        switch (this.curChar) {
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa8_0(active0, 4294967296L);
            }
            case 84:
            case 116: {
                if ((active0 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 33, 430);
                }
                break;
            }
        }
        return this.jjStartNfa_0(6, active0);
    }
    
    private int jjMoveStringLiteralDfa8_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(6, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(7, active0);
            return 8;
        }
        switch (this.curChar) {
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa9_0(active0, 4294967296L);
            }
            default: {
                return this.jjStartNfa_0(7, active0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa9_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(7, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(8, active0);
            return 9;
        }
        switch (this.curChar) {
            case 69:
            case 101: {
                if ((active0 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 32, 430);
                }
                break;
            }
        }
        return this.jjStartNfa_0(8, active0);
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
        this.jjnewStateCnt = 428;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            if (++this.jjround == Integer.MAX_VALUE) {
                this.ReInitRounds();
            }
            if (this.curChar < 64) {
                final long l = 1L << this.curChar;
                do {
                    switch (this.jjstateSet[--i]) {
                        case 61:
                        case 430: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(61, 62);
                            continue;
                        }
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAddStates(0, 74);
                                continue;
                            }
                            if ((0x100003600L & l) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAddTwoStates(102, 103);
                                continue;
                            }
                            if (this.curChar == 46) {
                                this.jjCheckNAddStates(75, 93);
                                continue;
                            }
                            if (this.curChar == 33) {
                                this.jjCheckNAddTwoStates(91, 100);
                                continue;
                            }
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            if (this.curChar == 35) {
                                this.jjCheckNAddTwoStates(1, 2);
                                continue;
                            }
                            continue;
                        }
                        case 429: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 60) {
                                    kind = 60;
                                }
                                this.jjCheckNAdd(310);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                                this.jjCheckNAdd(309);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(100, 102);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(276, 277);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(272, 275);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(269, 271);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(267, 268);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(264, 266);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(259, 263);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(255, 258);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(251, 254);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(248, 250);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(245, 247);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(242, 244);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(239, 241);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(236, 238);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(233, 235);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(230, 232);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(227, 229);
                                continue;
                            }
                            continue;
                        }
                        case 428: {
                            if ((0x3FF200000000000L & l) != 0x0L) {
                                if (kind > 58) {
                                    kind = 58;
                                }
                                this.jjCheckNAddTwoStates(329, 330);
                            }
                            else if (this.curChar == 40 && kind > 57) {
                                kind = 57;
                            }
                            if ((0x3FF200000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(103, 105);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 3: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 4: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(106, 113);
                            continue;
                        }
                        case 5: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(114, 116);
                            continue;
                        }
                        case 6: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 7:
                        case 9:
                        case 12:
                        case 16: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(5);
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 12;
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                                continue;
                            }
                            continue;
                        }
                        case 17: {
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((0xFFFFFFFB00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if (this.curChar == 34 && kind > 21) {
                                kind = 21;
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 22;
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(117, 125);
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(126, 129);
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 28:
                        case 30:
                        case 33:
                        case 37: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(26);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 30;
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 32;
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 33;
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 35;
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 36;
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 37;
                                continue;
                            }
                            continue;
                        }
                        case 38: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if ((0xFFFFFF7F00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if (this.curChar == 39 && kind > 21) {
                                kind = 21;
                                continue;
                            }
                            continue;
                        }
                        case 42: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 44: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 43;
                                continue;
                            }
                            continue;
                        }
                        case 45: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(130, 138);
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(139, 142);
                                continue;
                            }
                            continue;
                        }
                        case 48: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 49:
                        case 51:
                        case 54:
                        case 58: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(47);
                                continue;
                            }
                            continue;
                        }
                        case 50: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 51;
                                continue;
                            }
                            continue;
                        }
                        case 52: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 53;
                                continue;
                            }
                            continue;
                        }
                        case 53: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 54;
                                continue;
                            }
                            continue;
                        }
                        case 55: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 56;
                                continue;
                            }
                            continue;
                        }
                        case 56: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 57;
                                continue;
                            }
                            continue;
                        }
                        case 57: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 58;
                                continue;
                            }
                            continue;
                        }
                        case 63: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(61, 62);
                            continue;
                        }
                        case 64: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddStates(143, 150);
                            continue;
                        }
                        case 65: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddStates(151, 153);
                            continue;
                        }
                        case 66: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(61, 62);
                            continue;
                        }
                        case 67:
                        case 69:
                        case 72:
                        case 76: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(65);
                                continue;
                            }
                            continue;
                        }
                        case 68: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 69;
                                continue;
                            }
                            continue;
                        }
                        case 70: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 71;
                                continue;
                            }
                            continue;
                        }
                        case 71: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 72;
                                continue;
                            }
                            continue;
                        }
                        case 73: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 74;
                                continue;
                            }
                            continue;
                        }
                        case 74: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 75;
                                continue;
                            }
                            continue;
                        }
                        case 75: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 76;
                                continue;
                            }
                            continue;
                        }
                        case 78: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddStates(154, 161);
                            continue;
                        }
                        case 79: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddStates(162, 164);
                            continue;
                        }
                        case 80:
                        case 82:
                        case 85:
                        case 89: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(79);
                                continue;
                            }
                            continue;
                        }
                        case 81: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 82;
                                continue;
                            }
                            continue;
                        }
                        case 83: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 84;
                                continue;
                            }
                            continue;
                        }
                        case 84: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 85;
                                continue;
                            }
                            continue;
                        }
                        case 86: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 87;
                                continue;
                            }
                            continue;
                        }
                        case 87: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 88;
                                continue;
                            }
                            continue;
                        }
                        case 88: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 89;
                                continue;
                            }
                            continue;
                        }
                        case 90: {
                            if (this.curChar == 33) {
                                this.jjCheckNAddTwoStates(91, 100);
                                continue;
                            }
                            continue;
                        }
                        case 91: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(91, 100);
                                continue;
                            }
                            continue;
                        }
                        case 101: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAddTwoStates(102, 103);
                            continue;
                        }
                        case 102: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(102);
                            continue;
                        }
                        case 103: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 2) {
                                kind = 2;
                            }
                            this.jjCheckNAdd(103);
                            continue;
                        }
                        case 105: {
                            if (this.curChar == 40) {
                                this.jjCheckNAddStates(165, 170);
                                continue;
                            }
                            continue;
                        }
                        case 106: {
                            if ((0xFFFFFC7A00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(171, 174);
                                continue;
                            }
                            continue;
                        }
                        case 107: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(107, 108);
                                continue;
                            }
                            continue;
                        }
                        case 108: {
                            if (this.curChar == 41 && kind > 24) {
                                kind = 24;
                                continue;
                            }
                            continue;
                        }
                        case 110: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(171, 174);
                                continue;
                            }
                            continue;
                        }
                        case 111: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(175, 183);
                                continue;
                            }
                            continue;
                        }
                        case 112: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(184, 187);
                                continue;
                            }
                            continue;
                        }
                        case 113: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(171, 174);
                                continue;
                            }
                            continue;
                        }
                        case 114:
                        case 116:
                        case 119:
                        case 123: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(112);
                                continue;
                            }
                            continue;
                        }
                        case 115: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 116;
                                continue;
                            }
                            continue;
                        }
                        case 117: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 118;
                                continue;
                            }
                            continue;
                        }
                        case 118: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 119;
                                continue;
                            }
                            continue;
                        }
                        case 120: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 121;
                                continue;
                            }
                            continue;
                        }
                        case 121: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 122;
                                continue;
                            }
                            continue;
                        }
                        case 122: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 123;
                                continue;
                            }
                            continue;
                        }
                        case 124: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(188, 190);
                                continue;
                            }
                            continue;
                        }
                        case 125: {
                            if ((0xFFFFFF7F00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(188, 190);
                                continue;
                            }
                            continue;
                        }
                        case 126: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddTwoStates(107, 108);
                                continue;
                            }
                            continue;
                        }
                        case 128: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(188, 190);
                                continue;
                            }
                            continue;
                        }
                        case 129: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(188, 190);
                                continue;
                            }
                            continue;
                        }
                        case 130: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 129;
                                continue;
                            }
                            continue;
                        }
                        case 131: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(188, 190);
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(191, 199);
                                continue;
                            }
                            continue;
                        }
                        case 133: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(200, 203);
                                continue;
                            }
                            continue;
                        }
                        case 134: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(188, 190);
                                continue;
                            }
                            continue;
                        }
                        case 135:
                        case 137:
                        case 140:
                        case 144: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(133);
                                continue;
                            }
                            continue;
                        }
                        case 136: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 137;
                                continue;
                            }
                            continue;
                        }
                        case 138: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 139;
                                continue;
                            }
                            continue;
                        }
                        case 139: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 140;
                                continue;
                            }
                            continue;
                        }
                        case 141: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 142;
                                continue;
                            }
                            continue;
                        }
                        case 142: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 143;
                                continue;
                            }
                            continue;
                        }
                        case 143: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 144;
                                continue;
                            }
                            continue;
                        }
                        case 145: {
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 146: {
                            if ((0xFFFFFFFB00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 147: {
                            if (this.curChar == 34) {
                                this.jjCheckNAddTwoStates(107, 108);
                                continue;
                            }
                            continue;
                        }
                        case 149: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 150: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 151: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 150;
                                continue;
                            }
                            continue;
                        }
                        case 152: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 153: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(207, 215);
                                continue;
                            }
                            continue;
                        }
                        case 154: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(216, 219);
                                continue;
                            }
                            continue;
                        }
                        case 155: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 156:
                        case 158:
                        case 161:
                        case 165: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(154);
                                continue;
                            }
                            continue;
                        }
                        case 157: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 158;
                                continue;
                            }
                            continue;
                        }
                        case 159: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 160;
                                continue;
                            }
                            continue;
                        }
                        case 160: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 161;
                                continue;
                            }
                            continue;
                        }
                        case 162: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 163;
                                continue;
                            }
                            continue;
                        }
                        case 163: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 164;
                                continue;
                            }
                            continue;
                        }
                        case 164: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 165;
                                continue;
                            }
                            continue;
                        }
                        case 166: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(220, 226);
                                continue;
                            }
                            continue;
                        }
                        case 169: {
                            if (this.curChar == 43) {
                                this.jjCheckNAddStates(227, 229);
                                continue;
                            }
                            continue;
                        }
                        case 170:
                        case 199: {
                            if (this.curChar == 63 && kind > 61) {
                                kind = 61;
                                continue;
                            }
                            continue;
                        }
                        case 171: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(230, 238);
                            continue;
                        }
                        case 172: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(173);
                                continue;
                            }
                            continue;
                        }
                        case 173: {
                            if (this.curChar == 45) {
                                this.jjstateSet[this.jjnewStateCnt++] = 174;
                                continue;
                            }
                            continue;
                        }
                        case 174: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(239, 243);
                            continue;
                        }
                        case 175: {
                            if ((0x3FF000000000000L & l) != 0x0L && kind > 61) {
                                kind = 61;
                                continue;
                            }
                            continue;
                        }
                        case 176:
                        case 178:
                        case 181:
                        case 185: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(175);
                                continue;
                            }
                            continue;
                        }
                        case 177: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 178;
                                continue;
                            }
                            continue;
                        }
                        case 179: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 180;
                                continue;
                            }
                            continue;
                        }
                        case 180: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 181;
                                continue;
                            }
                            continue;
                        }
                        case 182: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 183;
                                continue;
                            }
                            continue;
                        }
                        case 183: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 184;
                                continue;
                            }
                            continue;
                        }
                        case 184: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 185;
                                continue;
                            }
                            continue;
                        }
                        case 186:
                        case 188:
                        case 191:
                        case 195: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(172);
                                continue;
                            }
                            continue;
                        }
                        case 187: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 188;
                                continue;
                            }
                            continue;
                        }
                        case 189: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 190;
                                continue;
                            }
                            continue;
                        }
                        case 190: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 191;
                                continue;
                            }
                            continue;
                        }
                        case 192: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 193;
                                continue;
                            }
                            continue;
                        }
                        case 193: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 194;
                                continue;
                            }
                            continue;
                        }
                        case 194: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 195;
                                continue;
                            }
                            continue;
                        }
                        case 196: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(244, 246);
                            continue;
                        }
                        case 197: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(247, 249);
                            continue;
                        }
                        case 198: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(250, 252);
                            continue;
                        }
                        case 200:
                        case 203:
                        case 205:
                        case 206:
                        case 209:
                        case 210:
                        case 212:
                        case 216:
                        case 220:
                        case 223:
                        case 225: {
                            if (this.curChar == 63) {
                                this.jjCheckNAdd(199);
                                continue;
                            }
                            continue;
                        }
                        case 201: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddTwoStates(170, 175);
                            continue;
                        }
                        case 202: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddTwoStates(199, 203);
                                continue;
                            }
                            continue;
                        }
                        case 204: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddStates(253, 255);
                                continue;
                            }
                            continue;
                        }
                        case 207: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 206;
                                continue;
                            }
                            continue;
                        }
                        case 208: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddStates(256, 259);
                                continue;
                            }
                            continue;
                        }
                        case 211: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 210;
                                continue;
                            }
                            continue;
                        }
                        case 213: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 212;
                                continue;
                            }
                            continue;
                        }
                        case 214: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 213;
                                continue;
                            }
                            continue;
                        }
                        case 215: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddStates(260, 264);
                                continue;
                            }
                            continue;
                        }
                        case 217: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 216;
                                continue;
                            }
                            continue;
                        }
                        case 218: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 217;
                                continue;
                            }
                            continue;
                        }
                        case 219: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 218;
                                continue;
                            }
                            continue;
                        }
                        case 221: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 220;
                                continue;
                            }
                            continue;
                        }
                        case 222: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 221;
                                continue;
                            }
                            continue;
                        }
                        case 224: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 223;
                                continue;
                            }
                            continue;
                        }
                        case 226: {
                            if (this.curChar == 46) {
                                this.jjCheckNAddStates(75, 93);
                                continue;
                            }
                            continue;
                        }
                        case 227: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(227, 229);
                                continue;
                            }
                            continue;
                        }
                        case 230: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(230, 232);
                                continue;
                            }
                            continue;
                        }
                        case 233: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(233, 235);
                                continue;
                            }
                            continue;
                        }
                        case 236: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(236, 238);
                                continue;
                            }
                            continue;
                        }
                        case 239: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(239, 241);
                                continue;
                            }
                            continue;
                        }
                        case 242: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(242, 244);
                                continue;
                            }
                            continue;
                        }
                        case 245: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(245, 247);
                                continue;
                            }
                            continue;
                        }
                        case 248: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(248, 250);
                                continue;
                            }
                            continue;
                        }
                        case 251: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(251, 254);
                                continue;
                            }
                            continue;
                        }
                        case 255: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(255, 258);
                                continue;
                            }
                            continue;
                        }
                        case 259: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(259, 263);
                                continue;
                            }
                            continue;
                        }
                        case 264: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(264, 266);
                                continue;
                            }
                            continue;
                        }
                        case 267: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(267, 268);
                                continue;
                            }
                            continue;
                        }
                        case 269: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(269, 271);
                                continue;
                            }
                            continue;
                        }
                        case 272: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(272, 275);
                                continue;
                            }
                            continue;
                        }
                        case 276: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(276, 277);
                                continue;
                            }
                            continue;
                        }
                        case 277: {
                            if (this.curChar == 37 && kind > 52) {
                                kind = 52;
                                continue;
                            }
                            continue;
                        }
                        case 278: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(100, 102);
                                continue;
                            }
                            continue;
                        }
                        case 280: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(280, 281);
                            continue;
                        }
                        case 282: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(280, 281);
                            continue;
                        }
                        case 283: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddStates(265, 272);
                            continue;
                        }
                        case 284: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddStates(273, 275);
                            continue;
                        }
                        case 285: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(280, 281);
                            continue;
                        }
                        case 286:
                        case 288:
                        case 291:
                        case 295: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(284);
                                continue;
                            }
                            continue;
                        }
                        case 287: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 288;
                                continue;
                            }
                            continue;
                        }
                        case 289: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 290;
                                continue;
                            }
                            continue;
                        }
                        case 290: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 291;
                                continue;
                            }
                            continue;
                        }
                        case 292: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 293;
                                continue;
                            }
                            continue;
                        }
                        case 293: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 294;
                                continue;
                            }
                            continue;
                        }
                        case 294: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 295;
                                continue;
                            }
                            continue;
                        }
                        case 297: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddStates(276, 283);
                            continue;
                        }
                        case 298: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddStates(284, 286);
                            continue;
                        }
                        case 299:
                        case 301:
                        case 304:
                        case 308: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(298);
                                continue;
                            }
                            continue;
                        }
                        case 300: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 301;
                                continue;
                            }
                            continue;
                        }
                        case 302: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 303;
                                continue;
                            }
                            continue;
                        }
                        case 303: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 304;
                                continue;
                            }
                            continue;
                        }
                        case 305: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 306;
                                continue;
                            }
                            continue;
                        }
                        case 306: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 307;
                                continue;
                            }
                            continue;
                        }
                        case 307: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 308;
                                continue;
                            }
                            continue;
                        }
                        case 309: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(309);
                            continue;
                        }
                        case 310: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 60) {
                                kind = 60;
                            }
                            this.jjCheckNAdd(310);
                            continue;
                        }
                        case 312: {
                            if ((0x3FF200000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(103, 105);
                                continue;
                            }
                            continue;
                        }
                        case 313: {
                            if (this.curChar == 40 && kind > 57) {
                                kind = 57;
                                continue;
                            }
                            continue;
                        }
                        case 315: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(103, 105);
                                continue;
                            }
                            continue;
                        }
                        case 316: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(287, 295);
                                continue;
                            }
                            continue;
                        }
                        case 317: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(296, 299);
                                continue;
                            }
                            continue;
                        }
                        case 318: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(103, 105);
                                continue;
                            }
                            continue;
                        }
                        case 319:
                        case 321:
                        case 324:
                        case 328: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(317);
                                continue;
                            }
                            continue;
                        }
                        case 320: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 321;
                                continue;
                            }
                            continue;
                        }
                        case 322: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 323;
                                continue;
                            }
                            continue;
                        }
                        case 323: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 324;
                                continue;
                            }
                            continue;
                        }
                        case 325: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 326;
                                continue;
                            }
                            continue;
                        }
                        case 326: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 327;
                                continue;
                            }
                            continue;
                        }
                        case 327: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 328;
                                continue;
                            }
                            continue;
                        }
                        case 329: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddTwoStates(329, 330);
                            continue;
                        }
                        case 331: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddTwoStates(329, 330);
                            continue;
                        }
                        case 332: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(300, 307);
                            continue;
                        }
                        case 333: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(308, 310);
                            continue;
                        }
                        case 334: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddTwoStates(329, 330);
                            continue;
                        }
                        case 335:
                        case 337:
                        case 340:
                        case 344: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(333);
                                continue;
                            }
                            continue;
                        }
                        case 336: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 337;
                                continue;
                            }
                            continue;
                        }
                        case 338: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 339;
                                continue;
                            }
                            continue;
                        }
                        case 339: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 340;
                                continue;
                            }
                            continue;
                        }
                        case 341: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 342;
                                continue;
                            }
                            continue;
                        }
                        case 342: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 343;
                                continue;
                            }
                            continue;
                        }
                        case 343: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 344;
                                continue;
                            }
                            continue;
                        }
                        case 345: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAddStates(0, 74);
                            continue;
                        }
                        case 346: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(346, 229);
                                continue;
                            }
                            continue;
                        }
                        case 347: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(347, 348);
                                continue;
                            }
                            continue;
                        }
                        case 348: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(227);
                                continue;
                            }
                            continue;
                        }
                        case 349: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(349, 232);
                                continue;
                            }
                            continue;
                        }
                        case 350: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(350, 351);
                                continue;
                            }
                            continue;
                        }
                        case 351: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(230);
                                continue;
                            }
                            continue;
                        }
                        case 352: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(352, 235);
                                continue;
                            }
                            continue;
                        }
                        case 353: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(353, 354);
                                continue;
                            }
                            continue;
                        }
                        case 354: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(233);
                                continue;
                            }
                            continue;
                        }
                        case 355: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(355, 238);
                                continue;
                            }
                            continue;
                        }
                        case 356: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(356, 357);
                                continue;
                            }
                            continue;
                        }
                        case 357: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(236);
                                continue;
                            }
                            continue;
                        }
                        case 358: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(358, 241);
                                continue;
                            }
                            continue;
                        }
                        case 359: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(359, 360);
                                continue;
                            }
                            continue;
                        }
                        case 360: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(239);
                                continue;
                            }
                            continue;
                        }
                        case 361: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(361, 244);
                                continue;
                            }
                            continue;
                        }
                        case 362: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(362, 363);
                                continue;
                            }
                            continue;
                        }
                        case 363: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(242);
                                continue;
                            }
                            continue;
                        }
                        case 364: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(364, 247);
                                continue;
                            }
                            continue;
                        }
                        case 365: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(365, 366);
                                continue;
                            }
                            continue;
                        }
                        case 366: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(245);
                                continue;
                            }
                            continue;
                        }
                        case 367: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(367, 250);
                                continue;
                            }
                            continue;
                        }
                        case 368: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(368, 369);
                                continue;
                            }
                            continue;
                        }
                        case 369: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(248);
                                continue;
                            }
                            continue;
                        }
                        case 370: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(370, 254);
                                continue;
                            }
                            continue;
                        }
                        case 371: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(371, 372);
                                continue;
                            }
                            continue;
                        }
                        case 372: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(251);
                                continue;
                            }
                            continue;
                        }
                        case 373: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(373, 258);
                                continue;
                            }
                            continue;
                        }
                        case 374: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(374, 375);
                                continue;
                            }
                            continue;
                        }
                        case 375: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(255);
                                continue;
                            }
                            continue;
                        }
                        case 376: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(376, 263);
                                continue;
                            }
                            continue;
                        }
                        case 377: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(377, 378);
                                continue;
                            }
                            continue;
                        }
                        case 378: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(259);
                                continue;
                            }
                            continue;
                        }
                        case 379: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(379, 266);
                                continue;
                            }
                            continue;
                        }
                        case 380: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(380, 381);
                                continue;
                            }
                            continue;
                        }
                        case 381: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(264);
                                continue;
                            }
                            continue;
                        }
                        case 382: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(382, 268);
                                continue;
                            }
                            continue;
                        }
                        case 383: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(383, 384);
                                continue;
                            }
                            continue;
                        }
                        case 384: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(267);
                                continue;
                            }
                            continue;
                        }
                        case 385: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(385, 271);
                                continue;
                            }
                            continue;
                        }
                        case 386: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(386, 387);
                                continue;
                            }
                            continue;
                        }
                        case 387: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(269);
                                continue;
                            }
                            continue;
                        }
                        case 388: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(388, 275);
                                continue;
                            }
                            continue;
                        }
                        case 389: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(389, 390);
                                continue;
                            }
                            continue;
                        }
                        case 390: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(272);
                                continue;
                            }
                            continue;
                        }
                        case 391: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(391, 277);
                                continue;
                            }
                            continue;
                        }
                        case 392: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(392, 393);
                                continue;
                            }
                            continue;
                        }
                        case 393: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(276);
                                continue;
                            }
                            continue;
                        }
                        case 394: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(311, 313);
                                continue;
                            }
                            continue;
                        }
                        case 395: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(395, 396);
                                continue;
                            }
                            continue;
                        }
                        case 396: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(278);
                                continue;
                            }
                            continue;
                        }
                        case 397: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(397);
                            continue;
                        }
                        case 398: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(398, 399);
                                continue;
                            }
                            continue;
                        }
                        case 399: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(309);
                                continue;
                            }
                            continue;
                        }
                        case 400: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 60) {
                                kind = 60;
                            }
                            this.jjCheckNAdd(400);
                            continue;
                        }
                        case 401: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(401, 402);
                                continue;
                            }
                            continue;
                        }
                        case 402: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(310);
                                continue;
                            }
                            continue;
                        }
                        case 404: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(314, 321);
                            continue;
                        }
                        case 405: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(322, 324);
                            continue;
                        }
                        case 406:
                        case 408:
                        case 411:
                        case 415: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(405);
                                continue;
                            }
                            continue;
                        }
                        case 407: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 408;
                                continue;
                            }
                            continue;
                        }
                        case 409: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 410;
                                continue;
                            }
                            continue;
                        }
                        case 410: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 411;
                                continue;
                            }
                            continue;
                        }
                        case 412: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 413;
                                continue;
                            }
                            continue;
                        }
                        case 413: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 414;
                                continue;
                            }
                            continue;
                        }
                        case 414: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 415;
                                continue;
                            }
                            continue;
                        }
                        case 416: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(325, 333);
                                continue;
                            }
                            continue;
                        }
                        case 417: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(334, 337);
                                continue;
                            }
                            continue;
                        }
                        case 418:
                        case 420:
                        case 423:
                        case 427: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(417);
                                continue;
                            }
                            continue;
                        }
                        case 419: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 420;
                                continue;
                            }
                            continue;
                        }
                        case 421: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 422;
                                continue;
                            }
                            continue;
                        }
                        case 422: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 423;
                                continue;
                            }
                            continue;
                        }
                        case 424: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 425;
                                continue;
                            }
                            continue;
                        }
                        case 425: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 426;
                                continue;
                            }
                            continue;
                        }
                        case 426: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 427;
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
            else if (this.curChar < 128) {
                final long l = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 60: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 34) {
                                    kind = 34;
                                }
                                this.jjCheckNAddTwoStates(61, 62);
                                continue;
                            }
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(63, 78);
                                continue;
                            }
                            continue;
                        }
                        case 430: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 34) {
                                    kind = 34;
                                }
                                this.jjCheckNAddTwoStates(61, 62);
                                continue;
                            }
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(63, 64);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 58) {
                                    kind = 58;
                                }
                                this.jjCheckNAddStates(338, 342);
                            }
                            else if (this.curChar == 92) {
                                this.jjCheckNAddStates(343, 346);
                            }
                            else if (this.curChar == 64) {
                                this.jjAddStates(347, 348);
                            }
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjAddStates(349, 350);
                                continue;
                            }
                            continue;
                        }
                        case 428: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 58) {
                                    kind = 58;
                                }
                                this.jjCheckNAddTwoStates(329, 330);
                            }
                            else if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(315, 316);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                this.jjCheckNAddStates(103, 105);
                                continue;
                            }
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(331, 332);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 2: {
                            if (this.curChar == 92) {
                                this.jjAddStates(351, 352);
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 4: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(106, 113);
                            continue;
                        }
                        case 5: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddStates(114, 116);
                            continue;
                        }
                        case 7:
                        case 9:
                        case 12:
                        case 16: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(5);
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 11;
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 12;
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 14;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 15;
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if (this.curChar == 92) {
                                this.jjAddStates(353, 356);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(117, 125);
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(126, 129);
                                continue;
                            }
                            continue;
                        }
                        case 28:
                        case 30:
                        case 33:
                        case 37: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(26);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 30;
                                continue;
                            }
                            continue;
                        }
                        case 31: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 32;
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 33;
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 35;
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 36;
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 37;
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if (this.curChar == 92) {
                                this.jjAddStates(357, 360);
                                continue;
                            }
                            continue;
                        }
                        case 45: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(130, 138);
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(139, 142);
                                continue;
                            }
                            continue;
                        }
                        case 49:
                        case 51:
                        case 54:
                        case 58: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(47);
                                continue;
                            }
                            continue;
                        }
                        case 50: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 51;
                                continue;
                            }
                            continue;
                        }
                        case 52: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 53;
                                continue;
                            }
                            continue;
                        }
                        case 53: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 54;
                                continue;
                            }
                            continue;
                        }
                        case 55: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 56;
                                continue;
                            }
                            continue;
                        }
                        case 56: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 57;
                                continue;
                            }
                            continue;
                        }
                        case 57: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 58;
                                continue;
                            }
                            continue;
                        }
                        case 59: {
                            if (this.curChar == 64) {
                                this.jjAddStates(347, 348);
                                continue;
                            }
                            continue;
                        }
                        case 61: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(61, 62);
                            continue;
                        }
                        case 62: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(63, 64);
                                continue;
                            }
                            continue;
                        }
                        case 63: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(61, 62);
                            continue;
                        }
                        case 64: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddStates(143, 150);
                            continue;
                        }
                        case 65: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddStates(151, 153);
                            continue;
                        }
                        case 67:
                        case 69:
                        case 72:
                        case 76: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(65);
                                continue;
                            }
                            continue;
                        }
                        case 68: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 69;
                                continue;
                            }
                            continue;
                        }
                        case 70: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 71;
                                continue;
                            }
                            continue;
                        }
                        case 71: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 72;
                                continue;
                            }
                            continue;
                        }
                        case 73: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 74;
                                continue;
                            }
                            continue;
                        }
                        case 74: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 75;
                                continue;
                            }
                            continue;
                        }
                        case 75: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 76;
                                continue;
                            }
                            continue;
                        }
                        case 77: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(63, 78);
                                continue;
                            }
                            continue;
                        }
                        case 78: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddStates(154, 161);
                            continue;
                        }
                        case 79: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddStates(162, 164);
                            continue;
                        }
                        case 80:
                        case 82:
                        case 85:
                        case 89: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(79);
                                continue;
                            }
                            continue;
                        }
                        case 81: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 82;
                                continue;
                            }
                            continue;
                        }
                        case 83: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 84;
                                continue;
                            }
                            continue;
                        }
                        case 84: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 85;
                                continue;
                            }
                            continue;
                        }
                        case 86: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 87;
                                continue;
                            }
                            continue;
                        }
                        case 87: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 88;
                                continue;
                            }
                            continue;
                        }
                        case 88: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 89;
                                continue;
                            }
                            continue;
                        }
                        case 92: {
                            if ((0x10000000100000L & l) != 0x0L && kind > 35) {
                                kind = 35;
                                continue;
                            }
                            continue;
                        }
                        case 93: {
                            if ((0x400000004000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 92;
                                continue;
                            }
                            continue;
                        }
                        case 94: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 93;
                                continue;
                            }
                            continue;
                        }
                        case 95: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 94;
                                continue;
                            }
                            continue;
                        }
                        case 96: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 95;
                                continue;
                            }
                            continue;
                        }
                        case 97: {
                            if ((0x800000008000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 96;
                                continue;
                            }
                            continue;
                        }
                        case 98: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 97;
                                continue;
                            }
                            continue;
                        }
                        case 99: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 98;
                                continue;
                            }
                            continue;
                        }
                        case 100: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 99;
                                continue;
                            }
                            continue;
                        }
                        case 104: {
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjAddStates(349, 350);
                                continue;
                            }
                            continue;
                        }
                        case 106: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(171, 174);
                                continue;
                            }
                            continue;
                        }
                        case 109: {
                            if (this.curChar == 92) {
                                this.jjAddStates(361, 362);
                                continue;
                            }
                            continue;
                        }
                        case 110: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(171, 174);
                                continue;
                            }
                            continue;
                        }
                        case 111: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(175, 183);
                                continue;
                            }
                            continue;
                        }
                        case 112: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(184, 187);
                                continue;
                            }
                            continue;
                        }
                        case 114:
                        case 116:
                        case 119:
                        case 123: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(112);
                                continue;
                            }
                            continue;
                        }
                        case 115: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 116;
                                continue;
                            }
                            continue;
                        }
                        case 117: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 118;
                                continue;
                            }
                            continue;
                        }
                        case 118: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 119;
                                continue;
                            }
                            continue;
                        }
                        case 120: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 121;
                                continue;
                            }
                            continue;
                        }
                        case 121: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 122;
                                continue;
                            }
                            continue;
                        }
                        case 122: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 123;
                                continue;
                            }
                            continue;
                        }
                        case 125: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(188, 190);
                                continue;
                            }
                            continue;
                        }
                        case 127: {
                            if (this.curChar == 92) {
                                this.jjAddStates(363, 366);
                                continue;
                            }
                            continue;
                        }
                        case 131: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(188, 190);
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(191, 199);
                                continue;
                            }
                            continue;
                        }
                        case 133: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(200, 203);
                                continue;
                            }
                            continue;
                        }
                        case 135:
                        case 137:
                        case 140:
                        case 144: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(133);
                                continue;
                            }
                            continue;
                        }
                        case 136: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 137;
                                continue;
                            }
                            continue;
                        }
                        case 138: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 139;
                                continue;
                            }
                            continue;
                        }
                        case 139: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 140;
                                continue;
                            }
                            continue;
                        }
                        case 141: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 142;
                                continue;
                            }
                            continue;
                        }
                        case 142: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 143;
                                continue;
                            }
                            continue;
                        }
                        case 143: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 144;
                                continue;
                            }
                            continue;
                        }
                        case 146: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 148: {
                            if (this.curChar == 92) {
                                this.jjAddStates(367, 370);
                                continue;
                            }
                            continue;
                        }
                        case 152: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 153: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(207, 215);
                                continue;
                            }
                            continue;
                        }
                        case 154: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(216, 219);
                                continue;
                            }
                            continue;
                        }
                        case 156:
                        case 158:
                        case 161:
                        case 165: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(154);
                                continue;
                            }
                            continue;
                        }
                        case 157: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 158;
                                continue;
                            }
                            continue;
                        }
                        case 159: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 160;
                                continue;
                            }
                            continue;
                        }
                        case 160: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 161;
                                continue;
                            }
                            continue;
                        }
                        case 162: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 163;
                                continue;
                            }
                            continue;
                        }
                        case 163: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 164;
                                continue;
                            }
                            continue;
                        }
                        case 164: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 165;
                                continue;
                            }
                            continue;
                        }
                        case 167: {
                            if ((0x100000001000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 105;
                                continue;
                            }
                            continue;
                        }
                        case 168: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 167;
                                continue;
                            }
                            continue;
                        }
                        case 171: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(230, 238);
                            continue;
                        }
                        case 172: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(173);
                                continue;
                            }
                            continue;
                        }
                        case 174: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(239, 243);
                            continue;
                        }
                        case 175: {
                            if ((0x7E0000007EL & l) != 0x0L && kind > 61) {
                                kind = 61;
                                continue;
                            }
                            continue;
                        }
                        case 176:
                        case 178:
                        case 181:
                        case 185: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(175);
                                continue;
                            }
                            continue;
                        }
                        case 177: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 178;
                                continue;
                            }
                            continue;
                        }
                        case 179: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 180;
                                continue;
                            }
                            continue;
                        }
                        case 180: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 181;
                                continue;
                            }
                            continue;
                        }
                        case 182: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 183;
                                continue;
                            }
                            continue;
                        }
                        case 183: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 184;
                                continue;
                            }
                            continue;
                        }
                        case 184: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 185;
                                continue;
                            }
                            continue;
                        }
                        case 186:
                        case 188:
                        case 191:
                        case 195: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(172);
                                continue;
                            }
                            continue;
                        }
                        case 187: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 188;
                                continue;
                            }
                            continue;
                        }
                        case 189: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 190;
                                continue;
                            }
                            continue;
                        }
                        case 190: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 191;
                                continue;
                            }
                            continue;
                        }
                        case 192: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 193;
                                continue;
                            }
                            continue;
                        }
                        case 193: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 194;
                                continue;
                            }
                            continue;
                        }
                        case 194: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 195;
                                continue;
                            }
                            continue;
                        }
                        case 196: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(244, 246);
                            continue;
                        }
                        case 197: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(247, 249);
                            continue;
                        }
                        case 198: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddStates(250, 252);
                            continue;
                        }
                        case 201: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 61) {
                                kind = 61;
                            }
                            this.jjCheckNAddTwoStates(170, 175);
                            continue;
                        }
                        case 228: {
                            if ((0x200000002000L & l) != 0x0L && kind > 37) {
                                kind = 37;
                                continue;
                            }
                            continue;
                        }
                        case 229: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 228;
                                continue;
                            }
                            continue;
                        }
                        case 231: {
                            if ((0x100000001000000L & l) != 0x0L && kind > 38) {
                                kind = 38;
                                continue;
                            }
                            continue;
                        }
                        case 232: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 231;
                                continue;
                            }
                            continue;
                        }
                        case 234: {
                            if ((0x100000001000000L & l) != 0x0L && kind > 39) {
                                kind = 39;
                                continue;
                            }
                            continue;
                        }
                        case 235: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 234;
                                continue;
                            }
                            continue;
                        }
                        case 237: {
                            if ((0x200000002000L & l) != 0x0L && kind > 40) {
                                kind = 40;
                                continue;
                            }
                            continue;
                        }
                        case 238: {
                            if ((0x800000008L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 237;
                                continue;
                            }
                            continue;
                        }
                        case 240: {
                            if ((0x200000002000L & l) != 0x0L && kind > 41) {
                                kind = 41;
                                continue;
                            }
                            continue;
                        }
                        case 241: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 240;
                                continue;
                            }
                            continue;
                        }
                        case 243: {
                            if ((0x400000004000L & l) != 0x0L && kind > 42) {
                                kind = 42;
                                continue;
                            }
                            continue;
                        }
                        case 244: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 243;
                                continue;
                            }
                            continue;
                        }
                        case 246: {
                            if ((0x10000000100000L & l) != 0x0L && kind > 43) {
                                kind = 43;
                                continue;
                            }
                            continue;
                        }
                        case 247: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 246;
                                continue;
                            }
                            continue;
                        }
                        case 249: {
                            if ((0x800000008L & l) != 0x0L && kind > 44) {
                                kind = 44;
                                continue;
                            }
                            continue;
                        }
                        case 250: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 249;
                                continue;
                            }
                            continue;
                        }
                        case 252: {
                            if ((0x8000000080L & l) != 0x0L && kind > 45) {
                                kind = 45;
                                continue;
                            }
                            continue;
                        }
                        case 253: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 252;
                                continue;
                            }
                            continue;
                        }
                        case 254: {
                            if ((0x1000000010L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 253;
                                continue;
                            }
                            continue;
                        }
                        case 256: {
                            if ((0x1000000010L & l) != 0x0L && kind > 46) {
                                kind = 46;
                                continue;
                            }
                            continue;
                        }
                        case 257: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 256;
                                continue;
                            }
                            continue;
                        }
                        case 258: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 257;
                                continue;
                            }
                            continue;
                        }
                        case 260: {
                            if ((0x1000000010L & l) != 0x0L && kind > 47) {
                                kind = 47;
                                continue;
                            }
                            continue;
                        }
                        case 261: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 260;
                                continue;
                            }
                            continue;
                        }
                        case 262: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 261;
                                continue;
                            }
                            continue;
                        }
                        case 263: {
                            if ((0x8000000080L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 262;
                                continue;
                            }
                            continue;
                        }
                        case 265: {
                            if ((0x8000000080000L & l) != 0x0L && kind > 48) {
                                kind = 48;
                                continue;
                            }
                            continue;
                        }
                        case 266: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 265;
                                continue;
                            }
                            continue;
                        }
                        case 268: {
                            if ((0x8000000080000L & l) != 0x0L && kind > 49) {
                                kind = 49;
                                continue;
                            }
                            continue;
                        }
                        case 270: {
                            if ((0x400000004000000L & l) != 0x0L && kind > 50) {
                                kind = 50;
                                continue;
                            }
                            continue;
                        }
                        case 271: {
                            if ((0x10000000100L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 270;
                                continue;
                            }
                            continue;
                        }
                        case 273: {
                            if ((0x400000004000000L & l) != 0x0L && kind > 51) {
                                kind = 51;
                                continue;
                            }
                            continue;
                        }
                        case 274: {
                            if ((0x10000000100L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 273;
                                continue;
                            }
                            continue;
                        }
                        case 275: {
                            if ((0x80000000800L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 274;
                                continue;
                            }
                            continue;
                        }
                        case 279:
                        case 280: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(280, 281);
                            continue;
                        }
                        case 281: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(282, 283);
                                continue;
                            }
                            continue;
                        }
                        case 282: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(280, 281);
                            continue;
                        }
                        case 283: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddStates(265, 272);
                            continue;
                        }
                        case 284: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddStates(273, 275);
                            continue;
                        }
                        case 286:
                        case 288:
                        case 291:
                        case 295: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(284);
                                continue;
                            }
                            continue;
                        }
                        case 287: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 288;
                                continue;
                            }
                            continue;
                        }
                        case 289: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 290;
                                continue;
                            }
                            continue;
                        }
                        case 290: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 291;
                                continue;
                            }
                            continue;
                        }
                        case 292: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 293;
                                continue;
                            }
                            continue;
                        }
                        case 293: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 294;
                                continue;
                            }
                            continue;
                        }
                        case 294: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 295;
                                continue;
                            }
                            continue;
                        }
                        case 296: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(282, 297);
                                continue;
                            }
                            continue;
                        }
                        case 297: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddStates(276, 283);
                            continue;
                        }
                        case 298: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddStates(284, 286);
                            continue;
                        }
                        case 299:
                        case 301:
                        case 304:
                        case 308: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(298);
                                continue;
                            }
                            continue;
                        }
                        case 300: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 301;
                                continue;
                            }
                            continue;
                        }
                        case 302: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 303;
                                continue;
                            }
                            continue;
                        }
                        case 303: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 304;
                                continue;
                            }
                            continue;
                        }
                        case 305: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 306;
                                continue;
                            }
                            continue;
                        }
                        case 306: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 307;
                                continue;
                            }
                            continue;
                        }
                        case 307: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 308;
                                continue;
                            }
                            continue;
                        }
                        case 311: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(338, 342);
                            continue;
                        }
                        case 312: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                this.jjCheckNAddStates(103, 105);
                                continue;
                            }
                            continue;
                        }
                        case 314: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(315, 316);
                                continue;
                            }
                            continue;
                        }
                        case 315: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(103, 105);
                                continue;
                            }
                            continue;
                        }
                        case 316: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(287, 295);
                                continue;
                            }
                            continue;
                        }
                        case 317: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(296, 299);
                                continue;
                            }
                            continue;
                        }
                        case 319:
                        case 321:
                        case 324:
                        case 328: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(317);
                                continue;
                            }
                            continue;
                        }
                        case 320: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 321;
                                continue;
                            }
                            continue;
                        }
                        case 322: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 323;
                                continue;
                            }
                            continue;
                        }
                        case 323: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 324;
                                continue;
                            }
                            continue;
                        }
                        case 325: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 326;
                                continue;
                            }
                            continue;
                        }
                        case 326: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 327;
                                continue;
                            }
                            continue;
                        }
                        case 327: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 328;
                                continue;
                            }
                            continue;
                        }
                        case 329: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddTwoStates(329, 330);
                            continue;
                        }
                        case 330: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(331, 332);
                                continue;
                            }
                            continue;
                        }
                        case 331: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddTwoStates(329, 330);
                            continue;
                        }
                        case 332: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(300, 307);
                            continue;
                        }
                        case 333: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(308, 310);
                            continue;
                        }
                        case 335:
                        case 337:
                        case 340:
                        case 344: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(333);
                                continue;
                            }
                            continue;
                        }
                        case 336: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 337;
                                continue;
                            }
                            continue;
                        }
                        case 338: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 339;
                                continue;
                            }
                            continue;
                        }
                        case 339: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 340;
                                continue;
                            }
                            continue;
                        }
                        case 341: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 342;
                                continue;
                            }
                            continue;
                        }
                        case 342: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 343;
                                continue;
                            }
                            continue;
                        }
                        case 343: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 344;
                                continue;
                            }
                            continue;
                        }
                        case 403: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddStates(343, 346);
                                continue;
                            }
                            continue;
                        }
                        case 404: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(314, 321);
                            continue;
                        }
                        case 405: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(322, 324);
                            continue;
                        }
                        case 406:
                        case 408:
                        case 411:
                        case 415: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(405);
                                continue;
                            }
                            continue;
                        }
                        case 407: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 408;
                                continue;
                            }
                            continue;
                        }
                        case 409: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 410;
                                continue;
                            }
                            continue;
                        }
                        case 410: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 411;
                                continue;
                            }
                            continue;
                        }
                        case 412: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 413;
                                continue;
                            }
                            continue;
                        }
                        case 413: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 414;
                                continue;
                            }
                            continue;
                        }
                        case 414: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 415;
                                continue;
                            }
                            continue;
                        }
                        case 416: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(325, 333);
                                continue;
                            }
                            continue;
                        }
                        case 417: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(334, 337);
                                continue;
                            }
                            continue;
                        }
                        case 418:
                        case 420:
                        case 423:
                        case 427: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(417);
                                continue;
                            }
                            continue;
                        }
                        case 419: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 420;
                                continue;
                            }
                            continue;
                        }
                        case 421: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 422;
                                continue;
                            }
                            continue;
                        }
                        case 422: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 423;
                                continue;
                            }
                            continue;
                        }
                        case 424: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 425;
                                continue;
                            }
                            continue;
                        }
                        case 425: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 426;
                                continue;
                            }
                            continue;
                        }
                        case 426: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 427;
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
                final int i3 = (this.curChar & 0xFF) >> 6;
                final long l3 = 1L << (this.curChar & 0x3F);
                do {
                    switch (this.jjstateSet[--i]) {
                        case 60:
                        case 63: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(61, 62);
                            continue;
                        }
                        case 61:
                        case 430: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 34) {
                                kind = 34;
                            }
                            this.jjCheckNAddTwoStates(61, 62);
                            continue;
                        }
                        case 0: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddStates(338, 342);
                            continue;
                        }
                        case 428: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(103, 105);
                            }
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                if (kind > 58) {
                                    kind = 58;
                                }
                                this.jjCheckNAddTwoStates(329, 330);
                                continue;
                            }
                            continue;
                        }
                        case 1:
                        case 3: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 20) {
                                kind = 20;
                            }
                            this.jjCheckNAddTwoStates(1, 2);
                            continue;
                        }
                        case 18:
                        case 24: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 39:
                        case 45: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 106:
                        case 110: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(171, 174);
                                continue;
                            }
                            continue;
                        }
                        case 125:
                        case 131: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(188, 190);
                                continue;
                            }
                            continue;
                        }
                        case 146:
                        case 152: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 279:
                        case 280:
                        case 282: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 53) {
                                kind = 53;
                            }
                            this.jjCheckNAddTwoStates(280, 281);
                            continue;
                        }
                        case 312:
                        case 315: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(103, 105);
                                continue;
                            }
                            continue;
                        }
                        case 329:
                        case 331: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 58) {
                                kind = 58;
                            }
                            this.jjCheckNAddTwoStates(329, 330);
                            continue;
                        }
                        default: {
                            if (i2 == 0 || l2 == 0L || i3 == 0 || l3 == 0L) {}
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
            final int n2 = 428;
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
            final String im = SACParserCSS2TokenManager.jjstrLiteralImages[this.jjmatchedKind];
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
                return (SACParserCSS2TokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (SACParserCSS2TokenManager.jjbitVec0[i1] & l1) != 0x0L;
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
                        if (this.jjmatchedPos < 0 || (this.jjmatchedPos == 0 && this.jjmatchedKind > 79)) {
                            this.jjmatchedKind = 79;
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
                if ((SACParserCSS2TokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                    final Token matchedToken = this.jjFillToken();
                    this.TokenLexicalActions(matchedToken);
                    if (SACParserCSS2TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSS2TokenManager.jjnewLexState[this.jjmatchedKind];
                    }
                    return matchedToken;
                }
                if ((SACParserCSS2TokenManager.jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                    this.jjimageLen += this.jjmatchedPos + 1;
                    if (SACParserCSS2TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSS2TokenManager.jjnewLexState[this.jjmatchedKind];
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
                if (SACParserCSS2TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = SACParserCSS2TokenManager.jjnewLexState[this.jjmatchedKind];
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
            case 21: {
                final StringBuilder image = this.image;
                final CharStream input_stream = this.input_stream;
                final int jjimageLen = this.jjimageLen;
                final int lengthOfMatch = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch;
                image.append(input_stream.GetSuffix(jjimageLen + lengthOfMatch));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 1);
                break;
            }
            case 24: {
                final StringBuilder image2 = this.image;
                final CharStream input_stream2 = this.input_stream;
                final int jjimageLen2 = this.jjimageLen;
                final int lengthOfMatch2 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch2;
                image2.append(input_stream2.GetSuffix(jjimageLen2 + lengthOfMatch2));
                matchedToken.image = ParserUtils.trimUrl(this.image);
                break;
            }
            case 37: {
                final StringBuilder image3 = this.image;
                final CharStream input_stream3 = this.input_stream;
                final int jjimageLen3 = this.jjimageLen;
                final int lengthOfMatch3 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch3;
                image3.append(input_stream3.GetSuffix(jjimageLen3 + lengthOfMatch3));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 38: {
                final StringBuilder image4 = this.image;
                final CharStream input_stream4 = this.input_stream;
                final int jjimageLen4 = this.jjimageLen;
                final int lengthOfMatch4 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch4;
                image4.append(input_stream4.GetSuffix(jjimageLen4 + lengthOfMatch4));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 39: {
                final StringBuilder image5 = this.image;
                final CharStream input_stream5 = this.input_stream;
                final int jjimageLen5 = this.jjimageLen;
                final int lengthOfMatch5 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch5;
                image5.append(input_stream5.GetSuffix(jjimageLen5 + lengthOfMatch5));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 40: {
                final StringBuilder image6 = this.image;
                final CharStream input_stream6 = this.input_stream;
                final int jjimageLen6 = this.jjimageLen;
                final int lengthOfMatch6 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch6;
                image6.append(input_stream6.GetSuffix(jjimageLen6 + lengthOfMatch6));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 41: {
                final StringBuilder image7 = this.image;
                final CharStream input_stream7 = this.input_stream;
                final int jjimageLen7 = this.jjimageLen;
                final int lengthOfMatch7 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch7;
                image7.append(input_stream7.GetSuffix(jjimageLen7 + lengthOfMatch7));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 42: {
                final StringBuilder image8 = this.image;
                final CharStream input_stream8 = this.input_stream;
                final int jjimageLen8 = this.jjimageLen;
                final int lengthOfMatch8 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch8;
                image8.append(input_stream8.GetSuffix(jjimageLen8 + lengthOfMatch8));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 43: {
                final StringBuilder image9 = this.image;
                final CharStream input_stream9 = this.input_stream;
                final int jjimageLen9 = this.jjimageLen;
                final int lengthOfMatch9 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch9;
                image9.append(input_stream9.GetSuffix(jjimageLen9 + lengthOfMatch9));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 44: {
                final StringBuilder image10 = this.image;
                final CharStream input_stream10 = this.input_stream;
                final int jjimageLen10 = this.jjimageLen;
                final int lengthOfMatch10 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch10;
                image10.append(input_stream10.GetSuffix(jjimageLen10 + lengthOfMatch10));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 45: {
                final StringBuilder image11 = this.image;
                final CharStream input_stream11 = this.input_stream;
                final int jjimageLen11 = this.jjimageLen;
                final int lengthOfMatch11 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch11;
                image11.append(input_stream11.GetSuffix(jjimageLen11 + lengthOfMatch11));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 46: {
                final StringBuilder image12 = this.image;
                final CharStream input_stream12 = this.input_stream;
                final int jjimageLen12 = this.jjimageLen;
                final int lengthOfMatch12 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch12;
                image12.append(input_stream12.GetSuffix(jjimageLen12 + lengthOfMatch12));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 47: {
                final StringBuilder image13 = this.image;
                final CharStream input_stream13 = this.input_stream;
                final int jjimageLen13 = this.jjimageLen;
                final int lengthOfMatch13 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch13;
                image13.append(input_stream13.GetSuffix(jjimageLen13 + lengthOfMatch13));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 4);
                break;
            }
            case 48: {
                final StringBuilder image14 = this.image;
                final CharStream input_stream14 = this.input_stream;
                final int jjimageLen14 = this.jjimageLen;
                final int lengthOfMatch14 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch14;
                image14.append(input_stream14.GetSuffix(jjimageLen14 + lengthOfMatch14));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 49: {
                final StringBuilder image15 = this.image;
                final CharStream input_stream15 = this.input_stream;
                final int jjimageLen15 = this.jjimageLen;
                final int lengthOfMatch15 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch15;
                image15.append(input_stream15.GetSuffix(jjimageLen15 + lengthOfMatch15));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 1);
                break;
            }
            case 50: {
                final StringBuilder image16 = this.image;
                final CharStream input_stream16 = this.input_stream;
                final int jjimageLen16 = this.jjimageLen;
                final int lengthOfMatch16 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch16;
                image16.append(input_stream16.GetSuffix(jjimageLen16 + lengthOfMatch16));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 51: {
                final StringBuilder image17 = this.image;
                final CharStream input_stream17 = this.input_stream;
                final int jjimageLen17 = this.jjimageLen;
                final int lengthOfMatch17 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch17;
                image17.append(input_stream17.GetSuffix(jjimageLen17 + lengthOfMatch17));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 3);
                break;
            }
            case 52: {
                final StringBuilder image18 = this.image;
                final CharStream input_stream18 = this.input_stream;
                final int jjimageLen18 = this.jjimageLen;
                final int lengthOfMatch18 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch18;
                image18.append(input_stream18.GetSuffix(jjimageLen18 + lengthOfMatch18));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 1);
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
            this.jjstateSet[this.jjnewStateCnt++] = SACParserCSS2TokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(SACParserCSS2TokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    public SACParserCSS2TokenManager(final CharStream stream) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[428];
        this.jjstateSet = new int[856];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.input_stream = stream;
    }
    
    public SACParserCSS2TokenManager(final CharStream stream, final int lexState) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[428];
        this.jjstateSet = new int[856];
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
        int i = 428;
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
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, "{", "}", ",", ".", ";", ":", "*", "/", "+", "-", "=", ">", "[", "]", null, null, ")", null, null, "<!--", "-->", "~=", "|=", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        jjnextStates = new int[] { 346, 347, 348, 229, 349, 350, 351, 232, 352, 353, 354, 235, 355, 356, 357, 238, 358, 359, 360, 241, 361, 362, 363, 244, 364, 365, 366, 247, 367, 368, 369, 250, 370, 371, 372, 254, 373, 374, 375, 258, 376, 377, 378, 263, 379, 380, 381, 266, 382, 383, 384, 268, 385, 386, 387, 271, 388, 389, 390, 275, 391, 392, 393, 277, 394, 395, 396, 279, 397, 398, 399, 400, 401, 402, 296, 227, 230, 233, 236, 239, 242, 245, 248, 251, 255, 259, 264, 267, 269, 272, 276, 278, 309, 310, 39, 40, 41, 18, 19, 20, 278, 279, 296, 312, 313, 314, 1, 5, 7, 8, 10, 13, 6, 2, 1, 6, 2, 18, 26, 28, 29, 31, 34, 27, 19, 20, 18, 27, 19, 20, 39, 47, 49, 50, 52, 55, 48, 40, 41, 39, 48, 40, 41, 61, 65, 67, 68, 70, 73, 66, 62, 61, 66, 62, 79, 80, 81, 83, 86, 66, 61, 62, 66, 61, 62, 106, 124, 145, 108, 109, 166, 106, 107, 108, 109, 106, 112, 114, 115, 117, 120, 108, 109, 113, 106, 108, 109, 113, 125, 126, 127, 125, 133, 135, 136, 138, 141, 134, 126, 127, 125, 134, 126, 127, 146, 147, 148, 146, 154, 156, 157, 159, 162, 155, 147, 148, 146, 155, 147, 148, 106, 124, 145, 107, 108, 109, 166, 170, 171, 215, 172, 186, 187, 189, 192, 173, 170, 196, 208, 175, 176, 177, 179, 182, 170, 197, 204, 170, 198, 202, 170, 200, 201, 199, 205, 207, 199, 209, 211, 214, 219, 222, 224, 225, 199, 280, 284, 286, 287, 289, 292, 285, 281, 280, 285, 281, 298, 299, 300, 302, 305, 285, 280, 281, 285, 280, 281, 312, 317, 319, 320, 322, 325, 318, 313, 314, 312, 318, 313, 314, 329, 333, 335, 336, 338, 341, 334, 330, 329, 334, 330, 394, 279, 296, 405, 406, 407, 409, 412, 334, 329, 330, 334, 329, 330, 417, 418, 419, 421, 424, 318, 312, 313, 314, 318, 312, 313, 314, 312, 313, 329, 330, 314, 315, 331, 404, 416, 60, 77, 168, 169, 3, 4, 21, 23, 24, 25, 42, 44, 45, 46, 110, 111, 128, 130, 131, 132, 149, 151, 152, 153 };
        lexStateNames = new String[] { "DEFAULT", "COMMENT" };
        jjnewLexState = new int[] { -1, -1, -1, 1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { 4035225266115575751L, 32768L };
        jjtoSkip = new long[] { 16L, 0L };
        jjtoSpecial = new long[] { 0L, 0L };
        jjtoMore = new long[] { 40L, 0L };
    }
}
