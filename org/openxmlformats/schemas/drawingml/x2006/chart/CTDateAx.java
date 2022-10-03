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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTDateAx extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTDateAx.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctdateaxbdd7type");
    
    CTUnsignedInt getAxId();
    
    void setAxId(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewAxId();
    
    CTScaling getScaling();
    
    void setScaling(final CTScaling p0);
    
    CTScaling addNewScaling();
    
    CTBoolean getDelete();
    
    boolean isSetDelete();
    
    void setDelete(final CTBoolean p0);
    
    CTBoolean addNewDelete();
    
    void unsetDelete();
    
    CTAxPos getAxPos();
    
    void setAxPos(final CTAxPos p0);
    
    CTAxPos addNewAxPos();
    
    CTChartLines getMajorGridlines();
    
    boolean isSetMajorGridlines();
    
    void setMajorGridlines(final CTChartLines p0);
    
    CTChartLines addNewMajorGridlines();
    
    void unsetMajorGridlines();
    
    CTChartLines getMinorGridlines();
    
    boolean isSetMinorGridlines();
    
    void setMinorGridlines(final CTChartLines p0);
    
    CTChartLines addNewMinorGridlines();
    
    void unsetMinorGridlines();
    
    CTTitle getTitle();
    
    boolean isSetTitle();
    
    void setTitle(final CTTitle p0);
    
    CTTitle addNewTitle();
    
    void unsetTitle();
    
    CTNumFmt getNumFmt();
    
    boolean isSetNumFmt();
    
    void setNumFmt(final CTNumFmt p0);
    
    CTNumFmt addNewNumFmt();
    
    void unsetNumFmt();
    
    CTTickMark getMajorTickMark();
    
    boolean isSetMajorTickMark();
    
    void setMajorTickMark(final CTTickMark p0);
    
    CTTickMark addNewMajorTickMark();
    
    void unsetMajorTickMark();
    
    CTTickMark getMinorTickMark();
    
    boolean isSetMinorTickMark();
    
    void setMinorTickMark(final CTTickMark p0);
    
    CTTickMark addNewMinorTickMark();
    
    void unsetMinorTickMark();
    
    CTTickLblPos getTickLblPos();
    
    boolean isSetTickLblPos();
    
    void setTickLblPos(final CTTickLblPos p0);
    
    CTTickLblPos addNewTickLblPos();
    
    void unsetTickLblPos();
    
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
    
    CTUnsignedInt getCrossAx();
    
    void setCrossAx(final CTUnsignedInt p0);
    
    CTUnsignedInt addNewCrossAx();
    
    CTCrosses getCrosses();
    
    boolean isSetCrosses();
    
    void setCrosses(final CTCrosses p0);
    
    CTCrosses addNewCrosses();
    
    void unsetCrosses();
    
    CTDouble getCrossesAt();
    
    boolean isSetCrossesAt();
    
    void setCrossesAt(final CTDouble p0);
    
    CTDouble addNewCrossesAt();
    
    void unsetCrossesAt();
    
    CTBoolean getAuto();
    
    boolean isSetAuto();
    
    void setAuto(final CTBoolean p0);
    
    CTBoolean addNewAuto();
    
    void unsetAuto();
    
    CTLblOffset getLblOffset();
    
    boolean isSetLblOffset();
    
    void setLblOffset(final CTLblOffset p0);
    
    CTLblOffset addNewLblOffset();
    
    void unsetLblOffset();
    
    CTTimeUnit getBaseTimeUnit();
    
    boolean isSetBaseTimeUnit();
    
    void setBaseTimeUnit(final CTTimeUnit p0);
    
    CTTimeUnit addNewBaseTimeUnit();
    
    void unsetBaseTimeUnit();
    
    CTAxisUnit getMajorUnit();
    
    boolean isSetMajorUnit();
    
    void setMajorUnit(final CTAxisUnit p0);
    
    CTAxisUnit addNewMajorUnit();
    
    void unsetMajorUnit();
    
    CTTimeUnit getMajorTimeUnit();
    
    boolean isSetMajorTimeUnit();
    
    void setMajorTimeUnit(final CTTimeUnit p0);
    
    CTTimeUnit addNewMajorTimeUnit();
    
    void unsetMajorTimeUnit();
    
    CTAxisUnit getMinorUnit();
    
    boolean isSetMinorUnit();
    
    void setMinorUnit(final CTAxisUnit p0);
    
    CTAxisUnit addNewMinorUnit();
    
    void unsetMinorUnit();
    
    CTTimeUnit getMinorTimeUnit();
    
    boolean isSetMinorTimeUnit();
    
    void setMinorTimeUnit(final CTTimeUnit p0);
    
    CTTimeUnit addNewMinorTimeUnit();
    
    void unsetMinorTimeUnit();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTDateAx.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTDateAx newInstance() {
            return (CTDateAx)getTypeLoader().newInstance(CTDateAx.type, (XmlOptions)null);
        }
        
        public static CTDateAx newInstance(final XmlOptions xmlOptions) {
            return (CTDateAx)getTypeLoader().newInstance(CTDateAx.type, xmlOptions);
        }
        
        public static CTDateAx parse(final String s) throws XmlException {
            return (CTDateAx)getTypeLoader().parse(s, CTDateAx.type, (XmlOptions)null);
        }
        
        public static CTDateAx parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTDateAx)getTypeLoader().parse(s, CTDateAx.type, xmlOptions);
        }
        
        public static CTDateAx parse(final File file) throws XmlException, IOException {
            return (CTDateAx)getTypeLoader().parse(file, CTDateAx.type, (XmlOptions)null);
        }
        
        public static CTDateAx parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDateAx)getTypeLoader().parse(file, CTDateAx.type, xmlOptions);
        }
        
        public static CTDateAx parse(final URL url) throws XmlException, IOException {
            return (CTDateAx)getTypeLoader().parse(url, CTDateAx.type, (XmlOptions)null);
        }
        
        public static CTDateAx parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDateAx)getTypeLoader().parse(url, CTDateAx.type, xmlOptions);
        }
        
        public static CTDateAx parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTDateAx)getTypeLoader().parse(inputStream, CTDateAx.type, (XmlOptions)null);
        }
        
        public static CTDateAx parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDateAx)getTypeLoader().parse(inputStream, CTDateAx.type, xmlOptions);
        }
        
        public static CTDateAx parse(final Reader reader) throws XmlException, IOException {
            return (CTDateAx)getTypeLoader().parse(reader, CTDateAx.type, (XmlOptions)null);
        }
        
        public static CTDateAx parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTDateAx)getTypeLoader().parse(reader, CTDateAx.type, xmlOptions);
        }
        
        public static CTDateAx parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTDateAx)getTypeLoader().parse(xmlStreamReader, CTDateAx.type, (XmlOptions)null);
        }
        
        public static CTDateAx parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTDateAx)getTypeLoader().parse(xmlStreamReader, CTDateAx.type, xmlOptions);
        }
        
        public static CTDateAx parse(final Node node) throws XmlException {
            return (CTDateAx)getTypeLoader().parse(node, CTDateAx.type, (XmlOptions)null);
        }
        
        public static CTDateAx parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTDateAx)getTypeLoader().parse(node, CTDateAx.type, xmlOptions);
        }
        
        @Deprecated
        public static CTDateAx parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTDateAx)getTypeLoader().parse(xmlInputStream, CTDateAx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTDateAx parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTDateAx)getTypeLoader().parse(xmlInputStream, CTDateAx.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDateAx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTDateAx.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
