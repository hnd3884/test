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

public interface STFontCollectionIndex extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STFontCollectionIndex.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stfontcollectionindex6766type");
    public static final Enum MAJOR = Enum.forString("major");
    public static final Enum MINOR = Enum.forString("minor");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_MAJOR = 1;
    public static final int INT_MINOR = 2;
    public static final int INT_NONE = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STFontCollectionIndex newValue(final Object o) {
            return (STFontCollectionIndex)STFontCollectionIndex.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STFontCollectionIndex.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STFontCollectionIndex newInstance() {
            return (STFontCollectionIndex)getTypeLoader().newInstance(STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        public static STFontCollectionIndex newInstance(final XmlOptions xmlOptions) {
            return (STFontCollectionIndex)getTypeLoader().newInstance(STFontCollectionIndex.type, xmlOptions);
        }
        
        public static STFontCollectionIndex parse(final String s) throws XmlException {
            return (STFontCollectionIndex)getTypeLoader().parse(s, STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        public static STFontCollectionIndex parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STFontCollectionIndex)getTypeLoader().parse(s, STFontCollectionIndex.type, xmlOptions);
        }
        
        public static STFontCollectionIndex parse(final File file) throws XmlException, IOException {
            return (STFontCollectionIndex)getTypeLoader().parse(file, STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        public static STFontCollectionIndex parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontCollectionIndex)getTypeLoader().parse(file, STFontCollectionIndex.type, xmlOptions);
        }
        
        public static STFontCollectionIndex parse(final URL url) throws XmlException, IOException {
            return (STFontCollectionIndex)getTypeLoader().parse(url, STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        public static STFontCollectionIndex parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontCollectionIndex)getTypeLoader().parse(url, STFontCollectionIndex.type, xmlOptions);
        }
        
        public static STFontCollectionIndex parse(final InputStream inputStream) throws XmlException, IOException {
            return (STFontCollectionIndex)getTypeLoader().parse(inputStream, STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        public static STFontCollectionIndex parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontCollectionIndex)getTypeLoader().parse(inputStream, STFontCollectionIndex.type, xmlOptions);
        }
        
        public static STFontCollectionIndex parse(final Reader reader) throws XmlException, IOException {
            return (STFontCollectionIndex)getTypeLoader().parse(reader, STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        public static STFontCollectionIndex parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STFontCollectionIndex)getTypeLoader().parse(reader, STFontCollectionIndex.type, xmlOptions);
        }
        
        public static STFontCollectionIndex parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STFontCollectionIndex)getTypeLoader().parse(xmlStreamReader, STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        public static STFontCollectionIndex parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STFontCollectionIndex)getTypeLoader().parse(xmlStreamReader, STFontCollectionIndex.type, xmlOptions);
        }
        
        public static STFontCollectionIndex parse(final Node node) throws XmlException {
            return (STFontCollectionIndex)getTypeLoader().parse(node, STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        public static STFontCollectionIndex parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STFontCollectionIndex)getTypeLoader().parse(node, STFontCollectionIndex.type, xmlOptions);
        }
        
        @Deprecated
        public static STFontCollectionIndex parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STFontCollectionIndex)getTypeLoader().parse(xmlInputStream, STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STFontCollectionIndex parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STFontCollectionIndex)getTypeLoader().parse(xmlInputStream, STFontCollectionIndex.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFontCollectionIndex.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STFontCollectionIndex.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_MAJOR = 1;
        static final int INT_MINOR = 2;
        static final int INT_NONE = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("major", 1), new Enum("minor", 2), new Enum("none", 3) });
        }
    }
}
