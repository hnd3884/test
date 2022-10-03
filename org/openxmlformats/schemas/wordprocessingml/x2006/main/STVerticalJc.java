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

public interface STVerticalJc extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STVerticalJc.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stverticaljc3629type");
    public static final Enum TOP = Enum.forString("top");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum BOTH = Enum.forString("both");
    public static final Enum BOTTOM = Enum.forString("bottom");
    public static final int INT_TOP = 1;
    public static final int INT_CENTER = 2;
    public static final int INT_BOTH = 3;
    public static final int INT_BOTTOM = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STVerticalJc newValue(final Object o) {
            return (STVerticalJc)STVerticalJc.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STVerticalJc.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STVerticalJc newInstance() {
            return (STVerticalJc)getTypeLoader().newInstance(STVerticalJc.type, (XmlOptions)null);
        }
        
        public static STVerticalJc newInstance(final XmlOptions xmlOptions) {
            return (STVerticalJc)getTypeLoader().newInstance(STVerticalJc.type, xmlOptions);
        }
        
        public static STVerticalJc parse(final String s) throws XmlException {
            return (STVerticalJc)getTypeLoader().parse(s, STVerticalJc.type, (XmlOptions)null);
        }
        
        public static STVerticalJc parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STVerticalJc)getTypeLoader().parse(s, STVerticalJc.type, xmlOptions);
        }
        
        public static STVerticalJc parse(final File file) throws XmlException, IOException {
            return (STVerticalJc)getTypeLoader().parse(file, STVerticalJc.type, (XmlOptions)null);
        }
        
        public static STVerticalJc parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalJc)getTypeLoader().parse(file, STVerticalJc.type, xmlOptions);
        }
        
        public static STVerticalJc parse(final URL url) throws XmlException, IOException {
            return (STVerticalJc)getTypeLoader().parse(url, STVerticalJc.type, (XmlOptions)null);
        }
        
        public static STVerticalJc parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalJc)getTypeLoader().parse(url, STVerticalJc.type, xmlOptions);
        }
        
        public static STVerticalJc parse(final InputStream inputStream) throws XmlException, IOException {
            return (STVerticalJc)getTypeLoader().parse(inputStream, STVerticalJc.type, (XmlOptions)null);
        }
        
        public static STVerticalJc parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalJc)getTypeLoader().parse(inputStream, STVerticalJc.type, xmlOptions);
        }
        
        public static STVerticalJc parse(final Reader reader) throws XmlException, IOException {
            return (STVerticalJc)getTypeLoader().parse(reader, STVerticalJc.type, (XmlOptions)null);
        }
        
        public static STVerticalJc parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STVerticalJc)getTypeLoader().parse(reader, STVerticalJc.type, xmlOptions);
        }
        
        public static STVerticalJc parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STVerticalJc)getTypeLoader().parse(xmlStreamReader, STVerticalJc.type, (XmlOptions)null);
        }
        
        public static STVerticalJc parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STVerticalJc)getTypeLoader().parse(xmlStreamReader, STVerticalJc.type, xmlOptions);
        }
        
        public static STVerticalJc parse(final Node node) throws XmlException {
            return (STVerticalJc)getTypeLoader().parse(node, STVerticalJc.type, (XmlOptions)null);
        }
        
        public static STVerticalJc parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STVerticalJc)getTypeLoader().parse(node, STVerticalJc.type, xmlOptions);
        }
        
        @Deprecated
        public static STVerticalJc parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STVerticalJc)getTypeLoader().parse(xmlInputStream, STVerticalJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STVerticalJc parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STVerticalJc)getTypeLoader().parse(xmlInputStream, STVerticalJc.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STVerticalJc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STVerticalJc.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TOP = 1;
        static final int INT_CENTER = 2;
        static final int INT_BOTH = 3;
        static final int INT_BOTTOM = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("top", 1), new Enum("center", 2), new Enum("both", 3), new Enum("bottom", 4) });
        }
    }
}
