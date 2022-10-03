package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import java.io.DataOutputStream;

public class RuntimeVisibleorInvisibleParameterAnnotationsAttribute extends AnnotationsAttribute
{
    private final int num_parameters;
    private final ParameterAnnotation[] parameter_annotations;
    
    public RuntimeVisibleorInvisibleParameterAnnotationsAttribute(final CPUTF8 name, final ParameterAnnotation[] parameter_annotations) {
        super(name);
        this.num_parameters = parameter_annotations.length;
        this.parameter_annotations = parameter_annotations;
    }
    
    @Override
    protected int getLength() {
        int length = 1;
        for (int i = 0; i < this.num_parameters; ++i) {
            length += this.parameter_annotations[i].getLength();
        }
        return length;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        for (int i = 0; i < this.parameter_annotations.length; ++i) {
            this.parameter_annotations[i].resolve(pool);
        }
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeByte(this.num_parameters);
        for (int i = 0; i < this.num_parameters; ++i) {
            this.parameter_annotations[i].writeBody(dos);
        }
    }
    
    @Override
    public String toString() {
        return this.attributeName.underlyingString() + ": " + this.num_parameters + " parameter annotations";
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        final List nested = new ArrayList();
        nested.add(this.attributeName);
        for (int i = 0; i < this.parameter_annotations.length; ++i) {
            nested.addAll(this.parameter_annotations[i].getClassFileEntries());
        }
        final ClassFileEntry[] nestedEntries = new ClassFileEntry[nested.size()];
        for (int j = 0; j < nestedEntries.length; ++j) {
            nestedEntries[j] = nested.get(j);
        }
        return nestedEntries;
    }
    
    public static class ParameterAnnotation
    {
        private final Annotation[] annotations;
        private final int num_annotations;
        
        public ParameterAnnotation(final Annotation[] annotations) {
            this.num_annotations = annotations.length;
            this.annotations = annotations;
        }
        
        public void writeBody(final DataOutputStream dos) throws IOException {
            dos.writeShort(this.num_annotations);
            for (int i = 0; i < this.annotations.length; ++i) {
                this.annotations[i].writeBody(dos);
            }
        }
        
        public void resolve(final ClassConstantPool pool) {
            for (int i = 0; i < this.annotations.length; ++i) {
                this.annotations[i].resolve(pool);
            }
        }
        
        public int getLength() {
            int length = 2;
            for (int i = 0; i < this.annotations.length; ++i) {
                length += this.annotations[i].getLength();
            }
            return length;
        }
        
        public List getClassFileEntries() {
            final List nested = new ArrayList();
            for (int i = 0; i < this.annotations.length; ++i) {
                nested.addAll(this.annotations[i].getClassFileEntries());
            }
            return nested;
        }
    }
}
