package sun.text.resources.it;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_it extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "gennaio", "febbraio", "marzo", "aprile", "maggio", "giugno", "luglio", "agosto", "settembre", "ottobre", "novembre", "dicembre", "" } }, { "standalone.MonthNames", { "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre", "" } }, { "MonthAbbreviations", { "gen", "feb", "mar", "apr", "mag", "giu", "lug", "ago", "set", "ott", "nov", "dic", "" } }, { "MonthNarrows", { "G", "F", "M", "A", "M", "G", "L", "A", "S", "O", "N", "D", "" } }, { "standalone.MonthNarrows", { "G", "F", "M", "A", "M", "G", "L", "A", "S", "O", "N", "D", "" } }, { "DayNames", { "domenica", "luned\u00ec", "marted\u00ec", "mercoled\u00ec", "gioved\u00ec", "venerd\u00ec", "sabato" } }, { "standalone.DayNames", { "Domenica", "Luned\u00ec", "Marted\u00ec", "Mercoled\u00ec", "Gioved\u00ec", "Venerd\u00ec", "Sabato" } }, { "DayAbbreviations", { "dom", "lun", "mar", "mer", "gio", "ven", "sab" } }, { "DayNarrows", { "D", "L", "M", "M", "G", "V", "S" } }, { "Eras", { "BC", "dopo Cristo" } }, { "short.Eras", { "aC", "dC" } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "H.mm.ss z", "H.mm.ss z", "H.mm.ss", "H.mm" } }, { "DatePatterns", { "EEEE d MMMM yyyy", "d MMMM yyyy", "d-MMM-yyyy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
