package com.microsoft.schemas.vml;

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

public interface STTrueFalse extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTrueFalse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttruefalse4ab9type");
    public static final Enum T = Enum.forString("t");
    public static final Enum F = Enum.forString("f");
    public static final Enum TRUE = Enum.forString("true");
    public static final Enum FALSE = Enum.forString("false");
    public static final int INT_T = 1;
    public static final int INT_F = 2;
    public static final int INT_TRUE = 3;
    public static final int INT_FALSE = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTrueFalse newValue(final Object o) {
            return (STTrueFalse)STTrueFalse.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTrueFalse.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTrueFalse newInstance() {
            return (STTrueFalse)getTypeLoader().newInstance(STTrueFalse.type, (XmlOptions)null);
        }
        
        public static STTrueFalse newInstance(final XmlOptions xmlOptions) {
            return (STTrueFalse)getTypeLoader().newInstance(STTrueFalse.type, xmlOptions);
        }
        
        public static STTrueFalse parse(final String s) throws XmlException {
            return (STTrueFalse)getTypeLoader().parse(s, STTrueFalse.type, (XmlOptions)null);
        }
        
        public static STTrueFalse parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTrueFalse)getTypeLoader().parse(s, STTrueFalse.type, xmlOptions);
        }
        
        public static STTrueFalse parse(final File file) throws XmlException, IOException {
            return (STTrueFalse)getTypeLoader().parse(file, STTrueFalse.type, (XmlOptions)null);
        }
        
        public static STTrueFalse parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTrueFalse)getTypeLoader().parse(file, STTrueFalse.type, xmlOptions);
        }
        
        public static STTrueFalse parse(final URL url) throws XmlException, IOException {
            return (STTrueFalse)getTypeLoader().parse(url, STTrueFalse.type, (XmlOptions)null);
        }
        
        public static STTrueFalse parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTrueFalse)getTypeLoader().parse(url, STTrueFalse.type, xmlOptions);
        }
        
        public static STTrueFalse parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTrueFalse)getTypeLoader().parse(inputStream, STTrueFalse.type, (XmlOptions)null);
        }
        
        public static STTrueFalse parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTrueFalse)getTypeLoader().parse(inputStream, STTrueFalse.type, xmlOptions);
        }
        
        public static STTrueFalse parse(final Reader reader) throws XmlException, IOException {
            return (STTrueFalse)getTypeLoader().parse(reader, STTrueFalse.type, (XmlOptions)null);
        }
        
        public static STTrueFalse parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTrueFalse)getTypeLoader().parse(reader, STTrueFalse.type, xmlOptions);
        }
        
        public static STTrueFalse parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTrueFalse)getTypeLoader().parse(xmlStreamReader, STTrueFalse.type, (XmlOptions)null);
        }
        
        public static STTrueFalse parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTrueFalse)getTypeLoader().parse(xmlStreamReader, STTrueFalse.type, xmlOptions);
        }
        
        public static STTrueFalse parse(final Node node) throws XmlException {
            return (STTrueFalse)getTypeLoader().parse(node, STTrueFalse.type, (XmlOptions)null);
        }
        
        public static STTrueFalse parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTrueFalse)getTypeLoader().parse(node, STTrueFalse.type, xmlOptions);
        }
        
        @Deprecated
        public static STTrueFalse parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTrueFalse)getTypeLoader().parse(xmlInputStream, STTrueFalse.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTrueFalse parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTrueFalse)getTypeLoader().parse(xmlInputStream, STTrueFalse.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTrueFalse.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTrueFalse.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_T = 1;
        static final int INT_F = 2;
        static final int INT_TRUE = 3;
        static final int INT_FALSE = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("t", 1), new Enum("f", 2), new Enum("true", 3), new Enum("false", 4) });
        }
    }
}
