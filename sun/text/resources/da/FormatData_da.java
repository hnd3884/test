package sun.text.resources.da;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_da extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "januar", "februar", "marts", "april", "maj", "juni", "juli", "august", "september", "oktober", "november", "december", "" } }, { "MonthAbbreviations", { "jan.", "feb.", "mar.", "apr.", "maj", "jun.", "jul.", "aug.", "sep.", "okt.", "nov.", "dec.", "" } }, { "MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "" } }, { "standalone.MonthAbbreviations", { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov", "dec", "" } }, { "DayNames", { "s\u00f8ndag", "mandag", "tirsdag", "onsdag", "torsdag", "fredag", "l\u00f8rdag" } }, { "DayAbbreviations", { "s\u00f8", "ma", "ti", "on", "to", "fr", "l\u00f8" } }, { "DayNarrows", { "S", "M", "T", "O", "T", "F", "L" } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "Eras", { "f.Kr.", "e.Kr." } }, { "short.Eras", { "f.Kr.", "e.Kr." } }, { "TimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "d. MMMM yyyy", "d. MMMM yyyy", "dd-MM-yyyy", "dd-MM-yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GuMtkHmsSEDFwWahKzZ" } };
    }
}
