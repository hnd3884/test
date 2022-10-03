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

public interface STLineSpacingRule extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STLineSpacingRule.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stlinespacingrule6237type");
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
        
        public static STLineSpacingRule newValue(final Object o) {
            return (STLineSpacingRule)STLineSpacingRule.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STLineSpacingRule.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STLineSpacingRule newInstance() {
            return (STLineSpacingRule)getTypeLoader().newInstance(STLineSpacingRule.type, (XmlOptions)null);
        }
        
        public static STLineSpacingRule newInstance(final XmlOptions xmlOptions) {
            return (STLineSpacingRule)getTypeLoader().newInstance(STLineSpacingRule.type, xmlOptions);
        }
        
        public static STLineSpacingRule parse(final String s) throws XmlException {
            return (STLineSpacingRule)getTypeLoader().parse(s, STLineSpacingRule.type, (XmlOptions)null);
        }
        
        public static STLineSpacingRule parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STLineSpacingRule)getTypeLoader().parse(s, STLineSpacingRule.type, xmlOptions);
        }
        
        public static STLineSpacingRule parse(final File file) throws XmlException, IOException {
            return (STLineSpacingRule)getTypeLoader().parse(file, STLineSpacingRule.type, (XmlOptions)null);
        }
        
        public static STLineSpacingRule parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineSpacingRule)getTypeLoader().parse(file, STLineSpacingRule.type, xmlOptions);
        }
        
        public static STLineSpacingRule parse(final URL url) throws XmlException, IOException {
            return (STLineSpacingRule)getTypeLoader().parse(url, STLineSpacingRule.type, (XmlOptions)null);
        }
        
        public static STLineSpacingRule parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineSpacingRule)getTypeLoader().parse(url, STLineSpacingRule.type, xmlOptions);
        }
        
        public static STLineSpacingRule parse(final InputStream inputStream) throws XmlException, IOException {
            return (STLineSpacingRule)getTypeLoader().parse(inputStream, STLineSpacingRule.type, (XmlOptions)null);
        }
        
        public static STLineSpacingRule parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineSpacingRule)getTypeLoader().parse(inputStream, STLineSpacingRule.type, xmlOptions);
        }
        
        public static STLineSpacingRule parse(final Reader reader) throws XmlException, IOException {
            return (STLineSpacingRule)getTypeLoader().parse(reader, STLineSpacingRule.type, (XmlOptions)null);
        }
        
        public static STLineSpacingRule parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STLineSpacingRule)getTypeLoader().parse(reader, STLineSpacingRule.type, xmlOptions);
        }
        
        public static STLineSpacingRule parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STLineSpacingRule)getTypeLoader().parse(xmlStreamReader, STLineSpacingRule.type, (XmlOptions)null);
        }
        
        public static STLineSpacingRule parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STLineSpacingRule)getTypeLoader().parse(xmlStreamReader, STLineSpacingRule.type, xmlOptions);
        }
        
        public static STLineSpacingRule parse(final Node node) throws XmlException {
            return (STLineSpacingRule)getTypeLoader().parse(node, STLineSpacingRule.type, (XmlOptions)null);
        }
        
        public static STLineSpacingRule parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STLineSpacingRule)getTypeLoader().parse(node, STLineSpacingRule.type, xmlOptions);
        }
        
        @Deprecated
        public static STLineSpacingRule parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STLineSpacingRule)getTypeLoader().parse(xmlInputStream, STLineSpacingRule.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STLineSpacingRule parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STLineSpacingRule)getTypeLoader().parse(xmlInputStream, STLineSpacingRule.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineSpacingRule.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STLineSpacingRule.type, xmlOptions);
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
