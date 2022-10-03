package org.apache.poi.hssf.extractor;

import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.OldFormulaRecord;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.OldStringRecord;
import org.apache.poi.hssf.record.OldLabelRecord;
import org.apache.poi.hssf.record.OldSheetRecord;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.poifs.filesystem.Entry;
import java.io.FileNotFoundException;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import java.io.BufferedInputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.io.FileInputStream;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.record.RecordInputStream;
import java.io.Closeable;

public class OldExcelExtractor implements Closeable
{
    private static final int FILE_PASS_RECORD_SID = 47;
    private static final int MAX_RECORD_LENGTH = 100000;
    private RecordInputStream ris;
    private Closeable toClose;
    private int biffVersion;
    private int fileType;
    
    public OldExcelExtractor(final InputStream input) throws IOException {
        this.open(input);
    }
    
    public OldExcelExtractor(final File f) throws IOException {
        POIFSFileSystem poifs = null;
        try {
            poifs = new POIFSFileSystem(f);
            this.open(poifs);
            this.toClose = poifs;
            return;
        }
        catch (final OldExcelFormatException | NotOLE2FileException ex) {}
        finally {
            if (this.toClose == null) {
                IOUtils.closeQuietly(poifs);
            }
        }
        final FileInputStream biffStream = new FileInputStream(f);
        try {
            this.open(biffStream);
        }
        catch (final IOException | RuntimeException e) {
            biffStream.close();
            throw e;
        }
    }
    
    public OldExcelExtractor(final POIFSFileSystem fs) throws IOException {
        this.open(fs);
    }
    
    public OldExcelExtractor(final DirectoryNode directory) throws IOException {
        this.open(directory);
    }
    
    private void open(final InputStream biffStream) throws IOException {
        final BufferedInputStream bis = (BufferedInputStream)((biffStream instanceof BufferedInputStream) ? biffStream : new BufferedInputStream(biffStream, 8));
        if (FileMagic.valueOf(bis) == FileMagic.OLE2) {
            final POIFSFileSystem poifs = new POIFSFileSystem(bis);
            try {
                this.open(poifs);
                this.toClose = poifs;
            }
            finally {
                if (this.toClose == null) {
                    poifs.close();
                }
            }
        }
        else {
            this.ris = new RecordInputStream(bis);
            this.toClose = bis;
            this.prepare();
        }
    }
    
    private void open(final POIFSFileSystem fs) throws IOException {
        this.open(fs.getRoot());
    }
    
    private void open(final DirectoryNode directory) throws IOException {
        DocumentNode book;
        try {
            book = (DocumentNode)directory.getEntry("Book");
        }
        catch (final FileNotFoundException | IllegalArgumentException e) {
            book = (DocumentNode)directory.getEntry(InternalWorkbook.WORKBOOK_DIR_ENTRY_NAMES[0]);
        }
        if (book == null) {
            throw new IOException("No Excel 5/95 Book stream found");
        }
        this.ris = new RecordInputStream(directory.createDocumentInputStream(book));
        this.prepare();
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("   OldExcelExtractor <filename>");
            System.exit(1);
        }
        final OldExcelExtractor extractor = new OldExcelExtractor(new File(args[0]));
        System.out.println(extractor.getText());
        extractor.close();
    }
    
    private void prepare() {
        if (!this.ris.hasNextRecord()) {
            throw new IllegalArgumentException("File contains no records!");
        }
        this.ris.nextRecord();
        final int bofSid = this.ris.getSid();
        switch (bofSid) {
            case 9: {
                this.biffVersion = 2;
                break;
            }
            case 521: {
                this.biffVersion = 3;
                break;
            }
            case 1033: {
                this.biffVersion = 4;
                break;
            }
            case 2057: {
                this.biffVersion = 5;
                break;
            }
            default: {
                throw new IllegalArgumentException("File does not begin with a BOF, found sid of " + bofSid);
            }
        }
        final BOFRecord bof = new BOFRecord(this.ris);
        this.fileType = bof.getType();
    }
    
    public int getBiffVersion() {
        return this.biffVersion;
    }
    
    public int getFileType() {
        return this.fileType;
    }
    
    public String getText() {
        final StringBuilder text = new StringBuilder();
        CodepageRecord codepage = null;
        while (this.ris.hasNextRecord()) {
            final int sid = this.ris.getNextSid();
            this.ris.nextRecord();
            switch (sid) {
                case 47: {
                    throw new EncryptedDocumentException("Encryption not supported for Old Excel files");
                }
                case 133: {
                    final OldSheetRecord shr = new OldSheetRecord(this.ris);
                    shr.setCodePage(codepage);
                    text.append("Sheet: ");
                    text.append(shr.getSheetname());
                    text.append('\n');
                    continue;
                }
                case 4:
                case 516: {
                    final OldLabelRecord lr = new OldLabelRecord(this.ris);
                    lr.setCodePage(codepage);
                    text.append(lr.getValue());
                    text.append('\n');
                    continue;
                }
                case 7:
                case 519: {
                    final OldStringRecord sr = new OldStringRecord(this.ris);
                    sr.setCodePage(codepage);
                    text.append(sr.getString());
                    text.append('\n');
                    continue;
                }
                case 515: {
                    final NumberRecord nr = new NumberRecord(this.ris);
                    this.handleNumericCell(text, nr.getValue());
                    continue;
                }
                case 6:
                case 518:
                case 1030: {
                    if (this.biffVersion == 5) {
                        final FormulaRecord fr = new FormulaRecord(this.ris);
                        if (fr.getCachedResultType() != CellType.NUMERIC.getCode()) {
                            continue;
                        }
                        this.handleNumericCell(text, fr.getValue());
                        continue;
                    }
                    final OldFormulaRecord fr2 = new OldFormulaRecord(this.ris);
                    if (fr2.getCachedResultType() != CellType.NUMERIC.getCode()) {
                        continue;
                    }
                    this.handleNumericCell(text, fr2.getValue());
                    continue;
                }
                case 638: {
                    final RKRecord rr = new RKRecord(this.ris);
                    this.handleNumericCell(text, rr.getRKNumber());
                    continue;
                }
                case 66: {
                    codepage = new CodepageRecord(this.ris);
                    continue;
                }
                default: {
                    this.ris.readFully(IOUtils.safelyAllocate(this.ris.remaining(), 100000));
                    continue;
                }
            }
        }
        this.close();
        this.ris = null;
        return text.toString();
    }
    
    @Override
    public void close() {
        if (this.toClose != null) {
            IOUtils.closeQuietly(this.toClose);
            this.toClose = null;
        }
    }
    
    protected void handleNumericCell(final StringBuilder text, final double value) {
        text.append(value);
        text.append('\n');
    }
}
