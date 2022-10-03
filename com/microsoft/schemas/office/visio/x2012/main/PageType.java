package com.microsoft.schemas.office.visio.x2012.main;

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
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface PageType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(PageType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("pagetype2fcatype");
    
    PageSheetType getPageSheet();
    
    boolean isSetPageSheet();
    
    void setPageSheet(final PageSheetType p0);
    
    PageSheetType addNewPageSheet();
    
    void unsetPageSheet();
    
    RelType getRel();
    
    void setRel(final RelType p0);
    
    RelType addNewRel();
    
    long getID();
    
    XmlUnsignedInt xgetID();
    
    void setID(final long p0);
    
    void xsetID(final XmlUnsignedInt p0);
    
    String getName();
    
    XmlString xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final XmlString p0);
    
    void unsetName();
    
    String getNameU();
    
    XmlString xgetNameU();
    
    boolean isSetNameU();
    
    void setNameU(final String p0);
    
    void xsetNameU(final XmlString p0);
    
    void unsetNameU();
    
    boolean getIsCustomName();
    
    XmlBoolean xgetIsCustomName();
    
    boolean isSetIsCustomName();
    
    void setIsCustomName(final boolean p0);
    
    void xsetIsCustomName(final XmlBoolean p0);
    
    void unsetIsCustomName();
    
    boolean getIsCustomNameU();
    
    XmlBoolean xgetIsCustomNameU();
    
    boolean isSetIsCustomNameU();
    
    void setIsCustomNameU(final boolean p0);
    
    void xsetIsCustomNameU(final XmlBoolean p0);
    
    void unsetIsCustomNameU();
    
    boolean getBackground();
    
    XmlBoolean xgetBackground();
    
    boolean isSetBackground();
    
    void setBackground(final boolean p0);
    
    void xsetBackground(final XmlBoolean p0);
    
    void unsetBackground();
    
    long getBackPage();
    
    XmlUnsignedInt xgetBackPage();
    
    boolean isSetBackPage();
    
    void setBackPage(final long p0);
    
    void xsetBackPage(final XmlUnsignedInt p0);
    
    void unsetBackPage();
    
    double getViewScale();
    
    XmlDouble xgetViewScale();
    
    boolean isSetViewScale();
    
    void setViewScale(final double p0);
    
    void xsetViewScale(final XmlDouble p0);
    
    void unsetViewScale();
    
    double getViewCenterX();
    
    XmlDouble xgetViewCenterX();
    
    boolean isSetViewCenterX();
    
    void setViewCenterX(final double p0);
    
    void xsetViewCenterX(final XmlDouble p0);
    
    void unsetViewCenterX();
    
    double getViewCenterY();
    
    XmlDouble xgetViewCenterY();
    
    boolean isSetViewCenterY();
    
    void setViewCenterY(final double p0);
    
    void xsetViewCenterY(final XmlDouble p0);
    
    void unsetViewCenterY();
    
    long getReviewerID();
    
    XmlUnsignedInt xgetReviewerID();
    
    boolean isSetReviewerID();
    
    void setReviewerID(final long p0);
    
    void xsetReviewerID(final XmlUnsignedInt p0);
    
    void unsetReviewerID();
    
    long getAssociatedPage();
    
    XmlUnsignedInt xgetAssociatedPage();
    
    boolean isSetAssociatedPage();
    
    void setAssociatedPage(final long p0);
    
    void xsetAssociatedPage(final XmlUnsignedInt p0);
    
    void unsetAssociatedPage();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(PageType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static PageType newInstance() {
            return (PageType)getTypeLoader().newInstance(PageType.type, (XmlOptions)null);
        }
        
        public static PageType newInstance(final XmlOptions xmlOptions) {
            return (PageType)getTypeLoader().newInstance(PageType.type, xmlOptions);
        }
        
        public static PageType parse(final String s) throws XmlException {
            return (PageType)getTypeLoader().parse(s, PageType.type, (XmlOptions)null);
        }
        
        public static PageType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (PageType)getTypeLoader().parse(s, PageType.type, xmlOptions);
        }
        
        public static PageType parse(final File file) throws XmlException, IOException {
            return (PageType)getTypeLoader().parse(file, PageType.type, (XmlOptions)null);
        }
        
        public static PageType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageType)getTypeLoader().parse(file, PageType.type, xmlOptions);
        }
        
        public static PageType parse(final URL url) throws XmlException, IOException {
            return (PageType)getTypeLoader().parse(url, PageType.type, (XmlOptions)null);
        }
        
        public static PageType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageType)getTypeLoader().parse(url, PageType.type, xmlOptions);
        }
        
        public static PageType parse(final InputStream inputStream) throws XmlException, IOException {
            return (PageType)getTypeLoader().parse(inputStream, PageType.type, (XmlOptions)null);
        }
        
        public static PageType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageType)getTypeLoader().parse(inputStream, PageType.type, xmlOptions);
        }
        
        public static PageType parse(final Reader reader) throws XmlException, IOException {
            return (PageType)getTypeLoader().parse(reader, PageType.type, (XmlOptions)null);
        }
        
        public static PageType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (PageType)getTypeLoader().parse(reader, PageType.type, xmlOptions);
        }
        
        public static PageType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (PageType)getTypeLoader().parse(xmlStreamReader, PageType.type, (XmlOptions)null);
        }
        
        public static PageType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (PageType)getTypeLoader().parse(xmlStreamReader, PageType.type, xmlOptions);
        }
        
        public static PageType parse(final Node node) throws XmlException {
            return (PageType)getTypeLoader().parse(node, PageType.type, (XmlOptions)null);
        }
        
        public static PageType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (PageType)getTypeLoader().parse(node, PageType.type, xmlOptions);
        }
        
        @Deprecated
        public static PageType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (PageType)getTypeLoader().parse(xmlInputStream, PageType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static PageType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (PageType)getTypeLoader().parse(xmlInputStream, PageType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PageType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, PageType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
