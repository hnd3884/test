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

public interface STCipherChaining extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCipherChaining.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s8C3F193EE11A2F798ACF65489B9E6078").resolveHandle("stcipherchaining1e98type");
    public static final Enum CHAINING_MODE_CBC = Enum.forString("ChainingModeCBC");
    public static final Enum CHAINING_MODE_CFB = Enum.forString("ChainingModeCFB");
    public static final int INT_CHAINING_MODE_CBC = 1;
    public static final int INT_CHAINING_MODE_CFB = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCipherChaining newValue(final Object o) {
            return (STCipherChaining)STCipherChaining.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCipherChaining.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCipherChaining newInstance() {
            return (STCipherChaining)getTypeLoader().newInstance(STCipherChaining.type, (XmlOptions)null);
        }
        
        public static STCipherChaining newInstance(final XmlOptions xmlOptions) {
            return (STCipherChaining)getTypeLoader().newInstance(STCipherChaining.type, xmlOptions);
        }
        
        public static STCipherChaining parse(final String s) throws XmlException {
            return (STCipherChaining)getTypeLoader().parse(s, STCipherChaining.type, (XmlOptions)null);
        }
        
        public static STCipherChaining parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCipherChaining)getTypeLoader().parse(s, STCipherChaining.type, xmlOptions);
        }
        
        public static STCipherChaining parse(final File file) throws XmlException, IOException {
            return (STCipherChaining)getTypeLoader().parse(file, STCipherChaining.type, (XmlOptions)null);
        }
        
        public static STCipherChaining parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCipherChaining)getTypeLoader().parse(file, STCipherChaining.type, xmlOptions);
        }
        
        public static STCipherChaining parse(final URL url) throws XmlException, IOException {
            return (STCipherChaining)getTypeLoader().parse(url, STCipherChaining.type, (XmlOptions)null);
        }
        
        public static STCipherChaining parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCipherChaining)getTypeLoader().parse(url, STCipherChaining.type, xmlOptions);
        }
        
        public static STCipherChaining parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCipherChaining)getTypeLoader().parse(inputStream, STCipherChaining.type, (XmlOptions)null);
        }
        
        public static STCipherChaining parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCipherChaining)getTypeLoader().parse(inputStream, STCipherChaining.type, xmlOptions);
        }
        
        public static STCipherChaining parse(final Reader reader) throws XmlException, IOException {
            return (STCipherChaining)getTypeLoader().parse(reader, STCipherChaining.type, (XmlOptions)null);
        }
        
        public static STCipherChaining parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCipherChaining)getTypeLoader().parse(reader, STCipherChaining.type, xmlOptions);
        }
        
        public static STCipherChaining parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCipherChaining)getTypeLoader().parse(xmlStreamReader, STCipherChaining.type, (XmlOptions)null);
        }
        
        public static STCipherChaining parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCipherChaining)getTypeLoader().parse(xmlStreamReader, STCipherChaining.type, xmlOptions);
        }
        
        public static STCipherChaining parse(final Node node) throws XmlException {
            return (STCipherChaining)getTypeLoader().parse(node, STCipherChaining.type, (XmlOptions)null);
        }
        
        public static STCipherChaining parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCipherChaining)getTypeLoader().parse(node, STCipherChaining.type, xmlOptions);
        }
        
        @Deprecated
        public static STCipherChaining parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCipherChaining)getTypeLoader().parse(xmlInputStream, STCipherChaining.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCipherChaining parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCipherChaining)getTypeLoader().parse(xmlInputStream, STCipherChaining.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCipherChaining.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCipherChaining.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_CHAINING_MODE_CBC = 1;
        static final int INT_CHAINING_MODE_CFB = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("ChainingModeCBC", 1), new Enum("ChainingModeCFB", 2) });
        }
    }
}
