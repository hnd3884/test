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

public interface STFtnEdn extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STFtnEdn.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stftnednd4c9type");
    public static final Enum NORMAL = Enum.forString("normal");
    public static final Enum SEPARATOR = Enum.forString("separator");
    public static final Enum CONTINUATION_SEPARATOR = Enum.forString("continuationSeparator");
    public static final Enum CONTINUATION_NOTICE = Enum.forString("continuationNotice");
    public static final int INT_NORMAL = 1;
    public static final int INT_SEPARATOR = 2;
    public static final int INT_CONTINUATION_SEPARATOR = 3;
    public static final int INT_CONTINUATION_NOTICE = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STFtnEdn newValue(final Object o) {
            return (STFtnEdn)STFtnEdn.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STFtnEdn.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STFtnEdn newInstance() {
            return (STFtnEdn)getTypeLoader().newInstance(STFtnEdn.type, (XmlOptions)null);
        }
        
        public static STFtnEdn newInstance(final XmlOptions xmlOptions) {
            return (STFtnEdn)getTypeLoader().newInstance(STFtnEdn.type, xmlOptions);
        }
        
        public static STFtnEdn parse(final String s) throws XmlException {
            return (STFtnEdn)getTypeLoader().parse(s, STFtnEdn.type, (XmlOptions)null);
        }
        
        public static STFtnEdn parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STFtnEdn)getTypeLoader().parse(s, STFtnEdn.type, xmlOptions);
        }
        
        public static STFtnEdn parse(final File file) throws XmlException, IOException {
            return (STFtnEdn)getTypeLoader().parse(file, STFtnEdn.type, (XmlOptions)null);
        }
        
        public static STFtnEdn parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFtnEdn)getTypeLoader().parse(file, STFtnEdn.type, xmlOptions);
        }
        
        public static STFtnEdn parse(final URL url) throws XmlException, IOException {
            return (STFtnEdn)getTypeLoader().parse(url, STFtnEdn.type, (XmlOptions)null);
        }
        
        public static STFtnEdn parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFtnEdn)getTypeLoader().parse(url, STFtnEdn.type, xmlOptions);
        }
        
        public static STFtnEdn parse(final InputStream inputStream) throws XmlException, IOException {
            return (STFtnEdn)getTypeLoader().parse(inputStream, STFtnEdn.type, (XmlOptions)null);
        }
        
        public static STFtnEdn parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFtnEdn)getTypeLoader().parse(inputStream, STFtnEdn.type, xmlOptions);
        }
        
        public static STFtnEdn parse(final Reader reader) throws XmlException, IOException {
            return (STFtnEdn)getTypeLoader().parse(reader, STFtnEdn.type, (XmlOptions)null);
        }
        
        public static STFtnEdn parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFtnEdn)getTypeLoader().parse(reader, STFtnEdn.type, xmlOptions);
        }
        
        public static STFtnEdn parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STFtnEdn)getTypeLoader().parse(xmlStreamReader, STFtnEdn.type, (XmlOptions)null);
        }
        
        public static STFtnEdn parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STFtnEdn)getTypeLoader().parse(xmlStreamReader, STFtnEdn.type, xmlOptions);
        }
        
        public static STFtnEdn parse(final Node node) throws XmlException {
            return (STFtnEdn)getTypeLoader().parse(node, STFtnEdn.type, (XmlOptions)null);
        }
        
        public static STFtnEdn parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STFtnEdn)getTypeLoader().parse(node, STFtnEdn.type, xmlOptions);
        }
        
        @Deprecated
        public static STFtnEdn parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STFtnEdn)getTypeLoader().parse(xmlInputStream, STFtnEdn.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STFtnEdn parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STFtnEdn)getTypeLoader().parse(xmlInputStream, STFtnEdn.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFtnEdn.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFtnEdn.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NORMAL = 1;
        static final int INT_SEPARATOR = 2;
        static final int INT_CONTINUATION_SEPARATOR = 3;
        static final int INT_CONTINUATION_NOTICE = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("normal", 1), new Enum("separator", 2), new Enum("continuationSeparator", 3), new Enum("continuationNotice", 4) });
        }
    }
}
