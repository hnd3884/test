package org.apache.xmlbeans.impl.jam.visitor;

import org.apache.xmlbeans.impl.jam.JComment;
import org.apache.xmlbeans.impl.jam.JAnnotation;
import org.apache.xmlbeans.impl.jam.JParameter;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JField;
import org.apache.xmlbeans.impl.jam.JConstructor;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JPackage;

public class CompositeJVisitor extends JVisitor
{
    private JVisitor[] mVisitors;
    
    public CompositeJVisitor(final JVisitor[] visitors) {
        if (visitors == null) {
            throw new IllegalArgumentException("null visitors");
        }
        this.mVisitors = visitors;
    }
    
    @Override
    public void visit(final JPackage pkg) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(pkg);
        }
    }
    
    @Override
    public void visit(final JClass clazz) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(clazz);
        }
    }
    
    @Override
    public void visit(final JConstructor ctor) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(ctor);
        }
    }
    
    @Override
    public void visit(final JField field) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(field);
        }
    }
    
    @Override
    public void visit(final JMethod method) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(method);
        }
    }
    
    @Override
    public void visit(final JParameter param) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(param);
        }
    }
    
    @Override
    public void visit(final JAnnotation ann) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(ann);
        }
    }
    
    @Override
    public void visit(final JComment comment) {
        for (int i = 0; i < this.mVisitors.length; ++i) {
            this.mVisitors[i].visit(comment);
        }
    }
}
