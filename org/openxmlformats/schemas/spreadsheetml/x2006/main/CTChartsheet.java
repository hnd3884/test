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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTChartsheet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTChartsheet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctchartsheetf68atype");
    
    CTChartsheetPr getSheetPr();
    
    boolean isSetSheetPr();
    
    void setSheetPr(final CTChartsheetPr p0);
    
    CTChartsheetPr addNewSheetPr();
    
    void unsetSheetPr();
    
    CTChartsheetViews getSheetViews();
    
    void setSheetViews(final CTChartsheetViews p0);
    
    CTChartsheetViews addNewSheetViews();
    
    CTChartsheetProtection getSheetProtection();
    
    boolean isSetSheetProtection();
    
    void setSheetProtection(final CTChartsheetProtection p0);
    
    CTChartsheetProtection addNewSheetProtection();
    
    void unsetSheetProtection();
    
    CTCustomChartsheetViews getCustomSheetViews();
    
    boolean isSetCustomSheetViews();
    
    void setCustomSheetViews(final CTCustomChartsheetViews p0);
    
    CTCustomChartsheetViews addNewCustomSheetViews();
    
    void unsetCustomSheetViews();
    
    CTPageMargins getPageMargins();
    
    boolean isSetPageMargins();
    
    void setPageMargins(final CTPageMargins p0);
    
    CTPageMargins addNewPageMargins();
    
    void unsetPageMargins();
    
    CTCsPageSetup getPageSetup();
    
    boolean isSetPageSetup();
    
    void setPageSetup(final CTCsPageSetup p0);
    
    CTCsPageSetup addNewPageSetup();
    
    void unsetPageSetup();
    
    CTHeaderFooter getHeaderFooter();
    
    boolean isSetHeaderFooter();
    
    void setHeaderFooter(final CTHeaderFooter p0);
    
    CTHeaderFooter addNewHeaderFooter();
    
    void unsetHeaderFooter();
    
    CTDrawing getDrawing();
    
    void setDrawing(final CTDrawing p0);
    
    CTDrawing addNewDrawing();
    
    CTLegacyDrawing getLegacyDrawing();
    
    boolean isSetLegacyDrawing();
    
    void setLegacyDrawing(final CTLegacyDrawing p0);
    
    CTLegacyDrawing addNewLegacyDrawing();
    
    void unsetLegacyDrawing();
    
    CTLegacyDrawing getLegacyDrawingHF();
    
    boolean isSetLegacyDrawingHF();
    
    void setLegacyDrawingHF(final CTLegacyDrawing p0);
    
    CTLegacyDrawing addNewLegacyDrawingHF();
    
    void unsetLegacyDrawingHF();
    
    CTSheetBackgroundPicture getPicture();
    
    boolean isSetPicture();
    
    void setPicture(final CTSheetBackgroundPicture p0);
    
    CTSheetBackgroundPicture addNewPicture();
    
    void unsetPicture();
    
    CTWebPublishItems getWebPublishItems();
    
    boolean isSetWebPublishItems();
    
    void setWebPublishItems(final CTWebPublishItems p0);
    
    CTWebPublishItems addNewWebPublishItems();
    
    void unsetWebPublishItems();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTChartsheet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTChartsheet newInstance() {
            return (CTChartsheet)getTypeLoader().newInstance(CTChartsheet.type, (XmlOptions)null);
        }
        
        public static CTChartsheet newInstance(final XmlOptions xmlOptions) {
            return (CTChartsheet)getTypeLoader().newInstance(CTChartsheet.type, xmlOptions);
        }
        
        public static CTChartsheet parse(final String s) throws XmlException {
            return (CTChartsheet)getTypeLoader().parse(s, CTChartsheet.type, (XmlOptions)null);
        }
        
        public static CTChartsheet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTChartsheet)getTypeLoader().parse(s, CTChartsheet.type, xmlOptions);
        }
        
        public static CTChartsheet parse(final File file) throws XmlException, IOException {
            return (CTChartsheet)getTypeLoader().parse(file, CTChartsheet.type, (XmlOptions)null);
        }
        
        public static CTChartsheet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartsheet)getTypeLoader().parse(file, CTChartsheet.type, xmlOptions);
        }
        
        public static CTChartsheet parse(final URL url) throws XmlException, IOException {
            return (CTChartsheet)getTypeLoader().parse(url, CTChartsheet.type, (XmlOptions)null);
        }
        
        public static CTChartsheet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartsheet)getTypeLoader().parse(url, CTChartsheet.type, xmlOptions);
        }
        
        public static CTChartsheet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTChartsheet)getTypeLoader().parse(inputStream, CTChartsheet.type, (XmlOptions)null);
        }
        
        public static CTChartsheet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartsheet)getTypeLoader().parse(inputStream, CTChartsheet.type, xmlOptions);
        }
        
        public static CTChartsheet parse(final Reader reader) throws XmlException, IOException {
            return (CTChartsheet)getTypeLoader().parse(reader, CTChartsheet.type, (XmlOptions)null);
        }
        
        public static CTChartsheet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartsheet)getTypeLoader().parse(reader, CTChartsheet.type, xmlOptions);
        }
        
        public static CTChartsheet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTChartsheet)getTypeLoader().parse(xmlStreamReader, CTChartsheet.type, (XmlOptions)null);
        }
        
        public static CTChartsheet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTChartsheet)getTypeLoader().parse(xmlStreamReader, CTChartsheet.type, xmlOptions);
        }
        
        public static CTChartsheet parse(final Node node) throws XmlException {
            return (CTChartsheet)getTypeLoader().parse(node, CTChartsheet.type, (XmlOptions)null);
        }
        
        public static CTChartsheet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTChartsheet)getTypeLoader().parse(node, CTChartsheet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTChartsheet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTChartsheet)getTypeLoader().parse(xmlInputStream, CTChartsheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTChartsheet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTChartsheet)getTypeLoader().parse(xmlInputStream, CTChartsheet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTChartsheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTChartsheet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
