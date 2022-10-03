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

public interface STStyleType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STStyleType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ststyletypec2b7type");
    public static final Enum PARAGRAPH = Enum.forString("paragraph");
    public static final Enum CHARACTER = Enum.forString("character");
    public static final Enum TABLE = Enum.forString("table");
    public static final Enum NUMBERING = Enum.forString("numbering");
    public static final int INT_PARAGRAPH = 1;
    public static final int INT_CHARACTER = 2;
    public static final int INT_TABLE = 3;
    public static final int INT_NUMBERING = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STStyleType newValue(final Object o) {
            return (STStyleType)STStyleType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STStyleType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STStyleType newInstance() {
            return (STStyleType)getTypeLoader().newInstance(STStyleType.type, (XmlOptions)null);
        }
        
        public static STStyleType newInstance(final XmlOptions xmlOptions) {
            return (STStyleType)getTypeLoader().newInstance(STStyleType.type, xmlOptions);
        }
        
        public static STStyleType parse(final String s) throws XmlException {
            return (STStyleType)getTypeLoader().parse(s, STStyleType.type, (XmlOptions)null);
        }
        
        public static STStyleType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STStyleType)getTypeLoader().parse(s, STStyleType.type, xmlOptions);
        }
        
        public static STStyleType parse(final File file) throws XmlException, IOException {
            return (STStyleType)getTypeLoader().parse(file, STStyleType.type, (XmlOptions)null);
        }
        
        public static STStyleType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStyleType)getTypeLoader().parse(file, STStyleType.type, xmlOptions);
        }
        
        public static STStyleType parse(final URL url) throws XmlException, IOException {
            return (STStyleType)getTypeLoader().parse(url, STStyleType.type, (XmlOptions)null);
        }
        
        public static STStyleType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStyleType)getTypeLoader().parse(url, STStyleType.type, xmlOptions);
        }
        
        public static STStyleType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STStyleType)getTypeLoader().parse(inputStream, STStyleType.type, (XmlOptions)null);
        }
        
        public static STStyleType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStyleType)getTypeLoader().parse(inputStream, STStyleType.type, xmlOptions);
        }
        
        public static STStyleType parse(final Reader reader) throws XmlException, IOException {
            return (STStyleType)getTypeLoader().parse(reader, STStyleType.type, (XmlOptions)null);
        }
        
        public static STStyleType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STStyleType)getTypeLoader().parse(reader, STStyleType.type, xmlOptions);
        }
        
        public static STStyleType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STStyleType)getTypeLoader().parse(xmlStreamReader, STStyleType.type, (XmlOptions)null);
        }
        
        public static STStyleType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STStyleType)getTypeLoader().parse(xmlStreamReader, STStyleType.type, xmlOptions);
        }
        
        public static STStyleType parse(final Node node) throws XmlException {
            return (STStyleType)getTypeLoader().parse(node, STStyleType.type, (XmlOptions)null);
        }
        
        public static STStyleType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STStyleType)getTypeLoader().parse(node, STStyleType.type, xmlOptions);
        }
        
        @Deprecated
        public static STStyleType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STStyleType)getTypeLoader().parse(xmlInputStream, STStyleType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STStyleType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STStyleType)getTypeLoader().parse(xmlInputStream, STStyleType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STStyleType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STStyleType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_PARAGRAPH = 1;
        static final int INT_CHARACTER = 2;
        static final int INT_TABLE = 3;
        static final int INT_NUMBERING = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("paragraph", 1), new Enum("character", 2), new Enum("table", 3), new Enum("numbering", 4) });
        }
    }
}
