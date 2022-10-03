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

public interface STTextFontAlignType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextFontAlignType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextfontaligntypecb44type");
    public static final Enum AUTO = Enum.forString("auto");
    public static final Enum T = Enum.forString("t");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum BASE = Enum.forString("base");
    public static final Enum B = Enum.forString("b");
    public static final int INT_AUTO = 1;
    public static final int INT_T = 2;
    public static final int INT_CTR = 3;
    public static final int INT_BASE = 4;
    public static final int INT_B = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextFontAlignType newValue(final Object o) {
            return (STTextFontAlignType)STTextFontAlignType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextFontAlignType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextFontAlignType newInstance() {
            return (STTextFontAlignType)getTypeLoader().newInstance(STTextFontAlignType.type, (XmlOptions)null);
        }
        
        public static STTextFontAlignType newInstance(final XmlOptions xmlOptions) {
            return (STTextFontAlignType)getTypeLoader().newInstance(STTextFontAlignType.type, xmlOptions);
        }
        
        public static STTextFontAlignType parse(final String s) throws XmlException {
            return (STTextFontAlignType)getTypeLoader().parse(s, STTextFontAlignType.type, (XmlOptions)null);
        }
        
        public static STTextFontAlignType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextFontAlignType)getTypeLoader().parse(s, STTextFontAlignType.type, xmlOptions);
        }
        
        public static STTextFontAlignType parse(final File file) throws XmlException, IOException {
            return (STTextFontAlignType)getTypeLoader().parse(file, STTextFontAlignType.type, (XmlOptions)null);
        }
        
        public static STTextFontAlignType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontAlignType)getTypeLoader().parse(file, STTextFontAlignType.type, xmlOptions);
        }
        
        public static STTextFontAlignType parse(final URL url) throws XmlException, IOException {
            return (STTextFontAlignType)getTypeLoader().parse(url, STTextFontAlignType.type, (XmlOptions)null);
        }
        
        public static STTextFontAlignType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontAlignType)getTypeLoader().parse(url, STTextFontAlignType.type, xmlOptions);
        }
        
        public static STTextFontAlignType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextFontAlignType)getTypeLoader().parse(inputStream, STTextFontAlignType.type, (XmlOptions)null);
        }
        
        public static STTextFontAlignType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontAlignType)getTypeLoader().parse(inputStream, STTextFontAlignType.type, xmlOptions);
        }
        
        public static STTextFontAlignType parse(final Reader reader) throws XmlException, IOException {
            return (STTextFontAlignType)getTypeLoader().parse(reader, STTextFontAlignType.type, (XmlOptions)null);
        }
        
        public static STTextFontAlignType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextFontAlignType)getTypeLoader().parse(reader, STTextFontAlignType.type, xmlOptions);
        }
        
        public static STTextFontAlignType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextFontAlignType)getTypeLoader().parse(xmlStreamReader, STTextFontAlignType.type, (XmlOptions)null);
        }
        
        public static STTextFontAlignType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextFontAlignType)getTypeLoader().parse(xmlStreamReader, STTextFontAlignType.type, xmlOptions);
        }
        
        public static STTextFontAlignType parse(final Node node) throws XmlException {
            return (STTextFontAlignType)getTypeLoader().parse(node, STTextFontAlignType.type, (XmlOptions)null);
        }
        
        public static STTextFontAlignType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextFontAlignType)getTypeLoader().parse(node, STTextFontAlignType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextFontAlignType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextFontAlignType)getTypeLoader().parse(xmlInputStream, STTextFontAlignType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextFontAlignType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextFontAlignType)getTypeLoader().parse(xmlInputStream, STTextFontAlignType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextFontAlignType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextFontAlignType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_AUTO = 1;
        static final int INT_T = 2;
        static final int INT_CTR = 3;
        static final int INT_BASE = 4;
        static final int INT_B = 5;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("auto", 1), new Enum("t", 2), new Enum("ctr", 3), new Enum("base", 4), new Enum("b", 5) });
        }
    }
}
