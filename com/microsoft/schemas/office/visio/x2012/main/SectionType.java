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
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlString;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface SectionType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SectionType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sectiontype30a6type");
    
    List<CellType> getCellList();
    
    @Deprecated
    CellType[] getCellArray();
    
    CellType getCellArray(final int p0);
    
    int sizeOfCellArray();
    
    void setCellArray(final CellType[] p0);
    
    void setCellArray(final int p0, final CellType p1);
    
    CellType insertNewCell(final int p0);
    
    CellType addNewCell();
    
    void removeCell(final int p0);
    
    List<TriggerType> getTriggerList();
    
    @Deprecated
    TriggerType[] getTriggerArray();
    
    TriggerType getTriggerArray(final int p0);
    
    int sizeOfTriggerArray();
    
    void setTriggerArray(final TriggerType[] p0);
    
    void setTriggerArray(final int p0, final TriggerType p1);
    
    TriggerType insertNewTrigger(final int p0);
    
    TriggerType addNewTrigger();
    
    void removeTrigger(final int p0);
    
    List<RowType> getRowList();
    
    @Deprecated
    RowType[] getRowArray();
    
    RowType getRowArray(final int p0);
    
    int sizeOfRowArray();
    
    void setRowArray(final RowType[] p0);
    
    void setRowArray(final int p0, final RowType p1);
    
    RowType insertNewRow(final int p0);
    
    RowType addNewRow();
    
    void removeRow(final int p0);
    
    String getN();
    
    XmlString xgetN();
    
    void setN(final String p0);
    
    void xsetN(final XmlString p0);
    
    boolean getDel();
    
    XmlBoolean xgetDel();
    
    boolean isSetDel();
    
    void setDel(final boolean p0);
    
    void xsetDel(final XmlBoolean p0);
    
    void unsetDel();
    
    long getIX();
    
    XmlUnsignedInt xgetIX();
    
    boolean isSetIX();
    
    void setIX(final long p0);
    
    void xsetIX(final XmlUnsignedInt p0);
    
    void unsetIX();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SectionType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SectionType newInstance() {
            return (SectionType)getTypeLoader().newInstance(SectionType.type, (XmlOptions)null);
        }
        
        public static SectionType newInstance(final XmlOptions xmlOptions) {
            return (SectionType)getTypeLoader().newInstance(SectionType.type, xmlOptions);
        }
        
        public static SectionType parse(final String s) throws XmlException {
            return (SectionType)getTypeLoader().parse(s, SectionType.type, (XmlOptions)null);
        }
        
        public static SectionType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SectionType)getTypeLoader().parse(s, SectionType.type, xmlOptions);
        }
        
        public static SectionType parse(final File file) throws XmlException, IOException {
            return (SectionType)getTypeLoader().parse(file, SectionType.type, (XmlOptions)null);
        }
        
        public static SectionType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SectionType)getTypeLoader().parse(file, SectionType.type, xmlOptions);
        }
        
        public static SectionType parse(final URL url) throws XmlException, IOException {
            return (SectionType)getTypeLoader().parse(url, SectionType.type, (XmlOptions)null);
        }
        
        public static SectionType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SectionType)getTypeLoader().parse(url, SectionType.type, xmlOptions);
        }
        
        public static SectionType parse(final InputStream inputStream) throws XmlException, IOException {
            return (SectionType)getTypeLoader().parse(inputStream, SectionType.type, (XmlOptions)null);
        }
        
        public static SectionType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SectionType)getTypeLoader().parse(inputStream, SectionType.type, xmlOptions);
        }
        
        public static SectionType parse(final Reader reader) throws XmlException, IOException {
            return (SectionType)getTypeLoader().parse(reader, SectionType.type, (XmlOptions)null);
        }
        
        public static SectionType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SectionType)getTypeLoader().parse(reader, SectionType.type, xmlOptions);
        }
        
        public static SectionType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SectionType)getTypeLoader().parse(xmlStreamReader, SectionType.type, (XmlOptions)null);
        }
        
        public static SectionType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SectionType)getTypeLoader().parse(xmlStreamReader, SectionType.type, xmlOptions);
        }
        
        public static SectionType parse(final Node node) throws XmlException {
            return (SectionType)getTypeLoader().parse(node, SectionType.type, (XmlOptions)null);
        }
        
        public static SectionType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SectionType)getTypeLoader().parse(node, SectionType.type, xmlOptions);
        }
        
        @Deprecated
        public static SectionType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SectionType)getTypeLoader().parse(xmlInputStream, SectionType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SectionType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SectionType)getTypeLoader().parse(xmlInputStream, SectionType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SectionType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SectionType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
