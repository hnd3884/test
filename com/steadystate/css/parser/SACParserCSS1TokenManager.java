package com.steadystate.css.parser;

import java.io.IOException;
import java.io.PrintStream;

public class SACParserCSS1TokenManager implements SACParserCSS1Constants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final long[] jjbitVec1;
    static final long[] jjbitVec3;
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
                if ((active0 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 3;
                    return 347;
                }
                if ((active0 & 0x2000L) != 0x0L) {
                    return 348;
                }
                if ((active0 & 0x10000000L) != 0x0L) {
                    return 72;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 3;
                    this.jjmatchedPos = 1;
                    return 347;
                }
                if ((active0 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 30;
                    this.jjmatchedPos = 1;
                    return 349;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 3;
                    this.jjmatchedPos = 2;
                    return 347;
                }
                if ((active0 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 30;
                    this.jjmatchedPos = 2;
                    return 349;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 30;
                    this.jjmatchedPos = 3;
                    return 349;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 30;
                    this.jjmatchedPos = 4;
                    return 349;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 30;
                    this.jjmatchedPos = 5;
                    return 349;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x10000000L) != 0x0L) {
                    return 349;
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
                return this.jjStopAtPos(0, 24);
            }
            case 43: {
                return this.jjStopAtPos(0, 17);
            }
            case 44: {
                return this.jjStopAtPos(0, 12);
            }
            case 45: {
                this.jjmatchedKind = 18;
                return this.jjMoveStringLiteralDfa1_0(134217728L);
            }
            case 46: {
                return this.jjStartNfaWithStates_0(0, 13, 348);
            }
            case 47: {
                this.jjmatchedKind = 16;
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
                return this.jjMoveStringLiteralDfa1_0(67108864L);
            }
            case 61: {
                return this.jjStopAtPos(0, 19);
            }
            case 62: {
                return this.jjStopAtPos(0, 20);
            }
            case 64: {
                return this.jjMoveStringLiteralDfa1_0(268435456L);
            }
            case 91: {
                return this.jjStopAtPos(0, 21);
            }
            case 93: {
                return this.jjStopAtPos(0, 22);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa1_0(2199023255552L);
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
                return this.jjMoveStringLiteralDfa2_0(active0, 67108864L);
            }
            case 42: {
                if ((active0 & 0x4L) != 0x0L) {
                    return this.jjStopAtPos(1, 2);
                }
                break;
            }
            case 45: {
                return this.jjMoveStringLiteralDfa2_0(active0, 134217728L);
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
                return this.jjMoveStringLiteralDfa2_0(active0, 2199023255552L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa2_0(active0, 268435456L);
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa2_0(active0, 16L);
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
                return this.jjMoveStringLiteralDfa3_0(active0, 67108864L);
            }
            case 62: {
                if ((active0 & 0x8000000L) != 0x0L) {
                    return this.jjStopAtPos(2, 27);
                }
                break;
            }
            case 66:
            case 98: {
                return this.jjMoveStringLiteralDfa3_0(active0, 2199023255552L);
            }
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa3_0(active0, 64L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa3_0(active0, 432L);
            }
            case 77:
            case 109: {
                return this.jjMoveStringLiteralDfa3_0(active0, 268435456L);
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
                if ((active0 & 0x20000000000L) != 0x0L) {
                    return this.jjStopAtPos(3, 41);
                }
                break;
            }
            case 45: {
                if ((active0 & 0x4000000L) != 0x0L) {
                    return this.jjStopAtPos(3, 26);
                }
                break;
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa4_0(active0, 16L);
            }
            case 80:
            case 112: {
                return this.jjMoveStringLiteralDfa4_0(active0, 268435456L);
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
                return this.jjMoveStringLiteralDfa5_0(active0, 96L);
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
                return this.jjMoveStringLiteralDfa5_0(active0, 268435456L);
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
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa6_0(active0, 268435456L);
            }
            case 84:
            case 116: {
                return this.jjMoveStringLiteralDfa6_0(active0, 416L);
            }
            case 86:
            case 118: {
                return this.jjMoveStringLiteralDfa6_0(active0, 64L);
            }
            default: {
                return this.jjStartNfa_0(4, active0);
            }
        }
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
                if ((active0 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 28, 349);
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
        this.jjnewStateCnt = 347;
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
                        case 348: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 44) {
                                    kind = 44;
                                }
                                this.jjCheckNAdd(271);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 40) {
                                    kind = 40;
                                }
                                this.jjCheckNAdd(270);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(268, 269);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(265, 267);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(262, 264);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(259, 261);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(256, 258);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(253, 255);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(250, 252);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(247, 249);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(244, 246);
                                continue;
                            }
                            continue;
                        }
                        case 73:
                        case 349: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddTwoStates(73, 74);
                            continue;
                        }
                        case 105:
                        case 347: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(105, 106);
                            continue;
                        }
                        case 1: {
                            if ((0x3FF200000000000L & l) != 0x0L) {
                                if (kind > 54) {
                                    kind = 54;
                                }
                            }
                            else if ((0x100003600L & l) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                                this.jjCheckNAdd(0);
                            }
                            else if (this.curChar == 46) {
                                this.jjCheckNAddStates(0, 10);
                            }
                            else if (this.curChar == 33) {
                                this.jjCheckNAddTwoStates(61, 70);
                            }
                            else if (this.curChar == 39) {
                                this.jjCheckNAddStates(11, 13);
                            }
                            else if (this.curChar == 34) {
                                this.jjCheckNAddStates(14, 16);
                            }
                            else if (this.curChar == 35) {
                                this.jjCheckNAddTwoStates(2, 3);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 40) {
                                    kind = 40;
                                }
                                this.jjCheckNAddStates(17, 58);
                                continue;
                            }
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
                        case 2: {
                            if ((0x3FF200000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 4: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 5: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddStates(59, 66);
                            continue;
                        }
                        case 6: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddStates(67, 69);
                            continue;
                        }
                        case 7: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
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
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((0xFFFFFFFB00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if (this.curChar == 34 && kind > 23) {
                                kind = 23;
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 23;
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(70, 78);
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(79, 82);
                                continue;
                            }
                            continue;
                        }
                        case 28: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 29:
                        case 31:
                        case 34:
                        case 38: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(27);
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 31;
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
                        case 37: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 38;
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if ((0xFFFFFF7F00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if (this.curChar == 39 && kind > 23) {
                                kind = 23;
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 44: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 45: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 44;
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(83, 91);
                                continue;
                            }
                            continue;
                        }
                        case 48: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(92, 95);
                                continue;
                            }
                            continue;
                        }
                        case 49: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 50:
                        case 52:
                        case 55:
                        case 59: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(48);
                                continue;
                            }
                            continue;
                        }
                        case 51: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
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
                        case 58: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 59;
                                continue;
                            }
                            continue;
                        }
                        case 60: {
                            if (this.curChar == 33) {
                                this.jjCheckNAddTwoStates(61, 70);
                                continue;
                            }
                            continue;
                        }
                        case 61: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(61, 70);
                                continue;
                            }
                            continue;
                        }
                        case 75: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddTwoStates(73, 74);
                            continue;
                        }
                        case 76: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddStates(96, 103);
                            continue;
                        }
                        case 77: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddStates(104, 106);
                            continue;
                        }
                        case 78: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddTwoStates(73, 74);
                            continue;
                        }
                        case 79:
                        case 81:
                        case 84:
                        case 88: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(77);
                                continue;
                            }
                            continue;
                        }
                        case 80: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 81;
                                continue;
                            }
                            continue;
                        }
                        case 82: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 83;
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
                        case 85: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 86;
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
                        case 90: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddStates(107, 114);
                            continue;
                        }
                        case 91: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddStates(115, 117);
                            continue;
                        }
                        case 92:
                        case 94:
                        case 97:
                        case 101: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(91);
                                continue;
                            }
                            continue;
                        }
                        case 93: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 94;
                                continue;
                            }
                            continue;
                        }
                        case 95: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 96;
                                continue;
                            }
                            continue;
                        }
                        case 96: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 97;
                                continue;
                            }
                            continue;
                        }
                        case 98: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 99;
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
                        case 103: {
                            if ((0x3FF200000000000L & l) != 0x0L && kind > 54) {
                                kind = 54;
                                continue;
                            }
                            continue;
                        }
                        case 107: {
                            if ((0xFFFFFFFF00000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(105, 106);
                            continue;
                        }
                        case 108: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(118, 125);
                            continue;
                        }
                        case 109: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(126, 128);
                            continue;
                        }
                        case 110: {
                            if ((0x100003600L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(105, 106);
                            continue;
                        }
                        case 111:
                        case 113:
                        case 116:
                        case 120: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(109);
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
                        case 114: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 115;
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
                        case 119: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 120;
                                continue;
                            }
                            continue;
                        }
                        case 122: {
                            if (this.curChar == 40) {
                                this.jjCheckNAddStates(129, 134);
                                continue;
                            }
                            continue;
                        }
                        case 123: {
                            if ((0xFFFFFC7A00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(135, 138);
                                continue;
                            }
                            continue;
                        }
                        case 124: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(124, 125);
                                continue;
                            }
                            continue;
                        }
                        case 125: {
                            if (this.curChar == 41 && kind > 25) {
                                kind = 25;
                                continue;
                            }
                            continue;
                        }
                        case 127: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(135, 138);
                                continue;
                            }
                            continue;
                        }
                        case 128: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(139, 147);
                                continue;
                            }
                            continue;
                        }
                        case 129: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(148, 151);
                                continue;
                            }
                            continue;
                        }
                        case 130: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(135, 138);
                                continue;
                            }
                            continue;
                        }
                        case 131:
                        case 133:
                        case 136:
                        case 140: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(129);
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 133;
                                continue;
                            }
                            continue;
                        }
                        case 134: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 135;
                                continue;
                            }
                            continue;
                        }
                        case 135: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 136;
                                continue;
                            }
                            continue;
                        }
                        case 137: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 138;
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
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 142: {
                            if ((0xFFFFFF7F00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 143: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddTwoStates(124, 125);
                                continue;
                            }
                            continue;
                        }
                        case 145: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 146: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 147: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 146;
                                continue;
                            }
                            continue;
                        }
                        case 148: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 149: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(155, 163);
                                continue;
                            }
                            continue;
                        }
                        case 150: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(164, 167);
                                continue;
                            }
                            continue;
                        }
                        case 151: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 152:
                        case 154:
                        case 157:
                        case 161: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(150);
                                continue;
                            }
                            continue;
                        }
                        case 153: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 154;
                                continue;
                            }
                            continue;
                        }
                        case 155: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 156;
                                continue;
                            }
                            continue;
                        }
                        case 156: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 157;
                                continue;
                            }
                            continue;
                        }
                        case 158: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 159;
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
                            if (this.curChar == 34) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 163: {
                            if ((0xFFFFFFFB00000200L & l) != 0x0L) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 164: {
                            if (this.curChar == 34) {
                                this.jjCheckNAddTwoStates(124, 125);
                                continue;
                            }
                            continue;
                        }
                        case 166: {
                            if ((0x3400L & l) != 0x0L) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 167: {
                            if (this.curChar == 10) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 168: {
                            if (this.curChar == 13) {
                                this.jjstateSet[this.jjnewStateCnt++] = 167;
                                continue;
                            }
                            continue;
                        }
                        case 169: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 170: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(171, 179);
                                continue;
                            }
                            continue;
                        }
                        case 171: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(180, 183);
                                continue;
                            }
                            continue;
                        }
                        case 172: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 173:
                        case 175:
                        case 178:
                        case 182: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(171);
                                continue;
                            }
                            continue;
                        }
                        case 174: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 175;
                                continue;
                            }
                            continue;
                        }
                        case 176: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 177;
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
                        case 181: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 182;
                                continue;
                            }
                            continue;
                        }
                        case 183: {
                            if ((0x100003600L & l) != 0x0L) {
                                this.jjCheckNAddStates(184, 190);
                                continue;
                            }
                            continue;
                        }
                        case 186: {
                            if (this.curChar == 43) {
                                this.jjCheckNAddStates(191, 193);
                                continue;
                            }
                            continue;
                        }
                        case 187:
                        case 216: {
                            if (this.curChar == 63 && kind > 45) {
                                kind = 45;
                                continue;
                            }
                            continue;
                        }
                        case 188: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(194, 202);
                            continue;
                        }
                        case 189: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(190);
                                continue;
                            }
                            continue;
                        }
                        case 190: {
                            if (this.curChar == 45) {
                                this.jjstateSet[this.jjnewStateCnt++] = 191;
                                continue;
                            }
                            continue;
                        }
                        case 191: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(203, 207);
                            continue;
                        }
                        case 192: {
                            if ((0x3FF000000000000L & l) != 0x0L && kind > 45) {
                                kind = 45;
                                continue;
                            }
                            continue;
                        }
                        case 193:
                        case 195:
                        case 198:
                        case 202: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(192);
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
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 197;
                                continue;
                            }
                            continue;
                        }
                        case 197: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 198;
                                continue;
                            }
                            continue;
                        }
                        case 199: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                                continue;
                            }
                            continue;
                        }
                        case 200: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 201;
                                continue;
                            }
                            continue;
                        }
                        case 201: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 202;
                                continue;
                            }
                            continue;
                        }
                        case 203:
                        case 205:
                        case 208:
                        case 212: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(189);
                                continue;
                            }
                            continue;
                        }
                        case 204: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 205;
                                continue;
                            }
                            continue;
                        }
                        case 206: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 207;
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
                        case 209: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 210;
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
                        case 213: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(208, 210);
                            continue;
                        }
                        case 214: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(211, 213);
                            continue;
                        }
                        case 215: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(214, 216);
                            continue;
                        }
                        case 217:
                        case 220:
                        case 222:
                        case 223:
                        case 226:
                        case 227:
                        case 229:
                        case 233:
                        case 237:
                        case 240:
                        case 242: {
                            if (this.curChar == 63) {
                                this.jjCheckNAdd(216);
                                continue;
                            }
                            continue;
                        }
                        case 218: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddTwoStates(187, 192);
                            continue;
                        }
                        case 219: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddTwoStates(216, 220);
                                continue;
                            }
                            continue;
                        }
                        case 221: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddStates(217, 219);
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
                        case 225: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddStates(220, 223);
                                continue;
                            }
                            continue;
                        }
                        case 228: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 227;
                                continue;
                            }
                            continue;
                        }
                        case 230: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 229;
                                continue;
                            }
                            continue;
                        }
                        case 231: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 230;
                                continue;
                            }
                            continue;
                        }
                        case 232: {
                            if (this.curChar == 63) {
                                this.jjCheckNAddStates(224, 228);
                                continue;
                            }
                            continue;
                        }
                        case 234: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 233;
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
                                this.jjstateSet[this.jjnewStateCnt++] = 235;
                                continue;
                            }
                            continue;
                        }
                        case 238: {
                            if (this.curChar == 63) {
                                this.jjstateSet[this.jjnewStateCnt++] = 237;
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
                        case 243: {
                            if (this.curChar == 46) {
                                this.jjCheckNAddStates(0, 10);
                                continue;
                            }
                            continue;
                        }
                        case 244: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(244, 246);
                                continue;
                            }
                            continue;
                        }
                        case 247: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(247, 249);
                                continue;
                            }
                            continue;
                        }
                        case 250: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(250, 252);
                                continue;
                            }
                            continue;
                        }
                        case 253: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(253, 255);
                                continue;
                            }
                            continue;
                        }
                        case 256: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(256, 258);
                                continue;
                            }
                            continue;
                        }
                        case 259: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(259, 261);
                                continue;
                            }
                            continue;
                        }
                        case 262: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(262, 264);
                                continue;
                            }
                            continue;
                        }
                        case 265: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(265, 267);
                                continue;
                            }
                            continue;
                        }
                        case 268: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(268, 269);
                                continue;
                            }
                            continue;
                        }
                        case 269: {
                            if (this.curChar == 37 && kind > 39) {
                                kind = 39;
                                continue;
                            }
                            continue;
                        }
                        case 270: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 40) {
                                kind = 40;
                            }
                            this.jjCheckNAdd(270);
                            continue;
                        }
                        case 271: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 44) {
                                kind = 44;
                            }
                            this.jjCheckNAdd(271);
                            continue;
                        }
                        case 273: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L && kind > 54) {
                                kind = 54;
                                continue;
                            }
                            continue;
                        }
                        case 274: {
                            if ((0xFFFFFFFF00000000L & l) != 0x0L && kind > 55) {
                                kind = 55;
                                continue;
                            }
                            continue;
                        }
                        case 275: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 55) {
                                kind = 55;
                            }
                            this.jjCheckNAddStates(229, 234);
                            continue;
                        }
                        case 276: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 55) {
                                kind = 55;
                            }
                            this.jjCheckNAdd(277);
                            continue;
                        }
                        case 277: {
                            if ((0x100003600L & l) != 0x0L && kind > 55) {
                                kind = 55;
                                continue;
                            }
                            continue;
                        }
                        case 278:
                        case 280:
                        case 283:
                        case 287: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(276);
                                continue;
                            }
                            continue;
                        }
                        case 279: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 280;
                                continue;
                            }
                            continue;
                        }
                        case 281: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 282;
                                continue;
                            }
                            continue;
                        }
                        case 282: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 283;
                                continue;
                            }
                            continue;
                        }
                        case 284: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 285;
                                continue;
                            }
                            continue;
                        }
                        case 285: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 286;
                                continue;
                            }
                            continue;
                        }
                        case 286: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 287;
                                continue;
                            }
                            continue;
                        }
                        case 288: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAddStates(235, 240);
                            continue;
                        }
                        case 289: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(290);
                            continue;
                        }
                        case 290: {
                            if ((0x100003600L & l) != 0x0L && kind > 54) {
                                kind = 54;
                                continue;
                            }
                            continue;
                        }
                        case 291:
                        case 293:
                        case 296:
                        case 300: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(289);
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
                        case 294: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 295;
                                continue;
                            }
                            continue;
                        }
                        case 295: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 296;
                                continue;
                            }
                            continue;
                        }
                        case 297: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 298;
                                continue;
                            }
                            continue;
                        }
                        case 298: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 299;
                                continue;
                            }
                            continue;
                        }
                        case 299: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 300;
                                continue;
                            }
                            continue;
                        }
                        case 301: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(241, 248);
                            continue;
                        }
                        case 302: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(249, 251);
                            continue;
                        }
                        case 303:
                        case 305:
                        case 308:
                        case 312: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(302);
                                continue;
                            }
                            continue;
                        }
                        case 304: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 305;
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
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 310;
                                continue;
                            }
                            continue;
                        }
                        case 310: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 311;
                                continue;
                            }
                            continue;
                        }
                        case 311: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 312;
                                continue;
                            }
                            continue;
                        }
                        case 313: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 40) {
                                kind = 40;
                            }
                            this.jjCheckNAddStates(17, 58);
                            continue;
                        }
                        case 314: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(314, 246);
                                continue;
                            }
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
                                this.jjCheckNAdd(244);
                                continue;
                            }
                            continue;
                        }
                        case 317: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(317, 249);
                                continue;
                            }
                            continue;
                        }
                        case 318: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(318, 319);
                                continue;
                            }
                            continue;
                        }
                        case 319: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(247);
                                continue;
                            }
                            continue;
                        }
                        case 320: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(320, 252);
                                continue;
                            }
                            continue;
                        }
                        case 321: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(321, 322);
                                continue;
                            }
                            continue;
                        }
                        case 322: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(250);
                                continue;
                            }
                            continue;
                        }
                        case 323: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(323, 255);
                                continue;
                            }
                            continue;
                        }
                        case 324: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(324, 325);
                                continue;
                            }
                            continue;
                        }
                        case 325: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(253);
                                continue;
                            }
                            continue;
                        }
                        case 326: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(326, 258);
                                continue;
                            }
                            continue;
                        }
                        case 327: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(327, 328);
                                continue;
                            }
                            continue;
                        }
                        case 328: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(256);
                                continue;
                            }
                            continue;
                        }
                        case 329: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(329, 261);
                                continue;
                            }
                            continue;
                        }
                        case 330: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(330, 331);
                                continue;
                            }
                            continue;
                        }
                        case 331: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(259);
                                continue;
                            }
                            continue;
                        }
                        case 332: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(332, 264);
                                continue;
                            }
                            continue;
                        }
                        case 333: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(333, 334);
                                continue;
                            }
                            continue;
                        }
                        case 334: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(262);
                                continue;
                            }
                            continue;
                        }
                        case 335: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(335, 267);
                                continue;
                            }
                            continue;
                        }
                        case 336: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(336, 337);
                                continue;
                            }
                            continue;
                        }
                        case 337: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(265);
                                continue;
                            }
                            continue;
                        }
                        case 338: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(338, 269);
                                continue;
                            }
                            continue;
                        }
                        case 339: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(339, 340);
                                continue;
                            }
                            continue;
                        }
                        case 340: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(268);
                                continue;
                            }
                            continue;
                        }
                        case 341: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 40) {
                                kind = 40;
                            }
                            this.jjCheckNAdd(341);
                            continue;
                        }
                        case 342: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(342, 343);
                                continue;
                            }
                            continue;
                        }
                        case 343: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(270);
                                continue;
                            }
                            continue;
                        }
                        case 344: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 44) {
                                kind = 44;
                            }
                            this.jjCheckNAdd(344);
                            continue;
                        }
                        case 345: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(345, 346);
                                continue;
                            }
                            continue;
                        }
                        case 346: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(271);
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
                        case 72: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L) {
                                if (kind > 30) {
                                    kind = 30;
                                }
                                this.jjCheckNAddTwoStates(73, 74);
                                continue;
                            }
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(75, 90);
                                continue;
                            }
                            continue;
                        }
                        case 349: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L) {
                                if (kind > 30) {
                                    kind = 30;
                                }
                                this.jjCheckNAddTwoStates(73, 74);
                                continue;
                            }
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(75, 76);
                                continue;
                            }
                            continue;
                        }
                        case 347: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L) {
                                if (kind > 3) {
                                    kind = 3;
                                }
                                this.jjCheckNAddTwoStates(105, 106);
                                continue;
                            }
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(107, 108);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L) {
                                if (kind > 3) {
                                    kind = 3;
                                }
                                this.jjCheckNAddTwoStates(105, 106);
                            }
                            else if (this.curChar == 92) {
                                this.jjCheckNAddStates(252, 257);
                            }
                            else if (this.curChar == 64) {
                                this.jjAddStates(258, 259);
                            }
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L && kind > 54) {
                                kind = 54;
                            }
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjAddStates(260, 261);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 3: {
                            if (this.curChar == 92) {
                                this.jjAddStates(262, 263);
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 5: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddStates(59, 66);
                            continue;
                        }
                        case 6: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
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
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if (this.curChar == 92) {
                                this.jjAddStates(264, 267);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(70, 78);
                                continue;
                            }
                            continue;
                        }
                        case 27: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(79, 82);
                                continue;
                            }
                            continue;
                        }
                        case 29:
                        case 31:
                        case 34:
                        case 38: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(27);
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 31;
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
                        case 37: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 38;
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 42: {
                            if (this.curChar == 92) {
                                this.jjAddStates(268, 271);
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(83, 91);
                                continue;
                            }
                            continue;
                        }
                        case 48: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(92, 95);
                                continue;
                            }
                            continue;
                        }
                        case 50:
                        case 52:
                        case 55:
                        case 59: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(48);
                                continue;
                            }
                            continue;
                        }
                        case 51: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
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
                        case 58: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 59;
                                continue;
                            }
                            continue;
                        }
                        case 62: {
                            if ((0x10000000100000L & l) != 0x0L && kind > 29) {
                                kind = 29;
                                continue;
                            }
                            continue;
                        }
                        case 63: {
                            if ((0x400000004000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 62;
                                continue;
                            }
                            continue;
                        }
                        case 64: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 63;
                                continue;
                            }
                            continue;
                        }
                        case 65: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 64;
                                continue;
                            }
                            continue;
                        }
                        case 66: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 65;
                                continue;
                            }
                            continue;
                        }
                        case 67: {
                            if ((0x800000008000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 66;
                                continue;
                            }
                            continue;
                        }
                        case 68: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 67;
                                continue;
                            }
                            continue;
                        }
                        case 69: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 68;
                                continue;
                            }
                            continue;
                        }
                        case 70: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 69;
                                continue;
                            }
                            continue;
                        }
                        case 71: {
                            if (this.curChar == 64) {
                                this.jjAddStates(258, 259);
                                continue;
                            }
                            continue;
                        }
                        case 73: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddTwoStates(73, 74);
                            continue;
                        }
                        case 74: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(75, 76);
                                continue;
                            }
                            continue;
                        }
                        case 75: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddTwoStates(73, 74);
                            continue;
                        }
                        case 76: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddStates(96, 103);
                            continue;
                        }
                        case 77: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddStates(104, 106);
                            continue;
                        }
                        case 79:
                        case 81:
                        case 84:
                        case 88: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(77);
                                continue;
                            }
                            continue;
                        }
                        case 80: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 81;
                                continue;
                            }
                            continue;
                        }
                        case 82: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 83;
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
                        case 85: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 86;
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
                        case 89: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(75, 90);
                                continue;
                            }
                            continue;
                        }
                        case 90: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddStates(107, 114);
                            continue;
                        }
                        case 91: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddStates(115, 117);
                            continue;
                        }
                        case 92:
                        case 94:
                        case 97:
                        case 101: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(91);
                                continue;
                            }
                            continue;
                        }
                        case 93: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 94;
                                continue;
                            }
                            continue;
                        }
                        case 95: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 96;
                                continue;
                            }
                            continue;
                        }
                        case 96: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 97;
                                continue;
                            }
                            continue;
                        }
                        case 98: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 99;
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
                        case 103: {
                            if ((0x7FFFFFE07FFFFFEL & l) != 0x0L && kind > 54) {
                                kind = 54;
                                continue;
                            }
                            continue;
                        }
                        case 104: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(105, 106);
                            continue;
                        }
                        case 105: {
                            if ((0x7FFFFFE07FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(105, 106);
                            continue;
                        }
                        case 106: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddTwoStates(107, 108);
                                continue;
                            }
                            continue;
                        }
                        case 107: {
                            if ((Long.MAX_VALUE & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(105, 106);
                            continue;
                        }
                        case 108: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(118, 125);
                            continue;
                        }
                        case 109: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(126, 128);
                            continue;
                        }
                        case 111:
                        case 113:
                        case 116:
                        case 120: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(109);
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
                        case 114: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 115;
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
                        case 119: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 120;
                                continue;
                            }
                            continue;
                        }
                        case 121: {
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjAddStates(260, 261);
                                continue;
                            }
                            continue;
                        }
                        case 123: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(135, 138);
                                continue;
                            }
                            continue;
                        }
                        case 126: {
                            if (this.curChar == 92) {
                                this.jjAddStates(272, 273);
                                continue;
                            }
                            continue;
                        }
                        case 127: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(135, 138);
                                continue;
                            }
                            continue;
                        }
                        case 128: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(139, 147);
                                continue;
                            }
                            continue;
                        }
                        case 129: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(148, 151);
                                continue;
                            }
                            continue;
                        }
                        case 131:
                        case 133:
                        case 136:
                        case 140: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(129);
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 133;
                                continue;
                            }
                            continue;
                        }
                        case 134: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 135;
                                continue;
                            }
                            continue;
                        }
                        case 135: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 136;
                                continue;
                            }
                            continue;
                        }
                        case 137: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 138;
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
                        case 142: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 144: {
                            if (this.curChar == 92) {
                                this.jjAddStates(274, 277);
                                continue;
                            }
                            continue;
                        }
                        case 148: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 149: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(155, 163);
                                continue;
                            }
                            continue;
                        }
                        case 150: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(164, 167);
                                continue;
                            }
                            continue;
                        }
                        case 152:
                        case 154:
                        case 157:
                        case 161: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(150);
                                continue;
                            }
                            continue;
                        }
                        case 153: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 154;
                                continue;
                            }
                            continue;
                        }
                        case 155: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 156;
                                continue;
                            }
                            continue;
                        }
                        case 156: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 157;
                                continue;
                            }
                            continue;
                        }
                        case 158: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 159;
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
                        case 163: {
                            if ((0x7FFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 165: {
                            if (this.curChar == 92) {
                                this.jjAddStates(278, 281);
                                continue;
                            }
                            continue;
                        }
                        case 169: {
                            if ((Long.MAX_VALUE & l) != 0x0L) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 170: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(171, 179);
                                continue;
                            }
                            continue;
                        }
                        case 171: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(180, 183);
                                continue;
                            }
                            continue;
                        }
                        case 173:
                        case 175:
                        case 178:
                        case 182: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(171);
                                continue;
                            }
                            continue;
                        }
                        case 174: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 175;
                                continue;
                            }
                            continue;
                        }
                        case 176: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 177;
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
                        case 181: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 182;
                                continue;
                            }
                            continue;
                        }
                        case 184: {
                            if ((0x100000001000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 122;
                                continue;
                            }
                            continue;
                        }
                        case 185: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 184;
                                continue;
                            }
                            continue;
                        }
                        case 188: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(194, 202);
                            continue;
                        }
                        case 189: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(190);
                                continue;
                            }
                            continue;
                        }
                        case 191: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(203, 207);
                            continue;
                        }
                        case 192: {
                            if ((0x7E0000007EL & l) != 0x0L && kind > 45) {
                                kind = 45;
                                continue;
                            }
                            continue;
                        }
                        case 193:
                        case 195:
                        case 198:
                        case 202: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(192);
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
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 197;
                                continue;
                            }
                            continue;
                        }
                        case 197: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 198;
                                continue;
                            }
                            continue;
                        }
                        case 199: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                                continue;
                            }
                            continue;
                        }
                        case 200: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 201;
                                continue;
                            }
                            continue;
                        }
                        case 201: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 202;
                                continue;
                            }
                            continue;
                        }
                        case 203:
                        case 205:
                        case 208:
                        case 212: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(189);
                                continue;
                            }
                            continue;
                        }
                        case 204: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 205;
                                continue;
                            }
                            continue;
                        }
                        case 206: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 207;
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
                        case 209: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 210;
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
                        case 213: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(208, 210);
                            continue;
                        }
                        case 214: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(211, 213);
                            continue;
                        }
                        case 215: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddStates(214, 216);
                            continue;
                        }
                        case 218: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 45) {
                                kind = 45;
                            }
                            this.jjCheckNAddTwoStates(187, 192);
                            continue;
                        }
                        case 245: {
                            if ((0x200000002000L & l) != 0x0L && kind > 31) {
                                kind = 31;
                                continue;
                            }
                            continue;
                        }
                        case 246: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 245;
                                continue;
                            }
                            continue;
                        }
                        case 248: {
                            if ((0x100000001000000L & l) != 0x0L && kind > 32) {
                                kind = 32;
                                continue;
                            }
                            continue;
                        }
                        case 249: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 248;
                                continue;
                            }
                            continue;
                        }
                        case 251: {
                            if ((0x100000001000000L & l) != 0x0L && kind > 33) {
                                kind = 33;
                                continue;
                            }
                            continue;
                        }
                        case 252: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 251;
                                continue;
                            }
                            continue;
                        }
                        case 254: {
                            if ((0x200000002000L & l) != 0x0L && kind > 34) {
                                kind = 34;
                                continue;
                            }
                            continue;
                        }
                        case 255: {
                            if ((0x800000008L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 254;
                                continue;
                            }
                            continue;
                        }
                        case 257: {
                            if ((0x200000002000L & l) != 0x0L && kind > 35) {
                                kind = 35;
                                continue;
                            }
                            continue;
                        }
                        case 258: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 257;
                                continue;
                            }
                            continue;
                        }
                        case 260: {
                            if ((0x400000004000L & l) != 0x0L && kind > 36) {
                                kind = 36;
                                continue;
                            }
                            continue;
                        }
                        case 261: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 260;
                                continue;
                            }
                            continue;
                        }
                        case 263: {
                            if ((0x10000000100000L & l) != 0x0L && kind > 37) {
                                kind = 37;
                                continue;
                            }
                            continue;
                        }
                        case 264: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 263;
                                continue;
                            }
                            continue;
                        }
                        case 266: {
                            if ((0x800000008L & l) != 0x0L && kind > 38) {
                                kind = 38;
                                continue;
                            }
                            continue;
                        }
                        case 267: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 266;
                                continue;
                            }
                            continue;
                        }
                        case 272: {
                            if (this.curChar == 92) {
                                this.jjCheckNAddStates(252, 257);
                                continue;
                            }
                            continue;
                        }
                        case 273: {
                            if ((Long.MAX_VALUE & l) != 0x0L && kind > 54) {
                                kind = 54;
                                continue;
                            }
                            continue;
                        }
                        case 274: {
                            if ((Long.MAX_VALUE & l) != 0x0L && kind > 55) {
                                kind = 55;
                                continue;
                            }
                            continue;
                        }
                        case 275: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 55) {
                                kind = 55;
                            }
                            this.jjCheckNAddStates(229, 234);
                            continue;
                        }
                        case 276: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 55) {
                                kind = 55;
                            }
                            this.jjCheckNAdd(277);
                            continue;
                        }
                        case 278:
                        case 280:
                        case 283:
                        case 287: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(276);
                                continue;
                            }
                            continue;
                        }
                        case 279: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 280;
                                continue;
                            }
                            continue;
                        }
                        case 281: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 282;
                                continue;
                            }
                            continue;
                        }
                        case 282: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 283;
                                continue;
                            }
                            continue;
                        }
                        case 284: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 285;
                                continue;
                            }
                            continue;
                        }
                        case 285: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 286;
                                continue;
                            }
                            continue;
                        }
                        case 286: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 287;
                                continue;
                            }
                            continue;
                        }
                        case 288: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAddStates(235, 240);
                            continue;
                        }
                        case 289: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 54) {
                                kind = 54;
                            }
                            this.jjCheckNAdd(290);
                            continue;
                        }
                        case 291:
                        case 293:
                        case 296:
                        case 300: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(289);
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
                        case 294: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 295;
                                continue;
                            }
                            continue;
                        }
                        case 295: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 296;
                                continue;
                            }
                            continue;
                        }
                        case 297: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 298;
                                continue;
                            }
                            continue;
                        }
                        case 298: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 299;
                                continue;
                            }
                            continue;
                        }
                        case 299: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 300;
                                continue;
                            }
                            continue;
                        }
                        case 301: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(241, 248);
                            continue;
                        }
                        case 302: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddStates(249, 251);
                            continue;
                        }
                        case 303:
                        case 305:
                        case 308:
                        case 312: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAdd(302);
                                continue;
                            }
                            continue;
                        }
                        case 304: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 305;
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
                        case 309: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 310;
                                continue;
                            }
                            continue;
                        }
                        case 310: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 311;
                                continue;
                            }
                            continue;
                        }
                        case 311: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 312;
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
                        case 72:
                        case 75: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddTwoStates(73, 74);
                            continue;
                        }
                        case 73:
                        case 349: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 30) {
                                kind = 30;
                            }
                            this.jjCheckNAddTwoStates(73, 74);
                            continue;
                        }
                        case 105:
                        case 107:
                        case 347: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(105, 106);
                            continue;
                        }
                        case 1: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 53) {
                                kind = 53;
                            }
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 54) {
                                kind = 54;
                            }
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                if (kind > 3) {
                                    kind = 3;
                                }
                                this.jjCheckNAddTwoStates(105, 106);
                                continue;
                            }
                            continue;
                        }
                        case 2:
                        case 4: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 9) {
                                kind = 9;
                            }
                            this.jjCheckNAddTwoStates(2, 3);
                            continue;
                        }
                        case 19: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(14, 16);
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(11, 13);
                                continue;
                            }
                            continue;
                        }
                        case 102: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 53) {
                                kind = 53;
                                continue;
                            }
                            continue;
                        }
                        case 103:
                        case 273: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 54) {
                                kind = 54;
                                continue;
                            }
                            continue;
                        }
                        case 104: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 3) {
                                kind = 3;
                            }
                            this.jjCheckNAddTwoStates(105, 106);
                            continue;
                        }
                        case 123: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(135, 138);
                                continue;
                            }
                            continue;
                        }
                        case 127: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(135, 138);
                                continue;
                            }
                            continue;
                        }
                        case 142: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 148: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(152, 154);
                                continue;
                            }
                            continue;
                        }
                        case 163: {
                            if (jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 169: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(168, 170);
                                continue;
                            }
                            continue;
                        }
                        case 274: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3) && kind > 55) {
                                kind = 55;
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
            final int n2 = 347;
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
                return this.jjMoveStringLiteralDfa1_1(1L);
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
                if ((active1 & 0x1L) != 0x0L) {
                    return this.jjStopAtPos(1, 64);
                }
                return 2;
            }
            default: {
                return 2;
            }
        }
    }
    
    protected Token jjFillToken() {
        final String im = SACParserCSS1TokenManager.jjstrLiteralImages[this.jjmatchedKind];
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
                return (SACParserCSS1TokenManager.jjbitVec0[i2] & l2) != 0x0L;
            }
            default: {
                return false;
            }
        }
    }
    
    private static final boolean jjCanMove_1(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (SACParserCSS1TokenManager.jjbitVec3[i2] & l2) != 0x0L;
            }
            default: {
                return (SACParserCSS1TokenManager.jjbitVec1[i1] & l1) != 0x0L;
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
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 66) {
                            this.jjmatchedKind = 66;
                            break;
                        }
                        break;
                    }
                    case 1: {
                        this.jjmatchedKind = Integer.MAX_VALUE;
                        this.jjmatchedPos = 0;
                        curPos = this.jjMoveStringLiteralDfa0_1();
                        if (this.jjmatchedPos == 0 && this.jjmatchedKind > 65) {
                            this.jjmatchedKind = 65;
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
                if ((SACParserCSS1TokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                    final Token matchedToken = this.jjFillToken();
                    this.TokenLexicalActions(matchedToken);
                    if (SACParserCSS1TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSS1TokenManager.jjnewLexState[this.jjmatchedKind];
                    }
                    return matchedToken;
                }
                if ((SACParserCSS1TokenManager.jjtoSkip[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                    this.jjimageLen += this.jjmatchedPos + 1;
                    if (SACParserCSS1TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                        this.curLexState = SACParserCSS1TokenManager.jjnewLexState[this.jjmatchedKind];
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
                if (SACParserCSS1TokenManager.jjnewLexState[this.jjmatchedKind] != -1) {
                    this.curLexState = SACParserCSS1TokenManager.jjnewLexState[this.jjmatchedKind];
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
            case 23: {
                final StringBuilder image6 = this.image;
                final CharStream input_stream6 = this.input_stream;
                final int jjimageLen6 = this.jjimageLen;
                final int lengthOfMatch6 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch6;
                image6.append(input_stream6.GetSuffix(jjimageLen6 + lengthOfMatch6));
                matchedToken.image = ParserUtils.trimBy(this.image, 1, 1);
                break;
            }
            case 25: {
                final StringBuilder image7 = this.image;
                final CharStream input_stream7 = this.input_stream;
                final int jjimageLen7 = this.jjimageLen;
                final int lengthOfMatch7 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch7;
                image7.append(input_stream7.GetSuffix(jjimageLen7 + lengthOfMatch7));
                matchedToken.image = ParserUtils.trimUrl(this.image);
                break;
            }
            case 31: {
                final StringBuilder image8 = this.image;
                final CharStream input_stream8 = this.input_stream;
                final int jjimageLen8 = this.jjimageLen;
                final int lengthOfMatch8 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch8;
                image8.append(input_stream8.GetSuffix(jjimageLen8 + lengthOfMatch8));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 32: {
                final StringBuilder image9 = this.image;
                final CharStream input_stream9 = this.input_stream;
                final int jjimageLen9 = this.jjimageLen;
                final int lengthOfMatch9 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch9;
                image9.append(input_stream9.GetSuffix(jjimageLen9 + lengthOfMatch9));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 33: {
                final StringBuilder image10 = this.image;
                final CharStream input_stream10 = this.input_stream;
                final int jjimageLen10 = this.jjimageLen;
                final int lengthOfMatch10 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch10;
                image10.append(input_stream10.GetSuffix(jjimageLen10 + lengthOfMatch10));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 34: {
                final StringBuilder image11 = this.image;
                final CharStream input_stream11 = this.input_stream;
                final int jjimageLen11 = this.jjimageLen;
                final int lengthOfMatch11 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch11;
                image11.append(input_stream11.GetSuffix(jjimageLen11 + lengthOfMatch11));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 35: {
                final StringBuilder image12 = this.image;
                final CharStream input_stream12 = this.input_stream;
                final int jjimageLen12 = this.jjimageLen;
                final int lengthOfMatch12 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch12;
                image12.append(input_stream12.GetSuffix(jjimageLen12 + lengthOfMatch12));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 36: {
                final StringBuilder image13 = this.image;
                final CharStream input_stream13 = this.input_stream;
                final int jjimageLen13 = this.jjimageLen;
                final int lengthOfMatch13 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch13;
                image13.append(input_stream13.GetSuffix(jjimageLen13 + lengthOfMatch13));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 37: {
                final StringBuilder image14 = this.image;
                final CharStream input_stream14 = this.input_stream;
                final int jjimageLen14 = this.jjimageLen;
                final int lengthOfMatch14 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch14;
                image14.append(input_stream14.GetSuffix(jjimageLen14 + lengthOfMatch14));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 38: {
                final StringBuilder image15 = this.image;
                final CharStream input_stream15 = this.input_stream;
                final int jjimageLen15 = this.jjimageLen;
                final int lengthOfMatch15 = this.jjmatchedPos + 1;
                this.lengthOfMatch = lengthOfMatch15;
                image15.append(input_stream15.GetSuffix(jjimageLen15 + lengthOfMatch15));
                matchedToken.image = ParserUtils.trimBy(this.image, 0, 2);
                break;
            }
            case 39: {
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
            this.jjstateSet[this.jjnewStateCnt++] = SACParserCSS1TokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(SACParserCSS1TokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    public SACParserCSS1TokenManager(final CharStream stream) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[347];
        this.jjstateSet = new int[694];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.input_stream = stream;
    }
    
    public SACParserCSS1TokenManager(final CharStream stream, final int lexState) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[347];
        this.jjstateSet = new int[694];
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
        int i = 347;
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
        jjbitVec0 = new long[] { 0L, 0L, -8589934592L, -1L };
        jjbitVec1 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec3 = new long[] { 0L, 0L, -1L, -1L };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, "{", "}", ",", ".", ";", ":", "/", "+", "-", "=", ">", "[", "]", null, ")", null, "<!--", "-->", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };
        jjnextStates = new int[] { 244, 247, 250, 253, 256, 259, 262, 265, 268, 270, 271, 40, 41, 42, 19, 20, 21, 314, 315, 316, 246, 317, 318, 319, 249, 320, 321, 322, 252, 323, 324, 325, 255, 326, 327, 328, 258, 329, 330, 331, 261, 332, 333, 334, 264, 335, 336, 337, 267, 338, 339, 340, 269, 341, 342, 343, 344, 345, 346, 2, 6, 8, 9, 11, 14, 7, 3, 2, 7, 3, 19, 27, 29, 30, 32, 35, 28, 20, 21, 19, 28, 20, 21, 40, 48, 50, 51, 53, 56, 49, 41, 42, 40, 49, 41, 42, 73, 77, 79, 80, 82, 85, 78, 74, 73, 78, 74, 91, 92, 93, 95, 98, 78, 73, 74, 78, 73, 74, 105, 109, 111, 112, 114, 117, 110, 106, 105, 110, 106, 123, 141, 162, 125, 126, 183, 123, 124, 125, 126, 123, 129, 131, 132, 134, 137, 125, 126, 130, 123, 125, 126, 130, 142, 143, 144, 142, 150, 152, 153, 155, 158, 151, 143, 144, 142, 151, 143, 144, 163, 164, 165, 163, 171, 173, 174, 176, 179, 172, 164, 165, 163, 172, 164, 165, 123, 141, 162, 124, 125, 126, 183, 187, 188, 232, 189, 203, 204, 206, 209, 190, 187, 213, 225, 192, 193, 194, 196, 199, 187, 214, 221, 187, 215, 219, 187, 217, 218, 216, 222, 224, 216, 226, 228, 231, 236, 239, 241, 242, 216, 276, 278, 279, 281, 284, 277, 289, 291, 292, 294, 297, 290, 302, 303, 304, 306, 309, 110, 105, 106, 110, 105, 106, 107, 273, 274, 275, 288, 301, 72, 89, 185, 186, 4, 5, 22, 24, 25, 26, 43, 45, 46, 47, 127, 128, 145, 147, 148, 149, 166, 168, 169, 170 };
        lexStateNames = new String[] { "DEFAULT", "COMMENT" };
        jjnewLexState = new int[] { -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1 };
        jjtoToken = new long[] { 63107569387831291L, 4L };
        jjtoSkip = new long[] { 0L, 1L };
        jjtoSpecial = new long[] { 0L, 0L };
        jjtoMore = new long[] { 4L, 2L };
    }
}
