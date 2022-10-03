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

public interface STHighlightColor extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHighlightColor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sthighlightcolora8e9type");
    public static final Enum BLACK = Enum.forString("black");
    public static final Enum BLUE = Enum.forString("blue");
    public static final Enum CYAN = Enum.forString("cyan");
    public static final Enum GREEN = Enum.forString("green");
    public static final Enum MAGENTA = Enum.forString("magenta");
    public static final Enum RED = Enum.forString("red");
    public static final Enum YELLOW = Enum.forString("yellow");
    public static final Enum WHITE = Enum.forString("white");
    public static final Enum DARK_BLUE = Enum.forString("darkBlue");
    public static final Enum DARK_CYAN = Enum.forString("darkCyan");
    public static final Enum DARK_GREEN = Enum.forString("darkGreen");
    public static final Enum DARK_MAGENTA = Enum.forString("darkMagenta");
    public static final Enum DARK_RED = Enum.forString("darkRed");
    public static final Enum DARK_YELLOW = Enum.forString("darkYellow");
    public static final Enum DARK_GRAY = Enum.forString("darkGray");
    public static final Enum LIGHT_GRAY = Enum.forString("lightGray");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_BLACK = 1;
    public static final int INT_BLUE = 2;
    public static final int INT_CYAN = 3;
    public static final int INT_GREEN = 4;
    public static final int INT_MAGENTA = 5;
    public static final int INT_RED = 6;
    public static final int INT_YELLOW = 7;
    public static final int INT_WHITE = 8;
    public static final int INT_DARK_BLUE = 9;
    public static final int INT_DARK_CYAN = 10;
    public static final int INT_DARK_GREEN = 11;
    public static final int INT_DARK_MAGENTA = 12;
    public static final int INT_DARK_RED = 13;
    public static final int INT_DARK_YELLOW = 14;
    public static final int INT_DARK_GRAY = 15;
    public static final int INT_LIGHT_GRAY = 16;
    public static final int INT_NONE = 17;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHighlightColor newValue(final Object o) {
            return (STHighlightColor)STHighlightColor.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHighlightColor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHighlightColor newInstance() {
            return (STHighlightColor)getTypeLoader().newInstance(STHighlightColor.type, (XmlOptions)null);
        }
        
        public static STHighlightColor newInstance(final XmlOptions xmlOptions) {
            return (STHighlightColor)getTypeLoader().newInstance(STHighlightColor.type, xmlOptions);
        }
        
        public static STHighlightColor parse(final String s) throws XmlException {
            return (STHighlightColor)getTypeLoader().parse(s, STHighlightColor.type, (XmlOptions)null);
        }
        
        public static STHighlightColor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHighlightColor)getTypeLoader().parse(s, STHighlightColor.type, xmlOptions);
        }
        
        public static STHighlightColor parse(final File file) throws XmlException, IOException {
            return (STHighlightColor)getTypeLoader().parse(file, STHighlightColor.type, (XmlOptions)null);
        }
        
        public static STHighlightColor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHighlightColor)getTypeLoader().parse(file, STHighlightColor.type, xmlOptions);
        }
        
        public static STHighlightColor parse(final URL url) throws XmlException, IOException {
            return (STHighlightColor)getTypeLoader().parse(url, STHighlightColor.type, (XmlOptions)null);
        }
        
        public static STHighlightColor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHighlightColor)getTypeLoader().parse(url, STHighlightColor.type, xmlOptions);
        }
        
        public static STHighlightColor parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHighlightColor)getTypeLoader().parse(inputStream, STHighlightColor.type, (XmlOptions)null);
        }
        
        public static STHighlightColor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHighlightColor)getTypeLoader().parse(inputStream, STHighlightColor.type, xmlOptions);
        }
        
        public static STHighlightColor parse(final Reader reader) throws XmlException, IOException {
            return (STHighlightColor)getTypeLoader().parse(reader, STHighlightColor.type, (XmlOptions)null);
        }
        
        public static STHighlightColor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHighlightColor)getTypeLoader().parse(reader, STHighlightColor.type, xmlOptions);
        }
        
        public static STHighlightColor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHighlightColor)getTypeLoader().parse(xmlStreamReader, STHighlightColor.type, (XmlOptions)null);
        }
        
        public static STHighlightColor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHighlightColor)getTypeLoader().parse(xmlStreamReader, STHighlightColor.type, xmlOptions);
        }
        
        public static STHighlightColor parse(final Node node) throws XmlException {
            return (STHighlightColor)getTypeLoader().parse(node, STHighlightColor.type, (XmlOptions)null);
        }
        
        public static STHighlightColor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHighlightColor)getTypeLoader().parse(node, STHighlightColor.type, xmlOptions);
        }
        
        @Deprecated
        public static STHighlightColor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHighlightColor)getTypeLoader().parse(xmlInputStream, STHighlightColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHighlightColor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHighlightColor)getTypeLoader().parse(xmlInputStream, STHighlightColor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHighlightColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHighlightColor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_BLACK = 1;
        static final int INT_BLUE = 2;
        static final int INT_CYAN = 3;
        static final int INT_GREEN = 4;
        static final int INT_MAGENTA = 5;
        static final int INT_RED = 6;
        static final int INT_YELLOW = 7;
        static final int INT_WHITE = 8;
        static final int INT_DARK_BLUE = 9;
        static final int INT_DARK_CYAN = 10;
        static final int INT_DARK_GREEN = 11;
        static final int INT_DARK_MAGENTA = 12;
        static final int INT_DARK_RED = 13;
        static final int INT_DARK_YELLOW = 14;
        static final int INT_DARK_GRAY = 15;
        static final int INT_LIGHT_GRAY = 16;
        static final int INT_NONE = 17;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("black", 1), new Enum("blue", 2), new Enum("cyan", 3), new Enum("green", 4), new Enum("magenta", 5), new Enum("red", 6), new Enum("yellow", 7), new Enum("white", 8), new Enum("darkBlue", 9), new Enum("darkCyan", 10), new Enum("darkGreen", 11), new Enum("darkMagenta", 12), new Enum("darkRed", 13), new Enum("darkYellow", 14), new Enum("darkGray", 15), new Enum("lightGray", 16), new Enum("none", 17) });
        }
    }
}
