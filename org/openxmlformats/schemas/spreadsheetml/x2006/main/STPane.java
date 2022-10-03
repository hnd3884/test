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

public interface STPane extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPane.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpane2ac1type");
    public static final Enum BOTTOM_RIGHT = Enum.forString("bottomRight");
    public static final Enum TOP_RIGHT = Enum.forString("topRight");
    public static final Enum BOTTOM_LEFT = Enum.forString("bottomLeft");
    public static final Enum TOP_LEFT = Enum.forString("topLeft");
    public static final int INT_BOTTOM_RIGHT = 1;
    public static final int INT_TOP_RIGHT = 2;
    public static final int INT_BOTTOM_LEFT = 3;
    public static final int INT_TOP_LEFT = 4;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPane newValue(final Object o) {
            return (STPane)STPane.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPane.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPane newInstance() {
            return (STPane)getTypeLoader().newInstance(STPane.type, (XmlOptions)null);
        }
        
        public static STPane newInstance(final XmlOptions xmlOptions) {
            return (STPane)getTypeLoader().newInstance(STPane.type, xmlOptions);
        }
        
        public static STPane parse(final String s) throws XmlException {
            return (STPane)getTypeLoader().parse(s, STPane.type, (XmlOptions)null);
        }
        
        public static STPane parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPane)getTypeLoader().parse(s, STPane.type, xmlOptions);
        }
        
        public static STPane parse(final File file) throws XmlException, IOException {
            return (STPane)getTypeLoader().parse(file, STPane.type, (XmlOptions)null);
        }
        
        public static STPane parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPane)getTypeLoader().parse(file, STPane.type, xmlOptions);
        }
        
        public static STPane parse(final URL url) throws XmlException, IOException {
            return (STPane)getTypeLoader().parse(url, STPane.type, (XmlOptions)null);
        }
        
        public static STPane parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPane)getTypeLoader().parse(url, STPane.type, xmlOptions);
        }
        
        public static STPane parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPane)getTypeLoader().parse(inputStream, STPane.type, (XmlOptions)null);
        }
        
        public static STPane parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPane)getTypeLoader().parse(inputStream, STPane.type, xmlOptions);
        }
        
        public static STPane parse(final Reader reader) throws XmlException, IOException {
            return (STPane)getTypeLoader().parse(reader, STPane.type, (XmlOptions)null);
        }
        
        public static STPane parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPane)getTypeLoader().parse(reader, STPane.type, xmlOptions);
        }
        
        public static STPane parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPane)getTypeLoader().parse(xmlStreamReader, STPane.type, (XmlOptions)null);
        }
        
        public static STPane parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPane)getTypeLoader().parse(xmlStreamReader, STPane.type, xmlOptions);
        }
        
        public static STPane parse(final Node node) throws XmlException {
            return (STPane)getTypeLoader().parse(node, STPane.type, (XmlOptions)null);
        }
        
        public static STPane parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPane)getTypeLoader().parse(node, STPane.type, xmlOptions);
        }
        
        @Deprecated
        public static STPane parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPane)getTypeLoader().parse(xmlInputStream, STPane.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPane parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPane)getTypeLoader().parse(xmlInputStream, STPane.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPane.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPane.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_BOTTOM_RIGHT = 1;
        static final int INT_TOP_RIGHT = 2;
        static final int INT_BOTTOM_LEFT = 3;
        static final int INT_TOP_LEFT = 4;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("bottomRight", 1), new Enum("topRight", 2), new Enum("bottomLeft", 3), new Enum("topLeft", 4) });
        }
    }
}
