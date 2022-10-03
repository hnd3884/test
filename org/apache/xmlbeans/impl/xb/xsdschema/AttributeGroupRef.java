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

public interface AttributeGroupRef extends AttributeGroup
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AttributeGroupRef.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("attributegroupref8375type");
    
    QName getRef();
    
    XmlQName xgetRef();
    
    boolean isSetRef();
    
    void setRef(final QName p0);
    
    void xsetRef(final XmlQName p0);
    
    void unsetRef();
    
    public static final class Factory
    {
        public static AttributeGroupRef newInstance() {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().newInstance(AttributeGroupRef.type, null);
        }
        
        public static AttributeGroupRef newInstance(final XmlOptions options) {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().newInstance(AttributeGroupRef.type, options);
        }
        
        public static AttributeGroupRef parse(final String xmlAsString) throws XmlException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(xmlAsString, AttributeGroupRef.type, null);
        }
        
        public static AttributeGroupRef parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(xmlAsString, AttributeGroupRef.type, options);
        }
        
        public static AttributeGroupRef parse(final File file) throws XmlException, IOException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(file, AttributeGroupRef.type, null);
        }
        
        public static AttributeGroupRef parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(file, AttributeGroupRef.type, options);
        }
        
        public static AttributeGroupRef parse(final URL u) throws XmlException, IOException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(u, AttributeGroupRef.type, null);
        }
        
        public static AttributeGroupRef parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(u, AttributeGroupRef.type, options);
        }
        
        public static AttributeGroupRef parse(final InputStream is) throws XmlException, IOException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(is, AttributeGroupRef.type, null);
        }
        
        public static AttributeGroupRef parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(is, AttributeGroupRef.type, options);
        }
        
        public static AttributeGroupRef parse(final Reader r) throws XmlException, IOException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(r, AttributeGroupRef.type, null);
        }
        
        public static AttributeGroupRef parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(r, AttributeGroupRef.type, options);
        }
        
        public static AttributeGroupRef parse(final XMLStreamReader sr) throws XmlException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(sr, AttributeGroupRef.type, null);
        }
        
        public static AttributeGroupRef parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(sr, AttributeGroupRef.type, options);
        }
        
        public static AttributeGroupRef parse(final Node node) throws XmlException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(node, AttributeGroupRef.type, null);
        }
        
        public static AttributeGroupRef parse(final Node node, final XmlOptions options) throws XmlException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(node, AttributeGroupRef.type, options);
        }
        
        @Deprecated
        public static AttributeGroupRef parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(xis, AttributeGroupRef.type, null);
        }
        
        @Deprecated
        public static AttributeGroupRef parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AttributeGroupRef)XmlBeans.getContextTypeLoader().parse(xis, AttributeGroupRef.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AttributeGroupRef.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AttributeGroupRef.type, options);
        }
        
        private Factory() {
        }
    }
}
