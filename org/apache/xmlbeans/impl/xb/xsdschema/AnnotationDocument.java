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
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface AnnotationDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AnnotationDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("annotationb034doctype");
    
    Annotation getAnnotation();
    
    void setAnnotation(final Annotation p0);
    
    Annotation addNewAnnotation();
    
    public interface Annotation extends OpenAttrs
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Annotation.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("annotation5abfelemtype");
        
        AppinfoDocument.Appinfo[] getAppinfoArray();
        
        AppinfoDocument.Appinfo getAppinfoArray(final int p0);
        
        int sizeOfAppinfoArray();
        
        void setAppinfoArray(final AppinfoDocument.Appinfo[] p0);
        
        void setAppinfoArray(final int p0, final AppinfoDocument.Appinfo p1);
        
        AppinfoDocument.Appinfo insertNewAppinfo(final int p0);
        
        AppinfoDocument.Appinfo addNewAppinfo();
        
        void removeAppinfo(final int p0);
        
        DocumentationDocument.Documentation[] getDocumentationArray();
        
        DocumentationDocument.Documentation getDocumentationArray(final int p0);
        
        int sizeOfDocumentationArray();
        
        void setDocumentationArray(final DocumentationDocument.Documentation[] p0);
        
        void setDocumentationArray(final int p0, final DocumentationDocument.Documentation p1);
        
        DocumentationDocument.Documentation insertNewDocumentation(final int p0);
        
        DocumentationDocument.Documentation addNewDocumentation();
        
        void removeDocumentation(final int p0);
        
        String getId();
        
        XmlID xgetId();
        
        boolean isSetId();
        
        void setId(final String p0);
        
        void xsetId(final XmlID p0);
        
        void unsetId();
        
        public static final class Factory
        {
            public static Annotation newInstance() {
                return (Annotation)XmlBeans.getContextTypeLoader().newInstance(Annotation.type, null);
            }
            
            public static Annotation newInstance(final XmlOptions options) {
                return (Annotation)XmlBeans.getContextTypeLoader().newInstance(Annotation.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static AnnotationDocument newInstance() {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().newInstance(AnnotationDocument.type, null);
        }
        
        public static AnnotationDocument newInstance(final XmlOptions options) {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().newInstance(AnnotationDocument.type, options);
        }
        
        public static AnnotationDocument parse(final String xmlAsString) throws XmlException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AnnotationDocument.type, null);
        }
        
        public static AnnotationDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AnnotationDocument.type, options);
        }
        
        public static AnnotationDocument parse(final File file) throws XmlException, IOException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(file, AnnotationDocument.type, null);
        }
        
        public static AnnotationDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(file, AnnotationDocument.type, options);
        }
        
        public static AnnotationDocument parse(final URL u) throws XmlException, IOException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(u, AnnotationDocument.type, null);
        }
        
        public static AnnotationDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(u, AnnotationDocument.type, options);
        }
        
        public static AnnotationDocument parse(final InputStream is) throws XmlException, IOException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(is, AnnotationDocument.type, null);
        }
        
        public static AnnotationDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(is, AnnotationDocument.type, options);
        }
        
        public static AnnotationDocument parse(final Reader r) throws XmlException, IOException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(r, AnnotationDocument.type, null);
        }
        
        public static AnnotationDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(r, AnnotationDocument.type, options);
        }
        
        public static AnnotationDocument parse(final XMLStreamReader sr) throws XmlException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(sr, AnnotationDocument.type, null);
        }
        
        public static AnnotationDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(sr, AnnotationDocument.type, options);
        }
        
        public static AnnotationDocument parse(final Node node) throws XmlException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(node, AnnotationDocument.type, null);
        }
        
        public static AnnotationDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(node, AnnotationDocument.type, options);
        }
        
        @Deprecated
        public static AnnotationDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(xis, AnnotationDocument.type, null);
        }
        
        @Deprecated
        public static AnnotationDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AnnotationDocument)XmlBeans.getContextTypeLoader().parse(xis, AnnotationDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AnnotationDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AnnotationDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
