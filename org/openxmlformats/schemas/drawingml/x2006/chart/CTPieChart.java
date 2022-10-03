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

public interface CTPieChart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTPieChart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctpiechartd34atype");
    
    CTBoolean getVaryColors();
    
    boolean isSetVaryColors();
    
    void setVaryColors(final CTBoolean p0);
    
    CTBoolean addNewVaryColors();
    
    void unsetVaryColors();
    
    List<CTPieSer> getSerList();
    
    @Deprecated
    CTPieSer[] getSerArray();
    
    CTPieSer getSerArray(final int p0);
    
    int sizeOfSerArray();
    
    void setSerArray(final CTPieSer[] p0);
    
    void setSerArray(final int p0, final CTPieSer p1);
    
    CTPieSer insertNewSer(final int p0);
    
    CTPieSer addNewSer();
    
    void removeSer(final int p0);
    
    CTDLbls getDLbls();
    
    boolean isSetDLbls();
    
    void setDLbls(final CTDLbls p0);
    
    CTDLbls addNewDLbls();
    
    void unsetDLbls();
    
    CTFirstSliceAng getFirstSliceAng();
    
    boolean isSetFirstSliceAng();
    
    void setFirstSliceAng(final CTFirstSliceAng p0);
    
    CTFirstSliceAng addNewFirstSliceAng();
    
    void unsetFirstSliceAng();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTPieChart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTPieChart newInstance() {
            return (CTPieChart)getTypeLoader().newInstance(CTPieChart.type, (XmlOptions)null);
        }
        
        public static CTPieChart newInstance(final XmlOptions xmlOptions) {
            return (CTPieChart)getTypeLoader().newInstance(CTPieChart.type, xmlOptions);
        }
        
        public static CTPieChart parse(final String s) throws XmlException {
            return (CTPieChart)getTypeLoader().parse(s, CTPieChart.type, (XmlOptions)null);
        }
        
        public static CTPieChart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTPieChart)getTypeLoader().parse(s, CTPieChart.type, xmlOptions);
        }
        
        public static CTPieChart parse(final File file) throws XmlException, IOException {
            return (CTPieChart)getTypeLoader().parse(file, CTPieChart.type, (XmlOptions)null);
        }
        
        public static CTPieChart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPieChart)getTypeLoader().parse(file, CTPieChart.type, xmlOptions);
        }
        
        public static CTPieChart parse(final URL url) throws XmlException, IOException {
            return (CTPieChart)getTypeLoader().parse(url, CTPieChart.type, (XmlOptions)null);
        }
        
        public static CTPieChart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPieChart)getTypeLoader().parse(url, CTPieChart.type, xmlOptions);
        }
        
        public static CTPieChart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTPieChart)getTypeLoader().parse(inputStream, CTPieChart.type, (XmlOptions)null);
        }
        
        public static CTPieChart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPieChart)getTypeLoader().parse(inputStream, CTPieChart.type, xmlOptions);
        }
        
        public static CTPieChart parse(final Reader reader) throws XmlException, IOException {
            return (CTPieChart)getTypeLoader().parse(reader, CTPieChart.type, (XmlOptions)null);
        }
        
        public static CTPieChart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTPieChart)getTypeLoader().parse(reader, CTPieChart.type, xmlOptions);
        }
        
        public static CTPieChart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTPieChart)getTypeLoader().parse(xmlStreamReader, CTPieChart.type, (XmlOptions)null);
        }
        
        public static CTPieChart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTPieChart)getTypeLoader().parse(xmlStreamReader, CTPieChart.type, xmlOptions);
        }
        
        public static CTPieChart parse(final Node node) throws XmlException {
            return (CTPieChart)getTypeLoader().parse(node, CTPieChart.type, (XmlOptions)null);
        }
        
        public static CTPieChart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTPieChart)getTypeLoader().parse(node, CTPieChart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTPieChart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTPieChart)getTypeLoader().parse(xmlInputStream, CTPieChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTPieChart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTPieChart)getTypeLoader().parse(xmlInputStream, CTPieChart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPieChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTPieChart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
