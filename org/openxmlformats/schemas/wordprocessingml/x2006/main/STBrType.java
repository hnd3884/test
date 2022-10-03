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

public interface STBrType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STBrType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stbrtypeb52etype");
    public static final Enum PAGE = Enum.forString("page");
    public static final Enum COLUMN = Enum.forString("column");
    public static final Enum TEXT_WRAPPING = Enum.forString("textWrapping");
    public static final int INT_PAGE = 1;
    public static final int INT_COLUMN = 2;
    public static final int INT_TEXT_WRAPPING = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STBrType newValue(final Object o) {
            return (STBrType)STBrType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STBrType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STBrType newInstance() {
            return (STBrType)getTypeLoader().newInstance(STBrType.type, (XmlOptions)null);
        }
        
        public static STBrType newInstance(final XmlOptions xmlOptions) {
            return (STBrType)getTypeLoader().newInstance(STBrType.type, xmlOptions);
        }
        
        public static STBrType parse(final String s) throws XmlException {
            return (STBrType)getTypeLoader().parse(s, STBrType.type, (XmlOptions)null);
        }
        
        public static STBrType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STBrType)getTypeLoader().parse(s, STBrType.type, xmlOptions);
        }
        
        public static STBrType parse(final File file) throws XmlException, IOException {
            return (STBrType)getTypeLoader().parse(file, STBrType.type, (XmlOptions)null);
        }
        
        public static STBrType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBrType)getTypeLoader().parse(file, STBrType.type, xmlOptions);
        }
        
        public static STBrType parse(final URL url) throws XmlException, IOException {
            return (STBrType)getTypeLoader().parse(url, STBrType.type, (XmlOptions)null);
        }
        
        public static STBrType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBrType)getTypeLoader().parse(url, STBrType.type, xmlOptions);
        }
        
        public static STBrType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STBrType)getTypeLoader().parse(inputStream, STBrType.type, (XmlOptions)null);
        }
        
        public static STBrType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBrType)getTypeLoader().parse(inputStream, STBrType.type, xmlOptions);
        }
        
        public static STBrType parse(final Reader reader) throws XmlException, IOException {
            return (STBrType)getTypeLoader().parse(reader, STBrType.type, (XmlOptions)null);
        }
        
        public static STBrType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBrType)getTypeLoader().parse(reader, STBrType.type, xmlOptions);
        }
        
        public static STBrType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STBrType)getTypeLoader().parse(xmlStreamReader, STBrType.type, (XmlOptions)null);
        }
        
        public static STBrType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STBrType)getTypeLoader().parse(xmlStreamReader, STBrType.type, xmlOptions);
        }
        
        public static STBrType parse(final Node node) throws XmlException {
            return (STBrType)getTypeLoader().parse(node, STBrType.type, (XmlOptions)null);
        }
        
        public static STBrType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STBrType)getTypeLoader().parse(node, STBrType.type, xmlOptions);
        }
        
        @Deprecated
        public static STBrType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STBrType)getTypeLoader().parse(xmlInputStream, STBrType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STBrType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STBrType)getTypeLoader().parse(xmlInputStream, STBrType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBrType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBrType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_PAGE = 1;
        static final int INT_COLUMN = 2;
        static final int INT_TEXT_WRAPPING = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("page", 1), new Enum("column", 2), new Enum("textWrapping", 3) });
        }
    }
}
