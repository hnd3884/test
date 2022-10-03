package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_NZ extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "TimePatterns", { "h:mm:ss a z", "h:mm:ss a", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE, d MMMM yyyy", "d MMMM yyyy", "d/MM/yyyy", "d/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
