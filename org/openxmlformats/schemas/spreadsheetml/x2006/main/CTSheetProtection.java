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

public interface CTSheetProtection extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSheetProtection.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsheetprotection22f7type");
    
    byte[] getPassword();
    
    STUnsignedShortHex xgetPassword();
    
    boolean isSetPassword();
    
    void setPassword(final byte[] p0);
    
    void xsetPassword(final STUnsignedShortHex p0);
    
    void unsetPassword();
    
    boolean getSheet();
    
    XmlBoolean xgetSheet();
    
    boolean isSetSheet();
    
    void setSheet(final boolean p0);
    
    void xsetSheet(final XmlBoolean p0);
    
    void unsetSheet();
    
    boolean getObjects();
    
    XmlBoolean xgetObjects();
    
    boolean isSetObjects();
    
    void setObjects(final boolean p0);
    
    void xsetObjects(final XmlBoolean p0);
    
    void unsetObjects();
    
    boolean getScenarios();
    
    XmlBoolean xgetScenarios();
    
    boolean isSetScenarios();
    
    void setScenarios(final boolean p0);
    
    void xsetScenarios(final XmlBoolean p0);
    
    void unsetScenarios();
    
    boolean getFormatCells();
    
    XmlBoolean xgetFormatCells();
    
    boolean isSetFormatCells();
    
    void setFormatCells(final boolean p0);
    
    void xsetFormatCells(final XmlBoolean p0);
    
    void unsetFormatCells();
    
    boolean getFormatColumns();
    
    XmlBoolean xgetFormatColumns();
    
    boolean isSetFormatColumns();
    
    void setFormatColumns(final boolean p0);
    
    void xsetFormatColumns(final XmlBoolean p0);
    
    void unsetFormatColumns();
    
    boolean getFormatRows();
    
    XmlBoolean xgetFormatRows();
    
    boolean isSetFormatRows();
    
    void setFormatRows(final boolean p0);
    
    void xsetFormatRows(final XmlBoolean p0);
    
    void unsetFormatRows();
    
    boolean getInsertColumns();
    
    XmlBoolean xgetInsertColumns();
    
    boolean isSetInsertColumns();
    
    void setInsertColumns(final boolean p0);
    
    void xsetInsertColumns(final XmlBoolean p0);
    
    void unsetInsertColumns();
    
    boolean getInsertRows();
    
    XmlBoolean xgetInsertRows();
    
    boolean isSetInsertRows();
    
    void setInsertRows(final boolean p0);
    
    void xsetInsertRows(final XmlBoolean p0);
    
    void unsetInsertRows();
    
    boolean getInsertHyperlinks();
    
    XmlBoolean xgetInsertHyperlinks();
    
    boolean isSetInsertHyperlinks();
    
    void setInsertHyperlinks(final boolean p0);
    
    void xsetInsertHyperlinks(final XmlBoolean p0);
    
    void unsetInsertHyperlinks();
    
    boolean getDeleteColumns();
    
    XmlBoolean xgetDeleteColumns();
    
    boolean isSetDeleteColumns();
    
    void setDeleteColumns(final boolean p0);
    
    void xsetDeleteColumns(final XmlBoolean p0);
    
    void unsetDeleteColumns();
    
    boolean getDeleteRows();
    
    XmlBoolean xgetDeleteRows();
    
    boolean isSetDeleteRows();
    
    void setDeleteRows(final boolean p0);
    
    void xsetDeleteRows(final XmlBoolean p0);
    
    void unsetDeleteRows();
    
    boolean getSelectLockedCells();
    
    XmlBoolean xgetSelectLockedCells();
    
    boolean isSetSelectLockedCells();
    
    void setSelectLockedCells(final boolean p0);
    
    void xsetSelectLockedCells(final XmlBoolean p0);
    
    void unsetSelectLockedCells();
    
    boolean getSort();
    
    XmlBoolean xgetSort();
    
    boolean isSetSort();
    
    void setSort(final boolean p0);
    
    void xsetSort(final XmlBoolean p0);
    
    void unsetSort();
    
    boolean getAutoFilter();
    
    XmlBoolean xgetAutoFilter();
    
    boolean isSetAutoFilter();
    
    void setAutoFilter(final boolean p0);
    
    void xsetAutoFilter(final XmlBoolean p0);
    
    void unsetAutoFilter();
    
    boolean getPivotTables();
    
    XmlBoolean xgetPivotTables();
    
    boolean isSetPivotTables();
    
    void setPivotTables(final boolean p0);
    
    void xsetPivotTables(final XmlBoolean p0);
    
    void unsetPivotTables();
    
    boolean getSelectUnlockedCells();
    
    XmlBoolean xgetSelectUnlockedCells();
    
    boolean isSetSelectUnlockedCells();
    
    void setSelectUnlockedCells(final boolean p0);
    
    void xsetSelectUnlockedCells(final XmlBoolean p0);
    
    void unsetSelectUnlockedCells();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSheetProtection.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSheetProtection newInstance() {
            return (CTSheetProtection)getTypeLoader().newInstance(CTSheetProtection.type, (XmlOptions)null);
        }
        
        public static CTSheetProtection newInstance(final XmlOptions xmlOptions) {
            return (CTSheetProtection)getTypeLoader().newInstance(CTSheetProtection.type, xmlOptions);
        }
        
        public static CTSheetProtection parse(final String s) throws XmlException {
            return (CTSheetProtection)getTypeLoader().parse(s, CTSheetProtection.type, (XmlOptions)null);
        }
        
        public static CTSheetProtection parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetProtection)getTypeLoader().parse(s, CTSheetProtection.type, xmlOptions);
        }
        
        public static CTSheetProtection parse(final File file) throws XmlException, IOException {
            return (CTSheetProtection)getTypeLoader().parse(file, CTSheetProtection.type, (XmlOptions)null);
        }
        
        public static CTSheetProtection parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetProtection)getTypeLoader().parse(file, CTSheetProtection.type, xmlOptions);
        }
        
        public static CTSheetProtection parse(final URL url) throws XmlException, IOException {
            return (CTSheetProtection)getTypeLoader().parse(url, CTSheetProtection.type, (XmlOptions)null);
        }
        
        public static CTSheetProtection parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetProtection)getTypeLoader().parse(url, CTSheetProtection.type, xmlOptions);
        }
        
        public static CTSheetProtection parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSheetProtection)getTypeLoader().parse(inputStream, CTSheetProtection.type, (XmlOptions)null);
        }
        
        public static CTSheetProtection parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetProtection)getTypeLoader().parse(inputStream, CTSheetProtection.type, xmlOptions);
        }
        
        public static CTSheetProtection parse(final Reader reader) throws XmlException, IOException {
            return (CTSheetProtection)getTypeLoader().parse(reader, CTSheetProtection.type, (XmlOptions)null);
        }
        
        public static CTSheetProtection parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSheetProtection)getTypeLoader().parse(reader, CTSheetProtection.type, xmlOptions);
        }
        
        public static CTSheetProtection parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSheetProtection)getTypeLoader().parse(xmlStreamReader, CTSheetProtection.type, (XmlOptions)null);
        }
        
        public static CTSheetProtection parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetProtection)getTypeLoader().parse(xmlStreamReader, CTSheetProtection.type, xmlOptions);
        }
        
        public static CTSheetProtection parse(final Node node) throws XmlException {
            return (CTSheetProtection)getTypeLoader().parse(node, CTSheetProtection.type, (XmlOptions)null);
        }
        
        public static CTSheetProtection parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSheetProtection)getTypeLoader().parse(node, CTSheetProtection.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSheetProtection parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSheetProtection)getTypeLoader().parse(xmlInputStream, CTSheetProtection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSheetProtection parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSheetProtection)getTypeLoader().parse(xmlInputStream, CTSheetProtection.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetProtection.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSheetProtection.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
