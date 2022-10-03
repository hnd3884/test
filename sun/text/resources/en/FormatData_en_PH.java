package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_PH extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###", "¤#,##0.00;(¤#,##0.00)", "#,##0%" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "TimePatterns", { "h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE, MMMM d, yyyy", "MMMM d, yyyy", "MM d, yy", "M/d/yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
