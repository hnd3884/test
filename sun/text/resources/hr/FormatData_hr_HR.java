package sun.text.resources.hr;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_hr_HR extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤ #,##0.##;-¤ #,##0.##", "#,##0%" } }, { "TimePatterns", { "HH:mm:ss z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "yyyy. MMMM dd", "yyyy. MMMM dd", "dd.MM.yyyy.", "dd.MM.yy." } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
