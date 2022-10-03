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

public interface STTabJc extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTabJc.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttabjc10f4type");
    public static final Enum CLEAR = Enum.forString("clear");
    public static final Enum LEFT = Enum.forString("left");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum RIGHT = Enum.forString("right");
    public static final Enum DECIMAL = Enum.forString("decimal");
    public static final Enum BAR = Enum.forString("bar");
    public static final Enum NUM = Enum.forString("num");
    public static final int INT_CLEAR = 1;
    public static final int INT_LEFT = 2;
    public static final int INT_CENTER = 3;
    public static final int INT_RIGHT = 4;
    public static final int INT_DECIMAL = 5;
    public static final int INT_BAR = 6;
    public static final int INT_NUM = 7;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTabJc newValue(final Object o) {
            return (STTabJc)STTabJc.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTabJc.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTabJc newInstance() {
            return (STTabJc)getTypeLoader().newInstance(STTabJc.type, (XmlOptions)null);
        }
        
        public static STTabJc newInstance(final XmlOptions xmlOptions) {
            return (STTabJc)getTypeLoader().newInstance(STTabJc.type, xmlOptions);
        }
        
        public static STTabJc parse(final String s) throws XmlException {
            return (STTabJc)getTypeLoader().parse(s, STTabJc.type, (XmlOptions)null);
        }
        
        public static STTabJc parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTabJc)getTypeLoader().parse(s, STTabJc.type, xmlOptions);
        }
        
        public static STTabJc parse(final File file) throws XmlException, IOException {
            return (STTabJc)getTypeLoader().parse(file, STTabJc.type, (XmlOptions)null);
        }
        
        public static STTabJc parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTabJc)getTypeLoader().parse(file, STTabJc.type, xmlOptions);
        }
        
        public static STTabJc parse(final URL url) throws XmlException, IOException {
            return (STTabJc)getTypeLoader().parse(url, STTabJc.type, (XmlOptions)null);
        }
        
        public static STTabJc parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTabJc)getTypeLoader().parse(url, STTabJc.type, xmlOptions);
        }
        
        public static STTabJc parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTabJc)getTypeLoader().parse(inputStream, STTabJc.type, (XmlOptions)null);
        }
        
        public static STTabJc parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTabJc)getTypeLoader().parse(inputStream, STTabJc.type, xmlOptions);
        }
        
        public static STTabJc parse(final Reader reader) throws XmlException, IOException {
            return (STTabJc)getTypeLoader().parse(reader, STTabJc.type, (XmlOptions)null);
        }
        
        public static STTabJc parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTabJc)getTypeLoader().parse(reader, STTabJc.type, xmlOptions);
        }
        
        public static STTabJc parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTabJc)getTypeLoader().parse(xmlStreamReader, STTabJc.type, (XmlOptions)null);
        }
        
        public static STTabJc parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTabJc)getTypeLoader().parse(xmlStreamReader, STTabJc.type, xmlOptions);
        }
        
        public static STTabJc parse(final Node node) throws XmlException {
            return (STTabJc)getTypeLoader().parse(node, STTabJc.type, (XmlOptions)null);
        }
        
        public static STTabJc parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTabJc)getTypeLoader().parse(node, STTabJc.type, xmlOptions);
        }
        
        @Deprecated
        public static STTabJc parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTabJc)getTypeLoader().parse(xmlInputStream, STTabJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTabJc parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTabJc)getTypeLoader().parse(xmlInputStream, STTabJc.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTabJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTabJc.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_CLEAR = 1;
        static final int INT_LEFT = 2;
        static final int INT_CENTER = 3;
        static final int INT_RIGHT = 4;
        static final int INT_DECIMAL = 5;
        static final int INT_BAR = 6;
        static final int INT_NUM = 7;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("clear", 1), new Enum("left", 2), new Enum("center", 3), new Enum("right", 4), new Enum("decimal", 5), new Enum("bar", 6), new Enum("num", 7) });
        }
    }
}
