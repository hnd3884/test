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

public interface CTAreaChart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTAreaChart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctareachart31b5type");
    
    CTGrouping getGrouping();
    
    boolean isSetGrouping();
    
    void setGrouping(final CTGrouping p0);
    
    CTGrouping addNewGrouping();
    
    void unsetGrouping();
    
    CTBoolean getVaryColors();
    
    boolean isSetVaryColors();
    
    void setVaryColors(final CTBoolean p0);
    
    CTBoolean addNewVaryColors();
    
    void unsetVaryColors();
    
    List<CTAreaSer> getSerList();
    
    @Deprecated
    CTAreaSer[] getSerArray();
    
    CTAreaSer getSerArray(final int p0);
    
    int sizeOfSerArray();
    
    void setSerArray(final CTAreaSer[] p0);
    
    void setSerArray(final int p0, final CTAreaSer p1);
    
    CTAreaSer insertNewSer(final int p0);
    
    CTAreaSer addNewSer();
    
    void removeSer(final int p0);
    
    CTDLbls getDLbls();
    
    boolean isSetDLbls();
    
    void setDLbls(final CTDLbls p0);
    
    CTDLbls addNewDLbls();
    
    void unsetDLbls();
    
    CTChartLines getDropLines();
    
    boolean isSetDropLines();
    
    void setDropLines(final CTChartLines p0);
    
    CTChartLines addNewDropLines();
    
    void unsetDropLines();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTAreaChart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTAreaChart newInstance() {
            return (CTAreaChart)getTypeLoader().newInstance(CTAreaChart.type, (XmlOptions)null);
        }
        
        public static CTAreaChart newInstance(final XmlOptions xmlOptions) {
            return (CTAreaChart)getTypeLoader().newInstance(CTAreaChart.type, xmlOptions);
        }
        
        public static CTAreaChart parse(final String s) throws XmlException {
            return (CTAreaChart)getTypeLoader().parse(s, CTAreaChart.type, (XmlOptions)null);
        }
        
        public static CTAreaChart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTAreaChart)getTypeLoader().parse(s, CTAreaChart.type, xmlOptions);
        }
        
        public static CTAreaChart parse(final File file) throws XmlException, IOException {
            return (CTAreaChart)getTypeLoader().parse(file, CTAreaChart.type, (XmlOptions)null);
        }
        
        public static CTAreaChart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAreaChart)getTypeLoader().parse(file, CTAreaChart.type, xmlOptions);
        }
        
        public static CTAreaChart parse(final URL url) throws XmlException, IOException {
            return (CTAreaChart)getTypeLoader().parse(url, CTAreaChart.type, (XmlOptions)null);
        }
        
        public static CTAreaChart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAreaChart)getTypeLoader().parse(url, CTAreaChart.type, xmlOptions);
        }
        
        public static CTAreaChart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTAreaChart)getTypeLoader().parse(inputStream, CTAreaChart.type, (XmlOptions)null);
        }
        
        public static CTAreaChart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAreaChart)getTypeLoader().parse(inputStream, CTAreaChart.type, xmlOptions);
        }
        
        public static CTAreaChart parse(final Reader reader) throws XmlException, IOException {
            return (CTAreaChart)getTypeLoader().parse(reader, CTAreaChart.type, (XmlOptions)null);
        }
        
        public static CTAreaChart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTAreaChart)getTypeLoader().parse(reader, CTAreaChart.type, xmlOptions);
        }
        
        public static CTAreaChart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTAreaChart)getTypeLoader().parse(xmlStreamReader, CTAreaChart.type, (XmlOptions)null);
        }
        
        public static CTAreaChart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTAreaChart)getTypeLoader().parse(xmlStreamReader, CTAreaChart.type, xmlOptions);
        }
        
        public static CTAreaChart parse(final Node node) throws XmlException {
            return (CTAreaChart)getTypeLoader().parse(node, CTAreaChart.type, (XmlOptions)null);
        }
        
        public static CTAreaChart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTAreaChart)getTypeLoader().parse(node, CTAreaChart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTAreaChart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTAreaChart)getTypeLoader().parse(xmlInputStream, CTAreaChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTAreaChart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTAreaChart)getTypeLoader().parse(xmlInputStream, CTAreaChart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAreaChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTAreaChart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
