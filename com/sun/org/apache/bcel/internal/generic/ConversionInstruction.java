package com.sun.org.apache.bcel.internal.generic;

public abstract class ConversionInstruction extends Instruction implements TypedInstruction, StackProducer, StackConsumer
{
    ConversionInstruction() {
    }
    
    protected ConversionInstruction(final short opcode) {
        super(opcode, (short)1);
    }
    
    @Override
    public Type getType(final ConstantPoolGen cp) {
        switch (this.opcode) {
            case 136:
            case 139:
            case 142: {
                return Type.INT;
            }
            case 134:
            case 137:
            case 144: {
                return Type.FLOAT;
            }
            case 133:
            case 140:
            case 143: {
                return Type.LONG;
            }
            case 135:
            case 138:
            case 141: {
                return Type.DOUBLE;
            }
            case 145: {
                return Type.BYTE;
            }
            case 146: {
                return Type.CHAR;
            }
            case 147: {
                return Type.SHORT;
            }
            default: {
                throw new ClassGenException("Unknown type " + this.opcode);
            }
        }
    }
}
