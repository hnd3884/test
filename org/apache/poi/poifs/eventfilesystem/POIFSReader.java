package org.apache.poi.poifs.eventfilesystem;

import java.util.Iterator;
import org.apache.poi.poifs.filesystem.POIFSDocument;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.property.RootProperty;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import java.io.File;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.InputStream;

public class POIFSReader
{
    private final POIFSReaderRegistry registry;
    private boolean registryClosed;
    private boolean notifyEmptyDirectories;
    
    public POIFSReader() {
        this.registry = new POIFSReaderRegistry();
        this.registryClosed = false;
    }
    
    public void read(final InputStream stream) throws IOException {
        try (final POIFSFileSystem poifs = new POIFSFileSystem(stream)) {
            this.read(poifs);
        }
    }
    
    public void read(final File poifsFile) throws IOException {
        try (final POIFSFileSystem poifs = new POIFSFileSystem(poifsFile, true)) {
            this.read(poifs);
        }
    }
    
    public void read(final POIFSFileSystem poifs) throws IOException {
        this.registryClosed = true;
        final PropertyTable properties = poifs.getPropertyTable();
        final RootProperty root = properties.getRoot();
        this.processProperties(poifs, root, new POIFSDocumentPath());
    }
    
    public void registerListener(final POIFSReaderListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (this.registryClosed) {
            throw new IllegalStateException();
        }
        this.registry.registerListener(listener);
    }
    
    public void registerListener(final POIFSReaderListener listener, final String name) {
        this.registerListener(listener, null, name);
    }
    
    public void registerListener(final POIFSReaderListener listener, final POIFSDocumentPath path, final String name) {
        if (listener == null || name == null || name.length() == 0) {
            throw new NullPointerException();
        }
        if (this.registryClosed) {
            throw new IllegalStateException();
        }
        this.registry.registerListener(listener, (path == null) ? new POIFSDocumentPath() : path, name);
    }
    
    public void setNotifyEmptyDirectories(final boolean notifyEmptyDirectories) {
        this.notifyEmptyDirectories = notifyEmptyDirectories;
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("at least one argument required: input filename(s)");
            System.exit(1);
        }
        for (final String arg : args) {
            final POIFSReader reader = new POIFSReader();
            reader.registerListener(POIFSReader::readEntry);
            System.out.println("reading " + arg);
            reader.read(new File(arg));
        }
    }
    
    private static void readEntry(final POIFSReaderEvent event) {
        final POIFSDocumentPath path = event.getPath();
        final StringBuilder sb = new StringBuilder();
        try (final DocumentInputStream istream = event.getStream()) {
            sb.setLength(0);
            for (int pathLength = path.length(), k = 0; k < pathLength; ++k) {
                sb.append("/").append(path.getComponent(k));
            }
            final byte[] data = IOUtils.toByteArray(istream);
            sb.append("/").append(event.getName()).append(": ").append(data.length).append(" bytes read");
            System.out.println(sb);
        }
        catch (final IOException ex) {}
    }
    
    private void processProperties(final POIFSFileSystem poifs, final DirectoryProperty dir, final POIFSDocumentPath path) {
        boolean hasChildren = false;
        for (final Property property : dir) {
            hasChildren = true;
            final String name = property.getName();
            if (property.isDirectory()) {
                final POIFSDocumentPath new_path = new POIFSDocumentPath(path, new String[] { name });
                this.processProperties(poifs, (DirectoryProperty)property, new_path);
            }
            else {
                POIFSDocument document = null;
                for (final POIFSReaderListener rl : this.registry.getListeners(path, name)) {
                    if (document == null) {
                        document = new POIFSDocument((DocumentProperty)property, poifs);
                    }
                    try (final DocumentInputStream dis = new DocumentInputStream(document)) {
                        final POIFSReaderEvent pe = new POIFSReaderEvent(dis, path, name);
                        rl.processPOIFSReaderEvent(pe);
                    }
                }
            }
        }
        if (hasChildren || !this.notifyEmptyDirectories) {
            return;
        }
        for (final POIFSReaderListener rl2 : this.registry.getListeners(path, ".")) {
            final POIFSReaderEvent pe2 = new POIFSReaderEvent(null, path, null);
            rl2.processPOIFSReaderEvent(pe2);
        }
    }
}
