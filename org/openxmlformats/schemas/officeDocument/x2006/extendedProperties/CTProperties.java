package org.openxmlformats.schemas.officeDocument.x2006.extendedProperties;

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
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlInt;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;

public interface CTProperties extends XmlObject
{
    public static final SchemaType type = (SchemaType)XmlBeans.typeSystemForClassLoader(CTProperties.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sD023D6490046BA0250A839A9AD24C443").resolveHandle("ctproperties3f10type");
    
    String getTemplate();
    
    XmlString xgetTemplate();
    
    boolean isSetTemplate();
    
    void setTemplate(final String p0);
    
    void xsetTemplate(final XmlString p0);
    
    void unsetTemplate();
    
    String getManager();
    
    XmlString xgetManager();
    
    boolean isSetManager();
    
    void setManager(final String p0);
    
    void xsetManager(final XmlString p0);
    
    void unsetManager();
    
    String getCompany();
    
    XmlString xgetCompany();
    
    boolean isSetCompany();
    
    void setCompany(final String p0);
    
    void xsetCompany(final XmlString p0);
    
    void unsetCompany();
    
    int getPages();
    
    XmlInt xgetPages();
    
    boolean isSetPages();
    
    void setPages(final int p0);
    
    void xsetPages(final XmlInt p0);
    
    void unsetPages();
    
    int getWords();
    
    XmlInt xgetWords();
    
    boolean isSetWords();
    
    void setWords(final int p0);
    
    void xsetWords(final XmlInt p0);
    
    void unsetWords();
    
    int getCharacters();
    
    XmlInt xgetCharacters();
    
    boolean isSetCharacters();
    
    void setCharacters(final int p0);
    
    void xsetCharacters(final XmlInt p0);
    
    void unsetCharacters();
    
    String getPresentationFormat();
    
    XmlString xgetPresentationFormat();
    
    boolean isSetPresentationFormat();
    
    void setPresentationFormat(final String p0);
    
    void xsetPresentationFormat(final XmlString p0);
    
    void unsetPresentationFormat();
    
    int getLines();
    
    XmlInt xgetLines();
    
    boolean isSetLines();
    
    void setLines(final int p0);
    
    void xsetLines(final XmlInt p0);
    
    void unsetLines();
    
    int getParagraphs();
    
    XmlInt xgetParagraphs();
    
    boolean isSetParagraphs();
    
    void setParagraphs(final int p0);
    
    void xsetParagraphs(final XmlInt p0);
    
    void unsetParagraphs();
    
    int getSlides();
    
    XmlInt xgetSlides();
    
    boolean isSetSlides();
    
    void setSlides(final int p0);
    
    void xsetSlides(final XmlInt p0);
    
    void unsetSlides();
    
    int getNotes();
    
    XmlInt xgetNotes();
    
    boolean isSetNotes();
    
    void setNotes(final int p0);
    
    void xsetNotes(final XmlInt p0);
    
    void unsetNotes();
    
    int getTotalTime();
    
    XmlInt xgetTotalTime();
    
    boolean isSetTotalTime();
    
    void setTotalTime(final int p0);
    
    void xsetTotalTime(final XmlInt p0);
    
    void unsetTotalTime();
    
    int getHiddenSlides();
    
    XmlInt xgetHiddenSlides();
    
    boolean isSetHiddenSlides();
    
    void setHiddenSlides(final int p0);
    
    void xsetHiddenSlides(final XmlInt p0);
    
    void unsetHiddenSlides();
    
    int getMMClips();
    
    XmlInt xgetMMClips();
    
    boolean isSetMMClips();
    
    void setMMClips(final int p0);
    
    void xsetMMClips(final XmlInt p0);
    
    void unsetMMClips();
    
    boolean getScaleCrop();
    
    XmlBoolean xgetScaleCrop();
    
    boolean isSetScaleCrop();
    
    void setScaleCrop(final boolean p0);
    
    void xsetScaleCrop(final XmlBoolean p0);
    
    void unsetScaleCrop();
    
    CTVectorVariant getHeadingPairs();
    
    boolean isSetHeadingPairs();
    
    void setHeadingPairs(final CTVectorVariant p0);
    
    CTVectorVariant addNewHeadingPairs();
    
    void unsetHeadingPairs();
    
    CTVectorLpstr getTitlesOfParts();
    
    boolean isSetTitlesOfParts();
    
    void setTitlesOfParts(final CTVectorLpstr p0);
    
    CTVectorLpstr addNewTitlesOfParts();
    
    void unsetTitlesOfParts();
    
    boolean getLinksUpToDate();
    
    XmlBoolean xgetLinksUpToDate();
    
    boolean isSetLinksUpToDate();
    
    void setLinksUpToDate(final boolean p0);
    
    void xsetLinksUpToDate(final XmlBoolean p0);
    
    void unsetLinksUpToDate();
    
    int getCharactersWithSpaces();
    
    XmlInt xgetCharactersWithSpaces();
    
    boolean isSetCharactersWithSpaces();
    
    void setCharactersWithSpaces(final int p0);
    
    void xsetCharactersWithSpaces(final XmlInt p0);
    
    void unsetCharactersWithSpaces();
    
    boolean getSharedDoc();
    
    XmlBoolean xgetSharedDoc();
    
    boolean isSetSharedDoc();
    
    void setSharedDoc(final boolean p0);
    
    void xsetSharedDoc(final XmlBoolean p0);
    
    void unsetSharedDoc();
    
    String getHyperlinkBase();
    
    XmlString xgetHyperlinkBase();
    
    boolean isSetHyperlinkBase();
    
    void setHyperlinkBase(final String p0);
    
    void xsetHyperlinkBase(final XmlString p0);
    
    void unsetHyperlinkBase();
    
    CTVectorVariant getHLinks();
    
    boolean isSetHLinks();
    
    void setHLinks(final CTVectorVariant p0);
    
    CTVectorVariant addNewHLinks();
    
    void unsetHLinks();
    
    boolean getHyperlinksChanged();
    
    XmlBoolean xgetHyperlinksChanged();
    
    boolean isSetHyperlinksChanged();
    
    void setHyperlinksChanged(final boolean p0);
    
    void xsetHyperlinksChanged(final XmlBoolean p0);
    
    void unsetHyperlinksChanged();
    
    CTDigSigBlob getDigSig();
    
    boolean isSetDigSig();
    
    void setDigSig(final CTDigSigBlob p0);
    
    CTDigSigBlob addNewDigSig();
    
    void unsetDigSig();
    
    String getApplication();
    
    XmlString xgetApplication();
    
    boolean isSetApplication();
    
    void setApplication(final String p0);
    
    void xsetApplication(final XmlString p0);
    
    void unsetApplication();
    
    String getAppVersion();
    
    XmlString xgetAppVersion();
    
    boolean isSetAppVersion();
    
    void setAppVersion(final String p0);
    
    void xsetAppVersion(final XmlString p0);
    
    void unsetAppVersion();
    
    int getDocSecurity();
    
    XmlInt xgetDocSecurity();
    
    boolean isSetDocSecurity();
    
    void setDocSecurity(final int p0);
    
    void xsetDocSecurity(final XmlInt p0);
    
    void unsetDocSecurity();
    
    public static final class Factory
    {
        private static SoftReference<SchemaTypeLoader> typeLoader;
        
        private static synchronized SchemaTypeLoader getTypeLoader() {
            SchemaTypeLoader typeLoaderForClassLoader = (Factory.typeLoader == null) ? null : Factory.typeLoader.get();
            if (typeLoaderForClassLoader == null) {
                typeLoaderForClassLoader = XmlBeans.typeLoaderForClassLoader(CTProperties.class.getClassLoader());
                Factory.typeLoader = new SoftReference<SchemaTypeLoader>(typeLoaderForClassLoader);
            }
            return typeLoaderForClassLoader;
        }
        
        public static CTProperties newInstance() {
            return (CTProperties)getTypeLoader().newInstance(CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties newInstance(final XmlOptions xmlOptions) {
            return (CTProperties)getTypeLoader().newInstance(CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final String s) throws XmlException {
            return (CTProperties)getTypeLoader().parse(s, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final String s, final XmlOptions xmlOptions) throws XmlException {
            return (CTProperties)getTypeLoader().parse(s, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final File file) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(file, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final File file, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(file, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final URL url) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(url, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final URL url, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(url, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final InputStream inputStream) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(inputStream, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final InputStream inputStream, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(inputStream, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final Reader reader) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(reader, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final Reader reader, final XmlOptions xmlOptions) throws XmlException, IOException {
            return (CTProperties)getTypeLoader().parse(reader, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final XMLStreamReader xmlStreamReader) throws XmlException {
            return (CTProperties)getTypeLoader().parse(xmlStreamReader, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final XMLStreamReader xmlStreamReader, final XmlOptions xmlOptions) throws XmlException {
            return (CTProperties)getTypeLoader().parse(xmlStreamReader, CTProperties.type, xmlOptions);
        }
        
        public static CTProperties parse(final Node node) throws XmlException {
            return (CTProperties)getTypeLoader().parse(node, CTProperties.type, (XmlOptions)null);
        }
        
        public static CTProperties parse(final Node node, final XmlOptions xmlOptions) throws XmlException {
            return (CTProperties)getTypeLoader().parse(node, CTProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static CTProperties parse(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return (CTProperties)getTypeLoader().parse(xmlInputStream, CTProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static CTProperties parse(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return (CTProperties)getTypeLoader().parse(xmlInputStream, CTProperties.type, xmlOptions);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTProperties.type, (XmlOptions)null);
        }
        
        @Deprecated
        public static XMLInputStream newValidatingXMLInputStream(final XMLInputStream xmlInputStream, final XmlOptions xmlOptions) throws XmlException, XMLStreamException {
            return getTypeLoader().newValidatingXMLInputStream(xmlInputStream, CTProperties.type, xmlOptions);
        }
        
        private Factory() {
        }
    }
}
