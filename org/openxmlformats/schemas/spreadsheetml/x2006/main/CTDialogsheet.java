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

public interface CTDialogsheet extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDialogsheet.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdialogsheet6f36type");
    
    CTSheetPr getSheetPr();
    
    boolean isSetSheetPr();
    
    void setSheetPr(final CTSheetPr p0);
    
    CTSheetPr addNewSheetPr();
    
    void unsetSheetPr();
    
    CTSheetViews getSheetViews();
    
    boolean isSetSheetViews();
    
    void setSheetViews(final CTSheetViews p0);
    
    CTSheetViews addNewSheetViews();
    
    void unsetSheetViews();
    
    CTSheetFormatPr getSheetFormatPr();
    
    boolean isSetSheetFormatPr();
    
    void setSheetFormatPr(final CTSheetFormatPr p0);
    
    CTSheetFormatPr addNewSheetFormatPr();
    
    void unsetSheetFormatPr();
    
    CTSheetProtection getSheetProtection();
    
    boolean isSetSheetProtection();
    
    void setSheetProtection(final CTSheetProtection p0);
    
    CTSheetProtection addNewSheetProtection();
    
    void unsetSheetProtection();
    
    CTCustomSheetViews getCustomSheetViews();
    
    boolean isSetCustomSheetViews();
    
    void setCustomSheetViews(final CTCustomSheetViews p0);
    
    CTCustomSheetViews addNewCustomSheetViews();
    
    void unsetCustomSheetViews();
    
    CTPrintOptions getPrintOptions();
    
    boolean isSetPrintOptions();
    
    void setPrintOptions(final CTPrintOptions p0);
    
    CTPrintOptions addNewPrintOptions();
    
    void unsetPrintOptions();
    
    CTPageMargins getPageMargins();
    
    boolean isSetPageMargins();
    
    void setPageMargins(final CTPageMargins p0);
    
    CTPageMargins addNewPageMargins();
    
    void unsetPageMargins();
    
    CTPageSetup getPageSetup();
    
    boolean isSetPageSetup();
    
    void setPageSetup(final CTPageSetup p0);
    
    CTPageSetup addNewPageSetup();
    
    void unsetPageSetup();
    
    CTHeaderFooter getHeaderFooter();
    
    boolean isSetHeaderFooter();
    
    void setHeaderFooter(final CTHeaderFooter p0);
    
    CTHeaderFooter addNewHeaderFooter();
    
    void unsetHeaderFooter();
    
    CTDrawing getDrawing();
    
    boolean isSetDrawing();
    
    void setDrawing(final CTDrawing p0);
    
    CTDrawing addNewDrawing();
    
    void unsetDrawing();
    
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
    
    CTOleObjects getOleObjects();
    
    boolean isSetOleObjects();
    
    void setOleObjects(final CTOleObjects p0);
    
    CTOleObjects addNewOleObjects();
    
    void unsetOleObjects();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDialogsheet.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDialogsheet newInstance() {
            return (CTDialogsheet)getTypeLoader().newInstance(CTDialogsheet.type, (XmlOptions)null);
        }
        
        public static CTDialogsheet newInstance(final XmlOptions xmlOptions) {
            return (CTDialogsheet)getTypeLoader().newInstance(CTDialogsheet.type, xmlOptions);
        }
        
        public static CTDialogsheet parse(final String s) throws XmlException {
            return (CTDialogsheet)getTypeLoader().parse(s, CTDialogsheet.type, (XmlOptions)null);
        }
        
        public static CTDialogsheet parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDialogsheet)getTypeLoader().parse(s, CTDialogsheet.type, xmlOptions);
        }
        
        public static CTDialogsheet parse(final File file) throws XmlException, IOException {
            return (CTDialogsheet)getTypeLoader().parse(file, CTDialogsheet.type, (XmlOptions)null);
        }
        
        public static CTDialogsheet parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDialogsheet)getTypeLoader().parse(file, CTDialogsheet.type, xmlOptions);
        }
        
        public static CTDialogsheet parse(final URL url) throws XmlException, IOException {
            return (CTDialogsheet)getTypeLoader().parse(url, CTDialogsheet.type, (XmlOptions)null);
        }
        
        public static CTDialogsheet parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDialogsheet)getTypeLoader().parse(url, CTDialogsheet.type, xmlOptions);
        }
        
        public static CTDialogsheet parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDialogsheet)getTypeLoader().parse(inputStream, CTDialogsheet.type, (XmlOptions)null);
        }
        
        public static CTDialogsheet parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDialogsheet)getTypeLoader().parse(inputStream, CTDialogsheet.type, xmlOptions);
        }
        
        public static CTDialogsheet parse(final Reader reader) throws XmlException, IOException {
            return (CTDialogsheet)getTypeLoader().parse(reader, CTDialogsheet.type, (XmlOptions)null);
        }
        
        public static CTDialogsheet parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDialogsheet)getTypeLoader().parse(reader, CTDialogsheet.type, xmlOptions);
        }
        
        public static CTDialogsheet parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDialogsheet)getTypeLoader().parse(xmlStreamReader, CTDialogsheet.type, (XmlOptions)null);
        }
        
        public static CTDialogsheet parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDialogsheet)getTypeLoader().parse(xmlStreamReader, CTDialogsheet.type, xmlOptions);
        }
        
        public static CTDialogsheet parse(final Node node) throws XmlException {
            return (CTDialogsheet)getTypeLoader().parse(node, CTDialogsheet.type, (XmlOptions)null);
        }
        
        public static CTDialogsheet parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDialogsheet)getTypeLoader().parse(node, CTDialogsheet.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDialogsheet parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDialogsheet)getTypeLoader().parse(xmlInputStream, CTDialogsheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDialogsheet parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDialogsheet)getTypeLoader().parse(xmlInputStream, CTDialogsheet.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDialogsheet.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDialogsheet.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
