package org.apache.poi.ss.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import org.apache.poi.util.POILogFactory;
import java.util.Locale;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.BorderStyle;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Collections;
import java.util.Map;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import java.util.Set;
import org.apache.poi.util.POILogger;

public final class CellUtil
{
    private static final POILogger log;
    public static final String ALIGNMENT = "alignment";
    public static final String BORDER_BOTTOM = "borderBottom";
    public static final String BORDER_LEFT = "borderLeft";
    public static final String BORDER_RIGHT = "borderRight";
    public static final String BORDER_TOP = "borderTop";
    public static final String BOTTOM_BORDER_COLOR = "bottomBorderColor";
    public static final String LEFT_BORDER_COLOR = "leftBorderColor";
    public static final String RIGHT_BORDER_COLOR = "rightBorderColor";
    public static final String TOP_BORDER_COLOR = "topBorderColor";
    public static final String DATA_FORMAT = "dataFormat";
    public static final String FILL_BACKGROUND_COLOR = "fillBackgroundColor";
    public static final String FILL_FOREGROUND_COLOR = "fillForegroundColor";
    public static final String FILL_PATTERN = "fillPattern";
    public static final String FONT = "font";
    public static final String HIDDEN = "hidden";
    public static final String INDENTION = "indention";
    public static final String LOCKED = "locked";
    public static final String ROTATION = "rotation";
    public static final String VERTICAL_ALIGNMENT = "verticalAlignment";
    public static final String WRAP_TEXT = "wrapText";
    private static final Set<String> shortValues;
    private static final Set<String> intValues;
    private static final Set<String> booleanValues;
    private static final Set<String> borderTypeValues;
    private static UnicodeMapping[] unicodeMappings;
    
    private CellUtil() {
    }
    
    public static Row getRow(final int rowIndex, final Sheet sheet) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }
    
    public static Cell getCell(final Row row, final int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }
    
    public static Cell createCell(final Row row, final int column, final String value, final CellStyle style) {
        final Cell cell = getCell(row, column);
        cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(value));
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }
    
    public static Cell createCell(final Row row, final int column, final String value) {
        return createCell(row, column, value, null);
    }
    
    public static void setAlignment(final Cell cell, final HorizontalAlignment align) {
        setCellStyleProperty(cell, "alignment", align);
    }
    
    public static void setVerticalAlignment(final Cell cell, final VerticalAlignment align) {
        setCellStyleProperty(cell, "verticalAlignment", align);
    }
    
    public static void setFont(final Cell cell, final Font font) {
        final Workbook wb = cell.getSheet().getWorkbook();
        final int fontIndex = font.getIndexAsInt();
        if (!wb.getFontAt(fontIndex).equals(font)) {
            throw new IllegalArgumentException("Font does not belong to this workbook");
        }
        setCellStyleProperty(cell, "font", fontIndex);
    }
    
    public static void setCellStyleProperties(final Cell cell, final Map<String, Object> properties) {
        final Workbook workbook = cell.getSheet().getWorkbook();
        final CellStyle originalStyle = cell.getCellStyle();
        CellStyle newStyle = null;
        final Map<String, Object> values = getFormatProperties(originalStyle);
        putAll(properties, values);
        for (int numberCellStyles = workbook.getNumCellStyles(), i = 0; i < numberCellStyles; ++i) {
            final CellStyle wbStyle = workbook.getCellStyleAt(i);
            final Map<String, Object> wbStyleMap = getFormatProperties(wbStyle);
            if (wbStyleMap.equals(values)) {
                newStyle = wbStyle;
                break;
            }
        }
        if (newStyle == null) {
            newStyle = workbook.createCellStyle();
            setFormatProperties(newStyle, workbook, values);
        }
        cell.setCellStyle(newStyle);
    }
    
    public static void setCellStyleProperty(final Cell cell, final String propertyName, final Object propertyValue) {
        final Map<String, Object> property = Collections.singletonMap(propertyName, propertyValue);
        setCellStyleProperties(cell, property);
    }
    
    private static Map<String, Object> getFormatProperties(final CellStyle style) {
        final Map<String, Object> properties = new HashMap<String, Object>();
        put(properties, "alignment", style.getAlignment());
        put(properties, "verticalAlignment", style.getVerticalAlignment());
        put(properties, "borderBottom", style.getBorderBottom());
        put(properties, "borderLeft", style.getBorderLeft());
        put(properties, "borderRight", style.getBorderRight());
        put(properties, "borderTop", style.getBorderTop());
        put(properties, "bottomBorderColor", style.getBottomBorderColor());
        put(properties, "dataFormat", style.getDataFormat());
        put(properties, "fillPattern", style.getFillPattern());
        put(properties, "fillForegroundColor", style.getFillForegroundColor());
        put(properties, "fillBackgroundColor", style.getFillBackgroundColor());
        put(properties, "font", style.getFontIndexAsInt());
        put(properties, "hidden", style.getHidden());
        put(properties, "indention", style.getIndention());
        put(properties, "leftBorderColor", style.getLeftBorderColor());
        put(properties, "locked", style.getLocked());
        put(properties, "rightBorderColor", style.getRightBorderColor());
        put(properties, "rotation", style.getRotation());
        put(properties, "topBorderColor", style.getTopBorderColor());
        put(properties, "wrapText", style.getWrapText());
        return properties;
    }
    
    private static void putAll(final Map<String, Object> src, final Map<String, Object> dest) {
        for (final String key : src.keySet()) {
            if (CellUtil.shortValues.contains(key)) {
                dest.put(key, getShort(src, key));
            }
            else if (CellUtil.intValues.contains(key)) {
                dest.put(key, getInt(src, key));
            }
            else if (CellUtil.booleanValues.contains(key)) {
                dest.put(key, getBoolean(src, key));
            }
            else if (CellUtil.borderTypeValues.contains(key)) {
                dest.put(key, getBorderStyle(src, key));
            }
            else if ("alignment".equals(key)) {
                dest.put(key, getHorizontalAlignment(src, key));
            }
            else if ("verticalAlignment".equals(key)) {
                dest.put(key, getVerticalAlignment(src, key));
            }
            else if ("fillPattern".equals(key)) {
                dest.put(key, getFillPattern(src, key));
            }
            else {
                if (!CellUtil.log.check(3)) {
                    continue;
                }
                CellUtil.log.log(3, "Ignoring unrecognized CellUtil format properties key: " + key);
            }
        }
    }
    
    private static void setFormatProperties(final CellStyle style, final Workbook workbook, final Map<String, Object> properties) {
        style.setAlignment(getHorizontalAlignment(properties, "alignment"));
        style.setVerticalAlignment(getVerticalAlignment(properties, "verticalAlignment"));
        style.setBorderBottom(getBorderStyle(properties, "borderBottom"));
        style.setBorderLeft(getBorderStyle(properties, "borderLeft"));
        style.setBorderRight(getBorderStyle(properties, "borderRight"));
        style.setBorderTop(getBorderStyle(properties, "borderTop"));
        style.setBottomBorderColor(getShort(properties, "bottomBorderColor"));
        style.setDataFormat(getShort(properties, "dataFormat"));
        style.setFillPattern(getFillPattern(properties, "fillPattern"));
        style.setFillForegroundColor(getShort(properties, "fillForegroundColor"));
        style.setFillBackgroundColor(getShort(properties, "fillBackgroundColor"));
        style.setFont(workbook.getFontAt(getInt(properties, "font")));
        style.setHidden(getBoolean(properties, "hidden"));
        style.setIndention(getShort(properties, "indention"));
        style.setLeftBorderColor(getShort(properties, "leftBorderColor"));
        style.setLocked(getBoolean(properties, "locked"));
        style.setRightBorderColor(getShort(properties, "rightBorderColor"));
        style.setRotation(getShort(properties, "rotation"));
        style.setTopBorderColor(getShort(properties, "topBorderColor"));
        style.setWrapText(getBoolean(properties, "wrapText"));
    }
    
    private static short getShort(final Map<String, Object> properties, final String name) {
        final Object value = properties.get(name);
        if (value instanceof Number) {
            return ((Number)value).shortValue();
        }
        return 0;
    }
    
    private static int getInt(final Map<String, Object> properties, final String name) {
        final Object value = properties.get(name);
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        return 0;
    }
    
    private static BorderStyle getBorderStyle(final Map<String, Object> properties, final String name) {
        final Object value = properties.get(name);
        BorderStyle border;
        if (value instanceof BorderStyle) {
            border = (BorderStyle)value;
        }
        else if (value instanceof Short) {
            if (CellUtil.log.check(5)) {
                CellUtil.log.log(5, "Deprecation warning: CellUtil properties map uses Short values for " + name + ". Should use BorderStyle enums instead.");
            }
            final short code = (short)value;
            border = BorderStyle.valueOf(code);
        }
        else {
            if (value != null) {
                throw new RuntimeException("Unexpected border style class. Must be BorderStyle or Short (deprecated).");
            }
            border = BorderStyle.NONE;
        }
        return border;
    }
    
    private static FillPatternType getFillPattern(final Map<String, Object> properties, final String name) {
        final Object value = properties.get(name);
        FillPatternType pattern;
        if (value instanceof FillPatternType) {
            pattern = (FillPatternType)value;
        }
        else if (value instanceof Short) {
            if (CellUtil.log.check(5)) {
                CellUtil.log.log(5, "Deprecation warning: CellUtil properties map uses Short values for " + name + ". Should use FillPatternType enums instead.");
            }
            final short code = (short)value;
            pattern = FillPatternType.forInt(code);
        }
        else {
            if (value != null) {
                throw new RuntimeException("Unexpected fill pattern style class. Must be FillPatternType or Short (deprecated).");
            }
            pattern = FillPatternType.NO_FILL;
        }
        return pattern;
    }
    
    private static HorizontalAlignment getHorizontalAlignment(final Map<String, Object> properties, final String name) {
        final Object value = properties.get(name);
        HorizontalAlignment align;
        if (value instanceof HorizontalAlignment) {
            align = (HorizontalAlignment)value;
        }
        else if (value instanceof Short) {
            if (CellUtil.log.check(5)) {
                CellUtil.log.log(5, "Deprecation warning: CellUtil properties map used a Short value for " + name + ". Should use HorizontalAlignment enums instead.");
            }
            final short code = (short)value;
            align = HorizontalAlignment.forInt(code);
        }
        else {
            if (value != null) {
                throw new RuntimeException("Unexpected horizontal alignment style class. Must be HorizontalAlignment or Short (deprecated).");
            }
            align = HorizontalAlignment.GENERAL;
        }
        return align;
    }
    
    private static VerticalAlignment getVerticalAlignment(final Map<String, Object> properties, final String name) {
        final Object value = properties.get(name);
        VerticalAlignment align;
        if (value instanceof VerticalAlignment) {
            align = (VerticalAlignment)value;
        }
        else if (value instanceof Short) {
            if (CellUtil.log.check(5)) {
                CellUtil.log.log(5, "Deprecation warning: CellUtil properties map used a Short value for " + name + ". Should use VerticalAlignment enums instead.");
            }
            final short code = (short)value;
            align = VerticalAlignment.forInt(code);
        }
        else {
            if (value != null) {
                throw new RuntimeException("Unexpected vertical alignment style class. Must be VerticalAlignment or Short (deprecated).");
            }
            align = VerticalAlignment.BOTTOM;
        }
        return align;
    }
    
    private static boolean getBoolean(final Map<String, Object> properties, final String name) {
        final Object value = properties.get(name);
        return value instanceof Boolean && (boolean)value;
    }
    
    private static void put(final Map<String, Object> properties, final String name, final Object value) {
        properties.put(name, value);
    }
    
    public static Cell translateUnicodeValues(final Cell cell) {
        String s = cell.getRichStringCellValue().getString();
        boolean foundUnicode = false;
        final String lowerCaseStr = s.toLowerCase(Locale.ROOT);
        for (final UnicodeMapping entry : CellUtil.unicodeMappings) {
            final String key = entry.entityName;
            if (lowerCaseStr.contains(key)) {
                s = s.replaceAll(key, entry.resolvedValue);
                foundUnicode = true;
            }
        }
        if (foundUnicode) {
            cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(s));
        }
        return cell;
    }
    
    private static UnicodeMapping um(final String entityName, final String resolvedValue) {
        return new UnicodeMapping(entityName, resolvedValue);
    }
    
    static {
        log = POILogFactory.getLogger(CellUtil.class);
        shortValues = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("bottomBorderColor", "leftBorderColor", "rightBorderColor", "topBorderColor", "fillForegroundColor", "fillBackgroundColor", "indention", "dataFormat", "rotation")));
        intValues = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("font")));
        booleanValues = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("locked", "hidden", "wrapText")));
        borderTypeValues = Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(Arrays.asList("borderBottom", "borderLeft", "borderRight", "borderTop")));
        CellUtil.unicodeMappings = new UnicodeMapping[] { um("alpha", "\u03b1"), um("beta", "\u03b2"), um("gamma", "\u03b3"), um("delta", "\u03b4"), um("epsilon", "\u03b5"), um("zeta", "\u03b6"), um("eta", "\u03b7"), um("theta", "\u03b8"), um("iota", "\u03b9"), um("kappa", "\u03ba"), um("lambda", "\u03bb"), um("mu", "\u03bc"), um("nu", "\u03bd"), um("xi", "\u03be"), um("omicron", "\u03bf") };
    }
    
    private static final class UnicodeMapping
    {
        public final String entityName;
        public final String resolvedValue;
        
        public UnicodeMapping(final String pEntityName, final String pResolvedValue) {
            this.entityName = "&" + pEntityName + ";";
            this.resolvedValue = pResolvedValue;
        }
    }
}
