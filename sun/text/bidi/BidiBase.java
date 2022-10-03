package sun.text.bidi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.text.normalizer.UCharacter;
import java.util.Arrays;
import java.text.Bidi;
import java.text.AttributedCharacterIterator;
import sun.text.normalizer.UTF16;
import java.lang.reflect.Array;
import java.io.IOException;
import java.util.MissingResourceException;
import sun.text.normalizer.UBiDiProps;

public class BidiBase
{
    public static final byte INTERNAL_LEVEL_DEFAULT_LTR = 126;
    public static final byte INTERNAL_LEVEL_DEFAULT_RTL = Byte.MAX_VALUE;
    public static final byte MAX_EXPLICIT_LEVEL = 61;
    public static final byte INTERNAL_LEVEL_OVERRIDE = Byte.MIN_VALUE;
    public static final int MAP_NOWHERE = -1;
    public static final byte MIXED = 2;
    public static final short DO_MIRRORING = 2;
    private static final short REORDER_DEFAULT = 0;
    private static final short REORDER_NUMBERS_SPECIAL = 1;
    private static final short REORDER_GROUP_NUMBERS_WITH_R = 2;
    private static final short REORDER_RUNS_ONLY = 3;
    private static final short REORDER_INVERSE_NUMBERS_AS_L = 4;
    private static final short REORDER_INVERSE_LIKE_DIRECT = 5;
    private static final short REORDER_INVERSE_FOR_NUMBERS_SPECIAL = 6;
    private static final short REORDER_LAST_LOGICAL_TO_VISUAL = 1;
    private static final int OPTION_INSERT_MARKS = 1;
    private static final int OPTION_REMOVE_CONTROLS = 2;
    private static final int OPTION_STREAMING = 4;
    private static final byte L = 0;
    private static final byte R = 1;
    private static final byte EN = 2;
    private static final byte ES = 3;
    private static final byte ET = 4;
    private static final byte AN = 5;
    private static final byte CS = 6;
    static final byte B = 7;
    private static final byte S = 8;
    private static final byte WS = 9;
    private static final byte ON = 10;
    private static final byte LRE = 11;
    private static final byte LRO = 12;
    private static final byte AL = 13;
    private static final byte RLE = 14;
    private static final byte RLO = 15;
    private static final byte PDF = 16;
    private static final byte NSM = 17;
    private static final byte BN = 18;
    private static final int MASK_R_AL = 8194;
    private static final char CR = '\r';
    private static final char LF = '\n';
    static final int LRM_BEFORE = 1;
    static final int LRM_AFTER = 2;
    static final int RLM_BEFORE = 4;
    static final int RLM_AFTER = 8;
    BidiBase paraBidi;
    final UBiDiProps bdp;
    char[] text;
    int originalLength;
    public int length;
    int resultLength;
    boolean mayAllocateText;
    boolean mayAllocateRuns;
    byte[] dirPropsMemory;
    byte[] levelsMemory;
    byte[] dirProps;
    byte[] levels;
    boolean orderParagraphsLTR;
    byte paraLevel;
    byte defaultParaLevel;
    ImpTabPair impTabPair;
    byte direction;
    int flags;
    int lastArabicPos;
    int trailingWSStart;
    int paraCount;
    int[] parasMemory;
    int[] paras;
    int[] simpleParas;
    int runCount;
    BidiRun[] runsMemory;
    BidiRun[] runs;
    BidiRun[] simpleRuns;
    int[] logicalToVisualRunsMap;
    boolean isGoodLogicalToVisualRunsMap;
    InsertPoints insertPoints;
    int controlCount;
    static final byte CONTEXT_RTL_SHIFT = 6;
    static final byte CONTEXT_RTL = 64;
    static final int DirPropFlagMultiRuns;
    static final int[] DirPropFlagLR;
    static final int[] DirPropFlagE;
    static final int[] DirPropFlagO;
    static final int MASK_LTR;
    static final int MASK_RTL;
    private static final int MASK_LRX;
    private static final int MASK_RLX;
    private static final int MASK_EXPLICIT;
    private static final int MASK_BN_EXPLICIT;
    private static final int MASK_B_S;
    static final int MASK_WS;
    private static final int MASK_N;
    private static final int MASK_POSSIBLE_N;
    static final int MASK_EMBEDDING;
    private static final int IMPTABPROPS_COLUMNS = 14;
    private static final int IMPTABPROPS_RES = 13;
    private static final short[] groupProp;
    private static final short _L = 0;
    private static final short _R = 1;
    private static final short _EN = 2;
    private static final short _AN = 3;
    private static final short _ON = 4;
    private static final short _S = 5;
    private static final short _B = 6;
    private static final short[][] impTabProps;
    private static final int IMPTABLEVELS_COLUMNS = 8;
    private static final int IMPTABLEVELS_RES = 7;
    private static final byte[][] impTabL_DEFAULT;
    private static final byte[][] impTabR_DEFAULT;
    private static final short[] impAct0;
    private static final ImpTabPair impTab_DEFAULT;
    private static final byte[][] impTabL_NUMBERS_SPECIAL;
    private static final ImpTabPair impTab_NUMBERS_SPECIAL;
    private static final byte[][] impTabL_GROUP_NUMBERS_WITH_R;
    private static final byte[][] impTabR_GROUP_NUMBERS_WITH_R;
    private static final ImpTabPair impTab_GROUP_NUMBERS_WITH_R;
    private static final byte[][] impTabL_INVERSE_NUMBERS_AS_L;
    private static final byte[][] impTabR_INVERSE_NUMBERS_AS_L;
    private static final ImpTabPair impTab_INVERSE_NUMBERS_AS_L;
    private static final byte[][] impTabR_INVERSE_LIKE_DIRECT;
    private static final short[] impAct1;
    private static final ImpTabPair impTab_INVERSE_LIKE_DIRECT;
    private static final byte[][] impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS;
    private static final byte[][] impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS;
    private static final short[] impAct2;
    private static final ImpTabPair impTab_INVERSE_LIKE_DIRECT_WITH_MARKS;
    private static final ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL;
    private static final byte[][] impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS;
    private static final ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS;
    static final int FIRSTALLOC = 10;
    private static final int INTERNAL_DIRECTION_DEFAULT_LEFT_TO_RIGHT = 126;
    private static final int INTERMAL_DIRECTION_DEFAULT_RIGHT_TO_LEFT = 127;
    
    static int DirPropFlag(final byte b) {
        return 1 << b;
    }
    
    static byte NoContextRTL(final byte b) {
        return (byte)(b & 0xFFFFFFBF);
    }
    
    static int DirPropFlagNC(final byte b) {
        return 1 << (b & 0xFFFFFFBF);
    }
    
    static final int DirPropFlagLR(final byte b) {
        return BidiBase.DirPropFlagLR[b & 0x1];
    }
    
    static final int DirPropFlagE(final byte b) {
        return BidiBase.DirPropFlagE[b & 0x1];
    }
    
    static final int DirPropFlagO(final byte b) {
        return BidiBase.DirPropFlagO[b & 0x1];
    }
    
    private static byte GetLRFromLevel(final byte b) {
        return (byte)(b & 0x1);
    }
    
    private static boolean IsDefaultLevel(final byte b) {
        return (b & 0x7E) == 0x7E;
    }
    
    byte GetParaLevelAt(final int n) {
        return (this.defaultParaLevel != 0) ? ((byte)(this.dirProps[n] >> 6)) : this.paraLevel;
    }
    
    static boolean IsBidiControlChar(final int n) {
        return (n & 0xFFFFFFFC) == 0x200C || (n >= 8234 && n <= 8238);
    }
    
    public void verifyValidPara() {
        if (this != this.paraBidi) {
            throw new IllegalStateException("");
        }
    }
    
    public void verifyValidParaOrLine() {
        final BidiBase paraBidi = this.paraBidi;
        if (this == paraBidi) {
            return;
        }
        if (paraBidi == null || paraBidi != paraBidi.paraBidi) {
            throw new IllegalStateException();
        }
    }
    
    public void verifyRange(final int n, final int n2, final int n3) {
        if (n < n2 || n >= n3) {
            throw new IllegalArgumentException("Value " + n + " is out of range " + n2 + " to " + n3);
        }
    }
    
    public void verifyIndex(final int n, final int n2, final int n3) {
        if (n < n2 || n >= n3) {
            throw new ArrayIndexOutOfBoundsException("Index " + n + " is out of range " + n2 + " to " + n3);
        }
    }
    
    public BidiBase(final int n, final int n2) {
        this.dirPropsMemory = new byte[1];
        this.levelsMemory = new byte[1];
        this.parasMemory = new int[1];
        this.simpleParas = new int[] { 0 };
        this.runsMemory = new BidiRun[0];
        this.simpleRuns = new BidiRun[] { new BidiRun() };
        this.insertPoints = new InsertPoints();
        if (n < 0 || n2 < 0) {
            throw new IllegalArgumentException();
        }
        try {
            this.bdp = UBiDiProps.getSingleton();
        }
        catch (final IOException ex) {
            throw new MissingResourceException(ex.getMessage(), "(BidiProps)", "");
        }
        if (n > 0) {
            this.getInitialDirPropsMemory(n);
            this.getInitialLevelsMemory(n);
        }
        else {
            this.mayAllocateText = true;
        }
        if (n2 > 0) {
            if (n2 > 1) {
                this.getInitialRunsMemory(n2);
            }
        }
        else {
            this.mayAllocateRuns = true;
        }
    }
    
    private Object getMemory(final String s, final Object o, final Class<?> clazz, final boolean b, final int n) {
        final int length = Array.getLength(o);
        if (n == length) {
            return o;
        }
        if (!b) {
            if (n <= length) {
                return o;
            }
            throw new OutOfMemoryError("Failed to allocate memory for " + s);
        }
        else {
            try {
                return Array.newInstance(clazz, n);
            }
            catch (final Exception ex) {
                throw new OutOfMemoryError("Failed to allocate memory for " + s);
            }
        }
    }
    
    private void getDirPropsMemory(final boolean b, final int n) {
        this.dirPropsMemory = (byte[])this.getMemory("DirProps", this.dirPropsMemory, Byte.TYPE, b, n);
    }
    
    void getDirPropsMemory(final int n) {
        this.getDirPropsMemory(this.mayAllocateText, n);
    }
    
    private void getLevelsMemory(final boolean b, final int n) {
        this.levelsMemory = (byte[])this.getMemory("Levels", this.levelsMemory, Byte.TYPE, b, n);
    }
    
    void getLevelsMemory(final int n) {
        this.getLevelsMemory(this.mayAllocateText, n);
    }
    
    private void getRunsMemory(final boolean b, final int n) {
        this.runsMemory = (BidiRun[])this.getMemory("Runs", this.runsMemory, BidiRun.class, b, n);
    }
    
    void getRunsMemory(final int n) {
        this.getRunsMemory(this.mayAllocateRuns, n);
    }
    
    private void getInitialDirPropsMemory(final int n) {
        this.getDirPropsMemory(true, n);
    }
    
    private void getInitialLevelsMemory(final int n) {
        this.getLevelsMemory(true, n);
    }
    
    private void getInitialParasMemory(final int n) {
        this.parasMemory = (int[])this.getMemory("Paras", this.parasMemory, Integer.TYPE, true, n);
    }
    
    private void getInitialRunsMemory(final int n) {
        this.getRunsMemory(true, n);
    }
    
    private void getDirProps() {
        this.flags = 0;
        byte b = 0;
        final boolean isDefaultLevel = IsDefaultLevel(this.paraLevel);
        this.lastArabicPos = -1;
        this.controlCount = 0;
        int n = 0;
        byte b2;
        int n2;
        if (isDefaultLevel) {
            b = (b2 = (byte)(((this.paraLevel & 0x1) != 0x0) ? 64 : 0));
            n2 = 1;
        }
        else {
            n2 = 0;
            b2 = 0;
        }
        int i = 0;
        while (i < this.originalLength) {
            final int n3 = i;
            final int char1 = UTF16.charAt(this.text, 0, this.originalLength, i);
            i += Character.charCount(char1);
            int j = i - 1;
            final byte b3 = (byte)this.bdp.getClass(char1);
            this.flags |= DirPropFlag(b3);
            this.dirProps[j] = (byte)(b3 | b2);
            if (j > n3) {
                this.flags |= DirPropFlag((byte)18);
                do {
                    this.dirProps[--j] = (byte)(0x12 | b2);
                } while (j > n3);
            }
            if (n2 == 1) {
                if (b3 == 0) {
                    n2 = 2;
                    if (b2 != 0) {
                        b2 = 0;
                        for (int k = n; k < i; ++k) {
                            final byte[] dirProps = this.dirProps;
                            final int n4 = k;
                            dirProps[n4] &= 0xFFFFFFBF;
                        }
                        continue;
                    }
                    continue;
                }
                else if (b3 == 1 || b3 == 13) {
                    n2 = 2;
                    if (b2 == 0) {
                        b2 = 64;
                        for (int l = n; l < i; ++l) {
                            final byte[] dirProps2 = this.dirProps;
                            final int n5 = l;
                            dirProps2[n5] |= 0x40;
                        }
                        continue;
                    }
                    continue;
                }
            }
            if (b3 == 0) {
                continue;
            }
            if (b3 == 1) {
                continue;
            }
            if (b3 == 13) {
                this.lastArabicPos = i - 1;
            }
            else {
                if (b3 != 7 || i >= this.originalLength) {
                    continue;
                }
                if (char1 != 13 || this.text[i] != '\n') {
                    ++this.paraCount;
                }
                if (!isDefaultLevel) {
                    continue;
                }
                n2 = 1;
                n = i;
                b2 = b;
            }
        }
        if (isDefaultLevel) {
            this.paraLevel = this.GetParaLevelAt(0);
        }
        this.flags |= DirPropFlagLR(this.paraLevel);
        if (this.orderParagraphsLTR && (this.flags & DirPropFlag((byte)7)) != 0x0) {
            this.flags |= DirPropFlag((byte)0);
        }
    }
    
    private byte directionFromFlags() {
        if ((this.flags & BidiBase.MASK_RTL) == 0x0 && ((this.flags & DirPropFlag((byte)5)) == 0x0 || (this.flags & BidiBase.MASK_POSSIBLE_N) == 0x0)) {
            return 0;
        }
        if ((this.flags & BidiBase.MASK_LTR) == 0x0) {
            return 1;
        }
        return 2;
    }
    
    private byte resolveExplicitLevels() {
        byte b = this.GetParaLevelAt(0);
        int n = 0;
        byte b2 = this.directionFromFlags();
        if (b2 == 2 || this.paraCount != 1) {
            if (this.paraCount == 1 && (this.flags & BidiBase.MASK_EXPLICIT) == 0x0) {
                for (int i = 0; i < this.length; ++i) {
                    this.levels[i] = b;
                }
            }
            else {
                byte getParaLevel = b;
                int n2 = 0;
                final byte[] array = new byte[61];
                int n3 = 0;
                int n4 = 0;
                this.flags = 0;
                for (int j = 0; j < this.length; ++j) {
                    final byte noContextRTL = NoContextRTL(this.dirProps[j]);
                    switch (noContextRTL) {
                        case 11:
                        case 12: {
                            final byte b3 = (byte)(getParaLevel + 2 & 0x7E);
                            if (b3 <= 61) {
                                array[n2] = getParaLevel;
                                n2 = (byte)(n2 + 1);
                                getParaLevel = b3;
                                if (noContextRTL == 12) {
                                    getParaLevel |= 0xFFFFFF80;
                                }
                            }
                            else if ((getParaLevel & 0x7F) == 0x3D) {
                                ++n4;
                            }
                            else {
                                ++n3;
                            }
                            this.flags |= DirPropFlag((byte)18);
                            break;
                        }
                        case 14:
                        case 15: {
                            final byte b4 = (byte)((getParaLevel & 0x7F) + 1 | 0x1);
                            if (b4 <= 61) {
                                array[n2] = getParaLevel;
                                n2 = (byte)(n2 + 1);
                                getParaLevel = b4;
                                if (noContextRTL == 15) {
                                    getParaLevel |= 0xFFFFFF80;
                                }
                            }
                            else {
                                ++n4;
                            }
                            this.flags |= DirPropFlag((byte)18);
                            break;
                        }
                        case 16: {
                            if (n4 > 0) {
                                --n4;
                            }
                            else if (n3 > 0 && (getParaLevel & 0x7F) != 0x3D) {
                                --n3;
                            }
                            else if (n2 > 0) {
                                n2 = (byte)(n2 - 1);
                                getParaLevel = array[n2];
                            }
                            this.flags |= DirPropFlag((byte)18);
                            break;
                        }
                        case 7: {
                            n2 = 0;
                            n3 = 0;
                            n4 = 0;
                            b = this.GetParaLevelAt(j);
                            if (j + 1 < this.length) {
                                getParaLevel = this.GetParaLevelAt(j + 1);
                                if (this.text[j] != '\r' || this.text[j + 1] != '\n') {
                                    this.paras[n++] = j + 1;
                                }
                            }
                            this.flags |= DirPropFlag((byte)7);
                            break;
                        }
                        case 18: {
                            this.flags |= DirPropFlag((byte)18);
                            break;
                        }
                        default: {
                            if (b != getParaLevel) {
                                b = getParaLevel;
                                if ((b & 0xFFFFFF80) != 0x0) {
                                    this.flags |= (DirPropFlagO(b) | BidiBase.DirPropFlagMultiRuns);
                                }
                                else {
                                    this.flags |= (DirPropFlagE(b) | BidiBase.DirPropFlagMultiRuns);
                                }
                            }
                            if ((b & 0xFFFFFF80) == 0x0) {
                                this.flags |= DirPropFlag(noContextRTL);
                                break;
                            }
                            break;
                        }
                    }
                    this.levels[j] = b;
                }
                if ((this.flags & BidiBase.MASK_EMBEDDING) != 0x0) {
                    this.flags |= DirPropFlagLR(this.paraLevel);
                }
                if (this.orderParagraphsLTR && (this.flags & DirPropFlag((byte)7)) != 0x0) {
                    this.flags |= DirPropFlag((byte)0);
                }
                b2 = this.directionFromFlags();
            }
        }
        return b2;
    }
    
    private byte checkExplicitLevels() {
        this.flags = 0;
        int n = 0;
        for (int i = 0; i < this.length; ++i) {
            if (this.levels[i] == 0) {
                this.levels[i] = this.paraLevel;
            }
            if (61 < (this.levels[i] & 0x7F)) {
                if ((this.levels[i] & 0xFFFFFF80) != 0x0) {
                    this.levels[i] = (byte)(this.paraLevel | 0xFFFFFF80);
                }
                else {
                    this.levels[i] = this.paraLevel;
                }
            }
            byte b = this.levels[i];
            final byte noContextRTL = NoContextRTL(this.dirProps[i]);
            if ((b & 0xFFFFFF80) != 0x0) {
                b &= 0x7F;
                this.flags |= DirPropFlagO(b);
            }
            else {
                this.flags |= (DirPropFlagE(b) | DirPropFlag(noContextRTL));
            }
            if ((b < this.GetParaLevelAt(i) && (b || noContextRTL != 7)) || 61 < b) {
                throw new IllegalArgumentException("level " + b + " out of bounds at index " + i);
            }
            if (noContextRTL == 7 && i + 1 < this.length && (this.text[i] != '\r' || this.text[i + 1] != '\n')) {
                this.paras[n++] = i + 1;
            }
        }
        if ((this.flags & BidiBase.MASK_EMBEDDING) != 0x0) {
            this.flags |= DirPropFlagLR(this.paraLevel);
        }
        return this.directionFromFlags();
    }
    
    private static short GetStateProps(final short n) {
        return (short)(n & 0x1F);
    }
    
    private static short GetActionProps(final short n) {
        return (short)(n >> 5);
    }
    
    private static short GetState(final byte b) {
        return (short)(b & 0xF);
    }
    
    private static short GetAction(final byte b) {
        return (short)(b >> 4);
    }
    
    private void addPoint(final int pos, final int flag) {
        final Point point = new Point();
        int length = this.insertPoints.points.length;
        if (length == 0) {
            this.insertPoints.points = new Point[10];
            length = 10;
        }
        if (this.insertPoints.size >= length) {
            System.arraycopy(this.insertPoints.points, 0, this.insertPoints.points = new Point[length * 2], 0, length);
        }
        point.pos = pos;
        point.flag = flag;
        this.insertPoints.points[this.insertPoints.size] = point;
        final InsertPoints insertPoints = this.insertPoints;
        ++insertPoints.size;
    }
    
    private void processPropertySeq(final LevState levState, final short n, int n2, final int n3) {
        final byte[][] impTab = levState.impTab;
        final short[] impAct = levState.impAct;
        final int n4 = n2;
        final short state = levState.state;
        final byte b = impTab[state][n];
        levState.state = GetState(b);
        final short n5 = impAct[GetAction(b)];
        final byte b2 = impTab[levState.state][7];
        if (n5 != 0) {
            switch (n5) {
                case 1: {
                    levState.startON = n4;
                    break;
                }
                case 2: {
                    n2 = levState.startON;
                    break;
                }
                case 3: {
                    if (levState.startL2EN >= 0) {
                        this.addPoint(levState.startL2EN, 1);
                    }
                    levState.startL2EN = -1;
                    if (this.insertPoints.points.length == 0 || this.insertPoints.size <= this.insertPoints.confirmed) {
                        levState.lastStrongRTL = -1;
                        if ((impTab[state][7] & 0x1) != 0x0 && levState.startON > 0) {
                            n2 = levState.startON;
                        }
                        if (n == 5) {
                            this.addPoint(n4, 1);
                            this.insertPoints.confirmed = this.insertPoints.size;
                            break;
                        }
                        break;
                    }
                    else {
                        for (int i = levState.lastStrongRTL + 1; i < n4; ++i) {
                            this.levels[i] = (byte)(this.levels[i] - 2 & 0xFFFFFFFE);
                        }
                        this.insertPoints.confirmed = this.insertPoints.size;
                        levState.lastStrongRTL = -1;
                        if (n == 5) {
                            this.addPoint(n4, 1);
                            this.insertPoints.confirmed = this.insertPoints.size;
                            break;
                        }
                        break;
                    }
                    break;
                }
                case 4: {
                    if (this.insertPoints.points.length > 0) {
                        this.insertPoints.size = this.insertPoints.confirmed;
                    }
                    levState.startON = -1;
                    levState.startL2EN = -1;
                    levState.lastStrongRTL = n3 - 1;
                    break;
                }
                case 5: {
                    if (n == 3 && NoContextRTL(this.dirProps[n4]) == 5) {
                        if (levState.startL2EN == -1) {
                            levState.lastStrongRTL = n3 - 1;
                            break;
                        }
                        if (levState.startL2EN >= 0) {
                            this.addPoint(levState.startL2EN, 1);
                            levState.startL2EN = -2;
                        }
                        this.addPoint(n4, 1);
                        break;
                    }
                    else {
                        if (levState.startL2EN == -1) {
                            levState.startL2EN = n4;
                            break;
                        }
                        break;
                    }
                    break;
                }
                case 6: {
                    levState.lastStrongRTL = n3 - 1;
                    levState.startON = -1;
                    break;
                }
                case 7: {
                    int n6;
                    for (n6 = n4 - 1; n6 >= 0 && (this.levels[n6] & 0x1) == 0x0; --n6) {}
                    if (n6 >= 0) {
                        this.addPoint(n6, 4);
                        this.insertPoints.confirmed = this.insertPoints.size;
                    }
                    levState.startON = n4;
                    break;
                }
                case 8: {
                    this.addPoint(n4, 1);
                    this.addPoint(n4, 2);
                    break;
                }
                case 9: {
                    this.insertPoints.size = this.insertPoints.confirmed;
                    if (n == 5) {
                        this.addPoint(n4, 4);
                        this.insertPoints.confirmed = this.insertPoints.size;
                        break;
                    }
                    break;
                }
                case 10: {
                    final byte b3 = (byte)(levState.runLevel + b2);
                    for (int j = levState.startON; j < n4; ++j) {
                        if (this.levels[j] < b3) {
                            this.levels[j] = b3;
                        }
                    }
                    this.insertPoints.confirmed = this.insertPoints.size;
                    levState.startON = n4;
                    break;
                }
                case 11: {
                    final byte runLevel = levState.runLevel;
                    for (int k = n4 - 1; k >= levState.startON; --k) {
                        if (this.levels[k] == runLevel + 3) {
                            while (this.levels[k] == runLevel + 3) {
                                final byte[] levels = this.levels;
                                final int n7 = k--;
                                levels[n7] -= 2;
                            }
                            while (this.levels[k] == runLevel) {
                                --k;
                            }
                        }
                        if (this.levels[k] == runLevel + 2) {
                            this.levels[k] = runLevel;
                        }
                        else {
                            this.levels[k] = (byte)(runLevel + 1);
                        }
                    }
                    break;
                }
                case 12: {
                    final byte b4 = (byte)(levState.runLevel + 1);
                    for (int l = n4 - 1; l >= levState.startON; --l) {
                        if (this.levels[l] > b4) {
                            final byte[] levels2 = this.levels;
                            final int n8 = l;
                            levels2[n8] -= 2;
                        }
                    }
                    break;
                }
                default: {
                    throw new IllegalStateException("Internal ICU error in processPropertySeq");
                }
            }
        }
        if (b2 != 0 || n2 < n4) {
            final byte b5 = (byte)(levState.runLevel + b2);
            for (int n9 = n2; n9 < n3; ++n9) {
                this.levels[n9] = b5;
            }
        }
    }
    
    private void resolveImplicitLevels(final int n, final int n2, final short n3, final short n4) {
        final LevState levState = new LevState();
        levState.startL2EN = -1;
        levState.lastStrongRTL = -1;
        levState.state = 0;
        levState.runLevel = this.levels[n];
        levState.impTab = this.impTabPair.imptab[levState.runLevel & 0x1];
        levState.impAct = this.impTabPair.impact[levState.runLevel & 0x1];
        this.processPropertySeq(levState, n3, n, n);
        int getStateProps;
        if (this.dirProps[n] == 17) {
            getStateProps = (short)(1 + n3);
        }
        else {
            getStateProps = 0;
        }
        int n5 = n;
        int n6 = 0;
        for (int i = n; i <= n2; ++i) {
            short n7;
            if (i >= n2) {
                n7 = n4;
            }
            else {
                n7 = BidiBase.groupProp[NoContextRTL(this.dirProps[i])];
            }
            final int n8 = getStateProps;
            final short n9 = BidiBase.impTabProps[n8][n7];
            getStateProps = GetStateProps(n9);
            int getActionProps = GetActionProps(n9);
            if (i == n2 && getActionProps == 0) {
                getActionProps = 1;
            }
            if (getActionProps != 0) {
                final short n10 = BidiBase.impTabProps[n8][13];
                switch (getActionProps) {
                    case 1: {
                        this.processPropertySeq(levState, n10, n5, i);
                        n5 = i;
                        break;
                    }
                    case 2: {
                        n6 = i;
                        break;
                    }
                    case 3: {
                        this.processPropertySeq(levState, n10, n5, n6);
                        this.processPropertySeq(levState, (short)4, n6, i);
                        n5 = i;
                        break;
                    }
                    case 4: {
                        this.processPropertySeq(levState, n10, n5, n6);
                        n5 = n6;
                        n6 = i;
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Internal ICU error in resolveImplicitLevels");
                    }
                }
            }
        }
        this.processPropertySeq(levState, n4, n2, n2);
    }
    
    private void adjustWSLevels() {
        if ((this.flags & BidiBase.MASK_WS) != 0x0) {
            int i = this.trailingWSStart;
            while (i > 0) {
                int dirPropFlagNC;
                while (i > 0 && ((dirPropFlagNC = DirPropFlagNC(this.dirProps[--i])) & BidiBase.MASK_WS) != 0x0) {
                    if (this.orderParagraphsLTR && (dirPropFlagNC & DirPropFlag((byte)7)) != 0x0) {
                        this.levels[i] = 0;
                    }
                    else {
                        this.levels[i] = this.GetParaLevelAt(i);
                    }
                }
                while (i > 0) {
                    final int dirPropFlagNC2 = DirPropFlagNC(this.dirProps[--i]);
                    if ((dirPropFlagNC2 & BidiBase.MASK_BN_EXPLICIT) != 0x0) {
                        this.levels[i] = this.levels[i + 1];
                    }
                    else {
                        if (this.orderParagraphsLTR && (dirPropFlagNC2 & DirPropFlag((byte)7)) != 0x0) {
                            this.levels[i] = 0;
                            break;
                        }
                        if ((dirPropFlagNC2 & BidiBase.MASK_B_S) != 0x0) {
                            this.levels[i] = this.GetParaLevelAt(i);
                            break;
                        }
                        continue;
                    }
                }
            }
        }
    }
    
    private int Bidi_Min(final int n, final int n2) {
        return (n < n2) ? n : n2;
    }
    
    private int Bidi_Abs(final int n) {
        return (n >= 0) ? n : (-n);
    }
    
    void setPara(final String s, final byte b, final byte[] array) {
        if (s == null) {
            this.setPara(new char[0], b, array);
        }
        else {
            this.setPara(s.toCharArray(), b, array);
        }
    }
    
    public void setPara(char[] text, final byte b, final byte[] levels) {
        if (b < 126) {
            this.verifyRange(b, 0, 62);
        }
        if (text == null) {
            text = new char[0];
        }
        this.paraBidi = null;
        this.text = text;
        final int length = this.text.length;
        this.resultLength = length;
        this.originalLength = length;
        this.length = length;
        this.paraLevel = b;
        this.direction = 0;
        this.paraCount = 1;
        this.dirProps = new byte[0];
        this.levels = new byte[0];
        this.runs = new BidiRun[0];
        this.isGoodLogicalToVisualRunsMap = false;
        this.insertPoints.size = 0;
        this.insertPoints.confirmed = 0;
        if (IsDefaultLevel(b)) {
            this.defaultParaLevel = b;
        }
        else {
            this.defaultParaLevel = 0;
        }
        if (this.length == 0) {
            if (IsDefaultLevel(b)) {
                this.paraLevel &= 0x1;
                this.defaultParaLevel = 0;
            }
            if ((this.paraLevel & 0x1) != 0x0) {
                this.flags = DirPropFlag((byte)1);
                this.direction = 1;
            }
            else {
                this.flags = DirPropFlag((byte)0);
                this.direction = 0;
            }
            this.runCount = 0;
            this.paraCount = 0;
            this.paraBidi = this;
            return;
        }
        this.runCount = -1;
        this.getDirPropsMemory(this.length);
        this.dirProps = this.dirPropsMemory;
        this.getDirProps();
        this.trailingWSStart = this.length;
        if (this.paraCount > 1) {
            this.getInitialParasMemory(this.paraCount);
            (this.paras = this.parasMemory)[this.paraCount - 1] = this.length;
        }
        else {
            this.paras = this.simpleParas;
            this.simpleParas[0] = this.length;
        }
        if (levels == null) {
            this.getLevelsMemory(this.length);
            this.levels = this.levelsMemory;
            this.direction = this.resolveExplicitLevels();
        }
        else {
            this.levels = levels;
            this.direction = this.checkExplicitLevels();
        }
        switch (this.direction) {
            case 0: {
                final byte b2 = (byte)(b + 1 & 0xFFFFFFFE);
                this.trailingWSStart = 0;
                break;
            }
            case 1: {
                final byte b3 = (byte)(b | 0x1);
                this.trailingWSStart = 0;
                break;
            }
            default: {
                this.impTabPair = BidiBase.impTab_DEFAULT;
                if (levels == null && this.paraCount <= 1 && (this.flags & BidiBase.DirPropFlagMultiRuns) == 0x0) {
                    this.resolveImplicitLevels(0, this.length, GetLRFromLevel(this.GetParaLevelAt(0)), GetLRFromLevel(this.GetParaLevelAt(this.length - 1)));
                }
                else {
                    int i = 0;
                    final byte getParaLevel = this.GetParaLevelAt(0);
                    byte getParaLevel2 = this.levels[0];
                    short n;
                    if (getParaLevel < getParaLevel2) {
                        n = GetLRFromLevel(getParaLevel2);
                    }
                    else {
                        n = GetLRFromLevel(getParaLevel);
                    }
                    do {
                        int j = i;
                        final byte b4 = getParaLevel2;
                        short n2;
                        if (j > 0 && NoContextRTL(this.dirProps[j - 1]) == 7) {
                            n2 = GetLRFromLevel(this.GetParaLevelAt(j));
                        }
                        else {
                            n2 = n;
                        }
                        while (++i < this.length && this.levels[i] == b4) {}
                        if (i < this.length) {
                            getParaLevel2 = this.levels[i];
                        }
                        else {
                            getParaLevel2 = this.GetParaLevelAt(this.length - 1);
                        }
                        if ((b4 & 0x7F) < (getParaLevel2 & 0x7F)) {
                            n = GetLRFromLevel(getParaLevel2);
                        }
                        else {
                            n = GetLRFromLevel(b4);
                        }
                        if ((b4 & 0xFFFFFF80) == 0x0) {
                            this.resolveImplicitLevels(j, i, n2, n);
                        }
                        else {
                            do {
                                final byte[] levels2 = this.levels;
                                final int n3 = j++;
                                levels2[n3] &= 0x7F;
                            } while (j < i);
                        }
                    } while (i < this.length);
                }
                this.adjustWSLevels();
                break;
            }
        }
        this.resultLength += this.insertPoints.size;
        this.paraBidi = this;
    }
    
    public void setPara(final AttributedCharacterIterator attributedCharacterIterator) {
        char c = attributedCharacterIterator.first();
        final Boolean b = (Boolean)attributedCharacterIterator.getAttribute(TextAttributeConstants.RUN_DIRECTION);
        final Object attribute = attributedCharacterIterator.getAttribute(TextAttributeConstants.NUMERIC_SHAPING);
        byte b2;
        if (b == null) {
            b2 = 126;
        }
        else {
            b2 = (byte)(b.equals(TextAttributeConstants.RUN_DIRECTION_LTR) ? 0 : 1);
        }
        byte[] array = null;
        final int n = attributedCharacterIterator.getEndIndex() - attributedCharacterIterator.getBeginIndex();
        final byte[] array2 = new byte[n];
        final char[] array3 = new char[n];
        for (int n2 = 0; c != '\uffff'; c = attributedCharacterIterator.next(), ++n2) {
            array3[n2] = c;
            final Integer n3 = (Integer)attributedCharacterIterator.getAttribute(TextAttributeConstants.BIDI_EMBEDDING);
            if (n3 != null) {
                final byte byteValue = n3.byteValue();
                if (byteValue != 0) {
                    if (byteValue < 0) {
                        array = array2;
                        array2[n2] = (byte)(0 - byteValue | 0xFFFFFF80);
                    }
                    else {
                        array = array2;
                        array2[n2] = byteValue;
                    }
                }
            }
        }
        if (attribute != null) {
            NumericShapings.shape(attribute, array3, 0, n);
        }
        this.setPara(array3, b2, array);
    }
    
    private void orderParagraphsLTR(final boolean orderParagraphsLTR) {
        this.orderParagraphsLTR = orderParagraphsLTR;
    }
    
    private byte getDirection() {
        this.verifyValidParaOrLine();
        return this.direction;
    }
    
    public int getLength() {
        this.verifyValidParaOrLine();
        return this.originalLength;
    }
    
    public byte getParaLevel() {
        this.verifyValidParaOrLine();
        return this.paraLevel;
    }
    
    public int getParagraphIndex(final int i) {
        this.verifyValidParaOrLine();
        final BidiBase paraBidi = this.paraBidi;
        this.verifyRange(i, 0, paraBidi.length);
        int n;
        for (n = 0; i >= paraBidi.paras[n]; ++n) {}
        return n;
    }
    
    public Bidi setLine(final Bidi bidi, final BidiBase bidiBase, final Bidi bidi2, final BidiBase bidiBase2, final int n, final int n2) {
        this.verifyValidPara();
        this.verifyRange(n, 0, n2);
        this.verifyRange(n2, 0, this.length + 1);
        return BidiLine.setLine(bidi, this, bidi2, bidiBase2, n, n2);
    }
    
    public byte getLevelAt(final int n) {
        if (n < 0 || n >= this.length) {
            return (byte)this.getBaseLevel();
        }
        this.verifyValidParaOrLine();
        this.verifyRange(n, 0, this.length);
        return BidiLine.getLevelAt(this, n);
    }
    
    private byte[] getLevels() {
        this.verifyValidParaOrLine();
        if (this.length <= 0) {
            return new byte[0];
        }
        return BidiLine.getLevels(this);
    }
    
    public int countRuns() {
        this.verifyValidParaOrLine();
        BidiLine.getRuns(this);
        return this.runCount;
    }
    
    private int[] getVisualMap() {
        this.countRuns();
        if (this.resultLength <= 0) {
            return new int[0];
        }
        return BidiLine.getVisualMap(this);
    }
    
    private static int[] reorderVisual(final byte[] array) {
        return BidiLine.reorderVisual(array);
    }
    
    public BidiBase(final char[] array, final int n, final byte[] array2, final int n2, final int n3, final int n4) {
        this(0, 0);
        byte b = 0;
        switch (n4) {
            default: {
                b = 0;
                break;
            }
            case 1: {
                b = 1;
                break;
            }
            case -2: {
                b = 126;
                break;
            }
            case -1: {
                b = 127;
                break;
            }
        }
        byte[] array3;
        if (array2 == null) {
            array3 = null;
        }
        else {
            array3 = new byte[n3];
            for (int i = 0; i < n3; ++i) {
                byte b2 = array2[i + n2];
                if (b2 < 0) {
                    b2 = (byte)(-b2 | 0xFFFFFF80);
                }
                else if (b2 == 0 && (b2 = b) > 61) {
                    b2 &= 0x1;
                }
                array3[i] = b2;
            }
        }
        if (n == 0 && n2 == 0 && n3 == array.length) {
            this.setPara(array, b, array3);
        }
        else {
            final char[] array4 = new char[n3];
            System.arraycopy(array, n, array4, 0, n3);
            this.setPara(array4, b, array3);
        }
    }
    
    public boolean isMixed() {
        return !this.isLeftToRight() && !this.isRightToLeft();
    }
    
    public boolean isLeftToRight() {
        return this.getDirection() == 0 && (this.paraLevel & 0x1) == 0x0;
    }
    
    public boolean isRightToLeft() {
        return this.getDirection() == 1 && (this.paraLevel & 0x1) == 0x1;
    }
    
    public boolean baseIsLeftToRight() {
        return this.getParaLevel() == 0;
    }
    
    public int getBaseLevel() {
        return this.getParaLevel();
    }
    
    private void getLogicalToVisualRunsMap() {
        if (this.isGoodLogicalToVisualRunsMap) {
            return;
        }
        final int countRuns = this.countRuns();
        if (this.logicalToVisualRunsMap == null || this.logicalToVisualRunsMap.length < countRuns) {
            this.logicalToVisualRunsMap = new int[countRuns];
        }
        final long[] array = new long[countRuns];
        for (int i = 0; i < countRuns; ++i) {
            array[i] = ((long)this.runs[i].start << 32) + i;
        }
        Arrays.sort(array);
        for (int j = 0; j < countRuns; ++j) {
            this.logicalToVisualRunsMap[j] = (int)(array[j] & -1L);
        }
        this.isGoodLogicalToVisualRunsMap = true;
    }
    
    public int getRunLevel(final int n) {
        this.verifyValidParaOrLine();
        BidiLine.getRuns(this);
        if (n < 0 || n >= this.runCount) {
            return this.getParaLevel();
        }
        this.getLogicalToVisualRunsMap();
        return this.runs[this.logicalToVisualRunsMap[n]].level;
    }
    
    public int getRunStart(final int n) {
        this.verifyValidParaOrLine();
        BidiLine.getRuns(this);
        if (this.runCount == 1) {
            return 0;
        }
        if (n == this.runCount) {
            return this.length;
        }
        this.verifyIndex(n, 0, this.runCount);
        this.getLogicalToVisualRunsMap();
        return this.runs[this.logicalToVisualRunsMap[n]].start;
    }
    
    public int getRunLimit(final int n) {
        this.verifyValidParaOrLine();
        BidiLine.getRuns(this);
        if (this.runCount == 1) {
            return this.length;
        }
        this.verifyIndex(n, 0, this.runCount);
        this.getLogicalToVisualRunsMap();
        final int n2 = this.logicalToVisualRunsMap[n];
        return this.runs[n2].start + ((n2 == 0) ? this.runs[n2].limit : (this.runs[n2].limit - this.runs[n2 - 1].limit));
    }
    
    public static boolean requiresBidi(final char[] array, final int n, final int n2) {
        if (0 > n || n > n2 || n2 > array.length) {
            throw new IllegalArgumentException("Value start " + n + " is out of range 0 to " + n2);
        }
        for (int i = n; i < n2; ++i) {
            if (Character.isHighSurrogate(array[i]) && i < n2 - 1 && Character.isLowSurrogate(array[i + 1])) {
                if ((1 << UCharacter.getDirection(Character.codePointAt(array, i)) & 0xE022) != 0x0) {
                    return true;
                }
            }
            else if ((1 << UCharacter.getDirection(array[i]) & 0xE022) != 0x0) {
                return true;
            }
        }
        return false;
    }
    
    public static void reorderVisually(final byte[] array, final int n, final Object[] array2, final int n2, final int n3) {
        if (0 > n || array.length <= n) {
            throw new IllegalArgumentException("Value levelStart " + n + " is out of range 0 to " + (array.length - 1));
        }
        if (0 > n2 || array2.length <= n2) {
            throw new IllegalArgumentException("Value objectStart " + n + " is out of range 0 to " + (array2.length - 1));
        }
        if (0 > n3 || array2.length < n2 + n3) {
            throw new IllegalArgumentException("Value count " + n + " is out of range 0 to " + (array2.length - n2));
        }
        final byte[] array3 = new byte[n3];
        System.arraycopy(array, n, array3, 0, n3);
        final int[] reorderVisual = reorderVisual(array3);
        final Object[] array4 = new Object[n3];
        System.arraycopy(array2, n2, array4, 0, n3);
        for (int i = 0; i < n3; ++i) {
            array2[n2 + i] = array4[reorderVisual[i]];
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append("[dir: ");
        sb.append(this.direction);
        sb.append(" baselevel: ");
        sb.append(this.paraLevel);
        sb.append(" length: ");
        sb.append(this.length);
        sb.append(" runs: ");
        if (this.levels == null) {
            sb.append("none");
        }
        else {
            sb.append('[');
            sb.append(this.levels[0]);
            for (int i = 1; i < this.levels.length; ++i) {
                sb.append(' ');
                sb.append(this.levels[i]);
            }
            sb.append(']');
        }
        sb.append(" text: [0x");
        sb.append(Integer.toHexString(this.text[0]));
        for (int j = 1; j < this.text.length; ++j) {
            sb.append(" 0x");
            sb.append(Integer.toHexString(this.text[j]));
        }
        sb.append("]]");
        return sb.toString();
    }
    
    static {
        DirPropFlagMultiRuns = DirPropFlag((byte)31);
        DirPropFlagLR = new int[] { DirPropFlag((byte)0), DirPropFlag((byte)1) };
        DirPropFlagE = new int[] { DirPropFlag((byte)11), DirPropFlag((byte)14) };
        DirPropFlagO = new int[] { DirPropFlag((byte)12), DirPropFlag((byte)15) };
        MASK_LTR = (DirPropFlag((byte)0) | DirPropFlag((byte)2) | DirPropFlag((byte)5) | DirPropFlag((byte)11) | DirPropFlag((byte)12));
        MASK_RTL = (DirPropFlag((byte)1) | DirPropFlag((byte)13) | DirPropFlag((byte)14) | DirPropFlag((byte)15));
        MASK_LRX = (DirPropFlag((byte)11) | DirPropFlag((byte)12));
        MASK_RLX = (DirPropFlag((byte)14) | DirPropFlag((byte)15));
        MASK_EXPLICIT = (BidiBase.MASK_LRX | BidiBase.MASK_RLX | DirPropFlag((byte)16));
        MASK_BN_EXPLICIT = (DirPropFlag((byte)18) | BidiBase.MASK_EXPLICIT);
        MASK_B_S = (DirPropFlag((byte)7) | DirPropFlag((byte)8));
        MASK_WS = (BidiBase.MASK_B_S | DirPropFlag((byte)9) | BidiBase.MASK_BN_EXPLICIT);
        MASK_N = (DirPropFlag((byte)10) | BidiBase.MASK_WS);
        MASK_POSSIBLE_N = (DirPropFlag((byte)6) | DirPropFlag((byte)3) | DirPropFlag((byte)4) | BidiBase.MASK_N);
        MASK_EMBEDDING = (DirPropFlag((byte)17) | BidiBase.MASK_POSSIBLE_N);
        groupProp = new short[] { 0, 1, 2, 7, 8, 3, 9, 6, 5, 4, 4, 10, 10, 12, 10, 10, 10, 11, 10 };
        impTabProps = new short[][] { { 1, 2, 4, 5, 7, 15, 17, 7, 9, 7, 0, 7, 3, 4 }, { 1, 34, 36, 37, 39, 47, 49, 39, 41, 39, 1, 1, 35, 0 }, { 33, 2, 36, 37, 39, 47, 49, 39, 41, 39, 2, 2, 35, 1 }, { 33, 34, 38, 38, 40, 48, 49, 40, 40, 40, 3, 3, 3, 1 }, { 33, 34, 4, 37, 39, 47, 49, 74, 11, 74, 4, 4, 35, 2 }, { 33, 34, 36, 5, 39, 47, 49, 39, 41, 76, 5, 5, 35, 3 }, { 33, 34, 6, 6, 40, 48, 49, 40, 40, 77, 6, 6, 35, 3 }, { 33, 34, 36, 37, 7, 47, 49, 7, 78, 7, 7, 7, 35, 4 }, { 33, 34, 38, 38, 8, 48, 49, 8, 8, 8, 8, 8, 35, 4 }, { 33, 34, 4, 37, 7, 47, 49, 7, 9, 7, 9, 9, 35, 4 }, { 97, 98, 4, 101, 135, 111, 113, 135, 142, 135, 10, 135, 99, 2 }, { 33, 34, 4, 37, 39, 47, 49, 39, 11, 39, 11, 11, 35, 2 }, { 97, 98, 100, 5, 135, 111, 113, 135, 142, 135, 12, 135, 99, 3 }, { 97, 98, 6, 6, 136, 112, 113, 136, 136, 136, 13, 136, 99, 3 }, { 33, 34, 132, 37, 7, 47, 49, 7, 14, 7, 14, 14, 35, 4 }, { 33, 34, 36, 37, 39, 15, 49, 39, 41, 39, 15, 39, 35, 5 }, { 33, 34, 38, 38, 40, 16, 49, 40, 40, 40, 16, 40, 35, 5 }, { 33, 34, 36, 37, 39, 47, 17, 39, 41, 39, 17, 39, 35, 6 } };
        impTabL_DEFAULT = new byte[][] { { 0, 1, 0, 2, 0, 0, 0, 0 }, { 0, 1, 3, 3, 20, 20, 0, 1 }, { 0, 1, 0, 2, 21, 21, 0, 2 }, { 0, 1, 3, 3, 20, 20, 0, 2 }, { 32, 1, 3, 3, 4, 4, 32, 1 }, { 32, 1, 32, 2, 5, 5, 32, 1 } };
        impTabR_DEFAULT = new byte[][] { { 1, 0, 2, 2, 0, 0, 0, 0 }, { 1, 0, 1, 3, 20, 20, 0, 1 }, { 1, 0, 2, 2, 0, 0, 0, 1 }, { 1, 0, 1, 3, 5, 5, 0, 1 }, { 33, 0, 33, 3, 4, 4, 0, 0 }, { 1, 0, 1, 3, 5, 5, 0, 0 } };
        impAct0 = new short[] { 0, 1, 2, 3, 4, 5, 6 };
        impTab_DEFAULT = new ImpTabPair(BidiBase.impTabL_DEFAULT, BidiBase.impTabR_DEFAULT, BidiBase.impAct0, BidiBase.impAct0);
        impTabL_NUMBERS_SPECIAL = new byte[][] { { 0, 2, 1, 1, 0, 0, 0, 0 }, { 0, 2, 1, 1, 0, 0, 0, 2 }, { 0, 2, 4, 4, 19, 0, 0, 1 }, { 32, 2, 4, 4, 3, 3, 32, 1 }, { 0, 2, 4, 4, 19, 19, 0, 2 } };
        impTab_NUMBERS_SPECIAL = new ImpTabPair(BidiBase.impTabL_NUMBERS_SPECIAL, BidiBase.impTabR_DEFAULT, BidiBase.impAct0, BidiBase.impAct0);
        impTabL_GROUP_NUMBERS_WITH_R = new byte[][] { { 0, 3, 17, 17, 0, 0, 0, 0 }, { 32, 3, 1, 1, 2, 32, 32, 2 }, { 32, 3, 1, 1, 2, 32, 32, 1 }, { 0, 3, 5, 5, 20, 0, 0, 1 }, { 32, 3, 5, 5, 4, 32, 32, 1 }, { 0, 3, 5, 5, 20, 0, 0, 2 } };
        impTabR_GROUP_NUMBERS_WITH_R = new byte[][] { { 2, 0, 1, 1, 0, 0, 0, 0 }, { 2, 0, 1, 1, 0, 0, 0, 1 }, { 2, 0, 20, 20, 19, 0, 0, 1 }, { 34, 0, 4, 4, 3, 0, 0, 0 }, { 34, 0, 4, 4, 3, 0, 0, 1 } };
        impTab_GROUP_NUMBERS_WITH_R = new ImpTabPair(BidiBase.impTabL_GROUP_NUMBERS_WITH_R, BidiBase.impTabR_GROUP_NUMBERS_WITH_R, BidiBase.impAct0, BidiBase.impAct0);
        impTabL_INVERSE_NUMBERS_AS_L = new byte[][] { { 0, 1, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 20, 20, 0, 1 }, { 0, 1, 0, 0, 21, 21, 0, 2 }, { 0, 1, 0, 0, 20, 20, 0, 2 }, { 32, 1, 32, 32, 4, 4, 32, 1 }, { 32, 1, 32, 32, 5, 5, 32, 1 } };
        impTabR_INVERSE_NUMBERS_AS_L = new byte[][] { { 1, 0, 1, 1, 0, 0, 0, 0 }, { 1, 0, 1, 1, 20, 20, 0, 1 }, { 1, 0, 1, 1, 0, 0, 0, 1 }, { 1, 0, 1, 1, 5, 5, 0, 1 }, { 33, 0, 33, 33, 4, 4, 0, 0 }, { 1, 0, 1, 1, 5, 5, 0, 0 } };
        impTab_INVERSE_NUMBERS_AS_L = new ImpTabPair(BidiBase.impTabL_INVERSE_NUMBERS_AS_L, BidiBase.impTabR_INVERSE_NUMBERS_AS_L, BidiBase.impAct0, BidiBase.impAct0);
        impTabR_INVERSE_LIKE_DIRECT = new byte[][] { { 1, 0, 2, 2, 0, 0, 0, 0 }, { 1, 0, 1, 2, 19, 19, 0, 1 }, { 1, 0, 2, 2, 0, 0, 0, 1 }, { 33, 48, 6, 4, 3, 3, 48, 0 }, { 33, 48, 6, 4, 5, 5, 48, 3 }, { 33, 48, 6, 4, 5, 5, 48, 2 }, { 33, 48, 6, 4, 3, 3, 48, 1 } };
        impAct1 = new short[] { 0, 1, 11, 12 };
        impTab_INVERSE_LIKE_DIRECT = new ImpTabPair(BidiBase.impTabL_DEFAULT, BidiBase.impTabR_INVERSE_LIKE_DIRECT, BidiBase.impAct0, BidiBase.impAct1);
        impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS = new byte[][] { { 0, 99, 0, 1, 0, 0, 0, 0 }, { 0, 99, 0, 1, 18, 48, 0, 4 }, { 32, 99, 32, 1, 2, 48, 32, 3 }, { 0, 99, 85, 86, 20, 48, 0, 3 }, { 48, 67, 85, 86, 4, 48, 48, 3 }, { 48, 67, 5, 86, 20, 48, 48, 4 }, { 48, 67, 85, 6, 20, 48, 48, 4 } };
        impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS = new byte[][] { { 19, 0, 1, 1, 0, 0, 0, 0 }, { 35, 0, 1, 1, 2, 64, 0, 1 }, { 35, 0, 1, 1, 2, 64, 0, 0 }, { 3, 0, 3, 54, 20, 64, 0, 1 }, { 83, 64, 5, 54, 4, 64, 64, 0 }, { 83, 64, 5, 54, 4, 64, 64, 1 }, { 83, 64, 6, 6, 4, 64, 64, 3 } };
        impAct2 = new short[] { 0, 1, 7, 8, 9, 10 };
        impTab_INVERSE_LIKE_DIRECT_WITH_MARKS = new ImpTabPair(BidiBase.impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS, BidiBase.impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, BidiBase.impAct0, BidiBase.impAct2);
        impTab_INVERSE_FOR_NUMBERS_SPECIAL = new ImpTabPair(BidiBase.impTabL_NUMBERS_SPECIAL, BidiBase.impTabR_INVERSE_LIKE_DIRECT, BidiBase.impAct0, BidiBase.impAct1);
        impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = new byte[][] { { 0, 98, 1, 1, 0, 0, 0, 0 }, { 0, 98, 1, 1, 0, 48, 0, 4 }, { 0, 98, 84, 84, 19, 48, 0, 3 }, { 48, 66, 84, 84, 3, 48, 48, 3 }, { 48, 66, 4, 4, 19, 48, 48, 4 } };
        impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = new ImpTabPair(BidiBase.impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS, BidiBase.impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, BidiBase.impAct0, BidiBase.impAct2);
    }
    
    class Point
    {
        int pos;
        int flag;
    }
    
    class InsertPoints
    {
        int size;
        int confirmed;
        Point[] points;
        
        InsertPoints() {
            this.points = new Point[0];
        }
    }
    
    private static class ImpTabPair
    {
        byte[][][] imptab;
        short[][] impact;
        
        ImpTabPair(final byte[][] array, final byte[][] array2, final short[] array3, final short[] array4) {
            this.imptab = new byte[][][] { array, array2 };
            this.impact = new short[][] { array3, array4 };
        }
    }
    
    private class LevState
    {
        byte[][] impTab;
        short[] impAct;
        int startON;
        int startL2EN;
        int lastStrongRTL;
        short state;
        byte runLevel;
    }
    
    private static class TextAttributeConstants
    {
        private static final Class<?> clazz;
        static final AttributedCharacterIterator.Attribute RUN_DIRECTION;
        static final AttributedCharacterIterator.Attribute NUMERIC_SHAPING;
        static final AttributedCharacterIterator.Attribute BIDI_EMBEDDING;
        static final Boolean RUN_DIRECTION_LTR;
        
        private static Class<?> getClass(final String s) {
            try {
                return Class.forName(s, true, null);
            }
            catch (final ClassNotFoundException ex) {
                return null;
            }
        }
        
        private static Object getStaticField(final Class<?> clazz, final String s) {
            try {
                return clazz.getField(s).get(null);
            }
            catch (final NoSuchFieldException | IllegalAccessException ex) {
                throw new AssertionError(ex);
            }
        }
        
        private static AttributedCharacterIterator.Attribute getTextAttribute(final String s) {
            if (TextAttributeConstants.clazz == null) {
                return new AttributedCharacterIterator.Attribute(s) {};
            }
            return (AttributedCharacterIterator.Attribute)getStaticField(TextAttributeConstants.clazz, s);
        }
        
        static {
            clazz = getClass("java.awt.font.TextAttribute");
            RUN_DIRECTION = getTextAttribute("RUN_DIRECTION");
            NUMERIC_SHAPING = getTextAttribute("NUMERIC_SHAPING");
            BIDI_EMBEDDING = getTextAttribute("BIDI_EMBEDDING");
            RUN_DIRECTION_LTR = (Boolean)((TextAttributeConstants.clazz == null) ? Boolean.FALSE : getStaticField(TextAttributeConstants.clazz, "RUN_DIRECTION_LTR"));
        }
    }
    
    private static class NumericShapings
    {
        private static final Class<?> clazz;
        private static final Method shapeMethod;
        
        private static Class<?> getClass(final String s) {
            try {
                return Class.forName(s, true, null);
            }
            catch (final ClassNotFoundException ex) {
                return null;
            }
        }
        
        private static Method getMethod(final Class<?> clazz, final String s, final Class<?>... array) {
            if (clazz != null) {
                try {
                    return clazz.getMethod(s, (Class[])array);
                }
                catch (final NoSuchMethodException ex) {
                    throw new AssertionError((Object)ex);
                }
            }
            return null;
        }
        
        static void shape(final Object o, final char[] array, final int n, final int n2) {
            if (NumericShapings.shapeMethod == null) {
                throw new AssertionError((Object)"Should not get here");
            }
            try {
                NumericShapings.shapeMethod.invoke(o, array, n, n2);
            }
            catch (final InvocationTargetException ex) {
                final Throwable cause = ex.getCause();
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
                throw new AssertionError((Object)ex);
            }
            catch (final IllegalAccessException ex2) {
                throw new AssertionError((Object)ex2);
            }
        }
        
        static {
            clazz = getClass("java.awt.font.NumericShaper");
            shapeMethod = getMethod(NumericShapings.clazz, "shape", char[].class, Integer.TYPE, Integer.TYPE);
        }
    }
}
