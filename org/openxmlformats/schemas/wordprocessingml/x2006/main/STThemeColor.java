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

public interface STThemeColor extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STThemeColor.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stthemecolor063etype");
    public static final Enum DARK_1 = Enum.forString("dark1");
    public static final Enum LIGHT_1 = Enum.forString("light1");
    public static final Enum DARK_2 = Enum.forString("dark2");
    public static final Enum LIGHT_2 = Enum.forString("light2");
    public static final Enum ACCENT_1 = Enum.forString("accent1");
    public static final Enum ACCENT_2 = Enum.forString("accent2");
    public static final Enum ACCENT_3 = Enum.forString("accent3");
    public static final Enum ACCENT_4 = Enum.forString("accent4");
    public static final Enum ACCENT_5 = Enum.forString("accent5");
    public static final Enum ACCENT_6 = Enum.forString("accent6");
    public static final Enum HYPERLINK = Enum.forString("hyperlink");
    public static final Enum FOLLOWED_HYPERLINK = Enum.forString("followedHyperlink");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum BACKGROUND_1 = Enum.forString("background1");
    public static final Enum TEXT_1 = Enum.forString("text1");
    public static final Enum BACKGROUND_2 = Enum.forString("background2");
    public static final Enum TEXT_2 = Enum.forString("text2");
    public static final int INT_DARK_1 = 1;
    public static final int INT_LIGHT_1 = 2;
    public static final int INT_DARK_2 = 3;
    public static final int INT_LIGHT_2 = 4;
    public static final int INT_ACCENT_1 = 5;
    public static final int INT_ACCENT_2 = 6;
    public static final int INT_ACCENT_3 = 7;
    public static final int INT_ACCENT_4 = 8;
    public static final int INT_ACCENT_5 = 9;
    public static final int INT_ACCENT_6 = 10;
    public static final int INT_HYPERLINK = 11;
    public static final int INT_FOLLOWED_HYPERLINK = 12;
    public static final int INT_NONE = 13;
    public static final int INT_BACKGROUND_1 = 14;
    public static final int INT_TEXT_1 = 15;
    public static final int INT_BACKGROUND_2 = 16;
    public static final int INT_TEXT_2 = 17;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STThemeColor newValue(final Object o) {
            return (STThemeColor)STThemeColor.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STThemeColor.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STThemeColor newInstance() {
            return (STThemeColor)getTypeLoader().newInstance(STThemeColor.type, (XmlOptions)null);
        }
        
        public static STThemeColor newInstance(final XmlOptions xmlOptions) {
            return (STThemeColor)getTypeLoader().newInstance(STThemeColor.type, xmlOptions);
        }
        
        public static STThemeColor parse(final String s) throws XmlException {
            return (STThemeColor)getTypeLoader().parse(s, STThemeColor.type, (XmlOptions)null);
        }
        
        public static STThemeColor parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STThemeColor)getTypeLoader().parse(s, STThemeColor.type, xmlOptions);
        }
        
        public static STThemeColor parse(final File file) throws XmlException, IOException {
            return (STThemeColor)getTypeLoader().parse(file, STThemeColor.type, (XmlOptions)null);
        }
        
        public static STThemeColor parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STThemeColor)getTypeLoader().parse(file, STThemeColor.type, xmlOptions);
        }
        
        public static STThemeColor parse(final URL url) throws XmlException, IOException {
            return (STThemeColor)getTypeLoader().parse(url, STThemeColor.type, (XmlOptions)null);
        }
        
        public static STThemeColor parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STThemeColor)getTypeLoader().parse(url, STThemeColor.type, xmlOptions);
        }
        
        public static STThemeColor parse(final InputStream inputStream) throws XmlException, IOException {
            return (STThemeColor)getTypeLoader().parse(inputStream, STThemeColor.type, (XmlOptions)null);
        }
        
        public static STThemeColor parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STThemeColor)getTypeLoader().parse(inputStream, STThemeColor.type, xmlOptions);
        }
        
        public static STThemeColor parse(final Reader reader) throws XmlException, IOException {
            return (STThemeColor)getTypeLoader().parse(reader, STThemeColor.type, (XmlOptions)null);
        }
        
        public static STThemeColor parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STThemeColor)getTypeLoader().parse(reader, STThemeColor.type, xmlOptions);
        }
        
        public static STThemeColor parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STThemeColor)getTypeLoader().parse(xmlStreamReader, STThemeColor.type, (XmlOptions)null);
        }
        
        public static STThemeColor parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STThemeColor)getTypeLoader().parse(xmlStreamReader, STThemeColor.type, xmlOptions);
        }
        
        public static STThemeColor parse(final Node node) throws XmlException {
            return (STThemeColor)getTypeLoader().parse(node, STThemeColor.type, (XmlOptions)null);
        }
        
        public static STThemeColor parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STThemeColor)getTypeLoader().parse(node, STThemeColor.type, xmlOptions);
        }
        
        @Deprecated
        public static STThemeColor parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STThemeColor)getTypeLoader().parse(xmlInputStream, STThemeColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STThemeColor parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STThemeColor)getTypeLoader().parse(xmlInputStream, STThemeColor.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STThemeColor.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STThemeColor.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_DARK_1 = 1;
        static final int INT_LIGHT_1 = 2;
        static final int INT_DARK_2 = 3;
        static final int INT_LIGHT_2 = 4;
        static final int INT_ACCENT_1 = 5;
        static final int INT_ACCENT_2 = 6;
        static final int INT_ACCENT_3 = 7;
        static final int INT_ACCENT_4 = 8;
        static final int INT_ACCENT_5 = 9;
        static final int INT_ACCENT_6 = 10;
        static final int INT_HYPERLINK = 11;
        static final int INT_FOLLOWED_HYPERLINK = 12;
        static final int INT_NONE = 13;
        static final int INT_BACKGROUND_1 = 14;
        static final int INT_TEXT_1 = 15;
        static final int INT_BACKGROUND_2 = 16;
        static final int INT_TEXT_2 = 17;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("dark1", 1), new Enum("light1", 2), new Enum("dark2", 3), new Enum("light2", 4), new Enum("accent1", 5), new Enum("accent2", 6), new Enum("accent3", 7), new Enum("accent4", 8), new Enum("accent5", 9), new Enum("accent6", 10), new Enum("hyperlink", 11), new Enum("followedHyperlink", 12), new Enum("none", 13), new Enum("background1", 14), new Enum("text1", 15), new Enum("background2", 16), new Enum("text2", 17) });
        }
    }
}
