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

public interface STTextAlignment extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTextAlignment.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttextalignment316ctype");
    public static final Enum TOP = Enum.forString("top");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum BASELINE = Enum.forString("baseline");
    public static final Enum BOTTOM = Enum.forString("bottom");
    public static final Enum AUTO = Enum.forString("auto");
    public static final int INT_TOP = 1;
    public static final int INT_CENTER = 2;
    public static final int INT_BASELINE = 3;
    public static final int INT_BOTTOM = 4;
    public static final int INT_AUTO = 5;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTextAlignment newValue(final Object o) {
            return (STTextAlignment)STTextAlignment.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTextAlignment.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTextAlignment newInstance() {
            return (STTextAlignment)getTypeLoader().newInstance(STTextAlignment.type, (XmlOptions)null);
        }
        
        public static STTextAlignment newInstance(final XmlOptions xmlOptions) {
            return (STTextAlignment)getTypeLoader().newInstance(STTextAlignment.type, xmlOptions);
        }
        
        public static STTextAlignment parse(final String s) throws XmlException {
            return (STTextAlignment)getTypeLoader().parse(s, STTextAlignment.type, (XmlOptions)null);
        }
        
        public static STTextAlignment parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAlignment)getTypeLoader().parse(s, STTextAlignment.type, xmlOptions);
        }
        
        public static STTextAlignment parse(final File file) throws XmlException, IOException {
            return (STTextAlignment)getTypeLoader().parse(file, STTextAlignment.type, (XmlOptions)null);
        }
        
        public static STTextAlignment parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAlignment)getTypeLoader().parse(file, STTextAlignment.type, xmlOptions);
        }
        
        public static STTextAlignment parse(final URL url) throws XmlException, IOException {
            return (STTextAlignment)getTypeLoader().parse(url, STTextAlignment.type, (XmlOptions)null);
        }
        
        public static STTextAlignment parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAlignment)getTypeLoader().parse(url, STTextAlignment.type, xmlOptions);
        }
        
        public static STTextAlignment parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTextAlignment)getTypeLoader().parse(inputStream, STTextAlignment.type, (XmlOptions)null);
        }
        
        public static STTextAlignment parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAlignment)getTypeLoader().parse(inputStream, STTextAlignment.type, xmlOptions);
        }
        
        public static STTextAlignment parse(final Reader reader) throws XmlException, IOException {
            return (STTextAlignment)getTypeLoader().parse(reader, STTextAlignment.type, (XmlOptions)null);
        }
        
        public static STTextAlignment parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTextAlignment)getTypeLoader().parse(reader, STTextAlignment.type, xmlOptions);
        }
        
        public static STTextAlignment parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTextAlignment)getTypeLoader().parse(xmlStreamReader, STTextAlignment.type, (XmlOptions)null);
        }
        
        public static STTextAlignment parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAlignment)getTypeLoader().parse(xmlStreamReader, STTextAlignment.type, xmlOptions);
        }
        
        public static STTextAlignment parse(final Node node) throws XmlException {
            return (STTextAlignment)getTypeLoader().parse(node, STTextAlignment.type, (XmlOptions)null);
        }
        
        public static STTextAlignment parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTextAlignment)getTypeLoader().parse(node, STTextAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static STTextAlignment parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTextAlignment)getTypeLoader().parse(xmlInputStream, STTextAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTextAlignment parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTextAlignment)getTypeLoader().parse(xmlInputStream, STTextAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTextAlignment.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TOP = 1;
        static final int INT_CENTER = 2;
        static final int INT_BASELINE = 3;
        static final int INT_BOTTOM = 4;
        static final int INT_AUTO = 5;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("top", 1), new Enum("center", 2), new Enum("baseline", 3), new Enum("bottom", 4), new Enum("auto", 5) });
        }
    }
}
