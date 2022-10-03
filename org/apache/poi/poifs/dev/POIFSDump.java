package org.apache.poi.poifs.dev;

import java.nio.ByteBuffer;
import org.apache.poi.poifs.filesystem.BlockStore;
import org.apache.poi.poifs.filesystem.POIFSStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.io.FileOutputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.FileInputStream;

public final class POIFSDump
{
    private static final int MAX_RECORD_LENGTH = 100000;
    
    private POIFSDump() {
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Must specify at least one file to dump");
            System.exit(1);
        }
        boolean dumpProps = false;
        boolean dumpMini = false;
        for (final String filename : args) {
            if (filename.equalsIgnoreCase("-dumprops") || filename.equalsIgnoreCase("-dump-props") || filename.equalsIgnoreCase("-dump-properties")) {
                dumpProps = true;
            }
            else if (filename.equalsIgnoreCase("-dumpmini") || filename.equalsIgnoreCase("-dump-mini") || filename.equalsIgnoreCase("-dump-ministream") || filename.equalsIgnoreCase("-dump-mini-stream")) {
                dumpMini = true;
            }
            else {
                System.out.println("Dumping " + filename);
                try (final FileInputStream is = new FileInputStream(filename);
                     final POIFSFileSystem fs = new POIFSFileSystem(is)) {
                    final DirectoryEntry root = fs.getRoot();
                    final String filenameWithoutPath = new File(filename).getName();
                    final File dumpDir = new File(filenameWithoutPath + "_dump");
                    final File file = new File(dumpDir, root.getName());
                    if (!file.exists() && !file.mkdirs()) {
                        throw new IOException("Could not create directory " + file);
                    }
                    dump(root, file);
                    if (dumpProps) {
                        final HeaderBlock header = fs.getHeaderBlock();
                        dump(fs, header.getPropertyStart(), "properties", file);
                    }
                    if (dumpMini) {
                        final PropertyTable props = fs.getPropertyTable();
                        final int startBlock = props.getRoot().getStartBlock();
                        if (startBlock == -2) {
                            System.err.println("No Mini Stream in file");
                        }
                        else {
                            dump(fs, startBlock, "mini-stream", file);
                        }
                    }
                }
            }
        }
    }
    
    public static void dump(final DirectoryEntry root, final File parent) throws IOException {
        final Iterator<Entry> it = root.getEntries();
        while (it.hasNext()) {
            final Entry entry = it.next();
            if (entry instanceof DocumentNode) {
                final DocumentNode node = (DocumentNode)entry;
                final DocumentInputStream is = new DocumentInputStream(node);
                final byte[] bytes = IOUtils.toByteArray(is);
                is.close();
                try (final OutputStream out = new FileOutputStream(new File(parent, node.getName().trim()))) {
                    out.write(bytes);
                }
            }
            else if (entry instanceof DirectoryEntry) {
                final DirectoryEntry dir = (DirectoryEntry)entry;
                final File file = new File(parent, entry.getName());
                if (!file.exists() && !file.mkdirs()) {
                    throw new IOException("Could not create directory " + file);
                }
                dump(dir, file);
            }
            else {
                System.err.println("Skipping unsupported POIFS entry: " + entry);
            }
        }
    }
    
    public static void dump(final POIFSFileSystem fs, final int startBlock, final String name, final File parent) throws IOException {
        final File file = new File(parent, name);
        try (final FileOutputStream out = new FileOutputStream(file)) {
            final POIFSStream stream = new POIFSStream(fs, startBlock);
            final byte[] b = IOUtils.safelyAllocate(fs.getBigBlockSize(), 100000);
            for (final ByteBuffer bb : stream) {
                final int len = bb.remaining();
                bb.get(b);
                out.write(b, 0, len);
            }
        }
    }
}
