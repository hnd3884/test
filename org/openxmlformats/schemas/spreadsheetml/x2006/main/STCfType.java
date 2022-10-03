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

public interface STCfType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCfType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcftype8016type");
    public static final Enum EXPRESSION = Enum.forString("expression");
    public static final Enum CELL_IS = Enum.forString("cellIs");
    public static final Enum COLOR_SCALE = Enum.forString("colorScale");
    public static final Enum DATA_BAR = Enum.forString("dataBar");
    public static final Enum ICON_SET = Enum.forString("iconSet");
    public static final Enum TOP_10 = Enum.forString("top10");
    public static final Enum UNIQUE_VALUES = Enum.forString("uniqueValues");
    public static final Enum DUPLICATE_VALUES = Enum.forString("duplicateValues");
    public static final Enum CONTAINS_TEXT = Enum.forString("containsText");
    public static final Enum NOT_CONTAINS_TEXT = Enum.forString("notContainsText");
    public static final Enum BEGINS_WITH = Enum.forString("beginsWith");
    public static final Enum ENDS_WITH = Enum.forString("endsWith");
    public static final Enum CONTAINS_BLANKS = Enum.forString("containsBlanks");
    public static final Enum NOT_CONTAINS_BLANKS = Enum.forString("notContainsBlanks");
    public static final Enum CONTAINS_ERRORS = Enum.forString("containsErrors");
    public static final Enum NOT_CONTAINS_ERRORS = Enum.forString("notContainsErrors");
    public static final Enum TIME_PERIOD = Enum.forString("timePeriod");
    public static final Enum ABOVE_AVERAGE = Enum.forString("aboveAverage");
    public static final int INT_EXPRESSION = 1;
    public static final int INT_CELL_IS = 2;
    public static final int INT_COLOR_SCALE = 3;
    public static final int INT_DATA_BAR = 4;
    public static final int INT_ICON_SET = 5;
    public static final int INT_TOP_10 = 6;
    public static final int INT_UNIQUE_VALUES = 7;
    public static final int INT_DUPLICATE_VALUES = 8;
    public static final int INT_CONTAINS_TEXT = 9;
    public static final int INT_NOT_CONTAINS_TEXT = 10;
    public static final int INT_BEGINS_WITH = 11;
    public static final int INT_ENDS_WITH = 12;
    public static final int INT_CONTAINS_BLANKS = 13;
    public static final int INT_NOT_CONTAINS_BLANKS = 14;
    public static final int INT_CONTAINS_ERRORS = 15;
    public static final int INT_NOT_CONTAINS_ERRORS = 16;
    public static final int INT_TIME_PERIOD = 17;
    public static final int INT_ABOVE_AVERAGE = 18;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCfType newValue(final Object o) {
            return (STCfType)STCfType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCfType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCfType newInstance() {
            return (STCfType)getTypeLoader().newInstance(STCfType.type, (XmlOptions)null);
        }
        
        public static STCfType newInstance(final XmlOptions xmlOptions) {
            return (STCfType)getTypeLoader().newInstance(STCfType.type, xmlOptions);
        }
        
        public static STCfType parse(final String s) throws XmlException {
            return (STCfType)getTypeLoader().parse(s, STCfType.type, (XmlOptions)null);
        }
        
        public static STCfType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCfType)getTypeLoader().parse(s, STCfType.type, xmlOptions);
        }
        
        public static STCfType parse(final File file) throws XmlException, IOException {
            return (STCfType)getTypeLoader().parse(file, STCfType.type, (XmlOptions)null);
        }
        
        public static STCfType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCfType)getTypeLoader().parse(file, STCfType.type, xmlOptions);
        }
        
        public static STCfType parse(final URL url) throws XmlException, IOException {
            return (STCfType)getTypeLoader().parse(url, STCfType.type, (XmlOptions)null);
        }
        
        public static STCfType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCfType)getTypeLoader().parse(url, STCfType.type, xmlOptions);
        }
        
        public static STCfType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCfType)getTypeLoader().parse(inputStream, STCfType.type, (XmlOptions)null);
        }
        
        public static STCfType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCfType)getTypeLoader().parse(inputStream, STCfType.type, xmlOptions);
        }
        
        public static STCfType parse(final Reader reader) throws XmlException, IOException {
            return (STCfType)getTypeLoader().parse(reader, STCfType.type, (XmlOptions)null);
        }
        
        public static STCfType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCfType)getTypeLoader().parse(reader, STCfType.type, xmlOptions);
        }
        
        public static STCfType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCfType)getTypeLoader().parse(xmlStreamReader, STCfType.type, (XmlOptions)null);
        }
        
        public static STCfType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCfType)getTypeLoader().parse(xmlStreamReader, STCfType.type, xmlOptions);
        }
        
        public static STCfType parse(final Node node) throws XmlException {
            return (STCfType)getTypeLoader().parse(node, STCfType.type, (XmlOptions)null);
        }
        
        public static STCfType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCfType)getTypeLoader().parse(node, STCfType.type, xmlOptions);
        }
        
        @Deprecated
        public static STCfType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCfType)getTypeLoader().parse(xmlInputStream, STCfType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCfType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCfType)getTypeLoader().parse(xmlInputStream, STCfType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCfType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCfType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_EXPRESSION = 1;
        static final int INT_CELL_IS = 2;
        static final int INT_COLOR_SCALE = 3;
        static final int INT_DATA_BAR = 4;
        static final int INT_ICON_SET = 5;
        static final int INT_TOP_10 = 6;
        static final int INT_UNIQUE_VALUES = 7;
        static final int INT_DUPLICATE_VALUES = 8;
        static final int INT_CONTAINS_TEXT = 9;
        static final int INT_NOT_CONTAINS_TEXT = 10;
        static final int INT_BEGINS_WITH = 11;
        static final int INT_ENDS_WITH = 12;
        static final int INT_CONTAINS_BLANKS = 13;
        static final int INT_NOT_CONTAINS_BLANKS = 14;
        static final int INT_CONTAINS_ERRORS = 15;
        static final int INT_NOT_CONTAINS_ERRORS = 16;
        static final int INT_TIME_PERIOD = 17;
        static final int INT_ABOVE_AVERAGE = 18;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("expression", 1), new Enum("cellIs", 2), new Enum("colorScale", 3), new Enum("dataBar", 4), new Enum("iconSet", 5), new Enum("top10", 6), new Enum("uniqueValues", 7), new Enum("duplicateValues", 8), new Enum("containsText", 9), new Enum("notContainsText", 10), new Enum("beginsWith", 11), new Enum("endsWith", 12), new Enum("containsBlanks", 13), new Enum("notContainsBlanks", 14), new Enum("containsErrors", 15), new Enum("notContainsErrors", 16), new Enum("timePeriod", 17), new Enum("aboveAverage", 18) });
        }
    }
}
