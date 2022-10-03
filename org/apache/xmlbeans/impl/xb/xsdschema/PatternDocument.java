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

public interface PatternDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PatternDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("pattern9585doctype");
    
    Pattern getPattern();
    
    void setPattern(final Pattern p0);
    
    Pattern addNewPattern();
    
    public interface Pattern extends NoFixedFacet
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Pattern.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("pattern6809elemtype");
        
        public static final class Factory
        {
            public static Pattern newInstance() {
                return (Pattern)XmlBeans.getContextTypeLoader().newInstance(Pattern.type, null);
            }
            
            public static Pattern newInstance(final XmlOptions options) {
                return (Pattern)XmlBeans.getContextTypeLoader().newInstance(Pattern.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static PatternDocument newInstance() {
            return (PatternDocument)XmlBeans.getContextTypeLoader().newInstance(PatternDocument.type, null);
        }
        
        public static PatternDocument newInstance(final XmlOptions options) {
            return (PatternDocument)XmlBeans.getContextTypeLoader().newInstance(PatternDocument.type, options);
        }
        
        public static PatternDocument parse(final String xmlAsString) throws XmlException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, PatternDocument.type, null);
        }
        
        public static PatternDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, PatternDocument.type, options);
        }
        
        public static PatternDocument parse(final File file) throws XmlException, IOException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(file, PatternDocument.type, null);
        }
        
        public static PatternDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(file, PatternDocument.type, options);
        }
        
        public static PatternDocument parse(final URL u) throws XmlException, IOException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(u, PatternDocument.type, null);
        }
        
        public static PatternDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(u, PatternDocument.type, options);
        }
        
        public static PatternDocument parse(final InputStream is) throws XmlException, IOException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(is, PatternDocument.type, null);
        }
        
        public static PatternDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(is, PatternDocument.type, options);
        }
        
        public static PatternDocument parse(final Reader r) throws XmlException, IOException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(r, PatternDocument.type, null);
        }
        
        public static PatternDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(r, PatternDocument.type, options);
        }
        
        public static PatternDocument parse(final XMLStreamReader sr) throws XmlException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(sr, PatternDocument.type, null);
        }
        
        public static PatternDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(sr, PatternDocument.type, options);
        }
        
        public static PatternDocument parse(final Node node) throws XmlException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(node, PatternDocument.type, null);
        }
        
        public static PatternDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(node, PatternDocument.type, options);
        }
        
        @Deprecated
        public static PatternDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(xis, PatternDocument.type, null);
        }
        
        @Deprecated
        public static PatternDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (PatternDocument)XmlBeans.getContextTypeLoader().parse(xis, PatternDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, PatternDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, PatternDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
