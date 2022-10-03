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
import org.apache.xmlbeans.XmlNCName;
import org.apache.xmlbeans.SchemaType;

public interface SimpleType extends Annotated
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SimpleType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sXMLSCHEMA").resolveHandle("simpletype0707type");
    
    RestrictionDocument.Restriction getRestriction();
    
    boolean isSetRestriction();
    
    void setRestriction(final RestrictionDocument.Restriction p0);
    
    RestrictionDocument.Restriction addNewRestriction();
    
    void unsetRestriction();
    
    ListDocument.List getList();
    
    boolean isSetList();
    
    void setList(final ListDocument.List p0);
    
    ListDocument.List addNewList();
    
    void unsetList();
    
    UnionDocument.Union getUnion();
    
    boolean isSetUnion();
    
    void setUnion(final UnionDocument.Union p0);
    
    UnionDocument.Union addNewUnion();
    
    void unsetUnion();
    
    Object getFinal();
    
    SimpleDerivationSet xgetFinal();
    
    boolean isSetFinal();
    
    void setFinal(final Object p0);
    
    void xsetFinal(final SimpleDerivationSet p0);
    
    void unsetFinal();
    
    String getName();
    
    XmlNCName xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlNCName p0);
    
    void unsetName();
    
    public static final class Factory
    {
        @Deprecated
        public static SimpleType newInstance() {
            return (SimpleType)XmlBeans.getContextTypeLoader().newInstance(SimpleType.type, null);
        }
        
        @Deprecated
        public static SimpleType newInstance(final XmlOptions options) {
            return (SimpleType)XmlBeans.getContextTypeLoader().newInstance(SimpleType.type, options);
        }
        
        public static SimpleType parse(final String xmlAsString) throws XmlException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleType.type, null);
        }
        
        public static SimpleType parse(final String xmlAsString, final XmlOptions options) throws XmlException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(xmlAsString, SimpleType.type, options);
        }
        
        public static SimpleType parse(final File file) throws XmlException, IOException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(file, SimpleType.type, null);
        }
        
        public static SimpleType parse(final File file, final XmlOptions options) throws XmlException, IOException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(file, SimpleType.type, options);
        }
        
        public static SimpleType parse(final URL u) throws XmlException, IOException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(u, SimpleType.type, null);
        }
        
        public static SimpleType parse(final URL u, final XmlOptions options) throws XmlException, IOException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(u, SimpleType.type, options);
        }
        
        public static SimpleType parse(final InputStream is) throws XmlException, IOException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(is, SimpleType.type, null);
        }
        
        public static SimpleType parse(final InputStream is, final XmlOptions options) throws XmlException, IOException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(is, SimpleType.type, options);
        }
        
        public static SimpleType parse(final Reader r) throws XmlException, IOException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(r, SimpleType.type, null);
        }
        
        public static SimpleType parse(final Reader r, final XmlOptions options) throws XmlException, IOException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(r, SimpleType.type, options);
        }
        
        public static SimpleType parse(final XMLStreamReader sr) throws XmlException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(sr, SimpleType.type, null);
        }
        
        public static SimpleType parse(final XMLStreamReader sr, final XmlOptions options) throws XmlException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(sr, SimpleType.type, options);
        }
        
        public static SimpleType parse(final Node node) throws XmlException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(node, SimpleType.type, null);
        }
        
        public static SimpleType parse(final Node node, final XmlOptions options) throws XmlException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(node, SimpleType.type, options);
        }
        
        @Deprecated
        public static SimpleType parse(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(xis, SimpleType.type, null);
        }
        
        @Deprecated
        public static SimpleType parse(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return (SimpleType)XmlBeans.getContextTypeLoader().parse(xis, SimpleType.type, options);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleType.type, null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xis, final XmlOptions options) throws XmlException, XMLStreamException {
            return XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, SimpleType.type, options);
        }
        
        private Factory() {
        }
    }
}
