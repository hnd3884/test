package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_ZA extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤ #,##0.00;¤-#,##0.00", "#,##0%" } }, { "TimePatterns", { "h:mm:ss a", "h:mm:ss a", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE dd MMMM yyyy", "dd MMMM yyyy", "dd MMM yyyy", "yyyy/MM/dd" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
