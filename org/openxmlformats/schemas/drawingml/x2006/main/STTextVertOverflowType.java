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

public interface STTextVertOverflowType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextVertOverflowType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextvertoverflowtype2725type");
    public static final Enum OVERFLOW = Enum.forString("overflow");
    public static final Enum ELLIPSIS = Enum.forString("ellipsis");
    public static final Enum CLIP = Enum.forString("clip");
    public static final int INT_OVERFLOW = 1;
    public static final int INT_ELLIPSIS = 2;
    public static final int INT_CLIP = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextVertOverflowType newValue(final Object o) {
            return (STTextVertOverflowType)STTextVertOverflowType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextVertOverflowType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextVertOverflowType newInstance() {
            return (STTextVertOverflowType)getTypeLoader().newInstance(STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextVertOverflowType newInstance(final XmlOptions xmlOptions) {
            return (STTextVertOverflowType)getTypeLoader().newInstance(STTextVertOverflowType.type, xmlOptions);
        }
        
        public static STTextVertOverflowType parse(final String s) throws XmlException {
            return (STTextVertOverflowType)getTypeLoader().parse(s, STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextVertOverflowType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextVertOverflowType)getTypeLoader().parse(s, STTextVertOverflowType.type, xmlOptions);
        }
        
        public static STTextVertOverflowType parse(final File file) throws XmlException, IOException {
            return (STTextVertOverflowType)getTypeLoader().parse(file, STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextVertOverflowType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextVertOverflowType)getTypeLoader().parse(file, STTextVertOverflowType.type, xmlOptions);
        }
        
        public static STTextVertOverflowType parse(final URL url) throws XmlException, IOException {
            return (STTextVertOverflowType)getTypeLoader().parse(url, STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextVertOverflowType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextVertOverflowType)getTypeLoader().parse(url, STTextVertOverflowType.type, xmlOptions);
        }
        
        public static STTextVertOverflowType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextVertOverflowType)getTypeLoader().parse(inputStream, STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextVertOverflowType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextVertOverflowType)getTypeLoader().parse(inputStream, STTextVertOverflowType.type, xmlOptions);
        }
        
        public static STTextVertOverflowType parse(final Reader reader) throws XmlException, IOException {
            return (STTextVertOverflowType)getTypeLoader().parse(reader, STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextVertOverflowType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextVertOverflowType)getTypeLoader().parse(reader, STTextVertOverflowType.type, xmlOptions);
        }
        
        public static STTextVertOverflowType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextVertOverflowType)getTypeLoader().parse(xmlStreamReader, STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextVertOverflowType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextVertOverflowType)getTypeLoader().parse(xmlStreamReader, STTextVertOverflowType.type, xmlOptions);
        }
        
        public static STTextVertOverflowType parse(final Node node) throws XmlException {
            return (STTextVertOverflowType)getTypeLoader().parse(node, STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextVertOverflowType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextVertOverflowType)getTypeLoader().parse(node, STTextVertOverflowType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextVertOverflowType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextVertOverflowType)getTypeLoader().parse(xmlInputStream, STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextVertOverflowType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextVertOverflowType)getTypeLoader().parse(xmlInputStream, STTextVertOverflowType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextVertOverflowType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextVertOverflowType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_OVERFLOW = 1;
        static final int INT_ELLIPSIS = 2;
        static final int INT_CLIP = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("overflow", 1), new Enum("ellipsis", 2), new Enum("clip", 3) });
        }
    }
}
