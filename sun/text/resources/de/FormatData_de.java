package sun.text.resources.de;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_de extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "Januar", "Februar", "M\u00e4rz", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember", "" } }, { "MonthAbbreviations", { "Jan", "Feb", "M\u00e4r", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez", "" } }, { "MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "" } }, { "standalone.MonthAbbreviations", { "Jan", "Feb", "M\u00e4r", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez", "" } }, { "DayNames", { "Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag" } }, { "DayAbbreviations", { "So", "Mo", "Di", "Mi", "Do", "Fr", "Sa" } }, { "standalone.DayAbbreviations", { "So", "Mo", "Di", "Mi", "Do", "Fr", "Sa" } }, { "DayNarrows", { "S", "M", "D", "M", "D", "F", "S" } }, { "Eras", { "v. Chr.", "n. Chr." } }, { "short.Eras", { "v. Chr.", "n. Chr." } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "HH:mm' Uhr 'z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d. MMMM yyyy", "d. MMMM yyyy", "dd.MM.yyyy", "dd.MM.yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GuMtkHmsSEDFwWahKzZ" } };
    }
}
