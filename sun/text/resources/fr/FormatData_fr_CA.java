package sun.text.resources.fr;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_fr_CA extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "#,##0.00 ¤;(#,##0.00¤)", "#,##0 %" } }, { "TimePatterns", { "H' h 'mm z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE d MMMM yyyy", "d MMMM yyyy", "yyyy-MM-dd", "yy-MM-dd" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GaMjkHmsSEDFwWxhKzZ" } };
    }
}
