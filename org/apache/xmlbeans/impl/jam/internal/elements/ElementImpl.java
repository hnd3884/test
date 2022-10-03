package org.apache.xmlbeans.impl.jam.internal.elements;

import org.apache.xmlbeans.impl.jam.internal.JamServiceContextImpl;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import org.apache.xmlbeans.impl.jam.JSourcePosition;
import org.apache.xmlbeans.impl.jam.JElement;
import org.apache.xmlbeans.impl.jam.mutable.MSourcePosition;
import org.apache.xmlbeans.impl.jam.JProperty;
import org.apache.xmlbeans.impl.jam.JPackage;
import org.apache.xmlbeans.impl.jam.mutable.MElement;

public abstract class ElementImpl implements Comparable, MElement
{
    public static final ElementImpl[] NO_NODE;
    public static final ClassImpl[] NO_CLASS;
    public static final FieldImpl[] NO_FIELD;
    public static final ConstructorImpl[] NO_CONSTRUCTOR;
    public static final MethodImpl[] NO_METHOD;
    public static final ParameterImpl[] NO_PARAMETER;
    public static final JPackage[] NO_PACKAGE;
    public static final AnnotationImpl[] NO_ANNOTATION;
    public static final CommentImpl[] NO_COMMENT;
    public static final JProperty[] NO_PROPERTY;
    private ElementContext mContext;
    protected String mSimpleName;
    private MSourcePosition mPosition;
    private Object mArtifact;
    private ElementImpl mParent;
    
    protected ElementImpl(final ElementImpl parent) {
        this.mPosition = null;
        this.mArtifact = null;
        if (parent == null) {
            throw new IllegalArgumentException("null ctx");
        }
        if (parent == this) {
            throw new IllegalArgumentException("An element cannot be its own parent");
        }
        for (JElement check = parent.getParent(); check != null; check = check.getParent()) {
            if (check == this) {
                throw new IllegalArgumentException("cycle detected");
            }
        }
        this.mContext = parent.getContext();
        this.mParent = parent;
    }
    
    protected ElementImpl(final ElementContext ctx) {
        this.mPosition = null;
        this.mArtifact = null;
        if (ctx == null) {
            throw new IllegalArgumentException("null ctx");
        }
        this.mContext = ctx;
    }
    
    @Override
    public final JElement getParent() {
        return this.mParent;
    }
    
    @Override
    public String getSimpleName() {
        return this.mSimpleName;
    }
    
    @Override
    public JSourcePosition getSourcePosition() {
        return this.mPosition;
    }
    
    @Override
    public Object getArtifact() {
        return this.mArtifact;
    }
    
    @Override
    public void setSimpleName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        this.mSimpleName = name.trim();
    }
    
    @Override
    public MSourcePosition createSourcePosition() {
        return this.mPosition = new SourcePositionImpl();
    }
    
    @Override
    public void removeSourcePosition() {
        this.mPosition = null;
    }
    
    @Override
    public MSourcePosition getMutableSourcePosition() {
        return this.mPosition;
    }
    
    @Override
    public void setArtifact(final Object artifact) {
        if (artifact == null) {}
        if (this.mArtifact != null) {
            throw new IllegalStateException("artifact already set");
        }
        this.mArtifact = artifact;
    }
    
    @Override
    public JamClassLoader getClassLoader() {
        return this.mContext.getClassLoader();
    }
    
    public static String defaultName(final int count) {
        return "unnamed_" + count;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ElementImpl)) {
            return false;
        }
        final ElementImpl eElement = (ElementImpl)o;
        final String qn = this.getQualifiedName();
        if (qn == null) {
            return false;
        }
        final String oqn = eElement.getQualifiedName();
        return oqn != null && qn.equals(oqn);
    }
    
    @Override
    public int hashCode() {
        final String qn = this.getQualifiedName();
        return (qn == null) ? 0 : qn.hashCode();
    }
    
    public ElementContext getContext() {
        return this.mContext;
    }
    
    @Override
    public String toString() {
        return this.getQualifiedName();
    }
    
    protected JamLogger getLogger() {
        return ((JamServiceContextImpl)this.mContext).getLogger();
    }
    
    @Override
    public int compareTo(final Object o) {
        if (!(o instanceof JElement)) {
            return -1;
        }
        return this.getQualifiedName().compareTo(((JElement)o).getQualifiedName());
    }
    
    static {
        NO_NODE = new ElementImpl[0];
        NO_CLASS = new ClassImpl[0];
        NO_FIELD = new FieldImpl[0];
        NO_CONSTRUCTOR = new ConstructorImpl[0];
        NO_METHOD = new MethodImpl[0];
        NO_PARAMETER = new ParameterImpl[0];
        NO_PACKAGE = new JPackage[0];
        NO_ANNOTATION = new AnnotationImpl[0];
        NO_COMMENT = new CommentImpl[0];
        NO_PROPERTY = new JProperty[0];
    }
}
