package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import java.util.HashMap;
import java.util.Iterator;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import com.sun.org.apache.bcel.internal.Constants;
import java.io.IOException;
import com.sun.org.apache.bcel.internal.util.ByteSequence;
import java.util.ArrayList;
import java.io.Serializable;

public class InstructionList implements Serializable
{
    private InstructionHandle start;
    private InstructionHandle end;
    private int length;
    private int[] byte_positions;
    private ArrayList observers;
    
    public InstructionList() {
        this.start = null;
        this.end = null;
        this.length = 0;
    }
    
    public InstructionList(final Instruction i) {
        this.start = null;
        this.end = null;
        this.length = 0;
        this.append(i);
    }
    
    public InstructionList(final BranchInstruction i) {
        this.start = null;
        this.end = null;
        this.length = 0;
        this.append(i);
    }
    
    public InstructionList(final CompoundInstruction c) {
        this.start = null;
        this.end = null;
        this.length = 0;
        this.append(c.getInstructionList());
    }
    
    public boolean isEmpty() {
        return this.start == null;
    }
    
    public static InstructionHandle findHandle(final InstructionHandle[] ihs, final int[] pos, final int count, final int target) {
        int l = 0;
        int r = count - 1;
        do {
            final int i = (l + r) / 2;
            final int j = pos[i];
            if (j == target) {
                return ihs[i];
            }
            if (target < j) {
                r = i - 1;
            }
            else {
                l = i + 1;
            }
        } while (l <= r);
        return null;
    }
    
    public InstructionHandle findHandle(final int pos) {
        final InstructionHandle[] ihs = this.getInstructionHandles();
        return findHandle(ihs, this.byte_positions, this.length, pos);
    }
    
    public InstructionList(final byte[] code) {
        this.start = null;
        this.end = null;
        this.length = 0;
        final ByteSequence bytes = new ByteSequence(code);
        final InstructionHandle[] ihs = new InstructionHandle[code.length];
        final int[] pos = new int[code.length];
        int count = 0;
        try {
            while (bytes.available() > 0) {
                final int off = bytes.getIndex();
                pos[count] = off;
                final Instruction i = Instruction.readInstruction(bytes);
                InstructionHandle ih;
                if (i instanceof BranchInstruction) {
                    ih = this.append((BranchInstruction)i);
                }
                else {
                    ih = this.append(i);
                }
                ih.setPosition(off);
                ihs[count] = ih;
                ++count;
            }
        }
        catch (final IOException e) {
            throw new ClassGenException(e.toString());
        }
        System.arraycopy(pos, 0, this.byte_positions = new int[count], 0, count);
        for (int j = 0; j < count; ++j) {
            if (ihs[j] instanceof BranchHandle) {
                final BranchInstruction bi = (BranchInstruction)ihs[j].instruction;
                int target = bi.position + bi.getIndex();
                InstructionHandle ih2 = findHandle(ihs, pos, count, target);
                if (ih2 == null) {
                    throw new ClassGenException("Couldn't find target for branch: " + bi);
                }
                bi.setTarget(ih2);
                if (bi instanceof Select) {
                    final Select s = (Select)bi;
                    final int[] indices = s.getIndices();
                    for (int k = 0; k < indices.length; ++k) {
                        target = bi.position + indices[k];
                        ih2 = findHandle(ihs, pos, count, target);
                        if (ih2 == null) {
                            throw new ClassGenException("Couldn't find target for switch: " + bi);
                        }
                        s.setTarget(k, ih2);
                    }
                }
            }
        }
    }
    
    public InstructionHandle append(final InstructionHandle ih, final InstructionList il) {
        if (il == null) {
            throw new ClassGenException("Appending null InstructionList");
        }
        if (il.isEmpty()) {
            return ih;
        }
        final InstructionHandle next = ih.next;
        final InstructionHandle ret = il.start;
        ih.next = il.start;
        il.start.prev = ih;
        if ((il.end.next = next) != null) {
            next.prev = il.end;
        }
        else {
            this.end = il.end;
        }
        this.length += il.length;
        il.clear();
        return ret;
    }
    
    public InstructionHandle append(final Instruction i, final InstructionList il) {
        final InstructionHandle ih;
        if ((ih = this.findInstruction2(i)) == null) {
            throw new ClassGenException("Instruction " + i + " is not contained in this list.");
        }
        return this.append(ih, il);
    }
    
    public InstructionHandle append(final InstructionList il) {
        if (il == null) {
            throw new ClassGenException("Appending null InstructionList");
        }
        if (il.isEmpty()) {
            return null;
        }
        if (this.isEmpty()) {
            this.start = il.start;
            this.end = il.end;
            this.length = il.length;
            il.clear();
            return this.start;
        }
        return this.append(this.end, il);
    }
    
    private void append(final InstructionHandle ih) {
        if (this.isEmpty()) {
            this.end = ih;
            this.start = ih;
            final InstructionHandle instructionHandle = null;
            ih.prev = instructionHandle;
            ih.next = instructionHandle;
        }
        else {
            this.end.next = ih;
            ih.prev = this.end;
            ih.next = null;
            this.end = ih;
        }
        ++this.length;
    }
    
    public InstructionHandle append(final Instruction i) {
        final InstructionHandle ih = InstructionHandle.getInstructionHandle(i);
        this.append(ih);
        return ih;
    }
    
    public BranchHandle append(final BranchInstruction i) {
        final BranchHandle ih = BranchHandle.getBranchHandle(i);
        this.append(ih);
        return ih;
    }
    
    public InstructionHandle append(final Instruction i, final Instruction j) {
        return this.append(i, new InstructionList(j));
    }
    
    public InstructionHandle append(final Instruction i, final CompoundInstruction c) {
        return this.append(i, c.getInstructionList());
    }
    
    public InstructionHandle append(final CompoundInstruction c) {
        return this.append(c.getInstructionList());
    }
    
    public InstructionHandle append(final InstructionHandle ih, final CompoundInstruction c) {
        return this.append(ih, c.getInstructionList());
    }
    
    public InstructionHandle append(final InstructionHandle ih, final Instruction i) {
        return this.append(ih, new InstructionList(i));
    }
    
    public BranchHandle append(final InstructionHandle ih, final BranchInstruction i) {
        final BranchHandle bh = BranchHandle.getBranchHandle(i);
        final InstructionList il = new InstructionList();
        il.append(bh);
        this.append(ih, il);
        return bh;
    }
    
    public InstructionHandle insert(final InstructionHandle ih, final InstructionList il) {
        if (il == null) {
            throw new ClassGenException("Inserting null InstructionList");
        }
        if (il.isEmpty()) {
            return ih;
        }
        final InstructionHandle prev = ih.prev;
        final InstructionHandle ret = il.start;
        ih.prev = il.end;
        il.end.next = ih;
        if ((il.start.prev = prev) != null) {
            prev.next = il.start;
        }
        else {
            this.start = il.start;
        }
        this.length += il.length;
        il.clear();
        return ret;
    }
    
    public InstructionHandle insert(final InstructionList il) {
        if (this.isEmpty()) {
            this.append(il);
            return this.start;
        }
        return this.insert(this.start, il);
    }
    
    private void insert(final InstructionHandle ih) {
        if (this.isEmpty()) {
            this.end = ih;
            this.start = ih;
            final InstructionHandle instructionHandle = null;
            ih.prev = instructionHandle;
            ih.next = instructionHandle;
        }
        else {
            this.start.prev = ih;
            ih.next = this.start;
            ih.prev = null;
            this.start = ih;
        }
        ++this.length;
    }
    
    public InstructionHandle insert(final Instruction i, final InstructionList il) {
        final InstructionHandle ih;
        if ((ih = this.findInstruction1(i)) == null) {
            throw new ClassGenException("Instruction " + i + " is not contained in this list.");
        }
        return this.insert(ih, il);
    }
    
    public InstructionHandle insert(final Instruction i) {
        final InstructionHandle ih = InstructionHandle.getInstructionHandle(i);
        this.insert(ih);
        return ih;
    }
    
    public BranchHandle insert(final BranchInstruction i) {
        final BranchHandle ih = BranchHandle.getBranchHandle(i);
        this.insert(ih);
        return ih;
    }
    
    public InstructionHandle insert(final Instruction i, final Instruction j) {
        return this.insert(i, new InstructionList(j));
    }
    
    public InstructionHandle insert(final Instruction i, final CompoundInstruction c) {
        return this.insert(i, c.getInstructionList());
    }
    
    public InstructionHandle insert(final CompoundInstruction c) {
        return this.insert(c.getInstructionList());
    }
    
    public InstructionHandle insert(final InstructionHandle ih, final Instruction i) {
        return this.insert(ih, new InstructionList(i));
    }
    
    public InstructionHandle insert(final InstructionHandle ih, final CompoundInstruction c) {
        return this.insert(ih, c.getInstructionList());
    }
    
    public BranchHandle insert(final InstructionHandle ih, final BranchInstruction i) {
        final BranchHandle bh = BranchHandle.getBranchHandle(i);
        final InstructionList il = new InstructionList();
        il.append(bh);
        this.insert(ih, il);
        return bh;
    }
    
    public void move(final InstructionHandle start, final InstructionHandle end, final InstructionHandle target) {
        if (start == null || end == null) {
            throw new ClassGenException("Invalid null handle: From " + start + " to " + end);
        }
        if (target == start || target == end) {
            throw new ClassGenException("Invalid range: From " + start + " to " + end + " contains target " + target);
        }
        for (InstructionHandle ih = start; ih != end.next; ih = ih.next) {
            if (ih == null) {
                throw new ClassGenException("Invalid range: From " + start + " to " + end);
            }
            if (ih == target) {
                throw new ClassGenException("Invalid range: From " + start + " to " + end + " contains target " + target);
            }
        }
        final InstructionHandle prev = start.prev;
        InstructionHandle next = end.next;
        if (prev != null) {
            prev.next = next;
        }
        else {
            this.start = next;
        }
        if (next != null) {
            next.prev = prev;
        }
        else {
            this.end = prev;
        }
        final InstructionHandle instructionHandle = null;
        end.next = instructionHandle;
        start.prev = instructionHandle;
        if (target == null) {
            end.next = this.start;
            this.start = start;
        }
        else {
            next = target.next;
            target.next = start;
            start.prev = target;
            if ((end.next = next) != null) {
                next.prev = end;
            }
        }
    }
    
    public void move(final InstructionHandle ih, final InstructionHandle target) {
        this.move(ih, ih, target);
    }
    
    private void remove(final InstructionHandle prev, InstructionHandle next) throws TargetLostException {
        InstructionHandle first;
        InstructionHandle last;
        if (prev == null && next == null) {
            last = (first = this.start);
            final InstructionHandle instructionHandle = null;
            this.end = instructionHandle;
            this.start = instructionHandle;
        }
        else {
            if (prev == null) {
                first = this.start;
                this.start = next;
            }
            else {
                first = prev.next;
                prev.next = next;
            }
            if (next == null) {
                last = this.end;
                this.end = prev;
            }
            else {
                last = next.prev;
                next.prev = prev;
            }
        }
        first.prev = null;
        last.next = null;
        final ArrayList target_vec = new ArrayList();
        for (InstructionHandle ih = first; ih != null; ih = ih.next) {
            ih.getInstruction().dispose();
        }
        final StringBuffer buf = new StringBuffer("{ ");
        for (InstructionHandle ih2 = first; ih2 != null; ih2 = next) {
            next = ih2.next;
            --this.length;
            if (ih2.hasTargeters()) {
                target_vec.add(ih2);
                buf.append(ih2.toString(true) + " ");
                final InstructionHandle instructionHandle2 = ih2;
                final InstructionHandle instructionHandle3 = ih2;
                final InstructionHandle instructionHandle4 = null;
                instructionHandle3.prev = instructionHandle4;
                instructionHandle2.next = instructionHandle4;
            }
            else {
                ih2.dispose();
            }
        }
        buf.append("}");
        if (!target_vec.isEmpty()) {
            final InstructionHandle[] targeted = new InstructionHandle[target_vec.size()];
            target_vec.toArray(targeted);
            throw new TargetLostException(targeted, buf.toString());
        }
    }
    
    public void delete(final InstructionHandle ih) throws TargetLostException {
        this.remove(ih.prev, ih.next);
    }
    
    public void delete(final Instruction i) throws TargetLostException {
        final InstructionHandle ih;
        if ((ih = this.findInstruction1(i)) == null) {
            throw new ClassGenException("Instruction " + i + " is not contained in this list.");
        }
        this.delete(ih);
    }
    
    public void delete(final InstructionHandle from, final InstructionHandle to) throws TargetLostException {
        this.remove(from.prev, to.next);
    }
    
    public void delete(final Instruction from, final Instruction to) throws TargetLostException {
        final InstructionHandle from_ih;
        if ((from_ih = this.findInstruction1(from)) == null) {
            throw new ClassGenException("Instruction " + from + " is not contained in this list.");
        }
        final InstructionHandle to_ih;
        if ((to_ih = this.findInstruction2(to)) == null) {
            throw new ClassGenException("Instruction " + to + " is not contained in this list.");
        }
        this.delete(from_ih, to_ih);
    }
    
    private InstructionHandle findInstruction1(final Instruction i) {
        for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
            if (ih.instruction == i) {
                return ih;
            }
        }
        return null;
    }
    
    private InstructionHandle findInstruction2(final Instruction i) {
        for (InstructionHandle ih = this.end; ih != null; ih = ih.prev) {
            if (ih.instruction == i) {
                return ih;
            }
        }
        return null;
    }
    
    public boolean contains(final InstructionHandle i) {
        if (i == null) {
            return false;
        }
        for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
            if (ih == i) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(final Instruction i) {
        return this.findInstruction1(i) != null;
    }
    
    public void setPositions() {
        this.setPositions(false);
    }
    
    public void setPositions(final boolean check) {
        int max_additional_bytes = 0;
        int additional_bytes = 0;
        int index = 0;
        int count = 0;
        final int[] pos = new int[this.length];
        if (check) {
            for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
                final Instruction i = ih.instruction;
                if (i instanceof BranchInstruction) {
                    Instruction inst = ((BranchInstruction)i).getTarget().instruction;
                    if (!this.contains(inst)) {
                        throw new ClassGenException("Branch target of " + Constants.OPCODE_NAMES[i.opcode] + ":" + inst + " not in instruction list");
                    }
                    if (i instanceof Select) {
                        final InstructionHandle[] targets = ((Select)i).getTargets();
                        for (int j = 0; j < targets.length; ++j) {
                            inst = targets[j].instruction;
                            if (!this.contains(inst)) {
                                throw new ClassGenException("Branch target of " + Constants.OPCODE_NAMES[i.opcode] + ":" + inst + " not in instruction list");
                            }
                        }
                    }
                    if (!(ih instanceof BranchHandle)) {
                        throw new ClassGenException("Branch instruction " + Constants.OPCODE_NAMES[i.opcode] + ":" + inst + " not contained in BranchHandle.");
                    }
                }
            }
        }
        for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
            final Instruction i = ih.instruction;
            ih.setPosition(index);
            pos[count++] = index;
            switch (i.getOpcode()) {
                case 167:
                case 168: {
                    max_additional_bytes += 2;
                    break;
                }
                case 170:
                case 171: {
                    max_additional_bytes += 3;
                    break;
                }
            }
            index += i.getLength();
        }
        for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
            additional_bytes += ih.updatePosition(additional_bytes, max_additional_bytes);
        }
        count = (index = 0);
        for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
            final Instruction i = ih.instruction;
            ih.setPosition(index);
            pos[count++] = index;
            index += i.getLength();
        }
        System.arraycopy(pos, 0, this.byte_positions = new int[count], 0, count);
    }
    
    public byte[] getByteCode() {
        this.setPositions();
        final ByteArrayOutputStream b = new ByteArrayOutputStream();
        final DataOutputStream out = new DataOutputStream(b);
        try {
            for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
                final Instruction i = ih.instruction;
                i.dump(out);
            }
        }
        catch (final IOException e) {
            System.err.println(e);
            return null;
        }
        return b.toByteArray();
    }
    
    public Instruction[] getInstructions() {
        final ByteSequence bytes = new ByteSequence(this.getByteCode());
        final ArrayList instructions = new ArrayList();
        try {
            while (bytes.available() > 0) {
                instructions.add(Instruction.readInstruction(bytes));
            }
        }
        catch (final IOException e) {
            throw new ClassGenException(e.toString());
        }
        final Instruction[] result = new Instruction[instructions.size()];
        instructions.toArray(result);
        return result;
    }
    
    @Override
    public String toString() {
        return this.toString(true);
    }
    
    public String toString(final boolean verbose) {
        final StringBuffer buf = new StringBuffer();
        for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
            buf.append(ih.toString(verbose) + "\n");
        }
        return buf.toString();
    }
    
    public Iterator iterator() {
        return new Iterator() {
            private InstructionHandle ih = InstructionList.this.start;
            
            @Override
            public Object next() {
                final InstructionHandle i = this.ih;
                this.ih = this.ih.next;
                return i;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean hasNext() {
                return this.ih != null;
            }
        };
    }
    
    public InstructionHandle[] getInstructionHandles() {
        final InstructionHandle[] ihs = new InstructionHandle[this.length];
        InstructionHandle ih = this.start;
        for (int i = 0; i < this.length; ++i) {
            ihs[i] = ih;
            ih = ih.next;
        }
        return ihs;
    }
    
    public int[] getInstructionPositions() {
        return this.byte_positions;
    }
    
    public InstructionList copy() {
        final HashMap map = new HashMap();
        final InstructionList il = new InstructionList();
        for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
            final Instruction i = ih.instruction;
            final Instruction c = i.copy();
            if (c instanceof BranchInstruction) {
                map.put(ih, il.append((BranchInstruction)c));
            }
            else {
                map.put(ih, il.append(c));
            }
        }
        for (InstructionHandle ih = this.start, ch = il.start; ih != null; ih = ih.next, ch = ch.next) {
            final Instruction j = ih.instruction;
            final Instruction c2 = ch.instruction;
            if (j instanceof BranchInstruction) {
                final BranchInstruction bi = (BranchInstruction)j;
                final BranchInstruction bc = (BranchInstruction)c2;
                final InstructionHandle itarget = bi.getTarget();
                bc.setTarget(map.get(itarget));
                if (bi instanceof Select) {
                    final InstructionHandle[] itargets = ((Select)bi).getTargets();
                    final InstructionHandle[] ctargets = ((Select)bc).getTargets();
                    for (int k = 0; k < itargets.length; ++k) {
                        ctargets[k] = map.get(itargets[k]);
                    }
                }
            }
        }
        return il;
    }
    
    public void replaceConstantPool(final ConstantPoolGen old_cp, final ConstantPoolGen new_cp) {
        for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
            final Instruction i = ih.instruction;
            if (i instanceof CPInstruction) {
                final CPInstruction ci = (CPInstruction)i;
                final Constant c = old_cp.getConstant(ci.getIndex());
                ci.setIndex(new_cp.addConstant(c, old_cp));
            }
        }
    }
    
    private void clear() {
        final InstructionHandle instructionHandle = null;
        this.end = instructionHandle;
        this.start = instructionHandle;
        this.length = 0;
    }
    
    public void dispose() {
        for (InstructionHandle ih = this.end; ih != null; ih = ih.prev) {
            ih.dispose();
        }
        this.clear();
    }
    
    public InstructionHandle getStart() {
        return this.start;
    }
    
    public InstructionHandle getEnd() {
        return this.end;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int size() {
        return this.length;
    }
    
    public void redirectBranches(final InstructionHandle old_target, final InstructionHandle new_target) {
        for (InstructionHandle ih = this.start; ih != null; ih = ih.next) {
            final Instruction i = ih.getInstruction();
            if (i instanceof BranchInstruction) {
                final BranchInstruction b = (BranchInstruction)i;
                final InstructionHandle target = b.getTarget();
                if (target == old_target) {
                    b.setTarget(new_target);
                }
                if (b instanceof Select) {
                    final InstructionHandle[] targets = ((Select)b).getTargets();
                    for (int j = 0; j < targets.length; ++j) {
                        if (targets[j] == old_target) {
                            ((Select)b).setTarget(j, new_target);
                        }
                    }
                }
            }
        }
    }
    
    public void redirectLocalVariables(final LocalVariableGen[] lg, final InstructionHandle old_target, final InstructionHandle new_target) {
        for (int i = 0; i < lg.length; ++i) {
            final InstructionHandle start = lg[i].getStart();
            final InstructionHandle end = lg[i].getEnd();
            if (start == old_target) {
                lg[i].setStart(new_target);
            }
            if (end == old_target) {
                lg[i].setEnd(new_target);
            }
        }
    }
    
    public void redirectExceptionHandlers(final CodeExceptionGen[] exceptions, final InstructionHandle old_target, final InstructionHandle new_target) {
        for (int i = 0; i < exceptions.length; ++i) {
            if (exceptions[i].getStartPC() == old_target) {
                exceptions[i].setStartPC(new_target);
            }
            if (exceptions[i].getEndPC() == old_target) {
                exceptions[i].setEndPC(new_target);
            }
            if (exceptions[i].getHandlerPC() == old_target) {
                exceptions[i].setHandlerPC(new_target);
            }
        }
    }
    
    public void addObserver(final InstructionListObserver o) {
        if (this.observers == null) {
            this.observers = new ArrayList();
        }
        this.observers.add(o);
    }
    
    public void removeObserver(final InstructionListObserver o) {
        if (this.observers != null) {
            this.observers.remove(o);
        }
    }
    
    public void update() {
        if (this.observers != null) {
            final Iterator e = this.observers.iterator();
            while (e.hasNext()) {
                e.next().notify(this);
            }
        }
    }
}
