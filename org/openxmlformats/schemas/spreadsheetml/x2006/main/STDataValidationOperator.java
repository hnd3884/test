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

public interface STDataValidationOperator extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STDataValidationOperator.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stdatavalidationoperatore0e0type");
    public static final Enum BETWEEN = Enum.forString("between");
    public static final Enum NOT_BETWEEN = Enum.forString("notBetween");
    public static final Enum EQUAL = Enum.forString("equal");
    public static final Enum NOT_EQUAL = Enum.forString("notEqual");
    public static final Enum LESS_THAN = Enum.forString("lessThan");
    public static final Enum LESS_THAN_OR_EQUAL = Enum.forString("lessThanOrEqual");
    public static final Enum GREATER_THAN = Enum.forString("greaterThan");
    public static final Enum GREATER_THAN_OR_EQUAL = Enum.forString("greaterThanOrEqual");
    public static final int INT_BETWEEN = 1;
    public static final int INT_NOT_BETWEEN = 2;
    public static final int INT_EQUAL = 3;
    public static final int INT_NOT_EQUAL = 4;
    public static final int INT_LESS_THAN = 5;
    public static final int INT_LESS_THAN_OR_EQUAL = 6;
    public static final int INT_GREATER_THAN = 7;
    public static final int INT_GREATER_THAN_OR_EQUAL = 8;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STDataValidationOperator newValue(final Object o) {
            return (STDataValidationOperator)STDataValidationOperator.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STDataValidationOperator.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STDataValidationOperator newInstance() {
            return (STDataValidationOperator)getTypeLoader().newInstance(STDataValidationOperator.type, (XmlOptions)null);
        }
        
        public static STDataValidationOperator newInstance(final XmlOptions xmlOptions) {
            return (STDataValidationOperator)getTypeLoader().newInstance(STDataValidationOperator.type, xmlOptions);
        }
        
        public static STDataValidationOperator parse(final String s) throws XmlException {
            return (STDataValidationOperator)getTypeLoader().parse(s, STDataValidationOperator.type, (XmlOptions)null);
        }
        
        public static STDataValidationOperator parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STDataValidationOperator)getTypeLoader().parse(s, STDataValidationOperator.type, xmlOptions);
        }
        
        public static STDataValidationOperator parse(final File file) throws XmlException, IOException {
            return (STDataValidationOperator)getTypeLoader().parse(file, STDataValidationOperator.type, (XmlOptions)null);
        }
        
        public static STDataValidationOperator parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationOperator)getTypeLoader().parse(file, STDataValidationOperator.type, xmlOptions);
        }
        
        public static STDataValidationOperator parse(final URL url) throws XmlException, IOException {
            return (STDataValidationOperator)getTypeLoader().parse(url, STDataValidationOperator.type, (XmlOptions)null);
        }
        
        public static STDataValidationOperator parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationOperator)getTypeLoader().parse(url, STDataValidationOperator.type, xmlOptions);
        }
        
        public static STDataValidationOperator parse(final InputStream inputStream) throws XmlException, IOException {
            return (STDataValidationOperator)getTypeLoader().parse(inputStream, STDataValidationOperator.type, (XmlOptions)null);
        }
        
        public static STDataValidationOperator parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationOperator)getTypeLoader().parse(inputStream, STDataValidationOperator.type, xmlOptions);
        }
        
        public static STDataValidationOperator parse(final Reader reader) throws XmlException, IOException {
            return (STDataValidationOperator)getTypeLoader().parse(reader, STDataValidationOperator.type, (XmlOptions)null);
        }
        
        public static STDataValidationOperator parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationOperator)getTypeLoader().parse(reader, STDataValidationOperator.type, xmlOptions);
        }
        
        public static STDataValidationOperator parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STDataValidationOperator)getTypeLoader().parse(xmlStreamReader, STDataValidationOperator.type, (XmlOptions)null);
        }
        
        public static STDataValidationOperator parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STDataValidationOperator)getTypeLoader().parse(xmlStreamReader, STDataValidationOperator.type, xmlOptions);
        }
        
        public static STDataValidationOperator parse(final Node node) throws XmlException {
            return (STDataValidationOperator)getTypeLoader().parse(node, STDataValidationOperator.type, (XmlOptions)null);
        }
        
        public static STDataValidationOperator parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STDataValidationOperator)getTypeLoader().parse(node, STDataValidationOperator.type, xmlOptions);
        }
        
        @Deprecated
        public static STDataValidationOperator parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STDataValidationOperator)getTypeLoader().parse(xmlInputStream, STDataValidationOperator.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STDataValidationOperator parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STDataValidationOperator)getTypeLoader().parse(xmlInputStream, STDataValidationOperator.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDataValidationOperator.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDataValidationOperator.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_BETWEEN = 1;
        static final int INT_NOT_BETWEEN = 2;
        static final int INT_EQUAL = 3;
        static final int INT_NOT_EQUAL = 4;
        static final int INT_LESS_THAN = 5;
        static final int INT_LESS_THAN_OR_EQUAL = 6;
        static final int INT_GREATER_THAN = 7;
        static final int INT_GREATER_THAN_OR_EQUAL = 8;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("between", 1), new Enum("notBetween", 2), new Enum("equal", 3), new Enum("notEqual", 4), new Enum("lessThan", 5), new Enum("lessThanOrEqual", 6), new Enum("greaterThan", 7), new Enum("greaterThanOrEqual", 8) });
        }
    }
}
