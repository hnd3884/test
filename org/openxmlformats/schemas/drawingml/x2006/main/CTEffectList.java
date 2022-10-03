package org.openxmlformats.schemas.drawingml.x2006.main;

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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTEffectList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTEffectList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cteffectlist6featype");
    
    CTBlurEffect getBlur();
    
    boolean isSetBlur();
    
    void setBlur(final CTBlurEffect p0);
    
    CTBlurEffect addNewBlur();
    
    void unsetBlur();
    
    CTFillOverlayEffect getFillOverlay();
    
    boolean isSetFillOverlay();
    
    void setFillOverlay(final CTFillOverlayEffect p0);
    
    CTFillOverlayEffect addNewFillOverlay();
    
    void unsetFillOverlay();
    
    CTGlowEffect getGlow();
    
    boolean isSetGlow();
    
    void setGlow(final CTGlowEffect p0);
    
    CTGlowEffect addNewGlow();
    
    void unsetGlow();
    
    CTInnerShadowEffect getInnerShdw();
    
    boolean isSetInnerShdw();
    
    void setInnerShdw(final CTInnerShadowEffect p0);
    
    CTInnerShadowEffect addNewInnerShdw();
    
    void unsetInnerShdw();
    
    CTOuterShadowEffect getOuterShdw();
    
    boolean isSetOuterShdw();
    
    void setOuterShdw(final CTOuterShadowEffect p0);
    
    CTOuterShadowEffect addNewOuterShdw();
    
    void unsetOuterShdw();
    
    CTPresetShadowEffect getPrstShdw();
    
    boolean isSetPrstShdw();
    
    void setPrstShdw(final CTPresetShadowEffect p0);
    
    CTPresetShadowEffect addNewPrstShdw();
    
    void unsetPrstShdw();
    
    CTReflectionEffect getReflection();
    
    boolean isSetReflection();
    
    void setReflection(final CTReflectionEffect p0);
    
    CTReflectionEffect addNewReflection();
    
    void unsetReflection();
    
    CTSoftEdgesEffect getSoftEdge();
    
    boolean isSetSoftEdge();
    
    void setSoftEdge(final CTSoftEdgesEffect p0);
    
    CTSoftEdgesEffect addNewSoftEdge();
    
    void unsetSoftEdge();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTEffectList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTEffectList newInstance() {
            return (CTEffectList)getTypeLoader().newInstance(CTEffectList.type, (XmlOptions)null);
        }
        
        public static CTEffectList newInstance(final XmlOptions xmlOptions) {
            return (CTEffectList)getTypeLoader().newInstance(CTEffectList.type, xmlOptions);
        }
        
        public static CTEffectList parse(final String s) throws XmlException {
            return (CTEffectList)getTypeLoader().parse(s, CTEffectList.type, (XmlOptions)null);
        }
        
        public static CTEffectList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectList)getTypeLoader().parse(s, CTEffectList.type, xmlOptions);
        }
        
        public static CTEffectList parse(final File file) throws XmlException, IOException {
            return (CTEffectList)getTypeLoader().parse(file, CTEffectList.type, (XmlOptions)null);
        }
        
        public static CTEffectList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectList)getTypeLoader().parse(file, CTEffectList.type, xmlOptions);
        }
        
        public static CTEffectList parse(final URL url) throws XmlException, IOException {
            return (CTEffectList)getTypeLoader().parse(url, CTEffectList.type, (XmlOptions)null);
        }
        
        public static CTEffectList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectList)getTypeLoader().parse(url, CTEffectList.type, xmlOptions);
        }
        
        public static CTEffectList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTEffectList)getTypeLoader().parse(inputStream, CTEffectList.type, (XmlOptions)null);
        }
        
        public static CTEffectList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectList)getTypeLoader().parse(inputStream, CTEffectList.type, xmlOptions);
        }
        
        public static CTEffectList parse(final Reader reader) throws XmlException, IOException {
            return (CTEffectList)getTypeLoader().parse(reader, CTEffectList.type, (XmlOptions)null);
        }
        
        public static CTEffectList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTEffectList)getTypeLoader().parse(reader, CTEffectList.type, xmlOptions);
        }
        
        public static CTEffectList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTEffectList)getTypeLoader().parse(xmlStreamReader, CTEffectList.type, (XmlOptions)null);
        }
        
        public static CTEffectList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectList)getTypeLoader().parse(xmlStreamReader, CTEffectList.type, xmlOptions);
        }
        
        public static CTEffectList parse(final Node node) throws XmlException {
            return (CTEffectList)getTypeLoader().parse(node, CTEffectList.type, (XmlOptions)null);
        }
        
        public static CTEffectList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTEffectList)getTypeLoader().parse(node, CTEffectList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTEffectList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTEffectList)getTypeLoader().parse(xmlInputStream, CTEffectList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTEffectList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTEffectList)getTypeLoader().parse(xmlInputStream, CTEffectList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEffectList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTEffectList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
