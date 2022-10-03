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
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlString;

public interface STItemType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STItemType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stitemtype6186type");
    public static final Enum DATA = Enum.forString("data");
    public static final Enum DEFAULT = Enum.forString("default");
    public static final Enum SUM = Enum.forString("sum");
    public static final Enum COUNT_A = Enum.forString("countA");
    public static final Enum AVG = Enum.forString("avg");
    public static final Enum MAX = Enum.forString("max");
    public static final Enum MIN = Enum.forString("min");
    public static final Enum PRODUCT = Enum.forString("product");
    public static final Enum COUNT = Enum.forString("count");
    public static final Enum STD_DEV = Enum.forString("stdDev");
    public static final Enum STD_DEV_P = Enum.forString("stdDevP");
    public static final Enum VAR = Enum.forString("var");
    public static final Enum VAR_P = Enum.forString("varP");
    public static final Enum GRAND = Enum.forString("grand");
    public static final Enum BLANK = Enum.forString("blank");
    public static final int INT_DATA = 1;
    public static final int INT_DEFAULT = 2;
    public static final int INT_SUM = 3;
    public static final int INT_COUNT_A = 4;
    public static final int INT_AVG = 5;
    public static final int INT_MAX = 6;
    public static final int INT_MIN = 7;
    public static final int INT_PRODUCT = 8;
    public static final int INT_COUNT = 9;
    public static final int INT_STD_DEV = 10;
    public static final int INT_STD_DEV_P = 11;
    public static final int INT_VAR = 12;
    public static final int INT_VAR_P = 13;
    public static final int INT_GRAND = 14;
    public static final int INT_BLANK = 15;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STItemType newValue(final Object o) {
            return (STItemType)STItemType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STItemType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STItemType newInstance() {
            return (STItemType)getTypeLoader().newInstance(STItemType.type, (XmlOptions)null);
        }
        
        public static STItemType newInstance(final XmlOptions xmlOptions) {
            return (STItemType)getTypeLoader().newInstance(STItemType.type, xmlOptions);
        }
        
        public static STItemType parse(final String s) throws XmlException {
            return (STItemType)getTypeLoader().parse(s, STItemType.type, (XmlOptions)null);
        }
        
        public static STItemType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STItemType)getTypeLoader().parse(s, STItemType.type, xmlOptions);
        }
        
        public static STItemType parse(final File file) throws XmlException, IOException {
            return (STItemType)getTypeLoader().parse(file, STItemType.type, (XmlOptions)null);
        }
        
        public static STItemType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STItemType)getTypeLoader().parse(file, STItemType.type, xmlOptions);
        }
        
        public static STItemType parse(final URL url) throws XmlException, IOException {
            return (STItemType)getTypeLoader().parse(url, STItemType.type, (XmlOptions)null);
        }
        
        public static STItemType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STItemType)getTypeLoader().parse(url, STItemType.type, xmlOptions);
        }
        
        public static STItemType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STItemType)getTypeLoader().parse(inputStream, STItemType.type, (XmlOptions)null);
        }
        
        public static STItemType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STItemType)getTypeLoader().parse(inputStream, STItemType.type, xmlOptions);
        }
        
        public static STItemType parse(final Reader reader) throws XmlException, IOException {
            return (STItemType)getTypeLoader().parse(reader, STItemType.type, (XmlOptions)null);
        }
        
        public static STItemType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STItemType)getTypeLoader().parse(reader, STItemType.type, xmlOptions);
        }
        
        public static STItemType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STItemType)getTypeLoader().parse(xmlStreamReader, STItemType.type, (XmlOptions)null);
        }
        
        public static STItemType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STItemType)getTypeLoader().parse(xmlStreamReader, STItemType.type, xmlOptions);
        }
        
        public static STItemType parse(final Node node) throws XmlException {
            return (STItemType)getTypeLoader().parse(node, STItemType.type, (XmlOptions)null);
        }
        
        public static STItemType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STItemType)getTypeLoader().parse(node, STItemType.type, xmlOptions);
        }
        
        @Deprecated
        public static STItemType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STItemType)getTypeLoader().parse(xmlInputStream, STItemType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STItemType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STItemType)getTypeLoader().parse(xmlInputStream, STItemType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STItemType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STItemType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_DATA = 1;
        static final int INT_DEFAULT = 2;
        static final int INT_SUM = 3;
        static final int INT_COUNT_A = 4;
        static final int INT_AVG = 5;
        static final int INT_MAX = 6;
        static final int INT_MIN = 7;
        static final int INT_PRODUCT = 8;
        static final int INT_COUNT = 9;
        static final int INT_STD_DEV = 10;
        static final int INT_STD_DEV_P = 11;
        static final int INT_VAR = 12;
        static final int INT_VAR_P = 13;
        static final int INT_GRAND = 14;
        static final int INT_BLANK = 15;
        public static final StringEnumAbstractBase.Table table;
        private static final long serialVersionUID = 1L;
        
        public static Enum forString(final String s) {
            return (Enum)Enum.table.forString(s);
        }
        
        public static Enum forInt(final int n) {
            return (Enum)Enum.table.forInt(n);
        }
        
        private Enum(final String s, final int n) {
            super(s, n);
        }
        
        private Object readResolve() {
            return forInt(this.intValue());
        }
        
        static {
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("data", 1), new Enum("default", 2), new Enum("sum", 3), new Enum("countA", 4), new Enum("avg", 5), new Enum("max", 6), new Enum("min", 7), new Enum("product", 8), new Enum("count", 9), new Enum("stdDev", 10), new Enum("stdDevP", 11), new Enum("var", 12), new Enum("varP", 13), new Enum("grand", 14), new Enum("blank", 15) });
        }
    }
}
