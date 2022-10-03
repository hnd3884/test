package org.apache.xmlbeans.impl.jam.visitor;

import org.apache.xmlbeans.impl.jam.mutable.MComment;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotation;
import org.apache.xmlbeans.impl.jam.mutable.MParameter;
import org.apache.xmlbeans.impl.jam.mutable.MInvokable;
import org.apache.xmlbeans.impl.jam.mutable.MMethod;
import org.apache.xmlbeans.impl.jam.mutable.MConstructor;
import org.apache.xmlbeans.impl.jam.mutable.MField;
import org.apache.xmlbeans.impl.jam.mutable.MClass;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotatedElement;
import org.apache.xmlbeans.impl.jam.mutable.MPackage;

public class TraversingMVisitor extends MVisitor
{
    private MVisitor mDelegate;
    
    public TraversingMVisitor(final MVisitor jv) {
        if (jv == null) {
            throw new IllegalArgumentException("null jv");
        }
        this.mDelegate = jv;
    }
    
    @Override
    public void visit(final MPackage pkg) {
        pkg.accept(this.mDelegate);
        final MClass[] c = pkg.getMutableClasses();
        for (int i = 0; i < c.length; ++i) {
            this.visit(c[i]);
        }
        this.visitAnnotations(pkg);
        this.visitComment(pkg);
    }
    
    @Override
    public void visit(final MClass clazz) {
        clazz.accept(this.mDelegate);
        final MField[] f = clazz.getMutableFields();
        for (int i = 0; i < f.length; ++i) {
            this.visit(f[i]);
        }
        final MConstructor[] c = clazz.getMutableConstructors();
        for (int i = 0; i < c.length; ++i) {
            this.visit(c[i]);
        }
        final MMethod[] m = clazz.getMutableMethods();
        for (int i = 0; i < m.length; ++i) {
            this.visit(m[i]);
        }
        this.visitAnnotations(clazz);
        this.visitComment(clazz);
    }
    
    @Override
    public void visit(final MField field) {
        field.accept(this.mDelegate);
        this.visitAnnotations(field);
        this.visitComment(field);
    }
    
    @Override
    public void visit(final MConstructor ctor) {
        ctor.accept(this.mDelegate);
        this.visitParameters(ctor);
        this.visitAnnotations(ctor);
        this.visitComment(ctor);
    }
    
    @Override
    public void visit(final MMethod method) {
        method.accept(this.mDelegate);
        this.visitParameters(method);
        this.visitAnnotations(method);
        this.visitComment(method);
    }
    
    @Override
    public void visit(final MParameter param) {
        param.accept(this.mDelegate);
        this.visitAnnotations(param);
        this.visitComment(param);
    }
    
    @Override
    public void visit(final MAnnotation ann) {
        ann.accept(this.mDelegate);
    }
    
    @Override
    public void visit(final MComment comment) {
        comment.accept(this.mDelegate);
    }
    
    private void visitParameters(final MInvokable iv) {
        final MParameter[] p = iv.getMutableParameters();
        for (int i = 0; i < p.length; ++i) {
            this.visit(p[i]);
        }
    }
    
    private void visitAnnotations(final MAnnotatedElement ae) {
        final MAnnotation[] anns = ae.getMutableAnnotations();
        for (int i = 0; i < anns.length; ++i) {
            this.visit(anns[i]);
        }
    }
    
    private void visitComment(final MAnnotatedElement e) {
        final MComment c = e.getMutableComment();
        if (c != null) {
            this.visit(c);
        }
    }
}
