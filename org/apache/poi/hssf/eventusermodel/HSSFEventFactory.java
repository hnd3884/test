package org.apache.poi.hssf.eventusermodel;

import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactoryInputStream;
import java.io.InputStream;
import java.util.Set;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class HSSFEventFactory
{
    public void processWorkbookEvents(final HSSFRequest req, final POIFSFileSystem fs) throws IOException {
        this.processWorkbookEvents(req, fs.getRoot());
    }
    
    public void processWorkbookEvents(final HSSFRequest req, final DirectoryNode dir) throws IOException {
        String name = null;
        final Set<String> entryNames = dir.getEntryNames();
        for (final String potentialName : InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES) {
            if (entryNames.contains(potentialName)) {
                name = potentialName;
                break;
            }
        }
        if (name == null) {
            name = InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES[0];
        }
        try (final InputStream in = dir.createDocumentInputStream(name)) {
            this.processEvents(req, in);
        }
    }
    
    public short abortableProcessWorkbookEvents(final HSSFRequest req, final POIFSFileSystem fs) throws IOException, HSSFUserException {
        return this.abortableProcessWorkbookEvents(req, fs.getRoot());
    }
    
    public short abortableProcessWorkbookEvents(final HSSFRequest req, final DirectoryNode dir) throws IOException, HSSFUserException {
        try (final InputStream in = dir.createDocumentInputStream("Workbook")) {
            return this.abortableProcessEvents(req, in);
        }
    }
    
    public void processEvents(final HSSFRequest req, final InputStream in) {
        try {
            this.genericProcessEvents(req, in);
        }
        catch (final HSSFUserException ex) {}
    }
    
    public short abortableProcessEvents(final HSSFRequest req, final InputStream in) throws HSSFUserException {
        return this.genericProcessEvents(req, in);
    }
    
    private short genericProcessEvents(final HSSFRequest req, final InputStream in) throws HSSFUserException {
        short userCode = 0;
        final RecordFactoryInputStream recordStream = new RecordFactoryInputStream(in, false);
        do {
            final Record r = recordStream.nextRecord();
            if (r == null) {
                break;
            }
            userCode = req.processRecord(r);
        } while (userCode == 0);
        return userCode;
    }
}
