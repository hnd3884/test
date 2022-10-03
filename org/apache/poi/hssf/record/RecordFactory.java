package org.apache.poi.hssf.record;

import java.lang.reflect.InvocationTargetException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.record.pivottable.ViewSourceRecord;
import org.apache.poi.hssf.record.pivottable.ViewFieldsRecord;
import org.apache.poi.hssf.record.pivottable.ViewDefinitionRecord;
import org.apache.poi.hssf.record.pivottable.StreamIDRecord;
import org.apache.poi.hssf.record.pivottable.PageItemRecord;
import org.apache.poi.hssf.record.pivottable.ExtendedPivotTableViewFieldsRecord;
import org.apache.poi.hssf.record.pivottable.DataItemRecord;
import org.apache.poi.hssf.record.chart.SeriesToChartGroupRecord;
import org.apache.poi.hssf.record.chart.LinkedDataRecord;
import org.apache.poi.hssf.record.chart.EndRecord;
import org.apache.poi.hssf.record.chart.DataFormatRecord;
import org.apache.poi.hssf.record.chart.CatLabRecord;
import org.apache.poi.hssf.record.chart.ChartEndObjectRecord;
import org.apache.poi.hssf.record.chart.ChartStartObjectRecord;
import org.apache.poi.hssf.record.chart.ChartEndBlockRecord;
import org.apache.poi.hssf.record.chart.ChartStartBlockRecord;
import org.apache.poi.hssf.record.chart.ChartFRTInfoRecord;
import org.apache.poi.hssf.record.chart.BeginRecord;
import org.apache.poi.hssf.record.chart.ValueRangeRecord;
import org.apache.poi.hssf.record.chart.SeriesTextRecord;
import org.apache.poi.hssf.record.chart.SeriesRecord;
import org.apache.poi.hssf.record.chart.LegendRecord;
import org.apache.poi.hssf.record.chart.ChartTitleFormatRecord;
import org.apache.poi.hssf.record.chart.ChartRecord;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.Locale;
import org.apache.poi.util.RecordFormatException;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;

public final class RecordFactory
{
    private static final int NUM_RECORDS = 512;
    private static final Class<?>[] CONSTRUCTOR_ARGS;
    private static final Class<? extends Record>[] recordClasses;
    private static final Map<Integer, I_RecordCreator> _recordCreatorsById;
    private static short[] _allKnownRecordSIDs;
    
    public static Class<? extends Record> getRecordClass(final int sid) {
        final I_RecordCreator rc = RecordFactory._recordCreatorsById.get(sid);
        if (rc == null) {
            return null;
        }
        return rc.getRecordClass();
    }
    
    public static Record[] createRecord(final RecordInputStream in) {
        final Record record = createSingleRecord(in);
        if (record instanceof DBCellRecord) {
            return new Record[] { null };
        }
        if (record instanceof RKRecord) {
            return new Record[] { convertToNumberRecord((RKRecord)record) };
        }
        if (record instanceof MulRKRecord) {
            return convertRKRecords((MulRKRecord)record);
        }
        return new Record[] { record };
    }
    
    public static Record createSingleRecord(final RecordInputStream in) {
        final I_RecordCreator constructor = RecordFactory._recordCreatorsById.get((int)in.getSid());
        if (constructor == null) {
            return new UnknownRecord(in);
        }
        return constructor.create(in);
    }
    
    public static NumberRecord convertToNumberRecord(final RKRecord rk) {
        final NumberRecord num = new NumberRecord();
        num.setColumn(rk.getColumn());
        num.setRow(rk.getRow());
        num.setXFIndex(rk.getXFIndex());
        num.setValue(rk.getRKNumber());
        return num;
    }
    
    public static NumberRecord[] convertRKRecords(final MulRKRecord mrk) {
        final NumberRecord[] mulRecs = new NumberRecord[mrk.getNumColumns()];
        for (int k = 0; k < mrk.getNumColumns(); ++k) {
            final NumberRecord nr = new NumberRecord();
            nr.setColumn((short)(k + mrk.getFirstColumn()));
            nr.setRow(mrk.getRow());
            nr.setXFIndex(mrk.getXFAt(k));
            nr.setValue(mrk.getRKNumberAt(k));
            mulRecs[k] = nr;
        }
        return mulRecs;
    }
    
    public static BlankRecord[] convertBlankRecords(final MulBlankRecord mbk) {
        final BlankRecord[] mulRecs = new BlankRecord[mbk.getNumColumns()];
        for (int k = 0; k < mbk.getNumColumns(); ++k) {
            final BlankRecord br = new BlankRecord();
            br.setColumn((short)(k + mbk.getFirstColumn()));
            br.setRow(mbk.getRow());
            br.setXFIndex(mbk.getXFAt(k));
            mulRecs[k] = br;
        }
        return mulRecs;
    }
    
    public static short[] getAllKnownRecordSIDs() {
        if (RecordFactory._allKnownRecordSIDs == null) {
            final short[] results = new short[RecordFactory._recordCreatorsById.size()];
            int i = 0;
            for (final Integer sid : RecordFactory._recordCreatorsById.keySet()) {
                results[i++] = sid.shortValue();
            }
            Arrays.sort(results);
            RecordFactory._allKnownRecordSIDs = results;
        }
        return RecordFactory._allKnownRecordSIDs.clone();
    }
    
    private static Map<Integer, I_RecordCreator> recordsToMap(final Class<? extends Record>[] records) {
        final Map<Integer, I_RecordCreator> result = new HashMap<Integer, I_RecordCreator>();
        final Set<Class<?>> uniqueRecClasses = new HashSet<Class<?>>(records.length * 3 / 2);
        for (final Class<? extends Record> recClass : records) {
            if (!Record.class.isAssignableFrom(recClass)) {
                throw new RuntimeException("Invalid record sub-class (" + recClass.getName() + ")");
            }
            if (Modifier.isAbstract(recClass.getModifiers())) {
                throw new RuntimeException("Invalid record class (" + recClass.getName() + ") - must not be abstract");
            }
            if (!uniqueRecClasses.add(recClass)) {
                throw new RuntimeException("duplicate record class (" + recClass.getName() + ")");
            }
            int sid;
            try {
                sid = recClass.getField("sid").getShort(null);
            }
            catch (final Exception illegalArgumentException) {
                throw new RecordFormatException("Unable to determine record types");
            }
            final Integer key = sid;
            if (result.containsKey(key)) {
                final Class<?> prevClass = result.get(key).getRecordClass();
                throw new RuntimeException("duplicate record sid 0x" + Integer.toHexString(sid).toUpperCase(Locale.ROOT) + " for classes (" + recClass.getName() + ") and (" + prevClass.getName() + ")");
            }
            result.put(key, getRecordCreator(recClass));
        }
        return result;
    }
    
    private static I_RecordCreator getRecordCreator(final Class<? extends Record> recClass) {
        try {
            final Constructor<? extends Record> constructor = recClass.getConstructor(RecordFactory.CONSTRUCTOR_ARGS);
            return new ReflectionConstructorRecordCreator(constructor);
        }
        catch (final NoSuchMethodException ex) {
            try {
                final Method m = recClass.getDeclaredMethod("create", RecordFactory.CONSTRUCTOR_ARGS);
                return new ReflectionMethodRecordCreator(m);
            }
            catch (final NoSuchMethodException e) {
                throw new RuntimeException("Failed to find constructor or create method for (" + recClass.getName() + ").");
            }
        }
    }
    
    public static List<Record> createRecords(final InputStream in) throws RecordFormatException {
        final List<Record> records = new ArrayList<Record>(512);
        final RecordFactoryInputStream recStream = new RecordFactoryInputStream(in, true);
        Record record;
        while ((record = recStream.nextRecord()) != null) {
            records.add(record);
        }
        return records;
    }
    
    static {
        CONSTRUCTOR_ARGS = new Class[] { RecordInputStream.class };
        recordClasses = new Class[] { ArrayRecord.class, AutoFilterInfoRecord.class, BackupRecord.class, BlankRecord.class, BOFRecord.class, BookBoolRecord.class, BoolErrRecord.class, BottomMarginRecord.class, BoundSheetRecord.class, CalcCountRecord.class, CalcModeRecord.class, CFHeaderRecord.class, CFHeader12Record.class, CFRuleRecord.class, CFRule12Record.class, ChartRecord.class, ChartTitleFormatRecord.class, CodepageRecord.class, ColumnInfoRecord.class, ContinueRecord.class, CountryRecord.class, CRNCountRecord.class, CRNRecord.class, DateWindow1904Record.class, DBCellRecord.class, DConRefRecord.class, DefaultColWidthRecord.class, DefaultRowHeightRecord.class, DeltaRecord.class, DimensionsRecord.class, DrawingGroupRecord.class, DrawingRecord.class, DrawingSelectionRecord.class, DSFRecord.class, DVALRecord.class, DVRecord.class, EOFRecord.class, ExtendedFormatRecord.class, ExternalNameRecord.class, ExternSheetRecord.class, ExtSSTRecord.class, FeatRecord.class, FeatHdrRecord.class, FilePassRecord.class, FileSharingRecord.class, FnGroupCountRecord.class, FontRecord.class, FooterRecord.class, FormatRecord.class, FormulaRecord.class, GridsetRecord.class, GutsRecord.class, HCenterRecord.class, HeaderRecord.class, HeaderFooterRecord.class, HideObjRecord.class, HorizontalPageBreakRecord.class, HyperlinkRecord.class, IndexRecord.class, InterfaceEndRecord.class, InterfaceHdrRecord.class, IterationRecord.class, LabelRecord.class, LabelSSTRecord.class, LeftMarginRecord.class, LegendRecord.class, MergeCellsRecord.class, MMSRecord.class, MulBlankRecord.class, MulRKRecord.class, NameRecord.class, NameCommentRecord.class, NoteRecord.class, NumberRecord.class, ObjectProtectRecord.class, ObjRecord.class, PaletteRecord.class, PaneRecord.class, PasswordRecord.class, PasswordRev4Record.class, PrecisionRecord.class, PrintGridlinesRecord.class, PrintHeadersRecord.class, PrintSetupRecord.class, ProtectionRev4Record.class, ProtectRecord.class, RecalcIdRecord.class, RefModeRecord.class, RefreshAllRecord.class, RightMarginRecord.class, RKRecord.class, RowRecord.class, SaveRecalcRecord.class, ScenarioProtectRecord.class, SelectionRecord.class, SeriesRecord.class, SeriesTextRecord.class, SharedFormulaRecord.class, SSTRecord.class, StringRecord.class, StyleRecord.class, SupBookRecord.class, TabIdRecord.class, TableRecord.class, TableStylesRecord.class, TextObjectRecord.class, TopMarginRecord.class, UncalcedRecord.class, UseSelFSRecord.class, UserSViewBegin.class, UserSViewEnd.class, ValueRangeRecord.class, VCenterRecord.class, VerticalPageBreakRecord.class, WindowOneRecord.class, WindowProtectRecord.class, WindowTwoRecord.class, WriteAccessRecord.class, WriteProtectRecord.class, WSBoolRecord.class, BeginRecord.class, ChartFRTInfoRecord.class, ChartStartBlockRecord.class, ChartEndBlockRecord.class, ChartStartObjectRecord.class, ChartEndObjectRecord.class, CatLabRecord.class, DataFormatRecord.class, EndRecord.class, LinkedDataRecord.class, SeriesToChartGroupRecord.class, DataItemRecord.class, ExtendedPivotTableViewFieldsRecord.class, PageItemRecord.class, StreamIDRecord.class, ViewDefinitionRecord.class, ViewFieldsRecord.class, ViewSourceRecord.class };
        _recordCreatorsById = recordsToMap(RecordFactory.recordClasses);
    }
    
    private static final class ReflectionConstructorRecordCreator implements I_RecordCreator
    {
        private final Constructor<? extends Record> _c;
        
        public ReflectionConstructorRecordCreator(final Constructor<? extends Record> c) {
            this._c = c;
        }
        
        @Override
        public Record create(final RecordInputStream in) {
            final Object[] args = { in };
            try {
                return (Record)this._c.newInstance(args);
            }
            catch (final IllegalArgumentException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(e);
            }
            catch (final InvocationTargetException e2) {
                final Throwable t = e2.getTargetException();
                if (t instanceof RecordFormatException) {
                    throw (RecordFormatException)t;
                }
                if (t instanceof EncryptedDocumentException) {
                    throw (EncryptedDocumentException)t;
                }
                throw new RecordFormatException("Unable to construct record instance", t);
            }
        }
        
        @Override
        public Class<? extends Record> getRecordClass() {
            return this._c.getDeclaringClass();
        }
    }
    
    private static final class ReflectionMethodRecordCreator implements I_RecordCreator
    {
        private final Method _m;
        
        public ReflectionMethodRecordCreator(final Method m) {
            this._m = m;
        }
        
        @Override
        public Record create(final RecordInputStream in) {
            final Object[] args = { in };
            try {
                return (Record)this._m.invoke(null, args);
            }
            catch (final IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            catch (final InvocationTargetException e2) {
                throw new RecordFormatException("Unable to construct record instance", e2.getTargetException());
            }
        }
        
        @Override
        public Class<? extends Record> getRecordClass() {
            return (Class<? extends Record>)this._m.getDeclaringClass();
        }
    }
    
    private interface I_RecordCreator
    {
        Record create(final RecordInputStream p0);
        
        Class<? extends Record> getRecordClass();
    }
}
