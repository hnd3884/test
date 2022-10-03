package sun.text.resources.zh;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_zh_SG extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "DayAbbreviations", { "\u5468\u65e5", "\u5468\u4e00", "\u5468\u4e8c", "\u5468\u4e09", "\u5468\u56db", "\u5468\u4e94", "\u5468\u516d" } }, { "NumberPatterns", { "#,##0.###", "¤#,##0.00", "#,##0%" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "TimePatterns", { "a hh:mm:ss", "a hh:mm:ss", "a hh:mm", "a hh:mm" } }, { "DatePatterns", { "dd MMMM yyyy", "dd MMM yyyy", "dd-MMM-yy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GanjkHmsSEDFwWxhKzZ" } };
    }
}
