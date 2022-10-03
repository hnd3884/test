package org.eclipse.jdt.internal.compiler.codegen;

import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import java.util.Arrays;

public class BranchLabel extends Label
{
    private int[] forwardReferences;
    private int forwardReferenceCount;
    BranchLabel delegate;
    public int tagBits;
    public static final int WIDE = 1;
    public static final int USED = 2;
    
    public BranchLabel() {
        this.forwardReferences = new int[10];
        this.forwardReferenceCount = 0;
    }
    
    public BranchLabel(final CodeStream codeStream) {
        super(codeStream);
        this.forwardReferences = new int[10];
        this.forwardReferenceCount = 0;
    }
    
    void addForwardReference(final int pos) {
        if (this.delegate != null) {
            this.delegate.addForwardReference(pos);
            return;
        }
        final int count = this.forwardReferenceCount;
        if (count >= 1) {
            final int previousValue = this.forwardReferences[count - 1];
            if (previousValue < pos) {
                final int length;
                if (count >= (length = this.forwardReferences.length)) {
                    System.arraycopy(this.forwardReferences, 0, this.forwardReferences = new int[2 * length], 0, length);
                }
                this.forwardReferences[this.forwardReferenceCount++] = pos;
            }
            else if (previousValue > pos) {
                final int[] refs = this.forwardReferences;
                for (int i = 0, max = this.forwardReferenceCount; i < max; ++i) {
                    if (refs[i] == pos) {
                        return;
                    }
                }
                final int length2;
                if (count >= (length2 = refs.length)) {
                    System.arraycopy(refs, 0, this.forwardReferences = new int[2 * length2], 0, length2);
                }
                this.forwardReferences[this.forwardReferenceCount++] = pos;
                Arrays.sort(this.forwardReferences, 0, this.forwardReferenceCount);
            }
        }
        else {
            final int length3;
            if (count >= (length3 = this.forwardReferences.length)) {
                System.arraycopy(this.forwardReferences, 0, this.forwardReferences = new int[2 * length3], 0, length3);
            }
            this.forwardReferences[this.forwardReferenceCount++] = pos;
        }
    }
    
    public void becomeDelegateFor(final BranchLabel otherLabel) {
        otherLabel.delegate = this;
        final int otherCount = otherLabel.forwardReferenceCount;
        if (otherCount == 0) {
            return;
        }
        final int[] mergedForwardReferences = new int[this.forwardReferenceCount + otherCount];
        int indexInMerge = 0;
        int j = 0;
        int i = 0;
        final int max = this.forwardReferenceCount;
        final int max2 = otherLabel.forwardReferenceCount;
    Label_0138_Outer:
        while (i < max) {
            final int value1 = this.forwardReferences[i];
            while (true) {
                while (j < max2) {
                    final int value2 = otherLabel.forwardReferences[j];
                    if (value1 < value2) {
                        mergedForwardReferences[indexInMerge++] = value1;
                    }
                    else {
                        if (value1 != value2) {
                            mergedForwardReferences[indexInMerge++] = value2;
                            ++j;
                            continue Label_0138_Outer;
                        }
                        mergedForwardReferences[indexInMerge++] = value1;
                        ++j;
                    }
                    ++i;
                    continue Label_0138_Outer;
                }
                mergedForwardReferences[indexInMerge++] = value1;
                continue;
            }
        }
        while (j < max2) {
            mergedForwardReferences[indexInMerge++] = otherLabel.forwardReferences[j];
            ++j;
        }
        this.forwardReferences = mergedForwardReferences;
        this.forwardReferenceCount = indexInMerge;
    }
    
    void branch() {
        this.tagBits |= 0x2;
        if (this.delegate != null) {
            this.delegate.branch();
            return;
        }
        if (this.position == -1) {
            this.addForwardReference(this.codeStream.position);
            final CodeStream codeStream = this.codeStream;
            codeStream.position += 2;
            final CodeStream codeStream2 = this.codeStream;
            codeStream2.classFileOffset += 2;
        }
        else {
            this.codeStream.writePosition(this);
        }
    }
    
    void branchWide() {
        this.tagBits |= 0x2;
        if (this.delegate != null) {
            this.delegate.branchWide();
            return;
        }
        if (this.position == -1) {
            this.addForwardReference(this.codeStream.position);
            this.tagBits |= 0x1;
            final CodeStream codeStream = this.codeStream;
            codeStream.position += 4;
            final CodeStream codeStream2 = this.codeStream;
            codeStream2.classFileOffset += 4;
        }
        else {
            this.codeStream.writeWidePosition(this);
        }
    }
    
    public int forwardReferenceCount() {
        if (this.delegate != null) {
            this.delegate.forwardReferenceCount();
        }
        return this.forwardReferenceCount;
    }
    
    public int[] forwardReferences() {
        if (this.delegate != null) {
            this.delegate.forwardReferences();
        }
        return this.forwardReferences;
    }
    
    public void initialize(final CodeStream stream) {
        this.codeStream = stream;
        this.position = -1;
        this.forwardReferenceCount = 0;
        this.delegate = null;
    }
    
    public boolean isCaseLabel() {
        return false;
    }
    
    public boolean isStandardLabel() {
        return true;
    }
    
    @Override
    public void place() {
        if (this.position == -1) {
            this.position = this.codeStream.position;
            this.codeStream.addLabel(this);
            final int oldPosition = this.position;
            boolean isOptimizedBranch = false;
            if (this.forwardReferenceCount != 0) {
                isOptimizedBranch = (this.forwardReferences[this.forwardReferenceCount - 1] + 2 == this.position && this.codeStream.bCodeStream[this.codeStream.classFileOffset - 3] == -89);
                if (isOptimizedBranch) {
                    if (this.codeStream.lastAbruptCompletion == this.position) {
                        this.codeStream.lastAbruptCompletion = -1;
                    }
                    final CodeStream codeStream = this.codeStream;
                    final int n = this.position - 3;
                    this.position = n;
                    codeStream.position = n;
                    final CodeStream codeStream2 = this.codeStream;
                    codeStream2.classFileOffset -= 3;
                    --this.forwardReferenceCount;
                    if (this.codeStream.lastEntryPC == oldPosition) {
                        this.codeStream.lastEntryPC = this.position;
                    }
                    if ((this.codeStream.generateAttributes & 0x1C) != 0x0) {
                        final LocalVariableBinding[] locals = this.codeStream.locals;
                        for (int i = 0, max = locals.length; i < max; ++i) {
                            final LocalVariableBinding local = locals[i];
                            if (local != null && local.initializationCount > 0) {
                                if (local.initializationPCs[(local.initializationCount - 1 << 1) + 1] == oldPosition) {
                                    local.initializationPCs[(local.initializationCount - 1 << 1) + 1] = this.position;
                                }
                                if (local.initializationPCs[local.initializationCount - 1 << 1] == oldPosition) {
                                    local.initializationPCs[local.initializationCount - 1 << 1] = this.position;
                                }
                            }
                        }
                    }
                    if ((this.codeStream.generateAttributes & 0x2) != 0x0) {
                        this.codeStream.removeUnusedPcToSourceMapEntries();
                    }
                }
            }
            for (int j = 0; j < this.forwardReferenceCount; ++j) {
                this.codeStream.writePosition(this, this.forwardReferences[j]);
            }
            if (isOptimizedBranch) {
                this.codeStream.optimizeBranch(oldPosition, this);
            }
        }
    }
    
    @Override
    public String toString() {
        String basic = this.getClass().getName();
        basic = basic.substring(basic.lastIndexOf(46) + 1);
        final StringBuffer buffer = new StringBuffer(basic);
        buffer.append('@').append(Integer.toHexString(this.hashCode()));
        buffer.append("(position=").append(this.position);
        if (this.delegate != null) {
            buffer.append("delegate=").append(this.delegate);
        }
        buffer.append(", forwards = [");
        for (int i = 0; i < this.forwardReferenceCount - 1; ++i) {
            buffer.append(String.valueOf(this.forwardReferences[i]) + ", ");
        }
        if (this.forwardReferenceCount >= 1) {
            buffer.append(this.forwardReferences[this.forwardReferenceCount - 1]);
        }
        buffer.append("] )");
        return buffer.toString();
    }
}
