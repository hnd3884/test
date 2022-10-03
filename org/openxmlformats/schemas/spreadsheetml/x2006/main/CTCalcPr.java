package org.openxmlformats.schemas.spreadsheetml.x2006.main;

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
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTCalcPr extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTCalcPr.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctcalcprd480type");
    
    long getCalcId();
    
    XmlUnsignedInt xgetCalcId();
    
    boolean isSetCalcId();
    
    void setCalcId(final long p0);
    
    void xsetCalcId(final XmlUnsignedInt p0);
    
    void unsetCalcId();
    
    STCalcMode.Enum getCalcMode();
    
    STCalcMode xgetCalcMode();
    
    boolean isSetCalcMode();
    
    void setCalcMode(final STCalcMode.Enum p0);
    
    void xsetCalcMode(final STCalcMode p0);
    
    void unsetCalcMode();
    
    boolean getFullCalcOnLoad();
    
    XmlBoolean xgetFullCalcOnLoad();
    
    boolean isSetFullCalcOnLoad();
    
    void setFullCalcOnLoad(final boolean p0);
    
    void xsetFullCalcOnLoad(final XmlBoolean p0);
    
    void unsetFullCalcOnLoad();
    
    STRefMode.Enum getRefMode();
    
    STRefMode xgetRefMode();
    
    boolean isSetRefMode();
    
    void setRefMode(final STRefMode.Enum p0);
    
    void xsetRefMode(final STRefMode p0);
    
    void unsetRefMode();
    
    boolean getIterate();
    
    XmlBoolean xgetIterate();
    
    boolean isSetIterate();
    
    void setIterate(final boolean p0);
    
    void xsetIterate(final XmlBoolean p0);
    
    void unsetIterate();
    
    long getIterateCount();
    
    XmlUnsignedInt xgetIterateCount();
    
    boolean isSetIterateCount();
    
    void setIterateCount(final long p0);
    
    void xsetIterateCount(final XmlUnsignedInt p0);
    
    void unsetIterateCount();
    
    double getIterateDelta();
    
    XmlDouble xgetIterateDelta();
    
    boolean isSetIterateDelta();
    
    void setIterateDelta(final double p0);
    
    void xsetIterateDelta(final XmlDouble p0);
    
    void unsetIterateDelta();
    
    boolean getFullPrecision();
    
    XmlBoolean xgetFullPrecision();
    
    boolean isSetFullPrecision();
    
    void setFullPrecision(final boolean p0);
    
    void xsetFullPrecision(final XmlBoolean p0);
    
    void unsetFullPrecision();
    
    boolean getCalcCompleted();
    
    XmlBoolean xgetCalcCompleted();
    
    boolean isSetCalcCompleted();
    
    void setCalcCompleted(final boolean p0);
    
    void xsetCalcCompleted(final XmlBoolean p0);
    
    void unsetCalcCompleted();
    
    boolean getCalcOnSave();
    
    XmlBoolean xgetCalcOnSave();
    
    boolean isSetCalcOnSave();
    
    void setCalcOnSave(final boolean p0);
    
    void xsetCalcOnSave(final XmlBoolean p0);
    
    void unsetCalcOnSave();
    
    boolean getConcurrentCalc();
    
    XmlBoolean xgetConcurrentCalc();
    
    boolean isSetConcurrentCalc();
    
    void setConcurrentCalc(final boolean p0);
    
    void xsetConcurrentCalc(final XmlBoolean p0);
    
    void unsetConcurrentCalc();
    
    long getConcurrentManualCount();
    
    XmlUnsignedInt xgetConcurrentManualCount();
    
    boolean isSetConcurrentManualCount();
    
    void setConcurrentManualCount(final long p0);
    
    void xsetConcurrentManualCount(final XmlUnsignedInt p0);
    
    void unsetConcurrentManualCount();
    
    boolean getForceFullCalc();
    
    XmlBoolean xgetForceFullCalc();
    
    boolean isSetForceFullCalc();
    
    void setForceFullCalc(final boolean p0);
    
    void xsetForceFullCalc(final XmlBoolean p0);
    
    void unsetForceFullCalc();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTCalcPr.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTCalcPr newInstance() {
            return (CTCalcPr)getTypeLoader().newInstance(CTCalcPr.type, (XmlOptions)null);
        }
        
        public static CTCalcPr newInstance(final XmlOptions xmlOptions) {
            return (CTCalcPr)getTypeLoader().newInstance(CTCalcPr.type, xmlOptions);
        }
        
        public static CTCalcPr parse(final String s) throws XmlException {
            return (CTCalcPr)getTypeLoader().parse(s, CTCalcPr.type, (XmlOptions)null);
        }
        
        public static CTCalcPr parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTCalcPr)getTypeLoader().parse(s, CTCalcPr.type, xmlOptions);
        }
        
        public static CTCalcPr parse(final File file) throws XmlException, IOException {
            return (CTCalcPr)getTypeLoader().parse(file, CTCalcPr.type, (XmlOptions)null);
        }
        
        public static CTCalcPr parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcPr)getTypeLoader().parse(file, CTCalcPr.type, xmlOptions);
        }
        
        public static CTCalcPr parse(final URL url) throws XmlException, IOException {
            return (CTCalcPr)getTypeLoader().parse(url, CTCalcPr.type, (XmlOptions)null);
        }
        
        public static CTCalcPr parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcPr)getTypeLoader().parse(url, CTCalcPr.type, xmlOptions);
        }
        
        public static CTCalcPr parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTCalcPr)getTypeLoader().parse(inputStream, CTCalcPr.type, (XmlOptions)null);
        }
        
        public static CTCalcPr parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcPr)getTypeLoader().parse(inputStream, CTCalcPr.type, xmlOptions);
        }
        
        public static CTCalcPr parse(final Reader reader) throws XmlException, IOException {
            return (CTCalcPr)getTypeLoader().parse(reader, CTCalcPr.type, (XmlOptions)null);
        }
        
        public static CTCalcPr parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTCalcPr)getTypeLoader().parse(reader, CTCalcPr.type, xmlOptions);
        }
        
        public static CTCalcPr parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTCalcPr)getTypeLoader().parse(xmlStreamReader, CTCalcPr.type, (XmlOptions)null);
        }
        
        public static CTCalcPr parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTCalcPr)getTypeLoader().parse(xmlStreamReader, CTCalcPr.type, xmlOptions);
        }
        
        public static CTCalcPr parse(final Node node) throws XmlException {
            return (CTCalcPr)getTypeLoader().parse(node, CTCalcPr.type, (XmlOptions)null);
        }
        
        public static CTCalcPr parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTCalcPr)getTypeLoader().parse(node, CTCalcPr.type, xmlOptions);
        }
        
        @Deprecated
        public static CTCalcPr parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTCalcPr)getTypeLoader().parse(xmlInputStream, CTCalcPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTCalcPr parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTCalcPr)getTypeLoader().parse(xmlInputStream, CTCalcPr.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCalcPr.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTCalcPr.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
