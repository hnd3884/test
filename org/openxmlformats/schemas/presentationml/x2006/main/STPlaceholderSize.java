package org.openxmlformats.schemas.presentationml.x2006.main;

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

public interface STPlaceholderSize extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPlaceholderSize.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stplaceholdersize914btype");
    public static final Enum FULL = Enum.forString("full");
    public static final Enum HALF = Enum.forString("half");
    public static final Enum QUARTER = Enum.forString("quarter");
    public static final int INT_FULL = 1;
    public static final int INT_HALF = 2;
    public static final int INT_QUARTER = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPlaceholderSize newValue(final Object o) {
            return (STPlaceholderSize)STPlaceholderSize.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPlaceholderSize.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPlaceholderSize newInstance() {
            return (STPlaceholderSize)getTypeLoader().newInstance(STPlaceholderSize.type, (XmlOptions)null);
        }
        
        public static STPlaceholderSize newInstance(final XmlOptions xmlOptions) {
            return (STPlaceholderSize)getTypeLoader().newInstance(STPlaceholderSize.type, xmlOptions);
        }
        
        public static STPlaceholderSize parse(final String s) throws XmlException {
            return (STPlaceholderSize)getTypeLoader().parse(s, STPlaceholderSize.type, (XmlOptions)null);
        }
        
        public static STPlaceholderSize parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPlaceholderSize)getTypeLoader().parse(s, STPlaceholderSize.type, xmlOptions);
        }
        
        public static STPlaceholderSize parse(final File file) throws XmlException, IOException {
            return (STPlaceholderSize)getTypeLoader().parse(file, STPlaceholderSize.type, (XmlOptions)null);
        }
        
        public static STPlaceholderSize parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPlaceholderSize)getTypeLoader().parse(file, STPlaceholderSize.type, xmlOptions);
        }
        
        public static STPlaceholderSize parse(final URL url) throws XmlException, IOException {
            return (STPlaceholderSize)getTypeLoader().parse(url, STPlaceholderSize.type, (XmlOptions)null);
        }
        
        public static STPlaceholderSize parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPlaceholderSize)getTypeLoader().parse(url, STPlaceholderSize.type, xmlOptions);
        }
        
        public static STPlaceholderSize parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPlaceholderSize)getTypeLoader().parse(inputStream, STPlaceholderSize.type, (XmlOptions)null);
        }
        
        public static STPlaceholderSize parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPlaceholderSize)getTypeLoader().parse(inputStream, STPlaceholderSize.type, xmlOptions);
        }
        
        public static STPlaceholderSize parse(final Reader reader) throws XmlException, IOException {
            return (STPlaceholderSize)getTypeLoader().parse(reader, STPlaceholderSize.type, (XmlOptions)null);
        }
        
        public static STPlaceholderSize parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPlaceholderSize)getTypeLoader().parse(reader, STPlaceholderSize.type, xmlOptions);
        }
        
        public static STPlaceholderSize parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPlaceholderSize)getTypeLoader().parse(xmlStreamReader, STPlaceholderSize.type, (XmlOptions)null);
        }
        
        public static STPlaceholderSize parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPlaceholderSize)getTypeLoader().parse(xmlStreamReader, STPlaceholderSize.type, xmlOptions);
        }
        
        public static STPlaceholderSize parse(final Node node) throws XmlException {
            return (STPlaceholderSize)getTypeLoader().parse(node, STPlaceholderSize.type, (XmlOptions)null);
        }
        
        public static STPlaceholderSize parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPlaceholderSize)getTypeLoader().parse(node, STPlaceholderSize.type, xmlOptions);
        }
        
        @Deprecated
        public static STPlaceholderSize parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPlaceholderSize)getTypeLoader().parse(xmlInputStream, STPlaceholderSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPlaceholderSize parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPlaceholderSize)getTypeLoader().parse(xmlInputStream, STPlaceholderSize.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPlaceholderSize.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPlaceholderSize.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_FULL = 1;
        static final int INT_HALF = 2;
        static final int INT_QUARTER = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("full", 1), new Enum("half", 2), new Enum("quarter", 3) });
        }
    }
}
