package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface STColorSchemeIndex extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STColorSchemeIndex.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcolorschemeindexe6f5type");
    public static final Enum DK_1 = Enum.forString("dk1");
    public static final Enum LT_1 = Enum.forString("lt1");
    public static final Enum DK_2 = Enum.forString("dk2");
    public static final Enum LT_2 = Enum.forString("lt2");
    public static final Enum ACCENT_1 = Enum.forString("accent1");
    public static final Enum ACCENT_2 = Enum.forString("accent2");
    public static final Enum ACCENT_3 = Enum.forString("accent3");
    public static final Enum ACCENT_4 = Enum.forString("accent4");
    public static final Enum ACCENT_5 = Enum.forString("accent5");
    public static final Enum ACCENT_6 = Enum.forString("accent6");
    public static final Enum HLINK = Enum.forString("hlink");
    public static final Enum FOL_HLINK = Enum.forString("folHlink");
    public static final int INT_DK_1 = 1;
    public static final int INT_LT_1 = 2;
    public static final int INT_DK_2 = 3;
    public static final int INT_LT_2 = 4;
    public static final int INT_ACCENT_1 = 5;
    public static final int INT_ACCENT_2 = 6;
    public static final int INT_ACCENT_3 = 7;
    public static final int INT_ACCENT_4 = 8;
    public static final int INT_ACCENT_5 = 9;
    public static final int INT_ACCENT_6 = 10;
    public static final int INT_HLINK = 11;
    public static final int INT_FOL_HLINK = 12;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STColorSchemeIndex newValue(final Object o) {
            return (STColorSchemeIndex)STColorSchemeIndex.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STColorSchemeIndex.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STColorSchemeIndex newInstance() {
            return (STColorSchemeIndex)getTypeLoader().newInstance(STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        public static STColorSchemeIndex newInstance(final XmlOptions xmlOptions) {
            return (STColorSchemeIndex)getTypeLoader().newInstance(STColorSchemeIndex.type, xmlOptions);
        }
        
        public static STColorSchemeIndex parse(final String s) throws XmlException {
            return (STColorSchemeIndex)getTypeLoader().parse(s, STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        public static STColorSchemeIndex parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STColorSchemeIndex)getTypeLoader().parse(s, STColorSchemeIndex.type, xmlOptions);
        }
        
        public static STColorSchemeIndex parse(final File file) throws XmlException, IOException {
            return (STColorSchemeIndex)getTypeLoader().parse(file, STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        public static STColorSchemeIndex parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColorSchemeIndex)getTypeLoader().parse(file, STColorSchemeIndex.type, xmlOptions);
        }
        
        public static STColorSchemeIndex parse(final URL url) throws XmlException, IOException {
            return (STColorSchemeIndex)getTypeLoader().parse(url, STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        public static STColorSchemeIndex parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColorSchemeIndex)getTypeLoader().parse(url, STColorSchemeIndex.type, xmlOptions);
        }
        
        public static STColorSchemeIndex parse(final InputStream inputStream) throws XmlException, IOException {
            return (STColorSchemeIndex)getTypeLoader().parse(inputStream, STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        public static STColorSchemeIndex parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColorSchemeIndex)getTypeLoader().parse(inputStream, STColorSchemeIndex.type, xmlOptions);
        }
        
        public static STColorSchemeIndex parse(final Reader reader) throws XmlException, IOException {
            return (STColorSchemeIndex)getTypeLoader().parse(reader, STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        public static STColorSchemeIndex parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STColorSchemeIndex)getTypeLoader().parse(reader, STColorSchemeIndex.type, xmlOptions);
        }
        
        public static STColorSchemeIndex parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STColorSchemeIndex)getTypeLoader().parse(xmlStreamReader, STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        public static STColorSchemeIndex parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STColorSchemeIndex)getTypeLoader().parse(xmlStreamReader, STColorSchemeIndex.type, xmlOptions);
        }
        
        public static STColorSchemeIndex parse(final Node node) throws XmlException {
            return (STColorSchemeIndex)getTypeLoader().parse(node, STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        public static STColorSchemeIndex parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STColorSchemeIndex)getTypeLoader().parse(node, STColorSchemeIndex.type, xmlOptions);
        }
        
        @Deprecated
        public static STColorSchemeIndex parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STColorSchemeIndex)getTypeLoader().parse(xmlInputStream, STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STColorSchemeIndex parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STColorSchemeIndex)getTypeLoader().parse(xmlInputStream, STColorSchemeIndex.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STColorSchemeIndex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STColorSchemeIndex.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_DK_1 = 1;
        static final int INT_LT_1 = 2;
        static final int INT_DK_2 = 3;
        static final int INT_LT_2 = 4;
        static final int INT_ACCENT_1 = 5;
        static final int INT_ACCENT_2 = 6;
        static final int INT_ACCENT_3 = 7;
        static final int INT_ACCENT_4 = 8;
        static final int INT_ACCENT_5 = 9;
        static final int INT_ACCENT_6 = 10;
        static final int INT_HLINK = 11;
        static final int INT_FOL_HLINK = 12;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("dk1", 1), new Enum("lt1", 2), new Enum("dk2", 3), new Enum("lt2", 4), new Enum("accent1", 5), new Enum("accent2", 6), new Enum("accent3", 7), new Enum("accent4", 8), new Enum("accent5", 9), new Enum("accent6", 10), new Enum("hlink", 11), new Enum("folHlink", 12) });
        }
    }
}
