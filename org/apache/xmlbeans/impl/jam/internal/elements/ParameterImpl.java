package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.JParameter;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.internal.classrefs.UnqualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.DirectJClassRef;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRefContext;
import org.apache.xmlbeans.impl.jam.internal.classrefs.QualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRef;
import org.apache.xmlbeans.impl.jam.mutable.MParameter;

public class ParameterImpl extends MemberImpl implements MParameter
{
    private JClassRef mTypeClassRef;
    
    ParameterImpl(final String simpleName, final InvokableImpl containingMember, final String typeName) {
        super(containingMember);
        this.setSimpleName(simpleName);
        this.setType(typeName);
    }
    
    @Override
    public String getQualifiedName() {
        return this.getSimpleName();
    }
    
    @Override
    public void setType(final String qcname) {
        if (qcname == null) {
            throw new IllegalArgumentException("null typename");
        }
        this.mTypeClassRef = QualifiedJClassRef.create(qcname, (JClassRefContext)this.getContainingClass());
    }
    
    @Override
    public void setType(final JClass qcname) {
        if (qcname == null) {
            throw new IllegalArgumentException("null qcname");
        }
        this.mTypeClassRef = DirectJClassRef.create(qcname);
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
        return this.mTypeClassRef.getRefClass();
    }
    
    @Override
    public void accept(final MVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public void accept(final JVisitor visitor) {
        visitor.visit(this);
    }
}
