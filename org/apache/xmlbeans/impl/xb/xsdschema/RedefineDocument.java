package org.apache.xmlbeans.impl.xb.xsdschema;

import org.apache.xmlbeans.xml.stream.XMLStreamException;
import org.apache.xmlbeans.xml.stream.XMLInputStream;
import org.w3c.dom.Node;
import javax.xml.stream.XMLStreamReader;
import java.io.Reader;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface RedefineDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(RedefineDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("redefine3f55doctype");
    
    Redefine getRedefine();
    
    void setRedefine(final Redefine p0);
    
    Redefine addNewRedefine();
    
    public interface Redefine extends OpenAttrs
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Redefine.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("redefine9e9felemtype");
        
        AnnotationDocument.Annotation[] getAnnotationArray();
        
        AnnotationDocument.Annotation getAnnotationArray(final int p0);
        
        int sizeOfAnnotationArray();
        
        void setAnnotationArray(final AnnotationDocument.Annotation[] p0);
        
        void setAnnotationArray(final int p0, final AnnotationDocument.Annotation p1);
        
        AnnotationDocument.Annotation insertNewAnnotation(final int p0);
        
        AnnotationDocument.Annotation addNewAnnotation();
        
        void removeAnnotation(final int p0);
        
        TopLevelSimpleType[] getSimpleTypeArray();
        
        TopLevelSimpleType getSimpleTypeArray(final int p0);
        
        int sizeOfSimpleTypeArray();
        
        void setSimpleTypeArray(final TopLevelSimpleType[] p0);
        
        void setSimpleTypeArray(final int p0, final TopLevelSimpleType p1);
        
        TopLevelSimpleType insertNewSimpleType(final int p0);
        
        TopLevelSimpleType addNewSimpleType();
        
        void removeSimpleType(final int p0);
        
        TopLevelComplexType[] getComplexTypeArray();
        
        TopLevelComplexType getComplexTypeArray(final int p0);
        
        int sizeOfComplexTypeArray();
        
        void setComplexTypeArray(final TopLevelComplexType[] p0);
        
        void setComplexTypeArray(final int p0, final TopLevelComplexType p1);
        
        TopLevelComplexType insertNewComplexType(final int p0);
        
        TopLevelComplexType addNewComplexType();
        
        void removeComplexType(final int p0);
        
        NamedGroup[] getGroupArray();
        
        NamedGroup getGroupArray(final int p0);
        
        int sizeOfGroupArray();
        
        void setGroupArray(final NamedGroup[] p0);
        
        void setGroupArray(final int p0, final NamedGroup p1);
        
        NamedGroup insertNewGroup(final int p0);
        
        NamedGroup addNewGroup();
        
        void removeGroup(final int p0);
        
        NamedAttributeGroup[] getAttributeGroupArray();
        
        NamedAttributeGroup getAttributeGroupArray(final int p0);
        
        int sizeOfAttributeGroupArray();
        
        void setAttributeGroupArray(final NamedAttributeGroup[] p0);
        
        void setAttributeGroupArray(final int p0, final NamedAttributeGroup p1);
        
        NamedAttributeGroup insertNewAttributeGroup(final int p0);
        
        NamedAttributeGroup addNewAttributeGroup();
        
        void removeAttributeGroup(final int p0);
        
        String getSchemaLocation();
        
        XmlAnyURI xgetSchemaLocation();
        
        void setSchemaLocation(final String p0);
        
        void xsetSchemaLocation(final XmlAnyURI p0);
        
        String getId();
        
        XmlID xgetId();
        
        boolean isSetId();
        
        void setId(final String p0);
        
        void xsetId(final XmlID p0);
        
        void unsetId();
        
        public static final class Factory
        {
            public static Redefine newInstance() {
                return (Redefine)XmlBeans.getContextTypeLoader().newInstance(Redefine.type, null);
            }
            
            public static Redefine newInstance(final XmlOptions options) {
                return (Redefine)XmlBeans.getContextTypeLoader().newInstance(Redefine.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static RedefineDocument newInstance() {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().newInstance(RedefineDocument.type, null);
        }
        
        public static RedefineDocument newInstance(final XmlOptions options) {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().newInstance(RedefineDocument.type, options);
        }
        
        public static RedefineDocument parse(final String xmlAsString) throws XmlException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, RedefineDocument.type, null);
        }
        
        public static RedefineDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, RedefineDocument.type, options);
        }
        
        public static RedefineDocument parse(final File file) throws XmlException, IOException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(file, RedefineDocument.type, null);
        }
        
        public static RedefineDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(file, RedefineDocument.type, options);
        }
        
        public static RedefineDocument parse(final URL u) throws XmlException, IOException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(u, RedefineDocument.type, null);
        }
        
        public static RedefineDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(u, RedefineDocument.type, options);
        }
        
        public static RedefineDocument parse(final InputStream is) throws XmlException, IOException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(is, RedefineDocument.type, null);
        }
        
        public static RedefineDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(is, RedefineDocument.type, options);
        }
        
        public static RedefineDocument parse(final Reader r) throws XmlException, IOException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(r, RedefineDocument.type, null);
        }
        
        public static RedefineDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(r, RedefineDocument.type, options);
        }
        
        public static RedefineDocument parse(final XMLStreamReader sr) throws XmlException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(sr, RedefineDocument.type, null);
        }
        
        public static RedefineDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(sr, RedefineDocument.type, options);
        }
        
        public static RedefineDocument parse(final Node node) throws XmlException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(node, RedefineDocument.type, null);
        }
        
        public static RedefineDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(node, RedefineDocument.type, options);
        }
        
        @Deprecated
        public static RedefineDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(xis, RedefineDocument.type, null);
        }
        
        @Deprecated
        public static RedefineDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (RedefineDocument)XmlBeans.getContextTypeLoader().parse(xis, RedefineDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, RedefineDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, RedefineDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
