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
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface TopLevelElement extends Element
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(TopLevelElement.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("toplevelelement98d8type");
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    public static final class Factory
    {
        public static TopLevelElement newInstance() {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().newInstance(TopLevelElement.type, null);
        }
        
        public static TopLevelElement newInstance(final XmlOptions options) {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().newInstance(TopLevelElement.type, options);
        }
        
        public static TopLevelElement parse(final String xmlAsString) throws XmlException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(xmlAsString, TopLevelElement.type, null);
        }
        
        public static TopLevelElement parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(xmlAsString, TopLevelElement.type, options);
        }
        
        public static TopLevelElement parse(final File file) throws XmlException, IOException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(file, TopLevelElement.type, null);
        }
        
        public static TopLevelElement parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(file, TopLevelElement.type, options);
        }
        
        public static TopLevelElement parse(final URL u) throws XmlException, IOException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(u, TopLevelElement.type, null);
        }
        
        public static TopLevelElement parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(u, TopLevelElement.type, options);
        }
        
        public static TopLevelElement parse(final InputStream is) throws XmlException, IOException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(is, TopLevelElement.type, null);
        }
        
        public static TopLevelElement parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(is, TopLevelElement.type, options);
        }
        
        public static TopLevelElement parse(final Reader r) throws XmlException, IOException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(r, TopLevelElement.type, null);
        }
        
        public static TopLevelElement parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(r, TopLevelElement.type, options);
        }
        
        public static TopLevelElement parse(final XMLStreamReader sr) throws XmlException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(sr, TopLevelElement.type, null);
        }
        
        public static TopLevelElement parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(sr, TopLevelElement.type, options);
        }
        
        public static TopLevelElement parse(final Node node) throws XmlException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(node, TopLevelElement.type, null);
        }
        
        public static TopLevelElement parse(final Node node, final XmlOptions options) throws XmlException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(node, TopLevelElement.type, options);
        }
        
        @Deprecated
        public static TopLevelElement parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(xis, TopLevelElement.type, null);
        }
        
        @Deprecated
        public static TopLevelElement parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (TopLevelElement)XmlBeans.getContextTypeLoader().parse(xis, TopLevelElement.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TopLevelElement.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, TopLevelElement.type, options);
        }
        
        private Factory() {
        }
    }
}
