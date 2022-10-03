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

public interface STBrClear extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STBrClear.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stbrclearb1e5type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum LEFT = Enum.forString("left");
    public static final Enum RIGHT = Enum.forString("right");
    public static final Enum ALL = Enum.forString("all");
    public static final int INT_NONE = 1;
    public static final int INT_LEFT = 2;
    public static final int INT_RIGHT = 3;
    public static final int INT_ALL = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STBrClear newValue(final Object o) {
            return (STBrClear)STBrClear.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STBrClear.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STBrClear newInstance() {
            return (STBrClear)getTypeLoader().newInstance(STBrClear.type, (XmlOptions)null);
        }
        
        public static STBrClear newInstance(final XmlOptions xmlOptions) {
            return (STBrClear)getTypeLoader().newInstance(STBrClear.type, xmlOptions);
        }
        
        public static STBrClear parse(final String s) throws XmlException {
            return (STBrClear)getTypeLoader().parse(s, STBrClear.type, (XmlOptions)null);
        }
        
        public static STBrClear parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STBrClear)getTypeLoader().parse(s, STBrClear.type, xmlOptions);
        }
        
        public static STBrClear parse(final File file) throws XmlException, IOException {
            return (STBrClear)getTypeLoader().parse(file, STBrClear.type, (XmlOptions)null);
        }
        
        public static STBrClear parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBrClear)getTypeLoader().parse(file, STBrClear.type, xmlOptions);
        }
        
        public static STBrClear parse(final URL url) throws XmlException, IOException {
            return (STBrClear)getTypeLoader().parse(url, STBrClear.type, (XmlOptions)null);
        }
        
        public static STBrClear parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBrClear)getTypeLoader().parse(url, STBrClear.type, xmlOptions);
        }
        
        public static STBrClear parse(final InputStream inputStream) throws XmlException, IOException {
            return (STBrClear)getTypeLoader().parse(inputStream, STBrClear.type, (XmlOptions)null);
        }
        
        public static STBrClear parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBrClear)getTypeLoader().parse(inputStream, STBrClear.type, xmlOptions);
        }
        
        public static STBrClear parse(final Reader reader) throws XmlException, IOException {
            return (STBrClear)getTypeLoader().parse(reader, STBrClear.type, (XmlOptions)null);
        }
        
        public static STBrClear parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBrClear)getTypeLoader().parse(reader, STBrClear.type, xmlOptions);
        }
        
        public static STBrClear parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STBrClear)getTypeLoader().parse(xmlStreamReader, STBrClear.type, (XmlOptions)null);
        }
        
        public static STBrClear parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STBrClear)getTypeLoader().parse(xmlStreamReader, STBrClear.type, xmlOptions);
        }
        
        public static STBrClear parse(final Node node) throws XmlException {
            return (STBrClear)getTypeLoader().parse(node, STBrClear.type, (XmlOptions)null);
        }
        
        public static STBrClear parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STBrClear)getTypeLoader().parse(node, STBrClear.type, xmlOptions);
        }
        
        @Deprecated
        public static STBrClear parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STBrClear)getTypeLoader().parse(xmlInputStream, STBrClear.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STBrClear parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STBrClear)getTypeLoader().parse(xmlInputStream, STBrClear.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBrClear.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBrClear.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_LEFT = 2;
        static final int INT_RIGHT = 3;
        static final int INT_ALL = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("left", 2), new Enum("right", 3), new Enum("all", 4) });
        }
    }
}
