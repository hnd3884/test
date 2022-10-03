package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import java.io.DataOutputStream;

public class RuntimeVisibleorInvisibleAnnotationsAttribute extends AnnotationsAttribute
{
    private final int num_annotations;
    private final Annotation[] annotations;
    
    public RuntimeVisibleorInvisibleAnnotationsAttribute(final CPUTF8 name, final Annotation[] annotations) {
        super(name);
        this.num_annotations = annotations.length;
        this.annotations = annotations;
    }
    
    @Override
    protected int getLength() {
        int length = 2;
        for (int i = 0; i < this.num_annotations; ++i) {
            length += this.annotations[i].getLength();
        }
        return length;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        for (int i = 0; i < this.annotations.length; ++i) {
            this.annotations[i].resolve(pool);
        }
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        final int size = dos.size();
        dos.writeShort(this.num_annotations);
        for (int i = 0; i < this.num_annotations; ++i) {
            this.annotations[i].writeBody(dos);
        }
        if (dos.size() - size != this.getLength()) {
            throw new Error();
        }
    }
    
    @Override
    public String toString() {
        return this.attributeName.underlyingString() + ": " + this.num_annotations + " annotations";
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        final List nested = new ArrayList();
        nested.add(this.attributeName);
        for (int i = 0; i < this.annotations.length; ++i) {
            nested.addAll(this.annotations[i].getClassFileEntries());
        }
        final ClassFileEntry[] nestedEntries = new ClassFileEntry[nested.size()];
        for (int j = 0; j < nestedEntries.length; ++j) {
            nestedEntries[j] = nested.get(j);
        }
        return nestedEntries;
    }
}
