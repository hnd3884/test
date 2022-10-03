package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.ss.usermodel.CellStyle;
import java.util.Iterator;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.hssf.record.common.UnicodeString;
import java.util.HashSet;
import org.apache.poi.hssf.record.FontRecord;

public class HSSFOptimiser
{
    public static void optimiseFonts(final HSSFWorkbook workbook) {
        final short[] newPos = new short[workbook.getWorkbook().getNumberOfFontRecords() + 1];
        final boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; ++i) {
            newPos[i] = (short)i;
            zapRecords[i] = false;
        }
        final FontRecord[] frecs = new FontRecord[newPos.length];
        for (int j = 0; j < newPos.length; ++j) {
            if (j != 4) {
                frecs[j] = workbook.getWorkbook().getFontRecordAt(j);
            }
        }
        for (int j = 5; j < newPos.length; ++j) {
            int earlierDuplicate = -1;
            for (int k = 0; k < j && earlierDuplicate == -1; ++k) {
                if (k != 4) {
                    final FontRecord frCheck = workbook.getWorkbook().getFontRecordAt(k);
                    if (frCheck.sameProperties(frecs[j])) {
                        earlierDuplicate = k;
                    }
                }
            }
            if (earlierDuplicate != -1) {
                newPos[j] = (short)earlierDuplicate;
                zapRecords[j] = true;
            }
        }
        for (int j = 5; j < newPos.length; ++j) {
            short newPosition;
            final short preDeletePos = newPosition = newPos[j];
            for (int l = 0; l < preDeletePos; ++l) {
                if (zapRecords[l]) {
                    --newPosition;
                }
            }
            newPos[j] = newPosition;
        }
        for (int j = 5; j < newPos.length; ++j) {
            if (zapRecords[j]) {
                workbook.getWorkbook().removeFontRecord(frecs[j]);
            }
        }
        workbook.resetFontCache();
        for (int j = 0; j < workbook.getWorkbook().getNumExFormats(); ++j) {
            final ExtendedFormatRecord xfr = workbook.getWorkbook().getExFormatAt(j);
            xfr.setFontIndex(newPos[xfr.getFontIndex()]);
        }
        final HashSet<UnicodeString> doneUnicodeStrings = new HashSet<UnicodeString>();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); ++sheetNum) {
            final HSSFSheet s = workbook.getSheetAt(sheetNum);
            for (final Row row : s) {
                for (final Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING) {
                        final HSSFRichTextString rtr = (HSSFRichTextString)cell.getRichStringCellValue();
                        final UnicodeString u = rtr.getRawUnicodeString();
                        if (doneUnicodeStrings.contains(u)) {
                            continue;
                        }
                        for (short m = 5; m < newPos.length; ++m) {
                            if (m != newPos[m]) {
                                u.swapFontUse(m, newPos[m]);
                            }
                        }
                        doneUnicodeStrings.add(u);
                    }
                }
            }
        }
    }
    
    public static void optimiseCellStyles(final HSSFWorkbook workbook) {
        final short[] newPos = new short[workbook.getWorkbook().getNumExFormats()];
        final boolean[] isUsed = new boolean[newPos.length];
        final boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; ++i) {
            isUsed[i] = false;
            newPos[i] = (short)i;
            zapRecords[i] = false;
        }
        final ExtendedFormatRecord[] xfrs = new ExtendedFormatRecord[newPos.length];
        for (int j = 0; j < newPos.length; ++j) {
            xfrs[j] = workbook.getWorkbook().getExFormatAt(j);
        }
        for (int j = 21; j < newPos.length; ++j) {
            int earlierDuplicate = -1;
            for (int k = 0; k < j && earlierDuplicate == -1; ++k) {
                final ExtendedFormatRecord xfCheck = workbook.getWorkbook().getExFormatAt(k);
                if (xfCheck.equals(xfrs[j]) && !isUserDefined(workbook, k)) {
                    earlierDuplicate = k;
                }
            }
            if (earlierDuplicate != -1) {
                newPos[j] = (short)earlierDuplicate;
                zapRecords[j] = true;
            }
        }
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); ++sheetNum) {
            final HSSFSheet s = workbook.getSheetAt(sheetNum);
            for (final Row row : s) {
                for (final Cell cellI : row) {
                    final HSSFCell cell = (HSSFCell)cellI;
                    final short oldXf = cell.getCellValueRecord().getXFIndex();
                    if (oldXf < newPos.length) {
                        isUsed[oldXf] = true;
                    }
                }
                final short oldXf2 = ((HSSFRow)row).getRowRecord().getXFIndex();
                if (oldXf2 < newPos.length) {
                    isUsed[oldXf2] = true;
                }
            }
            for (int col = s.getSheet().getMinColumnIndex(); col <= s.getSheet().getMaxColumnIndex(); ++col) {
                final short oldXf3 = s.getSheet().getXFIndexForColAt((short)col);
                if (oldXf3 < newPos.length) {
                    isUsed[oldXf3] = true;
                }
            }
        }
        for (int j = 21; j < isUsed.length; ++j) {
            if (isUserDefined(workbook, j)) {
                isUsed[j] = true;
            }
            if (newPos[j] != j && isUsed[j]) {
                isUsed[newPos[j]] = true;
            }
        }
        for (int j = 21; j < isUsed.length; ++j) {
            if (!isUsed[j]) {
                zapRecords[j] = true;
                newPos[j] = 0;
            }
        }
        for (int j = 21; j < newPos.length; ++j) {
            short newPosition;
            final short preDeletePos = newPosition = newPos[j];
            for (int l = 0; l < preDeletePos; ++l) {
                if (zapRecords[l]) {
                    --newPosition;
                }
            }
            newPos[j] = newPosition;
            if (j != newPosition && newPosition != 0) {
                workbook.getWorkbook().updateStyleRecord(j, newPosition);
                final ExtendedFormatRecord exFormat = workbook.getWorkbook().getExFormatAt(j);
                final short oldParent = exFormat.getParentIndex();
                if (oldParent < newPos.length) {
                    final short newParent = newPos[oldParent];
                    exFormat.setParentIndex(newParent);
                }
            }
        }
        int max = newPos.length;
        int removed = 0;
        for (int m = 21; m < max; ++m) {
            if (zapRecords[m + removed]) {
                workbook.getWorkbook().removeExFormatRecord(m);
                --m;
                --max;
                ++removed;
            }
        }
        for (int sheetNum2 = 0; sheetNum2 < workbook.getNumberOfSheets(); ++sheetNum2) {
            final HSSFSheet s2 = workbook.getSheetAt(sheetNum2);
            for (final Row row2 : s2) {
                for (final Cell cell2 : row2) {
                    final short oldXf4 = ((HSSFCell)cell2).getCellValueRecord().getXFIndex();
                    if (oldXf4 >= newPos.length) {
                        continue;
                    }
                    final HSSFCellStyle newStyle = workbook.getCellStyleAt(newPos[oldXf4]);
                    cell2.setCellStyle(newStyle);
                }
                final short oldXf5 = ((HSSFRow)row2).getRowRecord().getXFIndex();
                if (oldXf5 >= newPos.length) {
                    continue;
                }
                final HSSFCellStyle newStyle2 = workbook.getCellStyleAt(newPos[oldXf5]);
                row2.setRowStyle(newStyle2);
            }
            for (int col2 = s2.getSheet().getMinColumnIndex(); col2 <= s2.getSheet().getMaxColumnIndex(); ++col2) {
                final short oldXf6 = s2.getSheet().getXFIndexForColAt((short)col2);
                if (oldXf6 < newPos.length) {
                    final HSSFCellStyle newStyle3 = workbook.getCellStyleAt(newPos[oldXf6]);
                    s2.setDefaultColumnStyle(col2, newStyle3);
                }
            }
        }
    }
    
    private static boolean isUserDefined(final HSSFWorkbook workbook, final int index) {
        final StyleRecord styleRecord = workbook.getWorkbook().getStyleRecord(index);
        return styleRecord != null && !styleRecord.isBuiltin() && styleRecord.getName() != null;
    }
}
