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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTOutlinePr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTOutlinePr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctoutlineprc483type");
    
    boolean getApplyStyles();
    
    XmlBoolean xgetApplyStyles();
    
    boolean isSetApplyStyles();
    
    void setApplyStyles(final boolean p0);
    
    void xsetApplyStyles(final XmlBoolean p0);
    
    void unsetApplyStyles();
    
    boolean getSummaryBelow();
    
    XmlBoolean xgetSummaryBelow();
    
    boolean isSetSummaryBelow();
    
    void setSummaryBelow(final boolean p0);
    
    void xsetSummaryBelow(final XmlBoolean p0);
    
    void unsetSummaryBelow();
    
    boolean getSummaryRight();
    
    XmlBoolean xgetSummaryRight();
    
    boolean isSetSummaryRight();
    
    void setSummaryRight(final boolean p0);
    
    void xsetSummaryRight(final XmlBoolean p0);
    
    void unsetSummaryRight();
    
    boolean getShowOutlineSymbols();
    
    XmlBoolean xgetShowOutlineSymbols();
    
    boolean isSetShowOutlineSymbols();
    
    void setShowOutlineSymbols(final boolean p0);
    
    void xsetShowOutlineSymbols(final XmlBoolean p0);
    
    void unsetShowOutlineSymbols();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTOutlinePr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTOutlinePr newInstance() {
            return (CTOutlinePr)getTypeLoader().newInstance(CTOutlinePr.type, (XmlOptions)null);
        }
        
        public static CTOutlinePr newInstance(final XmlOptions xmlOptions) {
            return (CTOutlinePr)getTypeLoader().newInstance(CTOutlinePr.type, xmlOptions);
        }
        
        public static CTOutlinePr parse(final String s) throws XmlException {
            return (CTOutlinePr)getTypeLoader().parse(s, CTOutlinePr.type, (XmlOptions)null);
        }
        
        public static CTOutlinePr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTOutlinePr)getTypeLoader().parse(s, CTOutlinePr.type, xmlOptions);
        }
        
        public static CTOutlinePr parse(final File file) throws XmlException, IOException {
            return (CTOutlinePr)getTypeLoader().parse(file, CTOutlinePr.type, (XmlOptions)null);
        }
        
        public static CTOutlinePr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOutlinePr)getTypeLoader().parse(file, CTOutlinePr.type, xmlOptions);
        }
        
        public static CTOutlinePr parse(final URL url) throws XmlException, IOException {
            return (CTOutlinePr)getTypeLoader().parse(url, CTOutlinePr.type, (XmlOptions)null);
        }
        
        public static CTOutlinePr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOutlinePr)getTypeLoader().parse(url, CTOutlinePr.type, xmlOptions);
        }
        
        public static CTOutlinePr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTOutlinePr)getTypeLoader().parse(inputStream, CTOutlinePr.type, (XmlOptions)null);
        }
        
        public static CTOutlinePr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOutlinePr)getTypeLoader().parse(inputStream, CTOutlinePr.type, xmlOptions);
        }
        
        public static CTOutlinePr parse(final Reader reader) throws XmlException, IOException {
            return (CTOutlinePr)getTypeLoader().parse(reader, CTOutlinePr.type, (XmlOptions)null);
        }
        
        public static CTOutlinePr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTOutlinePr)getTypeLoader().parse(reader, CTOutlinePr.type, xmlOptions);
        }
        
        public static CTOutlinePr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTOutlinePr)getTypeLoader().parse(xmlStreamReader, CTOutlinePr.type, (XmlOptions)null);
        }
        
        public static CTOutlinePr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTOutlinePr)getTypeLoader().parse(xmlStreamReader, CTOutlinePr.type, xmlOptions);
        }
        
        public static CTOutlinePr parse(final Node node) throws XmlException {
            return (CTOutlinePr)getTypeLoader().parse(node, CTOutlinePr.type, (XmlOptions)null);
        }
        
        public static CTOutlinePr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTOutlinePr)getTypeLoader().parse(node, CTOutlinePr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTOutlinePr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTOutlinePr)getTypeLoader().parse(xmlInputStream, CTOutlinePr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTOutlinePr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTOutlinePr)getTypeLoader().parse(xmlInputStream, CTOutlinePr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOutlinePr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTOutlinePr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
