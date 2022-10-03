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

public interface STBlackWhiteMode extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STBlackWhiteMode.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stblackwhitemode0558type");
    public static final Enum CLR = Enum.forString("clr");
    public static final Enum AUTO = Enum.forString("auto");
    public static final Enum GRAY = Enum.forString("gray");
    public static final Enum LT_GRAY = Enum.forString("ltGray");
    public static final Enum INV_GRAY = Enum.forString("invGray");
    public static final Enum GRAY_WHITE = Enum.forString("grayWhite");
    public static final Enum BLACK_GRAY = Enum.forString("blackGray");
    public static final Enum BLACK_WHITE = Enum.forString("blackWhite");
    public static final Enum BLACK = Enum.forString("black");
    public static final Enum WHITE = Enum.forString("white");
    public static final Enum HIDDEN = Enum.forString("hidden");
    public static final int INT_CLR = 1;
    public static final int INT_AUTO = 2;
    public static final int INT_GRAY = 3;
    public static final int INT_LT_GRAY = 4;
    public static final int INT_INV_GRAY = 5;
    public static final int INT_GRAY_WHITE = 6;
    public static final int INT_BLACK_GRAY = 7;
    public static final int INT_BLACK_WHITE = 8;
    public static final int INT_BLACK = 9;
    public static final int INT_WHITE = 10;
    public static final int INT_HIDDEN = 11;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STBlackWhiteMode newValue(final Object o) {
            return (STBlackWhiteMode)STBlackWhiteMode.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STBlackWhiteMode.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STBlackWhiteMode newInstance() {
            return (STBlackWhiteMode)getTypeLoader().newInstance(STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        public static STBlackWhiteMode newInstance(final XmlOptions xmlOptions) {
            return (STBlackWhiteMode)getTypeLoader().newInstance(STBlackWhiteMode.type, xmlOptions);
        }
        
        public static STBlackWhiteMode parse(final String s) throws XmlException {
            return (STBlackWhiteMode)getTypeLoader().parse(s, STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        public static STBlackWhiteMode parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STBlackWhiteMode)getTypeLoader().parse(s, STBlackWhiteMode.type, xmlOptions);
        }
        
        public static STBlackWhiteMode parse(final File file) throws XmlException, IOException {
            return (STBlackWhiteMode)getTypeLoader().parse(file, STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        public static STBlackWhiteMode parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBlackWhiteMode)getTypeLoader().parse(file, STBlackWhiteMode.type, xmlOptions);
        }
        
        public static STBlackWhiteMode parse(final URL url) throws XmlException, IOException {
            return (STBlackWhiteMode)getTypeLoader().parse(url, STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        public static STBlackWhiteMode parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBlackWhiteMode)getTypeLoader().parse(url, STBlackWhiteMode.type, xmlOptions);
        }
        
        public static STBlackWhiteMode parse(final InputStream inputStream) throws XmlException, IOException {
            return (STBlackWhiteMode)getTypeLoader().parse(inputStream, STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        public static STBlackWhiteMode parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBlackWhiteMode)getTypeLoader().parse(inputStream, STBlackWhiteMode.type, xmlOptions);
        }
        
        public static STBlackWhiteMode parse(final Reader reader) throws XmlException, IOException {
            return (STBlackWhiteMode)getTypeLoader().parse(reader, STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        public static STBlackWhiteMode parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBlackWhiteMode)getTypeLoader().parse(reader, STBlackWhiteMode.type, xmlOptions);
        }
        
        public static STBlackWhiteMode parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STBlackWhiteMode)getTypeLoader().parse(xmlStreamReader, STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        public static STBlackWhiteMode parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STBlackWhiteMode)getTypeLoader().parse(xmlStreamReader, STBlackWhiteMode.type, xmlOptions);
        }
        
        public static STBlackWhiteMode parse(final Node node) throws XmlException {
            return (STBlackWhiteMode)getTypeLoader().parse(node, STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        public static STBlackWhiteMode parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STBlackWhiteMode)getTypeLoader().parse(node, STBlackWhiteMode.type, xmlOptions);
        }
        
        @Deprecated
        public static STBlackWhiteMode parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STBlackWhiteMode)getTypeLoader().parse(xmlInputStream, STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STBlackWhiteMode parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STBlackWhiteMode)getTypeLoader().parse(xmlInputStream, STBlackWhiteMode.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBlackWhiteMode.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBlackWhiteMode.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_CLR = 1;
        static final int INT_AUTO = 2;
        static final int INT_GRAY = 3;
        static final int INT_LT_GRAY = 4;
        static final int INT_INV_GRAY = 5;
        static final int INT_GRAY_WHITE = 6;
        static final int INT_BLACK_GRAY = 7;
        static final int INT_BLACK_WHITE = 8;
        static final int INT_BLACK = 9;
        static final int INT_WHITE = 10;
        static final int INT_HIDDEN = 11;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("clr", 1), new Enum("auto", 2), new Enum("gray", 3), new Enum("ltGray", 4), new Enum("invGray", 5), new Enum("grayWhite", 6), new Enum("blackGray", 7), new Enum("blackWhite", 8), new Enum("black", 9), new Enum("white", 10), new Enum("hidden", 11) });
        }
    }
}
