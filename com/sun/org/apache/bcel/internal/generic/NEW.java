package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class NEW extends CPInstruction implements LoadClass, AllocationInstruction, ExceptionThrower, StackProducer
{
    NEW() {
    }
    
    public NEW(final int index) {
        super((short)187, index);
    }
    
    @Override
    public Class[] getExceptions() {
        final Class[] cs = new Class[2 + ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length];
        System.arraycopy(ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION, 0, cs, 0, ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length);
        cs[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length + 1] = ExceptionConstants.INSTANTIATION_ERROR;
        cs[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length] = ExceptionConstants.ILLEGAL_ACCESS_ERROR;
        return cs;
    }
    
    @Override
    public ObjectType getLoadClassType(final ConstantPoolGen cpg) {
        return (ObjectType)this.getType(cpg);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitLoadClass(this);
        v.visitAllocationInstruction(this);
        v.visitExceptionThrower(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitCPInstruction(this);
        v.visitNEW(this);
    }
}
