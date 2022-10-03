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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTableColumn extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTableColumn.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttablecolumn08a3type");
    
    CTTableFormula getCalculatedColumnFormula();
    
    boolean isSetCalculatedColumnFormula();
    
    void setCalculatedColumnFormula(final CTTableFormula p0);
    
    CTTableFormula addNewCalculatedColumnFormula();
    
    void unsetCalculatedColumnFormula();
    
    CTTableFormula getTotalsRowFormula();
    
    boolean isSetTotalsRowFormula();
    
    void setTotalsRowFormula(final CTTableFormula p0);
    
    CTTableFormula addNewTotalsRowFormula();
    
    void unsetTotalsRowFormula();
    
    CTXmlColumnPr getXmlColumnPr();
    
    boolean isSetXmlColumnPr();
    
    void setXmlColumnPr(final CTXmlColumnPr p0);
    
    CTXmlColumnPr addNewXmlColumnPr();
    
    void unsetXmlColumnPr();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    long getId();
    
    XmlUnsignedInt xgetId();
    
    void setId(final long p0);
    
    void xsetId(final XmlUnsignedInt p0);
    
    String getUniqueName();
    
    STXstring xgetUniqueName();
    
    boolean isSetUniqueName();
    
    void setUniqueName(final String p0);
    
    void xsetUniqueName(final STXstring p0);
    
    void unsetUniqueName();
    
    String getName();
    
    STXstring xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STXstring p0);
    
    STTotalsRowFunction.Enum getTotalsRowFunction();
    
    STTotalsRowFunction xgetTotalsRowFunction();
    
    boolean isSetTotalsRowFunction();
    
    void setTotalsRowFunction(final STTotalsRowFunction.Enum p0);
    
    void xsetTotalsRowFunction(final STTotalsRowFunction p0);
    
    void unsetTotalsRowFunction();
    
    String getTotalsRowLabel();
    
    STXstring xgetTotalsRowLabel();
    
    boolean isSetTotalsRowLabel();
    
    void setTotalsRowLabel(final String p0);
    
    void xsetTotalsRowLabel(final STXstring p0);
    
    void unsetTotalsRowLabel();
    
    long getQueryTableFieldId();
    
    XmlUnsignedInt xgetQueryTableFieldId();
    
    boolean isSetQueryTableFieldId();
    
    void setQueryTableFieldId(final long p0);
    
    void xsetQueryTableFieldId(final XmlUnsignedInt p0);
    
    void unsetQueryTableFieldId();
    
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
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTableColumn.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTableColumn newInstance() {
            return (CTTableColumn)getTypeLoader().newInstance(CTTableColumn.type, (XmlOptions)null);
        }
        
        public static CTTableColumn newInstance(final XmlOptions xmlOptions) {
            return (CTTableColumn)getTypeLoader().newInstance(CTTableColumn.type, xmlOptions);
        }
        
        public static CTTableColumn parse(final String s) throws XmlException {
            return (CTTableColumn)getTypeLoader().parse(s, CTTableColumn.type, (XmlOptions)null);
        }
        
        public static CTTableColumn parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableColumn)getTypeLoader().parse(s, CTTableColumn.type, xmlOptions);
        }
        
        public static CTTableColumn parse(final File file) throws XmlException, IOException {
            return (CTTableColumn)getTypeLoader().parse(file, CTTableColumn.type, (XmlOptions)null);
        }
        
        public static CTTableColumn parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableColumn)getTypeLoader().parse(file, CTTableColumn.type, xmlOptions);
        }
        
        public static CTTableColumn parse(final URL url) throws XmlException, IOException {
            return (CTTableColumn)getTypeLoader().parse(url, CTTableColumn.type, (XmlOptions)null);
        }
        
        public static CTTableColumn parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableColumn)getTypeLoader().parse(url, CTTableColumn.type, xmlOptions);
        }
        
        public static CTTableColumn parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTableColumn)getTypeLoader().parse(inputStream, CTTableColumn.type, (XmlOptions)null);
        }
        
        public static CTTableColumn parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableColumn)getTypeLoader().parse(inputStream, CTTableColumn.type, xmlOptions);
        }
        
        public static CTTableColumn parse(final Reader reader) throws XmlException, IOException {
            return (CTTableColumn)getTypeLoader().parse(reader, CTTableColumn.type, (XmlOptions)null);
        }
        
        public static CTTableColumn parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTableColumn)getTypeLoader().parse(reader, CTTableColumn.type, xmlOptions);
        }
        
        public static CTTableColumn parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTableColumn)getTypeLoader().parse(xmlStreamReader, CTTableColumn.type, (XmlOptions)null);
        }
        
        public static CTTableColumn parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableColumn)getTypeLoader().parse(xmlStreamReader, CTTableColumn.type, xmlOptions);
        }
        
        public static CTTableColumn parse(final Node node) throws XmlException {
            return (CTTableColumn)getTypeLoader().parse(node, CTTableColumn.type, (XmlOptions)null);
        }
        
        public static CTTableColumn parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTableColumn)getTypeLoader().parse(node, CTTableColumn.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTableColumn parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTableColumn)getTypeLoader().parse(xmlInputStream, CTTableColumn.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTableColumn parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTableColumn)getTypeLoader().parse(xmlInputStream, CTTableColumn.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableColumn.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTableColumn.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
