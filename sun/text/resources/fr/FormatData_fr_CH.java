package sun.text.resources.fr;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_fr_CH extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤ #,##0.00;¤-#,##0.00", "#,##0 %" } }, { "NumberElements", { ".", "'", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "HH.mm.' h' z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d. MMMM yyyy", "d. MMMM yyyy", "d MMM yyyy", "dd.MM.yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GaMjkHmsSEDFwWxhKzZ" } };
    }
}
