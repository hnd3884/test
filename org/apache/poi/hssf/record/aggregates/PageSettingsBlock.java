package org.apache.poi.hssf.record.aggregates;

import org.apache.poi.hssf.record.ContinueRecord;
import java.util.Map;
import org.apache.poi.hssf.record.UserSViewBegin;
import org.apache.poi.util.HexDump;
import java.util.HashMap;
import java.util.Collection;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.hssf.record.Margin;
import java.util.Iterator;
import org.apache.poi.util.RecordFormatException;
import org.apache.poi.hssf.record.VerticalPageBreakRecord;
import org.apache.poi.hssf.record.HorizontalPageBreakRecord;
import java.util.ArrayList;
import org.apache.poi.hssf.model.RecordStream;
import org.apache.poi.hssf.record.HeaderFooterRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.PrintSetupRecord;
import java.util.List;
import org.apache.poi.hssf.record.BottomMarginRecord;
import org.apache.poi.hssf.record.TopMarginRecord;
import org.apache.poi.hssf.record.RightMarginRecord;
import org.apache.poi.hssf.record.LeftMarginRecord;
import org.apache.poi.hssf.record.VCenterRecord;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.PageBreakRecord;

public final class PageSettingsBlock extends RecordAggregate
{
    private PageBreakRecord _rowBreaksRecord;
    private PageBreakRecord _columnBreaksRecord;
    private HeaderRecord _header;
    private FooterRecord _footer;
    private HCenterRecord _hCenter;
    private VCenterRecord _vCenter;
    private LeftMarginRecord _leftMargin;
    private RightMarginRecord _rightMargin;
    private TopMarginRecord _topMargin;
    private BottomMarginRecord _bottomMargin;
    private final List<PLSAggregate> _plsRecords;
    private PrintSetupRecord _printSetup;
    private Record _bitmap;
    private HeaderFooterRecord _headerFooter;
    private final List<HeaderFooterRecord> _sviewHeaderFooters;
    private Record _printSize;
    
    public PageSettingsBlock(final RecordStream rs) {
        this._sviewHeaderFooters = new ArrayList<HeaderFooterRecord>();
        this._plsRecords = new ArrayList<PLSAggregate>();
        while (this.readARecord(rs)) {}
    }
    
    public PageSettingsBlock() {
        this._sviewHeaderFooters = new ArrayList<HeaderFooterRecord>();
        this._plsRecords = new ArrayList<PLSAggregate>();
        this._rowBreaksRecord = new HorizontalPageBreakRecord();
        this._columnBreaksRecord = new VerticalPageBreakRecord();
        this._header = new HeaderRecord("");
        this._footer = new FooterRecord("");
        this._hCenter = createHCenter();
        this._vCenter = createVCenter();
        this._printSetup = createPrintSetup();
    }
    
    public static boolean isComponentRecord(final int sid) {
        switch (sid) {
            case 20:
            case 21:
            case 26:
            case 27:
            case 38:
            case 39:
            case 40:
            case 41:
            case 51:
            case 77:
            case 131:
            case 132:
            case 161:
            case 233:
            case 2204: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean readARecord(final RecordStream rs) {
        switch (rs.peekNextSid()) {
            case 27: {
                this.checkNotPresent(this._rowBreaksRecord);
                this._rowBreaksRecord = (PageBreakRecord)rs.getNext();
                break;
            }
            case 26: {
                this.checkNotPresent(this._columnBreaksRecord);
                this._columnBreaksRecord = (PageBreakRecord)rs.getNext();
                break;
            }
            case 20: {
                this.checkNotPresent(this._header);
                this._header = (HeaderRecord)rs.getNext();
                break;
            }
            case 21: {
                this.checkNotPresent(this._footer);
                this._footer = (FooterRecord)rs.getNext();
                break;
            }
            case 131: {
                this.checkNotPresent(this._hCenter);
                this._hCenter = (HCenterRecord)rs.getNext();
                break;
            }
            case 132: {
                this.checkNotPresent(this._vCenter);
                this._vCenter = (VCenterRecord)rs.getNext();
                break;
            }
            case 38: {
                this.checkNotPresent(this._leftMargin);
                this._leftMargin = (LeftMarginRecord)rs.getNext();
                break;
            }
            case 39: {
                this.checkNotPresent(this._rightMargin);
                this._rightMargin = (RightMarginRecord)rs.getNext();
                break;
            }
            case 40: {
                this.checkNotPresent(this._topMargin);
                this._topMargin = (TopMarginRecord)rs.getNext();
                break;
            }
            case 41: {
                this.checkNotPresent(this._bottomMargin);
                this._bottomMargin = (BottomMarginRecord)rs.getNext();
                break;
            }
            case 77: {
                this._plsRecords.add(new PLSAggregate(rs));
                break;
            }
            case 161: {
                this.checkNotPresent(this._printSetup);
                this._printSetup = (PrintSetupRecord)rs.getNext();
                break;
            }
            case 233: {
                this.checkNotPresent(this._bitmap);
                this._bitmap = rs.getNext();
                break;
            }
            case 51: {
                this.checkNotPresent(this._printSize);
                this._printSize = rs.getNext();
                break;
            }
            case 2204: {
                final HeaderFooterRecord hf = (HeaderFooterRecord)rs.getNext();
                if (hf.isCurrentSheet()) {
                    this._headerFooter = hf;
                    break;
                }
                this._sviewHeaderFooters.add(hf);
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }
    
    private void checkNotPresent(final Record rec) {
        if (rec != null) {
            throw new RecordFormatException("Duplicate PageSettingsBlock record (sid=0x" + Integer.toHexString(rec.getSid()) + ")");
        }
    }
    
    private PageBreakRecord getRowBreaksRecord() {
        if (this._rowBreaksRecord == null) {
            this._rowBreaksRecord = new HorizontalPageBreakRecord();
        }
        return this._rowBreaksRecord;
    }
    
    private PageBreakRecord getColumnBreaksRecord() {
        if (this._columnBreaksRecord == null) {
            this._columnBreaksRecord = new VerticalPageBreakRecord();
        }
        return this._columnBreaksRecord;
    }
    
    public void setColumnBreak(final short column, final short fromRow, final short toRow) {
        this.getColumnBreaksRecord().addBreak(column, fromRow, toRow);
    }
    
    public void removeColumnBreak(final int column) {
        this.getColumnBreaksRecord().removeBreak(column);
    }
    
    @Override
    public void visitContainedRecords(final RecordVisitor rv) {
        visitIfPresent(this._rowBreaksRecord, rv);
        visitIfPresent(this._columnBreaksRecord, rv);
        if (this._header == null) {
            rv.visitRecord(new HeaderRecord(""));
        }
        else {
            rv.visitRecord(this._header);
        }
        if (this._footer == null) {
            rv.visitRecord(new FooterRecord(""));
        }
        else {
            rv.visitRecord(this._footer);
        }
        visitIfPresent(this._hCenter, rv);
        visitIfPresent(this._vCenter, rv);
        visitIfPresent(this._leftMargin, rv);
        visitIfPresent(this._rightMargin, rv);
        visitIfPresent(this._topMargin, rv);
        visitIfPresent(this._bottomMargin, rv);
        for (final RecordAggregate pls : this._plsRecords) {
            pls.visitContainedRecords(rv);
        }
        visitIfPresent(this._printSetup, rv);
        visitIfPresent(this._printSize, rv);
        visitIfPresent(this._headerFooter, rv);
        visitIfPresent(this._bitmap, rv);
    }
    
    private static void visitIfPresent(final Record r, final RecordVisitor rv) {
        if (r != null) {
            rv.visitRecord(r);
        }
    }
    
    private static void visitIfPresent(final PageBreakRecord r, final RecordVisitor rv) {
        if (r != null) {
            if (r.isEmpty()) {
                return;
            }
            rv.visitRecord(r);
        }
    }
    
    private static HCenterRecord createHCenter() {
        final HCenterRecord retval = new HCenterRecord();
        retval.setHCenter(false);
        return retval;
    }
    
    private static VCenterRecord createVCenter() {
        final VCenterRecord retval = new VCenterRecord();
        retval.setVCenter(false);
        return retval;
    }
    
    private static PrintSetupRecord createPrintSetup() {
        final PrintSetupRecord retval = new PrintSetupRecord();
        retval.setPaperSize((short)1);
        retval.setScale((short)100);
        retval.setPageStart((short)1);
        retval.setFitWidth((short)1);
        retval.setFitHeight((short)1);
        retval.setOptions((short)2);
        retval.setHResolution((short)300);
        retval.setVResolution((short)300);
        retval.setHeaderMargin(0.5);
        retval.setFooterMargin(0.5);
        retval.setCopies((short)1);
        return retval;
    }
    
    public HeaderRecord getHeader() {
        return this._header;
    }
    
    public void setHeader(final HeaderRecord newHeader) {
        this._header = newHeader;
    }
    
    public FooterRecord getFooter() {
        return this._footer;
    }
    
    public void setFooter(final FooterRecord newFooter) {
        this._footer = newFooter;
    }
    
    public PrintSetupRecord getPrintSetup() {
        return this._printSetup;
    }
    
    public void setPrintSetup(final PrintSetupRecord newPrintSetup) {
        this._printSetup = newPrintSetup;
    }
    
    private Margin getMarginRec(final int marginIndex) {
        switch (marginIndex) {
            case 0: {
                return this._leftMargin;
            }
            case 1: {
                return this._rightMargin;
            }
            case 2: {
                return this._topMargin;
            }
            case 3: {
                return this._bottomMargin;
            }
            default: {
                throw new IllegalArgumentException("Unknown margin constant:  " + marginIndex);
            }
        }
    }
    
    public double getMargin(final short margin) {
        final Margin m = this.getMarginRec(margin);
        if (m != null) {
            return m.getMargin();
        }
        switch (margin) {
            case 0: {
                return 0.75;
            }
            case 1: {
                return 0.75;
            }
            case 2: {
                return 1.0;
            }
            case 3: {
                return 1.0;
            }
            default: {
                throw new IllegalArgumentException("Unknown margin constant:  " + margin);
            }
        }
    }
    
    public void setMargin(final short margin, final double size) {
        Margin m = this.getMarginRec(margin);
        if (m == null) {
            switch (margin) {
                case 0: {
                    this._leftMargin = new LeftMarginRecord();
                    m = this._leftMargin;
                    break;
                }
                case 1: {
                    this._rightMargin = new RightMarginRecord();
                    m = this._rightMargin;
                    break;
                }
                case 2: {
                    this._topMargin = new TopMarginRecord();
                    m = this._topMargin;
                    break;
                }
                case 3: {
                    this._bottomMargin = new BottomMarginRecord();
                    m = this._bottomMargin;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unknown margin constant:  " + margin);
                }
            }
        }
        m.setMargin(size);
    }
    
    private static void shiftBreaks(final PageBreakRecord breaks, final int start, final int stop, final int count) {
        Iterator<PageBreakRecord.Break> iterator = breaks.getBreaksIterator();
        final List<PageBreakRecord.Break> shiftedBreak = new ArrayList<PageBreakRecord.Break>();
        while (iterator.hasNext()) {
            final PageBreakRecord.Break breakItem = iterator.next();
            final int breakLocation = breakItem.main;
            final boolean inStart = breakLocation >= start;
            final boolean inEnd = breakLocation <= stop;
            if (inStart && inEnd) {
                shiftedBreak.add(breakItem);
            }
        }
        iterator = shiftedBreak.iterator();
        while (iterator.hasNext()) {
            final PageBreakRecord.Break breakItem = iterator.next();
            breaks.removeBreak(breakItem.main);
            breaks.addBreak((short)(breakItem.main + count), breakItem.subFrom, breakItem.subTo);
        }
    }
    
    public void setRowBreak(final int row, final short fromCol, final short toCol) {
        this.getRowBreaksRecord().addBreak((short)row, fromCol, toCol);
    }
    
    public void removeRowBreak(final int row) {
        if (this.getRowBreaksRecord().getBreaks().length < 1) {
            throw new IllegalArgumentException("Sheet does not define any row breaks");
        }
        this.getRowBreaksRecord().removeBreak((short)row);
    }
    
    public boolean isRowBroken(final int row) {
        return this.getRowBreaksRecord().getBreak(row) != null;
    }
    
    public boolean isColumnBroken(final int column) {
        return this.getColumnBreaksRecord().getBreak(column) != null;
    }
    
    public void shiftRowBreaks(final int startingRow, final int endingRow, final int count) {
        shiftBreaks(this.getRowBreaksRecord(), startingRow, endingRow, count);
    }
    
    public void shiftColumnBreaks(final short startingCol, final short endingCol, final short count) {
        shiftBreaks(this.getColumnBreaksRecord(), startingCol, endingCol, count);
    }
    
    public int[] getRowBreaks() {
        return this.getRowBreaksRecord().getBreaks();
    }
    
    public int getNumRowBreaks() {
        return this.getRowBreaksRecord().getNumBreaks();
    }
    
    public int[] getColumnBreaks() {
        return this.getColumnBreaksRecord().getBreaks();
    }
    
    public int getNumColumnBreaks() {
        return this.getColumnBreaksRecord().getNumBreaks();
    }
    
    public VCenterRecord getVCenter() {
        return this._vCenter;
    }
    
    public HCenterRecord getHCenter() {
        return this._hCenter;
    }
    
    public void addLateHeaderFooter(final HeaderFooterRecord rec) {
        if (this._headerFooter != null) {
            throw new IllegalStateException("This page settings block already has a header/footer record");
        }
        if (rec.getSid() != 2204) {
            throw new RecordFormatException("Unexpected header-footer record sid: 0x" + Integer.toHexString(rec.getSid()));
        }
        this._headerFooter = rec;
    }
    
    public void addLateRecords(final RecordStream rs) {
        while (this.readARecord(rs)) {}
    }
    
    public void positionRecords(final List<RecordBase> sheetRecords) {
        final List<HeaderFooterRecord> hfRecordsToIterate = new ArrayList<HeaderFooterRecord>(this._sviewHeaderFooters);
        final Map<String, HeaderFooterRecord> hfGuidMap = new HashMap<String, HeaderFooterRecord>();
        for (final HeaderFooterRecord hf : hfRecordsToIterate) {
            hfGuidMap.put(HexDump.toHex(hf.getGuid()), hf);
        }
        for (final RecordBase rb : sheetRecords) {
            if (rb instanceof CustomViewSettingsRecordAggregate) {
                final CustomViewSettingsRecordAggregate cv = (CustomViewSettingsRecordAggregate)rb;
                cv.visitContainedRecords(r -> {
                    if (r.getSid() == 426) {
                        final String guid = HexDump.toHex(((UserSViewBegin)r).getGuid());
                        final HeaderFooterRecord hf2 = hfGuidMap.get(guid);
                        if (hf2 != null) {
                            cv.append(hf2);
                            this._sviewHeaderFooters.remove(hf2);
                        }
                    }
                });
            }
        }
    }
    
    private static final class PLSAggregate extends RecordAggregate
    {
        private static final ContinueRecord[] EMPTY_CONTINUE_RECORD_ARRAY;
        private final Record _pls;
        private ContinueRecord[] _plsContinues;
        
        public PLSAggregate(final RecordStream rs) {
            this._pls = rs.getNext();
            if (rs.peekNextSid() == 60) {
                final List<ContinueRecord> temp = new ArrayList<ContinueRecord>();
                while (rs.peekNextSid() == 60) {
                    temp.add((ContinueRecord)rs.getNext());
                }
                temp.toArray(this._plsContinues = new ContinueRecord[temp.size()]);
            }
            else {
                this._plsContinues = PLSAggregate.EMPTY_CONTINUE_RECORD_ARRAY;
            }
        }
        
        @Override
        public void visitContainedRecords(final RecordVisitor rv) {
            rv.visitRecord(this._pls);
            for (final ContinueRecord _plsContinue : this._plsContinues) {
                rv.visitRecord(_plsContinue);
            }
        }
        
        static {
            EMPTY_CONTINUE_RECORD_ARRAY = new ContinueRecord[0];
        }
    }
}
