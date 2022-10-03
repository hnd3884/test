package sun.text.resources.en;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_SG extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###", "¤#,##0.00", "#,##0%" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "NaN" } }, { "DatePatterns", { "EEEE, d MMMM, yyyy", "d MMMM, yyyy", "d MMM, yyyy", "d/M/yy" } } };
    }
}
