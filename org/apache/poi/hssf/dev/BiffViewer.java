package org.apache.poi.hssf.dev;

import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.SuppressForbidden;
import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.IOException;
import java.io.Closeable;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.io.Writer;
import java.io.OutputStreamWriter;
import org.apache.poi.util.StringUtil;
import java.io.FileOutputStream;
import org.apache.poi.hssf.record.UnknownRecord;
import org.apache.poi.hssf.record.pivottable.ExtendedPivotTableViewFieldsRecord;
import org.apache.poi.hssf.record.pivottable.DataItemRecord;
import org.apache.poi.hssf.record.pivottable.ViewFieldsRecord;
import org.apache.poi.hssf.record.pivottable.ViewDefinitionRecord;
import org.apache.poi.hssf.record.pivottable.PageItemRecord;
import org.apache.poi.hssf.record.pivottable.ViewSourceRecord;
import org.apache.poi.hssf.record.pivottable.StreamIDRecord;
import org.apache.poi.hssf.record.chart.ChartStartObjectRecord;
import org.apache.poi.hssf.record.chart.ChartStartBlockRecord;
import org.apache.poi.hssf.record.chart.ChartFRTInfoRecord;
import org.apache.poi.hssf.record.chart.ChartEndObjectRecord;
import org.apache.poi.hssf.record.chart.ChartEndBlockRecord;
import org.apache.poi.hssf.record.chart.CatLabRecord;
import org.apache.poi.hssf.record.WriteProtectRecord;
import org.apache.poi.hssf.record.WriteAccessRecord;
import org.apache.poi.hssf.record.WindowTwoRecord;
import org.apache.poi.hssf.record.WindowProtectRecord;
import org.apache.poi.hssf.record.WindowOneRecord;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.hssf.record.VerticalPageBreakRecord;
import org.apache.poi.hssf.record.chart.ValueRangeRecord;
import org.apache.poi.hssf.record.VCenterRecord;
import org.apache.poi.hssf.record.UseSelFSRecord;
import org.apache.poi.hssf.record.chart.UnitsRecord;
import org.apache.poi.hssf.record.UncalcedRecord;
import org.apache.poi.hssf.record.TopMarginRecord;
import org.apache.poi.hssf.record.chart.TickRecord;
import org.apache.poi.hssf.record.chart.TextRecord;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.hssf.record.TableRecord;
import org.apache.poi.hssf.record.TableStylesRecord;
import org.apache.poi.hssf.record.TabIdRecord;
import org.apache.poi.hssf.record.SupBookRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.record.chart.SheetPropertiesRecord;
import org.apache.poi.hssf.record.SharedFormulaRecord;
import org.apache.poi.hssf.record.chart.SeriesToChartGroupRecord;
import org.apache.poi.hssf.record.chart.SeriesTextRecord;
import org.apache.poi.hssf.record.chart.SeriesRecord;
import org.apache.poi.hssf.record.chart.SeriesListRecord;
import org.apache.poi.hssf.record.chart.SeriesIndexRecord;
import org.apache.poi.hssf.record.SelectionRecord;
import org.apache.poi.hssf.record.SaveRecalcRecord;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.RightMarginRecord;
import org.apache.poi.hssf.record.RefreshAllRecord;
import org.apache.poi.hssf.record.RefModeRecord;
import org.apache.poi.hssf.record.RecalcIdRecord;
import org.apache.poi.hssf.record.RKRecord;
import org.apache.poi.hssf.record.ProtectionRev4Record;
import org.apache.poi.hssf.record.ProtectRecord;
import org.apache.poi.hssf.record.PrintSetupRecord;
import org.apache.poi.hssf.record.PrintHeadersRecord;
import org.apache.poi.hssf.record.PrintGridlinesRecord;
import org.apache.poi.hssf.record.PrecisionRecord;
import org.apache.poi.hssf.record.chart.PlotGrowthRecord;
import org.apache.poi.hssf.record.chart.PlotAreaRecord;
import org.apache.poi.hssf.record.PasswordRev4Record;
import org.apache.poi.hssf.record.PasswordRecord;
import org.apache.poi.hssf.record.PaneRecord;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.chart.ObjectLinkRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.MulRKRecord;
import org.apache.poi.hssf.record.MulBlankRecord;
import org.apache.poi.hssf.record.MergeCellsRecord;
import org.apache.poi.hssf.record.MMSRecord;
import org.apache.poi.hssf.record.chart.LinkedDataRecord;
import org.apache.poi.hssf.record.chart.LineFormatRecord;
import org.apache.poi.hssf.record.chart.LegendRecord;
import org.apache.poi.hssf.record.LeftMarginRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.IterationRecord;
import org.apache.poi.hssf.record.InterfaceHdrRecord;
import org.apache.poi.hssf.record.InterfaceEndRecord;
import org.apache.poi.hssf.record.IndexRecord;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.HorizontalPageBreakRecord;
import org.apache.poi.hssf.record.HideObjRecord;
import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.HCenterRecord;
import org.apache.poi.hssf.record.GutsRecord;
import org.apache.poi.hssf.record.GridsetRecord;
import org.apache.poi.hssf.record.chart.FrameRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.FormatRecord;
import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.chart.FontIndexRecord;
import org.apache.poi.hssf.record.chart.FontBasisRecord;
import org.apache.poi.hssf.record.FnGroupCountRecord;
import org.apache.poi.hssf.record.FileSharingRecord;
import org.apache.poi.hssf.record.FilePassRecord;
import org.apache.poi.hssf.record.FeatHdrRecord;
import org.apache.poi.hssf.record.FeatRecord;
import org.apache.poi.hssf.record.ExternalNameRecord;
import org.apache.poi.hssf.record.ExternSheetRecord;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.hssf.record.ExtSSTRecord;
import org.apache.poi.hssf.record.chart.EndRecord;
import org.apache.poi.hssf.record.EOFRecord;
import org.apache.poi.hssf.record.DVALRecord;
import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.DrawingSelectionRecord;
import org.apache.poi.hssf.record.DrawingRecordForBiffViewer;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import org.apache.poi.hssf.record.DimensionsRecord;
import org.apache.poi.hssf.record.DeltaRecord;
import org.apache.poi.hssf.record.DefaultRowHeightRecord;
import org.apache.poi.hssf.record.chart.DefaultDataLabelTextPropertiesRecord;
import org.apache.poi.hssf.record.DefaultColWidthRecord;
import org.apache.poi.hssf.record.DConRefRecord;
import org.apache.poi.hssf.record.DateWindow1904Record;
import org.apache.poi.hssf.record.chart.DataFormatRecord;
import org.apache.poi.hssf.record.chart.DatRecord;
import org.apache.poi.hssf.record.DSFRecord;
import org.apache.poi.hssf.record.DBCellRecord;
import org.apache.poi.hssf.record.CountryRecord;
import org.apache.poi.hssf.record.ContinueRecord;
import org.apache.poi.hssf.record.ColumnInfoRecord;
import org.apache.poi.hssf.record.CodepageRecord;
import org.apache.poi.hssf.record.chart.ChartRecord;
import org.apache.poi.hssf.record.chart.ChartFormatRecord;
import org.apache.poi.hssf.record.chart.CategorySeriesAxisRecord;
import org.apache.poi.hssf.record.CalcModeRecord;
import org.apache.poi.hssf.record.CalcCountRecord;
import org.apache.poi.hssf.record.CFRule12Record;
import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.hssf.record.CFHeader12Record;
import org.apache.poi.hssf.record.CFHeaderRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.BottomMarginRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BookBoolRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.chart.BeginRecord;
import org.apache.poi.hssf.record.chart.BarRecord;
import org.apache.poi.hssf.record.BackupRecord;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.AutoFilterInfoRecord;
import org.apache.poi.hssf.record.chart.AxisUsedRecord;
import org.apache.poi.hssf.record.chart.AxisRecord;
import org.apache.poi.hssf.record.chart.AxisParentRecord;
import org.apache.poi.hssf.record.chart.AxisOptionsRecord;
import org.apache.poi.hssf.record.chart.AxisLineFormatRecord;
import org.apache.poi.hssf.record.ArrayRecord;
import org.apache.poi.hssf.record.chart.AreaRecord;
import org.apache.poi.hssf.record.chart.AreaFormatRecord;
import org.apache.poi.util.RecordFormatException;
import java.util.Iterator;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordInputStream;
import java.io.PrintWriter;
import java.io.InputStream;
import org.apache.poi.util.POILogger;

public final class BiffViewer
{
    private static final char[] NEW_LINE_CHARS;
    private static final POILogger logger;
    private static final int DUMP_LINE_LEN = 16;
    private static final char[] COLUMN_SEPARATOR;
    
    private BiffViewer() {
    }
    
    private static void createRecords(final InputStream is, final PrintWriter ps, final BiffRecordListener recListener, final boolean dumpInterpretedRecords) throws RecordFormatException {
        final RecordInputStream recStream = new RecordInputStream(is);
        while (true) {
            boolean hasNext;
            try {
                hasNext = recStream.hasNextRecord();
            }
            catch (final RecordInputStream.LeftoverDataException e) {
                BiffViewer.logger.log(7, "Discarding " + recStream.remaining() + " bytes and continuing", e);
                recStream.readRemainder();
                hasNext = recStream.hasNextRecord();
            }
            if (!hasNext) {
                break;
            }
            recStream.nextRecord();
            if (recStream.getSid() == 0) {
                continue;
            }
            if (dumpInterpretedRecords) {
                final Record record = createRecord(recStream);
                if (record.getSid() == 60) {
                    continue;
                }
                for (final String header : recListener.getRecentHeaders()) {
                    ps.println(header);
                }
                ps.print(record);
            }
            else {
                recStream.readRemainder();
            }
            ps.println();
        }
    }
    
    private static Record createRecord(final RecordInputStream in) {
        switch (in.getSid()) {
            case 4106: {
                return new AreaFormatRecord(in);
            }
            case 4122: {
                return new AreaRecord(in);
            }
            case 545: {
                return new ArrayRecord(in);
            }
            case 4129: {
                return new AxisLineFormatRecord(in);
            }
            case 4194: {
                return new AxisOptionsRecord(in);
            }
            case 4161: {
                return new AxisParentRecord(in);
            }
            case 4125: {
                return new AxisRecord(in);
            }
            case 4166: {
                return new AxisUsedRecord(in);
            }
            case 157: {
                return new AutoFilterInfoRecord(in);
            }
            case 2057: {
                return new BOFRecord(in);
            }
            case 64: {
                return new BackupRecord(in);
            }
            case 4119: {
                return new BarRecord(in);
            }
            case 4147: {
                return new BeginRecord(in);
            }
            case 513: {
                return new BlankRecord(in);
            }
            case 218: {
                return new BookBoolRecord(in);
            }
            case 517: {
                return new BoolErrRecord(in);
            }
            case 41: {
                return new BottomMarginRecord(in);
            }
            case 133: {
                return new BoundSheetRecord(in);
            }
            case 432: {
                return new CFHeaderRecord(in);
            }
            case 2169: {
                return new CFHeader12Record(in);
            }
            case 433: {
                return new CFRuleRecord(in);
            }
            case 2170: {
                return new CFRule12Record(in);
            }
            case 12: {
                return new CalcCountRecord(in);
            }
            case 13: {
                return new CalcModeRecord(in);
            }
            case 4128: {
                return new CategorySeriesAxisRecord(in);
            }
            case 4116: {
                return new ChartFormatRecord(in);
            }
            case 4098: {
                return new ChartRecord(in);
            }
            case 66: {
                return new CodepageRecord(in);
            }
            case 125: {
                return new ColumnInfoRecord(in);
            }
            case 60: {
                return new ContinueRecord(in);
            }
            case 140: {
                return new CountryRecord(in);
            }
            case 215: {
                return new DBCellRecord(in);
            }
            case 353: {
                return new DSFRecord(in);
            }
            case 4195: {
                return new DatRecord(in);
            }
            case 4102: {
                return new DataFormatRecord(in);
            }
            case 34: {
                return new DateWindow1904Record(in);
            }
            case 81: {
                return new DConRefRecord(in);
            }
            case 85: {
                return new DefaultColWidthRecord(in);
            }
            case 4132: {
                return new DefaultDataLabelTextPropertiesRecord(in);
            }
            case 549: {
                return new DefaultRowHeightRecord(in);
            }
            case 16: {
                return new DeltaRecord(in);
            }
            case 512: {
                return new DimensionsRecord(in);
            }
            case 235: {
                return new DrawingGroupRecord(in);
            }
            case 236: {
                return new DrawingRecordForBiffViewer(in);
            }
            case 237: {
                return new DrawingSelectionRecord(in);
            }
            case 446: {
                return new DVRecord(in);
            }
            case 434: {
                return new DVALRecord(in);
            }
            case 10: {
                return new EOFRecord(in);
            }
            case 4148: {
                return new EndRecord(in);
            }
            case 255: {
                return new ExtSSTRecord(in);
            }
            case 224: {
                return new ExtendedFormatRecord(in);
            }
            case 23: {
                return new ExternSheetRecord(in);
            }
            case 35: {
                return new ExternalNameRecord(in);
            }
            case 2152: {
                return new FeatRecord(in);
            }
            case 2151: {
                return new FeatHdrRecord(in);
            }
            case 47: {
                return new FilePassRecord(in);
            }
            case 91: {
                return new FileSharingRecord(in);
            }
            case 156: {
                return new FnGroupCountRecord(in);
            }
            case 4192: {
                return new FontBasisRecord(in);
            }
            case 4134: {
                return new FontIndexRecord(in);
            }
            case 49: {
                return new FontRecord(in);
            }
            case 21: {
                return new FooterRecord(in);
            }
            case 1054: {
                return new FormatRecord(in);
            }
            case 6: {
                return new FormulaRecord(in);
            }
            case 4146: {
                return new FrameRecord(in);
            }
            case 130: {
                return new GridsetRecord(in);
            }
            case 128: {
                return new GutsRecord(in);
            }
            case 131: {
                return new HCenterRecord(in);
            }
            case 20: {
                return new HeaderRecord(in);
            }
            case 141: {
                return new HideObjRecord(in);
            }
            case 27: {
                return new HorizontalPageBreakRecord(in);
            }
            case 440: {
                return new HyperlinkRecord(in);
            }
            case 523: {
                return new IndexRecord(in);
            }
            case 226: {
                return InterfaceEndRecord.create(in);
            }
            case 225: {
                return new InterfaceHdrRecord(in);
            }
            case 17: {
                return new IterationRecord(in);
            }
            case 516: {
                return new LabelRecord(in);
            }
            case 253: {
                return new LabelSSTRecord(in);
            }
            case 38: {
                return new LeftMarginRecord(in);
            }
            case 4117: {
                return new LegendRecord(in);
            }
            case 4103: {
                return new LineFormatRecord(in);
            }
            case 4177: {
                return new LinkedDataRecord(in);
            }
            case 193: {
                return new MMSRecord(in);
            }
            case 229: {
                return new MergeCellsRecord(in);
            }
            case 190: {
                return new MulBlankRecord(in);
            }
            case 189: {
                return new MulRKRecord(in);
            }
            case 24: {
                return new NameRecord(in);
            }
            case 2196: {
                return new NameCommentRecord(in);
            }
            case 28: {
                return new NoteRecord(in);
            }
            case 515: {
                return new NumberRecord(in);
            }
            case 93: {
                return new ObjRecord(in);
            }
            case 4135: {
                return new ObjectLinkRecord(in);
            }
            case 146: {
                return new PaletteRecord(in);
            }
            case 65: {
                return new PaneRecord(in);
            }
            case 19: {
                return new PasswordRecord(in);
            }
            case 444: {
                return new PasswordRev4Record(in);
            }
            case 4149: {
                return new PlotAreaRecord(in);
            }
            case 4196: {
                return new PlotGrowthRecord(in);
            }
            case 14: {
                return new PrecisionRecord(in);
            }
            case 43: {
                return new PrintGridlinesRecord(in);
            }
            case 42: {
                return new PrintHeadersRecord(in);
            }
            case 161: {
                return new PrintSetupRecord(in);
            }
            case 18: {
                return new ProtectRecord(in);
            }
            case 431: {
                return new ProtectionRev4Record(in);
            }
            case 638: {
                return new RKRecord(in);
            }
            case 449: {
                return new RecalcIdRecord(in);
            }
            case 15: {
                return new RefModeRecord(in);
            }
            case 439: {
                return new RefreshAllRecord(in);
            }
            case 39: {
                return new RightMarginRecord(in);
            }
            case 520: {
                return new RowRecord(in);
            }
            case 160: {
                return new SCLRecord(in);
            }
            case 252: {
                return new SSTRecord(in);
            }
            case 95: {
                return new SaveRecalcRecord(in);
            }
            case 29: {
                return new SelectionRecord(in);
            }
            case 4197: {
                return new SeriesIndexRecord(in);
            }
            case 4118: {
                return new SeriesListRecord(in);
            }
            case 4099: {
                return new SeriesRecord(in);
            }
            case 4109: {
                return new SeriesTextRecord(in);
            }
            case 4165: {
                return new SeriesToChartGroupRecord(in);
            }
            case 1212: {
                return new SharedFormulaRecord(in);
            }
            case 4164: {
                return new SheetPropertiesRecord(in);
            }
            case 519: {
                return new StringRecord(in);
            }
            case 659: {
                return new StyleRecord(in);
            }
            case 430: {
                return new SupBookRecord(in);
            }
            case 317: {
                return new TabIdRecord(in);
            }
            case 2190: {
                return new TableStylesRecord(in);
            }
            case 566: {
                return new TableRecord(in);
            }
            case 438: {
                return new TextObjectRecord(in);
            }
            case 4133: {
                return new TextRecord(in);
            }
            case 4126: {
                return new TickRecord(in);
            }
            case 40: {
                return new TopMarginRecord(in);
            }
            case 94: {
                return new UncalcedRecord(in);
            }
            case 4097: {
                return new UnitsRecord(in);
            }
            case 352: {
                return new UseSelFSRecord(in);
            }
            case 132: {
                return new VCenterRecord(in);
            }
            case 4127: {
                return new ValueRangeRecord(in);
            }
            case 26: {
                return new VerticalPageBreakRecord(in);
            }
            case 129: {
                return new WSBoolRecord(in);
            }
            case 61: {
                return new WindowOneRecord(in);
            }
            case 25: {
                return new WindowProtectRecord(in);
            }
            case 574: {
                return new WindowTwoRecord(in);
            }
            case 92: {
                return new WriteAccessRecord(in);
            }
            case 134: {
                return new WriteProtectRecord(in);
            }
            case 2134: {
                return new CatLabRecord(in);
            }
            case 2131: {
                return new ChartEndBlockRecord(in);
            }
            case 2133: {
                return new ChartEndObjectRecord(in);
            }
            case 2128: {
                return new ChartFRTInfoRecord(in);
            }
            case 2130: {
                return new ChartStartBlockRecord(in);
            }
            case 2132: {
                return new ChartStartObjectRecord(in);
            }
            case 213: {
                return new StreamIDRecord(in);
            }
            case 227: {
                return new ViewSourceRecord(in);
            }
            case 182: {
                return new PageItemRecord(in);
            }
            case 176: {
                return new ViewDefinitionRecord(in);
            }
            case 177: {
                return new ViewFieldsRecord(in);
            }
            case 197: {
                return new DataItemRecord(in);
            }
            case 256: {
                return new ExtendedPivotTableViewFieldsRecord(in);
            }
            default: {
                return new UnknownRecord(in);
            }
        }
    }
    
    public static void main(final String[] args) throws IOException, CommandParseException {
        final CommandArgs cmdArgs = CommandArgs.parse(args);
        PrintWriter pw;
        if (cmdArgs.shouldOutputToFile()) {
            final OutputStream os = new FileOutputStream(cmdArgs.getFile().getAbsolutePath() + ".out");
            pw = new PrintWriter(new OutputStreamWriter(os, StringUtil.UTF8));
        }
        else {
            pw = new PrintWriter(new OutputStreamWriter(System.out, Charset.defaultCharset()));
        }
        POIFSFileSystem fs = null;
        InputStream is = null;
        try {
            fs = new POIFSFileSystem(cmdArgs.getFile(), true);
            is = getPOIFSInputStream(fs);
            if (cmdArgs.shouldOutputRawHexOnly()) {
                final byte[] data = IOUtils.toByteArray(is);
                HexDump.dump(data, 0L, System.out, 0);
            }
            else {
                final boolean dumpInterpretedRecords = cmdArgs.shouldDumpRecordInterpretations();
                final boolean dumpHex = cmdArgs.shouldDumpBiffHex();
                runBiffViewer(pw, is, dumpInterpretedRecords, dumpHex, dumpInterpretedRecords, cmdArgs.suppressHeader());
            }
        }
        finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fs);
            IOUtils.closeQuietly(pw);
        }
    }
    
    static InputStream getPOIFSInputStream(final POIFSFileSystem fs) throws IOException {
        final String workbookName = HSSFWorkbook.getWorkbookDirEntryName(fs.getRoot());
        return fs.createDocumentInputStream(workbookName);
    }
    
    static void runBiffViewer(final PrintWriter pw, InputStream is, final boolean dumpInterpretedRecords, final boolean dumpHex, final boolean zeroAlignHexDump, final boolean suppressHeader) {
        final BiffRecordListener recListener = new BiffRecordListener((Writer)(dumpHex ? pw : null), zeroAlignHexDump, suppressHeader);
        is = new BiffDumpingStream(is, (IBiffRecordListener)recListener);
        createRecords(is, pw, recListener, dumpInterpretedRecords);
    }
    
    private static void hexDumpAligned(final Writer w, final byte[] data, final int dumpLen, final int globalOffset, final boolean zeroAlignEachRecord) {
        final int baseDataOffset = 0;
        final int globalStart = globalOffset + baseDataOffset;
        final int globalEnd = globalOffset + baseDataOffset + dumpLen;
        int startDelta = globalStart % 16;
        int endDelta = globalEnd % 16;
        if (zeroAlignEachRecord) {
            endDelta -= startDelta;
            if (endDelta < 0) {
                endDelta += 16;
            }
            startDelta = 0;
        }
        int endLineAddr;
        int startLineAddr;
        if (zeroAlignEachRecord) {
            endLineAddr = globalEnd - endDelta - (globalStart - startDelta);
            startLineAddr = 0;
        }
        else {
            startLineAddr = globalStart - startDelta;
            endLineAddr = globalEnd - endDelta;
        }
        int lineDataOffset = baseDataOffset - startDelta;
        int lineAddr = startLineAddr;
        if (startLineAddr == endLineAddr) {
            hexDumpLine(w, data, lineAddr, lineDataOffset, startDelta, endDelta);
            return;
        }
        hexDumpLine(w, data, lineAddr, lineDataOffset, startDelta, 16);
        while (true) {
            lineAddr += 16;
            lineDataOffset += 16;
            if (lineAddr >= endLineAddr) {
                break;
            }
            hexDumpLine(w, data, lineAddr, lineDataOffset, 0, 16);
        }
        if (endDelta != 0) {
            hexDumpLine(w, data, lineAddr, lineDataOffset, 0, endDelta);
        }
    }
    
    private static void hexDumpLine(final Writer w, final byte[] data, final int lineStartAddress, final int lineDataOffset, final int startDelta, final int endDelta) {
        final char[] buf = new char[8 + 2 * BiffViewer.COLUMN_SEPARATOR.length + 48 - 1 + 16 + BiffViewer.NEW_LINE_CHARS.length];
        if (startDelta >= endDelta) {
            throw new IllegalArgumentException("Bad start/end delta");
        }
        int idx = 0;
        try {
            writeHex(buf, idx, lineStartAddress, 8);
            idx = arraycopy(BiffViewer.COLUMN_SEPARATOR, buf, idx + 8);
            for (int i = 0; i < 16; ++i) {
                if (i > 0) {
                    buf[idx++] = ' ';
                }
                if (i >= startDelta && i < endDelta) {
                    writeHex(buf, idx, data[lineDataOffset + i], 2);
                }
                else {
                    buf[idx + 1] = (buf[idx] = ' ');
                }
                idx += 2;
            }
            idx = arraycopy(BiffViewer.COLUMN_SEPARATOR, buf, idx);
            for (int i = 0; i < 16; ++i) {
                char ch = ' ';
                if (i >= startDelta && i < endDelta) {
                    ch = getPrintableChar(data[lineDataOffset + i]);
                }
                buf[idx++] = ch;
            }
            idx = arraycopy(BiffViewer.NEW_LINE_CHARS, buf, idx);
            w.write(buf, 0, idx);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static int arraycopy(final char[] in, final char[] out, final int pos) {
        int idx = pos;
        for (final char c : in) {
            out[idx++] = c;
        }
        return idx;
    }
    
    private static char getPrintableChar(final byte b) {
        final char ib = (char)(b & 0xFF);
        if (ib < ' ' || ib > '~') {
            return '.';
        }
        return ib;
    }
    
    private static void writeHex(final char[] buf, final int startInBuf, final int value, final int nDigits) {
        int acc = value;
        for (int i = nDigits - 1; i >= 0; --i) {
            final int digit = acc & 0xF;
            buf[startInBuf + i] = (char)((digit < 10) ? (48 + digit) : (65 + digit - 10));
            acc >>>= 4;
        }
    }
    
    static {
        NEW_LINE_CHARS = System.getProperty("line.separator").toCharArray();
        logger = POILogFactory.getLogger(BiffViewer.class);
        COLUMN_SEPARATOR = " | ".toCharArray();
    }
    
    private static final class CommandArgs
    {
        private final boolean _biffhex;
        private final boolean _noint;
        private final boolean _out;
        private final boolean _rawhex;
        private final boolean _noHeader;
        private final File _file;
        
        private CommandArgs(final boolean biffhex, final boolean noint, final boolean out, final boolean rawhex, final boolean noHeader, final File file) {
            this._biffhex = biffhex;
            this._noint = noint;
            this._out = out;
            this._rawhex = rawhex;
            this._file = file;
            this._noHeader = noHeader;
        }
        
        public static CommandArgs parse(final String[] args) throws CommandParseException {
            final int nArgs = args.length;
            boolean biffhex = false;
            boolean noint = false;
            boolean out = false;
            boolean rawhex = false;
            boolean noheader = false;
            File file = null;
            for (int i = 0; i < nArgs; ++i) {
                final String arg = args[i];
                if (arg.startsWith("--")) {
                    if ("--biffhex".equals(arg)) {
                        biffhex = true;
                    }
                    else if ("--noint".equals(arg)) {
                        noint = true;
                    }
                    else if ("--out".equals(arg)) {
                        out = true;
                    }
                    else if ("--escher".equals(arg)) {
                        System.setProperty("poi.deserialize.escher", "true");
                    }
                    else if ("--rawhex".equals(arg)) {
                        rawhex = true;
                    }
                    else {
                        if (!"--noheader".equals(arg)) {
                            throw new CommandParseException("Unexpected option '" + arg + "'");
                        }
                        noheader = true;
                    }
                }
                else {
                    file = new File(arg);
                    if (!file.exists()) {
                        throw new CommandParseException("Specified file '" + arg + "' does not exist");
                    }
                    if (i + 1 < nArgs) {
                        throw new CommandParseException("File name must be the last arg");
                    }
                }
            }
            if (file == null) {
                throw new CommandParseException("Biff viewer needs a filename");
            }
            return new CommandArgs(biffhex, noint, out, rawhex, noheader, file);
        }
        
        boolean shouldDumpBiffHex() {
            return this._biffhex;
        }
        
        boolean shouldDumpRecordInterpretations() {
            return !this._noint;
        }
        
        boolean shouldOutputToFile() {
            return this._out;
        }
        
        boolean shouldOutputRawHexOnly() {
            return this._rawhex;
        }
        
        boolean suppressHeader() {
            return this._noHeader;
        }
        
        public File getFile() {
            return this._file;
        }
    }
    
    private static final class CommandParseException extends Exception
    {
        CommandParseException(final String msg) {
            super(msg);
        }
    }
    
    private static final class BiffRecordListener implements IBiffRecordListener
    {
        private final Writer _hexDumpWriter;
        private List<String> _headers;
        private final boolean _zeroAlignEachRecord;
        private final boolean _noHeader;
        
        private BiffRecordListener(final Writer hexDumpWriter, final boolean zeroAlignEachRecord, final boolean noHeader) {
            this._hexDumpWriter = hexDumpWriter;
            this._zeroAlignEachRecord = zeroAlignEachRecord;
            this._noHeader = noHeader;
            this._headers = new ArrayList<String>();
        }
        
        @Override
        public void processRecord(final int globalOffset, final int recordCounter, final int sid, final int dataSize, final byte[] data) {
            final String header = formatRecordDetails(globalOffset, sid, dataSize, recordCounter);
            if (!this._noHeader) {
                this._headers.add(header);
            }
            final Writer w = this._hexDumpWriter;
            if (w != null) {
                try {
                    w.write(header);
                    w.write(BiffViewer.NEW_LINE_CHARS);
                    hexDumpAligned(w, data, dataSize + 4, globalOffset, this._zeroAlignEachRecord);
                    w.flush();
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        
        private List<String> getRecentHeaders() {
            final List<String> result = this._headers;
            this._headers = new ArrayList<String>();
            return result;
        }
        
        private static String formatRecordDetails(final int globalOffset, final int sid, final int size, final int recordCounter) {
            return "Offset=" + HexDump.intToHex(globalOffset) + "(" + globalOffset + ") recno=" + recordCounter + " sid=" + HexDump.shortToHex(sid) + " size=" + HexDump.shortToHex(size) + "(" + size + ")";
        }
    }
    
    private static final class BiffDumpingStream extends InputStream
    {
        private final DataInputStream _is;
        private final IBiffRecordListener _listener;
        private final byte[] _data;
        private int _recordCounter;
        private int _overallStreamPos;
        private int _currentPos;
        private int _currentSize;
        private boolean _innerHasReachedEOF;
        
        private BiffDumpingStream(final InputStream is, final IBiffRecordListener listener) {
            this._is = new DataInputStream(is);
            this._listener = listener;
            this._data = new byte[8228];
            this._recordCounter = 0;
            this._overallStreamPos = 0;
            this._currentSize = 0;
            this._currentPos = 0;
        }
        
        @Override
        public int read() throws IOException {
            if (this._currentPos >= this._currentSize) {
                this.fillNextBuffer();
            }
            if (this._currentPos >= this._currentSize) {
                return -1;
            }
            final int result = this._data[this._currentPos] & 0xFF;
            ++this._currentPos;
            ++this._overallStreamPos;
            this.formatBufferIfAtEndOfRec();
            return result;
        }
        
        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            if (b == null || off < 0 || len < 0 || b.length < off + len) {
                throw new IllegalArgumentException();
            }
            if (this._currentPos >= this._currentSize) {
                this.fillNextBuffer();
            }
            if (this._currentPos >= this._currentSize) {
                return -1;
            }
            final int result = Math.min(len, this._currentSize - this._currentPos);
            System.arraycopy(this._data, this._currentPos, b, off, result);
            this._currentPos += result;
            this._overallStreamPos += result;
            this.formatBufferIfAtEndOfRec();
            return result;
        }
        
        @SuppressForbidden("just delegating the call")
        @Override
        public int available() throws IOException {
            return this._currentSize - this._currentPos + this._is.available();
        }
        
        private void fillNextBuffer() throws IOException {
            if (this._innerHasReachedEOF) {
                return;
            }
            final int b0 = this._is.read();
            if (b0 == -1) {
                this._innerHasReachedEOF = true;
                return;
            }
            this._data[0] = (byte)b0;
            this._is.readFully(this._data, 1, 3);
            final int len = LittleEndian.getShort(this._data, 2);
            this._is.readFully(this._data, 4, len);
            this._currentPos = 0;
            this._currentSize = len + 4;
            ++this._recordCounter;
        }
        
        private void formatBufferIfAtEndOfRec() {
            if (this._currentPos != this._currentSize) {
                return;
            }
            final int dataSize = this._currentSize - 4;
            final int sid = LittleEndian.getShort(this._data, 0);
            final int globalOffset = this._overallStreamPos - this._currentSize;
            this._listener.processRecord(globalOffset, this._recordCounter, sid, dataSize, this._data);
        }
        
        @Override
        public void close() throws IOException {
            this._is.close();
        }
    }
    
    private interface IBiffRecordListener
    {
        void processRecord(final int p0, final int p1, final int p2, final int p3, final byte[] p4);
    }
}
