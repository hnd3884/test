package org.apache.poi;

import java.io.OutputStream;
import java.io.File;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.DirectoryNode;

public abstract class POIReadOnlyDocument extends POIDocument
{
    protected POIReadOnlyDocument(final DirectoryNode dir) {
        super(dir);
    }
    
    protected POIReadOnlyDocument(final POIFSFileSystem fs) {
        super(fs);
    }
    
    @Override
    public void write() {
        throw new IllegalStateException("Writing is not yet implemented for this Document Format");
    }
    
    @Override
    public void write(final File file) {
        throw new IllegalStateException("Writing is not yet implemented for this Document Format");
    }
    
    @Override
    public void write(final OutputStream out) {
        throw new IllegalStateException("Writing is not yet implemented for this Document Format");
    }
}
