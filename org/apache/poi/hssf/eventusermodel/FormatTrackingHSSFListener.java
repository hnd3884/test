package org.apache.poi.hssf.eventusermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.Record;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import java.util.List;
import org.apache.poi.hssf.record.FormatRecord;
import java.util.Map;
import java.text.NumberFormat;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.util.POILogger;

public class FormatTrackingHSSFListener implements HSSFListener
{
    private static final POILogger logger;
    private final HSSFListener _childListener;
    private final HSSFDataFormatter _formatter;
    private final NumberFormat _defaultFormat;
    private final Map<Integer, FormatRecord> _customFormatRecords;
    private final List<ExtendedFormatRecord> _xfRecords;
    
    public FormatTrackingHSSFListener(final HSSFListener childListener) {
        this(childListener, LocaleUtil.getUserLocale());
    }
    
    public FormatTrackingHSSFListener(final HSSFListener childListener, final Locale locale) {
        this._customFormatRecords = new HashMap<Integer, FormatRecord>();
        this._xfRecords = new ArrayList<ExtendedFormatRecord>();
        this._childListener = childListener;
        this._formatter = new HSSFDataFormatter(locale);
        this._defaultFormat = NumberFormat.getInstance(locale);
    }
    
    protected int getNumberOfCustomFormats() {
        return this._customFormatRecords.size();
    }
    
    protected int getNumberOfExtendedFormats() {
        return this._xfRecords.size();
    }
    
    @Override
    public void processRecord(final Record record) {
        this.processRecordInternally(record);
        this._childListener.processRecord(record);
    }
    
    public void processRecordInternally(final Record record) {
        if (record instanceof FormatRecord) {
            final FormatRecord fr = (FormatRecord)record;
            this._customFormatRecords.put(fr.getIndexCode(), fr);
        }
        if (record instanceof ExtendedFormatRecord) {
            final ExtendedFormatRecord xr = (ExtendedFormatRecord)record;
            this._xfRecords.add(xr);
        }
    }
    
    public String formatNumberDateCell(final CellValueRecordInterface cell) {
        double value;
        if (cell instanceof NumberRecord) {
            value = ((NumberRecord)cell).getValue();
        }
        else {
            if (!(cell instanceof FormulaRecord)) {
                throw new IllegalArgumentException("Unsupported CellValue Record passed in " + cell);
            }
            value = ((FormulaRecord)cell).getValue();
        }
        final int formatIndex = this.getFormatIndex(cell);
        final String formatString = this.getFormatString(cell);
        if (formatString == null) {
            return this._defaultFormat.format(value);
        }
        return this._formatter.formatRawCellContents(value, formatIndex, formatString);
    }
    
    public String getFormatString(final int formatIndex) {
        String format = null;
        if (formatIndex >= HSSFDataFormat.getNumberOfBuiltinBuiltinFormats()) {
            final FormatRecord tfr = this._customFormatRecords.get(formatIndex);
            if (tfr == null) {
                FormatTrackingHSSFListener.logger.log(7, "Requested format at index " + formatIndex + ", but it wasn't found");
            }
            else {
                format = tfr.getFormatString();
            }
        }
        else {
            format = HSSFDataFormat.getBuiltinFormat((short)formatIndex);
        }
        return format;
    }
    
    public String getFormatString(final CellValueRecordInterface cell) {
        final int formatIndex = this.getFormatIndex(cell);
        if (formatIndex == -1) {
            return null;
        }
        return this.getFormatString(formatIndex);
    }
    
    public int getFormatIndex(final CellValueRecordInterface cell) {
        final ExtendedFormatRecord xfr = this._xfRecords.get(cell.getXFIndex());
        if (xfr == null) {
            FormatTrackingHSSFListener.logger.log(7, "Cell " + cell.getRow() + "," + cell.getColumn() + " uses XF with index " + cell.getXFIndex() + ", but we don't have that");
            return -1;
        }
        return xfr.getFormatIndex();
    }
    
    static {
        logger = POILogFactory.getLogger(FormatTrackingHSSFListener.class);
    }
}
