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

public interface STTextCapsType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextCapsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextcapstyped233type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum SMALL = Enum.forString("small");
    public static final Enum ALL = Enum.forString("all");
    public static final int INT_NONE = 1;
    public static final int INT_SMALL = 2;
    public static final int INT_ALL = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextCapsType newValue(final Object o) {
            return (STTextCapsType)STTextCapsType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextCapsType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextCapsType newInstance() {
            return (STTextCapsType)getTypeLoader().newInstance(STTextCapsType.type, (XmlOptions)null);
        }
        
        public static STTextCapsType newInstance(final XmlOptions xmlOptions) {
            return (STTextCapsType)getTypeLoader().newInstance(STTextCapsType.type, xmlOptions);
        }
        
        public static STTextCapsType parse(final String s) throws XmlException {
            return (STTextCapsType)getTypeLoader().parse(s, STTextCapsType.type, (XmlOptions)null);
        }
        
        public static STTextCapsType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextCapsType)getTypeLoader().parse(s, STTextCapsType.type, xmlOptions);
        }
        
        public static STTextCapsType parse(final File file) throws XmlException, IOException {
            return (STTextCapsType)getTypeLoader().parse(file, STTextCapsType.type, (XmlOptions)null);
        }
        
        public static STTextCapsType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextCapsType)getTypeLoader().parse(file, STTextCapsType.type, xmlOptions);
        }
        
        public static STTextCapsType parse(final URL url) throws XmlException, IOException {
            return (STTextCapsType)getTypeLoader().parse(url, STTextCapsType.type, (XmlOptions)null);
        }
        
        public static STTextCapsType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextCapsType)getTypeLoader().parse(url, STTextCapsType.type, xmlOptions);
        }
        
        public static STTextCapsType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextCapsType)getTypeLoader().parse(inputStream, STTextCapsType.type, (XmlOptions)null);
        }
        
        public static STTextCapsType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextCapsType)getTypeLoader().parse(inputStream, STTextCapsType.type, xmlOptions);
        }
        
        public static STTextCapsType parse(final Reader reader) throws XmlException, IOException {
            return (STTextCapsType)getTypeLoader().parse(reader, STTextCapsType.type, (XmlOptions)null);
        }
        
        public static STTextCapsType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextCapsType)getTypeLoader().parse(reader, STTextCapsType.type, xmlOptions);
        }
        
        public static STTextCapsType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextCapsType)getTypeLoader().parse(xmlStreamReader, STTextCapsType.type, (XmlOptions)null);
        }
        
        public static STTextCapsType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextCapsType)getTypeLoader().parse(xmlStreamReader, STTextCapsType.type, xmlOptions);
        }
        
        public static STTextCapsType parse(final Node node) throws XmlException {
            return (STTextCapsType)getTypeLoader().parse(node, STTextCapsType.type, (XmlOptions)null);
        }
        
        public static STTextCapsType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextCapsType)getTypeLoader().parse(node, STTextCapsType.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextCapsType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextCapsType)getTypeLoader().parse(xmlInputStream, STTextCapsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextCapsType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextCapsType)getTypeLoader().parse(xmlInputStream, STTextCapsType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextCapsType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextCapsType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_SMALL = 2;
        static final int INT_ALL = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("small", 2), new Enum("all", 3) });
        }
    }
}
