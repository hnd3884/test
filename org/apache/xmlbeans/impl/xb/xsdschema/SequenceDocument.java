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

public interface SequenceDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SequenceDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("sequencecba2doctype");
    
    ExplicitGroup getSequence();
    
    void setSequence(final ExplicitGroup p0);
    
    ExplicitGroup addNewSequence();
    
    public static final class Factory
    {
        public static SequenceDocument newInstance() {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().newInstance(SequenceDocument.type, null);
        }
        
        public static SequenceDocument newInstance(final XmlOptions options) {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().newInstance(SequenceDocument.type, options);
        }
        
        public static SequenceDocument parse(final String xmlAsString) throws XmlException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SequenceDocument.type, null);
        }
        
        public static SequenceDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SequenceDocument.type, options);
        }
        
        public static SequenceDocument parse(final File file) throws XmlException, IOException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(file, SequenceDocument.type, null);
        }
        
        public static SequenceDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(file, SequenceDocument.type, options);
        }
        
        public static SequenceDocument parse(final URL u) throws XmlException, IOException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(u, SequenceDocument.type, null);
        }
        
        public static SequenceDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(u, SequenceDocument.type, options);
        }
        
        public static SequenceDocument parse(final InputStream is) throws XmlException, IOException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(is, SequenceDocument.type, null);
        }
        
        public static SequenceDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(is, SequenceDocument.type, options);
        }
        
        public static SequenceDocument parse(final Reader r) throws XmlException, IOException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(r, SequenceDocument.type, null);
        }
        
        public static SequenceDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(r, SequenceDocument.type, options);
        }
        
        public static SequenceDocument parse(final XMLStreamReader sr) throws XmlException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(sr, SequenceDocument.type, null);
        }
        
        public static SequenceDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(sr, SequenceDocument.type, options);
        }
        
        public static SequenceDocument parse(final Node node) throws XmlException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(node, SequenceDocument.type, null);
        }
        
        public static SequenceDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(node, SequenceDocument.type, options);
        }
        
        @Deprecated
        public static SequenceDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(xis, SequenceDocument.type, null);
        }
        
        @Deprecated
        public static SequenceDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SequenceDocument)XmlBeans.getContextTypeLoader().parse(xis, SequenceDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SequenceDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SequenceDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
