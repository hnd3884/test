package sun.text.resources.cldr.fo;

import java.util.ListResourceBundle;

public class FormatData_fo extends ListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "januar", "februar", "mars", "apr\u00edl", "mai", "juni", "juli", "august", "september", "oktober", "november", "desember", "" } }, { "MonthAbbreviations", { "jan", "feb", "mar", "apr", "mai", "jun", "jul", "aug", "sep", "okt", "nov", "des", "" } }, { "DayNames", { "sunnudagur", "m\u00e1nadagur", "t\u00fdsdagur", "mikudagur", "h\u00f3sdagur", "fr\u00edggjadagur", "leygardagur" } }, { "DayAbbreviations", { "sun", "m\u00e1n", "t\u00fds", "mik", "h\u00f3s", "fr\u00ed", "ley" } }, { "QuarterNames", { "1. kvartal", "2. kvartal", "3. kvartal", "4. kvartal" } }, { "QuarterAbbreviations", { "K1", "K2", "K3", "K4" } }, { "DatePatterns", { "EEEE dd MMMM y", "d. MMM y", "dd-MM-yyyy", "dd-MM-yy" } }, { "DefaultNumberingSystem", "latn" }, { "latn.NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "NumberPatterns", { "#,##0.###", "¤#,##0.00;¤-#,##0.00", "#,##0%" } } };
    }
}
