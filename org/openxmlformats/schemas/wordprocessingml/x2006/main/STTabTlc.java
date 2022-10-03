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

public interface STTabTlc extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTabTlc.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttabtlc6f42type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum DOT = Enum.forString("dot");
    public static final Enum HYPHEN = Enum.forString("hyphen");
    public static final Enum UNDERSCORE = Enum.forString("underscore");
    public static final Enum HEAVY = Enum.forString("heavy");
    public static final Enum MIDDLE_DOT = Enum.forString("middleDot");
    public static final int INT_NONE = 1;
    public static final int INT_DOT = 2;
    public static final int INT_HYPHEN = 3;
    public static final int INT_UNDERSCORE = 4;
    public static final int INT_HEAVY = 5;
    public static final int INT_MIDDLE_DOT = 6;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTabTlc newValue(final Object o) {
            return (STTabTlc)STTabTlc.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTabTlc.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTabTlc newInstance() {
            return (STTabTlc)getTypeLoader().newInstance(STTabTlc.type, (XmlOptions)null);
        }
        
        public static STTabTlc newInstance(final XmlOptions xmlOptions) {
            return (STTabTlc)getTypeLoader().newInstance(STTabTlc.type, xmlOptions);
        }
        
        public static STTabTlc parse(final String s) throws XmlException {
            return (STTabTlc)getTypeLoader().parse(s, STTabTlc.type, (XmlOptions)null);
        }
        
        public static STTabTlc parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTabTlc)getTypeLoader().parse(s, STTabTlc.type, xmlOptions);
        }
        
        public static STTabTlc parse(final File file) throws XmlException, IOException {
            return (STTabTlc)getTypeLoader().parse(file, STTabTlc.type, (XmlOptions)null);
        }
        
        public static STTabTlc parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTabTlc)getTypeLoader().parse(file, STTabTlc.type, xmlOptions);
        }
        
        public static STTabTlc parse(final URL url) throws XmlException, IOException {
            return (STTabTlc)getTypeLoader().parse(url, STTabTlc.type, (XmlOptions)null);
        }
        
        public static STTabTlc parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTabTlc)getTypeLoader().parse(url, STTabTlc.type, xmlOptions);
        }
        
        public static STTabTlc parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTabTlc)getTypeLoader().parse(inputStream, STTabTlc.type, (XmlOptions)null);
        }
        
        public static STTabTlc parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTabTlc)getTypeLoader().parse(inputStream, STTabTlc.type, xmlOptions);
        }
        
        public static STTabTlc parse(final Reader reader) throws XmlException, IOException {
            return (STTabTlc)getTypeLoader().parse(reader, STTabTlc.type, (XmlOptions)null);
        }
        
        public static STTabTlc parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTabTlc)getTypeLoader().parse(reader, STTabTlc.type, xmlOptions);
        }
        
        public static STTabTlc parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTabTlc)getTypeLoader().parse(xmlStreamReader, STTabTlc.type, (XmlOptions)null);
        }
        
        public static STTabTlc parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTabTlc)getTypeLoader().parse(xmlStreamReader, STTabTlc.type, xmlOptions);
        }
        
        public static STTabTlc parse(final Node node) throws XmlException {
            return (STTabTlc)getTypeLoader().parse(node, STTabTlc.type, (XmlOptions)null);
        }
        
        public static STTabTlc parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTabTlc)getTypeLoader().parse(node, STTabTlc.type, xmlOptions);
        }
        
        @Deprecated
        public static STTabTlc parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTabTlc)getTypeLoader().parse(xmlInputStream, STTabTlc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTabTlc parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTabTlc)getTypeLoader().parse(xmlInputStream, STTabTlc.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTabTlc.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTabTlc.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_DOT = 2;
        static final int INT_HYPHEN = 3;
        static final int INT_UNDERSCORE = 4;
        static final int INT_HEAVY = 5;
        static final int INT_MIDDLE_DOT = 6;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("dot", 2), new Enum("hyphen", 3), new Enum("underscore", 4), new Enum("heavy", 5), new Enum("middleDot", 6) });
        }
    }
}
