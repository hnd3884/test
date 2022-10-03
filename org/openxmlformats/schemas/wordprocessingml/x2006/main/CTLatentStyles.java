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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLatentStyles extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLatentStyles.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlatentstyles2e3atype");
    
    List<CTLsdException> getLsdExceptionList();
    
    @Deprecated
    CTLsdException[] getLsdExceptionArray();
    
    CTLsdException getLsdExceptionArray(final int p0);
    
    int sizeOfLsdExceptionArray();
    
    void setLsdExceptionArray(final CTLsdException[] p0);
    
    void setLsdExceptionArray(final int p0, final CTLsdException p1);
    
    CTLsdException insertNewLsdException(final int p0);
    
    CTLsdException addNewLsdException();
    
    void removeLsdException(final int p0);
    
    STOnOff.Enum getDefLockedState();
    
    STOnOff xgetDefLockedState();
    
    boolean isSetDefLockedState();
    
    void setDefLockedState(final STOnOff.Enum p0);
    
    void xsetDefLockedState(final STOnOff p0);
    
    void unsetDefLockedState();
    
    BigInteger getDefUIPriority();
    
    STDecimalNumber xgetDefUIPriority();
    
    boolean isSetDefUIPriority();
    
    void setDefUIPriority(final BigInteger p0);
    
    void xsetDefUIPriority(final STDecimalNumber p0);
    
    void unsetDefUIPriority();
    
    STOnOff.Enum getDefSemiHidden();
    
    STOnOff xgetDefSemiHidden();
    
    boolean isSetDefSemiHidden();
    
    void setDefSemiHidden(final STOnOff.Enum p0);
    
    void xsetDefSemiHidden(final STOnOff p0);
    
    void unsetDefSemiHidden();
    
    STOnOff.Enum getDefUnhideWhenUsed();
    
    STOnOff xgetDefUnhideWhenUsed();
    
    boolean isSetDefUnhideWhenUsed();
    
    void setDefUnhideWhenUsed(final STOnOff.Enum p0);
    
    void xsetDefUnhideWhenUsed(final STOnOff p0);
    
    void unsetDefUnhideWhenUsed();
    
    STOnOff.Enum getDefQFormat();
    
    STOnOff xgetDefQFormat();
    
    boolean isSetDefQFormat();
    
    void setDefQFormat(final STOnOff.Enum p0);
    
    void xsetDefQFormat(final STOnOff p0);
    
    void unsetDefQFormat();
    
    BigInteger getCount();
    
    STDecimalNumber xgetCount();
    
    boolean isSetCount();
    
    void setCount(final BigInteger p0);
    
    void xsetCount(final STDecimalNumber p0);
    
    void unsetCount();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLatentStyles.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLatentStyles newInstance() {
            return (CTLatentStyles)getTypeLoader().newInstance(CTLatentStyles.type, (XmlOptions)null);
        }
        
        public static CTLatentStyles newInstance(final XmlOptions xmlOptions) {
            return (CTLatentStyles)getTypeLoader().newInstance(CTLatentStyles.type, xmlOptions);
        }
        
        public static CTLatentStyles parse(final String s) throws XmlException {
            return (CTLatentStyles)getTypeLoader().parse(s, CTLatentStyles.type, (XmlOptions)null);
        }
        
        public static CTLatentStyles parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLatentStyles)getTypeLoader().parse(s, CTLatentStyles.type, xmlOptions);
        }
        
        public static CTLatentStyles parse(final File file) throws XmlException, IOException {
            return (CTLatentStyles)getTypeLoader().parse(file, CTLatentStyles.type, (XmlOptions)null);
        }
        
        public static CTLatentStyles parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLatentStyles)getTypeLoader().parse(file, CTLatentStyles.type, xmlOptions);
        }
        
        public static CTLatentStyles parse(final URL url) throws XmlException, IOException {
            return (CTLatentStyles)getTypeLoader().parse(url, CTLatentStyles.type, (XmlOptions)null);
        }
        
        public static CTLatentStyles parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLatentStyles)getTypeLoader().parse(url, CTLatentStyles.type, xmlOptions);
        }
        
        public static CTLatentStyles parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLatentStyles)getTypeLoader().parse(inputStream, CTLatentStyles.type, (XmlOptions)null);
        }
        
        public static CTLatentStyles parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLatentStyles)getTypeLoader().parse(inputStream, CTLatentStyles.type, xmlOptions);
        }
        
        public static CTLatentStyles parse(final Reader reader) throws XmlException, IOException {
            return (CTLatentStyles)getTypeLoader().parse(reader, CTLatentStyles.type, (XmlOptions)null);
        }
        
        public static CTLatentStyles parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLatentStyles)getTypeLoader().parse(reader, CTLatentStyles.type, xmlOptions);
        }
        
        public static CTLatentStyles parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLatentStyles)getTypeLoader().parse(xmlStreamReader, CTLatentStyles.type, (XmlOptions)null);
        }
        
        public static CTLatentStyles parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLatentStyles)getTypeLoader().parse(xmlStreamReader, CTLatentStyles.type, xmlOptions);
        }
        
        public static CTLatentStyles parse(final Node node) throws XmlException {
            return (CTLatentStyles)getTypeLoader().parse(node, CTLatentStyles.type, (XmlOptions)null);
        }
        
        public static CTLatentStyles parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLatentStyles)getTypeLoader().parse(node, CTLatentStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLatentStyles parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLatentStyles)getTypeLoader().parse(xmlInputStream, CTLatentStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLatentStyles parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLatentStyles)getTypeLoader().parse(xmlInputStream, CTLatentStyles.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLatentStyles.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLatentStyles.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
