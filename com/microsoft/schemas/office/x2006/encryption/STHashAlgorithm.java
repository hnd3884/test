package com.microsoft.schemas.office.x2006.encryption;

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
import org.apache.xmlbeans.XmlToken;

public interface STHashAlgorithm extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHashAlgorithm.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("sthashalgorithm65e3type");
    public static final Enum SHA_1 = Enum.forString("SHA1");
    public static final Enum SHA_256 = Enum.forString("SHA256");
    public static final Enum SHA_384 = Enum.forString("SHA384");
    public static final Enum SHA_512 = Enum.forString("SHA512");
    public static final Enum MD_5 = Enum.forString("MD5");
    public static final Enum MD_4 = Enum.forString("MD4");
    public static final Enum MD_2 = Enum.forString("MD2");
    public static final Enum RIPEMD_128 = Enum.forString("RIPEMD-128");
    public static final Enum RIPEMD_160 = Enum.forString("RIPEMD-160");
    public static final Enum WHIRLPOOL = Enum.forString("WHIRLPOOL");
    public static final int INT_SHA_1 = 1;
    public static final int INT_SHA_256 = 2;
    public static final int INT_SHA_384 = 3;
    public static final int INT_SHA_512 = 4;
    public static final int INT_MD_5 = 5;
    public static final int INT_MD_4 = 6;
    public static final int INT_MD_2 = 7;
    public static final int INT_RIPEMD_128 = 8;
    public static final int INT_RIPEMD_160 = 9;
    public static final int INT_WHIRLPOOL = 10;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHashAlgorithm newValue(final Object o) {
            return (STHashAlgorithm)STHashAlgorithm.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHashAlgorithm.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHashAlgorithm newInstance() {
            return (STHashAlgorithm)getTypeLoader().newInstance(STHashAlgorithm.type, (XmlOptions)null);
        }
        
        public static STHashAlgorithm newInstance(final XmlOptions xmlOptions) {
            return (STHashAlgorithm)getTypeLoader().newInstance(STHashAlgorithm.type, xmlOptions);
        }
        
        public static STHashAlgorithm parse(final String s) throws XmlException {
            return (STHashAlgorithm)getTypeLoader().parse(s, STHashAlgorithm.type, (XmlOptions)null);
        }
        
        public static STHashAlgorithm parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHashAlgorithm)getTypeLoader().parse(s, STHashAlgorithm.type, xmlOptions);
        }
        
        public static STHashAlgorithm parse(final File file) throws XmlException, IOException {
            return (STHashAlgorithm)getTypeLoader().parse(file, STHashAlgorithm.type, (XmlOptions)null);
        }
        
        public static STHashAlgorithm parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHashAlgorithm)getTypeLoader().parse(file, STHashAlgorithm.type, xmlOptions);
        }
        
        public static STHashAlgorithm parse(final URL url) throws XmlException, IOException {
            return (STHashAlgorithm)getTypeLoader().parse(url, STHashAlgorithm.type, (XmlOptions)null);
        }
        
        public static STHashAlgorithm parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHashAlgorithm)getTypeLoader().parse(url, STHashAlgorithm.type, xmlOptions);
        }
        
        public static STHashAlgorithm parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHashAlgorithm)getTypeLoader().parse(inputStream, STHashAlgorithm.type, (XmlOptions)null);
        }
        
        public static STHashAlgorithm parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHashAlgorithm)getTypeLoader().parse(inputStream, STHashAlgorithm.type, xmlOptions);
        }
        
        public static STHashAlgorithm parse(final Reader reader) throws XmlException, IOException {
            return (STHashAlgorithm)getTypeLoader().parse(reader, STHashAlgorithm.type, (XmlOptions)null);
        }
        
        public static STHashAlgorithm parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHashAlgorithm)getTypeLoader().parse(reader, STHashAlgorithm.type, xmlOptions);
        }
        
        public static STHashAlgorithm parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHashAlgorithm)getTypeLoader().parse(xmlStreamReader, STHashAlgorithm.type, (XmlOptions)null);
        }
        
        public static STHashAlgorithm parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHashAlgorithm)getTypeLoader().parse(xmlStreamReader, STHashAlgorithm.type, xmlOptions);
        }
        
        public static STHashAlgorithm parse(final Node node) throws XmlException {
            return (STHashAlgorithm)getTypeLoader().parse(node, STHashAlgorithm.type, (XmlOptions)null);
        }
        
        public static STHashAlgorithm parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHashAlgorithm)getTypeLoader().parse(node, STHashAlgorithm.type, xmlOptions);
        }
        
        @Deprecated
        public static STHashAlgorithm parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHashAlgorithm)getTypeLoader().parse(xmlInputStream, STHashAlgorithm.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHashAlgorithm parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHashAlgorithm)getTypeLoader().parse(xmlInputStream, STHashAlgorithm.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHashAlgorithm.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHashAlgorithm.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SHA_1 = 1;
        static final int INT_SHA_256 = 2;
        static final int INT_SHA_384 = 3;
        static final int INT_SHA_512 = 4;
        static final int INT_MD_5 = 5;
        static final int INT_MD_4 = 6;
        static final int INT_MD_2 = 7;
        static final int INT_RIPEMD_128 = 8;
        static final int INT_RIPEMD_160 = 9;
        static final int INT_WHIRLPOOL = 10;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("SHA1", 1), new Enum("SHA256", 2), new Enum("SHA384", 3), new Enum("SHA512", 4), new Enum("MD5", 5), new Enum("MD4", 6), new Enum("MD2", 7), new Enum("RIPEMD-128", 8), new Enum("RIPEMD-160", 9), new Enum("WHIRLPOOL", 10) });
        }
    }
}
