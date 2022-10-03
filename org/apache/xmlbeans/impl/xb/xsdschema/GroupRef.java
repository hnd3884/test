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
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;

public interface GroupRef extends RealGroup
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(GroupRef.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("groupref303ftype");
    
    QName getRef();
    
    XmlQName xgetRef();
    
    boolean isSetRef();
    
    void setRef(final QName p0);
    
    void xsetRef(final XmlQName p0);
    
    void unsetRef();
    
    public static final class Factory
    {
        public static GroupRef newInstance() {
            return (GroupRef)XmlBeans.getContextTypeLoader().newInstance(GroupRef.type, null);
        }
        
        public static GroupRef newInstance(final XmlOptions options) {
            return (GroupRef)XmlBeans.getContextTypeLoader().newInstance(GroupRef.type, options);
        }
        
        public static GroupRef parse(final String xmlAsString) throws XmlException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(xmlAsString, GroupRef.type, null);
        }
        
        public static GroupRef parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(xmlAsString, GroupRef.type, options);
        }
        
        public static GroupRef parse(final File file) throws XmlException, IOException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(file, GroupRef.type, null);
        }
        
        public static GroupRef parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(file, GroupRef.type, options);
        }
        
        public static GroupRef parse(final URL u) throws XmlException, IOException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(u, GroupRef.type, null);
        }
        
        public static GroupRef parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(u, GroupRef.type, options);
        }
        
        public static GroupRef parse(final InputStream is) throws XmlException, IOException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(is, GroupRef.type, null);
        }
        
        public static GroupRef parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(is, GroupRef.type, options);
        }
        
        public static GroupRef parse(final Reader r) throws XmlException, IOException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(r, GroupRef.type, null);
        }
        
        public static GroupRef parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(r, GroupRef.type, options);
        }
        
        public static GroupRef parse(final XMLStreamReader sr) throws XmlException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(sr, GroupRef.type, null);
        }
        
        public static GroupRef parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(sr, GroupRef.type, options);
        }
        
        public static GroupRef parse(final Node node) throws XmlException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(node, GroupRef.type, null);
        }
        
        public static GroupRef parse(final Node node, final XmlOptions options) throws XmlException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(node, GroupRef.type, options);
        }
        
        @Deprecated
        public static GroupRef parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(xis, GroupRef.type, null);
        }
        
        @Deprecated
        public static GroupRef parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (GroupRef)XmlBeans.getContextTypeLoader().parse(xis, GroupRef.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, GroupRef.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, GroupRef.type, options);
        }
        
        private Factory() {
        }
    }
}
