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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface SheetType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(SheetType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sheettype25actype");
    
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
    
    List<SectionType> getSectionList();
    
    @Deprecated
    SectionType[] getSectionArray();
    
    SectionType getSectionArray(final int p0);
    
    int sizeOfSectionArray();
    
    void setSectionArray(final SectionType[] p0);
    
    void setSectionArray(final int p0, final SectionType p1);
    
    SectionType insertNewSection(final int p0);
    
    SectionType addNewSection();
    
    void removeSection(final int p0);
    
    long getLineStyle();
    
    XmlUnsignedInt xgetLineStyle();
    
    boolean isSetLineStyle();
    
    void setLineStyle(final long p0);
    
    void xsetLineStyle(final XmlUnsignedInt p0);
    
    void unsetLineStyle();
    
    long getFillStyle();
    
    XmlUnsignedInt xgetFillStyle();
    
    boolean isSetFillStyle();
    
    void setFillStyle(final long p0);
    
    void xsetFillStyle(final XmlUnsignedInt p0);
    
    void unsetFillStyle();
    
    long getTextStyle();
    
    XmlUnsignedInt xgetTextStyle();
    
    boolean isSetTextStyle();
    
    void setTextStyle(final long p0);
    
    void xsetTextStyle(final XmlUnsignedInt p0);
    
    void unsetTextStyle();
    
    public static final class Factory
    {
        @Deprecated
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(SheetType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static SheetType newInstance() {
            return (SheetType)getTypeLoader().newInstance(SheetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SheetType newInstance(final XmlOptions xmlOptions) {
            return (SheetType)getTypeLoader().newInstance(SheetType.type, xmlOptions);
        }
        
        public static SheetType parse(final String s) throws XmlException {
            return (SheetType)getTypeLoader().parse(s, SheetType.type, (XmlOptions)null);
        }
        
        public static SheetType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (SheetType)getTypeLoader().parse(s, SheetType.type, xmlOptions);
        }
        
        public static SheetType parse(final File file) throws XmlException, IOException {
            return (SheetType)getTypeLoader().parse(file, SheetType.type, (XmlOptions)null);
        }
        
        public static SheetType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SheetType)getTypeLoader().parse(file, SheetType.type, xmlOptions);
        }
        
        public static SheetType parse(final URL url) throws XmlException, IOException {
            return (SheetType)getTypeLoader().parse(url, SheetType.type, (XmlOptions)null);
        }
        
        public static SheetType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SheetType)getTypeLoader().parse(url, SheetType.type, xmlOptions);
        }
        
        public static SheetType parse(final InputStream inputStream) throws XmlException, IOException {
            return (SheetType)getTypeLoader().parse(inputStream, SheetType.type, (XmlOptions)null);
        }
        
        public static SheetType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SheetType)getTypeLoader().parse(inputStream, SheetType.type, xmlOptions);
        }
        
        public static SheetType parse(final Reader reader) throws XmlException, IOException {
            return (SheetType)getTypeLoader().parse(reader, SheetType.type, (XmlOptions)null);
        }
        
        public static SheetType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (SheetType)getTypeLoader().parse(reader, SheetType.type, xmlOptions);
        }
        
        public static SheetType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (SheetType)getTypeLoader().parse(xmlStreamReader, SheetType.type, (XmlOptions)null);
        }
        
        public static SheetType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (SheetType)getTypeLoader().parse(xmlStreamReader, SheetType.type, xmlOptions);
        }
        
        public static SheetType parse(final Node node) throws XmlException {
            return (SheetType)getTypeLoader().parse(node, SheetType.type, (XmlOptions)null);
        }
        
        public static SheetType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (SheetType)getTypeLoader().parse(node, SheetType.type, xmlOptions);
        }
        
        @Deprecated
        public static SheetType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (SheetType)getTypeLoader().parse(xmlInputStream, SheetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static SheetType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (SheetType)getTypeLoader().parse(xmlInputStream, SheetType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SheetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, SheetType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
