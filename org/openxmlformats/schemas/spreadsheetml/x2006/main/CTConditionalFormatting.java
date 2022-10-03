package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.SchemaTypeLoader;
import java.lang.ref.SoftReference;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlBoolean;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTConditionalFormatting extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTConditionalFormatting.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctconditionalformatting0deatype");
    
    List<CTCfRule> getCfRuleList();
    
    @Deprecated
    CTCfRule[] getCfRuleArray();
    
    CTCfRule getCfRuleArray(final int p0);
    
    int sizeOfCfRuleArray();
    
    void setCfRuleArray(final CTCfRule[] p0);
    
    void setCfRuleArray(final int p0, final CTCfRule p1);
    
    CTCfRule insertNewCfRule(final int p0);
    
    CTCfRule addNewCfRule();
    
    void removeCfRule(final int p0);
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    boolean getPivot();
    
    XmlBoolean xgetPivot();
    
    boolean isSetPivot();
    
    void setPivot(final boolean p0);
    
    void xsetPivot(final XmlBoolean p0);
    
    void unsetPivot();
    
    List getSqref();
    
    STSqref xgetSqref();
    
    boolean isSetSqref();
    
    void setSqref(final List p0);
    
    void xsetSqref(final STSqref p0);
    
    void unsetSqref();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTConditionalFormatting.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTConditionalFormatting newInstance() {
            return (CTConditionalFormatting)getTypeLoader().newInstance(CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        public static CTConditionalFormatting newInstance(final XmlOptions xmlOptions) {
            return (CTConditionalFormatting)getTypeLoader().newInstance(CTConditionalFormatting.type, xmlOptions);
        }
        
        public static CTConditionalFormatting parse(final String s) throws XmlException {
            return (CTConditionalFormatting)getTypeLoader().parse(s, CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        public static CTConditionalFormatting parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTConditionalFormatting)getTypeLoader().parse(s, CTConditionalFormatting.type, xmlOptions);
        }
        
        public static CTConditionalFormatting parse(final File file) throws XmlException, IOException {
            return (CTConditionalFormatting)getTypeLoader().parse(file, CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        public static CTConditionalFormatting parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConditionalFormatting)getTypeLoader().parse(file, CTConditionalFormatting.type, xmlOptions);
        }
        
        public static CTConditionalFormatting parse(final URL url) throws XmlException, IOException {
            return (CTConditionalFormatting)getTypeLoader().parse(url, CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        public static CTConditionalFormatting parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConditionalFormatting)getTypeLoader().parse(url, CTConditionalFormatting.type, xmlOptions);
        }
        
        public static CTConditionalFormatting parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTConditionalFormatting)getTypeLoader().parse(inputStream, CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        public static CTConditionalFormatting parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConditionalFormatting)getTypeLoader().parse(inputStream, CTConditionalFormatting.type, xmlOptions);
        }
        
        public static CTConditionalFormatting parse(final Reader reader) throws XmlException, IOException {
            return (CTConditionalFormatting)getTypeLoader().parse(reader, CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        public static CTConditionalFormatting parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTConditionalFormatting)getTypeLoader().parse(reader, CTConditionalFormatting.type, xmlOptions);
        }
        
        public static CTConditionalFormatting parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTConditionalFormatting)getTypeLoader().parse(xmlStreamReader, CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        public static CTConditionalFormatting parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTConditionalFormatting)getTypeLoader().parse(xmlStreamReader, CTConditionalFormatting.type, xmlOptions);
        }
        
        public static CTConditionalFormatting parse(final Node node) throws XmlException {
            return (CTConditionalFormatting)getTypeLoader().parse(node, CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        public static CTConditionalFormatting parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTConditionalFormatting)getTypeLoader().parse(node, CTConditionalFormatting.type, xmlOptions);
        }
        
        @Deprecated
        public static CTConditionalFormatting parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTConditionalFormatting)getTypeLoader().parse(xmlInputStream, CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTConditionalFormatting parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTConditionalFormatting)getTypeLoader().parse(xmlInputStream, CTConditionalFormatting.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConditionalFormatting.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTConditionalFormatting.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
