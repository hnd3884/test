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
import org.openxmlformats.schemas.drawingml.x2006.main.CTQuickTimeFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTVideoFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAudioFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTEmbeddedWAVAudioFile;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAudioCD;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTApplicationNonVisualDrawingProps extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTApplicationNonVisualDrawingProps.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctapplicationnonvisualdrawingprops2fb6type");
    
    CTPlaceholder getPh();
    
    boolean isSetPh();
    
    void setPh(final CTPlaceholder p0);
    
    CTPlaceholder addNewPh();
    
    void unsetPh();
    
    CTAudioCD getAudioCd();
    
    boolean isSetAudioCd();
    
    void setAudioCd(final CTAudioCD p0);
    
    CTAudioCD addNewAudioCd();
    
    void unsetAudioCd();
    
    CTEmbeddedWAVAudioFile getWavAudioFile();
    
    boolean isSetWavAudioFile();
    
    void setWavAudioFile(final CTEmbeddedWAVAudioFile p0);
    
    CTEmbeddedWAVAudioFile addNewWavAudioFile();
    
    void unsetWavAudioFile();
    
    CTAudioFile getAudioFile();
    
    boolean isSetAudioFile();
    
    void setAudioFile(final CTAudioFile p0);
    
    CTAudioFile addNewAudioFile();
    
    void unsetAudioFile();
    
    CTVideoFile getVideoFile();
    
    boolean isSetVideoFile();
    
    void setVideoFile(final CTVideoFile p0);
    
    CTVideoFile addNewVideoFile();
    
    void unsetVideoFile();
    
    CTQuickTimeFile getQuickTimeFile();
    
    boolean isSetQuickTimeFile();
    
    void setQuickTimeFile(final CTQuickTimeFile p0);
    
    CTQuickTimeFile addNewQuickTimeFile();
    
    void unsetQuickTimeFile();
    
    CTCustomerDataList getCustDataLst();
    
    boolean isSetCustDataLst();
    
    void setCustDataLst(final CTCustomerDataList p0);
    
    CTCustomerDataList addNewCustDataLst();
    
    void unsetCustDataLst();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    boolean getIsPhoto();
    
    XmlBoolean xgetIsPhoto();
    
    boolean isSetIsPhoto();
    
    void setIsPhoto(final boolean p0);
    
    void xsetIsPhoto(final XmlBoolean p0);
    
    void unsetIsPhoto();
    
    boolean getUserDrawn();
    
    XmlBoolean xgetUserDrawn();
    
    boolean isSetUserDrawn();
    
    void setUserDrawn(final boolean p0);
    
    void xsetUserDrawn(final XmlBoolean p0);
    
    void unsetUserDrawn();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTApplicationNonVisualDrawingProps.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTApplicationNonVisualDrawingProps newInstance() {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().newInstance(CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTApplicationNonVisualDrawingProps newInstance(final XmlOptions xmlOptions) {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().newInstance(CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final String s) throws XmlException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(s, CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(s, CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final File file) throws XmlException, IOException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(file, CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(file, CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final URL url) throws XmlException, IOException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(url, CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(url, CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(inputStream, CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(inputStream, CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final Reader reader) throws XmlException, IOException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(reader, CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(reader, CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(xmlStreamReader, CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(xmlStreamReader, CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final Node node) throws XmlException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(node, CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        public static CTApplicationNonVisualDrawingProps parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(node, CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        @Deprecated
        public static CTApplicationNonVisualDrawingProps parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(xmlInputStream, CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTApplicationNonVisualDrawingProps parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTApplicationNonVisualDrawingProps)getTypeLoader().parse(xmlInputStream, CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTApplicationNonVisualDrawingProps.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTApplicationNonVisualDrawingProps.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
