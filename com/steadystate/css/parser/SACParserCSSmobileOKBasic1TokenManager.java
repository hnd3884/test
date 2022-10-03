package com.steadystate.css.parser;

import java.io.IOException;
import java.io.PrintStream;

public class SACParserCSSmobileOKBasic1TokenManager implements SACParserCSSmobileOKBasic1Constants
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
                if ((active0 & 0x2000L) != 0x0L) {
                    return 317;
                }
                if ((active0 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 3;
                    return 318;
                }
                if ((active0 & 0x60000000L) != 0x0L) {
                    return 89;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x60000000L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 1;
                    return 319;
                }
                if ((active0 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 3;
                    this.jjmatchedPos = 1;
                    return 318;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 3;
                    this.jjmatchedPos = 2;
                    return 318;
                }
                if ((active0 & 0x60000000L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 2;
                    return 319;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x60000000L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 3;
                    return 319;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x60000000L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 4;
                    return 319;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x20000000L) != 0x0L) {
                    this.jjmatchedKind = 32;
                    this.jjmatchedPos = 5;
                    return 319;
                }
                if ((active0 & 0x40000000L) != 0x0L) {
                    return 319;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x20000000L) != 0x0L) {
                    return 319;
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
                return this.jjStopAtPos(0, 25);
            }
            case 42: {
                return this.jjStopAtPos(0, 16);
            }
            case 43: {
                return this.jjStopAtPos(0, 18);
            }
            case 44: {
                return this.jjStopAtPos(0, 12);
            }
            case 45: {
                this.jjmatchedKind = 19;
                return this.jjMoveStringLiteralDfa1_0(268435456L);
            }
            case 46: {
                return this.jjStartNfaWithStates_0(0, 13, 317);
            }
            case 47: {
                this.jjmatchedKind = 17;
                return this.jjMoveStringLiteralDfa1_0(4L);
            }
            case 58: {
                this.jjmatchedKind = 15;
                return this.jjMoveStringLiteralDfa1_0(496L);
            }
            case 59: {
                return this.jjStopAtPos(0, 14);
            }
            case 60: {
                return this.jjMoveStringLiteralDfa1_0(134217728L);
            }
            case 61: {
                return this.jjStopAtPos(0, 20);
            }
            case 62: {
                return this.jjStopAtPos(0, 21);
            }
            case 64: {
                return this.jjMoveStringLiteralDfa1_0(1610612736L);
            }
            case 91: {
                return this.jjStopAtPos(0, 22);
            }
            case 93: {
                return this.jjStopAtPos(0, 23);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa1_0(8796093022208L);
            }
            case 123: {
                return this.jjStopAtPos(0, 10);
            }
            case 125: {
                return this.jjStopAtPos(0, 11);
            }
            default: {
                return this.jjMoveNfa_0(1, 0);
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
                return this.jjMoveStringLiteralDfa2_0(active0, 134217728L);
            }
            case 42: {
                if ((active0 & 0x4L) != 0x0L) {
                    return this.jjStopAtPos(1, 2);
                }
                break;
            }
            case 45: {
                return this.jjMoveStringLiteralDfa2_0(active0, 268435456L);
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa2_0(active0, 64L);
            }
            case 70:
            case 102: {
                return this.jjMoveStringLiteralDfa2_0(active0, 384L);
            }
            case 71:
            case 103: {
                return this.jjMoveStringLiteralDfa2_0(active0, 8796093022208L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa2_0(active0, 536870912L);
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa2_0(active0, 16L);
            }
            case 77:
            case 109: {
                return this.jjMoveStringLiteralDfa2_0(active0, 1073741824L);
            }
            case 86:
            case 118: {
                return this.jjMoveStringLiteralDfa2_0(active0, 32L);
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
                return this.jjMoveStringLiteralDfa3_0(active0, 134217728L);
            }
            case 62: {
                if ((active0 & 0x10000000L) != 0x0L) {
                    return this.jjStopAtPos(2, 28);
                }
                break;
            }
            case 66:
            case 98: {
                return this.jjMoveStringLiteralDfa3_0(active0, 8796093022208L);
            }
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa3_0(active0, 64L);
            }
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa3_0(active0, 1073741824L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa3_0(active0, 432L);
            }
            case 77:
            case 109: {
                return this.jjMoveStringLiteralDfa3_0(active0, 536870912L);
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
                if ((active0 & 0x80000000000L) != 0x0L) {
                    return this.jjStopAtPos(3, 43);
                }
                break;
            }
            case 45: {
                if ((active0 & 0x8000000L) != 0x0L) {
                    return this.jjStopAtPos(3, 27);
                }
                break;
            }
            case 68:
            case 100: {
                return this.jjMoveStringLiteralDfa4_0(active0, 1073741824L);
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa4_0(active0, 16L);
            }
            case 80:
            case 112: {
                return this.jjMoveStringLiteralDfa4_0(active0, 536870912L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa4_0(active0, 384L);
            }
            case 83:
            case 115: {
                return this.jjMoveStringLiteralDfa4_0(active0, 32L);
            }
            case 84:
            case 116: {
                return this.jjMoveStringLiteralDfa4_0(active0, 64L);
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
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa5_0(active0, 1073741920L);
            }
            case 75:
            case 107: {
                if ((active0 & 0x10L) != 0x0L) {
                    return this.jjStopAtPos(4, 4);
                }
                break;
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa5_0(active0, 536870912L);
            }
            case 83:
            case 115: {
                return this.jjMoveStringLiteralDfa5_0(active0, 384L);
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
            case 65:
            case 97: {
                if ((active0 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 30, 319);
                }
                break;
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa6_0(active0, 536870912L);
            }
            case 84:
            case 116: {
                return this.jjMoveStringLiteralDfa6_0(active0, 416L);
            }
            case 86:
            case 118: {
                return this.jjMoveStringLiteralDfa6_0(active0, 64L);
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
            case 45: {
                return this.jjMoveStringLiteralDfa7_0(active0, 384L);
            }
            case 69:
            case 101: {
                if ((active0 & 0x40L) != 0x0L) {
                    return this.jjStopAtPos(6, 6);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 32L);
            }
            case 84:
            case 116: {
                if ((active0 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 29, 319);
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
            case 68:
            case 100: {
                if ((active0 & 0x20L) != 0x0L) {
                    return this.jjStopAtPos(7, 5);
                }
                break;
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa8_0(active0, 384L);
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
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa9_0(active0, 256L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa9_0(active0, 128L);
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
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa10_0(active0, 128L);
            }
            case 84:
            case 116: {
                return this.jjMoveStringLiteralDfa10_0(active0, 256L);
            }
            default: {
                return this.jjStartNfa_0(8, active0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa10_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(8, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(9, active0);
            return 10;
        }
        switch (this.curChar) {
            case 69:
            case 101: {
                if ((active0 & 0x80L) != 0x0L) {
                    return this.jjStopAtPos(10, 7);
                }
                break;
            }
            case 84:
            case 116: {
                return this.jjMoveStringLiteralDfa11_0(active0, 256L);
            }
        }
        return this.jjStartNfa_0(9, active0);
    }
    
    private int jjMoveStringLiteralDfa11_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(9, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(10, active0);
            return 11;
        }
        switch (this.curChar) {
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa12_0(active0, 256L);
            }
            default: {
                return this.jjStartNfa_0(10, active0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa12_0(final long old0, long active0) {
        if ((active0 &= old0) == 0x0L) {
            return this.jjStartNfa_0(10, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(11, active0);
            return 12;
        }
        switch (this.curChar) {
            case 82:
            case 114: {
                if ((active0 & 0x100L) != 0x0L) {
                    return this.jjStopAtPos(12, 8);
                }
                break;
            }
        }
        return this.jjStartNfa_0(11, active0);
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
        this.jjnewStateCnt = 317;
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
                        case 317: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 46) {
                                    kind = 46;
                                }
                                this.jjCheckNAdd(282);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 42) {
                                    kind = 42;
                                }
                                this.jjCheckNAdd(281);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(279, 280);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(276, 278);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(273, 275);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(270, 272);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(267, 269);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(264, 266);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(261, 263);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(258, 260);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(255, 257);
                                continue;
                            }
                            continue;
                        }
                        case 2:
                        case 318: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 1: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 42) {
                                    kind = 42;
                                }
                                this.jjCheckNAddStates(0, 41);
                                continue;
                            }
                            if ((0x100003600L & l) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAdd(0);
                                continue;
                            }
                            if (this.curChar == 46) {
                                this.jjCheckNAddStates(42, 52);
                                continue;
                            }
                            if (this.curChar == 33) {
                                this.jjCheckNAddTwoStates(78, 87);
                                continue;
                            }
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            if (this.curChar == 35) {
                                this.jjCheckNAddTwoStates(19, 20);
                                continue;
                            }
                            continue;
                        }
                        case 90:
                        case 319: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddTwoStates(90, 91);
                            continue;
                        }
                        case 0: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 1) {
                                kind = 1;
                            }
                            this.jjCheckNAdd(0);
                            continue;
                        }
                        case 4: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 5: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(59, 66);
                            continue;
                        }
                        case 6: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(67, 69);
                            continue;
                        }
                        case 7: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 8:
                        case 10:
                        case 13:
                        case 17: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(6);
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
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
                        case 12: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
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
                        case 16: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 17;
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if (this.curChar == 35) {
                                this.jjCheckNAddTwoStates(19, 20);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            continue;
                        }
                        case 21: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            continue;
                        }
                        case 22: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddStates(70, 77);
                            continue;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddStates(78, 80);
                            continue;
                        }
                        case 24: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            continue;
                        }
                        case 25:
                        case 27:
                        case 30:
                        case 34: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(23);
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 27;
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 29;
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
                        case 33: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 34;
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if ((0xFFFFFFFB00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            continue;
                        }
                        case 37: {
                            if (this.curChar == 34 && kind > 24) {
                                kind = 24;
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 40;
                                continue;
                            }
                            continue;
                        }
                        case 42: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(81, 89);
                                continue;
                            }
                            continue;
                        }
                        case 44: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(90, 93);
                                continue;
                            }
                            continue;
                        }
                        case 45: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            continue;
                        }
                        case 46:
                        case 48:
                        case 51:
                        case 55: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(44);
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 48;
                                continue;
                            }
                            continue;
                        }
                        case 49: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 50;
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
                        case 54: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 55;
                                continue;
                            }
                            continue;
                        }
                        case 56: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
                            continue;
                        }
                        case 57: {
                            if ((0xFFFFFF7F00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
                            continue;
                        }
                        case 58: {
                            if (this.curChar == 39 && kind > 24) {
                                kind = 24;
                                continue;
                            }
                            continue;
                        }
                        case 60: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
                            continue;
                        }
                        case 61: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
                            continue;
                        }
                        case 62: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 61;
                                continue;
                            }
                            continue;
                        }
                        case 63: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
                            continue;
                        }
                        case 64: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(94, 102);
                                continue;
                            }
                            continue;
                        }
                        case 65: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(103, 106);
                                continue;
                            }
                            continue;
                        }
                        case 66: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
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
                        case 77: {
                            if (this.curChar == 33) {
                                this.jjCheckNAddTwoStates(78, 87);
                                continue;
                            }
                            continue;
                        }
                        case 78: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(78, 87);
                                continue;
                            }
                            continue;
                        }
                        case 92: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddTwoStates(90, 91);
                            continue;
                        }
                        case 93: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddStates(107, 114);
                            continue;
                        }
                        case 94: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddStates(115, 117);
                            continue;
                        }
                        case 95: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddTwoStates(90, 91);
                            continue;
                        }
                        case 96:
                        case 98:
                        case 101:
                        case 105: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(94);
                                continue;
                            }
                            continue;
                        }
                        case 97: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 98;
                                continue;
                            }
                            continue;
                        }
                        case 99: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 100;
                                continue;
                            }
                            continue;
                        }
                        case 100: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 101;
                                continue;
                            }
                            continue;
                        }
                        case 102: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 103;
                                continue;
                            }
                            continue;
                        }
                        case 103: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 104;
                                continue;
                            }
                            continue;
                        }
                        case 104: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 105;
                                continue;
                            }
                            continue;
                        }
                        case 107: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddStates(118, 125);
                            continue;
                        }
                        case 108: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddStates(126, 128);
                            continue;
                        }
                        case 109:
                        case 111:
                        case 114:
                        case 118: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(108);
                                continue;
                            }
                            continue;
                        }
                        case 110: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 111;
                                continue;
                            }
                            continue;
                        }
                        case 112: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 113;
                                continue;
                            }
                            continue;
                        }
                        case 113: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 114;
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
                        case 116: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 117;
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
                        case 120: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(129, 136);
                            continue;
                        }
                        case 121: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(137, 139);
                            continue;
                        }
                        case 122:
                        case 124:
                        case 127:
                        case 131: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(121);
                                continue;
                            }
                            continue;
                        }
                        case 123: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 124;
                                continue;
                            }
                            continue;
                        }
                        case 125: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 126;
                                continue;
                            }
                            continue;
                        }
                        case 126: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 127;
                                continue;
                            }
                            continue;
                        }
                        case 128: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 129;
                                continue;
                            }
                            continue;
                        }
                        case 129: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 130;
                                continue;
                            }
                            continue;
                        }
                        case 130: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 131;
                                continue;
                            }
                            continue;
                        }
                        case 133: {
                            if (this.curChar == 40) {
                                this.jjCheckNAddStates(140, 145);
                                continue;
                            }
                            continue;
                        }
                        case 134: {
                            if ((0xFFFFFC7A00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(146, 149);
                                continue;
                            }
                            continue;
                        }
                        case 135: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(135, 136);
                                continue;
                            }
                            continue;
                        }
                        case 136: {
                            if (this.curChar == 41 && kind > 26) {
                                kind = 26;
                                continue;
                            }
                            continue;
                        }
                        case 138: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(146, 149);
                                continue;
                            }
                            continue;
                        }
                        case 139: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(150, 158);
                                continue;
                            }
                            continue;
                        }
                        case 140: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(159, 162);
                                continue;
                            }
                            continue;
                        }
                        case 141: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(146, 149);
                                continue;
                            }
                            continue;
                        }
                        case 142:
                        case 144:
                        case 147:
                        case 151: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(140);
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
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 146;
                                continue;
                            }
                            continue;
                        }
                        case 146: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 147;
                                continue;
                            }
                            continue;
                        }
                        case 148: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 149;
                                continue;
                            }
                            continue;
                        }
                        case 149: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 150;
                                continue;
                            }
                            continue;
                        }
                        case 150: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 151;
                                continue;
                            }
                            continue;
                        }
                        case 152: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(163, 165);
                                continue;
                            }
                            continue;
                        }
                        case 153: {
                            if ((0xFFFFFF7F00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(163, 165);
                                continue;
                            }
                            continue;
                        }
                        case 154: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddTwoStates(135, 136);
                                continue;
                            }
                            continue;
                        }
                        case 156: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(163, 165);
                                continue;
                            }
                            continue;
                        }
                        case 157: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(163, 165);
                                continue;
                            }
                            continue;
                        }
                        case 158: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 157;
                                continue;
                            }
                            continue;
                        }
                        case 159: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(163, 165);
                                continue;
                            }
                            continue;
                        }
                        case 160: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(166, 174);
                                continue;
                            }
                            continue;
                        }
                        case 161: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(175, 178);
                                continue;
                            }
                            continue;
                        }
                        case 162: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(163, 165);
                                continue;
                            }
                            continue;
                        }
                        case 163:
                        case 165:
                        case 168:
                        case 172: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(161);
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
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 167;
                                continue;
                            }
                            continue;
                        }
                        case 167: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 168;
                                continue;
                            }
                            continue;
                        }
                        case 169: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 170;
                                continue;
                            }
                            continue;
                        }
                        case 170: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 171;
                                continue;
                            }
                            continue;
                        }
                        case 171: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 172;
                                continue;
                            }
                            continue;
                        }
                        case 173: {
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(179, 181);
                                continue;
                            }
                            continue;
                        }
                        case 174: {
                            if ((0xFFFFFFFB00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(179, 181);
                                continue;
                            }
                            continue;
                        }
                        case 175: {
                            if (this.curChar == 34) {
                                this.jjCheckNAddTwoStates(135, 136);
                                continue;
                            }
                            continue;
                        }
                        case 177: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(179, 181);
                                continue;
                            }
                            continue;
                        }
                        case 178: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(179, 181);
                                continue;
                            }
                            continue;
                        }
                        case 179: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 178;
                                continue;
                            }
                            continue;
                        }
                        case 180: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(179, 181);
                                continue;
                            }
                            continue;
                        }
                        case 181: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(182, 190);
                                continue;
                            }
                            continue;
                        }
                        case 182: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(191, 194);
                                continue;
                            }
                            continue;
                        }
                        case 183: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(179, 181);
                                continue;
                            }
                            continue;
                        }
                        case 184:
                        case 186:
                        case 189:
                        case 193: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(182);
                                continue;
                            }
                            continue;
                        }
                        case 185: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 186;
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
                        case 188: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 189;
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
                        case 191: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 192;
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
                        case 194: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(195, 201);
                                continue;
                            }
                            continue;
                        }
                        case 197: {
                            if (this.curChar == 43) {
                                this.jjCheckNAddStates(202, 204);
                                continue;
                            }
                            continue;
                        }
                        case 198:
                        case 227: {
                            if (this.curChar == 63 && kind > 47) {
                                kind = 47;
                                continue;
                            }
                            continue;
                        }
                        case 199: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(205, 213);
                            continue;
                        }
                        case 200: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(201);
                                continue;
                            }
                            continue;
                        }
                        case 201: {
                            if (this.curChar == 45) {
                                this.jjstateSet[this.jjnewStateCnt++] = 202;
                                continue;
                            }
                            continue;
                        }
                        case 202: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(214, 218);
                            continue;
                        }
                        case 203: {
                            if ((0x3FF000000000000L & l) != 0x0L && kind > 47) {
                                kind = 47;
                                continue;
                            }
                            continue;
                        }
                        case 204:
                        case 206:
                        case 209:
                        case 213: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(203);
                                continue;
                            }
                            continue;
                        }
                        case 205: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 206;
                                continue;
                            }
                            continue;
                        }
                        case 207: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 208;
                                continue;
                            }
                            continue;
                        }
                        case 208: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 209;
                                continue;
                            }
                            continue;
                        }
                        case 210: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 211;
                                continue;
                            }
                            continue;
                        }
                        case 211: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 212;
                                continue;
                            }
                            continue;
                        }
                        case 212: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 213;
                                continue;
                            }
                            continue;
                        }
                        case 214:
                        case 216:
                        case 219:
                        case 223: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(200);
                                continue;
                            }
                            continue;
                        }
                        case 215: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 216;
                                continue;
                            }
                            continue;
                        }
                        case 217: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 218;
                                continue;
                            }
                            continue;
                        }
                        case 218: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 219;
                                continue;
                            }
                            continue;
                        }
                        case 220: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 221;
                                continue;
                            }
                            continue;
                        }
                        case 221: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 222;
                                continue;
                            }
                            continue;
                        }
                        case 222: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 223;
                                continue;
                            }
                            continue;
                        }
                        case 224: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(219, 221);
                            continue;
                        }
                        case 225: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(222, 224);
                            continue;
                        }
                        case 226: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(225, 227);
                            continue;
                        }
                        case 228:
                        case 231:
                        case 233:
                        case 234:
                        case 237:
                        case 238:
                        case 240:
                        case 244:
                        case 248:
                        case 251:
                        case 253: {
                            if (this.curChar == 63) {
                                this.jjCheckNAdd(227);
                                continue;
                            }
                            continue;
                        }
                        case 229: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddTwoStates(198, 203);
                            continue;
                        }
                        case 230: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddTwoStates(227, 231);
                                continue;
                            }
                            continue;
                        }
                        case 232: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddStates(228, 230);
                                continue;
                            }
                            continue;
                        }
                        case 235: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 234;
                                continue;
                            }
                            continue;
                        }
                        case 236: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddStates(231, 234);
                                continue;
                            }
                            continue;
                        }
                        case 239: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 238;
                                continue;
                            }
                            continue;
                        }
                        case 241: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 240;
                                continue;
                            }
                            continue;
                        }
                        case 242: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 241;
                                continue;
                            }
                            continue;
                        }
                        case 243: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddStates(235, 239);
                                continue;
                            }
                            continue;
                        }
                        case 245: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 244;
                                continue;
                            }
                            continue;
                        }
                        case 246: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 245;
                                continue;
                            }
                            continue;
                        }
                        case 247: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 246;
                                continue;
                            }
                            continue;
                        }
                        case 249: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 248;
                                continue;
                            }
                            continue;
                        }
                        case 250: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 249;
                                continue;
                            }
                            continue;
                        }
                        case 252: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 251;
                                continue;
                            }
                            continue;
                        }
                        case 254: {
                            if (this.curChar == 46) {
                                this.jjCheckNAddStates(42, 52);
                                continue;
                            }
                            continue;
                        }
                        case 255: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(255, 257);
                                continue;
                            }
                            continue;
                        }
                        case 258: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(258, 260);
                                continue;
                            }
                            continue;
                        }
                        case 261: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(261, 263);
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
                                this.jjCheckNAddTwoStates(267, 269);
                                continue;
                            }
                            continue;
                        }
                        case 270: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(270, 272);
                                continue;
                            }
                            continue;
                        }
                        case 273: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(273, 275);
                                continue;
                            }
                            continue;
                        }
                        case 276: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(276, 278);
                                continue;
                            }
                            continue;
                        }
                        case 279: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(279, 280);
                                continue;
                            }
                            continue;
                        }
                        case 280: {
                            if (this.curChar == 37 && kind > 41) {
                                kind = 41;
                                continue;
                            }
                            continue;
                        }
                        case 281: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 42) {
                                kind = 42;
                            }
                            this.jjCheckNAdd(281);
                            continue;
                        }
                        case 282: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 46) {
                                kind = 46;
                            }
                            this.jjCheckNAdd(282);
                            continue;
                        }
                        case 283: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 42) {
                                kind = 42;
                            }
                            this.jjCheckNAddStates(0, 41);
                            continue;
                        }
                        case 284: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(284, 257);
                                continue;
                            }
                            continue;
                        }
                        case 285: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(285, 286);
                                continue;
                            }
                            continue;
                        }
                        case 286: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(255);
                                continue;
                            }
                            continue;
                        }
                        case 287: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(287, 260);
                                continue;
                            }
                            continue;
                        }
                        case 288: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(288, 289);
                                continue;
                            }
                            continue;
                        }
                        case 289: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(258);
                                continue;
                            }
                            continue;
                        }
                        case 290: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(290, 263);
                                continue;
                            }
                            continue;
                        }
                        case 291: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(291, 292);
                                continue;
                            }
                            continue;
                        }
                        case 292: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(261);
                                continue;
                            }
                            continue;
                        }
                        case 293: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(293, 266);
                                continue;
                            }
                            continue;
                        }
                        case 294: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(294, 295);
                                continue;
                            }
                            continue;
                        }
                        case 295: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(264);
                                continue;
                            }
                            continue;
                        }
                        case 296: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(296, 269);
                                continue;
                            }
                            continue;
                        }
                        case 297: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(297, 298);
                                continue;
                            }
                            continue;
                        }
                        case 298: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(267);
                                continue;
                            }
                            continue;
                        }
                        case 299: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(299, 272);
                                continue;
                            }
                            continue;
                        }
                        case 300: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(300, 301);
                                continue;
                            }
                            continue;
                        }
                        case 301: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(270);
                                continue;
                            }
                            continue;
                        }
                        case 302: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(302, 275);
                                continue;
                            }
                            continue;
                        }
                        case 303: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(303, 304);
                                continue;
                            }
                            continue;
                        }
                        case 304: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(273);
                                continue;
                            }
                            continue;
                        }
                        case 305: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(305, 278);
                                continue;
                            }
                            continue;
                        }
                        case 306: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(306, 307);
                                continue;
                            }
                            continue;
                        }
                        case 307: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(276);
                                continue;
                            }
                            continue;
                        }
                        case 308: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(308, 280);
                                continue;
                            }
                            continue;
                        }
                        case 309: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(309, 310);
                                continue;
                            }
                            continue;
                        }
                        case 310: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(279);
                                continue;
                            }
                            continue;
                        }
                        case 311: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 42) {
                                kind = 42;
                            }
                            this.jjCheckNAdd(311);
                            continue;
                        }
                        case 312: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(312, 313);
                                continue;
                            }
                            continue;
                        }
                        case 313: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(281);
                                continue;
                            }
                            continue;
                        }
                        case 314: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 46) {
                                kind = 46;
                            }
                            this.jjCheckNAdd(314);
                            continue;
                        }
                        case 315: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(315, 316);
                                continue;
                            }
                            continue;
                        }
                        case 316: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(282);
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
                        case 318: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L) {
                                if (kind > 3) {
                                    kind = 3;
                                }
                                this.jjCheckNAddTwoStates(2, 3);
                                continue;
                            }
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(4, 5);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L) {
                                if (kind > 3) {
                                    kind = 3;
                                }
                                this.jjCheckNAddTwoStates(2, 3);
                            }
                            else if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(4, 120);
                            }
                            else if (this.curChar == 64) {
                                this.jjAddStates(240, 241);
                            }
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjAddStates(242, 243);
                                continue;
                            }
                            continue;
                        }
                        case 89: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L) {
                                if (kind > 32) {
                                    kind = 32;
                                }
                                this.jjCheckNAddTwoStates(90, 91);
                                continue;
                            }
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(92, 107);
                                continue;
                            }
                            continue;
                        }
                        case 319: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L) {
                                if (kind > 32) {
                                    kind = 32;
                                }
                                this.jjCheckNAddTwoStates(90, 91);
                                continue;
                            }
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(92, 93);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 3: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(4, 5);
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 5: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(59, 66);
                            continue;
                        }
                        case 6: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(67, 69);
                            continue;
                        }
                        case 8:
                        case 10:
                        case 13:
                        case 17: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(6);
                                continue;
                            }
                            continue;
                        }
                        case 9: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 10;
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
                        case 12: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
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
                        case 16: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 17;
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            continue;
                        }
                        case 20: {
                            if (this.curChar == 92) {
                                this.jjAddStates(244, 245);
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            continue;
                        }
                        case 22: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddStates(70, 77);
                            continue;
                        }
                        case 23: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddStates(78, 80);
                            continue;
                        }
                        case 25:
                        case 27:
                        case 30:
                        case 34: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(23);
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 27;
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 29;
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
                        case 33: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 34;
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            continue;
                        }
                        case 38: {
                            if (this.curChar == 92) {
                                this.jjAddStates(246, 249);
                                continue;
                            }
                            continue;
                        }
                        case 42: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(81, 89);
                                continue;
                            }
                            continue;
                        }
                        case 44: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(90, 93);
                                continue;
                            }
                            continue;
                        }
                        case 46:
                        case 48:
                        case 51:
                        case 55: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(44);
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 48;
                                continue;
                            }
                            continue;
                        }
                        case 49: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 50;
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
                        case 54: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 55;
                                continue;
                            }
                            continue;
                        }
                        case 57: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
                            continue;
                        }
                        case 59: {
                            if (this.curChar == 92) {
                                this.jjAddStates(250, 253);
                                continue;
                            }
                            continue;
                        }
                        case 63: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
                            continue;
                        }
                        case 64: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(94, 102);
                                continue;
                            }
                            continue;
                        }
                        case 65: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(103, 106);
                                continue;
                            }
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
                        case 79: {
                            if ((0x10000000100000L & l) != 0x0L && kind > 31) {
                                kind = 31;
                                continue;
                            }
                            continue;
                        }
                        case 80: {
                            if ((0x400000004000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 79;
                                continue;
                            }
                            continue;
                        }
                        case 81: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 80;
                                continue;
                            }
                            continue;
                        }
                        case 82: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 81;
                                continue;
                            }
                            continue;
                        }
                        case 83: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 82;
                                continue;
                            }
                            continue;
                        }
                        case 84: {
                            if ((0x800000008000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 83;
                                continue;
                            }
                            continue;
                        }
                        case 85: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 84;
                                continue;
                            }
                            continue;
                        }
                        case 86: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 85;
                                continue;
                            }
                            continue;
                        }
                        case 87: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 86;
                                continue;
                            }
                            continue;
                        }
                        case 88: {
                            if (this.curChar == 64) {
                                this.jjAddStates(240, 241);
                                continue;
                            }
                            continue;
                        }
                        case 90: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddTwoStates(90, 91);
                            continue;
                        }
                        case 91: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(92, 93);
                                continue;
                            }
                            continue;
                        }
                        case 92: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddTwoStates(90, 91);
                            continue;
                        }
                        case 93: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddStates(107, 114);
                            continue;
                        }
                        case 94: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddStates(115, 117);
                            continue;
                        }
                        case 96:
                        case 98:
                        case 101:
                        case 105: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(94);
                                continue;
                            }
                            continue;
                        }
                        case 97: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 98;
                                continue;
                            }
                            continue;
                        }
                        case 99: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 100;
                                continue;
                            }
                            continue;
                        }
                        case 100: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 101;
                                continue;
                            }
                            continue;
                        }
                        case 102: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 103;
                                continue;
                            }
                            continue;
                        }
                        case 103: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 104;
                                continue;
                            }
                            continue;
                        }
                        case 104: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 105;
                                continue;
                            }
                            continue;
                        }
                        case 106: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(92, 107);
                                continue;
                            }
                            continue;
                        }
                        case 107: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddStates(118, 125);
                            continue;
                        }
                        case 108: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddStates(126, 128);
                            continue;
                        }
                        case 109:
                        case 111:
                        case 114:
                        case 118: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(108);
                                continue;
                            }
                            continue;
                        }
                        case 110: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 111;
                                continue;
                            }
                            continue;
                        }
                        case 112: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 113;
                                continue;
                            }
                            continue;
                        }
                        case 113: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 114;
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
                        case 116: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 117;
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
                        case 119: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(4, 120);
                                continue;
                            }
                            continue;
                        }
                        case 120: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(129, 136);
                            continue;
                        }
                        case 121: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(137, 139);
                            continue;
                        }
                        case 122:
                        case 124:
                        case 127:
                        case 131: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(121);
                                continue;
                            }
                            continue;
                        }
                        case 123: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 124;
                                continue;
                            }
                            continue;
                        }
                        case 125: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 126;
                                continue;
                            }
                            continue;
                        }
                        case 126: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 127;
                                continue;
                            }
                            continue;
                        }
                        case 128: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 129;
                                continue;
                            }
                            continue;
                        }
                        case 129: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 130;
                                continue;
                            }
                            continue;
                        }
                        case 130: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 131;
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjAddStates(242, 243);
                                continue;
                            }
                            continue;
                        }
                        case 134: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(146, 149);
                                continue;
                            }
                            continue;
                        }
                        case 137: {
                            if (this.curChar == 92) {
                                this.jjAddStates(254, 255);
                                continue;
                            }
                            continue;
                        }
                        case 138: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(146, 149);
                                continue;
                            }
                            continue;
                        }
                        case 139: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(150, 158);
                                continue;
                            }
                            continue;
                        }
                        case 140: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(159, 162);
                                continue;
                            }
                            continue;
                        }
                        case 142:
                        case 144:
                        case 147:
                        case 151: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(140);
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
                        case 145: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 146;
                                continue;
                            }
                            continue;
                        }
                        case 146: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 147;
                                continue;
                            }
                            continue;
                        }
                        case 148: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 149;
                                continue;
                            }
                            continue;
                        }
                        case 149: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 150;
                                continue;
                            }
                            continue;
                        }
                        case 150: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 151;
                                continue;
                            }
                            continue;
                        }
                        case 153: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(163, 165);
                                continue;
                            }
                            continue;
                        }
                        case 155: {
                            if (this.curChar == 92) {
                                this.jjAddStates(256, 259);
                                continue;
                            }
                            continue;
                        }
                        case 159: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(163, 165);
                                continue;
                            }
                            continue;
                        }
                        case 160: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(166, 174);
                                continue;
                            }
                            continue;
                        }
                        case 161: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(175, 178);
                                continue;
                            }
                            continue;
                        }
                        case 163:
                        case 165:
                        case 168:
                        case 172: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(161);
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
                        case 166: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 167;
                                continue;
                            }
                            continue;
                        }
                        case 167: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 168;
                                continue;
                            }
                            continue;
                        }
                        case 169: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 170;
                                continue;
                            }
                            continue;
                        }
                        case 170: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 171;
                                continue;
                            }
                            continue;
                        }
                        case 171: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 172;
                                continue;
                            }
                            continue;
                        }
                        case 174: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(179, 181);
                                continue;
                            }
                            continue;
                        }
                        case 176: {
                            if (this.curChar == 92) {
                                this.jjAddStates(260, 263);
                                continue;
                            }
                            continue;
                        }
                        case 180: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(179, 181);
                                continue;
                            }
                            continue;
                        }
                        case 181: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(182, 190);
                                continue;
                            }
                            continue;
                        }
                        case 182: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(191, 194);
                                continue;
                            }
                            continue;
                        }
                        case 184:
                        case 186:
                        case 189:
                        case 193: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(182);
                                continue;
                            }
                            continue;
                        }
                        case 185: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 186;
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
                        case 188: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 189;
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
                        case 191: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 192;
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
                        case 195: {
                            if ((0x100000001000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 133;
                                continue;
                            }
                            continue;
                        }
                        case 196: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 195;
                                continue;
                            }
                            continue;
                        }
                        case 199: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(205, 213);
                            continue;
                        }
                        case 200: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(201);
                                continue;
                            }
                            continue;
                        }
                        case 202: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(214, 218);
                            continue;
                        }
                        case 203: {
                            if ((0x7E0000007EL & l) != 0x0L && kind > 47) {
                                kind = 47;
                                continue;
                            }
                            continue;
                        }
                        case 204:
                        case 206:
                        case 209:
                        case 213: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(203);
                                continue;
                            }
                            continue;
                        }
                        case 205: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 206;
                                continue;
                            }
                            continue;
                        }
                        case 207: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 208;
                                continue;
                            }
                            continue;
                        }
                        case 208: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 209;
                                continue;
                            }
                            continue;
                        }
                        case 210: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 211;
                                continue;
                            }
                            continue;
                        }
                        case 211: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 212;
                                continue;
                            }
                            continue;
                        }
                        case 212: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 213;
                                continue;
                            }
                            continue;
                        }
                        case 214:
                        case 216:
                        case 219:
                        case 223: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(200);
                                continue;
                            }
                            continue;
                        }
                        case 215: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 216;
                                continue;
                            }
                            continue;
                        }
                        case 217: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 218;
                                continue;
                            }
                            continue;
                        }
                        case 218: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 219;
                                continue;
                            }
                            continue;
                        }
                        case 220: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 221;
                                continue;
                            }
                            continue;
                        }
                        case 221: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 222;
                                continue;
                            }
                            continue;
                        }
                        case 222: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 223;
                                continue;
                            }
                            continue;
                        }
                        case 224: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(219, 221);
                            continue;
                        }
                        case 225: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(222, 224);
                            continue;
                        }
                        case 226: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddStates(225, 227);
                            continue;
                        }
                        case 229: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 47) {
                                kind = 47;
                            }
                            this.jjCheckNAddTwoStates(198, 203);
                            continue;
                        }
                        case 256: {
                            if ((0x200000002000L & l) != 0x0L && kind > 33) {
                                kind = 33;
                                continue;
                            }
                            continue;
                        }
                        case 257: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 256;
                                continue;
                            }
                            continue;
                        }
                        case 259: {
                            if ((0x100000001000000L & l) != 0x0L && kind > 34) {
                                kind = 34;
                                continue;
                            }
                            continue;
                        }
                        case 260: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 259;
                                continue;
                            }
                            continue;
                        }
                        case 262: {
                            if ((0x100000001000000L & l) != 0x0L && kind > 35) {
                                kind = 35;
                                continue;
                            }
                            continue;
                        }
                        case 263: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 262;
                                continue;
                            }
                            continue;
                        }
                        case 265: {
                            if ((0x200000002000L & l) != 0x0L && kind > 36) {
                                kind = 36;
                                continue;
                            }
                            continue;
                        }
                        case 266: {
                            if ((0x800000008L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 265;
                                continue;
                            }
                            continue;
                        }
                        case 268: {
                            if ((0x200000002000L & l) != 0x0L && kind > 37) {
                                kind = 37;
                                continue;
                            }
                            continue;
                        }
                        case 269: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 268;
                                continue;
                            }
                            continue;
                        }
                        case 271: {
                            if ((0x400000004000L & l) != 0x0L && kind > 38) {
                                kind = 38;
                                continue;
                            }
                            continue;
                        }
                        case 272: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 271;
                                continue;
                            }
                            continue;
                        }
                        case 274: {
                            if ((0x10000000100000L & l) != 0x0L && kind > 39) {
                                kind = 39;
                                continue;
                            }
                            continue;
                        }
                        case 275: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 274;
                                continue;
                            }
                            continue;
                        }
                        case 277: {
                            if ((0x800000008L & l) != 0x0L && kind > 40) {
                                kind = 40;
                                continue;
                            }
                            continue;
                        }
                        case 278: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 277;
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
                        case 2:
                        case 4:
                        case 318: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 1: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 89:
                        case 92: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddTwoStates(90, 91);
                            continue;
                        }
                        case 90:
                        case 319: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 32) {
                                kind = 32;
                            }
                            this.jjCheckNAddTwoStates(90, 91);
                            continue;
                        }
                        case 19:
                        case 21: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(19, 20);
                            continue;
                        }
                        case 36:
                        case 42: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(56, 58);
                                continue;
                            }
                            continue;
                        }
                        case 57:
                        case 63: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(53, 55);
                                continue;
                            }
                            continue;
                        }
                        case 134:
                        case 138: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(146, 149);
                                continue;
                            }
                            continue;
                        }
                        case 153:
                        case 159: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(163, 165);
                                continue;
                            }
                            continue;
                        }
                        case 174:
                        case 180: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(179, 181);
                                continue;
                            }
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
            final int n2 = 317;
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
                return this.jjMoveStringLiteralDfa1_1(2L);
            }
            default: {
                return 1;
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_1(final long active1) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            return 1;
        }
        switch (this.curChar) {
            case 47: {
                if ((active1 & 0x2L) != 0x0L) {
                    return this.jjStopAtPos(1, 65);
                }
                return 2;
            }
            default: {
                return 2;
            }
        }
    }
    
    protected Token jjFillToken() {
        final String im = SACParserCSSmobileOKBasic1TokenManager.jjstrLiteralImages[this.jjmatchedKind];
        final String curTokenImage = (im == null) ? this.input_stream.GetImage() : im;
        final int beginLine = this.input_stream.getBeginLine();
        final int beginColumn = this.input_stream.getBeginColumn();
        final int endLine = this.input_stream.getEndLine();
        final int endColumn = this.input_stream.getEndColumn();
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
                return (SACParserCSSmobileOKBasic1TokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (SACParserCSSmobileOKBasic1TokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    public Token getNextToken() {
        int curPos = 0;
    Label_0382:
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
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_0();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 67) {
                            this.jjmatchedKind = 67;
                            break;
                        }
                        break;
                    }
                    case 1: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 66) {
                            this.jjmatchedKind = 66;
                            break;
                        }
                        break;
                    }
                }
                if (this.jjmatchedKind == Integer.MAX_VALUE) {
                    break Label_0382;
                }
                if (this.jjmatchedPos + 1 < curPos) {
                    this.input_stream.backup(curPos - this.jjmatchedPos - 1);
                }
                if ((SACParserCSSmobileOKBasic1TokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                    final Token matchedToken = this.jjFillToken();
                    this.TokenLexicalActions(matchedToken);
                    if (SACParserCSSmobileOKBasic1TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSSmobileOKBasic1TokenManager.jjnewLexState[this.jjmatchedKind];
                    }
                    return matchedToken;
                }
                if ((SACParserCSSmobileOKBasic1TokenManager.jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                    this.jjimageLen += this.jjmatchedPos + 1;
                    if (SACParserCSSmobileOKBasic1TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSSmobileOKBasic1TokenManager.jjnewLexState[this.jjmatchedKind];
                    }
                    curPos = 0;
                    this.jjmatchedKind = Integer.MAX_VALUE;
                    try {
                        this.curChar = this.input_stream.readChar();
                        continue;
                    }
                    catch (final IOException ex) {}
                    break Label_0382;
                }
                if (SACParserCSSmobileOKBasic1TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = SACParserCSSmobileOKBasic1TokenManager.jjnewLexState[this.jjmatchedKind];
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
            case 4: {
                final StringBuilder image = this.image;
                final CharStream input_stream = this.input_stream;
                final int jjimageLen = this.jjimageLen;
                final int lengthOfMatch = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch;
                image.append(input_stream.GetSuffix(jjimageLen + lengthOfMatch));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 0);
                break;
            }
            case 5: {
                final StringBuilder image2 = this.image;
                final CharStream input_stream2 = this.input_stream;
                final int jjimageLen2 = this.jjimageLen;
                final int lengthOfMatch2 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch2;
                image2.append(input_stream2.GetSuffix(jjimageLen2 + lengthOfMatch2));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 0);
                break;
            }
            case 6: {
                final StringBuilder image3 = this.image;
                final CharStream input_stream3 = this.input_stream;
                final int jjimageLen3 = this.jjimageLen;
                final int lengthOfMatch3 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch3;
                image3.append(input_stream3.GetSuffix(jjimageLen3 + lengthOfMatch3));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 0);
                break;
            }
            case 7: {
                final StringBuilder image4 = this.image;
                final CharStream input_stream4 = this.input_stream;
                final int jjimageLen4 = this.jjimageLen;
                final int lengthOfMatch4 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch4;
                image4.append(input_stream4.GetSuffix(jjimageLen4 + lengthOfMatch4));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 0);
                break;
            }
            case 8: {
                final StringBuilder image5 = this.image;
                final CharStream input_stream5 = this.input_stream;
                final int jjimageLen5 = this.jjimageLen;
                final int lengthOfMatch5 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch5;
                image5.append(input_stream5.GetSuffix(jjimageLen5 + lengthOfMatch5));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 0);
                break;
            }
            case 24: {
                final StringBuilder image6 = this.image;
                final CharStream input_stream6 = this.input_stream;
                final int jjimageLen6 = this.jjimageLen;
                final int lengthOfMatch6 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch6;
                image6.append(input_stream6.GetSuffix(jjimageLen6 + lengthOfMatch6));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 1);
                break;
            }
            case 26: {
                final StringBuilder image7 = this.image;
                final CharStream input_stream7 = this.input_stream;
                final int jjimageLen7 = this.jjimageLen;
                final int lengthOfMatch7 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch7;
                image7.append(input_stream7.GetSuffix(jjimageLen7 + lengthOfMatch7));
                matchedToken.image = ParserUtils.trimUrl(this.image);
                break;
            }
            case 33: {
                final StringBuilder image8 = this.image;
                final CharStream input_stream8 = this.input_stream;
                final int jjimageLen8 = this.jjimageLen;
                final int lengthOfMatch8 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch8;
                image8.append(input_stream8.GetSuffix(jjimageLen8 + lengthOfMatch8));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 34: {
                final StringBuilder image9 = this.image;
                final CharStream input_stream9 = this.input_stream;
                final int jjimageLen9 = this.jjimageLen;
                final int lengthOfMatch9 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch9;
                image9.append(input_stream9.GetSuffix(jjimageLen9 + lengthOfMatch9));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 35: {
                final StringBuilder image10 = this.image;
                final CharStream input_stream10 = this.input_stream;
                final int jjimageLen10 = this.jjimageLen;
                final int lengthOfMatch10 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch10;
                image10.append(input_stream10.GetSuffix(jjimageLen10 + lengthOfMatch10));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 36: {
                final StringBuilder image11 = this.image;
                final CharStream input_stream11 = this.input_stream;
                final int jjimageLen11 = this.jjimageLen;
                final int lengthOfMatch11 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch11;
                image11.append(input_stream11.GetSuffix(jjimageLen11 + lengthOfMatch11));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 37: {
                final StringBuilder image12 = this.image;
                final CharStream input_stream12 = this.input_stream;
                final int jjimageLen12 = this.jjimageLen;
                final int lengthOfMatch12 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch12;
                image12.append(input_stream12.GetSuffix(jjimageLen12 + lengthOfMatch12));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 38: {
                final StringBuilder image13 = this.image;
                final CharStream input_stream13 = this.input_stream;
                final int jjimageLen13 = this.jjimageLen;
                final int lengthOfMatch13 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch13;
                image13.append(input_stream13.GetSuffix(jjimageLen13 + lengthOfMatch13));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 39: {
                final StringBuilder image14 = this.image;
                final CharStream input_stream14 = this.input_stream;
                final int jjimageLen14 = this.jjimageLen;
                final int lengthOfMatch14 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch14;
                image14.append(input_stream14.GetSuffix(jjimageLen14 + lengthOfMatch14));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 40: {
                final StringBuilder image15 = this.image;
                final CharStream input_stream15 = this.input_stream;
                final int jjimageLen15 = this.jjimageLen;
                final int lengthOfMatch15 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch15;
                image15.append(input_stream15.GetSuffix(jjimageLen15 + lengthOfMatch15));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 41: {
                final StringBuilder image16 = this.image;
                final CharStream input_stream16 = this.input_stream;
                final int jjimageLen16 = this.jjimageLen;
                final int lengthOfMatch16 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch16;
                image16.append(input_stream16.GetSuffix(jjimageLen16 + lengthOfMatch16));
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
            this.jjstateSet[this.jjnewStateCnt++] = SACParserCSSmobileOKBasic1TokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(SACParserCSSmobileOKBasic1TokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    public SACParserCSSmobileOKBasic1TokenManager(final CharStream stream) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[317];
        this.jjstateSet = new int[634];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.input_stream = stream;
    }
    
    public SACParserCSSmobileOKBasic1TokenManager(final CharStream stream, final int lexState) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[317];
        this.jjstateSet = new int[634];
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
        int i = 317;
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
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, "{", "}", ",", ".", ";", ":", "*", "/", "+", "-", "=", ">", "[", "]", null, ")", null, "<!--", "-->", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        jjnextStates = new int[] { 284, 285, 286, 257, 287, 288, 289, 260, 290, 291, 292, 263, 293, 294, 295, 266, 296, 297, 298, 269, 299, 300, 301, 272, 302, 303, 304, 275, 305, 306, 307, 278, 308, 309, 310, 280, 311, 312, 313, 314, 315, 316, 255, 258, 261, 264, 267, 270, 273, 276, 279, 281, 282, 57, 58, 59, 36, 37, 38, 2, 6, 8, 9, 11, 14, 7, 3, 2, 7, 3, 19, 23, 25, 26, 28, 31, 24, 20, 19, 24, 20, 36, 44, 46, 47, 49, 52, 45, 37, 38, 36, 45, 37, 38, 57, 65, 67, 68, 70, 73, 66, 58, 59, 57, 66, 58, 59, 90, 94, 96, 97, 99, 102, 95, 91, 90, 95, 91, 108, 109, 110, 112, 115, 95, 90, 91, 95, 90, 91, 121, 122, 123, 125, 128, 7, 2, 3, 7, 2, 3, 134, 152, 173, 136, 137, 194, 134, 135, 136, 137, 134, 140, 142, 143, 145, 148, 136, 137, 141, 134, 136, 137, 141, 153, 154, 155, 153, 161, 163, 164, 166, 169, 162, 154, 155, 153, 162, 154, 155, 174, 175, 176, 174, 182, 184, 185, 187, 190, 183, 175, 176, 174, 183, 175, 176, 134, 152, 173, 135, 136, 137, 194, 198, 199, 243, 200, 214, 215, 217, 220, 201, 198, 224, 236, 203, 204, 205, 207, 210, 198, 225, 232, 198, 226, 230, 198, 228, 229, 227, 233, 235, 227, 237, 239, 242, 247, 250, 252, 253, 227, 89, 106, 196, 197, 21, 22, 39, 41, 42, 43, 60, 62, 63, 64, 138, 139, 156, 158, 159, 160, 177, 179, 180, 181 };
        lexStateNames = new String[] { "DEFAULT", "COMMENT" };
        jjnewLexState = new int[] { -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1 };
        jjtoToken = new long[] { 228698418577403L, 8L };
        jjtoSkip = new long[] { 0L, 2L };
        jjtoSpecial = new long[] { 0L, 0L };
        jjtoMore = new long[] { 4L, 4L };
    }
}
