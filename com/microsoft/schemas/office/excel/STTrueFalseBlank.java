package com.microsoft.schemas.office.excel;

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

public interface STTrueFalseBlank extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTrueFalseBlank.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttruefalseblanka061type");
    public static final Enum TRUE = Enum.forString("True");
    public static final Enum T = Enum.forString("t");
    public static final Enum FALSE = Enum.forString("False");
    public static final Enum F = Enum.forString("f");
    public static final Enum X = Enum.forString("");
    public static final int INT_TRUE = 1;
    public static final int INT_T = 2;
    public static final int INT_FALSE = 3;
    public static final int INT_F = 4;
    public static final int INT_X = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTrueFalseBlank newValue(final Object o) {
            return (STTrueFalseBlank)STTrueFalseBlank.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTrueFalseBlank.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTrueFalseBlank newInstance() {
            return (STTrueFalseBlank)getTypeLoader().newInstance(STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        public static STTrueFalseBlank newInstance(final XmlOptions xmlOptions) {
            return (STTrueFalseBlank)getTypeLoader().newInstance(STTrueFalseBlank.type, xmlOptions);
        }
        
        public static STTrueFalseBlank parse(final String s) throws XmlException {
            return (STTrueFalseBlank)getTypeLoader().parse(s, STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        public static STTrueFalseBlank parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTrueFalseBlank)getTypeLoader().parse(s, STTrueFalseBlank.type, xmlOptions);
        }
        
        public static STTrueFalseBlank parse(final File file) throws XmlException, IOException {
            return (STTrueFalseBlank)getTypeLoader().parse(file, STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        public static STTrueFalseBlank parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTrueFalseBlank)getTypeLoader().parse(file, STTrueFalseBlank.type, xmlOptions);
        }
        
        public static STTrueFalseBlank parse(final URL url) throws XmlException, IOException {
            return (STTrueFalseBlank)getTypeLoader().parse(url, STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        public static STTrueFalseBlank parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTrueFalseBlank)getTypeLoader().parse(url, STTrueFalseBlank.type, xmlOptions);
        }
        
        public static STTrueFalseBlank parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTrueFalseBlank)getTypeLoader().parse(inputStream, STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        public static STTrueFalseBlank parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTrueFalseBlank)getTypeLoader().parse(inputStream, STTrueFalseBlank.type, xmlOptions);
        }
        
        public static STTrueFalseBlank parse(final Reader reader) throws XmlException, IOException {
            return (STTrueFalseBlank)getTypeLoader().parse(reader, STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        public static STTrueFalseBlank parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTrueFalseBlank)getTypeLoader().parse(reader, STTrueFalseBlank.type, xmlOptions);
        }
        
        public static STTrueFalseBlank parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTrueFalseBlank)getTypeLoader().parse(xmlStreamReader, STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        public static STTrueFalseBlank parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTrueFalseBlank)getTypeLoader().parse(xmlStreamReader, STTrueFalseBlank.type, xmlOptions);
        }
        
        public static STTrueFalseBlank parse(final Node node) throws XmlException {
            return (STTrueFalseBlank)getTypeLoader().parse(node, STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        public static STTrueFalseBlank parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTrueFalseBlank)getTypeLoader().parse(node, STTrueFalseBlank.type, xmlOptions);
        }
        
        @Deprecated
        public static STTrueFalseBlank parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTrueFalseBlank)getTypeLoader().parse(xmlInputStream, STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTrueFalseBlank parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTrueFalseBlank)getTypeLoader().parse(xmlInputStream, STTrueFalseBlank.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTrueFalseBlank.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTrueFalseBlank.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TRUE = 1;
        static final int INT_T = 2;
        static final int INT_FALSE = 3;
        static final int INT_F = 4;
        static final int INT_X = 5;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("True", 1), new Enum("t", 2), new Enum("False", 3), new Enum("f", 4), new Enum("", 5) });
        }
    }
}
