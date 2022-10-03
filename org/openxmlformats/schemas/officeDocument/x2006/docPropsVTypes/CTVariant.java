package org.openxmlformats.schemas.officeDocument.x2006.docPropsVTypes;

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
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlDecimal;
import java.math.BigDecimal;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlUnsignedLong;
import java.math.BigInteger;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlUnsignedShort;
import org.apache.xmlbeans.XmlUnsignedByte;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlShort;
import org.apache.xmlbeans.XmlByte;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTVariant extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTVariant.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctvariantedcatype");
    
    CTVariant getVariant();
    
    boolean isSetVariant();
    
    void setVariant(final CTVariant p0);
    
    CTVariant addNewVariant();
    
    void unsetVariant();
    
    CTVector getVector();
    
    boolean isSetVector();
    
    void setVector(final CTVector p0);
    
    CTVector addNewVector();
    
    void unsetVector();
    
    CTArray getArray();
    
    boolean isSetArray();
    
    void setArray(final CTArray p0);
    
    CTArray addNewArray();
    
    void unsetArray();
    
    byte[] getBlob();
    
    XmlBase64Binary xgetBlob();
    
    boolean isSetBlob();
    
    void setBlob(final byte[] p0);
    
    void xsetBlob(final XmlBase64Binary p0);
    
    void unsetBlob();
    
    byte[] getOblob();
    
    XmlBase64Binary xgetOblob();
    
    boolean isSetOblob();
    
    void setOblob(final byte[] p0);
    
    void xsetOblob(final XmlBase64Binary p0);
    
    void unsetOblob();
    
    CTEmpty getEmpty();
    
    boolean isSetEmpty();
    
    void setEmpty(final CTEmpty p0);
    
    CTEmpty addNewEmpty();
    
    void unsetEmpty();
    
    CTNull getNull();
    
    boolean isSetNull();
    
    void setNull(final CTNull p0);
    
    CTNull addNewNull();
    
    void unsetNull();
    
    byte getI1();
    
    XmlByte xgetI1();
    
    boolean isSetI1();
    
    void setI1(final byte p0);
    
    void xsetI1(final XmlByte p0);
    
    void unsetI1();
    
    short getI2();
    
    XmlShort xgetI2();
    
    boolean isSetI2();
    
    void setI2(final short p0);
    
    void xsetI2(final XmlShort p0);
    
    void unsetI2();
    
    int getI4();
    
    XmlInt xgetI4();
    
    boolean isSetI4();
    
    void setI4(final int p0);
    
    void xsetI4(final XmlInt p0);
    
    void unsetI4();
    
    long getI8();
    
    XmlLong xgetI8();
    
    boolean isSetI8();
    
    void setI8(final long p0);
    
    void xsetI8(final XmlLong p0);
    
    void unsetI8();
    
    int getInt();
    
    XmlInt xgetInt();
    
    boolean isSetInt();
    
    void setInt(final int p0);
    
    void xsetInt(final XmlInt p0);
    
    void unsetInt();
    
    short getUi1();
    
    XmlUnsignedByte xgetUi1();
    
    boolean isSetUi1();
    
    void setUi1(final short p0);
    
    void xsetUi1(final XmlUnsignedByte p0);
    
    void unsetUi1();
    
    int getUi2();
    
    XmlUnsignedShort xgetUi2();
    
    boolean isSetUi2();
    
    void setUi2(final int p0);
    
    void xsetUi2(final XmlUnsignedShort p0);
    
    void unsetUi2();
    
    long getUi4();
    
    XmlUnsignedInt xgetUi4();
    
    boolean isSetUi4();
    
    void setUi4(final long p0);
    
    void xsetUi4(final XmlUnsignedInt p0);
    
    void unsetUi4();
    
    BigInteger getUi8();
    
    XmlUnsignedLong xgetUi8();
    
    boolean isSetUi8();
    
    void setUi8(final BigInteger p0);
    
    void xsetUi8(final XmlUnsignedLong p0);
    
    void unsetUi8();
    
    long getUint();
    
    XmlUnsignedInt xgetUint();
    
    boolean isSetUint();
    
    void setUint(final long p0);
    
    void xsetUint(final XmlUnsignedInt p0);
    
    void unsetUint();
    
    float getR4();
    
    XmlFloat xgetR4();
    
    boolean isSetR4();
    
    void setR4(final float p0);
    
    void xsetR4(final XmlFloat p0);
    
    void unsetR4();
    
    double getR8();
    
    XmlDouble xgetR8();
    
    boolean isSetR8();
    
    void setR8(final double p0);
    
    void xsetR8(final XmlDouble p0);
    
    void unsetR8();
    
    BigDecimal getDecimal();
    
    XmlDecimal xgetDecimal();
    
    boolean isSetDecimal();
    
    void setDecimal(final BigDecimal p0);
    
    void xsetDecimal(final XmlDecimal p0);
    
    void unsetDecimal();
    
    String getLpstr();
    
    XmlString xgetLpstr();
    
    boolean isSetLpstr();
    
    void setLpstr(final String p0);
    
    void xsetLpstr(final XmlString p0);
    
    void unsetLpstr();
    
    String getLpwstr();
    
    XmlString xgetLpwstr();
    
    boolean isSetLpwstr();
    
    void setLpwstr(final String p0);
    
    void xsetLpwstr(final XmlString p0);
    
    void unsetLpwstr();
    
    String getBstr();
    
    XmlString xgetBstr();
    
    boolean isSetBstr();
    
    void setBstr(final String p0);
    
    void xsetBstr(final XmlString p0);
    
    void unsetBstr();
    
    Calendar getDate();
    
    XmlDateTime xgetDate();
    
    boolean isSetDate();
    
    void setDate(final Calendar p0);
    
    void xsetDate(final XmlDateTime p0);
    
    void unsetDate();
    
    Calendar getFiletime();
    
    XmlDateTime xgetFiletime();
    
    boolean isSetFiletime();
    
    void setFiletime(final Calendar p0);
    
    void xsetFiletime(final XmlDateTime p0);
    
    void unsetFiletime();
    
    boolean getBool();
    
    XmlBoolean xgetBool();
    
    boolean isSetBool();
    
    void setBool(final boolean p0);
    
    void xsetBool(final XmlBoolean p0);
    
    void unsetBool();
    
    String getCy();
    
    STCy xgetCy();
    
    boolean isSetCy();
    
    void setCy(final String p0);
    
    void xsetCy(final STCy p0);
    
    void unsetCy();
    
    String getError();
    
    STError xgetError();
    
    boolean isSetError();
    
    void setError(final String p0);
    
    void xsetError(final STError p0);
    
    void unsetError();
    
    byte[] getStream();
    
    XmlBase64Binary xgetStream();
    
    boolean isSetStream();
    
    void setStream(final byte[] p0);
    
    void xsetStream(final XmlBase64Binary p0);
    
    void unsetStream();
    
    byte[] getOstream();
    
    XmlBase64Binary xgetOstream();
    
    boolean isSetOstream();
    
    void setOstream(final byte[] p0);
    
    void xsetOstream(final XmlBase64Binary p0);
    
    void unsetOstream();
    
    byte[] getStorage();
    
    XmlBase64Binary xgetStorage();
    
    boolean isSetStorage();
    
    void setStorage(final byte[] p0);
    
    void xsetStorage(final XmlBase64Binary p0);
    
    void unsetStorage();
    
    byte[] getOstorage();
    
    XmlBase64Binary xgetOstorage();
    
    boolean isSetOstorage();
    
    void setOstorage(final byte[] p0);
    
    void xsetOstorage(final XmlBase64Binary p0);
    
    void unsetOstorage();
    
    CTVstream getVstream();
    
    boolean isSetVstream();
    
    void setVstream(final CTVstream p0);
    
    CTVstream addNewVstream();
    
    void unsetVstream();
    
    String getClsid();
    
    STClsid xgetClsid();
    
    boolean isSetClsid();
    
    void setClsid(final String p0);
    
    void xsetClsid(final STClsid p0);
    
    void unsetClsid();
    
    CTCf getCf();
    
    boolean isSetCf();
    
    void setCf(final CTCf p0);
    
    CTCf addNewCf();
    
    void unsetCf();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTVariant.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTVariant newInstance() {
            return (CTVariant)getTypeLoader().newInstance(CTVariant.type, (XmlOptions)null);
        }
        
        public static CTVariant newInstance(final XmlOptions xmlOptions) {
            return (CTVariant)getTypeLoader().newInstance(CTVariant.type, xmlOptions);
        }
        
        public static CTVariant parse(final String s) throws XmlException {
            return (CTVariant)getTypeLoader().parse(s, CTVariant.type, (XmlOptions)null);
        }
        
        public static CTVariant parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTVariant)getTypeLoader().parse(s, CTVariant.type, xmlOptions);
        }
        
        public static CTVariant parse(final File file) throws XmlException, IOException {
            return (CTVariant)getTypeLoader().parse(file, CTVariant.type, (XmlOptions)null);
        }
        
        public static CTVariant parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVariant)getTypeLoader().parse(file, CTVariant.type, xmlOptions);
        }
        
        public static CTVariant parse(final URL url) throws XmlException, IOException {
            return (CTVariant)getTypeLoader().parse(url, CTVariant.type, (XmlOptions)null);
        }
        
        public static CTVariant parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVariant)getTypeLoader().parse(url, CTVariant.type, xmlOptions);
        }
        
        public static CTVariant parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTVariant)getTypeLoader().parse(inputStream, CTVariant.type, (XmlOptions)null);
        }
        
        public static CTVariant parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVariant)getTypeLoader().parse(inputStream, CTVariant.type, xmlOptions);
        }
        
        public static CTVariant parse(final Reader reader) throws XmlException, IOException {
            return (CTVariant)getTypeLoader().parse(reader, CTVariant.type, (XmlOptions)null);
        }
        
        public static CTVariant parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVariant)getTypeLoader().parse(reader, CTVariant.type, xmlOptions);
        }
        
        public static CTVariant parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTVariant)getTypeLoader().parse(xmlStreamReader, CTVariant.type, (XmlOptions)null);
        }
        
        public static CTVariant parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTVariant)getTypeLoader().parse(xmlStreamReader, CTVariant.type, xmlOptions);
        }
        
        public static CTVariant parse(final Node node) throws XmlException {
            return (CTVariant)getTypeLoader().parse(node, CTVariant.type, (XmlOptions)null);
        }
        
        public static CTVariant parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTVariant)getTypeLoader().parse(node, CTVariant.type, xmlOptions);
        }
        
        @Deprecated
        public static CTVariant parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTVariant)getTypeLoader().parse(xmlInputStream, CTVariant.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTVariant parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTVariant)getTypeLoader().parse(xmlInputStream, CTVariant.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVariant.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVariant.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
