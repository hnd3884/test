package sun.text.resources.et;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_et extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "jaanuar", "veebruar", "m\u00e4rts", "aprill", "mai", "juuni", "juuli", "august", "september", "oktoober", "november", "detsember", "" } }, { "MonthAbbreviations", { "jaan", "veebr", "m\u00e4rts", "apr", "mai", "juuni", "juuli", "aug", "sept", "okt", "nov", "dets", "" } }, { "MonthNarrows", { "J", "V", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "" } }, { "DayNames", { "p\u00fchap\u00e4ev", "esmasp\u00e4ev", "teisip\u00e4ev", "kolmap\u00e4ev", "neljap\u00e4ev", "reede", "laup\u00e4ev" } }, { "DayAbbreviations", { "P", "E", "T", "K", "N", "R", "L" } }, { "DayNarrows", { "P", "E", "T", "K", "N", "R", "L" } }, { "Eras", { "e.m.a.", "m.a.j." } }, { "short.Eras", { "e.m.a.", "m.a.j." } }, { "NumberElements", { ",", " ", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "H:mm:ss z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE, d. MMMM yyyy", "EEEE, d. MMMM yyyy. 'a'", "d.MM.yyyy", "d.MM.yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
