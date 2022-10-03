package com.fasterxml.jackson.dataformat.xml;

import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.Annotated;

public interface XmlAnnotationIntrospector
{
    String findNamespace(final Annotated p0);
    
    Boolean isOutputAsAttribute(final Annotated p0);
    
    Boolean isOutputAsText(final Annotated p0);
    
    Boolean isOutputAsCData(final Annotated p0);
    
    void setDefaultUseWrapper(final boolean p0);
    
    public static class Pair extends AnnotationIntrospectorPair implements XmlAnnotationIntrospector
    {
        private static final long serialVersionUID = 1L;
        protected final XmlAnnotationIntrospector _xmlPrimary;
        protected final XmlAnnotationIntrospector _xmlSecondary;
        
        public Pair(final AnnotationIntrospector p, final AnnotationIntrospector s) {
            super(p, s);
            if (p instanceof XmlAnnotationIntrospector) {
                this._xmlPrimary = (XmlAnnotationIntrospector)p;
            }
            else if (p instanceof JaxbAnnotationIntrospector) {
                this._xmlPrimary = new JaxbWrapper((JaxbAnnotationIntrospector)p);
            }
            else {
                this._xmlPrimary = null;
            }
            if (s instanceof XmlAnnotationIntrospector) {
                this._xmlSecondary = (XmlAnnotationIntrospector)s;
            }
            else if (s instanceof JaxbAnnotationIntrospector) {
                this._xmlSecondary = new JaxbWrapper((JaxbAnnotationIntrospector)s);
            }
            else {
                this._xmlSecondary = null;
            }
        }
        
        public static Pair instance(final AnnotationIntrospector a1, final AnnotationIntrospector a2) {
            return new Pair(a1, a2);
        }
        
        public String findNamespace(final Annotated ann) {
            String value = (this._xmlPrimary == null) ? null : this._xmlPrimary.findNamespace(ann);
            if (value == null && this._xmlSecondary != null) {
                value = this._xmlSecondary.findNamespace(ann);
            }
            return value;
        }
        
        public Boolean isOutputAsAttribute(final Annotated ann) {
            Boolean value = (this._xmlPrimary == null) ? null : this._xmlPrimary.isOutputAsAttribute(ann);
            if (value == null && this._xmlSecondary != null) {
                value = this._xmlSecondary.isOutputAsAttribute(ann);
            }
            return value;
        }
        
        public Boolean isOutputAsText(final Annotated ann) {
            Boolean value = (this._xmlPrimary == null) ? null : this._xmlPrimary.isOutputAsText(ann);
            if (value == null && this._xmlSecondary != null) {
                value = this._xmlSecondary.isOutputAsText(ann);
            }
            return value;
        }
        
        public Boolean isOutputAsCData(final Annotated ann) {
            Boolean value = (this._xmlPrimary == null) ? null : this._xmlPrimary.isOutputAsCData(ann);
            if (value == null && this._xmlSecondary != null) {
                value = this._xmlSecondary.isOutputAsCData(ann);
            }
            return value;
        }
        
        public void setDefaultUseWrapper(final boolean b) {
            if (this._xmlPrimary != null) {
                this._xmlPrimary.setDefaultUseWrapper(b);
            }
            if (this._xmlSecondary != null) {
                this._xmlSecondary.setDefaultUseWrapper(b);
            }
        }
    }
    
    public static class JaxbWrapper implements XmlAnnotationIntrospector
    {
        protected final JaxbAnnotationIntrospector _intr;
        
        public JaxbWrapper(final JaxbAnnotationIntrospector i) {
            this._intr = i;
        }
        
        @Override
        public String findNamespace(final Annotated ann) {
            return this._intr.findNamespace(ann);
        }
        
        @Override
        public Boolean isOutputAsAttribute(final Annotated ann) {
            return this._intr.isOutputAsAttribute(ann);
        }
        
        @Override
        public Boolean isOutputAsText(final Annotated ann) {
            return this._intr.isOutputAsText(ann);
        }
        
        @Override
        public Boolean isOutputAsCData(final Annotated ann) {
            return null;
        }
        
        @Override
        public void setDefaultUseWrapper(final boolean b) {
        }
    }
}
