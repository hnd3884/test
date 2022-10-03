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

public interface CTLsdException extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLsdException.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlsdexceptiona296type");
    
    String getName();
    
    STString xgetName();
    
    void setName(final String p0);
    
    void xsetName(final STString p0);
    
    STOnOff.Enum getLocked();
    
    STOnOff xgetLocked();
    
    boolean isSetLocked();
    
    void setLocked(final STOnOff.Enum p0);
    
    void xsetLocked(final STOnOff p0);
    
    void unsetLocked();
    
    BigInteger getUiPriority();
    
    STDecimalNumber xgetUiPriority();
    
    boolean isSetUiPriority();
    
    void setUiPriority(final BigInteger p0);
    
    void xsetUiPriority(final STDecimalNumber p0);
    
    void unsetUiPriority();
    
    STOnOff.Enum getSemiHidden();
    
    STOnOff xgetSemiHidden();
    
    boolean isSetSemiHidden();
    
    void setSemiHidden(final STOnOff.Enum p0);
    
    void xsetSemiHidden(final STOnOff p0);
    
    void unsetSemiHidden();
    
    STOnOff.Enum getUnhideWhenUsed();
    
    STOnOff xgetUnhideWhenUsed();
    
    boolean isSetUnhideWhenUsed();
    
    void setUnhideWhenUsed(final STOnOff.Enum p0);
    
    void xsetUnhideWhenUsed(final STOnOff p0);
    
    void unsetUnhideWhenUsed();
    
    STOnOff.Enum getQFormat();
    
    STOnOff xgetQFormat();
    
    boolean isSetQFormat();
    
    void setQFormat(final STOnOff.Enum p0);
    
    void xsetQFormat(final STOnOff p0);
    
    void unsetQFormat();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLsdException.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLsdException newInstance() {
            return (CTLsdException)getTypeLoader().newInstance(CTLsdException.type, (XmlOptions)null);
        }
        
        public static CTLsdException newInstance(final XmlOptions xmlOptions) {
            return (CTLsdException)getTypeLoader().newInstance(CTLsdException.type, xmlOptions);
        }
        
        public static CTLsdException parse(final String s) throws XmlException {
            return (CTLsdException)getTypeLoader().parse(s, CTLsdException.type, (XmlOptions)null);
        }
        
        public static CTLsdException parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLsdException)getTypeLoader().parse(s, CTLsdException.type, xmlOptions);
        }
        
        public static CTLsdException parse(final File file) throws XmlException, IOException {
            return (CTLsdException)getTypeLoader().parse(file, CTLsdException.type, (XmlOptions)null);
        }
        
        public static CTLsdException parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLsdException)getTypeLoader().parse(file, CTLsdException.type, xmlOptions);
        }
        
        public static CTLsdException parse(final URL url) throws XmlException, IOException {
            return (CTLsdException)getTypeLoader().parse(url, CTLsdException.type, (XmlOptions)null);
        }
        
        public static CTLsdException parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLsdException)getTypeLoader().parse(url, CTLsdException.type, xmlOptions);
        }
        
        public static CTLsdException parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLsdException)getTypeLoader().parse(inputStream, CTLsdException.type, (XmlOptions)null);
        }
        
        public static CTLsdException parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLsdException)getTypeLoader().parse(inputStream, CTLsdException.type, xmlOptions);
        }
        
        public static CTLsdException parse(final Reader reader) throws XmlException, IOException {
            return (CTLsdException)getTypeLoader().parse(reader, CTLsdException.type, (XmlOptions)null);
        }
        
        public static CTLsdException parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLsdException)getTypeLoader().parse(reader, CTLsdException.type, xmlOptions);
        }
        
        public static CTLsdException parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLsdException)getTypeLoader().parse(xmlStreamReader, CTLsdException.type, (XmlOptions)null);
        }
        
        public static CTLsdException parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLsdException)getTypeLoader().parse(xmlStreamReader, CTLsdException.type, xmlOptions);
        }
        
        public static CTLsdException parse(final Node node) throws XmlException {
            return (CTLsdException)getTypeLoader().parse(node, CTLsdException.type, (XmlOptions)null);
        }
        
        public static CTLsdException parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLsdException)getTypeLoader().parse(node, CTLsdException.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLsdException parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLsdException)getTypeLoader().parse(xmlInputStream, CTLsdException.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLsdException parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLsdException)getTypeLoader().parse(xmlInputStream, CTLsdException.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLsdException.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLsdException.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
