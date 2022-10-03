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

public interface STOnOff extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STOnOff.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stonofffcd2type");
    public static final Enum TRUE = Enum.forString("true");
    public static final Enum FALSE = Enum.forString("false");
    public static final Enum ON = Enum.forString("on");
    public static final Enum OFF = Enum.forString("off");
    public static final Enum X_0 = Enum.forString("0");
    public static final Enum X_1 = Enum.forString("1");
    public static final int INT_TRUE = 1;
    public static final int INT_FALSE = 2;
    public static final int INT_ON = 3;
    public static final int INT_OFF = 4;
    public static final int INT_X_0 = 5;
    public static final int INT_X_1 = 6;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STOnOff newValue(final Object o) {
            return (STOnOff)STOnOff.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STOnOff.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STOnOff newInstance() {
            return (STOnOff)getTypeLoader().newInstance(STOnOff.type, (XmlOptions)null);
        }
        
        public static STOnOff newInstance(final XmlOptions xmlOptions) {
            return (STOnOff)getTypeLoader().newInstance(STOnOff.type, xmlOptions);
        }
        
        public static STOnOff parse(final String s) throws XmlException {
            return (STOnOff)getTypeLoader().parse(s, STOnOff.type, (XmlOptions)null);
        }
        
        public static STOnOff parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STOnOff)getTypeLoader().parse(s, STOnOff.type, xmlOptions);
        }
        
        public static STOnOff parse(final File file) throws XmlException, IOException {
            return (STOnOff)getTypeLoader().parse(file, STOnOff.type, (XmlOptions)null);
        }
        
        public static STOnOff parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOnOff)getTypeLoader().parse(file, STOnOff.type, xmlOptions);
        }
        
        public static STOnOff parse(final URL url) throws XmlException, IOException {
            return (STOnOff)getTypeLoader().parse(url, STOnOff.type, (XmlOptions)null);
        }
        
        public static STOnOff parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOnOff)getTypeLoader().parse(url, STOnOff.type, xmlOptions);
        }
        
        public static STOnOff parse(final InputStream inputStream) throws XmlException, IOException {
            return (STOnOff)getTypeLoader().parse(inputStream, STOnOff.type, (XmlOptions)null);
        }
        
        public static STOnOff parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOnOff)getTypeLoader().parse(inputStream, STOnOff.type, xmlOptions);
        }
        
        public static STOnOff parse(final Reader reader) throws XmlException, IOException {
            return (STOnOff)getTypeLoader().parse(reader, STOnOff.type, (XmlOptions)null);
        }
        
        public static STOnOff parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOnOff)getTypeLoader().parse(reader, STOnOff.type, xmlOptions);
        }
        
        public static STOnOff parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STOnOff)getTypeLoader().parse(xmlStreamReader, STOnOff.type, (XmlOptions)null);
        }
        
        public static STOnOff parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STOnOff)getTypeLoader().parse(xmlStreamReader, STOnOff.type, xmlOptions);
        }
        
        public static STOnOff parse(final Node node) throws XmlException {
            return (STOnOff)getTypeLoader().parse(node, STOnOff.type, (XmlOptions)null);
        }
        
        public static STOnOff parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STOnOff)getTypeLoader().parse(node, STOnOff.type, xmlOptions);
        }
        
        @Deprecated
        public static STOnOff parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STOnOff)getTypeLoader().parse(xmlInputStream, STOnOff.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STOnOff parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STOnOff)getTypeLoader().parse(xmlInputStream, STOnOff.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STOnOff.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STOnOff.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TRUE = 1;
        static final int INT_FALSE = 2;
        static final int INT_ON = 3;
        static final int INT_OFF = 4;
        static final int INT_X_0 = 5;
        static final int INT_X_1 = 6;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("true", 1), new Enum("false", 2), new Enum("on", 3), new Enum("off", 4), new Enum("0", 5), new Enum("1", 6) });
        }
    }
}
