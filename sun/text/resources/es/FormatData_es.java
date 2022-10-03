package sun.text.resources.es;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_es extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre", "" } }, { "MonthAbbreviations", { "ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic", "" } }, { "MonthNarrows", { "E", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "" } }, { "DayNames", { "domingo", "lunes", "martes", "mi\u00e9rcoles", "jueves", "viernes", "s\u00e1bado" } }, { "DayAbbreviations", { "dom", "lun", "mar", "mi\u00e9", "jue", "vie", "s\u00e1b" } }, { "DayNarrows", { "D", "L", "M", "X", "J", "V", "S" } }, { "Eras", { "antes de Cristo", "anno D\u00f3mini" } }, { "short.Eras", { "a.C.", "d.C." } }, { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤#,##0.00;(¤#,##0.00)", "#,##0%" } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "HH'H'mm'' z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE d' de 'MMMM' de 'yyyy", "d' de 'MMMM' de 'yyyy", "dd-MMM-yyyy", "d/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
