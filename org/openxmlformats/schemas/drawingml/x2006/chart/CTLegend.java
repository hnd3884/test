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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTLegend extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTLegend.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctlegenda54ftype");
    
    CTLegendPos getLegendPos();
    
    boolean isSetLegendPos();
    
    void setLegendPos(final CTLegendPos p0);
    
    CTLegendPos addNewLegendPos();
    
    void unsetLegendPos();
    
    List<CTLegendEntry> getLegendEntryList();
    
    @Deprecated
    CTLegendEntry[] getLegendEntryArray();
    
    CTLegendEntry getLegendEntryArray(final int p0);
    
    int sizeOfLegendEntryArray();
    
    void setLegendEntryArray(final CTLegendEntry[] p0);
    
    void setLegendEntryArray(final int p0, final CTLegendEntry p1);
    
    CTLegendEntry insertNewLegendEntry(final int p0);
    
    CTLegendEntry addNewLegendEntry();
    
    void removeLegendEntry(final int p0);
    
    CTLayout getLayout();
    
    boolean isSetLayout();
    
    void setLayout(final CTLayout p0);
    
    CTLayout addNewLayout();
    
    void unsetLayout();
    
    CTBoolean getOverlay();
    
    boolean isSetOverlay();
    
    void setOverlay(final CTBoolean p0);
    
    CTBoolean addNewOverlay();
    
    void unsetOverlay();
    
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
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTLegend.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTLegend newInstance() {
            return (CTLegend)getTypeLoader().newInstance(CTLegend.type, (XmlOptions)null);
        }
        
        public static CTLegend newInstance(final XmlOptions xmlOptions) {
            return (CTLegend)getTypeLoader().newInstance(CTLegend.type, xmlOptions);
        }
        
        public static CTLegend parse(final String s) throws XmlException {
            return (CTLegend)getTypeLoader().parse(s, CTLegend.type, (XmlOptions)null);
        }
        
        public static CTLegend parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegend)getTypeLoader().parse(s, CTLegend.type, xmlOptions);
        }
        
        public static CTLegend parse(final File file) throws XmlException, IOException {
            return (CTLegend)getTypeLoader().parse(file, CTLegend.type, (XmlOptions)null);
        }
        
        public static CTLegend parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegend)getTypeLoader().parse(file, CTLegend.type, xmlOptions);
        }
        
        public static CTLegend parse(final URL url) throws XmlException, IOException {
            return (CTLegend)getTypeLoader().parse(url, CTLegend.type, (XmlOptions)null);
        }
        
        public static CTLegend parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegend)getTypeLoader().parse(url, CTLegend.type, xmlOptions);
        }
        
        public static CTLegend parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTLegend)getTypeLoader().parse(inputStream, CTLegend.type, (XmlOptions)null);
        }
        
        public static CTLegend parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegend)getTypeLoader().parse(inputStream, CTLegend.type, xmlOptions);
        }
        
        public static CTLegend parse(final Reader reader) throws XmlException, IOException {
            return (CTLegend)getTypeLoader().parse(reader, CTLegend.type, (XmlOptions)null);
        }
        
        public static CTLegend parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTLegend)getTypeLoader().parse(reader, CTLegend.type, xmlOptions);
        }
        
        public static CTLegend parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTLegend)getTypeLoader().parse(xmlStreamReader, CTLegend.type, (XmlOptions)null);
        }
        
        public static CTLegend parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegend)getTypeLoader().parse(xmlStreamReader, CTLegend.type, xmlOptions);
        }
        
        public static CTLegend parse(final Node node) throws XmlException {
            return (CTLegend)getTypeLoader().parse(node, CTLegend.type, (XmlOptions)null);
        }
        
        public static CTLegend parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTLegend)getTypeLoader().parse(node, CTLegend.type, xmlOptions);
        }
        
        @Deprecated
        public static CTLegend parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTLegend)getTypeLoader().parse(xmlInputStream, CTLegend.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTLegend parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTLegend)getTypeLoader().parse(xmlInputStream, CTLegend.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLegend.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTLegend.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
