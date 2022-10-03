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

public interface STTheme extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STTheme.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sttheme58b9type");
    public static final Enum MAJOR_EAST_ASIA = Enum.forString("majorEastAsia");
    public static final Enum MAJOR_BIDI = Enum.forString("majorBidi");
    public static final Enum MAJOR_ASCII = Enum.forString("majorAscii");
    public static final Enum MAJOR_H_ANSI = Enum.forString("majorHAnsi");
    public static final Enum MINOR_EAST_ASIA = Enum.forString("minorEastAsia");
    public static final Enum MINOR_BIDI = Enum.forString("minorBidi");
    public static final Enum MINOR_ASCII = Enum.forString("minorAscii");
    public static final Enum MINOR_H_ANSI = Enum.forString("minorHAnsi");
    public static final int INT_MAJOR_EAST_ASIA = 1;
    public static final int INT_MAJOR_BIDI = 2;
    public static final int INT_MAJOR_ASCII = 3;
    public static final int INT_MAJOR_H_ANSI = 4;
    public static final int INT_MINOR_EAST_ASIA = 5;
    public static final int INT_MINOR_BIDI = 6;
    public static final int INT_MINOR_ASCII = 7;
    public static final int INT_MINOR_H_ANSI = 8;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STTheme newValue(final Object o) {
            return (STTheme)STTheme.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STTheme.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STTheme newInstance() {
            return (STTheme)getTypeLoader().newInstance(STTheme.type, (XmlOptions)null);
        }
        
        public static STTheme newInstance(final XmlOptions xmlOptions) {
            return (STTheme)getTypeLoader().newInstance(STTheme.type, xmlOptions);
        }
        
        public static STTheme parse(final String s) throws XmlException {
            return (STTheme)getTypeLoader().parse(s, STTheme.type, (XmlOptions)null);
        }
        
        public static STTheme parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STTheme)getTypeLoader().parse(s, STTheme.type, xmlOptions);
        }
        
        public static STTheme parse(final File file) throws XmlException, IOException {
            return (STTheme)getTypeLoader().parse(file, STTheme.type, (XmlOptions)null);
        }
        
        public static STTheme parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTheme)getTypeLoader().parse(file, STTheme.type, xmlOptions);
        }
        
        public static STTheme parse(final URL url) throws XmlException, IOException {
            return (STTheme)getTypeLoader().parse(url, STTheme.type, (XmlOptions)null);
        }
        
        public static STTheme parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTheme)getTypeLoader().parse(url, STTheme.type, xmlOptions);
        }
        
        public static STTheme parse(final InputStream inputStream) throws XmlException, IOException {
            return (STTheme)getTypeLoader().parse(inputStream, STTheme.type, (XmlOptions)null);
        }
        
        public static STTheme parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTheme)getTypeLoader().parse(inputStream, STTheme.type, xmlOptions);
        }
        
        public static STTheme parse(final Reader reader) throws XmlException, IOException {
            return (STTheme)getTypeLoader().parse(reader, STTheme.type, (XmlOptions)null);
        }
        
        public static STTheme parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STTheme)getTypeLoader().parse(reader, STTheme.type, xmlOptions);
        }
        
        public static STTheme parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STTheme)getTypeLoader().parse(xmlStreamReader, STTheme.type, (XmlOptions)null);
        }
        
        public static STTheme parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STTheme)getTypeLoader().parse(xmlStreamReader, STTheme.type, xmlOptions);
        }
        
        public static STTheme parse(final Node node) throws XmlException {
            return (STTheme)getTypeLoader().parse(node, STTheme.type, (XmlOptions)null);
        }
        
        public static STTheme parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STTheme)getTypeLoader().parse(node, STTheme.type, xmlOptions);
        }
        
        @Deprecated
        public static STTheme parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STTheme)getTypeLoader().parse(xmlInputStream, STTheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STTheme parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STTheme)getTypeLoader().parse(xmlInputStream, STTheme.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTheme.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STTheme.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_MAJOR_EAST_ASIA = 1;
        static final int INT_MAJOR_BIDI = 2;
        static final int INT_MAJOR_ASCII = 3;
        static final int INT_MAJOR_H_ANSI = 4;
        static final int INT_MINOR_EAST_ASIA = 5;
        static final int INT_MINOR_BIDI = 6;
        static final int INT_MINOR_ASCII = 7;
        static final int INT_MINOR_H_ANSI = 8;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("majorEastAsia", 1), new Enum("majorBidi", 2), new Enum("majorAscii", 3), new Enum("majorHAnsi", 4), new Enum("minorEastAsia", 5), new Enum("minorBidi", 6), new Enum("minorAscii", 7), new Enum("minorHAnsi", 8) });
        }
    }
}
