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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.SchemaType;

public interface Facet extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Facet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("facet446etype");
    
    XmlAnySimpleType getValue();
    
    void setValue(final XmlAnySimpleType p0);
    
    XmlAnySimpleType addNewValue();
    
    boolean getFixed();
    
    XmlBoolean xgetFixed();
    
    boolean isSetFixed();
    
    void setFixed(final boolean p0);
    
    void xsetFixed(final XmlBoolean p0);
    
    void unsetFixed();
    
    public static final class Factory
    {
        public static Facet newInstance() {
            return (Facet)XmlBeans.getContextTypeLoader().newInstance(Facet.type, null);
        }
        
        public static Facet newInstance(final XmlOptions options) {
            return (Facet)XmlBeans.getContextTypeLoader().newInstance(Facet.type, options);
        }
        
        public static Facet parse(final String xmlAsString) throws XmlException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(xmlAsString, Facet.type, null);
        }
        
        public static Facet parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(xmlAsString, Facet.type, options);
        }
        
        public static Facet parse(final File file) throws XmlException, IOException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(file, Facet.type, null);
        }
        
        public static Facet parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(file, Facet.type, options);
        }
        
        public static Facet parse(final URL u) throws XmlException, IOException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(u, Facet.type, null);
        }
        
        public static Facet parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(u, Facet.type, options);
        }
        
        public static Facet parse(final InputStream is) throws XmlException, IOException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(is, Facet.type, null);
        }
        
        public static Facet parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(is, Facet.type, options);
        }
        
        public static Facet parse(final Reader r) throws XmlException, IOException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(r, Facet.type, null);
        }
        
        public static Facet parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(r, Facet.type, options);
        }
        
        public static Facet parse(final XMLStreamReader sr) throws XmlException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(sr, Facet.type, null);
        }
        
        public static Facet parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(sr, Facet.type, options);
        }
        
        public static Facet parse(final Node node) throws XmlException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(node, Facet.type, null);
        }
        
        public static Facet parse(final Node node, final XmlOptions options) throws XmlException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(node, Facet.type, options);
        }
        
        @Deprecated
        public static Facet parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(xis, Facet.type, null);
        }
        
        @Deprecated
        public static Facet parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Facet)XmlBeans.getContextTypeLoader().parse(xis, Facet.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Facet.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Facet.type, options);
        }
        
        private Factory() {
        }
    }
}
