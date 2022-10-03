package org.apache.poi.util;

import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public final class DrawingDump
{
    private DrawingDump() {
    }
    
    public static void main(final String[] args) throws IOException {
        final OutputStreamWriter osw = new OutputStreamWriter(System.out, Charset.defaultCharset());
        final PrintWriter pw = new PrintWriter(osw);
        final POIFSFileSystem fs = new POIFSFileSystem(new File(args[0]));
        final HSSFWorkbook wb = new HSSFWorkbook(fs);
        try {
            pw.println("Drawing group:");
            wb.dumpDrawingGroupRecords(true);
            final int i = 1;
            for (final Sheet sheet : wb) {
                pw.println("Sheet " + i + "(" + sheet.getSheetName() + "):");
                ((HSSFSheet)sheet).dumpDrawingRecords(true, pw);
            }
        }
        finally {
            wb.close();
            fs.close();
        }
    }
}
