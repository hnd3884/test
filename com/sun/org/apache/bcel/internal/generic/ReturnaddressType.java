package com.sun.org.apache.bcel.internal.generic;

import java.util.Objects;

public class ReturnaddressType extends Type
{
    public static final ReturnaddressType NO_TARGET;
    private InstructionHandle returnTarget;
    
    private ReturnaddressType() {
        super((byte)16, "<return address>");
    }
    
    public ReturnaddressType(final InstructionHandle returnTarget) {
        super((byte)16, "<return address targeting " + returnTarget + ">");
        this.returnTarget = returnTarget;
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(this.returnTarget);
    }
    
    @Override
    public boolean equals(final Object rat) {
        return rat instanceof ReturnaddressType && ((ReturnaddressType)rat).returnTarget.equals(this.returnTarget);
    }
    
    public InstructionHandle getTarget() {
        return this.returnTarget;
    }
    
    static {
        NO_TARGET = new ReturnaddressType();
    }
}
