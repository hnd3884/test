package sun.text.resources.de;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_de_CH extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤ #,##0.00;¤-#,##0.00", "#,##0 %" } }, { "NumberElements", { ".", "'", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "DateTimePatternChars", "GuMtkHmsSEDFwWahKzZ" } };
    }
}
