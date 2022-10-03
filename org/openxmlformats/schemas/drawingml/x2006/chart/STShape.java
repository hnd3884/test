package org.openxmlformats.schemas.drawingml.x2006.chart;

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

public interface STShape extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STShape.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stshapecdf5type");
    public static final Enum CONE = Enum.forString("cone");
    public static final Enum CONE_TO_MAX = Enum.forString("coneToMax");
    public static final Enum BOX = Enum.forString("box");
    public static final Enum CYLINDER = Enum.forString("cylinder");
    public static final Enum PYRAMID = Enum.forString("pyramid");
    public static final Enum PYRAMID_TO_MAX = Enum.forString("pyramidToMax");
    public static final int INT_CONE = 1;
    public static final int INT_CONE_TO_MAX = 2;
    public static final int INT_BOX = 3;
    public static final int INT_CYLINDER = 4;
    public static final int INT_PYRAMID = 5;
    public static final int INT_PYRAMID_TO_MAX = 6;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STShape newValue(final Object o) {
            return (STShape)STShape.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STShape.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STShape newInstance() {
            return (STShape)getTypeLoader().newInstance(STShape.type, (XmlOptions)null);
        }
        
        public static STShape newInstance(final XmlOptions xmlOptions) {
            return (STShape)getTypeLoader().newInstance(STShape.type, xmlOptions);
        }
        
        public static STShape parse(final String s) throws XmlException {
            return (STShape)getTypeLoader().parse(s, STShape.type, (XmlOptions)null);
        }
        
        public static STShape parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STShape)getTypeLoader().parse(s, STShape.type, xmlOptions);
        }
        
        public static STShape parse(final File file) throws XmlException, IOException {
            return (STShape)getTypeLoader().parse(file, STShape.type, (XmlOptions)null);
        }
        
        public static STShape parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STShape)getTypeLoader().parse(file, STShape.type, xmlOptions);
        }
        
        public static STShape parse(final URL url) throws XmlException, IOException {
            return (STShape)getTypeLoader().parse(url, STShape.type, (XmlOptions)null);
        }
        
        public static STShape parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STShape)getTypeLoader().parse(url, STShape.type, xmlOptions);
        }
        
        public static STShape parse(final InputStream inputStream) throws XmlException, IOException {
            return (STShape)getTypeLoader().parse(inputStream, STShape.type, (XmlOptions)null);
        }
        
        public static STShape parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STShape)getTypeLoader().parse(inputStream, STShape.type, xmlOptions);
        }
        
        public static STShape parse(final Reader reader) throws XmlException, IOException {
            return (STShape)getTypeLoader().parse(reader, STShape.type, (XmlOptions)null);
        }
        
        public static STShape parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STShape)getTypeLoader().parse(reader, STShape.type, xmlOptions);
        }
        
        public static STShape parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STShape)getTypeLoader().parse(xmlStreamReader, STShape.type, (XmlOptions)null);
        }
        
        public static STShape parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STShape)getTypeLoader().parse(xmlStreamReader, STShape.type, xmlOptions);
        }
        
        public static STShape parse(final Node node) throws XmlException {
            return (STShape)getTypeLoader().parse(node, STShape.type, (XmlOptions)null);
        }
        
        public static STShape parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STShape)getTypeLoader().parse(node, STShape.type, xmlOptions);
        }
        
        @Deprecated
        public static STShape parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STShape)getTypeLoader().parse(xmlInputStream, STShape.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STShape parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STShape)getTypeLoader().parse(xmlInputStream, STShape.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STShape.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STShape.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_CONE = 1;
        static final int INT_CONE_TO_MAX = 2;
        static final int INT_BOX = 3;
        static final int INT_CYLINDER = 4;
        static final int INT_PYRAMID = 5;
        static final int INT_PYRAMID_TO_MAX = 6;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("cone", 1), new Enum("coneToMax", 2), new Enum("box", 3), new Enum("cylinder", 4), new Enum("pyramid", 5), new Enum("pyramidToMax", 6) });
        }
    }
}
