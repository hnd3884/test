package org.apache.xmlbeans.impl.jam.visitor;

import org.apache.xmlbeans.impl.jam.JComment;
import org.apache.xmlbeans.impl.jam.JAnnotation;
import org.apache.xmlbeans.impl.jam.JParameter;
import org.apache.xmlbeans.impl.jam.JInvokable;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JConstructor;
import org.apache.xmlbeans.impl.jam.JField;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JAnnotatedElement;
import org.apache.xmlbeans.impl.jam.JPackage;

public class TraversingJVisitor extends JVisitor
{
    private JVisitor mDelegate;
    
    public TraversingJVisitor(final JVisitor jv) {
        if (jv == null) {
            throw new IllegalArgumentException("null jv");
        }
        this.mDelegate = jv;
    }
    
    @Override
    public void visit(final JPackage pkg) {
        pkg.accept(this.mDelegate);
        final JClass[] c = pkg.getClasses();
        for (int i = 0; i < c.length; ++i) {
            this.visit(c[i]);
        }
        this.visitAnnotations(pkg);
        this.visitComment(pkg);
    }
    
    @Override
    public void visit(final JClass clazz) {
        clazz.accept(this.mDelegate);
        final JField[] f = clazz.getDeclaredFields();
        for (int i = 0; i < f.length; ++i) {
            this.visit(f[i]);
        }
        final JConstructor[] c = clazz.getConstructors();
        for (int i = 0; i < c.length; ++i) {
            this.visit(c[i]);
        }
        final JMethod[] m = clazz.getMethods();
        for (int i = 0; i < m.length; ++i) {
            this.visit(m[i]);
        }
        this.visitAnnotations(clazz);
        this.visitComment(clazz);
    }
    
    @Override
    public void visit(final JField field) {
        field.accept(this.mDelegate);
        this.visitAnnotations(field);
        this.visitComment(field);
    }
    
    @Override
    public void visit(final JConstructor ctor) {
        ctor.accept(this.mDelegate);
        this.visitParameters(ctor);
        this.visitAnnotations(ctor);
        this.visitComment(ctor);
    }
    
    @Override
    public void visit(final JMethod method) {
        method.accept(this.mDelegate);
        this.visitParameters(method);
        this.visitAnnotations(method);
        this.visitComment(method);
    }
    
    @Override
    public void visit(final JParameter param) {
        param.accept(this.mDelegate);
        this.visitAnnotations(param);
        this.visitComment(param);
    }
    
    @Override
    public void visit(final JAnnotation ann) {
        ann.accept(this.mDelegate);
    }
    
    @Override
    public void visit(final JComment comment) {
        comment.accept(this.mDelegate);
    }
    
    private void visitParameters(final JInvokable iv) {
        final JParameter[] p = iv.getParameters();
        for (int i = 0; i < p.length; ++i) {
            this.visit(p[i]);
        }
    }
    
    private void visitAnnotations(final JAnnotatedElement ae) {
        final JAnnotation[] anns = ae.getAnnotations();
        for (int i = 0; i < anns.length; ++i) {
            this.visit(anns[i]);
        }
    }
    
    private void visitComment(final JAnnotatedElement e) {
        final JComment c = e.getComment();
        if (c != null) {
            this.visit(c);
        }
    }
}
