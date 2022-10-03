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

public interface STTickMark extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTickMark.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttickmark69e2type");
    public static final Enum CROSS = Enum.forString("cross");
    public static final Enum IN = Enum.forString("in");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum OUT = Enum.forString("out");
    public static final int INT_CROSS = 1;
    public static final int INT_IN = 2;
    public static final int INT_NONE = 3;
    public static final int INT_OUT = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTickMark newValue(final Object o) {
            return (STTickMark)STTickMark.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTickMark.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTickMark newInstance() {
            return (STTickMark)getTypeLoader().newInstance(STTickMark.type, (XmlOptions)null);
        }
        
        public static STTickMark newInstance(final XmlOptions xmlOptions) {
            return (STTickMark)getTypeLoader().newInstance(STTickMark.type, xmlOptions);
        }
        
        public static STTickMark parse(final String s) throws XmlException {
            return (STTickMark)getTypeLoader().parse(s, STTickMark.type, (XmlOptions)null);
        }
        
        public static STTickMark parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTickMark)getTypeLoader().parse(s, STTickMark.type, xmlOptions);
        }
        
        public static STTickMark parse(final File file) throws XmlException, IOException {
            return (STTickMark)getTypeLoader().parse(file, STTickMark.type, (XmlOptions)null);
        }
        
        public static STTickMark parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTickMark)getTypeLoader().parse(file, STTickMark.type, xmlOptions);
        }
        
        public static STTickMark parse(final URL url) throws XmlException, IOException {
            return (STTickMark)getTypeLoader().parse(url, STTickMark.type, (XmlOptions)null);
        }
        
        public static STTickMark parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTickMark)getTypeLoader().parse(url, STTickMark.type, xmlOptions);
        }
        
        public static STTickMark parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTickMark)getTypeLoader().parse(inputStream, STTickMark.type, (XmlOptions)null);
        }
        
        public static STTickMark parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTickMark)getTypeLoader().parse(inputStream, STTickMark.type, xmlOptions);
        }
        
        public static STTickMark parse(final Reader reader) throws XmlException, IOException {
            return (STTickMark)getTypeLoader().parse(reader, STTickMark.type, (XmlOptions)null);
        }
        
        public static STTickMark parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTickMark)getTypeLoader().parse(reader, STTickMark.type, xmlOptions);
        }
        
        public static STTickMark parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTickMark)getTypeLoader().parse(xmlStreamReader, STTickMark.type, (XmlOptions)null);
        }
        
        public static STTickMark parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTickMark)getTypeLoader().parse(xmlStreamReader, STTickMark.type, xmlOptions);
        }
        
        public static STTickMark parse(final Node node) throws XmlException {
            return (STTickMark)getTypeLoader().parse(node, STTickMark.type, (XmlOptions)null);
        }
        
        public static STTickMark parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTickMark)getTypeLoader().parse(node, STTickMark.type, xmlOptions);
        }
        
        @Deprecated
        public static STTickMark parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTickMark)getTypeLoader().parse(xmlInputStream, STTickMark.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTickMark parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTickMark)getTypeLoader().parse(xmlInputStream, STTickMark.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTickMark.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTickMark.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_CROSS = 1;
        static final int INT_IN = 2;
        static final int INT_NONE = 3;
        static final int INT_OUT = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("cross", 1), new Enum("in", 2), new Enum("none", 3), new Enum("out", 4) });
        }
    }
}
