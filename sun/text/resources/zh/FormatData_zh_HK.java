package sun.text.resources.zh;

import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.ResourceBundleBasedAdapter;
import sun.util.resources.ParallelListResourceBundle;

public class FormatData_zh_HK extends ParallelListResourceBundle
{
    public FormatData_zh_HK() {
        this.setParent(((ResourceBundleBasedAdapter)LocaleProviderAdapter.forJRE()).getLocaleData().getDateFormatData(Locale.TAIWAN));
    }
    
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "MonthAbbreviations", { "1\u6708", "2\u6708", "3\u6708", "4\u6708", "5\u6708", "6\u6708", "7\u6708", "8\u6708", "9\u6708", "10\u6708", "11\u6708", "12\u6708", "" } }, { "DayAbbreviations", { "\u65e5", "\u4e00", "\u4e8c", "\u4e09", "\u56db", "\u4e94", "\u516d" } }, { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤#,##0.00;(¤#,##0.00)", "#,##0%" } }, { "TimePatterns", { "ahh'\u6642'mm'\u5206'ss'\u79d2' z", "ahh'\u6642'mm'\u5206'ss'\u79d2'", "ahh:mm:ss", "ah:mm" } }, { "DatePatterns", { "yyyy'\u5e74'MM'\u6708'dd'\u65e5' EEEE", "yyyy'\u5e74'MM'\u6708'dd'\u65e5' EEEE", "yyyy'\u5e74'M'\u6708'd'\u65e5'", "yy'\u5e74'M'\u6708'd'\u65e5'" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GanjkHmsSEDFwWxhKzZ" } };
    }
}
