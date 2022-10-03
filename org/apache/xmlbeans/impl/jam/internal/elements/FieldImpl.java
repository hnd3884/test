package org.apache.xmlbeans.impl.jam.internal.elements;

import java.io.StringWriter;
import org.apache.xmlbeans.impl.jam.JField;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import java.lang.reflect.Modifier;
import org.apache.xmlbeans.impl.jam.internal.classrefs.UnqualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.DirectJClassRef;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRefContext;
import org.apache.xmlbeans.impl.jam.internal.classrefs.QualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRef;
import org.apache.xmlbeans.impl.jam.mutable.MField;

public final class FieldImpl extends MemberImpl implements MField
{
    private JClassRef mTypeClassRef;
    
    FieldImpl(final String simpleName, final ClassImpl containingClass, final String qualifiedTypeClassName) {
        super(containingClass);
        super.setSimpleName(simpleName);
        this.mTypeClassRef = QualifiedJClassRef.create(qualifiedTypeClassName, containingClass);
    }
    
    @Override
    public void setType(final JClass type) {
        if (type == null) {
            throw new IllegalArgumentException("null type");
        }
        this.mTypeClassRef = DirectJClassRef.create(type);
    }
    
    @Override
    public void setType(final String qcname) {
        if (qcname == null) {
            throw new IllegalArgumentException("null qcname");
        }
        this.mTypeClassRef = QualifiedJClassRef.create(qcname, (JClassRefContext)this.getContainingClass());
    }
    
    @Override
    public void setUnqualifiedType(final String ucname) {
        if (ucname == null) {
            throw new IllegalArgumentException("null ucname");
        }
        this.mTypeClassRef = UnqualifiedJClassRef.create(ucname, (JClassRefContext)this.getContainingClass());
    }
    
    @Override
    public JClass getType() {
        if (this.mTypeClassRef == null) {
            throw new IllegalStateException();
        }
        return this.mTypeClassRef.getRefClass();
    }
    
    @Override
    public boolean isFinal() {
        return Modifier.isFinal(this.getModifiers());
    }
    
    @Override
    public boolean isStatic() {
        return Modifier.isStatic(this.getModifiers());
    }
    
    @Override
    public boolean isVolatile() {
        return Modifier.isVolatile(this.getModifiers());
    }
    
    @Override
    public boolean isTransient() {
        return Modifier.isTransient(this.getModifiers());
    }
    
    @Override
    public void accept(final MVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public void accept(final JVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String getQualifiedName() {
        final StringWriter sbuf = new StringWriter();
        sbuf.write(Modifier.toString(this.getModifiers()));
        sbuf.write(32);
        sbuf.write(this.getType().getQualifiedName());
        sbuf.write(32);
        sbuf.write(this.getContainingClass().getQualifiedName());
        sbuf.write(46);
        sbuf.write(this.getSimpleName());
        return sbuf.toString();
    }
}
