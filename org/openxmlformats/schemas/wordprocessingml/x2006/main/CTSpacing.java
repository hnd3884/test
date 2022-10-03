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
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTSpacing extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSpacing.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctspacingff2ftype");
    
    BigInteger getBefore();
    
    STTwipsMeasure xgetBefore();
    
    boolean isSetBefore();
    
    void setBefore(final BigInteger p0);
    
    void xsetBefore(final STTwipsMeasure p0);
    
    void unsetBefore();
    
    BigInteger getBeforeLines();
    
    STDecimalNumber xgetBeforeLines();
    
    boolean isSetBeforeLines();
    
    void setBeforeLines(final BigInteger p0);
    
    void xsetBeforeLines(final STDecimalNumber p0);
    
    void unsetBeforeLines();
    
    STOnOff.Enum getBeforeAutospacing();
    
    STOnOff xgetBeforeAutospacing();
    
    boolean isSetBeforeAutospacing();
    
    void setBeforeAutospacing(final STOnOff.Enum p0);
    
    void xsetBeforeAutospacing(final STOnOff p0);
    
    void unsetBeforeAutospacing();
    
    BigInteger getAfter();
    
    STTwipsMeasure xgetAfter();
    
    boolean isSetAfter();
    
    void setAfter(final BigInteger p0);
    
    void xsetAfter(final STTwipsMeasure p0);
    
    void unsetAfter();
    
    BigInteger getAfterLines();
    
    STDecimalNumber xgetAfterLines();
    
    boolean isSetAfterLines();
    
    void setAfterLines(final BigInteger p0);
    
    void xsetAfterLines(final STDecimalNumber p0);
    
    void unsetAfterLines();
    
    STOnOff.Enum getAfterAutospacing();
    
    STOnOff xgetAfterAutospacing();
    
    boolean isSetAfterAutospacing();
    
    void setAfterAutospacing(final STOnOff.Enum p0);
    
    void xsetAfterAutospacing(final STOnOff p0);
    
    void unsetAfterAutospacing();
    
    BigInteger getLine();
    
    STSignedTwipsMeasure xgetLine();
    
    boolean isSetLine();
    
    void setLine(final BigInteger p0);
    
    void xsetLine(final STSignedTwipsMeasure p0);
    
    void unsetLine();
    
    STLineSpacingRule.Enum getLineRule();
    
    STLineSpacingRule xgetLineRule();
    
    boolean isSetLineRule();
    
    void setLineRule(final STLineSpacingRule.Enum p0);
    
    void xsetLineRule(final STLineSpacingRule p0);
    
    void unsetLineRule();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSpacing.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSpacing newInstance() {
            return (CTSpacing)getTypeLoader().newInstance(CTSpacing.type, (XmlOptions)null);
        }
        
        public static CTSpacing newInstance(final XmlOptions xmlOptions) {
            return (CTSpacing)getTypeLoader().newInstance(CTSpacing.type, xmlOptions);
        }
        
        public static CTSpacing parse(final String s) throws XmlException {
            return (CTSpacing)getTypeLoader().parse(s, CTSpacing.type, (XmlOptions)null);
        }
        
        public static CTSpacing parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSpacing)getTypeLoader().parse(s, CTSpacing.type, xmlOptions);
        }
        
        public static CTSpacing parse(final File file) throws XmlException, IOException {
            return (CTSpacing)getTypeLoader().parse(file, CTSpacing.type, (XmlOptions)null);
        }
        
        public static CTSpacing parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSpacing)getTypeLoader().parse(file, CTSpacing.type, xmlOptions);
        }
        
        public static CTSpacing parse(final URL url) throws XmlException, IOException {
            return (CTSpacing)getTypeLoader().parse(url, CTSpacing.type, (XmlOptions)null);
        }
        
        public static CTSpacing parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSpacing)getTypeLoader().parse(url, CTSpacing.type, xmlOptions);
        }
        
        public static CTSpacing parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSpacing)getTypeLoader().parse(inputStream, CTSpacing.type, (XmlOptions)null);
        }
        
        public static CTSpacing parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSpacing)getTypeLoader().parse(inputStream, CTSpacing.type, xmlOptions);
        }
        
        public static CTSpacing parse(final Reader reader) throws XmlException, IOException {
            return (CTSpacing)getTypeLoader().parse(reader, CTSpacing.type, (XmlOptions)null);
        }
        
        public static CTSpacing parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSpacing)getTypeLoader().parse(reader, CTSpacing.type, xmlOptions);
        }
        
        public static CTSpacing parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSpacing)getTypeLoader().parse(xmlStreamReader, CTSpacing.type, (XmlOptions)null);
        }
        
        public static CTSpacing parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSpacing)getTypeLoader().parse(xmlStreamReader, CTSpacing.type, xmlOptions);
        }
        
        public static CTSpacing parse(final Node node) throws XmlException {
            return (CTSpacing)getTypeLoader().parse(node, CTSpacing.type, (XmlOptions)null);
        }
        
        public static CTSpacing parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSpacing)getTypeLoader().parse(node, CTSpacing.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSpacing parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSpacing)getTypeLoader().parse(xmlInputStream, CTSpacing.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSpacing parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSpacing)getTypeLoader().parse(xmlInputStream, CTSpacing.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSpacing.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSpacing.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
