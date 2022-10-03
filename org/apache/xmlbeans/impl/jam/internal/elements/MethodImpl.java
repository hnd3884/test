package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.JParameter;
import java.io.StringWriter;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import java.lang.reflect.Modifier;
import org.apache.xmlbeans.impl.jam.internal.classrefs.DirectJClassRef;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.internal.classrefs.UnqualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRefContext;
import org.apache.xmlbeans.impl.jam.internal.classrefs.QualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRef;
import org.apache.xmlbeans.impl.jam.mutable.MMethod;

public final class MethodImpl extends InvokableImpl implements MMethod
{
    private JClassRef mReturnTypeRef;
    
    MethodImpl(final String simpleName, final ClassImpl containingClass) {
        super(containingClass);
        this.mReturnTypeRef = null;
        this.setSimpleName(simpleName);
    }
    
    @Override
    public void setReturnType(final String className) {
        this.mReturnTypeRef = QualifiedJClassRef.create(className, (JClassRefContext)this.getContainingClass());
    }
    
    @Override
    public void setUnqualifiedReturnType(final String unqualifiedTypeName) {
        this.mReturnTypeRef = UnqualifiedJClassRef.create(unqualifiedTypeName, (JClassRefContext)this.getContainingClass());
    }
    
    @Override
    public void setReturnType(final JClass c) {
        this.mReturnTypeRef = DirectJClassRef.create(c);
    }
    
    @Override
    public JClass getReturnType() {
        if (this.mReturnTypeRef == null) {
            return this.getClassLoader().loadClass("void");
        }
        return this.mReturnTypeRef.getRefClass();
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
    public boolean isAbstract() {
        return Modifier.isAbstract(this.getModifiers());
    }
    
    @Override
    public boolean isNative() {
        return Modifier.isNative(this.getModifiers());
    }
    
    @Override
    public boolean isSynchronized() {
        return Modifier.isSynchronized(this.getModifiers());
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
        final JClass returnJClass = this.getReturnType();
        if (returnJClass == null) {
            sbuf.write("void ");
        }
        else {
            sbuf.write(returnJClass.getQualifiedName());
            sbuf.write(32);
        }
        sbuf.write(this.getSimpleName());
        sbuf.write(40);
        final JParameter[] params = this.getParameters();
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; ++i) {
                sbuf.write(params[i].getType().getQualifiedName());
                if (i < params.length - 1) {
                    sbuf.write(44);
                }
            }
        }
        sbuf.write(41);
        final JClass[] thrown = this.getExceptionTypes();
        if (thrown != null && thrown.length > 0) {
            sbuf.write(" throws ");
            for (int i = 0; i < thrown.length; ++i) {
                sbuf.write(thrown[i].getQualifiedName());
                if (i < thrown.length - 1) {
                    sbuf.write(44);
                }
            }
        }
        return sbuf.toString();
    }
}
