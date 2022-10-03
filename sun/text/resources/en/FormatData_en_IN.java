package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_IN extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE, d MMMM, yyyy", "d MMMM, yyyy", "d MMM, yyyy", "d/M/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" } };
    }
}
