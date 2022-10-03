package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class GETFIELD extends FieldInstruction implements ExceptionThrower, StackConsumer, StackProducer
{
    GETFIELD() {
    }
    
    public GETFIELD(final int index) {
        super((short)180, index);
    }
    
    @Override
    public int produceStack(final ConstantPoolGen cpg) {
        return this.getFieldSize(cpg);
    }
    
    @Override
    public Class[] getExceptions() {
        final Class[] cs = new Class[2 + ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length];
        System.arraycopy(ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION, 0, cs, 0, ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length);
        cs[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length + 1] = ExceptionConstants.INCOMPATIBLE_CLASS_CHANGE_ERROR;
        cs[ExceptionConstants.EXCS_FIELD_AND_METHOD_RESOLUTION.length] = ExceptionConstants.NULL_POINTER_EXCEPTION;
        return cs;
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitExceptionThrower(this);
        v.visitStackConsumer(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitLoadClass(this);
        v.visitCPInstruction(this);
        v.visitFieldOrMethod(this);
        v.visitFieldInstruction(this);
        v.visitGETFIELD(this);
    }
}
