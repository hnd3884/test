package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.io.IOException;
import java.io.DataOutputStream;

public class MULTIANEWARRAY extends CPInstruction implements LoadClass, AllocationInstruction, ExceptionThrower
{
    private short dimensions;
    
    MULTIANEWARRAY() {
    }
    
    public MULTIANEWARRAY(final int index, final short dimensions) {
        super((short)197, index);
        if (dimensions < 1) {
            throw new ClassGenException("Invalid dimensions value: " + dimensions);
        }
        this.dimensions = dimensions;
        this.length = 4;
    }
    
    @Override
    public void dump(final DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        out.writeShort(this.index);
        out.writeByte(this.dimensions);
    }
    
    @Override
    protected void initFromFile(final ByteSequence bytes, final boolean wide) throws IOException {
        super.initFromFile(bytes, wide);
        this.dimensions = bytes.readByte();
        this.length = 4;
    }
    
    public final short getDimensions() {
        return this.dimensions;
    }
    
    @Override
    public String toString(final boolean verbose) {
        return super.toString(verbose) + " " + this.index + " " + this.dimensions;
    }
    
    @Override
    public String toString(final ConstantPool cp) {
        return super.toString(cp) + " " + this.dimensions;
    }
    
    @Override
    public int consumeStack(final ConstantPoolGen cpg) {
        return this.dimensions;
    }
    
    @Override
    public Class[] getExceptions() {
        final Class[] cs = new Class[2 + ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length];
        System.arraycopy(ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION, 0, cs, 0, ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length);
        cs[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length + 1] = ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION;
        cs[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length] = ExceptionConstants.ILLEGAL_ACCESS_ERROR;
        return cs;
    }
    
    @Override
    public ObjectType getLoadClassType(final ConstantPoolGen cpg) {
        Type t = this.getType(cpg);
        if (t instanceof ArrayType) {
            t = ((ArrayType)t).getBasicType();
        }
        return (t instanceof ObjectType) ? ((ObjectType)t) : null;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitLoadClass(this);
        v.visitAllocationInstruction(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitCPInstruction(this);
        v.visitMULTIANEWARRAY(this);
    }
}
