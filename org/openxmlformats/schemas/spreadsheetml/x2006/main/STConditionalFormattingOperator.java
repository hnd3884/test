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

public interface STConditionalFormattingOperator extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STConditionalFormattingOperator.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stconditionalformattingoperatora99etype");
    public static final Enum LESS_THAN = Enum.forString("lessThan");
    public static final Enum LESS_THAN_OR_EQUAL = Enum.forString("lessThanOrEqual");
    public static final Enum EQUAL = Enum.forString("equal");
    public static final Enum NOT_EQUAL = Enum.forString("notEqual");
    public static final Enum GREATER_THAN_OR_EQUAL = Enum.forString("greaterThanOrEqual");
    public static final Enum GREATER_THAN = Enum.forString("greaterThan");
    public static final Enum BETWEEN = Enum.forString("between");
    public static final Enum NOT_BETWEEN = Enum.forString("notBetween");
    public static final Enum CONTAINS_TEXT = Enum.forString("containsText");
    public static final Enum NOT_CONTAINS = Enum.forString("notContains");
    public static final Enum BEGINS_WITH = Enum.forString("beginsWith");
    public static final Enum ENDS_WITH = Enum.forString("endsWith");
    public static final int INT_LESS_THAN = 1;
    public static final int INT_LESS_THAN_OR_EQUAL = 2;
    public static final int INT_EQUAL = 3;
    public static final int INT_NOT_EQUAL = 4;
    public static final int INT_GREATER_THAN_OR_EQUAL = 5;
    public static final int INT_GREATER_THAN = 6;
    public static final int INT_BETWEEN = 7;
    public static final int INT_NOT_BETWEEN = 8;
    public static final int INT_CONTAINS_TEXT = 9;
    public static final int INT_NOT_CONTAINS = 10;
    public static final int INT_BEGINS_WITH = 11;
    public static final int INT_ENDS_WITH = 12;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STConditionalFormattingOperator newValue(final Object o) {
            return (STConditionalFormattingOperator)STConditionalFormattingOperator.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STConditionalFormattingOperator.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STConditionalFormattingOperator newInstance() {
            return (STConditionalFormattingOperator)getTypeLoader().newInstance(STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        public static STConditionalFormattingOperator newInstance(final XmlOptions xmlOptions) {
            return (STConditionalFormattingOperator)getTypeLoader().newInstance(STConditionalFormattingOperator.type, xmlOptions);
        }
        
        public static STConditionalFormattingOperator parse(final String s) throws XmlException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(s, STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        public static STConditionalFormattingOperator parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(s, STConditionalFormattingOperator.type, xmlOptions);
        }
        
        public static STConditionalFormattingOperator parse(final File file) throws XmlException, IOException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(file, STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        public static STConditionalFormattingOperator parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(file, STConditionalFormattingOperator.type, xmlOptions);
        }
        
        public static STConditionalFormattingOperator parse(final URL url) throws XmlException, IOException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(url, STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        public static STConditionalFormattingOperator parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(url, STConditionalFormattingOperator.type, xmlOptions);
        }
        
        public static STConditionalFormattingOperator parse(final InputStream inputStream) throws XmlException, IOException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(inputStream, STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        public static STConditionalFormattingOperator parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(inputStream, STConditionalFormattingOperator.type, xmlOptions);
        }
        
        public static STConditionalFormattingOperator parse(final Reader reader) throws XmlException, IOException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(reader, STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        public static STConditionalFormattingOperator parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(reader, STConditionalFormattingOperator.type, xmlOptions);
        }
        
        public static STConditionalFormattingOperator parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(xmlStreamReader, STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        public static STConditionalFormattingOperator parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(xmlStreamReader, STConditionalFormattingOperator.type, xmlOptions);
        }
        
        public static STConditionalFormattingOperator parse(final Node node) throws XmlException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(node, STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        public static STConditionalFormattingOperator parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(node, STConditionalFormattingOperator.type, xmlOptions);
        }
        
        @Deprecated
        public static STConditionalFormattingOperator parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(xmlInputStream, STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STConditionalFormattingOperator parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STConditionalFormattingOperator)getTypeLoader().parse(xmlInputStream, STConditionalFormattingOperator.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STConditionalFormattingOperator.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STConditionalFormattingOperator.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_LESS_THAN = 1;
        static final int INT_LESS_THAN_OR_EQUAL = 2;
        static final int INT_EQUAL = 3;
        static final int INT_NOT_EQUAL = 4;
        static final int INT_GREATER_THAN_OR_EQUAL = 5;
        static final int INT_GREATER_THAN = 6;
        static final int INT_BETWEEN = 7;
        static final int INT_NOT_BETWEEN = 8;
        static final int INT_CONTAINS_TEXT = 9;
        static final int INT_NOT_CONTAINS = 10;
        static final int INT_BEGINS_WITH = 11;
        static final int INT_ENDS_WITH = 12;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("lessThan", 1), new Enum("lessThanOrEqual", 2), new Enum("equal", 3), new Enum("notEqual", 4), new Enum("greaterThanOrEqual", 5), new Enum("greaterThan", 6), new Enum("between", 7), new Enum("notBetween", 8), new Enum("containsText", 9), new Enum("notContains", 10), new Enum("beginsWith", 11), new Enum("endsWith", 12) });
        }
    }
}
