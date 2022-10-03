package sun.text.resources.fr;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_fr extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthNames", { "janvier", "f\u00e9vrier", "mars", "avril", "mai", "juin", "juillet", "ao\u00fbt", "septembre", "octobre", "novembre", "d\u00e9cembre", "" } }, { "MonthAbbreviations", { "janv.", "f\u00e9vr.", "mars", "avr.", "mai", "juin", "juil.", "ao\u00fbt", "sept.", "oct.", "nov.", "d\u00e9c.", "" } }, { "MonthNarrows", { "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D", "" } }, { "DayNames", { "dimanche", "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi" } }, { "DayAbbreviations", { "dim.", "lun.", "mar.", "mer.", "jeu.", "ven.", "sam." } }, { "standalone.DayAbbreviations", { "dim.", "lun.", "mar.", "mer.", "jeu.", "ven.", "sam." } }, { "DayNarrows", { "D", "L", "M", "M", "J", "V", "S" } }, { "Eras", { "BC", "ap. J.-C." } }, { "short.Eras", { "av. J.-C.", "ap. J.-C." } }, { "buddhist.Eras", { "BC", "\u00e8re bouddhiste" } }, { "buddhist.short.Eras", { "BC", "\u00e8re b." } }, { "buddhist.narrow.Eras", { "BC", "E.B." } }, { "NumberPatterns", { "#,##0.###;-#,##0.###", "#,##0.00 ¤;-#,##0.00 ¤", "#,##0 %" } }, { "NumberElements", { ",", " ", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "HH' h 'mm z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE d MMMM yyyy", "d MMMM yyyy", "d MMM yyyy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GaMjkHmsSEDFwWxhKzZ" } };
    }
}
