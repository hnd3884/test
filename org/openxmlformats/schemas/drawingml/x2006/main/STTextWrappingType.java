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

public interface STTextWrappingType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextWrappingType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextwrappingtype4b4etype");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum SQUARE = Enum.forString("square");
    public static final int INT_NONE = 1;
    public static final int INT_SQUARE = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextWrappingType newValue(final Object o) {
            return (STTextWrappingType)STTextWrappingType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextWrappingType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextWrappingType newInstance() {
            return (STTextWrappingType)getTypeLoader().newInstance(STTextWrappingType.type, (XmlOptions)null);
        }
        
        public static STTextWrappingType newInstance(final XmlOptions xmlOptions) {
            return (STTextWrappingType)getTypeLoader().newInstance(STTextWrappingType.type, xmlOptions);
        }
        
        public static STTextWrappingType parse(final String s) throws XmlException {
            return (STTextWrappingType)getTypeLoader().parse(s, STTextWrappingType.type, (XmlOptions)null);
        }
        
        public static STTextWrappingType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextWrappingType)getTypeLoader().parse(s, STTextWrappingType.type, xmlOptions);
        }
        
        public static STTextWrappingType parse(final File file) throws XmlException, IOException {
            return (STTextWrappingType)getTypeLoader().parse(file, STTextWrappingType.type, (XmlOptions)null);
        }
        
        public static STTextWrappingType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextWrappingType)getTypeLoader().parse(file, STTextWrappingType.type, xmlOptions);
        }
        
        public static STTextWrappingType parse(final URL url) throws XmlException, IOException {
            return (STTextWrappingType)getTypeLoader().parse(url, STTextWrappingType.type, (XmlOptions)null);
        }
        
        public static STTextWrappingType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextWrappingType)getTypeLoader().parse(url, STTextWrappingType.type, xmlOptions);
        }
        
        public static STTextWrappingType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextWrappingType)getTypeLoader().parse(inputStream, STTextWrappingType.type, (XmlOptions)null);
        }
        
        public static STTextWrappingType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextWrappingType)getTypeLoader().parse(inputStream, STTextWrappingType.type, xmlOptions);
        }
        
        public static STTextWrappingType parse(final Reader reader) throws XmlException, IOException {
            return (STTextWrappingType)getTypeLoader().parse(reader, STTextWrappingType.type, (XmlOptions)null);
        }
        
        public static STTextWrappingType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextWrappingType)getTypeLoader().parse(reader, STTextWrappingType.type, xmlOptions);
        }
        
        public static STTextWrappingType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextWrappingType)getTypeLoader().parse(xmlStreamReader, STTextWrappingType.type, (XmlOptions)null);
        }
        
        public static STTextWrappingType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextWrappingType)getTypeLoader().parse(xmlStreamReader, STTextWrappingType.type, xmlOptions);
        }
        
        public static STTextWrappingType parse(final Node node) throws XmlException {
            return (STTextWrappingType)getTypeLoader().parse(node, STTextWrappingType.type, (XmlOptions)null);
        }
        
        public static STTextWrappingType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextWrappingType)getTypeLoader().parse(node, STTextWrappingType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextWrappingType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextWrappingType)getTypeLoader().parse(xmlInputStream, STTextWrappingType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextWrappingType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextWrappingType)getTypeLoader().parse(xmlInputStream, STTextWrappingType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextWrappingType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextWrappingType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_SQUARE = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("square", 2) });
        }
    }
}
