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

public interface STEm extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STEm.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stem5e70type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum DOT = Enum.forString("dot");
    public static final Enum COMMA = Enum.forString("comma");
    public static final Enum CIRCLE = Enum.forString("circle");
    public static final Enum UNDER_DOT = Enum.forString("underDot");
    public static final int INT_NONE = 1;
    public static final int INT_DOT = 2;
    public static final int INT_COMMA = 3;
    public static final int INT_CIRCLE = 4;
    public static final int INT_UNDER_DOT = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STEm newValue(final Object o) {
            return (STEm)STEm.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STEm.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STEm newInstance() {
            return (STEm)getTypeLoader().newInstance(STEm.type, (XmlOptions)null);
        }
        
        public static STEm newInstance(final XmlOptions xmlOptions) {
            return (STEm)getTypeLoader().newInstance(STEm.type, xmlOptions);
        }
        
        public static STEm parse(final String s) throws XmlException {
            return (STEm)getTypeLoader().parse(s, STEm.type, (XmlOptions)null);
        }
        
        public static STEm parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STEm)getTypeLoader().parse(s, STEm.type, xmlOptions);
        }
        
        public static STEm parse(final File file) throws XmlException, IOException {
            return (STEm)getTypeLoader().parse(file, STEm.type, (XmlOptions)null);
        }
        
        public static STEm parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEm)getTypeLoader().parse(file, STEm.type, xmlOptions);
        }
        
        public static STEm parse(final URL url) throws XmlException, IOException {
            return (STEm)getTypeLoader().parse(url, STEm.type, (XmlOptions)null);
        }
        
        public static STEm parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEm)getTypeLoader().parse(url, STEm.type, xmlOptions);
        }
        
        public static STEm parse(final InputStream inputStream) throws XmlException, IOException {
            return (STEm)getTypeLoader().parse(inputStream, STEm.type, (XmlOptions)null);
        }
        
        public static STEm parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEm)getTypeLoader().parse(inputStream, STEm.type, xmlOptions);
        }
        
        public static STEm parse(final Reader reader) throws XmlException, IOException {
            return (STEm)getTypeLoader().parse(reader, STEm.type, (XmlOptions)null);
        }
        
        public static STEm parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STEm)getTypeLoader().parse(reader, STEm.type, xmlOptions);
        }
        
        public static STEm parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STEm)getTypeLoader().parse(xmlStreamReader, STEm.type, (XmlOptions)null);
        }
        
        public static STEm parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STEm)getTypeLoader().parse(xmlStreamReader, STEm.type, xmlOptions);
        }
        
        public static STEm parse(final Node node) throws XmlException {
            return (STEm)getTypeLoader().parse(node, STEm.type, (XmlOptions)null);
        }
        
        public static STEm parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STEm)getTypeLoader().parse(node, STEm.type, xmlOptions);
        }
        
        @Deprecated
        public static STEm parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STEm)getTypeLoader().parse(xmlInputStream, STEm.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STEm parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STEm)getTypeLoader().parse(xmlInputStream, STEm.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STEm.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STEm.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_DOT = 2;
        static final int INT_COMMA = 3;
        static final int INT_CIRCLE = 4;
        static final int INT_UNDER_DOT = 5;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("dot", 2), new Enum("comma", 3), new Enum("circle", 4), new Enum("underDot", 5) });
        }
    }
}
