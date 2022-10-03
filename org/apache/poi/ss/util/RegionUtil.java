package org.apache.poi.ss.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;

public final class RegionUtil
{
    private RegionUtil() {
    }
    
    public static void setBorderLeft(final BorderStyle border, final CellRangeAddress region, final Sheet sheet) {
        final int rowStart = region.getFirstRow();
        final int rowEnd = region.getLastRow();
        final int column = region.getFirstColumn();
        final CellPropertySetter cps = new CellPropertySetter("borderLeft", border);
        for (int i = rowStart; i <= rowEnd; ++i) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }
    
    public static void setLeftBorderColor(final int color, final CellRangeAddress region, final Sheet sheet) {
        final int rowStart = region.getFirstRow();
        final int rowEnd = region.getLastRow();
        final int column = region.getFirstColumn();
        final CellPropertySetter cps = new CellPropertySetter("leftBorderColor", color);
        for (int i = rowStart; i <= rowEnd; ++i) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }
    
    public static void setBorderRight(final BorderStyle border, final CellRangeAddress region, final Sheet sheet) {
        final int rowStart = region.getFirstRow();
        final int rowEnd = region.getLastRow();
        final int column = region.getLastColumn();
        final CellPropertySetter cps = new CellPropertySetter("borderRight", border);
        for (int i = rowStart; i <= rowEnd; ++i) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }
    
    public static void setRightBorderColor(final int color, final CellRangeAddress region, final Sheet sheet) {
        final int rowStart = region.getFirstRow();
        final int rowEnd = region.getLastRow();
        final int column = region.getLastColumn();
        final CellPropertySetter cps = new CellPropertySetter("rightBorderColor", color);
        for (int i = rowStart; i <= rowEnd; ++i) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }
    
    public static void setBorderBottom(final BorderStyle border, final CellRangeAddress region, final Sheet sheet) {
        final int colStart = region.getFirstColumn();
        final int colEnd = region.getLastColumn();
        final int rowIndex = region.getLastRow();
        final CellPropertySetter cps = new CellPropertySetter("borderBottom", border);
        final Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; ++i) {
            cps.setProperty(row, i);
        }
    }
    
    public static void setBottomBorderColor(final int color, final CellRangeAddress region, final Sheet sheet) {
        final int colStart = region.getFirstColumn();
        final int colEnd = region.getLastColumn();
        final int rowIndex = region.getLastRow();
        final CellPropertySetter cps = new CellPropertySetter("bottomBorderColor", color);
        final Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; ++i) {
            cps.setProperty(row, i);
        }
    }
    
    public static void setBorderTop(final BorderStyle border, final CellRangeAddress region, final Sheet sheet) {
        final int colStart = region.getFirstColumn();
        final int colEnd = region.getLastColumn();
        final int rowIndex = region.getFirstRow();
        final CellPropertySetter cps = new CellPropertySetter("borderTop", border);
        final Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; ++i) {
            cps.setProperty(row, i);
        }
    }
    
    public static void setTopBorderColor(final int color, final CellRangeAddress region, final Sheet sheet) {
        final int colStart = region.getFirstColumn();
        final int colEnd = region.getLastColumn();
        final int rowIndex = region.getFirstRow();
        final CellPropertySetter cps = new CellPropertySetter("topBorderColor", color);
        final Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; ++i) {
            cps.setProperty(row, i);
        }
    }
    
    private static final class CellPropertySetter
    {
        private final String _propertyName;
        private final Object _propertyValue;
        
        public CellPropertySetter(final String propertyName, final int value) {
            this._propertyName = propertyName;
            this._propertyValue = value;
        }
        
        public CellPropertySetter(final String propertyName, final BorderStyle value) {
            this._propertyName = propertyName;
            this._propertyValue = value;
        }
        
        public void setProperty(final Row row, final int column) {
            final Cell cell = CellUtil.getCell(row, column);
            CellUtil.setCellStyleProperty(cell, this._propertyName, this._propertyValue);
        }
    }
}
