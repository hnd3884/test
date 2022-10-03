package sun.text.resources.sr;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_sr_Latn extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "januar", "februar", "mart", "april", "maj", "jun", "jul", "avgust", "septembar", "oktobar", "novembar", "decembar", "" } }, { "MonthAbbreviations", { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "avg", "sep", "okt", "nov", "dec", "" } }, { "MonthNarrows", { "j", "f", "m", "a", "m", "j", "j", "a", "s", "o", "n", "d", "" } }, { "DayNames", { "nedelja", "ponedeljak", "utorak", "sreda", "\u010detvrtak", "petak", "subota" } }, { "DayAbbreviations", { "ned", "pon", "uto", "sre", "\u010det", "pet", "sub" } }, { "DayNarrows", { "n", "p", "u", "s", "\u010d", "p", "s" } }, { "Eras", { "p. n. e.", "n. e" } }, { "NumberPatterns", { "#,##0.###", "да#,##0.00", "#,##0%" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "TimePatterns", { "HH.mm.ss zzzz", "HH.mm.ss z", "HH.mm.ss", "HH.mm" } }, { "DatePatterns", { "EEEE, dd. MMMM y.", "dd. MMMM y.", "dd.MM.y.", "d.M.yy." } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
