package org.apache.poi.ss.util;

import org.apache.poi.util.Removal;
import java.util.Map;
import org.apache.poi.ss.usermodel.CellValue;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import org.apache.poi.util.Internal;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.font.TextLayout;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.CellStyle;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.Locale;
import java.text.AttributedString;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Cell;
import java.awt.font.FontRenderContext;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class SheetUtil
{
    private static final char defaultChar = '0';
    private static final double fontHeightMultiple = 2.0;
    private static final FormulaEvaluator dummyEvaluator;
    private static final FontRenderContext fontRenderContext;
    
    public static double getCellWidth(Cell cell, final int defaultCharWidth, final DataFormatter formatter, final boolean useMergedCells) {
        final Sheet sheet = cell.getSheet();
        final Workbook wb = sheet.getWorkbook();
        final Row row = cell.getRow();
        final int column = cell.getColumnIndex();
        int colspan = 1;
        for (final CellRangeAddress region : sheet.getMergedRegions()) {
            if (region.isInRange(row.getRowNum(), column)) {
                if (!useMergedCells) {
                    return -1.0;
                }
                cell = row.getCell(region.getFirstColumn());
                colspan = 1 + region.getLastColumn() - region.getFirstColumn();
            }
        }
        final CellStyle style = cell.getCellStyle();
        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }
        final Font font = wb.getFontAt(style.getFontIndexAsInt());
        double width = -1.0;
        if (cellType == CellType.STRING) {
            final RichTextString rt = cell.getRichStringCellValue();
            final String[] split;
            final String[] lines = split = rt.getString().split("\\n");
            for (final String line : split) {
                final String txt = line + '0';
                final AttributedString str = new AttributedString(txt);
                copyAttributes(font, str, 0, txt.length());
                width = getCellWidth(defaultCharWidth, colspan, style, width, str);
            }
        }
        else {
            String sval = null;
            if (cellType == CellType.NUMERIC) {
                try {
                    sval = formatter.formatCellValue(cell, SheetUtil.dummyEvaluator);
                }
                catch (final Exception e) {
                    sval = String.valueOf(cell.getNumericCellValue());
                }
            }
            else if (cellType == CellType.BOOLEAN) {
                sval = String.valueOf(cell.getBooleanCellValue()).toUpperCase(Locale.ROOT);
            }
            if (sval != null) {
                final String txt2 = sval + '0';
                final AttributedString str2 = new AttributedString(txt2);
                copyAttributes(font, str2, 0, txt2.length());
                width = getCellWidth(defaultCharWidth, colspan, style, width, str2);
            }
        }
        return width;
    }
    
    private static double getCellWidth(final int defaultCharWidth, final int colspan, final CellStyle style, final double minWidth, final AttributedString str) {
        final TextLayout layout = new TextLayout(str.getIterator(), SheetUtil.fontRenderContext);
        Rectangle2D bounds;
        if (style.getRotation() != 0) {
            final AffineTransform trans = new AffineTransform();
            trans.concatenate(AffineTransform.getRotateInstance(style.getRotation() * 2.0 * 3.141592653589793 / 360.0));
            trans.concatenate(AffineTransform.getScaleInstance(1.0, 2.0));
            bounds = layout.getOutline(trans).getBounds();
        }
        else {
            bounds = layout.getBounds();
        }
        final double frameWidth = bounds.getX() + bounds.getWidth();
        return Math.max(minWidth, frameWidth / colspan / defaultCharWidth + style.getIndention());
    }
    
    public static double getColumnWidth(final Sheet sheet, final int column, final boolean useMergedCells) {
        return getColumnWidth(sheet, column, useMergedCells, sheet.getFirstRowNum(), sheet.getLastRowNum());
    }
    
    public static double getColumnWidth(final Sheet sheet, final int column, final boolean useMergedCells, final int firstRow, final int lastRow) {
        final DataFormatter formatter = new DataFormatter();
        final int defaultCharWidth = getDefaultCharWidth(sheet.getWorkbook());
        double width = -1.0;
        for (int rowIdx = firstRow; rowIdx <= lastRow; ++rowIdx) {
            final Row row = sheet.getRow(rowIdx);
            if (row != null) {
                final double cellWidth = getColumnWidthForRow(row, column, defaultCharWidth, formatter, useMergedCells);
                width = Math.max(width, cellWidth);
            }
        }
        return width;
    }
    
    @Internal
    public static int getDefaultCharWidth(final Workbook wb) {
        final Font defaultFont = wb.getFontAt(0);
        final AttributedString str = new AttributedString(String.valueOf('0'));
        copyAttributes(defaultFont, str, 0, 1);
        final TextLayout layout = new TextLayout(str.getIterator(), SheetUtil.fontRenderContext);
        return (int)layout.getAdvance();
    }
    
    private static double getColumnWidthForRow(final Row row, final int column, final int defaultCharWidth, final DataFormatter formatter, final boolean useMergedCells) {
        if (row == null) {
            return -1.0;
        }
        final Cell cell = row.getCell(column);
        if (cell == null) {
            return -1.0;
        }
        return getCellWidth(cell, defaultCharWidth, formatter, useMergedCells);
    }
    
    public static boolean canComputeColumnWidth(final Font font) {
        final AttributedString str = new AttributedString("1w");
        copyAttributes(font, str, 0, "1w".length());
        final TextLayout layout = new TextLayout(str.getIterator(), SheetUtil.fontRenderContext);
        return layout.getBounds().getWidth() > 0.0;
    }
    
    private static void copyAttributes(final Font font, final AttributedString str, final int startIdx, final int endIdx) {
        str.addAttribute(TextAttribute.FAMILY, font.getFontName(), startIdx, endIdx);
        str.addAttribute(TextAttribute.SIZE, (float)font.getFontHeightInPoints());
        if (font.getBold()) {
            str.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, startIdx, endIdx);
        }
        if (font.getItalic()) {
            str.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, startIdx, endIdx);
        }
        if (font.getUnderline() == 1) {
            str.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, startIdx, endIdx);
        }
    }
    
    public static Cell getCell(final Sheet sheet, final int rowIx, final int colIx) {
        final Row r = sheet.getRow(rowIx);
        if (r != null) {
            return r.getCell(colIx);
        }
        return null;
    }
    
    public static Cell getCellWithMerges(final Sheet sheet, final int rowIx, final int colIx) {
        final Cell c = getCell(sheet, rowIx, colIx);
        if (c != null) {
            return c;
        }
        for (final CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
            if (mergedRegion.isInRange(rowIx, colIx)) {
                final Row r = sheet.getRow(mergedRegion.getFirstRow());
                if (r != null) {
                    return r.getCell(mergedRegion.getFirstColumn());
                }
                continue;
            }
        }
        return null;
    }
    
    static {
        dummyEvaluator = new FormulaEvaluator() {
            @Override
            public void clearAllCachedResultValues() {
            }
            
            @Override
            public void notifySetFormula(final Cell cell) {
            }
            
            @Override
            public void notifyDeleteCell(final Cell cell) {
            }
            
            @Override
            public void notifyUpdateCell(final Cell cell) {
            }
            
            @Override
            public CellValue evaluate(final Cell cell) {
                return null;
            }
            
            @Override
            public Cell evaluateInCell(final Cell cell) {
                return null;
            }
            
            @Override
            public void setupReferencedWorkbooks(final Map<String, FormulaEvaluator> workbooks) {
            }
            
            @Override
            public void setDebugEvaluationOutputForNextEval(final boolean value) {
            }
            
            @Override
            public void setIgnoreMissingWorkbooks(final boolean ignore) {
            }
            
            @Override
            public void evaluateAll() {
            }
            
            @Override
            public CellType evaluateFormulaCell(final Cell cell) {
                return cell.getCachedFormulaResultType();
            }
            
            @Deprecated
            @Removal(version = "4.2")
            @Internal(since = "POI 3.15 beta 3")
            @Override
            public CellType evaluateFormulaCellEnum(final Cell cell) {
                return this.evaluateFormulaCell(cell);
            }
        };
        fontRenderContext = new FontRenderContext(null, true, true);
    }
}
