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

public interface STDataConsolidateFunction extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STDataConsolidateFunction.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stdataconsolidatefunction1206type");
    public static final Enum AVERAGE = Enum.forString("average");
    public static final Enum COUNT = Enum.forString("count");
    public static final Enum COUNT_NUMS = Enum.forString("countNums");
    public static final Enum MAX = Enum.forString("max");
    public static final Enum MIN = Enum.forString("min");
    public static final Enum PRODUCT = Enum.forString("product");
    public static final Enum STD_DEV = Enum.forString("stdDev");
    public static final Enum STD_DEVP = Enum.forString("stdDevp");
    public static final Enum SUM = Enum.forString("sum");
    public static final Enum VAR = Enum.forString("var");
    public static final Enum VARP = Enum.forString("varp");
    public static final int INT_AVERAGE = 1;
    public static final int INT_COUNT = 2;
    public static final int INT_COUNT_NUMS = 3;
    public static final int INT_MAX = 4;
    public static final int INT_MIN = 5;
    public static final int INT_PRODUCT = 6;
    public static final int INT_STD_DEV = 7;
    public static final int INT_STD_DEVP = 8;
    public static final int INT_SUM = 9;
    public static final int INT_VAR = 10;
    public static final int INT_VARP = 11;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STDataConsolidateFunction newValue(final Object o) {
            return (STDataConsolidateFunction)STDataConsolidateFunction.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STDataConsolidateFunction.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STDataConsolidateFunction newInstance() {
            return (STDataConsolidateFunction)getTypeLoader().newInstance(STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        public static STDataConsolidateFunction newInstance(final XmlOptions xmlOptions) {
            return (STDataConsolidateFunction)getTypeLoader().newInstance(STDataConsolidateFunction.type, xmlOptions);
        }
        
        public static STDataConsolidateFunction parse(final String s) throws XmlException {
            return (STDataConsolidateFunction)getTypeLoader().parse(s, STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        public static STDataConsolidateFunction parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STDataConsolidateFunction)getTypeLoader().parse(s, STDataConsolidateFunction.type, xmlOptions);
        }
        
        public static STDataConsolidateFunction parse(final File file) throws XmlException, IOException {
            return (STDataConsolidateFunction)getTypeLoader().parse(file, STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        public static STDataConsolidateFunction parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataConsolidateFunction)getTypeLoader().parse(file, STDataConsolidateFunction.type, xmlOptions);
        }
        
        public static STDataConsolidateFunction parse(final URL url) throws XmlException, IOException {
            return (STDataConsolidateFunction)getTypeLoader().parse(url, STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        public static STDataConsolidateFunction parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataConsolidateFunction)getTypeLoader().parse(url, STDataConsolidateFunction.type, xmlOptions);
        }
        
        public static STDataConsolidateFunction parse(final InputStream inputStream) throws XmlException, IOException {
            return (STDataConsolidateFunction)getTypeLoader().parse(inputStream, STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        public static STDataConsolidateFunction parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataConsolidateFunction)getTypeLoader().parse(inputStream, STDataConsolidateFunction.type, xmlOptions);
        }
        
        public static STDataConsolidateFunction parse(final Reader reader) throws XmlException, IOException {
            return (STDataConsolidateFunction)getTypeLoader().parse(reader, STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        public static STDataConsolidateFunction parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataConsolidateFunction)getTypeLoader().parse(reader, STDataConsolidateFunction.type, xmlOptions);
        }
        
        public static STDataConsolidateFunction parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STDataConsolidateFunction)getTypeLoader().parse(xmlStreamReader, STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        public static STDataConsolidateFunction parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STDataConsolidateFunction)getTypeLoader().parse(xmlStreamReader, STDataConsolidateFunction.type, xmlOptions);
        }
        
        public static STDataConsolidateFunction parse(final Node node) throws XmlException {
            return (STDataConsolidateFunction)getTypeLoader().parse(node, STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        public static STDataConsolidateFunction parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STDataConsolidateFunction)getTypeLoader().parse(node, STDataConsolidateFunction.type, xmlOptions);
        }
        
        @Deprecated
        public static STDataConsolidateFunction parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STDataConsolidateFunction)getTypeLoader().parse(xmlInputStream, STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STDataConsolidateFunction parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STDataConsolidateFunction)getTypeLoader().parse(xmlInputStream, STDataConsolidateFunction.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDataConsolidateFunction.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDataConsolidateFunction.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_AVERAGE = 1;
        static final int INT_COUNT = 2;
        static final int INT_COUNT_NUMS = 3;
        static final int INT_MAX = 4;
        static final int INT_MIN = 5;
        static final int INT_PRODUCT = 6;
        static final int INT_STD_DEV = 7;
        static final int INT_STD_DEVP = 8;
        static final int INT_SUM = 9;
        static final int INT_VAR = 10;
        static final int INT_VARP = 11;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("average", 1), new Enum("count", 2), new Enum("countNums", 3), new Enum("max", 4), new Enum("min", 5), new Enum("product", 6), new Enum("stdDev", 7), new Enum("stdDevp", 8), new Enum("sum", 9), new Enum("var", 10), new Enum("varp", 11) });
        }
    }
}
