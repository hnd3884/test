package org.apache.xmlbeans.impl.jam.visitor;

import org.apache.xmlbeans.impl.jam.mutable.MComment;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotation;
import org.apache.xmlbeans.impl.jam.mutable.MParameter;
import org.apache.xmlbeans.impl.jam.mutable.MMethod;
import org.apache.xmlbeans.impl.jam.mutable.MField;
import org.apache.xmlbeans.impl.jam.mutable.MConstructor;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.mutable.MPackage;

public class CompositeMVisitor extends MVisitor
{
    private MVisitor[] mVisitors;
    
    public CompositeMVisitor(final MVisitor[] visitors) {
        if (visitors == null) {
            throw new IllegalArgumentException("null visitors");
        }
        this.mVisitors = visitors;
    }
    
    @Override
    public void visit(final MPackage pkg) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(pkg);
        }
    }
    
    @Override
    public void visit(final MClass clazz) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(clazz);
        }
    }
    
    @Override
    public void visit(final MConstructor ctor) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(ctor);
        }
    }
    
    @Override
    public void visit(final MField field) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(field);
        }
    }
    
    @Override
    public void visit(final MMethod method) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(method);
        }
    }
    
    @Override
    public void visit(final MParameter param) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(param);
        }
    }
    
    @Override
    public void visit(final MAnnotation ann) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(ann);
        }
    }
    
    @Override
    public void visit(final MComment comment) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(comment);
        }
    }
}
