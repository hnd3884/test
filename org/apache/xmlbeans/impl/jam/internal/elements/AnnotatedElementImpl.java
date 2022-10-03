package org.apache.xmlbeans.impl.jam.internal.elements;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotation;
import org.apache.xmlbeans.impl.jam.annotation.AnnotationProxy;
import org.apache.xmlbeans.impl.jam.JComment;
import org.apache.xmlbeans.impl.jam.JAnnotationValue;
import org.apache.xmlbeans.impl.jam.JAnnotation;
import java.util.List;
import org.apache.xmlbeans.impl.jam.mutable.MComment;
import java.util.Map;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotatedElement;

public abstract class AnnotatedElementImpl extends ElementImpl implements MAnnotatedElement
{
    private Map mName2Annotation;
    private MComment mComment;
    private List mAllAnnotations;
    
    protected AnnotatedElementImpl(final ElementContext ctx) {
        super(ctx);
        this.mName2Annotation = null;
        this.mComment = null;
        this.mAllAnnotations = null;
    }
    
    protected AnnotatedElementImpl(final ElementImpl parent) {
        super(parent);
        this.mName2Annotation = null;
        this.mComment = null;
        this.mAllAnnotations = null;
    }
    
    @Override
    public JAnnotation[] getAnnotations() {
        return this.getMutableAnnotations();
    }
    
    @Override
    public JAnnotation getAnnotation(final Class proxyClass) {
        return this.getMutableAnnotation(proxyClass.getName());
    }
    
    @Override
    public JAnnotation getAnnotation(final String named) {
        return this.getMutableAnnotation(named);
    }
    
    @Override
    public JAnnotationValue getAnnotationValue(String valueId) {
        if (this.mName2Annotation == null) {
            return null;
        }
        valueId = valueId.trim();
        final int delim = valueId.indexOf(64);
        if (delim == -1 || delim == valueId.length() - 1) {
            final JAnnotation ann = this.getAnnotation(valueId);
            if (ann == null) {
                return null;
            }
            return ann.getValue("value");
        }
        else {
            final JAnnotation ann = this.getAnnotation(valueId.substring(0, delim));
            if (ann == null) {
                return null;
            }
            return ann.getValue(valueId.substring(delim + 1));
        }
    }
    
    @Override
    public Object getAnnotationProxy(final Class proxyClass) {
        return this.getEditableProxy(proxyClass);
    }
    
    @Override
    public JComment getComment() {
        return this.getMutableComment();
    }
    
    @Override
    @Deprecated
    public JAnnotation[] getAllJavadocTags() {
        if (this.mAllAnnotations == null) {
            return AnnotatedElementImpl.NO_ANNOTATION;
        }
        final JAnnotation[] out = new JAnnotation[this.mAllAnnotations.size()];
        this.mAllAnnotations.toArray(out);
        return out;
    }
    
    public AnnotationProxy getEditableProxy(final Class proxyClass) {
        if (this.mName2Annotation == null) {
            return null;
        }
        final MAnnotation out = this.getMutableAnnotation(proxyClass.getName());
        return (out == null) ? null : ((AnnotationProxy)out.getProxy());
    }
    
    public void removeAnnotation(final MAnnotation ann) {
        if (this.mName2Annotation != null) {
            this.mName2Annotation.values().remove(ann);
        }
    }
    
    @Override
    public MAnnotation[] getMutableAnnotations() {
        if (this.mName2Annotation == null) {
            return new MAnnotation[0];
        }
        final MAnnotation[] out = new MAnnotation[this.mName2Annotation.values().size()];
        this.mName2Annotation.values().toArray(out);
        return out;
    }
    
    @Override
    public MAnnotation getMutableAnnotation(String named) {
        if (this.mName2Annotation == null) {
            return null;
        }
        named = named.trim();
        return this.mName2Annotation.get(named);
    }
    
    @Override
    public MAnnotation findOrCreateAnnotation(final String annotationName) {
        MAnnotation ann = this.getMutableAnnotation(annotationName);
        if (ann != null) {
            return ann;
        }
        final AnnotationProxy proxy = this.getContext().createAnnotationProxy(annotationName);
        ann = new AnnotationImpl(this.getContext(), proxy, annotationName);
        if (this.mName2Annotation == null) {
            this.mName2Annotation = new HashMap();
        }
        this.mName2Annotation.put(ann.getQualifiedName(), ann);
        return ann;
    }
    
    @Override
    public MAnnotation addLiteralAnnotation(String annName) {
        if (annName == null) {
            throw new IllegalArgumentException("null tagname");
        }
        annName = annName.trim();
        final AnnotationProxy proxy = this.getContext().createAnnotationProxy(annName);
        final MAnnotation ann = new AnnotationImpl(this.getContext(), proxy, annName);
        if (this.mAllAnnotations == null) {
            this.mAllAnnotations = new ArrayList();
        }
        this.mAllAnnotations.add(ann);
        if (this.getMutableAnnotation(annName) == null) {
            if (this.mName2Annotation == null) {
                this.mName2Annotation = new HashMap();
            }
            this.mName2Annotation.put(annName, ann);
        }
        return ann;
    }
    
    @Override
    public MComment getMutableComment() {
        return this.mComment;
    }
    
    @Override
    public MComment createComment() {
        return this.mComment = new CommentImpl(this);
    }
    
    @Override
    public void removeComment() {
        this.mComment = null;
    }
    
    protected void addAnnotation(final JAnnotation ann) {
        if (this.mName2Annotation == null) {
            (this.mName2Annotation = new HashMap()).put(ann.getQualifiedName(), ann);
        }
        else if (this.mName2Annotation.get(ann.getQualifiedName()) == null) {
            this.mName2Annotation.put(ann.getQualifiedName(), ann);
        }
        if (this.mAllAnnotations == null) {
            this.mAllAnnotations = new ArrayList();
        }
        this.mAllAnnotations.add(ann);
    }
    
    @Deprecated
    public MAnnotation addAnnotationForProxy(final Class proxyClass, final AnnotationProxy proxy) {
        final String annotationName = proxyClass.getName();
        MAnnotation ann = this.getMutableAnnotation(annotationName);
        if (ann != null) {
            return ann;
        }
        ann = new AnnotationImpl(this.getContext(), proxy, annotationName);
        if (this.mName2Annotation == null) {
            this.mName2Annotation = new HashMap();
        }
        this.mName2Annotation.put(ann.getQualifiedName(), ann);
        return ann;
    }
}
