package com.microsoft.schemas.office.excel;

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

public interface STObjectType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STObjectType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stobjecttype97a7type");
    public static final Enum BUTTON = Enum.forString("Button");
    public static final Enum CHECKBOX = Enum.forString("Checkbox");
    public static final Enum DIALOG = Enum.forString("Dialog");
    public static final Enum DROP = Enum.forString("Drop");
    public static final Enum EDIT = Enum.forString("Edit");
    public static final Enum G_BOX = Enum.forString("GBox");
    public static final Enum LABEL = Enum.forString("Label");
    public static final Enum LINE_A = Enum.forString("LineA");
    public static final Enum LIST = Enum.forString("List");
    public static final Enum MOVIE = Enum.forString("Movie");
    public static final Enum NOTE = Enum.forString("Note");
    public static final Enum PICT = Enum.forString("Pict");
    public static final Enum RADIO = Enum.forString("Radio");
    public static final Enum RECT_A = Enum.forString("RectA");
    public static final Enum SCROLL = Enum.forString("Scroll");
    public static final Enum SPIN = Enum.forString("Spin");
    public static final Enum SHAPE = Enum.forString("Shape");
    public static final Enum GROUP = Enum.forString("Group");
    public static final Enum RECT = Enum.forString("Rect");
    public static final int INT_BUTTON = 1;
    public static final int INT_CHECKBOX = 2;
    public static final int INT_DIALOG = 3;
    public static final int INT_DROP = 4;
    public static final int INT_EDIT = 5;
    public static final int INT_G_BOX = 6;
    public static final int INT_LABEL = 7;
    public static final int INT_LINE_A = 8;
    public static final int INT_LIST = 9;
    public static final int INT_MOVIE = 10;
    public static final int INT_NOTE = 11;
    public static final int INT_PICT = 12;
    public static final int INT_RADIO = 13;
    public static final int INT_RECT_A = 14;
    public static final int INT_SCROLL = 15;
    public static final int INT_SPIN = 16;
    public static final int INT_SHAPE = 17;
    public static final int INT_GROUP = 18;
    public static final int INT_RECT = 19;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STObjectType newValue(final Object o) {
            return (STObjectType)STObjectType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STObjectType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STObjectType newInstance() {
            return (STObjectType)getTypeLoader().newInstance(STObjectType.type, (XmlOptions)null);
        }
        
        public static STObjectType newInstance(final XmlOptions xmlOptions) {
            return (STObjectType)getTypeLoader().newInstance(STObjectType.type, xmlOptions);
        }
        
        public static STObjectType parse(final String s) throws XmlException {
            return (STObjectType)getTypeLoader().parse(s, STObjectType.type, (XmlOptions)null);
        }
        
        public static STObjectType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STObjectType)getTypeLoader().parse(s, STObjectType.type, xmlOptions);
        }
        
        public static STObjectType parse(final File file) throws XmlException, IOException {
            return (STObjectType)getTypeLoader().parse(file, STObjectType.type, (XmlOptions)null);
        }
        
        public static STObjectType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STObjectType)getTypeLoader().parse(file, STObjectType.type, xmlOptions);
        }
        
        public static STObjectType parse(final URL url) throws XmlException, IOException {
            return (STObjectType)getTypeLoader().parse(url, STObjectType.type, (XmlOptions)null);
        }
        
        public static STObjectType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STObjectType)getTypeLoader().parse(url, STObjectType.type, xmlOptions);
        }
        
        public static STObjectType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STObjectType)getTypeLoader().parse(inputStream, STObjectType.type, (XmlOptions)null);
        }
        
        public static STObjectType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STObjectType)getTypeLoader().parse(inputStream, STObjectType.type, xmlOptions);
        }
        
        public static STObjectType parse(final Reader reader) throws XmlException, IOException {
            return (STObjectType)getTypeLoader().parse(reader, STObjectType.type, (XmlOptions)null);
        }
        
        public static STObjectType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STObjectType)getTypeLoader().parse(reader, STObjectType.type, xmlOptions);
        }
        
        public static STObjectType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STObjectType)getTypeLoader().parse(xmlStreamReader, STObjectType.type, (XmlOptions)null);
        }
        
        public static STObjectType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STObjectType)getTypeLoader().parse(xmlStreamReader, STObjectType.type, xmlOptions);
        }
        
        public static STObjectType parse(final Node node) throws XmlException {
            return (STObjectType)getTypeLoader().parse(node, STObjectType.type, (XmlOptions)null);
        }
        
        public static STObjectType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STObjectType)getTypeLoader().parse(node, STObjectType.type, xmlOptions);
        }
        
        @Deprecated
        public static STObjectType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STObjectType)getTypeLoader().parse(xmlInputStream, STObjectType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STObjectType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STObjectType)getTypeLoader().parse(xmlInputStream, STObjectType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STObjectType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STObjectType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_BUTTON = 1;
        static final int INT_CHECKBOX = 2;
        static final int INT_DIALOG = 3;
        static final int INT_DROP = 4;
        static final int INT_EDIT = 5;
        static final int INT_G_BOX = 6;
        static final int INT_LABEL = 7;
        static final int INT_LINE_A = 8;
        static final int INT_LIST = 9;
        static final int INT_MOVIE = 10;
        static final int INT_NOTE = 11;
        static final int INT_PICT = 12;
        static final int INT_RADIO = 13;
        static final int INT_RECT_A = 14;
        static final int INT_SCROLL = 15;
        static final int INT_SPIN = 16;
        static final int INT_SHAPE = 17;
        static final int INT_GROUP = 18;
        static final int INT_RECT = 19;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("Button", 1), new Enum("Checkbox", 2), new Enum("Dialog", 3), new Enum("Drop", 4), new Enum("Edit", 5), new Enum("GBox", 6), new Enum("Label", 7), new Enum("LineA", 8), new Enum("List", 9), new Enum("Movie", 10), new Enum("Note", 11), new Enum("Pict", 12), new Enum("Radio", 13), new Enum("RectA", 14), new Enum("Scroll", 15), new Enum("Spin", 16), new Enum("Shape", 17), new Enum("Group", 18), new Enum("Rect", 19) });
        }
    }
}
