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

public interface STLayoutTarget extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLayoutTarget.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlayouttarget19f1type");
    public static final Enum INNER = Enum.forString("inner");
    public static final Enum OUTER = Enum.forString("outer");
    public static final int INT_INNER = 1;
    public static final int INT_OUTER = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STLayoutTarget newValue(final Object o) {
            return (STLayoutTarget)STLayoutTarget.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLayoutTarget.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLayoutTarget newInstance() {
            return (STLayoutTarget)getTypeLoader().newInstance(STLayoutTarget.type, (XmlOptions)null);
        }
        
        public static STLayoutTarget newInstance(final XmlOptions xmlOptions) {
            return (STLayoutTarget)getTypeLoader().newInstance(STLayoutTarget.type, xmlOptions);
        }
        
        public static STLayoutTarget parse(final String s) throws XmlException {
            return (STLayoutTarget)getTypeLoader().parse(s, STLayoutTarget.type, (XmlOptions)null);
        }
        
        public static STLayoutTarget parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLayoutTarget)getTypeLoader().parse(s, STLayoutTarget.type, xmlOptions);
        }
        
        public static STLayoutTarget parse(final File file) throws XmlException, IOException {
            return (STLayoutTarget)getTypeLoader().parse(file, STLayoutTarget.type, (XmlOptions)null);
        }
        
        public static STLayoutTarget parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLayoutTarget)getTypeLoader().parse(file, STLayoutTarget.type, xmlOptions);
        }
        
        public static STLayoutTarget parse(final URL url) throws XmlException, IOException {
            return (STLayoutTarget)getTypeLoader().parse(url, STLayoutTarget.type, (XmlOptions)null);
        }
        
        public static STLayoutTarget parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLayoutTarget)getTypeLoader().parse(url, STLayoutTarget.type, xmlOptions);
        }
        
        public static STLayoutTarget parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLayoutTarget)getTypeLoader().parse(inputStream, STLayoutTarget.type, (XmlOptions)null);
        }
        
        public static STLayoutTarget parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLayoutTarget)getTypeLoader().parse(inputStream, STLayoutTarget.type, xmlOptions);
        }
        
        public static STLayoutTarget parse(final Reader reader) throws XmlException, IOException {
            return (STLayoutTarget)getTypeLoader().parse(reader, STLayoutTarget.type, (XmlOptions)null);
        }
        
        public static STLayoutTarget parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLayoutTarget)getTypeLoader().parse(reader, STLayoutTarget.type, xmlOptions);
        }
        
        public static STLayoutTarget parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLayoutTarget)getTypeLoader().parse(xmlStreamReader, STLayoutTarget.type, (XmlOptions)null);
        }
        
        public static STLayoutTarget parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLayoutTarget)getTypeLoader().parse(xmlStreamReader, STLayoutTarget.type, xmlOptions);
        }
        
        public static STLayoutTarget parse(final Node node) throws XmlException {
            return (STLayoutTarget)getTypeLoader().parse(node, STLayoutTarget.type, (XmlOptions)null);
        }
        
        public static STLayoutTarget parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLayoutTarget)getTypeLoader().parse(node, STLayoutTarget.type, xmlOptions);
        }
        
        @Deprecated
        public static STLayoutTarget parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLayoutTarget)getTypeLoader().parse(xmlInputStream, STLayoutTarget.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLayoutTarget parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLayoutTarget)getTypeLoader().parse(xmlInputStream, STLayoutTarget.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLayoutTarget.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLayoutTarget.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_INNER = 1;
        static final int INT_OUTER = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("inner", 1), new Enum("outer", 2) });
        }
    }
}
