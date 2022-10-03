package com.sun.org.apache.bcel.internal.generic;

public interface InstructionTargeter
{
    boolean containsTarget(final InstructionHandle p0);
    
    void updateTarget(final InstructionHandle p0, final InstructionHandle p1);
}
