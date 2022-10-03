package org.openxmlformats.schemas.drawingml.x2006.main;

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
import org.apache.xmlbeans.XmlToken;

public interface STLineCap extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLineCap.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlinecapcddftype");
    public static final Enum RND = Enum.forString("rnd");
    public static final Enum SQ = Enum.forString("sq");
    public static final Enum FLAT = Enum.forString("flat");
    public static final int INT_RND = 1;
    public static final int INT_SQ = 2;
    public static final int INT_FLAT = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STLineCap newValue(final Object o) {
            return (STLineCap)STLineCap.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLineCap.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLineCap newInstance() {
            return (STLineCap)getTypeLoader().newInstance(STLineCap.type, (XmlOptions)null);
        }
        
        public static STLineCap newInstance(final XmlOptions xmlOptions) {
            return (STLineCap)getTypeLoader().newInstance(STLineCap.type, xmlOptions);
        }
        
        public static STLineCap parse(final String s) throws XmlException {
            return (STLineCap)getTypeLoader().parse(s, STLineCap.type, (XmlOptions)null);
        }
        
        public static STLineCap parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLineCap)getTypeLoader().parse(s, STLineCap.type, xmlOptions);
        }
        
        public static STLineCap parse(final File file) throws XmlException, IOException {
            return (STLineCap)getTypeLoader().parse(file, STLineCap.type, (XmlOptions)null);
        }
        
        public static STLineCap parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineCap)getTypeLoader().parse(file, STLineCap.type, xmlOptions);
        }
        
        public static STLineCap parse(final URL url) throws XmlException, IOException {
            return (STLineCap)getTypeLoader().parse(url, STLineCap.type, (XmlOptions)null);
        }
        
        public static STLineCap parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineCap)getTypeLoader().parse(url, STLineCap.type, xmlOptions);
        }
        
        public static STLineCap parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLineCap)getTypeLoader().parse(inputStream, STLineCap.type, (XmlOptions)null);
        }
        
        public static STLineCap parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineCap)getTypeLoader().parse(inputStream, STLineCap.type, xmlOptions);
        }
        
        public static STLineCap parse(final Reader reader) throws XmlException, IOException {
            return (STLineCap)getTypeLoader().parse(reader, STLineCap.type, (XmlOptions)null);
        }
        
        public static STLineCap parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineCap)getTypeLoader().parse(reader, STLineCap.type, xmlOptions);
        }
        
        public static STLineCap parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLineCap)getTypeLoader().parse(xmlStreamReader, STLineCap.type, (XmlOptions)null);
        }
        
        public static STLineCap parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLineCap)getTypeLoader().parse(xmlStreamReader, STLineCap.type, xmlOptions);
        }
        
        public static STLineCap parse(final Node node) throws XmlException {
            return (STLineCap)getTypeLoader().parse(node, STLineCap.type, (XmlOptions)null);
        }
        
        public static STLineCap parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLineCap)getTypeLoader().parse(node, STLineCap.type, xmlOptions);
        }
        
        @Deprecated
        public static STLineCap parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLineCap)getTypeLoader().parse(xmlInputStream, STLineCap.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLineCap parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLineCap)getTypeLoader().parse(xmlInputStream, STLineCap.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineCap.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineCap.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_RND = 1;
        static final int INT_SQ = 2;
        static final int INT_FLAT = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("rnd", 1), new Enum("sq", 2), new Enum("flat", 3) });
        }
    }
}
