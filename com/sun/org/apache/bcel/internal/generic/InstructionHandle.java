package com.sun.org.apache.bcel.internal.generic;

import java.util.Collection;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.util.HashMap;
import java.util.HashSet;
import java.io.Serializable;

public class InstructionHandle implements Serializable
{
    InstructionHandle next;
    InstructionHandle prev;
    Instruction instruction;
    protected int i_position;
    private HashSet targeters;
    private HashMap attributes;
    private static InstructionHandle ih_list;
    
    public final InstructionHandle getNext() {
        return this.next;
    }
    
    public final InstructionHandle getPrev() {
        return this.prev;
    }
    
    public final Instruction getInstruction() {
        return this.instruction;
    }
    
    public void setInstruction(final Instruction i) {
        if (i == null) {
            throw new ClassGenException("Assigning null to handle");
        }
        if (this.getClass() != BranchHandle.class && i instanceof BranchInstruction) {
            throw new ClassGenException("Assigning branch instruction " + i + " to plain handle");
        }
        if (this.instruction != null) {
            this.instruction.dispose();
        }
        this.instruction = i;
    }
    
    public Instruction swapInstruction(final Instruction i) {
        final Instruction oldInstruction = this.instruction;
        this.instruction = i;
        return oldInstruction;
    }
    
    protected InstructionHandle(final Instruction i) {
        this.i_position = -1;
        this.setInstruction(i);
    }
    
    static final InstructionHandle getInstructionHandle(final Instruction i) {
        if (InstructionHandle.ih_list == null) {
            return new InstructionHandle(i);
        }
        final InstructionHandle ih = InstructionHandle.ih_list;
        InstructionHandle.ih_list = ih.next;
        ih.setInstruction(i);
        return ih;
    }
    
    protected int updatePosition(final int offset, final int max_offset) {
        this.i_position += offset;
        return 0;
    }
    
    public int getPosition() {
        return this.i_position;
    }
    
    void setPosition(final int pos) {
        this.i_position = pos;
    }
    
    protected void addHandle() {
        this.next = InstructionHandle.ih_list;
        InstructionHandle.ih_list = this;
    }
    
    void dispose() {
        final InstructionHandle instructionHandle = null;
        this.prev = instructionHandle;
        this.next = instructionHandle;
        this.instruction.dispose();
        this.instruction = null;
        this.i_position = -1;
        this.attributes = null;
        this.removeAllTargeters();
        this.addHandle();
    }
    
    public void removeAllTargeters() {
        if (this.targeters != null) {
            this.targeters.clear();
        }
    }
    
    public void removeTargeter(final InstructionTargeter t) {
        this.targeters.remove(t);
    }
    
    public void addTargeter(final InstructionTargeter t) {
        if (this.targeters == null) {
            this.targeters = new HashSet();
        }
        this.targeters.add(t);
    }
    
    public boolean hasTargeters() {
        return this.targeters != null && this.targeters.size() > 0;
    }
    
    public InstructionTargeter[] getTargeters() {
        if (!this.hasTargeters()) {
            return null;
        }
        final InstructionTargeter[] t = new InstructionTargeter[this.targeters.size()];
        this.targeters.toArray(t);
        return t;
    }
    
    public String toString(final boolean verbose) {
        return Utility.format(this.i_position, 4, false, ' ') + ": " + this.instruction.toString(verbose);
    }
    
    @Override
    public String toString() {
        return this.toString(true);
    }
    
    public void addAttribute(final Object key, final Object attr) {
        if (this.attributes == null) {
            this.attributes = new HashMap(3);
        }
        this.attributes.put(key, attr);
    }
    
    public void removeAttribute(final Object key) {
        if (this.attributes != null) {
            this.attributes.remove(key);
        }
    }
    
    public Object getAttribute(final Object key) {
        if (this.attributes != null) {
            return this.attributes.get(key);
        }
        return null;
    }
    
    public Collection getAttributes() {
        return this.attributes.values();
    }
    
    public void accept(final Visitor v) {
        this.instruction.accept(v);
    }
    
    static {
        InstructionHandle.ih_list = null;
    }
}
