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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface AllDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AllDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("all7214doctype");
    
    All getAll();
    
    void setAll(final All p0);
    
    All addNewAll();
    
    public static final class Factory
    {
        public static AllDocument newInstance() {
            return (AllDocument)XmlBeans.getContextTypeLoader().newInstance(AllDocument.type, null);
        }
        
        public static AllDocument newInstance(final XmlOptions options) {
            return (AllDocument)XmlBeans.getContextTypeLoader().newInstance(AllDocument.type, options);
        }
        
        public static AllDocument parse(final String xmlAsString) throws XmlException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AllDocument.type, null);
        }
        
        public static AllDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, AllDocument.type, options);
        }
        
        public static AllDocument parse(final File file) throws XmlException, IOException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(file, AllDocument.type, null);
        }
        
        public static AllDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(file, AllDocument.type, options);
        }
        
        public static AllDocument parse(final URL u) throws XmlException, IOException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(u, AllDocument.type, null);
        }
        
        public static AllDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(u, AllDocument.type, options);
        }
        
        public static AllDocument parse(final InputStream is) throws XmlException, IOException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(is, AllDocument.type, null);
        }
        
        public static AllDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(is, AllDocument.type, options);
        }
        
        public static AllDocument parse(final Reader r) throws XmlException, IOException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(r, AllDocument.type, null);
        }
        
        public static AllDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(r, AllDocument.type, options);
        }
        
        public static AllDocument parse(final XMLStreamReader sr) throws XmlException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(sr, AllDocument.type, null);
        }
        
        public static AllDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(sr, AllDocument.type, options);
        }
        
        public static AllDocument parse(final Node node) throws XmlException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(node, AllDocument.type, null);
        }
        
        public static AllDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(node, AllDocument.type, options);
        }
        
        @Deprecated
        public static AllDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(xis, AllDocument.type, null);
        }
        
        @Deprecated
        public static AllDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AllDocument)XmlBeans.getContextTypeLoader().parse(xis, AllDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AllDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AllDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
