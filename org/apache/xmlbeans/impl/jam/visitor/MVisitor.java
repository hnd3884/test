package org.apache.xmlbeans.impl.jam.visitor;

import org.apache.xmlbeans.impl.jam.mutable.MComment;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotation;
import org.apache.xmlbeans.impl.jam.mutable.MParameter;
import org.apache.xmlbeans.impl.jam.mutable.MMethod;
import org.apache.xmlbeans.impl.jam.mutable.MField;
import org.apache.xmlbeans.impl.jam.mutable.MConstructor;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.mutable.MPackage;

public abstract class MVisitor
{
    public void visit(final MPackage pkg) {
    }
    
    public void visit(final MClass clazz) {
    }
    
    public void visit(final MConstructor ctor) {
    }
    
    public void visit(final MField field) {
    }
    
    public void visit(final MMethod method) {
    }
    
    public void visit(final MParameter param) {
    }
    
    public void visit(final MAnnotation ann) {
    }
    
    public void visit(final MComment comment) {
    }
}
