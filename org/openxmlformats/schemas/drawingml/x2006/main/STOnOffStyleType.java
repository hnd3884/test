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

public interface STOnOffStyleType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STOnOffStyleType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stonoffstyletype4606type");
    public static final Enum ON = Enum.forString("on");
    public static final Enum OFF = Enum.forString("off");
    public static final Enum DEF = Enum.forString("def");
    public static final int INT_ON = 1;
    public static final int INT_OFF = 2;
    public static final int INT_DEF = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STOnOffStyleType newValue(final Object o) {
            return (STOnOffStyleType)STOnOffStyleType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STOnOffStyleType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STOnOffStyleType newInstance() {
            return (STOnOffStyleType)getTypeLoader().newInstance(STOnOffStyleType.type, (XmlOptions)null);
        }
        
        public static STOnOffStyleType newInstance(final XmlOptions xmlOptions) {
            return (STOnOffStyleType)getTypeLoader().newInstance(STOnOffStyleType.type, xmlOptions);
        }
        
        public static STOnOffStyleType parse(final String s) throws XmlException {
            return (STOnOffStyleType)getTypeLoader().parse(s, STOnOffStyleType.type, (XmlOptions)null);
        }
        
        public static STOnOffStyleType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STOnOffStyleType)getTypeLoader().parse(s, STOnOffStyleType.type, xmlOptions);
        }
        
        public static STOnOffStyleType parse(final File file) throws XmlException, IOException {
            return (STOnOffStyleType)getTypeLoader().parse(file, STOnOffStyleType.type, (XmlOptions)null);
        }
        
        public static STOnOffStyleType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOnOffStyleType)getTypeLoader().parse(file, STOnOffStyleType.type, xmlOptions);
        }
        
        public static STOnOffStyleType parse(final URL url) throws XmlException, IOException {
            return (STOnOffStyleType)getTypeLoader().parse(url, STOnOffStyleType.type, (XmlOptions)null);
        }
        
        public static STOnOffStyleType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOnOffStyleType)getTypeLoader().parse(url, STOnOffStyleType.type, xmlOptions);
        }
        
        public static STOnOffStyleType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STOnOffStyleType)getTypeLoader().parse(inputStream, STOnOffStyleType.type, (XmlOptions)null);
        }
        
        public static STOnOffStyleType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOnOffStyleType)getTypeLoader().parse(inputStream, STOnOffStyleType.type, xmlOptions);
        }
        
        public static STOnOffStyleType parse(final Reader reader) throws XmlException, IOException {
            return (STOnOffStyleType)getTypeLoader().parse(reader, STOnOffStyleType.type, (XmlOptions)null);
        }
        
        public static STOnOffStyleType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOnOffStyleType)getTypeLoader().parse(reader, STOnOffStyleType.type, xmlOptions);
        }
        
        public static STOnOffStyleType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STOnOffStyleType)getTypeLoader().parse(xmlStreamReader, STOnOffStyleType.type, (XmlOptions)null);
        }
        
        public static STOnOffStyleType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STOnOffStyleType)getTypeLoader().parse(xmlStreamReader, STOnOffStyleType.type, xmlOptions);
        }
        
        public static STOnOffStyleType parse(final Node node) throws XmlException {
            return (STOnOffStyleType)getTypeLoader().parse(node, STOnOffStyleType.type, (XmlOptions)null);
        }
        
        public static STOnOffStyleType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STOnOffStyleType)getTypeLoader().parse(node, STOnOffStyleType.type, xmlOptions);
        }
        
        @Deprecated
        public static STOnOffStyleType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STOnOffStyleType)getTypeLoader().parse(xmlInputStream, STOnOffStyleType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STOnOffStyleType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STOnOffStyleType)getTypeLoader().parse(xmlInputStream, STOnOffStyleType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STOnOffStyleType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STOnOffStyleType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_ON = 1;
        static final int INT_OFF = 2;
        static final int INT_DEF = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("on", 1), new Enum("off", 2), new Enum("def", 3) });
        }
    }
}
