package org.openxmlformats.schemas.drawingml.x2006.main;

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

public interface CTBackgroundFillStyleList extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTBackgroundFillStyleList.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctbackgroundfillstylelist13cftype");
    
    List<CTNoFillProperties> getNoFillList();
    
    @Deprecated
    CTNoFillProperties[] getNoFillArray();
    
    CTNoFillProperties getNoFillArray(final int p0);
    
    int sizeOfNoFillArray();
    
    void setNoFillArray(final CTNoFillProperties[] p0);
    
    void setNoFillArray(final int p0, final CTNoFillProperties p1);
    
    CTNoFillProperties insertNewNoFill(final int p0);
    
    CTNoFillProperties addNewNoFill();
    
    void removeNoFill(final int p0);
    
    List<CTSolidColorFillProperties> getSolidFillList();
    
    @Deprecated
    CTSolidColorFillProperties[] getSolidFillArray();
    
    CTSolidColorFillProperties getSolidFillArray(final int p0);
    
    int sizeOfSolidFillArray();
    
    void setSolidFillArray(final CTSolidColorFillProperties[] p0);
    
    void setSolidFillArray(final int p0, final CTSolidColorFillProperties p1);
    
    CTSolidColorFillProperties insertNewSolidFill(final int p0);
    
    CTSolidColorFillProperties addNewSolidFill();
    
    void removeSolidFill(final int p0);
    
    List<CTGradientFillProperties> getGradFillList();
    
    @Deprecated
    CTGradientFillProperties[] getGradFillArray();
    
    CTGradientFillProperties getGradFillArray(final int p0);
    
    int sizeOfGradFillArray();
    
    void setGradFillArray(final CTGradientFillProperties[] p0);
    
    void setGradFillArray(final int p0, final CTGradientFillProperties p1);
    
    CTGradientFillProperties insertNewGradFill(final int p0);
    
    CTGradientFillProperties addNewGradFill();
    
    void removeGradFill(final int p0);
    
    List<CTBlipFillProperties> getBlipFillList();
    
    @Deprecated
    CTBlipFillProperties[] getBlipFillArray();
    
    CTBlipFillProperties getBlipFillArray(final int p0);
    
    int sizeOfBlipFillArray();
    
    void setBlipFillArray(final CTBlipFillProperties[] p0);
    
    void setBlipFillArray(final int p0, final CTBlipFillProperties p1);
    
    CTBlipFillProperties insertNewBlipFill(final int p0);
    
    CTBlipFillProperties addNewBlipFill();
    
    void removeBlipFill(final int p0);
    
    List<CTPatternFillProperties> getPattFillList();
    
    @Deprecated
    CTPatternFillProperties[] getPattFillArray();
    
    CTPatternFillProperties getPattFillArray(final int p0);
    
    int sizeOfPattFillArray();
    
    void setPattFillArray(final CTPatternFillProperties[] p0);
    
    void setPattFillArray(final int p0, final CTPatternFillProperties p1);
    
    CTPatternFillProperties insertNewPattFill(final int p0);
    
    CTPatternFillProperties addNewPattFill();
    
    void removePattFill(final int p0);
    
    List<CTGroupFillProperties> getGrpFillList();
    
    @Deprecated
    CTGroupFillProperties[] getGrpFillArray();
    
    CTGroupFillProperties getGrpFillArray(final int p0);
    
    int sizeOfGrpFillArray();
    
    void setGrpFillArray(final CTGroupFillProperties[] p0);
    
    void setGrpFillArray(final int p0, final CTGroupFillProperties p1);
    
    CTGroupFillProperties insertNewGrpFill(final int p0);
    
    CTGroupFillProperties addNewGrpFill();
    
    void removeGrpFill(final int p0);
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTBackgroundFillStyleList.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTBackgroundFillStyleList newInstance() {
            return (CTBackgroundFillStyleList)getTypeLoader().newInstance(CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        public static CTBackgroundFillStyleList newInstance(final XmlOptions xmlOptions) {
            return (CTBackgroundFillStyleList)getTypeLoader().newInstance(CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        public static CTBackgroundFillStyleList parse(final String s) throws XmlException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(s, CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        public static CTBackgroundFillStyleList parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(s, CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        public static CTBackgroundFillStyleList parse(final File file) throws XmlException, IOException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(file, CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        public static CTBackgroundFillStyleList parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(file, CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        public static CTBackgroundFillStyleList parse(final URL url) throws XmlException, IOException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(url, CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        public static CTBackgroundFillStyleList parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(url, CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        public static CTBackgroundFillStyleList parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(inputStream, CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        public static CTBackgroundFillStyleList parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(inputStream, CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        public static CTBackgroundFillStyleList parse(final Reader reader) throws XmlException, IOException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(reader, CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        public static CTBackgroundFillStyleList parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(reader, CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        public static CTBackgroundFillStyleList parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(xmlStreamReader, CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        public static CTBackgroundFillStyleList parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(xmlStreamReader, CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        public static CTBackgroundFillStyleList parse(final Node node) throws XmlException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(node, CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        public static CTBackgroundFillStyleList parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(node, CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        @Deprecated
        public static CTBackgroundFillStyleList parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(xmlInputStream, CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTBackgroundFillStyleList parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTBackgroundFillStyleList)getTypeLoader().parse(xmlInputStream, CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBackgroundFillStyleList.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTBackgroundFillStyleList.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
