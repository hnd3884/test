package org.apache.xmlbeans.impl.jam.internal.classrefs;

import org.apache.xmlbeans.impl.jam.JClass;

public class DirectJClassRef implements JClassRef
{
    private JClass mClass;
    
    public static JClassRef create(final JClass clazz) {
        if (clazz instanceof JClassRef) {
            return (JClassRef)clazz;
        }
        return new DirectJClassRef(clazz);
    }
    
    private DirectJClassRef(final JClass clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("null clazz");
        }
        this.mClass = clazz;
    }
    
    @Override
    public JClass getRefClass() {
        return this.mClass;
    }
    
    @Override
    public String getQualifiedName() {
        return this.mClass.getQualifiedName();
    }
}
