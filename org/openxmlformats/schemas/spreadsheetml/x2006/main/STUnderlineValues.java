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

public interface STUnderlineValues extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STUnderlineValues.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stunderlinevaluesb6ddtype");
    public static final Enum SINGLE = Enum.forString("single");
    public static final Enum DOUBLE = Enum.forString("double");
    public static final Enum SINGLE_ACCOUNTING = Enum.forString("singleAccounting");
    public static final Enum DOUBLE_ACCOUNTING = Enum.forString("doubleAccounting");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_SINGLE = 1;
    public static final int INT_DOUBLE = 2;
    public static final int INT_SINGLE_ACCOUNTING = 3;
    public static final int INT_DOUBLE_ACCOUNTING = 4;
    public static final int INT_NONE = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STUnderlineValues newValue(final Object o) {
            return (STUnderlineValues)STUnderlineValues.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STUnderlineValues.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STUnderlineValues newInstance() {
            return (STUnderlineValues)getTypeLoader().newInstance(STUnderlineValues.type, (XmlOptions)null);
        }
        
        public static STUnderlineValues newInstance(final XmlOptions xmlOptions) {
            return (STUnderlineValues)getTypeLoader().newInstance(STUnderlineValues.type, xmlOptions);
        }
        
        public static STUnderlineValues parse(final String s) throws XmlException {
            return (STUnderlineValues)getTypeLoader().parse(s, STUnderlineValues.type, (XmlOptions)null);
        }
        
        public static STUnderlineValues parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STUnderlineValues)getTypeLoader().parse(s, STUnderlineValues.type, xmlOptions);
        }
        
        public static STUnderlineValues parse(final File file) throws XmlException, IOException {
            return (STUnderlineValues)getTypeLoader().parse(file, STUnderlineValues.type, (XmlOptions)null);
        }
        
        public static STUnderlineValues parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnderlineValues)getTypeLoader().parse(file, STUnderlineValues.type, xmlOptions);
        }
        
        public static STUnderlineValues parse(final URL url) throws XmlException, IOException {
            return (STUnderlineValues)getTypeLoader().parse(url, STUnderlineValues.type, (XmlOptions)null);
        }
        
        public static STUnderlineValues parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnderlineValues)getTypeLoader().parse(url, STUnderlineValues.type, xmlOptions);
        }
        
        public static STUnderlineValues parse(final InputStream inputStream) throws XmlException, IOException {
            return (STUnderlineValues)getTypeLoader().parse(inputStream, STUnderlineValues.type, (XmlOptions)null);
        }
        
        public static STUnderlineValues parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnderlineValues)getTypeLoader().parse(inputStream, STUnderlineValues.type, xmlOptions);
        }
        
        public static STUnderlineValues parse(final Reader reader) throws XmlException, IOException {
            return (STUnderlineValues)getTypeLoader().parse(reader, STUnderlineValues.type, (XmlOptions)null);
        }
        
        public static STUnderlineValues parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnderlineValues)getTypeLoader().parse(reader, STUnderlineValues.type, xmlOptions);
        }
        
        public static STUnderlineValues parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STUnderlineValues)getTypeLoader().parse(xmlStreamReader, STUnderlineValues.type, (XmlOptions)null);
        }
        
        public static STUnderlineValues parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STUnderlineValues)getTypeLoader().parse(xmlStreamReader, STUnderlineValues.type, xmlOptions);
        }
        
        public static STUnderlineValues parse(final Node node) throws XmlException {
            return (STUnderlineValues)getTypeLoader().parse(node, STUnderlineValues.type, (XmlOptions)null);
        }
        
        public static STUnderlineValues parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STUnderlineValues)getTypeLoader().parse(node, STUnderlineValues.type, xmlOptions);
        }
        
        @Deprecated
        public static STUnderlineValues parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STUnderlineValues)getTypeLoader().parse(xmlInputStream, STUnderlineValues.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STUnderlineValues parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STUnderlineValues)getTypeLoader().parse(xmlInputStream, STUnderlineValues.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnderlineValues.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnderlineValues.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SINGLE = 1;
        static final int INT_DOUBLE = 2;
        static final int INT_SINGLE_ACCOUNTING = 3;
        static final int INT_DOUBLE_ACCOUNTING = 4;
        static final int INT_NONE = 5;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("single", 1), new Enum("double", 2), new Enum("singleAccounting", 3), new Enum("doubleAccounting", 4), new Enum("none", 5) });
        }
    }
}
