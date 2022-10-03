package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import org.apache.poi.poifs.nio.FileBackedDataSource;
import org.apache.poi.util.TempFile;
import java.io.File;

public class TempFilePOIFSFileSystem extends POIFSFileSystem
{
    File tempFile;
    
    @Override
    protected void createNewDataSource() {
        try {
            this.tempFile = TempFile.createTempFile("poifs", ".tmp");
            this._data = new FileBackedDataSource(this.tempFile, false);
        }
        catch (final IOException e) {
            throw new RuntimeException("Failed to create data source", e);
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.tempFile != null && this.tempFile.exists()) {
            this.tempFile.delete();
        }
        super.close();
    }
}
