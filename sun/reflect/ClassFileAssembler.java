package sun.reflect;

class ClassFileAssembler implements ClassFileConstants
{
    private ByteVector vec;
    private short cpIdx;
    private int stack;
    private int maxStack;
    private int maxLocals;
    
    public ClassFileAssembler() {
        this(ByteVectorFactory.create());
    }
    
    public ClassFileAssembler(final ByteVector vec) {
        this.cpIdx = 0;
        this.stack = 0;
        this.maxStack = 0;
        this.maxLocals = 0;
        this.vec = vec;
    }
    
    public ByteVector getData() {
        return this.vec;
    }
    
    public short getLength() {
        return (short)this.vec.getLength();
    }
    
    public void emitMagicAndVersion() {
        this.emitInt(-889275714);
        this.emitShort((short)0);
        this.emitShort((short)49);
    }
    
    public void emitInt(final int n) {
        this.emitByte((byte)(n >> 24));
        this.emitByte((byte)(n >> 16 & 0xFF));
        this.emitByte((byte)(n >> 8 & 0xFF));
        this.emitByte((byte)(n & 0xFF));
    }
    
    public void emitShort(final short n) {
        this.emitByte((byte)(n >> 8 & 0xFF));
        this.emitByte((byte)(n & 0xFF));
    }
    
    void emitShort(final short n, final short n2) {
        this.vec.put(n, (byte)(n2 >> 8 & 0xFF));
        this.vec.put(n + 1, (byte)(n2 & 0xFF));
    }
    
    public void emitByte(final byte b) {
        this.vec.add(b);
    }
    
    public void append(final ClassFileAssembler classFileAssembler) {
        this.append(classFileAssembler.vec);
    }
    
    public void append(final ByteVector byteVector) {
        for (int i = 0; i < byteVector.getLength(); ++i) {
            this.emitByte(byteVector.get(i));
        }
    }
    
    public short cpi() {
        if (this.cpIdx == 0) {
            throw new RuntimeException("Illegal use of ClassFileAssembler");
        }
        return this.cpIdx;
    }
    
    public void emitConstantPoolUTF8(final String s) {
        final byte[] encode = UTF8.encode(s);
        this.emitByte((byte)1);
        this.emitShort((short)encode.length);
        for (int i = 0; i < encode.length; ++i) {
            this.emitByte(encode[i]);
        }
        ++this.cpIdx;
    }
    
    public void emitConstantPoolClass(final short n) {
        this.emitByte((byte)7);
        this.emitShort(n);
        ++this.cpIdx;
    }
    
    public void emitConstantPoolNameAndType(final short n, final short n2) {
        this.emitByte((byte)12);
        this.emitShort(n);
        this.emitShort(n2);
        ++this.cpIdx;
    }
    
    public void emitConstantPoolFieldref(final short n, final short n2) {
        this.emitByte((byte)9);
        this.emitShort(n);
        this.emitShort(n2);
        ++this.cpIdx;
    }
    
    public void emitConstantPoolMethodref(final short n, final short n2) {
        this.emitByte((byte)10);
        this.emitShort(n);
        this.emitShort(n2);
        ++this.cpIdx;
    }
    
    public void emitConstantPoolInterfaceMethodref(final short n, final short n2) {
        this.emitByte((byte)11);
        this.emitShort(n);
        this.emitShort(n2);
        ++this.cpIdx;
    }
    
    public void emitConstantPoolString(final short n) {
        this.emitByte((byte)8);
        this.emitShort(n);
        ++this.cpIdx;
    }
    
    private void incStack() {
        this.setStack(this.stack + 1);
    }
    
    private void decStack() {
        --this.stack;
    }
    
    public short getMaxStack() {
        return (short)this.maxStack;
    }
    
    public short getMaxLocals() {
        return (short)this.maxLocals;
    }
    
    public void setMaxLocals(final int maxLocals) {
        this.maxLocals = maxLocals;
    }
    
    public int getStack() {
        return this.stack;
    }
    
    public void setStack(final int stack) {
        this.stack = stack;
        if (this.stack > this.maxStack) {
            this.maxStack = this.stack;
        }
    }
    
    public void opc_aconst_null() {
        this.emitByte((byte)1);
        this.incStack();
    }
    
    public void opc_sipush(final short n) {
        this.emitByte((byte)17);
        this.emitShort(n);
        this.incStack();
    }
    
    public void opc_ldc(final byte b) {
        this.emitByte((byte)18);
        this.emitByte(b);
        this.incStack();
    }
    
    public void opc_iload_0() {
        this.emitByte((byte)26);
        if (this.maxLocals < 1) {
            this.maxLocals = 1;
        }
        this.incStack();
    }
    
    public void opc_iload_1() {
        this.emitByte((byte)27);
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        this.incStack();
    }
    
    public void opc_iload_2() {
        this.emitByte((byte)28);
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        this.incStack();
    }
    
    public void opc_iload_3() {
        this.emitByte((byte)29);
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        this.incStack();
    }
    
    public void opc_lload_0() {
        this.emitByte((byte)30);
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        this.incStack();
        this.incStack();
    }
    
    public void opc_lload_1() {
        this.emitByte((byte)31);
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        this.incStack();
        this.incStack();
    }
    
    public void opc_lload_2() {
        this.emitByte((byte)32);
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        this.incStack();
        this.incStack();
    }
    
    public void opc_lload_3() {
        this.emitByte((byte)33);
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        this.incStack();
        this.incStack();
    }
    
    public void opc_fload_0() {
        this.emitByte((byte)34);
        if (this.maxLocals < 1) {
            this.maxLocals = 1;
        }
        this.incStack();
    }
    
    public void opc_fload_1() {
        this.emitByte((byte)35);
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        this.incStack();
    }
    
    public void opc_fload_2() {
        this.emitByte((byte)36);
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        this.incStack();
    }
    
    public void opc_fload_3() {
        this.emitByte((byte)37);
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        this.incStack();
    }
    
    public void opc_dload_0() {
        this.emitByte((byte)38);
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        this.incStack();
        this.incStack();
    }
    
    public void opc_dload_1() {
        this.emitByte((byte)39);
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        this.incStack();
        this.incStack();
    }
    
    public void opc_dload_2() {
        this.emitByte((byte)40);
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        this.incStack();
        this.incStack();
    }
    
    public void opc_dload_3() {
        this.emitByte((byte)41);
        if (this.maxLocals < 5) {
            this.maxLocals = 5;
        }
        this.incStack();
        this.incStack();
    }
    
    public void opc_aload_0() {
        this.emitByte((byte)42);
        if (this.maxLocals < 1) {
            this.maxLocals = 1;
        }
        this.incStack();
    }
    
    public void opc_aload_1() {
        this.emitByte((byte)43);
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        this.incStack();
    }
    
    public void opc_aload_2() {
        this.emitByte((byte)44);
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        this.incStack();
    }
    
    public void opc_aload_3() {
        this.emitByte((byte)45);
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        this.incStack();
    }
    
    public void opc_aaload() {
        this.emitByte((byte)50);
        this.decStack();
    }
    
    public void opc_astore_0() {
        this.emitByte((byte)75);
        if (this.maxLocals < 1) {
            this.maxLocals = 1;
        }
        this.decStack();
    }
    
    public void opc_astore_1() {
        this.emitByte((byte)76);
        if (this.maxLocals < 2) {
            this.maxLocals = 2;
        }
        this.decStack();
    }
    
    public void opc_astore_2() {
        this.emitByte((byte)77);
        if (this.maxLocals < 3) {
            this.maxLocals = 3;
        }
        this.decStack();
    }
    
    public void opc_astore_3() {
        this.emitByte((byte)78);
        if (this.maxLocals < 4) {
            this.maxLocals = 4;
        }
        this.decStack();
    }
    
    public void opc_pop() {
        this.emitByte((byte)87);
        this.decStack();
    }
    
    public void opc_dup() {
        this.emitByte((byte)89);
        this.incStack();
    }
    
    public void opc_dup_x1() {
        this.emitByte((byte)90);
        this.incStack();
    }
    
    public void opc_swap() {
        this.emitByte((byte)95);
    }
    
    public void opc_i2l() {
        this.emitByte((byte)(-123));
    }
    
    public void opc_i2f() {
        this.emitByte((byte)(-122));
    }
    
    public void opc_i2d() {
        this.emitByte((byte)(-121));
    }
    
    public void opc_l2f() {
        this.emitByte((byte)(-119));
    }
    
    public void opc_l2d() {
        this.emitByte((byte)(-118));
    }
    
    public void opc_f2d() {
        this.emitByte((byte)(-115));
    }
    
    public void opc_ifeq(final short n) {
        this.emitByte((byte)(-103));
        this.emitShort(n);
        this.decStack();
    }
    
    public void opc_ifeq(final Label label) {
        final short length = this.getLength();
        this.emitByte((byte)(-103));
        label.add(this, length, this.getLength(), this.getStack() - 1);
        this.emitShort((short)(-1));
    }
    
    public void opc_if_icmpeq(final short n) {
        this.emitByte((byte)(-97));
        this.emitShort(n);
        this.setStack(this.getStack() - 2);
    }
    
    public void opc_if_icmpeq(final Label label) {
        final short length = this.getLength();
        this.emitByte((byte)(-97));
        label.add(this, length, this.getLength(), this.getStack() - 2);
        this.emitShort((short)(-1));
    }
    
    public void opc_goto(final short n) {
        this.emitByte((byte)(-89));
        this.emitShort(n);
    }
    
    public void opc_goto(final Label label) {
        final short length = this.getLength();
        this.emitByte((byte)(-89));
        label.add(this, length, this.getLength(), this.getStack());
        this.emitShort((short)(-1));
    }
    
    public void opc_ifnull(final short n) {
        this.emitByte((byte)(-58));
        this.emitShort(n);
        this.decStack();
    }
    
    public void opc_ifnull(final Label label) {
        final short length = this.getLength();
        this.emitByte((byte)(-58));
        label.add(this, length, this.getLength(), this.getStack() - 1);
        this.emitShort((short)(-1));
        this.decStack();
    }
    
    public void opc_ifnonnull(final short n) {
        this.emitByte((byte)(-57));
        this.emitShort(n);
        this.decStack();
    }
    
    public void opc_ifnonnull(final Label label) {
        final short length = this.getLength();
        this.emitByte((byte)(-57));
        label.add(this, length, this.getLength(), this.getStack() - 1);
        this.emitShort((short)(-1));
        this.decStack();
    }
    
    public void opc_ireturn() {
        this.emitByte((byte)(-84));
        this.setStack(0);
    }
    
    public void opc_lreturn() {
        this.emitByte((byte)(-83));
        this.setStack(0);
    }
    
    public void opc_freturn() {
        this.emitByte((byte)(-82));
        this.setStack(0);
    }
    
    public void opc_dreturn() {
        this.emitByte((byte)(-81));
        this.setStack(0);
    }
    
    public void opc_areturn() {
        this.emitByte((byte)(-80));
        this.setStack(0);
    }
    
    public void opc_return() {
        this.emitByte((byte)(-79));
        this.setStack(0);
    }
    
    public void opc_getstatic(final short n, final int n2) {
        this.emitByte((byte)(-78));
        this.emitShort(n);
        this.setStack(this.getStack() + n2);
    }
    
    public void opc_putstatic(final short n, final int n2) {
        this.emitByte((byte)(-77));
        this.emitShort(n);
        this.setStack(this.getStack() - n2);
    }
    
    public void opc_getfield(final short n, final int n2) {
        this.emitByte((byte)(-76));
        this.emitShort(n);
        this.setStack(this.getStack() + n2 - 1);
    }
    
    public void opc_putfield(final short n, final int n2) {
        this.emitByte((byte)(-75));
        this.emitShort(n);
        this.setStack(this.getStack() - n2 - 1);
    }
    
    public void opc_invokevirtual(final short n, final int n2, final int n3) {
        this.emitByte((byte)(-74));
        this.emitShort(n);
        this.setStack(this.getStack() - n2 - 1 + n3);
    }
    
    public void opc_invokespecial(final short n, final int n2, final int n3) {
        this.emitByte((byte)(-73));
        this.emitShort(n);
        this.setStack(this.getStack() - n2 - 1 + n3);
    }
    
    public void opc_invokestatic(final short n, final int n2, final int n3) {
        this.emitByte((byte)(-72));
        this.emitShort(n);
        this.setStack(this.getStack() - n2 + n3);
    }
    
    public void opc_invokeinterface(final short n, final int n2, final byte b, final int n3) {
        this.emitByte((byte)(-71));
        this.emitShort(n);
        this.emitByte(b);
        this.emitByte((byte)0);
        this.setStack(this.getStack() - n2 - 1 + n3);
    }
    
    public void opc_arraylength() {
        this.emitByte((byte)(-66));
    }
    
    public void opc_new(final short n) {
        this.emitByte((byte)(-69));
        this.emitShort(n);
        this.incStack();
    }
    
    public void opc_athrow() {
        this.emitByte((byte)(-65));
        this.setStack(1);
    }
    
    public void opc_checkcast(final short n) {
        this.emitByte((byte)(-64));
        this.emitShort(n);
    }
    
    public void opc_instanceof(final short n) {
        this.emitByte((byte)(-63));
        this.emitShort(n);
    }
}
