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
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTTextListStyle extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTTextListStyle.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("cttextliststyleab77type");
    
    CTTextParagraphProperties getDefPPr();
    
    boolean isSetDefPPr();
    
    void setDefPPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewDefPPr();
    
    void unsetDefPPr();
    
    CTTextParagraphProperties getLvl1PPr();
    
    boolean isSetLvl1PPr();
    
    void setLvl1PPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewLvl1PPr();
    
    void unsetLvl1PPr();
    
    CTTextParagraphProperties getLvl2PPr();
    
    boolean isSetLvl2PPr();
    
    void setLvl2PPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewLvl2PPr();
    
    void unsetLvl2PPr();
    
    CTTextParagraphProperties getLvl3PPr();
    
    boolean isSetLvl3PPr();
    
    void setLvl3PPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewLvl3PPr();
    
    void unsetLvl3PPr();
    
    CTTextParagraphProperties getLvl4PPr();
    
    boolean isSetLvl4PPr();
    
    void setLvl4PPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewLvl4PPr();
    
    void unsetLvl4PPr();
    
    CTTextParagraphProperties getLvl5PPr();
    
    boolean isSetLvl5PPr();
    
    void setLvl5PPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewLvl5PPr();
    
    void unsetLvl5PPr();
    
    CTTextParagraphProperties getLvl6PPr();
    
    boolean isSetLvl6PPr();
    
    void setLvl6PPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewLvl6PPr();
    
    void unsetLvl6PPr();
    
    CTTextParagraphProperties getLvl7PPr();
    
    boolean isSetLvl7PPr();
    
    void setLvl7PPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewLvl7PPr();
    
    void unsetLvl7PPr();
    
    CTTextParagraphProperties getLvl8PPr();
    
    boolean isSetLvl8PPr();
    
    void setLvl8PPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewLvl8PPr();
    
    void unsetLvl8PPr();
    
    CTTextParagraphProperties getLvl9PPr();
    
    boolean isSetLvl9PPr();
    
    void setLvl9PPr(final CTTextParagraphProperties p0);
    
    CTTextParagraphProperties addNewLvl9PPr();
    
    void unsetLvl9PPr();
    
    CTOfficeArtExtensionList getExtLst();
    
    boolean isSetExtLst();
    
    void setExtLst(final CTOfficeArtExtensionList p0);
    
    CTOfficeArtExtensionList addNewExtLst();
    
    void unsetExtLst();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTTextListStyle.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTTextListStyle newInstance() {
            return (CTTextListStyle)getTypeLoader().newInstance(CTTextListStyle.type, (XmlOptions)null);
        }
        
        public static CTTextListStyle newInstance(final XmlOptions xmlOptions) {
            return (CTTextListStyle)getTypeLoader().newInstance(CTTextListStyle.type, xmlOptions);
        }
        
        public static CTTextListStyle parse(final String s) throws XmlException {
            return (CTTextListStyle)getTypeLoader().parse(s, CTTextListStyle.type, (XmlOptions)null);
        }
        
        public static CTTextListStyle parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextListStyle)getTypeLoader().parse(s, CTTextListStyle.type, xmlOptions);
        }
        
        public static CTTextListStyle parse(final File file) throws XmlException, IOException {
            return (CTTextListStyle)getTypeLoader().parse(file, CTTextListStyle.type, (XmlOptions)null);
        }
        
        public static CTTextListStyle parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextListStyle)getTypeLoader().parse(file, CTTextListStyle.type, xmlOptions);
        }
        
        public static CTTextListStyle parse(final URL url) throws XmlException, IOException {
            return (CTTextListStyle)getTypeLoader().parse(url, CTTextListStyle.type, (XmlOptions)null);
        }
        
        public static CTTextListStyle parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextListStyle)getTypeLoader().parse(url, CTTextListStyle.type, xmlOptions);
        }
        
        public static CTTextListStyle parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTTextListStyle)getTypeLoader().parse(inputStream, CTTextListStyle.type, (XmlOptions)null);
        }
        
        public static CTTextListStyle parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextListStyle)getTypeLoader().parse(inputStream, CTTextListStyle.type, xmlOptions);
        }
        
        public static CTTextListStyle parse(final Reader reader) throws XmlException, IOException {
            return (CTTextListStyle)getTypeLoader().parse(reader, CTTextListStyle.type, (XmlOptions)null);
        }
        
        public static CTTextListStyle parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTTextListStyle)getTypeLoader().parse(reader, CTTextListStyle.type, xmlOptions);
        }
        
        public static CTTextListStyle parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTTextListStyle)getTypeLoader().parse(xmlStreamReader, CTTextListStyle.type, (XmlOptions)null);
        }
        
        public static CTTextListStyle parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextListStyle)getTypeLoader().parse(xmlStreamReader, CTTextListStyle.type, xmlOptions);
        }
        
        public static CTTextListStyle parse(final Node node) throws XmlException {
            return (CTTextListStyle)getTypeLoader().parse(node, CTTextListStyle.type, (XmlOptions)null);
        }
        
        public static CTTextListStyle parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTTextListStyle)getTypeLoader().parse(node, CTTextListStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static CTTextListStyle parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTTextListStyle)getTypeLoader().parse(xmlInputStream, CTTextListStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTTextListStyle parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTTextListStyle)getTypeLoader().parse(xmlInputStream, CTTextListStyle.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextListStyle.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTTextListStyle.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
