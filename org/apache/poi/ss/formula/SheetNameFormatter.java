package org.apache.poi.ss.formula;

import java.util.regex.Matcher;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.SpreadsheetVersion;
import java.io.IOException;
import org.apache.poi.util.Removal;
import java.util.regex.Pattern;

public final class SheetNameFormatter
{
    private static final char DELIMITER = '\'';
    private static final Pattern CELL_REF_PATTERN;
    
    private SheetNameFormatter() {
    }
    
    public static String format(final String rawSheetName) {
        final StringBuilder sb = new StringBuilder(((rawSheetName == null) ? 0 : rawSheetName.length()) + 2);
        appendFormat(sb, rawSheetName);
        return sb.toString();
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public static void appendFormat(final StringBuffer out, final String rawSheetName) {
        appendFormat((Appendable)out, rawSheetName);
    }
    
    @Deprecated
    @Removal(version = "5.0.0")
    public static void appendFormat(final StringBuffer out, final String workbookName, final String rawSheetName) {
        appendFormat((Appendable)out, workbookName, rawSheetName);
    }
    
    @Removal(version = "5.0.0")
    public static void appendFormat(final StringBuilder out, final String rawSheetName) {
        appendFormat((Appendable)out, rawSheetName);
    }
    
    @Removal(version = "5.0.0")
    public static void appendFormat(final StringBuilder out, final String workbookName, final String rawSheetName) {
        appendFormat((Appendable)out, workbookName, rawSheetName);
    }
    
    public static void appendFormat(final Appendable out, final String rawSheetName) {
        try {
            final boolean needsQuotes = needsDelimiting(rawSheetName);
            if (needsQuotes) {
                out.append('\'');
                appendAndEscape(out, rawSheetName);
                out.append('\'');
            }
            else {
                appendAndEscape(out, rawSheetName);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void appendFormat(final Appendable out, final String workbookName, final String rawSheetName) {
        try {
            final boolean needsQuotes = needsDelimiting(workbookName) || needsDelimiting(rawSheetName);
            if (needsQuotes) {
                out.append('\'');
                out.append('[');
                appendAndEscape(out, workbookName.replace('[', '(').replace(']', ')'));
                out.append(']');
                appendAndEscape(out, rawSheetName);
                out.append('\'');
            }
            else {
                out.append('[');
                appendOrREF(out, workbookName);
                out.append(']');
                appendOrREF(out, rawSheetName);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void appendOrREF(final Appendable out, final String name) throws IOException {
        if (name == null) {
            out.append("#REF");
        }
        else {
            out.append(name);
        }
    }
    
    static void appendAndEscape(final Appendable sb, final String rawSheetName) {
        try {
            if (rawSheetName == null) {
                sb.append("#REF");
                return;
            }
            for (int len = rawSheetName.length(), i = 0; i < len; ++i) {
                final char ch = rawSheetName.charAt(i);
                if (ch == '\'') {
                    sb.append('\'');
                }
                sb.append(ch);
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static boolean needsDelimiting(final String rawSheetName) {
        if (rawSheetName == null) {
            return false;
        }
        final int len = rawSheetName.length();
        if (len < 1) {
            return false;
        }
        if (Character.isDigit(rawSheetName.charAt(0))) {
            return true;
        }
        for (int i = 0; i < len; ++i) {
            final char ch = rawSheetName.charAt(i);
            if (isSpecialChar(ch)) {
                return true;
            }
        }
        return (Character.isLetter(rawSheetName.charAt(0)) && Character.isDigit(rawSheetName.charAt(len - 1)) && nameLooksLikePlainCellReference(rawSheetName)) || nameLooksLikeBooleanLiteral(rawSheetName);
    }
    
    private static boolean nameLooksLikeBooleanLiteral(final String rawSheetName) {
        switch (rawSheetName.charAt(0)) {
            case 'T':
            case 't': {
                return "TRUE".equalsIgnoreCase(rawSheetName);
            }
            case 'F':
            case 'f': {
                return "FALSE".equalsIgnoreCase(rawSheetName);
            }
            default: {
                return false;
            }
        }
    }
    
    static boolean isSpecialChar(final char ch) {
        if (Character.isLetterOrDigit(ch)) {
            return false;
        }
        switch (ch) {
            case '.':
            case '_': {
                return false;
            }
            case '\t':
            case '\n':
            case '\r': {
                throw new RuntimeException("Illegal character (0x" + Integer.toHexString(ch) + ") found in sheet name");
            }
            default: {
                return true;
            }
        }
    }
    
    static boolean cellReferenceIsWithinRange(final String lettersPrefix, final String numbersSuffix) {
        return CellReference.cellReferenceIsWithinRange(lettersPrefix, numbersSuffix, SpreadsheetVersion.EXCEL97);
    }
    
    static boolean nameLooksLikePlainCellReference(final String rawSheetName) {
        final Matcher matcher = SheetNameFormatter.CELL_REF_PATTERN.matcher(rawSheetName);
        if (!matcher.matches()) {
            return false;
        }
        final String lettersPrefix = matcher.group(1);
        final String numbersSuffix = matcher.group(2);
        return cellReferenceIsWithinRange(lettersPrefix, numbersSuffix);
    }
    
    static {
        CELL_REF_PATTERN = Pattern.compile("([A-Za-z]+)([0-9]+)");
    }
}
