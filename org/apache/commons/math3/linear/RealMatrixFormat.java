package org.apache.commons.math3.linear;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.exception.MathParseException;
import java.text.ParsePosition;
import java.text.FieldPosition;
import java.util.Locale;
import org.apache.commons.math3.util.CompositeFormat;
import java.text.NumberFormat;

public class RealMatrixFormat
{
    private static final String DEFAULT_PREFIX = "{";
    private static final String DEFAULT_SUFFIX = "}";
    private static final String DEFAULT_ROW_PREFIX = "{";
    private static final String DEFAULT_ROW_SUFFIX = "}";
    private static final String DEFAULT_ROW_SEPARATOR = ",";
    private static final String DEFAULT_COLUMN_SEPARATOR = ",";
    private final String prefix;
    private final String suffix;
    private final String rowPrefix;
    private final String rowSuffix;
    private final String rowSeparator;
    private final String columnSeparator;
    private final NumberFormat format;
    
    public RealMatrixFormat() {
        this("{", "}", "{", "}", ",", ",", CompositeFormat.getDefaultNumberFormat());
    }
    
    public RealMatrixFormat(final NumberFormat format) {
        this("{", "}", "{", "}", ",", ",", format);
    }
    
    public RealMatrixFormat(final String prefix, final String suffix, final String rowPrefix, final String rowSuffix, final String rowSeparator, final String columnSeparator) {
        this(prefix, suffix, rowPrefix, rowSuffix, rowSeparator, columnSeparator, CompositeFormat.getDefaultNumberFormat());
    }
    
    public RealMatrixFormat(final String prefix, final String suffix, final String rowPrefix, final String rowSuffix, final String rowSeparator, final String columnSeparator, final NumberFormat format) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.rowPrefix = rowPrefix;
        this.rowSuffix = rowSuffix;
        this.rowSeparator = rowSeparator;
        this.columnSeparator = columnSeparator;
        (this.format = format).setGroupingUsed(false);
    }
    
    public static Locale[] getAvailableLocales() {
        return NumberFormat.getAvailableLocales();
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    public String getRowPrefix() {
        return this.rowPrefix;
    }
    
    public String getRowSuffix() {
        return this.rowSuffix;
    }
    
    public String getRowSeparator() {
        return this.rowSeparator;
    }
    
    public String getColumnSeparator() {
        return this.columnSeparator;
    }
    
    public NumberFormat getFormat() {
        return this.format;
    }
    
    public static RealMatrixFormat getInstance() {
        return getInstance(Locale.getDefault());
    }
    
    public static RealMatrixFormat getInstance(final Locale locale) {
        return new RealMatrixFormat(CompositeFormat.getDefaultNumberFormat(locale));
    }
    
    public String format(final RealMatrix m) {
        return this.format(m, new StringBuffer(), new FieldPosition(0)).toString();
    }
    
    public StringBuffer format(final RealMatrix matrix, final StringBuffer toAppendTo, final FieldPosition pos) {
        pos.setBeginIndex(0);
        pos.setEndIndex(0);
        toAppendTo.append(this.prefix);
        for (int rows = matrix.getRowDimension(), i = 0; i < rows; ++i) {
            toAppendTo.append(this.rowPrefix);
            for (int j = 0; j < matrix.getColumnDimension(); ++j) {
                if (j > 0) {
                    toAppendTo.append(this.columnSeparator);
                }
                CompositeFormat.formatDouble(matrix.getEntry(i, j), this.format, toAppendTo, pos);
            }
            toAppendTo.append(this.rowSuffix);
            if (i < rows - 1) {
                toAppendTo.append(this.rowSeparator);
            }
        }
        toAppendTo.append(this.suffix);
        return toAppendTo;
    }
    
    public RealMatrix parse(final String source) {
        final ParsePosition parsePosition = new ParsePosition(0);
        final RealMatrix result = this.parse(source, parsePosition);
        if (parsePosition.getIndex() == 0) {
            throw new MathParseException(source, parsePosition.getErrorIndex(), Array2DRowRealMatrix.class);
        }
        return result;
    }
    
    public RealMatrix parse(final String source, final ParsePosition pos) {
        final int initialIndex = pos.getIndex();
        final String trimmedPrefix = this.prefix.trim();
        final String trimmedSuffix = this.suffix.trim();
        final String trimmedRowPrefix = this.rowPrefix.trim();
        final String trimmedRowSuffix = this.rowSuffix.trim();
        final String trimmedColumnSeparator = this.columnSeparator.trim();
        final String trimmedRowSeparator = this.rowSeparator.trim();
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, trimmedPrefix, pos)) {
            return null;
        }
        final List<List<Number>> matrix = new ArrayList<List<Number>>();
        List<Number> rowComponents = new ArrayList<Number>();
        boolean loop = true;
        while (loop) {
            if (!rowComponents.isEmpty()) {
                CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                if (!CompositeFormat.parseFixedstring(source, trimmedColumnSeparator, pos)) {
                    if (trimmedRowSuffix.length() != 0 && !CompositeFormat.parseFixedstring(source, trimmedRowSuffix, pos)) {
                        return null;
                    }
                    CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                    if (CompositeFormat.parseFixedstring(source, trimmedRowSeparator, pos)) {
                        matrix.add(rowComponents);
                        rowComponents = new ArrayList<Number>();
                        continue;
                    }
                    loop = false;
                }
            }
            else {
                CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                if (trimmedRowPrefix.length() != 0 && !CompositeFormat.parseFixedstring(source, trimmedRowPrefix, pos)) {
                    return null;
                }
            }
            if (loop) {
                CompositeFormat.parseAndIgnoreWhitespace(source, pos);
                final Number component = CompositeFormat.parseNumber(source, this.format, pos);
                if (component != null) {
                    rowComponents.add(component);
                }
                else {
                    if (!rowComponents.isEmpty()) {
                        pos.setIndex(initialIndex);
                        return null;
                    }
                    loop = false;
                }
            }
        }
        if (!rowComponents.isEmpty()) {
            matrix.add(rowComponents);
        }
        CompositeFormat.parseAndIgnoreWhitespace(source, pos);
        if (!CompositeFormat.parseFixedstring(source, trimmedSuffix, pos)) {
            return null;
        }
        if (matrix.isEmpty()) {
            pos.setIndex(initialIndex);
            return null;
        }
        final double[][] data = new double[matrix.size()][];
        int row = 0;
        for (final List<Number> rowList : matrix) {
            data[row] = new double[rowList.size()];
            for (int i = 0; i < rowList.size(); ++i) {
                data[row][i] = rowList.get(i).doubleValue();
            }
            ++row;
        }
        return MatrixUtils.createRealMatrix(data);
    }
}
