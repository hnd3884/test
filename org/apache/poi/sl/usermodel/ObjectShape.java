package org.apache.poi.sl.usermodel;

import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.io.ByteArrayInputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.FileMagic;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public interface ObjectShape<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> extends Shape<S, P>, PlaceableShape<S, P>
{
    PictureData getPictureData();
    
    String getProgId();
    
    String getFullName();
    
    OutputStream updateObjectData(final ObjectMetaData.Application p0, final ObjectMetaData p1) throws IOException;
    
    default InputStream readObjectData() throws IOException {
        final String progId = this.getProgId();
        if (progId == null) {
            throw new IllegalStateException("Ole object hasn't been initialized or provided in the source xml. use updateObjectData() first or check the corresponding slideXXX.xml");
        }
        final ObjectMetaData.Application app = ObjectMetaData.Application.lookup(progId);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(50000);
        try (final InputStream is = FileMagic.prepareToCheckMagic(this.readObjectDataRaw())) {
            final FileMagic fm = FileMagic.valueOf(is);
            if (fm == FileMagic.OLE2) {
                try (final POIFSFileSystem poifs = new POIFSFileSystem(is)) {
                    final String[] names = { (app == null) ? null : app.getMetaData().getOleEntry(), "Package", "Contents", "CONTENTS", "CONTENTSV30" };
                    final DirectoryNode root = poifs.getRoot();
                    String entryName = null;
                    for (final String n : names) {
                        if (root.hasEntry(n)) {
                            entryName = n;
                            break;
                        }
                    }
                    if (entryName == null) {
                        poifs.writeFilesystem(bos);
                    }
                    else {
                        try (final InputStream is2 = poifs.createDocumentInputStream(entryName)) {
                            IOUtils.copy(is2, bos);
                        }
                    }
                }
            }
            else {
                IOUtils.copy(is, bos);
            }
        }
        return new ByteArrayInputStream(bos.toByteArray());
    }
    
    default InputStream readObjectDataRaw() throws IOException {
        return this.getObjectData().getInputStream();
    }
    
    ObjectData getObjectData();
}
