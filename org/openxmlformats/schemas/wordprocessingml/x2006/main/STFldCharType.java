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

public interface STFldCharType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STFldCharType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stfldchartype1eb4type");
    public static final Enum BEGIN = Enum.forString("begin");
    public static final Enum SEPARATE = Enum.forString("separate");
    public static final Enum END = Enum.forString("end");
    public static final int INT_BEGIN = 1;
    public static final int INT_SEPARATE = 2;
    public static final int INT_END = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STFldCharType newValue(final Object o) {
            return (STFldCharType)STFldCharType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STFldCharType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STFldCharType newInstance() {
            return (STFldCharType)getTypeLoader().newInstance(STFldCharType.type, (XmlOptions)null);
        }
        
        public static STFldCharType newInstance(final XmlOptions xmlOptions) {
            return (STFldCharType)getTypeLoader().newInstance(STFldCharType.type, xmlOptions);
        }
        
        public static STFldCharType parse(final String s) throws XmlException {
            return (STFldCharType)getTypeLoader().parse(s, STFldCharType.type, (XmlOptions)null);
        }
        
        public static STFldCharType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STFldCharType)getTypeLoader().parse(s, STFldCharType.type, xmlOptions);
        }
        
        public static STFldCharType parse(final File file) throws XmlException, IOException {
            return (STFldCharType)getTypeLoader().parse(file, STFldCharType.type, (XmlOptions)null);
        }
        
        public static STFldCharType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFldCharType)getTypeLoader().parse(file, STFldCharType.type, xmlOptions);
        }
        
        public static STFldCharType parse(final URL url) throws XmlException, IOException {
            return (STFldCharType)getTypeLoader().parse(url, STFldCharType.type, (XmlOptions)null);
        }
        
        public static STFldCharType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFldCharType)getTypeLoader().parse(url, STFldCharType.type, xmlOptions);
        }
        
        public static STFldCharType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STFldCharType)getTypeLoader().parse(inputStream, STFldCharType.type, (XmlOptions)null);
        }
        
        public static STFldCharType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFldCharType)getTypeLoader().parse(inputStream, STFldCharType.type, xmlOptions);
        }
        
        public static STFldCharType parse(final Reader reader) throws XmlException, IOException {
            return (STFldCharType)getTypeLoader().parse(reader, STFldCharType.type, (XmlOptions)null);
        }
        
        public static STFldCharType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFldCharType)getTypeLoader().parse(reader, STFldCharType.type, xmlOptions);
        }
        
        public static STFldCharType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STFldCharType)getTypeLoader().parse(xmlStreamReader, STFldCharType.type, (XmlOptions)null);
        }
        
        public static STFldCharType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STFldCharType)getTypeLoader().parse(xmlStreamReader, STFldCharType.type, xmlOptions);
        }
        
        public static STFldCharType parse(final Node node) throws XmlException {
            return (STFldCharType)getTypeLoader().parse(node, STFldCharType.type, (XmlOptions)null);
        }
        
        public static STFldCharType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STFldCharType)getTypeLoader().parse(node, STFldCharType.type, xmlOptions);
        }
        
        @Deprecated
        public static STFldCharType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STFldCharType)getTypeLoader().parse(xmlInputStream, STFldCharType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STFldCharType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STFldCharType)getTypeLoader().parse(xmlInputStream, STFldCharType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFldCharType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFldCharType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_BEGIN = 1;
        static final int INT_SEPARATE = 2;
        static final int INT_END = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("begin", 1), new Enum("separate", 2), new Enum("end", 3) });
        }
    }
}
