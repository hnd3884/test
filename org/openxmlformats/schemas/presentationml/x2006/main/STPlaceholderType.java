package org.openxmlformats.schemas.presentationml.x2006.main;

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

public interface STPlaceholderType extends XmlToken
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(STPlaceholderType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("stplaceholdertypeca72type");
    public static final Enum TITLE = Enum.forString("title");
    public static final Enum BODY = Enum.forString("body");
    public static final Enum CTR_TITLE = Enum.forString("ctrTitle");
    public static final Enum SUB_TITLE = Enum.forString("subTitle");
    public static final Enum DT = Enum.forString("dt");
    public static final Enum SLD_NUM = Enum.forString("sldNum");
    public static final Enum FTR = Enum.forString("ftr");
    public static final Enum HDR = Enum.forString("hdr");
    public static final Enum OBJ = Enum.forString("obj");
    public static final Enum CHART = Enum.forString("chart");
    public static final Enum TBL = Enum.forString("tbl");
    public static final Enum CLIP_ART = Enum.forString("clipArt");
    public static final Enum DGM = Enum.forString("dgm");
    public static final Enum MEDIA = Enum.forString("media");
    public static final Enum SLD_IMG = Enum.forString("sldImg");
    public static final Enum PIC = Enum.forString("pic");
    public static final int INT_TITLE = 1;
    public static final int INT_BODY = 2;
    public static final int INT_CTR_TITLE = 3;
    public static final int INT_SUB_TITLE = 4;
    public static final int INT_DT = 5;
    public static final int INT_SLD_NUM = 6;
    public static final int INT_FTR = 7;
    public static final int INT_HDR = 8;
    public static final int INT_OBJ = 9;
    public static final int INT_CHART = 10;
    public static final int INT_TBL = 11;
    public static final int INT_CLIP_ART = 12;
    public static final int INT_DGM = 13;
    public static final int INT_MEDIA = 14;
    public static final int INT_SLD_IMG = 15;
    public static final int INT_PIC = 16;
    
    StringEnumAbstractBase enumValue();
    
    void set(final StringEnumAbstractBase p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        public static STPlaceholderType newValue(final Object o) {
            return (STPlaceholderType)STPlaceholderType.type.newValue(o);
        }
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(STPlaceholderType.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static STPlaceholderType newInstance() {
            return (STPlaceholderType)getTypeLoader().newInstance(STPlaceholderType.type, (XmlOptions)null);
        }
        
        public static STPlaceholderType newInstance(final XmlOptions xmlOptions) {
            return (STPlaceholderType)getTypeLoader().newInstance(STPlaceholderType.type, xmlOptions);
        }
        
        public static STPlaceholderType parse(final String s) throws XmlException {
            return (STPlaceholderType)getTypeLoader().parse(s, STPlaceholderType.type, (XmlOptions)null);
        }
        
        public static STPlaceholderType parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (STPlaceholderType)getTypeLoader().parse(s, STPlaceholderType.type, xmlOptions);
        }
        
        public static STPlaceholderType parse(final File file) throws XmlException, IOException {
            return (STPlaceholderType)getTypeLoader().parse(file, STPlaceholderType.type, (XmlOptions)null);
        }
        
        public static STPlaceholderType parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPlaceholderType)getTypeLoader().parse(file, STPlaceholderType.type, xmlOptions);
        }
        
        public static STPlaceholderType parse(final URL url) throws XmlException, IOException {
            return (STPlaceholderType)getTypeLoader().parse(url, STPlaceholderType.type, (XmlOptions)null);
        }
        
        public static STPlaceholderType parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPlaceholderType)getTypeLoader().parse(url, STPlaceholderType.type, xmlOptions);
        }
        
        public static STPlaceholderType parse(final InputStream inputStream) throws XmlException, IOException {
            return (STPlaceholderType)getTypeLoader().parse(inputStream, STPlaceholderType.type, (XmlOptions)null);
        }
        
        public static STPlaceholderType parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPlaceholderType)getTypeLoader().parse(inputStream, STPlaceholderType.type, xmlOptions);
        }
        
        public static STPlaceholderType parse(final Reader reader) throws XmlException, IOException {
            return (STPlaceholderType)getTypeLoader().parse(reader, STPlaceholderType.type, (XmlOptions)null);
        }
        
        public static STPlaceholderType parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (STPlaceholderType)getTypeLoader().parse(reader, STPlaceholderType.type, xmlOptions);
        }
        
        public static STPlaceholderType parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (STPlaceholderType)getTypeLoader().parse(xmlStreamReader, STPlaceholderType.type, (XmlOptions)null);
        }
        
        public static STPlaceholderType parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (STPlaceholderType)getTypeLoader().parse(xmlStreamReader, STPlaceholderType.type, xmlOptions);
        }
        
        public static STPlaceholderType parse(final Node node) throws XmlException {
            return (STPlaceholderType)getTypeLoader().parse(node, STPlaceholderType.type, (XmlOptions)null);
        }
        
        public static STPlaceholderType parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (STPlaceholderType)getTypeLoader().parse(node, STPlaceholderType.type, xmlOptions);
        }
        
        @Deprecated
        public static STPlaceholderType parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (STPlaceholderType)getTypeLoader().parse(xmlInputStream, STPlaceholderType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static STPlaceholderType parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (STPlaceholderType)getTypeLoader().parse(xmlInputStream, STPlaceholderType.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPlaceholderType.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, STPlaceholderType.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
    
    public static final class Enum extends StringEnumAbstractBase
    {
        static final int INT_TITLE = 1;
        static final int INT_BODY = 2;
        static final int INT_CTR_TITLE = 3;
        static final int INT_SUB_TITLE = 4;
        static final int INT_DT = 5;
        static final int INT_SLD_NUM = 6;
        static final int INT_FTR = 7;
        static final int INT_HDR = 8;
        static final int INT_OBJ = 9;
        static final int INT_CHART = 10;
        static final int INT_TBL = 11;
        static final int INT_CLIP_ART = 12;
        static final int INT_DGM = 13;
        static final int INT_MEDIA = 14;
        static final int INT_SLD_IMG = 15;
        static final int INT_PIC = 16;
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
            table = new StringEnumAbstractBase.Table((StringEnumAbstractBase[])new Enum[] { new Enum("title", 1), new Enum("body", 2), new Enum("ctrTitle", 3), new Enum("subTitle", 4), new Enum("dt", 5), new Enum("sldNum", 6), new Enum("ftr", 7), new Enum("hdr", 8), new Enum("obj", 9), new Enum("chart", 10), new Enum("tbl", 11), new Enum("clipArt", 12), new Enum("dgm", 13), new Enum("media", 14), new Enum("sldImg", 15), new Enum("pic", 16) });
        }
    }
}
