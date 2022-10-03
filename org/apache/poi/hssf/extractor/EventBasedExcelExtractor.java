package org.apache.poi.hssf.extractor;

import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.Record;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import java.io.IOException;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import java.io.Closeable;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.POIDocument;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.ss.extractor.ExcelExtractor;
import org.apache.poi.extractor.POIOLE2TextExtractor;

public class EventBasedExcelExtractor extends POIOLE2TextExtractor implements ExcelExtractor
{
    private DirectoryNode _dir;
    boolean _includeSheetNames;
    boolean _formulasNotResults;
    
    public EventBasedExcelExtractor(final DirectoryNode dir) {
        super((POIDocument)null);
        this._includeSheetNames = true;
        this._dir = dir;
    }
    
    public EventBasedExcelExtractor(final POIFSFileSystem fs) {
        this(fs.getRoot());
        super.setFilesystem(fs);
    }
    
    @Override
    public DocumentSummaryInformation getDocSummaryInformation() {
        throw new IllegalStateException("Metadata extraction not supported in streaming mode, please use ExcelExtractor");
    }
    
    @Override
    public SummaryInformation getSummaryInformation() {
        throw new IllegalStateException("Metadata extraction not supported in streaming mode, please use ExcelExtractor");
    }
    
    @Override
    public void setIncludeCellComments(final boolean includeComments) {
        throw new IllegalStateException("Comment extraction not supported in streaming mode, please use ExcelExtractor");
    }
    
    @Override
    public void setIncludeHeadersFooters(final boolean includeHeadersFooters) {
        throw new IllegalStateException("Header/Footer extraction not supported in streaming mode, please use ExcelExtractor");
    }
    
    @Override
    public void setIncludeSheetNames(final boolean includeSheetNames) {
        this._includeSheetNames = includeSheetNames;
    }
    
    @Override
    public void setFormulasNotResults(final boolean formulasNotResults) {
        this._formulasNotResults = formulasNotResults;
    }
    
    @Override
    public String getText() {
        String text;
        try {
            final TextListener tl = this.triggerExtraction();
            text = tl._text.toString();
            if (!text.endsWith("\n")) {
                text += "\n";
            }
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return text;
    }
    
    private TextListener triggerExtraction() throws IOException {
        final TextListener tl = new TextListener();
        final FormatTrackingHSSFListener ft = new FormatTrackingHSSFListener(tl);
        tl._ft = ft;
        final HSSFEventFactory factory = new HSSFEventFactory();
        final HSSFRequest request = new HSSFRequest();
        request.addListenerForAllRecords(ft);
        factory.processWorkbookEvents(request, this._dir);
        return tl;
    }
    
    private class TextListener implements HSSFListener
    {
        FormatTrackingHSSFListener _ft;
        private SSTRecord sstRecord;
        private final List<String> sheetNames;
        final StringBuilder _text;
        private int sheetNum;
        private int rowNum;
        private boolean outputNextStringValue;
        private int nextRow;
        
        public TextListener() {
            this._text = new StringBuilder();
            this.sheetNum = -1;
            this.nextRow = -1;
            this.sheetNames = new ArrayList<String>();
        }
        
        @Override
        public void processRecord(final Record record) {
            String thisText = null;
            int thisRow = -1;
            switch (record.getSid()) {
                case 133: {
                    final BoundSheetRecord sr = (BoundSheetRecord)record;
                    this.sheetNames.add(sr.getSheetname());
                    break;
                }
                case 2057: {
                    final BOFRecord bof = (BOFRecord)record;
                    if (bof.getType() != 16) {
                        break;
                    }
                    ++this.sheetNum;
                    this.rowNum = -1;
                    if (EventBasedExcelExtractor.this._includeSheetNames) {
                        if (this._text.length() > 0) {
                            this._text.append("\n");
                        }
                        this._text.append(this.sheetNames.get(this.sheetNum));
                        break;
                    }
                    break;
                }
                case 252: {
                    this.sstRecord = (SSTRecord)record;
                    break;
                }
                case 6: {
                    final FormulaRecord frec = (FormulaRecord)record;
                    thisRow = frec.getRow();
                    if (EventBasedExcelExtractor.this._formulasNotResults) {
                        thisText = HSSFFormulaParser.toFormulaString(null, frec.getParsedExpression());
                        break;
                    }
                    if (frec.hasCachedResultString()) {
                        this.outputNextStringValue = true;
                        this.nextRow = frec.getRow();
                        break;
                    }
                    thisText = this._ft.formatNumberDateCell(frec);
                    break;
                }
                case 519: {
                    if (this.outputNextStringValue) {
                        final StringRecord srec = (StringRecord)record;
                        thisText = srec.getString();
                        thisRow = this.nextRow;
                        this.outputNextStringValue = false;
                        break;
                    }
                    break;
                }
                case 516: {
                    final LabelRecord lrec = (LabelRecord)record;
                    thisRow = lrec.getRow();
                    thisText = lrec.getValue();
                    break;
                }
                case 253: {
                    final LabelSSTRecord lsrec = (LabelSSTRecord)record;
                    thisRow = lsrec.getRow();
                    if (this.sstRecord == null) {
                        throw new IllegalStateException("No SST record found");
                    }
                    thisText = this.sstRecord.getString(lsrec.getSSTIndex()).toString();
                    break;
                }
                case 28: {
                    final NoteRecord nrec = (NoteRecord)record;
                    thisRow = nrec.getRow();
                    break;
                }
                case 515: {
                    final NumberRecord numrec = (NumberRecord)record;
                    thisRow = numrec.getRow();
                    thisText = this._ft.formatNumberDateCell(numrec);
                    break;
                }
            }
            if (thisText != null) {
                if (thisRow != this.rowNum) {
                    this.rowNum = thisRow;
                    if (this._text.length() > 0) {
                        this._text.append("\n");
                    }
                }
                else {
                    this._text.append("\t");
                }
                this._text.append(thisText);
            }
        }
    }
}
