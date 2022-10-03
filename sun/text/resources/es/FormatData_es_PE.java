package sun.text.resources.es;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData_es_PE extends ParallelListResourceBundle
{
    @Override
    protected final Object[][] getContents() {
        return new Object[][] { { "NumberPatterns", { "#,##0.###;-#,##0.###", "�#,##0.00;�-#,##0.00", "#,##0%" } }, { "TimePatterns", { "hh:mm:ss a z", "hh:mm:ss a z", "hh:mm:ss a", "hh:mm a" } }, { "NumberElements", { ".", ",", ";", "%", "0", "#", "-", "E", "\u2030", "\u221e", "\ufffd" } }, { "DatePatterns", { "EEEE d' de 'MMMM' de 'yyyy", "d' de 'MMMM' de 'yyyy", "dd/MM/yyyy", "dd/MM/yy" } }, { "DateTimePatterns", { "{1} {0}" } } };
    }
}
