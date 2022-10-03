package com.adventnet.swissqlapi.sql.parser;

import java.io.IOException;
import java.io.PrintStream;

public class ALLSQLTokenManager implements ALLSQLConstants
{
    public PrintStream debugStream;
    static final long[] jjbitVec0;
    static final long[] jjbitVec2;
    static final int[] jjnextStates;
    public static final String[] jjstrLiteralImages;
    public static final String[] lexStateNames;
    static final long[] jjtoToken;
    static final long[] jjtoSkip;
    static final long[] jjtoSpecial;
    protected JavaCharStream input_stream;
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
    
    private final int jjStopStringLiteralDfa_0(final int pos, final long active0, final long active1, final long active2, final long active3, final long active4, final long active5, final long active6, final long active7, final long active8, final long active9) {
        switch (pos) {
            case 0: {
                if ((active8 & 0x1000000000000L) != 0x0L) {
                    return 88;
                }
                if ((active8 & 0x400000000000000L) != 0x0L) {
                    return 286;
                }
                if ((active8 & 0x800000000000L) != 0x0L) {
                    return 0;
                }
                if ((active0 & 0x7E78FFEFFFDFFE00L) != 0x0L || (active1 & 0xFFFFFBFFFBF7FFFFL) != 0x0L || (active2 & 0xFFFBFFFFDFFFED5FL) != 0x0L || (active3 & 0xFBFEFFFF87FFFFFFL) != 0x0L || (active4 & 0xFFFDFFFFFFFFFFFFL) != 0x0L || (active5 & 0xFFAFBFBEFFFFFFFDL) != 0x0L || (active6 & 0xFE57D77F7EF7F3EFL) != 0x0L || (active7 & 0xFFDFFFD8F7FFC7FFL) != 0x0L || (active8 & 0x3F7FEFL) != 0x0L) {
                    this.jjmatchedKind = 535;
                    return 287;
                }
                if ((active0 & 0x100000000000000L) != 0x0L || (active8 & 0x40000000000000L) != 0x0L) {
                    return 288;
                }
                if ((active0 & 0x8007001000000000L) != 0x0L || (active1 & 0x40004080000L) != 0x0L || (active2 & 0x40000200012A0L) != 0x0L || (active3 & 0x401000078000000L) != 0x0L || (active4 & 0x2000000000000L) != 0x0L || (active5 & 0x50404100000002L) != 0x0L || (active6 & 0x1A8288081080C10L) != 0x0L || (active7 & 0x20002708003800L) != 0x0L || (active8 & 0x8010L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    return 48;
                }
                if ((active8 & 0x40000000000L) != 0x0L) {
                    return 289;
                }
                if ((active8 & 0x200000000000L) != 0x0L) {
                    return 14;
                }
                if ((active8 & 0x400000L) != 0x0L) {
                    return 5;
                }
                return -1;
            }
            case 1: {
                if ((active0 & 0x3A07A00000C000L) != 0x0L || (active1 & 0x100060022000000L) != 0x0L || (active2 & 0x50000010002A0L) != 0x0L || (active3 & 0x1308078800000L) != 0x0L || (active4 & 0x8000000000000L) != 0x0L || (active5 & 0x28D040402808402AL) != 0x0L || (active6 & 0x81AA2A8000882C50L) != 0x0L || (active7 & 0xC0000000000000F8L) != 0x0L || (active8 & 0x140L) != 0x0L) {
                    return 287;
                }
                if ((active0 & 0xFE45F85FFFDF3E00L) != 0x0L || (active1 & 0xFEFFF9FFDDFFFFFFL) != 0x0L || (active2 & 0xFFFAFFFFFEFFFD5FL) != 0x0L || (active3 & 0xFFFECF7F877FFFFFL) != 0x0L || (active4 & 0xFFF7FFFFFFFFFFFFL) != 0x0L || (active5 & 0xD72FBFBFD7F7BFD5L) != 0x0L || (active6 & 0x7E55D57FFF77D3AFL) != 0x0L || (active7 & 0x3FFFFFFFFFFFFF07L) != 0x0L || (active8 & 0x3FFEBFL) != 0x0L) {
                    if (this.jjmatchedPos != 1) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 1;
                    }
                    return 287;
                }
                return -1;
            }
            case 2: {
                if ((active0 & 0xF665F9597FDF2800L) != 0x0L || (active1 & 0xFE1BF87FFDFFF7DFL) != 0x0L || (active2 & 0xFECFF9FFFCFFFFBFL) != 0x0L || (active3 & 0x7FEEFF6FFEFFFEFFL) != 0x0L || (active4 & 0xFE6BFF7BFFFDFFFEL) != 0x0L || (active5 & 0xF7DE7F3FF3DEBDD7L) != 0x0L || (active6 & 0x7DFFFFBFFFFFFFEFL) != 0x0L || (active7 & 0x3FF5FFFFFFFF8707L) != 0x0L || (active8 & 0x1FFF3FL) != 0x0L) {
                    if (this.jjmatchedPos != 2) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 2;
                    }
                    return 287;
                }
                if ((active0 & 0x802040680009600L) != 0x0L || (active1 & 0xE4058002000820L) != 0x0L || (active2 & 0x130060002000040L) != 0x0L || (active3 & 0x8011001001000100L) != 0x0L || (active4 & 0x194008400020001L) != 0x0L || (active5 & 0x821808004214228L) != 0x0L || (active6 & 0x200004000000000L) != 0x0L || (active7 & 0xC00A0000000078F8L) != 0x0L || (active8 & 0x2000C0L) != 0x0L) {
                    return 287;
                }
                return -1;
            }
            case 3: {
                if ((active0 & 0x366181536BDD2800L) != 0x0L || (active1 & 0xBEFC787EEC1D560DL) != 0x0L || (active2 & 0xFAEF6FFFEC1FFFFBL) != 0x0L || (active3 & 0xB3FFF77E5B9DBFF7L) != 0x0L || (active4 & 0xFD32177FFFFFD3FDL) != 0x0L || (active5 & 0x9FFFFEBFB38AF077L) != 0x0L || (active6 & 0x4FFFFBEBFEBCFFEDL) != 0x0L || (active7 & 0xFBA59D776FECEF0DL) != 0x0L || (active8 & 0x59867L) != 0x0L) {
                    if (this.jjmatchedPos != 3) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 3;
                    }
                    return 287;
                }
                if ((active0 & 0xC0047C0814020000L) != 0x0L || (active1 & 0x4003810111E2A1D2L) != 0x0L || (active2 & 0x400900010E00004L) != 0x0L || (active3 & 0x4C000801A4624008L) != 0x0L || (active4 & 0x24DE80000002C02L) != 0x0L || (active5 & 0x6000010040540F80L) != 0x0L || (active6 & 0x3000041401430002L) != 0x0L || (active7 & 0x450628890130002L) != 0x0L || (active8 & 0x1A6718L) != 0x0L) {
                    return 287;
                }
                if ((active7 & 0xE0L) != 0x0L) {
                    return 9;
                }
                return -1;
            }
            case 4: {
                if ((active0 & 0x2020901000082000L) != 0x0L || (active1 & 0x8000000004041200L) != 0x0L || (active2 & 0x80E000001C200001L) != 0x0L || (active3 & 0x4448062008000A0L) != 0x0L || (active4 & 0xA0020000100100A0L) != 0x0L || (active5 & 0xD8A0082038AA044L) != 0x0L || (active6 & 0x40002080308000L) != 0x0L || (active7 & 0x24042100088700L) != 0x0L || (active8 & 0x1D02L) != 0x0L) {
                    return 287;
                }
                if ((active0 & 0x564101436BD50800L) != 0x0L || (active1 & 0x7EFF787EF999441DL) != 0x0L || (active2 & 0x7A0FFFFFE05FFFFAL) != 0x0L || (active3 & 0xB3BB7F1CDB7DBF57L) != 0x0L || (active4 & 0x5D31177FEFFEF35DL) != 0x0L || (active5 & 0x9275FE3DF00050B3L) != 0x0L || (active6 & 0x5FBFFBDB7E8C7FEFL) != 0x0L || (active7 & 0xFBC1B9D6EFE5680DL) != 0x0L || (active8 & 0x1FE06DL) != 0x0L) {
                    if (this.jjmatchedPos != 4) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 4;
                    }
                    return 287;
                }
                if ((active0 & 0x600000000000L) != 0x0L) {
                    return 9;
                }
                return -1;
            }
            case 5: {
                if ((active0 & 0x5241800309850800L) != 0x0L || (active1 & 0x4CEF78169989401DL) != 0x0L || (active2 & 0x7A0FF7FA006FF3F9L) != 0x0L || (active3 & 0x31823B14DAE9A527L) != 0x0L || (active4 & 0x5D231737FFBCF35DL) != 0x0L || (active5 & 0x9375FE2CE0004083L) != 0x0L || (active6 & 0x1DBF73DB7E8C77AEL) != 0x0L || (active7 & 0xDBC139C6EF45600DL) != 0x0L || (active8 & 0x1DC06DL) != 0x0L) {
                    if (this.jjmatchedPos != 5) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 5;
                    }
                    return 287;
                }
                if ((active0 & 0x400014062500000L) != 0x0L || (active1 & 0x3210006860101400L) != 0x0L || (active2 & 0x805E0100C02L) != 0x0L || (active3 & 0x8239440801141A50L) != 0x0L || (active4 & 0x10004800420000L) != 0x0L || (active5 & 0x1110001030L) != 0x0L || (active6 & 0x4200880080000841L) != 0x0L || (active7 & 0x2000801000A00800L) != 0x0L || (active8 & 0x22800L) != 0x0L) {
                    return 287;
                }
                if ((active7 & 0x600L) != 0x0L) {
                    return 9;
                }
                return -1;
            }
            case 6: {
                if ((active0 & 0x201800201050800L) != 0x0L || (active1 & 0x4000001610010004L) != 0x0L || (active2 & 0x8020E00003051L) != 0x0L || (active3 & 0x1080010008413404L) != 0x0L || (active4 & 0x5920141000143049L) != 0x0L || (active5 & 0x1230B62400000000L) != 0x0L || (active6 & 0x1800209008010A0L) != 0x0L || (active7 & 0x800004064016008L) != 0x0L || (active8 & 0x8020L) != 0x0L) {
                    return 287;
                }
                if ((active0 & 0x5040000108800000L) != 0x0L || (active1 & 0xEFF780089884019L) != 0x0L || (active2 & 0x7A07F5F0007FC3A8L) != 0x0L || (active3 & 0x210A7A14D2A88123L) != 0x0L || (active4 & 0x4030367FFA8C314L) != 0x0L || (active5 & 0x81454808E0004083L) != 0x0L || (active6 & 0x1C3F71D27E0C670FL) != 0x0L || (active7 & 0xD3C139868B440005L) != 0x0L || (active8 & 0x1D404DL) != 0x0L) {
                    if (this.jjmatchedPos != 6) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 6;
                    }
                    return 287;
                }
                return -1;
            }
            case 7: {
                if ((active0 & 0x1000000200800000L) != 0x0L || (active1 & 0xEFA500089884008L) != 0x0L || (active2 & 0x780765F0005F03A0L) != 0x0L || (active3 & 0x10A601452808023L) != 0x0L || (active4 & 0x101452FA08100L) != 0x0L || (active5 & 0x8145600060004082L) != 0x0L || (active6 & 0x1C2A23D21E08050FL) != 0x0L || (active7 & 0x1081110081440005L) != 0x0L || (active8 & 0x44001L) != 0x0L) {
                    if (this.jjmatchedPos != 7) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 7;
                    }
                    return 287;
                }
                if ((active0 & 0x4040000108000000L) != 0x0L || (active1 & 0x5280000000011L) != 0x0L || (active2 & 0x20090000020C008L) != 0x0L || (active3 & 0x20001A0080280100L) != 0x0L || (active4 & 0xC020222D0084214L) != 0x0L || (active5 & 0x80880000001L) != 0x0L || (active6 & 0x15500060046200L) != 0x0L || (active7 & 0xC34028860A000000L) != 0x0L || (active8 & 0x19004CL) != 0x0L) {
                    return 287;
                }
                if ((active7 & 0x40000000L) != 0x0L) {
                    return 9;
                }
                return -1;
            }
            case 8: {
                if ((active0 & 0x1000000200800000L) != 0x0L || (active1 & 0xCF8780089804008L) != 0x0L || (active2 & 0x241C0005C0300L) != 0x0L || (active3 & 0x100601052008022L) != 0x0L || (active4 & 0x642FA08000L) != 0x0L || (active5 & 0x105400060000002L) != 0x0L || (active6 & 0x1C2821D21608050FL) != 0x0L || (active7 & 0x1080110000040001L) != 0x0L || (active8 & 0x140001L) != 0x0L) {
                    if (this.jjmatchedPos != 8) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 8;
                    }
                    return 287;
                }
                if ((active1 & 0x202000000080000L) != 0x0L || (active2 & 0x78052430000300A0L) != 0x0L || (active3 & 0xA000400800001L) != 0x0L || (active4 & 0x1010100000100L) != 0x0L || (active5 & 0x8040200000004080L) != 0x0L || (active6 & 0x2020028000000L) != 0x0L || (active7 & 0x1000081400004L) != 0x0L || (active8 & 0x4000L) != 0x0L) {
                    return 287;
                }
                if ((active7 & 0x400000000L) != 0x0L || (active8 & 0x80000L) != 0x0L) {
                    return 9;
                }
                return -1;
            }
            case 9: {
                if ((active0 & 0x800000L) != 0x0L || (active1 & 0x89000008L) != 0x0L || (active2 & 0x2140005C0000L) != 0x0L || (active3 & 0x601042008022L) != 0x0L || (active4 & 0x2400008000L) != 0x0L || (active5 & 0x105000000000002L) != 0x0L || (active6 & 0x828210006080504L) != 0x0L || (active7 & 0x1080100000040001L) != 0x0L) {
                    return 287;
                }
                if ((active0 & 0x1000000200000000L) != 0x0L || (active1 & 0xCF8780000804000L) != 0x0L || (active2 & 0x2408000000300L) != 0x0L || (active3 & 0x100000410000000L) != 0x0L || (active4 & 0x402FA00000L) != 0x0L || (active5 & 0x400060000000L) != 0x0L || (active6 & 0x140000D21000000BL) != 0x0L || (active7 & 0x10000000000L) != 0x0L || (active8 & 0x144001L) != 0x0L) {
                    if (this.jjmatchedPos != 9) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 9;
                    }
                    return 287;
                }
                return -1;
            }
            case 10: {
                if ((active0 & 0x1000000200000000L) != 0x0L || (active1 & 0x8F0780000004000L) != 0x0L || (active2 & 0x2408000000200L) != 0x0L || (active3 & 0x100000410000000L) != 0x0L || (active4 & 0x402FA00000L) != 0x0L || (active5 & 0x400060000000L) != 0x0L || (active6 & 0x140000D00000000FL) != 0x0L || (active7 & 0x10000000000L) != 0x0L || (active8 & 0x100001L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 10;
                    return 287;
                }
                if ((active1 & 0x408000000800000L) != 0x0L || (active2 & 0x100L) != 0x0L || (active6 & 0x210000000L) != 0x0L || (active7 & 0x1L) != 0x0L || (active8 & 0x44000L) != 0x0L) {
                    return 287;
                }
                return -1;
            }
            case 11: {
                if ((active1 & 0x800600000004000L) != 0x0L || (active2 & 0x2400000000000L) != 0x0L || (active3 & 0x100000410000000L) != 0x0L || (active4 & 0x800000L) != 0x0L || (active5 & 0x400020000000L) != 0x0L || (active6 & 0x1000000000L) != 0x0L || (active8 & 0x1L) != 0x0L) {
                    return 287;
                }
                if ((active0 & 0x1000000200000000L) != 0x0L || (active1 & 0xF0180000000000L) != 0x0L || (active2 & 0x8000000200L) != 0x0L || (active4 & 0x402F200000L) != 0x0L || (active5 & 0x40000000L) != 0x0L || (active6 & 0x140000C00000000FL) != 0x0L || (active7 & 0x10000000000L) != 0x0L || (active8 & 0x100000L) != 0x0L) {
                    if (this.jjmatchedPos != 11) {
                        this.jjmatchedKind = 535;
                        this.jjmatchedPos = 11;
                    }
                    return 287;
                }
                return -1;
            }
            case 12: {
                if ((active0 & 0x1000000200000000L) != 0x0L || (active1 & 0xD0180000000000L) != 0x0L || (active4 & 0x402B000000L) != 0x0L || (active5 & 0x40000000L) != 0x0L || (active6 & 0xC00000000DL) != 0x0L || (active8 & 0x100000L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 12;
                    return 287;
                }
                if ((active1 & 0x20000000000000L) != 0x0L || (active2 & 0x408000000200L) != 0x0L || (active4 & 0x4200000L) != 0x0L || (active6 & 0x1400000000000002L) != 0x0L || (active7 & 0x10000000000L) != 0x0L) {
                    return 287;
                }
                return -1;
            }
            case 13: {
                if ((active1 & 0x10100000000000L) != 0x0L || (active4 & 0x9000000L) != 0x0L || (active5 & 0x40000000L) != 0x0L || (active6 & 0x9L) != 0x0L || (active8 & 0x100000L) != 0x0L) {
                    return 287;
                }
                if ((active0 & 0x1000000200000000L) != 0x0L || (active1 & 0xC0080000000000L) != 0x0L || (active4 & 0x4022000000L) != 0x0L || (active6 & 0xC000000004L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 13;
                    return 287;
                }
                return -1;
            }
            case 14: {
                if ((active1 & 0x40000000000000L) != 0x0L || (active4 & 0x20000000L) != 0x0L || (active6 & 0x4000000000L) != 0x0L) {
                    return 287;
                }
                if ((active0 & 0x1000000200000000L) != 0x0L || (active1 & 0x80080000000000L) != 0x0L || (active4 & 0x4002000000L) != 0x0L || (active6 & 0x8000000004L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 14;
                    return 287;
                }
                return -1;
            }
            case 15: {
                if ((active0 & 0x1000000200000000L) != 0x0L || (active1 & 0x80000000000L) != 0x0L || (active6 & 0x8000000004L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 15;
                    return 287;
                }
                if ((active1 & 0x80000000000000L) != 0x0L || (active4 & 0x4002000000L) != 0x0L) {
                    return 287;
                }
                return -1;
            }
            case 16: {
                if ((active1 & 0x80000000000L) != 0x0L || (active6 & 0x8000000000L) != 0x0L) {
                    return 287;
                }
                if ((active0 & 0x1000000200000000L) != 0x0L || (active6 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 16;
                    return 287;
                }
                return -1;
            }
            case 17: {
                if ((active0 & 0x1000000000000000L) != 0x0L || (active6 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 17;
                    return 287;
                }
                if ((active0 & 0x200000000L) != 0x0L) {
                    return 287;
                }
                return -1;
            }
            case 18: {
                if ((active0 & 0x1000000000000000L) != 0x0L) {
                    return 287;
                }
                if ((active6 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 18;
                    return 287;
                }
                return -1;
            }
            case 19: {
                if ((active6 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 19;
                    return 287;
                }
                return -1;
            }
            case 20: {
                if ((active6 & 0x4L) != 0x0L) {
                    this.jjmatchedKind = 535;
                    this.jjmatchedPos = 20;
                    return 287;
                }
                return -1;
            }
            default: {
                return -1;
            }
        }
    }
    
    private final int jjStartNfa_0(final int pos, final long active0, final long active1, final long active2, final long active3, final long active4, final long active5, final long active6, final long active7, final long active8, final long active9) {
        return this.jjMoveNfa_0(this.jjStopStringLiteralDfa_0(pos, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9), pos + 1);
    }
    
    private int jjStopAtPos(final int pos, final int kind) {
        this.jjmatchedKind = kind;
        return (this.jjmatchedPos = pos) + 1;
    }
    
    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case '!': {
                this.jjmatchedKind = 574;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 6L);
            }
            case '%': {
                return this.jjStopAtPos(0, 562);
            }
            case '&': {
                return this.jjStartNfaWithStates_0(0, 534, 5);
            }
            case '(': {
                return this.jjStopAtPos(0, 555);
            }
            case ')': {
                return this.jjStopAtPos(0, 556);
            }
            case '*': {
                this.jjmatchedKind = 558;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 2251799813685248L, 0L);
            }
            case '+': {
                return this.jjStopAtPos(0, 55);
            }
            case ',': {
                return this.jjStopAtPos(0, 21);
            }
            case '-': {
                return this.jjStartNfaWithStates_0(0, 559, 0);
            }
            case '.': {
                return this.jjStartNfaWithStates_0(0, 554, 289);
            }
            case '/': {
                return this.jjStartNfaWithStates_0(0, 560, 88);
            }
            case ':': {
                return this.jjMoveStringLiteralDfa1_0(72057594037927936L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 18014398509481984L, 0L);
            }
            case '<': {
                return this.jjStopAtPos(0, 572);
            }
            case '=': {
                return this.jjStopAtPos(0, 565);
            }
            case '>': {
                return this.jjStopAtPos(0, 573);
            }
            case '@': {
                return this.jjStartNfaWithStates_0(0, 557, 14);
            }
            case '[': {
                return this.jjStartNfaWithStates_0(0, 570, 286);
            }
            case ']': {
                return this.jjStopAtPos(0, 571);
            }
            case '^': {
                this.jjmatchedKind = 564;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, Long.MIN_VALUE, 0L);
            }
            case 'A':
            case 'a': {
                return this.jjMoveStringLiteralDfa1_0(65024L, 136233888745783296L, -9223372036837997568L, 416L, 144115188210073600L, 576460822197305344L, 137438953472L, 0L, 0L, 0L);
            }
            case 'B':
            case 'b': {
                return this.jjMoveStringLiteralDfa1_0(137439019008L, 4294967296L, 16L, -4611545280939032064L, 128L, 2305843009213693952L, 8590983168L, 2307674795594153984L, 256L, 0L);
            }
            case 'C':
            case 'c': {
                return this.jjMoveStringLiteralDfa1_0(14548992L, 4755801489971085312L, 17314086912L, 36028797018979328L, 1152939234500542976L, 2287121893294080L, 2323857407723176608L, 36775657473L, 36L, 0L);
            }
            case 'D':
            case 'd': {
                return this.jjMoveStringLiteralDfa1_0(4611686018679046144L, 1153767578804420608L, 4391170L, 8796093267969L, 143005769187328L, 0L, 1099511627778L, 618477404164L, 3670144L, 0L);
            }
            case 'E':
            case 'e': {
                return this.jjMoveStringLiteralDfa1_0(8321499136L, 2305913378226307072L, 34359738376L, 70368746012672L, 864691128455135232L, 1040L, 576531121047601152L, 288230376151711744L, 0L, 0L);
            }
            case 'F':
            case 'f': {
                return this.jjMoveStringLiteralDfa1_0(60129542144L, 2200096997385L, 4611686155866341376L, 2097152L, -9223372036854775806L, 1267187151282176L, 144115189686468608L, 34560L, 0L, 0L);
            }
            case 'G':
            case 'g': {
                return this.jjMoveStringLiteralDfa1_0(68719476736L, 67633152L, 0L, 0L, 562949953421312L, 4294967296L, 2147483664L, 0L, 32784L, 0L);
            }
            case 'H':
            case 'h': {
                return this.jjMoveStringLiteralDfa1_0(274877906944L, 0L, 0L, 4194304L, 2097152L, 1099511627777L, 4194304L, 0L, 0L, 0L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa1_0(8246337208320L, 4538784536330240L, 281474976710656L, 52776566521858L, 4194560L, 36037593112526848L, -9222806887869702143L, -4611681620380876552L, 64L, 0L);
            }
            case 'J':
            case 'j': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 1048576L, 0L, 0L, 0L, 0L);
            }
            case 'K':
            case 'k': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 2097152L, 17179869184L, 0L, 0L, 0L);
            }
            case 'L':
            case 'l': {
                return this.jjMoveStringLiteralDfa1_0(131941395333120L, 140737488355552L, 10771005504L, 1729382256910270464L, 4503599635824640L, 9007207848869952L, 65536L, 18119951625748482L, 8L, 0L);
            }
            case 'M':
            case 'm': {
                return this.jjMoveStringLiteralDfa1_0(140737488355328L, -9223363240761737212L, 2392537302040577L, 117440528L, 552977039360L, 562949953454080L, 5629499634892800L, 36046389222309888L, 6146L, 0L);
            }
            case 'N':
            case 'n': {
                return this.jjMoveStringLiteralDfa1_0(-9221401712017801216L, 4398046511104L, 1125900443718304L, 288511853141688320L, 0L, 22588641758937090L, 119389920363547648L, 9007366892697600L, 0L, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa1_0(15762598695796736L, 33619984L, 8796094070784L, 15032385536L, 2256197860720640L, 679481376L, 4294967360L, 0L, 0L, 0L);
            }
            case 'P':
            case 'p': {
                return this.jjMoveStringLiteralDfa1_0(0L, 34493956096L, 2324455816927117312L, 1688867040133124L, 2314850221353336848L, -9223347828271611904L, 268697600L, 4194304L, 0L, 0L);
            }
            case 'Q':
            case 'q': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 32L, 0L, 0L, 0L, 0L, 0L);
            }
            case 'R':
            case 'r': {
                return this.jjMoveStringLiteralDfa1_0(144115188075855872L, 17592186044416L, 49540765476464640L, 6755502520270920L, 17179873349L, 281509336517124L, 4611690725713678336L, 2251799814733824L, 262144L, 0L);
            }
            case 'S':
            case 's': {
                return this.jjMoveStringLiteralDfa1_0(2035627031571464192L, 578712775455408130L, 936819641001443332L, 99079604119011328L, 1127308773556232L, 432345564227567872L, 288652588616777996L, 1099545444352L, 1L, 0L);
            }
            case 'T':
            case 't': {
                return this.jjMoveStringLiteralDfa1_0(2305843009213693952L, 2160066816L, 17592186044416L, 2305846857504391168L, 18339853951303680L, 4683743612465315976L, 68719607808L, 76569989825429504L, 24576L, 0L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa1_0(0L, 288230376151715328L, 1152921504606846976L, 144119586122366976L, 4611686018427387904L, 1152921504623624192L, 134217728L, 1873497444986126336L, 0L, 0L);
            }
            case 'V':
            case 'v': {
                return this.jjMoveStringLiteralDfa1_0(0L, 12288L, 144115192437948416L, 0L, 0L, 0L, 1152939096792891392L, 281751465230336L, 0L, 0L);
            }
            case 'W':
            case 'w': {
                return this.jjMoveStringLiteralDfa1_0(0L, 2523136L, 262144L, 0L, 11264L, 0L, 0L, 0L, 131072L, 0L);
            }
            case 'X':
            case 'x': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 6597069766656L, 0L, 108086391056891904L, 0L, 0L, 0L, 0L, 0L);
            }
            case 'Y':
            case 'y': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1536L, 0L);
            }
            case 'Z':
            case 'z': {
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 70368744177664L, 0L, 0L, 0L, 65536L, 0L);
            }
            case '{': {
                return this.jjStopAtPos(0, 568);
            }
            case '|': {
                return this.jjStopAtPos(0, 561);
            }
            case '}': {
                return this.jjStopAtPos(0, 569);
            }
            case '~': {
                this.jjmatchedKind = 567;
                return this.jjMoveStringLiteralDfa1_0(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1L);
            }
            default: {
                return this.jjMoveNfa_0(2, 0);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa1_0(final long active0, final long active1, final long active2, final long active3, final long active4, final long active5, final long active6, final long active7, final long active8, final long active9) {
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(0, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9);
            return 1;
        }
        switch (this.curChar) {
            case '*': {
                if ((active8 & 0x8000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 563);
                }
                if ((active9 & 0x1L) != 0x0L) {
                    return this.jjStopAtPos(1, 576);
                }
                break;
            }
            case ':': {
                if ((active0 & 0x100000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 56);
                }
                break;
            }
            case '=': {
                if ((active8 & 0x40000000000000L) != 0x0L) {
                    return this.jjStopAtPos(1, 566);
                }
                if ((active8 & Long.MIN_VALUE) != 0x0L) {
                    return this.jjStopAtPos(1, 575);
                }
                break;
            }
            case 'A':
            case 'a': {
                return this.jjMoveStringLiteralDfa2_0(active0, -2305420521870589952L, active1, 854870290616324L, active2, 578747740856008721L, active3, 586039714788688912L, active4, 140751446999042L, active5, -9148499538430918656L, active6, 1143492164501504L, active7, 2534101168947200L, active8, 1572864L, active9, 0L);
            }
            case 'B':
            case 'b': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 8796093022208L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 68719476736L, active8, 0L, active9, 0L);
            }
            case 'C':
            case 'c': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 72620818869257216L, active3, 137438953472L, active4, 0L, active5, 6665789243392L, active6, 288230376420147200L, active7, 141733920768L, active8, 0L, active9, 0L);
            }
            case 'D':
            case 'd': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1024L, active1, 35184372088832L, active2, Long.MIN_VALUE, active3, 34L, active4, 0L, active5, 8796093022208L, active6, 0L, active7, 0L, active8, 3L, active9, 0L);
            }
            case 'E':
            case 'e': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1008806316648497152L, active1, -7493972168430583808L, active2, 472896725319759874L, active3, 1155173338780303936L, active4, 10143270182244425L, active5, 282608854378624L, active6, 287092810780672L, active7, 40549988850221056L, active8, 329344L, active9, 0L);
            }
            case 'F':
            case 'f': {
                if ((active5 & 0x8000000L) != 0x0L) {
                    this.jjmatchedKind = 347;
                    this.jjmatchedPos = 1;
                }
                else if ((active6 & Long.MIN_VALUE) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 447, 287);
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 33554432L, active2, 0L, active3, 128L, active4, 0L, active5, 32L, active6, 0L, active7, 1125899906842624L, active8, 0L, active9, 0L);
            }
            case 'G':
            case 'g': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 4503599627370496L, active2, 0L, active3, 0L, active4, 4194304L, active5, 0L, active6, 1L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'H':
            case 'h': {
                return this.jjMoveStringLiteralDfa2_0(active0, 786432L, active1, 4611686018427781376L, active2, 134479876L, active3, 6144L, active4, 268435456L, active5, 0L, active6, 131072L, active7, 2415919104L, active8, 0L, active9, 0L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa2_0(active0, 18146340039032832L, active1, 1073782859L, active2, 131328L, active3, -9223372036854710271L, active4, -6899197967633208320L, active5, 1125899906875460L, active6, 1157425104267771904L, active7, 72770077648257024L, active8, 2252800L, active9, 0L);
            }
            case 'K':
            case 'k': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 8388608L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'L':
            case 'l': {
                return this.jjMoveStringLiteralDfa2_0(active0, 268444160L, active1, 64176294691078144L, active2, 34359738368L, active3, 140737488355584L, active4, 576460752303423488L, active5, 35188667056128L, active6, 32L, active7, 2233382995712L, active8, 0L, active9, 0L);
            }
            case 'M':
            case 'm': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 6597069766656L, active3, 0L, active4, 108086391056892160L, active5, 0L, active6, 0L, active7, 5497591955456L, active8, 0L, active9, 0L);
            }
            case 'N':
            case 'n': {
                if ((active0 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 39;
                    this.jjmatchedPos = 1;
                }
                else if ((active0 & 0x8000000000000L) != 0x0L) {
                    this.jjmatchedKind = 51;
                    this.jjmatchedPos = 1;
                }
                else if ((active1 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 105, 287);
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 5499705628672L, active1, 2594073385902277120L, active2, 1153202979583557640L, active3, 144172362689150976L, active4, 4613937818241138688L, active5, 1188950301693460480L, active6, 577025901422714944L, active7, -2449958197289549576L, active8, 64L, active9, 0L);
            }
            case 'O':
            case 'o': {
                if ((active3 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 231;
                    this.jjmatchedPos = 1;
                }
                else if ((active5 & 0x4000000000L) != 0x0L) {
                    this.jjmatchedKind = 358;
                    this.jjmatchedPos = 1;
                }
                else if ((active6 & 0x10L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 388, 287);
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 562975735808000L, active1, 146512406894346400L, active2, 4621959880880718560L, active3, 4652499960900624392L, active4, 1157496177361682948L, active5, 31736862239425291L, active6, 4875191401727921800L, active7, 18119951628566531L, active8, 6184L, active9, 0L);
            }
            case 'P':
            case 'p': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 66560L, active2, 1048576L, active3, 274877906944L, active4, 524288L, active5, 33558528L, active6, 140741783322624L, active7, 2305843009213693952L, active8, 0L, active9, 0L);
            }
            case 'Q':
            case 'q': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1152921504606846976L, active1, 0L, active2, 0L, active3, 0L, active4, 50331648L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'R':
            case 'r': {
                if ((active0 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 52;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 9007302335004672L, active1, 213909504L, active2, 2305843146652647424L, active3, 2306972207655550980L, active4, 562949953421328L, active5, 17594870530048L, active6, 3758096386L, active7, 4194304L, active8, 32768L, active9, 0L);
            }
            case 'S':
            case 's': {
                if ((active0 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 14;
                    this.jjmatchedPos = 1;
                }
                else if ((active0 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(1, 41, 287);
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 5368741888L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 576460752320200704L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'T':
            case 't': {
                if ((active2 & 0x1000000L) != 0x0L) {
                    this.jjmatchedKind = 152;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 72057594037929984L, active2, 0L, active3, 18014398509481984L, active4, 34426847232L, active5, 432345564227567616L, active6, 3145988L, active7, 9007199254740992L, active8, 0L, active9, 0L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa2_0(active0, 1125899906842624L, active1, 38671482880L, active2, 18085317009473536L, active3, 360287970191736832L, active4, 144139377331667104L, active5, 1082392576L, active6, 2305843155242582016L, active7, 14340L, active8, 20L, active9, 0L);
            }
            case 'V':
            case 'v': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 16L, active2, 0L, active3, 6442450944L, active4, 134217728L, active5, 0L, active6, 0L, active7, 25769803776L, active8, 0L, active9, 0L);
            }
            case 'W':
            case 'w': {
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 0L, active3, 8589934592L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'X':
            case 'x': {
                return this.jjMoveStringLiteralDfa2_0(active0, 536870912L, active1, 70369012613120L, active2, 0L, active3, 70368745750528L, active4, 288230376151711744L, active5, 1040L, active6, 70368744177664L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'Y':
            case 'y': {
                if ((active0 & 0x2000000000L) != 0x0L) {
                    this.jjmatchedKind = 37;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 206158430208L, active2, 0L, active3, 0L, active4, 68719476736L, active5, 6917529027641081856L, active6, 18014398509481984L, active7, 0L, active8, 256L, active9, 0L);
            }
            case '~': {
                if ((active9 & 0x2L) != 0x0L) {
                    this.jjmatchedKind = 577;
                    this.jjmatchedPos = 1;
                }
                return this.jjMoveStringLiteralDfa2_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 4L);
            }
        }
        return this.jjStartNfa_0(0, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9);
    }
    
    private int jjMoveStringLiteralDfa2_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8, final long old9, long active9) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8) | (active9 &= old9)) == 0x0L) {
            return this.jjStartNfa_0(0, old0, old1, old2, old3, old4, old5, old6, old7, old8, old9);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(1, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9);
            return 2;
        }
        switch (this.curChar) {
            case ' ': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 72057594037927936L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
            }
            case '*': {
                if ((active9 & 0x4L) != 0x0L) {
                    return this.jjStopAtPos(2, 578);
                }
                break;
            }
            case 'A':
            case 'a': {
                return this.jjMoveStringLiteralDfa3_0(active0, 264192L, active1, 4611686018507079808L, active2, 270532608L, active3, 3476778912330323968L, active4, 34359738368L, active5, 1099511629824L, active6, 140737488486660L, active7, 5525778726912L, active8, 34304L, active9, 0L);
            }
            case 'B':
            case 'b': {
                if ((active2 & 0x2000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 153, 287);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 2323857407723175936L, active1, 34359738368L, active2, 1152992423106838560L, active3, 72057594037927936L, active4, 0L, active5, 72057594037927936L, active6, 2305843009213693952L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'C':
            case 'c': {
                if ((active0 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 15;
                    this.jjmatchedPos = 2;
                }
                else if ((active8 & 0x80L) != 0x0L) {
                    this.jjmatchedKind = 519;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 1073741824L, active1, 288371113640067072L, active2, 281483566687240L, active3, 70368878919680L, active4, 4295098368L, active5, 583216160334413840L, active6, 631066932145030144L, active7, 2305843077933187072L, active8, 0L, active9, 0L);
            }
            case 'D':
            case 'd': {
                if ((active0 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 10, 287);
                }
                if ((active0 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 12, 287);
                }
                if ((active0 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 31, 287);
                }
                if ((active4 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 295;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 9007199254740992L, active1, 1024L, active2, 528L, active3, 25165824L, active4, 4096L, active5, -9187343239835811840L, active6, 137438953472L, active7, 36046389221785600L, active8, 0L, active9, 0L);
            }
            case 'E':
            case 'e': {
                if ((active1 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 75, 287);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 1572864L, active1, 35185714733328L, active2, 2594073557164097536L, active3, 6442450946L, active4, 594475151081865264L, active5, 8798240506880L, active6, 1152921523397328896L, active7, 9007199258935296L, active8, 0L, active9, 0L);
            }
            case 'F':
            case 'f': {
                if ((active1 & 0x2000000L) != 0x0L) {
                    this.jjmatchedKind = 89;
                    this.jjmatchedPos = 2;
                }
                else if ((active2 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 180;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 16777216L, active1, 536870912L, active2, 2L, active3, 512L, active4, 16385L, active5, 281474980904992L, active6, 1108101562368L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'G':
            case 'g': {
                if ((active1 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 69;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 3458764513820540928L, active2, 64L, active3, 0L, active4, 4503599763685384L, active5, 9007199791611908L, active6, 0L, active7, 8388608L, active8, 262144L, active9, 0L);
            }
            case 'H':
            case 'h': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 2L, active6, 288230376151711744L, active7, 4294967296L, active8, 0L, active9, 0L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa3_0(active0, 536870912L, active1, 134218240L, active2, 8650752L, active3, 145242187494326272L, active4, 128L, active5, 1152939096810717184L, active6, 2199031652352L, active7, 1730508156817113088L, active8, 16L, active9, 0L);
            }
            case 'J':
            case 'j': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0L, active2, 8796093024256L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'K':
            case 'k': {
                return this.jjMoveStringLiteralDfa3_0(active0, 131941395333120L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'L':
            case 'l': {
                if ((active0 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 9;
                    this.jjmatchedPos = 2;
                }
                else if ((active4 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 311;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 1442277780703150080L, active1, 64176307574935561L, active2, 4611693732255826048L, active3, 288230651031724296L, active4, 74309669317771268L, active5, 19140298416586753L, active6, 4611708008794161216L, active7, 137438953472L, active8, 0L, active9, 0L);
            }
            case 'M':
            case 'm': {
                if ((active7 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 460;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, Long.MIN_VALUE, active1, 144115188075855872L, active2, -9222228544761888768L, active3, 9007199523176481L, active4, 327791904293632L, active5, 268435904L, active6, 11258999068426880L, active7, 10240L, active8, 24576L, active9, 0L);
            }
            case 'N':
            case 'n': {
                if ((active2 & 0x100000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 184, 287);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 8388608L, active1, 4503943225278464L, active2, 0L, active3, 36028840539062288L, active4, 1152991944222277632L, active5, 70368744734720L, active6, 4503599660957697L, active7, 90327079312687105L, active8, 6152L, active9, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa3_0(active0, 103079215104L, active1, 0L, active2, 4L, active3, 140876001181700L, active4, 562949953421312L, active5, 432345568522665984L, active6, 72057596185411586L, active7, 2233383192322L, active8, 1L, active9, 0L);
            }
            case 'P':
            case 'p': {
                if ((active5 & 0x8L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 323, 287);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 144115188075855872L, active1, 70368744177664L, active2, 2251799813685248L, active3, 1024L, active4, 4899917494090727424L, active5, 4611686018460942336L, active6, 524288L, active7, 4L, active8, 0L, active9, 0L);
            }
            case 'Q':
            case 'q': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 281474976710656L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'R':
            case 'r': {
                if ((active0 & 0x400000000L) != 0x0L) {
                    this.jjmatchedKind = 34;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 8589934592L, active1, -8644659482588086272L, active2, 162164775253065728L, active3, 562967133290496L, active4, -9214347245346881536L, active5, 141304424038400L, active6, 144124533928099848L, active7, 281751465230336L, active8, 65540L, active9, 0L);
            }
            case 'S':
            case 's': {
                return this.jjMoveStringLiteralDfa3_0(active0, 1099981520896L, active1, 17746804867136L, active2, 536871168L, active3, 576478344493727744L, active4, 8589934594L, active5, 171798691840L, active6, 4261888L, active7, 144115188075855872L, active8, 2L, active9, 0L);
            }
            case 'T':
            case 't': {
                if ((active0 & 0x2000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 49, 287);
                }
                if ((active0 & 0x800000000000000L) != 0x0L) {
                    this.jjmatchedKind = 59;
                    this.jjmatchedPos = 2;
                }
                else if ((active7 & 0x10L) != 0x0L) {
                    this.jjmatchedKind = 452;
                    this.jjmatchedPos = 2;
                }
                else if ((active7 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 497;
                    this.jjmatchedPos = 2;
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 4612112633234006016L, active1, 844424946958336L, active2, 563294629789697L, active3, -4609425422519615360L, active4, 145386223517576192L, active5, 2306412626038517760L, active6, 70373307580416L, active7, -4611685468537356056L, active8, 1704256L, active9, 0L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 0L, active2, 36028797153181696L, active3, 6597069766656L, active4, 0L, active5, 35184372088832L, active6, 32L, active7, 288230376153808896L, active8, 32L, active9, 0L);
            }
            case 'V':
            case 'v': {
                if ((active8 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 533, 287);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 274877906944L, active1, 0L, active2, 576601491939262464L, active3, 35184439197760L, active4, 2305843009213693952L, active5, 0L, active6, 35184372092928L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'W':
            case 'w': {
                if ((active1 & 0x40000000000L) != 0x0L) {
                    this.jjmatchedKind = 106;
                    this.jjmatchedPos = 2;
                }
                else if ((active5 & 0x10000L) != 0x0L) {
                    this.jjmatchedKind = 336;
                    this.jjmatchedPos = 2;
                }
                else if ((active7 & 0x8000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(2, 499, 287);
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 1048576L, active2, 9007199254745088L, active3, 4785143323557888L, active4, 17188257856L, active5, 512L, active6, 274877906944L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'X':
            case 'x': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 8796093022212L, active2, 0L, active3, 0L, active4, 1073741824L, active5, 0L, active6, 1125968710221824L, active7, 4503599627403264L, active8, 0L, active9, 0L);
            }
            case 'Y':
            case 'y': {
                if ((active1 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 103;
                    this.jjmatchedPos = 2;
                }
                else {
                    if ((active5 & 0x200000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(2, 341, 287);
                    }
                    if ((active5 & 0x4000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(2, 346, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 1099511627776L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
            }
            case 'Z':
            case 'z': {
                return this.jjMoveStringLiteralDfa3_0(active0, 0L, active1, 2L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L, active9, 0L);
            }
        }
        return this.jjStartNfa_0(1, active0, active1, active2, active3, active4, active5, active6, active7, active8, active9);
    }
    
    private int jjMoveStringLiteralDfa3_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8, final long old9, long active9) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8) | (active9 &= old9)) == 0x0L) {
            return this.jjStartNfa_0(1, old0, old1, old2, old3, old4, old5, old6, old7, old8, old9);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(2, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 3;
        }
        switch (this.curChar) {
            case '2': {
                if ((active7 & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 453, 9);
                }
                break;
            }
            case '4': {
                if ((active7 & 0x40L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 454, 9);
                }
                break;
            }
            case '8': {
                if ((active7 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 455, 9);
                }
                break;
            }
            case '_': {
                return this.jjMoveStringLiteralDfa4_0(active0, 1152921504606846976L, active1, 79164837199872L, active2, 0L, active3, 0L, active4, 20594032640L, active5, Long.MIN_VALUE, active6, 0L, active7, 0L, active8, 1L);
            }
            case 'A':
            case 'a': {
                if ((active3 & 0x4000L) != 0x0L) {
                    this.jjmatchedKind = 206;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 4611686019518955520L, active1, 844424931181568L, active2, 1127008008962080L, active3, 325489801625616L, active4, 1168836132864L, active5, 4503608754176000L, active6, 2286984186560512L, active7, 140741783324416L, active8, 0L);
            }
            case 'B':
            case 'b': {
                if ((active7 & 0x800000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 483, 287);
                }
                if ((active7 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 489, 287);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 262144L, active4, 16L, active5, 4294967296L, active6, 0L, active7, 281474978809856L, active8, 0L);
            }
            case 'C':
            case 'c': {
                if ((active0 & 0x4000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 26, 287);
                }
                if ((active5 & 0x400L) != 0x0L) {
                    this.jjmatchedKind = 330;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 140737488879616L, active1, 275146342400L, active2, 2305843009213694209L, active3, 140737488355328L, active4, 9007199523176448L, active5, 1153555510499221504L, active6, 0L, active7, 576460753918230528L, active8, 0L);
            }
            case 'D':
            case 'd': {
                if ((active1 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 71, 287);
                }
                if ((active2 & 0x400000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 186, 287);
                }
                if ((active5 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 331;
                    this.jjmatchedPos = 3;
                }
                else if ((active8 & 0x10L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 516, 287);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 270532608L, active3, 1152921504606846976L, active4, 72057594037927936L, active5, 1L, active6, 274877906944L, active7, 0L, active8, 0L);
            }
            case 'E':
            case 'e': {
                if ((active0 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 17, 287);
                }
                if ((active0 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 28, 287);
                }
                if ((active0 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 43;
                    this.jjmatchedPos = 3;
                }
                else {
                    if ((active0 & Long.MIN_VALUE) != 0x0L) {
                        return this.jjStartNfaWithStates_0(3, 63, 287);
                    }
                    if ((active1 & 0x2L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(3, 65, 287);
                    }
                    if ((active3 & 0x8L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(3, 195, 287);
                    }
                    if ((active3 & 0x4000000L) != 0x0L) {
                        this.jjmatchedKind = 218;
                        this.jjmatchedPos = 3;
                    }
                    else {
                        if ((active3 & 0x20000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(3, 221, 287);
                        }
                        if ((active4 & 0x200000000000L) != 0x0L) {
                            this.jjmatchedKind = 301;
                            this.jjmatchedPos = 3;
                        }
                        else {
                            if ((active4 & 0x400000000000L) != 0x0L) {
                                return this.jjStartNfaWithStates_0(3, 302, 287);
                            }
                            if ((active4 & 0x800000000000L) != 0x0L) {
                                this.jjmatchedKind = 303;
                                this.jjmatchedPos = 3;
                            }
                            else {
                                if ((active5 & 0x100L) != 0x0L) {
                                    return this.jjStartNfaWithStates_0(3, 328, 287);
                                }
                                if ((active5 & 0x2000000000000000L) != 0x0L) {
                                    this.jjmatchedKind = 381;
                                    this.jjmatchedPos = 3;
                                }
                                else {
                                    if ((active5 & 0x4000000000000000L) != 0x0L) {
                                        return this.jjStartNfaWithStates_0(3, 382, 287);
                                    }
                                    if ((active6 & 0x2000000000000000L) != 0x0L) {
                                        return this.jjStartNfaWithStates_0(3, 445, 287);
                                    }
                                }
                            }
                        }
                    }
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 297361828843888640L, active1, 17592186585097L, active2, 576610494195043328L, active3, 70377343549569L, active4, 281749854634240L, active5, 36451009492959248L, active6, 288310642214965248L, active7, -4611685468671008760L, active8, 1859904L);
            }
            case 'F':
            case 'f': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 80L, active3, 4L, active4, 4398046511104L, active5, 2199023255552L, active6, 8589934592L, active7, 0L, active8, 0L);
            }
            case 'G':
            case 'g': {
                if ((active7 & 0x400000000000L) != 0x0L) {
                    this.jjmatchedKind = 494;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, Long.MIN_VALUE, active2, 18014398509481984L, active3, 1168231104512L, active4, 4503599627370496L, active5, 9007199254740992L, active6, 32768L, active7, 18053980928081920L, active8, 8L);
            }
            case 'H':
            case 'h': {
                if ((active1 & 0x8000L) != 0x0L) {
                    this.jjmatchedKind = 79;
                    this.jjmatchedPos = 3;
                }
                else {
                    if ((active3 & 0x4000000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(3, 254, 287);
                    }
                    if ((active6 & 0x400000L) != 0x0L) {
                        this.jjmatchedKind = 406;
                        this.jjmatchedPos = 3;
                    }
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 138936320L, active4, 2108416L, active5, 2251799813685252L, active6, 0L, active7, 2305843009213693952L, active8, 131072L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa4_0(active0, 279173136384L, active1, 2954361356091916288L, active2, -9214364837598903806L, active3, 2305843284141932576L, active4, 4611686018427387904L, active5, 576460821022904384L, active6, 9024933577359424L, active7, 180161577440264192L, active8, 0L);
            }
            case 'K':
            case 'k': {
                if ((active1 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 85, 287);
                }
                if ((active1 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 96, 287);
                }
                if ((active1 & 0x800000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 111, 287);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 4294967296L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'L':
            case 'l': {
                if ((active0 & 0x4000000000000L) != 0x0L) {
                    this.jjmatchedKind = 50;
                    this.jjmatchedPos = 3;
                }
                else if ((active5 & 0x40000L) != 0x0L) {
                    this.jjmatchedKind = 338;
                    this.jjmatchedPos = 3;
                }
                else if ((active7 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 465;
                    this.jjmatchedPos = 3;
                }
                else if ((active7 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 468, 287);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 2467972595799033856L, active1, 44023414784L, active2, 4611967493404102664L, active3, 288230376153849856L, active4, 288230376151711876L, active5, 73183493978324992L, active6, 4629700416936869888L, active7, 1127068171829252L, active8, 0L);
            }
            case 'M':
            case 'm': {
                if ((active0 & 0x800000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 35, 287);
                }
                if ((active7 & 0x400000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 506, 287);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 144115190223339520L, active2, 34359738368L, active3, Long.MIN_VALUE, active4, 576460752303685640L, active5, 17592454479872L, active6, 144115188075855872L, active7, 0L, active8, 0L);
            }
            case 'N':
            case 'n': {
                if ((active1 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 72, 287);
                }
                if ((active1 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 81, 287);
                }
                if ((active1 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 86;
                    this.jjmatchedPos = 3;
                }
                else {
                    if ((active5 & 0x100000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(3, 340, 287);
                    }
                    if ((active6 & 0x20000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(3, 401, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 35184447651840L, active2, 134217728L, active3, 40534595669596162L, active4, 0L, active5, 8796109799424L, active6, 0L, active7, 0L, active8, 32L);
            }
            case 'O':
            case 'o': {
                if ((active0 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 42, 287);
                }
                if ((active4 & 0x200000000000000L) != 0x0L) {
                    this.jjmatchedKind = 313;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 355784439298523648L, active2, 1152921504606855296L, active3, 1125900175278912L, active4, 2305843009218019328L, active5, 18014399583223810L, active6, 549755816961L, active7, 137438953472L, active8, 65536L);
            }
            case 'P':
            case 'p': {
                if ((active2 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 151, 287);
                }
                if ((active3 & 0x20000L) != 0x0L) {
                    this.jjmatchedKind = 209;
                    this.jjmatchedPos = 3;
                }
                else if ((active4 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 299;
                    this.jjmatchedPos = 3;
                }
                else {
                    if ((active5 & 0x10000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(3, 360, 287);
                    }
                    if ((active6 & 0x400000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(3, 418, 287);
                    }
                    if ((active7 & 0x2L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(3, 449, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 1125899906842624L, active2, 2339760743907328L, active3, 81064930731622400L, active4, 2336462209536L, active5, 128L, active6, 642L, active7, 0L, active8, 32768L);
            }
            case 'Q':
            case 'q': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 144115188075855872L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'R':
            case 'r': {
                if ((active3 & 0x100000000L) != 0x0L) {
                    this.jjmatchedKind = 224;
                    this.jjmatchedPos = 3;
                }
                else if ((active7 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 476;
                    this.jjmatchedPos = 3;
                }
                else if ((active8 & 0x200L) != 0x0L) {
                    this.jjmatchedKind = 521;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 5764607523034497040L, active2, 4294967296L, active3, 18014400656965632L, active4, 17592186568801L, active5, 432345564227567616L, active6, 649222033783128064L, active7, 27917287424L, active8, 1028L);
            }
            case 'S':
            case 's': {
                if ((active1 & 0x10000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 104, 287);
                }
                if ((active4 & 0x4000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 306, 287);
                }
                if ((active4 & 0x40000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 310, 287);
                }
                if ((active5 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 329, 287);
                }
                if ((active6 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 400, 287);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 545259520L, active1, 17179869188L, active2, 180148932897144832L, active3, 4398046511104L, active4, -9223372028264808448L, active5, 35186519703584L, active6, 32L, active7, 1L, active8, 0L);
            }
            case 'T':
            case 't': {
                if ((active1 & 0x40L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 70, 287);
                }
                if ((active3 & 0x800000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 251, 287);
                }
                if ((active4 & 0x2L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 257, 287);
                }
                if ((active5 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 342, 287);
                }
                if ((active6 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 408, 287);
                }
                if ((active7 & 0x10000000000000L) != 0x0L) {
                    this.jjmatchedKind = 500;
                    this.jjmatchedPos = 3;
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 134217728L, active1, 2251937252638720L, active2, 600333885898752L, active3, 580559319335936L, active4, 34359738368L, active5, 51539607552L, active6, 2267751145740L, active7, 0L, active8, 6144L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa4_0(active0, 281543700381696L, active1, 4096L, active2, 18320719872L, active3, 2251799813685248L, active4, 562949953425408L, active5, 4398046543872L, active6, 281477124194304L, active7, 0L, active8, 0L);
            }
            case 'V':
            case 'v': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 134217728L, active2, 274877906944L, active3, 1073741824L, active4, 1152921504606912512L, active5, 0L, active6, 5629499534213120L, active7, 1152921504606846976L, active8, 0L);
            }
            case 'W':
            case 'w': {
                if ((active1 & 0x2000L) != 0x0L) {
                    this.jjmatchedKind = 77;
                    this.jjmatchedPos = 3;
                }
                else if ((active2 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 130, 287);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 65536L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 1152921504606846976L, active7, 0L, active8, 0L);
            }
            case 'X':
            case 'x': {
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 9007199254740992L, active8, 0L);
            }
            case 'Y':
            case 'y': {
                if ((active4 & 0x8000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 307, 287);
                }
                if ((active6 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(3, 426, 287);
                }
                return this.jjMoveStringLiteralDfa4_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 36028831378702336L, active7, 72066665075965952L, active8, 2L);
            }
        }
        return this.jjStartNfa_0(2, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
    }
    
    private int jjMoveStringLiteralDfa4_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(2, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(3, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 4;
        }
        switch (this.curChar) {
            case ' ': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 3072L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case '2': {
                if ((active0 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 45, 9);
                }
                break;
            }
            case '4': {
                if ((active0 & 0x400000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 46, 9);
                }
                break;
            }
            case '_': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 4194304L, active3, 0L, active4, 4297064448L, active5, 1073741824L, active6, 1152921504606846986L, active7, 0L, active8, 0L);
            }
            case 'A':
            case 'a': {
                if ((active8 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 520, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 144115188075855872L, active1, 577586927088173056L, active2, 70373039161600L, active3, -9151296850630270976L, active4, 72057594038452224L, active5, 144132921995821056L, active6, 149744687610068992L, active7, 3458764515968024576L, active8, 0L);
            }
            case 'B':
            case 'b': {
                if ((active7 & 0x2000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 485, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 4611686018427387904L, active1, 0L, active2, 0L, active3, 65536L, active4, 16777220L, active5, 0L, active6, 0L, active7, 43980465111040L, active8, 0L);
            }
            case 'C':
            case 'c': {
                if ((active0 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 44, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 1441151880758558720L, active1, 16777216L, active2, 13194139666432L, active3, 2199023255808L, active4, 274877911040L, active5, 4503599627370496L, active6, 36028831378702336L, active7, 25769803776L, active8, 8L);
            }
            case 'D':
            case 'd': {
                if ((active2 & 0x20000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 181, 287);
                }
                if ((active4 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 263, 287);
                }
                if ((active7 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 463, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 1073741824L, active2, 32L, active3, 0L, active4, 131328L, active5, 0L, active6, 72075186223972352L, active7, 0L, active8, 0L);
            }
            case 'E':
            case 'e': {
                if ((active0 & 0x2000000000000000L) != 0x0L) {
                    this.jjmatchedKind = 61;
                    this.jjmatchedPos = 4;
                }
                else {
                    if ((active1 & 0x40000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 82, 287);
                    }
                    if ((active1 & Long.MIN_VALUE) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 127, 287);
                    }
                    if ((active2 & 0x4000000L) != 0x0L) {
                        this.jjmatchedKind = 154;
                        this.jjmatchedPos = 4;
                    }
                    else {
                        if ((active2 & 0x40000000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 182, 287);
                        }
                        if ((active2 & 0x80000000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 183, 287);
                        }
                        if ((active3 & 0x2000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 229, 287);
                        }
                        if ((active5 & 0x8000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 359, 287);
                        }
                        if ((active5 & 0x8000000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 371, 287);
                        }
                        if ((active5 & 0x400000000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 378, 287);
                        }
                        if ((active6 & 0x8000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 399, 287);
                        }
                        if ((active6 & 0x100000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 404, 287);
                        }
                        if ((active6 & 0x200000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 405, 287);
                        }
                        if ((active6 & 0x40000000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 438, 287);
                        }
                        if ((active7 & 0x40000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 490, 287);
                        }
                        if ((active7 & 0x4000000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(4, 498, 287);
                        }
                    }
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 65536L, active1, 1155173450449424384L, active2, 2305843318988472320L, active3, 36033196273434624L, active4, 1742910647978721289L, active5, 72057596185411616L, active6, 844708397973504L, active7, 67584L, active8, 4L);
            }
            case 'F':
            case 'f': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 0L, active3, 8798257283072L, active4, 2216203124736L, active5, 1125899906842624L, active6, 0L, active7, 0L, active8, 65536L);
            }
            case 'G':
            case 'g': {
                if ((active5 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 344, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 8L, active2, 128L, active3, 1099511633936L, active4, 0L, active5, 18014398509481984L, active6, 0L, active7, 144115188075855880L, active8, 1L);
            }
            case 'H':
            case 'h': {
                if ((active5 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 333, 287);
                }
                if ((active5 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 369;
                    this.jjmatchedPos = 4;
                }
                else if ((active8 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 524;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 140737488355328L, active1, 0L, active2, 562949953421313L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 576460753914036224L, active8, 34816L);
            }
            case 'I':
            case 'i': {
                if ((active5 & 0x800000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 379, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 18014407233634304L, active1, 144115241897164804L, active2, 146402172261634128L, active3, 1153765946721042436L, active4, 288234808625070144L, active5, -9214224082663374848L, active6, 2267751121156L, active7, 281749925920772L, active8, 131072L);
            }
            case 'K':
            case 'k': {
                if ((active0 & 0x80000L) != 0x0L) {
                    this.jjmatchedKind = 19;
                    this.jjmatchedPos = 4;
                }
                else {
                    if ((active2 & 0x8000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 155, 287);
                    }
                    if ((active3 & 0x800000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 239, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 0L, active3, 64L, active4, 268435456L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'L':
            case 'l': {
                if ((active4 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 272, 287);
                }
                if ((active5 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 353, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 536870912L, active2, 17594333528064L, active3, 2314885392840818688L, active4, 512L, active5, 70368744177667L, active6, 43982075985920L, active7, 1099547541504L, active8, 0L);
            }
            case 'M':
            case 'm': {
                return this.jjMoveStringLiteralDfa5_0(active0, 4299161600L, active1, 288230376151711744L, active2, 140754668814336L, active3, 34359738368L, active4, 68719476736L, active5, 0L, active6, 288230380580897792L, active7, 16384L, active8, 0L);
            }
            case 'N':
            case 'n': {
                if ((active1 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 73, 287);
                }
                if ((active2 & Long.MIN_VALUE) != 0x0L) {
                    this.jjmatchedKind = 191;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 274878169088L, active1, 2305843077933170689L, active2, 68719476738L, active3, 269484065L, active4, 0L, active5, 536870912L, active6, 9007199523176512L, active7, 8388608L, active8, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 4611686018428436488L, active3, 0L, active4, 8192L, active5, 1152921573326327936L, active6, 0L, active7, 68853694464L, active8, 0L);
            }
            case 'P':
            case 'p': {
                if ((active0 & 0x1000000000L) != 0x0L) {
                    this.jjmatchedKind = 36;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 1073741824L, active1, 562949953421312L, active2, 577586789649219584L, active3, 70368744177664L, active4, 562949961809920L, active5, 16L, active6, 2147483648L, active7, 0L, active8, 0L);
            }
            case 'R':
            case 'r': {
                if ((active0 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 13, 287);
                }
                if ((active0 & 0x20000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 53, 287);
                }
                if ((active3 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 199, 287);
                }
                if ((active3 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 225, 287);
                }
                if ((active3 & 0x4000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 242, 287);
                }
                if ((active5 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 339, 287);
                }
                if ((active5 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 343, 287);
                }
                if ((active7 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 480, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 282574488338432L, active1, 4881831627866128L, active2, 1073750016L, active3, 2251799813685760L, active4, 1240310235136L, active5, 283708359720960L, active6, 71468256360961L, active7, -4611545280939024384L, active8, 64L);
            }
            case 'S':
            case 's': {
                if ((active2 & 0x10000000L) != 0x0L) {
                    this.jjmatchedKind = 156;
                    this.jjmatchedPos = 4;
                }
                else {
                    if ((active3 & 0x400000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 250, 287);
                    }
                    if ((active5 & 0x8000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 335, 287);
                    }
                    if ((active5 & 0x20000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 337, 287);
                    }
                    if ((active8 & 0x2L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 513, 287);
                    }
                    if ((active8 & 0x400L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(4, 522, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 4683743612473704448L, active2, 2098688L, active3, 0L, active4, 281475010265088L, active5, 4398046511104L, active6, 140737488355328L, active7, 0L, active8, 16384L);
            }
            case 'T':
            case 't': {
                if ((active1 & 0x4000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 90, 287);
                }
                if ((active3 & 0x4000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 230, 287);
                }
                if ((active3 & 0x40000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 246, 287);
                }
                if ((active4 & 0x2000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 317, 287);
                }
                if ((active4 & Long.MIN_VALUE) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 319, 287);
                }
                if ((active5 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 322, 287);
                }
                if ((active5 & 0x40L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 326, 287);
                }
                if ((active6 & 0x2000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 421, 287);
                }
                if ((active7 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 456;
                    this.jjmatchedPos = 4;
                }
                else if ((active7 & 0x20000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 501, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 579862528L, active1, 35184372089856L, active2, 1657857409024L, active3, 35651586L, active4, 0L, active5, 43980465111040L, active6, 32L, active7, 90072542303225345L, active8, 1581088L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa5_0(active0, 16777216L, active1, 268435456L, active2, 1153202979583557632L, active3, 148618856422704128L, active4, 16L, active5, 0L, active6, 4611686018427388032L, active7, 36046389221785600L, active8, 0L);
            }
            case 'V':
            case 'v': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 4611686018427387904L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'W':
            case 'w': {
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 63050394783186944L, active2, 0L, active3, 0L, active4, 8589934592L, active5, 0L, active6, 549755813888L, active7, 0L, active8, 0L);
            }
            case 'X':
            case 'x': {
                if ((active5 & 0x80000000000000L) != 0x0L) {
                    this.jjmatchedKind = 375;
                    this.jjmatchedPos = 4;
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 0L, active1, 65536L, active2, 0L, active3, 8388608L, active4, 0L, active5, 0L, active6, 2251799914348544L, active7, 0L, active8, 262144L);
            }
            case 'Y':
            case 'y': {
                if ((active4 & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 261, 287);
                }
                if ((active5 & 0x2000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 345, 287);
                }
                if ((active7 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(4, 467, 287);
                }
                return this.jjMoveStringLiteralDfa5_0(active0, 2048L, active1, 1048576L, active2, 2199023255552L, active3, 0L, active4, 537919488L, active5, 0L, active6, 576460752303423488L, active7, 0L, active8, 0L);
            }
        }
        return this.jjStartNfa_0(3, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
    }
    
    private int jjMoveStringLiteralDfa5_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(3, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(4, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 5;
        }
        switch (this.curChar) {
            case '4': {
                if ((active7 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 457, 9);
                }
                break;
            }
            case '8': {
                if ((active7 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 458, 9);
                }
                break;
            }
            case '_': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 63050394783186944L, active2, 0L, active3, 0L, active4, 536903680L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'A':
            case 'a': {
                return this.jjMoveStringLiteralDfa6_0(active0, 5764889002305912832L, active1, 562949962334209L, active2, 17592186044416L, active3, 2199023255808L, active4, 1099511627780L, active5, 1125917086711808L, active6, 288250171664916480L, active7, 576460753914118144L, active8, 0L);
            }
            case 'B':
            case 'b': {
                if ((active7 & 0x1000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 484, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 16384L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'C':
            case 'c': {
                if ((active1 & 0x800000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 99, 287);
                }
                if ((active4 & 0x800000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 291, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 144115188075855872L, active1, 8589934592L, active2, 4194816L, active3, 36028797153181696L, active4, 288230376151711744L, active5, 0L, active6, 268435456L, active7, 2147483652L, active8, 0L);
            }
            case 'D':
            case 'd': {
                if ((active2 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 157, 287);
                }
                if ((active3 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 234, 287);
                }
                if ((active4 & 0x10000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 308, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 2251799813685248L, active2, 2306124484190412800L, active3, 0L, active4, 0L, active5, 1152921642045800450L, active6, 549755813888L, active7, 1099511627776L, active8, 0L);
            }
            case 'E':
            case 'e': {
                if ((active0 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 20, 287);
                }
                if ((active0 & 0x2000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 25, 287);
                }
                if ((active0 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 30, 287);
                }
                if ((active1 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 74, 287);
                }
                if ((active1 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 93, 287);
                }
                if ((active1 & 0x1000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 124, 287);
                }
                if ((active1 & 0x2000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 125, 287);
                }
                if ((active2 & 0x2L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 129, 287);
                }
                if ((active3 & 0x10L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 196, 287);
                }
                if ((active3 & 0x40L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 198, 287);
                }
                if ((active3 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 201, 287);
                }
                if ((active3 & 0x800L) != 0x0L) {
                    this.jjmatchedKind = 203;
                    this.jjmatchedPos = 5;
                }
                else {
                    if ((active3 & 0x40000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 210, 287);
                    }
                    if ((active3 & 0x800000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 227, 287);
                    }
                    if ((active3 & 0x20000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 245, 287);
                    }
                    if ((active3 & 0x200000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 249, 287);
                    }
                    if ((active4 & 0x20000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 273, 287);
                    }
                    if ((active4 & 0x400000L) != 0x0L) {
                        this.jjmatchedKind = 278;
                        this.jjmatchedPos = 5;
                    }
                    else {
                        if ((active6 & 0x40L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(5, 390, 287);
                        }
                        if ((active6 & 0x800000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(5, 431, 287);
                        }
                        if ((active7 & 0x200000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(5, 469, 287);
                        }
                    }
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 140737488683008L, active1, 4616189618054758400L, active2, 140737488879617L, active3, 1099513729024L, active4, 137440006656L, active5, 323256418566144L, active6, 72057594037928483L, active7, 90071992547409928L, active8, 33L);
            }
            case 'F':
            case 'f': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 160L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'G':
            case 'g': {
                if ((active0 & 0x4000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 38, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 8589934592L, active1, 1125899906842624L, active2, 0L, active3, 0L, active4, 67108864L, active5, 162270324073693184L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'H':
            case 'h': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 4398046511104L, active3, 0L, active4, 0L, active5, 4503599627370496L, active6, 0L, active7, 25769803776L, active8, 8L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 35184372105232L, active2, 1649269637120L, active3, 2305887058666717218L, active4, 565217712931088L, active5, 8832063373312L, active6, 35186116919304L, active7, 549789376512L, active8, 1671168L);
            }
            case 'K':
            case 'k': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 4294967296L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'L':
            case 'l': {
                if ((active5 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 352, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 16777216L, active1, 576460752437641216L, active2, 80L, active3, 2147549188L, active4, 4398046511104L, active5, 0L, active6, 41658330913177600L, active7, 43980465111040L, active8, 0L);
            }
            case 'M':
            case 'm': {
                if ((active1 & 0x2000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 101, 287);
                }
                if ((active3 & 0x10000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 244, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 288230376151777280L, active2, 0L, active3, 0L, active4, 33554432L, active5, 0L, active6, 1153484523279745024L, active7, 36046389222047744L, active8, 0L);
            }
            case 'N':
            case 'n': {
                if ((active0 & 0x400000L) != 0x0L) {
                    this.jjmatchedKind = 22;
                    this.jjmatchedPos = 5;
                }
                else if ((active2 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 158;
                    this.jjmatchedPos = 5;
                }
                else if ((active5 & 0x1000L) != 0x0L) {
                    this.jjmatchedKind = 332;
                    this.jjmatchedPos = 5;
                }
                else {
                    if ((active5 & 0x1000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 356, 287);
                    }
                    if ((active8 & 0x20000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 529, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 18014398643699712L, active1, 2147483648L, active2, 1155173355961192448L, active3, 1155190896611295232L, active4, 585485543744471048L, active5, -9214364837600034816L, active6, 351843720888320L, active7, 144396938131800064L, active8, 4L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 72418251048484864L, active2, 720576086408167424L, active3, 33587200L, active4, 4611686047552634880L, active5, 1L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'P':
            case 'p': {
                if ((active3 & Long.MIN_VALUE) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 255, 287);
                }
                if ((active6 & 0x4000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 446, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 1128098930098176L, active3, 0L, active4, 2098176L, active5, 0L, active6, 576461027181331456L, active7, 0L, active8, 262144L);
            }
            case 'R':
            case 'r': {
                if ((active7 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 459, 287);
                }
                if ((active7 & 0x2000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 509, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 8388608L, active1, 8L, active2, 633593575506176L, active3, 72057595111670784L, active4, 1152921504615254016L, active5, 17594333528192L, active6, 1108101562368L, active7, 1152921504606846977L, active8, 0L);
            }
            case 'S':
            case 's': {
                if ((active0 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 29, 287);
                }
                if ((active1 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 76, 287);
                }
                if ((active1 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 84, 287);
                }
                if ((active1 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 94, 287);
                }
                if ((active2 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 138, 287);
                }
                if ((active2 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 159, 287);
                }
                if ((active6 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 415, 287);
                }
                if ((active8 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 523, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 262152L, active3, 1L, active4, 268435457L, active5, 72057594037944320L, active6, 4356L, active7, 4194304L, active8, 0L);
            }
            case 'T':
            case 't': {
                if ((active0 & 0x10000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 40, 287);
                }
                if ((active0 & 0x400000000000000L) != 0x0L) {
                    this.jjmatchedKind = 58;
                    this.jjmatchedPos = 5;
                }
                else {
                    if ((active1 & 0x4000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 102, 287);
                    }
                    if ((active2 & 0x800L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 139, 287);
                    }
                    if ((active2 & 0x80000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 171, 287);
                    }
                    if ((active3 & 0x100000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 212, 287);
                    }
                    if ((active3 & 0x1000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(5, 240, 287);
                    }
                    if ((active5 & 0x10L) != 0x0L) {
                        this.jjmatchedKind = 324;
                        this.jjmatchedPos = 5;
                    }
                    else {
                        if ((active5 & 0x20L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(5, 325, 287);
                        }
                        if ((active5 & 0x10000000L) != 0x0L) {
                            this.jjmatchedKind = 348;
                            this.jjmatchedPos = 5;
                        }
                        else {
                            if ((active6 & 0x800L) != 0x0L) {
                                return this.jjStartNfaWithStates_0(5, 395, 287);
                            }
                            if ((active6 & 0x200000000000000L) != 0x0L) {
                                return this.jjStartNfaWithStates_0(5, 441, 287);
                            }
                            if ((active7 & 0x800000L) != 0x0L) {
                                return this.jjStartNfaWithStates_0(5, 471, 287);
                            }
                        }
                    }
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 144115188344291328L, active2, 35253091696640L, active3, 633335885864960L, active4, 72339343893069888L, active5, 0L, active6, 100663424L, active7, 0L, active8, 16384L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 8192L, active5, 70368744177664L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'V':
            case 'v': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 17592186044416L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 11258999068426240L, active7, -4611686018427387904L, active8, 64L);
            }
            case 'W':
            case 'w': {
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 0L, active2, 4611686018427387904L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'Y':
            case 'y': {
                if ((active2 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 160, 287);
                }
                if ((active3 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 216, 287);
                }
                if ((active6 & 0x80000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 427, 287);
                }
                if ((active7 & 0x800000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 495, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 0L, active1, 68719476736L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'Z':
            case 'z': {
                if ((active8 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(5, 525, 287);
                }
                return this.jjMoveStringLiteralDfa6_0(active0, 2048L, active1, 4L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
        }
        return this.jjStartNfa_0(4, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
    }
    
    private int jjMoveStringLiteralDfa6_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(4, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(5, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 6;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 6755399441055744L, active2, 0L, active3, 0L, active4, 274877906944L, active5, 0L, active6, 8589934593L, active7, 0L, active8, 262144L);
            }
            case 'A':
            case 'a': {
                if ((active4 & 0x100000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 312, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 8388608L, active1, 2147500032L, active2, 5243392L, active3, 0L, active4, 281475010265344L, active5, 128L, active6, 11330536043708416L, active7, -4611403418035027963L, active8, 16456L);
            }
            case 'B':
            case 'b': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 288230376151711744L, active7, 17592186044416L, active8, 0L);
            }
            case 'C':
            case 'c': {
                if ((active4 & 0x1000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 292, 287);
                }
                if ((active7 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 461, 287);
                }
                if ((active8 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 527, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 134217728L, active1, 8388608L, active2, 0L, active3, 17592186077184L, active4, 4L, active5, 1125934266580993L, active6, 281474976710656L, active7, 1152921504606846976L, active8, 4L);
            }
            case 'D':
            case 'd': {
                if ((active0 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 18, 287);
                }
                if ((active2 & 0x1L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 128, 287);
                }
                if ((active4 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 268, 287);
                }
                if ((active4 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 276, 287);
                }
                if ((active5 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 362, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 9007199254741008L, active2, 1152921504606847232L, active3, 35253091565568L, active4, 0L, active5, Long.MIN_VALUE, active6, 35184372088832L, active7, 0L, active8, 0L);
            }
            case 'E':
            case 'e': {
                if ((active0 & 0x800L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 11, 287);
                }
                if ((active0 & 0x200000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 57, 287);
                }
                if ((active1 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 66, 287);
                }
                if ((active1 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 92, 287);
                }
                if ((active2 & 0x10L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 132, 287);
                }
                if ((active2 & 0x40L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 134, 287);
                }
                if ((active2 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 140, 287);
                }
                if ((active2 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 169, 287);
                }
                if ((active3 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 194, 287);
                }
                if ((active3 & 0x400L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 202, 287);
                }
                if ((active3 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 205, 287);
                }
                if ((active3 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 208, 287);
                }
                if ((active4 & 0x40L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 262, 287);
                }
                if ((active4 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 298, 287);
                }
                if ((active5 & 0x2000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 357, 287);
                }
                if ((active5 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 361, 287);
                }
                if ((active5 & 0x10000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 372, 287);
                }
                if ((active5 & 0x200000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 377, 287);
                }
                if ((active5 & 0x1000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 380, 287);
                }
                if ((active6 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 391, 287);
                }
                if ((active6 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 396, 287);
                }
                if ((active6 & 0x800000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 419, 287);
                }
                if ((active6 & 0x80000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 439, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 1143492227104768L, active2, 567347999932424L, active3, 0L, active4, 4294983680L, active5, 16384L, active6, 1153485279294914560L, active7, 144115188075855872L, active8, 0L);
            }
            case 'F':
            case 'f': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 1073741826L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'G':
            case 'g': {
                if ((active2 & 0x8000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 179, 287);
                }
                if ((active3 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 214, 287);
                }
                if ((active3 & 0x1000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 252, 287);
                }
                if ((active5 & 0x20000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 373, 287);
                }
                if ((active7 & 0x4000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 486, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 18014398509481984L, active1, 562949953421312L, active2, 0L, active3, 524288L, active4, 16777216L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'H':
            case 'h': {
                if ((active4 & 0x1L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 256, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 67108864L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 864691128455135232L, active2, 7495432407919624352L, active3, 2885135691153409L, active4, 288230376160100352L, active5, 18014398509481984L, active6, 0L, active7, 20971520L, active8, 0L);
            }
            case 'K':
            case 'k': {
                if ((active3 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 219, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 536870912L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'L':
            case 'l': {
                if ((active0 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 48, 287);
                }
                if ((active1 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 80, 287);
                }
                if ((active5 & 0x400000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 354, 287);
                }
                if ((active6 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 407;
                    this.jjmatchedPos = 6;
                }
                else {
                    if ((active6 & 0x100000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 416, 287);
                    }
                    if ((active7 & 0x4000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 462, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 1152921504606846976L, active1, 72057594037927936L, active2, 16384L, active3, 8796093022208L, active4, 2199023255568L, active5, 2L, active6, 2199023779840L, active7, 0L, active8, 65536L);
            }
            case 'M':
            case 'm': {
                if ((active1 & 0x1000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 100, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 16777217L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 549755813888L, active8, 1572864L);
            }
            case 'N':
            case 'n': {
                if ((active0 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 16, 287);
                }
                if ((active1 & 0x400000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 98, 287);
                }
                if ((active5 & 0x800000000000L) != 0x0L) {
                    this.jjmatchedKind = 367;
                    this.jjmatchedPos = 6;
                }
                else if ((active7 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 464, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 8589934592L, active1, 0L, active2, 144255925564211200L, active3, 2305843009213693952L, active4, 562949953421312L, active5, 281476050452480L, active6, 24584L, active7, 33554432L, active8, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 8L, active2, 1236950745088L, active3, 2147483648L, active4, 526336L, active5, 0L, active6, 0L, active7, 43980465373184L, active8, 1L);
            }
            case 'P':
            case 'p': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 36028797018963968L, active2, 262144L, active3, 0L, active4, 0L, active5, 72057594037927936L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'R':
            case 'r': {
                if ((active2 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 161, 287);
                }
                if ((active3 & 0x10000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 232, 287);
                }
                if ((active6 & 0x20L) != 0x0L) {
                    this.jjmatchedKind = 389;
                    this.jjmatchedPos = 6;
                }
                else {
                    if ((active6 & 0x100000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 440, 287);
                    }
                    if ((active7 & 0x8L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 451, 287);
                    }
                    if ((active7 & 0x20000000L) != 0x0L) {
                        this.jjmatchedKind = 477;
                        this.jjmatchedPos = 6;
                    }
                    else {
                        if ((active7 & 0x800000000000000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(6, 507, 287);
                        }
                        if ((active8 & 0x20L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(6, 517, 287);
                        }
                    }
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 18014398509481984L, active2, 0L, active3, 33554432L, active4, 25771934720L, active5, 35184372088832L, active6, 268436480L, active7, 1073741824L, active8, 0L);
            }
            case 'S':
            case 's': {
                if ((active0 & 0x800000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 47, 287);
                }
                if ((active2 & 0x2000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 141, 287);
                }
                if ((active2 & 0x400000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 162, 287);
                }
                if ((active3 & 0x1000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 204, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 4611686018427387904L, active1, 0L, active2, 274877906944L, active3, 32L, active4, 137438953472L, active5, 70368744177664L, active6, 1610613248L, active7, 0L, active8, 0L);
            }
            case 'T':
            case 't': {
                if ((active0 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 24, 287);
                }
                if ((active1 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 97, 287);
                }
                if ((active1 & 0x4000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 126, 287);
                }
                if ((active2 & 0x800000000L) != 0x0L) {
                    this.jjmatchedKind = 163;
                    this.jjmatchedPos = 6;
                }
                else {
                    if ((active3 & 0x80000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 247, 287);
                    }
                    if ((active4 & 0x8L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 259, 287);
                    }
                    if ((active4 & 0x2000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 269, 287);
                    }
                    if ((active4 & 0x40000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 274, 287);
                    }
                    if ((active4 & 0x100000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 300, 287);
                    }
                    if ((active4 & 0x20000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 309, 287);
                    }
                    if ((active4 & 0x1000000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 316, 287);
                    }
                    if ((active4 & 0x4000000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 318, 287);
                    }
                    if ((active7 & 0x4000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(6, 474, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 4294967296L, active1, 144150372448468992L, active2, 88510686625792L, active3, 72059793329619200L, active4, 576461851815051776L, active5, 8796093022208L, active6, 576478344623685892L, active7, 36028799166447616L, active8, 0L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 268435456L, active5, 0L, active6, 5629499534213120L, active7, 0L, active8, 0L);
            }
            case 'V':
            case 'v': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 2147483648L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'W':
            case 'w': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 360639813910528L, active2, 0L, active3, 0L, active4, 3355443200L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'X':
            case 'x': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 2097152L, active4, 0L, active5, 0L, active6, 2L, active7, 90071992547409920L, active8, 0L);
            }
            case 'Y':
            case 'y': {
                if ((active5 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(6, 364, 287);
                }
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 0L, active3, 8388608L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'Z':
            case 'z': {
                return this.jjMoveStringLiteralDfa7_0(active0, 0L, active1, 0L, active2, 2097152L, active3, 0L, active4, 0L, active5, 536870912L, active6, 0L, active7, 0L, active8, 0L);
            }
            default: {
                return this.jjStartNfa_0(5, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa7_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(5, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(6, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 7;
        }
        switch (this.curChar) {
            case '2': {
                if ((active7 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 478, 9);
                }
                break;
            }
            case '_': {
                return this.jjMoveStringLiteralDfa8_0(active0, 8589934592L, active1, 70368744177664L, active2, 0L, active3, 0L, active4, 150994944L, active5, 0L, active6, 8L, active7, 0L, active8, 0L);
            }
            case 'A':
            case 'a': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 108086391056891904L, active2, 262144L, active3, 35184372121600L, active4, 32768L, active5, 72057594574798848L, active6, 35184372088832L, active7, 0L, active8, 0L);
            }
            case 'B':
            case 'b': {
                if ((active7 & 0x80000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 491, 287);
                }
                if ((active7 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 493, 287);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 1099511627776L, active7, 0L, active8, 0L);
            }
            case 'C':
            case 'c': {
                return this.jjMoveStringLiteralDfa8_0(active0, 1152921504606846976L, active1, 0L, active2, 4194304L, active3, 68719476736L, active4, 0L, active5, 281476050468864L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'D':
            case 'd': {
                if ((active2 & 0x8L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 131, 287);
                }
                if ((active4 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 270, 287);
                }
                if ((active4 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 289, 287);
                }
                if ((active7 & 0x200000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 505, 287);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 6755399441055744L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 1L, active7, 0L, active8, 0L);
            }
            case 'E':
            case 'e': {
                if ((active0 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 32, 287);
                }
                if ((active0 & 0x4000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 62, 287);
                }
                if ((active1 & 0x1L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 64, 287);
                }
                if ((active1 & 0x10L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 68, 287);
                }
                if ((active2 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 142, 287);
                }
                if ((active2 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 149, 287);
                }
                if ((active2 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 172, 287);
                }
                if ((active3 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 200, 287);
                }
                if ((active3 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 211, 287);
                }
                if ((active3 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 233, 287);
                }
                if ((active3 & 0x80000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 235, 287);
                }
                if ((active3 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 236, 287);
                }
                if ((active4 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 265, 287);
                }
                if ((active4 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 297, 287);
                }
                if ((active5 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 351, 287);
                }
                if ((active6 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 428, 287);
                }
                if ((active6 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 432, 287);
                }
                if ((active6 & 0x4000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 434, 287);
                }
                if ((active6 & 0x10000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 436, 287);
                }
                if ((active7 & 0x8000000000L) != 0x0L) {
                    this.jjmatchedKind = 487;
                    this.jjmatchedPos = 7;
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 144678138029801472L, active2, 1152921504607436800L, active3, 0L, active4, 536870912L, active5, -9223336852482686976L, active6, 402654208L, active7, 36028799166447616L, active8, 1572864L);
            }
            case 'F':
            case 'f': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 256L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'G':
            case 'g': {
                if ((active3 & 0x2000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 253, 287);
                }
                if ((active4 & 0x2000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 305, 287);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 134217728L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 68719476736L, active7, 0L, active8, 0L);
            }
            case 'H':
            case 'h': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 1152921504606846976L, active8, 0L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa8_0(active0, 8388608L, active1, 0L, active2, 70643622084608L, active3, 72057594071482370L, active4, 2098176L, active5, 0L, active6, 864691128455135494L, active7, 1L, active8, 0L);
            }
            case 'K':
            case 'k': {
                if ((active4 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 258, 287);
                }
                if ((active5 & 0x1L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 320, 287);
                }
                break;
            }
            case 'L':
            case 'l': {
                if ((active6 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 402, 287);
                }
                if ((active6 & 0x400000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 430, 287);
                }
                if ((active7 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 475, 287);
                }
                if ((active8 & 0x40L) != 0x0L) {
                    this.jjmatchedKind = 518;
                    this.jjmatchedPos = 7;
                }
                else if ((active8 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 528, 287);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 16384L, active2, 137440002208L, active3, 1073741824L, active4, 33554432L, active5, 0L, active6, 11261198092206080L, active7, -4611668426241343488L, active8, 262144L);
            }
            case 'M':
            case 'm': {
                if ((active4 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 284, 287);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 16777216L, active2, 4398046511104L, active3, 0L, active4, 281492156579840L, active5, 0L, active6, 0L, active7, 0L, active8, 16385L);
            }
            case 'N':
            case 'n': {
                if ((active2 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 143, 287);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 7495398254339686400L, active3, 2251799813685248L, active4, 0L, active5, 18014398509481984L, active6, 563224931991552L, active7, 17039360L, active8, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 18014398509481984L, active2, 35253091565568L, active3, 633336145903617L, active4, 1099520016384L, active5, 2L, active6, 0L, active7, 4194304L, active8, 0L);
            }
            case 'P':
            case 'p': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 17592186044416L, active2, 0L, active3, 8388608L, active4, 0L, active5, 0L, active6, 558345748480L, active7, 0L, active8, 0L);
            }
            case 'R':
            case 'r': {
                if ((active4 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 275, 287);
                }
                if ((active7 & 0x200000000L) != 0x0L) {
                    this.jjmatchedKind = 481;
                    this.jjmatchedPos = 7;
                }
                else if ((active8 & 0x8L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 515, 287);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 0L, active2, 131584L, active3, 0L, active4, 0L, active5, 128L, active6, 0L, active7, 281492156579840L, active8, 0L);
            }
            case 'S':
            case 's': {
                if ((active0 & 0x40000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 54, 287);
                }
                if ((active1 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 112, 287);
                }
                if ((active1 & 0x4000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 114, 287);
                }
                if ((active2 & 0x200000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 185, 287);
                }
                if ((active4 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 286;
                    this.jjmatchedPos = 7;
                }
                else {
                    if ((active4 & 0x80000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(7, 287, 287);
                    }
                    if ((active4 & 0x800000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(7, 315, 287);
                    }
                    if ((active6 & 0x200L) != 0x0L) {
                        this.jjmatchedKind = 393;
                        this.jjmatchedPos = 7;
                    }
                    else {
                        if ((active6 & 0x2000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(7, 397, 287);
                        }
                        if ((active6 & 0x4000L) != 0x0L) {
                            return this.jjStartNfaWithStates_0(7, 398, 287);
                        }
                    }
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 8796093022208L, active2, 562949953421312L, active3, 0L, active4, 412316860416L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'T':
            case 't': {
                if ((active0 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 27, 287);
                }
                if ((active2 & 0x800000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 175, 287);
                }
                if ((active3 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 213, 287);
                }
                if ((active4 & 0x10L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 260, 287);
                }
                if ((active4 & 0x400000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 314, 287);
                }
                if ((active5 & 0x800000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 355, 287);
                }
                if ((active6 & 0x40000000L) != 0x0L) {
                    this.jjmatchedKind = 414;
                    this.jjmatchedPos = 7;
                }
                else {
                    if ((active7 & 0x2000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(7, 473, 287);
                    }
                    if ((active7 & 0x40000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(7, 502, 287);
                    }
                    if ((active7 & 0x100000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(7, 504, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 288230378307584000L, active2, 0L, active3, 32L, active4, 67109120L, active5, 1196268651020288L, active6, 1152921505143717888L, active7, 1099511627780L, active8, 0L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 9007199254740992L, active2, 549755813888L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'W':
            case 'w': {
                if ((active3 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 223, 287);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 8L, active2, 0L, active3, 0L, active4, 2048L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'Y':
            case 'y': {
                if ((active5 & 0x80000000000L) != 0x0L) {
                    this.jjmatchedKind = 363;
                    this.jjmatchedPos = 7;
                }
                else if ((active8 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(7, 514, 287);
                }
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 35184372088832L, active2, 0L, active3, 0L, active4, 4294967296L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'Z':
            case 'z': {
                return this.jjMoveStringLiteralDfa8_0(active0, 0L, active1, 576460752303423488L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
        }
        return this.jjStartNfa_0(6, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
    }
    
    private int jjMoveStringLiteralDfa8_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(6, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(7, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 8;
        }
        switch (this.curChar) {
            case ' ': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, -4611686018427387904L, active8, 0L);
            }
            case '2': {
                if ((active7 & 0x400000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 482, 9);
                }
                if ((active8 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 531, 9);
                }
                break;
            }
            case '_': {
                return this.jjMoveStringLiteralDfa9_0(active0, 1152921504606846976L, active1, 43980465111040L, active2, 0L, active3, 0L, active4, 67108864L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'A':
            case 'a': {
                if ((active2 & 0x40000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 170, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 578730144303153152L, active2, 1099511627776L, active3, 0L, active4, 17179869184L, active5, 0L, active6, 1152921504875282432L, active7, 1152921504606846976L, active8, 0L);
            }
            case 'C':
            case 'c': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 262144L, active3, 0L, active4, 0L, active5, 72057594037927938L, active6, 260L, active7, 0L, active8, 0L);
            }
            case 'D':
            case 'd': {
                if ((active1 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 83, 287);
                }
                if ((active1 & 0x200000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 121, 287);
                }
                if ((active2 & 0x10000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 144, 287);
                }
                if ((active2 & 0x1000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 188, 287);
                }
                if ((active5 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 365, 287);
                }
                if ((active6 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 411, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 512L, active3, 0L, active4, 0L, active5, 0L, active6, 274877906944L, active7, 0L, active8, 0L);
            }
            case 'E':
            case 'e': {
                if ((active2 & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 133, 287);
                }
                if ((active2 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 135, 287);
                }
                if ((active3 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 215, 287);
                }
                if ((active4 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 264, 287);
                }
                if ((active7 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 450, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 2281701376L, active2, 0L, active3, 34L, active4, 137438953472L, active5, 351843720888320L, active6, 618475814912L, active7, 1099511889920L, active8, 1L);
            }
            case 'G':
            case 'g': {
                if ((active2 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 176, 287);
                }
                if ((active2 & 0x4000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 178, 287);
                }
                if ((active2 & 0x2000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 189, 287);
                }
                if ((active2 & 0x4000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 190, 287);
                }
                if ((active3 & 0x8000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 243, 287);
                }
                if ((active5 & 0x40000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 374, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 36028797018963968L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'H':
            case 'h': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 562949957615616L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 25182208L, active2, 256L, active3, 0L, active4, 2048L, active5, 0L, active6, 0L, active7, 0L, active8, 262144L);
            }
            case 'K':
            case 'k': {
                return this.jjMoveStringLiteralDfa9_0(active0, 8589934592L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'L':
            case 'l': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 1048576L, active3, 0L, active4, 167772160L, active5, 0L, active6, 1099511627776L, active7, 0L, active8, 0L);
            }
            case 'M':
            case 'm': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 1024L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'N':
            case 'n': {
                if ((active2 & 0x1000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 164, 287);
                }
                if ((active3 & 0x1L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 192, 287);
                }
                if ((active3 & 0x2000000000000L) != 0x0L) {
                    this.jjmatchedKind = 241;
                    this.jjmatchedPos = 8;
                }
                else if ((active7 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 470, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 8388608L, active1, 0L, active2, 35184372088832L, active3, 70385957601280L, active4, 32768L, active5, 0L, active6, 288230376151711744L, active7, 1L, active8, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 274877906944L, active3, 69793218560L, active4, 2097152L, active5, 1125899906842624L, active6, 576460760893358080L, active7, 17592186044416L, active8, 1048576L);
            }
            case 'P':
            case 'p': {
                if ((active4 & 0x1000000000000L) != 0x0L) {
                    this.jjmatchedKind = 304;
                    this.jjmatchedPos = 8;
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 9007199254740992L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 16384L);
            }
            case 'R':
            case 'r': {
                if ((active4 & 0x10000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 296, 287);
                }
                if ((active7 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 479, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 524288L, active3, 268435456L, active4, 25165824L, active5, 1073741824L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'S':
            case 's': {
                if ((active1 & 0x2000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 113, 287);
                }
                if ((active2 & 0x2000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 165, 287);
                }
                if ((active4 & 0x100000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 288, 287);
                }
                if ((active6 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 413, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 70368744177664L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 1026L, active7, 0L, active8, 0L);
            }
            case 'T':
            case 't': {
                if ((active2 & 0x800000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 187, 287);
                }
                if ((active5 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 334, 287);
                }
                if ((active6 & 0x2000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 433, 287);
                }
                if ((active7 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 472, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 360287970189639688L, active2, 70918499991552L, active3, 72092778410049536L, active4, 274877906944L, active5, 536870912L, active6, 35184472752136L, active7, 0L, active8, 0L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 4503599627370496L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 11258999068426241L, active7, 0L, active8, 0L);
            }
            case 'W':
            case 'w': {
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 18014398509481984L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'X':
            case 'x': {
                if ((active5 & Long.MIN_VALUE) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 383, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 36028797018963968L, active8, 0L);
            }
            case 'Y':
            case 'y': {
                if ((active2 & 0x20000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 145, 287);
                }
                if ((active5 & 0x80L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 327, 287);
                }
                if ((active6 & 0x20000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 425, 287);
                }
                if ((active7 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(8, 496, 287);
                }
                return this.jjMoveStringLiteralDfa9_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 536870912L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
        }
        return this.jjStartNfa_0(7, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
    }
    
    private int jjMoveStringLiteralDfa9_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(7, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(8, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 9;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 27021597764222976L, active2, 0L, active3, 0L, active4, 570425344L, active5, 0L, active6, 68719476736L, active7, 0L, active8, 0L);
            }
            case 'A':
            case 'a': {
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 549755813888L, active3, 0L, active4, 274877907968L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'B':
            case 'b': {
                if ((active7 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 492, 287);
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 576460752303423488L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'D':
            case 'd': {
                if ((active1 & 0x80000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 95, 287);
                }
                if ((active3 & 0x2L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 193, 287);
                }
                if ((active4 & 0x800L) != 0x0L) {
                    return this.jjStopAtPos(9, 267);
                }
                if ((active4 & 0x2000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 293, 287);
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 1441151880758558720L, active7, Long.MIN_VALUE, active8, 0L);
            }
            case 'E':
            case 'e': {
                if ((active2 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 146, 287);
                }
                if ((active2 & 0x400000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 150, 287);
                }
                if ((active3 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 207, 287);
                }
                if ((active3 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 237, 287);
                }
                if ((active5 & 0x100000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 376, 287);
                }
                if ((active6 & 0x10000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 424, 287);
                }
                if ((active6 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 429, 287);
                }
                if ((active6 & 0x8000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 435, 287);
                }
                if ((active6 & 0x20000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 437, 287);
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 8589934592L, active1, 324259173170675712L, active2, 0L, active3, 0L, active4, 150994944L, active5, 1073741824L, active6, 274877906952L, active7, 0L, active8, 0L);
            }
            case 'F':
            case 'f': {
                return this.jjMoveStringLiteralDfa10_0(active0, 1152921504606846976L, active1, 0L, active2, 512L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 1048576L);
            }
            case 'G':
            case 'g': {
                if ((active3 & 0x2000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 217, 287);
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 52776558133248L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'H':
            case 'h': {
                if ((active1 & 0x8L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 67, 287);
                }
                break;
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 72127962782105600L, active2, 70368744177664L, active3, 72057611486232576L, active4, 8388608L, active5, 536870912L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'J':
            case 'j': {
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 67108864L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'K':
            case 'k': {
                if ((active4 & 0x8000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 271, 287);
                }
                if ((active5 & 0x2L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 321, 287);
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 262144L);
            }
            case 'L':
            case 'l': {
                if ((active2 & 0x10000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 168, 287);
                }
                if ((active3 & 0x1000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 228, 287);
                }
                if ((active6 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 403, 287);
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 256L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'N':
            case 'n': {
                if ((active2 & 0x4000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 166, 287);
                }
                if ((active6 & 0x800000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 443, 287);
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 549755813888L, active7, 0L, active8, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 8388608L, active2, 562949953421312L, active3, 0L, active4, 0L, active5, 0L, active6, 8589934592L, active7, 0L, active8, 0L);
            }
            case 'P':
            case 'p': {
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 4512395720392704L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 1L, active7, 0L, active8, 0L);
            }
            case 'R':
            case 'r': {
                if ((active3 & 0x20L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 197, 287);
                }
                if ((active5 & 0x4000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 370, 287);
                }
                if ((active7 & 0x1000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 508, 287);
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 2097152L, active5, 70368744177664L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'S':
            case 's': {
                if ((active1 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 91, 287);
                }
                if ((active2 & 0x80000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 147, 287);
                }
                if ((active2 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 173, 287);
                }
                if ((active3 & 0x400000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 238, 287);
                }
                if ((active5 & 0x1000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 368, 287);
                }
                if ((active6 & 0x100L) != 0x0L) {
                    this.jjmatchedKind = 392;
                    this.jjmatchedPos = 9;
                }
                else {
                    if ((active6 & 0x400L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(9, 394, 287);
                    }
                    if ((active6 & 0x2000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(9, 409, 287);
                    }
                    if ((active6 & 0x4000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(9, 410, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 268435460L, active7, 0L, active8, 0L);
            }
            case 'T':
            case 't': {
                if ((active0 & 0x800000L) != 0x0L) {
                    this.jjmatchedKind = 23;
                    this.jjmatchedPos = 9;
                }
                else {
                    if ((active1 & 0x1000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(9, 88, 287);
                    }
                    if ((active4 & 0x400000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(9, 290, 287);
                    }
                    if ((active7 & 0x80000000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(9, 503, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 2251799813685248L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 2L, active7, 1099511627777L, active8, 16385L);
            }
            case 'W':
            case 'w': {
                if ((active3 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 222, 287);
                }
                break;
            }
            case 'Y':
            case 'y': {
                if ((active2 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 148, 287);
                }
                if ((active7 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(9, 466, 287);
                }
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 4611686018427387904L, active8, 0L);
            }
            case 'Z':
            case 'z': {
                return this.jjMoveStringLiteralDfa10_0(active0, 0L, active1, 16384L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
        }
        return this.jjStartNfa_0(8, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
    }
    
    private int jjMoveStringLiteralDfa10_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(8, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(9, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 10;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 40532396646334464L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 5L, active7, 0L, active8, 0L);
            }
            case 'A':
            case 'a': {
                if ((active1 & 0x8000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 115, 287);
                }
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 35184372088832L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 1152921504606846976L, active7, Long.MIN_VALUE, active8, 0L);
            }
            case 'B':
            case 'b': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 549755813888L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'D':
            case 'd': {
                if ((active1 & 0x400000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 122, 287);
                }
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 549755813888L, active7, 0L, active8, 0L);
            }
            case 'E':
            case 'e': {
                if ((active2 & 0x100L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 136, 287);
                }
                if ((active6 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 412, 287);
                }
                if ((active8 & 0x40000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 530, 287);
                }
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 26388279083008L, active2, 0L, active3, 0L, active4, 0L, active5, 70368744177664L, active6, 0L, active7, 4611686018427387904L, active8, 0L);
            }
            case 'F':
            case 'f': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 1048576L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 512L, active3, 0L, active4, 2097152L, active5, 0L, active6, 288230376151711746L, active7, 1099511627776L, active8, 0L);
            }
            case 'L':
            case 'l': {
                if ((active6 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 417, 287);
                }
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 594475150812905472L, active2, 562949953421312L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'M':
            case 'm': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 1073741824L, active6, 8L, active7, 0L, active8, 0L);
            }
            case 'N':
            case 'n': {
                if ((active1 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 87, 287);
                }
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 17448304640L, active4, 134217728L, active5, 0L, active6, 274877906944L, active7, 0L, active8, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa11_0(active0, 1152921504606846976L, active1, 72057594037927936L, active2, 70368744177664L, active3, 72057594037927936L, active4, 67108864L, active5, 536870912L, active6, 68719476736L, active7, 0L, active8, 0L);
            }
            case 'R':
            case 'r': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 9007199254740992L, active2, 0L, active3, 0L, active4, 33555456L, active5, 0L, active6, 0L, active7, 0L, active8, 1L);
            }
            case 'S':
            case 's': {
                if ((active7 & 0x1L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 448, 287);
                }
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 16777216L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'T':
            case 't': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 274886295552L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'W':
            case 'w': {
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 536870912L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'Y':
            case 'y': {
                return this.jjMoveStringLiteralDfa11_0(active0, 8589934592L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'Z':
            case 'z': {
                if ((active8 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(10, 526, 287);
                }
                return this.jjMoveStringLiteralDfa11_0(active0, 0L, active1, 70368744177664L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            default: {
                return this.jjStartNfa_0(9, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa11_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(9, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(10, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
            return 11;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa12_0(active0, 8589934592L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'A':
            case 'a': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 4611686018427387904L, active8, 0L);
            }
            case 'C':
            case 'c': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 274877906944L, active7, 0L, active8, 0L);
            }
            case 'D':
            case 'd': {
                if ((active1 & 0x4000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 78, 287);
                }
                if ((active2 & 0x2000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 177, 287);
                }
                if ((active5 & 0x400000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 366, 287);
                }
                break;
            }
            case 'E':
            case 'e': {
                if ((active1 & 0x400000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 110, 287);
                }
                if ((active1 & 0x800000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 123, 287);
                }
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 274911461376L, active5, 1073741824L, active6, 549755813888L, active7, 0L, active8, 0L);
            }
            case 'G':
            case 'g': {
                if ((active3 & 0x10000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 220, 287);
                }
                if ((active3 & 0x400000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 226, 287);
                }
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 17592186044416L, active2, 0L, active3, 0L, active4, 134217728L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 67108864L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'K':
            case 'k': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 1L, active7, 0L, active8, 0L);
            }
            case 'L':
            case 'l': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 36028797018963968L, active2, 549755814400L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'M':
            case 'm': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 1099511627776L, active8, 0L);
            }
            case 'N':
            case 'n': {
                if ((active1 & 0x100000000000000L) != 0x0L) {
                    return this.jjStopAtPos(11, 120);
                }
                if ((active3 & 0x100000000000000L) != 0x0L) {
                    this.jjmatchedKind = 248;
                    this.jjmatchedPos = 11;
                }
                else {
                    if ((active5 & 0x20000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(11, 349, 287);
                    }
                    if ((active6 & 0x1000000000L) != 0x0L) {
                        return this.jjStartNfaWithStates_0(11, 420, 287);
                    }
                }
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 70368744177664L, active3, 0L, active4, 0L, active5, 0L, active6, 288230376151711750L, active7, 0L, active8, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 27021597764222976L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'P':
            case 'p': {
                if ((active1 & 0x200000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 109, 287);
                }
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 8L, active7, 0L, active8, 0L);
            }
            case 'R':
            case 'r': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 4512395720392704L, active2, 0L, active3, 0L, active4, 536870912L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'S':
            case 's': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 1048576L);
            }
            case 'T':
            case 't': {
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 2097152L, active5, 0L, active6, 1152921504606846976L, active7, 0L, active8, 0L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa12_0(active0, 1152921504606846976L, active1, 0L, active2, 0L, active3, 0L, active4, 16777216L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'Y':
            case 'y': {
                if ((active4 & 0x800000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 279, 287);
                }
                if ((active8 & 0x1L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(11, 512, 287);
                }
                return this.jjMoveStringLiteralDfa12_0(active0, 0L, active1, 0L, active2, 0L, active3, 0L, active4, 1024L, active5, 0L, active6, 0L, active7, Long.MIN_VALUE, active8, 0L);
            }
        }
        return this.jjStartNfa_0(10, active0, active1, active2, active3, active4, active5, active6, active7, active8, 0L);
    }
    
    private int jjMoveStringLiteralDfa12_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old3, long active3, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active3 &= old3) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(10, old0, old1, old2, old3, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(11, active0, active1, active2, 0L, active4, active5, active6, active7, active8, 0L);
            return 12;
        }
        switch (this.curChar) {
            case ' ': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 1024L, active5, 0L, active6, 0L, active7, Long.MIN_VALUE, active8, 0L);
            }
            case '_': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 8796093022208L, active2, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'A':
            case 'a': {
                if ((active6 & 0x1000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 444, 287);
                }
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 17592186044416L, active2, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'C':
            case 'c': {
                return this.jjMoveStringLiteralDfa13_0(active0, 8589934592L, active1, 18014398509481984L, active2, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'D':
            case 'd': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 0L, active5, 0L, active6, 8L, active7, 0L, active8, 0L);
            }
            case 'E':
            case 'e': {
                if ((active2 & 0x200L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 137, 287);
                }
                if ((active2 & 0x8000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 167, 287);
                }
                if ((active7 & 0x10000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 488, 287);
                }
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 0L, active5, 0L, active6, 1L, active7, 0L, active8, 1048576L);
            }
            case 'G':
            case 'g': {
                if ((active6 & 0x2L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 385, 287);
                }
                if ((active6 & 0x400000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 442, 287);
                }
                break;
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 536870912L, active5, 0L, active6, 274877906944L, active7, 0L, active8, 0L);
            }
            case 'L':
            case 'l': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 16777216L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'M':
            case 'm': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 274877906944L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'N':
            case 'n': {
                if ((active4 & 0x4000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 282, 287);
                }
                return this.jjMoveStringLiteralDfa13_0(active0, 1152921504606846976L, active1, 0L, active2, 0L, active4, 0L, active5, 1073741824L, active6, 549755813888L, active7, 0L, active8, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 40532396646334464L, active2, 0L, active4, 0L, active5, 0L, active6, 4L, active7, 0L, active8, 0L);
            }
            case 'R':
            case 'r': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 4611686018427387904L, active8, 0L);
            }
            case 'S':
            case 's': {
                if ((active2 & 0x400000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 174, 287);
                }
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 33554432L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'T':
            case 't': {
                return this.jjMoveStringLiteralDfa13_0(active0, 0L, active1, 0L, active2, 0L, active4, 134217728L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'W':
            case 'w': {
                if ((active1 & 0x20000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 117, 287);
                }
                break;
            }
            case 'Y':
            case 'y': {
                if ((active4 & 0x200000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(12, 277, 287);
                }
                break;
            }
        }
        return this.jjStartNfa_0(11, active0, active1, active2, 0L, active4, active5, active6, active7, active8, 0L);
    }
    
    private int jjMoveStringLiteralDfa13_0(final long old0, long active0, final long old1, long active1, final long old2, long active2, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active2 &= old2) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(11, old0, old1, old2, 0L, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(12, active0, active1, 0L, 0L, active4, active5, active6, active7, active8, 0L);
            return 13;
        }
        switch (this.curChar) {
            case ' ': {
                return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 4611686018427387904L, active8, 0L);
            }
            case 'B':
            case 'b': {
                if ((active6 & 0x8L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(13, 387, 287);
                }
                break;
            }
            case 'C':
            case 'c': {
                return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 36028797018963968L, active4, 0L, active5, 0L, active6, 549755813888L, active7, 0L, active8, 0L);
            }
            case 'D':
            case 'd': {
                return this.jjMoveStringLiteralDfa14_0(active0, 1152921504606846976L, active1, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'E':
            case 'e': {
                return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 274877906944L, active5, 0L, active6, 274877906944L, active7, 0L, active8, 0L);
            }
            case 'H':
            case 'h': {
                if ((active4 & 0x8000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(13, 283, 287);
                }
                return this.jjMoveStringLiteralDfa14_0(active0, 8589934592L, active1, 0L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'K':
            case 'k': {
                return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 18014398509481984L, active4, 1024L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'P':
            case 'p': {
                if ((active1 & 0x100000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(13, 108, 287);
                }
                return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 8796093022208L, active4, 0L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'R':
            case 'r': {
                return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 0L, active5, 0L, active6, 4L, active7, 0L, active8, 0L);
            }
            case 'T':
            case 't': {
                if ((active4 & 0x1000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(13, 280, 287);
                }
                if ((active5 & 0x40000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(13, 350, 287);
                }
                if ((active8 & 0x100000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(13, 532, 287);
                }
                return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 536870912L, active5, 0L, active6, 0L, active7, Long.MIN_VALUE, active8, 0L);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa14_0(active0, 0L, active1, 0L, active4, 33554432L, active5, 0L, active6, 0L, active7, 0L, active8, 0L);
            }
            case 'W':
            case 'w': {
                if ((active1 & 0x10000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(13, 116, 287);
                }
                break;
            }
            case 'Y':
            case 'y': {
                if ((active6 & 0x1L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(13, 384, 287);
                }
                break;
            }
        }
        return this.jjStartNfa_0(12, active0, active1, 0L, 0L, active4, active5, active6, active7, active8, 0L);
    }
    
    private int jjMoveStringLiteralDfa14_0(final long old0, long active0, final long old1, long active1, final long old4, long active4, final long old5, long active5, final long old6, long active6, final long old7, long active7, final long old8, long active8) {
        if (((active0 &= old0) | (active1 &= old1) | (active4 &= old4) | (active5 &= old5) | (active6 &= old6) | (active7 &= old7) | (active8 &= old8)) == 0x0L) {
            return this.jjStartNfa_0(12, old0, old1, 0L, 0L, old4, old5, old6, old7, old8, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(13, active0, active1, 0L, 0L, active4, 0L, active6, active7, 0L, 0L);
            return 14;
        }
        switch (this.curChar) {
            case '_': {
                return this.jjMoveStringLiteralDfa15_0(active0, 1152921504606846976L, active1, 0L, active4, 0L, active6, 0L, active7, 0L);
            }
            case 'A':
            case 'a': {
                return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 8796093022208L, active4, 0L, active6, 0L, active7, 0L);
            }
            case 'E':
            case 'e': {
                if ((active4 & 0x20000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(14, 285, 287);
                }
                return this.jjMoveStringLiteralDfa15_0(active0, 8589934592L, active1, 0L, active4, 1024L, active6, 4L, active7, 0L);
            }
            case 'I':
            case 'i': {
                return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 0L, active6, 549755813888L, active7, 0L);
            }
            case 'K':
            case 'k': {
                return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 36028797018963968L, active4, 0L, active6, 0L, active7, 0L);
            }
            case 'L':
            case 'l': {
                return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 33554432L, active6, 0L, active7, 0L);
            }
            case 'N':
            case 'n': {
                return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 274877906944L, active6, 0L, active7, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 0L, active6, 0L, active7, Long.MIN_VALUE);
            }
            case 'S':
            case 's': {
                if ((active1 & 0x40000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(14, 118, 287);
                }
                if ((active6 & 0x4000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(14, 422, 287);
                }
                break;
            }
            case 'T':
            case 't': {
                return this.jjMoveStringLiteralDfa15_0(active0, 0L, active1, 0L, active4, 0L, active6, 0L, active7, 4611686018427387904L);
            }
        }
        return this.jjStartNfa_0(13, active0, active1, 0L, 0L, active4, 0L, active6, active7, 0L, 0L);
    }
    
    private int jjMoveStringLiteralDfa15_0(final long old0, long active0, final long old1, long active1, final long old4, long active4, final long old6, long active6, final long old7, long active7) {
        if (((active0 &= old0) | (active1 &= old1) | (active4 &= old4) | (active6 &= old6) | (active7 &= old7)) == 0x0L) {
            return this.jjStartNfa_0(13, old0, old1, 0L, 0L, old4, 0L, old6, old7, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(14, active0, active1, 0L, 0L, active4, 0L, active6, active7, 0L, 0L);
            return 15;
        }
        switch (this.curChar) {
            case ' ': {
                return this.jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0L, active4, 0L, active6, 0L, active7, Long.MIN_VALUE);
            }
            case 'C':
            case 'c': {
                return this.jjMoveStringLiteralDfa16_0(active0, 8589934592L, active1, 0L, active4, 0L, active6, 4L, active7, 0L);
            }
            case 'E':
            case 'e': {
                return this.jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0L, active4, 0L, active6, 549755813888L, active7, 0L);
            }
            case 'G':
            case 'g': {
                return this.jjMoveStringLiteralDfa16_0(active0, 0L, active1, 8796093022208L, active4, 0L, active6, 0L, active7, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa16_0(active0, 0L, active1, 0L, active4, 0L, active6, 0L, active7, 4611686018427387904L);
            }
            case 'R':
            case 'r': {
                return this.jjMoveStringLiteralDfa16_0(active0, 1152921504606846976L, active1, 0L, active4, 0L, active6, 0L, active7, 0L);
            }
            case 'S':
            case 's': {
                if ((active1 & 0x80000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(15, 119, 287);
                }
                break;
            }
            case 'T':
            case 't': {
                if ((active4 & 0x2000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(15, 281, 287);
                }
                if ((active4 & 0x4000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(15, 294, 287);
                }
                break;
            }
            case 'Y':
            case 'y': {
                if ((active4 & 0x400L) != 0x0L) {
                    return this.jjStopAtPos(15, 266);
                }
                break;
            }
        }
        return this.jjStartNfa_0(14, active0, active1, 0L, 0L, active4, 0L, active6, active7, 0L, 0L);
    }
    
    private int jjMoveStringLiteralDfa16_0(final long old0, long active0, final long old1, long active1, final long old4, long active4, final long old6, long active6, final long old7, long active7) {
        if (((active0 &= old0) | (active1 &= old1) | (active4 &= old4) | (active6 &= old6) | (active7 &= old7)) == 0x0L) {
            return this.jjStartNfa_0(14, old0, old1, 0L, 0L, old4, 0L, old6, old7, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(15, active0, active1, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
            return 16;
        }
        switch (this.curChar) {
            case ' ': {
                return this.jjMoveStringLiteralDfa17_0(active0, 0L, active1, 0L, active6, 0L, active7, 4611686018427387904L);
            }
            case 'E':
            case 'e': {
                if ((active1 & 0x80000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(16, 107, 287);
                }
                break;
            }
            case 'K':
            case 'k': {
                return this.jjMoveStringLiteralDfa17_0(active0, 8589934592L, active1, 0L, active6, 0L, active7, 0L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa17_0(active0, 1152921504606846976L, active1, 0L, active6, 4L, active7, 0L);
            }
            case 'S':
            case 's': {
                if ((active6 & 0x8000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(16, 423, 287);
                }
                return this.jjMoveStringLiteralDfa17_0(active0, 0L, active1, 0L, active6, 0L, active7, Long.MIN_VALUE);
            }
        }
        return this.jjStartNfa_0(15, active0, active1, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
    }
    
    private int jjMoveStringLiteralDfa17_0(final long old0, long active0, final long old1, long active1, final long old6, long active6, final long old7, long active7) {
        if (((active0 &= old0) | (active1 &= old1) | (active6 &= old6) | (active7 &= old7)) == 0x0L) {
            return this.jjStartNfa_0(15, old0, old1, 0L, 0L, 0L, 0L, old6, old7, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(16, active0, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
            return 17;
        }
        switch (this.curChar) {
            case 'E':
            case 'e': {
                return this.jjMoveStringLiteralDfa18_0(active0, 0L, active6, 0L, active7, Long.MIN_VALUE);
            }
            case 'M':
            case 'm': {
                return this.jjMoveStringLiteralDfa18_0(active0, 0L, active6, 4L, active7, 4611686018427387904L);
            }
            case 'S':
            case 's': {
                if ((active0 & 0x200000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(17, 33, 287);
                }
                break;
            }
            case 'W':
            case 'w': {
                return this.jjMoveStringLiteralDfa18_0(active0, 1152921504606846976L, active6, 0L, active7, 0L);
            }
        }
        return this.jjStartNfa_0(16, active0, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
    }
    
    private int jjMoveStringLiteralDfa18_0(final long old0, long active0, final long old6, long active6, final long old7, long active7) {
        if (((active0 &= old0) | (active6 &= old6) | (active7 &= old7)) == 0x0L) {
            return this.jjStartNfa_0(16, old0, 0L, 0L, 0L, 0L, 0L, old6, old7, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(17, active0, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
            return 18;
        }
        switch (this.curChar) {
            case 'C':
            case 'c': {
                return this.jjMoveStringLiteralDfa19_0(active0, 0L, active6, 0L, active7, Long.MIN_VALUE);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa19_0(active0, 0L, active6, 0L, active7, 4611686018427387904L);
            }
            case 'P':
            case 'p': {
                return this.jjMoveStringLiteralDfa19_0(active0, 0L, active6, 4L, active7, 0L);
            }
            case 'S':
            case 's': {
                if ((active0 & 0x1000000000000000L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(18, 60, 287);
                }
                break;
            }
        }
        return this.jjStartNfa_0(17, active0, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
    }
    
    private int jjMoveStringLiteralDfa19_0(final long old0, long active0, final long old6, long active6, final long old7, long active7) {
        active0 &= old0;
        if ((active0 | (active6 &= old6) | (active7 &= old7)) == 0x0L) {
            return this.jjStartNfa_0(17, old0, 0L, 0L, 0L, 0L, 0L, old6, old7, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(18, 0L, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
            return 19;
        }
        switch (this.curChar) {
            case 'N':
            case 'n': {
                return this.jjMoveStringLiteralDfa20_0(active6, 0L, active7, 4611686018427387904L);
            }
            case 'O':
            case 'o': {
                return this.jjMoveStringLiteralDfa20_0(active6, 0L, active7, Long.MIN_VALUE);
            }
            case 'U':
            case 'u': {
                return this.jjMoveStringLiteralDfa20_0(active6, 4L, active7, 0L);
            }
            default: {
                return this.jjStartNfa_0(18, 0L, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa20_0(final long old6, long active6, final long old7, long active7) {
        if (((active6 &= old6) | (active7 &= old7)) == 0x0L) {
            return this.jjStartNfa_0(18, 0L, 0L, 0L, 0L, 0L, 0L, old6, old7, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(19, 0L, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
            return 20;
        }
        switch (this.curChar) {
            case 'N':
            case 'n': {
                return this.jjMoveStringLiteralDfa21_0(active6, 0L, active7, Long.MIN_VALUE);
            }
            case 'T':
            case 't': {
                return this.jjMoveStringLiteralDfa21_0(active6, 4L, active7, 4611686018427387904L);
            }
            default: {
                return this.jjStartNfa_0(19, 0L, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
            }
        }
    }
    
    private int jjMoveStringLiteralDfa21_0(final long old6, long active6, final long old7, long active7) {
        if (((active6 &= old6) | (active7 &= old7)) == 0x0L) {
            return this.jjStartNfa_0(19, 0L, 0L, 0L, 0L, 0L, 0L, old6, old7, 0L, 0L);
        }
        try {
            this.curChar = this.input_stream.readChar();
        }
        catch (final IOException e) {
            this.jjStopStringLiteralDfa_0(20, 0L, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
            return 21;
        }
        switch (this.curChar) {
            case 'D':
            case 'd': {
                if ((active7 & Long.MIN_VALUE) != 0x0L) {
                    return this.jjStopAtPos(21, 511);
                }
                break;
            }
            case 'E':
            case 'e': {
                if ((active6 & 0x4L) != 0x0L) {
                    return this.jjStartNfaWithStates_0(21, 386, 287);
                }
                break;
            }
            case 'H':
            case 'h': {
                if ((active7 & 0x4000000000000000L) != 0x0L) {
                    return this.jjStopAtPos(21, 510);
                }
                break;
            }
        }
        return this.jjStartNfa_0(20, 0L, 0L, 0L, 0L, 0L, 0L, active6, active7, 0L, 0L);
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
        this.jjnewStateCnt = 286;
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
                        case 14: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 10);
                            }
                            else if ((0x1800000000L & l) != 0x0L) {
                                if (kind > 539) {
                                    kind = 539;
                                }
                                this.jjCheckNAddStates(11, 13);
                            }
                            else if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(256, 258);
                            }
                            else if ((0x8000004000000000L & l) != 0x0L) {
                                if (kind > 539) {
                                    kind = 539;
                                }
                                this.jjCheckNAdd(17);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(15, 16);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 541) {
                                    kind = 541;
                                }
                                this.jjCheckNAddStates(14, 21);
                            }
                            else if ((0x1800000000L & l) != 0x0L) {
                                if (kind > 538) {
                                    kind = 538;
                                }
                                this.jjCheckNAddStates(22, 24);
                            }
                            else if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(230, 232);
                            }
                            else if (this.curChar == '&') {
                                this.jjCheckNAdd(32);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(128, 129);
                            }
                            else if (this.curChar == '?') {
                                this.jjCheckNAdd(30);
                            }
                            if (this.curChar == '$') {
                                this.jjAddStates(25, 28);
                            }
                            else if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            else if ((0x8000004000000000L & l) != 0x0L) {
                                if (kind > 538) {
                                    kind = 538;
                                }
                                this.jjCheckNAdd(130);
                            }
                            if (this.curChar == '$') {
                                this.jjAddStates(29, 32);
                                continue;
                            }
                            if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 149;
                                continue;
                            }
                            if (this.curChar == '&') {
                                this.jjCheckNAdd(146);
                                continue;
                            }
                            if (this.curChar == '?') {
                                this.jjCheckNAdd(144);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 548) {
                                    kind = 548;
                                }
                                this.jjCheckNAddStates(33, 43);
                            }
                            else if ((0x1800000000L & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAddTwoStates(8, 9);
                            }
                            else if (this.curChar == ':') {
                                this.jjCheckNAddStates(44, 56);
                            }
                            else if (this.curChar == '/') {
                                this.jjAddStates(57, 61);
                            }
                            else if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(64, 66);
                            }
                            else if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(43, 44);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(11, 12);
                            }
                            else if (this.curChar == '&') {
                                this.jjCheckNAdd(5);
                            }
                            else if (this.curChar == '-') {
                                this.jjstateSet[this.jjnewStateCnt++] = 0;
                            }
                            else if (this.curChar == '?') {
                                this.jjCheckNAdd(7);
                            }
                            if (this.curChar == '$') {
                                this.jjAddStates(62, 65);
                                continue;
                            }
                            if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 60;
                                continue;
                            }
                            if ((0x8000004000000000L & l) != 0x0L && kind > 535) {
                                kind = 535;
                                continue;
                            }
                            continue;
                        }
                        case 287: {
                            if ((0x3FF001800000000L & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAdd(9);
                            }
                            if ((0x1800000000L & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAddTwoStates(8, 9);
                                continue;
                            }
                            continue;
                        }
                        case 88: {
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 120;
                            }
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 111;
                            }
                            if (this.curChar == '*') {
                                this.jjCheckNAddTwoStates(105, 106);
                            }
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 96;
                            }
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 89;
                                continue;
                            }
                            continue;
                        }
                        case 289: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 548) {
                                    kind = 548;
                                }
                                this.jjCheckNAddTwoStates(66, 67);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 548) {
                                    kind = 548;
                                }
                                this.jjCheckNAdd(64);
                                continue;
                            }
                            continue;
                        }
                        case 288: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 10);
                            }
                            else if ((0x1800000000L & l) != 0x0L) {
                                if (kind > 538) {
                                    kind = 538;
                                }
                                this.jjCheckNAddStates(22, 24);
                            }
                            else if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(256, 258);
                            }
                            else if ((0x8000004000000000L & l) != 0x0L) {
                                if (kind > 538) {
                                    kind = 538;
                                }
                                this.jjCheckNAdd(130);
                            }
                            else if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(128, 129);
                            }
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                if (kind > 541) {
                                    kind = 541;
                                }
                                this.jjCheckNAddStates(14, 21);
                            }
                            else if (this.curChar == '$') {
                                this.jjAddStates(25, 28);
                            }
                            else if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(230, 232);
                            }
                            else if (this.curChar == '&') {
                                this.jjCheckNAdd(146);
                            }
                            else if (this.curChar == '?') {
                                this.jjCheckNAdd(144);
                            }
                            if (this.curChar == '$') {
                                this.jjAddStates(29, 32);
                            }
                            else if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                            }
                            if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 149;
                                continue;
                            }
                            continue;
                        }
                        case 48: {
                            if ((0x3FF001800000000L & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAdd(9);
                            }
                            else if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(49, 50);
                            }
                            if ((0x1800000000L & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAddTwoStates(8, 9);
                                continue;
                            }
                            continue;
                        }
                        case 0: {
                            if (this.curChar != '-') {
                                continue;
                            }
                            if (kind > 5) {
                                kind = 5;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 1: {
                            if ((0xFFFFFFFFFFFFDBFFL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 5) {
                                kind = 5;
                            }
                            this.jjCheckNAdd(1);
                            continue;
                        }
                        case 3: {
                            if ((0x8000004000000000L & l) != 0x0L && kind > 535) {
                                kind = 535;
                                continue;
                            }
                            continue;
                        }
                        case 4: {
                            if (this.curChar == '&') {
                                this.jjCheckNAdd(5);
                                continue;
                            }
                            continue;
                        }
                        case 5: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 535) {
                                kind = 535;
                            }
                            this.jjCheckNAdd(5);
                            continue;
                        }
                        case 6: {
                            if (this.curChar == '?') {
                                this.jjCheckNAdd(7);
                                continue;
                            }
                            continue;
                        }
                        case 7: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 535) {
                                kind = 535;
                            }
                            this.jjCheckNAdd(7);
                            continue;
                        }
                        case 8: {
                            if ((0x1800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 535) {
                                kind = 535;
                            }
                            this.jjCheckNAddTwoStates(8, 9);
                            continue;
                        }
                        case 9: {
                            if ((0x3FF001800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 535) {
                                kind = 535;
                            }
                            this.jjCheckNAdd(9);
                            continue;
                        }
                        case 10: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(11, 12);
                                continue;
                            }
                            continue;
                        }
                        case 11: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(11, 12);
                                continue;
                            }
                            continue;
                        }
                        case 12: {
                            if (this.curChar == '\"' && kind > 537) {
                                kind = 537;
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(15, 16);
                                continue;
                            }
                            continue;
                        }
                        case 16: {
                            if (this.curChar != '\"') {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAdd(17);
                            continue;
                        }
                        case 17: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(66, 70);
                                continue;
                            }
                            continue;
                        }
                        case 18: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(19, 16);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(19, 16);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if ((0x1800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddStates(71, 73);
                            continue;
                        }
                        case 21: {
                            if ((0x3FF001800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddTwoStates(17, 21);
                            continue;
                        }
                        case 22: {
                            if (this.curChar == '?') {
                                this.jjCheckNAdd(23);
                                continue;
                            }
                            continue;
                        }
                        case 23: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddTwoStates(17, 23);
                            continue;
                        }
                        case 24: {
                            if (this.curChar == '&') {
                                this.jjCheckNAdd(25);
                                continue;
                            }
                            continue;
                        }
                        case 25: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddTwoStates(17, 25);
                            continue;
                        }
                        case 26: {
                            if ((0x8000004000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAdd(17);
                            continue;
                        }
                        case 27: {
                            if ((0x1800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddStates(11, 13);
                            continue;
                        }
                        case 28: {
                            if ((0x3FF001800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddTwoStates(28, 17);
                            continue;
                        }
                        case 29: {
                            if (this.curChar == '?') {
                                this.jjCheckNAdd(30);
                                continue;
                            }
                            continue;
                        }
                        case 30: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddTwoStates(30, 17);
                            continue;
                        }
                        case 31: {
                            if (this.curChar == '&') {
                                this.jjCheckNAdd(32);
                                continue;
                            }
                            continue;
                        }
                        case 32: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddTwoStates(32, 17);
                            continue;
                        }
                        case 34: {
                            if ((0x8000004000000000L & l) != 0x0L && kind > 542) {
                                kind = 542;
                                continue;
                            }
                            continue;
                        }
                        case 35: {
                            if (this.curChar == '&') {
                                this.jjCheckNAdd(36);
                                continue;
                            }
                            continue;
                        }
                        case 36: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 542) {
                                kind = 542;
                            }
                            this.jjCheckNAdd(36);
                            continue;
                        }
                        case 37: {
                            if (this.curChar == '?') {
                                this.jjCheckNAdd(38);
                                continue;
                            }
                            continue;
                        }
                        case 38: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 542) {
                                kind = 542;
                            }
                            this.jjCheckNAdd(38);
                            continue;
                        }
                        case 39: {
                            if ((0x1800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 542) {
                                kind = 542;
                            }
                            this.jjCheckNAddTwoStates(39, 40);
                            continue;
                        }
                        case 40: {
                            if ((0x3FF001800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 542) {
                                kind = 542;
                            }
                            this.jjCheckNAdd(40);
                            continue;
                        }
                        case 42: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(43, 44);
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(43, 44);
                                continue;
                            }
                            continue;
                        }
                        case 44: {
                            if (this.curChar != '\'') {
                                continue;
                            }
                            if (kind > 543) {
                                kind = 543;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 45;
                            continue;
                        }
                        case 45: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(46, 44);
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(46, 44);
                                continue;
                            }
                            continue;
                        }
                        case 49: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(49, 50);
                                continue;
                            }
                            continue;
                        }
                        case 50: {
                            if (this.curChar != '\'') {
                                continue;
                            }
                            if (kind > 543) {
                                kind = 543;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 51;
                            continue;
                        }
                        case 51: {
                            if (this.curChar == '\'') {
                                this.jjCheckNAddTwoStates(52, 50);
                                continue;
                            }
                            continue;
                        }
                        case 52: {
                            if ((0xFFFFFF7FFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(52, 50);
                                continue;
                            }
                            continue;
                        }
                        case 59: {
                            if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 60;
                                continue;
                            }
                            continue;
                        }
                        case 61: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 61;
                            continue;
                        }
                        case 62: {
                            if (this.curChar == '$') {
                                this.jjAddStates(62, 65);
                                continue;
                            }
                            continue;
                        }
                        case 63: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(64);
                                continue;
                            }
                            continue;
                        }
                        case 64: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(64);
                            continue;
                        }
                        case 65: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(66);
                                continue;
                            }
                            continue;
                        }
                        case 66: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddTwoStates(66, 67);
                            continue;
                        }
                        case 68: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjAddStates(76, 77);
                                continue;
                            }
                            continue;
                        }
                        case 69: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(70);
                                continue;
                            }
                            continue;
                        }
                        case 70: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(70);
                            continue;
                        }
                        case 71: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddStates(78, 80);
                            continue;
                        }
                        case 72: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(72);
                            continue;
                        }
                        case 73: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddTwoStates(73, 74);
                            continue;
                        }
                        case 74: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(75);
                            continue;
                        }
                        case 75: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(75);
                            continue;
                        }
                        case 76: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddStates(81, 84);
                            continue;
                        }
                        case 77: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddTwoStates(77, 67);
                            continue;
                        }
                        case 78: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddStates(85, 87);
                            continue;
                        }
                        case 79: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddTwoStates(80, 67);
                            continue;
                        }
                        case 80: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddTwoStates(80, 67);
                            continue;
                        }
                        case 81: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddStates(88, 90);
                            continue;
                        }
                        case 82: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(82);
                            continue;
                        }
                        case 83: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddTwoStates(83, 84);
                            continue;
                        }
                        case 84: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(85);
                            continue;
                        }
                        case 85: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(85);
                            continue;
                        }
                        case 86: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(64, 66);
                                continue;
                            }
                            continue;
                        }
                        case 87: {
                            if (this.curChar == '/') {
                                this.jjAddStates(57, 61);
                                continue;
                            }
                            continue;
                        }
                        case 89: {
                            if (this.curChar == '+') {
                                this.jjCheckNAddTwoStates(90, 91);
                                continue;
                            }
                            continue;
                        }
                        case 90: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(90, 91);
                                continue;
                            }
                            continue;
                        }
                        case 91: {
                            if (this.curChar == '*') {
                                this.jjCheckNAddStates(91, 93);
                                continue;
                            }
                            continue;
                        }
                        case 92: {
                            if ((0xFFFF7BFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(93, 91);
                                continue;
                            }
                            continue;
                        }
                        case 93: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(93, 91);
                                continue;
                            }
                            continue;
                        }
                        case 94: {
                            if (this.curChar == '/' && kind > 6) {
                                kind = 6;
                                continue;
                            }
                            continue;
                        }
                        case 95: {
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 96;
                                continue;
                            }
                            continue;
                        }
                        case 96: {
                            if (this.curChar == '%') {
                                this.jjCheckNAdd(97);
                                continue;
                            }
                            continue;
                        }
                        case 97: {
                            if ((0x1800000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(97, 98);
                                continue;
                            }
                            continue;
                        }
                        case 98: {
                            if (this.curChar == '%') {
                                this.jjCheckNAddTwoStates(99, 100);
                                continue;
                            }
                            continue;
                        }
                        case 99: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(99, 100);
                                continue;
                            }
                            continue;
                        }
                        case 100: {
                            if (this.curChar == '*') {
                                this.jjCheckNAddStates(94, 96);
                                continue;
                            }
                            continue;
                        }
                        case 101: {
                            if ((0xFFFF7BFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(102, 100);
                                continue;
                            }
                            continue;
                        }
                        case 102: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(102, 100);
                                continue;
                            }
                            continue;
                        }
                        case 103: {
                            if (this.curChar == '/' && kind > 7) {
                                kind = 7;
                                continue;
                            }
                            continue;
                        }
                        case 104: {
                            if (this.curChar == '*') {
                                this.jjCheckNAddTwoStates(105, 106);
                                continue;
                            }
                            continue;
                        }
                        case 105: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(105, 106);
                                continue;
                            }
                            continue;
                        }
                        case 106: {
                            if (this.curChar == '*') {
                                this.jjCheckNAddStates(97, 99);
                                continue;
                            }
                            continue;
                        }
                        case 107: {
                            if ((0xFFFF7BFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(108, 106);
                                continue;
                            }
                            continue;
                        }
                        case 108: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(108, 106);
                                continue;
                            }
                            continue;
                        }
                        case 109: {
                            if (this.curChar == '/' && kind > 8) {
                                kind = 8;
                                continue;
                            }
                            continue;
                        }
                        case 110: {
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 111;
                                continue;
                            }
                            continue;
                        }
                        case 111: {
                            if (this.curChar == '%') {
                                this.jjCheckNAdd(112);
                                continue;
                            }
                            continue;
                        }
                        case 112: {
                            if ((0x1800000000L & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(112, 113);
                                continue;
                            }
                            continue;
                        }
                        case 113: {
                            if (this.curChar == '%') {
                                this.jjCheckNAddTwoStates(114, 115);
                                continue;
                            }
                            continue;
                        }
                        case 114: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(114, 115);
                                continue;
                            }
                            continue;
                        }
                        case 115: {
                            if (this.curChar == '*') {
                                this.jjCheckNAddStates(100, 102);
                                continue;
                            }
                            continue;
                        }
                        case 116: {
                            if ((0xFFFF7BFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(117, 115);
                                continue;
                            }
                            continue;
                        }
                        case 117: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(117, 115);
                                continue;
                            }
                            continue;
                        }
                        case 118: {
                            if (this.curChar == '/' && kind > 546) {
                                kind = 546;
                                continue;
                            }
                            continue;
                        }
                        case 119: {
                            if (this.curChar == '*') {
                                this.jjstateSet[this.jjnewStateCnt++] = 120;
                                continue;
                            }
                            continue;
                        }
                        case 120: {
                            if (this.curChar == '+') {
                                this.jjCheckNAddTwoStates(121, 122);
                                continue;
                            }
                            continue;
                        }
                        case 121: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(121, 122);
                                continue;
                            }
                            continue;
                        }
                        case 122: {
                            if (this.curChar == '*') {
                                this.jjCheckNAddStates(103, 105);
                                continue;
                            }
                            continue;
                        }
                        case 123: {
                            if ((0xFFFF7BFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(124, 122);
                                continue;
                            }
                            continue;
                        }
                        case 124: {
                            if ((0xFFFFFBFFFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(124, 122);
                                continue;
                            }
                            continue;
                        }
                        case 125: {
                            if (this.curChar == '/' && kind > 547) {
                                kind = 547;
                                continue;
                            }
                            continue;
                        }
                        case 126: {
                            if (this.curChar == ':') {
                                this.jjCheckNAddStates(44, 56);
                                continue;
                            }
                            continue;
                        }
                        case 127: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(128, 129);
                                continue;
                            }
                            continue;
                        }
                        case 128: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(128, 129);
                                continue;
                            }
                            continue;
                        }
                        case 129: {
                            if (this.curChar != '\"') {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAdd(130);
                            continue;
                        }
                        case 130: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(106, 110);
                                continue;
                            }
                            continue;
                        }
                        case 131: {
                            if (this.curChar == '\"') {
                                this.jjCheckNAddTwoStates(132, 133);
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if ((0xFFFFFFFBFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(132, 133);
                                continue;
                            }
                            continue;
                        }
                        case 133: {
                            if (this.curChar == '\"' && kind > 538) {
                                kind = 538;
                                continue;
                            }
                            continue;
                        }
                        case 134: {
                            if ((0x1800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAddTwoStates(134, 135);
                            continue;
                        }
                        case 135: {
                            if ((0x3FF001800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAdd(135);
                            continue;
                        }
                        case 136: {
                            if (this.curChar == '?') {
                                this.jjCheckNAdd(137);
                                continue;
                            }
                            continue;
                        }
                        case 137: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAdd(137);
                            continue;
                        }
                        case 138: {
                            if (this.curChar == '&') {
                                this.jjCheckNAdd(139);
                                continue;
                            }
                            continue;
                        }
                        case 139: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAdd(139);
                            continue;
                        }
                        case 140: {
                            if ((0x8000004000000000L & l) != 0x0L && kind > 538) {
                                kind = 538;
                                continue;
                            }
                            continue;
                        }
                        case 141: {
                            if ((0x1800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAddStates(22, 24);
                            continue;
                        }
                        case 142: {
                            if ((0x3FF001800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAddTwoStates(142, 130);
                            continue;
                        }
                        case 143: {
                            if (this.curChar == '?') {
                                this.jjCheckNAdd(144);
                                continue;
                            }
                            continue;
                        }
                        case 144: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAddTwoStates(144, 130);
                            continue;
                        }
                        case 145: {
                            if (this.curChar == '&') {
                                this.jjCheckNAdd(146);
                                continue;
                            }
                            continue;
                        }
                        case 146: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAddTwoStates(146, 130);
                            continue;
                        }
                        case 147: {
                            if ((0x8000004000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAdd(130);
                            continue;
                        }
                        case 148: {
                            if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 149;
                                continue;
                            }
                            continue;
                        }
                        case 150: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(111, 115);
                                continue;
                            }
                            continue;
                        }
                        case 151: {
                            if ((0x8000004000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAdd(152);
                            continue;
                        }
                        case 152: {
                            if (this.curChar == '.') {
                                this.jjAddStates(116, 119);
                                continue;
                            }
                            continue;
                        }
                        case 153: {
                            if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 154;
                                continue;
                            }
                            continue;
                        }
                        case 155: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(120, 124);
                                continue;
                            }
                            continue;
                        }
                        case 156: {
                            if ((0x8000004000000000L & l) != 0x0L && kind > 540) {
                                kind = 540;
                                continue;
                            }
                            continue;
                        }
                        case 157: {
                            if (this.curChar == '&') {
                                this.jjCheckNAdd(158);
                                continue;
                            }
                            continue;
                        }
                        case 158: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAdd(158);
                            continue;
                        }
                        case 159: {
                            if (this.curChar == '?') {
                                this.jjCheckNAdd(160);
                                continue;
                            }
                            continue;
                        }
                        case 160: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAdd(160);
                            continue;
                        }
                        case 161: {
                            if ((0x1800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAddTwoStates(161, 162);
                            continue;
                        }
                        case 162: {
                            if ((0x3FF001800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAdd(162);
                            continue;
                        }
                        case 163: {
                            if (this.curChar == '$') {
                                this.jjAddStates(125, 128);
                                continue;
                            }
                            continue;
                        }
                        case 164: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(165);
                                continue;
                            }
                            continue;
                        }
                        case 165: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(129, 133);
                                continue;
                            }
                            continue;
                        }
                        case 166: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(167);
                                continue;
                            }
                            continue;
                        }
                        case 167: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(134, 139);
                                continue;
                            }
                            continue;
                        }
                        case 169: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjAddStates(140, 141);
                                continue;
                            }
                            continue;
                        }
                        case 170: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(171);
                                continue;
                            }
                            continue;
                        }
                        case 171: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(142, 146);
                                continue;
                            }
                            continue;
                        }
                        case 172: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(147, 153);
                                continue;
                            }
                            continue;
                        }
                        case 173: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(154, 158);
                                continue;
                            }
                            continue;
                        }
                        case 174: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(159, 164);
                                continue;
                            }
                            continue;
                        }
                        case 175: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(165, 169);
                                continue;
                            }
                            continue;
                        }
                        case 176: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(165, 169);
                                continue;
                            }
                            continue;
                        }
                        case 177: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(170, 177);
                                continue;
                            }
                            continue;
                        }
                        case 178: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(178, 183);
                                continue;
                            }
                            continue;
                        }
                        case 179: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(184, 190);
                                continue;
                            }
                            continue;
                        }
                        case 180: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(191, 196);
                                continue;
                            }
                            continue;
                        }
                        case 181: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(191, 196);
                                continue;
                            }
                            continue;
                        }
                        case 182: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(197, 203);
                                continue;
                            }
                            continue;
                        }
                        case 183: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(204, 208);
                                continue;
                            }
                            continue;
                        }
                        case 184: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(209, 214);
                                continue;
                            }
                            continue;
                        }
                        case 185: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(215, 219);
                                continue;
                            }
                            continue;
                        }
                        case 186: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(215, 219);
                                continue;
                            }
                            continue;
                        }
                        case 187: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(165, 167);
                                continue;
                            }
                            continue;
                        }
                        case 188: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(220, 230);
                                continue;
                            }
                            continue;
                        }
                        case 189: {
                            if (this.curChar == '&') {
                                this.jjCheckNAdd(190);
                                continue;
                            }
                            continue;
                        }
                        case 190: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAddTwoStates(190, 152);
                            continue;
                        }
                        case 191: {
                            if (this.curChar == '?') {
                                this.jjCheckNAdd(192);
                                continue;
                            }
                            continue;
                        }
                        case 192: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAddTwoStates(192, 152);
                            continue;
                        }
                        case 193: {
                            if ((0x1800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAddStates(231, 233);
                            continue;
                        }
                        case 194: {
                            if ((0x3FF001800000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAddTwoStates(194, 152);
                            continue;
                        }
                        case 195: {
                            if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 196;
                                continue;
                            }
                            continue;
                        }
                        case 197: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(197, 198);
                            continue;
                        }
                        case 198: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(234, 237);
                                continue;
                            }
                            continue;
                        }
                        case 199: {
                            if (this.curChar == '0') {
                                this.jjstateSet[this.jjnewStateCnt++] = 200;
                                continue;
                            }
                            continue;
                        }
                        case 201: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 201;
                            continue;
                        }
                        case 202: {
                            if (this.curChar == '$') {
                                this.jjAddStates(238, 241);
                                continue;
                            }
                            continue;
                        }
                        case 203: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(204);
                                continue;
                            }
                            continue;
                        }
                        case 204: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(204);
                            continue;
                        }
                        case 205: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(206);
                                continue;
                            }
                            continue;
                        }
                        case 206: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(206, 207);
                            continue;
                        }
                        case 208: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjAddStates(242, 243);
                                continue;
                            }
                            continue;
                        }
                        case 209: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(210);
                                continue;
                            }
                            continue;
                        }
                        case 210: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(210);
                            continue;
                        }
                        case 211: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(244, 246);
                            continue;
                        }
                        case 212: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(212);
                            continue;
                        }
                        case 213: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(213, 214);
                            continue;
                        }
                        case 214: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(215);
                            continue;
                        }
                        case 215: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(215);
                            continue;
                        }
                        case 216: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(247, 250);
                            continue;
                        }
                        case 217: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(217, 207);
                            continue;
                        }
                        case 218: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(251, 253);
                            continue;
                        }
                        case 219: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(220, 207);
                            continue;
                        }
                        case 220: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(220, 207);
                            continue;
                        }
                        case 221: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(254, 256);
                            continue;
                        }
                        case 222: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(222);
                            continue;
                        }
                        case 223: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(223, 224);
                            continue;
                        }
                        case 224: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(225);
                            continue;
                        }
                        case 225: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(225);
                            continue;
                        }
                        case 226: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(204, 206);
                                continue;
                            }
                            continue;
                        }
                        case 227: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(257, 263);
                            continue;
                        }
                        case 228: {
                            if (this.curChar == '$') {
                                this.jjAddStates(29, 32);
                                continue;
                            }
                            continue;
                        }
                        case 229: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(230);
                                continue;
                            }
                            continue;
                        }
                        case 230: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(230, 198);
                            continue;
                        }
                        case 231: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(232);
                                continue;
                            }
                            continue;
                        }
                        case 232: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(264, 266);
                            continue;
                        }
                        case 234: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjAddStates(267, 268);
                                continue;
                            }
                            continue;
                        }
                        case 235: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(236);
                                continue;
                            }
                            continue;
                        }
                        case 236: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(236, 198);
                            continue;
                        }
                        case 237: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(269, 272);
                            continue;
                        }
                        case 238: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(238, 198);
                            continue;
                        }
                        case 239: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(239, 240);
                            continue;
                        }
                        case 240: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(273, 278);
                            continue;
                        }
                        case 241: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(241, 198);
                            continue;
                        }
                        case 242: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(279, 283);
                            continue;
                        }
                        case 243: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(284, 286);
                            continue;
                        }
                        case 244: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(287, 289);
                            continue;
                        }
                        case 245: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(290, 296);
                            continue;
                        }
                        case 246: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(297, 299);
                            continue;
                        }
                        case 247: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(300, 303);
                            continue;
                        }
                        case 248: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(248, 198);
                            continue;
                        }
                        case 249: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(249, 250);
                            continue;
                        }
                        case 250: {
                            if (this.curChar != '.') {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(304, 309);
                            continue;
                        }
                        case 251: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(251, 198);
                            continue;
                        }
                        case 252: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(230, 232);
                                continue;
                            }
                            continue;
                        }
                        case 253: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddStates(14, 21);
                            continue;
                        }
                        case 254: {
                            if (this.curChar == '$') {
                                this.jjAddStates(25, 28);
                                continue;
                            }
                            continue;
                        }
                        case 255: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(256);
                                continue;
                            }
                            continue;
                        }
                        case 256: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(310, 314);
                                continue;
                            }
                            continue;
                        }
                        case 257: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(258);
                                continue;
                            }
                            continue;
                        }
                        case 258: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(315, 320);
                                continue;
                            }
                            continue;
                        }
                        case 260: {
                            if ((0x280000000000L & l) != 0x0L) {
                                this.jjAddStates(321, 322);
                                continue;
                            }
                            continue;
                        }
                        case 261: {
                            if (this.curChar == '.') {
                                this.jjCheckNAdd(262);
                                continue;
                            }
                            continue;
                        }
                        case 262: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(323, 327);
                                continue;
                            }
                            continue;
                        }
                        case 263: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(328, 334);
                                continue;
                            }
                            continue;
                        }
                        case 264: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(335, 339);
                                continue;
                            }
                            continue;
                        }
                        case 265: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(340, 345);
                                continue;
                            }
                            continue;
                        }
                        case 266: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(346, 350);
                                continue;
                            }
                            continue;
                        }
                        case 267: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(346, 350);
                                continue;
                            }
                            continue;
                        }
                        case 268: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(351, 358);
                                continue;
                            }
                            continue;
                        }
                        case 269: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(359, 364);
                                continue;
                            }
                            continue;
                        }
                        case 270: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(365, 371);
                                continue;
                            }
                            continue;
                        }
                        case 271: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(372, 377);
                                continue;
                            }
                            continue;
                        }
                        case 272: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(372, 377);
                                continue;
                            }
                            continue;
                        }
                        case 273: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(378, 384);
                                continue;
                            }
                            continue;
                        }
                        case 274: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(385, 389);
                                continue;
                            }
                            continue;
                        }
                        case 275: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(390, 395);
                                continue;
                            }
                            continue;
                        }
                        case 276: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddStates(396, 400);
                                continue;
                            }
                            continue;
                        }
                        case 277: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(396, 400);
                                continue;
                            }
                            continue;
                        }
                        case 278: {
                            if (this.curChar == '.') {
                                this.jjCheckNAddTwoStates(256, 258);
                                continue;
                            }
                            continue;
                        }
                        case 279: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(0, 10);
                                continue;
                            }
                            continue;
                        }
                        case 280: {
                            if ((0x3FF000000000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAddStates(33, 43);
                            continue;
                        }
                        case 281: {
                            if ((0x3FF000000000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(401, 404);
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 57:
                        case 286: {
                            this.jjCheckNAddTwoStates(57, 58);
                            continue;
                        }
                        case 54: {
                            this.jjAddStates(74, 75);
                            continue;
                        }
                    }
                } while (i != startsAt);
            }
            else if (this.curChar < '\u0080') {
                final long l = 1L << (this.curChar & '?');
                do {
                    switch (this.jjstateSet[--i]) {
                        case 14: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 539) {
                                    kind = 539;
                                }
                                this.jjCheckNAddStates(11, 13);
                            }
                            else if (this.curChar == '@') {
                                this.jjCheckNAddStates(405, 408);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 538) {
                                    kind = 538;
                                }
                                this.jjCheckNAddStates(22, 24);
                                continue;
                            }
                            continue;
                        }
                        case 2: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAddTwoStates(8, 9);
                            }
                            else if (this.curChar == '@') {
                                this.jjCheckNAddStates(44, 56);
                            }
                            else if (this.curChar == '[') {
                                this.jjCheckNAddTwoStates(57, 58);
                            }
                            else if (this.curChar == '`') {
                                this.jjCheckNAddTwoStates(54, 55);
                            }
                            if ((0x408000004080L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 48;
                            }
                            else if (this.curChar == '@') {
                                this.jjstateSet[this.jjnewStateCnt++] = 33;
                            }
                            if (this.curChar == '@') {
                                this.jjCheckNAddStates(409, 413);
                                continue;
                            }
                            continue;
                        }
                        case 287: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAdd(9);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAddTwoStates(8, 9);
                                continue;
                            }
                            continue;
                        }
                        case 141:
                        case 288: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAddStates(22, 24);
                            continue;
                        }
                        case 286: {
                            if ((0xFFFFFFFFDFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(57, 58);
                                continue;
                            }
                            if (this.curChar == ']' && kind > 545) {
                                kind = 545;
                                continue;
                            }
                            continue;
                        }
                        case 48: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAdd(9);
                            }
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                if (kind > 535) {
                                    kind = 535;
                                }
                                this.jjCheckNAddTwoStates(8, 9);
                                continue;
                            }
                            continue;
                        }
                        case 8: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 535) {
                                kind = 535;
                            }
                            this.jjCheckNAddTwoStates(8, 9);
                            continue;
                        }
                        case 9: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 535) {
                                kind = 535;
                            }
                            this.jjCheckNAdd(9);
                            continue;
                        }
                        case 13: {
                            if (this.curChar == '@') {
                                this.jjCheckNAddStates(409, 413);
                                continue;
                            }
                            continue;
                        }
                        case 20: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddStates(71, 73);
                            continue;
                        }
                        case 21: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddTwoStates(17, 21);
                            continue;
                        }
                        case 27: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddStates(11, 13);
                            continue;
                        }
                        case 28: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 539) {
                                kind = 539;
                            }
                            this.jjCheckNAddTwoStates(28, 17);
                            continue;
                        }
                        case 33: {
                            if (this.curChar == '@') {
                                this.jjCheckNAddStates(405, 408);
                                continue;
                            }
                            continue;
                        }
                        case 39: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 542) {
                                kind = 542;
                            }
                            this.jjCheckNAddTwoStates(39, 40);
                            continue;
                        }
                        case 40: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 542) {
                                kind = 542;
                            }
                            this.jjCheckNAdd(40);
                            continue;
                        }
                        case 41: {
                            if (this.curChar == '@') {
                                this.jjstateSet[this.jjnewStateCnt++] = 33;
                                continue;
                            }
                            continue;
                        }
                        case 47: {
                            if ((0x408000004080L & l) != 0x0L) {
                                this.jjstateSet[this.jjnewStateCnt++] = 48;
                                continue;
                            }
                            continue;
                        }
                        case 53: {
                            if (this.curChar == '`') {
                                this.jjCheckNAddTwoStates(54, 55);
                                continue;
                            }
                            continue;
                        }
                        case 54: {
                            if ((0xFFFFFFFEFFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(54, 55);
                                continue;
                            }
                            continue;
                        }
                        case 55: {
                            if (this.curChar == '`' && kind > 544) {
                                kind = 544;
                                continue;
                            }
                            continue;
                        }
                        case 56: {
                            if (this.curChar == '[') {
                                this.jjCheckNAddTwoStates(57, 58);
                                continue;
                            }
                            continue;
                        }
                        case 57: {
                            if ((0xFFFFFFFFDFFFFFFFL & l) != 0x0L) {
                                this.jjCheckNAddTwoStates(57, 58);
                                continue;
                            }
                            continue;
                        }
                        case 58: {
                            if (this.curChar == ']' && kind > 545) {
                                kind = 545;
                                continue;
                            }
                            continue;
                        }
                        case 60: {
                            if ((0x100000001000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(61);
                            continue;
                        }
                        case 61: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 548) {
                                kind = 548;
                            }
                            this.jjCheckNAdd(61);
                            continue;
                        }
                        case 67: {
                            if ((0x2080000020L & l) != 0x0L) {
                                this.jjAddStates(416, 418);
                                continue;
                            }
                            continue;
                        }
                        case 97: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                this.jjAddStates(419, 420);
                                continue;
                            }
                            continue;
                        }
                        case 112: {
                            if ((0x7FFFFFE87FFFFFEL & l) != 0x0L) {
                                this.jjAddStates(421, 422);
                                continue;
                            }
                            continue;
                        }
                        case 126: {
                            if (this.curChar == '@') {
                                this.jjCheckNAddStates(44, 56);
                                continue;
                            }
                            continue;
                        }
                        case 134: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAddTwoStates(134, 135);
                            continue;
                        }
                        case 135: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAdd(135);
                            continue;
                        }
                        case 142: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 538) {
                                kind = 538;
                            }
                            this.jjCheckNAddTwoStates(142, 130);
                            continue;
                        }
                        case 149: {
                            if ((0x100000001000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(111, 115);
                                continue;
                            }
                            continue;
                        }
                        case 150: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(111, 115);
                                continue;
                            }
                            continue;
                        }
                        case 154: {
                            if ((0x100000001000000L & l) != 0x0L) {
                                this.jjCheckNAddStates(120, 124);
                                continue;
                            }
                            continue;
                        }
                        case 155: {
                            if ((0x7E0000007EL & l) != 0x0L) {
                                this.jjCheckNAddStates(120, 124);
                                continue;
                            }
                            continue;
                        }
                        case 161: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAddTwoStates(161, 162);
                            continue;
                        }
                        case 162: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAdd(162);
                            continue;
                        }
                        case 168: {
                            if ((0x2080000020L & l) != 0x0L) {
                                this.jjAddStates(427, 429);
                                continue;
                            }
                            continue;
                        }
                        case 193: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAddStates(231, 233);
                            continue;
                        }
                        case 194: {
                            if ((0x7FFFFFE87FFFFFEL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 540) {
                                kind = 540;
                            }
                            this.jjCheckNAddTwoStates(194, 152);
                            continue;
                        }
                        case 196: {
                            if ((0x100000001000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(197, 198);
                            continue;
                        }
                        case 197: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAddTwoStates(197, 198);
                            continue;
                        }
                        case 200: {
                            if ((0x100000001000000L & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(201);
                            continue;
                        }
                        case 201: {
                            if ((0x7E0000007EL & l) == 0x0L) {
                                continue;
                            }
                            if (kind > 541) {
                                kind = 541;
                            }
                            this.jjCheckNAdd(201);
                            continue;
                        }
                        case 207: {
                            if ((0x2080000020L & l) != 0x0L) {
                                this.jjAddStates(430, 432);
                                continue;
                            }
                            continue;
                        }
                        case 233: {
                            if ((0x2080000020L & l) != 0x0L) {
                                this.jjAddStates(433, 435);
                                continue;
                            }
                            continue;
                        }
                        case 259: {
                            if ((0x2080000020L & l) != 0x0L) {
                                this.jjAddStates(436, 438);
                                continue;
                            }
                            continue;
                        }
                        case 282: {
                            if ((0x400000004L & l) != 0x0L && kind > 549) {
                                kind = 549;
                                continue;
                            }
                            continue;
                        }
                        case 283: {
                            if ((0x200000002000L & l) != 0x0L) {
                                this.jjCheckNAdd(282);
                                continue;
                            }
                            continue;
                        }
                        case 284: {
                            if ((0x80000000800L & l) != 0x0L) {
                                this.jjCheckNAdd(282);
                                continue;
                            }
                            continue;
                        }
                        case 285: {
                            if ((0x288000002880L & l) != 0x0L && kind > 549) {
                                kind = 549;
                                continue;
                            }
                            continue;
                        }
                        default: {
                            continue;
                        }
                        case 1: {
                            if (kind > 5) {
                                kind = 5;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            continue;
                        }
                        case 11: {
                            this.jjAddStates(414, 415);
                            continue;
                        }
                        case 15: {
                            this.jjCheckNAddTwoStates(15, 16);
                            continue;
                        }
                        case 19: {
                            this.jjCheckNAddTwoStates(19, 16);
                            continue;
                        }
                        case 43: {
                            this.jjCheckNAddTwoStates(43, 44);
                            continue;
                        }
                        case 46: {
                            this.jjCheckNAddTwoStates(46, 44);
                            continue;
                        }
                        case 49: {
                            this.jjCheckNAddTwoStates(49, 50);
                            continue;
                        }
                        case 52: {
                            this.jjCheckNAddTwoStates(52, 50);
                            continue;
                        }
                        case 90: {
                            this.jjCheckNAddTwoStates(90, 91);
                            continue;
                        }
                        case 92:
                        case 93: {
                            this.jjCheckNAddTwoStates(93, 91);
                            continue;
                        }
                        case 99: {
                            this.jjCheckNAddTwoStates(99, 100);
                            continue;
                        }
                        case 101:
                        case 102: {
                            this.jjCheckNAddTwoStates(102, 100);
                            continue;
                        }
                        case 105: {
                            this.jjCheckNAddTwoStates(105, 106);
                            continue;
                        }
                        case 107:
                        case 108: {
                            this.jjCheckNAddTwoStates(108, 106);
                            continue;
                        }
                        case 114: {
                            this.jjCheckNAddTwoStates(114, 115);
                            continue;
                        }
                        case 116:
                        case 117: {
                            this.jjCheckNAddTwoStates(117, 115);
                            continue;
                        }
                        case 121: {
                            this.jjCheckNAddTwoStates(121, 122);
                            continue;
                        }
                        case 123:
                        case 124: {
                            this.jjCheckNAddTwoStates(124, 122);
                            continue;
                        }
                        case 128: {
                            this.jjAddStates(423, 424);
                            continue;
                        }
                        case 132: {
                            this.jjAddStates(425, 426);
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
                        case 57:
                        case 286: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(57, 58);
                                continue;
                            }
                            continue;
                        }
                        case 1: {
                            if (!jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                continue;
                            }
                            if (kind > 5) {
                                kind = 5;
                            }
                            this.jjstateSet[this.jjnewStateCnt++] = 1;
                            continue;
                        }
                        case 11: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(414, 415);
                                continue;
                            }
                            continue;
                        }
                        case 15: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(15, 16);
                                continue;
                            }
                            continue;
                        }
                        case 19: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(19, 16);
                                continue;
                            }
                            continue;
                        }
                        case 43: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(43, 44);
                                continue;
                            }
                            continue;
                        }
                        case 46: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(46, 44);
                                continue;
                            }
                            continue;
                        }
                        case 49: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(49, 50);
                                continue;
                            }
                            continue;
                        }
                        case 52: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(52, 50);
                                continue;
                            }
                            continue;
                        }
                        case 54: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(74, 75);
                                continue;
                            }
                            continue;
                        }
                        case 90: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(90, 91);
                                continue;
                            }
                            continue;
                        }
                        case 92:
                        case 93: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(93, 91);
                                continue;
                            }
                            continue;
                        }
                        case 99: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(99, 100);
                                continue;
                            }
                            continue;
                        }
                        case 101:
                        case 102: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(102, 100);
                                continue;
                            }
                            continue;
                        }
                        case 105: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(105, 106);
                                continue;
                            }
                            continue;
                        }
                        case 107:
                        case 108: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(108, 106);
                                continue;
                            }
                            continue;
                        }
                        case 114: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(114, 115);
                                continue;
                            }
                            continue;
                        }
                        case 116:
                        case 117: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(117, 115);
                                continue;
                            }
                            continue;
                        }
                        case 121: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(121, 122);
                                continue;
                            }
                            continue;
                        }
                        case 123:
                        case 124: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjCheckNAddTwoStates(124, 122);
                                continue;
                            }
                            continue;
                        }
                        case 128: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(423, 424);
                                continue;
                            }
                            continue;
                        }
                        case 132: {
                            if (jjCanMove_0(hiByte, i2, i3, l2, l3)) {
                                this.jjAddStates(425, 426);
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
            final int n2 = 286;
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
                return (ALLSQLTokenManager.jjbitVec2[i2] & l2) != 0x0L;
            }
            default: {
                return (ALLSQLTokenManager.jjbitVec0[i1] & l1) != 0x0L;
            }
        }
    }
    
    public ALLSQLTokenManager(final JavaCharStream stream) {
        this.debugStream = System.out;
        this.jjrounds = new int[286];
        this.jjstateSet = new int[572];
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.input_stream = stream;
    }
    
    public ALLSQLTokenManager(final JavaCharStream stream, final int lexState) {
        this(stream);
        this.SwitchTo(lexState);
    }
    
    public void ReInit(final JavaCharStream stream) {
        final int n = 0;
        this.jjnewStateCnt = n;
        this.jjmatchedPos = n;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        this.ReInitRounds();
    }
    
    private void ReInitRounds() {
        this.jjround = -2147483647;
        int i = 286;
        while (i-- > 0) {
            this.jjrounds[i] = Integer.MIN_VALUE;
        }
    }
    
    public void ReInit(final JavaCharStream stream, final int lexState) {
        this.ReInit(stream);
        this.SwitchTo(lexState);
    }
    
    public void SwitchTo(final int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
    
    protected Token jjFillToken() {
        final String im = ALLSQLTokenManager.jjstrLiteralImages[this.jjmatchedKind];
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
        Token specialToken = null;
        int curPos = 0;
        while (true) {
            try {
                this.curChar = this.input_stream.BeginToken();
            }
            catch (final IOException e) {
                this.jjmatchedKind = 0;
                final Token matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                return matchedToken;
            }
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
            if ((ALLSQLTokenManager.jjtoToken[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) != 0x0L) {
                final Token matchedToken = this.jjFillToken();
                matchedToken.specialToken = specialToken;
                return matchedToken;
            }
            if ((ALLSQLTokenManager.jjtoSpecial[this.jjmatchedKind >> 6] & 1L << (this.jjmatchedKind & 0x3F)) == 0x0L) {
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
    
    private void jjCheckNAdd(final int state) {
        if (this.jjrounds[state] != this.jjround) {
            this.jjstateSet[this.jjnewStateCnt++] = state;
            this.jjrounds[state] = this.jjround;
        }
    }
    
    private void jjAddStates(int start, final int end) {
        do {
            this.jjstateSet[this.jjnewStateCnt++] = ALLSQLTokenManager.jjnextStates[start];
        } while (start++ != end);
    }
    
    private void jjCheckNAddTwoStates(final int state1, final int state2) {
        this.jjCheckNAdd(state1);
        this.jjCheckNAdd(state2);
    }
    
    private void jjCheckNAddStates(int start, final int end) {
        do {
            this.jjCheckNAdd(ALLSQLTokenManager.jjnextStates[start]);
        } while (start++ != end);
    }
    
    static {
        jjbitVec0 = new long[] { -2L, -1L, -1L, -1L };
        jjbitVec2 = new long[] { 0L, 0L, -1L, -1L };
        jjnextStates = new int[] { 274, 275, 276, 269, 270, 271, 259, 151, 189, 191, 193, 27, 28, 17, 248, 249, 243, 244, 233, 198, 245, 250, 141, 142, 130, 255, 257, 268, 273, 229, 231, 242, 247, 82, 83, 84, 77, 78, 79, 67, 281, 283, 284, 285, 127, 141, 143, 145, 147, 148, 195, 228, 252, 253, 254, 278, 279, 88, 95, 104, 110, 119, 63, 65, 76, 81, 18, 20, 22, 24, 26, 17, 20, 21, 54, 55, 69, 71, 72, 73, 74, 77, 78, 79, 67, 78, 79, 67, 82, 83, 84, 91, 92, 94, 100, 101, 103, 106, 107, 109, 115, 116, 118, 122, 123, 125, 131, 134, 136, 138, 140, 150, 151, 189, 191, 193, 153, 163, 187, 188, 155, 156, 157, 159, 161, 164, 166, 177, 182, 165, 156, 157, 159, 161, 167, 168, 156, 157, 159, 161, 170, 172, 171, 156, 157, 159, 161, 173, 174, 175, 156, 157, 159, 161, 173, 156, 157, 159, 161, 174, 175, 156, 157, 159, 161, 176, 156, 157, 159, 161, 178, 179, 180, 168, 156, 157, 159, 161, 178, 168, 156, 157, 159, 161, 179, 180, 168, 156, 157, 159, 161, 181, 168, 156, 157, 159, 161, 183, 184, 185, 156, 157, 159, 161, 183, 156, 157, 159, 161, 184, 185, 156, 157, 159, 161, 186, 156, 157, 159, 161, 183, 184, 185, 178, 179, 180, 168, 156, 157, 159, 161, 193, 194, 152, 199, 202, 226, 227, 203, 205, 216, 221, 209, 211, 212, 213, 214, 217, 218, 219, 207, 218, 219, 207, 222, 223, 224, 222, 223, 224, 217, 218, 219, 207, 232, 233, 198, 235, 237, 238, 239, 198, 240, 241, 198, 199, 202, 226, 227, 243, 244, 233, 198, 245, 243, 233, 198, 244, 233, 245, 246, 233, 198, 199, 202, 226, 227, 246, 233, 198, 248, 249, 198, 250, 251, 198, 199, 202, 226, 227, 256, 151, 189, 191, 193, 258, 259, 151, 189, 191, 193, 261, 263, 262, 151, 189, 191, 193, 264, 265, 266, 151, 189, 191, 193, 264, 151, 189, 191, 193, 265, 266, 151, 189, 191, 193, 267, 151, 189, 191, 193, 269, 270, 271, 259, 151, 189, 191, 193, 269, 259, 151, 189, 191, 193, 270, 271, 259, 151, 189, 191, 193, 272, 259, 151, 189, 191, 193, 274, 275, 276, 151, 189, 191, 193, 274, 151, 189, 191, 193, 275, 276, 151, 189, 191, 193, 277, 151, 189, 191, 193, 281, 283, 284, 285, 34, 35, 37, 39, 14, 27, 29, 31, 26, 11, 12, 68, 69, 71, 97, 98, 112, 113, 128, 129, 132, 133, 169, 170, 172, 208, 209, 211, 234, 235, 237, 260, 261, 263 };
        jjstrLiteralImages = new String[] { "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ",", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "+", "::", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "&", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, ".", "(", ")", "@", "*", "-", "/", "|", "%", "**", "^", "=", ":=", "~", "{", "}", "[", "]", "<", ">", "!", "^=", "~*", "!~", "!~*" };
        lexStateNames = new String[] { "DEFAULT" };
        jjtoToken = new long[] { -511L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -4123185381377L, 7L };
        jjtoSkip = new long[] { 510L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L };
        jjtoSpecial = new long[] { 480L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L };
    }
}
