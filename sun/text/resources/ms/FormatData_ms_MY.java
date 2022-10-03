package sun.text.resources.ms;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_ms_MY extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###", "¤#,##0.00;(¤#,##0.00)", "#,##0%" } }, { "TimePatterns", { "h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm" } }, { "DatePatterns", { "EEEE dd MMM yyyy", "dd MMMM yyyy", "dd MMMM yyyy", "dd/MM/yyyy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
