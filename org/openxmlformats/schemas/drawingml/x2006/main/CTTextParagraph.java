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

public interface CTTextParagraph extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextParagraph.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextparagraphcaf2type");
    
    CTTextParagraphProperties getPPr();
    
    boolean isSetPPr();
    
    void setPPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewPPr();
    
    void unsetPPr();
    
    List<CTRegularTextRun> getRList();
    
    @Deprecated
    CTRegularTextRun[] getRArray();
    
    CTRegularTextRun getRArray(final int p0);
    
    int sizeOfRArray();
    
    void setRArray(final CTRegularTextRun[] p0);
    
    void setRArray(final int p0, final CTRegularTextRun p1);
    
    CTRegularTextRun insertNewR(final int p0);
    
    CTRegularTextRun addNewR();
    
    void removeR(final int p0);
    
    List<CTTextLineBreak> getBrList();
    
    @Deprecated
    CTTextLineBreak[] getBrArray();
    
    CTTextLineBreak getBrArray(final int p0);
    
    int sizeOfBrArray();
    
    void setBrArray(final CTTextLineBreak[] p0);
    
    void setBrArray(final int p0, final CTTextLineBreak p1);
    
    CTTextLineBreak insertNewBr(final int p0);
    
    CTTextLineBreak addNewBr();
    
    void removeBr(final int p0);
    
    List<CTTextField> getFldList();
    
    @Deprecated
    CTTextField[] getFldArray();
    
    CTTextField getFldArray(final int p0);
    
    int sizeOfFldArray();
    
    void setFldArray(final CTTextField[] p0);
    
    void setFldArray(final int p0, final CTTextField p1);
    
    CTTextField insertNewFld(final int p0);
    
    CTTextField addNewFld();
    
    void removeFld(final int p0);
    
    CTTextCharacterProperties getEndParaRPr();
    
    boolean isSetEndParaRPr();
    
    void setEndParaRPr(final CTTextCharacterProperties p0);
    
    CTTextCharacterProperties addNewEndParaRPr();
    
    void unsetEndParaRPr();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextParagraph.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextParagraph newInstance() {
            return (CTTextParagraph)getTypeLoader().newInstance(CTTextParagraph.type, (XmlOptions)null);
        }
        
        public static CTTextParagraph newInstance(final XmlOptions xmlOptions) {
            return (CTTextParagraph)getTypeLoader().newInstance(CTTextParagraph.type, xmlOptions);
        }
        
        public static CTTextParagraph parse(final String s) throws XmlException {
            return (CTTextParagraph)getTypeLoader().parse(s, CTTextParagraph.type, (XmlOptions)null);
        }
        
        public static CTTextParagraph parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextParagraph)getTypeLoader().parse(s, CTTextParagraph.type, xmlOptions);
        }
        
        public static CTTextParagraph parse(final File file) throws XmlException, IOException {
            return (CTTextParagraph)getTypeLoader().parse(file, CTTextParagraph.type, (XmlOptions)null);
        }
        
        public static CTTextParagraph parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextParagraph)getTypeLoader().parse(file, CTTextParagraph.type, xmlOptions);
        }
        
        public static CTTextParagraph parse(final URL url) throws XmlException, IOException {
            return (CTTextParagraph)getTypeLoader().parse(url, CTTextParagraph.type, (XmlOptions)null);
        }
        
        public static CTTextParagraph parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextParagraph)getTypeLoader().parse(url, CTTextParagraph.type, xmlOptions);
        }
        
        public static CTTextParagraph parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextParagraph)getTypeLoader().parse(inputStream, CTTextParagraph.type, (XmlOptions)null);
        }
        
        public static CTTextParagraph parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextParagraph)getTypeLoader().parse(inputStream, CTTextParagraph.type, xmlOptions);
        }
        
        public static CTTextParagraph parse(final Reader reader) throws XmlException, IOException {
            return (CTTextParagraph)getTypeLoader().parse(reader, CTTextParagraph.type, (XmlOptions)null);
        }
        
        public static CTTextParagraph parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextParagraph)getTypeLoader().parse(reader, CTTextParagraph.type, xmlOptions);
        }
        
        public static CTTextParagraph parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextParagraph)getTypeLoader().parse(xmlStreamReader, CTTextParagraph.type, (XmlOptions)null);
        }
        
        public static CTTextParagraph parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextParagraph)getTypeLoader().parse(xmlStreamReader, CTTextParagraph.type, xmlOptions);
        }
        
        public static CTTextParagraph parse(final Node node) throws XmlException {
            return (CTTextParagraph)getTypeLoader().parse(node, CTTextParagraph.type, (XmlOptions)null);
        }
        
        public static CTTextParagraph parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextParagraph)getTypeLoader().parse(node, CTTextParagraph.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextParagraph parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextParagraph)getTypeLoader().parse(xmlInputStream, CTTextParagraph.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextParagraph parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextParagraph)getTypeLoader().parse(xmlInputStream, CTTextParagraph.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextParagraph.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextParagraph.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
