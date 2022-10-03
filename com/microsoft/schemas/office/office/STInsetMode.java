package com.microsoft.schemas.office.office;

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

public interface STInsetMode extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STInsetMode.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stinsetmode3b89type");
    public static final Enum AUTO = Enum.forString("auto");
    public static final Enum CUSTOM = Enum.forString("custom");
    public static final int INT_AUTO = 1;
    public static final int INT_CUSTOM = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STInsetMode newValue(final Object o) {
            return (STInsetMode)STInsetMode.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STInsetMode.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STInsetMode newInstance() {
            return (STInsetMode)getTypeLoader().newInstance(STInsetMode.type, (XmlOptions)null);
        }
        
        public static STInsetMode newInstance(final XmlOptions xmlOptions) {
            return (STInsetMode)getTypeLoader().newInstance(STInsetMode.type, xmlOptions);
        }
        
        public static STInsetMode parse(final String s) throws XmlException {
            return (STInsetMode)getTypeLoader().parse(s, STInsetMode.type, (XmlOptions)null);
        }
        
        public static STInsetMode parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STInsetMode)getTypeLoader().parse(s, STInsetMode.type, xmlOptions);
        }
        
        public static STInsetMode parse(final File file) throws XmlException, IOException {
            return (STInsetMode)getTypeLoader().parse(file, STInsetMode.type, (XmlOptions)null);
        }
        
        public static STInsetMode parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STInsetMode)getTypeLoader().parse(file, STInsetMode.type, xmlOptions);
        }
        
        public static STInsetMode parse(final URL url) throws XmlException, IOException {
            return (STInsetMode)getTypeLoader().parse(url, STInsetMode.type, (XmlOptions)null);
        }
        
        public static STInsetMode parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STInsetMode)getTypeLoader().parse(url, STInsetMode.type, xmlOptions);
        }
        
        public static STInsetMode parse(final InputStream inputStream) throws XmlException, IOException {
            return (STInsetMode)getTypeLoader().parse(inputStream, STInsetMode.type, (XmlOptions)null);
        }
        
        public static STInsetMode parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STInsetMode)getTypeLoader().parse(inputStream, STInsetMode.type, xmlOptions);
        }
        
        public static STInsetMode parse(final Reader reader) throws XmlException, IOException {
            return (STInsetMode)getTypeLoader().parse(reader, STInsetMode.type, (XmlOptions)null);
        }
        
        public static STInsetMode parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STInsetMode)getTypeLoader().parse(reader, STInsetMode.type, xmlOptions);
        }
        
        public static STInsetMode parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STInsetMode)getTypeLoader().parse(xmlStreamReader, STInsetMode.type, (XmlOptions)null);
        }
        
        public static STInsetMode parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STInsetMode)getTypeLoader().parse(xmlStreamReader, STInsetMode.type, xmlOptions);
        }
        
        public static STInsetMode parse(final Node node) throws XmlException {
            return (STInsetMode)getTypeLoader().parse(node, STInsetMode.type, (XmlOptions)null);
        }
        
        public static STInsetMode parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STInsetMode)getTypeLoader().parse(node, STInsetMode.type, xmlOptions);
        }
        
        @Deprecated
        public static STInsetMode parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STInsetMode)getTypeLoader().parse(xmlInputStream, STInsetMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STInsetMode parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STInsetMode)getTypeLoader().parse(xmlInputStream, STInsetMode.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STInsetMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STInsetMode.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_AUTO = 1;
        static final int INT_CUSTOM = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("auto", 1), new Enum("custom", 2) });
        }
    }
}
