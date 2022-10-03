package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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

public interface STSourceType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STSourceType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stsourcetype074etype");
    public static final Enum WORKSHEET = Enum.forString("worksheet");
    public static final Enum EXTERNAL = Enum.forString("external");
    public static final Enum CONSOLIDATION = Enum.forString("consolidation");
    public static final Enum SCENARIO = Enum.forString("scenario");
    public static final int INT_WORKSHEET = 1;
    public static final int INT_EXTERNAL = 2;
    public static final int INT_CONSOLIDATION = 3;
    public static final int INT_SCENARIO = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STSourceType newValue(final Object o) {
            return (STSourceType)STSourceType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STSourceType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STSourceType newInstance() {
            return (STSourceType)getTypeLoader().newInstance(STSourceType.type, (XmlOptions)null);
        }
        
        public static STSourceType newInstance(final XmlOptions xmlOptions) {
            return (STSourceType)getTypeLoader().newInstance(STSourceType.type, xmlOptions);
        }
        
        public static STSourceType parse(final String s) throws XmlException {
            return (STSourceType)getTypeLoader().parse(s, STSourceType.type, (XmlOptions)null);
        }
        
        public static STSourceType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STSourceType)getTypeLoader().parse(s, STSourceType.type, xmlOptions);
        }
        
        public static STSourceType parse(final File file) throws XmlException, IOException {
            return (STSourceType)getTypeLoader().parse(file, STSourceType.type, (XmlOptions)null);
        }
        
        public static STSourceType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSourceType)getTypeLoader().parse(file, STSourceType.type, xmlOptions);
        }
        
        public static STSourceType parse(final URL url) throws XmlException, IOException {
            return (STSourceType)getTypeLoader().parse(url, STSourceType.type, (XmlOptions)null);
        }
        
        public static STSourceType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSourceType)getTypeLoader().parse(url, STSourceType.type, xmlOptions);
        }
        
        public static STSourceType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STSourceType)getTypeLoader().parse(inputStream, STSourceType.type, (XmlOptions)null);
        }
        
        public static STSourceType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSourceType)getTypeLoader().parse(inputStream, STSourceType.type, xmlOptions);
        }
        
        public static STSourceType parse(final Reader reader) throws XmlException, IOException {
            return (STSourceType)getTypeLoader().parse(reader, STSourceType.type, (XmlOptions)null);
        }
        
        public static STSourceType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STSourceType)getTypeLoader().parse(reader, STSourceType.type, xmlOptions);
        }
        
        public static STSourceType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STSourceType)getTypeLoader().parse(xmlStreamReader, STSourceType.type, (XmlOptions)null);
        }
        
        public static STSourceType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STSourceType)getTypeLoader().parse(xmlStreamReader, STSourceType.type, xmlOptions);
        }
        
        public static STSourceType parse(final Node node) throws XmlException {
            return (STSourceType)getTypeLoader().parse(node, STSourceType.type, (XmlOptions)null);
        }
        
        public static STSourceType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STSourceType)getTypeLoader().parse(node, STSourceType.type, xmlOptions);
        }
        
        @Deprecated
        public static STSourceType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STSourceType)getTypeLoader().parse(xmlInputStream, STSourceType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STSourceType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STSourceType)getTypeLoader().parse(xmlInputStream, STSourceType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSourceType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STSourceType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_WORKSHEET = 1;
        static final int INT_EXTERNAL = 2;
        static final int INT_CONSOLIDATION = 3;
        static final int INT_SCENARIO = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("worksheet", 1), new Enum("external", 2), new Enum("consolidation", 3), new Enum("scenario", 4) });
        }
    }
}
