package sun.text.resources.es;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_es_CL extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤#,##0.00;¤-#,##0.00", "#,##0%" } }, { "TimePatterns", { "HH:mm:ss zzzz", "H:mm:ss z", "H:mm:ss", "H:mm" } }, { "DatePatterns", { "EEEE d' de 'MMMM' de 'yyyy", "d' de 'MMMM' de 'yyyy", "dd-MM-yyyy", "dd-MM-yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
