package org.apache.xmlbeans.impl.xb.xmlschema;

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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface BaseAttribute extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(BaseAttribute.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLLANG").resolveHandle("basece23attrtypetype");
    
    String getBase();
    
    XmlAnyURI xgetBase();
    
    boolean isSetBase();
    
    void setBase(final String p0);
    
    void xsetBase(final XmlAnyURI p0);
    
    void unsetBase();
    
    public static final class Factory
    {
        public static BaseAttribute newInstance() {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().newInstance(BaseAttribute.type, null);
        }
        
        public static BaseAttribute newInstance(final XmlOptions options) {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().newInstance(BaseAttribute.type, options);
        }
        
        public static BaseAttribute parse(final String xmlAsString) throws XmlException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, BaseAttribute.type, null);
        }
        
        public static BaseAttribute parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(xmlAsString, BaseAttribute.type, options);
        }
        
        public static BaseAttribute parse(final File file) throws XmlException, IOException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(file, BaseAttribute.type, null);
        }
        
        public static BaseAttribute parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(file, BaseAttribute.type, options);
        }
        
        public static BaseAttribute parse(final URL u) throws XmlException, IOException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(u, BaseAttribute.type, null);
        }
        
        public static BaseAttribute parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(u, BaseAttribute.type, options);
        }
        
        public static BaseAttribute parse(final InputStream is) throws XmlException, IOException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(is, BaseAttribute.type, null);
        }
        
        public static BaseAttribute parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(is, BaseAttribute.type, options);
        }
        
        public static BaseAttribute parse(final Reader r) throws XmlException, IOException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(r, BaseAttribute.type, null);
        }
        
        public static BaseAttribute parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(r, BaseAttribute.type, options);
        }
        
        public static BaseAttribute parse(final XMLStreamReader sr) throws XmlException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(sr, BaseAttribute.type, null);
        }
        
        public static BaseAttribute parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(sr, BaseAttribute.type, options);
        }
        
        public static BaseAttribute parse(final Node node) throws XmlException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(node, BaseAttribute.type, null);
        }
        
        public static BaseAttribute parse(final Node node, final XmlOptions options) throws XmlException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(node, BaseAttribute.type, options);
        }
        
        @Deprecated
        public static BaseAttribute parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(xis, BaseAttribute.type, null);
        }
        
        @Deprecated
        public static BaseAttribute parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (BaseAttribute)XmlBeans.getContextTypeLoader().parse(xis, BaseAttribute.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, BaseAttribute.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, BaseAttribute.type, options);
        }
        
        private Factory() {
        }
    }
}
