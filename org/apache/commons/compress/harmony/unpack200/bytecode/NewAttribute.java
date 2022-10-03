package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.Iterator;
import java.io.IOException;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewAttribute extends BCIRenumberedAttribute
{
    private final List lengths;
    private final List body;
    private ClassConstantPool pool;
    private final int layoutIndex;
    
    public NewAttribute(final CPUTF8 attributeName, final int layoutIndex) {
        super(attributeName);
        this.lengths = new ArrayList();
        this.body = new ArrayList();
        this.layoutIndex = layoutIndex;
    }
    
    public int getLayoutIndex() {
        return this.layoutIndex;
    }
    
    @Override
    protected int getLength() {
        int length = 0;
        for (int iter = 0; iter < this.lengths.size(); ++iter) {
            length += this.lengths.get(iter);
        }
        return length;
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        for (int i = 0; i < this.lengths.size(); ++i) {
            final int length = this.lengths.get(i);
            final Object obj = this.body.get(i);
            long value = 0L;
            if (obj instanceof Long) {
                value = (long)obj;
            }
            else if (obj instanceof ClassFileEntry) {
                value = this.pool.indexOf((ClassFileEntry)obj);
            }
            else if (obj instanceof BCValue) {
                value = ((BCValue)obj).actualValue;
            }
            if (length == 1) {
                dos.writeByte((int)value);
            }
            else if (length == 2) {
                dos.writeShort((int)value);
            }
            else if (length == 4) {
                dos.writeInt((int)value);
            }
            else if (length == 8) {
                dos.writeLong(value);
            }
        }
    }
    
    @Override
    public String toString() {
        return this.attributeName.underlyingString();
    }
    
    public void addInteger(final int length, final long value) {
        this.lengths.add(length);
        this.body.add(value);
    }
    
    public void addBCOffset(final int length, final int value) {
        this.lengths.add(length);
        this.body.add(new BCOffset(value));
    }
    
    public void addBCIndex(final int length, final int value) {
        this.lengths.add(length);
        this.body.add(new BCIndex(value));
    }
    
    public void addBCLength(final int length, final int value) {
        this.lengths.add(length);
        this.body.add(new BCLength(value));
    }
    
    public void addToBody(final int length, final Object value) {
        this.lengths.add(length);
        this.body.add(value);
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        for (int iter = 0; iter < this.body.size(); ++iter) {
            final Object element = this.body.get(iter);
            if (element instanceof ClassFileEntry) {
                ((ClassFileEntry)element).resolve(pool);
            }
        }
        this.pool = pool;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        int total = 1;
        for (int iter = 0; iter < this.body.size(); ++iter) {
            final Object element = this.body.get(iter);
            if (element instanceof ClassFileEntry) {
                ++total;
            }
        }
        final ClassFileEntry[] nested = new ClassFileEntry[total];
        nested[0] = this.getAttributeName();
        int i = 1;
        for (int iter2 = 0; iter2 < this.body.size(); ++iter2) {
            final Object element2 = this.body.get(iter2);
            if (element2 instanceof ClassFileEntry) {
                nested[i] = (ClassFileEntry)element2;
                ++i;
            }
        }
        return nested;
    }
    
    @Override
    protected int[] getStartPCs() {
        return null;
    }
    
    @Override
    public void renumber(final List byteCodeOffsets) {
        if (!this.renumbered) {
            Object previous = null;
            for (final Object obj : this.body) {
                if (obj instanceof BCIndex) {
                    final BCIndex bcIndex = (BCIndex)obj;
                    bcIndex.setActualValue(byteCodeOffsets.get(bcIndex.index));
                }
                else if (obj instanceof BCOffset) {
                    final BCOffset bcOffset = (BCOffset)obj;
                    if (previous instanceof BCIndex) {
                        final int index = ((BCIndex)previous).index + bcOffset.offset;
                        bcOffset.setIndex(index);
                        bcOffset.setActualValue(byteCodeOffsets.get(index));
                    }
                    else if (previous instanceof BCOffset) {
                        final int index = ((BCOffset)previous).index + bcOffset.offset;
                        bcOffset.setIndex(index);
                        bcOffset.setActualValue(byteCodeOffsets.get(index));
                    }
                    else {
                        bcOffset.setActualValue(byteCodeOffsets.get(bcOffset.offset));
                    }
                }
                else if (obj instanceof BCLength) {
                    final BCLength bcLength = (BCLength)obj;
                    final BCIndex prevIndex = (BCIndex)previous;
                    final int index2 = prevIndex.index + bcLength.length;
                    final int actualLength = byteCodeOffsets.get(index2) - prevIndex.actualValue;
                    bcLength.setActualValue(actualLength);
                }
                previous = obj;
            }
            this.renumbered = true;
        }
    }
    
    private static class BCOffset extends BCValue
    {
        private final int offset;
        private int index;
        
        public BCOffset(final int offset) {
            this.offset = offset;
        }
        
        public void setIndex(final int index) {
            this.index = index;
        }
    }
    
    private static class BCIndex extends BCValue
    {
        private final int index;
        
        public BCIndex(final int index) {
            this.index = index;
        }
    }
    
    private static class BCLength extends BCValue
    {
        private final int length;
        
        public BCLength(final int length) {
            this.length = length;
        }
    }
    
    private abstract static class BCValue
    {
        int actualValue;
        
        public void setActualValue(final int value) {
            this.actualValue = value;
        }
    }
}
