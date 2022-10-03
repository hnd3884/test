package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.JAnnotation;
import org.apache.xmlbeans.impl.jam.visitor.JVisitor;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JAnnotationValue;
import org.apache.xmlbeans.impl.jam.annotation.AnnotationProxy;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotation;

public final class AnnotationImpl extends ElementImpl implements MAnnotation
{
    private AnnotationProxy mProxy;
    private Object mAnnotationInstance;
    private String mQualifiedName;
    
    AnnotationImpl(final ElementContext ctx, final AnnotationProxy proxy, final String qualifiedName) {
        super(ctx);
        this.mAnnotationInstance = null;
        this.mQualifiedName = null;
        if (proxy == null) {
            throw new IllegalArgumentException("null proxy");
        }
        if (qualifiedName == null) {
            throw new IllegalArgumentException("null qn");
        }
        this.mProxy = proxy;
        this.setSimpleName(qualifiedName.substring(qualifiedName.lastIndexOf(46) + 1));
        this.mQualifiedName = qualifiedName;
    }
    
    @Override
    public Object getProxy() {
        return this.mProxy;
    }
    
    @Override
    public JAnnotationValue[] getValues() {
        return this.mProxy.getValues();
    }
    
    @Override
    public JAnnotationValue getValue(final String name) {
        return this.mProxy.getValue(name);
    }
    
    @Override
    public Object getAnnotationInstance() {
        return this.mAnnotationInstance;
    }
    
    @Override
    public void setAnnotationInstance(final Object o) {
        this.mAnnotationInstance = o;
    }
    
    @Override
    public void setSimpleValue(final String name, final Object value, final JClass type) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (type == null) {
            throw new IllegalArgumentException("null type");
        }
        if (value == null) {
            throw new IllegalArgumentException("null value");
        }
        this.mProxy.setValue(name, value, type);
    }
    
    @Override
    public MAnnotation createNestedValue(final String name, final String annTypeName) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (annTypeName == null) {
            throw new IllegalArgumentException("null typename");
        }
        final AnnotationProxy p = this.getContext().createAnnotationProxy(annTypeName);
        final AnnotationImpl out = new AnnotationImpl(this.getContext(), p, annTypeName);
        final JClass type = this.getContext().getClassLoader().loadClass(annTypeName);
        this.mProxy.setValue(name, out, type);
        return out;
    }
    
    @Override
    public MAnnotation[] createNestedValueArray(final String name, final String annComponentTypeName, final int dimensions) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (annComponentTypeName == null) {
            throw new IllegalArgumentException("null typename");
        }
        if (dimensions < 0) {
            throw new IllegalArgumentException("dimensions = " + dimensions);
        }
        final MAnnotation[] out = new MAnnotation[dimensions];
        for (int i = 0; i < out.length; ++i) {
            final AnnotationProxy p = this.getContext().createAnnotationProxy(annComponentTypeName);
            out[i] = new AnnotationImpl(this.getContext(), p, annComponentTypeName);
        }
        final JClass type = this.getContext().getClassLoader().loadClass("[L" + annComponentTypeName + ";");
        this.mProxy.setValue(name, out, type);
        return out;
    }
    
    @Override
    public String getQualifiedName() {
        return this.mQualifiedName;
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
