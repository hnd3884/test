package org.apache.poi.hssf.dev;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;

public class EFBiffViewer
{
    String file;
    
    public void run() throws IOException {
        try (final POIFSFileSystem fs = new POIFSFileSystem(new File(this.file), true);
             final InputStream din = BiffViewer.getPOIFSInputStream(fs)) {
            final HSSFRequest req = new HSSFRequest();
            req.addListenerForAllRecords(System.out::println);
            final HSSFEventFactory factory = new HSSFEventFactory();
            factory.processEvents(req, din);
        }
    }
    
    public void setFile(final String file) {
        this.file = file;
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length == 1 && !args[0].equals("--help")) {
            final EFBiffViewer viewer = new EFBiffViewer();
            viewer.setFile(args[0]);
            viewer.run();
        }
        else {
            System.out.println("EFBiffViewer");
            System.out.println("Outputs biffview of records based on HSSFEventFactory");
            System.out.println("usage: java org.apache.poi.hssf.dev.EBBiffViewer filename");
        }
    }
}
