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

public interface STConnectType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STConnectType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stconnecttype97adtype");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum RECT = Enum.forString("rect");
    public static final Enum SEGMENTS = Enum.forString("segments");
    public static final Enum CUSTOM = Enum.forString("custom");
    public static final int INT_NONE = 1;
    public static final int INT_RECT = 2;
    public static final int INT_SEGMENTS = 3;
    public static final int INT_CUSTOM = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STConnectType newValue(final Object o) {
            return (STConnectType)STConnectType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STConnectType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STConnectType newInstance() {
            return (STConnectType)getTypeLoader().newInstance(STConnectType.type, (XmlOptions)null);
        }
        
        public static STConnectType newInstance(final XmlOptions xmlOptions) {
            return (STConnectType)getTypeLoader().newInstance(STConnectType.type, xmlOptions);
        }
        
        public static STConnectType parse(final String s) throws XmlException {
            return (STConnectType)getTypeLoader().parse(s, STConnectType.type, (XmlOptions)null);
        }
        
        public static STConnectType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STConnectType)getTypeLoader().parse(s, STConnectType.type, xmlOptions);
        }
        
        public static STConnectType parse(final File file) throws XmlException, IOException {
            return (STConnectType)getTypeLoader().parse(file, STConnectType.type, (XmlOptions)null);
        }
        
        public static STConnectType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STConnectType)getTypeLoader().parse(file, STConnectType.type, xmlOptions);
        }
        
        public static STConnectType parse(final URL url) throws XmlException, IOException {
            return (STConnectType)getTypeLoader().parse(url, STConnectType.type, (XmlOptions)null);
        }
        
        public static STConnectType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STConnectType)getTypeLoader().parse(url, STConnectType.type, xmlOptions);
        }
        
        public static STConnectType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STConnectType)getTypeLoader().parse(inputStream, STConnectType.type, (XmlOptions)null);
        }
        
        public static STConnectType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STConnectType)getTypeLoader().parse(inputStream, STConnectType.type, xmlOptions);
        }
        
        public static STConnectType parse(final Reader reader) throws XmlException, IOException {
            return (STConnectType)getTypeLoader().parse(reader, STConnectType.type, (XmlOptions)null);
        }
        
        public static STConnectType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STConnectType)getTypeLoader().parse(reader, STConnectType.type, xmlOptions);
        }
        
        public static STConnectType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STConnectType)getTypeLoader().parse(xmlStreamReader, STConnectType.type, (XmlOptions)null);
        }
        
        public static STConnectType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STConnectType)getTypeLoader().parse(xmlStreamReader, STConnectType.type, xmlOptions);
        }
        
        public static STConnectType parse(final Node node) throws XmlException {
            return (STConnectType)getTypeLoader().parse(node, STConnectType.type, (XmlOptions)null);
        }
        
        public static STConnectType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STConnectType)getTypeLoader().parse(node, STConnectType.type, xmlOptions);
        }
        
        @Deprecated
        public static STConnectType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STConnectType)getTypeLoader().parse(xmlInputStream, STConnectType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STConnectType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STConnectType)getTypeLoader().parse(xmlInputStream, STConnectType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STConnectType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STConnectType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_RECT = 2;
        static final int INT_SEGMENTS = 3;
        static final int INT_CUSTOM = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("rect", 2), new Enum("segments", 3), new Enum("custom", 4) });
        }
    }
}
