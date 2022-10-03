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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlString;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface RowType extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(RowType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("rowtype03d1type");
    
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
    
    String getN();
    
    XmlString xgetN();
    
    boolean isSetN();
    
    void setN(final String p0);
    
    void xsetN(final XmlString p0);
    
    void unsetN();
    
    String getLocalName();
    
    XmlString xgetLocalName();
    
    boolean isSetLocalName();
    
    void setLocalName(final String p0);
    
    void xsetLocalName(final XmlString p0);
    
    void unsetLocalName();
    
    long getIX();
    
    XmlUnsignedInt xgetIX();
    
    boolean isSetIX();
    
    void setIX(final long p0);
    
    void xsetIX(final XmlUnsignedInt p0);
    
    void unsetIX();
    
    String getT();
    
    XmlString xgetT();
    
    boolean isSetT();
    
    void setT(final String p0);
    
    void xsetT(final XmlString p0);
    
    void unsetT();
    
    boolean getDel();
    
    XmlBoolean xgetDel();
    
    boolean isSetDel();
    
    void setDel(final boolean p0);
    
    void xsetDel(final XmlBoolean p0);
    
    void unsetDel();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(RowType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static RowType newInstance() {
            return (RowType)getTypeLoader().newInstance(RowType.type, (XmlOptions)null);
        }
        
        public static RowType newInstance(final XmlOptions xmlOptions) {
            return (RowType)getTypeLoader().newInstance(RowType.type, xmlOptions);
        }
        
        public static RowType parse(final String s) throws XmlException {
            return (RowType)getTypeLoader().parse(s, RowType.type, (XmlOptions)null);
        }
        
        public static RowType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (RowType)getTypeLoader().parse(s, RowType.type, xmlOptions);
        }
        
        public static RowType parse(final File file) throws XmlException, IOException {
            return (RowType)getTypeLoader().parse(file, RowType.type, (XmlOptions)null);
        }
        
        public static RowType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RowType)getTypeLoader().parse(file, RowType.type, xmlOptions);
        }
        
        public static RowType parse(final URL url) throws XmlException, IOException {
            return (RowType)getTypeLoader().parse(url, RowType.type, (XmlOptions)null);
        }
        
        public static RowType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RowType)getTypeLoader().parse(url, RowType.type, xmlOptions);
        }
        
        public static RowType parse(final InputStream inputStream) throws XmlException, IOException {
            return (RowType)getTypeLoader().parse(inputStream, RowType.type, (XmlOptions)null);
        }
        
        public static RowType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RowType)getTypeLoader().parse(inputStream, RowType.type, xmlOptions);
        }
        
        public static RowType parse(final Reader reader) throws XmlException, IOException {
            return (RowType)getTypeLoader().parse(reader, RowType.type, (XmlOptions)null);
        }
        
        public static RowType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (RowType)getTypeLoader().parse(reader, RowType.type, xmlOptions);
        }
        
        public static RowType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (RowType)getTypeLoader().parse(xmlStreamReader, RowType.type, (XmlOptions)null);
        }
        
        public static RowType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (RowType)getTypeLoader().parse(xmlStreamReader, RowType.type, xmlOptions);
        }
        
        public static RowType parse(final Node node) throws XmlException {
            return (RowType)getTypeLoader().parse(node, RowType.type, (XmlOptions)null);
        }
        
        public static RowType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (RowType)getTypeLoader().parse(node, RowType.type, xmlOptions);
        }
        
        @Deprecated
        public static RowType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (RowType)getTypeLoader().parse(xmlInputStream, RowType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static RowType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (RowType)getTypeLoader().parse(xmlInputStream, RowType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, RowType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, RowType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
