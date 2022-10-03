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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTRadarChart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRadarChart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctradarchart0f04type");
    
    CTRadarStyle getRadarStyle();
    
    void setRadarStyle(final CTRadarStyle p0);
    
    CTRadarStyle addNewRadarStyle();
    
    CTBoolean getVaryColors();
    
    boolean isSetVaryColors();
    
    void setVaryColors(final CTBoolean p0);
    
    CTBoolean addNewVaryColors();
    
    void unsetVaryColors();
    
    List<CTRadarSer> getSerList();
    
    @Deprecated
    CTRadarSer[] getSerArray();
    
    CTRadarSer getSerArray(final int p0);
    
    int sizeOfSerArray();
    
    void setSerArray(final CTRadarSer[] p0);
    
    void setSerArray(final int p0, final CTRadarSer p1);
    
    CTRadarSer insertNewSer(final int p0);
    
    CTRadarSer addNewSer();
    
    void removeSer(final int p0);
    
    CTDLbls getDLbls();
    
    boolean isSetDLbls();
    
    void setDLbls(final CTDLbls p0);
    
    CTDLbls addNewDLbls();
    
    void unsetDLbls();
    
    List<CTUnsignedInt> getAxIdList();
    
    @Deprecated
    CTUnsignedInt[] getAxIdArray();
    
    CTUnsignedInt getAxIdArray(final int p0);
    
    int sizeOfAxIdArray();
    
    void setAxIdArray(final CTUnsignedInt[] p0);
    
    void setAxIdArray(final int p0, final CTUnsignedInt p1);
    
    CTUnsignedInt insertNewAxId(final int p0);
    
    CTUnsignedInt addNewAxId();
    
    void removeAxId(final int p0);
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRadarChart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRadarChart newInstance() {
            return (CTRadarChart)getTypeLoader().newInstance(CTRadarChart.type, (XmlOptions)null);
        }
        
        public static CTRadarChart newInstance(final XmlOptions xmlOptions) {
            return (CTRadarChart)getTypeLoader().newInstance(CTRadarChart.type, xmlOptions);
        }
        
        public static CTRadarChart parse(final String s) throws XmlException {
            return (CTRadarChart)getTypeLoader().parse(s, CTRadarChart.type, (XmlOptions)null);
        }
        
        public static CTRadarChart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRadarChart)getTypeLoader().parse(s, CTRadarChart.type, xmlOptions);
        }
        
        public static CTRadarChart parse(final File file) throws XmlException, IOException {
            return (CTRadarChart)getTypeLoader().parse(file, CTRadarChart.type, (XmlOptions)null);
        }
        
        public static CTRadarChart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarChart)getTypeLoader().parse(file, CTRadarChart.type, xmlOptions);
        }
        
        public static CTRadarChart parse(final URL url) throws XmlException, IOException {
            return (CTRadarChart)getTypeLoader().parse(url, CTRadarChart.type, (XmlOptions)null);
        }
        
        public static CTRadarChart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarChart)getTypeLoader().parse(url, CTRadarChart.type, xmlOptions);
        }
        
        public static CTRadarChart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRadarChart)getTypeLoader().parse(inputStream, CTRadarChart.type, (XmlOptions)null);
        }
        
        public static CTRadarChart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarChart)getTypeLoader().parse(inputStream, CTRadarChart.type, xmlOptions);
        }
        
        public static CTRadarChart parse(final Reader reader) throws XmlException, IOException {
            return (CTRadarChart)getTypeLoader().parse(reader, CTRadarChart.type, (XmlOptions)null);
        }
        
        public static CTRadarChart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRadarChart)getTypeLoader().parse(reader, CTRadarChart.type, xmlOptions);
        }
        
        public static CTRadarChart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRadarChart)getTypeLoader().parse(xmlStreamReader, CTRadarChart.type, (XmlOptions)null);
        }
        
        public static CTRadarChart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRadarChart)getTypeLoader().parse(xmlStreamReader, CTRadarChart.type, xmlOptions);
        }
        
        public static CTRadarChart parse(final Node node) throws XmlException {
            return (CTRadarChart)getTypeLoader().parse(node, CTRadarChart.type, (XmlOptions)null);
        }
        
        public static CTRadarChart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRadarChart)getTypeLoader().parse(node, CTRadarChart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRadarChart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRadarChart)getTypeLoader().parse(xmlInputStream, CTRadarChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRadarChart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRadarChart)getTypeLoader().parse(xmlInputStream, CTRadarChart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRadarChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRadarChart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
