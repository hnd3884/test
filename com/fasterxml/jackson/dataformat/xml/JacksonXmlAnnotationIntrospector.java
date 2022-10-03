package com.fasterxml.jackson.dataformat.xml;

import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.Annotated;
import java.lang.annotation.Annotation;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class JacksonXmlAnnotationIntrospector extends JacksonAnnotationIntrospector implements XmlAnnotationIntrospector
{
    private static final long serialVersionUID = 1L;
    private static final Class<? extends Annotation>[] ANNOTATIONS_TO_INFER_XML_PROP;
    public static final boolean DEFAULT_USE_WRAPPER = true;
    protected boolean _cfgDefaultUseWrapper;
    
    public JacksonXmlAnnotationIntrospector() {
        this(true);
    }
    
    public JacksonXmlAnnotationIntrospector(final boolean defaultUseWrapper) {
        this._cfgDefaultUseWrapper = defaultUseWrapper;
    }
    
    public PropertyName findWrapperName(final Annotated ann) {
        final JacksonXmlElementWrapper w = (JacksonXmlElementWrapper)ann.getAnnotation((Class)JacksonXmlElementWrapper.class);
        if (w != null) {
            if (!w.useWrapping()) {
                return PropertyName.NO_NAME;
            }
            final String localName = w.localName();
            if (localName == null || localName.length() == 0) {
                return PropertyName.USE_DEFAULT;
            }
            return PropertyName.construct(w.localName(), w.namespace());
        }
        else {
            if (this._cfgDefaultUseWrapper) {
                return PropertyName.USE_DEFAULT;
            }
            return null;
        }
    }
    
    public PropertyName findRootName(final AnnotatedClass ac) {
        final JacksonXmlRootElement root = (JacksonXmlRootElement)ac.getAnnotation((Class)JacksonXmlRootElement.class);
        if (root == null) {
            return super.findRootName(ac);
        }
        final String local = root.localName();
        final String ns = root.namespace();
        if (local.length() == 0 && ns.length() == 0) {
            return PropertyName.USE_DEFAULT;
        }
        return new PropertyName(local, ns);
    }
    
    public String findNamespace(final Annotated ann) {
        final JacksonXmlProperty prop = (JacksonXmlProperty)ann.getAnnotation((Class)JacksonXmlProperty.class);
        if (prop != null) {
            return prop.namespace();
        }
        return null;
    }
    
    public Boolean isOutputAsAttribute(final Annotated ann) {
        final JacksonXmlProperty prop = (JacksonXmlProperty)ann.getAnnotation((Class)JacksonXmlProperty.class);
        if (prop != null) {
            return prop.isAttribute() ? Boolean.TRUE : Boolean.FALSE;
        }
        return null;
    }
    
    public Boolean isOutputAsText(final Annotated ann) {
        final JacksonXmlText prop = (JacksonXmlText)ann.getAnnotation((Class)JacksonXmlText.class);
        if (prop != null) {
            return prop.value() ? Boolean.TRUE : Boolean.FALSE;
        }
        return null;
    }
    
    public Boolean isOutputAsCData(final Annotated ann) {
        final JacksonXmlCData prop = (JacksonXmlCData)ann.getAnnotation((Class)JacksonXmlCData.class);
        if (prop != null) {
            return prop.value() ? Boolean.TRUE : Boolean.FALSE;
        }
        return null;
    }
    
    public void setDefaultUseWrapper(final boolean b) {
        this._cfgDefaultUseWrapper = b;
    }
    
    public PropertyName findNameForSerialization(final Annotated a) {
        PropertyName name = this._findXmlName(a);
        if (name == null) {
            name = super.findNameForSerialization(a);
            if (name == null && this._hasOneOf(a, (Class[])JacksonXmlAnnotationIntrospector.ANNOTATIONS_TO_INFER_XML_PROP)) {
                return PropertyName.USE_DEFAULT;
            }
        }
        return name;
    }
    
    public PropertyName findNameForDeserialization(final Annotated a) {
        PropertyName name = this._findXmlName(a);
        if (name == null) {
            name = super.findNameForDeserialization(a);
            if (name == null && this._hasOneOf(a, (Class[])JacksonXmlAnnotationIntrospector.ANNOTATIONS_TO_INFER_XML_PROP)) {
                return PropertyName.USE_DEFAULT;
            }
        }
        return name;
    }
    
    protected StdTypeResolverBuilder _constructStdTypeResolverBuilder() {
        return new XmlTypeResolverBuilder();
    }
    
    protected PropertyName _findXmlName(final Annotated a) {
        final JacksonXmlProperty pann = (JacksonXmlProperty)a.getAnnotation((Class)JacksonXmlProperty.class);
        if (pann != null) {
            return PropertyName.construct(pann.localName(), pann.namespace());
        }
        return null;
    }
    
    static {
        ANNOTATIONS_TO_INFER_XML_PROP = new Class[] { JacksonXmlText.class, JacksonXmlElementWrapper.class };
    }
}
