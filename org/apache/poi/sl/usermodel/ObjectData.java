package org.apache.poi.sl.usermodel;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.util.IOUtils;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public interface ObjectData
{
    InputStream getInputStream() throws IOException;
    
    OutputStream getOutputStream() throws IOException;
    
    default byte[] getBytes() throws IOException {
        try (final InputStream is = this.getInputStream()) {
            return IOUtils.toByteArray(is);
        }
    }
    
    default boolean hasDirectoryEntry() {
        try (final InputStream is = FileMagic.prepareToCheckMagic(this.getInputStream())) {
            final FileMagic fm = FileMagic.valueOf(is);
            return fm == FileMagic.OLE2;
        }
        catch (final IOException e) {
            final POILogger LOG = POILogFactory.getLogger(ObjectData.class);
            LOG.log(5, "Can't determine filemagic of ole stream", e);
            return false;
        }
    }
    
    default DirectoryEntry getDirectory() throws IOException {
        try (final InputStream is = this.getInputStream()) {
            return new POIFSFileSystem(is).getRoot();
        }
    }
    
    String getOLE2ClassName();
    
    String getFileName();
}
