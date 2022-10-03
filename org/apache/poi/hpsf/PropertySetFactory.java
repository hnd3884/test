package org.apache.poi.hpsf;

import org.apache.poi.util.LittleEndianInputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

public class PropertySetFactory
{
    public static PropertySet create(final DirectoryEntry dir, final String name) throws FileNotFoundException, NoPropertySetStreamException, IOException, UnsupportedEncodingException {
        InputStream inp = null;
        try {
            final DocumentEntry entry = (DocumentEntry)dir.getEntry(name);
            inp = new DocumentInputStream(entry);
            try {
                return create(inp);
            }
            catch (final MarkUnsupportedException e) {
                return null;
            }
        }
        finally {
            if (inp != null) {
                inp.close();
            }
        }
    }
    
    public static PropertySet create(final InputStream stream) throws NoPropertySetStreamException, MarkUnsupportedException, UnsupportedEncodingException, IOException {
        stream.mark(45);
        final LittleEndianInputStream leis = new LittleEndianInputStream(stream);
        final int byteOrder = leis.readUShort();
        final int format = leis.readUShort();
        leis.readUInt();
        final byte[] clsIdBuf = new byte[16];
        leis.readFully(clsIdBuf);
        final int sectionCount = (int)leis.readUInt();
        if (byteOrder != 65534 || format != 0 || sectionCount < 0) {
            throw new NoPropertySetStreamException();
        }
        if (sectionCount > 0) {
            leis.readFully(clsIdBuf);
        }
        stream.reset();
        final ClassID clsId = new ClassID(clsIdBuf, 0);
        if (sectionCount > 0 && PropertySet.matchesSummary(clsId, SummaryInformation.FORMAT_ID)) {
            return new SummaryInformation(stream);
        }
        if (sectionCount > 0 && PropertySet.matchesSummary(clsId, DocumentSummaryInformation.FORMAT_ID)) {
            return new DocumentSummaryInformation(stream);
        }
        return new PropertySet(stream);
    }
    
    public static SummaryInformation newSummaryInformation() {
        return new SummaryInformation();
    }
    
    public static DocumentSummaryInformation newDocumentSummaryInformation() {
        return new DocumentSummaryInformation();
    }
}
