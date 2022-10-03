package sun.text.resources.is;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_is extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "jan\u00faar", "febr\u00faar", "mars", "apr\u00edl", "ma\u00ed", "j\u00fan\u00ed", "j\u00fal\u00ed", "\u00e1g\u00fast", "september", "okt\u00f3ber", "n\u00f3vember", "desember", "" } }, { "MonthAbbreviations", { "jan.", "feb.", "mar.", "apr.", "ma\u00ed", "j\u00fan.", "j\u00fal.", "\u00e1g\u00fa.", "sep.", "okt.", "n\u00f3v.", "des.", "" } }, { "MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "\u00c1", "L", "O", "N", "D", "" } }, { "standalone.MonthNarrows", { "j", "f", "m", "a", "m", "j", "j", "\u00e1", "s", "o", "n", "d", "" } }, { "DayNames", { "sunnudagur", "m\u00e1nudagur", "\u00feri\u00f0judagur", "mi\u00f0vikudagur", "fimmtudagur", "f\u00f6studagur", "laugardagur" } }, { "DayAbbreviations", { "sun.", "m\u00e1n.", "\u00feri.", "mi\u00f0.", "fim.", "f\u00f6s.", "lau." } }, { "DayNarrows", { "S", "M", "\u00de", "M", "F", "F", "L" } }, { "standalone.DayNarrows", { "s", "m", "\u00fe", "m", "f", "f", "l" } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "d. MMMM yyyy", "d. MMMM yyyy", "d.M.yyyy", "d.M.yyyy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
