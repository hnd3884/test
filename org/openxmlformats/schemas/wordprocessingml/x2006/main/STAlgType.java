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

public interface STAlgType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STAlgType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stalgtype156ctype");
    public static final Enum TYPE_ANY = Enum.forString("typeAny");
    public static final int INT_TYPE_ANY = 1;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STAlgType newValue(final Object o) {
            return (STAlgType)STAlgType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STAlgType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STAlgType newInstance() {
            return (STAlgType)getTypeLoader().newInstance(STAlgType.type, (XmlOptions)null);
        }
        
        public static STAlgType newInstance(final XmlOptions xmlOptions) {
            return (STAlgType)getTypeLoader().newInstance(STAlgType.type, xmlOptions);
        }
        
        public static STAlgType parse(final String s) throws XmlException {
            return (STAlgType)getTypeLoader().parse(s, STAlgType.type, (XmlOptions)null);
        }
        
        public static STAlgType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STAlgType)getTypeLoader().parse(s, STAlgType.type, xmlOptions);
        }
        
        public static STAlgType parse(final File file) throws XmlException, IOException {
            return (STAlgType)getTypeLoader().parse(file, STAlgType.type, (XmlOptions)null);
        }
        
        public static STAlgType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAlgType)getTypeLoader().parse(file, STAlgType.type, xmlOptions);
        }
        
        public static STAlgType parse(final URL url) throws XmlException, IOException {
            return (STAlgType)getTypeLoader().parse(url, STAlgType.type, (XmlOptions)null);
        }
        
        public static STAlgType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAlgType)getTypeLoader().parse(url, STAlgType.type, xmlOptions);
        }
        
        public static STAlgType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STAlgType)getTypeLoader().parse(inputStream, STAlgType.type, (XmlOptions)null);
        }
        
        public static STAlgType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAlgType)getTypeLoader().parse(inputStream, STAlgType.type, xmlOptions);
        }
        
        public static STAlgType parse(final Reader reader) throws XmlException, IOException {
            return (STAlgType)getTypeLoader().parse(reader, STAlgType.type, (XmlOptions)null);
        }
        
        public static STAlgType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAlgType)getTypeLoader().parse(reader, STAlgType.type, xmlOptions);
        }
        
        public static STAlgType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STAlgType)getTypeLoader().parse(xmlStreamReader, STAlgType.type, (XmlOptions)null);
        }
        
        public static STAlgType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STAlgType)getTypeLoader().parse(xmlStreamReader, STAlgType.type, xmlOptions);
        }
        
        public static STAlgType parse(final Node node) throws XmlException {
            return (STAlgType)getTypeLoader().parse(node, STAlgType.type, (XmlOptions)null);
        }
        
        public static STAlgType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STAlgType)getTypeLoader().parse(node, STAlgType.type, xmlOptions);
        }
        
        @Deprecated
        public static STAlgType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STAlgType)getTypeLoader().parse(xmlInputStream, STAlgType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STAlgType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STAlgType)getTypeLoader().parse(xmlInputStream, STAlgType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAlgType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAlgType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TYPE_ANY = 1;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("typeAny", 1) });
        }
    }
}
