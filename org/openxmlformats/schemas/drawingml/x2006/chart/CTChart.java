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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTChart extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTChart.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctchartc108type");
    
    CTTitle getTitle();
    
    boolean isSetTitle();
    
    void setTitle(final CTTitle p0);
    
    CTTitle addNewTitle();
    
    void unsetTitle();
    
    CTBoolean getAutoTitleDeleted();
    
    boolean isSetAutoTitleDeleted();
    
    void setAutoTitleDeleted(final CTBoolean p0);
    
    CTBoolean addNewAutoTitleDeleted();
    
    void unsetAutoTitleDeleted();
    
    CTPivotFmts getPivotFmts();
    
    boolean isSetPivotFmts();
    
    void setPivotFmts(final CTPivotFmts p0);
    
    CTPivotFmts addNewPivotFmts();
    
    void unsetPivotFmts();
    
    CTView3D getView3D();
    
    boolean isSetView3D();
    
    void setView3D(final CTView3D p0);
    
    CTView3D addNewView3D();
    
    void unsetView3D();
    
    CTSurface getFloor();
    
    boolean isSetFloor();
    
    void setFloor(final CTSurface p0);
    
    CTSurface addNewFloor();
    
    void unsetFloor();
    
    CTSurface getSideWall();
    
    boolean isSetSideWall();
    
    void setSideWall(final CTSurface p0);
    
    CTSurface addNewSideWall();
    
    void unsetSideWall();
    
    CTSurface getBackWall();
    
    boolean isSetBackWall();
    
    void setBackWall(final CTSurface p0);
    
    CTSurface addNewBackWall();
    
    void unsetBackWall();
    
    CTPlotArea getPlotArea();
    
    void setPlotArea(final CTPlotArea p0);
    
    CTPlotArea addNewPlotArea();
    
    CTLegend getLegend();
    
    boolean isSetLegend();
    
    void setLegend(final CTLegend p0);
    
    CTLegend addNewLegend();
    
    void unsetLegend();
    
    CTBoolean getPlotVisOnly();
    
    boolean isSetPlotVisOnly();
    
    void setPlotVisOnly(final CTBoolean p0);
    
    CTBoolean addNewPlotVisOnly();
    
    void unsetPlotVisOnly();
    
    CTDispBlanksAs getDispBlanksAs();
    
    boolean isSetDispBlanksAs();
    
    void setDispBlanksAs(final CTDispBlanksAs p0);
    
    CTDispBlanksAs addNewDispBlanksAs();
    
    void unsetDispBlanksAs();
    
    CTBoolean getShowDLblsOverMax();
    
    boolean isSetShowDLblsOverMax();
    
    void setShowDLblsOverMax(final CTBoolean p0);
    
    CTBoolean addNewShowDLblsOverMax();
    
    void unsetShowDLblsOverMax();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTChart.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTChart newInstance() {
            return (CTChart)getTypeLoader().newInstance(CTChart.type, (XmlOptions)null);
        }
        
        public static CTChart newInstance(final XmlOptions xmlOptions) {
            return (CTChart)getTypeLoader().newInstance(CTChart.type, xmlOptions);
        }
        
        public static CTChart parse(final String s) throws XmlException {
            return (CTChart)getTypeLoader().parse(s, CTChart.type, (XmlOptions)null);
        }
        
        public static CTChart parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTChart)getTypeLoader().parse(s, CTChart.type, xmlOptions);
        }
        
        public static CTChart parse(final File file) throws XmlException, IOException {
            return (CTChart)getTypeLoader().parse(file, CTChart.type, (XmlOptions)null);
        }
        
        public static CTChart parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChart)getTypeLoader().parse(file, CTChart.type, xmlOptions);
        }
        
        public static CTChart parse(final URL url) throws XmlException, IOException {
            return (CTChart)getTypeLoader().parse(url, CTChart.type, (XmlOptions)null);
        }
        
        public static CTChart parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChart)getTypeLoader().parse(url, CTChart.type, xmlOptions);
        }
        
        public static CTChart parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTChart)getTypeLoader().parse(inputStream, CTChart.type, (XmlOptions)null);
        }
        
        public static CTChart parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChart)getTypeLoader().parse(inputStream, CTChart.type, xmlOptions);
        }
        
        public static CTChart parse(final Reader reader) throws XmlException, IOException {
            return (CTChart)getTypeLoader().parse(reader, CTChart.type, (XmlOptions)null);
        }
        
        public static CTChart parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTChart)getTypeLoader().parse(reader, CTChart.type, xmlOptions);
        }
        
        public static CTChart parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTChart)getTypeLoader().parse(xmlStreamReader, CTChart.type, (XmlOptions)null);
        }
        
        public static CTChart parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTChart)getTypeLoader().parse(xmlStreamReader, CTChart.type, xmlOptions);
        }
        
        public static CTChart parse(final Node node) throws XmlException {
            return (CTChart)getTypeLoader().parse(node, CTChart.type, (XmlOptions)null);
        }
        
        public static CTChart parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTChart)getTypeLoader().parse(node, CTChart.type, xmlOptions);
        }
        
        @Deprecated
        public static CTChart parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTChart)getTypeLoader().parse(xmlInputStream, CTChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTChart parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTChart)getTypeLoader().parse(xmlInputStream, CTChart.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTChart.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTChart.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
