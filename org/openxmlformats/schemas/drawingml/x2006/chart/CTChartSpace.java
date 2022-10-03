package org.openxmlformats.schemas.drawingml.x2006.chart;

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
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTChartSpace extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTChartSpace.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctchartspacef9b4type");
    
    CTBoolean getDate1904();
    
    boolean isSetDate1904();
    
    void setDate1904(final CTBoolean p0);
    
    CTBoolean addNewDate1904();
    
    void unsetDate1904();
    
    CTTextLanguageID getLang();
    
    boolean isSetLang();
    
    void setLang(final CTTextLanguageID p0);
    
    CTTextLanguageID addNewLang();
    
    void unsetLang();
    
    CTBoolean getRoundedCorners();
    
    boolean isSetRoundedCorners();
    
    void setRoundedCorners(final CTBoolean p0);
    
    CTBoolean addNewRoundedCorners();
    
    void unsetRoundedCorners();
    
    CTStyle getStyle();
    
    boolean isSetStyle();
    
    void setStyle(final CTStyle p0);
    
    CTStyle addNewStyle();
    
    void unsetStyle();
    
    CTColorMapping getClrMapOvr();
    
    boolean isSetClrMapOvr();
    
    void setClrMapOvr(final CTColorMapping p0);
    
    CTColorMapping addNewClrMapOvr();
    
    void unsetClrMapOvr();
    
    CTPivotSource getPivotSource();
    
    boolean isSetPivotSource();
    
    void setPivotSource(final CTPivotSource p0);
    
    CTPivotSource addNewPivotSource();
    
    void unsetPivotSource();
    
    CTProtection getProtection();
    
    boolean isSetProtection();
    
    void setProtection(final CTProtection p0);
    
    CTProtection addNewProtection();
    
    void unsetProtection();
    
    CTChart getChart();
    
    void setChart(final CTChart p0);
    
    CTChart addNewChart();
    
    CTShapeProperties getSpPr();
    
    boolean isSetSpPr();
    
    void setSpPr(final CTShapeProperties p0);
    
    CTShapeProperties addNewSpPr();
    
    void unsetSpPr();
    
    CTTextBody getTxPr();
    
    boolean isSetTxPr();
    
    void setTxPr(final CTTextBody p0);
    
    CTTextBody addNewTxPr();
    
    void unsetTxPr();
    
    CTExternalData getExternalData();
    
    boolean isSetExternalData();
    
    void setExternalData(final CTExternalData p0);
    
    CTExternalData addNewExternalData();
    
    void unsetExternalData();
    
    CTPrintSettings getPrintSettings();
    
    boolean isSetPrintSettings();
    
    void setPrintSettings(final CTPrintSettings p0);
    
    CTPrintSettings addNewPrintSettings();
    
    void unsetPrintSettings();
    
    CTRelId getUserShapes();
    
    boolean isSetUserShapes();
    
    void setUserShapes(final CTRelId p0);
    
    CTRelId addNewUserShapes();
    
    void unsetUserShapes();
    
    CTExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTExtensionList p0);
    
    CTExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTChartSpace.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTChartSpace newInstance() {
            return (CTChartSpace)getTypeLoader().newInstance(CTChartSpace.type, (XmlOptions)null);
        }
        
        public static CTChartSpace newInstance(final XmlOptions xmlOptions) {
            return (CTChartSpace)getTypeLoader().newInstance(CTChartSpace.type, xmlOptions);
        }
        
        public static CTChartSpace parse(final String s) throws XmlException {
            return (CTChartSpace)getTypeLoader().parse(s, CTChartSpace.type, (XmlOptions)null);
        }
        
        public static CTChartSpace parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTChartSpace)getTypeLoader().parse(s, CTChartSpace.type, xmlOptions);
        }
        
        public static CTChartSpace parse(final File file) throws XmlException, IOException {
            return (CTChartSpace)getTypeLoader().parse(file, CTChartSpace.type, (XmlOptions)null);
        }
        
        public static CTChartSpace parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartSpace)getTypeLoader().parse(file, CTChartSpace.type, xmlOptions);
        }
        
        public static CTChartSpace parse(final URL url) throws XmlException, IOException {
            return (CTChartSpace)getTypeLoader().parse(url, CTChartSpace.type, (XmlOptions)null);
        }
        
        public static CTChartSpace parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartSpace)getTypeLoader().parse(url, CTChartSpace.type, xmlOptions);
        }
        
        public static CTChartSpace parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTChartSpace)getTypeLoader().parse(inputStream, CTChartSpace.type, (XmlOptions)null);
        }
        
        public static CTChartSpace parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartSpace)getTypeLoader().parse(inputStream, CTChartSpace.type, xmlOptions);
        }
        
        public static CTChartSpace parse(final Reader reader) throws XmlException, IOException {
            return (CTChartSpace)getTypeLoader().parse(reader, CTChartSpace.type, (XmlOptions)null);
        }
        
        public static CTChartSpace parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChartSpace)getTypeLoader().parse(reader, CTChartSpace.type, xmlOptions);
        }
        
        public static CTChartSpace parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTChartSpace)getTypeLoader().parse(xmlStreamReader, CTChartSpace.type, (XmlOptions)null);
        }
        
        public static CTChartSpace parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTChartSpace)getTypeLoader().parse(xmlStreamReader, CTChartSpace.type, xmlOptions);
        }
        
        public static CTChartSpace parse(final Node node) throws XmlException {
            return (CTChartSpace)getTypeLoader().parse(node, CTChartSpace.type, (XmlOptions)null);
        }
        
        public static CTChartSpace parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTChartSpace)getTypeLoader().parse(node, CTChartSpace.type, xmlOptions);
        }
        
        @Deprecated
        public static CTChartSpace parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTChartSpace)getTypeLoader().parse(xmlInputStream, CTChartSpace.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTChartSpace parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTChartSpace)getTypeLoader().parse(xmlInputStream, CTChartSpace.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTChartSpace.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTChartSpace.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
