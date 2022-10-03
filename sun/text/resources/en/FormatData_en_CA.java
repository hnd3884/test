package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_CA extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "TimePatterns", { "h:mm:ss 'o''clock' a z", "h:mm:ss z a", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE, MMMM d, yyyy", "MMMM d, yyyy", "d-MMM-yyyy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
