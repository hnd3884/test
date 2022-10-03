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
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SchemaType;

public interface Annotated extends OpenAttrs
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Annotated.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("annotateda52dtype");
    
    AnnotationDocument.Annotation getAnnotation();
    
    boolean isSetAnnotation();
    
    void setAnnotation(final AnnotationDocument.Annotation p0);
    
    AnnotationDocument.Annotation addNewAnnotation();
    
    void unsetAnnotation();
    
    String getId();
    
    XmlID xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlID p0);
    
    void unsetId();
    
    public static final class Factory
    {
        public static Annotated newInstance() {
            return (Annotated)XmlBeans.getContextTypeLoader().newInstance(Annotated.type, null);
        }
        
        public static Annotated newInstance(final XmlOptions options) {
            return (Annotated)XmlBeans.getContextTypeLoader().newInstance(Annotated.type, options);
        }
        
        public static Annotated parse(final String xmlAsString) throws XmlException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(xmlAsString, Annotated.type, null);
        }
        
        public static Annotated parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(xmlAsString, Annotated.type, options);
        }
        
        public static Annotated parse(final File file) throws XmlException, IOException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(file, Annotated.type, null);
        }
        
        public static Annotated parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(file, Annotated.type, options);
        }
        
        public static Annotated parse(final URL u) throws XmlException, IOException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(u, Annotated.type, null);
        }
        
        public static Annotated parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(u, Annotated.type, options);
        }
        
        public static Annotated parse(final InputStream is) throws XmlException, IOException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(is, Annotated.type, null);
        }
        
        public static Annotated parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(is, Annotated.type, options);
        }
        
        public static Annotated parse(final Reader r) throws XmlException, IOException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(r, Annotated.type, null);
        }
        
        public static Annotated parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(r, Annotated.type, options);
        }
        
        public static Annotated parse(final XMLStreamReader sr) throws XmlException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(sr, Annotated.type, null);
        }
        
        public static Annotated parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(sr, Annotated.type, options);
        }
        
        public static Annotated parse(final Node node) throws XmlException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(node, Annotated.type, null);
        }
        
        public static Annotated parse(final Node node, final XmlOptions options) throws XmlException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(node, Annotated.type, options);
        }
        
        @Deprecated
        public static Annotated parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(xis, Annotated.type, null);
        }
        
        @Deprecated
        public static Annotated parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Annotated)XmlBeans.getContextTypeLoader().parse(xis, Annotated.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Annotated.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Annotated.type, options);
        }
        
        private Factory() {
        }
    }
}
