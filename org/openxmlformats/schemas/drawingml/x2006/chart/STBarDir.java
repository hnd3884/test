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

public interface STBarDir extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STBarDir.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stbardir9d32type");
    public static final Enum BAR = Enum.forString("bar");
    public static final Enum COL = Enum.forString("col");
    public static final int INT_BAR = 1;
    public static final int INT_COL = 2;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STBarDir newValue(final Object o) {
            return (STBarDir)STBarDir.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STBarDir.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STBarDir newInstance() {
            return (STBarDir)getTypeLoader().newInstance(STBarDir.type, (XmlOptions)null);
        }
        
        public static STBarDir newInstance(final XmlOptions xmlOptions) {
            return (STBarDir)getTypeLoader().newInstance(STBarDir.type, xmlOptions);
        }
        
        public static STBarDir parse(final String s) throws XmlException {
            return (STBarDir)getTypeLoader().parse(s, STBarDir.type, (XmlOptions)null);
        }
        
        public static STBarDir parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STBarDir)getTypeLoader().parse(s, STBarDir.type, xmlOptions);
        }
        
        public static STBarDir parse(final File file) throws XmlException, IOException {
            return (STBarDir)getTypeLoader().parse(file, STBarDir.type, (XmlOptions)null);
        }
        
        public static STBarDir parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBarDir)getTypeLoader().parse(file, STBarDir.type, xmlOptions);
        }
        
        public static STBarDir parse(final URL url) throws XmlException, IOException {
            return (STBarDir)getTypeLoader().parse(url, STBarDir.type, (XmlOptions)null);
        }
        
        public static STBarDir parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBarDir)getTypeLoader().parse(url, STBarDir.type, xmlOptions);
        }
        
        public static STBarDir parse(final InputStream inputStream) throws XmlException, IOException {
            return (STBarDir)getTypeLoader().parse(inputStream, STBarDir.type, (XmlOptions)null);
        }
        
        public static STBarDir parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBarDir)getTypeLoader().parse(inputStream, STBarDir.type, xmlOptions);
        }
        
        public static STBarDir parse(final Reader reader) throws XmlException, IOException {
            return (STBarDir)getTypeLoader().parse(reader, STBarDir.type, (XmlOptions)null);
        }
        
        public static STBarDir parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBarDir)getTypeLoader().parse(reader, STBarDir.type, xmlOptions);
        }
        
        public static STBarDir parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STBarDir)getTypeLoader().parse(xmlStreamReader, STBarDir.type, (XmlOptions)null);
        }
        
        public static STBarDir parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STBarDir)getTypeLoader().parse(xmlStreamReader, STBarDir.type, xmlOptions);
        }
        
        public static STBarDir parse(final Node node) throws XmlException {
            return (STBarDir)getTypeLoader().parse(node, STBarDir.type, (XmlOptions)null);
        }
        
        public static STBarDir parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STBarDir)getTypeLoader().parse(node, STBarDir.type, xmlOptions);
        }
        
        @Deprecated
        public static STBarDir parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STBarDir)getTypeLoader().parse(xmlInputStream, STBarDir.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STBarDir parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STBarDir)getTypeLoader().parse(xmlInputStream, STBarDir.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBarDir.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBarDir.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_BAR = 1;
        static final int INT_COL = 2;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("bar", 1), new Enum("col", 2) });
        }
    }
}
