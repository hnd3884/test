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

public interface STBorderStyle extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STBorderStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stborderstylec774type");
    public static final Enum NONE = Enum.forString("none");
    public static final Enum THIN = Enum.forString("thin");
    public static final Enum MEDIUM = Enum.forString("medium");
    public static final Enum DASHED = Enum.forString("dashed");
    public static final Enum DOTTED = Enum.forString("dotted");
    public static final Enum THICK = Enum.forString("thick");
    public static final Enum DOUBLE = Enum.forString("double");
    public static final Enum HAIR = Enum.forString("hair");
    public static final Enum MEDIUM_DASHED = Enum.forString("mediumDashed");
    public static final Enum DASH_DOT = Enum.forString("dashDot");
    public static final Enum MEDIUM_DASH_DOT = Enum.forString("mediumDashDot");
    public static final Enum DASH_DOT_DOT = Enum.forString("dashDotDot");
    public static final Enum MEDIUM_DASH_DOT_DOT = Enum.forString("mediumDashDotDot");
    public static final Enum SLANT_DASH_DOT = Enum.forString("slantDashDot");
    public static final int INT_NONE = 1;
    public static final int INT_THIN = 2;
    public static final int INT_MEDIUM = 3;
    public static final int INT_DASHED = 4;
    public static final int INT_DOTTED = 5;
    public static final int INT_THICK = 6;
    public static final int INT_DOUBLE = 7;
    public static final int INT_HAIR = 8;
    public static final int INT_MEDIUM_DASHED = 9;
    public static final int INT_DASH_DOT = 10;
    public static final int INT_MEDIUM_DASH_DOT = 11;
    public static final int INT_DASH_DOT_DOT = 12;
    public static final int INT_MEDIUM_DASH_DOT_DOT = 13;
    public static final int INT_SLANT_DASH_DOT = 14;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STBorderStyle newValue(final Object o) {
            return (STBorderStyle)STBorderStyle.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STBorderStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STBorderStyle newInstance() {
            return (STBorderStyle)getTypeLoader().newInstance(STBorderStyle.type, (XmlOptions)null);
        }
        
        public static STBorderStyle newInstance(final XmlOptions xmlOptions) {
            return (STBorderStyle)getTypeLoader().newInstance(STBorderStyle.type, xmlOptions);
        }
        
        public static STBorderStyle parse(final String s) throws XmlException {
            return (STBorderStyle)getTypeLoader().parse(s, STBorderStyle.type, (XmlOptions)null);
        }
        
        public static STBorderStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STBorderStyle)getTypeLoader().parse(s, STBorderStyle.type, xmlOptions);
        }
        
        public static STBorderStyle parse(final File file) throws XmlException, IOException {
            return (STBorderStyle)getTypeLoader().parse(file, STBorderStyle.type, (XmlOptions)null);
        }
        
        public static STBorderStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBorderStyle)getTypeLoader().parse(file, STBorderStyle.type, xmlOptions);
        }
        
        public static STBorderStyle parse(final URL url) throws XmlException, IOException {
            return (STBorderStyle)getTypeLoader().parse(url, STBorderStyle.type, (XmlOptions)null);
        }
        
        public static STBorderStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBorderStyle)getTypeLoader().parse(url, STBorderStyle.type, xmlOptions);
        }
        
        public static STBorderStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (STBorderStyle)getTypeLoader().parse(inputStream, STBorderStyle.type, (XmlOptions)null);
        }
        
        public static STBorderStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBorderStyle)getTypeLoader().parse(inputStream, STBorderStyle.type, xmlOptions);
        }
        
        public static STBorderStyle parse(final Reader reader) throws XmlException, IOException {
            return (STBorderStyle)getTypeLoader().parse(reader, STBorderStyle.type, (XmlOptions)null);
        }
        
        public static STBorderStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STBorderStyle)getTypeLoader().parse(reader, STBorderStyle.type, xmlOptions);
        }
        
        public static STBorderStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STBorderStyle)getTypeLoader().parse(xmlStreamReader, STBorderStyle.type, (XmlOptions)null);
        }
        
        public static STBorderStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STBorderStyle)getTypeLoader().parse(xmlStreamReader, STBorderStyle.type, xmlOptions);
        }
        
        public static STBorderStyle parse(final Node node) throws XmlException {
            return (STBorderStyle)getTypeLoader().parse(node, STBorderStyle.type, (XmlOptions)null);
        }
        
        public static STBorderStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STBorderStyle)getTypeLoader().parse(node, STBorderStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static STBorderStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STBorderStyle)getTypeLoader().parse(xmlInputStream, STBorderStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STBorderStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STBorderStyle)getTypeLoader().parse(xmlInputStream, STBorderStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBorderStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STBorderStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_NONE = 1;
        static final int INT_THIN = 2;
        static final int INT_MEDIUM = 3;
        static final int INT_DASHED = 4;
        static final int INT_DOTTED = 5;
        static final int INT_THICK = 6;
        static final int INT_DOUBLE = 7;
        static final int INT_HAIR = 8;
        static final int INT_MEDIUM_DASHED = 9;
        static final int INT_DASH_DOT = 10;
        static final int INT_MEDIUM_DASH_DOT = 11;
        static final int INT_DASH_DOT_DOT = 12;
        static final int INT_MEDIUM_DASH_DOT_DOT = 13;
        static final int INT_SLANT_DASH_DOT = 14;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("none", 1), new Enum("thin", 2), new Enum("medium", 3), new Enum("dashed", 4), new Enum("dotted", 5), new Enum("thick", 6), new Enum("double", 7), new Enum("hair", 8), new Enum("mediumDashed", 9), new Enum("dashDot", 10), new Enum("mediumDashDot", 11), new Enum("dashDotDot", 12), new Enum("mediumDashDotDot", 13), new Enum("slantDashDot", 14) });
        }
    }
}
