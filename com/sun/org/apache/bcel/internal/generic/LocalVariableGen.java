package com.sun.org.apache.bcel.internal.generic;

import java.util.Objects;
import com.sun.org.apache.bcel.internal.classfile.LocalVariable;
import java.io.Serializable;

public class LocalVariableGen implements InstructionTargeter, NamedAndTyped, Cloneable, Serializable
{
    private final int index;
    private String name;
    private Type type;
    private InstructionHandle start;
    private InstructionHandle end;
    
    public LocalVariableGen(final int index, final String name, final Type type, final InstructionHandle start, final InstructionHandle end) {
        if (index < 0 || index > 65535) {
            throw new ClassGenException("Invalid index index: " + index);
        }
        this.name = name;
        this.type = type;
        this.index = index;
        this.setStart(start);
        this.setEnd(end);
    }
    
    public LocalVariable getLocalVariable(final ConstantPoolGen cp) {
        final int start_pc = this.start.getPosition();
        int length = this.end.getPosition() - start_pc;
        if (length > 0) {
            length += this.end.getInstruction().getLength();
        }
        final int name_index = cp.addUtf8(this.name);
        final int signature_index = cp.addUtf8(this.type.getSignature());
        return new LocalVariable(start_pc, length, name_index, signature_index, this.index, cp.getConstantPool());
    }
    
    public int getIndex() {
        return this.index;
    }
    
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void setType(final Type type) {
        this.type = type;
    }
    
    @Override
    public Type getType() {
        return this.type;
    }
    
    public InstructionHandle getStart() {
        return this.start;
    }
    
    public InstructionHandle getEnd() {
        return this.end;
    }
    
    void notifyTargetChanging() {
        BranchInstruction.notifyTargetChanging(this.start, this);
        if (this.end != this.start) {
            BranchInstruction.notifyTargetChanging(this.end, this);
        }
    }
    
    void notifyTargetChanged() {
        BranchInstruction.notifyTargetChanged(this.start, this);
        if (this.end != this.start) {
            BranchInstruction.notifyTargetChanged(this.end, this);
        }
    }
    
    public final void setStart(final InstructionHandle start) {
        this.notifyTargetChanging();
        this.start = start;
        this.notifyTargetChanged();
    }
    
    public final void setEnd(final InstructionHandle end) {
        this.notifyTargetChanging();
        this.end = end;
        this.notifyTargetChanged();
    }
    
    @Override
    public void updateTarget(final InstructionHandle old_ih, final InstructionHandle new_ih) {
        boolean targeted = false;
        if (this.start == old_ih) {
            targeted = true;
            this.setStart(new_ih);
        }
        if (this.end == old_ih) {
            targeted = true;
            this.setEnd(new_ih);
        }
        if (!targeted) {
            throw new ClassGenException("Not targeting " + old_ih + ", but {" + this.start + ", " + this.end + "}");
        }
    }
    
    @Override
    public boolean containsTarget(final InstructionHandle ih) {
        return this.start == ih || this.end == ih;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof LocalVariableGen)) {
            return false;
        }
        final LocalVariableGen l = (LocalVariableGen)o;
        return l.index == this.index && l.start == this.start && l.end == this.end;
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.index;
        hash = 59 * hash + Objects.hashCode(this.start);
        hash = 59 * hash + Objects.hashCode(this.end);
        return hash;
    }
    
    @Override
    public String toString() {
        return "LocalVariableGen(" + this.name + ", " + this.type + ", " + this.start + ", " + this.end + ")";
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
