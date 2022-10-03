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
import com.microsoft.schemas.office.office.STConnectType;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTPath extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPath.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpath5963type");
    
    String getId();
    
    XmlString xgetId();
    
    boolean isSetId();
    
    void setId(final String p0);
    
    void xsetId(final XmlString p0);
    
    void unsetId();
    
    String getV();
    
    XmlString xgetV();
    
    boolean isSetV();
    
    void setV(final String p0);
    
    void xsetV(final XmlString p0);
    
    void unsetV();
    
    String getLimo();
    
    XmlString xgetLimo();
    
    boolean isSetLimo();
    
    void setLimo(final String p0);
    
    void xsetLimo(final XmlString p0);
    
    void unsetLimo();
    
    String getTextboxrect();
    
    XmlString xgetTextboxrect();
    
    boolean isSetTextboxrect();
    
    void setTextboxrect(final String p0);
    
    void xsetTextboxrect(final XmlString p0);
    
    void unsetTextboxrect();
    
    STTrueFalse.Enum getFillok();
    
    STTrueFalse xgetFillok();
    
    boolean isSetFillok();
    
    void setFillok(final STTrueFalse.Enum p0);
    
    void xsetFillok(final STTrueFalse p0);
    
    void unsetFillok();
    
    STTrueFalse.Enum getStrokeok();
    
    STTrueFalse xgetStrokeok();
    
    boolean isSetStrokeok();
    
    void setStrokeok(final STTrueFalse.Enum p0);
    
    void xsetStrokeok(final STTrueFalse p0);
    
    void unsetStrokeok();
    
    STTrueFalse.Enum getShadowok();
    
    STTrueFalse xgetShadowok();
    
    boolean isSetShadowok();
    
    void setShadowok(final STTrueFalse.Enum p0);
    
    void xsetShadowok(final STTrueFalse p0);
    
    void unsetShadowok();
    
    STTrueFalse.Enum getArrowok();
    
    STTrueFalse xgetArrowok();
    
    boolean isSetArrowok();
    
    void setArrowok(final STTrueFalse.Enum p0);
    
    void xsetArrowok(final STTrueFalse p0);
    
    void unsetArrowok();
    
    STTrueFalse.Enum getGradientshapeok();
    
    STTrueFalse xgetGradientshapeok();
    
    boolean isSetGradientshapeok();
    
    void setGradientshapeok(final STTrueFalse.Enum p0);
    
    void xsetGradientshapeok(final STTrueFalse p0);
    
    void unsetGradientshapeok();
    
    STTrueFalse.Enum getTextpathok();
    
    STTrueFalse xgetTextpathok();
    
    boolean isSetTextpathok();
    
    void setTextpathok(final STTrueFalse.Enum p0);
    
    void xsetTextpathok(final STTrueFalse p0);
    
    void unsetTextpathok();
    
    STTrueFalse.Enum getInsetpenok();
    
    STTrueFalse xgetInsetpenok();
    
    boolean isSetInsetpenok();
    
    void setInsetpenok(final STTrueFalse.Enum p0);
    
    void xsetInsetpenok(final STTrueFalse p0);
    
    void unsetInsetpenok();
    
    STConnectType.Enum getConnecttype();
    
    STConnectType xgetConnecttype();
    
    boolean isSetConnecttype();
    
    void setConnecttype(final STConnectType.Enum p0);
    
    void xsetConnecttype(final STConnectType p0);
    
    void unsetConnecttype();
    
    String getConnectlocs();
    
    XmlString xgetConnectlocs();
    
    boolean isSetConnectlocs();
    
    void setConnectlocs(final String p0);
    
    void xsetConnectlocs(final XmlString p0);
    
    void unsetConnectlocs();
    
    String getConnectangles();
    
    XmlString xgetConnectangles();
    
    boolean isSetConnectangles();
    
    void setConnectangles(final String p0);
    
    void xsetConnectangles(final XmlString p0);
    
    void unsetConnectangles();
    
    com.microsoft.schemas.office.office.STTrueFalse.Enum getExtrusionok();
    
    com.microsoft.schemas.office.office.STTrueFalse xgetExtrusionok();
    
    boolean isSetExtrusionok();
    
    void setExtrusionok(final com.microsoft.schemas.office.office.STTrueFalse.Enum p0);
    
    void xsetExtrusionok(final com.microsoft.schemas.office.office.STTrueFalse p0);
    
    void unsetExtrusionok();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPath.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPath newInstance() {
            return (CTPath)getTypeLoader().newInstance(CTPath.type, (XmlOptions)null);
        }
        
        public static CTPath newInstance(final XmlOptions xmlOptions) {
            return (CTPath)getTypeLoader().newInstance(CTPath.type, xmlOptions);
        }
        
        public static CTPath parse(final String s) throws XmlException {
            return (CTPath)getTypeLoader().parse(s, CTPath.type, (XmlOptions)null);
        }
        
        public static CTPath parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath)getTypeLoader().parse(s, CTPath.type, xmlOptions);
        }
        
        public static CTPath parse(final File file) throws XmlException, IOException {
            return (CTPath)getTypeLoader().parse(file, CTPath.type, (XmlOptions)null);
        }
        
        public static CTPath parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath)getTypeLoader().parse(file, CTPath.type, xmlOptions);
        }
        
        public static CTPath parse(final URL url) throws XmlException, IOException {
            return (CTPath)getTypeLoader().parse(url, CTPath.type, (XmlOptions)null);
        }
        
        public static CTPath parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath)getTypeLoader().parse(url, CTPath.type, xmlOptions);
        }
        
        public static CTPath parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPath)getTypeLoader().parse(inputStream, CTPath.type, (XmlOptions)null);
        }
        
        public static CTPath parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath)getTypeLoader().parse(inputStream, CTPath.type, xmlOptions);
        }
        
        public static CTPath parse(final Reader reader) throws XmlException, IOException {
            return (CTPath)getTypeLoader().parse(reader, CTPath.type, (XmlOptions)null);
        }
        
        public static CTPath parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPath)getTypeLoader().parse(reader, CTPath.type, xmlOptions);
        }
        
        public static CTPath parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPath)getTypeLoader().parse(xmlStreamReader, CTPath.type, (XmlOptions)null);
        }
        
        public static CTPath parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath)getTypeLoader().parse(xmlStreamReader, CTPath.type, xmlOptions);
        }
        
        public static CTPath parse(final Node node) throws XmlException {
            return (CTPath)getTypeLoader().parse(node, CTPath.type, (XmlOptions)null);
        }
        
        public static CTPath parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPath)getTypeLoader().parse(node, CTPath.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPath parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPath)getTypeLoader().parse(xmlInputStream, CTPath.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPath parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPath)getTypeLoader().parse(xmlInputStream, CTPath.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPath.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
