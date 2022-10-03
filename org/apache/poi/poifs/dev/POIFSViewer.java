package org.apache.poi.poifs.dev;

import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;

public final class POIFSViewer
{
    private POIFSViewer() {
    }
    
    public static void main(final String[] args) {
        if (args.length == 0) {
            System.err.println("Must specify at least one file to view");
            System.exit(1);
        }
        final boolean printNames = args.length > 1;
        for (final String arg : args) {
            viewFile(arg, printNames);
        }
    }
    
    private static void viewFile(final String filename, final boolean printName) {
        if (printName) {
            final StringBuilder flowerbox = new StringBuilder();
            flowerbox.append(".");
            for (int j = 0; j < filename.length(); ++j) {
                flowerbox.append("-");
            }
            flowerbox.append(".");
            System.out.println(flowerbox);
            System.out.println("|" + filename + "|");
            System.out.println(flowerbox);
        }
        try {
            final POIFSFileSystem fs = new POIFSFileSystem(new File(filename));
            final List<String> strings = POIFSViewEngine.inspectViewable(fs, true, 0, "  ");
            for (final String s : strings) {
                System.out.print(s);
            }
            fs.close();
        }
        catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
