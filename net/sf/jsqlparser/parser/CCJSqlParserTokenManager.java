package net.sf.jsqlparser.parser;

import java.io.IOException;
import java.io.PrintStream;

public class CCJSqlParserTokenManager implements CCJSqlParserConstants
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
    static final long[] jjbitVec9;
    static final long[] jjbitVec10;
    static final long[] jjbitVec11;
    static final long[] jjbitVec12;
    static final long[] jjbitVec13;
    static final long[] jjbitVec14;
    static final long[] jjbitVec15;
    static final long[] jjbitVec16;
    static final long[] jjbitVec17;
    static final long[] jjbitVec18;
    static final long[] jjbitVec19;
    static final long[] jjbitVec20;
    static final long[] jjbitVec21;
    static final long[] jjbitVec22;
    static final long[] jjbitVec23;
    static final long[] jjbitVec24;
    static final long[] jjbitVec25;
    static final long[] jjbitVec26;
    static final long[] jjbitVec27;
    static final long[] jjbitVec28;
    static final long[] jjbitVec29;
    static final long[] jjbitVec30;
    static final long[] jjbitVec31;
    static final long[] jjbitVec32;
    static final long[] jjbitVec33;
    static final long[] jjbitVec34;
    static final long[] jjbitVec35;
    static final long[] jjbitVec36;
    static final long[] jjbitVec37;
    static final long[] jjbitVec38;
    static final long[] jjbitVec39;
    static final long[] jjbitVec40;
    static final long[] jjbitVec41;
    static final long[] jjbitVec42;
    static final long[] jjbitVec43;
    static final long[] jjbitVec44;
    static final long[] jjbitVec45;
    static final long[] jjbitVec46;
    static final long[] jjbitVec47;
    static final long[] jjbitVec48;
    static final long[] jjbitVec49;
    static final long[] jjbitVec50;
    static final long[] jjbitVec51;
    static final long[] jjbitVec52;
    static final long[] jjbitVec53;
    static final long[] jjbitVec54;
    static final long[] jjbitVec55;
    static final long[] jjbitVec56;
    static final long[] jjbitVec57;
    static final long[] jjbitVec58;
    static final long[] jjbitVec59;
    static final long[] jjbitVec60;
    static final long[] jjbitVec61;
    static final long[] jjbitVec62;
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
    protected SimpleCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    private final StringBuilder jjimage;
    private StringBuilder image;
    private int jjimageLen;
    private int lengthOfMatch;
    protected int curChar;
    
    public void CommonTokenAction(final Token t) {
        t.absoluteBegin = this.getCurrentTokenAbsolutePosition();
        t.absoluteEnd = t.absoluteBegin + t.image.length();
    }
    
    public int getCurrentTokenAbsolutePosition() {
        if (this.input_stream instanceof SimpleCharStream) {
            return this.input_stream.getAbsoluteTokenBegin();
        }
        return -1;
    }
    
    public void setDebugStream(final PrintStream ds) {
        this.debugStream = ds;
    }
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0, final long active1, final long active2, final long active3) {
        switch (pos) {
            case 0: {
                if ((active3 & 0x200000L) != 0x0L) {
                    return 35;
                }
                if ((active3 & 0x80L) != 0x0L) {
                    return 19;
                }
                if ((active0 & 0xFE7FF7BEB6F6FFECL) != 0x0L || (active1 & 0xFFBFF7FBF72FFA7FL) != 0x0L || (active2 & 0x6ED2EBBL) != 0x0L) {
                    this.jjmatchedKind = 168;
                    return 27;
                }
                if ((active3 & 0x30018L) != 0x0L) {
                    return 16;
                }
                if ((active2 & 0x80000000000000L) != 0x0L) {
                    return 156;
                }
                if ((active0 & 0x80000001080010L) != 0x0L || (active1 & 0x100L) != 0x0L || (active2 & 0x1108140L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    return 89;
                }
                if ((active0 & 0x4100010000L) != 0x0L || (active1 & 0x80L) != 0x0L || (active2 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    return 137;
                }
                if ((active2 & 0x8100000000000000L) != 0x0L) {
                    return 157;
                }
                if ((active0 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    return 9;
                }
                if ((active2 & 0x8000000000000L) != 0x0L) {
                    return 5;
                }
                if ((active0 & 0x100000048000000L) != 0x0L || (active1 & 0x40080408D00400L) != 0x0L || (active2 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    return 64;
                }
                return -1;
            }
            case 1: {
                if ((active1 & 0x80000000000L) != 0x0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 168;
                        this.jjmatchedPos = 1;
                    }
                    return 63;
                }
                if ((active0 & 0xDBECFFFFFFEF5E00L) != 0x0L || (active1 & 0xF5DFF7FFDFFBFDFFL) != 0x0L || (active2 & 0x7BF2FEFL) != 0x0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 168;
                        this.jjmatchedPos = 1;
                    }
                    return 27;
                }
                if ((active0 & 0x241300000010A1FCL) != 0x0L || (active1 & 0xA20000020040200L) != 0x0L || (active2 & 0x408010L) != 0x0L) {
                    return 27;
                }
                return -1;
            }
            case 2: {
                if ((active1 & 0x80000000000L) != 0x0L) {
                    if (this.jjmatchedPos != 2) {
                        this.jjmatchedKind = 168;
                        this.jjmatchedPos = 2;
                    }
                    return 62;
                }
                if ((active0 & 0xFFFFF5FFFFFA0000L) != 0x0L || (active1 & 0xF5FFE7BEFEF7FFFFL) != 0x0L || (active2 & 0x1FFAFEFL) != 0x0L) {
                    if (this.jjmatchedPos != 2) {
                        this.jjmatchedKind = 168;
                        this.jjmatchedPos = 2;
                    }
                    return 27;
                }
                if ((active0 & 0xA000005FE00L) != 0x0L || (active1 & 0x104101080000L) != 0x0L || (active2 & 0x6000000L) != 0x0L) {
                    return 27;
                }
                return -1;
            }
            case 3: {
                if ((active1 & 0x80000000000L) != 0x0L) {
                    if (this.jjmatchedPos != 3) {
                        this.jjmatchedKind = 168;
                        this.jjmatchedPos = 3;
                    }
                    return 61;
                }
                if ((active0 & 0xFFFFF54008820000L) != 0x0L || (active1 & 0x64C9E796FEFFDBF7L) != 0x0L || (active2 & 0x5F787EFL) != 0x0L) {
                    if (this.jjmatchedPos != 3) {
                        this.jjmatchedKind = 168;
                        this.jjmatchedPos = 3;
                    }
                    return 27;
                }
                if ((active0 & 0xBFF7780000L) != 0x0L || (active1 & 0x9136006800002408L) != 0x0L || (active2 & 0x82800L) != 0x0L) {
                    return 27;
                }
                return -1;
            }
            case 4: {
                if ((active1 & 0x80000000000L) != 0x0L) {
                    if (this.jjmatchedPos != 4) {
                        this.jjmatchedKind = 168;
                        this.jjmatchedPos = 4;
                    }
                    return 60;
                }
                if ((active0 & 0xFF80002000020000L) != 0x0L || (active1 & 0x45C5E7066ABFCBFEL) != 0x0L || (active2 & 0x1F587EEL) != 0x0L) {
                    if (this.jjmatchedPos != 4) {
                        this.jjmatchedKind = 168;
                        this.jjmatchedPos = 4;
                    }
                    return 27;
                }
                if ((active0 & 0x7FF54008800000L) != 0x0L || (active1 & 0x2008009894401001L) != 0x0L || (active2 & 0x4020001L) != 0x0L) {
                    return 27;
                }
                return -1;
            }
            case 5: {
                if ((active1 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 5;
                    return 59;
                }
                if ((active0 & 0xFF80000000000000L) != 0x0L || (active1 & 0x1C0C00600000803L) != 0x0L || (active2 & 0x808420L) != 0x0L) {
                    return 27;
                }
                if ((active0 & 0x2000020000L) != 0x0L || (active1 & 0x640527006ABFC3FCL) != 0x0L || (active2 & 0x17703CEL) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 5;
                    return 27;
                }
                return -1;
            }
            case 6: {
                if ((active1 & 0x6401270040B64388L) != 0x0L || (active2 & 0x7302C8L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 6;
                    return 27;
                }
                if ((active1 & 0x80000000000L) != 0x0L) {
                    return 58;
                }
                if ((active0 & 0x2000020000L) != 0x0L || (active1 & 0x400002A098074L) != 0x0L || (active2 & 0x1040106L) != 0x0L) {
                    return 27;
                }
                return -1;
            }
            case 7: {
                if ((active1 & 0x6400270000B24200L) != 0x0L || (active2 & 0x5302C0L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 7;
                    return 27;
                }
                if ((active1 & 0x1000040040188L) != 0x0L || (active2 & 0x200008L) != 0x0L) {
                    return 27;
                }
                return -1;
            }
            case 8: {
                if ((active1 & 0x2000000000320000L) != 0x0L || (active2 & 0x520280L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 8;
                    return 27;
                }
                if ((active1 & 0x4400270000804200L) != 0x0L || (active2 & 0x10040L) != 0x0L) {
                    return 27;
                }
                return -1;
            }
            case 9: {
                if ((active1 & 0x2000000000020000L) != 0x0L || (active2 & 0x280L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 9;
                    return 27;
                }
                if ((active1 & 0x300000L) != 0x0L || (active2 & 0x520000L) != 0x0L) {
                    return 27;
                }
                return -1;
            }
            case 10: {
                if ((active1 & 0x2000000000020000L) != 0x0L || (active2 & 0x280L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 10;
                    return 27;
                }
                return -1;
            }
            case 11: {
                if ((active2 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 168;
                    this.jjmatchedPos = 11;
                    return 27;
                }
                if ((active1 & 0x2000000000020000L) != 0x0L || (active2 & 0x80L) != 0x0L) {
                    return 27;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_0(final int pos, final long active0, final long active1, final long active2, final long active3) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1, active2, active3), pos + 1);
    }
    
    private int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case 33: {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 3458764515968024576L, 0L);
            }
            case 35: {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 786432L);
            }
            case 37: {
                return this.jjStopAtPos(0, 200);
            }
            case 38: {
                return this.jjStopAtPos(0, 198);
            }
            case 40: {
                return this.jjStopAtPos(0, 176);
            }
            case 41: {
                return this.jjStopAtPos(0, 177);
            }
            case 42: {
                return this.jjStopAtPos(0, 178);
            }
            case 43: {
                return this.jjStopAtPos(0, 182);
            }
            case 44: {
                return this.jjStopAtPos(0, 175);
            }
            case 45: {
                this.jjmatchedKind = 195;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 196624L);
            }
            case 46: {
                return this.jjStartNfaWithStates_0(0, 179, 5);
            }
            case 47: {
                return this.jjStartNfaWithStates_0(0, 199, 19);
            }
            case 58: {
                this.jjmatchedKind = 181;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 16384L);
            }
            case 59: {
                return this.jjStopAtPos(0, 155);
            }
            case 60: {
                this.jjmatchedKind = 184;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, Long.MIN_VALUE, 0L);
            }
            case 61: {
                return this.jjStopAtPos(0, 174);
            }
            case 62: {
                return this.jjStartNfaWithStates_0(0, 183, 156);
            }
            case 63: {
                this.jjmatchedKind = 180;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 3L);
            }
            case 64: {
                this.jjmatchedKind = 207;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 4755801206503243776L, 0L);
            }
            case 91: {
                return this.jjStartNfaWithStates_0(0, 213, 35);
            }
            case 93: {
                return this.jjStopAtPos(0, 214);
            }
            case 94: {
                return this.jjStopAtPos(0, 201);
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa1_0(36356L, 6442450944L, 32L, 0L);
            }
            case 66:
            case 98: {
                return this.jjMoveStringLiteralDfa1_0(140737488355336L, 70368760954944L, 0L, 0L);
            }
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa1_0(72057595245887488L, 18023211930223616L, 4L, 0L);
            }
            case 68:
            case 100: {
                return this.jjMoveStringLiteralDfa1_0(36028797036265488L, 256L, 17858880L, 0L);
            }
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa1_0(576460760893620224L, 1688849860298754L, 8650752L, 0L);
            }
            case 70:
            case 102: {
                return this.jjMoveStringLiteralDfa1_0(2233651429376L, 2256266580197384L, 67108864L, 0L);
            }
            case 71:
            case 103: {
                return this.jjMoveStringLiteralDfa1_0(70368744177664L, 2305843009213693952L, 0L, 0L);
            }
            case 72:
            case 104: {
                return this.jjMoveStringLiteralDfa1_0(1152921504606846976L, 0L, 512L, 0L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa1_0(2306687434153263200L, 144115188076118528L, 1024L, 0L);
            }
            case 74:
            case 106: {
                return this.jjMoveStringLiteralDfa1_0(33554432L, 0L, 0L, 0L);
            }
            case 75:
            case 107: {
                return this.jjMoveStringLiteralDfa1_0(4096L, 1152921504606846976L, 0L, 0L);
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa1_0(1125899978145792L, 137439019008L, 128L, 0L);
            }
            case 77:
            case 109: {
                return this.jjMoveStringLiteralDfa1_0(0L, 8590069760L, 3L, 0L);
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa1_0(2105344L, 4503634523979792L, 4194320L, 0L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa1_0(294985776129638784L, 585467951558172672L, 0L, 0L);
            }
            case 80:
            case 112: {
                return this.jjMoveStringLiteralDfa1_0(4398046642176L, 2199291707396L, 65536L, 0L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa1_0(9007199254740992L, 288424714834018336L, 8L, 0L);
            }
            case 83:
            case 115: {
                return this.jjMoveStringLiteralDfa1_0(144115205255741440L, -4611686017286537216L, 2048L, 0L);
            }
            case 84:
            case 116: {
                return this.jjMoveStringLiteralDfa1_0(279172939776L, 128L, 131072L, 0L);
            }
            case 85:
            case 117: {
                return this.jjMoveStringLiteralDfa1_0(-4611633241869254656L, 36311371507302400L, 33554432L, 0L);
            }
            case 86:
            case 118: {
                return this.jjMoveStringLiteralDfa1_0(18014948265295872L, 33554433L, 2097152L, 0L);
            }
            case 87:
            case 119: {
                return this.jjMoveStringLiteralDfa1_0(1307817541632L, 72057594037927936L, 524288L, 0L);
            }
            case 88:
            case 120: {
                return this.jjMoveStringLiteralDfa1_0(8796093022208L, 0L, 0L, 0L);
            }
            case 90:
            case 122: {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 8192L, 0L);
            }
            case 123: {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 1061888L);
            }
            case 124: {
                this.jjmatchedKind = 197;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 4L);
            }
            case 125: {
                return this.jjStopAtPos(0, 203);
            }
            case 126: {
                this.jjmatchedKind = 186;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 576460752303423488L, 0L);
            }
            default: {
                return this.jjMoveNfa_0(0, 0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_0(final long active0, final long active1, final long active2, final long active3) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0, active1, active2, active3);
            return 1;
        }
        switch (this.curChar) {
            case 35: {
                if ((active3 & 0x10L) != 0x0L) {
                    return this.jjStopAtPos(1, 196);
                }
                break;
            }
            case 38: {
                if ((active3 & 0x2L) != 0x0L) {
                    return this.jjStopAtPos(1, 193);
                }
                break;
            }
            case 42: {
                if ((active2 & 0x800000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 187);
                }
                break;
            }
            case 58: {
                if ((active3 & 0x4000L) != 0x0L) {
                    return this.jjStopAtPos(1, 206);
                }
                break;
            }
            case 61: {
                if ((active2 & 0x80000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 159);
                }
                break;
            }
            case 62: {
                if ((active2 & 0x4000000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 190);
                }
                if ((active3 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 208;
                    this.jjmatchedPos = 1;
                }
                else if ((active3 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 210;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 0L, active3, 655360L);
            }
            case 64: {
                if ((active2 & 0x200000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 185);
                }
                if ((active2 & Long.MIN_VALUE) != 0x0L) {
                    return this.jjStopAtPos(1, 191);
                }
                break;
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa2_0(active0, 1170936179067977728L, active1, 687228535825L, active2, 2752518L, active3, 0L);
            }
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 32L, active3, 0L);
            }
            case 68:
            case 100: {
                if ((active3 & 0x400L) != 0x0L) {
                    return this.jjStopAtPos(1, 202);
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 4294967296L, active2, 0L, active3, 0L);
            }
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa2_0(active0, 180284722650959872L, active1, 6059769220489543776L, active2, 1050889L, active3, 0L);
            }
            case 70:
            case 102: {
                if ((active1 & 0x200000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 121, 27);
                }
                if ((active1 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 123;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 288230376151711744L, active1, 0L, active2, 0L, active3, 1048576L);
            }
            case 71:
            case 103: {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 1024L, active3, 0L);
            }
            case 72:
            case 104: {
                return this.jjMoveStringLiteralDfa2_0(active0, 1105954078720L, active1, 12582912L, active2, 0L, active3, 0L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa2_0(active0, 10138253126533120L, active1, 72128032592105728L, active2, 16777728L, active3, 0L);
            }
            case 75:
            case 107: {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, Long.MIN_VALUE, active2, 0L, active3, 0L);
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa2_0(active0, 8598323712L, active1, 2147483648L, active2, 0L, active3, 0L);
            }
            case 77:
            case 109: {
                return this.jjMoveStringLiteralDfa2_0(active0, 8796093022208L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 78:
            case 110: {
                if ((active0 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 6;
                    this.jjmatchedPos = 1;
                }
                else if ((active0 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 8;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 2306722618517228544L, active1, 45318570762306048L, active2, 8388608L, active3, 0L);
            }
            case 79:
            case 111: {
                if ((active0 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 4;
                    this.jjmatchedPos = 1;
                }
                else if ((active2 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 132;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 2216236752896L, active1, 18036690062409728L, active2, 71344256L, active3, 0L);
            }
            case 80:
            case 112: {
                return this.jjMoveStringLiteralDfa2_0(active0, -4611686017890516992L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 82:
            case 114: {
                if ((active0 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 7;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 76631562828906496L, active1, 2305845208505385092L, active2, 65536L, active3, 0L);
            }
            case 83:
            case 115: {
                if ((active0 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 2;
                    this.jjmatchedPos = 1;
                }
                else if ((active0 & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 5, 27);
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 17592186077184L, active1, 2L, active2, 33554432L, active3, 0L);
            }
            case 84:
            case 116: {
                if ((active3 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 204;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 67108864L, active2, 0L, active3, 8192L);
            }
            case 85:
            case 117: {
                return this.jjMoveStringLiteralDfa2_0(active0, 2251834175520768L, active1, 8830452760584L, active2, 64L, active3, 0L);
            }
            case 86:
            case 118: {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 8192L, active2, 0L, active3, 0L);
            }
            case 88:
            case 120: {
                return this.jjMoveStringLiteralDfa2_0(active0, 576460752303423488L, active1, 1688849860298752L, active2, 262144L, active3, 0L);
            }
            case 89:
            case 121: {
                if ((active0 & 0x8L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 3, 27);
                }
                break;
            }
            case 124: {
                if ((active3 & 0x1L) != 0x0L) {
                    return this.jjStopAtPos(1, 192);
                }
                if ((active3 & 0x4L) != 0x0L) {
                    return this.jjStopAtPos(1, 194);
                }
                break;
            }
            case 126: {
                if ((active2 & 0x1000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 188;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 2305843009213693952L, active3, 0L);
            }
        }
        return this.jjStartNfa_0(0, active0, active1, active2, active3);
    }
    
    private int jjMoveStringLiteralDfa2_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3)) == 0x0L) {
            return this.jjStartNfa_0(0, old0, old1, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0, active1, active2, active3);
            return 2;
        }
        switch (this.curChar) {
            case 42: {
                if ((active2 & 0x2000000000000000L) != 0x0L) {
                    return this.jjStopAtPos(2, 189);
                }
                break;
            }
            case 62: {
                if ((active3 & 0x20000L) != 0x0L) {
                    return this.jjStopAtPos(2, 209);
                }
                if ((active3 & 0x80000L) != 0x0L) {
                    return this.jjStopAtPos(2, 211);
                }
                break;
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 75497472L, active2, 8388608L, active3, 0L);
            }
            case 66:
            case 98: {
                return this.jjMoveStringLiteralDfa3_0(active0, 274877906944L, active1, 1100585369600L, active2, 131072L, active3, 0L);
            }
            case 67:
            case 99: {
                if ((active0 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 15, 27);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 288230376688584706L, active2, 262144L, active3, 0L);
            }
            case 68:
            case 100: {
                if ((active0 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 10, 27);
                }
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 18, 27);
                }
                if ((active1 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 96, 27);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 4616471093031469056L, active1, 8589934592L, active2, 0L, active3, 0L);
            }
            case 69:
            case 101: {
                if ((active2 & 0x2000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 153, 27);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 72059250284691456L, active1, 1154612553494568960L, active2, 65536L, active3, 0L);
            }
            case 70:
            case 102: {
                return this.jjMoveStringLiteralDfa3_0(active0, 288230376218820608L, active1, 2097152L, active2, 1048576L, active3, 0L);
            }
            case 71:
            case 103: {
                return this.jjMoveStringLiteralDfa3_0(active0, 9147936743096320L, active1, 140737488355328L, active2, 512L, active3, 0L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa3_0(active0, 576513528903499776L, active1, -9187343239567376380L, active2, 524288L, active3, 0L);
            }
            case 75:
            case 107: {
                return this.jjMoveStringLiteralDfa3_0(active0, 4194304L, active1, 0L, active2, 0L, active3, 0L);
            }
            case 76:
            case 108: {
                if ((active0 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 9, 27);
                }
                if ((active0 & 0x80000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 43, 27);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 198158417966137344L, active1, 9293123817570313L, active2, 2097408L, active3, 0L);
            }
            case 77:
            case 109: {
                return this.jjMoveStringLiteralDfa3_0(active0, 1125917086711808L, active1, 18014398509481984L, active2, 2048L, active3, 0L);
            }
            case 78:
            case 110: {
                if ((active3 & 0x100000L) != 0x0L) {
                    return this.jjStopAtPos(2, 212);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 562949953421312L, active1, 70918635261952L, active2, 9216L, active3, 0L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa3_0(active0, 70369163608064L, active1, 2305843009213693952L, active2, 0L, active3, 0L);
            }
            case 80:
            case 112: {
                if ((active0 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 16, 27);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 4611686018427387936L, active2, 64L, active3, 0L);
            }
            case 82:
            case 114: {
                if ((active0 & 0x20000000000L) != 0x0L) {
                    this.jjmatchedKind = 41;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 131072L, active1, 8864846594048L, active2, 67108865L, active3, 0L);
            }
            case 83:
            case 115: {
                if ((active3 & 0x2000L) != 0x0L) {
                    return this.jjStopAtPos(2, 205);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, -6917529017976881152L, active1, 137438954752L, active2, 16777228L, active3, 0L);
            }
            case 84:
            case 116: {
                if ((active0 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 13, 27);
                }
                if ((active0 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 14, 27);
                }
                if ((active1 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 88, 27);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 2252005973164032L, active1, 74344580371677776L, active2, 34L, active3, 0L);
            }
            case 85:
            case 117: {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 128L, active2, 32768L, active3, 0L);
            }
            case 86:
            case 118: {
                return this.jjMoveStringLiteralDfa3_0(active0, 1152925902653358080L, active1, 0L, active2, 4194304L, active3, 0L);
            }
            case 87:
            case 119: {
                if ((active1 & 0x100000000000L) != 0x0L) {
                    this.jjmatchedKind = 108;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 274877906944L, active2, 128L, active3, 0L);
            }
            case 88:
            case 120: {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 4503599627370496L, active2, 0L, active3, 0L);
            }
            case 89:
            case 121: {
                if ((active0 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 11, 27);
                }
                if ((active0 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 12, 27);
                }
                break;
            }
        }
        return this.jjStartNfa_0(1, active0, active1, active2, active3);
    }
    
    private int jjMoveStringLiteralDfa3_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3)) == 0x0L) {
            return this.jjStartNfa_0(1, old0, old1, old2, old3);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0, active1, active2, 0L);
            return 3;
        }
        switch (this.curChar) {
            case 95: {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 128L);
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa4_0(active0, 4683743612465315840L, active1, 4611756387171565570L, active2, 20971776L);
            }
            case 66:
            case 98: {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 8421376L);
            }
            case 67:
            case 99: {
                if ((active0 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 19, 27);
                }
                if ((active1 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 113;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 131072L, active1, 3379898747977728L, active2, 67174406L);
            }
            case 69:
            case 101: {
                if ((active0 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 22, 27);
                }
                if ((active0 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 30, 27);
                }
                if ((active0 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 33, 27);
                }
                if ((active0 & 0x400000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 34, 27);
                }
                if ((active2 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 141, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, -6729785218175074304L, active1, 140739638921728L, active2, 1048576L);
            }
            case 71:
            case 103: {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 549755813888L, active2, 1L);
            }
            case 72:
            case 104: {
                if ((active0 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 36;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 9007336693694464L, active1, 72057594037927936L, active2, 512L);
            }
            case 73:
            case 105: {
                if ((active2 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 139, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 1154188142002044928L, active1, 8589934592L, active2, 2097184L);
            }
            case 75:
            case 107: {
                return this.jjMoveStringLiteralDfa4_0(active0, 8388608L, active1, 0L, active2, 0L);
            }
            case 76:
            case 108: {
                if ((active0 & 0x200000L) != 0x0L) {
                    this.jjmatchedKind = 21;
                    this.jjmatchedPos = 3;
                }
                else if ((active0 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 35;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 274877906944L, active1, 4433479991336L, active2, 393280L);
            }
            case 77:
            case 109: {
                if ((active0 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 28, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 18014398509481988L, active2, 0L);
            }
            case 78:
            case 110: {
                if ((active0 & 0x2000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 25, 27);
                }
                if ((active0 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 29, 27);
                }
                if ((active0 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 31, 27);
                }
                if ((active0 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 32, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 17592186044416L, active1, 134217856L, active2, 0L);
            }
            case 79:
            case 111: {
                if ((active0 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 20, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 39582418599936L, active1, 282574756773888L, active2, 1024L);
            }
            case 80:
            case 112: {
                if ((active0 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 24, 27);
                }
                if ((active1 & 0x1000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 124, 27);
                }
                if ((active1 & Long.MIN_VALUE) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 127, 27);
                }
                break;
            }
            case 81:
            case 113: {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 36028797018963968L, active2, 0L);
            }
            case 82:
            case 114: {
                if ((active1 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 77, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 1099511627776L, active1, 8796168552448L, active2, 0L);
            }
            case 83:
            case 115: {
                if ((active1 & 0x4000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 102, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 864691128589352960L, active1, 68720525312L, active2, 0L);
            }
            case 84:
            case 116: {
                if ((active0 & 0x4000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 26, 27);
                }
                if ((active1 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 74, 27);
                }
                if ((active1 & 0x2000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 101, 27);
                }
                if ((active1 & 0x10000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 116, 27);
                }
                if ((active2 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 147, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 16640L, active2, 8L);
            }
            case 85:
            case 117: {
                return this.jjMoveStringLiteralDfa4_0(active0, 18084767253659648L, active1, 2594108586917367825L, active2, 0L);
            }
            case 87:
            case 119: {
                if ((active0 & 0x8000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 39, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 64L, active2, 0L);
            }
            case 89:
            case 121: {
                if ((active1 & 0x20000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 117, 27);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 570425344L, active2, 0L);
            }
        }
        return this.jjStartNfa_0(2, active0, active1, active2, 0L);
    }
    
    private int jjMoveStringLiteralDfa4_0(final long old0, long active0, final long old1, long active1, final long old2, long active2) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0x0L) {
            return this.jjStartNfa_0(2, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0, active1, active2, 0L);
            return 4;
        }
        switch (this.curChar) {
            case 95: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 512L);
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 8421412L, active2, 4L);
            }
            case 66:
            case 98: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 16777216L);
            }
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa5_0(active0, 144115188075855872L, active1, 536871040L, active2, 0L);
            }
            case 68:
            case 100: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 2097152L);
            }
            case 69:
            case 101: {
                if ((active0 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 23, 27);
                }
                if ((active0 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 38;
                    this.jjmatchedPos = 4;
                }
                else {
                    if ((active0 & 0x10000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 40, 27);
                    }
                    if ((active0 & 0x40000000000000L) != 0x0L) {
                        this.jjmatchedKind = 54;
                        this.jjmatchedPos = 4;
                    }
                    else {
                        if ((active1 & 0x8000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 103, 27);
                        }
                        if ((active2 & 0x1L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 128, 27);
                        }
                        if ((active2 & 0x4000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 154, 27);
                        }
                    }
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 288230376151842816L, active1, 10995250495553L, active2, 131072L);
            }
            case 70:
            case 102: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 8589934592L, active2, 0L);
            }
            case 71:
            case 103: {
                if ((active0 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 44, 27);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 281474976710656L, active2, 0L);
            }
            case 72:
            case 104: {
                if ((active1 & 0x8000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 115, 27);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 2L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 90071993655247104L, active2, 65600L);
            }
            case 75:
            case 107: {
                if ((active1 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 86, 27);
                }
                break;
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 12615680L);
            }
            case 77:
            case 109: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 17179869184L, active2, 0L);
            }
            case 78:
            case 110: {
                if ((active0 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 45, 27);
                }
                if ((active0 & 0x800000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 47, 27);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 1152921504606846976L, active1, 0L, active2, 0L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa5_0(active0, 137438953472L, active1, 4398046511104L, active2, 32L);
            }
            case 80:
            case 112: {
                if ((active0 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 46;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 2305843009213696002L, active2, 128L);
            }
            case 82:
            case 114: {
                if ((active0 & 0x2000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 49, 27);
                }
                if ((active0 & 0x8000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 51, 27);
                }
                if ((active0 & 0x10000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 52, 27);
                }
                if ((active1 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 92, 27);
                }
                if ((active1 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 95, 27);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, -6917529027641081856L, active1, 4900021947697922576L, active2, 1049608L);
            }
            case 83:
            case 115: {
                if ((active0 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 27, 27);
                }
                if ((active1 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 76, 27);
                }
                if ((active1 & 0x800000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 99, 27);
                }
                break;
            }
            case 84:
            case 116: {
                if ((active0 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 42, 27);
                }
                if ((active0 & 0x4000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 50, 27);
                }
                if ((active0 & 0x20000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 53, 27);
                }
                if ((active1 & 0x4000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 90, 27);
                }
                if ((active1 & 0x1000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 100, 27);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 5296233161787703296L, active1, 1048584L, active2, 0L);
            }
            case 85:
            case 117: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 37155796437434368L, active2, 262144L);
            }
            case 88:
            case 120: {
                if ((active0 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 48, 27);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 140737488355328L, active2, 0L);
            }
            case 89:
            case 121: {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 256L);
            }
        }
        return this.jjStartNfa_0(3, active0, active1, active2, 0L);
    }
    
    private int jjMoveStringLiteralDfa5_0(final long old0, long active0, final long old1, long active1, final long old2, long active2) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0x0L) {
            return this.jjStartNfa_0(3, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0, active1, active2, 0L);
            return 5;
        }
        switch (this.curChar) {
            case 95: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 2305843009213693952L, active2, 0L);
            }
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 4611686018427453584L, active2, 2097152L);
            }
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 142639136L, active2, 64L);
            }
            case 68:
            case 100: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 2199023255552L, active2, 262148L);
            }
            case 69:
            case 101: {
                if ((active0 & 0x80000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 55, 27);
                }
                if ((active0 & 0x100000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 56, 27);
                }
                if ((active0 & 0x4000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 62, 27);
                }
                if ((active1 & 0x2L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 65, 27);
                }
                if ((active1 & 0x80000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 119, 27);
                }
                if ((active2 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 138, 27);
                }
                if ((active2 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 143, 27);
                }
                if ((active2 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 151, 27);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 2097224L, active2, 258L);
            }
            case 71:
            case 103: {
                if ((active0 & 0x1000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 60, 27);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 281474977234944L, active2, 0L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 131072L, active2, 4194312L);
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 536870912L, active2, 16777216L);
            }
            case 78:
            case 110: {
                if ((active1 & 0x400000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 98, 27);
                }
                if ((active1 & 0x100000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 120, 27);
                }
                if ((active2 & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 133, 27);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 131072L, active1, 45081084035328L, active2, 0L);
            }
            case 80:
            case 112: {
                if ((active1 & 0x800000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 111, 27);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 512L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 1048580L, active2, 1048704L);
            }
            case 83:
            case 115: {
                if ((active0 & 0x800000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 59, 27);
                }
                if ((active1 & 0x1L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 64, 27);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 288230376151712256L, active2, 196608L);
            }
            case 84:
            case 116: {
                if ((active0 & 0x200000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 57, 27);
                }
                if ((active0 & 0x400000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 58, 27);
                }
                if ((active0 & 0x2000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 61, 27);
                }
                if ((active0 & Long.MIN_VALUE) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 63, 27);
                }
                if ((active1 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 75, 27);
                }
                if ((active1 & 0x40000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 118, 27);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 1125899906859008L, active2, 0L);
            }
            case 85:
            case 117: {
                return this.jjMoveStringLiteralDfa6_0(active0, 137438953472L, active1, 0L, active2, 0L);
            }
            case 86:
            case 118: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 262144L, active2, 0L);
            }
            case 87:
            case 119: {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 4398046511104L, active2, 0L);
            }
            case 89:
            case 121: {
                if ((active1 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 97, 27);
                }
                if ((active1 & 0x400000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 110, 27);
                }
                break;
            }
        }
        return this.jjStartNfa_0(4, active0, active1, active2, 0L);
    }
    
    private int jjMoveStringLiteralDfa6_0(final long old0, long active0, final long old1, long active1, final long old2, long active2) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2)) == 0x0L) {
            return this.jjStartNfa_0(4, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0, active1, active2, 0L);
            return 6;
        }
        switch (this.curChar) {
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 1441792L, active2, 1048640L);
            }
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 2305843009213694208L, active2, 8L);
            }
            case 68:
            case 100: {
                if ((active2 & 0x2L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 129, 27);
                }
                if ((active2 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 136, 27);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 1099511627776L, active2, 4194304L);
            }
            case 69:
            case 101: {
                if ((active1 & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 69, 27);
                }
                if ((active1 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 93, 27);
                }
                if ((active1 & 0x4000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 114, 27);
                }
                if ((active2 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 130, 27);
                }
                if ((active2 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 146, 27);
                }
                if ((active2 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 152, 27);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 281474976711168L, active2, 0L);
            }
            case 71:
            case 103: {
                if ((active1 & 0x2000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 89, 27);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 1073741824L, active2, 0L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 288272157593583616L, active2, 65664L);
            }
            case 76:
            case 108: {
                if ((active1 & 0x10L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 68, 27);
                }
                if ((active1 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 80, 27);
                }
                break;
            }
            case 78:
            case 110: {
                if ((active1 & 0x40L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 70, 27);
                }
                if ((active1 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 83, 27);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 2097152L, active2, 0L);
            }
            case 80:
            case 112: {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 131072L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 512L);
            }
            case 84:
            case 116: {
                if ((active0 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 17, 27);
                }
                if ((active0 & 0x2000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 37, 27);
                }
                if ((active1 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 79, 27);
                }
                if ((active1 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 91, 27);
                }
                if ((active1 & 0x80000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 107, 58);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 4611686018435776640L, active2, 2097152L);
            }
            case 88:
            case 120: {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 8L, active2, 0L);
            }
            case 89:
            case 121: {
                if ((active1 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 66, 27);
                }
                break;
            }
        }
        return this.jjStartNfa_0(5, active0, active1, active2, 0L);
    }
    
    private int jjMoveStringLiteralDfa7_0(final long old0, long active0, final long old1, long active1, final long old2, long active2) {
        active0 &= old0;
        if ((active0 | (active1 &= old1) | (active2 &= old2)) == 0x0L) {
            return this.jjStartNfa_0(5, old0, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(6, 0L, active1, active2, 0L);
            return 7;
        }
        switch (this.curChar) {
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa8_0(active1, 0L, active2, 4325376L);
            }
            case 66:
            case 98: {
                return this.jjMoveStringLiteralDfa8_0(active1, 0L, active2, 1048576L);
            }
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa8_0(active1, 2097664L, active2, 0L);
            }
            case 68:
            case 100: {
                if ((active1 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 112, 27);
                }
                break;
            }
            case 69:
            case 101: {
                if ((active1 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 71, 27);
                }
                if ((active2 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 149, 27);
                }
                return this.jjMoveStringLiteralDfa8_0(active1, 1099520016384L, active2, 0L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa8_0(active1, 1048576L, active2, 512L);
            }
            case 76:
            case 108: {
                if ((active1 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 82, 27);
                }
                return this.jjMoveStringLiteralDfa8_0(active1, 131072L, active2, 0L);
            }
            case 78:
            case 110: {
                return this.jjMoveStringLiteralDfa8_0(active1, 41781441855488L, active2, 0L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa8_0(active1, 6917529027641098240L, active2, 65664L);
            }
            case 83:
            case 115: {
                if ((active1 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 94, 27);
                }
                break;
            }
            case 84:
            case 116: {
                if ((active1 & 0x8L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 67, 27);
                }
                if ((active1 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 72, 27);
                }
                if ((active2 & 0x8L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 131, 27);
                }
                return this.jjMoveStringLiteralDfa8_0(active1, 0L, active2, 64L);
            }
            case 86:
            case 118: {
                return this.jjMoveStringLiteralDfa8_0(active1, 288230376151711744L, active2, 0L);
            }
        }
        return this.jjStartNfa_0(6, 0L, active1, active2, 0L);
    }
    
    private int jjMoveStringLiteralDfa8_0(final long old1, long active1, final long old2, long active2) {
        if (((active1 &= old1) | (active2 &= old2)) == 0x0L) {
            return this.jjStartNfa_0(6, 0L, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(7, 0L, active1, active2, 0L);
            return 8;
        }
        switch (this.curChar) {
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa9_0(active1, 0L, active2, 131072L);
            }
            case 68:
            case 100: {
                if ((active1 & 0x10000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 104, 27);
                }
                break;
            }
            case 69:
            case 101: {
                if ((active1 & 0x400000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 122, 27);
                }
                if ((active2 & 0x40L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 134, 27);
                }
                return this.jjMoveStringLiteralDfa9_0(active1, 2097152L, active2, 0L);
            }
            case 71:
            case 103: {
                if ((active1 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 105, 27);
                }
                if ((active1 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 106, 27);
                }
                if ((active1 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 109, 27);
                }
                break;
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa9_0(active1, 131072L, active2, 0L);
            }
            case 76:
            case 108: {
                return this.jjMoveStringLiteralDfa9_0(active1, 0L, active2, 1048576L);
            }
            case 78:
            case 110: {
                if ((active1 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 78, 27);
                }
                if ((active2 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 144, 27);
                }
                return this.jjMoveStringLiteralDfa9_0(active1, 2305843009214742528L, active2, 0L);
            }
            case 79:
            case 111: {
                return this.jjMoveStringLiteralDfa9_0(active1, 0L, active2, 512L);
            }
            case 82:
            case 114: {
                if ((active1 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 87, 27);
                }
                if ((active1 & 0x4000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 126, 27);
                }
                return this.jjMoveStringLiteralDfa9_0(active1, 0L, active2, 128L);
            }
            case 84:
            case 116: {
                if ((active1 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 73, 27);
                }
                return this.jjMoveStringLiteralDfa9_0(active1, 0L, active2, 4194304L);
            }
        }
        return this.jjStartNfa_0(7, 0L, active1, active2, 0L);
    }
    
    private int jjMoveStringLiteralDfa9_0(final long old1, long active1, final long old2, long active2) {
        if (((active1 &= old1) | (active2 &= old2)) == 0x0L) {
            return this.jjStartNfa_0(7, 0L, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(8, 0L, active1, active2, 0L);
            return 9;
        }
        switch (this.curChar) {
            case 67:
            case 99: {
                return this.jjMoveStringLiteralDfa10_0(active1, 2305843009213693952L, active2, 0L);
            }
            case 69:
            case 101: {
                if ((active2 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 145, 27);
                }
                if ((active2 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 148, 27);
                }
                if ((active2 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 150, 27);
                }
                break;
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa10_0(active1, 0L, active2, 128L);
            }
            case 82:
            case 114: {
                return this.jjMoveStringLiteralDfa10_0(active1, 0L, active2, 512L);
            }
            case 83:
            case 115: {
                if ((active1 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 85, 27);
                }
                break;
            }
            case 84:
            case 116: {
                if ((active1 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 84, 27);
                }
                break;
            }
            case 90:
            case 122: {
                return this.jjMoveStringLiteralDfa10_0(active1, 131072L, active2, 0L);
            }
        }
        return this.jjStartNfa_0(8, 0L, active1, active2, 0L);
    }
    
    private int jjMoveStringLiteralDfa10_0(final long old1, long active1, final long old2, long active2) {
        if (((active1 &= old1) | (active2 &= old2)) == 0x0L) {
            return this.jjStartNfa_0(8, 0L, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(9, 0L, active1, active2, 0L);
            return 10;
        }
        switch (this.curChar) {
            case 65:
            case 97: {
                return this.jjMoveStringLiteralDfa11_0(active1, 2305843009213693952L, active2, 0L);
            }
            case 69:
            case 101: {
                return this.jjMoveStringLiteralDfa11_0(active1, 131072L, active2, 0L);
            }
            case 73:
            case 105: {
                return this.jjMoveStringLiteralDfa11_0(active1, 0L, active2, 512L);
            }
            case 84:
            case 116: {
                return this.jjMoveStringLiteralDfa11_0(active1, 0L, active2, 128L);
            }
            default: {
                return this.jjStartNfa_0(9, 0L, active1, active2, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa11_0(final long old1, long active1, final long old2, long active2) {
        if (((active1 &= old1) | (active2 &= old2)) == 0x0L) {
            return this.jjStartNfa_0(9, 0L, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(10, 0L, active1, active2, 0L);
            return 11;
        }
        switch (this.curChar) {
            case 68:
            case 100: {
                if ((active1 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 81, 27);
                }
                break;
            }
            case 84:
            case 116: {
                if ((active1 & 0x2000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 125, 27);
                }
                return this.jjMoveStringLiteralDfa12_0(active1, 0L, active2, 512L);
            }
            case 89:
            case 121: {
                if ((active2 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 135, 27);
                }
                break;
            }
        }
        return this.jjStartNfa_0(10, 0L, active1, active2, 0L);
    }
    
    private int jjMoveStringLiteralDfa12_0(final long old1, long active1, final long old2, long active2) {
        active1 &= old1;
        if ((active1 | (active2 &= old2)) == 0x0L) {
            return this.jjStartNfa_0(10, 0L, old1, old2, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(11, 0L, 0L, active2, 0L);
            return 12;
        }
        switch (this.curChar) {
            case 89:
            case 121: {
                if ((active2 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 137, 27);
                }
                break;
            }
        }
        return this.jjStartNfa_0(11, 0L, 0L, active2, 0L);
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
        this.jjnewStateCnt = 156;
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
                        case 156: {
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(2, 3);
                                continue;
                            }
                            if (this.curChar == 61 && kind > 156) {
                                kind = 156;
                                continue;
                            }
                            continue;
                        }
                        case 157: {
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(121, 122);
                            }
                            else if (this.curChar == 62) {
                                if (kind > 158) {
                                    kind = 158;
                                }
                            }
                            else if (this.curChar == 61 && kind > 157) {
                                kind = 157;
                            }
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(119, 120);
                                continue;
                            }
                            continue;
                        }
                        case 27:
                        case 59: {
                            if ((0x3FF00180FFFC1FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 89: {
                            if ((0x3FF00180FFFC1FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 62: {
                            if ((0x3FF00180FFFC1FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 137: {
                            if ((0x3FF00180FFFC1FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 0: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 162) {
                                    kind = 162;
                                }
                                this.jjCheckNAddStates(0, 6);
                            }
                            else if ((0x100002600L & l) != 0x0L) {
                                if (kind > 1) {
                                    kind = 1;
                                }
                            }
                            else if (this.curChar == 60) {
                                this.jjCheckNAddStates(7, 10);
                            }
                            else if (this.curChar == 39) {
                                this.jjCheckNAddStates(11, 16);
                            }
                            else if (this.curChar == 34) {
                                this.jjCheckNAdd(29);
                            }
                            else if (this.curChar == 36) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            else if (this.curChar == 47) {
                                this.jjstateSet[this.jjnewStateCnt++] = 19;
                            }
                            else if (this.curChar == 45) {
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                            }
                            else if (this.curChar == 46) {
                                this.jjCheckNAdd(5);
                            }
                            else if (this.curChar == 62) {
                                this.jjCheckNAddTwoStates(2, 3);
                            }
                            if (this.curChar == 48) {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
                                continue;
                            }
                            continue;
                        }
                        case 60: {
                            if ((0x3FF00180FFFC1FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 9: {
                            if ((0x3FF00180FFFC1FFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                                continue;
                            }
                            if (this.curChar == 39) {
                                this.jjCheckNAdd(10);
                                continue;
                            }
                            continue;
                        }
                        case 63: {
                            if ((0x3FF00180FFFC1FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 58: {
                            if ((0x3FF00180FFFC1FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 61: {
                            if ((0x3FF00180FFFC1FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 64: {
                            if ((0x3FF00180FFFC1FFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 1: {
                            if (this.curChar == 62) {
                                this.jjCheckNAddTwoStates(2, 3);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(2, 3);
                                continue;
                            }
                            continue;
                        }
                        case 3: {
                            if (this.curChar == 61 && kind > 156) {
                                kind = 156;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar == 46) {
                                this.jjCheckNAdd(5);
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 161) {
                                kind = 161;
                            }
                            this.jjCheckNAddTwoStates(5, 6);
                            continue;
                        }
                        case 7: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(8);
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 161) {
                                kind = 161;
                            }
                            this.jjCheckNAdd(8);
                            continue;
                        }
                        case 10: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(10, 11);
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if (this.curChar == 39 && kind > 164) {
                                kind = 164;
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 164) {
                                kind = 164;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 14;
                            continue;
                        }
                        case 15: {
                            if (this.curChar == 48) {
                                this.jjstateSet[this.jjnewStateCnt++] = 13;
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if (this.curChar != 45) {
                                continue;
                            }
                            if (kind > 166) {
                                kind = 166;
                            }
                            this.jjCheckNAdd(17);
                            continue;
                        }
                        case 17: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 166) {
                                kind = 166;
                            }
                            this.jjCheckNAdd(17);
                            continue;
                        }
                        case 18: {
                            if (this.curChar == 45) {
                                this.jjstateSet[this.jjnewStateCnt++] = 16;
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if (this.curChar == 42) {
                                this.jjCheckNAddTwoStates(20, 21);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(20, 21);
                                continue;
                            }
                            continue;
                        }
                        case 21: {
                            if (this.curChar == 42) {
                                this.jjCheckNAddStates(17, 19);
                                continue;
                            }
                            continue;
                        }
                        case 22: {
                            if ((0xFFFF7BFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(23, 21);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(23, 21);
                                continue;
                            }
                            continue;
                        }
                        case 24: {
                            if (this.curChar == 47 && kind > 167) {
                                kind = 167;
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if (this.curChar == 47) {
                                this.jjstateSet[this.jjnewStateCnt++] = 19;
                                continue;
                            }
                            continue;
                        }
                        case 26: {
                            if (this.curChar != 36) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 28: {
                            if (this.curChar == 34) {
                                this.jjCheckNAdd(29);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if ((0xFFFFFFFBFFFFDBFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(29, 30);
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if (this.curChar == 34 && kind > 172) {
                                kind = 172;
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) != 0x0L) {
                                this.jjAddStates(20, 21);
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if ((0xFC00FFFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(36, 37);
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(36, 37);
                                continue;
                            }
                            continue;
                        }
                        case 38: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(11, 16);
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if ((0x8400000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(22, 24);
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if ((0xFFFFFF7FFFFFDBFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(22, 24);
                                continue;
                            }
                            continue;
                        }
                        case 42: {
                            if (this.curChar == 39 && kind > 171) {
                                kind = 171;
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if (this.curChar == 39) {
                                this.jjCheckNAddStates(25, 27);
                                continue;
                            }
                            continue;
                        }
                        case 44: {
                            if (this.curChar == 39) {
                                this.jjstateSet[this.jjnewStateCnt++] = 43;
                                continue;
                            }
                            continue;
                        }
                        case 45: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(25, 27);
                                continue;
                            }
                            continue;
                        }
                        case 48: {
                            if (this.curChar == 41 && kind > 142) {
                                kind = 142;
                                continue;
                            }
                            continue;
                        }
                        case 49: {
                            if (this.curChar == 40) {
                                this.jjstateSet[this.jjnewStateCnt++] = 48;
                                continue;
                            }
                            continue;
                        }
                        case 91: {
                            if (this.curChar == 40) {
                                this.jjCheckNAdd(92);
                                continue;
                            }
                            continue;
                        }
                        case 92: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(92, 93);
                                continue;
                            }
                            continue;
                        }
                        case 93: {
                            if (this.curChar == 41) {
                                this.jjCheckNAddTwoStates(94, 95);
                                continue;
                            }
                            continue;
                        }
                        case 94: {
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(94, 95);
                                continue;
                            }
                            continue;
                        }
                        case 97: {
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjAddStates(28, 29);
                                continue;
                            }
                            continue;
                        }
                        case 99: {
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjAddStates(30, 31);
                                continue;
                            }
                            continue;
                        }
                        case 115: {
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjCheckNAddStates(32, 35);
                                continue;
                            }
                            continue;
                        }
                        case 118: {
                            if (this.curChar == 60) {
                                this.jjCheckNAddStates(7, 10);
                                continue;
                            }
                            continue;
                        }
                        case 119: {
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(119, 120);
                                continue;
                            }
                            continue;
                        }
                        case 120: {
                            if (this.curChar == 61 && kind > 157) {
                                kind = 157;
                                continue;
                            }
                            continue;
                        }
                        case 121: {
                            if ((0x100002600L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(121, 122);
                                continue;
                            }
                            continue;
                        }
                        case 122: {
                            if (this.curChar == 62 && kind > 158) {
                                kind = 158;
                                continue;
                            }
                            continue;
                        }
                        case 123: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 162) {
                                kind = 162;
                            }
                            this.jjCheckNAddStates(0, 6);
                            continue;
                        }
                        case 124: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(124, 4);
                                continue;
                            }
                            continue;
                        }
                        case 125: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(125, 126);
                                continue;
                            }
                            continue;
                        }
                        case 126: {
                            if (this.curChar != 46) {
                                continue;
                            }
                            if (kind > 161) {
                                kind = 161;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 127;
                            continue;
                        }
                        case 128: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(129);
                                continue;
                            }
                            continue;
                        }
                        case 129: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 161) {
                                kind = 161;
                            }
                            this.jjCheckNAdd(129);
                            continue;
                        }
                        case 130: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(130, 131);
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjCheckNAdd(133);
                                continue;
                            }
                            continue;
                        }
                        case 133: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 161) {
                                kind = 161;
                            }
                            this.jjCheckNAdd(133);
                            continue;
                        }
                        case 134: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 162) {
                                kind = 162;
                            }
                            this.jjCheckNAdd(134);
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
                        case 59: {
                            if ((0x87FFFFFE87FFFFFFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 79;
                            }
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 69;
                            }
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 58;
                                continue;
                            }
                            continue;
                        }
                        case 89: {
                            if ((0x87FFFFFE87FFFFFFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 116;
                            }
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 88;
                                continue;
                            }
                            continue;
                        }
                        case 62: {
                            if ((0x87FFFFFE87FFFFFFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 82;
                            }
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 72;
                            }
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 61;
                                continue;
                            }
                            continue;
                        }
                        case 137: {
                            if ((0x87FFFFFE87FFFFFFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 154;
                            }
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 146;
                            }
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 144;
                            }
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 136;
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            else if (this.curChar == 91) {
                                this.jjstateSet[this.jjnewStateCnt++] = 35;
                            }
                            else if (this.curChar == 96) {
                                this.jjCheckNAdd(32);
                            }
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjAddStates(36, 39);
                                continue;
                            }
                            if ((0x1000000010L & l) != 0x0L) {
                                this.jjAddStates(40, 41);
                                continue;
                            }
                            if ((0x800000008L & l) != 0x0L) {
                                this.jjAddStates(42, 44);
                                continue;
                            }
                            if ((0x100000001000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                                continue;
                            }
                            continue;
                        }
                        case 60: {
                            if ((0x87FFFFFE87FFFFFFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            if ((0x400000004000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 80;
                            }
                            if ((0x400000004000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 70;
                            }
                            if ((0x400000004000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 59;
                                continue;
                            }
                            continue;
                        }
                        case 9:
                        case 27: {
                            if ((0x87FFFFFE87FFFFFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 63: {
                            if ((0x87FFFFFE87FFFFFFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 83;
                            }
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 73;
                            }
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 62;
                                continue;
                            }
                            continue;
                        }
                        case 58: {
                            if ((0x87FFFFFE87FFFFFFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            if (this.curChar == 95) {
                                this.jjstateSet[this.jjnewStateCnt++] = 78;
                            }
                            if (this.curChar == 95) {
                                this.jjstateSet[this.jjnewStateCnt++] = 68;
                            }
                            if (this.curChar == 95) {
                                this.jjstateSet[this.jjnewStateCnt++] = 57;
                                continue;
                            }
                            continue;
                        }
                        case 61: {
                            if ((0x87FFFFFE87FFFFFFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 81;
                            }
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 71;
                            }
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 60;
                                continue;
                            }
                            continue;
                        }
                        case 64: {
                            if ((0x87FFFFFE87FFFFFFL & l) != 0x0L) {
                                if (kind > 168) {
                                    kind = 168;
                                }
                                this.jjCheckNAdd(27);
                            }
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 84;
                            }
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 74;
                            }
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 63;
                                continue;
                            }
                            continue;
                        }
                        case 6: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(45, 46);
                                continue;
                            }
                            continue;
                        }
                        case 10: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjAddStates(47, 48);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if ((0x100000001000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 9;
                                continue;
                            }
                            continue;
                        }
                        case 13: {
                            if ((0x100000001000000L & l) != 0x0L) {
                                this.jjCheckNAdd(14);
                                continue;
                            }
                            continue;
                        }
                        case 14: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 164) {
                                kind = 164;
                            }
                            this.jjCheckNAdd(14);
                            continue;
                        }
                        case 26: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 31: {
                            if (this.curChar == 96) {
                                this.jjCheckNAdd(32);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(32, 33);
                                continue;
                            }
                            continue;
                        }
                        case 33: {
                            if (this.curChar == 96 && kind > 172) {
                                kind = 172;
                                continue;
                            }
                            continue;
                        }
                        case 34: {
                            if (this.curChar == 91) {
                                this.jjstateSet[this.jjnewStateCnt++] = 35;
                                continue;
                            }
                            continue;
                        }
                        case 35:
                        case 36: {
                            if ((0xFFFFFFFFDFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(36, 37);
                                continue;
                            }
                            continue;
                        }
                        case 37: {
                            if (this.curChar == 93 && kind > 172) {
                                kind = 172;
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if (this.curChar == 92) {
                                this.jjstateSet[this.jjnewStateCnt++] = 40;
                                continue;
                            }
                            continue;
                        }
                        case 40: {
                            if ((0x14404410144044L & l) != 0x0L) {
                                this.jjCheckNAddStates(22, 24);
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if ((0xFFFFFFFFEFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddStates(22, 24);
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if ((0x800000008L & l) != 0x0L) {
                                this.jjAddStates(42, 44);
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if ((0x1000000010000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 142) {
                                kind = 142;
                            }
                            this.jjCheckNAdd(49);
                            continue;
                        }
                        case 50: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 47;
                                continue;
                            }
                            continue;
                        }
                        case 51: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 50;
                                continue;
                            }
                            continue;
                        }
                        case 52: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 51;
                                continue;
                            }
                            continue;
                        }
                        case 53: {
                            if ((0x8000000080000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 52;
                                continue;
                            }
                            continue;
                        }
                        case 54: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 53;
                                continue;
                            }
                            continue;
                        }
                        case 55: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 54;
                                continue;
                            }
                            continue;
                        }
                        case 56: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 55;
                                continue;
                            }
                            continue;
                        }
                        case 57: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 56;
                                continue;
                            }
                            continue;
                        }
                        case 65: {
                            if ((0x2000000020L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 142) {
                                kind = 142;
                            }
                            this.jjCheckNAdd(49);
                            continue;
                        }
                        case 66: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjCheckNAdd(65);
                                continue;
                            }
                            continue;
                        }
                        case 67: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 66;
                                continue;
                            }
                            continue;
                        }
                        case 68: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 67;
                                continue;
                            }
                            continue;
                        }
                        case 69: {
                            if (this.curChar == 95) {
                                this.jjstateSet[this.jjnewStateCnt++] = 68;
                                continue;
                            }
                            continue;
                        }
                        case 70: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 69;
                                continue;
                            }
                            continue;
                        }
                        case 71: {
                            if ((0x400000004000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 70;
                                continue;
                            }
                            continue;
                        }
                        case 72: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 71;
                                continue;
                            }
                            continue;
                        }
                        case 73: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 72;
                                continue;
                            }
                            continue;
                        }
                        case 74: {
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 73;
                                continue;
                            }
                            continue;
                        }
                        case 75: {
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 74;
                                continue;
                            }
                            continue;
                        }
                        case 76: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjCheckNAdd(65);
                                continue;
                            }
                            continue;
                        }
                        case 77: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 76;
                                continue;
                            }
                            continue;
                        }
                        case 78: {
                            if ((0x1000000010L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 77;
                                continue;
                            }
                            continue;
                        }
                        case 79: {
                            if (this.curChar == 95) {
                                this.jjstateSet[this.jjnewStateCnt++] = 78;
                                continue;
                            }
                            continue;
                        }
                        case 80: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 79;
                                continue;
                            }
                            continue;
                        }
                        case 81: {
                            if ((0x400000004000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 80;
                                continue;
                            }
                            continue;
                        }
                        case 82: {
                            if ((0x2000000020L & l) != 0x0L) {
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
                            if ((0x4000000040000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 83;
                                continue;
                            }
                            continue;
                        }
                        case 85: {
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 84;
                                continue;
                            }
                            continue;
                        }
                        case 86: {
                            if ((0x1000000010L & l) != 0x0L) {
                                this.jjAddStates(40, 41);
                                continue;
                            }
                            continue;
                        }
                        case 87: {
                            if ((0x2000000020L & l) != 0x0L && kind > 140) {
                                kind = 140;
                                continue;
                            }
                            continue;
                        }
                        case 88: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjCheckNAdd(87);
                                continue;
                            }
                            continue;
                        }
                        case 90: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjCheckNAddStates(51, 53);
                                continue;
                            }
                            continue;
                        }
                        case 95: {
                            if ((0x80000000800000L & l) != 0x0L) {
                                this.jjAddStates(54, 55);
                                continue;
                            }
                            continue;
                        }
                        case 96: {
                            if ((0x10000000100L & l) != 0x0L) {
                                this.jjCheckNAdd(97);
                                continue;
                            }
                            continue;
                        }
                        case 98: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 99;
                                continue;
                            }
                            continue;
                        }
                        case 100: {
                            if ((0x2000000020L & l) != 0x0L && kind > 160) {
                                kind = 160;
                                continue;
                            }
                            continue;
                        }
                        case 101: {
                            if ((0x400000004000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 100;
                                continue;
                            }
                            continue;
                        }
                        case 102: {
                            if ((0x800000008000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 101;
                                continue;
                            }
                            continue;
                        }
                        case 103: {
                            if ((0x400000004000000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 102;
                                continue;
                            }
                            continue;
                        }
                        case 104: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 98;
                                continue;
                            }
                            continue;
                        }
                        case 105: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 104;
                                continue;
                            }
                            continue;
                        }
                        case 106: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 105;
                                continue;
                            }
                            continue;
                        }
                        case 107: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 96;
                                continue;
                            }
                            continue;
                        }
                        case 108: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 107;
                                continue;
                            }
                            continue;
                        }
                        case 109: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjCheckNAdd(97);
                                continue;
                            }
                            continue;
                        }
                        case 110: {
                            if ((0x20000000200000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 109;
                                continue;
                            }
                            continue;
                        }
                        case 111: {
                            if ((0x800000008000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 110;
                                continue;
                            }
                            continue;
                        }
                        case 112: {
                            if ((0x10000000100L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 111;
                                continue;
                            }
                            continue;
                        }
                        case 113: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 112;
                                continue;
                            }
                            continue;
                        }
                        case 114: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 113;
                                continue;
                            }
                            continue;
                        }
                        case 116: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjCheckNAdd(90);
                                continue;
                            }
                            continue;
                        }
                        case 117: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 116;
                                continue;
                            }
                            continue;
                        }
                        case 127: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(56, 57);
                                continue;
                            }
                            continue;
                        }
                        case 131: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjAddStates(58, 59);
                                continue;
                            }
                            continue;
                        }
                        case 135: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjAddStates(36, 39);
                                continue;
                            }
                            continue;
                        }
                        case 136: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjCheckNAdd(87);
                                continue;
                            }
                            continue;
                        }
                        case 138: {
                            if ((0x1000000010000L & l) != 0x0L && kind > 140) {
                                kind = 140;
                                continue;
                            }
                            continue;
                        }
                        case 139: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 138;
                                continue;
                            }
                            continue;
                        }
                        case 140: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 139;
                                continue;
                            }
                            continue;
                        }
                        case 141: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 140;
                                continue;
                            }
                            continue;
                        }
                        case 142: {
                            if ((0x8000000080000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 141;
                                continue;
                            }
                            continue;
                        }
                        case 143: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 142;
                                continue;
                            }
                            continue;
                        }
                        case 144: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 143;
                                continue;
                            }
                            continue;
                        }
                        case 145: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 144;
                                continue;
                            }
                            continue;
                        }
                        case 146: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjCheckNAdd(90);
                                continue;
                            }
                            continue;
                        }
                        case 147: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 146;
                                continue;
                            }
                            continue;
                        }
                        case 148: {
                            if ((0x1000000010000L & l) != 0x0L) {
                                this.jjCheckNAddStates(51, 53);
                                continue;
                            }
                            continue;
                        }
                        case 149: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 148;
                                continue;
                            }
                            continue;
                        }
                        case 150: {
                            if ((0x200000002L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 149;
                                continue;
                            }
                            continue;
                        }
                        case 151: {
                            if ((0x10000000100000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 150;
                                continue;
                            }
                            continue;
                        }
                        case 152: {
                            if ((0x8000000080000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 151;
                                continue;
                            }
                            continue;
                        }
                        case 153: {
                            if ((0x2000000020L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 152;
                                continue;
                            }
                            continue;
                        }
                        case 154: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 153;
                                continue;
                            }
                            continue;
                        }
                        case 155: {
                            if ((0x20000000200L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 154;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 17: {
                            if (kind > 166) {
                                kind = 166;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            continue;
                        }
                        case 20: {
                            this.jjCheckNAddTwoStates(20, 21);
                            continue;
                        }
                        case 22:
                        case 23: {
                            this.jjCheckNAddTwoStates(23, 21);
                            continue;
                        }
                        case 29: {
                            this.jjAddStates(49, 50);
                            continue;
                        }
                        case 45: {
                            this.jjCheckNAddStates(25, 27);
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
                        case 27:
                        case 59: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 89: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 62: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 137: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 0: {
                            if (!jjCanMove_1(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 60: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 9: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 63: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 58: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 61: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 64: {
                            if (!jjCanMove_2(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 168) {
                                kind = 168;
                            }
                            this.jjCheckNAdd(27);
                            continue;
                        }
                        case 17: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 166) {
                                kind = 166;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 17;
                            continue;
                        }
                        case 20: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(20, 21);
                                continue;
                            }
                            continue;
                        }
                        case 22:
                        case 23: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(23, 21);
                                continue;
                            }
                            continue;
                        }
                        case 29: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(49, 50);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(20, 21);
                                continue;
                            }
                            continue;
                        }
                        case 35:
                        case 36: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(36, 37);
                                continue;
                            }
                            continue;
                        }
                        case 41: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(22, 24);
                                continue;
                            }
                            continue;
                        }
                        case 45: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddStates(25, 27);
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
            final int n2 = 156;
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
    
    protected Token jjFillToken() {
        final String im = CCJSqlParserTokenManager.jjstrLiteralImages[this.jjmatchedKind];
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
                return (CCJSqlParserTokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (CCJSqlParserTokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_1(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (CCJSqlParserTokenManager.jjbitVec4[i2] & l2) != 0x0L;
            }
            case 2: {
                return (CCJSqlParserTokenManager.jjbitVec5[i2] & l2) != 0x0L;
            }
            case 3: {
                return (CCJSqlParserTokenManager.jjbitVec6[i2] & l2) != 0x0L;
            }
            case 4: {
                return (CCJSqlParserTokenManager.jjbitVec7[i2] & l2) != 0x0L;
            }
            case 5: {
                return (CCJSqlParserTokenManager.jjbitVec8[i2] & l2) != 0x0L;
            }
            case 6: {
                return (CCJSqlParserTokenManager.jjbitVec9[i2] & l2) != 0x0L;
            }
            case 7: {
                return (CCJSqlParserTokenManager.jjbitVec10[i2] & l2) != 0x0L;
            }
            case 9: {
                return (CCJSqlParserTokenManager.jjbitVec11[i2] & l2) != 0x0L;
            }
            case 10: {
                return (CCJSqlParserTokenManager.jjbitVec12[i2] & l2) != 0x0L;
            }
            case 11: {
                return (CCJSqlParserTokenManager.jjbitVec13[i2] & l2) != 0x0L;
            }
            case 12: {
                return (CCJSqlParserTokenManager.jjbitVec14[i2] & l2) != 0x0L;
            }
            case 13: {
                return (CCJSqlParserTokenManager.jjbitVec15[i2] & l2) != 0x0L;
            }
            case 14: {
                return (CCJSqlParserTokenManager.jjbitVec16[i2] & l2) != 0x0L;
            }
            case 15: {
                return (CCJSqlParserTokenManager.jjbitVec17[i2] & l2) != 0x0L;
            }
            case 16: {
                return (CCJSqlParserTokenManager.jjbitVec18[i2] & l2) != 0x0L;
            }
            case 17: {
                return (CCJSqlParserTokenManager.jjbitVec19[i2] & l2) != 0x0L;
            }
            case 18: {
                return (CCJSqlParserTokenManager.jjbitVec20[i2] & l2) != 0x0L;
            }
            case 19: {
                return (CCJSqlParserTokenManager.jjbitVec21[i2] & l2) != 0x0L;
            }
            case 20: {
                return (CCJSqlParserTokenManager.jjbitVec0[i2] & l2) != 0x0L;
            }
            case 22: {
                return (CCJSqlParserTokenManager.jjbitVec22[i2] & l2) != 0x0L;
            }
            case 23: {
                return (CCJSqlParserTokenManager.jjbitVec23[i2] & l2) != 0x0L;
            }
            case 24: {
                return (CCJSqlParserTokenManager.jjbitVec24[i2] & l2) != 0x0L;
            }
            case 30: {
                return (CCJSqlParserTokenManager.jjbitVec25[i2] & l2) != 0x0L;
            }
            case 31: {
                return (CCJSqlParserTokenManager.jjbitVec26[i2] & l2) != 0x0L;
            }
            case 32: {
                return (CCJSqlParserTokenManager.jjbitVec27[i2] & l2) != 0x0L;
            }
            case 33: {
                return (CCJSqlParserTokenManager.jjbitVec28[i2] & l2) != 0x0L;
            }
            case 45: {
                return (CCJSqlParserTokenManager.jjbitVec29[i2] & l2) != 0x0L;
            }
            case 48: {
                return (CCJSqlParserTokenManager.jjbitVec30[i2] & l2) != 0x0L;
            }
            case 49: {
                return (CCJSqlParserTokenManager.jjbitVec31[i2] & l2) != 0x0L;
            }
            case 77: {
                return (CCJSqlParserTokenManager.jjbitVec32[i2] & l2) != 0x0L;
            }
            case 159: {
                return (CCJSqlParserTokenManager.jjbitVec33[i2] & l2) != 0x0L;
            }
            case 164: {
                return (CCJSqlParserTokenManager.jjbitVec34[i2] & l2) != 0x0L;
            }
            case 215: {
                return (CCJSqlParserTokenManager.jjbitVec35[i2] & l2) != 0x0L;
            }
            case 250: {
                return (CCJSqlParserTokenManager.jjbitVec36[i2] & l2) != 0x0L;
            }
            case 251: {
                return (CCJSqlParserTokenManager.jjbitVec37[i2] & l2) != 0x0L;
            }
            case 253: {
                return (CCJSqlParserTokenManager.jjbitVec38[i2] & l2) != 0x0L;
            }
            case 254: {
                return (CCJSqlParserTokenManager.jjbitVec39[i2] & l2) != 0x0L;
            }
            case 255: {
                return (CCJSqlParserTokenManager.jjbitVec40[i2] & l2) != 0x0L;
            }
            default: {
                return (CCJSqlParserTokenManager.jjbitVec3[i1] & l1) != 0x0L;
            }
        }
    }
    
    private static final boolean jjCanMove_2(final int hiByte, final int i1, final int i2, final long l1, final long l2) {
        switch (hiByte) {
            case 0: {
                return (CCJSqlParserTokenManager.jjbitVec41[i2] & l2) != 0x0L;
            }
            case 2: {
                return (CCJSqlParserTokenManager.jjbitVec5[i2] & l2) != 0x0L;
            }
            case 3: {
                return (CCJSqlParserTokenManager.jjbitVec42[i2] & l2) != 0x0L;
            }
            case 4: {
                return (CCJSqlParserTokenManager.jjbitVec43[i2] & l2) != 0x0L;
            }
            case 5: {
                return (CCJSqlParserTokenManager.jjbitVec44[i2] & l2) != 0x0L;
            }
            case 6: {
                return (CCJSqlParserTokenManager.jjbitVec45[i2] & l2) != 0x0L;
            }
            case 7: {
                return (CCJSqlParserTokenManager.jjbitVec46[i2] & l2) != 0x0L;
            }
            case 9: {
                return (CCJSqlParserTokenManager.jjbitVec47[i2] & l2) != 0x0L;
            }
            case 10: {
                return (CCJSqlParserTokenManager.jjbitVec48[i2] & l2) != 0x0L;
            }
            case 11: {
                return (CCJSqlParserTokenManager.jjbitVec49[i2] & l2) != 0x0L;
            }
            case 12: {
                return (CCJSqlParserTokenManager.jjbitVec50[i2] & l2) != 0x0L;
            }
            case 13: {
                return (CCJSqlParserTokenManager.jjbitVec51[i2] & l2) != 0x0L;
            }
            case 14: {
                return (CCJSqlParserTokenManager.jjbitVec52[i2] & l2) != 0x0L;
            }
            case 15: {
                return (CCJSqlParserTokenManager.jjbitVec53[i2] & l2) != 0x0L;
            }
            case 16: {
                return (CCJSqlParserTokenManager.jjbitVec54[i2] & l2) != 0x0L;
            }
            case 17: {
                return (CCJSqlParserTokenManager.jjbitVec19[i2] & l2) != 0x0L;
            }
            case 18: {
                return (CCJSqlParserTokenManager.jjbitVec20[i2] & l2) != 0x0L;
            }
            case 19: {
                return (CCJSqlParserTokenManager.jjbitVec55[i2] & l2) != 0x0L;
            }
            case 20: {
                return (CCJSqlParserTokenManager.jjbitVec0[i2] & l2) != 0x0L;
            }
            case 22: {
                return (CCJSqlParserTokenManager.jjbitVec22[i2] & l2) != 0x0L;
            }
            case 23: {
                return (CCJSqlParserTokenManager.jjbitVec56[i2] & l2) != 0x0L;
            }
            case 24: {
                return (CCJSqlParserTokenManager.jjbitVec57[i2] & l2) != 0x0L;
            }
            case 30: {
                return (CCJSqlParserTokenManager.jjbitVec25[i2] & l2) != 0x0L;
            }
            case 31: {
                return (CCJSqlParserTokenManager.jjbitVec26[i2] & l2) != 0x0L;
            }
            case 32: {
                return (CCJSqlParserTokenManager.jjbitVec58[i2] & l2) != 0x0L;
            }
            case 33: {
                return (CCJSqlParserTokenManager.jjbitVec28[i2] & l2) != 0x0L;
            }
            case 45: {
                return (CCJSqlParserTokenManager.jjbitVec29[i2] & l2) != 0x0L;
            }
            case 48: {
                return (CCJSqlParserTokenManager.jjbitVec59[i2] & l2) != 0x0L;
            }
            case 49: {
                return (CCJSqlParserTokenManager.jjbitVec31[i2] & l2) != 0x0L;
            }
            case 77: {
                return (CCJSqlParserTokenManager.jjbitVec32[i2] & l2) != 0x0L;
            }
            case 159: {
                return (CCJSqlParserTokenManager.jjbitVec33[i2] & l2) != 0x0L;
            }
            case 164: {
                return (CCJSqlParserTokenManager.jjbitVec34[i2] & l2) != 0x0L;
            }
            case 215: {
                return (CCJSqlParserTokenManager.jjbitVec35[i2] & l2) != 0x0L;
            }
            case 250: {
                return (CCJSqlParserTokenManager.jjbitVec36[i2] & l2) != 0x0L;
            }
            case 251: {
                return (CCJSqlParserTokenManager.jjbitVec60[i2] & l2) != 0x0L;
            }
            case 253: {
                return (CCJSqlParserTokenManager.jjbitVec38[i2] & l2) != 0x0L;
            }
            case 254: {
                return (CCJSqlParserTokenManager.jjbitVec61[i2] & l2) != 0x0L;
            }
            case 255: {
                return (CCJSqlParserTokenManager.jjbitVec62[i2] & l2) != 0x0L;
            }
            default: {
                return (CCJSqlParserTokenManager.jjbitVec3[i1] & l1) != 0x0L;
            }
        }
    }
    
    public Token getNextToken() {
        Token specialToken = null;
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (final Exception e) {
                this.jjmatchedKind = 0;
                this.jjmatchedPos = -1;
                final Token matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                this.CommonTokenAction(matchedToken);
                return matchedToken;
            }
            this.jjmatchedKind = Integer.MAX_VALUE;
            this.jjmatchedPos = 0;
            curPos = this.jjMoveStringLiteralDfa0_0();
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
            if (this.jjmatchedPos + 1 < curPos) {
                this.input_stream.backup(curPos - this.jjmatchedPos - 1);
            }
            if ((CCJSqlParserTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                this.CommonTokenAction(matchedToken);
                return matchedToken;
            }
            if ((CCJSqlParserTokenManager.jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
                continue;
            }
            final Token matchedToken = this.jjFillToken();
            if (specialToken == null) {
                specialToken = matchedToken;
            }
            else {
                matchedToken.specialToken = specialToken;
                final Token token = specialToken;
                final Token next = matchedToken;
                token.next = next;
                specialToken = next;
            }
        }
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
        final int jjmatchedKind = this.jjmatchedKind;
    }
    
    private void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = CCJSqlParserTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(CCJSqlParserTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    public CCJSqlParserTokenManager(final SimpleCharStream stream) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[156];
        this.jjstateSet = new int[312];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.input_stream = stream;
    }
    
    public CCJSqlParserTokenManager(final SimpleCharStream stream, final int lexState) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[156];
        this.jjstateSet = new int[312];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.ReInit(stream);
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
    
    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 156;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final SimpleCharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    static {
        jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
        jjbitVec3 = new long[] { -4503599625273342L, -8193L, -17525614051329L, 1297036692691091455L };
        jjbitVec4 = new long[] { 0L, 0L, 297242231151001600L, -36028797027352577L };
        jjbitVec5 = new long[] { 4503586742468607L, -65536L, -432556670460100609L, 70501888360451L };
        jjbitVec6 = new long[] { 0L, 288230376151711744L, -17179879616L, 4503599577006079L };
        jjbitVec7 = new long[] { -1L, -1L, -4093L, 234187180623206815L };
        jjbitVec8 = new long[] { -562949953421312L, -8547991553L, 255L, 1979120929931264L };
        jjbitVec9 = new long[] { 576460743713488896L, -562949953419265L, -1L, 2017613045381988351L };
        jjbitVec10 = new long[] { 35184371892224L, 0L, 274877906943L, 0L };
        jjbitVec11 = new long[] { 2594073385365405664L, 17163157504L, 271902628478820320L, 4222140488351744L };
        jjbitVec12 = new long[] { 247132830528276448L, 7881300924956672L, 2589004636761075680L, 4295032832L };
        jjbitVec13 = new long[] { 2579997437506199520L, 15837691904L, 270153412153034720L, 0L };
        jjbitVec14 = new long[] { 283724577500946400L, 12884901888L, 283724577500946400L, 13958643712L };
        jjbitVec15 = new long[] { 288228177128316896L, 12884901888L, 3457638613854978016L, 127L };
        jjbitVec16 = new long[] { -9219431387180826626L, 127L, 2309762420256548246L, 805306463L };
        jjbitVec17 = new long[] { 1L, 8796093021951L, 3840L, 0L };
        jjbitVec18 = new long[] { 7679401525247L, 4128768L, -4294967296L, 36028797018898495L };
        jjbitVec19 = new long[] { -1L, -2080374785L, -1065151889409L, 288230376151711743L };
        jjbitVec20 = new long[] { -129L, -3263218305L, 9168625153884503423L, -140737496776899L };
        jjbitVec21 = new long[] { -2160230401L, 134217599L, -4294967296L, 9007199254740991L };
        jjbitVec22 = new long[] { -1L, 35923243902697471L, -4160749570L, 8796093022207L };
        jjbitVec23 = new long[] { 0L, 0L, 4503599627370495L, 134217728L };
        jjbitVec24 = new long[] { -4294967296L, 72057594037927935L, 2199023255551L, 0L };
        jjbitVec25 = new long[] { -1L, -1L, -4026531841L, 288230376151711743L };
        jjbitVec26 = new long[] { -3233808385L, 4611686017001275199L, 6908521828386340863L, 2295745090394464220L };
        jjbitVec27 = new long[] { Long.MIN_VALUE, -9223372036854775807L, 281470681743360L, 0L };
        jjbitVec28 = new long[] { 287031153606524036L, -4294967296L, 15L, 0L };
        jjbitVec29 = new long[] { 274877906943L, 0L, 0L, 0L };
        jjbitVec30 = new long[] { 521858996278132960L, -2L, -6977224705L, Long.MAX_VALUE };
        jjbitVec31 = new long[] { -527765581332512L, -1L, 72057589742993407L, 0L };
        jjbitVec32 = new long[] { -1L, -1L, 18014398509481983L, 0L };
        jjbitVec33 = new long[] { -1L, -1L, 274877906943L, 0L };
        jjbitVec34 = new long[] { -1L, -1L, 8191L, 0L };
        jjbitVec35 = new long[] { -1L, -1L, 68719476735L, 0L };
        jjbitVec36 = new long[] { 70368744177663L, 0L, 0L, 0L };
        jjbitVec37 = new long[] { 6881498030004502655L, -37L, 1125899906842623L, -524288L };
        jjbitVec38 = new long[] { 4611686018427387903L, -65536L, -196609L, 1152640029630136575L };
        jjbitVec39 = new long[] { 6755399441055744L, -11538275021824000L, -1L, 2305843009213693951L };
        jjbitVec40 = new long[] { -8646911293141286896L, -137304735746L, Long.MAX_VALUE, 425688104188L };
        jjbitVec41 = new long[] { 0L, 0L, 297242235445968895L, -36028797027352577L };
        jjbitVec42 = new long[] { -1L, 288230406216515583L, -17179879616L, 4503599577006079L };
        jjbitVec43 = new long[] { -1L, -1L, -3973L, 234187180623206815L };
        jjbitVec44 = new long[] { -562949953421312L, -8547991553L, -4899916411759099649L, 1979120929931286L };
        jjbitVec45 = new long[] { 576460743713488896L, -277081220972545L, -1L, 2305629702346244095L };
        jjbitVec46 = new long[] { -246290604654592L, 2047L, 562949953421311L, 0L };
        jjbitVec47 = new long[] { -864691128455135250L, 281268803551231L, -3186861885341720594L, 4503392135166367L };
        jjbitVec48 = new long[] { -3211631683292264476L, 9006925953907079L, -869759877059465234L, 281204393851839L };
        jjbitVec49 = new long[] { -878767076314341394L, 281215949093263L, -4341532606274353172L, 280925229301191L };
        jjbitVec50 = new long[] { -4327961440926441490L, 281212990012895L, -4327961440926441492L, 281214063754719L };
        jjbitVec51 = new long[] { -4323457841299070996L, 281212992110031L, 3457638613854978028L, 3377704004977791L };
        jjbitVec52 = new long[] { -8646911284551352322L, 67076095L, 4323434403644581270L, 872365919L };
        jjbitVec53 = new long[] { -4422530440275951615L, -554153860399361L, 2305843009196855263L, 64L };
        jjbitVec54 = new long[] { 272457864671395839L, 67044351L, -4294967296L, 36028797018898495L };
        jjbitVec55 = new long[] { -2160230401L, 1123701017804671L, -4294967296L, 9007199254740991L };
        jjbitVec56 = new long[] { 0L, 0L, -1L, 4393886810111L };
        jjbitVec57 = new long[] { -4227893248L, 72057594037927935L, 4398046511103L, 0L };
        jjbitVec58 = new long[] { -9223235697412870144L, -9223094959924576255L, 281470681743360L, 9126739968L };
        jjbitVec59 = new long[] { 522136073208332512L, -2L, -6876561409L, Long.MAX_VALUE };
        jjbitVec60 = new long[] { 6881498031078244479L, -37L, 1125899906842623L, -524288L };
        jjbitVec61 = new long[] { 6755463865565184L, -11538275021824000L, -1L, -6917529027641081857L };
        jjbitVec62 = new long[] { -8646911293074243568L, -137304735746L, Long.MAX_VALUE, 1008806742219095292L };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ";", null, null, null, "!=", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "=", ",", "(", ")", "*", ".", "?", ":", "+", ">", "<", "@@", "~", "~*", "!~", "!~*", "@>", "<@", "?|", "?&", "||", "-", "-#", "|", "&", "/", "%", "^", null, "}", null, null, "::", "@", "->", "->>", "#>", "#>>", null, "[", "]" };
        jjnextStates = new int[] { 124, 4, 125, 126, 130, 131, 134, 119, 120, 121, 122, 39, 41, 42, 44, 45, 42, 21, 22, 24, 32, 33, 39, 41, 42, 44, 45, 42, 97, 106, 99, 103, 91, 94, 95, 115, 137, 145, 147, 155, 89, 117, 64, 75, 85, 7, 8, 10, 11, 29, 30, 91, 95, 115, 108, 114, 128, 129, 132, 133 };
        lexStateNames = new String[] { "DEFAULT" };
        jjnewLexState = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };
        jjtoToken = new long[] { -3L, -1L, -42777874268161L, 8388607L };
        jjtoSkip = new long[] { 2L, 0L, 824633720832L, 0L };
        jjtoSpecial = new long[] { 0L, 0L, 824633720832L, 0L };
        jjtoMore = new long[] { 0L, 0L, 0L, 0L };
    }
}
