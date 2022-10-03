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
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface NotationDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(NotationDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("notation3381doctype");
    
    Notation getNotation();
    
    void setNotation(final Notation p0);
    
    Notation addNewNotation();
    
    public interface Notation extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Notation.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("notation8b1felemtype");
        
        String getName();
        
        XmlNCName xgetName();
        
        void setName(final String p0);
        
        void xsetName(final XmlNCName p0);
        
        String getPublic();
        
        Public xgetPublic();
        
        boolean isSetPublic();
        
        void setPublic(final String p0);
        
        void xsetPublic(final Public p0);
        
        void unsetPublic();
        
        String getSystem();
        
        XmlAnyURI xgetSystem();
        
        boolean isSetSystem();
        
        void setSystem(final String p0);
        
        void xsetSystem(final XmlAnyURI p0);
        
        void unsetSystem();
        
        public static final class Factory
        {
            public static Notation newInstance() {
                return (Notation)XmlBeans.getContextTypeLoader().newInstance(Notation.type, null);
            }
            
            public static Notation newInstance(final XmlOptions options) {
                return (Notation)XmlBeans.getContextTypeLoader().newInstance(Notation.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static NotationDocument newInstance() {
            return (NotationDocument)XmlBeans.getContextTypeLoader().newInstance(NotationDocument.type, null);
        }
        
        public static NotationDocument newInstance(final XmlOptions options) {
            return (NotationDocument)XmlBeans.getContextTypeLoader().newInstance(NotationDocument.type, options);
        }
        
        public static NotationDocument parse(final String xmlAsString) throws XmlException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, NotationDocument.type, null);
        }
        
        public static NotationDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, NotationDocument.type, options);
        }
        
        public static NotationDocument parse(final File file) throws XmlException, IOException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(file, NotationDocument.type, null);
        }
        
        public static NotationDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(file, NotationDocument.type, options);
        }
        
        public static NotationDocument parse(final URL u) throws XmlException, IOException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(u, NotationDocument.type, null);
        }
        
        public static NotationDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(u, NotationDocument.type, options);
        }
        
        public static NotationDocument parse(final InputStream is) throws XmlException, IOException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(is, NotationDocument.type, null);
        }
        
        public static NotationDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(is, NotationDocument.type, options);
        }
        
        public static NotationDocument parse(final Reader r) throws XmlException, IOException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(r, NotationDocument.type, null);
        }
        
        public static NotationDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(r, NotationDocument.type, options);
        }
        
        public static NotationDocument parse(final XMLStreamReader sr) throws XmlException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(sr, NotationDocument.type, null);
        }
        
        public static NotationDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(sr, NotationDocument.type, options);
        }
        
        public static NotationDocument parse(final Node node) throws XmlException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(node, NotationDocument.type, null);
        }
        
        public static NotationDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(node, NotationDocument.type, options);
        }
        
        @Deprecated
        public static NotationDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(xis, NotationDocument.type, null);
        }
        
        @Deprecated
        public static NotationDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (NotationDocument)XmlBeans.getContextTypeLoader().parse(xis, NotationDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NotationDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, NotationDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
