package sun.text.resources.es;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_es_US extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "AmPmMarkers", { "a.m.", "p.m." } }, { "Eras", { "a.C.", "d.C." } }, { "NumberPatterns", { "#,##0.###", "¤#,##0.00;(¤#,##0.00)", "#,##0%" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "TimePatterns", { "h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE d' de 'MMMM' de 'yyyy", "d' de 'MMMM' de 'yyyy", "MMM d, yyyy", "M/d/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GuMtkHmsSEDFwWahKzZ" } };
    }
}
