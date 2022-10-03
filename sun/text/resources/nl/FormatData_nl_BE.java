package sun.text.resources.nl;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_nl_BE extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "#,##0.00 ¤;-#,##0.00 ¤", "#,##0%" } }, { "TimePatterns", { "H.mm' u. 'z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE d MMMM yyyy", "d MMMM yyyy", "d-MMM-yyyy", "d/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
