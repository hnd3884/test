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

public interface ExtensionType extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ExtensionType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("extensiontypeed4ctype");
    
    GroupRef getGroup();
    
    boolean isSetGroup();
    
    void setGroup(final GroupRef p0);
    
    GroupRef addNewGroup();
    
    void unsetGroup();
    
    All getAll();
    
    boolean isSetAll();
    
    void setAll(final All p0);
    
    All addNewAll();
    
    void unsetAll();
    
    ExplicitGroup getChoice();
    
    boolean isSetChoice();
    
    void setChoice(final ExplicitGroup p0);
    
    ExplicitGroup addNewChoice();
    
    void unsetChoice();
    
    ExplicitGroup getSequence();
    
    boolean isSetSequence();
    
    void setSequence(final ExplicitGroup p0);
    
    ExplicitGroup addNewSequence();
    
    void unsetSequence();
    
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
    
    QName getBase();
    
    XmlQName xgetBase();
    
    void setBase(final QName p0);
    
    void xsetBase(final XmlQName p0);
    
    public static final class Factory
    {
        public static ExtensionType newInstance() {
            return (ExtensionType)XmlBeans.getContextTypeLoader().newInstance(ExtensionType.type, null);
        }
        
        public static ExtensionType newInstance(final XmlOptions options) {
            return (ExtensionType)XmlBeans.getContextTypeLoader().newInstance(ExtensionType.type, options);
        }
        
        public static ExtensionType parse(final String xmlAsString) throws XmlException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, ExtensionType.type, null);
        }
        
        public static ExtensionType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(xmlAsString, ExtensionType.type, options);
        }
        
        public static ExtensionType parse(final File file) throws XmlException, IOException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(file, ExtensionType.type, null);
        }
        
        public static ExtensionType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(file, ExtensionType.type, options);
        }
        
        public static ExtensionType parse(final URL u) throws XmlException, IOException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(u, ExtensionType.type, null);
        }
        
        public static ExtensionType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(u, ExtensionType.type, options);
        }
        
        public static ExtensionType parse(final InputStream is) throws XmlException, IOException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(is, ExtensionType.type, null);
        }
        
        public static ExtensionType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(is, ExtensionType.type, options);
        }
        
        public static ExtensionType parse(final Reader r) throws XmlException, IOException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(r, ExtensionType.type, null);
        }
        
        public static ExtensionType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(r, ExtensionType.type, options);
        }
        
        public static ExtensionType parse(final XMLStreamReader sr) throws XmlException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(sr, ExtensionType.type, null);
        }
        
        public static ExtensionType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(sr, ExtensionType.type, options);
        }
        
        public static ExtensionType parse(final Node node) throws XmlException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(node, ExtensionType.type, null);
        }
        
        public static ExtensionType parse(final Node node, final XmlOptions options) throws XmlException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(node, ExtensionType.type, options);
        }
        
        @Deprecated
        public static ExtensionType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(xis, ExtensionType.type, null);
        }
        
        @Deprecated
        public static ExtensionType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ExtensionType)XmlBeans.getContextTypeLoader().parse(xis, ExtensionType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ExtensionType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ExtensionType.type, options);
        }
        
        private Factory() {
        }
    }
}
