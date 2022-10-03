package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface STCryptProv extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCryptProv.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcryptprov6ccbtype");
    public static final Enum RSA_AES = Enum.forString("rsaAES");
    public static final Enum RSA_FULL = Enum.forString("rsaFull");
    public static final int INT_RSA_AES = 1;
    public static final int INT_RSA_FULL = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCryptProv newValue(final Object o) {
            return (STCryptProv)STCryptProv.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCryptProv.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCryptProv newInstance() {
            return (STCryptProv)getTypeLoader().newInstance(STCryptProv.type, (XmlOptions)null);
        }
        
        public static STCryptProv newInstance(final XmlOptions xmlOptions) {
            return (STCryptProv)getTypeLoader().newInstance(STCryptProv.type, xmlOptions);
        }
        
        public static STCryptProv parse(final String s) throws XmlException {
            return (STCryptProv)getTypeLoader().parse(s, STCryptProv.type, (XmlOptions)null);
        }
        
        public static STCryptProv parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCryptProv)getTypeLoader().parse(s, STCryptProv.type, xmlOptions);
        }
        
        public static STCryptProv parse(final File file) throws XmlException, IOException {
            return (STCryptProv)getTypeLoader().parse(file, STCryptProv.type, (XmlOptions)null);
        }
        
        public static STCryptProv parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCryptProv)getTypeLoader().parse(file, STCryptProv.type, xmlOptions);
        }
        
        public static STCryptProv parse(final URL url) throws XmlException, IOException {
            return (STCryptProv)getTypeLoader().parse(url, STCryptProv.type, (XmlOptions)null);
        }
        
        public static STCryptProv parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCryptProv)getTypeLoader().parse(url, STCryptProv.type, xmlOptions);
        }
        
        public static STCryptProv parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCryptProv)getTypeLoader().parse(inputStream, STCryptProv.type, (XmlOptions)null);
        }
        
        public static STCryptProv parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCryptProv)getTypeLoader().parse(inputStream, STCryptProv.type, xmlOptions);
        }
        
        public static STCryptProv parse(final Reader reader) throws XmlException, IOException {
            return (STCryptProv)getTypeLoader().parse(reader, STCryptProv.type, (XmlOptions)null);
        }
        
        public static STCryptProv parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCryptProv)getTypeLoader().parse(reader, STCryptProv.type, xmlOptions);
        }
        
        public static STCryptProv parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCryptProv)getTypeLoader().parse(xmlStreamReader, STCryptProv.type, (XmlOptions)null);
        }
        
        public static STCryptProv parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCryptProv)getTypeLoader().parse(xmlStreamReader, STCryptProv.type, xmlOptions);
        }
        
        public static STCryptProv parse(final Node node) throws XmlException {
            return (STCryptProv)getTypeLoader().parse(node, STCryptProv.type, (XmlOptions)null);
        }
        
        public static STCryptProv parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCryptProv)getTypeLoader().parse(node, STCryptProv.type, xmlOptions);
        }
        
        @Deprecated
        public static STCryptProv parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCryptProv)getTypeLoader().parse(xmlInputStream, STCryptProv.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCryptProv parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCryptProv)getTypeLoader().parse(xmlInputStream, STCryptProv.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCryptProv.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCryptProv.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_RSA_AES = 1;
        static final int INT_RSA_FULL = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("rsaAES", 1), new Enum("rsaFull", 2) });
        }
    }
}
