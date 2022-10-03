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

public interface SimpleContentDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SimpleContentDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("simplecontent8acedoctype");
    
    SimpleContent getSimpleContent();
    
    void setSimpleContent(final SimpleContent p0);
    
    SimpleContent addNewSimpleContent();
    
    public interface SimpleContent extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SimpleContent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("simplecontent9a5belemtype");
        
        SimpleRestrictionType getRestriction();
        
        boolean isSetRestriction();
        
        void setRestriction(final SimpleRestrictionType p0);
        
        SimpleRestrictionType addNewRestriction();
        
        void unsetRestriction();
        
        SimpleExtensionType getExtension();
        
        boolean isSetExtension();
        
        void setExtension(final SimpleExtensionType p0);
        
        SimpleExtensionType addNewExtension();
        
        void unsetExtension();
        
        public static final class Factory
        {
            public static SimpleContent newInstance() {
                return (SimpleContent)XmlBeans.getContextTypeLoader().newInstance(SimpleContent.type, null);
            }
            
            public static SimpleContent newInstance(final XmlOptions options) {
                return (SimpleContent)XmlBeans.getContextTypeLoader().newInstance(SimpleContent.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static SimpleContentDocument newInstance() {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().newInstance(SimpleContentDocument.type, null);
        }
        
        public static SimpleContentDocument newInstance(final XmlOptions options) {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().newInstance(SimpleContentDocument.type, options);
        }
        
        public static SimpleContentDocument parse(final String xmlAsString) throws XmlException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleContentDocument.type, null);
        }
        
        public static SimpleContentDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleContentDocument.type, options);
        }
        
        public static SimpleContentDocument parse(final File file) throws XmlException, IOException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(file, SimpleContentDocument.type, null);
        }
        
        public static SimpleContentDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(file, SimpleContentDocument.type, options);
        }
        
        public static SimpleContentDocument parse(final URL u) throws XmlException, IOException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(u, SimpleContentDocument.type, null);
        }
        
        public static SimpleContentDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(u, SimpleContentDocument.type, options);
        }
        
        public static SimpleContentDocument parse(final InputStream is) throws XmlException, IOException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(is, SimpleContentDocument.type, null);
        }
        
        public static SimpleContentDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(is, SimpleContentDocument.type, options);
        }
        
        public static SimpleContentDocument parse(final Reader r) throws XmlException, IOException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(r, SimpleContentDocument.type, null);
        }
        
        public static SimpleContentDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(r, SimpleContentDocument.type, options);
        }
        
        public static SimpleContentDocument parse(final XMLStreamReader sr) throws XmlException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(sr, SimpleContentDocument.type, null);
        }
        
        public static SimpleContentDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(sr, SimpleContentDocument.type, options);
        }
        
        public static SimpleContentDocument parse(final Node node) throws XmlException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(node, SimpleContentDocument.type, null);
        }
        
        public static SimpleContentDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(node, SimpleContentDocument.type, options);
        }
        
        @Deprecated
        public static SimpleContentDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(xis, SimpleContentDocument.type, null);
        }
        
        @Deprecated
        public static SimpleContentDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SimpleContentDocument)XmlBeans.getContextTypeLoader().parse(xis, SimpleContentDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleContentDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleContentDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
