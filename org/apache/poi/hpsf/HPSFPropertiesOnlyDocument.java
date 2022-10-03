package org.apache.poi.hpsf;

import org.apache.poi.poifs.filesystem.EntryUtils;
import java.util.Collection;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.FilteringDirectoryNode;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.POIDocument;

public class HPSFPropertiesOnlyDocument extends POIDocument
{
    public HPSFPropertiesOnlyDocument(final POIFSFileSystem fs) {
        super(fs);
    }
    
    @Override
    public void write() throws IOException {
        final POIFSFileSystem fs = this.getDirectory().getFileSystem();
        this.validateInPlaceWritePossible();
        this.writeProperties(fs, null);
        fs.writeFilesystem();
    }
    
    @Override
    public void write(final File newFile) throws IOException {
        try (final POIFSFileSystem fs = POIFSFileSystem.create(newFile)) {
            this.write(fs);
            fs.writeFilesystem();
        }
    }
    
    @Override
    public void write(final OutputStream out) throws IOException {
        try (final POIFSFileSystem fs = new POIFSFileSystem()) {
            this.write(fs);
            fs.writeFilesystem(out);
        }
    }
    
    private void write(final POIFSFileSystem fs) throws IOException {
        final List<String> excepts = new ArrayList<String>(2);
        this.writeProperties(fs, excepts);
        final FilteringDirectoryNode src = new FilteringDirectoryNode(this.getDirectory(), excepts);
        final FilteringDirectoryNode dest = new FilteringDirectoryNode(fs.getRoot(), excepts);
        EntryUtils.copyNodes(src, dest);
    }
}
