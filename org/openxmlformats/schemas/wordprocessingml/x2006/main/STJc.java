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

public interface STJc extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STJc.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stjc977ftype");
    public static final Enum LEFT = Enum.forString("left");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum RIGHT = Enum.forString("right");
    public static final Enum BOTH = Enum.forString("both");
    public static final Enum MEDIUM_KASHIDA = Enum.forString("mediumKashida");
    public static final Enum DISTRIBUTE = Enum.forString("distribute");
    public static final Enum NUM_TAB = Enum.forString("numTab");
    public static final Enum HIGH_KASHIDA = Enum.forString("highKashida");
    public static final Enum LOW_KASHIDA = Enum.forString("lowKashida");
    public static final Enum THAI_DISTRIBUTE = Enum.forString("thaiDistribute");
    public static final int INT_LEFT = 1;
    public static final int INT_CENTER = 2;
    public static final int INT_RIGHT = 3;
    public static final int INT_BOTH = 4;
    public static final int INT_MEDIUM_KASHIDA = 5;
    public static final int INT_DISTRIBUTE = 6;
    public static final int INT_NUM_TAB = 7;
    public static final int INT_HIGH_KASHIDA = 8;
    public static final int INT_LOW_KASHIDA = 9;
    public static final int INT_THAI_DISTRIBUTE = 10;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STJc newValue(final Object o) {
            return (STJc)STJc.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STJc.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STJc newInstance() {
            return (STJc)getTypeLoader().newInstance(STJc.type, (XmlOptions)null);
        }
        
        public static STJc newInstance(final XmlOptions xmlOptions) {
            return (STJc)getTypeLoader().newInstance(STJc.type, xmlOptions);
        }
        
        public static STJc parse(final String s) throws XmlException {
            return (STJc)getTypeLoader().parse(s, STJc.type, (XmlOptions)null);
        }
        
        public static STJc parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STJc)getTypeLoader().parse(s, STJc.type, xmlOptions);
        }
        
        public static STJc parse(final File file) throws XmlException, IOException {
            return (STJc)getTypeLoader().parse(file, STJc.type, (XmlOptions)null);
        }
        
        public static STJc parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STJc)getTypeLoader().parse(file, STJc.type, xmlOptions);
        }
        
        public static STJc parse(final URL url) throws XmlException, IOException {
            return (STJc)getTypeLoader().parse(url, STJc.type, (XmlOptions)null);
        }
        
        public static STJc parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STJc)getTypeLoader().parse(url, STJc.type, xmlOptions);
        }
        
        public static STJc parse(final InputStream inputStream) throws XmlException, IOException {
            return (STJc)getTypeLoader().parse(inputStream, STJc.type, (XmlOptions)null);
        }
        
        public static STJc parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STJc)getTypeLoader().parse(inputStream, STJc.type, xmlOptions);
        }
        
        public static STJc parse(final Reader reader) throws XmlException, IOException {
            return (STJc)getTypeLoader().parse(reader, STJc.type, (XmlOptions)null);
        }
        
        public static STJc parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STJc)getTypeLoader().parse(reader, STJc.type, xmlOptions);
        }
        
        public static STJc parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STJc)getTypeLoader().parse(xmlStreamReader, STJc.type, (XmlOptions)null);
        }
        
        public static STJc parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STJc)getTypeLoader().parse(xmlStreamReader, STJc.type, xmlOptions);
        }
        
        public static STJc parse(final Node node) throws XmlException {
            return (STJc)getTypeLoader().parse(node, STJc.type, (XmlOptions)null);
        }
        
        public static STJc parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STJc)getTypeLoader().parse(node, STJc.type, xmlOptions);
        }
        
        @Deprecated
        public static STJc parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STJc)getTypeLoader().parse(xmlInputStream, STJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STJc parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STJc)getTypeLoader().parse(xmlInputStream, STJc.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STJc.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_LEFT = 1;
        static final int INT_CENTER = 2;
        static final int INT_RIGHT = 3;
        static final int INT_BOTH = 4;
        static final int INT_MEDIUM_KASHIDA = 5;
        static final int INT_DISTRIBUTE = 6;
        static final int INT_NUM_TAB = 7;
        static final int INT_HIGH_KASHIDA = 8;
        static final int INT_LOW_KASHIDA = 9;
        static final int INT_THAI_DISTRIBUTE = 10;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("left", 1), new Enum("center", 2), new Enum("right", 3), new Enum("both", 4), new Enum("mediumKashida", 5), new Enum("distribute", 6), new Enum("numTab", 7), new Enum("highKashida", 8), new Enum("lowKashida", 9), new Enum("thaiDistribute", 10) });
        }
    }
}
