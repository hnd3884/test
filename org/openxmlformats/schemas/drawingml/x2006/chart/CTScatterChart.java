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

public interface CTScatterChart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTScatterChart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctscatterchart2bfctype");
    
    CTScatterStyle getScatterStyle();
    
    void setScatterStyle(final CTScatterStyle p0);
    
    CTScatterStyle addNewScatterStyle();
    
    CTBoolean getVaryColors();
    
    boolean isSetVaryColors();
    
    void setVaryColors(final CTBoolean p0);
    
    CTBoolean addNewVaryColors();
    
    void unsetVaryColors();
    
    List<CTScatterSer> getSerList();
    
    @Deprecated
    CTScatterSer[] getSerArray();
    
    CTScatterSer getSerArray(final int p0);
    
    int sizeOfSerArray();
    
    void setSerArray(final CTScatterSer[] p0);
    
    void setSerArray(final int p0, final CTScatterSer p1);
    
    CTScatterSer insertNewSer(final int p0);
    
    CTScatterSer addNewSer();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTScatterChart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTScatterChart newInstance() {
            return (CTScatterChart)getTypeLoader().newInstance(CTScatterChart.type, (XmlOptions)null);
        }
        
        public static CTScatterChart newInstance(final XmlOptions xmlOptions) {
            return (CTScatterChart)getTypeLoader().newInstance(CTScatterChart.type, xmlOptions);
        }
        
        public static CTScatterChart parse(final String s) throws XmlException {
            return (CTScatterChart)getTypeLoader().parse(s, CTScatterChart.type, (XmlOptions)null);
        }
        
        public static CTScatterChart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTScatterChart)getTypeLoader().parse(s, CTScatterChart.type, xmlOptions);
        }
        
        public static CTScatterChart parse(final File file) throws XmlException, IOException {
            return (CTScatterChart)getTypeLoader().parse(file, CTScatterChart.type, (XmlOptions)null);
        }
        
        public static CTScatterChart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterChart)getTypeLoader().parse(file, CTScatterChart.type, xmlOptions);
        }
        
        public static CTScatterChart parse(final URL url) throws XmlException, IOException {
            return (CTScatterChart)getTypeLoader().parse(url, CTScatterChart.type, (XmlOptions)null);
        }
        
        public static CTScatterChart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterChart)getTypeLoader().parse(url, CTScatterChart.type, xmlOptions);
        }
        
        public static CTScatterChart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTScatterChart)getTypeLoader().parse(inputStream, CTScatterChart.type, (XmlOptions)null);
        }
        
        public static CTScatterChart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterChart)getTypeLoader().parse(inputStream, CTScatterChart.type, xmlOptions);
        }
        
        public static CTScatterChart parse(final Reader reader) throws XmlException, IOException {
            return (CTScatterChart)getTypeLoader().parse(reader, CTScatterChart.type, (XmlOptions)null);
        }
        
        public static CTScatterChart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTScatterChart)getTypeLoader().parse(reader, CTScatterChart.type, xmlOptions);
        }
        
        public static CTScatterChart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTScatterChart)getTypeLoader().parse(xmlStreamReader, CTScatterChart.type, (XmlOptions)null);
        }
        
        public static CTScatterChart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTScatterChart)getTypeLoader().parse(xmlStreamReader, CTScatterChart.type, xmlOptions);
        }
        
        public static CTScatterChart parse(final Node node) throws XmlException {
            return (CTScatterChart)getTypeLoader().parse(node, CTScatterChart.type, (XmlOptions)null);
        }
        
        public static CTScatterChart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTScatterChart)getTypeLoader().parse(node, CTScatterChart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTScatterChart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTScatterChart)getTypeLoader().parse(xmlInputStream, CTScatterChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTScatterChart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTScatterChart)getTypeLoader().parse(xmlInputStream, CTScatterChart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScatterChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTScatterChart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
