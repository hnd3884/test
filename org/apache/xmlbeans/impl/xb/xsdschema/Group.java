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
import org.apache.xmlbeans.XmlNonNegativeInteger;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlQName;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface Group extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(Group.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("group7ca6type");
    
    LocalElement[] getElementArray();
    
    LocalElement getElementArray(final int p0);
    
    int sizeOfElementArray();
    
    void setElementArray(final LocalElement[] p0);
    
    void setElementArray(final int p0, final LocalElement p1);
    
    LocalElement insertNewElement(final int p0);
    
    LocalElement addNewElement();
    
    void removeElement(final int p0);
    
    GroupRef[] getGroupArray();
    
    GroupRef getGroupArray(final int p0);
    
    int sizeOfGroupArray();
    
    void setGroupArray(final GroupRef[] p0);
    
    void setGroupArray(final int p0, final GroupRef p1);
    
    GroupRef insertNewGroup(final int p0);
    
    GroupRef addNewGroup();
    
    void removeGroup(final int p0);
    
    All[] getAllArray();
    
    All getAllArray(final int p0);
    
    int sizeOfAllArray();
    
    void setAllArray(final All[] p0);
    
    void setAllArray(final int p0, final All p1);
    
    All insertNewAll(final int p0);
    
    All addNewAll();
    
    void removeAll(final int p0);
    
    ExplicitGroup[] getChoiceArray();
    
    ExplicitGroup getChoiceArray(final int p0);
    
    int sizeOfChoiceArray();
    
    void setChoiceArray(final ExplicitGroup[] p0);
    
    void setChoiceArray(final int p0, final ExplicitGroup p1);
    
    ExplicitGroup insertNewChoice(final int p0);
    
    ExplicitGroup addNewChoice();
    
    void removeChoice(final int p0);
    
    ExplicitGroup[] getSequenceArray();
    
    ExplicitGroup getSequenceArray(final int p0);
    
    int sizeOfSequenceArray();
    
    void setSequenceArray(final ExplicitGroup[] p0);
    
    void setSequenceArray(final int p0, final ExplicitGroup p1);
    
    ExplicitGroup insertNewSequence(final int p0);
    
    ExplicitGroup addNewSequence();
    
    void removeSequence(final int p0);
    
    AnyDocument.Any[] getAnyArray();
    
    AnyDocument.Any getAnyArray(final int p0);
    
    int sizeOfAnyArray();
    
    void setAnyArray(final AnyDocument.Any[] p0);
    
    void setAnyArray(final int p0, final AnyDocument.Any p1);
    
    AnyDocument.Any insertNewAny(final int p0);
    
    AnyDocument.Any addNewAny();
    
    void removeAny(final int p0);
    
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
    
    public static final class Factory
    {
        @Deprecated
        public static Group newInstance() {
            return (Group)XmlBeans.getContextTypeLoader().newInstance(Group.type, null);
        }
        
        @Deprecated
        public static Group newInstance(final XmlOptions options) {
            return (Group)XmlBeans.getContextTypeLoader().newInstance(Group.type, options);
        }
        
        public static Group parse(final String xmlAsString) throws XmlException {
            return (Group)XmlBeans.getContextTypeLoader().parse(xmlAsString, Group.type, null);
        }
        
        public static Group parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (Group)XmlBeans.getContextTypeLoader().parse(xmlAsString, Group.type, options);
        }
        
        public static Group parse(final File file) throws XmlException, IOException {
            return (Group)XmlBeans.getContextTypeLoader().parse(file, Group.type, null);
        }
        
        public static Group parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (Group)XmlBeans.getContextTypeLoader().parse(file, Group.type, options);
        }
        
        public static Group parse(final URL u) throws XmlException, IOException {
            return (Group)XmlBeans.getContextTypeLoader().parse(u, Group.type, null);
        }
        
        public static Group parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (Group)XmlBeans.getContextTypeLoader().parse(u, Group.type, options);
        }
        
        public static Group parse(final InputStream is) throws XmlException, IOException {
            return (Group)XmlBeans.getContextTypeLoader().parse(is, Group.type, null);
        }
        
        public static Group parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (Group)XmlBeans.getContextTypeLoader().parse(is, Group.type, options);
        }
        
        public static Group parse(final Reader r) throws XmlException, IOException {
            return (Group)XmlBeans.getContextTypeLoader().parse(r, Group.type, null);
        }
        
        public static Group parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (Group)XmlBeans.getContextTypeLoader().parse(r, Group.type, options);
        }
        
        public static Group parse(final XMLStreamReader sr) throws XmlException {
            return (Group)XmlBeans.getContextTypeLoader().parse(sr, Group.type, null);
        }
        
        public static Group parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (Group)XmlBeans.getContextTypeLoader().parse(sr, Group.type, options);
        }
        
        public static Group parse(final Node node) throws XmlException {
            return (Group)XmlBeans.getContextTypeLoader().parse(node, Group.type, null);
        }
        
        public static Group parse(final Node node, final XmlOptions options) throws XmlException {
            return (Group)XmlBeans.getContextTypeLoader().parse(node, Group.type, options);
        }
        
        @Deprecated
        public static Group parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (Group)XmlBeans.getContextTypeLoader().parse(xis, Group.type, null);
        }
        
        @Deprecated
        public static Group parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (Group)XmlBeans.getContextTypeLoader().parse(xis, Group.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Group.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, Group.type, options);
        }
        
        private Factory() {
        }
    }
}
