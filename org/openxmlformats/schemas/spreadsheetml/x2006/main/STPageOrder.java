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

public interface STPageOrder extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPageOrder.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpageorderd2cetype");
    public static final Enum DOWN_THEN_OVER = Enum.forString("downThenOver");
    public static final Enum OVER_THEN_DOWN = Enum.forString("overThenDown");
    public static final int INT_DOWN_THEN_OVER = 1;
    public static final int INT_OVER_THEN_DOWN = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPageOrder newValue(final Object o) {
            return (STPageOrder)STPageOrder.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPageOrder.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPageOrder newInstance() {
            return (STPageOrder)getTypeLoader().newInstance(STPageOrder.type, (XmlOptions)null);
        }
        
        public static STPageOrder newInstance(final XmlOptions xmlOptions) {
            return (STPageOrder)getTypeLoader().newInstance(STPageOrder.type, xmlOptions);
        }
        
        public static STPageOrder parse(final String s) throws XmlException {
            return (STPageOrder)getTypeLoader().parse(s, STPageOrder.type, (XmlOptions)null);
        }
        
        public static STPageOrder parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPageOrder)getTypeLoader().parse(s, STPageOrder.type, xmlOptions);
        }
        
        public static STPageOrder parse(final File file) throws XmlException, IOException {
            return (STPageOrder)getTypeLoader().parse(file, STPageOrder.type, (XmlOptions)null);
        }
        
        public static STPageOrder parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPageOrder)getTypeLoader().parse(file, STPageOrder.type, xmlOptions);
        }
        
        public static STPageOrder parse(final URL url) throws XmlException, IOException {
            return (STPageOrder)getTypeLoader().parse(url, STPageOrder.type, (XmlOptions)null);
        }
        
        public static STPageOrder parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPageOrder)getTypeLoader().parse(url, STPageOrder.type, xmlOptions);
        }
        
        public static STPageOrder parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPageOrder)getTypeLoader().parse(inputStream, STPageOrder.type, (XmlOptions)null);
        }
        
        public static STPageOrder parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPageOrder)getTypeLoader().parse(inputStream, STPageOrder.type, xmlOptions);
        }
        
        public static STPageOrder parse(final Reader reader) throws XmlException, IOException {
            return (STPageOrder)getTypeLoader().parse(reader, STPageOrder.type, (XmlOptions)null);
        }
        
        public static STPageOrder parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPageOrder)getTypeLoader().parse(reader, STPageOrder.type, xmlOptions);
        }
        
        public static STPageOrder parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPageOrder)getTypeLoader().parse(xmlStreamReader, STPageOrder.type, (XmlOptions)null);
        }
        
        public static STPageOrder parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPageOrder)getTypeLoader().parse(xmlStreamReader, STPageOrder.type, xmlOptions);
        }
        
        public static STPageOrder parse(final Node node) throws XmlException {
            return (STPageOrder)getTypeLoader().parse(node, STPageOrder.type, (XmlOptions)null);
        }
        
        public static STPageOrder parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPageOrder)getTypeLoader().parse(node, STPageOrder.type, xmlOptions);
        }
        
        @Deprecated
        public static STPageOrder parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPageOrder)getTypeLoader().parse(xmlInputStream, STPageOrder.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPageOrder parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPageOrder)getTypeLoader().parse(xmlInputStream, STPageOrder.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPageOrder.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPageOrder.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_DOWN_THEN_OVER = 1;
        static final int INT_OVER_THEN_DOWN = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("downThenOver", 1), new Enum("overThenDown", 2) });
        }
    }
}
