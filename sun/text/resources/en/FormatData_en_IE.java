package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_IE extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "TimePatterns", { "HH:mm:ss 'o''clock' z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "dd MMMM yyyy", "dd MMMM yyyy", "dd-MMM-yyyy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
