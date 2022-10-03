package sun.text.resources.hr;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_hr extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "prije R.O.C.", "R.O.C." };
        return new Object[][] { { "MonthNames", { "sije\u010dnja", "velja\u010de", "o\u017eujka", "travnja", "svibnja", "lipnja", "srpnja", "kolovoza", "rujna", "listopada", "studenoga", "prosinca", "" } }, { "standalone.MonthNames", { "sije\u010danj", "velja\u010da", "o\u017eujak", "travanj", "svibanj", "lipanj", "srpanj", "kolovoz", "rujan", "listopad", "studeni", "prosinac", "" } }, { "MonthAbbreviations", { "sij", "velj", "o\u017eu", "tra", "svi", "lip", "srp", "kol", "ruj", "lis", "stu", "pro", "" } }, { "standalone.MonthAbbreviations", { "sij", "vel", "o\u017eu", "tra", "svi", "lip", "srp", "kol", "ruj", "lis", "stu", "pro", "" } }, { "MonthNarrows", { "1.", "2.", "3.", "4.", "5.", "6.", "7.", "8.", "9.", "10.", "11.", "12.", "" } }, { "standalone.MonthNarrows", { "1.", "2.", "3.", "4.", "5.", "6.", "7.", "8.", "9.", "10.", "11.", "12.", "" } }, { "DayNames", { "nedjelja", "ponedjeljak", "utorak", "srijeda", "\u010detvrtak", "petak", "subota" } }, { "standalone.DayNames", { "nedjelja", "ponedjeljak", "utorak", "srijeda", "\u010detvrtak", "petak", "subota" } }, { "DayAbbreviations", { "ned", "pon", "uto", "sri", "\u010det", "pet", "sub" } }, { "standalone.DayAbbreviations", { "ned", "pon", "uto", "sri", "\u010det", "pet", "sub" } }, { "DayNarrows", { "N", "P", "U", "S", "\u010c", "P", "S" } }, { "standalone.DayNarrows", { "n", "p", "u", "s", "\u010d", "p", "s" } }, { "Eras", { "Prije Krista", "Poslije Krista" } }, { "short.Eras", { "p. n. e.", "A. D." } }, { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "yyyy. MMMM dd", "yyyy. MMMM dd", "yyyy.MM.dd", "yyyy.MM.dd" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GanjkHmsSEDFwWxhKzZ" } };
    }
}
