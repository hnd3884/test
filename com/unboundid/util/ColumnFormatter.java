package com.unboundid.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.io.Serializable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class ColumnFormatter implements Serializable
{
    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS;
    private static final OutputFormat DEFAULT_OUTPUT_FORMAT;
    private static final String DEFAULT_SPACER = " ";
    private static final String DEFAULT_TIMESTAMP_FORMAT = "HH:mm:ss";
    private static final long serialVersionUID = -2524398424293401200L;
    private final boolean includeTimestamp;
    private final FormattableColumn timestampColumn;
    private final FormattableColumn[] columns;
    private final OutputFormat outputFormat;
    private final String spacer;
    private final String timestampFormat;
    private final transient ThreadLocal<DecimalFormat> decimalFormatter;
    private final transient ThreadLocal<SimpleDateFormat> timestampFormatter;
    
    public ColumnFormatter(final FormattableColumn... columns) {
        this(false, null, null, null, columns);
    }
    
    public ColumnFormatter(final boolean includeTimestamp, final String timestampFormat, final OutputFormat outputFormat, final String spacer, final FormattableColumn... columns) {
        Validator.ensureNotNull(columns);
        Validator.ensureTrue(columns.length > 0);
        this.includeTimestamp = includeTimestamp;
        this.columns = columns;
        this.decimalFormatter = new ThreadLocal<DecimalFormat>();
        this.timestampFormatter = new ThreadLocal<SimpleDateFormat>();
        if (timestampFormat == null) {
            this.timestampFormat = "HH:mm:ss";
        }
        else {
            this.timestampFormat = timestampFormat;
        }
        if (outputFormat == null) {
            this.outputFormat = ColumnFormatter.DEFAULT_OUTPUT_FORMAT;
        }
        else {
            this.outputFormat = outputFormat;
        }
        if (spacer == null) {
            this.spacer = " ";
        }
        else {
            this.spacer = spacer;
        }
        if (includeTimestamp) {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(this.timestampFormat);
            final String timestamp = dateFormat.format(new Date());
            final String label = UtilityMessages.INFO_COLUMN_LABEL_TIMESTAMP.get();
            final int width = Math.max(label.length(), timestamp.length());
            this.timestampFormatter.set(dateFormat);
            this.timestampColumn = new FormattableColumn(width, HorizontalAlignment.LEFT, new String[] { label });
        }
        else {
            this.timestampColumn = null;
        }
    }
    
    public boolean includeTimestamps() {
        return this.includeTimestamp;
    }
    
    public String getTimestampFormatString() {
        return this.timestampFormat;
    }
    
    public OutputFormat getOutputFormat() {
        return this.outputFormat;
    }
    
    public String getSpacer() {
        return this.spacer;
    }
    
    public FormattableColumn[] getColumns() {
        final FormattableColumn[] copy = new FormattableColumn[this.columns.length];
        System.arraycopy(this.columns, 0, copy, 0, this.columns.length);
        return copy;
    }
    
    public String[] getHeaderLines(final boolean includeDashes) {
        if (this.outputFormat == OutputFormat.COLUMNS) {
            int maxColumns = 1;
            final String[][] headerLines = new String[this.columns.length][];
            for (int i = 0; i < this.columns.length; ++i) {
                headerLines[i] = this.columns[i].getLabelLines();
                maxColumns = Math.max(maxColumns, headerLines[i].length);
            }
            final StringBuilder[] buffers = new StringBuilder[maxColumns];
            for (int j = 0; j < maxColumns; ++j) {
                final StringBuilder buffer = new StringBuilder();
                buffers[j] = buffer;
                if (this.includeTimestamp) {
                    if (j == maxColumns - 1) {
                        this.timestampColumn.format(buffer, this.timestampColumn.getSingleLabelLine(), this.outputFormat);
                    }
                    else {
                        this.timestampColumn.format(buffer, "", this.outputFormat);
                    }
                }
                for (int k = 0; k < this.columns.length; ++k) {
                    if (this.includeTimestamp || k > 0) {
                        buffer.append(this.spacer);
                    }
                    final int rowNumber = j + headerLines[k].length - maxColumns;
                    if (rowNumber < 0) {
                        this.columns[k].format(buffer, "", this.outputFormat);
                    }
                    else {
                        this.columns[k].format(buffer, headerLines[k][rowNumber], this.outputFormat);
                    }
                }
            }
            String[] returnArray;
            if (includeDashes) {
                returnArray = new String[maxColumns + 1];
            }
            else {
                returnArray = new String[maxColumns];
            }
            for (int l = 0; l < maxColumns; ++l) {
                returnArray[l] = buffers[l].toString();
            }
            if (includeDashes) {
                final StringBuilder buffer = new StringBuilder();
                if (this.timestampColumn != null) {
                    for (int m = 0; m < this.timestampColumn.getWidth(); ++m) {
                        buffer.append('-');
                    }
                }
                for (int m = 0; m < this.columns.length; ++m) {
                    if (this.includeTimestamp || m > 0) {
                        buffer.append(this.spacer);
                    }
                    for (int j2 = 0; j2 < this.columns[m].getWidth(); ++j2) {
                        buffer.append('-');
                    }
                }
                returnArray[returnArray.length - 1] = buffer.toString();
            }
            return returnArray;
        }
        final StringBuilder buffer2 = new StringBuilder();
        if (this.timestampColumn != null) {
            this.timestampColumn.format(buffer2, this.timestampColumn.getSingleLabelLine(), this.outputFormat);
        }
        for (int i2 = 0; i2 < this.columns.length; ++i2) {
            if (this.includeTimestamp || i2 > 0) {
                if (this.outputFormat == OutputFormat.TAB_DELIMITED_TEXT) {
                    buffer2.append('\t');
                }
                else if (this.outputFormat == OutputFormat.CSV) {
                    buffer2.append(',');
                }
            }
            final FormattableColumn c = this.columns[i2];
            c.format(buffer2, c.getSingleLabelLine(), this.outputFormat);
        }
        return new String[] { buffer2.toString() };
    }
    
    public String formatRow(final Object... columnData) {
        final StringBuilder buffer = new StringBuilder();
        if (this.includeTimestamp) {
            SimpleDateFormat dateFormat = this.timestampFormatter.get();
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat(this.timestampFormat);
                this.timestampFormatter.set(dateFormat);
            }
            this.timestampColumn.format(buffer, dateFormat.format(new Date()), this.outputFormat);
        }
        for (int i = 0; i < this.columns.length; ++i) {
            if (this.includeTimestamp || i > 0) {
                switch (this.outputFormat) {
                    case TAB_DELIMITED_TEXT: {
                        buffer.append('\t');
                        break;
                    }
                    case CSV: {
                        buffer.append(',');
                        break;
                    }
                    case COLUMNS: {
                        buffer.append(this.spacer);
                        break;
                    }
                }
            }
            if (i >= columnData.length) {
                this.columns[i].format(buffer, "", this.outputFormat);
            }
            else {
                this.columns[i].format(buffer, this.toString(columnData[i]), this.outputFormat);
            }
        }
        return buffer.toString();
    }
    
    private String toString(final Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof Float || o instanceof Double) {
            DecimalFormat f = this.decimalFormatter.get();
            if (f == null) {
                f = new DecimalFormat("0.000", ColumnFormatter.DECIMAL_FORMAT_SYMBOLS);
                this.decimalFormatter.set(f);
            }
            double d;
            if (o instanceof Float) {
                d = (double)o;
            }
            else {
                d = (double)o;
            }
            return f.format(d);
        }
        return String.valueOf(o);
    }
    
    static {
        (DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols()).setInfinity("inf");
        ColumnFormatter.DECIMAL_FORMAT_SYMBOLS.setNaN("NaN");
        DEFAULT_OUTPUT_FORMAT = OutputFormat.COLUMNS;
    }
}
