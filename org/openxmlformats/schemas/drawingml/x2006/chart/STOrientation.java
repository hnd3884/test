package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface STOrientation extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STOrientation.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("storientationc326type");
    public static final Enum MAX_MIN = Enum.forString("maxMin");
    public static final Enum MIN_MAX = Enum.forString("minMax");
    public static final int INT_MAX_MIN = 1;
    public static final int INT_MIN_MAX = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STOrientation newValue(final Object o) {
            return (STOrientation)STOrientation.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STOrientation.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STOrientation newInstance() {
            return (STOrientation)getTypeLoader().newInstance(STOrientation.type, (XmlOptions)null);
        }
        
        public static STOrientation newInstance(final XmlOptions xmlOptions) {
            return (STOrientation)getTypeLoader().newInstance(STOrientation.type, xmlOptions);
        }
        
        public static STOrientation parse(final String s) throws XmlException {
            return (STOrientation)getTypeLoader().parse(s, STOrientation.type, (XmlOptions)null);
        }
        
        public static STOrientation parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STOrientation)getTypeLoader().parse(s, STOrientation.type, xmlOptions);
        }
        
        public static STOrientation parse(final File file) throws XmlException, IOException {
            return (STOrientation)getTypeLoader().parse(file, STOrientation.type, (XmlOptions)null);
        }
        
        public static STOrientation parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOrientation)getTypeLoader().parse(file, STOrientation.type, xmlOptions);
        }
        
        public static STOrientation parse(final URL url) throws XmlException, IOException {
            return (STOrientation)getTypeLoader().parse(url, STOrientation.type, (XmlOptions)null);
        }
        
        public static STOrientation parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOrientation)getTypeLoader().parse(url, STOrientation.type, xmlOptions);
        }
        
        public static STOrientation parse(final InputStream inputStream) throws XmlException, IOException {
            return (STOrientation)getTypeLoader().parse(inputStream, STOrientation.type, (XmlOptions)null);
        }
        
        public static STOrientation parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOrientation)getTypeLoader().parse(inputStream, STOrientation.type, xmlOptions);
        }
        
        public static STOrientation parse(final Reader reader) throws XmlException, IOException {
            return (STOrientation)getTypeLoader().parse(reader, STOrientation.type, (XmlOptions)null);
        }
        
        public static STOrientation parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STOrientation)getTypeLoader().parse(reader, STOrientation.type, xmlOptions);
        }
        
        public static STOrientation parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STOrientation)getTypeLoader().parse(xmlStreamReader, STOrientation.type, (XmlOptions)null);
        }
        
        public static STOrientation parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STOrientation)getTypeLoader().parse(xmlStreamReader, STOrientation.type, xmlOptions);
        }
        
        public static STOrientation parse(final Node node) throws XmlException {
            return (STOrientation)getTypeLoader().parse(node, STOrientation.type, (XmlOptions)null);
        }
        
        public static STOrientation parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STOrientation)getTypeLoader().parse(node, STOrientation.type, xmlOptions);
        }
        
        @Deprecated
        public static STOrientation parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STOrientation)getTypeLoader().parse(xmlInputStream, STOrientation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STOrientation parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STOrientation)getTypeLoader().parse(xmlInputStream, STOrientation.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STOrientation.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STOrientation.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_MAX_MIN = 1;
        static final int INT_MIN_MAX = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("maxMin", 1), new Enum("minMax", 2) });
        }
    }
}
