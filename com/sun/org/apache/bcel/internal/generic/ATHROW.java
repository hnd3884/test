package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.ExceptionConstants;

public class ATHROW extends Instruction implements UnconditionalBranch, ExceptionThrower
{
    public ATHROW() {
        super((short)191, (short)1);
    }
    
    @Override
    public Class[] getExceptions() {
        return new Class[] { ExceptionConstants.THROWABLE };
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitUnconditionalBranch(this);
        v.visitExceptionThrower(this);
        v.visitATHROW(this);
    }
}
