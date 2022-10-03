package com.sun.java.util.jar.pack;

import java.util.Arrays;
import java.util.Collection;

class Code extends Attribute.Holder
{
    Package.Class.Method m;
    private static final ConstantPool.Entry[] noRefs;
    int max_stack;
    int max_locals;
    ConstantPool.Entry[] handler_class;
    int[] handler_start;
    int[] handler_end;
    int[] handler_catch;
    byte[] bytes;
    Fixups fixups;
    Object insnMap;
    static final boolean shrinkMaps = true;
    
    public Code(final Package.Class.Method m) {
        this.handler_class = Code.noRefs;
        this.handler_start = Constants.noInts;
        this.handler_end = Constants.noInts;
        this.handler_catch = Constants.noInts;
        this.m = m;
    }
    
    public Package.Class.Method getMethod() {
        return this.m;
    }
    
    public Package.Class thisClass() {
        return this.m.thisClass();
    }
    
    public Package getPackage() {
        return this.m.thisClass().getPackage();
    }
    
    public ConstantPool.Entry[] getCPMap() {
        return this.m.getCPMap();
    }
    
    int getLength() {
        return this.bytes.length;
    }
    
    int getMaxStack() {
        return this.max_stack;
    }
    
    void setMaxStack(final int max_stack) {
        this.max_stack = max_stack;
    }
    
    int getMaxNALocals() {
        return this.max_locals - this.m.getArgumentSize();
    }
    
    void setMaxNALocals(final int n) {
        this.max_locals = this.m.getArgumentSize() + n;
    }
    
    int getHandlerCount() {
        assert this.handler_class.length == this.handler_start.length;
        assert this.handler_class.length == this.handler_end.length;
        assert this.handler_class.length == this.handler_catch.length;
        return this.handler_class.length;
    }
    
    void setHandlerCount(final int n) {
        if (n > 0) {
            this.handler_class = new ConstantPool.Entry[n];
            this.handler_start = new int[n];
            this.handler_end = new int[n];
            this.handler_catch = new int[n];
        }
    }
    
    void setBytes(final byte[] array) {
        this.bytes = array;
        if (this.fixups != null) {
            this.fixups.setBytes(array);
        }
    }
    
    void setInstructionMap(final int[] array, final int n) {
        this.insnMap = this.allocateInstructionMap(array, n);
    }
    
    void setInstructionMap(final int[] array) {
        this.setInstructionMap(array, array.length);
    }
    
    int[] getInstructionMap() {
        return this.expandInstructionMap(this.getInsnMap());
    }
    
    void addFixups(final Collection<Fixups.Fixup> collection) {
        if (this.fixups == null) {
            this.fixups = new Fixups(this.bytes);
        }
        assert this.fixups.getBytes() == this.bytes;
        this.fixups.addAll(collection);
    }
    
    @Override
    public void trimToSize() {
        if (this.fixups != null) {
            this.fixups.trimToSize();
            if (this.fixups.size() == 0) {
                this.fixups = null;
            }
        }
        super.trimToSize();
    }
    
    @Override
    protected void visitRefs(final int n, final Collection<ConstantPool.Entry> collection) {
        final int verbose = this.getPackage().verbose;
        if (verbose > 2) {
            System.out.println("Reference scan " + this);
        }
        collection.addAll(Arrays.asList(this.handler_class));
        if (this.fixups != null) {
            this.fixups.visitRefs(collection);
        }
        else {
            final ConstantPool.Entry[] cpMap = this.getCPMap();
            for (Instruction instruction = this.instructionAt(0); instruction != null; instruction = instruction.next()) {
                if (verbose > 4) {
                    System.out.println(instruction);
                }
                final int cpIndex = instruction.getCPIndex();
                if (cpIndex >= 0) {
                    collection.add(cpMap[cpIndex]);
                }
            }
        }
        super.visitRefs(n, collection);
    }
    
    private Object allocateInstructionMap(final int[] array, final int n) {
        final int length = this.getLength();
        if (length <= 255) {
            final byte[] array2 = new byte[n + 1];
            for (int i = 0; i < n; ++i) {
                array2[i] = (byte)(array[i] - 128);
            }
            array2[n] = (byte)(length - 128);
            return array2;
        }
        if (length < 65535) {
            final short[] array3 = new short[n + 1];
            for (int j = 0; j < n; ++j) {
                array3[j] = (short)(array[j] - 32768);
            }
            array3[n] = (short)(length - 32768);
            return array3;
        }
        final int[] copy = Arrays.copyOf(array, n + 1);
        copy[n] = length;
        return copy;
    }
    
    private int[] expandInstructionMap(final Object o) {
        int[] copyOfRange;
        if (o instanceof byte[]) {
            final byte[] array = (byte[])o;
            copyOfRange = new int[array.length - 1];
            for (int i = 0; i < copyOfRange.length; ++i) {
                copyOfRange[i] = array[i] + 128;
            }
        }
        else if (o instanceof short[]) {
            final short[] array2 = (short[])o;
            copyOfRange = new int[array2.length - 1];
            for (int j = 0; j < copyOfRange.length; ++j) {
                copyOfRange[j] = array2[j] + 128;
            }
        }
        else {
            final int[] array3 = (int[])o;
            copyOfRange = Arrays.copyOfRange(array3, 0, array3.length - 1);
        }
        return copyOfRange;
    }
    
    Object getInsnMap() {
        if (this.insnMap != null) {
            return this.insnMap;
        }
        final int[] array = new int[this.getLength()];
        int n = 0;
        for (Instruction instruction = this.instructionAt(0); instruction != null; instruction = instruction.next()) {
            array[n++] = instruction.getPC();
        }
        return this.insnMap = this.allocateInstructionMap(array, n);
    }
    
    public int encodeBCI(final int n) {
        if (n <= 0 || n > this.getLength()) {
            return n;
        }
        final Object insnMap = this.getInsnMap();
        int n2;
        int n3;
        if (insnMap instanceof byte[]) {
            final byte[] array = (byte[])insnMap;
            n2 = array.length;
            n3 = Arrays.binarySearch(array, (byte)(n - 128));
        }
        else if (insnMap instanceof short[]) {
            final short[] array2 = (short[])insnMap;
            n2 = array2.length;
            n3 = Arrays.binarySearch(array2, (short)(n - 32768));
        }
        else {
            final int[] array3 = (int[])insnMap;
            n2 = array3.length;
            n3 = Arrays.binarySearch(array3, n);
        }
        assert n3 != -1;
        assert n3 != 0;
        assert n3 != n2;
        assert n3 != -n2 - 1;
        return (n3 >= 0) ? n3 : (n2 + n - (-n3 - 1));
    }
    
    public int decodeBCI(final int n) {
        if (n <= 0 || n > this.getLength()) {
            return n;
        }
        final Object insnMap = this.getInsnMap();
        int n2;
        int n3;
        if (insnMap instanceof byte[]) {
            final byte[] array = (byte[])insnMap;
            n2 = array.length;
            if (n < n2) {
                return array[n] + 128;
            }
            n3 = Arrays.binarySearch(array, (byte)(n - 128));
            if (n3 < 0) {
                n3 = -n3 - 1;
            }
            while (array[n3 - 1] - (n3 - 1) > n - n2 - 128) {
                --n3;
            }
        }
        else if (insnMap instanceof short[]) {
            final short[] array2 = (short[])insnMap;
            n2 = array2.length;
            if (n < n2) {
                return array2[n] + 32768;
            }
            n3 = Arrays.binarySearch(array2, (short)(n - 32768));
            if (n3 < 0) {
                n3 = -n3 - 1;
            }
            while (array2[n3 - 1] - (n3 - 1) > n - n2 - 32768) {
                --n3;
            }
        }
        else {
            final int[] array3 = (int[])insnMap;
            n2 = array3.length;
            if (n < n2) {
                return array3[n];
            }
            n3 = Arrays.binarySearch(array3, n);
            if (n3 < 0) {
                n3 = -n3 - 1;
            }
            while (array3[n3 - 1] - (n3 - 1) > n - n2) {
                --n3;
            }
        }
        return n - n2 + n3;
    }
    
    public void finishRefs(final ConstantPool.Index index) {
        if (this.fixups != null) {
            this.fixups.finishRefs(index);
            this.fixups = null;
        }
    }
    
    Instruction instructionAt(final int n) {
        return Instruction.at(this.bytes, n);
    }
    
    static boolean flagsRequireCode(final int n) {
        return (n & 0x500) == 0x0;
    }
    
    @Override
    public String toString() {
        return this.m + ".Code";
    }
    
    public int getInt(final int n) {
        return Instruction.getInt(this.bytes, n);
    }
    
    public int getShort(final int n) {
        return Instruction.getShort(this.bytes, n);
    }
    
    public int getByte(final int n) {
        return Instruction.getByte(this.bytes, n);
    }
    
    void setInt(final int n, final int n2) {
        Instruction.setInt(this.bytes, n, n2);
    }
    
    void setShort(final int n, final int n2) {
        Instruction.setShort(this.bytes, n, n2);
    }
    
    void setByte(final int n, final int n2) {
        Instruction.setByte(this.bytes, n, n2);
    }
    
    static {
        noRefs = ConstantPool.noRefs;
    }
}
