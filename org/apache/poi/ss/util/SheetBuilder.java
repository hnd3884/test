package org.apache.poi.ss.util;

import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class SheetBuilder
{
    private final Workbook workbook;
    private final Object[][] cells;
    private boolean shouldCreateEmptyCells;
    private String sheetName;
    
    public SheetBuilder(final Workbook workbook, final Object[][] cells) {
        this.workbook = workbook;
        this.cells = cells.clone();
    }
    
    public boolean getCreateEmptyCells() {
        return this.shouldCreateEmptyCells;
    }
    
    public SheetBuilder setCreateEmptyCells(final boolean shouldCreateEmptyCells) {
        this.shouldCreateEmptyCells = shouldCreateEmptyCells;
        return this;
    }
    
    public SheetBuilder setSheetName(final String sheetName) {
        this.sheetName = sheetName;
        return this;
    }
    
    public Sheet build() {
        final Sheet sheet = (this.sheetName == null) ? this.workbook.createSheet() : this.workbook.createSheet(this.sheetName);
        for (int rowIndex = 0; rowIndex < this.cells.length; ++rowIndex) {
            final Object[] rowArray = this.cells[rowIndex];
            final Row currentRow = sheet.createRow(rowIndex);
            for (int cellIndex = 0; cellIndex < rowArray.length; ++cellIndex) {
                final Object cellValue = rowArray[cellIndex];
                if (cellValue != null || this.shouldCreateEmptyCells) {
                    final Cell currentCell = currentRow.createCell(cellIndex);
                    this.setCellValue(currentCell, cellValue);
                }
            }
        }
        return sheet;
    }
    
    private void setCellValue(final Cell cell, final Object value) {
        if (value == null || cell == null) {
            return;
        }
        if (value instanceof Number) {
            final double doubleValue = ((Number)value).doubleValue();
            cell.setCellValue(doubleValue);
        }
        else if (value instanceof Date) {
            cell.setCellValue((Date)value);
        }
        else if (value instanceof Calendar) {
            cell.setCellValue((Calendar)value);
        }
        else if (this.isFormulaDefinition(value)) {
            cell.setCellFormula(this.getFormula(value));
        }
        else {
            cell.setCellValue(value.toString());
        }
    }
    
    private boolean isFormulaDefinition(final Object obj) {
        if (obj instanceof String) {
            final String str = (String)obj;
            return str.length() >= 2 && str.charAt(0) == '=';
        }
        return false;
    }
    
    private String getFormula(final Object obj) {
        return ((String)obj).substring(1);
    }
}
