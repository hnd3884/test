package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class IREM extends ArithmeticInstruction implements ExceptionThrower
{
    public IREM() {
        super((short)112);
    }
    
    @Override
    public Class[] getExceptions() {
        return new Class[] { ExceptionConstants.ARITHMETIC_EXCEPTION };
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitIREM(this);
    }
}
