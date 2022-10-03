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

public interface STIconSetType extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STIconSetType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("sticonsettype6112type");
    public static final Enum X_3_ARROWS = Enum.forString("3Arrows");
    public static final Enum X_3_ARROWS_GRAY = Enum.forString("3ArrowsGray");
    public static final Enum X_3_FLAGS = Enum.forString("3Flags");
    public static final Enum X_3_TRAFFIC_LIGHTS_1 = Enum.forString("3TrafficLights1");
    public static final Enum X_3_TRAFFIC_LIGHTS_2 = Enum.forString("3TrafficLights2");
    public static final Enum X_3_SIGNS = Enum.forString("3Signs");
    public static final Enum X_3_SYMBOLS = Enum.forString("3Symbols");
    public static final Enum X_3_SYMBOLS_2 = Enum.forString("3Symbols2");
    public static final Enum X_4_ARROWS = Enum.forString("4Arrows");
    public static final Enum X_4_ARROWS_GRAY = Enum.forString("4ArrowsGray");
    public static final Enum X_4_RED_TO_BLACK = Enum.forString("4RedToBlack");
    public static final Enum X_4_RATING = Enum.forString("4Rating");
    public static final Enum X_4_TRAFFIC_LIGHTS = Enum.forString("4TrafficLights");
    public static final Enum X_5_ARROWS = Enum.forString("5Arrows");
    public static final Enum X_5_ARROWS_GRAY = Enum.forString("5ArrowsGray");
    public static final Enum X_5_RATING = Enum.forString("5Rating");
    public static final Enum X_5_QUARTERS = Enum.forString("5Quarters");
    public static final int INT_X_3_ARROWS = 1;
    public static final int INT_X_3_ARROWS_GRAY = 2;
    public static final int INT_X_3_FLAGS = 3;
    public static final int INT_X_3_TRAFFIC_LIGHTS_1 = 4;
    public static final int INT_X_3_TRAFFIC_LIGHTS_2 = 5;
    public static final int INT_X_3_SIGNS = 6;
    public static final int INT_X_3_SYMBOLS = 7;
    public static final int INT_X_3_SYMBOLS_2 = 8;
    public static final int INT_X_4_ARROWS = 9;
    public static final int INT_X_4_ARROWS_GRAY = 10;
    public static final int INT_X_4_RED_TO_BLACK = 11;
    public static final int INT_X_4_RATING = 12;
    public static final int INT_X_4_TRAFFIC_LIGHTS = 13;
    public static final int INT_X_5_ARROWS = 14;
    public static final int INT_X_5_ARROWS_GRAY = 15;
    public static final int INT_X_5_RATING = 16;
    public static final int INT_X_5_QUARTERS = 17;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STIconSetType newValue(final Object o) {
            return (STIconSetType)STIconSetType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STIconSetType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STIconSetType newInstance() {
            return (STIconSetType)getTypeLoader().newInstance(STIconSetType.type, (XmlOptions)null);
        }
        
        public static STIconSetType newInstance(final XmlOptions xmlOptions) {
            return (STIconSetType)getTypeLoader().newInstance(STIconSetType.type, xmlOptions);
        }
        
        public static STIconSetType parse(final String s) throws XmlException {
            return (STIconSetType)getTypeLoader().parse(s, STIconSetType.type, (XmlOptions)null);
        }
        
        public static STIconSetType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STIconSetType)getTypeLoader().parse(s, STIconSetType.type, xmlOptions);
        }
        
        public static STIconSetType parse(final File file) throws XmlException, IOException {
            return (STIconSetType)getTypeLoader().parse(file, STIconSetType.type, (XmlOptions)null);
        }
        
        public static STIconSetType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STIconSetType)getTypeLoader().parse(file, STIconSetType.type, xmlOptions);
        }
        
        public static STIconSetType parse(final URL url) throws XmlException, IOException {
            return (STIconSetType)getTypeLoader().parse(url, STIconSetType.type, (XmlOptions)null);
        }
        
        public static STIconSetType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STIconSetType)getTypeLoader().parse(url, STIconSetType.type, xmlOptions);
        }
        
        public static STIconSetType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STIconSetType)getTypeLoader().parse(inputStream, STIconSetType.type, (XmlOptions)null);
        }
        
        public static STIconSetType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STIconSetType)getTypeLoader().parse(inputStream, STIconSetType.type, xmlOptions);
        }
        
        public static STIconSetType parse(final Reader reader) throws XmlException, IOException {
            return (STIconSetType)getTypeLoader().parse(reader, STIconSetType.type, (XmlOptions)null);
        }
        
        public static STIconSetType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STIconSetType)getTypeLoader().parse(reader, STIconSetType.type, xmlOptions);
        }
        
        public static STIconSetType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STIconSetType)getTypeLoader().parse(xmlStreamReader, STIconSetType.type, (XmlOptions)null);
        }
        
        public static STIconSetType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STIconSetType)getTypeLoader().parse(xmlStreamReader, STIconSetType.type, xmlOptions);
        }
        
        public static STIconSetType parse(final Node node) throws XmlException {
            return (STIconSetType)getTypeLoader().parse(node, STIconSetType.type, (XmlOptions)null);
        }
        
        public static STIconSetType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STIconSetType)getTypeLoader().parse(node, STIconSetType.type, xmlOptions);
        }
        
        @Deprecated
        public static STIconSetType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STIconSetType)getTypeLoader().parse(xmlInputStream, STIconSetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STIconSetType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STIconSetType)getTypeLoader().parse(xmlInputStream, STIconSetType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STIconSetType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STIconSetType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_X_3_ARROWS = 1;
        static final int INT_X_3_ARROWS_GRAY = 2;
        static final int INT_X_3_FLAGS = 3;
        static final int INT_X_3_TRAFFIC_LIGHTS_1 = 4;
        static final int INT_X_3_TRAFFIC_LIGHTS_2 = 5;
        static final int INT_X_3_SIGNS = 6;
        static final int INT_X_3_SYMBOLS = 7;
        static final int INT_X_3_SYMBOLS_2 = 8;
        static final int INT_X_4_ARROWS = 9;
        static final int INT_X_4_ARROWS_GRAY = 10;
        static final int INT_X_4_RED_TO_BLACK = 11;
        static final int INT_X_4_RATING = 12;
        static final int INT_X_4_TRAFFIC_LIGHTS = 13;
        static final int INT_X_5_ARROWS = 14;
        static final int INT_X_5_ARROWS_GRAY = 15;
        static final int INT_X_5_RATING = 16;
        static final int INT_X_5_QUARTERS = 17;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("3Arrows", 1), new Enum("3ArrowsGray", 2), new Enum("3Flags", 3), new Enum("3TrafficLights1", 4), new Enum("3TrafficLights2", 5), new Enum("3Signs", 6), new Enum("3Symbols", 7), new Enum("3Symbols2", 8), new Enum("4Arrows", 9), new Enum("4ArrowsGray", 10), new Enum("4RedToBlack", 11), new Enum("4Rating", 12), new Enum("4TrafficLights", 13), new Enum("5Arrows", 14), new Enum("5ArrowsGray", 15), new Enum("5Rating", 16), new Enum("5Quarters", 17) });
        }
    }
}
