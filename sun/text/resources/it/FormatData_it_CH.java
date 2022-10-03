package sun.text.resources.it;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_it_CH extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤ #,##0.00;¤-#,##0.00", "#,##0%" } }, { "NumberElements", { ".", "'", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "TimePatterns", { "H.mm' h' z", "HH:mm:ss z", "HH:mm:ss", "HH:mm" } }, { "DatePatterns", { "EEEE, d. MMMM yyyy", "d. MMMM yyyy", "d-MMM-yyyy", "dd.MM.yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GuMtkHmsSEDFwWahKzZ" } };
    }
}
