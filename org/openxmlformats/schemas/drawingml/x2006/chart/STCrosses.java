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

public interface STCrosses extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STCrosses.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stcrosses3cc8type");
    public static final Enum AUTO_ZERO = Enum.forString("autoZero");
    public static final Enum MAX = Enum.forString("max");
    public static final Enum MIN = Enum.forString("min");
    public static final int INT_AUTO_ZERO = 1;
    public static final int INT_MAX = 2;
    public static final int INT_MIN = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STCrosses newValue(final Object o) {
            return (STCrosses)STCrosses.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STCrosses.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STCrosses newInstance() {
            return (STCrosses)getTypeLoader().newInstance(STCrosses.type, (XmlOptions)null);
        }
        
        public static STCrosses newInstance(final XmlOptions xmlOptions) {
            return (STCrosses)getTypeLoader().newInstance(STCrosses.type, xmlOptions);
        }
        
        public static STCrosses parse(final String s) throws XmlException {
            return (STCrosses)getTypeLoader().parse(s, STCrosses.type, (XmlOptions)null);
        }
        
        public static STCrosses parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STCrosses)getTypeLoader().parse(s, STCrosses.type, xmlOptions);
        }
        
        public static STCrosses parse(final File file) throws XmlException, IOException {
            return (STCrosses)getTypeLoader().parse(file, STCrosses.type, (XmlOptions)null);
        }
        
        public static STCrosses parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCrosses)getTypeLoader().parse(file, STCrosses.type, xmlOptions);
        }
        
        public static STCrosses parse(final URL url) throws XmlException, IOException {
            return (STCrosses)getTypeLoader().parse(url, STCrosses.type, (XmlOptions)null);
        }
        
        public static STCrosses parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCrosses)getTypeLoader().parse(url, STCrosses.type, xmlOptions);
        }
        
        public static STCrosses parse(final InputStream inputStream) throws XmlException, IOException {
            return (STCrosses)getTypeLoader().parse(inputStream, STCrosses.type, (XmlOptions)null);
        }
        
        public static STCrosses parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCrosses)getTypeLoader().parse(inputStream, STCrosses.type, xmlOptions);
        }
        
        public static STCrosses parse(final Reader reader) throws XmlException, IOException {
            return (STCrosses)getTypeLoader().parse(reader, STCrosses.type, (XmlOptions)null);
        }
        
        public static STCrosses parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STCrosses)getTypeLoader().parse(reader, STCrosses.type, xmlOptions);
        }
        
        public static STCrosses parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STCrosses)getTypeLoader().parse(xmlStreamReader, STCrosses.type, (XmlOptions)null);
        }
        
        public static STCrosses parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STCrosses)getTypeLoader().parse(xmlStreamReader, STCrosses.type, xmlOptions);
        }
        
        public static STCrosses parse(final Node node) throws XmlException {
            return (STCrosses)getTypeLoader().parse(node, STCrosses.type, (XmlOptions)null);
        }
        
        public static STCrosses parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STCrosses)getTypeLoader().parse(node, STCrosses.type, xmlOptions);
        }
        
        @Deprecated
        public static STCrosses parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STCrosses)getTypeLoader().parse(xmlInputStream, STCrosses.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STCrosses parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STCrosses)getTypeLoader().parse(xmlInputStream, STCrosses.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCrosses.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STCrosses.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_AUTO_ZERO = 1;
        static final int INT_MAX = 2;
        static final int INT_MIN = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("autoZero", 1), new Enum("max", 2), new Enum("min", 3) });
        }
    }
}
