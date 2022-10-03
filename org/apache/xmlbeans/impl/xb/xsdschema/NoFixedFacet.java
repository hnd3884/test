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

public interface NoFixedFacet extends Facet
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NoFixedFacet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("nofixedfacet250ftype");
    
    public static final class Factory
    {
        public static NoFixedFacet newInstance() {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().newInstance(NoFixedFacet.type, null);
        }
        
        public static NoFixedFacet newInstance(final XmlOptions options) {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().newInstance(NoFixedFacet.type, options);
        }
        
        public static NoFixedFacet parse(final String xmlAsString) throws XmlException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(xmlAsString, NoFixedFacet.type, null);
        }
        
        public static NoFixedFacet parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(xmlAsString, NoFixedFacet.type, options);
        }
        
        public static NoFixedFacet parse(final File file) throws XmlException, IOException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(file, NoFixedFacet.type, null);
        }
        
        public static NoFixedFacet parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(file, NoFixedFacet.type, options);
        }
        
        public static NoFixedFacet parse(final URL u) throws XmlException, IOException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(u, NoFixedFacet.type, null);
        }
        
        public static NoFixedFacet parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(u, NoFixedFacet.type, options);
        }
        
        public static NoFixedFacet parse(final InputStream is) throws XmlException, IOException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(is, NoFixedFacet.type, null);
        }
        
        public static NoFixedFacet parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(is, NoFixedFacet.type, options);
        }
        
        public static NoFixedFacet parse(final Reader r) throws XmlException, IOException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(r, NoFixedFacet.type, null);
        }
        
        public static NoFixedFacet parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(r, NoFixedFacet.type, options);
        }
        
        public static NoFixedFacet parse(final XMLStreamReader sr) throws XmlException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(sr, NoFixedFacet.type, null);
        }
        
        public static NoFixedFacet parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(sr, NoFixedFacet.type, options);
        }
        
        public static NoFixedFacet parse(final Node node) throws XmlException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(node, NoFixedFacet.type, null);
        }
        
        public static NoFixedFacet parse(final Node node, final XmlOptions options) throws XmlException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(node, NoFixedFacet.type, options);
        }
        
        @Deprecated
        public static NoFixedFacet parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(xis, NoFixedFacet.type, null);
        }
        
        @Deprecated
        public static NoFixedFacet parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (NoFixedFacet)XmlBeans.getContextTypeLoader().parse(xis, NoFixedFacet.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NoFixedFacet.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NoFixedFacet.type, options);
        }
        
        private Factory() {
        }
    }
}
