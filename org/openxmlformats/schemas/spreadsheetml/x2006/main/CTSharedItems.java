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
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlBoolean;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSharedItems extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSharedItems.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctshareditems677atype");
    
    List<CTMissing> getMList();
    
    @Deprecated
    CTMissing[] getMArray();
    
    CTMissing getMArray(final int p0);
    
    int sizeOfMArray();
    
    void setMArray(final CTMissing[] p0);
    
    void setMArray(final int p0, final CTMissing p1);
    
    CTMissing insertNewM(final int p0);
    
    CTMissing addNewM();
    
    void removeM(final int p0);
    
    List<CTNumber> getNList();
    
    @Deprecated
    CTNumber[] getNArray();
    
    CTNumber getNArray(final int p0);
    
    int sizeOfNArray();
    
    void setNArray(final CTNumber[] p0);
    
    void setNArray(final int p0, final CTNumber p1);
    
    CTNumber insertNewN(final int p0);
    
    CTNumber addNewN();
    
    void removeN(final int p0);
    
    List<CTBoolean> getBList();
    
    @Deprecated
    CTBoolean[] getBArray();
    
    CTBoolean getBArray(final int p0);
    
    int sizeOfBArray();
    
    void setBArray(final CTBoolean[] p0);
    
    void setBArray(final int p0, final CTBoolean p1);
    
    CTBoolean insertNewB(final int p0);
    
    CTBoolean addNewB();
    
    void removeB(final int p0);
    
    List<CTError> getEList();
    
    @Deprecated
    CTError[] getEArray();
    
    CTError getEArray(final int p0);
    
    int sizeOfEArray();
    
    void setEArray(final CTError[] p0);
    
    void setEArray(final int p0, final CTError p1);
    
    CTError insertNewE(final int p0);
    
    CTError addNewE();
    
    void removeE(final int p0);
    
    List<CTString> getSList();
    
    @Deprecated
    CTString[] getSArray();
    
    CTString getSArray(final int p0);
    
    int sizeOfSArray();
    
    void setSArray(final CTString[] p0);
    
    void setSArray(final int p0, final CTString p1);
    
    CTString insertNewS(final int p0);
    
    CTString addNewS();
    
    void removeS(final int p0);
    
    List<CTDateTime> getDList();
    
    @Deprecated
    CTDateTime[] getDArray();
    
    CTDateTime getDArray(final int p0);
    
    int sizeOfDArray();
    
    void setDArray(final CTDateTime[] p0);
    
    void setDArray(final int p0, final CTDateTime p1);
    
    CTDateTime insertNewD(final int p0);
    
    CTDateTime addNewD();
    
    void removeD(final int p0);
    
    boolean getContainsSemiMixedTypes();
    
    XmlBoolean xgetContainsSemiMixedTypes();
    
    boolean isSetContainsSemiMixedTypes();
    
    void setContainsSemiMixedTypes(final boolean p0);
    
    void xsetContainsSemiMixedTypes(final XmlBoolean p0);
    
    void unsetContainsSemiMixedTypes();
    
    boolean getContainsNonDate();
    
    XmlBoolean xgetContainsNonDate();
    
    boolean isSetContainsNonDate();
    
    void setContainsNonDate(final boolean p0);
    
    void xsetContainsNonDate(final XmlBoolean p0);
    
    void unsetContainsNonDate();
    
    boolean getContainsDate();
    
    XmlBoolean xgetContainsDate();
    
    boolean isSetContainsDate();
    
    void setContainsDate(final boolean p0);
    
    void xsetContainsDate(final XmlBoolean p0);
    
    void unsetContainsDate();
    
    boolean getContainsString();
    
    XmlBoolean xgetContainsString();
    
    boolean isSetContainsString();
    
    void setContainsString(final boolean p0);
    
    void xsetContainsString(final XmlBoolean p0);
    
    void unsetContainsString();
    
    boolean getContainsBlank();
    
    XmlBoolean xgetContainsBlank();
    
    boolean isSetContainsBlank();
    
    void setContainsBlank(final boolean p0);
    
    void xsetContainsBlank(final XmlBoolean p0);
    
    void unsetContainsBlank();
    
    boolean getContainsMixedTypes();
    
    XmlBoolean xgetContainsMixedTypes();
    
    boolean isSetContainsMixedTypes();
    
    void setContainsMixedTypes(final boolean p0);
    
    void xsetContainsMixedTypes(final XmlBoolean p0);
    
    void unsetContainsMixedTypes();
    
    boolean getContainsNumber();
    
    XmlBoolean xgetContainsNumber();
    
    boolean isSetContainsNumber();
    
    void setContainsNumber(final boolean p0);
    
    void xsetContainsNumber(final XmlBoolean p0);
    
    void unsetContainsNumber();
    
    boolean getContainsInteger();
    
    XmlBoolean xgetContainsInteger();
    
    boolean isSetContainsInteger();
    
    void setContainsInteger(final boolean p0);
    
    void xsetContainsInteger(final XmlBoolean p0);
    
    void unsetContainsInteger();
    
    double getMinValue();
    
    XmlDouble xgetMinValue();
    
    boolean isSetMinValue();
    
    void setMinValue(final double p0);
    
    void xsetMinValue(final XmlDouble p0);
    
    void unsetMinValue();
    
    double getMaxValue();
    
    XmlDouble xgetMaxValue();
    
    boolean isSetMaxValue();
    
    void setMaxValue(final double p0);
    
    void xsetMaxValue(final XmlDouble p0);
    
    void unsetMaxValue();
    
    Calendar getMinDate();
    
    XmlDateTime xgetMinDate();
    
    boolean isSetMinDate();
    
    void setMinDate(final Calendar p0);
    
    void xsetMinDate(final XmlDateTime p0);
    
    void unsetMinDate();
    
    Calendar getMaxDate();
    
    XmlDateTime xgetMaxDate();
    
    boolean isSetMaxDate();
    
    void setMaxDate(final Calendar p0);
    
    void xsetMaxDate(final XmlDateTime p0);
    
    void unsetMaxDate();
    
    long getCount();
    
    XmlUnsignedInt xgetCount();
    
    boolean isSetCount();
    
    void setCount(final long p0);
    
    void xsetCount(final XmlUnsignedInt p0);
    
    void unsetCount();
    
    boolean getLongText();
    
    XmlBoolean xgetLongText();
    
    boolean isSetLongText();
    
    void setLongText(final boolean p0);
    
    void xsetLongText(final XmlBoolean p0);
    
    void unsetLongText();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSharedItems.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSharedItems newInstance() {
            return (CTSharedItems)getTypeLoader().newInstance(CTSharedItems.type, (XmlOptions)null);
        }
        
        public static CTSharedItems newInstance(final XmlOptions xmlOptions) {
            return (CTSharedItems)getTypeLoader().newInstance(CTSharedItems.type, xmlOptions);
        }
        
        public static CTSharedItems parse(final String s) throws XmlException {
            return (CTSharedItems)getTypeLoader().parse(s, CTSharedItems.type, (XmlOptions)null);
        }
        
        public static CTSharedItems parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSharedItems)getTypeLoader().parse(s, CTSharedItems.type, xmlOptions);
        }
        
        public static CTSharedItems parse(final File file) throws XmlException, IOException {
            return (CTSharedItems)getTypeLoader().parse(file, CTSharedItems.type, (XmlOptions)null);
        }
        
        public static CTSharedItems parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSharedItems)getTypeLoader().parse(file, CTSharedItems.type, xmlOptions);
        }
        
        public static CTSharedItems parse(final URL url) throws XmlException, IOException {
            return (CTSharedItems)getTypeLoader().parse(url, CTSharedItems.type, (XmlOptions)null);
        }
        
        public static CTSharedItems parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSharedItems)getTypeLoader().parse(url, CTSharedItems.type, xmlOptions);
        }
        
        public static CTSharedItems parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSharedItems)getTypeLoader().parse(inputStream, CTSharedItems.type, (XmlOptions)null);
        }
        
        public static CTSharedItems parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSharedItems)getTypeLoader().parse(inputStream, CTSharedItems.type, xmlOptions);
        }
        
        public static CTSharedItems parse(final Reader reader) throws XmlException, IOException {
            return (CTSharedItems)getTypeLoader().parse(reader, CTSharedItems.type, (XmlOptions)null);
        }
        
        public static CTSharedItems parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSharedItems)getTypeLoader().parse(reader, CTSharedItems.type, xmlOptions);
        }
        
        public static CTSharedItems parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSharedItems)getTypeLoader().parse(xmlStreamReader, CTSharedItems.type, (XmlOptions)null);
        }
        
        public static CTSharedItems parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSharedItems)getTypeLoader().parse(xmlStreamReader, CTSharedItems.type, xmlOptions);
        }
        
        public static CTSharedItems parse(final Node node) throws XmlException {
            return (CTSharedItems)getTypeLoader().parse(node, CTSharedItems.type, (XmlOptions)null);
        }
        
        public static CTSharedItems parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSharedItems)getTypeLoader().parse(node, CTSharedItems.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSharedItems parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSharedItems)getTypeLoader().parse(xmlInputStream, CTSharedItems.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSharedItems parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSharedItems)getTypeLoader().parse(xmlInputStream, CTSharedItems.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSharedItems.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSharedItems.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
