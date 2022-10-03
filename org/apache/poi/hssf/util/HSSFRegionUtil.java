package org.apache.poi.hssf.util;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Removal;

@Removal(version = "4.2")
public final class HSSFRegionUtil
{
    private HSSFRegionUtil() {
    }
    
    public static void setBorderLeft(final int border, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBorderLeft(BorderStyle.valueOf((short)border), region, sheet);
    }
    
    public static void setLeftBorderColor(final int color, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setLeftBorderColor(color, region, sheet);
    }
    
    public static void setBorderRight(final int border, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBorderRight(BorderStyle.valueOf((short)border), region, sheet);
    }
    
    public static void setRightBorderColor(final int color, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setRightBorderColor(color, region, sheet);
    }
    
    public static void setBorderBottom(final int border, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBorderBottom(BorderStyle.valueOf((short)border), region, sheet);
    }
    
    public static void setBottomBorderColor(final int color, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBottomBorderColor(color, region, sheet);
    }
    
    public static void setBorderTop(final int border, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBorderTop(BorderStyle.valueOf((short)border), region, sheet);
    }
    
    public static void setTopBorderColor(final int color, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setTopBorderColor(color, region, sheet);
    }
}
