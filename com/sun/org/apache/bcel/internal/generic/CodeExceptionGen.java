package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.CodeException;
import java.io.Serializable;

public final class CodeExceptionGen implements InstructionTargeter, Cloneable, Serializable
{
    private InstructionHandle start_pc;
    private InstructionHandle end_pc;
    private InstructionHandle handler_pc;
    private ObjectType catch_type;
    
    public CodeExceptionGen(final InstructionHandle start_pc, final InstructionHandle end_pc, final InstructionHandle handler_pc, final ObjectType catch_type) {
        this.setStartPC(start_pc);
        this.setEndPC(end_pc);
        this.setHandlerPC(handler_pc);
        this.catch_type = catch_type;
    }
    
    public CodeException getCodeException(final ConstantPoolGen cp) {
        return new CodeException(this.start_pc.getPosition(), this.end_pc.getPosition() + this.end_pc.getInstruction().getLength(), this.handler_pc.getPosition(), (this.catch_type == null) ? 0 : cp.addClass(this.catch_type));
    }
    
    public final void setStartPC(final InstructionHandle start_pc) {
        BranchInstruction.notifyTargetChanging(this.start_pc, this);
        BranchInstruction.notifyTargetChanged(this.start_pc = start_pc, this);
    }
    
    public final void setEndPC(final InstructionHandle end_pc) {
        BranchInstruction.notifyTargetChanging(this.end_pc, this);
        BranchInstruction.notifyTargetChanged(this.end_pc = end_pc, this);
    }
    
    public final void setHandlerPC(final InstructionHandle handler_pc) {
        BranchInstruction.notifyTargetChanging(this.handler_pc, this);
        BranchInstruction.notifyTargetChanged(this.handler_pc = handler_pc, this);
    }
    
    @Override
    public void updateTarget(final InstructionHandle old_ih, final InstructionHandle new_ih) {
        boolean targeted = false;
        if (this.start_pc == old_ih) {
            targeted = true;
            this.setStartPC(new_ih);
        }
        if (this.end_pc == old_ih) {
            targeted = true;
            this.setEndPC(new_ih);
        }
        if (this.handler_pc == old_ih) {
            targeted = true;
            this.setHandlerPC(new_ih);
        }
        if (!targeted) {
            throw new ClassGenException("Not targeting " + old_ih + ", but {" + this.start_pc + ", " + this.end_pc + ", " + this.handler_pc + "}");
        }
    }
    
    @Override
    public boolean containsTarget(final InstructionHandle ih) {
        return this.start_pc == ih || this.end_pc == ih || this.handler_pc == ih;
    }
    
    public void setCatchType(final ObjectType catch_type) {
        this.catch_type = catch_type;
    }
    
    public ObjectType getCatchType() {
        return this.catch_type;
    }
    
    public InstructionHandle getStartPC() {
        return this.start_pc;
    }
    
    public InstructionHandle getEndPC() {
        return this.end_pc;
    }
    
    public InstructionHandle getHandlerPC() {
        return this.handler_pc;
    }
    
    @Override
    public String toString() {
        return "CodeExceptionGen(" + this.start_pc + ", " + this.end_pc + ", " + this.handler_pc + ")";
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            System.err.println(e);
            return null;
        }
    }
}
