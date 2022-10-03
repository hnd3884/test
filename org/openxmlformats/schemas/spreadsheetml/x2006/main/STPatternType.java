package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface STPatternType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPatternType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpatterntype7939type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum SOLID = Enum.forString("solid");
    public static final Enum MEDIUM_GRAY = Enum.forString("mediumGray");
    public static final Enum DARK_GRAY = Enum.forString("darkGray");
    public static final Enum LIGHT_GRAY = Enum.forString("lightGray");
    public static final Enum DARK_HORIZONTAL = Enum.forString("darkHorizontal");
    public static final Enum DARK_VERTICAL = Enum.forString("darkVertical");
    public static final Enum DARK_DOWN = Enum.forString("darkDown");
    public static final Enum DARK_UP = Enum.forString("darkUp");
    public static final Enum DARK_GRID = Enum.forString("darkGrid");
    public static final Enum DARK_TRELLIS = Enum.forString("darkTrellis");
    public static final Enum LIGHT_HORIZONTAL = Enum.forString("lightHorizontal");
    public static final Enum LIGHT_VERTICAL = Enum.forString("lightVertical");
    public static final Enum LIGHT_DOWN = Enum.forString("lightDown");
    public static final Enum LIGHT_UP = Enum.forString("lightUp");
    public static final Enum LIGHT_GRID = Enum.forString("lightGrid");
    public static final Enum LIGHT_TRELLIS = Enum.forString("lightTrellis");
    public static final Enum GRAY_125 = Enum.forString("gray125");
    public static final Enum GRAY_0625 = Enum.forString("gray0625");
    public static final int INT_NONE = 1;
    public static final int INT_SOLID = 2;
    public static final int INT_MEDIUM_GRAY = 3;
    public static final int INT_DARK_GRAY = 4;
    public static final int INT_LIGHT_GRAY = 5;
    public static final int INT_DARK_HORIZONTAL = 6;
    public static final int INT_DARK_VERTICAL = 7;
    public static final int INT_DARK_DOWN = 8;
    public static final int INT_DARK_UP = 9;
    public static final int INT_DARK_GRID = 10;
    public static final int INT_DARK_TRELLIS = 11;
    public static final int INT_LIGHT_HORIZONTAL = 12;
    public static final int INT_LIGHT_VERTICAL = 13;
    public static final int INT_LIGHT_DOWN = 14;
    public static final int INT_LIGHT_UP = 15;
    public static final int INT_LIGHT_GRID = 16;
    public static final int INT_LIGHT_TRELLIS = 17;
    public static final int INT_GRAY_125 = 18;
    public static final int INT_GRAY_0625 = 19;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPatternType newValue(final Object o) {
            return (STPatternType)STPatternType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPatternType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPatternType newInstance() {
            return (STPatternType)getTypeLoader().newInstance(STPatternType.type, (XmlOptions)null);
        }
        
        public static STPatternType newInstance(final XmlOptions xmlOptions) {
            return (STPatternType)getTypeLoader().newInstance(STPatternType.type, xmlOptions);
        }
        
        public static STPatternType parse(final String s) throws XmlException {
            return (STPatternType)getTypeLoader().parse(s, STPatternType.type, (XmlOptions)null);
        }
        
        public static STPatternType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPatternType)getTypeLoader().parse(s, STPatternType.type, xmlOptions);
        }
        
        public static STPatternType parse(final File file) throws XmlException, IOException {
            return (STPatternType)getTypeLoader().parse(file, STPatternType.type, (XmlOptions)null);
        }
        
        public static STPatternType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPatternType)getTypeLoader().parse(file, STPatternType.type, xmlOptions);
        }
        
        public static STPatternType parse(final URL url) throws XmlException, IOException {
            return (STPatternType)getTypeLoader().parse(url, STPatternType.type, (XmlOptions)null);
        }
        
        public static STPatternType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPatternType)getTypeLoader().parse(url, STPatternType.type, xmlOptions);
        }
        
        public static STPatternType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPatternType)getTypeLoader().parse(inputStream, STPatternType.type, (XmlOptions)null);
        }
        
        public static STPatternType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPatternType)getTypeLoader().parse(inputStream, STPatternType.type, xmlOptions);
        }
        
        public static STPatternType parse(final Reader reader) throws XmlException, IOException {
            return (STPatternType)getTypeLoader().parse(reader, STPatternType.type, (XmlOptions)null);
        }
        
        public static STPatternType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPatternType)getTypeLoader().parse(reader, STPatternType.type, xmlOptions);
        }
        
        public static STPatternType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPatternType)getTypeLoader().parse(xmlStreamReader, STPatternType.type, (XmlOptions)null);
        }
        
        public static STPatternType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPatternType)getTypeLoader().parse(xmlStreamReader, STPatternType.type, xmlOptions);
        }
        
        public static STPatternType parse(final Node node) throws XmlException {
            return (STPatternType)getTypeLoader().parse(node, STPatternType.type, (XmlOptions)null);
        }
        
        public static STPatternType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPatternType)getTypeLoader().parse(node, STPatternType.type, xmlOptions);
        }
        
        @Deprecated
        public static STPatternType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPatternType)getTypeLoader().parse(xmlInputStream, STPatternType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPatternType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPatternType)getTypeLoader().parse(xmlInputStream, STPatternType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPatternType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPatternType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_SOLID = 2;
        static final int INT_MEDIUM_GRAY = 3;
        static final int INT_DARK_GRAY = 4;
        static final int INT_LIGHT_GRAY = 5;
        static final int INT_DARK_HORIZONTAL = 6;
        static final int INT_DARK_VERTICAL = 7;
        static final int INT_DARK_DOWN = 8;
        static final int INT_DARK_UP = 9;
        static final int INT_DARK_GRID = 10;
        static final int INT_DARK_TRELLIS = 11;
        static final int INT_LIGHT_HORIZONTAL = 12;
        static final int INT_LIGHT_VERTICAL = 13;
        static final int INT_LIGHT_DOWN = 14;
        static final int INT_LIGHT_UP = 15;
        static final int INT_LIGHT_GRID = 16;
        static final int INT_LIGHT_TRELLIS = 17;
        static final int INT_GRAY_125 = 18;
        static final int INT_GRAY_0625 = 19;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("solid", 2), new Enum("mediumGray", 3), new Enum("darkGray", 4), new Enum("lightGray", 5), new Enum("darkHorizontal", 6), new Enum("darkVertical", 7), new Enum("darkDown", 8), new Enum("darkUp", 9), new Enum("darkGrid", 10), new Enum("darkTrellis", 11), new Enum("lightHorizontal", 12), new Enum("lightVertical", 13), new Enum("lightDown", 14), new Enum("lightUp", 15), new Enum("lightGrid", 16), new Enum("lightTrellis", 17), new Enum("gray125", 18), new Enum("gray0625", 19) });
        }
    }
}
