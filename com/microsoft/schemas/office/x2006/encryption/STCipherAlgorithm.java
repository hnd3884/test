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

public interface STCipherAlgorithm extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCipherAlgorithm.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("stcipheralgorithme346type");
    public static final Enum AES = Enum.forString("AES");
    public static final Enum RC_2 = Enum.forString("RC2");
    public static final Enum RC_4 = Enum.forString("RC4");
    public static final Enum DES = Enum.forString("DES");
    public static final Enum DESX = Enum.forString("DESX");
    public static final Enum X_3_DES = Enum.forString("3DES");
    public static final Enum X_3_DES_112 = Enum.forString("3DES_112");
    public static final int INT_AES = 1;
    public static final int INT_RC_2 = 2;
    public static final int INT_RC_4 = 3;
    public static final int INT_DES = 4;
    public static final int INT_DESX = 5;
    public static final int INT_X_3_DES = 6;
    public static final int INT_X_3_DES_112 = 7;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCipherAlgorithm newValue(final Object o) {
            return (STCipherAlgorithm)STCipherAlgorithm.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCipherAlgorithm.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCipherAlgorithm newInstance() {
            return (STCipherAlgorithm)getTypeLoader().newInstance(STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        public static STCipherAlgorithm newInstance(final XmlOptions xmlOptions) {
            return (STCipherAlgorithm)getTypeLoader().newInstance(STCipherAlgorithm.type, xmlOptions);
        }
        
        public static STCipherAlgorithm parse(final String s) throws XmlException {
            return (STCipherAlgorithm)getTypeLoader().parse(s, STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        public static STCipherAlgorithm parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCipherAlgorithm)getTypeLoader().parse(s, STCipherAlgorithm.type, xmlOptions);
        }
        
        public static STCipherAlgorithm parse(final File file) throws XmlException, IOException {
            return (STCipherAlgorithm)getTypeLoader().parse(file, STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        public static STCipherAlgorithm parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCipherAlgorithm)getTypeLoader().parse(file, STCipherAlgorithm.type, xmlOptions);
        }
        
        public static STCipherAlgorithm parse(final URL url) throws XmlException, IOException {
            return (STCipherAlgorithm)getTypeLoader().parse(url, STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        public static STCipherAlgorithm parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCipherAlgorithm)getTypeLoader().parse(url, STCipherAlgorithm.type, xmlOptions);
        }
        
        public static STCipherAlgorithm parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCipherAlgorithm)getTypeLoader().parse(inputStream, STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        public static STCipherAlgorithm parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCipherAlgorithm)getTypeLoader().parse(inputStream, STCipherAlgorithm.type, xmlOptions);
        }
        
        public static STCipherAlgorithm parse(final Reader reader) throws XmlException, IOException {
            return (STCipherAlgorithm)getTypeLoader().parse(reader, STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        public static STCipherAlgorithm parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCipherAlgorithm)getTypeLoader().parse(reader, STCipherAlgorithm.type, xmlOptions);
        }
        
        public static STCipherAlgorithm parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCipherAlgorithm)getTypeLoader().parse(xmlStreamReader, STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        public static STCipherAlgorithm parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCipherAlgorithm)getTypeLoader().parse(xmlStreamReader, STCipherAlgorithm.type, xmlOptions);
        }
        
        public static STCipherAlgorithm parse(final Node node) throws XmlException {
            return (STCipherAlgorithm)getTypeLoader().parse(node, STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        public static STCipherAlgorithm parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCipherAlgorithm)getTypeLoader().parse(node, STCipherAlgorithm.type, xmlOptions);
        }
        
        @Deprecated
        public static STCipherAlgorithm parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCipherAlgorithm)getTypeLoader().parse(xmlInputStream, STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCipherAlgorithm parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCipherAlgorithm)getTypeLoader().parse(xmlInputStream, STCipherAlgorithm.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCipherAlgorithm.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCipherAlgorithm.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_AES = 1;
        static final int INT_RC_2 = 2;
        static final int INT_RC_4 = 3;
        static final int INT_DES = 4;
        static final int INT_DESX = 5;
        static final int INT_X_3_DES = 6;
        static final int INT_X_3_DES_112 = 7;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("AES", 1), new Enum("RC2", 2), new Enum("RC4", 3), new Enum("DES", 4), new Enum("DESX", 5), new Enum("3DES", 6), new Enum("3DES_112", 7) });
        }
    }
}
