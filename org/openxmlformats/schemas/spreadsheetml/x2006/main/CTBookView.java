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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBookView extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBookView.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbookviewf677type");
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    STVisibility.Enum getVisibility();
    
    STVisibility xgetVisibility();
    
    boolean isSetVisibility();
    
    void setVisibility(final STVisibility.Enum p0);
    
    void xsetVisibility(final STVisibility p0);
    
    void unsetVisibility();
    
    boolean getMinimized();
    
    XmlBoolean xgetMinimized();
    
    boolean isSetMinimized();
    
    void setMinimized(final boolean p0);
    
    void xsetMinimized(final XmlBoolean p0);
    
    void unsetMinimized();
    
    boolean getShowHorizontalScroll();
    
    XmlBoolean xgetShowHorizontalScroll();
    
    boolean isSetShowHorizontalScroll();
    
    void setShowHorizontalScroll(final boolean p0);
    
    void xsetShowHorizontalScroll(final XmlBoolean p0);
    
    void unsetShowHorizontalScroll();
    
    boolean getShowVerticalScroll();
    
    XmlBoolean xgetShowVerticalScroll();
    
    boolean isSetShowVerticalScroll();
    
    void setShowVerticalScroll(final boolean p0);
    
    void xsetShowVerticalScroll(final XmlBoolean p0);
    
    void unsetShowVerticalScroll();
    
    boolean getShowSheetTabs();
    
    XmlBoolean xgetShowSheetTabs();
    
    boolean isSetShowSheetTabs();
    
    void setShowSheetTabs(final boolean p0);
    
    void xsetShowSheetTabs(final XmlBoolean p0);
    
    void unsetShowSheetTabs();
    
    int getXWindow();
    
    XmlInt xgetXWindow();
    
    boolean isSetXWindow();
    
    void setXWindow(final int p0);
    
    void xsetXWindow(final XmlInt p0);
    
    void unsetXWindow();
    
    int getYWindow();
    
    XmlInt xgetYWindow();
    
    boolean isSetYWindow();
    
    void setYWindow(final int p0);
    
    void xsetYWindow(final XmlInt p0);
    
    void unsetYWindow();
    
    long getWindowWidth();
    
    XmlUnsignedInt xgetWindowWidth();
    
    boolean isSetWindowWidth();
    
    void setWindowWidth(final long p0);
    
    void xsetWindowWidth(final XmlUnsignedInt p0);
    
    void unsetWindowWidth();
    
    long getWindowHeight();
    
    XmlUnsignedInt xgetWindowHeight();
    
    boolean isSetWindowHeight();
    
    void setWindowHeight(final long p0);
    
    void xsetWindowHeight(final XmlUnsignedInt p0);
    
    void unsetWindowHeight();
    
    long getTabRatio();
    
    XmlUnsignedInt xgetTabRatio();
    
    boolean isSetTabRatio();
    
    void setTabRatio(final long p0);
    
    void xsetTabRatio(final XmlUnsignedInt p0);
    
    void unsetTabRatio();
    
    long getFirstSheet();
    
    XmlUnsignedInt xgetFirstSheet();
    
    boolean isSetFirstSheet();
    
    void setFirstSheet(final long p0);
    
    void xsetFirstSheet(final XmlUnsignedInt p0);
    
    void unsetFirstSheet();
    
    long getActiveTab();
    
    XmlUnsignedInt xgetActiveTab();
    
    boolean isSetActiveTab();
    
    void setActiveTab(final long p0);
    
    void xsetActiveTab(final XmlUnsignedInt p0);
    
    void unsetActiveTab();
    
    boolean getAutoFilterDateGrouping();
    
    XmlBoolean xgetAutoFilterDateGrouping();
    
    boolean isSetAutoFilterDateGrouping();
    
    void setAutoFilterDateGrouping(final boolean p0);
    
    void xsetAutoFilterDateGrouping(final XmlBoolean p0);
    
    void unsetAutoFilterDateGrouping();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBookView.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBookView newInstance() {
            return (CTBookView)getTypeLoader().newInstance(CTBookView.type, (XmlOptions)null);
        }
        
        public static CTBookView newInstance(final XmlOptions xmlOptions) {
            return (CTBookView)getTypeLoader().newInstance(CTBookView.type, xmlOptions);
        }
        
        public static CTBookView parse(final String s) throws XmlException {
            return (CTBookView)getTypeLoader().parse(s, CTBookView.type, (XmlOptions)null);
        }
        
        public static CTBookView parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookView)getTypeLoader().parse(s, CTBookView.type, xmlOptions);
        }
        
        public static CTBookView parse(final File file) throws XmlException, IOException {
            return (CTBookView)getTypeLoader().parse(file, CTBookView.type, (XmlOptions)null);
        }
        
        public static CTBookView parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookView)getTypeLoader().parse(file, CTBookView.type, xmlOptions);
        }
        
        public static CTBookView parse(final URL url) throws XmlException, IOException {
            return (CTBookView)getTypeLoader().parse(url, CTBookView.type, (XmlOptions)null);
        }
        
        public static CTBookView parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookView)getTypeLoader().parse(url, CTBookView.type, xmlOptions);
        }
        
        public static CTBookView parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBookView)getTypeLoader().parse(inputStream, CTBookView.type, (XmlOptions)null);
        }
        
        public static CTBookView parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookView)getTypeLoader().parse(inputStream, CTBookView.type, xmlOptions);
        }
        
        public static CTBookView parse(final Reader reader) throws XmlException, IOException {
            return (CTBookView)getTypeLoader().parse(reader, CTBookView.type, (XmlOptions)null);
        }
        
        public static CTBookView parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBookView)getTypeLoader().parse(reader, CTBookView.type, xmlOptions);
        }
        
        public static CTBookView parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBookView)getTypeLoader().parse(xmlStreamReader, CTBookView.type, (XmlOptions)null);
        }
        
        public static CTBookView parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookView)getTypeLoader().parse(xmlStreamReader, CTBookView.type, xmlOptions);
        }
        
        public static CTBookView parse(final Node node) throws XmlException {
            return (CTBookView)getTypeLoader().parse(node, CTBookView.type, (XmlOptions)null);
        }
        
        public static CTBookView parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBookView)getTypeLoader().parse(node, CTBookView.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBookView parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBookView)getTypeLoader().parse(xmlInputStream, CTBookView.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBookView parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBookView)getTypeLoader().parse(xmlInputStream, CTBookView.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBookView.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBookView.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
