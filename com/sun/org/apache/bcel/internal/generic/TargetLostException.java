package com.sun.org.apache.bcel.internal.generic;

public final class TargetLostException extends Exception
{
    private InstructionHandle[] targets;
    
    TargetLostException(final InstructionHandle[] t, final String mesg) {
        super(mesg);
        this.targets = t;
    }
    
    public InstructionHandle[] getTargets() {
        return this.targets;
    }
}
