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

public interface STHdrFtr extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHdrFtr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sthdrftr30catype");
    public static final Enum EVEN = Enum.forString("even");
    public static final Enum DEFAULT = Enum.forString("default");
    public static final Enum FIRST = Enum.forString("first");
    public static final int INT_EVEN = 1;
    public static final int INT_DEFAULT = 2;
    public static final int INT_FIRST = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHdrFtr newValue(final Object o) {
            return (STHdrFtr)STHdrFtr.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHdrFtr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHdrFtr newInstance() {
            return (STHdrFtr)getTypeLoader().newInstance(STHdrFtr.type, (XmlOptions)null);
        }
        
        public static STHdrFtr newInstance(final XmlOptions xmlOptions) {
            return (STHdrFtr)getTypeLoader().newInstance(STHdrFtr.type, xmlOptions);
        }
        
        public static STHdrFtr parse(final String s) throws XmlException {
            return (STHdrFtr)getTypeLoader().parse(s, STHdrFtr.type, (XmlOptions)null);
        }
        
        public static STHdrFtr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHdrFtr)getTypeLoader().parse(s, STHdrFtr.type, xmlOptions);
        }
        
        public static STHdrFtr parse(final File file) throws XmlException, IOException {
            return (STHdrFtr)getTypeLoader().parse(file, STHdrFtr.type, (XmlOptions)null);
        }
        
        public static STHdrFtr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHdrFtr)getTypeLoader().parse(file, STHdrFtr.type, xmlOptions);
        }
        
        public static STHdrFtr parse(final URL url) throws XmlException, IOException {
            return (STHdrFtr)getTypeLoader().parse(url, STHdrFtr.type, (XmlOptions)null);
        }
        
        public static STHdrFtr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHdrFtr)getTypeLoader().parse(url, STHdrFtr.type, xmlOptions);
        }
        
        public static STHdrFtr parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHdrFtr)getTypeLoader().parse(inputStream, STHdrFtr.type, (XmlOptions)null);
        }
        
        public static STHdrFtr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHdrFtr)getTypeLoader().parse(inputStream, STHdrFtr.type, xmlOptions);
        }
        
        public static STHdrFtr parse(final Reader reader) throws XmlException, IOException {
            return (STHdrFtr)getTypeLoader().parse(reader, STHdrFtr.type, (XmlOptions)null);
        }
        
        public static STHdrFtr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHdrFtr)getTypeLoader().parse(reader, STHdrFtr.type, xmlOptions);
        }
        
        public static STHdrFtr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHdrFtr)getTypeLoader().parse(xmlStreamReader, STHdrFtr.type, (XmlOptions)null);
        }
        
        public static STHdrFtr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHdrFtr)getTypeLoader().parse(xmlStreamReader, STHdrFtr.type, xmlOptions);
        }
        
        public static STHdrFtr parse(final Node node) throws XmlException {
            return (STHdrFtr)getTypeLoader().parse(node, STHdrFtr.type, (XmlOptions)null);
        }
        
        public static STHdrFtr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHdrFtr)getTypeLoader().parse(node, STHdrFtr.type, xmlOptions);
        }
        
        @Deprecated
        public static STHdrFtr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHdrFtr)getTypeLoader().parse(xmlInputStream, STHdrFtr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHdrFtr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHdrFtr)getTypeLoader().parse(xmlInputStream, STHdrFtr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHdrFtr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHdrFtr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_EVEN = 1;
        static final int INT_DEFAULT = 2;
        static final int INT_FIRST = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("even", 1), new Enum("default", 2), new Enum("first", 3) });
        }
    }
}
