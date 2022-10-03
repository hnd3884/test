package org.apache.xmlbeans.impl.jam.visitor;

import org.apache.xmlbeans.impl.jam.JProperty;
import org.apache.xmlbeans.impl.jam.JComment;
import org.apache.xmlbeans.impl.jam.JAnnotation;
import org.apache.xmlbeans.impl.jam.JParameter;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JField;
import org.apache.xmlbeans.impl.jam.JConstructor;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JPackage;

public abstract class JVisitor
{
    public void visit(final JPackage pkg) {
    }
    
    public void visit(final JClass clazz) {
    }
    
    public void visit(final JConstructor ctor) {
    }
    
    public void visit(final JField field) {
    }
    
    public void visit(final JMethod method) {
    }
    
    public void visit(final JParameter param) {
    }
    
    public void visit(final JAnnotation ann) {
    }
    
    public void visit(final JComment comment) {
    }
    
    public void visit(final JProperty proprty) {
    }
}
