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

public interface FieldDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(FieldDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("field3f9bdoctype");
    
    Field getField();
    
    void setField(final Field p0);
    
    Field addNewField();
    
    public interface Field extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Field.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("field12f5elemtype");
        
        String getXpath();
        
        Xpath xgetXpath();
        
        void setXpath(final String p0);
        
        void xsetXpath(final Xpath p0);
        
        public interface Xpath extends XmlToken
        {
            public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Xpath.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("xpath7f90attrtype");
            
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
            public static Field newInstance() {
                return (Field)XmlBeans.getContextTypeLoader().newInstance(Field.type, null);
            }
            
            public static Field newInstance(final XmlOptions options) {
                return (Field)XmlBeans.getContextTypeLoader().newInstance(Field.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static FieldDocument newInstance() {
            return (FieldDocument)XmlBeans.getContextTypeLoader().newInstance(FieldDocument.type, null);
        }
        
        public static FieldDocument newInstance(final XmlOptions options) {
            return (FieldDocument)XmlBeans.getContextTypeLoader().newInstance(FieldDocument.type, options);
        }
        
        public static FieldDocument parse(final String xmlAsString) throws XmlException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, FieldDocument.type, null);
        }
        
        public static FieldDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, FieldDocument.type, options);
        }
        
        public static FieldDocument parse(final File file) throws XmlException, IOException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(file, FieldDocument.type, null);
        }
        
        public static FieldDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(file, FieldDocument.type, options);
        }
        
        public static FieldDocument parse(final URL u) throws XmlException, IOException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(u, FieldDocument.type, null);
        }
        
        public static FieldDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(u, FieldDocument.type, options);
        }
        
        public static FieldDocument parse(final InputStream is) throws XmlException, IOException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(is, FieldDocument.type, null);
        }
        
        public static FieldDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(is, FieldDocument.type, options);
        }
        
        public static FieldDocument parse(final Reader r) throws XmlException, IOException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(r, FieldDocument.type, null);
        }
        
        public static FieldDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(r, FieldDocument.type, options);
        }
        
        public static FieldDocument parse(final XMLStreamReader sr) throws XmlException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(sr, FieldDocument.type, null);
        }
        
        public static FieldDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(sr, FieldDocument.type, options);
        }
        
        public static FieldDocument parse(final Node node) throws XmlException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(node, FieldDocument.type, null);
        }
        
        public static FieldDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(node, FieldDocument.type, options);
        }
        
        @Deprecated
        public static FieldDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(xis, FieldDocument.type, null);
        }
        
        @Deprecated
        public static FieldDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (FieldDocument)XmlBeans.getContextTypeLoader().parse(xis, FieldDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, FieldDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, FieldDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
