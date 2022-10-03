package org.apache.poi.poifs.crypt.temp;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.xssf.streaming.SheetDataWriter;
import java.io.IOException;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import java.io.OutputStream;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class SXSSFWorkbookWithCustomZipEntrySource extends SXSSFWorkbook
{
    private static final POILogger LOG;
    
    public SXSSFWorkbookWithCustomZipEntrySource() {
        super(20);
        this.setCompressTempFiles(true);
    }
    
    @Override
    public void write(final OutputStream stream) throws IOException {
        this.flushSheets();
        final EncryptedTempData tempData = new EncryptedTempData();
        ZipEntrySource source = null;
        try {
            try (final OutputStream os = tempData.getOutputStream()) {
                this.getXSSFWorkbook().write(os);
            }
            source = AesZipFileZipEntrySource.createZipEntrySource(tempData.getInputStream());
            this.injectData(source, stream);
        }
        finally {
            tempData.dispose();
            IOUtils.closeQuietly((Closeable)source);
        }
    }
    
    @Override
    protected SheetDataWriter createSheetDataWriter() throws IOException {
        SXSSFWorkbookWithCustomZipEntrySource.LOG.log(3, new Object[] { "isCompressTempFiles: " + this.isCompressTempFiles() });
        SXSSFWorkbookWithCustomZipEntrySource.LOG.log(3, new Object[] { "SharedStringSource: " + this.getSharedStringSource() });
        return new SheetDataWriterWithDecorator();
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)SXSSFWorkbookWithCustomZipEntrySource.class);
    }
}
