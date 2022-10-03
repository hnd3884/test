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

public interface STAlgClass extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STAlgClass.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stalgclass061ctype");
    public static final Enum HASH = Enum.forString("hash");
    public static final int INT_HASH = 1;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STAlgClass newValue(final Object o) {
            return (STAlgClass)STAlgClass.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STAlgClass.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STAlgClass newInstance() {
            return (STAlgClass)getTypeLoader().newInstance(STAlgClass.type, (XmlOptions)null);
        }
        
        public static STAlgClass newInstance(final XmlOptions xmlOptions) {
            return (STAlgClass)getTypeLoader().newInstance(STAlgClass.type, xmlOptions);
        }
        
        public static STAlgClass parse(final String s) throws XmlException {
            return (STAlgClass)getTypeLoader().parse(s, STAlgClass.type, (XmlOptions)null);
        }
        
        public static STAlgClass parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STAlgClass)getTypeLoader().parse(s, STAlgClass.type, xmlOptions);
        }
        
        public static STAlgClass parse(final File file) throws XmlException, IOException {
            return (STAlgClass)getTypeLoader().parse(file, STAlgClass.type, (XmlOptions)null);
        }
        
        public static STAlgClass parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAlgClass)getTypeLoader().parse(file, STAlgClass.type, xmlOptions);
        }
        
        public static STAlgClass parse(final URL url) throws XmlException, IOException {
            return (STAlgClass)getTypeLoader().parse(url, STAlgClass.type, (XmlOptions)null);
        }
        
        public static STAlgClass parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAlgClass)getTypeLoader().parse(url, STAlgClass.type, xmlOptions);
        }
        
        public static STAlgClass parse(final InputStream inputStream) throws XmlException, IOException {
            return (STAlgClass)getTypeLoader().parse(inputStream, STAlgClass.type, (XmlOptions)null);
        }
        
        public static STAlgClass parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAlgClass)getTypeLoader().parse(inputStream, STAlgClass.type, xmlOptions);
        }
        
        public static STAlgClass parse(final Reader reader) throws XmlException, IOException {
            return (STAlgClass)getTypeLoader().parse(reader, STAlgClass.type, (XmlOptions)null);
        }
        
        public static STAlgClass parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAlgClass)getTypeLoader().parse(reader, STAlgClass.type, xmlOptions);
        }
        
        public static STAlgClass parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STAlgClass)getTypeLoader().parse(xmlStreamReader, STAlgClass.type, (XmlOptions)null);
        }
        
        public static STAlgClass parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STAlgClass)getTypeLoader().parse(xmlStreamReader, STAlgClass.type, xmlOptions);
        }
        
        public static STAlgClass parse(final Node node) throws XmlException {
            return (STAlgClass)getTypeLoader().parse(node, STAlgClass.type, (XmlOptions)null);
        }
        
        public static STAlgClass parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STAlgClass)getTypeLoader().parse(node, STAlgClass.type, xmlOptions);
        }
        
        @Deprecated
        public static STAlgClass parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STAlgClass)getTypeLoader().parse(xmlInputStream, STAlgClass.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STAlgClass parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STAlgClass)getTypeLoader().parse(xmlInputStream, STAlgClass.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAlgClass.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAlgClass.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_HASH = 1;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("hash", 1) });
        }
    }
}
