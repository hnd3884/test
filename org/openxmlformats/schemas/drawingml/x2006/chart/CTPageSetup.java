package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPageSetup extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPageSetup.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpagesetupdb38type");
    
    long getPaperSize();
    
    XmlUnsignedInt xgetPaperSize();
    
    boolean isSetPaperSize();
    
    void setPaperSize(final long p0);
    
    void xsetPaperSize(final XmlUnsignedInt p0);
    
    void unsetPaperSize();
    
    long getFirstPageNumber();
    
    XmlUnsignedInt xgetFirstPageNumber();
    
    boolean isSetFirstPageNumber();
    
    void setFirstPageNumber(final long p0);
    
    void xsetFirstPageNumber(final XmlUnsignedInt p0);
    
    void unsetFirstPageNumber();
    
    STPageSetupOrientation.Enum getOrientation();
    
    STPageSetupOrientation xgetOrientation();
    
    boolean isSetOrientation();
    
    void setOrientation(final STPageSetupOrientation.Enum p0);
    
    void xsetOrientation(final STPageSetupOrientation p0);
    
    void unsetOrientation();
    
    boolean getBlackAndWhite();
    
    XmlBoolean xgetBlackAndWhite();
    
    boolean isSetBlackAndWhite();
    
    void setBlackAndWhite(final boolean p0);
    
    void xsetBlackAndWhite(final XmlBoolean p0);
    
    void unsetBlackAndWhite();
    
    boolean getDraft();
    
    XmlBoolean xgetDraft();
    
    boolean isSetDraft();
    
    void setDraft(final boolean p0);
    
    void xsetDraft(final XmlBoolean p0);
    
    void unsetDraft();
    
    boolean getUseFirstPageNumber();
    
    XmlBoolean xgetUseFirstPageNumber();
    
    boolean isSetUseFirstPageNumber();
    
    void setUseFirstPageNumber(final boolean p0);
    
    void xsetUseFirstPageNumber(final XmlBoolean p0);
    
    void unsetUseFirstPageNumber();
    
    int getHorizontalDpi();
    
    XmlInt xgetHorizontalDpi();
    
    boolean isSetHorizontalDpi();
    
    void setHorizontalDpi(final int p0);
    
    void xsetHorizontalDpi(final XmlInt p0);
    
    void unsetHorizontalDpi();
    
    int getVerticalDpi();
    
    XmlInt xgetVerticalDpi();
    
    boolean isSetVerticalDpi();
    
    void setVerticalDpi(final int p0);
    
    void xsetVerticalDpi(final XmlInt p0);
    
    void unsetVerticalDpi();
    
    long getCopies();
    
    XmlUnsignedInt xgetCopies();
    
    boolean isSetCopies();
    
    void setCopies(final long p0);
    
    void xsetCopies(final XmlUnsignedInt p0);
    
    void unsetCopies();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPageSetup.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPageSetup newInstance() {
            return (CTPageSetup)getTypeLoader().newInstance(CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup newInstance(final XmlOptions xmlOptions) {
            return (CTPageSetup)getTypeLoader().newInstance(CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final String s) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(s, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(s, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final File file) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(file, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(file, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final URL url) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(url, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(url, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(inputStream, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(inputStream, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final Reader reader) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(reader, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPageSetup)getTypeLoader().parse(reader, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(xmlStreamReader, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(xmlStreamReader, CTPageSetup.type, xmlOptions);
        }
        
        public static CTPageSetup parse(final Node node) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(node, CTPageSetup.type, (XmlOptions)null);
        }
        
        public static CTPageSetup parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPageSetup)getTypeLoader().parse(node, CTPageSetup.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPageSetup parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPageSetup)getTypeLoader().parse(xmlInputStream, CTPageSetup.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPageSetup parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPageSetup)getTypeLoader().parse(xmlInputStream, CTPageSetup.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageSetup.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPageSetup.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
