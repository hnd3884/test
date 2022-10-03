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

public interface ChoiceDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ChoiceDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("choicedf82doctype");
    
    ExplicitGroup getChoice();
    
    void setChoice(final ExplicitGroup p0);
    
    ExplicitGroup addNewChoice();
    
    public static final class Factory
    {
        public static ChoiceDocument newInstance() {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().newInstance(ChoiceDocument.type, null);
        }
        
        public static ChoiceDocument newInstance(final XmlOptions options) {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().newInstance(ChoiceDocument.type, options);
        }
        
        public static ChoiceDocument parse(final String xmlAsString) throws XmlException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ChoiceDocument.type, null);
        }
        
        public static ChoiceDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ChoiceDocument.type, options);
        }
        
        public static ChoiceDocument parse(final File file) throws XmlException, IOException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(file, ChoiceDocument.type, null);
        }
        
        public static ChoiceDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(file, ChoiceDocument.type, options);
        }
        
        public static ChoiceDocument parse(final URL u) throws XmlException, IOException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(u, ChoiceDocument.type, null);
        }
        
        public static ChoiceDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(u, ChoiceDocument.type, options);
        }
        
        public static ChoiceDocument parse(final InputStream is) throws XmlException, IOException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(is, ChoiceDocument.type, null);
        }
        
        public static ChoiceDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(is, ChoiceDocument.type, options);
        }
        
        public static ChoiceDocument parse(final Reader r) throws XmlException, IOException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(r, ChoiceDocument.type, null);
        }
        
        public static ChoiceDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(r, ChoiceDocument.type, options);
        }
        
        public static ChoiceDocument parse(final XMLStreamReader sr) throws XmlException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(sr, ChoiceDocument.type, null);
        }
        
        public static ChoiceDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(sr, ChoiceDocument.type, options);
        }
        
        public static ChoiceDocument parse(final Node node) throws XmlException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(node, ChoiceDocument.type, null);
        }
        
        public static ChoiceDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(node, ChoiceDocument.type, options);
        }
        
        @Deprecated
        public static ChoiceDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(xis, ChoiceDocument.type, null);
        }
        
        @Deprecated
        public static ChoiceDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ChoiceDocument)XmlBeans.getContextTypeLoader().parse(xis, ChoiceDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ChoiceDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ChoiceDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
