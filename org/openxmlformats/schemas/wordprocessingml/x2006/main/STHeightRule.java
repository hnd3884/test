package org.openxmlformats.schemas.wordprocessingml.x2006.main;

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

public interface STHeightRule extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHeightRule.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stheightrulea535type");
    public static final Enum AUTO = Enum.forString("auto");
    public static final Enum EXACT = Enum.forString("exact");
    public static final Enum AT_LEAST = Enum.forString("atLeast");
    public static final int INT_AUTO = 1;
    public static final int INT_EXACT = 2;
    public static final int INT_AT_LEAST = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHeightRule newValue(final Object o) {
            return (STHeightRule)STHeightRule.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHeightRule.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHeightRule newInstance() {
            return (STHeightRule)getTypeLoader().newInstance(STHeightRule.type, (XmlOptions)null);
        }
        
        public static STHeightRule newInstance(final XmlOptions xmlOptions) {
            return (STHeightRule)getTypeLoader().newInstance(STHeightRule.type, xmlOptions);
        }
        
        public static STHeightRule parse(final String s) throws XmlException {
            return (STHeightRule)getTypeLoader().parse(s, STHeightRule.type, (XmlOptions)null);
        }
        
        public static STHeightRule parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHeightRule)getTypeLoader().parse(s, STHeightRule.type, xmlOptions);
        }
        
        public static STHeightRule parse(final File file) throws XmlException, IOException {
            return (STHeightRule)getTypeLoader().parse(file, STHeightRule.type, (XmlOptions)null);
        }
        
        public static STHeightRule parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHeightRule)getTypeLoader().parse(file, STHeightRule.type, xmlOptions);
        }
        
        public static STHeightRule parse(final URL url) throws XmlException, IOException {
            return (STHeightRule)getTypeLoader().parse(url, STHeightRule.type, (XmlOptions)null);
        }
        
        public static STHeightRule parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHeightRule)getTypeLoader().parse(url, STHeightRule.type, xmlOptions);
        }
        
        public static STHeightRule parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHeightRule)getTypeLoader().parse(inputStream, STHeightRule.type, (XmlOptions)null);
        }
        
        public static STHeightRule parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHeightRule)getTypeLoader().parse(inputStream, STHeightRule.type, xmlOptions);
        }
        
        public static STHeightRule parse(final Reader reader) throws XmlException, IOException {
            return (STHeightRule)getTypeLoader().parse(reader, STHeightRule.type, (XmlOptions)null);
        }
        
        public static STHeightRule parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHeightRule)getTypeLoader().parse(reader, STHeightRule.type, xmlOptions);
        }
        
        public static STHeightRule parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHeightRule)getTypeLoader().parse(xmlStreamReader, STHeightRule.type, (XmlOptions)null);
        }
        
        public static STHeightRule parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHeightRule)getTypeLoader().parse(xmlStreamReader, STHeightRule.type, xmlOptions);
        }
        
        public static STHeightRule parse(final Node node) throws XmlException {
            return (STHeightRule)getTypeLoader().parse(node, STHeightRule.type, (XmlOptions)null);
        }
        
        public static STHeightRule parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHeightRule)getTypeLoader().parse(node, STHeightRule.type, xmlOptions);
        }
        
        @Deprecated
        public static STHeightRule parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHeightRule)getTypeLoader().parse(xmlInputStream, STHeightRule.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHeightRule parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHeightRule)getTypeLoader().parse(xmlInputStream, STHeightRule.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHeightRule.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHeightRule.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_AUTO = 1;
        static final int INT_EXACT = 2;
        static final int INT_AT_LEAST = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("auto", 1), new Enum("exact", 2), new Enum("atLeast", 3) });
        }
    }
}
