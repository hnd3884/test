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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface ComplexType extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ComplexType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("complextype5dbbtype");
    
    SimpleContentDocument.SimpleContent getSimpleContent();
    
    boolean isSetSimpleContent();
    
    void setSimpleContent(final SimpleContentDocument.SimpleContent p0);
    
    SimpleContentDocument.SimpleContent addNewSimpleContent();
    
    void unsetSimpleContent();
    
    ComplexContentDocument.ComplexContent getComplexContent();
    
    boolean isSetComplexContent();
    
    void setComplexContent(final ComplexContentDocument.ComplexContent p0);
    
    ComplexContentDocument.ComplexContent addNewComplexContent();
    
    void unsetComplexContent();
    
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
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    boolean getMixed();
    
    XmlBoolean xgetMixed();
    
    boolean isSetMixed();
    
    void setMixed(final boolean p0);
    
    void xsetMixed(final XmlBoolean p0);
    
    void unsetMixed();
    
    boolean getAbstract();
    
    XmlBoolean xgetAbstract();
    
    boolean isSetAbstract();
    
    void setAbstract(final boolean p0);
    
    void xsetAbstract(final XmlBoolean p0);
    
    void unsetAbstract();
    
    Object getFinal();
    
    DerivationSet xgetFinal();
    
    boolean isSetFinal();
    
    void setFinal(final Object p0);
    
    void xsetFinal(final DerivationSet p0);
    
    void unsetFinal();
    
    Object getBlock();
    
    DerivationSet xgetBlock();
    
    boolean isSetBlock();
    
    void setBlock(final Object p0);
    
    void xsetBlock(final DerivationSet p0);
    
    void unsetBlock();
    
    public static final class Factory
    {
        @Deprecated
        public static ComplexType newInstance() {
            return (ComplexType)XmlBeans.getContextTypeLoader().newInstance(ComplexType.type, null);
        }
        
        @Deprecated
        public static ComplexType newInstance(final XmlOptions options) {
            return (ComplexType)XmlBeans.getContextTypeLoader().newInstance(ComplexType.type, options);
        }
        
        public static ComplexType parse(final String xmlAsString) throws XmlException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(xmlAsString, ComplexType.type, null);
        }
        
        public static ComplexType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(xmlAsString, ComplexType.type, options);
        }
        
        public static ComplexType parse(final File file) throws XmlException, IOException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(file, ComplexType.type, null);
        }
        
        public static ComplexType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(file, ComplexType.type, options);
        }
        
        public static ComplexType parse(final URL u) throws XmlException, IOException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(u, ComplexType.type, null);
        }
        
        public static ComplexType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(u, ComplexType.type, options);
        }
        
        public static ComplexType parse(final InputStream is) throws XmlException, IOException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(is, ComplexType.type, null);
        }
        
        public static ComplexType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(is, ComplexType.type, options);
        }
        
        public static ComplexType parse(final Reader r) throws XmlException, IOException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(r, ComplexType.type, null);
        }
        
        public static ComplexType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(r, ComplexType.type, options);
        }
        
        public static ComplexType parse(final XMLStreamReader sr) throws XmlException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(sr, ComplexType.type, null);
        }
        
        public static ComplexType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(sr, ComplexType.type, options);
        }
        
        public static ComplexType parse(final Node node) throws XmlException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(node, ComplexType.type, null);
        }
        
        public static ComplexType parse(final Node node, final XmlOptions options) throws XmlException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(node, ComplexType.type, options);
        }
        
        @Deprecated
        public static ComplexType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(xis, ComplexType.type, null);
        }
        
        @Deprecated
        public static ComplexType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (ComplexType)XmlBeans.getContextTypeLoader().parse(xis, ComplexType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ComplexType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, ComplexType.type, options);
        }
        
        private Factory() {
        }
    }
}
