package sun.text.resources.fr;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_fr_BE extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberElements", { ",", ".", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "H' h 'mm' min 'ss' s 'z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE d MMMM yyyy", "d MMMM yyyy", "dd-MMM-yyyy", "d/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GaMjkHmsSEDFwWxhKzZ" } };
    }
}
