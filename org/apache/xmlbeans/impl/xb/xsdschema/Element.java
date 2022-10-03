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
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlNonNegativeInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface Element extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Element.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("elementd189type");
    
    LocalSimpleType getSimpleType();
    
    boolean isSetSimpleType();
    
    void setSimpleType(final LocalSimpleType p0);
    
    LocalSimpleType addNewSimpleType();
    
    void unsetSimpleType();
    
    LocalComplexType getComplexType();
    
    boolean isSetComplexType();
    
    void setComplexType(final LocalComplexType p0);
    
    LocalComplexType addNewComplexType();
    
    void unsetComplexType();
    
    Keybase[] getUniqueArray();
    
    Keybase getUniqueArray(final int p0);
    
    int sizeOfUniqueArray();
    
    void setUniqueArray(final Keybase[] p0);
    
    void setUniqueArray(final int p0, final Keybase p1);
    
    Keybase insertNewUnique(final int p0);
    
    Keybase addNewUnique();
    
    void removeUnique(final int p0);
    
    Keybase[] getKeyArray();
    
    Keybase getKeyArray(final int p0);
    
    int sizeOfKeyArray();
    
    void setKeyArray(final Keybase[] p0);
    
    void setKeyArray(final int p0, final Keybase p1);
    
    Keybase insertNewKey(final int p0);
    
    Keybase addNewKey();
    
    void removeKey(final int p0);
    
    KeyrefDocument.Keyref[] getKeyrefArray();
    
    KeyrefDocument.Keyref getKeyrefArray(final int p0);
    
    int sizeOfKeyrefArray();
    
    void setKeyrefArray(final KeyrefDocument.Keyref[] p0);
    
    void setKeyrefArray(final int p0, final KeyrefDocument.Keyref p1);
    
    KeyrefDocument.Keyref insertNewKeyref(final int p0);
    
    KeyrefDocument.Keyref addNewKeyref();
    
    void removeKeyref(final int p0);
    
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
    
    QName getType();
    
    XmlQName xgetType();
    
    boolean isSetType();
    
    void setType(final QName p0);
    
    void xsetType(final XmlQName p0);
    
    void unsetType();
    
    QName getSubstitutionGroup();
    
    XmlQName xgetSubstitutionGroup();
    
    boolean isSetSubstitutionGroup();
    
    void setSubstitutionGroup(final QName p0);
    
    void xsetSubstitutionGroup(final XmlQName p0);
    
    void unsetSubstitutionGroup();
    
    BigInteger getMinOccurs();
    
    XmlNonNegativeInteger xgetMinOccurs();
    
    boolean isSetMinOccurs();
    
    void setMinOccurs(final BigInteger p0);
    
    void xsetMinOccurs(final XmlNonNegativeInteger p0);
    
    void unsetMinOccurs();
    
    Object getMaxOccurs();
    
    AllNNI xgetMaxOccurs();
    
    boolean isSetMaxOccurs();
    
    void setMaxOccurs(final Object p0);
    
    void xsetMaxOccurs(final AllNNI p0);
    
    void unsetMaxOccurs();
    
    String getDefault();
    
    XmlString xgetDefault();
    
    boolean isSetDefault();
    
    void setDefault(final String p0);
    
    void xsetDefault(final XmlString p0);
    
    void unsetDefault();
    
    String getFixed();
    
    XmlString xgetFixed();
    
    boolean isSetFixed();
    
    void setFixed(final String p0);
    
    void xsetFixed(final XmlString p0);
    
    void unsetFixed();
    
    boolean getNillable();
    
    XmlBoolean xgetNillable();
    
    boolean isSetNillable();
    
    void setNillable(final boolean p0);
    
    void xsetNillable(final XmlBoolean p0);
    
    void unsetNillable();
    
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
    
    BlockSet xgetBlock();
    
    boolean isSetBlock();
    
    void setBlock(final Object p0);
    
    void xsetBlock(final BlockSet p0);
    
    void unsetBlock();
    
    FormChoice.Enum getForm();
    
    FormChoice xgetForm();
    
    boolean isSetForm();
    
    void setForm(final FormChoice.Enum p0);
    
    void xsetForm(final FormChoice p0);
    
    void unsetForm();
    
    public static final class Factory
    {
        @Deprecated
        public static Element newInstance() {
            return (Element)XmlBeans.getContextTypeLoader().newInstance(Element.type, null);
        }
        
        @Deprecated
        public static Element newInstance(final XmlOptions options) {
            return (Element)XmlBeans.getContextTypeLoader().newInstance(Element.type, options);
        }
        
        public static Element parse(final String xmlAsString) throws XmlException {
            return (Element)XmlBeans.getContextTypeLoader().parse(xmlAsString, Element.type, null);
        }
        
        public static Element parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Element)XmlBeans.getContextTypeLoader().parse(xmlAsString, Element.type, options);
        }
        
        public static Element parse(final File file) throws XmlException, IOException {
            return (Element)XmlBeans.getContextTypeLoader().parse(file, Element.type, null);
        }
        
        public static Element parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Element)XmlBeans.getContextTypeLoader().parse(file, Element.type, options);
        }
        
        public static Element parse(final URL u) throws XmlException, IOException {
            return (Element)XmlBeans.getContextTypeLoader().parse(u, Element.type, null);
        }
        
        public static Element parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Element)XmlBeans.getContextTypeLoader().parse(u, Element.type, options);
        }
        
        public static Element parse(final InputStream is) throws XmlException, IOException {
            return (Element)XmlBeans.getContextTypeLoader().parse(is, Element.type, null);
        }
        
        public static Element parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Element)XmlBeans.getContextTypeLoader().parse(is, Element.type, options);
        }
        
        public static Element parse(final Reader r) throws XmlException, IOException {
            return (Element)XmlBeans.getContextTypeLoader().parse(r, Element.type, null);
        }
        
        public static Element parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Element)XmlBeans.getContextTypeLoader().parse(r, Element.type, options);
        }
        
        public static Element parse(final XMLStreamReader sr) throws XmlException {
            return (Element)XmlBeans.getContextTypeLoader().parse(sr, Element.type, null);
        }
        
        public static Element parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Element)XmlBeans.getContextTypeLoader().parse(sr, Element.type, options);
        }
        
        public static Element parse(final Node node) throws XmlException {
            return (Element)XmlBeans.getContextTypeLoader().parse(node, Element.type, null);
        }
        
        public static Element parse(final Node node, final XmlOptions options) throws XmlException {
            return (Element)XmlBeans.getContextTypeLoader().parse(node, Element.type, options);
        }
        
        @Deprecated
        public static Element parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Element)XmlBeans.getContextTypeLoader().parse(xis, Element.type, null);
        }
        
        @Deprecated
        public static Element parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Element)XmlBeans.getContextTypeLoader().parse(xis, Element.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Element.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Element.type, options);
        }
        
        private Factory() {
        }
    }
}
