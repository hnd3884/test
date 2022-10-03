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
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface SelectorDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SelectorDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("selectorcb44doctype");
    
    Selector getSelector();
    
    void setSelector(final Selector p0);
    
    Selector addNewSelector();
    
    public interface Selector extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Selector.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("selector233felemtype");
        
        String getXpath();
        
        Xpath xgetXpath();
        
        void setXpath(final String p0);
        
        void xsetXpath(final Xpath p0);
        
        public interface Xpath extends XmlToken
        {
            public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Xpath.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("xpath6f9aattrtype");
            
            public static final class Factory
            {
                public static Xpath newValue(final Object obj) {
                    return (Xpath)Xpath.type.newValue(obj);
                }
                
                public static Xpath newInstance() {
                    return (Xpath)XmlBeans.getContextTypeLoader().newInstance(Xpath.type, null);
                }
                
                public static Xpath newInstance(final XmlOptions options) {
                    return (Xpath)XmlBeans.getContextTypeLoader().newInstance(Xpath.type, options);
                }
                
                private Factory() {
                }
            }
        }
        
        public static final class Factory
        {
            public static Selector newInstance() {
                return (Selector)XmlBeans.getContextTypeLoader().newInstance(Selector.type, null);
            }
            
            public static Selector newInstance(final XmlOptions options) {
                return (Selector)XmlBeans.getContextTypeLoader().newInstance(Selector.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static SelectorDocument newInstance() {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().newInstance(SelectorDocument.type, null);
        }
        
        public static SelectorDocument newInstance(final XmlOptions options) {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().newInstance(SelectorDocument.type, options);
        }
        
        public static SelectorDocument parse(final String xmlAsString) throws XmlException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SelectorDocument.type, null);
        }
        
        public static SelectorDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SelectorDocument.type, options);
        }
        
        public static SelectorDocument parse(final File file) throws XmlException, IOException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(file, SelectorDocument.type, null);
        }
        
        public static SelectorDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(file, SelectorDocument.type, options);
        }
        
        public static SelectorDocument parse(final URL u) throws XmlException, IOException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(u, SelectorDocument.type, null);
        }
        
        public static SelectorDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(u, SelectorDocument.type, options);
        }
        
        public static SelectorDocument parse(final InputStream is) throws XmlException, IOException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(is, SelectorDocument.type, null);
        }
        
        public static SelectorDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(is, SelectorDocument.type, options);
        }
        
        public static SelectorDocument parse(final Reader r) throws XmlException, IOException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(r, SelectorDocument.type, null);
        }
        
        public static SelectorDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(r, SelectorDocument.type, options);
        }
        
        public static SelectorDocument parse(final XMLStreamReader sr) throws XmlException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(sr, SelectorDocument.type, null);
        }
        
        public static SelectorDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(sr, SelectorDocument.type, options);
        }
        
        public static SelectorDocument parse(final Node node) throws XmlException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(node, SelectorDocument.type, null);
        }
        
        public static SelectorDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(node, SelectorDocument.type, options);
        }
        
        @Deprecated
        public static SelectorDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(xis, SelectorDocument.type, null);
        }
        
        @Deprecated
        public static SelectorDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SelectorDocument)XmlBeans.getContextTypeLoader().parse(xis, SelectorDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SelectorDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SelectorDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
