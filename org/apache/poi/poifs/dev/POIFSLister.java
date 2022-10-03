package org.apache.poi.poifs.dev;

import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;
import java.io.IOException;

public class POIFSLister
{
    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Must specify at least one file to view");
            System.exit(1);
        }
        boolean withSizes = false;
        boolean newPOIFS = true;
        for (final String arg : args) {
            if (arg.equalsIgnoreCase("-size") || arg.equalsIgnoreCase("-sizes")) {
                withSizes = true;
            }
            else if (arg.equalsIgnoreCase("-old") || arg.equalsIgnoreCase("-old-poifs")) {
                newPOIFS = false;
            }
            else if (newPOIFS) {
                viewFile(arg, withSizes);
            }
            else {
                viewFileOld(arg, withSizes);
            }
        }
    }
    
    public static void viewFile(final String filename, final boolean withSizes) throws IOException {
        try (final POIFSFileSystem fs = new POIFSFileSystem(new File(filename))) {
            displayDirectory(fs.getRoot(), "", withSizes);
        }
    }
    
    public static void viewFileOld(final String filename, final boolean withSizes) throws IOException {
        try (final FileInputStream fis = new FileInputStream(filename);
             final POIFSFileSystem fs = new POIFSFileSystem(fis)) {
            displayDirectory(fs.getRoot(), "", withSizes);
        }
    }
    
    public static void displayDirectory(final DirectoryNode dir, final String indent, final boolean withSizes) {
        System.out.println(indent + dir.getName() + " -");
        final String newIndent = indent + "  ";
        boolean hadChildren = false;
        final Iterator<Entry> it = dir.getEntries();
        while (it.hasNext()) {
            hadChildren = true;
            final Entry entry = it.next();
            if (entry instanceof DirectoryNode) {
                displayDirectory((DirectoryNode)entry, newIndent, withSizes);
            }
            else {
                final DocumentNode doc = (DocumentNode)entry;
                String name = doc.getName();
                String size = "";
                if (name.charAt(0) < '\n') {
                    final String altname = "(0x0" + (int)name.charAt(0) + ")" + name.substring(1);
                    name = name.substring(1) + " <" + altname + ">";
                }
                if (withSizes) {
                    size = " [" + doc.getSize() + " / 0x" + Integer.toHexString(doc.getSize()) + "]";
                }
                System.out.println(newIndent + name + size);
            }
        }
        if (!hadChildren) {
            System.out.println(newIndent + "(no children)");
        }
    }
}
