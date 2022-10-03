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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTable extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTable.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttable736dtype");
    
    CTAutoFilter getAutoFilter();
    
    boolean isSetAutoFilter();
    
    void setAutoFilter(final CTAutoFilter p0);
    
    CTAutoFilter addNewAutoFilter();
    
    void unsetAutoFilter();
    
    CTSortState getSortState();
    
    boolean isSetSortState();
    
    void setSortState(final CTSortState p0);
    
    CTSortState addNewSortState();
    
    void unsetSortState();
    
    CTTableColumns getTableColumns();
    
    void setTableColumns(final CTTableColumns p0);
    
    CTTableColumns addNewTableColumns();
    
    CTTableStyleInfo getTableStyleInfo();
    
    boolean isSetTableStyleInfo();
    
    void setTableStyleInfo(final CTTableStyleInfo p0);
    
    CTTableStyleInfo addNewTableStyleInfo();
    
    void unsetTableStyleInfo();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getId();
    
    XmlUnsignedInt xgetId();
    
    void setId(final long p0);
    
    void xsetId(final XmlUnsignedInt p0);
    
    String getName();
    
    STXstring xgetName();
    
    boolean isSetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    void unsetName();
    
    String getDisplayName();
    
    STXstring xgetDisplayName();
    
    void setDisplayName(final String p0);
    
    void xsetDisplayName(final STXstring p0);
    
    String getComment();
    
    STXstring xgetComment();
    
    boolean isSetComment();
    
    void setComment(final String p0);
    
    void xsetComment(final STXstring p0);
    
    void unsetComment();
    
    String getRef();
    
    STRef xgetRef();
    
    void setRef(final String p0);
    
    void xsetRef(final STRef p0);
    
    STTableType.Enum getTableType();
    
    STTableType xgetTableType();
    
    boolean isSetTableType();
    
    void setTableType(final STTableType.Enum p0);
    
    void xsetTableType(final STTableType p0);
    
    void unsetTableType();
    
    long getHeaderRowCount();
    
    XmlUnsignedInt xgetHeaderRowCount();
    
    boolean isSetHeaderRowCount();
    
    void setHeaderRowCount(final long p0);
    
    void xsetHeaderRowCount(final XmlUnsignedInt p0);
    
    void unsetHeaderRowCount();
    
    boolean getInsertRow();
    
    XmlBoolean xgetInsertRow();
    
    boolean isSetInsertRow();
    
    void setInsertRow(final boolean p0);
    
    void xsetInsertRow(final XmlBoolean p0);
    
    void unsetInsertRow();
    
    boolean getInsertRowShift();
    
    XmlBoolean xgetInsertRowShift();
    
    boolean isSetInsertRowShift();
    
    void setInsertRowShift(final boolean p0);
    
    void xsetInsertRowShift(final XmlBoolean p0);
    
    void unsetInsertRowShift();
    
    long getTotalsRowCount();
    
    XmlUnsignedInt xgetTotalsRowCount();
    
    boolean isSetTotalsRowCount();
    
    void setTotalsRowCount(final long p0);
    
    void xsetTotalsRowCount(final XmlUnsignedInt p0);
    
    void unsetTotalsRowCount();
    
    boolean getTotalsRowShown();
    
    XmlBoolean xgetTotalsRowShown();
    
    boolean isSetTotalsRowShown();
    
    void setTotalsRowShown(final boolean p0);
    
    void xsetTotalsRowShown(final XmlBoolean p0);
    
    void unsetTotalsRowShown();
    
    boolean getPublished();
    
    XmlBoolean xgetPublished();
    
    boolean isSetPublished();
    
    void setPublished(final boolean p0);
    
    void xsetPublished(final XmlBoolean p0);
    
    void unsetPublished();
    
    long getHeaderRowDxfId();
    
    STDxfId xgetHeaderRowDxfId();
    
    boolean isSetHeaderRowDxfId();
    
    void setHeaderRowDxfId(final long p0);
    
    void xsetHeaderRowDxfId(final STDxfId p0);
    
    void unsetHeaderRowDxfId();
    
    long getDataDxfId();
    
    STDxfId xgetDataDxfId();
    
    boolean isSetDataDxfId();
    
    void setDataDxfId(final long p0);
    
    void xsetDataDxfId(final STDxfId p0);
    
    void unsetDataDxfId();
    
    long getTotalsRowDxfId();
    
    STDxfId xgetTotalsRowDxfId();
    
    boolean isSetTotalsRowDxfId();
    
    void setTotalsRowDxfId(final long p0);
    
    void xsetTotalsRowDxfId(final STDxfId p0);
    
    void unsetTotalsRowDxfId();
    
    long getHeaderRowBorderDxfId();
    
    STDxfId xgetHeaderRowBorderDxfId();
    
    boolean isSetHeaderRowBorderDxfId();
    
    void setHeaderRowBorderDxfId(final long p0);
    
    void xsetHeaderRowBorderDxfId(final STDxfId p0);
    
    void unsetHeaderRowBorderDxfId();
    
    long getTableBorderDxfId();
    
    STDxfId xgetTableBorderDxfId();
    
    boolean isSetTableBorderDxfId();
    
    void setTableBorderDxfId(final long p0);
    
    void xsetTableBorderDxfId(final STDxfId p0);
    
    void unsetTableBorderDxfId();
    
    long getTotalsRowBorderDxfId();
    
    STDxfId xgetTotalsRowBorderDxfId();
    
    boolean isSetTotalsRowBorderDxfId();
    
    void setTotalsRowBorderDxfId(final long p0);
    
    void xsetTotalsRowBorderDxfId(final STDxfId p0);
    
    void unsetTotalsRowBorderDxfId();
    
    String getHeaderRowCellStyle();
    
    STXstring xgetHeaderRowCellStyle();
    
    boolean isSetHeaderRowCellStyle();
    
    void setHeaderRowCellStyle(final String p0);
    
    void xsetHeaderRowCellStyle(final STXstring p0);
    
    void unsetHeaderRowCellStyle();
    
    String getDataCellStyle();
    
    STXstring xgetDataCellStyle();
    
    boolean isSetDataCellStyle();
    
    void setDataCellStyle(final String p0);
    
    void xsetDataCellStyle(final STXstring p0);
    
    void unsetDataCellStyle();
    
    String getTotalsRowCellStyle();
    
    STXstring xgetTotalsRowCellStyle();
    
    boolean isSetTotalsRowCellStyle();
    
    void setTotalsRowCellStyle(final String p0);
    
    void xsetTotalsRowCellStyle(final STXstring p0);
    
    void unsetTotalsRowCellStyle();
    
    long getConnectionId();
    
    XmlUnsignedInt xgetConnectionId();
    
    boolean isSetConnectionId();
    
    void setConnectionId(final long p0);
    
    void xsetConnectionId(final XmlUnsignedInt p0);
    
    void unsetConnectionId();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTable.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTable newInstance() {
            return (CTTable)getTypeLoader().newInstance(CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable newInstance(final XmlOptions xmlOptions) {
            return (CTTable)getTypeLoader().newInstance(CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final String s) throws XmlException {
            return (CTTable)getTypeLoader().parse(s, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTable)getTypeLoader().parse(s, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final File file) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(file, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(file, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final URL url) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(url, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(url, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(inputStream, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(inputStream, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final Reader reader) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(reader, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTable)getTypeLoader().parse(reader, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTable)getTypeLoader().parse(xmlStreamReader, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTable)getTypeLoader().parse(xmlStreamReader, CTTable.type, xmlOptions);
        }
        
        public static CTTable parse(final Node node) throws XmlException {
            return (CTTable)getTypeLoader().parse(node, CTTable.type, (XmlOptions)null);
        }
        
        public static CTTable parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTable)getTypeLoader().parse(node, CTTable.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTable parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTable)getTypeLoader().parse(xmlInputStream, CTTable.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTable parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTable)getTypeLoader().parse(xmlInputStream, CTTable.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTable.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTable.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
