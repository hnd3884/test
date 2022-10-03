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
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface AttributeGroup extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(AttributeGroup.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("attributegroupe530type");
    
    Attribute[] getAttributeArray();
    
    Attribute getAttributeArray(final int p0);
    
    int sizeOfAttributeArray();
    
    void setAttributeArray(final Attribute[] p0);
    
    void setAttributeArray(final int p0, final Attribute p1);
    
    Attribute insertNewAttribute(final int p0);
    
    Attribute addNewAttribute();
    
    void removeAttribute(final int p0);
    
    AttributeGroupRef[] getAttributeGroupArray();
    
    AttributeGroupRef getAttributeGroupArray(final int p0);
    
    int sizeOfAttributeGroupArray();
    
    void setAttributeGroupArray(final AttributeGroupRef[] p0);
    
    void setAttributeGroupArray(final int p0, final AttributeGroupRef p1);
    
    AttributeGroupRef insertNewAttributeGroup(final int p0);
    
    AttributeGroupRef addNewAttributeGroup();
    
    void removeAttributeGroup(final int p0);
    
    Wildcard getAnyAttribute();
    
    boolean isSetAnyAttribute();
    
    void setAnyAttribute(final Wildcard p0);
    
    Wildcard addNewAnyAttribute();
    
    void unsetAnyAttribute();
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    QName getRef();
    
    XmlQName xgetRef();
    
    boolean isSetRef();
    
    void setRef(final QName p0);
    
    void xsetRef(final XmlQName p0);
    
    void unsetRef();
    
    public static final class Factory
    {
        @Deprecated
        public static AttributeGroup newInstance() {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().newInstance(AttributeGroup.type, null);
        }
        
        @Deprecated
        public static AttributeGroup newInstance(final XmlOptions options) {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().newInstance(AttributeGroup.type, options);
        }
        
        public static AttributeGroup parse(final String xmlAsString) throws XmlException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, AttributeGroup.type, null);
        }
        
        public static AttributeGroup parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(xmlAsString, AttributeGroup.type, options);
        }
        
        public static AttributeGroup parse(final File file) throws XmlException, IOException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(file, AttributeGroup.type, null);
        }
        
        public static AttributeGroup parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(file, AttributeGroup.type, options);
        }
        
        public static AttributeGroup parse(final URL u) throws XmlException, IOException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(u, AttributeGroup.type, null);
        }
        
        public static AttributeGroup parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(u, AttributeGroup.type, options);
        }
        
        public static AttributeGroup parse(final InputStream is) throws XmlException, IOException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(is, AttributeGroup.type, null);
        }
        
        public static AttributeGroup parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(is, AttributeGroup.type, options);
        }
        
        public static AttributeGroup parse(final Reader r) throws XmlException, IOException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(r, AttributeGroup.type, null);
        }
        
        public static AttributeGroup parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(r, AttributeGroup.type, options);
        }
        
        public static AttributeGroup parse(final XMLStreamReader sr) throws XmlException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(sr, AttributeGroup.type, null);
        }
        
        public static AttributeGroup parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(sr, AttributeGroup.type, options);
        }
        
        public static AttributeGroup parse(final Node node) throws XmlException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(node, AttributeGroup.type, null);
        }
        
        public static AttributeGroup parse(final Node node, final XmlOptions options) throws XmlException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(node, AttributeGroup.type, options);
        }
        
        @Deprecated
        public static AttributeGroup parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(xis, AttributeGroup.type, null);
        }
        
        @Deprecated
        public static AttributeGroup parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (AttributeGroup)XmlBeans.getContextTypeLoader().parse(xis, AttributeGroup.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AttributeGroup.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, AttributeGroup.type, options);
        }
        
        private Factory() {
        }
    }
}
