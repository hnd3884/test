package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.formula.FormulaParseException;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import org.apache.poi.util.Removal;

public interface Cell
{
    int getColumnIndex();
    
    int getRowIndex();
    
    Sheet getSheet();
    
    Row getRow();
    
    @Deprecated
    @Removal(version = "5.0")
    void setCellType(final CellType p0);
    
    void setBlank();
    
    CellType getCellType();
    
    @Deprecated
    @Removal(version = "4.2")
    CellType getCellTypeEnum();
    
    CellType getCachedFormulaResultType();
    
    @Deprecated
    @Removal(version = "4.2")
    CellType getCachedFormulaResultTypeEnum();
    
    void setCellValue(final double p0);
    
    void setCellValue(final Date p0);
    
    void setCellValue(final LocalDateTime p0);
    
    default void setCellValue(final LocalDate value) {
        this.setCellValue((value == null) ? null : value.atStartOfDay());
    }
    
    void setCellValue(final Calendar p0);
    
    void setCellValue(final RichTextString p0);
    
    void setCellValue(final String p0);
    
    void setCellFormula(final String p0) throws FormulaParseException, IllegalStateException;
    
    void removeFormula() throws IllegalStateException;
    
    String getCellFormula();
    
    double getNumericCellValue();
    
    Date getDateCellValue();
    
    LocalDateTime getLocalDateTimeCellValue();
    
    RichTextString getRichStringCellValue();
    
    String getStringCellValue();
    
    void setCellValue(final boolean p0);
    
    void setCellErrorValue(final byte p0);
    
    boolean getBooleanCellValue();
    
    byte getErrorCellValue();
    
    void setCellStyle(final CellStyle p0);
    
    CellStyle getCellStyle();
    
    void setAsActiveCell();
    
    CellAddress getAddress();
    
    void setCellComment(final Comment p0);
    
    Comment getCellComment();
    
    void removeCellComment();
    
    Hyperlink getHyperlink();
    
    void setHyperlink(final Hyperlink p0);
    
    void removeHyperlink();
    
    CellRangeAddress getArrayFormulaRange();
    
    boolean isPartOfArrayFormulaGroup();
}
