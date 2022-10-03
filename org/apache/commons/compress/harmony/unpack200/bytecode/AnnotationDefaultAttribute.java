package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.io.IOException;
import java.io.DataOutputStream;

public class AnnotationDefaultAttribute extends AnnotationsAttribute
{
    private final ElementValue element_value;
    private static CPUTF8 attributeName;
    
    public static void setAttributeName(final CPUTF8 cpUTF8Value) {
        AnnotationDefaultAttribute.attributeName = cpUTF8Value;
    }
    
    public AnnotationDefaultAttribute(final ElementValue element_value) {
        super(AnnotationDefaultAttribute.attributeName);
        this.element_value = element_value;
    }
    
    @Override
    protected int getLength() {
        return this.element_value.getLength();
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        this.element_value.writeBody(dos);
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.element_value.resolve(pool);
    }
    
    @Override
    public String toString() {
        return "AnnotationDefault: " + this.element_value;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        final List nested = new ArrayList();
        nested.add(AnnotationDefaultAttribute.attributeName);
        nested.addAll(this.element_value.getClassFileEntries());
        final ClassFileEntry[] nestedEntries = new ClassFileEntry[nested.size()];
        for (int i = 0; i < nestedEntries.length; ++i) {
            nestedEntries[i] = nested.get(i);
        }
        return nestedEntries;
    }
}
