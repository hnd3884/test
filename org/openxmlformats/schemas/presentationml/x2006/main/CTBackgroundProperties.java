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
import org.apache.xmlbeans.XmlBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectContainer;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEffectList;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGroupFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPatternFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTBlipFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSolidColorFillProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNoFillProperties;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTBackgroundProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBackgroundProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbackgroundpropertiesb184type");
    
    CTNoFillProperties getNoFill();
    
    boolean isSetNoFill();
    
    void setNoFill(final CTNoFillProperties p0);
    
    CTNoFillProperties addNewNoFill();
    
    void unsetNoFill();
    
    CTSolidColorFillProperties getSolidFill();
    
    boolean isSetSolidFill();
    
    void setSolidFill(final CTSolidColorFillProperties p0);
    
    CTSolidColorFillProperties addNewSolidFill();
    
    void unsetSolidFill();
    
    CTGradientFillProperties getGradFill();
    
    boolean isSetGradFill();
    
    void setGradFill(final CTGradientFillProperties p0);
    
    CTGradientFillProperties addNewGradFill();
    
    void unsetGradFill();
    
    CTBlipFillProperties getBlipFill();
    
    boolean isSetBlipFill();
    
    void setBlipFill(final CTBlipFillProperties p0);
    
    CTBlipFillProperties addNewBlipFill();
    
    void unsetBlipFill();
    
    CTPatternFillProperties getPattFill();
    
    boolean isSetPattFill();
    
    void setPattFill(final CTPatternFillProperties p0);
    
    CTPatternFillProperties addNewPattFill();
    
    void unsetPattFill();
    
    CTGroupFillProperties getGrpFill();
    
    boolean isSetGrpFill();
    
    void setGrpFill(final CTGroupFillProperties p0);
    
    CTGroupFillProperties addNewGrpFill();
    
    void unsetGrpFill();
    
    CTEffectList getEffectLst();
    
    boolean isSetEffectLst();
    
    void setEffectLst(final CTEffectList p0);
    
    CTEffectList addNewEffectLst();
    
    void unsetEffectLst();
    
    CTEffectContainer getEffectDag();
    
    boolean isSetEffectDag();
    
    void setEffectDag(final CTEffectContainer p0);
    
    CTEffectContainer addNewEffectDag();
    
    void unsetEffectDag();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    boolean getShadeToTitle();
    
    XmlBoolean xgetShadeToTitle();
    
    boolean isSetShadeToTitle();
    
    void setShadeToTitle(final boolean p0);
    
    void xsetShadeToTitle(final XmlBoolean p0);
    
    void unsetShadeToTitle();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBackgroundProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBackgroundProperties newInstance() {
            return (CTBackgroundProperties)getTypeLoader().newInstance(CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        public static CTBackgroundProperties newInstance(final XmlOptions xmlOptions) {
            return (CTBackgroundProperties)getTypeLoader().newInstance(CTBackgroundProperties.type, xmlOptions);
        }
        
        public static CTBackgroundProperties parse(final String s) throws XmlException {
            return (CTBackgroundProperties)getTypeLoader().parse(s, CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        public static CTBackgroundProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBackgroundProperties)getTypeLoader().parse(s, CTBackgroundProperties.type, xmlOptions);
        }
        
        public static CTBackgroundProperties parse(final File file) throws XmlException, IOException {
            return (CTBackgroundProperties)getTypeLoader().parse(file, CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        public static CTBackgroundProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackgroundProperties)getTypeLoader().parse(file, CTBackgroundProperties.type, xmlOptions);
        }
        
        public static CTBackgroundProperties parse(final URL url) throws XmlException, IOException {
            return (CTBackgroundProperties)getTypeLoader().parse(url, CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        public static CTBackgroundProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackgroundProperties)getTypeLoader().parse(url, CTBackgroundProperties.type, xmlOptions);
        }
        
        public static CTBackgroundProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBackgroundProperties)getTypeLoader().parse(inputStream, CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        public static CTBackgroundProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackgroundProperties)getTypeLoader().parse(inputStream, CTBackgroundProperties.type, xmlOptions);
        }
        
        public static CTBackgroundProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTBackgroundProperties)getTypeLoader().parse(reader, CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        public static CTBackgroundProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackgroundProperties)getTypeLoader().parse(reader, CTBackgroundProperties.type, xmlOptions);
        }
        
        public static CTBackgroundProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBackgroundProperties)getTypeLoader().parse(xmlStreamReader, CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        public static CTBackgroundProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBackgroundProperties)getTypeLoader().parse(xmlStreamReader, CTBackgroundProperties.type, xmlOptions);
        }
        
        public static CTBackgroundProperties parse(final Node node) throws XmlException {
            return (CTBackgroundProperties)getTypeLoader().parse(node, CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        public static CTBackgroundProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBackgroundProperties)getTypeLoader().parse(node, CTBackgroundProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBackgroundProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBackgroundProperties)getTypeLoader().parse(xmlInputStream, CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBackgroundProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBackgroundProperties)getTypeLoader().parse(xmlInputStream, CTBackgroundProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBackgroundProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBackgroundProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
