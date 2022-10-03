package sun.text.resources.pl;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_pl_PL extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "#,##0.## ¤;-#,##0.## ¤", "#,##0%" } }, { "TimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d MMMM yyyy", "d MMMM yyyy", "yyyy-MM-dd", "dd.MM.yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
