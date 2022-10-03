package org.apache.poi.extractor;

import org.apache.poi.util.POILogFactory;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import java.util.List;
import org.apache.poi.poifs.filesystem.Entry;
import java.util.ArrayList;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.extractor.EventBasedExcelExtractor;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.lang.reflect.Method;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.POILogger;

public final class OLE2ExtractorFactory
{
    private static final POILogger LOGGER;
    private static final ThreadLocal<Boolean> threadPreferEventExtractors;
    private static Boolean allPreferEventExtractors;
    
    private OLE2ExtractorFactory() {
    }
    
    public static boolean getThreadPrefersEventExtractors() {
        return OLE2ExtractorFactory.threadPreferEventExtractors.get();
    }
    
    public static Boolean getAllThreadsPreferEventExtractors() {
        return OLE2ExtractorFactory.allPreferEventExtractors;
    }
    
    public static void setThreadPrefersEventExtractors(final boolean preferEventExtractors) {
        OLE2ExtractorFactory.threadPreferEventExtractors.set(preferEventExtractors);
    }
    
    public static void setAllThreadsPreferEventExtractors(final Boolean preferEventExtractors) {
        OLE2ExtractorFactory.allPreferEventExtractors = preferEventExtractors;
    }
    
    public static boolean getPreferEventExtractor() {
        if (OLE2ExtractorFactory.allPreferEventExtractors != null) {
            return OLE2ExtractorFactory.allPreferEventExtractors;
        }
        return OLE2ExtractorFactory.threadPreferEventExtractors.get();
    }
    
    public static <T extends POITextExtractor> T createExtractor(final POIFSFileSystem fs) throws IOException {
        return (T)createExtractor(fs.getRoot());
    }
    
    public static <T extends POITextExtractor> T createExtractor(final InputStream input) throws IOException {
        final Class<?> cls = getOOXMLClass();
        if (cls != null) {
            try {
                final Method m = cls.getDeclaredMethod("createExtractor", InputStream.class);
                return (T)m.invoke(null, input);
            }
            catch (final IllegalArgumentException iae) {
                throw iae;
            }
            catch (final Exception e) {
                throw new IllegalArgumentException("Error creating Extractor for InputStream", e);
            }
        }
        return createExtractor(new POIFSFileSystem(input));
    }
    
    private static Class<?> getOOXMLClass() {
        try {
            return OLE2ExtractorFactory.class.getClassLoader().loadClass("org.apache.poi.extractor.ExtractorFactory");
        }
        catch (final ClassNotFoundException e) {
            OLE2ExtractorFactory.LOGGER.log(5, "POI OOXML jar missing");
            return null;
        }
    }
    
    private static Class<?> getScratchpadClass() {
        try {
            return OLE2ExtractorFactory.class.getClassLoader().loadClass("org.apache.poi.extractor.ole2.OLE2ScratchpadExtractorFactory");
        }
        catch (final ClassNotFoundException e) {
            OLE2ExtractorFactory.LOGGER.log(7, "POI Scratchpad jar missing");
            throw new IllegalStateException("POI Scratchpad jar missing, required for ExtractorFactory");
        }
    }
    
    public static POITextExtractor createExtractor(final DirectoryNode poifsDir) throws IOException {
        final String[] workbook_DIR_ENTRY_NAMES = InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES;
        final int length = workbook_DIR_ENTRY_NAMES.length;
        int i = 0;
        while (i < length) {
            final String workbookName = workbook_DIR_ENTRY_NAMES[i];
            if (poifsDir.hasEntry(workbookName)) {
                if (getPreferEventExtractor()) {
                    return new EventBasedExcelExtractor(poifsDir);
                }
                return new ExcelExtractor(poifsDir);
            }
            else {
                ++i;
            }
        }
        if (poifsDir.hasEntry("Book")) {
            throw new OldExcelFormatException("Old Excel Spreadsheet format (1-95) found. Please call OldExcelExtractor directly for basic text extraction");
        }
        final Class<?> cls = getScratchpadClass();
        try {
            final Method m = cls.getDeclaredMethod("createExtractor", DirectoryNode.class);
            final POITextExtractor ext = (POITextExtractor)m.invoke(null, poifsDir);
            if (ext != null) {
                return ext;
            }
        }
        catch (final IllegalArgumentException iae) {
            throw iae;
        }
        catch (final Exception e) {
            throw new IllegalArgumentException("Error creating Scratchpad Extractor", e);
        }
        throw new IllegalArgumentException("No supported documents found in the OLE2 stream");
    }
    
    public static POITextExtractor[] getEmbededDocsTextExtractors(final POIOLE2TextExtractor ext) throws IOException {
        final List<Entry> dirs = new ArrayList<Entry>();
        final List<InputStream> nonPOIFS = new ArrayList<InputStream>();
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
            final Class<?> cls = getScratchpadClass();
            try {
                final Method m = cls.getDeclaredMethod("identifyEmbeddedResources", POIOLE2TextExtractor.class, List.class, List.class);
                m.invoke(null, ext, dirs, nonPOIFS);
            }
            catch (final Exception e) {
                throw new IllegalArgumentException("Error checking for Scratchpad embedded resources", e);
            }
        }
        if (dirs.size() == 0 && nonPOIFS.size() == 0) {
            return new POITextExtractor[0];
        }
        final ArrayList<POITextExtractor> e2 = new ArrayList<POITextExtractor>();
        for (final Entry dir : dirs) {
            e2.add(createExtractor((DirectoryNode)dir));
        }
        for (final InputStream stream : nonPOIFS) {
            try {
                e2.add(createExtractor(stream));
            }
            catch (final Exception xe) {
                OLE2ExtractorFactory.LOGGER.log(5, xe);
            }
        }
        return e2.toArray(new POITextExtractor[0]);
    }
    
    static {
        LOGGER = POILogFactory.getLogger(OLE2ExtractorFactory.class);
        threadPreferEventExtractors = ThreadLocal.withInitial(() -> Boolean.FALSE);
    }
}
