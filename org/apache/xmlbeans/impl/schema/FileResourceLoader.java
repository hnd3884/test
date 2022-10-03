package org.apache.xmlbeans.impl.schema;

import java.util.zip.ZipEntry;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.zip.ZipFile;
import java.io.File;
import org.apache.xmlbeans.ResourceLoader;

public class FileResourceLoader implements ResourceLoader
{
    private File _directory;
    private ZipFile _zipfile;
    
    public FileResourceLoader(final File file) throws IOException {
        if (file.isDirectory()) {
            this._directory = file;
        }
        else {
            this._zipfile = new ZipFile(file);
        }
    }
    
    @Override
    public InputStream getResourceAsStream(final String resourceName) {
        try {
            if (this._zipfile == null) {
                return new FileInputStream(new File(this._directory, resourceName));
            }
            final ZipEntry entry = this._zipfile.getEntry(resourceName);
            if (entry == null) {
                return null;
            }
            return this._zipfile.getInputStream(entry);
        }
        catch (final IOException e) {
            return null;
        }
    }
    
    @Override
    public void close() {
        if (this._zipfile != null) {
            try {
                this._zipfile.close();
            }
            catch (final IOException ex) {}
            this._zipfile = null;
        }
    }
}
