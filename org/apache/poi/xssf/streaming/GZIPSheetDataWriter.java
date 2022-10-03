package org.apache.poi.xssf.streaming;

import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.poi.util.TempFile;
import java.io.File;
import org.apache.poi.xssf.model.SharedStringsTable;
import java.io.IOException;

public class GZIPSheetDataWriter extends SheetDataWriter
{
    public GZIPSheetDataWriter() throws IOException {
    }
    
    public GZIPSheetDataWriter(final SharedStringsTable sharedStringsTable) throws IOException {
        super(sharedStringsTable);
    }
    
    @Override
    public File createTempFile() throws IOException {
        return TempFile.createTempFile("poi-sxssf-sheet-xml", ".gz");
    }
    
    @Override
    protected InputStream decorateInputStream(final FileInputStream fis) throws IOException {
        return new GZIPInputStream(fis);
    }
    
    @Override
    protected OutputStream decorateOutputStream(final FileOutputStream fos) throws IOException {
        return new GZIPOutputStream(fos);
    }
}
