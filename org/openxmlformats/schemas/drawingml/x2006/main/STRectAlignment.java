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

public interface STRectAlignment extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STRectAlignment.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("strectalignmentd400type");
    public static final Enum TL = Enum.forString("tl");
    public static final Enum T = Enum.forString("t");
    public static final Enum TR = Enum.forString("tr");
    public static final Enum L = Enum.forString("l");
    public static final Enum CTR = Enum.forString("ctr");
    public static final Enum R = Enum.forString("r");
    public static final Enum BL = Enum.forString("bl");
    public static final Enum B = Enum.forString("b");
    public static final Enum BR = Enum.forString("br");
    public static final int INT_TL = 1;
    public static final int INT_T = 2;
    public static final int INT_TR = 3;
    public static final int INT_L = 4;
    public static final int INT_CTR = 5;
    public static final int INT_R = 6;
    public static final int INT_BL = 7;
    public static final int INT_B = 8;
    public static final int INT_BR = 9;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STRectAlignment newValue(final Object o) {
            return (STRectAlignment)STRectAlignment.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STRectAlignment.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STRectAlignment newInstance() {
            return (STRectAlignment)getTypeLoader().newInstance(STRectAlignment.type, (XmlOptions)null);
        }
        
        public static STRectAlignment newInstance(final XmlOptions xmlOptions) {
            return (STRectAlignment)getTypeLoader().newInstance(STRectAlignment.type, xmlOptions);
        }
        
        public static STRectAlignment parse(final String s) throws XmlException {
            return (STRectAlignment)getTypeLoader().parse(s, STRectAlignment.type, (XmlOptions)null);
        }
        
        public static STRectAlignment parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STRectAlignment)getTypeLoader().parse(s, STRectAlignment.type, xmlOptions);
        }
        
        public static STRectAlignment parse(final File file) throws XmlException, IOException {
            return (STRectAlignment)getTypeLoader().parse(file, STRectAlignment.type, (XmlOptions)null);
        }
        
        public static STRectAlignment parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRectAlignment)getTypeLoader().parse(file, STRectAlignment.type, xmlOptions);
        }
        
        public static STRectAlignment parse(final URL url) throws XmlException, IOException {
            return (STRectAlignment)getTypeLoader().parse(url, STRectAlignment.type, (XmlOptions)null);
        }
        
        public static STRectAlignment parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRectAlignment)getTypeLoader().parse(url, STRectAlignment.type, xmlOptions);
        }
        
        public static STRectAlignment parse(final InputStream inputStream) throws XmlException, IOException {
            return (STRectAlignment)getTypeLoader().parse(inputStream, STRectAlignment.type, (XmlOptions)null);
        }
        
        public static STRectAlignment parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRectAlignment)getTypeLoader().parse(inputStream, STRectAlignment.type, xmlOptions);
        }
        
        public static STRectAlignment parse(final Reader reader) throws XmlException, IOException {
            return (STRectAlignment)getTypeLoader().parse(reader, STRectAlignment.type, (XmlOptions)null);
        }
        
        public static STRectAlignment parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STRectAlignment)getTypeLoader().parse(reader, STRectAlignment.type, xmlOptions);
        }
        
        public static STRectAlignment parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STRectAlignment)getTypeLoader().parse(xmlStreamReader, STRectAlignment.type, (XmlOptions)null);
        }
        
        public static STRectAlignment parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STRectAlignment)getTypeLoader().parse(xmlStreamReader, STRectAlignment.type, xmlOptions);
        }
        
        public static STRectAlignment parse(final Node node) throws XmlException {
            return (STRectAlignment)getTypeLoader().parse(node, STRectAlignment.type, (XmlOptions)null);
        }
        
        public static STRectAlignment parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STRectAlignment)getTypeLoader().parse(node, STRectAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static STRectAlignment parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STRectAlignment)getTypeLoader().parse(xmlInputStream, STRectAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STRectAlignment parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STRectAlignment)getTypeLoader().parse(xmlInputStream, STRectAlignment.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRectAlignment.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STRectAlignment.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TL = 1;
        static final int INT_T = 2;
        static final int INT_TR = 3;
        static final int INT_L = 4;
        static final int INT_CTR = 5;
        static final int INT_R = 6;
        static final int INT_BL = 7;
        static final int INT_B = 8;
        static final int INT_BR = 9;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("tl", 1), new Enum("t", 2), new Enum("tr", 3), new Enum("l", 4), new Enum("ctr", 5), new Enum("r", 6), new Enum("bl", 7), new Enum("b", 8), new Enum("br", 9) });
        }
    }
}
