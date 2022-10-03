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

public interface STTickLblPos extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTickLblPos.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stticklblposc551type");
    public static final Enum HIGH = Enum.forString("high");
    public static final Enum LOW = Enum.forString("low");
    public static final Enum NEXT_TO = Enum.forString("nextTo");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_HIGH = 1;
    public static final int INT_LOW = 2;
    public static final int INT_NEXT_TO = 3;
    public static final int INT_NONE = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTickLblPos newValue(final Object o) {
            return (STTickLblPos)STTickLblPos.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTickLblPos.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTickLblPos newInstance() {
            return (STTickLblPos)getTypeLoader().newInstance(STTickLblPos.type, (XmlOptions)null);
        }
        
        public static STTickLblPos newInstance(final XmlOptions xmlOptions) {
            return (STTickLblPos)getTypeLoader().newInstance(STTickLblPos.type, xmlOptions);
        }
        
        public static STTickLblPos parse(final String s) throws XmlException {
            return (STTickLblPos)getTypeLoader().parse(s, STTickLblPos.type, (XmlOptions)null);
        }
        
        public static STTickLblPos parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTickLblPos)getTypeLoader().parse(s, STTickLblPos.type, xmlOptions);
        }
        
        public static STTickLblPos parse(final File file) throws XmlException, IOException {
            return (STTickLblPos)getTypeLoader().parse(file, STTickLblPos.type, (XmlOptions)null);
        }
        
        public static STTickLblPos parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTickLblPos)getTypeLoader().parse(file, STTickLblPos.type, xmlOptions);
        }
        
        public static STTickLblPos parse(final URL url) throws XmlException, IOException {
            return (STTickLblPos)getTypeLoader().parse(url, STTickLblPos.type, (XmlOptions)null);
        }
        
        public static STTickLblPos parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTickLblPos)getTypeLoader().parse(url, STTickLblPos.type, xmlOptions);
        }
        
        public static STTickLblPos parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTickLblPos)getTypeLoader().parse(inputStream, STTickLblPos.type, (XmlOptions)null);
        }
        
        public static STTickLblPos parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTickLblPos)getTypeLoader().parse(inputStream, STTickLblPos.type, xmlOptions);
        }
        
        public static STTickLblPos parse(final Reader reader) throws XmlException, IOException {
            return (STTickLblPos)getTypeLoader().parse(reader, STTickLblPos.type, (XmlOptions)null);
        }
        
        public static STTickLblPos parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTickLblPos)getTypeLoader().parse(reader, STTickLblPos.type, xmlOptions);
        }
        
        public static STTickLblPos parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTickLblPos)getTypeLoader().parse(xmlStreamReader, STTickLblPos.type, (XmlOptions)null);
        }
        
        public static STTickLblPos parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTickLblPos)getTypeLoader().parse(xmlStreamReader, STTickLblPos.type, xmlOptions);
        }
        
        public static STTickLblPos parse(final Node node) throws XmlException {
            return (STTickLblPos)getTypeLoader().parse(node, STTickLblPos.type, (XmlOptions)null);
        }
        
        public static STTickLblPos parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTickLblPos)getTypeLoader().parse(node, STTickLblPos.type, xmlOptions);
        }
        
        @Deprecated
        public static STTickLblPos parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTickLblPos)getTypeLoader().parse(xmlInputStream, STTickLblPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTickLblPos parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTickLblPos)getTypeLoader().parse(xmlInputStream, STTickLblPos.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTickLblPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTickLblPos.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_HIGH = 1;
        static final int INT_LOW = 2;
        static final int INT_NEXT_TO = 3;
        static final int INT_NONE = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("high", 1), new Enum("low", 2), new Enum("nextTo", 3), new Enum("none", 4) });
        }
    }
}
