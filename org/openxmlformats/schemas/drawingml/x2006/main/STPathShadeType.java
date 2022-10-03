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

public interface STPathShadeType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPathShadeType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpathshadetype93c3type");
    public static final Enum SHAPE = Enum.forString("shape");
    public static final Enum CIRCLE = Enum.forString("circle");
    public static final Enum RECT = Enum.forString("rect");
    public static final int INT_SHAPE = 1;
    public static final int INT_CIRCLE = 2;
    public static final int INT_RECT = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPathShadeType newValue(final Object o) {
            return (STPathShadeType)STPathShadeType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPathShadeType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPathShadeType newInstance() {
            return (STPathShadeType)getTypeLoader().newInstance(STPathShadeType.type, (XmlOptions)null);
        }
        
        public static STPathShadeType newInstance(final XmlOptions xmlOptions) {
            return (STPathShadeType)getTypeLoader().newInstance(STPathShadeType.type, xmlOptions);
        }
        
        public static STPathShadeType parse(final String s) throws XmlException {
            return (STPathShadeType)getTypeLoader().parse(s, STPathShadeType.type, (XmlOptions)null);
        }
        
        public static STPathShadeType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPathShadeType)getTypeLoader().parse(s, STPathShadeType.type, xmlOptions);
        }
        
        public static STPathShadeType parse(final File file) throws XmlException, IOException {
            return (STPathShadeType)getTypeLoader().parse(file, STPathShadeType.type, (XmlOptions)null);
        }
        
        public static STPathShadeType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPathShadeType)getTypeLoader().parse(file, STPathShadeType.type, xmlOptions);
        }
        
        public static STPathShadeType parse(final URL url) throws XmlException, IOException {
            return (STPathShadeType)getTypeLoader().parse(url, STPathShadeType.type, (XmlOptions)null);
        }
        
        public static STPathShadeType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPathShadeType)getTypeLoader().parse(url, STPathShadeType.type, xmlOptions);
        }
        
        public static STPathShadeType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPathShadeType)getTypeLoader().parse(inputStream, STPathShadeType.type, (XmlOptions)null);
        }
        
        public static STPathShadeType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPathShadeType)getTypeLoader().parse(inputStream, STPathShadeType.type, xmlOptions);
        }
        
        public static STPathShadeType parse(final Reader reader) throws XmlException, IOException {
            return (STPathShadeType)getTypeLoader().parse(reader, STPathShadeType.type, (XmlOptions)null);
        }
        
        public static STPathShadeType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPathShadeType)getTypeLoader().parse(reader, STPathShadeType.type, xmlOptions);
        }
        
        public static STPathShadeType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPathShadeType)getTypeLoader().parse(xmlStreamReader, STPathShadeType.type, (XmlOptions)null);
        }
        
        public static STPathShadeType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPathShadeType)getTypeLoader().parse(xmlStreamReader, STPathShadeType.type, xmlOptions);
        }
        
        public static STPathShadeType parse(final Node node) throws XmlException {
            return (STPathShadeType)getTypeLoader().parse(node, STPathShadeType.type, (XmlOptions)null);
        }
        
        public static STPathShadeType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPathShadeType)getTypeLoader().parse(node, STPathShadeType.type, xmlOptions);
        }
        
        @Deprecated
        public static STPathShadeType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPathShadeType)getTypeLoader().parse(xmlInputStream, STPathShadeType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPathShadeType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPathShadeType)getTypeLoader().parse(xmlInputStream, STPathShadeType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPathShadeType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPathShadeType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SHAPE = 1;
        static final int INT_CIRCLE = 2;
        static final int INT_RECT = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("shape", 1), new Enum("circle", 2), new Enum("rect", 3) });
        }
    }
}
