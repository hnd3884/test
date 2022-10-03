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

public interface STCellType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCellType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcelltypebf95type");
    public static final Enum B = Enum.forString("b");
    public static final Enum N = Enum.forString("n");
    public static final Enum E = Enum.forString("e");
    public static final Enum S = Enum.forString("s");
    public static final Enum STR = Enum.forString("str");
    public static final Enum INLINE_STR = Enum.forString("inlineStr");
    public static final int INT_B = 1;
    public static final int INT_N = 2;
    public static final int INT_E = 3;
    public static final int INT_S = 4;
    public static final int INT_STR = 5;
    public static final int INT_INLINE_STR = 6;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCellType newValue(final Object o) {
            return (STCellType)STCellType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCellType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCellType newInstance() {
            return (STCellType)getTypeLoader().newInstance(STCellType.type, (XmlOptions)null);
        }
        
        public static STCellType newInstance(final XmlOptions xmlOptions) {
            return (STCellType)getTypeLoader().newInstance(STCellType.type, xmlOptions);
        }
        
        public static STCellType parse(final String s) throws XmlException {
            return (STCellType)getTypeLoader().parse(s, STCellType.type, (XmlOptions)null);
        }
        
        public static STCellType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCellType)getTypeLoader().parse(s, STCellType.type, xmlOptions);
        }
        
        public static STCellType parse(final File file) throws XmlException, IOException {
            return (STCellType)getTypeLoader().parse(file, STCellType.type, (XmlOptions)null);
        }
        
        public static STCellType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellType)getTypeLoader().parse(file, STCellType.type, xmlOptions);
        }
        
        public static STCellType parse(final URL url) throws XmlException, IOException {
            return (STCellType)getTypeLoader().parse(url, STCellType.type, (XmlOptions)null);
        }
        
        public static STCellType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellType)getTypeLoader().parse(url, STCellType.type, xmlOptions);
        }
        
        public static STCellType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCellType)getTypeLoader().parse(inputStream, STCellType.type, (XmlOptions)null);
        }
        
        public static STCellType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellType)getTypeLoader().parse(inputStream, STCellType.type, xmlOptions);
        }
        
        public static STCellType parse(final Reader reader) throws XmlException, IOException {
            return (STCellType)getTypeLoader().parse(reader, STCellType.type, (XmlOptions)null);
        }
        
        public static STCellType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCellType)getTypeLoader().parse(reader, STCellType.type, xmlOptions);
        }
        
        public static STCellType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCellType)getTypeLoader().parse(xmlStreamReader, STCellType.type, (XmlOptions)null);
        }
        
        public static STCellType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCellType)getTypeLoader().parse(xmlStreamReader, STCellType.type, xmlOptions);
        }
        
        public static STCellType parse(final Node node) throws XmlException {
            return (STCellType)getTypeLoader().parse(node, STCellType.type, (XmlOptions)null);
        }
        
        public static STCellType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCellType)getTypeLoader().parse(node, STCellType.type, xmlOptions);
        }
        
        @Deprecated
        public static STCellType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCellType)getTypeLoader().parse(xmlInputStream, STCellType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCellType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCellType)getTypeLoader().parse(xmlInputStream, STCellType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCellType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_B = 1;
        static final int INT_N = 2;
        static final int INT_E = 3;
        static final int INT_S = 4;
        static final int INT_STR = 5;
        static final int INT_INLINE_STR = 6;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("b", 1), new Enum("n", 2), new Enum("e", 3), new Enum("s", 4), new Enum("str", 5), new Enum("inlineStr", 6) });
        }
    }
}
