package com.sun.xml.internal.ws.org.objectweb.asm;

public interface FieldVisitor
{
    AnnotationVisitor visitAnnotation(final String p0, final boolean p1);
    
    void visitAttribute(final Attribute p0);
    
    void visitEnd();
}
