package org.apache.poi.ooxml.extractor;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.util.NotImplemented;
import java.lang.reflect.Method;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import java.util.List;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.poifs.filesystem.Entry;
import java.util.ArrayList;
import org.apache.poi.util.Removal;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.xssf.extractor.XSSFBEventBasedExcelExtractor;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.extractor.XSLFPowerPointExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xdgf.extractor.XDGFVisioExtractor;
import org.apache.poi.poifs.filesystem.FileMagic;
import java.io.InputStream;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.util.IOUtils;
import java.io.Closeable;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.extractor.POITextExtractor;
import java.io.File;
import org.apache.poi.extractor.OLE2ExtractorFactory;
import org.apache.poi.util.POILogger;

public final class ExtractorFactory
{
    private static final POILogger logger;
    public static final String CORE_DOCUMENT_REL = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument";
    private static final String VISIO_DOCUMENT_REL = "http://schemas.microsoft.com/visio/2010/relationships/document";
    private static final String STRICT_DOCUMENT_REL = "http://purl.oclc.org/ooxml/officeDocument/relationships/officeDocument";
    
    private ExtractorFactory() {
    }
    
    public static boolean getThreadPrefersEventExtractors() {
        return OLE2ExtractorFactory.getThreadPrefersEventExtractors();
    }
    
    public static Boolean getAllThreadsPreferEventExtractors() {
        return OLE2ExtractorFactory.getAllThreadsPreferEventExtractors();
    }
    
    public static void setThreadPrefersEventExtractors(final boolean preferEventExtractors) {
        OLE2ExtractorFactory.setThreadPrefersEventExtractors(preferEventExtractors);
    }
    
    public static void setAllThreadsPreferEventExtractors(final Boolean preferEventExtractors) {
        OLE2ExtractorFactory.setAllThreadsPreferEventExtractors(preferEventExtractors);
    }
    
    public static boolean getPreferEventExtractor() {
        return OLE2ExtractorFactory.getPreferEventExtractor();
    }
    
    public static <T extends POITextExtractor> T createExtractor(final File f) throws IOException, OpenXML4JException, XmlException {
        POIFSFileSystem fs = null;
        try {
            fs = new POIFSFileSystem(f);
            if (fs.getRoot().hasEntry("EncryptedPackage")) {
                return (T)createEncryptedOOXMLExtractor(fs);
            }
            final POITextExtractor extractor = createExtractor(fs);
            extractor.setFilesystem((Closeable)fs);
            return (T)extractor;
        }
        catch (final OfficeXmlFileException e) {
            IOUtils.closeQuietly((Closeable)fs);
            final OPCPackage pkg = OPCPackage.open(f.toString(), PackageAccess.READ);
            final T t = (T)createExtractor(pkg);
            t.setFilesystem((Closeable)pkg);
            return t;
        }
        catch (final NotOLE2FileException ne) {
            IOUtils.closeQuietly((Closeable)fs);
            throw new IllegalArgumentException("Your File was neither an OLE2 file, nor an OOXML file", (Throwable)ne);
        }
        catch (final OpenXML4JException | Error | RuntimeException | IOException | XmlException e2) {
            IOUtils.closeQuietly((Closeable)fs);
            throw e2;
        }
    }
    
    public static POITextExtractor createExtractor(final InputStream inp) throws IOException, OpenXML4JException, XmlException {
        final InputStream is = FileMagic.prepareToCheckMagic(inp);
        final FileMagic fm = FileMagic.valueOf(is);
        switch (fm) {
            case OLE2: {
                final POIFSFileSystem fs = new POIFSFileSystem(is);
                final boolean isEncrypted = fs.getRoot().hasEntry("EncryptedPackage");
                return isEncrypted ? createEncryptedOOXMLExtractor(fs) : createExtractor(fs);
            }
            case OOXML: {
                return createExtractor(OPCPackage.open(is));
            }
            default: {
                throw new IllegalArgumentException("Your InputStream was neither an OLE2 stream, nor an OOXML stream, found type: " + fm);
            }
        }
    }
    
    public static POITextExtractor createExtractor(final OPCPackage pkg) throws IOException, OpenXML4JException, XmlException {
        try {
            PackageRelationshipCollection core = pkg.getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument");
            if (core.size() == 0) {
                core = pkg.getRelationshipsByType("http://purl.oclc.org/ooxml/officeDocument/relationships/officeDocument");
            }
            if (core.size() == 0) {
                core = pkg.getRelationshipsByType("http://schemas.microsoft.com/visio/2010/relationships/document");
                if (core.size() == 1) {
                    return new XDGFVisioExtractor(pkg);
                }
            }
            if (core.size() != 1) {
                throw new IllegalArgumentException("Invalid OOXML Package received - expected 1 core document, found " + core.size());
            }
            final PackagePart corePart = pkg.getPart(core.getRelationship(0));
            final String contentType = corePart.getContentType();
            final XSSFRelation[] supported_TYPES = XSSFExcelExtractor.SUPPORTED_TYPES;
            final int length = supported_TYPES.length;
            int i = 0;
            while (i < length) {
                final XSSFRelation rel = supported_TYPES[i];
                if (rel.getContentType().equals(contentType)) {
                    if (getPreferEventExtractor()) {
                        return new XSSFEventBasedExcelExtractor(pkg);
                    }
                    return new XSSFExcelExtractor(pkg);
                }
                else {
                    ++i;
                }
            }
            for (final XWPFRelation rel2 : XWPFWordExtractor.SUPPORTED_TYPES) {
                if (rel2.getContentType().equals(contentType)) {
                    return new XWPFWordExtractor(pkg);
                }
            }
            for (final XSLFRelation rel3 : XSLFPowerPointExtractor.SUPPORTED_TYPES) {
                if (rel3.getContentType().equals(contentType)) {
                    return (POITextExtractor)new SlideShowExtractor((SlideShow)new XMLSlideShow(pkg));
                }
            }
            if (XSLFRelation.THEME_MANAGER.getContentType().equals(contentType)) {
                return (POITextExtractor)new SlideShowExtractor((SlideShow)new XMLSlideShow(pkg));
            }
            final XSSFRelation[] supported_TYPES4 = XSSFBEventBasedExcelExtractor.SUPPORTED_TYPES;
            for (int length4 = supported_TYPES4.length, l = 0; l < length4; ++l) {
                final XSSFRelation rel = supported_TYPES4[l];
                if (rel.getContentType().equals(contentType)) {
                    return new XSSFBEventBasedExcelExtractor(pkg);
                }
            }
            throw new IllegalArgumentException("No supported documents found in the OOXML package (found " + contentType + ")");
        }
        catch (final IOException | Error | RuntimeException | XmlException | OpenXML4JException e) {
            pkg.revert();
            throw e;
        }
    }
    
    public static <T extends POITextExtractor> T createExtractor(final POIFSFileSystem fs) throws IOException, OpenXML4JException, XmlException {
        return createExtractor(fs.getRoot());
    }
    
    public static <T extends POITextExtractor> T createExtractor(final DirectoryNode poifsDir) throws IOException, OpenXML4JException, XmlException {
        for (final String entryName : poifsDir.getEntryNames()) {
            if (entryName.equals("Package")) {
                final OPCPackage pkg = OPCPackage.open((InputStream)poifsDir.createDocumentInputStream("Package"));
                return (T)createExtractor(pkg);
            }
        }
        return (T)OLE2ExtractorFactory.createExtractor(poifsDir);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public static POITextExtractor[] getEmbededDocsTextExtractors(final POIOLE2TextExtractor ext) throws IOException, OpenXML4JException, XmlException {
        return getEmbeddedDocsTextExtractors(ext);
    }
    
    public static POITextExtractor[] getEmbeddedDocsTextExtractors(final POIOLE2TextExtractor ext) throws IOException, OpenXML4JException, XmlException {
        final ArrayList<Entry> dirs = new ArrayList<Entry>();
        final ArrayList<InputStream> nonPOIFS = new ArrayList<InputStream>();
        final DirectoryEntry root = ext.getRoot();
        if (root == null) {
            throw new IllegalStateException("The extractor didn't know which POIFS it came from!");
        }
        if (ext instanceof ExcelExtractor) {
            final Iterator<Entry> it = root.getEntries();
            while (it.hasNext()) {
                final Entry entry = it.next();
                if (entry.getName().startsWith("MBD")) {
                    dirs.add(entry);
                }
            }
        }
        else {
            try {
                final Class<?> clazz = Class.forName("org.apache.poi.extractor.ole2.OLE2ScratchpadExtractorFactory");
                final Method m = clazz.getDeclaredMethod("identifyEmbeddedResources", POIOLE2TextExtractor.class, List.class, List.class);
                m.invoke(null, ext, dirs, nonPOIFS);
            }
            catch (final ReflectiveOperationException e) {
                ExtractorFactory.logger.log(5, new Object[] { "POI Scratchpad jar not included ", e.getLocalizedMessage() });
                return new POITextExtractor[0];
            }
        }
        if (dirs.size() == 0 && nonPOIFS.size() == 0) {
            return new POITextExtractor[0];
        }
        final ArrayList<POITextExtractor> textExtractors = new ArrayList<POITextExtractor>();
        for (final Entry dir : dirs) {
            textExtractors.add(createExtractor((DirectoryNode)dir));
        }
        for (final InputStream nonPOIF : nonPOIFS) {
            try {
                textExtractors.add(createExtractor(nonPOIF));
            }
            catch (final IllegalArgumentException e2) {
                ExtractorFactory.logger.log(3, new Object[] { "Format not supported yet", e2.getLocalizedMessage() });
            }
            catch (final XmlException | OpenXML4JException e3) {
                throw new IOException(e3.getMessage(), e3);
            }
        }
        return textExtractors.toArray(new POITextExtractor[0]);
    }
    
    @Deprecated
    @Removal(version = "4.2")
    @NotImplemented
    public static POITextExtractor[] getEmbededDocsTextExtractors(final POIXMLTextExtractor ext) {
        return getEmbeddedDocsTextExtractors(ext);
    }
    
    @NotImplemented
    public static POITextExtractor[] getEmbeddedDocsTextExtractors(final POIXMLTextExtractor ext) {
        throw new IllegalStateException("Not yet supported");
    }
    
    private static POITextExtractor createEncryptedOOXMLExtractor(final POIFSFileSystem fs) throws IOException {
        String pass = Biff8EncryptionKey.getCurrentUserPassword();
        if (pass == null) {
            pass = "VelvetSweatshop";
        }
        final EncryptionInfo ei = new EncryptionInfo(fs);
        final Decryptor dec = ei.getDecryptor();
        InputStream is = null;
        try {
            if (!dec.verifyPassword(pass)) {
                throw new EncryptedDocumentException("Invalid password specified - use Biff8EncryptionKey.setCurrentUserPassword() before calling extractor");
            }
            is = dec.getDataStream(fs);
            return createExtractor(OPCPackage.open(is));
        }
        catch (final IOException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new EncryptedDocumentException((Throwable)e2);
        }
        finally {
            IOUtils.closeQuietly((Closeable)is);
            fs.close();
        }
    }
    
    static {
        logger = POILogFactory.getLogger((Class)ExtractorFactory.class);
    }
}
