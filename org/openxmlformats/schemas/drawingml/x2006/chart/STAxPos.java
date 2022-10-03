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

public interface STAxPos extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STAxPos.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("staxpos4379type");
    public static final Enum B = Enum.forString("b");
    public static final Enum L = Enum.forString("l");
    public static final Enum R = Enum.forString("r");
    public static final Enum T = Enum.forString("t");
    public static final int INT_B = 1;
    public static final int INT_L = 2;
    public static final int INT_R = 3;
    public static final int INT_T = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STAxPos newValue(final Object o) {
            return (STAxPos)STAxPos.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STAxPos.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STAxPos newInstance() {
            return (STAxPos)getTypeLoader().newInstance(STAxPos.type, (XmlOptions)null);
        }
        
        public static STAxPos newInstance(final XmlOptions xmlOptions) {
            return (STAxPos)getTypeLoader().newInstance(STAxPos.type, xmlOptions);
        }
        
        public static STAxPos parse(final String s) throws XmlException {
            return (STAxPos)getTypeLoader().parse(s, STAxPos.type, (XmlOptions)null);
        }
        
        public static STAxPos parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STAxPos)getTypeLoader().parse(s, STAxPos.type, xmlOptions);
        }
        
        public static STAxPos parse(final File file) throws XmlException, IOException {
            return (STAxPos)getTypeLoader().parse(file, STAxPos.type, (XmlOptions)null);
        }
        
        public static STAxPos parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAxPos)getTypeLoader().parse(file, STAxPos.type, xmlOptions);
        }
        
        public static STAxPos parse(final URL url) throws XmlException, IOException {
            return (STAxPos)getTypeLoader().parse(url, STAxPos.type, (XmlOptions)null);
        }
        
        public static STAxPos parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAxPos)getTypeLoader().parse(url, STAxPos.type, xmlOptions);
        }
        
        public static STAxPos parse(final InputStream inputStream) throws XmlException, IOException {
            return (STAxPos)getTypeLoader().parse(inputStream, STAxPos.type, (XmlOptions)null);
        }
        
        public static STAxPos parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAxPos)getTypeLoader().parse(inputStream, STAxPos.type, xmlOptions);
        }
        
        public static STAxPos parse(final Reader reader) throws XmlException, IOException {
            return (STAxPos)getTypeLoader().parse(reader, STAxPos.type, (XmlOptions)null);
        }
        
        public static STAxPos parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STAxPos)getTypeLoader().parse(reader, STAxPos.type, xmlOptions);
        }
        
        public static STAxPos parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STAxPos)getTypeLoader().parse(xmlStreamReader, STAxPos.type, (XmlOptions)null);
        }
        
        public static STAxPos parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STAxPos)getTypeLoader().parse(xmlStreamReader, STAxPos.type, xmlOptions);
        }
        
        public static STAxPos parse(final Node node) throws XmlException {
            return (STAxPos)getTypeLoader().parse(node, STAxPos.type, (XmlOptions)null);
        }
        
        public static STAxPos parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STAxPos)getTypeLoader().parse(node, STAxPos.type, xmlOptions);
        }
        
        @Deprecated
        public static STAxPos parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STAxPos)getTypeLoader().parse(xmlInputStream, STAxPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STAxPos parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STAxPos)getTypeLoader().parse(xmlInputStream, STAxPos.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAxPos.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STAxPos.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_B = 1;
        static final int INT_L = 2;
        static final int INT_R = 3;
        static final int INT_T = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("b", 1), new Enum("l", 2), new Enum("r", 3), new Enum("t", 4) });
        }
    }
}
