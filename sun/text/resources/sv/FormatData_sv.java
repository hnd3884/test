package sun.text.resources.sv;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_sv extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "f\u00f6re R.K.", "R.K." };
        return new Object[][] { { "MonthNames", { "januari", "februari", "mars", "april", "maj", "juni", "juli", "augusti", "september", "oktober", "november", "december", "" } }, { "MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "" } }, { "MonthAbbreviations", { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov", "dec", "" } }, { "standalone.MonthAbbreviations", { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "aug", "sep", "okt", "nov", "dec", "" } }, { "standalone.MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "" } }, { "DayNames", { "s\u00f6ndag", "m\u00e5ndag", "tisdag", "onsdag", "torsdag", "fredag", "l\u00f6rdag" } }, { "DayAbbreviations", { "s\u00f6", "m\u00e5", "ti", "on", "to", "fr", "l\u00f6" } }, { "standalone.DayAbbreviations", { "s\u00f6n", "m\u00e5n", "tis", "ons", "tor", "fre", "l\u00f6r" } }, { "DayNarrows", { "S", "M", "T", "O", "T", "F", "L" } }, { "standalone.DayNames", { "s\u00f6ndag", "m\u00e5ndag", "tisdag", "onsdag", "torsdag", "fredag", "l\u00f6rdag" } }, { "standalone.DayNarrows", { "S", "M", "T", "O", "T", "F", "L" } }, { "Eras", { "f\u00f6re Kristus", "efter Kristus" } }, { "short.Eras", { "f.Kr.", "e.Kr." } }, { "narrow.Eras", { "f.Kr.", "e.Kr." } }, { "AmPmMarkers", { "fm", "em" } }, { "narrow.AmPmMarkers", { "f", "e" } }, { "NumberElements", { ",", " ", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "'kl 'H:mm z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "'den 'd MMMM yyyy", "'den 'd MMMM yyyy", "yyyy-MMM-dd", "yyyy-MM-dd" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
