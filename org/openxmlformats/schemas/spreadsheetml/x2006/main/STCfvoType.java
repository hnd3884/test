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

public interface STCfvoType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCfvoType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcfvotypeeb0ftype");
    public static final Enum NUM = Enum.forString("num");
    public static final Enum PERCENT = Enum.forString("percent");
    public static final Enum MAX = Enum.forString("max");
    public static final Enum MIN = Enum.forString("min");
    public static final Enum FORMULA = Enum.forString("formula");
    public static final Enum PERCENTILE = Enum.forString("percentile");
    public static final int INT_NUM = 1;
    public static final int INT_PERCENT = 2;
    public static final int INT_MAX = 3;
    public static final int INT_MIN = 4;
    public static final int INT_FORMULA = 5;
    public static final int INT_PERCENTILE = 6;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCfvoType newValue(final Object o) {
            return (STCfvoType)STCfvoType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCfvoType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCfvoType newInstance() {
            return (STCfvoType)getTypeLoader().newInstance(STCfvoType.type, (XmlOptions)null);
        }
        
        public static STCfvoType newInstance(final XmlOptions xmlOptions) {
            return (STCfvoType)getTypeLoader().newInstance(STCfvoType.type, xmlOptions);
        }
        
        public static STCfvoType parse(final String s) throws XmlException {
            return (STCfvoType)getTypeLoader().parse(s, STCfvoType.type, (XmlOptions)null);
        }
        
        public static STCfvoType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCfvoType)getTypeLoader().parse(s, STCfvoType.type, xmlOptions);
        }
        
        public static STCfvoType parse(final File file) throws XmlException, IOException {
            return (STCfvoType)getTypeLoader().parse(file, STCfvoType.type, (XmlOptions)null);
        }
        
        public static STCfvoType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCfvoType)getTypeLoader().parse(file, STCfvoType.type, xmlOptions);
        }
        
        public static STCfvoType parse(final URL url) throws XmlException, IOException {
            return (STCfvoType)getTypeLoader().parse(url, STCfvoType.type, (XmlOptions)null);
        }
        
        public static STCfvoType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCfvoType)getTypeLoader().parse(url, STCfvoType.type, xmlOptions);
        }
        
        public static STCfvoType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCfvoType)getTypeLoader().parse(inputStream, STCfvoType.type, (XmlOptions)null);
        }
        
        public static STCfvoType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCfvoType)getTypeLoader().parse(inputStream, STCfvoType.type, xmlOptions);
        }
        
        public static STCfvoType parse(final Reader reader) throws XmlException, IOException {
            return (STCfvoType)getTypeLoader().parse(reader, STCfvoType.type, (XmlOptions)null);
        }
        
        public static STCfvoType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCfvoType)getTypeLoader().parse(reader, STCfvoType.type, xmlOptions);
        }
        
        public static STCfvoType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCfvoType)getTypeLoader().parse(xmlStreamReader, STCfvoType.type, (XmlOptions)null);
        }
        
        public static STCfvoType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCfvoType)getTypeLoader().parse(xmlStreamReader, STCfvoType.type, xmlOptions);
        }
        
        public static STCfvoType parse(final Node node) throws XmlException {
            return (STCfvoType)getTypeLoader().parse(node, STCfvoType.type, (XmlOptions)null);
        }
        
        public static STCfvoType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCfvoType)getTypeLoader().parse(node, STCfvoType.type, xmlOptions);
        }
        
        @Deprecated
        public static STCfvoType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCfvoType)getTypeLoader().parse(xmlInputStream, STCfvoType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCfvoType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCfvoType)getTypeLoader().parse(xmlInputStream, STCfvoType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCfvoType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCfvoType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NUM = 1;
        static final int INT_PERCENT = 2;
        static final int INT_MAX = 3;
        static final int INT_MIN = 4;
        static final int INT_FORMULA = 5;
        static final int INT_PERCENTILE = 6;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("num", 1), new Enum("percent", 2), new Enum("max", 3), new Enum("min", 4), new Enum("formula", 5), new Enum("percentile", 6) });
        }
    }
}
