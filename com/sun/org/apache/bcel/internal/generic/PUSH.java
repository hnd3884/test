package com.sun.org.apache.bcel.internal.generic;

public final class PUSH implements CompoundInstruction, VariableLengthInstruction, InstructionConstants
{
    private Instruction instruction;
    
    public PUSH(final ConstantPoolGen cp, final int value) {
        if (value >= -1 && value <= 5) {
            this.instruction = PUSH.INSTRUCTIONS[3 + value];
        }
        else if (value >= -128 && value <= 127) {
            this.instruction = new BIPUSH((byte)value);
        }
        else if (value >= -32768 && value <= 32767) {
            this.instruction = new SIPUSH((short)value);
        }
        else {
            this.instruction = new LDC(cp.addInteger(value));
        }
    }
    
    public PUSH(final ConstantPoolGen cp, final boolean value) {
        this.instruction = PUSH.INSTRUCTIONS[3 + (value ? 1 : 0)];
    }
    
    public PUSH(final ConstantPoolGen cp, final float value) {
        if (value == 0.0) {
            this.instruction = PUSH.FCONST_0;
        }
        else if (value == 1.0) {
            this.instruction = PUSH.FCONST_1;
        }
        else if (value == 2.0) {
            this.instruction = PUSH.FCONST_2;
        }
        else {
            this.instruction = new LDC(cp.addFloat(value));
        }
    }
    
    public PUSH(final ConstantPoolGen cp, final long value) {
        if (value == 0L) {
            this.instruction = PUSH.LCONST_0;
        }
        else if (value == 1L) {
            this.instruction = PUSH.LCONST_1;
        }
        else {
            this.instruction = new LDC2_W(cp.addLong(value));
        }
    }
    
    public PUSH(final ConstantPoolGen cp, final double value) {
        if (value == 0.0) {
            this.instruction = PUSH.DCONST_0;
        }
        else if (value == 1.0) {
            this.instruction = PUSH.DCONST_1;
        }
        else {
            this.instruction = new LDC2_W(cp.addDouble(value));
        }
    }
    
    public PUSH(final ConstantPoolGen cp, final String value) {
        if (value == null) {
            this.instruction = PUSH.ACONST_NULL;
        }
        else {
            this.instruction = new LDC(cp.addString(value));
        }
    }
    
    public PUSH(final ConstantPoolGen cp, final Number value) {
        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            this.instruction = new PUSH(cp, value.intValue()).instruction;
        }
        else if (value instanceof Double) {
            this.instruction = new PUSH(cp, value.doubleValue()).instruction;
        }
        else if (value instanceof Float) {
            this.instruction = new PUSH(cp, value.floatValue()).instruction;
        }
        else {
            if (!(value instanceof Long)) {
                throw new ClassGenException("What's this: " + value);
            }
            this.instruction = new PUSH(cp, value.longValue()).instruction;
        }
    }
    
    public PUSH(final ConstantPoolGen cp, final Character value) {
        this(cp, (int)value);
    }
    
    public PUSH(final ConstantPoolGen cp, final Boolean value) {
        this(cp, (boolean)value);
    }
    
    @Override
    public final InstructionList getInstructionList() {
        return new InstructionList(this.instruction);
    }
    
    public final Instruction getInstruction() {
        return this.instruction;
    }
    
    @Override
    public String toString() {
        return this.instruction.toString() + " (PUSH)";
    }
}
