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

public interface NumFacet extends Facet
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NumFacet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("numfacet93a2type");
    
    public static final class Factory
    {
        public static NumFacet newInstance() {
            return (NumFacet)XmlBeans.getContextTypeLoader().newInstance(NumFacet.type, null);
        }
        
        public static NumFacet newInstance(final XmlOptions options) {
            return (NumFacet)XmlBeans.getContextTypeLoader().newInstance(NumFacet.type, options);
        }
        
        public static NumFacet parse(final String xmlAsString) throws XmlException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(xmlAsString, NumFacet.type, null);
        }
        
        public static NumFacet parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(xmlAsString, NumFacet.type, options);
        }
        
        public static NumFacet parse(final File file) throws XmlException, IOException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(file, NumFacet.type, null);
        }
        
        public static NumFacet parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(file, NumFacet.type, options);
        }
        
        public static NumFacet parse(final URL u) throws XmlException, IOException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(u, NumFacet.type, null);
        }
        
        public static NumFacet parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(u, NumFacet.type, options);
        }
        
        public static NumFacet parse(final InputStream is) throws XmlException, IOException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(is, NumFacet.type, null);
        }
        
        public static NumFacet parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(is, NumFacet.type, options);
        }
        
        public static NumFacet parse(final Reader r) throws XmlException, IOException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(r, NumFacet.type, null);
        }
        
        public static NumFacet parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(r, NumFacet.type, options);
        }
        
        public static NumFacet parse(final XMLStreamReader sr) throws XmlException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(sr, NumFacet.type, null);
        }
        
        public static NumFacet parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(sr, NumFacet.type, options);
        }
        
        public static NumFacet parse(final Node node) throws XmlException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(node, NumFacet.type, null);
        }
        
        public static NumFacet parse(final Node node, final XmlOptions options) throws XmlException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(node, NumFacet.type, options);
        }
        
        @Deprecated
        public static NumFacet parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(xis, NumFacet.type, null);
        }
        
        @Deprecated
        public static NumFacet parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (NumFacet)XmlBeans.getContextTypeLoader().parse(xis, NumFacet.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NumFacet.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NumFacet.type, options);
        }
        
        private Factory() {
        }
    }
}
