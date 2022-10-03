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

public interface STTextHorzOverflowType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextHorzOverflowType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttexthorzoverflowtype6003type");
    public static final Enum OVERFLOW = Enum.forString("overflow");
    public static final Enum CLIP = Enum.forString("clip");
    public static final int INT_OVERFLOW = 1;
    public static final int INT_CLIP = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextHorzOverflowType newValue(final Object o) {
            return (STTextHorzOverflowType)STTextHorzOverflowType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextHorzOverflowType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextHorzOverflowType newInstance() {
            return (STTextHorzOverflowType)getTypeLoader().newInstance(STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextHorzOverflowType newInstance(final XmlOptions xmlOptions) {
            return (STTextHorzOverflowType)getTypeLoader().newInstance(STTextHorzOverflowType.type, xmlOptions);
        }
        
        public static STTextHorzOverflowType parse(final String s) throws XmlException {
            return (STTextHorzOverflowType)getTypeLoader().parse(s, STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextHorzOverflowType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextHorzOverflowType)getTypeLoader().parse(s, STTextHorzOverflowType.type, xmlOptions);
        }
        
        public static STTextHorzOverflowType parse(final File file) throws XmlException, IOException {
            return (STTextHorzOverflowType)getTypeLoader().parse(file, STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextHorzOverflowType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextHorzOverflowType)getTypeLoader().parse(file, STTextHorzOverflowType.type, xmlOptions);
        }
        
        public static STTextHorzOverflowType parse(final URL url) throws XmlException, IOException {
            return (STTextHorzOverflowType)getTypeLoader().parse(url, STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextHorzOverflowType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextHorzOverflowType)getTypeLoader().parse(url, STTextHorzOverflowType.type, xmlOptions);
        }
        
        public static STTextHorzOverflowType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextHorzOverflowType)getTypeLoader().parse(inputStream, STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextHorzOverflowType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextHorzOverflowType)getTypeLoader().parse(inputStream, STTextHorzOverflowType.type, xmlOptions);
        }
        
        public static STTextHorzOverflowType parse(final Reader reader) throws XmlException, IOException {
            return (STTextHorzOverflowType)getTypeLoader().parse(reader, STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextHorzOverflowType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextHorzOverflowType)getTypeLoader().parse(reader, STTextHorzOverflowType.type, xmlOptions);
        }
        
        public static STTextHorzOverflowType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextHorzOverflowType)getTypeLoader().parse(xmlStreamReader, STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextHorzOverflowType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextHorzOverflowType)getTypeLoader().parse(xmlStreamReader, STTextHorzOverflowType.type, xmlOptions);
        }
        
        public static STTextHorzOverflowType parse(final Node node) throws XmlException {
            return (STTextHorzOverflowType)getTypeLoader().parse(node, STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        public static STTextHorzOverflowType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextHorzOverflowType)getTypeLoader().parse(node, STTextHorzOverflowType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextHorzOverflowType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextHorzOverflowType)getTypeLoader().parse(xmlInputStream, STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextHorzOverflowType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextHorzOverflowType)getTypeLoader().parse(xmlInputStream, STTextHorzOverflowType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextHorzOverflowType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextHorzOverflowType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_OVERFLOW = 1;
        static final int INT_CLIP = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("overflow", 1), new Enum("clip", 2) });
        }
    }
}
