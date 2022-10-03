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
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface ListDocument extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ListDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("listcde5doctype");
    
    List getList();
    
    void setList(final List p0);
    
    List addNewList();
    
    public interface List extends Annotated
    {
        public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(List.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("list391felemtype");
        
        LocalSimpleType getSimpleType();
        
        boolean isSetSimpleType();
        
        void setSimpleType(final LocalSimpleType p0);
        
        LocalSimpleType addNewSimpleType();
        
        void unsetSimpleType();
        
        QName getItemType();
        
        XmlQName xgetItemType();
        
        boolean isSetItemType();
        
        void setItemType(final QName p0);
        
        void xsetItemType(final XmlQName p0);
        
        void unsetItemType();
        
        public static final class Factory
        {
            public static List newInstance() {
                return (List)XmlBeans.getContextTypeLoader().newInstance(List.type, null);
            }
            
            public static List newInstance(final XmlOptions options) {
                return (List)XmlBeans.getContextTypeLoader().newInstance(List.type, options);
            }
            
            private Factory() {
            }
        }
    }
    
    public static final class Factory
    {
        public static ListDocument newInstance() {
            return (ListDocument)XmlBeans.getContextTypeLoader().newInstance(ListDocument.type, null);
        }
        
        public static ListDocument newInstance(final XmlOptions options) {
            return (ListDocument)XmlBeans.getContextTypeLoader().newInstance(ListDocument.type, options);
        }
        
        public static ListDocument parse(final String xmlAsString) throws XmlException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ListDocument.type, null);
        }
        
        public static ListDocument parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(xmlAsString, ListDocument.type, options);
        }
        
        public static ListDocument parse(final File file) throws XmlException, IOException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(file, ListDocument.type, null);
        }
        
        public static ListDocument parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(file, ListDocument.type, options);
        }
        
        public static ListDocument parse(final URL u) throws XmlException, IOException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(u, ListDocument.type, null);
        }
        
        public static ListDocument parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(u, ListDocument.type, options);
        }
        
        public static ListDocument parse(final InputStream is) throws XmlException, IOException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(is, ListDocument.type, null);
        }
        
        public static ListDocument parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(is, ListDocument.type, options);
        }
        
        public static ListDocument parse(final Reader r) throws XmlException, IOException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(r, ListDocument.type, null);
        }
        
        public static ListDocument parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(r, ListDocument.type, options);
        }
        
        public static ListDocument parse(final XMLStreamReader sr) throws XmlException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(sr, ListDocument.type, null);
        }
        
        public static ListDocument parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(sr, ListDocument.type, options);
        }
        
        public static ListDocument parse(final Node node) throws XmlException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(node, ListDocument.type, null);
        }
        
        public static ListDocument parse(final Node node, final XmlOptions options) throws XmlException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(node, ListDocument.type, options);
        }
        
        @Deprecated
        public static ListDocument parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(xis, ListDocument.type, null);
        }
        
        @Deprecated
        public static ListDocument parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ListDocument)XmlBeans.getContextTypeLoader().parse(xis, ListDocument.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ListDocument.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ListDocument.type, options);
        }
        
        private Factory() {
        }
    }
}
