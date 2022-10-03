package org.apache.poi.ss.formula;

public class SheetRangeAndWorkbookIndexFormatter
{
    private SheetRangeAndWorkbookIndexFormatter() {
    }
    
    public static String format(final StringBuilder sb, final int workbookIndex, final String firstSheetName, final String lastSheetName) {
        if (anySheetNameNeedsEscaping(firstSheetName, lastSheetName)) {
            return formatWithDelimiting(sb, workbookIndex, firstSheetName, lastSheetName);
        }
        return formatWithoutDelimiting(sb, workbookIndex, firstSheetName, lastSheetName);
    }
    
    private static String formatWithDelimiting(final StringBuilder sb, final int workbookIndex, final String firstSheetName, final String lastSheetName) {
        sb.append('\'');
        if (workbookIndex >= 0) {
            sb.append('[');
            sb.append(workbookIndex);
            sb.append(']');
        }
        SheetNameFormatter.appendAndEscape(sb, firstSheetName);
        if (lastSheetName != null) {
            sb.append(':');
            SheetNameFormatter.appendAndEscape(sb, lastSheetName);
        }
        sb.append('\'');
        return sb.toString();
    }
    
    private static String formatWithoutDelimiting(final StringBuilder sb, final int workbookIndex, final String firstSheetName, final String lastSheetName) {
        if (workbookIndex >= 0) {
            sb.append('[');
            sb.append(workbookIndex);
            sb.append(']');
        }
        sb.append(firstSheetName);
        if (lastSheetName != null) {
            sb.append(':');
            sb.append(lastSheetName);
        }
        return sb.toString();
    }
    
    private static boolean anySheetNameNeedsEscaping(final String firstSheetName, final String lastSheetName) {
        boolean anySheetNameNeedsDelimiting = SheetNameFormatter.needsDelimiting(firstSheetName);
        anySheetNameNeedsDelimiting |= SheetNameFormatter.needsDelimiting(lastSheetName);
        return anySheetNameNeedsDelimiting;
    }
}
