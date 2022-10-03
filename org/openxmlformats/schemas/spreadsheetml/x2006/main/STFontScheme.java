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

public interface STFontScheme extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STFontScheme.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stfontschemef36dtype");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum MAJOR = Enum.forString("major");
    public static final Enum MINOR = Enum.forString("minor");
    public static final int INT_NONE = 1;
    public static final int INT_MAJOR = 2;
    public static final int INT_MINOR = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STFontScheme newValue(final Object o) {
            return (STFontScheme)STFontScheme.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STFontScheme.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STFontScheme newInstance() {
            return (STFontScheme)getTypeLoader().newInstance(STFontScheme.type, (XmlOptions)null);
        }
        
        public static STFontScheme newInstance(final XmlOptions xmlOptions) {
            return (STFontScheme)getTypeLoader().newInstance(STFontScheme.type, xmlOptions);
        }
        
        public static STFontScheme parse(final String s) throws XmlException {
            return (STFontScheme)getTypeLoader().parse(s, STFontScheme.type, (XmlOptions)null);
        }
        
        public static STFontScheme parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STFontScheme)getTypeLoader().parse(s, STFontScheme.type, xmlOptions);
        }
        
        public static STFontScheme parse(final File file) throws XmlException, IOException {
            return (STFontScheme)getTypeLoader().parse(file, STFontScheme.type, (XmlOptions)null);
        }
        
        public static STFontScheme parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontScheme)getTypeLoader().parse(file, STFontScheme.type, xmlOptions);
        }
        
        public static STFontScheme parse(final URL url) throws XmlException, IOException {
            return (STFontScheme)getTypeLoader().parse(url, STFontScheme.type, (XmlOptions)null);
        }
        
        public static STFontScheme parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontScheme)getTypeLoader().parse(url, STFontScheme.type, xmlOptions);
        }
        
        public static STFontScheme parse(final InputStream inputStream) throws XmlException, IOException {
            return (STFontScheme)getTypeLoader().parse(inputStream, STFontScheme.type, (XmlOptions)null);
        }
        
        public static STFontScheme parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontScheme)getTypeLoader().parse(inputStream, STFontScheme.type, xmlOptions);
        }
        
        public static STFontScheme parse(final Reader reader) throws XmlException, IOException {
            return (STFontScheme)getTypeLoader().parse(reader, STFontScheme.type, (XmlOptions)null);
        }
        
        public static STFontScheme parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontScheme)getTypeLoader().parse(reader, STFontScheme.type, xmlOptions);
        }
        
        public static STFontScheme parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STFontScheme)getTypeLoader().parse(xmlStreamReader, STFontScheme.type, (XmlOptions)null);
        }
        
        public static STFontScheme parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STFontScheme)getTypeLoader().parse(xmlStreamReader, STFontScheme.type, xmlOptions);
        }
        
        public static STFontScheme parse(final Node node) throws XmlException {
            return (STFontScheme)getTypeLoader().parse(node, STFontScheme.type, (XmlOptions)null);
        }
        
        public static STFontScheme parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STFontScheme)getTypeLoader().parse(node, STFontScheme.type, xmlOptions);
        }
        
        @Deprecated
        public static STFontScheme parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STFontScheme)getTypeLoader().parse(xmlInputStream, STFontScheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STFontScheme parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STFontScheme)getTypeLoader().parse(xmlInputStream, STFontScheme.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFontScheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFontScheme.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_MAJOR = 2;
        static final int INT_MINOR = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("major", 2), new Enum("minor", 3) });
        }
    }
}
