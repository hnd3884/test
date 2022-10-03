package sun.text.resources.ja;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_ja extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        final String[] array = { "\u897f\u66a6", "\u660e\u6cbb", "\u5927\u6b63", "\u662d\u548c", "\u5e73\u6210", "\u4ee4\u548c" };
        final String[] array2 = { "\u6c11\u56fd\u524d", "\u6c11\u56fd" };
        final String[] array3 = { "\u7d00\u5143\u524d", "\u897f\u66a6" };
        return new Object[][] { { "MonthNames", { "1\u6708", "2\u6708", "3\u6708", "4\u6708", "5\u6708", "6\u6708", "7\u6708", "8\u6708", "9\u6708", "10\u6708", "11\u6708", "12\u6708", "" } }, { "MonthAbbreviations", { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "" } }, { "DayNames", { "\u65e5\u66dc\u65e5", "\u6708\u66dc\u65e5", "\u706b\u66dc\u65e5", "\u6c34\u66dc\u65e5", "\u6728\u66dc\u65e5", "\u91d1\u66dc\u65e5", "\u571f\u66dc\u65e5" } }, { "DayAbbreviations", { "\u65e5", "\u6708", "\u706b", "\u6c34", "\u6728", "\u91d1", "\u571f" } }, { "DayNarrows", { "\u65e5", "\u6708", "\u706b", "\u6c34", "\u6728", "\u91d1", "\u571f" } }, { "AmPmMarkers", { "\u5348\u524d", "\u5348\u5f8c" } }, { "Eras", array3 }, { "short.Eras", array3 }, { "buddhist.Eras", { "\u7d00\u5143\u524d", "\u4ecf\u66a6" } }, { "japanese.Eras", array }, { "japanese.FirstYear", { "\u5143" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "H'\u6642'mm'\u5206'ss'\u79d2' z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "yyyy'\u5e74'M'\u6708'd'\u65e5'", "yyyy/MM/dd", "yyyy/MM/dd", "yy/MM/dd" } }, { "DateTimePatterns", { "{1} {0}" } }, { "japanese.DatePatterns", { "GGGGyyyy'\u5e74'M'\u6708'd'\u65e5'", "Gy.MM.dd", "Gy.MM.dd", "Gy.MM.dd" } }, { "japanese.TimePatterns", { "H'\u6642'mm'\u5206'ss'\u79d2' z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "japanese.DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
