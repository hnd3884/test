package sun.text.resources.in;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_in_ID extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "TimePatterns", { "H:mm:ss", "H:mm:ss", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE dd MMMM yyyy", "dd MMMM yyyy", "dd MMM yy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
