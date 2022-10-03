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

public interface CTSurfaceChart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTSurfaceChart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctsurfacecharta96btype");
    
    CTBoolean getWireframe();
    
    boolean isSetWireframe();
    
    void setWireframe(final CTBoolean p0);
    
    CTBoolean addNewWireframe();
    
    void unsetWireframe();
    
    List<CTSurfaceSer> getSerList();
    
    @Deprecated
    CTSurfaceSer[] getSerArray();
    
    CTSurfaceSer getSerArray(final int p0);
    
    int sizeOfSerArray();
    
    void setSerArray(final CTSurfaceSer[] p0);
    
    void setSerArray(final int p0, final CTSurfaceSer p1);
    
    CTSurfaceSer insertNewSer(final int p0);
    
    CTSurfaceSer addNewSer();
    
    void removeSer(final int p0);
    
    CTBandFmts getBandFmts();
    
    boolean isSetBandFmts();
    
    void setBandFmts(final CTBandFmts p0);
    
    CTBandFmts addNewBandFmts();
    
    void unsetBandFmts();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTSurfaceChart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTSurfaceChart newInstance() {
            return (CTSurfaceChart)getTypeLoader().newInstance(CTSurfaceChart.type, (XmlOptions)null);
        }
        
        public static CTSurfaceChart newInstance(final XmlOptions xmlOptions) {
            return (CTSurfaceChart)getTypeLoader().newInstance(CTSurfaceChart.type, xmlOptions);
        }
        
        public static CTSurfaceChart parse(final String s) throws XmlException {
            return (CTSurfaceChart)getTypeLoader().parse(s, CTSurfaceChart.type, (XmlOptions)null);
        }
        
        public static CTSurfaceChart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTSurfaceChart)getTypeLoader().parse(s, CTSurfaceChart.type, xmlOptions);
        }
        
        public static CTSurfaceChart parse(final File file) throws XmlException, IOException {
            return (CTSurfaceChart)getTypeLoader().parse(file, CTSurfaceChart.type, (XmlOptions)null);
        }
        
        public static CTSurfaceChart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurfaceChart)getTypeLoader().parse(file, CTSurfaceChart.type, xmlOptions);
        }
        
        public static CTSurfaceChart parse(final URL url) throws XmlException, IOException {
            return (CTSurfaceChart)getTypeLoader().parse(url, CTSurfaceChart.type, (XmlOptions)null);
        }
        
        public static CTSurfaceChart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurfaceChart)getTypeLoader().parse(url, CTSurfaceChart.type, xmlOptions);
        }
        
        public static CTSurfaceChart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTSurfaceChart)getTypeLoader().parse(inputStream, CTSurfaceChart.type, (XmlOptions)null);
        }
        
        public static CTSurfaceChart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurfaceChart)getTypeLoader().parse(inputStream, CTSurfaceChart.type, xmlOptions);
        }
        
        public static CTSurfaceChart parse(final Reader reader) throws XmlException, IOException {
            return (CTSurfaceChart)getTypeLoader().parse(reader, CTSurfaceChart.type, (XmlOptions)null);
        }
        
        public static CTSurfaceChart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTSurfaceChart)getTypeLoader().parse(reader, CTSurfaceChart.type, xmlOptions);
        }
        
        public static CTSurfaceChart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTSurfaceChart)getTypeLoader().parse(xmlStreamReader, CTSurfaceChart.type, (XmlOptions)null);
        }
        
        public static CTSurfaceChart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTSurfaceChart)getTypeLoader().parse(xmlStreamReader, CTSurfaceChart.type, xmlOptions);
        }
        
        public static CTSurfaceChart parse(final Node node) throws XmlException {
            return (CTSurfaceChart)getTypeLoader().parse(node, CTSurfaceChart.type, (XmlOptions)null);
        }
        
        public static CTSurfaceChart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTSurfaceChart)getTypeLoader().parse(node, CTSurfaceChart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTSurfaceChart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTSurfaceChart)getTypeLoader().parse(xmlInputStream, CTSurfaceChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTSurfaceChart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTSurfaceChart)getTypeLoader().parse(xmlInputStream, CTSurfaceChart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSurfaceChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTSurfaceChart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
