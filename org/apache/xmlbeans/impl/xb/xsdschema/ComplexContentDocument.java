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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ComplexContentDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ComplexContentDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("complexcontentc57adoctype");
    
    ComplexContent getComplexContent();
    
    void setComplexContent(final ComplexContent p0);
    
    ComplexContent addNewComplexContent();
    
    public interface ComplexContent extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ComplexContent.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("complexcontentaa7felemtype");
        
        ComplexRestrictionType getRestriction();
        
        boolean isSetRestriction();
        
        void setRestriction(final ComplexRestrictionType p0);
        
        ComplexRestrictionType addNewRestriction();
        
        void unsetRestriction();
        
        ExtensionType getExtension();
        
        boolean isSetExtension();
        
        void setExtension(final ExtensionType p0);
        
        ExtensionType addNewExtension();
        
        void unsetExtension();
        
        boolean getMixed();
        
        XmlBoolean xgetMixed();
        
        boolean isSetMixed();
        
        void setMixed(final boolean p0);
        
        void xsetMixed(final XmlBoolean p0);
        
        void unsetMixed();
        
        public static final class Factory
        {
            public static ComplexContent newInstance() {
                return (ComplexContent)XmlBeans.getContextTypeLoader().newInstance(ComplexContent.type, null);
            }
            
            public static ComplexContent newInstance(final XmlOptions options) {
                return (ComplexContent)XmlBeans.getContextTypeLoader().newInstance(ComplexContent.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static ComplexContentDocument newInstance() {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().newInstance(ComplexContentDocument.type, null);
        }
        
        public static ComplexContentDocument newInstance(final XmlOptions options) {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().newInstance(ComplexContentDocument.type, options);
        }
        
        public static ComplexContentDocument parse(final String xmlAsString) throws XmlException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ComplexContentDocument.type, null);
        }
        
        public static ComplexContentDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ComplexContentDocument.type, options);
        }
        
        public static ComplexContentDocument parse(final File file) throws XmlException, IOException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(file, ComplexContentDocument.type, null);
        }
        
        public static ComplexContentDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(file, ComplexContentDocument.type, options);
        }
        
        public static ComplexContentDocument parse(final URL u) throws XmlException, IOException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(u, ComplexContentDocument.type, null);
        }
        
        public static ComplexContentDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(u, ComplexContentDocument.type, options);
        }
        
        public static ComplexContentDocument parse(final InputStream is) throws XmlException, IOException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(is, ComplexContentDocument.type, null);
        }
        
        public static ComplexContentDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(is, ComplexContentDocument.type, options);
        }
        
        public static ComplexContentDocument parse(final Reader r) throws XmlException, IOException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(r, ComplexContentDocument.type, null);
        }
        
        public static ComplexContentDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(r, ComplexContentDocument.type, options);
        }
        
        public static ComplexContentDocument parse(final XMLStreamReader sr) throws XmlException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(sr, ComplexContentDocument.type, null);
        }
        
        public static ComplexContentDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(sr, ComplexContentDocument.type, options);
        }
        
        public static ComplexContentDocument parse(final Node node) throws XmlException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(node, ComplexContentDocument.type, null);
        }
        
        public static ComplexContentDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(node, ComplexContentDocument.type, options);
        }
        
        @Deprecated
        public static ComplexContentDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(xis, ComplexContentDocument.type, null);
        }
        
        @Deprecated
        public static ComplexContentDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ComplexContentDocument)XmlBeans.getContextTypeLoader().parse(xis, ComplexContentDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ComplexContentDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ComplexContentDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
