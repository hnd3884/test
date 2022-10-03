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
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;

public interface ShapeSheetType extends SheetType
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(ShapeSheetType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("shapesheettype59bbtype");
    
    TextType getText();
    
    boolean isSetText();
    
    void setText(final TextType p0);
    
    TextType addNewText();
    
    void unsetText();
    
    DataType getData1();
    
    boolean isSetData1();
    
    void setData1(final DataType p0);
    
    DataType addNewData1();
    
    void unsetData1();
    
    DataType getData2();
    
    boolean isSetData2();
    
    void setData2(final DataType p0);
    
    DataType addNewData2();
    
    void unsetData2();
    
    DataType getData3();
    
    boolean isSetData3();
    
    void setData3(final DataType p0);
    
    DataType addNewData3();
    
    void unsetData3();
    
    ForeignDataType getForeignData();
    
    boolean isSetForeignData();
    
    void setForeignData(final ForeignDataType p0);
    
    ForeignDataType addNewForeignData();
    
    void unsetForeignData();
    
    ShapesType getShapes();
    
    boolean isSetShapes();
    
    void setShapes(final ShapesType p0);
    
    ShapesType addNewShapes();
    
    void unsetShapes();
    
    long getID();
    
    XmlUnsignedInt xgetID();
    
    void setID(final long p0);
    
    void xsetID(final XmlUnsignedInt p0);
    
    long getOriginalID();
    
    XmlUnsignedInt xgetOriginalID();
    
    boolean isSetOriginalID();
    
    void setOriginalID(final long p0);
    
    void xsetOriginalID(final XmlUnsignedInt p0);
    
    void unsetOriginalID();
    
    boolean getDel();
    
    XmlBoolean xgetDel();
    
    boolean isSetDel();
    
    void setDel(final boolean p0);
    
    void xsetDel(final XmlBoolean p0);
    
    void unsetDel();
    
    long getMasterShape();
    
    XmlUnsignedInt xgetMasterShape();
    
    boolean isSetMasterShape();
    
    void setMasterShape(final long p0);
    
    void xsetMasterShape(final XmlUnsignedInt p0);
    
    void unsetMasterShape();
    
    String getUniqueID();
    
    XmlString xgetUniqueID();
    
    boolean isSetUniqueID();
    
    void setUniqueID(final String p0);
    
    void xsetUniqueID(final XmlString p0);
    
    void unsetUniqueID();
    
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
    
    long getMaster();
    
    XmlUnsignedInt xgetMaster();
    
    boolean isSetMaster();
    
    void setMaster(final long p0);
    
    void xsetMaster(final XmlUnsignedInt p0);
    
    void unsetMaster();
    
    String getType();
    
    XmlToken xgetType();
    
    boolean isSetType();
    
    void setType(final String p0);
    
    void xsetType(final XmlToken p0);
    
    void unsetType();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(ShapeSheetType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static ShapeSheetType newInstance() {
            return (ShapeSheetType)getTypeLoader().newInstance(ShapeSheetType.type, (XmlOptions)null);
        }
        
        public static ShapeSheetType newInstance(final XmlOptions xmlOptions) {
            return (ShapeSheetType)getTypeLoader().newInstance(ShapeSheetType.type, xmlOptions);
        }
        
        public static ShapeSheetType parse(final String s) throws XmlException {
            return (ShapeSheetType)getTypeLoader().parse(s, ShapeSheetType.type, (XmlOptions)null);
        }
        
        public static ShapeSheetType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (ShapeSheetType)getTypeLoader().parse(s, ShapeSheetType.type, xmlOptions);
        }
        
        public static ShapeSheetType parse(final File file) throws XmlException, IOException {
            return (ShapeSheetType)getTypeLoader().parse(file, ShapeSheetType.type, (XmlOptions)null);
        }
        
        public static ShapeSheetType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ShapeSheetType)getTypeLoader().parse(file, ShapeSheetType.type, xmlOptions);
        }
        
        public static ShapeSheetType parse(final URL url) throws XmlException, IOException {
            return (ShapeSheetType)getTypeLoader().parse(url, ShapeSheetType.type, (XmlOptions)null);
        }
        
        public static ShapeSheetType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ShapeSheetType)getTypeLoader().parse(url, ShapeSheetType.type, xmlOptions);
        }
        
        public static ShapeSheetType parse(final InputStream inputStream) throws XmlException, IOException {
            return (ShapeSheetType)getTypeLoader().parse(inputStream, ShapeSheetType.type, (XmlOptions)null);
        }
        
        public static ShapeSheetType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ShapeSheetType)getTypeLoader().parse(inputStream, ShapeSheetType.type, xmlOptions);
        }
        
        public static ShapeSheetType parse(final Reader reader) throws XmlException, IOException {
            return (ShapeSheetType)getTypeLoader().parse(reader, ShapeSheetType.type, (XmlOptions)null);
        }
        
        public static ShapeSheetType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (ShapeSheetType)getTypeLoader().parse(reader, ShapeSheetType.type, xmlOptions);
        }
        
        public static ShapeSheetType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (ShapeSheetType)getTypeLoader().parse(xmlStreamReader, ShapeSheetType.type, (XmlOptions)null);
        }
        
        public static ShapeSheetType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (ShapeSheetType)getTypeLoader().parse(xmlStreamReader, ShapeSheetType.type, xmlOptions);
        }
        
        public static ShapeSheetType parse(final Node node) throws XmlException {
            return (ShapeSheetType)getTypeLoader().parse(node, ShapeSheetType.type, (XmlOptions)null);
        }
        
        public static ShapeSheetType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (ShapeSheetType)getTypeLoader().parse(node, ShapeSheetType.type, xmlOptions);
        }
        
        @Deprecated
        public static ShapeSheetType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (ShapeSheetType)getTypeLoader().parse(xmlInputStream, ShapeSheetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static ShapeSheetType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (ShapeSheetType)getTypeLoader().parse(xmlInputStream, ShapeSheetType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ShapeSheetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, ShapeSheetType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
