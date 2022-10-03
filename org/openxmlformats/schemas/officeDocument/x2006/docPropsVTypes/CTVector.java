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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTVector extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTVector.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctvectorc3e2type");
    
    List<CTVariant> getVariantList();
    
    @Deprecated
    CTVariant[] getVariantArray();
    
    CTVariant getVariantArray(final int p0);
    
    int sizeOfVariantArray();
    
    void setVariantArray(final CTVariant[] p0);
    
    void setVariantArray(final int p0, final CTVariant p1);
    
    CTVariant insertNewVariant(final int p0);
    
    CTVariant addNewVariant();
    
    void removeVariant(final int p0);
    
    List<Byte> getI1List();
    
    @Deprecated
    byte[] getI1Array();
    
    byte getI1Array(final int p0);
    
    List<XmlByte> xgetI1List();
    
    @Deprecated
    XmlByte[] xgetI1Array();
    
    XmlByte xgetI1Array(final int p0);
    
    int sizeOfI1Array();
    
    void setI1Array(final byte[] p0);
    
    void setI1Array(final int p0, final byte p1);
    
    void xsetI1Array(final XmlByte[] p0);
    
    void xsetI1Array(final int p0, final XmlByte p1);
    
    void insertI1(final int p0, final byte p1);
    
    void addI1(final byte p0);
    
    XmlByte insertNewI1(final int p0);
    
    XmlByte addNewI1();
    
    void removeI1(final int p0);
    
    List<Short> getI2List();
    
    @Deprecated
    short[] getI2Array();
    
    short getI2Array(final int p0);
    
    List<XmlShort> xgetI2List();
    
    @Deprecated
    XmlShort[] xgetI2Array();
    
    XmlShort xgetI2Array(final int p0);
    
    int sizeOfI2Array();
    
    void setI2Array(final short[] p0);
    
    void setI2Array(final int p0, final short p1);
    
    void xsetI2Array(final XmlShort[] p0);
    
    void xsetI2Array(final int p0, final XmlShort p1);
    
    void insertI2(final int p0, final short p1);
    
    void addI2(final short p0);
    
    XmlShort insertNewI2(final int p0);
    
    XmlShort addNewI2();
    
    void removeI2(final int p0);
    
    List<Integer> getI4List();
    
    @Deprecated
    int[] getI4Array();
    
    int getI4Array(final int p0);
    
    List<XmlInt> xgetI4List();
    
    @Deprecated
    XmlInt[] xgetI4Array();
    
    XmlInt xgetI4Array(final int p0);
    
    int sizeOfI4Array();
    
    void setI4Array(final int[] p0);
    
    void setI4Array(final int p0, final int p1);
    
    void xsetI4Array(final XmlInt[] p0);
    
    void xsetI4Array(final int p0, final XmlInt p1);
    
    void insertI4(final int p0, final int p1);
    
    void addI4(final int p0);
    
    XmlInt insertNewI4(final int p0);
    
    XmlInt addNewI4();
    
    void removeI4(final int p0);
    
    List<Long> getI8List();
    
    @Deprecated
    long[] getI8Array();
    
    long getI8Array(final int p0);
    
    List<XmlLong> xgetI8List();
    
    @Deprecated
    XmlLong[] xgetI8Array();
    
    XmlLong xgetI8Array(final int p0);
    
    int sizeOfI8Array();
    
    void setI8Array(final long[] p0);
    
    void setI8Array(final int p0, final long p1);
    
    void xsetI8Array(final XmlLong[] p0);
    
    void xsetI8Array(final int p0, final XmlLong p1);
    
    void insertI8(final int p0, final long p1);
    
    void addI8(final long p0);
    
    XmlLong insertNewI8(final int p0);
    
    XmlLong addNewI8();
    
    void removeI8(final int p0);
    
    List<Short> getUi1List();
    
    @Deprecated
    short[] getUi1Array();
    
    short getUi1Array(final int p0);
    
    List<XmlUnsignedByte> xgetUi1List();
    
    @Deprecated
    XmlUnsignedByte[] xgetUi1Array();
    
    XmlUnsignedByte xgetUi1Array(final int p0);
    
    int sizeOfUi1Array();
    
    void setUi1Array(final short[] p0);
    
    void setUi1Array(final int p0, final short p1);
    
    void xsetUi1Array(final XmlUnsignedByte[] p0);
    
    void xsetUi1Array(final int p0, final XmlUnsignedByte p1);
    
    void insertUi1(final int p0, final short p1);
    
    void addUi1(final short p0);
    
    XmlUnsignedByte insertNewUi1(final int p0);
    
    XmlUnsignedByte addNewUi1();
    
    void removeUi1(final int p0);
    
    List<Integer> getUi2List();
    
    @Deprecated
    int[] getUi2Array();
    
    int getUi2Array(final int p0);
    
    List<XmlUnsignedShort> xgetUi2List();
    
    @Deprecated
    XmlUnsignedShort[] xgetUi2Array();
    
    XmlUnsignedShort xgetUi2Array(final int p0);
    
    int sizeOfUi2Array();
    
    void setUi2Array(final int[] p0);
    
    void setUi2Array(final int p0, final int p1);
    
    void xsetUi2Array(final XmlUnsignedShort[] p0);
    
    void xsetUi2Array(final int p0, final XmlUnsignedShort p1);
    
    void insertUi2(final int p0, final int p1);
    
    void addUi2(final int p0);
    
    XmlUnsignedShort insertNewUi2(final int p0);
    
    XmlUnsignedShort addNewUi2();
    
    void removeUi2(final int p0);
    
    List<Long> getUi4List();
    
    @Deprecated
    long[] getUi4Array();
    
    long getUi4Array(final int p0);
    
    List<XmlUnsignedInt> xgetUi4List();
    
    @Deprecated
    XmlUnsignedInt[] xgetUi4Array();
    
    XmlUnsignedInt xgetUi4Array(final int p0);
    
    int sizeOfUi4Array();
    
    void setUi4Array(final long[] p0);
    
    void setUi4Array(final int p0, final long p1);
    
    void xsetUi4Array(final XmlUnsignedInt[] p0);
    
    void xsetUi4Array(final int p0, final XmlUnsignedInt p1);
    
    void insertUi4(final int p0, final long p1);
    
    void addUi4(final long p0);
    
    XmlUnsignedInt insertNewUi4(final int p0);
    
    XmlUnsignedInt addNewUi4();
    
    void removeUi4(final int p0);
    
    List<BigInteger> getUi8List();
    
    @Deprecated
    BigInteger[] getUi8Array();
    
    BigInteger getUi8Array(final int p0);
    
    List<XmlUnsignedLong> xgetUi8List();
    
    @Deprecated
    XmlUnsignedLong[] xgetUi8Array();
    
    XmlUnsignedLong xgetUi8Array(final int p0);
    
    int sizeOfUi8Array();
    
    void setUi8Array(final BigInteger[] p0);
    
    void setUi8Array(final int p0, final BigInteger p1);
    
    void xsetUi8Array(final XmlUnsignedLong[] p0);
    
    void xsetUi8Array(final int p0, final XmlUnsignedLong p1);
    
    void insertUi8(final int p0, final BigInteger p1);
    
    void addUi8(final BigInteger p0);
    
    XmlUnsignedLong insertNewUi8(final int p0);
    
    XmlUnsignedLong addNewUi8();
    
    void removeUi8(final int p0);
    
    List<Float> getR4List();
    
    @Deprecated
    float[] getR4Array();
    
    float getR4Array(final int p0);
    
    List<XmlFloat> xgetR4List();
    
    @Deprecated
    XmlFloat[] xgetR4Array();
    
    XmlFloat xgetR4Array(final int p0);
    
    int sizeOfR4Array();
    
    void setR4Array(final float[] p0);
    
    void setR4Array(final int p0, final float p1);
    
    void xsetR4Array(final XmlFloat[] p0);
    
    void xsetR4Array(final int p0, final XmlFloat p1);
    
    void insertR4(final int p0, final float p1);
    
    void addR4(final float p0);
    
    XmlFloat insertNewR4(final int p0);
    
    XmlFloat addNewR4();
    
    void removeR4(final int p0);
    
    List<Double> getR8List();
    
    @Deprecated
    double[] getR8Array();
    
    double getR8Array(final int p0);
    
    List<XmlDouble> xgetR8List();
    
    @Deprecated
    XmlDouble[] xgetR8Array();
    
    XmlDouble xgetR8Array(final int p0);
    
    int sizeOfR8Array();
    
    void setR8Array(final double[] p0);
    
    void setR8Array(final int p0, final double p1);
    
    void xsetR8Array(final XmlDouble[] p0);
    
    void xsetR8Array(final int p0, final XmlDouble p1);
    
    void insertR8(final int p0, final double p1);
    
    void addR8(final double p0);
    
    XmlDouble insertNewR8(final int p0);
    
    XmlDouble addNewR8();
    
    void removeR8(final int p0);
    
    List<String> getLpstrList();
    
    @Deprecated
    String[] getLpstrArray();
    
    String getLpstrArray(final int p0);
    
    List<XmlString> xgetLpstrList();
    
    @Deprecated
    XmlString[] xgetLpstrArray();
    
    XmlString xgetLpstrArray(final int p0);
    
    int sizeOfLpstrArray();
    
    void setLpstrArray(final String[] p0);
    
    void setLpstrArray(final int p0, final String p1);
    
    void xsetLpstrArray(final XmlString[] p0);
    
    void xsetLpstrArray(final int p0, final XmlString p1);
    
    void insertLpstr(final int p0, final String p1);
    
    void addLpstr(final String p0);
    
    XmlString insertNewLpstr(final int p0);
    
    XmlString addNewLpstr();
    
    void removeLpstr(final int p0);
    
    List<String> getLpwstrList();
    
    @Deprecated
    String[] getLpwstrArray();
    
    String getLpwstrArray(final int p0);
    
    List<XmlString> xgetLpwstrList();
    
    @Deprecated
    XmlString[] xgetLpwstrArray();
    
    XmlString xgetLpwstrArray(final int p0);
    
    int sizeOfLpwstrArray();
    
    void setLpwstrArray(final String[] p0);
    
    void setLpwstrArray(final int p0, final String p1);
    
    void xsetLpwstrArray(final XmlString[] p0);
    
    void xsetLpwstrArray(final int p0, final XmlString p1);
    
    void insertLpwstr(final int p0, final String p1);
    
    void addLpwstr(final String p0);
    
    XmlString insertNewLpwstr(final int p0);
    
    XmlString addNewLpwstr();
    
    void removeLpwstr(final int p0);
    
    List<String> getBstrList();
    
    @Deprecated
    String[] getBstrArray();
    
    String getBstrArray(final int p0);
    
    List<XmlString> xgetBstrList();
    
    @Deprecated
    XmlString[] xgetBstrArray();
    
    XmlString xgetBstrArray(final int p0);
    
    int sizeOfBstrArray();
    
    void setBstrArray(final String[] p0);
    
    void setBstrArray(final int p0, final String p1);
    
    void xsetBstrArray(final XmlString[] p0);
    
    void xsetBstrArray(final int p0, final XmlString p1);
    
    void insertBstr(final int p0, final String p1);
    
    void addBstr(final String p0);
    
    XmlString insertNewBstr(final int p0);
    
    XmlString addNewBstr();
    
    void removeBstr(final int p0);
    
    List<Calendar> getDateList();
    
    @Deprecated
    Calendar[] getDateArray();
    
    Calendar getDateArray(final int p0);
    
    List<XmlDateTime> xgetDateList();
    
    @Deprecated
    XmlDateTime[] xgetDateArray();
    
    XmlDateTime xgetDateArray(final int p0);
    
    int sizeOfDateArray();
    
    void setDateArray(final Calendar[] p0);
    
    void setDateArray(final int p0, final Calendar p1);
    
    void xsetDateArray(final XmlDateTime[] p0);
    
    void xsetDateArray(final int p0, final XmlDateTime p1);
    
    void insertDate(final int p0, final Calendar p1);
    
    void addDate(final Calendar p0);
    
    XmlDateTime insertNewDate(final int p0);
    
    XmlDateTime addNewDate();
    
    void removeDate(final int p0);
    
    List<Calendar> getFiletimeList();
    
    @Deprecated
    Calendar[] getFiletimeArray();
    
    Calendar getFiletimeArray(final int p0);
    
    List<XmlDateTime> xgetFiletimeList();
    
    @Deprecated
    XmlDateTime[] xgetFiletimeArray();
    
    XmlDateTime xgetFiletimeArray(final int p0);
    
    int sizeOfFiletimeArray();
    
    void setFiletimeArray(final Calendar[] p0);
    
    void setFiletimeArray(final int p0, final Calendar p1);
    
    void xsetFiletimeArray(final XmlDateTime[] p0);
    
    void xsetFiletimeArray(final int p0, final XmlDateTime p1);
    
    void insertFiletime(final int p0, final Calendar p1);
    
    void addFiletime(final Calendar p0);
    
    XmlDateTime insertNewFiletime(final int p0);
    
    XmlDateTime addNewFiletime();
    
    void removeFiletime(final int p0);
    
    List<Boolean> getBoolList();
    
    @Deprecated
    boolean[] getBoolArray();
    
    boolean getBoolArray(final int p0);
    
    List<XmlBoolean> xgetBoolList();
    
    @Deprecated
    XmlBoolean[] xgetBoolArray();
    
    XmlBoolean xgetBoolArray(final int p0);
    
    int sizeOfBoolArray();
    
    void setBoolArray(final boolean[] p0);
    
    void setBoolArray(final int p0, final boolean p1);
    
    void xsetBoolArray(final XmlBoolean[] p0);
    
    void xsetBoolArray(final int p0, final XmlBoolean p1);
    
    void insertBool(final int p0, final boolean p1);
    
    void addBool(final boolean p0);
    
    XmlBoolean insertNewBool(final int p0);
    
    XmlBoolean addNewBool();
    
    void removeBool(final int p0);
    
    List<String> getCyList();
    
    @Deprecated
    String[] getCyArray();
    
    String getCyArray(final int p0);
    
    List<STCy> xgetCyList();
    
    @Deprecated
    STCy[] xgetCyArray();
    
    STCy xgetCyArray(final int p0);
    
    int sizeOfCyArray();
    
    void setCyArray(final String[] p0);
    
    void setCyArray(final int p0, final String p1);
    
    void xsetCyArray(final STCy[] p0);
    
    void xsetCyArray(final int p0, final STCy p1);
    
    void insertCy(final int p0, final String p1);
    
    void addCy(final String p0);
    
    STCy insertNewCy(final int p0);
    
    STCy addNewCy();
    
    void removeCy(final int p0);
    
    List<String> getErrorList();
    
    @Deprecated
    String[] getErrorArray();
    
    String getErrorArray(final int p0);
    
    List<STError> xgetErrorList();
    
    @Deprecated
    STError[] xgetErrorArray();
    
    STError xgetErrorArray(final int p0);
    
    int sizeOfErrorArray();
    
    void setErrorArray(final String[] p0);
    
    void setErrorArray(final int p0, final String p1);
    
    void xsetErrorArray(final STError[] p0);
    
    void xsetErrorArray(final int p0, final STError p1);
    
    void insertError(final int p0, final String p1);
    
    void addError(final String p0);
    
    STError insertNewError(final int p0);
    
    STError addNewError();
    
    void removeError(final int p0);
    
    List<String> getClsidList();
    
    @Deprecated
    String[] getClsidArray();
    
    String getClsidArray(final int p0);
    
    List<STClsid> xgetClsidList();
    
    @Deprecated
    STClsid[] xgetClsidArray();
    
    STClsid xgetClsidArray(final int p0);
    
    int sizeOfClsidArray();
    
    void setClsidArray(final String[] p0);
    
    void setClsidArray(final int p0, final String p1);
    
    void xsetClsidArray(final STClsid[] p0);
    
    void xsetClsidArray(final int p0, final STClsid p1);
    
    void insertClsid(final int p0, final String p1);
    
    void addClsid(final String p0);
    
    STClsid insertNewClsid(final int p0);
    
    STClsid addNewClsid();
    
    void removeClsid(final int p0);
    
    List<CTCf> getCfList();
    
    @Deprecated
    CTCf[] getCfArray();
    
    CTCf getCfArray(final int p0);
    
    int sizeOfCfArray();
    
    void setCfArray(final CTCf[] p0);
    
    void setCfArray(final int p0, final CTCf p1);
    
    CTCf insertNewCf(final int p0);
    
    CTCf addNewCf();
    
    void removeCf(final int p0);
    
    STVectorBaseType.Enum getBaseType();
    
    STVectorBaseType xgetBaseType();
    
    void setBaseType(final STVectorBaseType.Enum p0);
    
    void xsetBaseType(final STVectorBaseType p0);
    
    long getSize();
    
    XmlUnsignedInt xgetSize();
    
    void setSize(final long p0);
    
    void xsetSize(final XmlUnsignedInt p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTVector.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTVector newInstance() {
            return (CTVector)getTypeLoader().newInstance(CTVector.type, (XmlOptions)null);
        }
        
        public static CTVector newInstance(final XmlOptions xmlOptions) {
            return (CTVector)getTypeLoader().newInstance(CTVector.type, xmlOptions);
        }
        
        public static CTVector parse(final String s) throws XmlException {
            return (CTVector)getTypeLoader().parse(s, CTVector.type, (XmlOptions)null);
        }
        
        public static CTVector parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTVector)getTypeLoader().parse(s, CTVector.type, xmlOptions);
        }
        
        public static CTVector parse(final File file) throws XmlException, IOException {
            return (CTVector)getTypeLoader().parse(file, CTVector.type, (XmlOptions)null);
        }
        
        public static CTVector parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVector)getTypeLoader().parse(file, CTVector.type, xmlOptions);
        }
        
        public static CTVector parse(final URL url) throws XmlException, IOException {
            return (CTVector)getTypeLoader().parse(url, CTVector.type, (XmlOptions)null);
        }
        
        public static CTVector parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVector)getTypeLoader().parse(url, CTVector.type, xmlOptions);
        }
        
        public static CTVector parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTVector)getTypeLoader().parse(inputStream, CTVector.type, (XmlOptions)null);
        }
        
        public static CTVector parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVector)getTypeLoader().parse(inputStream, CTVector.type, xmlOptions);
        }
        
        public static CTVector parse(final Reader reader) throws XmlException, IOException {
            return (CTVector)getTypeLoader().parse(reader, CTVector.type, (XmlOptions)null);
        }
        
        public static CTVector parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTVector)getTypeLoader().parse(reader, CTVector.type, xmlOptions);
        }
        
        public static CTVector parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTVector)getTypeLoader().parse(xmlStreamReader, CTVector.type, (XmlOptions)null);
        }
        
        public static CTVector parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTVector)getTypeLoader().parse(xmlStreamReader, CTVector.type, xmlOptions);
        }
        
        public static CTVector parse(final Node node) throws XmlException {
            return (CTVector)getTypeLoader().parse(node, CTVector.type, (XmlOptions)null);
        }
        
        public static CTVector parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTVector)getTypeLoader().parse(node, CTVector.type, xmlOptions);
        }
        
        @Deprecated
        public static CTVector parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTVector)getTypeLoader().parse(xmlInputStream, CTVector.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTVector parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTVector)getTypeLoader().parse(xmlInputStream, CTVector.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVector.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTVector.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
