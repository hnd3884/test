package com.fasterxml.jackson.dataformat.xml.util;

import java.util.Iterator;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.dataformat.xml.XmlAnnotationIntrospector;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.JavaType;
import javax.xml.namespace.QName;
import com.fasterxml.jackson.databind.type.ClassKey;
import com.fasterxml.jackson.databind.util.LRUMap;
import java.io.Serializable;

public class XmlRootNameLookup implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final transient LRUMap<ClassKey, QName> _rootNames;
    
    public XmlRootNameLookup() {
        this._rootNames = (LRUMap<ClassKey, QName>)new LRUMap(40, 200);
    }
    
    protected Object readResolve() {
        if (this._rootNames == null) {
            return new XmlRootNameLookup();
        }
        return this;
    }
    
    public QName findRootName(final JavaType rootType, final MapperConfig<?> config) {
        return this.findRootName(rootType.getRawClass(), config);
    }
    
    public QName findRootName(final Class<?> rootType, final MapperConfig<?> config) {
        final ClassKey key = new ClassKey((Class)rootType);
        QName name;
        synchronized (this._rootNames) {
            name = (QName)this._rootNames.get((Object)key);
        }
        if (name != null) {
            return name;
        }
        name = this._findRootName(rootType, config);
        synchronized (this._rootNames) {
            this._rootNames.put((Object)key, (Object)name);
        }
        return name;
    }
    
    protected QName _findRootName(final Class<?> rootType, final MapperConfig<?> config) {
        final BeanDescription beanDesc = config.introspectClassAnnotations((Class)rootType);
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final AnnotatedClass ac = beanDesc.getClassInfo();
        String localName = null;
        String ns = null;
        final PropertyName root = intr.findRootName(ac);
        if (root != null) {
            localName = root.getSimpleName();
            ns = root.getNamespace();
        }
        if (localName == null || localName.length() == 0) {
            localName = StaxUtil.sanitizeXmlTypeName(rootType.getSimpleName());
            return new QName("", localName);
        }
        if (ns == null || ns.length() == 0) {
            ns = this.findNamespace(intr, ac);
        }
        if (ns == null) {
            ns = "";
        }
        return new QName(ns, localName);
    }
    
    private String findNamespace(final AnnotationIntrospector ai, final AnnotatedClass ann) {
        for (final AnnotationIntrospector intr : ai.allIntrospectors()) {
            if (intr instanceof XmlAnnotationIntrospector) {
                final String ns = ((XmlAnnotationIntrospector)intr).findNamespace((Annotated)ann);
                if (ns != null) {
                    return ns;
                }
                continue;
            }
        }
        return null;
    }
}
