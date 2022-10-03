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

public interface STPaneState extends XmlString
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPaneState.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stpanestateae58type");
    public static final Enum SPLIT = Enum.forString("split");
    public static final Enum FROZEN = Enum.forString("frozen");
    public static final Enum FROZEN_SPLIT = Enum.forString("frozenSplit");
    public static final int INT_SPLIT = 1;
    public static final int INT_FROZEN = 2;
    public static final int INT_FROZEN_SPLIT = 3;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPaneState newValue(final Object o) {
            return (STPaneState)STPaneState.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPaneState.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPaneState newInstance() {
            return (STPaneState)getTypeLoader().newInstance(STPaneState.type, (XmlOptions)null);
        }
        
        public static STPaneState newInstance(final XmlOptions xmlOptions) {
            return (STPaneState)getTypeLoader().newInstance(STPaneState.type, xmlOptions);
        }
        
        public static STPaneState parse(final String s) throws XmlException {
            return (STPaneState)getTypeLoader().parse(s, STPaneState.type, (XmlOptions)null);
        }
        
        public static STPaneState parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPaneState)getTypeLoader().parse(s, STPaneState.type, xmlOptions);
        }
        
        public static STPaneState parse(final File file) throws XmlException, IOException {
            return (STPaneState)getTypeLoader().parse(file, STPaneState.type, (XmlOptions)null);
        }
        
        public static STPaneState parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPaneState)getTypeLoader().parse(file, STPaneState.type, xmlOptions);
        }
        
        public static STPaneState parse(final URL url) throws XmlException, IOException {
            return (STPaneState)getTypeLoader().parse(url, STPaneState.type, (XmlOptions)null);
        }
        
        public static STPaneState parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPaneState)getTypeLoader().parse(url, STPaneState.type, xmlOptions);
        }
        
        public static STPaneState parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPaneState)getTypeLoader().parse(inputStream, STPaneState.type, (XmlOptions)null);
        }
        
        public static STPaneState parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPaneState)getTypeLoader().parse(inputStream, STPaneState.type, xmlOptions);
        }
        
        public static STPaneState parse(final Reader reader) throws XmlException, IOException {
            return (STPaneState)getTypeLoader().parse(reader, STPaneState.type, (XmlOptions)null);
        }
        
        public static STPaneState parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPaneState)getTypeLoader().parse(reader, STPaneState.type, xmlOptions);
        }
        
        public static STPaneState parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPaneState)getTypeLoader().parse(xmlStreamReader, STPaneState.type, (XmlOptions)null);
        }
        
        public static STPaneState parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPaneState)getTypeLoader().parse(xmlStreamReader, STPaneState.type, xmlOptions);
        }
        
        public static STPaneState parse(final Node node) throws XmlException {
            return (STPaneState)getTypeLoader().parse(node, STPaneState.type, (XmlOptions)null);
        }
        
        public static STPaneState parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPaneState)getTypeLoader().parse(node, STPaneState.type, xmlOptions);
        }
        
        @Deprecated
        public static STPaneState parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPaneState)getTypeLoader().parse(xmlInputStream, STPaneState.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPaneState parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPaneState)getTypeLoader().parse(xmlInputStream, STPaneState.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPaneState.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPaneState.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_SPLIT = 1;
        static final int INT_FROZEN = 2;
        static final int INT_FROZEN_SPLIT = 3;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("split", 1), new Enum("frozen", 2), new Enum("frozenSplit", 3) });
        }
    }
}
