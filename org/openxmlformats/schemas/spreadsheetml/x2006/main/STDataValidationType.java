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

public interface STDataValidationType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STDataValidationType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stdatavalidationtypeabf6type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum WHOLE = Enum.forString("whole");
    public static final Enum DECIMAL = Enum.forString("decimal");
    public static final Enum LIST = Enum.forString("list");
    public static final Enum DATE = Enum.forString("date");
    public static final Enum TIME = Enum.forString("time");
    public static final Enum TEXT_LENGTH = Enum.forString("textLength");
    public static final Enum CUSTOM = Enum.forString("custom");
    public static final int INT_NONE = 1;
    public static final int INT_WHOLE = 2;
    public static final int INT_DECIMAL = 3;
    public static final int INT_LIST = 4;
    public static final int INT_DATE = 5;
    public static final int INT_TIME = 6;
    public static final int INT_TEXT_LENGTH = 7;
    public static final int INT_CUSTOM = 8;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STDataValidationType newValue(final Object o) {
            return (STDataValidationType)STDataValidationType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STDataValidationType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STDataValidationType newInstance() {
            return (STDataValidationType)getTypeLoader().newInstance(STDataValidationType.type, (XmlOptions)null);
        }
        
        public static STDataValidationType newInstance(final XmlOptions xmlOptions) {
            return (STDataValidationType)getTypeLoader().newInstance(STDataValidationType.type, xmlOptions);
        }
        
        public static STDataValidationType parse(final String s) throws XmlException {
            return (STDataValidationType)getTypeLoader().parse(s, STDataValidationType.type, (XmlOptions)null);
        }
        
        public static STDataValidationType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STDataValidationType)getTypeLoader().parse(s, STDataValidationType.type, xmlOptions);
        }
        
        public static STDataValidationType parse(final File file) throws XmlException, IOException {
            return (STDataValidationType)getTypeLoader().parse(file, STDataValidationType.type, (XmlOptions)null);
        }
        
        public static STDataValidationType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationType)getTypeLoader().parse(file, STDataValidationType.type, xmlOptions);
        }
        
        public static STDataValidationType parse(final URL url) throws XmlException, IOException {
            return (STDataValidationType)getTypeLoader().parse(url, STDataValidationType.type, (XmlOptions)null);
        }
        
        public static STDataValidationType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationType)getTypeLoader().parse(url, STDataValidationType.type, xmlOptions);
        }
        
        public static STDataValidationType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STDataValidationType)getTypeLoader().parse(inputStream, STDataValidationType.type, (XmlOptions)null);
        }
        
        public static STDataValidationType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationType)getTypeLoader().parse(inputStream, STDataValidationType.type, xmlOptions);
        }
        
        public static STDataValidationType parse(final Reader reader) throws XmlException, IOException {
            return (STDataValidationType)getTypeLoader().parse(reader, STDataValidationType.type, (XmlOptions)null);
        }
        
        public static STDataValidationType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STDataValidationType)getTypeLoader().parse(reader, STDataValidationType.type, xmlOptions);
        }
        
        public static STDataValidationType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STDataValidationType)getTypeLoader().parse(xmlStreamReader, STDataValidationType.type, (XmlOptions)null);
        }
        
        public static STDataValidationType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STDataValidationType)getTypeLoader().parse(xmlStreamReader, STDataValidationType.type, xmlOptions);
        }
        
        public static STDataValidationType parse(final Node node) throws XmlException {
            return (STDataValidationType)getTypeLoader().parse(node, STDataValidationType.type, (XmlOptions)null);
        }
        
        public static STDataValidationType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STDataValidationType)getTypeLoader().parse(node, STDataValidationType.type, xmlOptions);
        }
        
        @Deprecated
        public static STDataValidationType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STDataValidationType)getTypeLoader().parse(xmlInputStream, STDataValidationType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STDataValidationType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STDataValidationType)getTypeLoader().parse(xmlInputStream, STDataValidationType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDataValidationType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STDataValidationType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_WHOLE = 2;
        static final int INT_DECIMAL = 3;
        static final int INT_LIST = 4;
        static final int INT_DATE = 5;
        static final int INT_TIME = 6;
        static final int INT_TEXT_LENGTH = 7;
        static final int INT_CUSTOM = 8;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("whole", 2), new Enum("decimal", 3), new Enum("list", 4), new Enum("date", 5), new Enum("time", 6), new Enum("textLength", 7), new Enum("custom", 8) });
        }
    }
}
