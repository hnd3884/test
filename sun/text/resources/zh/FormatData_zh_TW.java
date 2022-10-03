package sun.text.resources.zh;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_zh_TW extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "\u6c11\u570b\u524d", "\u6c11\u570b" };
        final String[] array2 = { "\u897f\u5143\u524d", "\u897f\u5143" };
        return new Object[][] { { "Eras", array2 }, { "short.Eras", array2 }, { "standalone.MonthAbbreviations", { "1\u6708", "2\u6708", "3\u6708", "4\u6708", "5\u6708", "6\u6708", "7\u6708", "8\u6708", "9\u6708", "10\u6708", "11\u6708", "12\u6708", "" } }, { "MonthNarrows", { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "" } }, { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤#,##0.00;-¤#,##0.00", "#,##0%" } }, { "TimePatterns", { "ahh'\u6642'mm'\u5206'ss'\u79d2' z", "ahh'\u6642'mm'\u5206'ss'\u79d2'", "a hh:mm:ss", "a h:mm" } }, { "DatePatterns", { "yyyy'\u5e74'M'\u6708'd'\u65e5' EEEE", "yyyy'\u5e74'M'\u6708'd'\u65e5'", "yyyy/M/d", "yyyy/M/d" } }, { "DateTimePatterns", { "{1} {0}" } }, { "buddhist.DatePatterns", { "GGGGy\u5e74M\u6708d\u65e5EEEE", "GGGGy\u5e74M\u6708d\u65e5", "GGGGy/M/d", "GGGGy/M/d" } }, { "japanese.DatePatterns", { "GGGGy\u5e74M\u6708d\u65e5EEEE", "GGGGy\u5e74M\u6708d\u65e5", "GGGGy/M/d", "GGGGy/M/d" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
