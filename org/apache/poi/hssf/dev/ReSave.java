package org.apache.poi.hssf.dev;

import java.io.OutputStream;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;

public class ReSave
{
    public static void main(final String[] args) throws Exception {
        boolean initDrawing = false;
        boolean saveToMemory = false;
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (final String filename : args) {
            if (filename.equals("-dg")) {
                initDrawing = true;
            }
            else if (filename.equals("-bos")) {
                saveToMemory = true;
            }
            else {
                System.out.print("reading " + filename + "...");
                final FileInputStream is = new FileInputStream(filename);
                final HSSFWorkbook wb = new HSSFWorkbook(is);
                try {
                    System.out.println("done");
                    for (int i = 0; i < wb.getNumberOfSheets(); ++i) {
                        final HSSFSheet sheet = wb.getSheetAt(i);
                        if (initDrawing) {
                            sheet.getDrawingPatriarch();
                        }
                    }
                    OutputStream os;
                    if (saveToMemory) {
                        bos.reset();
                        os = bos;
                    }
                    else {
                        final String outputFile = filename.replace(".xls", "-saved.xls");
                        System.out.print("saving to " + outputFile + "...");
                        os = new FileOutputStream(outputFile);
                    }
                    try {
                        wb.write(os);
                    }
                    finally {
                        os.close();
                    }
                    System.out.println("done");
                }
                finally {
                    wb.close();
                    is.close();
                }
            }
        }
    }
}
