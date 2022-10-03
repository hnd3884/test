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

public interface CTValAx extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTValAx.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctvalaxd06etype");
    
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
    
    CTCrossBetween getCrossBetween();
    
    boolean isSetCrossBetween();
    
    void setCrossBetween(final CTCrossBetween p0);
    
    CTCrossBetween addNewCrossBetween();
    
    void unsetCrossBetween();
    
    CTAxisUnit getMajorUnit();
    
    boolean isSetMajorUnit();
    
    void setMajorUnit(final CTAxisUnit p0);
    
    CTAxisUnit addNewMajorUnit();
    
    void unsetMajorUnit();
    
    CTAxisUnit getMinorUnit();
    
    boolean isSetMinorUnit();
    
    void setMinorUnit(final CTAxisUnit p0);
    
    CTAxisUnit addNewMinorUnit();
    
    void unsetMinorUnit();
    
    CTDispUnits getDispUnits();
    
    boolean isSetDispUnits();
    
    void setDispUnits(final CTDispUnits p0);
    
    CTDispUnits addNewDispUnits();
    
    void unsetDispUnits();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTValAx.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTValAx newInstance() {
            return (CTValAx)getTypeLoader().newInstance(CTValAx.type, (XmlOptions)null);
        }
        
        public static CTValAx newInstance(final XmlOptions xmlOptions) {
            return (CTValAx)getTypeLoader().newInstance(CTValAx.type, xmlOptions);
        }
        
        public static CTValAx parse(final String s) throws XmlException {
            return (CTValAx)getTypeLoader().parse(s, CTValAx.type, (XmlOptions)null);
        }
        
        public static CTValAx parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTValAx)getTypeLoader().parse(s, CTValAx.type, xmlOptions);
        }
        
        public static CTValAx parse(final File file) throws XmlException, IOException {
            return (CTValAx)getTypeLoader().parse(file, CTValAx.type, (XmlOptions)null);
        }
        
        public static CTValAx parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTValAx)getTypeLoader().parse(file, CTValAx.type, xmlOptions);
        }
        
        public static CTValAx parse(final URL url) throws XmlException, IOException {
            return (CTValAx)getTypeLoader().parse(url, CTValAx.type, (XmlOptions)null);
        }
        
        public static CTValAx parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTValAx)getTypeLoader().parse(url, CTValAx.type, xmlOptions);
        }
        
        public static CTValAx parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTValAx)getTypeLoader().parse(inputStream, CTValAx.type, (XmlOptions)null);
        }
        
        public static CTValAx parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTValAx)getTypeLoader().parse(inputStream, CTValAx.type, xmlOptions);
        }
        
        public static CTValAx parse(final Reader reader) throws XmlException, IOException {
            return (CTValAx)getTypeLoader().parse(reader, CTValAx.type, (XmlOptions)null);
        }
        
        public static CTValAx parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTValAx)getTypeLoader().parse(reader, CTValAx.type, xmlOptions);
        }
        
        public static CTValAx parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTValAx)getTypeLoader().parse(xmlStreamReader, CTValAx.type, (XmlOptions)null);
        }
        
        public static CTValAx parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTValAx)getTypeLoader().parse(xmlStreamReader, CTValAx.type, xmlOptions);
        }
        
        public static CTValAx parse(final Node node) throws XmlException {
            return (CTValAx)getTypeLoader().parse(node, CTValAx.type, (XmlOptions)null);
        }
        
        public static CTValAx parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTValAx)getTypeLoader().parse(node, CTValAx.type, xmlOptions);
        }
        
        @Deprecated
        public static CTValAx parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTValAx)getTypeLoader().parse(xmlInputStream, CTValAx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTValAx parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTValAx)getTypeLoader().parse(xmlInputStream, CTValAx.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTValAx.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTValAx.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
