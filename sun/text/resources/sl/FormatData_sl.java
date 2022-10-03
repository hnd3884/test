package sun.text.resources.sl;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_sl extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "januar", "februar", "marec", "april", "maj", "junij", "julij", "avgust", "september", "oktober", "november", "december", "" } }, { "MonthAbbreviations", { "jan.", "feb.", "mar.", "apr.", "maj", "jun.", "jul.", "avg.", "sep.", "okt.", "nov.", "dec.", "" } }, { "standalone.MonthAbbreviations", { "jan", "feb", "mar", "apr", "maj", "jun", "jul", "avg", "sep", "okt", "nov", "dec", "" } }, { "MonthNarrows", { "j", "f", "m", "a", "m", "j", "j", "a", "s", "o", "n", "d", "" } }, { "DayNames", { "Nedelja", "Ponedeljek", "Torek", "Sreda", "\u010cetrtek", "Petek", "Sobota" } }, { "DayAbbreviations", { "Ned", "Pon", "Tor", "Sre", "\u010cet", "Pet", "Sob" } }, { "standalone.DayAbbreviations", { "ned", "pon", "tor", "sre", "\u010det", "pet", "sob" } }, { "DayNarrows", { "n", "p", "t", "s", "\u010d", "p", "s" } }, { "Eras", { "pr.n.\u0161.", "po Kr." } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "H:mm:ss z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE, dd. MMMM y", "dd. MMMM y", "d.M.yyyy", "d.M.y" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GanjkHmsSEDFwWxhKzZ" } };
    }
}
