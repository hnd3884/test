package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.util.Arrays;

class Instruction
{
    protected byte[] bytes;
    protected int pc;
    protected int bc;
    protected int w;
    protected int length;
    protected boolean special;
    private static final byte[][] BC_LENGTH;
    private static final byte[][] BC_INDEX;
    private static final byte[][] BC_TAG;
    private static final byte[][] BC_BRANCH;
    private static final byte[][] BC_SLOT;
    private static final byte[][] BC_CON;
    private static final String[] BC_NAME;
    private static final String[][] BC_FORMAT;
    private static int BW;
    
    protected Instruction(final byte[] array, final int n, final int n2, final int n3, final int n4) {
        this.reset(array, n, n2, n3, n4);
    }
    
    private void reset(final byte[] bytes, final int pc, final int bc, final int w, final int length) {
        this.bytes = bytes;
        this.pc = pc;
        this.bc = bc;
        this.w = w;
        this.length = length;
    }
    
    public int getBC() {
        return this.bc;
    }
    
    public boolean isWide() {
        return this.w != 0;
    }
    
    public byte[] getBytes() {
        return this.bytes;
    }
    
    public int getPC() {
        return this.pc;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getNextPC() {
        return this.pc + this.length;
    }
    
    public Instruction next() {
        final int n = this.pc + this.length;
        if (n == this.bytes.length) {
            return null;
        }
        return at(this.bytes, n, this);
    }
    
    public boolean isNonstandard() {
        return isNonstandard(this.bc);
    }
    
    public void setNonstandardLength(final int length) {
        assert this.isNonstandard();
        this.length = length;
    }
    
    public Instruction forceNextPC(final int n) {
        return new Instruction(this.bytes, this.pc, -1, -1, n - this.pc);
    }
    
    public static Instruction at(final byte[] array, final int n) {
        return at(array, n, null);
    }
    
    public static Instruction at(final byte[] array, final int n, final Instruction instruction) {
        int n2 = getByte(array, n);
        int n3 = 0;
        int n4 = Instruction.BC_LENGTH[n3][n2];
        if (n4 == 0) {
            switch (n2) {
                case 196: {
                    n2 = getByte(array, n + 1);
                    n3 = 1;
                    n4 = Instruction.BC_LENGTH[n3][n2];
                    if (n4 == 0) {
                        n4 = 1;
                        break;
                    }
                    break;
                }
                case 170: {
                    return new TableSwitch(array, n);
                }
                case 171: {
                    return new LookupSwitch(array, n);
                }
                default: {
                    n4 = 1;
                    break;
                }
            }
        }
        assert n4 > 0;
        assert n + n4 <= array.length;
        if (instruction != null && !instruction.special) {
            instruction.reset(array, n, n2, n3, n4);
            return instruction;
        }
        return new Instruction(array, n, n2, n3, n4);
    }
    
    public byte getCPTag() {
        return Instruction.BC_TAG[this.w][this.bc];
    }
    
    public int getCPIndex() {
        final byte b = Instruction.BC_INDEX[this.w][this.bc];
        if (b == 0) {
            return -1;
        }
        assert this.w == 0;
        if (this.length == 2) {
            return getByte(this.bytes, this.pc + b);
        }
        return getShort(this.bytes, this.pc + b);
    }
    
    public void setCPIndex(final int n) {
        final byte b = Instruction.BC_INDEX[this.w][this.bc];
        assert b != 0;
        if (this.length == 2) {
            setByte(this.bytes, this.pc + b, n);
        }
        else {
            setShort(this.bytes, this.pc + b, n);
        }
        assert this.getCPIndex() == n;
    }
    
    public ConstantPool.Entry getCPRef(final ConstantPool.Entry[] array) {
        final int cpIndex = this.getCPIndex();
        return (cpIndex < 0) ? null : array[cpIndex];
    }
    
    public int getLocalSlot() {
        final byte b = Instruction.BC_SLOT[this.w][this.bc];
        if (b == 0) {
            return -1;
        }
        if (this.w == 0) {
            return getByte(this.bytes, this.pc + b);
        }
        return getShort(this.bytes, this.pc + b);
    }
    
    public int getBranchLabel() {
        final byte b = Instruction.BC_BRANCH[this.w][this.bc];
        if (b == 0) {
            return -1;
        }
        assert this.w == 0;
        assert this.length == 5;
        int int1;
        if (this.length == 3) {
            int1 = (short)getShort(this.bytes, this.pc + b);
        }
        else {
            int1 = getInt(this.bytes, this.pc + b);
        }
        assert int1 + this.pc >= 0;
        assert int1 + this.pc <= this.bytes.length;
        return int1 + this.pc;
    }
    
    public void setBranchLabel(final int n) {
        final byte b = Instruction.BC_BRANCH[this.w][this.bc];
        assert b != 0;
        if (this.length == 3) {
            setShort(this.bytes, this.pc + b, n - this.pc);
        }
        else {
            setInt(this.bytes, this.pc + b, n - this.pc);
        }
        assert n == this.getBranchLabel();
    }
    
    public int getConstant() {
        final byte b = Instruction.BC_CON[this.w][this.bc];
        if (b == 0) {
            return 0;
        }
        switch (this.length - b) {
            case 1: {
                return (byte)getByte(this.bytes, this.pc + b);
            }
            case 2: {
                return (short)getShort(this.bytes, this.pc + b);
            }
            default: {
                assert false;
                return 0;
            }
        }
    }
    
    public void setConstant(final int n) {
        final byte b = Instruction.BC_CON[this.w][this.bc];
        assert b != 0;
        switch (this.length - b) {
            case 1: {
                setByte(this.bytes, this.pc + b, n);
                break;
            }
            case 2: {
                setShort(this.bytes, this.pc + b, n);
                break;
            }
        }
        assert n == this.getConstant();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o.getClass() == Instruction.class && this.equals((Instruction)o);
    }
    
    @Override
    public int hashCode() {
        return 11 * (11 * (11 * (11 * (11 * 3 + Arrays.hashCode(this.bytes)) + this.pc) + this.bc) + this.w) + this.length;
    }
    
    public boolean equals(final Instruction instruction) {
        if (this.pc != instruction.pc) {
            return false;
        }
        if (this.bc != instruction.bc) {
            return false;
        }
        if (this.w != instruction.w) {
            return false;
        }
        if (this.length != instruction.length) {
            return false;
        }
        for (int i = 1; i < this.length; ++i) {
            if (this.bytes[this.pc + i] != instruction.bytes[instruction.pc + i]) {
                return false;
            }
        }
        return true;
    }
    
    static String labstr(final int n) {
        if (n >= 0 && n < 100000) {
            return (100000 + n + "").substring(1);
        }
        return n + "";
    }
    
    @Override
    public String toString() {
        return this.toString(null);
    }
    
    public String toString(final ConstantPool.Entry[] array) {
        String s = labstr(this.pc) + ": ";
        if (this.bc >= 202) {
            return s + Integer.toHexString(this.bc);
        }
        if (this.w == 1) {
            s += "wide ";
        }
        final String s2 = (this.bc < Instruction.BC_NAME.length) ? Instruction.BC_NAME[this.bc] : null;
        if (s2 == null) {
            return s + "opcode#" + this.bc;
        }
        String s3 = s + s2;
        final byte cpTag = this.getCPTag();
        if (cpTag != 0) {
            s3 = s3 + " " + ConstantPool.tagName(cpTag) + ":";
        }
        final int cpIndex = this.getCPIndex();
        if (cpIndex >= 0) {
            s3 += ((array == null) ? ("" + cpIndex) : ("=" + array[cpIndex].stringValue()));
        }
        final int localSlot = this.getLocalSlot();
        if (localSlot >= 0) {
            s3 = s3 + " Local:" + localSlot;
        }
        final int branchLabel = this.getBranchLabel();
        if (branchLabel >= 0) {
            s3 = s3 + " To:" + labstr(branchLabel);
        }
        final int constant = this.getConstant();
        if (constant != 0) {
            s3 = s3 + " Con:" + constant;
        }
        return s3;
    }
    
    public int getIntAt(final int n) {
        return getInt(this.bytes, this.pc + n);
    }
    
    public int getShortAt(final int n) {
        return getShort(this.bytes, this.pc + n);
    }
    
    public int getByteAt(final int n) {
        return getByte(this.bytes, this.pc + n);
    }
    
    public static int getInt(final byte[] array, final int n) {
        return (getShort(array, n + 0) << 16) + (getShort(array, n + 2) << 0);
    }
    
    public static int getShort(final byte[] array, final int n) {
        return (getByte(array, n + 0) << 8) + (getByte(array, n + 1) << 0);
    }
    
    public static int getByte(final byte[] array, final int n) {
        return array[n] & 0xFF;
    }
    
    public static void setInt(final byte[] array, final int n, final int n2) {
        setShort(array, n + 0, n2 >> 16);
        setShort(array, n + 2, n2 >> 0);
    }
    
    public static void setShort(final byte[] array, final int n, final int n2) {
        setByte(array, n + 0, n2 >> 8);
        setByte(array, n + 1, n2 >> 0);
    }
    
    public static void setByte(final byte[] array, final int n, final int n2) {
        array[n] = (byte)n2;
    }
    
    public static boolean isNonstandard(final int n) {
        return Instruction.BC_LENGTH[0][n] < 0;
    }
    
    public static int opLength(final int n) {
        final byte b = Instruction.BC_LENGTH[0][n];
        assert b > 0;
        return b;
    }
    
    public static int opWideLength(final int n) {
        final byte b = Instruction.BC_LENGTH[1][n];
        assert b > 0;
        return b;
    }
    
    public static boolean isLocalSlotOp(final int n) {
        return n < Instruction.BC_SLOT[0].length && Instruction.BC_SLOT[0][n] > 0;
    }
    
    public static boolean isBranchOp(final int n) {
        return n < Instruction.BC_BRANCH[0].length && Instruction.BC_BRANCH[0][n] > 0;
    }
    
    public static boolean isCPRefOp(final int n) {
        return (n < Instruction.BC_INDEX[0].length && Instruction.BC_INDEX[0][n] > 0) || (n >= 233 && n < 242) || (n == 242 || n == 243);
    }
    
    public static byte getCPRefOpTag(final int n) {
        if (n < Instruction.BC_INDEX[0].length && Instruction.BC_INDEX[0][n] > 0) {
            return Instruction.BC_TAG[0][n];
        }
        if (n >= 233 && n < 242) {
            return 51;
        }
        if (n == 243 || n == 242) {
            return 11;
        }
        return 0;
    }
    
    public static boolean isFieldOp(final int n) {
        return n >= 178 && n <= 181;
    }
    
    public static boolean isInvokeInitOp(final int n) {
        return n >= 230 && n < 233;
    }
    
    public static boolean isSelfLinkerOp(final int n) {
        return n >= 202 && n < 230;
    }
    
    public static String byteName(final int n) {
        String s = null;
        if (n < Instruction.BC_NAME.length && Instruction.BC_NAME[n] != null) {
            s = Instruction.BC_NAME[n];
        }
        else if (isSelfLinkerOp(n)) {
            int n2 = n - 202;
            final boolean b = n2 >= 14;
            if (b) {
                n2 -= 14;
            }
            final boolean b2 = n2 >= 7;
            if (b2) {
                n2 -= 7;
            }
            final int n3 = 178 + n2;
            assert n3 >= 178 && n3 <= 184;
            String s2 = Instruction.BC_NAME[n3] + (b ? "_super" : "_this");
            if (b2) {
                s2 = "aload_0&" + s2;
            }
            s = "*" + s2;
        }
        else if (isInvokeInitOp(n)) {
            final int n4 = n - 230;
            switch (n4) {
                case 0: {
                    s = "*invokespecial_init_this";
                    break;
                }
                case 1: {
                    s = "*invokespecial_init_super";
                    break;
                }
                default: {
                    assert n4 == 2;
                    s = "*invokespecial_init_new";
                    break;
                }
            }
        }
        else {
            switch (n) {
                case 234: {
                    s = "*ildc";
                    break;
                }
                case 235: {
                    s = "*fldc";
                    break;
                }
                case 237: {
                    s = "*ildc_w";
                    break;
                }
                case 238: {
                    s = "*fldc_w";
                    break;
                }
                case 239: {
                    s = "*dldc2_w";
                    break;
                }
                case 233: {
                    s = "*cldc";
                    break;
                }
                case 236: {
                    s = "*cldc_w";
                    break;
                }
                case 240: {
                    s = "*qldc";
                    break;
                }
                case 241: {
                    s = "*qldc_w";
                    break;
                }
                case 254: {
                    s = "*byte_escape";
                    break;
                }
                case 253: {
                    s = "*ref_escape";
                    break;
                }
                case 255: {
                    s = "*end";
                    break;
                }
                default: {
                    s = "*bc#" + n;
                    break;
                }
            }
        }
        return s;
    }
    
    private static void def(final String s, final int n) {
        def(s, n, n);
    }
    
    private static void def(String s, final int n, final int n2) {
        final String[] array = { s, null };
        if (s.indexOf(119) > 0) {
            array[1] = s.substring(s.indexOf(119));
            array[0] = s.substring(0, s.indexOf(119));
        }
        for (int i = 0; i <= 1; ++i) {
            s = array[i];
            if (s != null) {
                final int length = s.length();
                final int max = Math.max(0, s.indexOf(107));
                int n3 = 0;
                final int max2 = Math.max(0, s.indexOf(111));
                final int max3 = Math.max(0, s.indexOf(108));
                final int max4 = Math.max(0, s.indexOf(120));
                if (max > 0 && max + 1 < length) {
                    switch (s.charAt(max + 1)) {
                        case 'c': {
                            n3 = 7;
                            break;
                        }
                        case 'k': {
                            n3 = 51;
                            break;
                        }
                        case 'f': {
                            n3 = 9;
                            break;
                        }
                        case 'm': {
                            n3 = 10;
                            break;
                        }
                        case 'i': {
                            n3 = 11;
                            break;
                        }
                        case 'y': {
                            n3 = 18;
                            break;
                        }
                    }
                    assert n3 != 0;
                }
                else if (max > 0 && length == 2) {
                    assert n == 18;
                    n3 = 51;
                }
                for (int j = n; j <= n2; ++j) {
                    Instruction.BC_FORMAT[i][j] = s;
                    assert Instruction.BC_LENGTH[i][j] == -1;
                    Instruction.BC_LENGTH[i][j] = (byte)length;
                    Instruction.BC_INDEX[i][j] = (byte)max;
                    Instruction.BC_TAG[i][j] = (byte)n3;
                    assert n3 == 0;
                    Instruction.BC_BRANCH[i][j] = (byte)max2;
                    Instruction.BC_SLOT[i][j] = (byte)max3;
                    assert max3 == 0;
                    assert max == 0;
                    assert max == 0;
                    Instruction.BC_CON[i][j] = (byte)max4;
                }
            }
        }
    }
    
    public static void opcodeChecker(final byte[] array, final ConstantPool.Entry[] array2, final Package.Version version) throws FormatException {
        for (Instruction instruction = at(array, 0); instruction != null; instruction = instruction.next()) {
            final int bc = instruction.getBC();
            if (bc < 0 || bc > 201) {
                throw new FormatException("illegal opcode: " + bc + " " + instruction);
            }
            final ConstantPool.Entry cpRef = instruction.getCPRef(array2);
            if (cpRef != null) {
                final byte cpTag = instruction.getCPTag();
                int tagMatches = cpRef.tagMatches(cpTag) ? 1 : 0;
                if (tagMatches == 0 && (instruction.bc == 183 || instruction.bc == 184) && cpRef.tagMatches(11) && version.greaterThan(Constants.JAVA7_MAX_CLASS_VERSION)) {
                    tagMatches = 1;
                }
                if (tagMatches == 0) {
                    throw new FormatException("illegal reference, expected type=" + ConstantPool.tagName(cpTag) + ": " + instruction.toString(array2));
                }
            }
        }
    }
    
    static {
        BC_LENGTH = new byte[2][256];
        BC_INDEX = new byte[2][256];
        BC_TAG = new byte[2][256];
        BC_BRANCH = new byte[2][256];
        BC_SLOT = new byte[2][256];
        BC_CON = new byte[2][256];
        BC_NAME = new String[256];
        BC_FORMAT = new String[2][202];
        for (int i = 0; i < 202; ++i) {
            Instruction.BC_LENGTH[0][i] = -1;
            Instruction.BC_LENGTH[1][i] = -1;
        }
        def("b", 0, 15);
        def("bx", 16);
        def("bxx", 17);
        def("bk", 18);
        def("bkk", 19, 20);
        def("blwbll", 21, 25);
        def("b", 26, 53);
        def("blwbll", 54, 58);
        def("b", 59, 131);
        def("blxwbllxx", 132);
        def("b", 133, 152);
        def("boo", 153, 168);
        def("blwbll", 169);
        def("", 170, 171);
        def("b", 172, 177);
        def("bkf", 178, 181);
        def("bkm", 182, 184);
        def("bkixx", 185);
        def("bkyxx", 186);
        def("bkc", 187);
        def("bx", 188);
        def("bkc", 189);
        def("b", 190, 191);
        def("bkc", 192, 193);
        def("b", 194, 195);
        def("", 196);
        def("bkcx", 197);
        def("boo", 198, 199);
        def("boooo", 200, 201);
        for (int j = 0; j < 202; ++j) {
            if (Instruction.BC_LENGTH[0][j] != -1) {
                if (Instruction.BC_LENGTH[1][j] == -1) {
                    Instruction.BC_LENGTH[1][j] = (byte)(1 + Instruction.BC_LENGTH[0][j]);
                }
            }
        }
        String substring = "nop aconst_null iconst_m1 iconst_0 iconst_1 iconst_2 iconst_3 iconst_4 iconst_5 lconst_0 lconst_1 fconst_0 fconst_1 fconst_2 dconst_0 dconst_1 bipush sipush ldc ldc_w ldc2_w iload lload fload dload aload iload_0 iload_1 iload_2 iload_3 lload_0 lload_1 lload_2 lload_3 fload_0 fload_1 fload_2 fload_3 dload_0 dload_1 dload_2 dload_3 aload_0 aload_1 aload_2 aload_3 iaload laload faload daload aaload baload caload saload istore lstore fstore dstore astore istore_0 istore_1 istore_2 istore_3 lstore_0 lstore_1 lstore_2 lstore_3 fstore_0 fstore_1 fstore_2 fstore_3 dstore_0 dstore_1 dstore_2 dstore_3 astore_0 astore_1 astore_2 astore_3 iastore lastore fastore dastore aastore bastore castore sastore pop pop2 dup dup_x1 dup_x2 dup2 dup2_x1 dup2_x2 swap iadd ladd fadd dadd isub lsub fsub dsub imul lmul fmul dmul idiv ldiv fdiv ddiv irem lrem frem drem ineg lneg fneg dneg ishl lshl ishr lshr iushr lushr iand land ior lor ixor lxor iinc i2l i2f i2d l2i l2f l2d f2i f2l f2d d2i d2l d2f i2b i2c i2s lcmp fcmpl fcmpg dcmpl dcmpg ifeq ifne iflt ifge ifgt ifle if_icmpeq if_icmpne if_icmplt if_icmpge if_icmpgt if_icmple if_acmpeq if_acmpne goto jsr ret tableswitch lookupswitch ireturn lreturn freturn dreturn areturn return getstatic putstatic getfield putfield invokevirtual invokespecial invokestatic invokeinterface invokedynamic new newarray anewarray arraylength athrow checkcast instanceof monitorenter monitorexit wide multianewarray ifnull ifnonnull goto_w jsr_w ";
        int index;
        for (int n = 0; substring.length() > 0; substring = substring.substring(index + 1), ++n) {
            index = substring.indexOf(32);
            Instruction.BC_NAME[n] = substring.substring(0, index);
        }
        Instruction.BW = 4;
    }
    
    public abstract static class Switch extends Instruction
    {
        protected int apc;
        
        public abstract int getCaseCount();
        
        public abstract int getCaseValue(final int p0);
        
        public abstract int getCaseLabel(final int p0);
        
        public abstract void setCaseCount(final int p0);
        
        public abstract void setCaseValue(final int p0, final int p1);
        
        public abstract void setCaseLabel(final int p0, final int p1);
        
        protected abstract int getLength(final int p0);
        
        public int getDefaultLabel() {
            return this.intAt(0) + this.pc;
        }
        
        public void setDefaultLabel(final int n) {
            this.setIntAt(0, n - this.pc);
        }
        
        protected int intAt(final int n) {
            return Instruction.getInt(this.bytes, this.apc + n * 4);
        }
        
        protected void setIntAt(final int n, final int n2) {
            Instruction.setInt(this.bytes, this.apc + n * 4, n2);
        }
        
        protected Switch(final byte[] array, final int n, final int n2) {
            super(array, n, n2, 0, 0);
            this.apc = alignPC(n + 1);
            this.special = true;
            this.length = this.getLength(this.getCaseCount());
        }
        
        public int getAlignedPC() {
            return this.apc;
        }
        
        @Override
        public String toString() {
            String s = super.toString() + " Default:" + Instruction.labstr(this.getDefaultLabel());
            for (int caseCount = this.getCaseCount(), i = 0; i < caseCount; ++i) {
                s = s + "\n\tCase " + this.getCaseValue(i) + ":" + Instruction.labstr(this.getCaseLabel(i));
            }
            return s;
        }
        
        public static int alignPC(int n) {
            while (n % 4 != 0) {
                ++n;
            }
            return n;
        }
    }
    
    public static class TableSwitch extends Switch
    {
        public int getLowCase() {
            return this.intAt(1);
        }
        
        public int getHighCase() {
            return this.intAt(2);
        }
        
        @Override
        public int getCaseCount() {
            return this.intAt(2) - this.intAt(1) + 1;
        }
        
        @Override
        public int getCaseValue(final int n) {
            return this.getLowCase() + n;
        }
        
        @Override
        public int getCaseLabel(final int n) {
            return this.intAt(3 + n) + this.pc;
        }
        
        public void setLowCase(final int n) {
            this.setIntAt(1, n);
        }
        
        public void setHighCase(final int n) {
            this.setIntAt(2, n);
        }
        
        @Override
        public void setCaseLabel(final int n, final int n2) {
            this.setIntAt(3 + n, n2 - this.pc);
        }
        
        @Override
        public void setCaseCount(final int n) {
            this.setHighCase(this.getLowCase() + n - 1);
            this.length = this.getLength(n);
        }
        
        @Override
        public void setCaseValue(final int n, final int lowCase) {
            if (n != 0) {
                throw new UnsupportedOperationException();
            }
            final int caseCount = this.getCaseCount();
            this.setLowCase(lowCase);
            this.setCaseCount(caseCount);
        }
        
        TableSwitch(final byte[] array, final int n) {
            super(array, n, 170);
        }
        
        @Override
        protected int getLength(final int n) {
            return this.apc - this.pc + (3 + n) * 4;
        }
    }
    
    public static class LookupSwitch extends Switch
    {
        @Override
        public int getCaseCount() {
            return this.intAt(1);
        }
        
        @Override
        public int getCaseValue(final int n) {
            return this.intAt(2 + n * 2 + 0);
        }
        
        @Override
        public int getCaseLabel(final int n) {
            return this.intAt(2 + n * 2 + 1) + this.pc;
        }
        
        @Override
        public void setCaseCount(final int n) {
            this.setIntAt(1, n);
            this.length = this.getLength(n);
        }
        
        @Override
        public void setCaseValue(final int n, final int n2) {
            this.setIntAt(2 + n * 2 + 0, n2);
        }
        
        @Override
        public void setCaseLabel(final int n, final int n2) {
            this.setIntAt(2 + n * 2 + 1, n2 - this.pc);
        }
        
        LookupSwitch(final byte[] array, final int n) {
            super(array, n, 171);
        }
        
        @Override
        protected int getLength(final int n) {
            return this.apc - this.pc + (2 + n * 2) * 4;
        }
    }
    
    static class FormatException extends IOException
    {
        private static final long serialVersionUID = 3175572275651367015L;
        
        FormatException(final String s) {
            super(s);
        }
    }
}
