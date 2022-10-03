package sun.text.resources.nl;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_nl extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "januari", "februari", "maart", "april", "mei", "juni", "juli", "augustus", "september", "oktober", "november", "december", "" } }, { "MonthAbbreviations", { "jan", "feb", "mrt", "apr", "mei", "jun", "jul", "aug", "sep", "okt", "nov", "dec", "" } }, { "MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "" } }, { "DayNames", { "zondag", "maandag", "dinsdag", "woensdag", "donderdag", "vrijdag", "zaterdag" } }, { "DayAbbreviations", { "zo", "ma", "di", "wo", "do", "vr", "za" } }, { "DayNarrows", { "Z", "M", "D", "W", "D", "V", "Z" } }, { "Eras", { "v. Chr.", "n. Chr." } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "H:mm:ss' uur' z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE d MMMM yyyy", "d MMMM yyyy", "d-MMM-yyyy", "d-M-yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
