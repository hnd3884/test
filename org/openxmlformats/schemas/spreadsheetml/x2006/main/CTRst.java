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
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTRst extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTRst.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctrsta472type");
    
    String getT();
    
    STXstring xgetT();
    
    boolean isSetT();
    
    void setT(final String p0);
    
    void xsetT(final STXstring p0);
    
    void unsetT();
    
    List<CTRElt> getRList();
    
    @Deprecated
    CTRElt[] getRArray();
    
    CTRElt getRArray(final int p0);
    
    int sizeOfRArray();
    
    void setRArray(final CTRElt[] p0);
    
    void setRArray(final int p0, final CTRElt p1);
    
    CTRElt insertNewR(final int p0);
    
    CTRElt addNewR();
    
    void removeR(final int p0);
    
    List<CTPhoneticRun> getRPhList();
    
    @Deprecated
    CTPhoneticRun[] getRPhArray();
    
    CTPhoneticRun getRPhArray(final int p0);
    
    int sizeOfRPhArray();
    
    void setRPhArray(final CTPhoneticRun[] p0);
    
    void setRPhArray(final int p0, final CTPhoneticRun p1);
    
    CTPhoneticRun insertNewRPh(final int p0);
    
    CTPhoneticRun addNewRPh();
    
    void removeRPh(final int p0);
    
    CTPhoneticPr getPhoneticPr();
    
    boolean isSetPhoneticPr();
    
    void setPhoneticPr(final CTPhoneticPr p0);
    
    CTPhoneticPr addNewPhoneticPr();
    
    void unsetPhoneticPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTRst.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTRst newInstance() {
            return (CTRst)getTypeLoader().newInstance(CTRst.type, (XmlOptions)null);
        }
        
        public static CTRst newInstance(final XmlOptions xmlOptions) {
            return (CTRst)getTypeLoader().newInstance(CTRst.type, xmlOptions);
        }
        
        public static CTRst parse(final String s) throws XmlException {
            return (CTRst)getTypeLoader().parse(s, CTRst.type, (XmlOptions)null);
        }
        
        public static CTRst parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTRst)getTypeLoader().parse(s, CTRst.type, xmlOptions);
        }
        
        public static CTRst parse(final File file) throws XmlException, IOException {
            return (CTRst)getTypeLoader().parse(file, CTRst.type, (XmlOptions)null);
        }
        
        public static CTRst parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRst)getTypeLoader().parse(file, CTRst.type, xmlOptions);
        }
        
        public static CTRst parse(final URL url) throws XmlException, IOException {
            return (CTRst)getTypeLoader().parse(url, CTRst.type, (XmlOptions)null);
        }
        
        public static CTRst parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRst)getTypeLoader().parse(url, CTRst.type, xmlOptions);
        }
        
        public static CTRst parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTRst)getTypeLoader().parse(inputStream, CTRst.type, (XmlOptions)null);
        }
        
        public static CTRst parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRst)getTypeLoader().parse(inputStream, CTRst.type, xmlOptions);
        }
        
        public static CTRst parse(final Reader reader) throws XmlException, IOException {
            return (CTRst)getTypeLoader().parse(reader, CTRst.type, (XmlOptions)null);
        }
        
        public static CTRst parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTRst)getTypeLoader().parse(reader, CTRst.type, xmlOptions);
        }
        
        public static CTRst parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTRst)getTypeLoader().parse(xmlStreamReader, CTRst.type, (XmlOptions)null);
        }
        
        public static CTRst parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTRst)getTypeLoader().parse(xmlStreamReader, CTRst.type, xmlOptions);
        }
        
        public static CTRst parse(final Node node) throws XmlException {
            return (CTRst)getTypeLoader().parse(node, CTRst.type, (XmlOptions)null);
        }
        
        public static CTRst parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTRst)getTypeLoader().parse(node, CTRst.type, xmlOptions);
        }
        
        @Deprecated
        public static CTRst parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTRst)getTypeLoader().parse(xmlInputStream, CTRst.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTRst parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTRst)getTypeLoader().parse(xmlInputStream, CTRst.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRst.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTRst.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
