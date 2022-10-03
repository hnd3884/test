package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.mutable.MMethod;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.JSourcePosition;
import org.apache.xmlbeans.impl.jam.JComment;
import org.apache.xmlbeans.impl.jam.JAnnotation;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRefContext;
import org.apache.xmlbeans.impl.jam.internal.classrefs.QualifiedJClassRef;
import org.apache.xmlbeans.impl.jam.internal.classrefs.JClassRef;
import org.apache.xmlbeans.impl.jam.JMethod;
import org.apache.xmlbeans.impl.jam.JProperty;

public class PropertyImpl extends AnnotatedElementImpl implements JProperty
{
    private String mName;
    private JMethod mGetter;
    private JMethod mSetter;
    private JClassRef mTypeRef;
    
    public PropertyImpl(final String name, final JMethod getter, final JMethod setter, final String qualifiedTypeName) {
        super((ElementImpl)((getter != null) ? getter.getParent() : setter.getParent()));
        this.mName = name;
        this.mGetter = getter;
        this.mSetter = setter;
        final ClassImpl cont = (ClassImpl)((getter != null) ? getter.getContainingClass() : setter.getContainingClass());
        this.mTypeRef = QualifiedJClassRef.create(qualifiedTypeName, cont);
        this.initAnnotations();
    }
    
    @Override
    public JClass getType() {
        return this.mTypeRef.getRefClass();
    }
    
    @Override
    public String getSimpleName() {
        return this.mName;
    }
    
    @Override
    public String getQualifiedName() {
        return this.getParent().getQualifiedName() + "." + this.getSimpleName();
    }
    
    @Override
    public JMethod getSetter() {
        return this.mSetter;
    }
    
    @Override
    public JMethod getGetter() {
        return this.mGetter;
    }
    
    @Override
    public JAnnotation[] getAnnotations() {
        return this.combine((this.mGetter == null) ? ElementImpl.NO_ANNOTATION : this.mGetter.getAnnotations(), (this.mSetter == null) ? ElementImpl.NO_ANNOTATION : this.mSetter.getAnnotations());
    }
    
    public void setSetter(final JMethod method) {
        this.mSetter = method;
    }
    
    public void setGetter(final JMethod method) {
        this.mGetter = method;
    }
    
    @Override
    public JAnnotation getAnnotation(final String named) {
        final JAnnotation out = (this.mGetter != null) ? this.mGetter.getAnnotation(named) : null;
        if (out != null) {
            return out;
        }
        return (this.mSetter != null) ? this.mSetter.getAnnotation(named) : null;
    }
    
    @Override
    public JComment getComment() {
        if (this.mGetter != null) {
            return this.mGetter.getComment();
        }
        if (this.mSetter != null) {
            return this.mSetter.getComment();
        }
        return null;
    }
    
    @Override
    public JSourcePosition getSourcePosition() {
        return (this.mGetter != null) ? this.mGetter.getSourcePosition() : this.mSetter.getSourcePosition();
    }
    
    @Override
    public void accept(final JVisitor visitor) {
        if (this.mGetter != null) {
            visitor.visit(this.mGetter);
        }
        if (this.mSetter != null) {
            visitor.visit(this.mSetter);
        }
    }
    
    @Override
    public String toString() {
        return this.getQualifiedName();
    }
    
    private void initAnnotations() {
        if (this.mSetter != null) {
            JAnnotation[] anns = this.mSetter.getAnnotations();
            for (int i = 0; i < anns.length; ++i) {
                super.addAnnotation(anns[i]);
            }
            anns = this.mSetter.getAllJavadocTags();
            for (int i = 0; i < anns.length; ++i) {
                super.addAnnotation(anns[i]);
            }
        }
        if (this.mGetter != null) {
            JAnnotation[] anns = this.mGetter.getAnnotations();
            for (int i = 0; i < anns.length; ++i) {
                super.addAnnotation(anns[i]);
            }
            anns = this.mGetter.getAllJavadocTags();
            for (int i = 0; i < anns.length; ++i) {
                super.addAnnotation(anns[i]);
            }
        }
    }
    
    private JAnnotation[] combine(final JAnnotation[] a, final JAnnotation[] b) {
        if (a.length == 0) {
            return b;
        }
        if (b.length == 0) {
            return a;
        }
        final JAnnotation[] out = new JAnnotation[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
    
    private JComment[] combine(final JComment[] a, final JComment[] b) {
        if (a.length == 0) {
            return b;
        }
        if (b.length == 0) {
            return a;
        }
        final JComment[] out = new JComment[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }
    
    @Override
    public void accept(final MVisitor visitor) {
        if (this.mGetter != null) {
            visitor.visit((MMethod)this.mGetter);
        }
        if (this.mSetter != null) {
            visitor.visit((MMethod)this.mSetter);
        }
    }
}
