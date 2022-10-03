package com.microsoft.schemas.vml;

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

public interface STExt extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STExt.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stext2fe5type");
    public static final Enum VIEW = Enum.forString("view");
    public static final Enum EDIT = Enum.forString("edit");
    public static final Enum BACKWARD_COMPATIBLE = Enum.forString("backwardCompatible");
    public static final int INT_VIEW = 1;
    public static final int INT_EDIT = 2;
    public static final int INT_BACKWARD_COMPATIBLE = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STExt newValue(final Object o) {
            return (STExt)STExt.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STExt.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STExt newInstance() {
            return (STExt)getTypeLoader().newInstance(STExt.type, (XmlOptions)null);
        }
        
        public static STExt newInstance(final XmlOptions xmlOptions) {
            return (STExt)getTypeLoader().newInstance(STExt.type, xmlOptions);
        }
        
        public static STExt parse(final String s) throws XmlException {
            return (STExt)getTypeLoader().parse(s, STExt.type, (XmlOptions)null);
        }
        
        public static STExt parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STExt)getTypeLoader().parse(s, STExt.type, xmlOptions);
        }
        
        public static STExt parse(final File file) throws XmlException, IOException {
            return (STExt)getTypeLoader().parse(file, STExt.type, (XmlOptions)null);
        }
        
        public static STExt parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STExt)getTypeLoader().parse(file, STExt.type, xmlOptions);
        }
        
        public static STExt parse(final URL url) throws XmlException, IOException {
            return (STExt)getTypeLoader().parse(url, STExt.type, (XmlOptions)null);
        }
        
        public static STExt parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STExt)getTypeLoader().parse(url, STExt.type, xmlOptions);
        }
        
        public static STExt parse(final InputStream inputStream) throws XmlException, IOException {
            return (STExt)getTypeLoader().parse(inputStream, STExt.type, (XmlOptions)null);
        }
        
        public static STExt parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STExt)getTypeLoader().parse(inputStream, STExt.type, xmlOptions);
        }
        
        public static STExt parse(final Reader reader) throws XmlException, IOException {
            return (STExt)getTypeLoader().parse(reader, STExt.type, (XmlOptions)null);
        }
        
        public static STExt parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STExt)getTypeLoader().parse(reader, STExt.type, xmlOptions);
        }
        
        public static STExt parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STExt)getTypeLoader().parse(xmlStreamReader, STExt.type, (XmlOptions)null);
        }
        
        public static STExt parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STExt)getTypeLoader().parse(xmlStreamReader, STExt.type, xmlOptions);
        }
        
        public static STExt parse(final Node node) throws XmlException {
            return (STExt)getTypeLoader().parse(node, STExt.type, (XmlOptions)null);
        }
        
        public static STExt parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STExt)getTypeLoader().parse(node, STExt.type, xmlOptions);
        }
        
        @Deprecated
        public static STExt parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STExt)getTypeLoader().parse(xmlInputStream, STExt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STExt parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STExt)getTypeLoader().parse(xmlInputStream, STExt.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STExt.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STExt.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_VIEW = 1;
        static final int INT_EDIT = 2;
        static final int INT_BACKWARD_COMPATIBLE = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("view", 1), new Enum("edit", 2), new Enum("backwardCompatible", 3) });
        }
    }
}
