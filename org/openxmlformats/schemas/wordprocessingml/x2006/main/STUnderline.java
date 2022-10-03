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

public interface STUnderline extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STUnderline.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stunderlinef416type");
    public static final Enum SINGLE = Enum.forString("single");
    public static final Enum WORDS = Enum.forString("words");
    public static final Enum DOUBLE = Enum.forString("double");
    public static final Enum THICK = Enum.forString("thick");
    public static final Enum DOTTED = Enum.forString("dotted");
    public static final Enum DOTTED_HEAVY = Enum.forString("dottedHeavy");
    public static final Enum DASH = Enum.forString("dash");
    public static final Enum DASHED_HEAVY = Enum.forString("dashedHeavy");
    public static final Enum DASH_LONG = Enum.forString("dashLong");
    public static final Enum DASH_LONG_HEAVY = Enum.forString("dashLongHeavy");
    public static final Enum DOT_DASH = Enum.forString("dotDash");
    public static final Enum DASH_DOT_HEAVY = Enum.forString("dashDotHeavy");
    public static final Enum DOT_DOT_DASH = Enum.forString("dotDotDash");
    public static final Enum DASH_DOT_DOT_HEAVY = Enum.forString("dashDotDotHeavy");
    public static final Enum WAVE = Enum.forString("wave");
    public static final Enum WAVY_HEAVY = Enum.forString("wavyHeavy");
    public static final Enum WAVY_DOUBLE = Enum.forString("wavyDouble");
    public static final Enum NONE = Enum.forString("none");
    public static final int INT_SINGLE = 1;
    public static final int INT_WORDS = 2;
    public static final int INT_DOUBLE = 3;
    public static final int INT_THICK = 4;
    public static final int INT_DOTTED = 5;
    public static final int INT_DOTTED_HEAVY = 6;
    public static final int INT_DASH = 7;
    public static final int INT_DASHED_HEAVY = 8;
    public static final int INT_DASH_LONG = 9;
    public static final int INT_DASH_LONG_HEAVY = 10;
    public static final int INT_DOT_DASH = 11;
    public static final int INT_DASH_DOT_HEAVY = 12;
    public static final int INT_DOT_DOT_DASH = 13;
    public static final int INT_DASH_DOT_DOT_HEAVY = 14;
    public static final int INT_WAVE = 15;
    public static final int INT_WAVY_HEAVY = 16;
    public static final int INT_WAVY_DOUBLE = 17;
    public static final int INT_NONE = 18;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STUnderline newValue(final Object o) {
            return (STUnderline)STUnderline.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STUnderline.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STUnderline newInstance() {
            return (STUnderline)getTypeLoader().newInstance(STUnderline.type, (XmlOptions)null);
        }
        
        public static STUnderline newInstance(final XmlOptions xmlOptions) {
            return (STUnderline)getTypeLoader().newInstance(STUnderline.type, xmlOptions);
        }
        
        public static STUnderline parse(final String s) throws XmlException {
            return (STUnderline)getTypeLoader().parse(s, STUnderline.type, (XmlOptions)null);
        }
        
        public static STUnderline parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STUnderline)getTypeLoader().parse(s, STUnderline.type, xmlOptions);
        }
        
        public static STUnderline parse(final File file) throws XmlException, IOException {
            return (STUnderline)getTypeLoader().parse(file, STUnderline.type, (XmlOptions)null);
        }
        
        public static STUnderline parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnderline)getTypeLoader().parse(file, STUnderline.type, xmlOptions);
        }
        
        public static STUnderline parse(final URL url) throws XmlException, IOException {
            return (STUnderline)getTypeLoader().parse(url, STUnderline.type, (XmlOptions)null);
        }
        
        public static STUnderline parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnderline)getTypeLoader().parse(url, STUnderline.type, xmlOptions);
        }
        
        public static STUnderline parse(final InputStream inputStream) throws XmlException, IOException {
            return (STUnderline)getTypeLoader().parse(inputStream, STUnderline.type, (XmlOptions)null);
        }
        
        public static STUnderline parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnderline)getTypeLoader().parse(inputStream, STUnderline.type, xmlOptions);
        }
        
        public static STUnderline parse(final Reader reader) throws XmlException, IOException {
            return (STUnderline)getTypeLoader().parse(reader, STUnderline.type, (XmlOptions)null);
        }
        
        public static STUnderline parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STUnderline)getTypeLoader().parse(reader, STUnderline.type, xmlOptions);
        }
        
        public static STUnderline parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STUnderline)getTypeLoader().parse(xmlStreamReader, STUnderline.type, (XmlOptions)null);
        }
        
        public static STUnderline parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STUnderline)getTypeLoader().parse(xmlStreamReader, STUnderline.type, xmlOptions);
        }
        
        public static STUnderline parse(final Node node) throws XmlException {
            return (STUnderline)getTypeLoader().parse(node, STUnderline.type, (XmlOptions)null);
        }
        
        public static STUnderline parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STUnderline)getTypeLoader().parse(node, STUnderline.type, xmlOptions);
        }
        
        @Deprecated
        public static STUnderline parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STUnderline)getTypeLoader().parse(xmlInputStream, STUnderline.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STUnderline parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STUnderline)getTypeLoader().parse(xmlInputStream, STUnderline.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnderline.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STUnderline.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SINGLE = 1;
        static final int INT_WORDS = 2;
        static final int INT_DOUBLE = 3;
        static final int INT_THICK = 4;
        static final int INT_DOTTED = 5;
        static final int INT_DOTTED_HEAVY = 6;
        static final int INT_DASH = 7;
        static final int INT_DASHED_HEAVY = 8;
        static final int INT_DASH_LONG = 9;
        static final int INT_DASH_LONG_HEAVY = 10;
        static final int INT_DOT_DASH = 11;
        static final int INT_DASH_DOT_HEAVY = 12;
        static final int INT_DOT_DOT_DASH = 13;
        static final int INT_DASH_DOT_DOT_HEAVY = 14;
        static final int INT_WAVE = 15;
        static final int INT_WAVY_HEAVY = 16;
        static final int INT_WAVY_DOUBLE = 17;
        static final int INT_NONE = 18;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("single", 1), new Enum("words", 2), new Enum("double", 3), new Enum("thick", 4), new Enum("dotted", 5), new Enum("dottedHeavy", 6), new Enum("dash", 7), new Enum("dashedHeavy", 8), new Enum("dashLong", 9), new Enum("dashLongHeavy", 10), new Enum("dotDash", 11), new Enum("dashDotHeavy", 12), new Enum("dotDotDash", 13), new Enum("dashDotDotHeavy", 14), new Enum("wave", 15), new Enum("wavyHeavy", 16), new Enum("wavyDouble", 17), new Enum("none", 18) });
        }
    }
}
