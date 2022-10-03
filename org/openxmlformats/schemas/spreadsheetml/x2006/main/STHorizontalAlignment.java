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

public interface STHorizontalAlignment extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STHorizontalAlignment.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sthorizontalalignmentf92etype");
    public static final Enum GENERAL = Enum.forString("general");
    public static final Enum LEFT = Enum.forString("left");
    public static final Enum CENTER = Enum.forString("center");
    public static final Enum RIGHT = Enum.forString("right");
    public static final Enum FILL = Enum.forString("fill");
    public static final Enum JUSTIFY = Enum.forString("justify");
    public static final Enum CENTER_CONTINUOUS = Enum.forString("centerContinuous");
    public static final Enum DISTRIBUTED = Enum.forString("distributed");
    public static final int INT_GENERAL = 1;
    public static final int INT_LEFT = 2;
    public static final int INT_CENTER = 3;
    public static final int INT_RIGHT = 4;
    public static final int INT_FILL = 5;
    public static final int INT_JUSTIFY = 6;
    public static final int INT_CENTER_CONTINUOUS = 7;
    public static final int INT_DISTRIBUTED = 8;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STHorizontalAlignment newValue(final Object o) {
            return (STHorizontalAlignment)STHorizontalAlignment.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STHorizontalAlignment.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STHorizontalAlignment newInstance() {
            return (STHorizontalAlignment)getTypeLoader().newInstance(STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        public static STHorizontalAlignment newInstance(final XmlOptions xmlOptions) {
            return (STHorizontalAlignment)getTypeLoader().newInstance(STHorizontalAlignment.type, xmlOptions);
        }
        
        public static STHorizontalAlignment parse(final String s) throws XmlException {
            return (STHorizontalAlignment)getTypeLoader().parse(s, STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        public static STHorizontalAlignment parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STHorizontalAlignment)getTypeLoader().parse(s, STHorizontalAlignment.type, xmlOptions);
        }
        
        public static STHorizontalAlignment parse(final File file) throws XmlException, IOException {
            return (STHorizontalAlignment)getTypeLoader().parse(file, STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        public static STHorizontalAlignment parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHorizontalAlignment)getTypeLoader().parse(file, STHorizontalAlignment.type, xmlOptions);
        }
        
        public static STHorizontalAlignment parse(final URL url) throws XmlException, IOException {
            return (STHorizontalAlignment)getTypeLoader().parse(url, STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        public static STHorizontalAlignment parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHorizontalAlignment)getTypeLoader().parse(url, STHorizontalAlignment.type, xmlOptions);
        }
        
        public static STHorizontalAlignment parse(final InputStream inputStream) throws XmlException, IOException {
            return (STHorizontalAlignment)getTypeLoader().parse(inputStream, STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        public static STHorizontalAlignment parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHorizontalAlignment)getTypeLoader().parse(inputStream, STHorizontalAlignment.type, xmlOptions);
        }
        
        public static STHorizontalAlignment parse(final Reader reader) throws XmlException, IOException {
            return (STHorizontalAlignment)getTypeLoader().parse(reader, STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        public static STHorizontalAlignment parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STHorizontalAlignment)getTypeLoader().parse(reader, STHorizontalAlignment.type, xmlOptions);
        }
        
        public static STHorizontalAlignment parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STHorizontalAlignment)getTypeLoader().parse(xmlStreamReader, STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        public static STHorizontalAlignment parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STHorizontalAlignment)getTypeLoader().parse(xmlStreamReader, STHorizontalAlignment.type, xmlOptions);
        }
        
        public static STHorizontalAlignment parse(final Node node) throws XmlException {
            return (STHorizontalAlignment)getTypeLoader().parse(node, STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        public static STHorizontalAlignment parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STHorizontalAlignment)getTypeLoader().parse(node, STHorizontalAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static STHorizontalAlignment parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STHorizontalAlignment)getTypeLoader().parse(xmlInputStream, STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STHorizontalAlignment parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STHorizontalAlignment)getTypeLoader().parse(xmlInputStream, STHorizontalAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHorizontalAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STHorizontalAlignment.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_GENERAL = 1;
        static final int INT_LEFT = 2;
        static final int INT_CENTER = 3;
        static final int INT_RIGHT = 4;
        static final int INT_FILL = 5;
        static final int INT_JUSTIFY = 6;
        static final int INT_CENTER_CONTINUOUS = 7;
        static final int INT_DISTRIBUTED = 8;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("general", 1), new Enum("left", 2), new Enum("center", 3), new Enum("right", 4), new Enum("fill", 5), new Enum("justify", 6), new Enum("centerContinuous", 7), new Enum("distributed", 8) });
        }
    }
}
